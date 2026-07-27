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
mvn -q exec:java -Dexec.mainClass=demos.formatsql.formatsql \
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
> `org.simpleframework.xml`) fails with `NoClassDefFoundError` under `runtime`
> even though the jar is right there in `lib/`. `compile` scope includes them
> and works for every demo.

> **Package names are not uniform yet.** A move of the demos from `demos.*` to
> `gudusoft.gsqlparser.demos.*` is partly done: 176 files sit under
> `src/main/java/gudusoft/` while still declaring `package demos.*`, so the class
> you pass to `-Dexec.mainClass` follows the **package declaration**, not the
> directory. `checksyntax` is `gudusoft.gsqlparser.demos.checksyntax.checksyntax`;
> `formatsql` is `demos.formatsql.formatsql`. When in doubt, grep the first
> `package` line — some of these files lead with a blank line or a comment, so
> plain `head -1` sometimes returns nothing:
>
> ```bash
> grep -m1 '^package' src/main/java/gudusoft/gsqlparser/demos/<demo>/<Demo>.java
> ```

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

152 tests run. **Three currently fail**, all in
`gudusoft.gsqlparser.demosTest.analyzespTest` (`testSample1`, `testSample6`,
`testSample8`). They compare stored-procedure relation output against golden
strings written for an older parser build, and that output has since changed.
They are left in place rather than deleted or rewritten, because they are a
real signal about output drift rather than a broken harness. The other 149
pass. That is why the getting-started step above uses `-DskipTests`.

(`traceDataLineageTest` used to contribute 2 of those "passing" tests with
every line commented out — no assertions, testing nothing, referencing fixture
files that never shipped with this repo. It's been replaced with one real
test against inline SQL; see
[#43](https://github.com/sqlparser/gsp_demo_java/issues/43).)

## What is excluded from the build

Some demos read metadata straight out of a running database over JDBC, using
the `TSQLDataSource` / `TSQLEnv` family. The public parser artifact ships no
`*SQLDataSource` class, so those sources cannot compile against it, and they
would need JDBC drivers and a live server to do anything. `pom.xml` excludes
them from the default build:

- `demos/dbConnect/**` — 143 files
- `gudusoft/gsqlparser/demos/gettablecolumns/runGetTableColumn.java`
- `gudusoft/gsqlparser/demos/columninspect/ColumnInspect.java`
- `gudusoft/gsqlparser/demos/dlineage/DataFlowAnalyzer.java`
- `gudusoft/gsqlparser/demos/gettablecolumns/TGetTableColumn_notUsed.java` — a
  dead copy that redefines classes the retained `TGetTableColumn.java` provides
- `commonTest/testDBVendor.java` — asserts on three `TSQLEnv` collation fields
  the published artifact does not expose
- `demos/visitors/XmlSchemaValidationTest.java` and its `TestRunner` — they read
  an XSD from `../gsp_java_core/`, outside this repository

Everything else in those packages still builds. Reviving them needs an API
migration against a build that includes the metadata layer, not just a
recompile.

## The .bat scripts (Windows, no Maven)

Each demo directory also ships `compile_<demo>.bat` and `run_<demo>.bat`, with
`setenv/setenv.bat` holding `JAVA_HOME`. The original workflow was:

1. edit `setenv/setenv.bat` and set `JAVA_HOME` to your JDK
2. `cd` into a demo directory, for example `src/main/java/gudusoft/gsqlparser/demos/checksyntax`
3. run `compile_checksyntax.bat`, then `run_checksyntax.bat`

> **These scripts are stale.** They still compile
> `src\main\java\demos\<demo>\<demo>.java` and `cd` up five levels, both of
> which were correct before the demos moved under
> `src/main/java/gudusoft/gsqlparser/demos/`. They need their paths and depth
> updated. Use Maven in the meantime; the workflow is recorded here so it is not
> lost.


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
