#!/usr/bin/env bash
#
# Run the standalone data-lineage jar and check what it produced.
#
# This exists because building the dlineage demo proved nothing, twice. Until
# 2026-07-28 it was a second POM, pom_dlineage.xml, and the only thing CI did
# with it was `package`:
#
#   * issue #47 -- it was missing the JAXB dependency the root POM already
#     carried. Compiles perfectly. Dies at runtime on every JDK after 8.
#   * issue #46 -- the documented run command put external_lib/ on the
#     classpath, a directory only the Windows .bat route ever creates, so
#     nobody following the Maven instructions could run the thing at all.
#
# Both shipped through a green build. So this runs it.
#
# The assertions are about the OUTPUT, not the exit status, and that is the
# whole point. In issue #47 the process created lineage.json, died before
# writing a byte into it, and still exited 0. An existence check, or an exit
# code check, would both have called that a pass. Non-empty and parseable is
# the weakest check that would actually have caught it.
#
# Usage:
#   smoke-dlineage-jar.sh [jar]
#
# Exit codes:
#   0  the jar ran and produced valid, non-empty lineage in both formats
#   1  anything else

set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")/../.." || exit 1

JAR="${1:-target/gsp_demo_java-1.0-SNAPSHOT-dlineage.jar}"
SQL="samples/dlineage/demo.sql"

fail() { echo "::error::$*" >&2; exit 1; }

[ -f "$JAR" ] || fail "$JAR was not produced by 'mvn package'"
[ -f "$SQL" ] || fail "$SQL is missing; the smoke test has no input"

OUT=$(mktemp -d)
trap 'rm -rf "$OUT"' EXIT

echo "running $JAR on $SQL"
echo "java: $(java -version 2>&1 | head -1)"

# --- JSON ------------------------------------------------------------------
# No classpath, no version number, no external_lib. If this invocation ever
# needs more than `java -jar`, the packaging has regressed and the README has
# gone stale with it.
java -jar "$JAR" /f "$SQL" /o "$OUT/lineage.json" /json \
     /simpleShowRelationTypes fdd,fdr /filterRelationTypes fdd

[ -s "$OUT/lineage.json" ] ||
    fail "lineage.json is empty or missing: the tool ran but produced nothing (cf. issue #47)"

python3 - "$OUT/lineage.json" <<'PY'
import json, sys
path = sys.argv[1]
try:
    d = json.load(open(path))
except Exception as e:
    sys.exit("lineage.json is not valid JSON: %s" % e)

# The shape differs with /graph, so accept either place the relationships live.
rel = d.get("relationships") or d.get("data", {}).get("sqlflow", {}).get("relationships", [])
if not rel:
    sys.exit("lineage.json parsed but carries no relationships; "
             "the analyzer returned an empty result")
print("ok: JSON output, %d relationships" % len(rel))
PY

# --- XML -------------------------------------------------------------------
# A separate code path, through JAXB, and the one that actually broke. With
# jaxb-runtime 2.3.3 on JDK 11+ the JSON above still succeeds while this writes
# a 0-byte file and exits 0, so checking JSON alone is not enough. See the
# jaxb-runtime comment in pom.xml for why the API and the runtime are
# deliberately on different versions.
java -jar "$JAR" /f "$SQL" /o "$OUT/lineage.xml"

[ -s "$OUT/lineage.xml" ] ||
    fail "lineage.xml is empty or missing: the JAXB output path failed (cf. issue #47)"

python3 - "$OUT/lineage.xml" <<'PY'
import sys, xml.dom.minidom
path = sys.argv[1]
try:
    doc = xml.dom.minidom.parse(path)
except Exception as e:
    sys.exit("lineage.xml is not well-formed: %s" % e)

rel = doc.getElementsByTagName("relationship")
if not rel:
    sys.exit("lineage.xml is well-formed but carries no <relationship> elements")
print("ok: XML output, %d relationships" % len(rel))
PY

echo "ok: the standalone dlineage jar runs and produces lineage in both formats"
