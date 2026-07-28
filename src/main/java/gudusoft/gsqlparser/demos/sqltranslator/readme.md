## Description

Reports what stands between a script and a different database: identifiers that
collide with the target's rules, keywords, data types and functions. Each
category is split into what the tool can rewrite automatically and what needs a
human.

It answers "how much work is this migration", which is a different question from
"translate this query".

## Usage

```
java SqlTranslator <scriptfile> <source> <target> [/t] [/d] [/o <output file path>]
```

`source` and `target` are required (`oracle`, `mysql`, `mssql`). `/o` writes to
a file.

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.sqltranslator.SqlTranslator \
    -Dexec.args="q.sql oracle mysql" -Dexec.classpathScope=compile
```

```text
Check the input sql file q.sql
Found 0 identifier need to be translated, 0 identifier can be translated by tool, 0 identifier need to be translated by handy.
Found 0 keyword need to be translated, 0 keyword can be translated by tool, 0 keyword need to be translated by handy.
Found 0 data type need to be translated, 0 data type can be translated by tool, 0 data type need to be translated by handy.
Found 0 function need to be translated, 0 function can be translated by tool, 0 function need to be translated by handy.
```

All zeros above because the sample query is portable. Feed it vendor-specific
SQL to see the counts move.
