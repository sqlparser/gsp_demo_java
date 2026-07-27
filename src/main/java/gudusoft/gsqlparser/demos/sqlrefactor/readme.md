## Description

`rmdupParenthesis` removes redundant duplicated parentheses from a SQL
statement using the source token list, e.g. `WHERE ((a.x > 1))` becomes
`WHERE (a.x > 1)`.

## Usage

```
java rmdupParenthesis <sqlfile.sql> [/t <database type>]
```

`/t` is optional and defaults to `oracle`.

```bash
cat > paren.sql <<'SQL'
SELECT * FROM ta WHERE ((a.x > 1));
SQL

mvn -q exec:java -Dexec.mainClass=demos.sqlrefactor.rmdupParenthesis \
    -Dexec.args="paren.sql /t mssql" -Dexec.classpathScope=runtime
```

```
Selected SQL dialect: dbvmssql
SELECT * FROM ta WHERE (a.x > 1);
Time Escaped: 764
```
