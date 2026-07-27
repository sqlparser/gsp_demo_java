## Description

Reads every `.sql` file in a directory (T-SQL / `dbvmssql` syntax) as one
combined script, builds up a model of the tables it creates and the
`INSERT ... SELECT` / `UPDATE` statements and stored procedures that move data
between them, then traces each base-table column forward to every column it
ultimately feeds into. This is a directory/multi-script tool, unlike most
other demos here which parse one query or file — see `traceColumn` for
tracing a single query's result columns back to their sources instead.

## Usage

```
java gudusoft.gsqlparser.demos.tracedatalineage.traceDataLineage <sql scripts directory path> [<output file path>]
```

The first argument **must be a directory**, not a single `.sql` file —
passing a file silently matches nothing (`SqlFileList` only lists `.sql` files
inside a directory) and the tool exits 0 with no output, which looks
identical to "ran fine, found no lineage." The optional second argument
writes the result to a file instead of stdout.

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.tracedatalineage.traceDataLineage \
    -Dexec.args="samples/tracedatalineage" \
    -Dexec.classpathScope=compile
```

```
source_tbl.id	----->	target_tbl.id
source_tbl.amount	----->	target_tbl.total
```

`sample/` contains the two-file script this example reads: one file creates
`source_tbl` and `target_tbl`, the other loads the first into the second with
`INSERT INTO target_tbl (id, total) SELECT id, amount FROM source_tbl`.

Lineage is only recorded across `INSERT`/`UPDATE` statements and stored
procedures — a `CREATE VIEW` that merely selects from a table does not, on its
own, produce a traceable relation here.
