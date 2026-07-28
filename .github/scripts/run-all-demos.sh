#!/usr/bin/env bash
#
# Launch every demo in this repository and report which ones could not start.
#
# Why "could not start" rather than "produced the right answer": the demos take
# wildly different arguments, and most print a usage line when given none. What
# is worth checking on every nightly is the class of failure a parser upgrade
# actually causes -- a class or method that no longer exists -- which surfaces
# as NoClassDefFoundError, NoSuchMethodError, ClassNotFoundException or a
# failure in static initialisation, regardless of arguments. Demos driven with
# real arguments and checked against expected output live in
# .github/scripts/demo-cases.tsv and run separately.
#
# Usage:
#   .github/scripts/run-all-demos.sh [--timeout SECONDS] [--jobs N]
#
# Exit codes:
#   0  every demo started
#   1  at least one demo failed to start, or the classpath could not be built

set -uo pipefail

TIMEOUT=60
JOBS=1
while [ $# -gt 0 ]; do
    case "$1" in
        --timeout) TIMEOUT="$2"; shift 2 ;;
        --jobs)    JOBS="$2";    shift 2 ;;
        *) echo "unknown flag: $1" >&2; exit 2 ;;
    esac
done

cd "$(dirname "${BASH_SOURCE[0]}")/../.." || exit 1
ROOT=$(pwd)

WORK=$(mktemp -d)
trap 'rm -rf "$WORK"' EXIT

# Extra Maven arguments, e.g. MVN_ARGS="-Dgsp.core.version=4.1.8". Without this
# the classpath would always resolve the version pinned in pom.xml, so a job
# testing a different parser would run new classes against the old jar and
# report a pass that means nothing.
read -r -a EXTRA_MVN <<< "${MVN_ARGS:-}"

echo "== building classpath ${MVN_ARGS:+($MVN_ARGS)} =="
# compile scope covers everything the demos need. It used to be load-bearing for
# a different reason: three dependencies were <scope>system</scope> jars under
# lib/, and system scope is on the compile classpath but not the runtime one,
# which is why the demos were all documented as needing
# -Dexec.classpathScope=compile. Those became ordinary Maven coordinates when
# pom_dlineage.xml was merged into pom.xml, so that caveat no longer applies.
if ! mvn -B -q ${EXTRA_MVN[@]+"${EXTRA_MVN[@]}"} dependency:build-classpath \
        -Dmdep.outputFile="$WORK/cp.txt" \
        -Dmdep.includeScope=compile > "$WORK/cp.log" 2>&1; then
    echo "::error::could not build the classpath"
    tail -40 "$WORK/cp.log"
    exit 1
fi
CP="$ROOT/target/classes:$(cat "$WORK/cp.txt")"

if [ ! -d "$ROOT/target/classes" ]; then
    echo "::error::target/classes does not exist; run mvn compile first"
    exit 1
fi

# Every class carrying a main(), taken from the compiled output rather than from
# the sources, so anything pom.xml excludes from the build is excluded here too
# instead of being reported as a phantom failure.
mapfile -t DEMOS < <(
    cd "$ROOT/target/classes" || exit 1
    find . -name "*.class" ! -name "*\$*" -print0 |
        xargs -0 -r grep -l "main" 2>/dev/null |
        sed 's#^\./##; s#\.class$##; s#/#.#g' |
        sort
)

# grep -l on bytecode is a prefilter, not proof: it matches any class whose
# constant pool contains "main". Confirm a real entry point with javap.
CONFIRMED=()
for c in "${DEMOS[@]}"; do
    if javap -classpath "$ROOT/target/classes" -public "$c" 2>/dev/null |
            grep -q "public static void main(java.lang.String"; then
        CONFIRMED+=("$c")
    fi
done

echo "== launching ${#CONFIRMED[@]} demos (timeout ${TIMEOUT}s each, ${JOBS} at a time) =="
echo

run_one() {
    local cls="$1" out rc
    out=$(cd "$WORK" && timeout "$TIMEOUT" java -cp "$CP" "$cls" < /dev/null 2>&1)
    rc=$?

    # A demo that cannot start. Everything else -- a usage line, a complaint
    # about missing arguments, a non-zero exit -- is a demo that started and
    # then declined to do anything useful without input, which is correct.
    if grep -qE "ClassNotFoundException|NoClassDefFoundError|NoSuchMethodError|NoSuchFieldError|UnsupportedClassVersionError|ExceptionInInitializerError|Main method not found|IncompatibleClassChangeError" <<<"$out"; then
        printf 'FAIL %s\n' "$cls"
        sed 's/^/       /' <<<"$out" | head -15
        return 1
    fi
    if [ "$rc" = "124" ]; then
        printf 'HANG %s (still running after %ss)\n' "$cls" "$TIMEOUT"
        return 1
    fi
    printf '  ok %s\n' "$cls"
    return 0
}
export -f run_one
export CP TIMEOUT WORK

FAILED=0
if [ "$JOBS" -gt 1 ] && command -v xargs >/dev/null; then
    printf '%s\n' "${CONFIRMED[@]}" |
        xargs -P "$JOBS" -I{} bash -c 'run_one "$@"' _ {} > "$WORK/out.txt" 2>&1
    FAILED=$(grep -cE "^(FAIL|HANG) " "$WORK/out.txt")
    cat "$WORK/out.txt"
else
    for c in "${CONFIRMED[@]}"; do
        run_one "$c" || FAILED=$((FAILED + 1))
    done
fi

echo
echo "launched ${#CONFIRMED[@]} demos, ${FAILED} failed to start"
[ "$FAILED" -eq 0 ] || {
    echo "::error::${FAILED} demo(s) could not start"
    exit 1
}
exit 0
