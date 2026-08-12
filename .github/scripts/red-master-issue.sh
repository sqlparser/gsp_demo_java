#!/usr/bin/env bash
#
# Open, update, and close the one GitHub issue that tracks "master is red".
#
# Why this exists: on 2026-08-10 a commit bumped the parser version in pom.xml
# and missed the three connector POMs. CI caught it *within the same minute* --
# and it stayed broken for 21 hours, through a second failing run, because a red
# build on a repository nobody is actively watching is a grey checkmark in a list
# that nobody loads. The check was never the weak part; noticing was.
#
# So a failure gets something that follows you: an issue, assigned to whoever
# triggered the run, which stays open until master is actually green again and
# closes itself when it is.
#
# Called by .github/workflows/red-master.yml. Reads its input from the
# environment so the workflow_run payload and a manual dry run can both drive it:
#
#   CONCLUSION   success | failure | cancelled | ...
#   BRANCH       branch the run was on; anything but master is ignored
#   RUN_NAME     display name of the workflow that finished
#   RUN_URL      link to it
#   RUN_ID       its id, used to avoid commenting twice about one run
#   SHA          commit it ran against
#   ACTOR        who to assign; best effort, a non-assignable login is not fatal
#   DRY_RUN      1 to print every mutation instead of making it
#
# Requires gh, authenticated with issues:write and actions:read.

set -uo pipefail

LABEL="ci-red"
TITLE="CI is red on master"
# The workflows whose result decides whether master is green. Must match the
# `name:` of each, and the list in red-master.yml's `workflows:` trigger.
WATCHED=("Build and test" "Nightly")

CONCLUSION="${CONCLUSION:-}"
BRANCH="${BRANCH:-}"
RUN_NAME="${RUN_NAME:-a workflow}"
RUN_URL="${RUN_URL:-}"
RUN_ID="${RUN_ID:-}"
SHA="${SHA:-}"
ACTOR="${ACTOR:-}"
DRY_RUN="${DRY_RUN:-0}"

say() { printf '%s\n' "$*"; }

# Every mutation goes through here, so --dry-run cannot half-apply.
run() {
    if [ "$DRY_RUN" = "1" ]; then
        say "DRY RUN would: $*"
        return 0
    fi
    "$@"
}

if [ "$BRANCH" != "master" ]; then
    say "run was on '$BRANCH', not master; nothing to do"
    exit 0
fi

short_sha="${SHA:0:7}"

# The open tracker, if there is one. `gh issue list` filters to open by default;
# being explicit because this decides whether we create or comment.
existing=$(gh issue list --label "$LABEL" --state open --limit 1 \
             --json number --jq '.[0].number // empty' 2>/dev/null)

case "$CONCLUSION" in
failure|timed_out)
    if [ -z "$existing" ]; then
        # The label may not exist yet on a fresh repository. --force makes this
        # idempotent instead of failing the second time.
        run gh label create "$LABEL" \
            --color B60205 \
            --description "master is failing CI" \
            --force >/dev/null 2>&1 || true

        body="**\`$RUN_NAME\` failed on \`master\`.**

- commit: \`$short_sha\`
- run: $RUN_URL

This issue is opened automatically by [\`red-master.yml\`](https://github.com/${GITHUB_REPOSITORY:-sqlparser/gsp_demo_java}/blob/master/.github/workflows/red-master.yml) the first time a run fails on \`master\`, and closes itself once every watched workflow is green again. Later failures are added as comments rather than as new issues.

Please do not close it by hand while master is still red -- a closed issue is how the last one went unnoticed for 21 hours."

        if [ "$DRY_RUN" = "1" ]; then
            say "DRY RUN would: create issue '$TITLE' (label $LABEL, assignee ${ACTOR:-none})"
            say "--- body ---"
            say "$body"
            say "------------"
        else
            # `gh issue create` prints the new issue's URL; there is no --json
            # on it. Assign in a second call: an actor who is not a repository
            # collaborator cannot be assigned, and that must not cost us the
            # issue itself.
            num=$(gh issue create --title "$TITLE" --label "$LABEL" --body "$body" \
                    | sed -n 's|.*/issues/\([0-9][0-9]*\).*|\1|p')
            if [ -z "$num" ]; then
                say "::error::could not create the tracking issue"
                exit 1
            fi
            say "opened #$num"
            if [ -n "$ACTOR" ] && [[ "$ACTOR" != *"[bot]" ]]; then
                gh issue edit "$num" --add-assignee "$ACTOR" >/dev/null 2>&1 \
                    && say "assigned to @$ACTOR" \
                    || say "could not assign @$ACTOR (not a collaborator?); left unassigned"
            fi
        fi
    else
        # One comment per failing run, not per re-read of the same one.
        if [ -n "$RUN_ID" ] && gh issue view "$existing" --json comments \
             --jq '.comments[].body' 2>/dev/null | grep -qF "/runs/$RUN_ID"; then
            say "#$existing already mentions run $RUN_ID; not commenting twice"
            exit 0
        fi
        run gh issue comment "$existing" --body \
"Still red: **\`$RUN_NAME\`** failed on \`$short_sha\`.

$RUN_URL"
        say "commented on #$existing"
    fi
    ;;

success)
    if [ -z "$existing" ]; then
        say "master is green and no tracking issue is open; nothing to do"
        exit 0
    fi

    # One green run does not mean master is green: build.yml can pass while the
    # nightly is still failing. Close only when the newest completed run of
    # every watched workflow succeeded.
    still_red=""
    for wf in "${WATCHED[@]}"; do
        latest=$(gh run list --workflow "$wf" --branch master --status completed \
                   --limit 1 --json conclusion --jq '.[0].conclusion // empty' 2>/dev/null)
        say "  latest completed '$wf' on master: ${latest:-none}"
        case "$latest" in
            ""|success|skipped|neutral) ;;
            *) still_red="$still_red $wf" ;;
        esac
    done

    if [ -n "$still_red" ]; then
        say "leaving #$existing open;$still_red is still failing"
        exit 0
    fi

    run gh issue comment "$existing" --body \
"Green again: every watched workflow passed on \`$short_sha\`.

$RUN_URL"
    run gh issue close "$existing" --reason completed
    say "closed #$existing"
    ;;

*)
    say "conclusion '$CONCLUSION' is neither success nor failure; ignoring"
    ;;
esac
