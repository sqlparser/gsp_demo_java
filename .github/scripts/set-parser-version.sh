#!/usr/bin/env bash
#
# Read, set, or verify the GSP parser version everywhere this repository writes
# it down.
#
# The version lives in four files. The root build declares it as a
# ${gsp.core.version} property, and the three connector/ modules hardcode the
# version in their gsqlparser dependency, because they are separate builds with
# no parent to inherit a property from. Nothing made them agree, so they could
# drift apart silently -- and a bump meant four hand edits, which is most of why
# bumping felt expensive.
#
# It was five until pom_dlineage.xml was merged into pom.xml. That second POM
# declared its own copy of the property for no reason other than that it was a
# second POM.
#
# Usage:
#   set-parser-version.sh              print the current version(s)
#   set-parser-version.sh --check      exit 1 if the four disagree
#   set-parser-version.sh 4.1.8        rewrite all four to 4.1.8
#
# Exit codes:
#   0  success, or --check found them consistent
#   1  --check found a disagreement, or a file could not be updated
#   2  bad usage

set -uo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")/../.." || exit 1

MODE="show"
NEW=""
case "${1:-}" in
    "")        MODE="show" ;;
    --check)   MODE="check" ;;
    -h|--help) sed -n '2,20p' "$0" | sed 's/^# \{0,1\}//'; exit 0 ;;
    -*)        echo "unknown flag: $1" >&2; exit 2 ;;
    *)
        MODE="set"; NEW="$1"
        if ! [[ "$NEW" =~ ^[0-9]+(\.[0-9]+)+$ ]]; then
            echo "not a version: $NEW" >&2
            exit 2
        fi
        ;;
esac

PYTHON=$(command -v python3 || command -v python) || {
    echo "python is required" >&2; exit 1
}

MODE="$MODE" NEW="$NEW" "$PYTHON" - <<'PY'
import os, re, sys

mode = os.environ["MODE"]
new = os.environ["NEW"]

# (path, human label, regex with exactly one capturing group around the version)
TARGETS = [
    ("pom.xml", "root build property",
     r"(?s)(<gsp\.core\.version>)([^<]+)(</gsp\.core\.version>)"),
]
for mod in ("oracleConnector", "snowflakeConnector", "sqlServerConnector"):
    TARGETS.append((
        "connector/%s/pom.xml" % mod,
        "connector/%s dependency" % mod,
        # Anchor on the gsqlparser dependency so we never touch the JDBC
        # driver's <version> sitting a few lines below it.
        r"(?s)(<artifactId>gsqlparser</artifactId>\s*<version>)([^<]+)(</version>)",
    ))

found, missing, changed = [], [], []

for path, label, pattern in TARGETS:
    try:
        with open(path, encoding="utf-8") as fh:
            text = fh.read()
    except IOError as e:
        missing.append((path, label, str(e)))
        continue
    m = re.search(pattern, text)
    if not m:
        missing.append((path, label, "no version element matched"))
        continue
    found.append((path, label, m.group(2)))
    if mode == "set" and m.group(2) != new:
        text = text[:m.start(2)] + new + text[m.end(2):]
        with open(path, "w", encoding="utf-8") as fh:
            fh.write(text)
        changed.append((path, label, m.group(2)))

for path, label, why in missing:
    print("::error::%s (%s): %s" % (path, label, why))

if mode == "set":
    for path, label, old in changed:
        print("  %-42s %s -> %s" % (path, old, new))
    unchanged = [f for f in found if f[2] == new]
    if unchanged:
        print("  %d file(s) already at %s" % (len(unchanged), new))
    print("set parser version to %s in %d file(s)" % (new, len(changed)))
    sys.exit(1 if missing else 0)

width = max(len(p) for p, _, _ in found) if found else 10
for path, label, ver in found:
    print("  %-*s  %-28s %s" % (width, path, label, ver))

versions = set(v for _, _, v in found)
if mode == "check":
    if missing:
        sys.exit(1)
    if len(versions) > 1:
        print("::error::parser version disagrees across files: %s"
              % ", ".join(sorted(versions)))
        print("::error::run .github/scripts/set-parser-version.sh <version> to make them agree")
        sys.exit(1)
    print("consistent: %s" % versions.pop())
    sys.exit(0)

if len(versions) > 1:
    print("WARNING: these disagree: %s" % ", ".join(sorted(versions)))
sys.exit(1 if missing else 0)
PY
