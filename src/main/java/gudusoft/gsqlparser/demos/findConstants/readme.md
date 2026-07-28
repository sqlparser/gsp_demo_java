## Description

Lists every string literal and numeric constant in a SQL script. The kind of
sweep you would run before parameterising hard-coded values, or to find
embedded secrets and magic numbers.

## Usage

```
java findConstants <scriptfile> [/t <database type>]
```

`/t` defaults to `oracle`.

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.findConstants.findConstants \
    -Dexec.args="q.sql /t oracle" -Dexec.classpathScope=compile
```

For `SELECT a.id, b.name, 100 AS n FROM ta a JOIN tb b ON a.id = b.id WHERE a.x > 1 AND 'k' = 'k';`

```text
string literals and numeric constants:
100, 1, 'k', 'k'
```

Constants are reported once per occurrence, not deduplicated: `'k'` appears
twice above because the query contains it twice.
