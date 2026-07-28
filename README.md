# General SQL Parser — Java Demos

[![Build and test](https://github.com/sqlparser/gsp_demo_java/actions/workflows/build.yml/badge.svg)](https://github.com/sqlparser/gsp_demo_java/actions/workflows/build.yml)
[![Nightly](https://github.com/sqlparser/gsp_demo_java/actions/workflows/nightly.yml/badge.svg)](https://github.com/sqlparser/gsp_demo_java/actions/workflows/nightly.yml)

Runnable sample programs for [General SQL Parser](https://www.sqlparser.com):
syntax checking, SQL formatting, column-level lineage, AST traversal, stored
procedure analysis and SQL rewriting.

Clone this repository and build it. The parser comes from Gudu's public Maven
repository, so there is nothing to download or install by hand.

## What the library does

General SQL Parser turns SQL text into a parse tree you can inspect and modify,
in process, with no database connection. You get statement types, tables,
columns, expressions, joins, CTEs and subqueries; you can rewrite the tree and
generate SQL back out, run the formatter over it, or trace column lineage
through a script. It parses vendor dialects as the real thing rather than as
generic SQL.

## Requirements

- Java 8 or later (verified here on OpenJDK 21)
- Maven 3.6+

## Get started

```bash
git clone https://github.com/sqlparser/gsp_demo_java.git
cd gsp_demo_java
mvn package -DskipTests
```

Check whether some SQL parses:

```bash
cat > q.sql <<'SQL'
SELECT a.id, b.name FROM ta a JOIN tb b ON a.id = b.id WHERE a.x > 1;
SQL

mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.checksyntax.checksyntax \
    -Dexec.args="/f q.sql /t oracle"
```

```text
Time Escaped: 1546, file processed: 1, syntax errors: 0
```

(The elapsed figure varies per run; `syntax errors: 0` is the part that matters.)

Reformat it:

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.formatsql.formatsql \
    -Dexec.args="q.sql"
```

```text
SELECT a.id,
       b.name
FROM   ta a
       JOIN tb b
       ON a.id = b.id
WHERE  a.x > 1;
```

Argument conventions differ between demos: `checksyntax` takes `/f <file>` and
`/t <vendor>`, while `formatsql` takes a bare filename. Run any demo with no
arguments and it prints its own usage line.

> **`-Dexec.classpathScope=compile` is no longer needed.** Every command in
> this README used to carry it, and older instructions elsewhere still do. It
> was a workaround: `pom.xml` declared `simple-xml`, `fastjson` and `expr4j`
> with `<scope>system</scope>`, pointing at jars committed under `lib/`, and
> Maven's `runtime` scope excludes `system`-scoped dependencies by design — so
> any demo touching one of them died with `NoClassDefFoundError` under the
> default scope even though the jar was right there on disk.
>
> All three are ordinary Maven dependencies now (see
> [Dependencies and security advisories](#dependencies-and-security-advisories) below) and `lib/` is gone, so the
> default scope works. The flag is harmless if you keep typing it.

> **Every demo is `gudusoft.gsqlparser.demos.<demo>.<Class>`.** The directory
> path under `src/main/java/` *is* the package, for all 190 files, so you can
> read the `-Dexec.mainClass` value straight off the file's location:
> `src/main/java/gudusoft/gsqlparser/demos/checksyntax/checksyntax.java` is
> `gudusoft.gsqlparser.demos.checksyntax.checksyntax`.
>
> This used to be the single biggest trap in the repository. A half-finished
> move had left 177 files sitting under `src/main/java/gudusoft/` while still
> declaring `package demos.*`, so `-Dexec.mainClass` followed the package line
> and not the directory, and the two disagreed for 263 of 273 files. That is
> finished now; see "One package root" below.

## Where the parser comes from

The parser is **not on Maven Central**. It is published to Gudu's own public
Maven repository, which `pom.xml` declares:

```xml
<repositories>
  <repository>
    <id>gudu-public-releases</id>
    <url>https://www.sqlparser.com/maven/</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.gudusoft</groupId>
  <artifactId>gsqlparser</artifactId>
  <version>${gsp.core.version}</version>
</dependency>
```

To move every demo to a different parser build, run one command:

```bash
.github/scripts/set-parser-version.sh 4.1.9
```

The version is written in **four** files, not one: the `${gsp.core.version}`
property in `pom.xml`, plus a hardcoded `<version>` in each of the three
`connector/*/pom.xml`, which are separate builds with no parent to inherit a
property from. Editing them by hand is how one gets left behind, silently
building against an older parser, so the script owns all four and CI runs
`set-parser-version.sh --check` to fail the build if they ever disagree.

(It was five until `pom_dlineage.xml` was folded into `pom.xml`. That POM
declared a second copy of the property for no reason beyond being a second
POM.)

Available versions:
<https://www.sqlparser.com/maven/com/gudusoft/gsqlparser/maven-metadata.xml>

### The repository keeps only the newest version

This is the single most important thing to know about the parser dependency.
`https://www.sqlparser.com/maven/` is **not** an archive. When a release goes
out, the previous ones are **deleted**, and `maven-metadata.xml` is rewritten to
list only the new one.

Observed directly on 2026-07-28: at 06:50 UTC the repository served five
versions, 4.1.4 through 4.1.8, all downloading and checksum-verifying. By 10:23
UTC, 4.1.9 had been published and **all five of the others returned 404**. A
build pinned to 4.1.6 that morning could not resolve its parser by lunchtime.

Two consequences:

- **A pin goes stale-broken, not just stale.** Falling behind is not "you are on
  an older parser", it is "your build no longer resolves", for anyone without
  that jar already in `~/.m2`. This repository must track the current release
  closely, which is exactly what the nightly is for.
- **Ranges do not save you.** `[4.1,)` reads the same rewritten metadata. Worse,
  it is cached per `updatePolicy`, so a machine holding yesterday's copy resolves
  a version that no longer exists and fails with no useful explanation. A pin at
  least names the missing version in the error.

### Why the version is still pinned

A floating version (`RELEASE`, `LATEST`, or a range) would remove the bumps. It
is not used here on purpose:

- This repository is what people clone to evaluate GSP. With a floating version,
  two clones a week apart get different parsers, and a bug report becomes
  unanswerable without knowing which one. The pin is what makes
  `git clone && mvn package` a reproducible starting point.
- A range is not dependably "latest" either, for the caching reason above.
- A bad upstream release would move every clone onto it at once, with no commit
  here to revert.
- `RELEASE` and `LATEST` also draw `both of them are being deprecated` from
  Maven 3 and are gone in Maven 4.

Instead the nightly does the bumping. Its `latest` job resolves the newest
release, runs the whole suite and every demo against it, and **only if all of
that passes** opens a PR moving the four files. So you never edit a version: you
merge a PR that is already proven green. Given that old versions are deleted,
treat a red `pinned` job with a green `latest` job as urgent rather than
informational: it means the pinned release is gone.

### Trial edition

`com.gudusoft:gsqlparser` is the **trial** build. It is fully functional for
evaluation and covers every demo here. Commercial builds may carry newer fixes
and use a more specific four-part version; the public Maven version is a
three-part number (for example `4.1.9`) and does not necessarily match the
four-part product version in the release notes. See <https://www.sqlparser.com>
for licensing.

## Running the demos against a local parser build

By default the demos resolve the published parser. If you are working on the
library itself and want a demo to exercise the build you just made, use the
`local` profile. The core installs under the `gudusoft` groupId rather than
`com.gudusoft`, which is why it needs its own profile:

```bash
# in the library checkout
cd gsp_java
mvn install -N && mvn install -pl gsp_java_core -Pquick_install

# then here, with the version you just installed
cd ../gsp_demo_java
mvn -Plocal -Dgsp.core.version=4.1.5.9 compile

mvn -q -Plocal -Dgsp.core.version=4.1.5.9 exec:java \
    -Dexec.mainClass=gudusoft.gsqlparser.demos.checksyntax.checksyntax \
    -Dexec.args="/f q.sql /t oracle"
```

This replaces the loop that existed while the demos were a vendored module of
the `gsp_java` reactor.


## The demos

Runnable programs under `src/main/java/gudusoft/gsqlparser/demos/`. Common starting points:

| Demo | What it does |
|------|--------------|
| `checksyntax` | Parse SQL and report syntax errors |
| `formatsql` | Pretty-print / reformat SQL |
| `gettablecolumns` | Extract table and column names |
| `columnImpact` | Trace column-level impact through SELECTs |
| `dlineage` / `dlineageBasic` | Data lineage analysis |
| `traceColumn` / `tracedatalineage` | Follow a column through a script |
| `analyzesp` | Analyze stored procedures |
| `analyzescript` | Walk a multi-statement script |
| `scriptwriter` / `modifysql` / `sqlrefactor` | Rewrite SQL through the AST |
| `joinConvert` | Convert between old-style and ANSI JOIN syntax |
| `expressionTraverser` / `visitors` | Walk the AST with a visitor |
| `sqltranslator` | Translate SQL between dialects |
| `listGSPInfo` | Print parser version and build info |

Others cover CRUD extraction, join-relation analysis, constant folding, source
tokens, table scanning, anti-SQL-injection checks and benchmarks. Most
directories carry their own `readme.md`.

## Running the tests

```bash
mvn test
```

144 tests, all passing, nothing skipped. Every input the suite needs is in the
repository, so a plain clone runs the whole thing.

> **Correction.** Until 2026-07-28 this section said three tests in
> `gudusoft.gsqlparser.demosTest.analyzespTest` failed because their expected
> output was written for an older parser and had since drifted, and that they
> were kept red on purpose as a drift signal. That was wrong, and it sent
> people looking at parser output for a bug that was never there.
>
> Those tests read stored procedures from the library's SQL corpus over a
> relative path, `gspCommon.BASE_SQL_DIR`. The path read `../gsp_java_core/`,
> one directory level short: it named a sibling of this checkout rather than
> the module inside `gsp_java`, so it resolved to nothing at all. `Analyze_SP`
> never found an input file, returned an empty string, and comparing that with
> the expected output failed. The expected strings match the current parser's
> output exactly, character for character, on all three. `testSample7` "passed"
> throughout only because it expects empty output, which is also what a missing
> file produces, so it was asserting nothing.
>
> The four scripts now live in `src/test/resources/sqlscripts/analyze_sp/`,
> copied byte for byte from the corpus, which removes the sibling-checkout
> requirement rather than just correcting it. See that directory's `readme.md`.

Every test here exercises a **demo** in this repository. The tests that
exercise the *parser* live in the library, not here — see "The library-side
test tree" below.

(`traceDataLineageTest` used to contribute 2 of those "passing" tests with
every line commented out — no assertions, testing nothing, referencing fixture
files that never shipped with this repo. It's been replaced with one real
test against inline SQL; see
[#43](https://github.com/sqlparser/gsp_demo_java/issues/43).)

### The library-side test tree

`src/test/java/gudusoft/gsqlparser/` used to hold 222 files, but 204 of them
were a stale copy of the library's own test tree, carried in by the 2026/7/26
merge. They have been returned to `gsp_java_core`, which is where they belong
and where they actually work:

- They test the parser, not any demo here — none referenced demo code.
- They resolve fixtures through `gspCommon.BASE_SQL_DIR`, which points at
  `../gsp_java_core/gsp_sqlfiles/TestCases/`. That path does not exist relative
  to this repository, so their data was never found here.
- Most are named `testXxx.java` (lowercase), which Surefire's default include
  patterns (`Test*.java`, `*Test.java`, `*TestCase.java`) do not match. Only 3
  of the 204 were ever executed by `mvn test` here. `gsp_java_core`'s POM adds
  `**/test*.java`, so they run there.

169 were byte-identical to, or an older revision of, a file already in
`gsp_java_core` and were simply deleted; 35 existed only here and were moved
over. What remains are the 18 files that genuinely belong to this repository:
13 tests that import demo classes, `commonTest/SqlFileList.java` and
`commonTest/gspCommon.java` which those tests use, and three files kept back
because moving them would have required adding `org.jdom` and
`com.alibaba.fastjson` to the library's POM (`commonTest/testXmlXSD.java`,
`sqlenvTest/TJSONSQLEnv.java`, `sqlenvTest/testJSONEnv.java`).

`src/test/java/demos/visitors/` — the last test package still sitting outside
the `gudusoft/gsqlparser/` tree — has since been folded into it as
`gudusoft/gsqlparser/visitorsTest/`, following the `<demoName>Test` convention
the other directories already use (`joinConvertTest` covers the `joinConvert`
demo, `antiSQLInjectionTest` covers `antiSQLInjection`, and so on). All 20 test
files now live under one root.

### The vendored expression library

`src/main/java/gudusoft/gsqlparser/demos/antiSQLInjection/` used to carry 365
`.java` files of a third-party expression evaluator under `org/boris/expr/`.
That was **57% of every source file in this repository**, and it duplicated
`lib/expr4j.jar`, which was already a declared dependency: the jar holds the
same 365 top-level classes and nothing else, so the library shipped twice and
javac quietly compiled the sources while the jar sat unused.

The sources are gone; the jar supplies `org.boris.expr`. Only one file ever
imported it (`GEval.java`), the `antiSQLInjection` tests cover the path, and
they pass against the jar. One consequence worth knowing: **`mvn package` no
longer puts `org/boris/expr/**` inside the project jar.** Nothing here consumes
that jar as a library, so this only matters if you start doing so. (The shaded
`-dlineage` jar does contain it, along with every other runtime dependency —
that is what an uber jar is.)

The dependency's coordinates were also wrong: it was declared as
`tk.pratanumandal:expr4j`, a different library entirely, which would have
pointed SBOM and vulnerability tooling at the wrong project. It now names what
is actually on disk, with a checksum recorded alongside it, since the jar
carries no version metadata of its own.

It is also the one dependency here with no coordinate in any public repository —
searched and confirmed — so it is served from `lib-repo/`, a file-based Maven
repository inside this checkout, laid out exactly like `~/.m2/repository`. See
[`lib-repo/readme.md`](lib-repo/readme.md). It was a `<scope>system</scope>` jar
under `lib/` until 2026-07-28, which is worse than it sounds: `system` scope is
deprecated, invisible to Dependabot, **and skipped by `maven-shade-plugin`,
`maven-assembly-plugin` and `dependency:copy-dependencies` alike**, so it could
never appear in a packaged artifact or a generated classpath. That last part is
most of why the dlineage demo needed a hand-assembled `java -cp`
([#46](https://github.com/sqlparser/gsp_demo_java/issues/46)).

If Gudu ever uploads it to `https://www.sqlparser.com/maven/`, delete `lib-repo/`
and declare it normally — publishing to a repository you control is the better
answer, and this project already resolves from that one.

### dbConnect was removed

`dbConnect` was a **complete Maven project nested inside `src/main/java`** — its
own `pom.xml`, its own `src/main/java`, and 10 JDBC driver jars, all sitting in
the compile root of the project that contains it. It was moved out to
`connector/dbConnect/` and then deleted outright, because it had stopped being
revivable:

- It is written against `gudusoft.gsqlparser.sqlenv.util`, which the public
  parser no longer ships, so it needs an API migration rather than a rebuild.
- `pom.xml` excluded it from the build, and nothing else built it either.
- Its own parser dependency was pinned to `<version>latest</version>`, which
  Maven cannot resolve.
- Its 10 vendored JDBC drivers accounted for **16 of this repository's 28
  Dependabot advisories**, and none of them could be patched by upgrading a
  declared version, because the jars were files in git rather than resolvable
  dependencies.

Deleting it removed all 16 advisories, 10 binaries and 83 Java files in one go.
It is in git history if it ever needs reviving, but reviving it means the API
migration, not a checkout.

Moving it out first also resolved one of the two split packages: 16 of
`demos.sqlenv`'s files were dbConnect's and 1 (`runSQLEnv.java`) was not.

### One package root

`src/main/java` had grown four competing package roots — `demos.*`,
`gudusoft.*`, `gsp.demos.dlineage` and a stray `com.gudusoft.gsqlparser.demo`
(`App.java`, sitting loose at the very top of the source tree). Java's one hard
rule is that a file's directory path mirrors its package, and **263 of 273
files broke it**. Two packages were even split across both roots, so their
halves shared package-private access from unrelated directories.

There is now exactly one root, `gudusoft/`, and **path equals package for all
190 files**:

| | session start | now |
|---|---|---|
| `.java` under `src/main/java` | 273 | 190 |
| files whose path contradicts their `package` | 263 | **0** |
| package roots | 4 | **1** |
| packages split across roots | 2 | **0** |
| JDBC driver `.jar` files inside `src/main/java` | 10 | **0** |
| Maven projects nested in the compile root | 1 | **0** |

Everything moved to `gudusoft.gsqlparser.demos.<demo>`, which is the naming the
repository had been half-migrated toward for years. **This changes every
`-Dexec.mainClass` value**, so older instructions that say
`demos.checksyntax.checksyntax` need `gudusoft.gsqlparser.demos.checksyntax.checksyntax`.
The per-demo `readme.md` files, the `.bat` scripts and the CI workflow were all
updated with it.

A side effect worth knowing: the `.bat` scripts now work again. They had been
stale twice over — compiling `src\main\java\demos\<demo>\` and `cd`-ing up five
levels, both correct only before the demos moved under
`gudusoft/gsqlparser/demos/`. Since the rename had to touch them anyway, their
paths and directory depths were corrected to match where each script actually
sits (7 levels for most, 8 for the nested ones).

### samples/ and src/main/resources/

`src/main/java` is a *source* root: Maven compiles what is in it and does not
put anything else on the classpath. 229 files in there were not `.java`, and
that turned out to be hiding a real bug.

**`snowflake.js` was broken.** `SnowflakeSQLExtractor` loads it with
`getResourceAsStream("snowflake.js")`, but the file sat in `src/main/java`, so
Maven never copied it to `target/classes` — the call returned `null` and the
demo died on an NPE. It is the only classpath resource in the repository, and
it now lives at
`src/main/resources/gudusoft/gsqlparser/demos/snowflake/sqlextract/snowflake.js`,
mirroring its package so the same lookup resolves. (The demo also uses Nashorn,
removed from the JDK in 15, so it still needs Java 8–14 to run.)

**Sample SQL moved to `samples/`.** The 85 `.sql` files were input data passed
on the command line, never read from the classpath, so `src/main/resources` is
the wrong home for them too — they belong outside `src/` entirely:

```
samples/dlineageBasic/{oracle,mysql,mssql}/…   81 files
samples/tracedatalineage/                       2
samples/dlineage/                               1
samples/callgraph/                              1
```

**Build output deleted.** `demos/dlineage/class/` was created and `rm -rf`'d by
`buildJar.sh` on every run, and `data-lineage-result.xml` was a generated
lineage report nothing referenced. Both are now in `.gitignore`.

`buildJar.sh`, `buildJar.bat` and `demos/dlineage/MANIFEST.MF` went with them on
2026-07-28. All three existed to hand-assemble the same `data_flow_analyzer.jar`
that `maven-shade-plugin` now produces from the normal build, and all three
named files that do not exist: `buildJar.sh` copied
`../../../../../lib/gudusoft.gsqlparser.jar`, which was never in `lib/` under
that name, and the manifest's `Class-Path` listed
`lib/gudusoft.gsqlparser.jar` and `lib/sqlflow-exporter.jar`, neither of which
survived. Nothing in CI ran any of them, which is why nobody noticed.

What deliberately stays next to its demo: each demo's `readme.md`, its
`compile_*.bat` / `run_*.bat` (which `cd` relative to their own location, so
they cannot move), and a handful of per-demo assets (`tree-view.xsl` and
`tree-view.css`, referenced by relative href from generated XML;
`sqlflow-settings.png`; the dlineage PDF). Co-locating documentation and
per-demo tooling with the demo is the point of this repository's layout.

### Dependencies and security advisories

GitHub reported 28 open Dependabot advisories against this repository. They came
from one root cause: dependencies declared with `<scope>system</scope>` and a
`<systemPath>` into `lib/`. **Dependabot cannot patch those** — a system-scope
dependency is a file on disk, not something Maven resolves, so there is no
version for a bot to bump.

All 28 are now closed:

| where | count | what was done |
|---|---|---|
| `pom.xml` — `junrar` | 4 | `0.7` system-scope → `7.5.10` from Maven Central, `test` scope |
| `pom.xml` — `jdom` | 1 | every version of `org.jdom:jdom` is affected; migrated to the successor artifact `org.jdom:jdom2:2.0.6.1`, whose classes are `org.jdom2.*` |
| `pom.xml` — `junit` | 1 | `4.12` → `4.13.2` |
| `connector/snowflakeConnector` | 6 | `snowflake-jdbc` `3.12.9` → `4.3.2`, resolved from Central instead of a jar you drop into `lib/` |
| `connector/dbConnect` | 16 | module deleted (see above) |

Two things worth keeping in mind for next time:

- **`junrar` and `jdom` were only ever used by tests**, so they are `test` scope
  now. Both jars are gone from `lib/`.
- `fastjson` is pinned at `1.2.83` and is **not** flagged — that is the final
  1.x release, and it is where the 1.x deserialization advisories were fixed.
  Leave it, or move to `fastjson2`; do not "upgrade" it within 1.x.

**`lib/` is now gone entirely, and with it the last `system`-scope dependency.**
Three jars were left in it, believed to have no public coordinate. Two of them
did:

| jar | outcome |
|---|---|
| `simple-xml-2.7.1.jar` | on Maven Central as `org.simpleframework:simple-xml:2.7.1`, and **byte-for-byte identical** to the committed copy (`sha256 7a43d2d5…f4e429f9`). Declared normally; jar deleted. |
| `fastjson-1.2.83.jar` | on Maven Central as `com.alibaba:fastjson:1.2.83`, likewise byte-identical (`sha256 641a4d65…5fe692631d`). Declared normally; jar deleted. |
| `expr4j.jar` | genuinely not on Central under any groupId. Moved to `lib-repo/` as a real artifact, `org.boris:expr:0.0.0-vendored`. |

Since the two Central jars are the same bytes, that swap changed nothing at
runtime and gained three things: Dependabot can see and patch them, packaging
plugins stop skipping them, and their **transitive** dependencies now resolve —
`simple-xml` pulls in `stax` and `xpp3`, which under `system` scope were simply
absent, waiting to surface as a `NoClassDefFoundError` on some code path nobody
had exercised yet.

The rule that follows: **add a dependency by coordinate, not by file.** Check
Maven Central first — two of the three "no public coordinate" jars here were on
it all along. If it truly has no coordinate, publishing it to Gudu's own Maven
repository beats committing it; `lib-repo/` is the fallback for when nobody can
upload one.

`sqlflow-exporter.jar` and `sqlflow-library.jar` were dropped on 2026-07-28.
They supplied `gudusoft.dbadapter.T<Vendor>SQLDataSource`, which two demos used
to read table metadata out of a running database — so both demos needed a live
server and a JDBC driver, and `pom.xml` excluded them from the build for exactly
that reason. Both now take their metadata offline instead
(`runGetTableColumn` via `TSQLEnv`, `ColumnInspect` via a `/metadata` JSON file),
so both build and run like every other demo, and the jars had nothing left
holding them in. Two dead helpers went with them, `dlineage/SqlflowIngester.java`
and `dlineage/DataSourceProvider.java`: neither had a `main`, and the only
reference to either in the tree was a commented-out call in `DataFlowAnalyzer`.

Eight more jars used to sit alongside them, 5.5 MB of the directory's 7.1 MB.
None was declared by any POM, imported by any source, or named by any `.bat`
script, so nothing resolved them — `lib/` was just where they had been dropped:

| removed | why it was dead |
|---|---|
| `lib/jdbc/ojdbc-1.1.1.jar`, `lib/jdbc/sqljdbc4-4.0.jar` | the connector POMs resolve `${project.basedir}/lib/`, which is `connector/<module>/lib/` — never this directory. Download the driver into the module's own `lib/`, as its readme says. |
| `lib/proguard/proguard.jar`, `proguard.pro` | byte-identical to `gsp_java_core/proguard/`, and the `.pro` obfuscates `gudusoft.gsqlparser.jar` — the *library* artifact. Release tooling for the other repository. |
| `lib/jdk1.5/junit-4.5.jar`, `junit.jar` | JUnit comes from Maven at `4.13.2`. |
| `lib/commons-logging-1.1.3.jar` | referenced only by `fastjson`'s `support/spring/*` adapters; there is no Spring here. |
| `lib/jarLoader.jar` | Eclipse's jar-in-jar export loader, used by the IDE wizard, not by any build. |

Vendored driver jars in particular should not come back. They are invisible to
Dependabot for the same reason the system-scope entries above were, so they age
in place with no bot to flag them.

The one exception, `lib-repo/`, is deliberately not a place to drop jars: it is a
Maven repository with a strict layout, every artifact needs a hand-written
`.pom`, and its readme says to check Central and Gudu's own repository first.
The friction is the point.

## What is excluded from the build

Two demos used to be excluded because they read metadata straight out of a
running database over JDBC, and so needed a driver and a live server to do
anything. **Both are now in the build**, since 2026-07-28: they take that
metadata offline instead, which also let the two vendored jars behind it leave
`lib/`.

- `gettablecolumns/runGetTableColumn.java` — dropped its `/h /P /u /p` flags;
  supply a `TSQLEnv` in code or from JSON if you want column disambiguation
- `columninspect/ColumnInspect.java` — `/jdbc /u /p` became `/metadata <file>`,
  reading the same JSON the connection used to return. Every line of the
  inspection already worked off that JSON string, so nothing else changed.
  `samples/columninspect/` has a runnable metadata file and script

**No main source is excluded from the build any more.**
`gudusoft/gsqlparser/demos/dlineage/DataFlowAnalyzer.java` was the last one, and
its exclusion was circular: it was excluded because `pom_dlineage.xml` compiled
it separately, and that POM existed only to produce a standalone jar. With
`maven-shade-plugin` producing that jar from the one build, the exclusion had
nothing left to justify it. See
[The standalone dlineage tool](#the-standalone-dlineage-tool).

What `pom.xml` still excludes is two **test** sources:

- `gudusoft/gsqlparser/visitorsTest/XmlSchemaValidationTest.java` and its
  `TestRunner` — they read an XSD from `../gsp_java_core/`, outside this
  repository. They compile fine; only that path stops them running, so they
  are excluded rather than deleted

Everything else in those packages still builds. Reviving them needs an API
migration against a build that includes the metadata layer, not just a
recompile.

## The .bat scripts (Windows)

Each demo directory also ships `compile_<demo>.bat` and `run_<demo>.bat`, with
`setenv/setenv.bat` holding the shared environment.

```
cd src\main\java\gudusoft\gsqlparser\demos\checksyntax
compile_checksyntax.bat
run_checksyntax.bat /f ..\..\..\..\..\..\..\q.sql /t oracle
```

That is the whole workflow now. You no longer edit `setenv.bat` first: it keeps
whatever `JAVA_HOME` is already set, and it fetches the parser for you.

**No jar of any kind is committed to this repository**, so the first script you
run pulls them down. `setenv.bat` calls `setenv/fetch-parser.bat`, which runs
`mvn dependency:copy-dependencies` into `external_lib/`. It is a no-op once they
are there, and `external_lib/` is gitignored; delete that directory to force a
refetch after changing a dependency.

It used to copy only the parser, because everything else the demos needed lived
in the committed `lib/` directory that this script put on the `CLASSPATH`. With
`lib/` gone, `copy-dependencies` brings the whole set — parser, `fastjson`,
`simple-xml`, `org.boris.expr` out of `lib-repo/`, JAXB, SnakeYAML — in one go,
straight from `pom.xml`. No version is named anywhere in the `.bat` family, so
it cannot drift from what the Maven build resolves. `CLASSPATH` is now a single
directory, `external_lib\*`.

That means the `.bat` route needs Maven **once**, to fetch, and never again.
That is a deliberate trade. Vendoring a parser under `lib/` is what let these
scripts quietly compile against a build years older than their own source: the
jar in `lib/` was 3.1.1.0 while the demos had moved on to APIs like
`EOBTenantMode`, so `compile_checksyntax.bat` failed on a symbol that a current
parser has. One artifact, resolved from one place, is worth a one-time Maven
call.

A side effect worth noting: `run_DataFlowAnalyzer.bat` gets JAXB now. It never
did before, because `lib/` had no copy of it, so the `.bat` route hit
[#47](https://github.com/sqlparser/gsp_demo_java/issues/47) on any modern JDK
just as the Maven route did.

### Verification status

**Every `.bat` script is exercised on Windows on every push.** The `windows-bat`
job in `.github/workflows/build.yml` runs on `windows-latest`; the badge at the
top of this file covers it.

| phase | covered | current |
|-------|---------|---------|
| Bootstrap | assert no parser jar is committed, then `fetch-parser.bat` pulls the parser and every other dependency into `external_lib/` | passing |
| Compile | all **39** `compile_<demo>.bat` | **39/39** |
| Launch | all **50** `run_<demo>.bat`, no arguments | **50/50** |
| Run for real | 4 demos with arguments, output checked against an expected string | passing |

The launch phase runs each script with no arguments, so most simply print their
own usage line. What it proves is that the class name in the script still
resolves — a stale name after a package move shows up as
`ClassNotFoundException`, which is precisely what had happened. The four driven
with real arguments cover the distinct argument shapes: `checksyntax`
(`/f <file> /t <vendor>`), `formatsql` (bare filename), `listGSPInfo` (none),
and `modifysql`, whose compile and run scripts are named differently
(`compile_modifysql.bat` builds the folder, `run_replaceTablename.bat` runs one
class).

Each script ends with `pause`, so CI feeds their stdin from `NUL` to stop them
blocking on a runner with no keyboard.

## The nightly build

`.github/workflows/build.yml` runs on push and pull request, so it only reports
on changes made *here*. The other moving part is outside this repository: the
parser published to <https://www.sqlparser.com/maven/>. A release there can
change demo output or drop an API without a commit landing here, and nothing
would notice until someone ran a demo by hand.

`.github/workflows/nightly.yml` runs at 03:17 UTC and closes that gap. Three
jobs, each proving no parser jar is committed, compiling everything, running the
full suite, and then running the demos:

| job | parser | JDK |
|---|---|---|
| `pinned` | `${gsp.core.version}` from `pom.xml` | 8 and 21 |
| `latest` | newest `<release>` on sqlparser.com, resolved at run time | 21 |
| `windows-bat` | fetched by `setenv\fetch-parser.bat` | 8 |

**`latest` is the job that earns the nightly.** If it is red while `pinned` is
green, a new parser release broke the demos. If it is green and the versions
differ, `gsp.core.version` can be bumped. Failing to resolve a version is an
error rather than a fallback to the pinned one: a job that quietly re-tests what
`pinned` already covers would look green while testing nothing new.

Three scripts under `.github/scripts/`, all runnable locally:

- **`check-test-results.sh`** — parses the surefire XML and fails on any
  failure or error. There are no expected failures: the three that used to be
  tolerated were a wrong fixture path, not parser drift (see "Running the
  tests"). It also lists anything skipped, and fails if *every* test was — a
  run that proved nothing should not read as a pass just because no assertion
  got far enough to fail. Nothing skips today; that guard is there for whatever
  gets added next.
- **`run-all-demos.sh`** — launches **every** class with a `main()`, taken from
  the compiled output so anything `pom.xml` excludes is excluded here too. It
  checks that each demo *starts*: `NoClassDefFoundError`, `NoSuchMethodError`
  and friends are the shape a parser upgrade breaks things in, whatever
  arguments you pass. A demo that prints a usage line and stops is a pass.
- **`run-demo-cases.sh`** + **`demo-cases.tsv`** — drives a table of demos with
  real arguments and checks each one's output against a string it must contain,
  which is what catches a parser upgrade that changes *behaviour* rather than
  breaking linkage. Every expected string was taken from that demo's actual
  output. A row with an empty expectation is rejected as an authoring error, so
  a case cannot silently degrade into asserting nothing.

Set `MVN_ARGS` to point the demo scripts at a different parser, exactly as the
`latest` job does:

```bash
mvn -q compile -Dgsp.core.version=4.1.9
MVN_ARGS="-Dgsp.core.version=4.1.9" .github/scripts/run-demo-cases.sh
```

Without it they would resolve the version pinned in `pom.xml` and run new
classes against the old jar, reporting a pass that means nothing.

**What this does not cover.** 13 of the 49 demo folders ship no `.bat` at all —
`callgraph`, `evaluator`, `events`, `findConstants`, `findproceduralsql`,
`generateLineage`, `performance`, `removeSpecialConditions`, `scansql`,
`scriptwriter`, `snowflake`, `sqlenv` and `utils`. Those are Maven-only, and the
Linux job covers them. Nor does the launch phase assert on output for the 46
scripts it does not drive with arguments; it asserts only that they start.

### What testing them found

They had been stale for years — compiling `src\main\java\demos\<demo>\` and
`cd`-ing up five levels, both correct only before the demos moved under
`gudusoft/gsqlparser/demos/`. Nothing noticed, because nothing ran them. Putting
them under CI turned up five faults, none of which anything else would have
caught:

| fault | scripts |
|-------|---------|
| Still compiled `src\main\java\demos\*.java`, a directory the package rename deleted | 2 |
| Doubled path `analyzesp\sybase\sybase\` | 1 |
| Named a package that no longer existed after `ColumnImpact` moved | 2 |
| Passed only their own folder to `javac`, so cross-demo imports failed to resolve — fixed with `-sourcepath src\main\java` | 6 |
| No `-encoding`, so Windows `javac` used the platform codepage against UTF-8 sources | 39 |

The last one is the reason a real Windows runner was worth the trouble. `javac`
there defaults to `Cp1252`, and two demos failed with `unmappable character for
encoding Cp1252`. `pom.xml` has always declared `project.build.sourceEncoding`
as UTF-8, so Maven was never affected, and on Linux the default is UTF-8 anyway
— simulating all 39 scripts there passed cleanly. It only reproduces with the
encoding forced.

Also deleted along the way: `TGetTableColumn_notUsed.java`, which redefined
three classes from the `TGetTableColumn.java` beside it and so broke any
wildcard compile of that folder, which is exactly how
`compile_gettablecolumns.bat` compiles it.

## The standalone dlineage tool

`mvn package` produces a second, self-contained jar alongside the ordinary one:

```
target/gsp_demo_java-1.0-SNAPSHOT.jar            the demos, needs a classpath
target/gsp_demo_java-1.0-SNAPSHOT-dlineage.jar   standalone, runs on its own
```

The second is an executable uber jar built by `maven-shade-plugin` with every
runtime dependency inside it, so running the data-lineage analyser takes no
classpath at all:

```bash
mvn package -DskipTests

java -jar target/gsp_demo_java-1.0-SNAPSHOT-dlineage.jar \
     /f samples/dlineage/demo.sql /o lineage.json /json /graph \
     /simpleShowRelationTypes fdd,fdr /filterRelationTypes fdd
```

Other invocations it supports: `/t mssql`, `/t postgresql`, `/showER`,
`/filterRelationTypes fdd`. Omit `/json` to get XML instead.

It carries every demo class, not just this one, so it doubles as a
no-setup way to run any of them:

```bash
java -cp target/gsp_demo_java-1.0-SNAPSHOT-dlineage.jar \
     gudusoft.gsqlparser.demos.checksyntax.checksyntax /f q.sql /t oracle
```

### What this replaced, and why it kept breaking

Until 2026-07-28 this was a second POM, `pom_dlineage.xml`, built with
`mvn -f pom_dlineage.xml package`. It broke four times, and **every one of them
was invisible to a green build**, because until the very end nothing in CI ran
the thing — and for most of that time nothing even built it.

1. **It pinned a parser jar from 2019.** It declared
   `lib/gsqlparser-3.1.1.0.jar` on `system` scope while `DataFlowAnalyzer` had
   moved on to `getOption().setTraceTablePosition(...)` and
   `ProcessUtility.generateColumnLevelLineageCsvSimple(...)`, neither of which
   that jar has.
2. **It kept a `<parent>` published nowhere**, `gudusoft:gsp_java:1.0-SNAPSHOT`,
   the private library reactor. Anyone without that POM in their local
   repository got `Non-resolvable parent POM` before Maven read a line of the
   file. It looked fine to everyone at Gudu, who all have the library checked
   out. The root `pom.xml` had been cut loose from that parent years earlier;
   this one was missed.
3. **[#46](https://github.com/sqlparser/gsp_demo_java/issues/46) — the
   documented run command could not work.** It put `external_lib/*` on the
   classpath, a directory only the Windows `.bat` route ever creates, so anyone
   following the Maven instructions in order hit `NoClassDefFoundError` on the
   first command in the section. The jar Maven had actually downloaded sat in
   `~/.m2`, under a filename containing a version number that changes with every
   release, so it could not be guessed either.
4. **[#47](https://github.com/sqlparser/gsp_demo_java/issues/47) — it was
   missing the JAXB dependency `pom.xml` already had.** JAXB left the JDK after
   Java 8; `pom.xml` had carried `jakarta.xml.bind-api` and `jaxb-runtime` for
   ages, and `pom_dlineage.xml` had neither. Since `pom.xml` *excluded*
   `DataFlowAnalyzer` from its own build, this second POM was the only thing
   that ever compiled the class, so no other build path could catch it.

Merging it into `pom.xml` removes the category rather than the four instances.
There is no second POM to drift, no second parser version to forget, no second
dependency list to fall behind — and no
[#39](https://github.com/sqlparser/gsp_demo_java/issues/39) either, the bug
where the second POM's incremental-compile cleanup deleted the root build's
`target/classes` because both POMs shared `${project.basedir}`. One POM cannot
collide with itself, so `target-dlineage/` is gone too.

The exclusion that made all this necessary was circular: `DataFlowAnalyzer` was
excluded from the root build *because* a separate POM compiled it, and that POM
existed to produce a standalone jar. `maven-shade-plugin` produces the
standalone jar from the one build, so nothing is excluded from `pom.xml` any
more.

**What actually keeps it working now is that CI runs it.**
`.github/scripts/smoke-dlineage-jar.sh` executes the jar on
`samples/dlineage/demo.sql` in both `build.yml` and `nightly.yml`, on JDK 8 and
21, and asserts on the *output*: non-empty, parses, and contains lineage
relationships. That last part is not pedantry — in #47 the process created
`lineage.json`, died before writing a byte into it, and still exited `0`. A file
that exists and an exit code of zero were both, at that moment, lies. It checks
XML as well as JSON, because those are separate code paths and the JAXB one is
the one that broke.

## master and dev branches

`master` is updated when a new GSP version is released on
<https://sqlparser.com/download.php>. The dev branches move faster and may not
compile against the released parser that `pom.xml` pins.

## Tutorials

- SQL modify and rebuild, SQL refactor
  - [add/modify/remove a join](src/test/java/gudusoft/gsqlparser/commonTest/testModifySql.java)
  - [add/modify/remove a filter condition](src/test/java/gudusoft/gsqlparser/commonTest/testModifySql.java)
  - [add/modify/remove columns in the select list](src/test/java/gudusoft/gsqlparser/commonTest/testModifySql.java)

## Links

- Product site: <https://www.sqlparser.com>
- Java documentation: <https://docs.sqlparser.com>
- Quick start: <https://docs.sqlparser.com/quick-start/>
- .NET demos: <https://github.com/sqlparser/gsp_demo_dotnet>

# Changes

- **[2026/7/26]** Merged with the library-side demo tree, which had diverged.
  That tree is authoritative, so its version wins wherever the two differed (61
  files). Nothing was dropped: the 282 files that existed only here — 143 under
  `demos/dbConnect`, 130 tests, and a few data files — were kept. Test count
  went from 123 to 153. Most demos moved to `src/main/java/gudusoft/gsqlparser/demos/`.
- **[2026/7/26]** The build no longer inherits from the private `gudusoft:gsp_java`
  parent POM, which was why nobody outside Gudu could build this repository.
  It now resolves `com.gudusoft:gsqlparser` from
  <https://www.sqlparser.com/maven/> and builds standalone.
- **[2024/9/17]** Test packages moved from `src/test/java/` to
  `src/test/java/gudusoft/gsqlparser/`, so unit tests now live under package
  names like `gudusoft.gsqlparser.xxxTest`. The demos followed later, in the
  2026/7/26 merge above, and that move is still only partly reflected in their
  `package` declarations.

---

## Appendix: organising multiple demos as Maven modules

Design note carried over from the library-side tree. This describes a structure
the repository does **not** currently use; it is kept as guidance.

When a project contains multiple demos that need to be built separately, the
usual approach is a Maven multi-module project: one parent `pom.xml` with
`<packaging>pom</packaging>` and a `<modules>` section, plus a child `pom.xml`
per demo declaring its own dependencies and inheriting shared versions from the
parent's `<dependencyManagement>`.

```
gsp_demo_java/
├── pom.xml               <-- parent POM, manages the modules
├── dlineage-demo/
│   ├── pom.xml
│   └── src/main/java/demos/dlineage/DataFlowAnalyzer.java
└── another-demo/
    ├── pom.xml
    └── src/main/java/demos/another/AnotherDemo.java
```

Build everything with `mvn clean package` from the root, or a single module with
`mvn -pl dlineage-demo clean package`.
