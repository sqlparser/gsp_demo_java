## Description

Walks the expressions in a SQL script and evaluates what it can. Columns have
no value outside a database, so they are substituted with 0 and reported; from
there any expression built only from constants is folded to a result.

## Usage

```
java EvaluatorDemo [/f <path_to_sql_file>] [/t <database type>]
```

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.evaluator.EvaluatorDemo \
    -Dexec.args="/f q.sql /t oracle" -Dexec.classpathScope=compile
```

```text
Output:
a.id is a column, set value to 0,...
b.name is a column, set value to 0,...
0
0
100

DbVendor:dbvoracle, Time Escaped: 1306ms
```

The trailing `100` is the literal in the select list, folded as a constant.
