#!/usr/bin/env bash
#
# Assert the test suite came out clean, and report what was skipped.
#
# Until 2026-07-28 this asserted "exactly three known analyzespTest failures",
# because those three were believed to be golden strings written for an older
# parser. They were not: gspCommon.BASE_SQL_DIR pointed one directory level
# short of the shared SQL corpus, so the input file was never found,
# Analyze_SP returned an empty string, and the comparison failed. With the path
# corrected the expected strings match the current parser's output exactly, and
# the suite has no expected failures at all. Any failure is now a real one.
#
# Skips are reported rather than ignored. Tests that read the shared corpus skip
# themselves when it is absent, which is the normal state on CI, since the
# corpus lives in the gsp_java library repository under a directory named
# private and is not published. A skip is therefore expected on CI and means
# "not covered here", not "passed".
#
# Usage:  .github/scripts/check-test-results.sh [surefire-reports-dir]
#
# Exit codes:
#   0  no failures and no errors
#   1  something failed, or no reports were produced, or every test was skipped

set -uo pipefail

REPORTS="${1:-target/surefire-reports}"

if [ ! -d "$REPORTS" ]; then
    echo "::error::no surefire reports at $REPORTS -- the test run did not get far enough to produce any"
    exit 1
fi

PYTHON=$(command -v python3 || command -v python) || {
    echo "::error::python is required to parse the surefire XML"
    exit 1
}

"$PYTHON" - "$REPORTS" <<'PY'
import glob, os, sys, xml.etree.ElementTree as ET

reports = sys.argv[1]
total = 0
bad = []
skipped = []

files = glob.glob(os.path.join(reports, "*.xml"))
if not files:
    print("::error::no surefire XML reports found in %s" % reports)
    sys.exit(1)

for f in files:
    try:
        root = ET.parse(f).getroot()
    except ET.ParseError as e:
        print("::error::unreadable surefire report %s: %s" % (f, e))
        sys.exit(1)
    for tc in root.iter("testcase"):
        total += 1
        name = "%s#%s" % (tc.get("classname"), tc.get("name"))
        if tc.find("skipped") is not None:
            skipped.append(name)
            continue
        for kind in ("failure", "error"):
            node = tc.find(kind)
            if node is not None:
                bad.append((name, kind, (node.get("message") or "").strip().splitlines()[:1]))

print("%d tests, %d failed, %d skipped" % (total, len(bad), len(skipped)))

if total == 0:
    print("::error::the reports contain no test cases at all")
    sys.exit(1)

for name, kind, msg in sorted(bad):
    print("::error::%s: %s%s" % (kind, name, (" -- " + msg[0]) if msg else ""))

if skipped:
    print("::notice::%d test(s) skipped, needing the shared SQL corpus from the "
          "gsp_java repository (expected on CI):" % len(skipped))
    for name in sorted(skipped):
        print("    %s" % name)

# Everything skipped means the run proved nothing, which should not read as a
# pass just because no assertion got as far as failing.
if len(skipped) == total:
    print("::error::every test was skipped; the suite proved nothing")
    sys.exit(1)

if bad:
    sys.exit(1)

print("clean: no failures, no errors")
PY
