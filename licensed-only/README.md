# Licensed-only demos

**Nothing in this directory builds with the trial parser, and that is not a
bug.** These three modules extract table and column metadata from a live
database over JDBC, which needs `gudusoft.gsqlparser.sqlenv.T*SQLDataSource`
and `sqlenv.metadata.DDL` — classes the public trial artifact this repository
resolves does not ship. Confirmed against `gsqlparser-4.2.6.jar`: `TSQLEnv` and
`TJSONSQLEnvParser` are present, `TOracleSQLDataSource`,
`TSnowflakeSQLDataSource`, `TMssqlSQLDataSource` and `metadata.DDL` are not.

They used to live in `connector/` at the repository root, where they read as
part of the ordinary demo set. A first-time evaluation in August 2026 started
there, hit a wall of `cannot find symbol`, and reasonably concluded the library
did not compile. Moving them here and having them refuse the build with an
explanation is the fix for that; it is not a statement about the code, which is
fine and works on a licensed parser.

```console
$ mvn -f licensed-only/snowflakeConnector/pom.xml compile
[ERROR] This module needs a LICENSED General SQL Parser, not the public trial
jar this repository resolves by default. ...
```

With a licensed parser installed, build normally:

```bash
mvn -f licensed-only/snowflakeConnector/pom.xml -Plicensed package
```

`-Plicensed` deactivates the default `trial-guard` profile. Nothing else about
the modules changes.

## What to evaluate instead

Metadata-aware column resolution works on the trial parser — it just takes the
metadata offline rather than over JDBC. `columninspect` reads the same JSON a
catalog export produces, and `samples/columninspect/` has a runnable pair:

```bash
mvn -q exec:java \
    -Dexec.mainClass=gudusoft.gsqlparser.demos.columninspect.ColumnInspect \
    -Dexec.args="/t mssql /f samples/columninspect/sample.sql \
                 /metadata samples/columninspect/metadata.json \
                 /db testdb /schema dbo"
```

`gettablecolumns/runGetTableColumn` takes the same approach with a `TSQLEnv`
built in code. Both were themselves live-JDBC demos until 2026-07-28 and were
converted rather than deleted, for exactly this reason.

## The modules

| module | driver | main class |
|---|---|---|
| `oracleConnector` | `com.oracle.database.jdbc:ojdbc8` | `demos.connector.OracleDataSourceConnector` |
| `snowflakeConnector` | `net.snowflake:snowflake-jdbc` | `demos.connector.SnowflakeDataSourceConnector` |
| `sqlServerConnector` | `com.microsoft.sqlserver:mssql-jdbc` | `demos.connector.SqlServerDataSourceConnector` |

Each is a **separate Maven build** with no parent — they are not modules of the
root reactor, so `mvn test` and `mvn package` at the root never touch them.
That is also why each POM hardcodes the parser version instead of inheriting
`${gsp.core.version}`; `.github/scripts/set-parser-version.sh` keeps all four
files in step, and both workflows check it.

All three drivers now resolve from Maven Central. Two of them did not until
2026-08-24: Oracle's was declared as `com:ojdbc:1.1.1` on `system` scope
pointing at a `lib/ojdbc-1.1.1.jar` that has never existed here, and SQL
Server's as `sqljdbc4:4.0` the same way. Both killed the build during
dependency resolution, before any plugin could explain anything — which is why
the guard above could not fire until they were real coordinates. **Do not
reintroduce a `system`-scope dependency or vendor a driver jar**; see the root
README on `lib-repo/`.
