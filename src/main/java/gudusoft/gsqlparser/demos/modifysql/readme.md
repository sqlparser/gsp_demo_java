## Description

Three small demos of rewriting SQL through the parse tree instead of by string
manipulation. Each takes no arguments; the query is inline in its source.

| Class | What it rewrites |
|-------|------------------|
| `replaceTablename` | Swaps a table reference for a derived table, rewriting every qualified column that pointed at it |
| `replaceConstant` | Turns literals into `?` bind parameters |
| `add2SQL` | Adds conditions to a `WHERE` clause |

## Usage

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.modifysql.replaceTablename \
    -Dexec.classpathScope=compile
```

```text
input sql:
select table1.col1, table2.col2
from table1, table2
where table1.foo > table2.foo

output sql:
select table1.col1, table3.col2
from table1, (tableX join tableY using (id)) as table3
where table1.foo > table3.foo
```

Note that `table2.col2` and `table2.foo` both became `table3.…`: the aliases
follow the substitution, which is the part that string replacement gets wrong.

`replaceConstant` turns `VALUES ('arun','deep')` into `VALUES (?,?)` — the usual
first step in retrofitting prepared statements onto generated SQL.
