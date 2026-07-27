## Description

Follows each result column of a `SELECT` (or the subquery of a `CREATE VIEW`)
back to the base-table columns and expressions it's built from, printing an
indented trace: alias, the column's own expression, then each source column it
reads from, recursing into subqueries, derived tables and nested expressions.

## Usage

`runTraceColumn` runs a single query that's inline in `main()` — there's no
`/f` file argument or CLI parsing here, unlike most other demos. Run it with:

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.traceColumn.runTraceColumn -Dexec.classpathScope=compile
```

To trace a different query, edit the `sqltext` (or the vendor passed to
`runText`) in `runTraceColumn.java` and rebuild, or call
`TTraceColumn` directly from your own code:

```java
TTraceColumn traceColumn = new TTraceColumn(EDbVendor.dbvoracle);
traceColumn.runText("SELECT a.id, b.name FROM ta a JOIN tb b ON a.id = b.id");
System.out.print(traceColumn.getInfos().toString());
```

## Sample output

For the query built into `runTraceColumn.java`:

```
"Department"
 -->a.deptno(expr)
  -->a.deptno
   -->deptno(expr)
    -->scott.emp.deptno
"Employees"
  -->a.num_emp/b.total_count(expr)
   -->a.num_emp
    -->COUNT(*)(expr)
     -->scott.emp.*
   -->b.total_count
    -->COUNT(*)(expr)
     -->scott.emp.*
...
```

Each `-->` level is one hop back through the expression tree; a line ending in
`(expr)` is the expression at that point, and the line above it (when present)
is the alias or source column it was reached through.
