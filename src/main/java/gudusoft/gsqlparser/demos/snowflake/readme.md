## Description

Snowflake stored procedures are written in JavaScript, with the SQL embedded in
string expressions. `SnowflakeSQLExtractor` runs that JavaScript on a script
engine to recover the SQL statements the procedure would actually execute,
including ones assembled from concatenation, so they can then be parsed.

## Usage

```
java SnowflakeSQLExtractor [/f <path_to_sql_file>] [/d <path_to_directory_includes_sql_files>]
```

```bash
mvn -q exec:java \
    -Dexec.mainClass=gudusoft.gsqlparser.demos.snowflake.sqlextract.SnowflakeSQLExtractor \
    -Dexec.args="/f proc.sql" -Dexec.classpathScope=compile
```

> **Needs Java 8–14.** It evaluates the JavaScript with Nashorn, which was
> removed from the JDK in Java 15. On a newer JDK the script engine is not
> found.

The JavaScript helper it evaluates lives at
`src/main/resources/gudusoft/gsqlparser/demos/snowflake/sqlextract/snowflake.js`,
loaded from the classpath. It is under `src/main/resources`, not next to the
`.java`, because only `src/main/resources` is copied onto the classpath — it sat
in the wrong place for a long time and this demo could never run. See the root
`README.md`.
