#!/usr/bin/env bash
#
# Prove the licensed-only modules refuse to build with an explanation.
#
# licensed-only/{oracle,snowflake,sqlServer}Connector need a LICENSED parser:
# the public trial artifact this repository resolves does not ship
# gudusoft.gsqlparser.sqlenv.T*SQLDataSource. Before 2026-08-24 they simply
# failed -- javac's "cannot find symbol" for snowflake, and for the other two a
# dependency-resolution error about a JDBC jar nobody had, thrown before any
# plugin could speak. A first-time evaluator read that as "this library does not
# compile" and said so in an evaluation report.
#
# Now each POM stops at validate with a message naming the licensing boundary
# and pointing at the trial-friendly alternative. That message is the whole
# feature, so this asserts on the message, not on the exit status: a build that
# fails for the old confusing reason also exits non-zero.
#
# Usage:
#   check-licensed-only-guard.sh

set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")/../.."

MODULES=(oracleConnector snowflakeConnector sqlServerConnector)
NEEDLE="needs a LICENSED General SQL Parser"

failed=0

for m in "${MODULES[@]}"; do
    pom="licensed-only/$m/pom.xml"
    if [ ! -f "$pom" ]; then
        echo "  FAIL  $pom is missing"
        failed=1
        continue
    fi

    out=$(mvn -B -f "$pom" validate 2>&1 || true)

    if ! grep -qF "$NEEDLE" <<<"$out"; then
        echo "  FAIL  $m built or failed without explaining the licence boundary"
        echo "$out" | tail -20 | sed 's/^/        /'
        failed=1
        continue
    fi

    # It must be the guard that stopped it, not something incidental that
    # happened to print the same words.
    if ! grep -q "BUILD FAILURE" <<<"$out"; then
        echo "  FAIL  $m printed the message but the build succeeded"
        failed=1
        continue
    fi

    echo "  ok    $m stops at validate and says why"
done

echo

if [ "$failed" -ne 0 ]; then
    echo "::error::a licensed-only module no longer refuses the trial build with an explanation"
    exit 1
fi

echo "ok: all ${#MODULES[@]} licensed-only modules refuse the trial build with an explanation"
