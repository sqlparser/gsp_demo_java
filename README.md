# General SQL Parser — Java Demos

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
    -Dexec.args="/f q.sql /t oracle" -Dexec.classpathScope=compile
```

```text
Time Escaped: 1546, file processed: 1, syntax errors: 0
```

(The elapsed figure varies per run; `syntax errors: 0` is the part that matters.)

Reformat it:

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.formatsql.formatsql \
    -Dexec.args="q.sql" -Dexec.classpathScope=compile
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

> **Use `-Dexec.classpathScope=compile`, not `runtime`.** `pom.xml` declares
> `simple-xml`, `fastjson` and a few other jars under `lib/` with
> `<scope>system</scope>`, since they have no public Maven coordinate. Maven's
> `runtime` classpath scope excludes `system`-scoped dependencies by design, so
> any demo that touches one of them (e.g. `dlineageBasic`, which uses
> `org.simpleframework.xml`, or `antiSQLInjection`, which uses
> `org.boris.expr`) fails with `NoClassDefFoundError` under `runtime`
> even though the jar is right there in `lib/`. `compile` scope includes them
> and works for every demo.

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

To move every demo to a different parser build, change one property:

```xml
<gsp.core.version>4.1.6</gsp.core.version>
```

Available versions:
<https://www.sqlparser.com/maven/com/gudusoft/gsqlparser/maven-metadata.xml>

### Trial edition

`com.gudusoft:gsqlparser` is the **trial** build. It is fully functional for
evaluation and covers every demo here. Commercial builds may carry newer fixes
and use a more specific four-part version; the public Maven version is a
three-part number (for example `4.1.6`) and does not necessarily match the
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
    -Dexec.args="/f q.sql /t oracle" -Dexec.classpathScope=compile
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

144 tests run. **Three currently fail**, all in
`gudusoft.gsqlparser.demosTest.analyzespTest` (`testSample1`, `testSample6`,
`testSample8`). They compare stored-procedure relation output against golden
strings written for an older parser build, and that output has since changed.
They are left in place rather than deleted or rewritten, because they are a
real signal about output drift rather than a broken harness. The other 141
pass. That is why the getting-started step above uses `-DskipTests`.

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

The sources are gone; the jar now supplies `org.boris.expr`. Only one file ever
imported it (`GEval.java`), the `antiSQLInjection` tests cover the path, and
they pass against the jar. Two consequences worth knowing:

- **The `antiSQLInjection` demo now needs `-Dexec.classpathScope=compile`**, for
  the `system`-scope reason described above. It used to work under `runtime`
  only because the classes happened to be compiled into `target/classes`.
- **`mvn package` no longer puts `org/boris/expr/**` inside the project jar.**
  Nothing here consumes that jar as a library, so this only matters if you start
  doing so.

The dependency's coordinates were also wrong: it was declared as
`tk.pratanumandal:expr4j`, a different library entirely, which would have
pointed SBOM and vulnerability tooling at the wrong project. It now names what
is actually on disk, with a checksum recorded in `pom.xml` since the jar carries
no version metadata of its own.

### dbConnect moved to connector/

`dbConnect` was a **complete Maven project nested inside `src/main/java`** — its
own `pom.xml`, its own `src/main/java`, and 10 JDBC driver jars, all sitting in
the compile root of the project that contains it. It was never built here
(`pom.xml` excluded it), because it needs JDBC drivers and a live server, and is
written against `gudusoft.gsqlparser.sqlenv.util` which the public parser no
longer ships.

It now lives at `connector/dbConnect/`, beside `oracleConnector`,
`snowflakeConnector` and `sqlServerConnector` — standalone JDBC modules with
exactly the same `pom.xml` + `src/` + `lib/` shape, none of them part of the
root build. Nothing in `src/` referenced it, and everything it imports from
`demos.sqlenv` is inside its own tree, so the move needed no code changes and
the exclusion in `pom.xml` is simply gone.

It also resolved one of the two split packages: 16 of `demos.sqlenv`'s files
were dbConnect's and 1 (`runSQLEnv.java`) was not.

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

**Build output deleted.** `demos/dlineage/class/` is created and `rm -rf`'d by
`buildJar.sh` on every run, and `data-lineage-result.xml` was a generated
lineage report nothing referenced. Both are now in `.gitignore`. The source
manifest `demos/dlineage/MANIFEST.MF` stays, since `buildJar.sh` copies it —
its `Main-Class` had been missed by the package rename and is now correct.

What deliberately stays next to its demo: each demo's `readme.md`, its
`compile_*.bat` / `run_*.bat` (which `cd` relative to their own location, so
they cannot move), and a handful of per-demo assets (`tree-view.xsl` and
`tree-view.css`, referenced by relative href from generated XML;
`sqlflow-settings.png`; the dlineage PDF). Co-locating documentation and
per-demo tooling with the demo is the point of this repository's layout.

## What is excluded from the build

Some demos read metadata straight out of a running database over JDBC, using
the `TSQLDataSource` / `TSQLEnv` family. The public parser artifact ships no
`*SQLDataSource` class, so those sources cannot compile against it, and they
would need JDBC drivers and a live server to do anything. `pom.xml` excludes
them from the default build:

- `gudusoft/gsqlparser/demos/gettablecolumns/runGetTableColumn.java`
- `gudusoft/gsqlparser/demos/columninspect/ColumnInspect.java`
- `gudusoft/gsqlparser/demos/dlineage/DataFlowAnalyzer.java`
- `gudusoft/gsqlparser/demos/gettablecolumns/TGetTableColumn_notUsed.java` — a
  dead copy that redefines classes the retained `TGetTableColumn.java` provides
- `gudusoft/gsqlparser/visitorsTest/XmlSchemaValidationTest.java` and its
  `TestRunner` — they read an XSD from `../gsp_java_core/`, outside this
  repository. They compile fine; only that path stops them running, so they
  are excluded rather than deleted

Everything else in those packages still builds. Reviving them needs an API
migration against a build that includes the metadata layer, not just a
recompile.

## The .bat scripts (Windows, no Maven)

Each demo directory also ships `compile_<demo>.bat` and `run_<demo>.bat`, with
`setenv/setenv.bat` holding `JAVA_HOME`. The original workflow was:

1. edit `setenv/setenv.bat` and set `JAVA_HOME` to your JDK
2. `cd` into a demo directory, for example `src/main/java/gudusoft/gsqlparser/demos/checksyntax`
3. run `compile_checksyntax.bat`, then `run_checksyntax.bat`

These were stale for a long time: they compiled `src\main\java\demos\<demo>\`
and `cd`-ed up five levels, both correct only before the demos moved under
`src/main/java/gudusoft/gsqlparser/demos/`. The package rename had to rewrite
their class names anyway, so their paths and `cd` depths were corrected at the
same time — 7 levels for most, 8 for the two nested under another demo. They
have not been run on Windows since, so treat them as repaired-but-unverified;
Maven remains the tested path.


## Building the dlineage demo on its own

`pom_dlineage.xml` builds just `DataFlowAnalyzer` into its own jar, for use as a
standalone lineage tool:

```bash
mvn -f pom_dlineage.xml package

java -cp "target-dlineage/gsp_demo_java_dlineage-1.0-SNAPSHOT.jar:lib/*" \
     gudusoft.gsqlparser.demos.dlineage.DataFlowAnalyzer \
     /f demo.sql /o lineage.json /json /graph \
     /simpleShowRelationTypes fdd,fdr /filterRelationTypes fdd
```

Other invocations it supports: `/t mssql`, `/t postgresql`, `/showER`,
`/filterRelationTypes fdd`.

`pom_dlineage.xml` builds into `target-dlineage/`, a directory of its own,
rather than the root build's `target/`. It shares this repository's
`${project.basedir}` with `pom.xml` and declares only one source file, so
without a separate output directory the compiler plugin's incremental-build
cleanup would delete every `.class` file the root build produced that isn't
part of this smaller source set — wiping out a working root build before this
one even reaches its own compile error below. See
[#39](https://github.com/sqlparser/gsp_demo_java/issues/39).

> **This build currently fails**, and did so before the trees were merged. It
> pins `lib/gsqlparser-3.1.1.0.jar`, but `DataFlowAnalyzer` has moved on and now
> calls `getOption().setTraceTablePosition(...)` and
> `ProcessUtility.generateColumnLevelLineageCsvSimple(...)`, neither of which
> that jar has. It needs a parser build carrying both those methods and the
> metadata layer. Recorded here so the invocations above are not lost.

## master and dev branches

`master` is updated when a new GSP version is released on
<https://sqlparser.com/download.php>. The dev branches move faster and may not
compile against the released jar or the one under `lib/`.

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
