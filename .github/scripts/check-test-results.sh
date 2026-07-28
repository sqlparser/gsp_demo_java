#!/usr/bin/env bash
#
# Assert the test suite failed in exactly the way it is expected to, by NAME.
#
# Three tests in analyzespTest compare stored-procedure output against golden
# strings written for an older parser build, and are kept deliberately as a
# drift signal rather than deleted (see the README). So "all tests pass" here
# means "nothing fails except those three".
#
# Counting failures is not enough: if analyzespTest#testSample1 started passing
# on the same run that a different test started failing, the count would still
# read 3 and the build would go green over a real regression. This checks the
# identities.
#
# Usage:  .github/scripts/check-test-results.sh [surefire-reports-dir]
#
# Exit codes:
#   0  only the known failures failed
#   1  an unexpected test failed, or a known failure started passing without
#      this list being updated, or no reports were produced

set -uo pipefail

REPORTS="${1:-target/surefire-reports}"

# Keep in step with the README and pom.xml. When these are fixed, remove them
# here in the same commit that updates the README.
export KNOWN="gudusoft.gsqlparser.demosTest.analyzespTest#testSample1
gudusoft.gsqlparser.demosTest.analyzespTest#testSample6
gudusoft.gsqlparser.demosTest.analyzespTest#testSample8"

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
known = set(os.environ["KNOWN"].split("\n"))

total, failing = 0, set()
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
        for kind in ("failure", "error"):
            if tc.find(kind) is not None:
                failing.add("%s#%s" % (tc.get("classname"), tc.get("name")))

print("%d tests, %d failing" % (total, len(failing)))

if total == 0:
    print("::error::the reports contain no test cases at all")
    sys.exit(1)

unexpected = sorted(failing - known)
fixed      = sorted(known - failing)

for t in unexpected:
    print("::error::unexpected test failure: %s" % t)
for t in fixed:
    print("::notice::%s now passes -- drop it from KNOWN in "
          ".github/scripts/check-test-results.sh and update the README" % t)

if unexpected:
    sys.exit(1)
# A known failure starting to pass is good news, but silently tolerating it
# would let the list rot until it protects nothing. Make it a build failure so
# the list gets trimmed.
if fixed:
    sys.exit(1)

print("only the %d known analyzespTest failures failed" % len(known))
PY
