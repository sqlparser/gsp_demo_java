# ColumnInspect
Lists the details of the column in the specified select list in the specified file and matches the specified database, getting the type of the column from the matched table.

## Usage
```
Usage: java ColumnInspect [/t] [dbname] [/f] [sql file path] [/metadata] [metadata json path] [/db] [database] [/schema] [schema]
/t: required, specify the database type.
/f: required, specify the SQL script file path to analyze.
/metadata: required, specify a JSON file describing the database metadata.
/db: required, specify the database to which the script to analyze belongs.
/schema: optional, specify the schema to which the script to analyze belongs.
```

Here is the list of available database after /t option:
```
mysql, postgres, oracle, sqlserver
```

## Run it

`samples/columninspect/` carries a metadata file and a matching script, so this
works from a fresh clone with no database:

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.columninspect.ColumnInspect \
    -Dexec.classpathScope=compile \
    -Dexec.args="/t mssql /f samples/columninspect/sample.sql /metadata samples/columninspect/metadata.json /db testdb /schema dbo"
```

`select * from emp` expands against the metadata:

```
emp：
column name:ename, data type: char(50)
column name:deptid, data type: int
column name:id, data type: int
```

## Where the metadata comes from

The demo reads the same JSON shape `TSQLEnv` does: a top-level `databases`
array, each entry with a `name` and a `tables` array, each table with a `name`,
a `schema` and a `columns` array whose objects carry `name` and `dataType`.

```json
{ "databases": [ { "name": "testdb", "tables": [
    { "name": "emp", "schema": "dbo",
      "columns": [ { "name": "ename", "dataType": "char(50)" } ] } ] } ] }
```

Until 2026-07-28 this demo pulled that JSON out of a running database instead,
over JDBC, using `gudusoft.dbadapter.TSQLDataSource` from the vendored
`lib/sqlflow-exporter.jar` — so it needed a reachable server and a driver, which
meant it never ran in this repository and was excluded from the build. Every
line of the inspection already worked off the returned JSON string, so it now
reads that JSON from a file, and the demo builds and runs like any other. Point
`/metadata` at an export from your own server to inspect real schemas.
