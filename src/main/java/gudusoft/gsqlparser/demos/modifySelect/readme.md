## Description

Adds conditions to an existing `SELECT`'s `WHERE` clause through the parse
tree rather than by string concatenation: a join predicate between two tables,
plus bind-parameter placeholders.

Doing this on the AST is what keeps it correct when the original `WHERE` is
already non-trivial, where appending ` AND ...` to the text would not be.

## Usage

Takes no arguments; the query is inline in `ModifySelect.java`.

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.modifySelect.ModifySelect \
    -Dexec.classpathScope=compile
```

```text
Original SQL:
SELECT A.COLUMN1, B.COLUMN2 from TABLE1 A, TABLE2 B where A.COLUMN1=B.COLUMN1
Modified SQL:
SELECT A.COLUMN1, B.COLUMN2 from TABLE1 A, TABLE2 B where A.COLUMN1=B.COLUMN1 AND A.newcolumn=B.newcolumn AND A.newcolumn=? AND B.newcolumn=?
```
