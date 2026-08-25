#!/usr/bin/env bash
#
# Fail if any documentation still tells the reader to use something this
# repository deleted.
#
# Every string below was, at some point, the documented way to do something
# here, and every one of them outlived the thing it named. The lineage demo's
# own readme told visitors for months that the demo "cannot be built" and gave
# them `mvn -f pom_dlineage.xml package` -- a file removed on 2026-07-28 -- and
# `java -jar gudusoft.dlineage.jar`, a jar this build has never produced. A
# first-time evaluator who opened that folder concluded the product was broken.
# Nothing went red, because no check reads prose.
#
# So this one does. It is deliberately dumb: fixed strings, no cleverness about
# context. A file whose job is to record that these things died is listed in
# ALLOW; anything else naming them is treated as an instruction to the reader.
#
# Run --self-test to prove the check still catches what it claims to. A grep
# that silently matches nothing would pass this repository forever.

set -euo pipefail

cd "$(dirname "$0")/../.."

# Documents whose subject IS the removal. They have to name what was removed.
#
# docs/maintenance-notes.md was the other entry until 2026-08-25, when it was
# moved out of this repository -- it was maintainer history, not evaluation
# material, and it sat in the README nav bar of a repo people clone to decide
# whether to buy the parser. It also showed the cost of being on this list: it
# was exempt from this check and had quietly drifted (a stale test count, and a
# directory renamed out from under it). Keep this list short.
ALLOW=(
    "README.md"
)

DEAD=(
    "pom_dlineage.xml"
    "gudusoft.dlineage.jar"
    "src/main/java/demos/"
    'src\main\java\demos\'
    ".;lib/*"
    "build.xml"
    "ftp.gudusoft.com"
    "support.sqlparser.com"
)

WHY=(
    "merged into pom.xml on 2026-07-28; build with 'mvn package -DskipTests'"
    "the standalone jar is target/gsp_demo_java-1.0-SNAPSHOT-dlineage.jar"
    "second package root removed 2026-07-27; demos are under src/main/java/gudusoft/gsqlparser/demos/"
    "same, in Windows path form"
    "lib/ was deleted on 2026-07-28; nothing needs a hand-written classpath, use the uber jar"
    "both Ant builds were deleted on 2026-08-25; Maven and the .bat scripts are the two supported routes"
    "does not resolve (NXDOMAIN, checked 2026-08-25)"
    "does not resolve (NXDOMAIN, checked 2026-08-25)"
)

# scan <file>...  -- prints every hit, returns 1 if there was any
scan() {
    local failed=0 f i hits line
    for f in "$@"; do
        for i in "${!DEAD[@]}"; do
            if hits=$(grep -Fn -- "${DEAD[$i]}" "$f" 2>/dev/null); then
                while IFS= read -r line; do
                    printf '  %s:%s\n' "$f" "$line"
                done <<<"$hits"
                printf '    ^ "%s" is gone -- %s\n\n' "${DEAD[$i]}" "${WHY[$i]}"
                failed=1
            fi
        done
    done
    return "$failed"
}

# check_links <file>...  -- every relative markdown link must resolve
#
# Separate from the fixed-string scan above because it catches the other half of
# the same problem: not a document naming something deleted, but a document
# pointing at something that moved. Two had rotted unnoticed before this was
# written -- columnImpact linking ../dlineage from a directory where that
# resolves to antiSQLInjection/dlineage, and search linking ./visitors from
# inside search/ -- and neither was visible to any build.
check_links() {
    python3 - "$@" <<'PY'
import os, re, sys

bad = []
for f in sys.argv[1:]:
    base = os.path.dirname(f)
    try:
        text = open(f, encoding="utf-8").read()
    except OSError as e:
        bad.append((f, "", "unreadable: %s" % e))
        continue
    for m in re.finditer(r"\]\(([^)#\s]+)(?:#[^)\s]*)?\)", text):
        target = m.group(1)
        if target.startswith(("http://", "https://", "mailto:")):
            continue
        resolved = os.path.normpath(os.path.join(base, target))
        if not os.path.exists(resolved):
            bad.append((f, target, resolved))

for f, target, resolved in bad:
    print("  %s -> %s" % (f, target))
    print("    ^ resolves to %s, which does not exist\n" % resolved)

sys.exit(1 if bad else 0)
PY
}

