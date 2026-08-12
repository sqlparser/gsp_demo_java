#!/usr/bin/env bash
#
# Exercise .githooks/pre-commit against a throwaway clone.
#
# The hook exists to stop a parser-version bump that misses one of the four
# POMs. A hook that silently stopped working would be invisible -- it fails
# open by design, so the only symptom is that a bad commit sails through
# months later. This drives it through four cases and asserts on what it
# actually does:
#
#   1. a consistent POM edit           -> commit succeeds
#   2. a drifting POM edit             -> commit is refused
#   3. a commit touching no POM        -> hook stays out of the way
#   4. --no-verify on a drifting edit  -> commit succeeds (the documented escape)
#
# Case 2 is the point; case 1 is what stops it from being a hook that just
# refuses everything, and case 4 keeps the documented bypass honest.

set -uo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")/../.." || exit 1
SRC=$(pwd)

FAILED=0
TMP=$(mktemp -d)
trap 'rm -rf "$TMP"' EXIT

ok()   { printf '  ok    %s\n' "$1"; }
fail() { printf '::error::%s\n' "$1"; FAILED=$((FAILED + 1)); }

# A clone rather than the checkout itself: these cases commit, and no test
# should be able to write to the tree it is testing.
git clone --quiet --no-hardlinks "$SRC" "$TMP/repo" || {
    echo "::error::could not clone the repository"
    exit 1
}
cd "$TMP/repo" || exit 1
git config user.name  "hook test"
git config user.email "hook-test@example.invalid"
git config commit.gpgsign false
git config core.hooksPath .githooks

if [ ! -x .githooks/pre-commit ]; then
    echo "::error::.githooks/pre-commit is missing or not executable in the committed tree"
    echo "         (git tracks the exec bit: chmod +x it and commit the mode change)"
    exit 1
fi

version=$(.github/scripts/set-parser-version.sh --check | sed -n 's/^consistent: //p')
if [ -z "$version" ]; then
    echo "::error::the checked-out tree is already inconsistent; fix that before testing the hook"
    .github/scripts/set-parser-version.sh --check
    exit 1
fi
echo "testing the pre-commit hook against a clone at parser $version"
echo

# ---------------------------------------------------------------- case 1
# A POM edit that keeps all four in agreement must not be blocked.
printf '\n<!-- hook test: consistent edit -->\n' >> pom.xml
git add pom.xml
if git commit --quiet -m "consistent POM edit" >/dev/null 2>&1; then
    ok "a consistent POM edit commits"
else
    fail "the hook blocked a consistent POM edit"
fi

# ---------------------------------------------------------------- case 2
# The real case: bump the root property and leave the connectors behind, which
# is exactly what afb6f3a did.
sed -i.bak "s|<gsp\.core\.version>$version</gsp\.core\.version>|<gsp.core.version>9.9.9</gsp.core.version>|" pom.xml
rm -f pom.xml.bak
if ! grep -q '9\.9\.9' pom.xml; then
    fail "could not stage a drifting edit -- the property is not where this test expects it"
else
    git add pom.xml
    out=$(git commit -m "drifting POM edit" 2>&1)
    if [ $? -eq 0 ]; then
        fail "the hook let a drifting parser version through"
    elif grep -q "disagrees across files" <<<"$out"; then
        ok "a drifting POM edit is refused, and says why"
    else
        fail "the commit was refused but not by the version check: $out"
    fi
fi

# ---------------------------------------------------------------- case 4
# The documented escape hatch, tested while the index is still drifting.
if git commit --quiet --no-verify -m "drifting POM edit, bypassed" >/dev/null 2>&1; then
    ok "--no-verify commits anyway"
else
    fail "--no-verify did not bypass the hook"
fi
git reset --quiet --hard HEAD~1   # back to the consistent commit from case 1

# ---------------------------------------------------------------- case 3
# A commit touching no POM must not pay for the check, or run it at all.
printf '\nhook test\n' >> README.md
git add README.md
if git commit --quiet -m "a commit touching no POM" >/dev/null 2>&1; then
    ok "a commit touching no POM is left alone"
else
    fail "the hook blocked a commit that touches no POM"
fi

echo
if [ "$FAILED" -gt 0 ]; then
    echo "::error::pre-commit hook: $FAILED case(s) failed"
    exit 1
fi
echo "pre-commit hook: all 4 cases pass"
