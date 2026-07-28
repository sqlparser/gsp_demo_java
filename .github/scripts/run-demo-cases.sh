#!/usr/bin/env bash
#
# Drive the demos listed in demo-cases.tsv with real arguments and check each
# one's output against a string it must contain.
#
# run-all-demos.sh proves every demo still starts. This proves a representative
# set still produces the right answer, which is the part that catches a parser
# upgrade changing behaviour rather than breaking linkage.
#
# Usage:
#   .github/scripts/run-demo-cases.sh [--timeout SECONDS]
#
# Exit codes:
#   0  every case produced its expected output
#   1  at least one case failed, or the classpath could not be built

set -uo pipefail

TIMEOUT=120
while [ $# -gt 0 ]; do
    case "$1" in
        --timeout) TIMEOUT="$2"; shift 2 ;;
        *) echo "unknown flag: $1" >&2; exit 2 ;;
    esac
done

cd "$(dirname "${BASH_SOURCE[0]}")/../.." || exit 1
ROOT=$(pwd)
CASES="$ROOT/.github/scripts/demo-cases.tsv"

[ -f "$CASES" ] || { echo "::error::$CASES not found"; exit 1; }
[ -d "$ROOT/target/classes" ] || { echo "::error::target/classes missing; run mvn compile first"; exit 1; }

WORK=$(mktemp -d)
trap 'rm -rf "$WORK"' EXIT

# Extra Maven arguments, e.g. MVN_ARGS="-Dgsp.core.version=4.1.8". Without this
# the classpath would always resolve the version pinned in pom.xml, so a job
# testing a different parser would run new classes against the old jar.
read -r -a EXTRA_MVN <<< "${MVN_ARGS:-}"

echo "== building classpath ${MVN_ARGS:+($MVN_ARGS)} =="
if ! mvn -B -q ${EXTRA_MVN[@]+"${EXTRA_MVN[@]}"} dependency:build-classpath \
        -Dmdep.outputFile="$WORK/cp.txt" \
        -Dmdep.includeScope=compile > "$WORK/cp.log" 2>&1; then
    echo "::error::could not build the classpath"
    tail -40 "$WORK/cp.log"
    exit 1
fi
CP="$ROOT/target/classes:$(cat "$WORK/cp.txt")"

# The scratch inputs the placeholders point at. %SQL% deliberately exercises a
# join with an alias and a WHERE clause, so lineage and column demos have
# something to resolve rather than a trivial single-table select.
mkdir -p "$WORK/out"
printf 'SELECT a.id, b.name FROM ta a JOIN tb b ON a.id = b.id WHERE a.x > 1;\n' > "$WORK/q.sql"

PASS=0
FAIL=0
FAILED_CASES=()

while IFS= read -r line; do
    case "${line:-}" in ''|'#'*) continue ;; esac

    # Split on tab by hand. `IFS=$'\t' read -r cls args expect` looks like the
    # obvious way to do this and is wrong: tab is an IFS *whitespace*
    # character, so bash collapses a run of them into a single delimiter. Every
    # no-argument row (class, empty args, expected string) then parsed as
    # class + args, leaving the expectation empty and the case checking
    # nothing while still reporting ok.
    cls=${line%%$'\t'*}
    rest=${line#*$'\t'}
    if [ "$rest" = "$line" ]; then
        args=""; expect=""
    else
        args=${rest%%$'\t'*}
        if [ "${rest#*$'\t'}" = "$rest" ]; then expect=""; else expect=${rest#*$'\t'}; fi
    fi

    # An empty expectation is now an authoring error rather than a silent pass.
    # A case that genuinely only asserts exit 0 has to say so with a literal -.
    if [ -z "$expect" ]; then
        echo "::error::demo-cases.tsv: '$cls' has no expected string. Use - if exit 0 is the whole check."
        FAIL=$((FAIL + 1)); FAILED_CASES+=("$cls: no expected string")
        continue
    fi
    [ "$expect" = "-" ] && expect=""

    args=${args//%SQL%/$WORK/q.sql}
    args=${args//%OUT%/$WORK/out}
    args=${args//%REPO%/$ROOT}

    # args is a pre-split argument string from the table, so the word splitting
    # below is the point rather than an oversight.
    # shellcheck disable=SC2086
    out=$(cd "$WORK" && timeout "$TIMEOUT" java -cp "$CP" "$cls" $args < /dev/null 2>&1)
    rc=$?

    short=${cls##gudusoft.gsqlparser.demos.}

    if [ "$rc" = "124" ]; then
        printf 'FAIL %-52s still running after %ss\n' "$short" "$TIMEOUT"
        FAIL=$((FAIL + 1)); FAILED_CASES+=("$short: timeout")
        continue
    fi
    if [ "$rc" != "0" ]; then
        printf 'FAIL %-52s exit %s\n' "$short" "$rc"
        sed 's/^/       /' <<<"$out" | head -12
        FAIL=$((FAIL + 1)); FAILED_CASES+=("$short: exit $rc")
        continue
    fi
    if [ -n "${expect:-}" ] && ! grep -qF -- "$expect" <<<"$out"; then
        printf 'FAIL %-52s output did not contain: %s\n' "$short" "$expect"
        sed 's/^/       /' <<<"$out" | head -12
        FAIL=$((FAIL + 1)); FAILED_CASES+=("$short: missing \"$expect\"")
        continue
    fi
    printf '  ok %-52s %s\n' "$short" "${expect:-exit 0}"
    PASS=$((PASS + 1))
done < "$CASES"

echo
echo "$((PASS + FAIL)) cases: $PASS passed, $FAIL failed"
if [ "$FAIL" -gt 0 ]; then
    for f in "${FAILED_CASES[@]}"; do echo "::error::demo case failed -- $f"; done
    exit 1
fi
exit 0
