## Description

Scans a directory of `.sql` files and picks out the ones containing procedural
SQL — stored procedures, functions, triggers, packages — copying them to a
second directory.

Useful for triaging a large dump before analysis, since procedural code is what
tends to need the heavier tooling (`analyzesp`, `callgraph`, `tracedatalineage`).

## Usage

```
java FindProceduralSqlFiles <dbvendor> <source-sql-dir> <output-sql-dir>
```

`dbvendor` is `oracle`, `mssql` or `sqlserver`. Running it with no arguments
prints exactly that.

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.findproceduralsql.FindProceduralSqlFiles \
    -Dexec.args="oracle /path/to/scripts /path/to/procedural-only" \
    -Dexec.classpathScope=compile
```

Both directories are filesystem paths; the output directory receives copies, so
the source is left untouched.
