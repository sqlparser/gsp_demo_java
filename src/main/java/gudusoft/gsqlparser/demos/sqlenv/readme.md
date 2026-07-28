## Description

Feeds the parser a database schema so it can resolve names that SQL alone
leaves ambiguous. Given `select ename from emp, dept`, only the metadata says
which table `ename` belongs to.

Here the metadata comes from a JSON file through `TJsonSQLEnv` (in
`demos/utils`), so no database connection is needed. That is the difference
between this demo and `gettablecolumns`' `runGetTableColumn`, which reaches for
a live JDBC connection and is excluded from the build.

## Usage

```
java runSQLEnv [/f <path_to_sql_file>]
```

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.sqlenv.runSQLEnv \
    -Dexec.args="/f schema.json" -Dexec.classpathScope=compile
```

The `/f` argument is the **JSON metadata file**, not a SQL script. `TJsonSQLEnv`
in `demos/utils` defines the format it expects.
