# General SQL Parser — Java demos

[![Build and test](https://github.com/sqlparser/gsp_demo_java/actions/workflows/build.yml/badge.svg)](https://github.com/sqlparser/gsp_demo_java/actions/workflows/build.yml)
[![Nightly](https://github.com/sqlparser/gsp_demo_java/actions/workflows/nightly.yml/badge.svg)](https://github.com/sqlparser/gsp_demo_java/actions/workflows/nightly.yml)

Runnable sample programs for [General SQL Parser](https://www.sqlparser.com):
syntax checking, SQL formatting, column-level lineage, AST traversal, stored
procedure analysis and SQL rewriting.

Clone and build. The parser is resolved from Gudu's public Maven repository, so
there is nothing to download or install by hand.

```bash
git clone https://github.com/sqlparser/gsp_demo_java.git
cd gsp_demo_java
mvn package -DskipTests
```

**Contents** · [What the library does](#what-the-library-does) ·
[Quick start](#quick-start) · [The demos](#the-demos) ·
[Rewriting SQL](#rewriting-sql-through-the-parse-tree) ·
[Tests](#running-the-tests) · [The parser dependency](#the-parser-dependency) ·
[Project layout](#project-layout) ·
[Standalone lineage tool](#the-standalone-lineage-tool) ·
[Windows .bat scripts](#the-windows-bat-scripts) · [CI](#what-ci-checks) ·
[Contributing](#contributing)

## What the library does

General SQL Parser turns SQL text into a parse tree you can inspect and modify,
in process, with no database connection. You get statement types, tables,
columns, expressions, joins, CTEs and subqueries; you can rewrite the tree and
generate SQL back out, run the formatter over it, or trace column lineage
through a script. It parses vendor dialects as the real thing rather than as
generic SQL.

**Requirements:** Java 8 or later (verified on OpenJDK 8 and 21) and Maven 3.6+.

## Quick start

Check whether some SQL parses:

```bash
cat > q.sql <<'SQL'
SELECT a.id, b.name FROM ta a JOIN tb b ON a.id = b.id WHERE a.x > 1;
SQL

mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.checksyntax.OfflineSyntaxCheck \
    -Dexec.args="/f q.sql /t oracle"
```

```text
Offline SQL syntax validation
Input: .../q.sql
Dialect: oracle
Database connection used: no
Result: ACCEPTED
Statements parsed: 1
```

Reformat it:

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.formatsql.formatsql \
    -Dexec.args="q.sql /t oracle"
```

```text
SELECT a.id,
       b.name
FROM   ta a
       JOIN tb b
       ON a.id = b.id
WHERE  a.x > 1;
```

Java also includes the fault-tolerant `pp2` formatter. It formats parseable
regions through the normal AST formatter and recovers malformed regions with a
token-preserving lexical formatter. The SQL does not need to pass `parse()`
first:

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.formatsql.formatsql \
    -Dexec.args="samples/formatsql/mixed-valid-invalid.sql /t oracle /tolerant"
```

```sql
SELECT 1
FROM   dual;
SELECT FROM
WHERE;
SELECT 2
FROM   dual;
```

The demo writes `Formatter status: OK_WITH_RECOVERY` and the recovered-region
diagnostics to stderr. Formatting is deliberately non-destructive: `pp2`
preserves the invalid tokens; it does not claim to repair or validate them.

Argument conventions differ between demos — `OfflineSyntaxCheck` takes
`/f <file>` and `/t <vendor>`, while `formatsql` takes a bare filename followed
by optional `/t <vendor>` and `/tolerant` flags. The offline validator runs a
built-in Oracle query when no file is supplied.

> **Finding the `-Dexec.mainClass` value.** Every demo is
> `gudusoft.gsqlparser.demos.<demo>.<Class>`, and the directory path under
> `src/main/java/` *is* the package for every demo source file — so read it straight off
> the file's location.
> `src/main/java/gudusoft/gsqlparser/demos/checksyntax/OfflineSyntaxCheck.java`
> is `gudusoft.gsqlparser.demos.checksyntax.OfflineSyntaxCheck`.
>
> Instructions written before 2026-07-27 say things like
> `demos.checksyntax.checksyntax`. Those need the `gudusoft.gsqlparser.` prefix
> now. Before that rename this tree carried four package roots and 263 files
> whose path contradicted their own `package` line; there is one root now, and
> the path under `src/main/java/` *is* the package.

If you have older notes telling you to add `-Dexec.classpathScope=compile`, you
no longer need it. It worked around `system`-scope dependencies that are gone.
Twenty demo readmes still carried it until 2026-08-25; `check-stale-docs.sh`
now keeps it out.

## The demos

Runnable programs under `src/main/java/gudusoft/gsqlparser/demos/`. Common
starting points:

| Demo | What it does |
|------|--------------|
| `checksyntax` | Validate SQL offline and return parser diagnostics without a database connection |
| `formatsql` | Pretty-print SQL, including malformed input with `/tolerant` |
| `gettablecolumns` | Extract table and column names |
| `columnImpact` | Trace column-level impact through SELECTs |
| `dlineage` / `dlineageBasic` | Data lineage analysis |
| `traceColumn` / `tracedatalineage` | Follow a column through a script |
| `analyzesp` | Analyze stored procedures |
| `analyzescript` | Walk a multi-statement script |
| `scriptwriter` / `modifysql` / `sqlrefactor` | Rewrite SQL through the AST |
| `modifySqlAst` | Apply a SELECT policy, modify its AST, regenerate and revalidate SQL |
| `joinConvert` | Convert between old-style and ANSI JOIN syntax |
| `expressionTraverser` / `visitors` | Walk the AST with a visitor |
| `sqltranslator` | Translate SQL between dialects |
| `listGSPInfo` | Print parser version and build info |

Others cover CRUD extraction, join-relation analysis, constant folding, source
tokens, table scanning, anti-SQL-injection checks and benchmarks. **Most demo
directories carry their own `readme.md`** — that is the best documentation for
any individual demo.

`samples/` holds sample `.sql` files to feed them.

### Demos that connect to a database — licensed parser only

`licensed-only/{oracleConnector,snowflakeConnector,sqlServerConnector}/` are
separate, independently built Maven modules showing JDBC-connected metadata
extraction. **They cannot be built with the trial parser**, which does not ship
`gudusoft.gsqlparser.sqlenv.T*SQLDataSource`; each stops at `validate` with a
message saying so, and `-Plicensed` turns that guard off once you have a
licensed parser. They are not part of `mvn package` or `mvn test` at the root.
See [`licensed-only/README.md`](licensed-only/README.md).

They sat in `connector/` until 2026-08-24, where they read as part of the
ordinary demo set: a first-time evaluation started there, met `cannot find
symbol`, and concluded the library did not compile.

**On the trial parser, `columninspect` is the thing to run instead.** It does
the same metadata-aware column resolution from a JSON catalog export rather
than a live connection, and `samples/columninspect/` has a runnable pair.

## Rewriting SQL through the parse tree

The point of a parse tree is that you can change SQL without touching strings.
Most of these take **no arguments** — the query is inline in the source, so they
read as worked examples you can run immediately:

| Want to | Look at | Input |
|---|---|---|
| Add a condition to a `WHERE` clause | [`modifySelect/ModifySelect.java`](src/main/java/gudusoft/gsqlparser/demos/modifySelect/ModifySelect.java) | inline |
| Build a pre-execution policy and rewrite gate | [`modifySqlAst/ModifySqlAst.java`](src/main/java/gudusoft/gsqlparser/demos/modifySqlAst/ModifySqlAst.java) | inline |
| Rename a table throughout a statement | [`modifysql/replaceTablename.java`](src/main/java/gudusoft/gsqlparser/demos/modifysql/replaceTablename.java) | inline |
| Replace a literal constant | [`modifysql/replaceConstant.java`](src/main/java/gudusoft/gsqlparser/demos/modifysql/replaceConstant.java) | inline |
| Append to an existing statement | [`modifysql/add2SQL.java`](src/main/java/gudusoft/gsqlparser/demos/modifysql/add2SQL.java) | inline |
| Convert proprietary joins to ANSI | [`joinConvert/JoinConverter.java`](src/main/java/gudusoft/gsqlparser/demos/joinConvert/) | inline |
| Remove redundant parentheses | [`sqlrefactor/rmdupParenthesis.java`](src/main/java/gudusoft/gsqlparser/demos/sqlrefactor/rmdupParenthesis.java) | `<file.sql> [/t <vendor>]` |
| Regenerate SQL from a tree | [`scriptwriter/scriptwriter.java`](src/main/java/gudusoft/gsqlparser/demos/scriptwriter/scriptwriter.java) | `[<file.sql>]` — its built-in query exceeds the trial limit, so pass your own |

So, for example:

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.modifySelect.ModifySelect
```

## Running the tests

```bash
mvn test                                   # all 156
mvn test -Dtest=ClassName                  # one class
mvn test -Dtest=ClassName#methodName       # one method
```

**156 tests, all passing, nothing skipped.** There are no expected failures, and
every input the suite needs is checked in, so a plain clone runs the whole
thing. Any red test is a real one.

Every test here exercises a **demo in this repository**. Tests for the *parser*
live in the library (`gsp_java_core`), not here — please don't add them here.

## The parser dependency

The parser is **not on Maven Central**. It comes from Gudu's own public Maven
repository, declared in `pom.xml`:

```xml
<repository>
  <id>gudu-public-releases</id>
  <url>https://www.sqlparser.com/maven/</url>
</repository>

<dependency>
  <groupId>com.gudusoft</groupId>
  <artifactId>gsqlparser</artifactId>
  <version>${gsp.core.version}</version>
</dependency>
```

`com.gudusoft:gsqlparser` is the **trial** build. Commercial builds may carry
newer fixes and use a more specific four-part version, so the public Maven
version (e.g. `4.1.9`) does not necessarily match the one in the release notes.

> **The trial build refuses input larger than 10,000 bytes**, reporting
> `trial version can only process query with size of at most 10000 bytes`.
> The limit is on a single parse, not on total throughput.
>
> Every demo here works within that except `scriptwriter`, whose built-in query
> is ~49 KB on purpose — give it your own smaller file, or use a licensed
> parser. **16 of the 89 `.sql` files under `samples/` are also over the
> limit**, all of them vendor schema dumps under `samples/dlineageBasic/`
> (10,378 to 99,139 bytes). Those are for licensed evaluation; the rejection
> arrives as an `<error>` inside otherwise-normal output, which reads as "no
> lineage found" rather than as a licence limit. For schema-scale lineage on
> the trial jar use
> [`samples/dlineageBasic/oracle/hr_mini/`](samples/dlineageBasic/oracle/hr_mini/readme.md)
> — 5,212 bytes, 120 relationships, added for that purpose and size-checked in
> CI.

### Published versions are kept; one batch was recalled in July 2026

**Releases are additive: a published version stays downloadable at its original
URL.** Nothing in the publish pipeline deletes: the upload mirrors the new
version directory in without `--delete`, `maven-metadata.xml` is rebuilt by
*merging* the existing version list and aborts if the result would be shorter,
and an already-published coordinate cannot be overwritten without an explicit
`force`. Since 2026-07-30 each release also asserts, after uploading, that every
previously published version still returns 200 for both its `.jar` and `.pom`,
checked against an append-only ledger in the parser repo rather than against the
server's own metadata.

**One exception happened, and it is why the paragraph above is now enforced
rather than assumed.** On 2026-07-28 at 06:50 UTC the repository served 4.1.4
through 4.1.8; by 10:23 UTC 4.1.9 was published and **all five returned 404**. A
build pinned to 4.1.6 that morning could not resolve its parser by lunchtime.
That was **not** the release mechanism — it was a deliberate one-time recall:
those builds did not carry the trial restrictions they were supposed to, so they
were withdrawn. The publish workflow now compiles a probe against the jar and
refuses to upload unless the restrictions actually bite, so that class of recall
cannot recur.

The residue is that those five are still 404 and are not coming back; everything
published since has stayed put. As of 2026-08-24 the server serves `4.1.9`,
`4.1.11` and `4.2.6` — all three returning 200 for `.jar` and `.pom`, all three
listed in `maven-metadata.xml` — and `4.1.11` went on resolving after `4.2.6`
was published on 2026-08-23. Treat a future removal as what it would be — a
security or licensing recall, announced — not as routine cleanup after a
release.

Available versions:
<https://www.sqlparser.com/maven/com/gudusoft/gsqlparser/maven-metadata.xml>

### Changing the parser version

One command, never by hand:

```bash
.github/scripts/set-parser-version.sh 4.1.9     # move all four files
.github/scripts/set-parser-version.sh --check   # fail if they disagree
```

The version lives in **four** files — the `${gsp.core.version}` property in
`pom.xml`, plus a hardcoded `<version>` in each of the three
`licensed-only/*/pom.xml`, which are separate builds with no parent to inherit a
property from. Both workflows run `--check`, so a missed file is a red build
rather than a connector quietly compiling against an older parser.

In practice **you never edit a version at all**: the nightly's `latest` job
tests the newest release and opens a bump PR only when everything passes, so
bumping is merging a pre-verified PR. The pin is what keeps a fresh clone
reproducible for evaluation.

There is a pre-commit hook for the moments you edit one anyway. It runs the same
`--check` against what you have *staged* and refuses the commit if the POMs
disagree, which is a shorter feedback loop than a red build:

```bash
git config core.hooksPath .githooks   # once per clone; .git/hooks is not versioned
git commit --no-verify                # bypass, for a deliberately half-done bump
```

It stays out of the way of any commit that touches no POM, and fails open if
python is missing rather than blocking you.

> **If the `pinned` job goes red while `latest` is green, treat it as urgent.**
> The pinned version has stopped resolving while the newest one still does —
> normally that means it was recalled (see above), so anyone pinned to it is
> broken too and needs to hear about it.

### Building against a local parser

To run the demos against a parser you just compiled instead of the published one
(needs the `gsp_java` library checkout as a sibling directory):

```bash
cd gsp_java
mvn install -N && mvn install -pl gsp_java_core -Pquick_install

cd ../gsp_demo_java
mvn -Plocal -Dgsp.core.version=<installed version> compile
```

The `local` profile resolves the parser under groupId `gudusoft` (the
locally-installed core) rather than `com.gudusoft` (the published trial jar).

## Project layout

```
src/main/java/gudusoft/gsqlparser/demos/<demo>/   the demos, one dir per topic
src/main/resources/                               classpath resources (one file)
src/test/java/gudusoft/gsqlparser/                tests, all exercising demos
samples/                                          sample .sql for the demos
licensed-only/<vendor>Connector/                  JDBC modules, licensed parser only
lib-repo/                                         in-project Maven repository
setenv/ + per-demo *.bat                          the Windows route
.github/scripts/                                  CI checks, all runnable locally
```

Four rules worth knowing before you add anything:

- **`gudusoft/` is the only package root under `src/main/java`, and path always
  equals package.** Put a new demo in
  `src/main/java/gudusoft/gsqlparser/demos/<demo>/` declaring the matching
  package.
- **Sample SQL goes in `samples/`,** not under `src/`. It is passed on the
  command line, never read from the classpath.
- **Anything loaded with `getResourceAsStream` must live in
  `src/main/resources/`** under its package path. A copy in `src/main/java` is
  not on the classpath — that bug silently broke the snowflake demo.
- **Add dependencies by coordinate, not by committing a jar.** See
  [`lib-repo/readme.md`](lib-repo/readme.md); a vendored jar is invisible to
  Dependabot and skipped by Maven's packaging plugins.

## The standalone lineage tool

`mvn package` produces a second, self-contained jar alongside the ordinary one:

```
target/gsp_demo_java-1.0-SNAPSHOT.jar            the demos, needs a classpath
target/gsp_demo_java-1.0-SNAPSHOT-dlineage.jar   standalone, runs on its own
```

The second is an executable uber jar (`maven-shade-plugin`) with every runtime
dependency inside it, so running the data-lineage analyser needs no classpath:

```bash
java -jar target/gsp_demo_java-1.0-SNAPSHOT-dlineage.jar \
     /f samples/dlineage/demo.sql /o lineage.json /json /graph \
     /simpleShowRelationTypes fdd,fdr /filterRelationTypes fdd
```

Omit `/json` to get XML. Other options include `/t mssql`, `/t postgresql`,
`/showER` and `/filterRelationTypes fdd`.

Because it carries every demo class, it also doubles as a no-setup way to run
any other demo:

```bash
java -cp target/gsp_demo_java-1.0-SNAPSHOT-dlineage.jar \
     gudusoft.gsqlparser.demos.checksyntax.checksyntax /f q.sql /t oracle
```

Check it yourself with `.github/scripts/smoke-dlineage-jar.sh`, which is what CI
runs.

> This replaced `pom_dlineage.xml`, a second POM that broke four separate times
> without a build ever going red: it pinned a parser jar predating the APIs its
> own source used; it inherited from a private parent published nowhere, so
> nobody outside Gudu could read it at all; its documented run command needed a
> directory only the Windows route creates; and it lacked a JAXB dependency the
> root POM already had, so it compiled and then died on every JDK past 8. Each
> was invisible because CI only ever *built* it. Think hard before adding a
> second POM.

## The Windows .bat scripts

Each demo directory also ships `compile_<demo>.bat` and `run_<demo>.bat`, with
`setenv/setenv.bat` holding the shared environment. This is the original
no-Maven workflow:

```
cd src\main\java\gudusoft\gsqlparser\demos\checksyntax
compile_checksyntax.bat
run_checksyntax.bat /f ..\..\..\..\..\..\..\q.sql /t oracle
```

That is the whole thing — you do not edit `setenv.bat` first. It keeps whatever
`JAVA_HOME` is already set, and on first use calls `setenv/fetch-parser.bat`,
which runs `mvn dependency:copy-dependencies` into the gitignored
`external_lib/`. So the `.bat` route needs **Maven once**, to fetch, and never
again. Delete `external_lib/` to force a refetch after changing a dependency.

No jar is committed to this repository, and no version is named anywhere in the
`.bat` family — everything is read from `pom.xml`, so the two routes cannot
drift apart.

If you move a demo, fix its two `.bat` files' paths and `cd` depth too.

## What CI checks

Two workflows, and the important property is that they **run** things rather
than only building them.

`.github/workflows/build.yml` — on every push and pull request:

| check | detail |
|---|---|
| Parser version consistency | `set-parser-version.sh --check` across all four POMs |
| The pre-commit hook | `test-pre-commit-hook.sh`: a drifting bump is refused in a throwaway clone |
| Documentation | `check-stale-docs.sh`: no readme names a deleted thing (`pom_dlineage.xml`, `gudusoft.dlineage.jar`, the old `demos` package root, the Ant builds, two dead download hosts, `-Dexec.classpathScope`), and every relative link resolves; `--self-test` first, so a check that matches nothing cannot pass as a clean repo |
| Licensed-only guard | `check-licensed-only-guard.sh`: each `licensed-only/*` module stops at `validate` **with the licence message**, not with `cannot find symbol` |
| Build and test | JDK 8 and 21; 156 tests, and a run that skipped everything fails |
| Demo smoke test | `checksyntax` against known SQL |
| Standalone lineage jar | `smoke-dlineage-jar.sh` on JDK 8 and 21 — asserts on **output**, in JSON *and* XML, and that `hr_mini.sql` stays under the trial parser's 10,000-byte cap |
| Windows `.bat` | `windows-latest`: bootstrap, 39 compile scripts, 50 run scripts, 4 driven with real arguments |

`.github/workflows/nightly.yml` — at 03:17 UTC, because the parser is the moving
part that lives outside this repository:

| job | parser | JDK |
|---|---|---|
| `pinned` | `${gsp.core.version}` from `pom.xml` | 8 and 21 |
| `latest` | newest release on sqlparser.com, resolved at run time | 21 |
| `windows-bat` | fetched fresh by `setenv\fetch-parser.bat` | 8 |

**`latest` is the job that earns the nightly.** Red `latest` with green `pinned`
means a new parser release broke the demos. Green `latest` with a newer version
means `gsp.core.version` can be bumped, and it opens that PR itself — then
approves the PR's own `build.yml` run, which GitHub otherwise parks in
`action_required` because `github-actions[bot]` counts as a first-time
contributor. Check that a bump PR's build really ran before merging: a blocked
run reports as "no checks reported", not as blocked.

`.github/workflows/red-master.yml` — after either of those finishes on `master`:

| conclusion | what happens |
|---|---|
| first failure | opens **one** issue labelled `ci-red`, assigned to whoever triggered the run |
| further failures | a comment on that issue, not a second issue |
| green again | closes it, but only once the newest completed run of *both* workflows passed |

It exists because the checks were never the weak part. In August 2026 a bad
parser bump was caught by the push build inside a minute and stayed on master
for 21 hours regardless, because a red run leaves no trace in any view you open
for another reason. To see it work without breaking master, run it from the
Actions tab: it defaults to a dry run that makes every query and prints the
mutations instead of applying them.

Every check is a script under `.github/scripts/`, runnable locally:

```bash
.github/scripts/test-pre-commit-hook.sh    # the hook refuses a drifting version bump
.github/scripts/check-test-results.sh      # surefire XML: no failures, not all skipped
.github/scripts/run-all-demos.sh           # every class with a main() starts
.github/scripts/run-demo-cases.sh          # demos driven with real args, output checked
.github/scripts/smoke-dlineage-jar.sh      # the standalone jar produces real lineage
```

Point them at a different parser exactly as the `latest` job does:

```bash
mvn -q compile -Dgsp.core.version=4.1.9
MVN_ARGS="-Dgsp.core.version=4.1.9" .github/scripts/run-demo-cases.sh
```

**What this does not cover:** 13 of the 49 demo folders ship no `.bat` at all
and are Maven-only (the Linux job covers them), and the `.bat` launch phase
asserts only that a script starts, not what it prints, for the 46 it does not
drive with arguments.

## Contributing

- **Wire new things into CI in the same commit.** Nearly every bug this
  repository has had shares one cause: something nothing ran. Building is not
  running — assert on output, not exit status.
- **One package root**, path equals package. Don't add a second.
- **Don't commit jars**; add dependencies by coordinate.
- **Don't add a live-JDBC path to a demo** under `src/main/java`. It can't run
  in CI, and it is what got two demos excluded from the build for years. The
  `licensed-only/*` modules are where database connections belong.
- **Don't add parser tests here**; they belong in `gsp_java_core`.

`master` tracks released GSP versions from
<https://sqlparser.com/download.php>. `dev` branches move faster and may not
compile against the parser `pom.xml` pins.

## Links

- Product site: <https://www.sqlparser.com>
- Java documentation: <https://docs.sqlparser.com>
- Quick start: <https://docs.sqlparser.com/quick-start/>
- .NET demos: <https://github.com/sqlparser/gsp_demo_dotnet>