self_test() {
    local tmp rc pass=0 fail=0 i
    tmp=$(mktemp -d)
    trap 'rm -rf "$tmp"' RETURN

    printf 'Build it with `mvn package -DskipTests`.\n' >"$tmp/clean.md"
    if scan "$tmp/clean.md" >/dev/null 2>&1; then
        echo "  ok    a clean document passes"
        pass=$((pass + 1))
    else
        echo "  FAIL  a clean document was reported as stale"
        fail=$((fail + 1))
    fi

    for i in "${!DEAD[@]}"; do
        printf 'run %s to build it\n' "${DEAD[$i]}" >"$tmp/stale.md"
        rc=0
        scan "$tmp/stale.md" >/dev/null 2>&1 || rc=$?
        if [ "$rc" -eq 1 ]; then
            echo "  ok    \"${DEAD[$i]}\" is caught"
            pass=$((pass + 1))
        else
            echo "  FAIL  \"${DEAD[$i]}\" slipped through"
            fail=$((fail + 1))
        fi
    done

    # The link half of the check, proved the same way.
    mkdir -p "$tmp/sub"
    printf 'see [the sub page](sub/page.md)\n' >"$tmp/links-ok.md"
    printf 'placeholder\n' >"$tmp/sub/page.md"
    if check_links "$tmp/links-ok.md" >/dev/null 2>&1; then
        echo "  ok    a resolving relative link passes"
        pass=$((pass + 1))
    else
        echo "  FAIL  a resolving relative link was reported as broken"
        fail=$((fail + 1))
    fi

    printf 'see [the missing page](sub/gone.md)\n' >"$tmp/links-bad.md"
    if check_links "$tmp/links-bad.md" >/dev/null 2>&1; then
        echo "  FAIL  a broken relative link slipped through"
        fail=$((fail + 1))
    else
        echo "  ok    a broken relative link is caught"
        pass=$((pass + 1))
    fi

    printf 'see [the product site](https://www.sqlparser.com/nope)\n' >"$tmp/links-http.md"
    if check_links "$tmp/links-http.md" >/dev/null 2>&1; then
        echo "  ok    an http link is left alone"
        pass=$((pass + 1))
    else
        echo "  FAIL  an http link was treated as a file path"
        fail=$((fail + 1))
    fi

    echo
    if [ "$fail" -gt 0 ]; then
        echo "self-test: $fail of $((pass + fail)) cases FAILED"
        return 1
    fi
    echo "self-test: all $pass cases pass"
}

if [ "${1:-}" = "--self-test" ]; then
    echo "proving check-stale-docs.sh catches what it claims to"
    echo
    self_test
    exit $?
fi

all=()
while IFS= read -r f; do all+=("$f"); done < <(git ls-files '*.md')

if [ "${#all[@]}" -eq 0 ]; then
    echo "::error::no markdown files found to check; is this a git checkout?"
    exit 1
fi

# The dead-string scan skips ALLOW; the link check does not. A file may have a
# reason to name something that was removed, but never to link at something
# that is not there.
files=()
for f in "${all[@]}"; do
    skip=0
    for a in "${ALLOW[@]}"; do
        if [ "$f" = "$a" ]; then skip=1; fi
    done
    if [ "$skip" -eq 0 ]; then files+=("$f"); fi
done

rc=0

echo "checking ${#files[@]} markdown files for references to deleted things"
echo
if scan "${files[@]}"; then
    echo "ok: no documentation points at anything that has been removed"
else
    echo "::error::documentation above still instructs the reader to use something that no longer exists"
    echo "If a file's purpose is to record the removal, add it to ALLOW in $0."
    rc=1
fi

echo
echo "checking relative links in ${#all[@]} markdown files"
echo
if check_links "${all[@]}"; then
    echo "ok: every relative link resolves"
else
    echo "::error::a relative link above points at a path that does not exist"
    rc=1
fi

exit "$rc"
