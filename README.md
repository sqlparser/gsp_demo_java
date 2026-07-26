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

mvn -q exec:java -Dexec.mainClass=demos.checksyntax.checksyntax \
    -Dexec.args="/f q.sql /t oracle" -Dexec.classpathScope=runtime
```

```text
Time Escaped: 1007,file processed: 1,syntax errors:0
```

(The elapsed figure varies per run; `syntax errors:0` is the part that matters.)

Reformat it:

```bash
mvn -q exec:java -Dexec.mainClass=demos.formatsql.formatsql \
    -Dexec.args="q.sql" -Dexec.classpathScope=runtime
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

## The demos

43 runnable programs under `src/main/java/demos/`. Common starting points:

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

123 tests run. **Three currently fail**, all in
`gudusoft.gsqlparser.demosTest.analyzespTest` (`testSample1`, `testSample6`,
`testSample8`). They compare stored-procedure relation output against golden
strings written for an older parser build, and that output has since changed.
They are left in place rather than deleted or rewritten, because they are a
real signal about output drift rather than a broken harness. The other 120
pass. That is why the getting-started step above uses `-DskipTests`.

## What is excluded from the build

Some demos read metadata straight out of a running database over JDBC, using
the `TSQLDataSource` / `TSQLEnv` family. The public parser artifact ships no
`*SQLDataSource` class, so those sources cannot compile against it, and they
would need JDBC drivers and a live server to do anything. `pom.xml` excludes
them from the default build:

- `demos/dbConnect/**`
- `demos/gettablecolumns/runGetTableColumn.java`
- `demos/columninspect/ColumnInspect.java`
- `demos/dlineage/DataFlowAnalyzer.java`
- the matching JUnit tests under `sqlenvTest`, `gettablecolumnTest` and
  `commonTest`

Everything else in those packages still builds. Reviving them needs an API
migration against a build that includes the metadata layer, not just a
recompile.

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

- **[2026/7/26]** The build no longer inherits from the private `gudusoft:gsp_java`
  parent POM, which was why nobody outside Gudu could build this repository.
  It now resolves `com.gudusoft:gsqlparser` from
  <https://www.sqlparser.com/maven/> and builds standalone.
- **[2024/9/17]** Test packages moved from `src/test/java/` to
  `src/test/java/gudusoft/gsqlparser/`, so unit tests now live under package
  names like `gudusoft.gsqlparser.xxxTest`. The demos themselves were *not*
  moved; they remain under `src/main/java/demos/`.
