## Description
Get all table and columns involved in the input SQL script, tells how table is effected such as select/insert/delete/update,
the clause where the column located such as select list. join condition, shows the datatype if a column is defined in create table
statement.

For more detailed information about how this tools works, please check [this article](http://support.sqlparser.com/tutorials/gsp-demo-get-table-column/).

## Usage
`java runGetTableColumn [/f <path_to_sql_file>] [/t <database type>] [/<show option>]`

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.gettablecolumns.runGetTableColumn \
    -Dexec.classpathScope=compile -Dexec.args="/f your.sql /t mssql"
```

> **This demo opens no database connection.** It used to accept `/h /P /u /p`
> and pull table metadata from a live server through
> `gudusoft.dbadapter.T<Vendor>SQLDataSource`, out of the vendored
> `lib/sqlflow-exporter.jar`. That path needed a reachable database and a JDBC
> driver, so it never ran here, and it was the last thing keeping that jar in
> the build. It was removed on 2026-07-28, and the demo now compiles and runs
> as part of the normal build.
>
> Ambiguous columns (see "Resolve the ambiguous columns in SQL query" below)
> can still be resolved without a server, by handing the analyser a `TSQLEnv`:
> build one in code, as the `TSQLServerEnv` / `THiveEnv` classes at the bottom
> of `runGetTableColumn.java` do, or parse one from a metadata JSON file with
> `gudusoft.gsqlparser.sqlenv.parser.TJSONSQLEnvParser`. The standalone
> "Binary version" below still ships the live-connection build.

## Binary version
https://www.gudusoft.com/gsp_java/gettablecolumn.zip

In order to run this utility, please install Oracle JDK1.8 or higher on your computer correctly.
Then, run this utility like this:

```
java -jar gudusoft.gettablecolumns.jar /t mssql /f path_to_sql_file
```

## Resolve the ambiguous columns in SQL query
```sql
select ename
from emp, dept
where emp.deptid = dept.id
```

column `ename` in the first line is not qualified by table name `emp`, so it’s ambiguous to know which table this column belongs to?

If we already created table `emp`, `dept` in the database using this DDL.
```sql
create table emp(
	id int,
	ename char(50),
	deptid int
);

create table dept(
	id int,
	dname char(50)
);
```

Given that metadata, column `ename` is linked to table `emp` correctly.

The metadata has to reach the analyser as a `TSQLEnv`. In this repository, build
one offline — in code (see `TSQLServerEnv` at the bottom of
`runGetTableColumn.java`) or from a metadata JSON file via
`gudusoft.gsqlparser.sqlenv.parser.TJSONSQLEnvParser` — and pass it with
`getTableColumn.setSqlEnv(...)`.

The JDBC arguments below (`/h /P /u /p /db /schema`) belong to the **"Binary
version"** above, not to the build in this repository; they were removed here on
2026-07-28 along with the vendored `sqlflow-exporter.jar` they depended on.

```
/h: Optional, specify the host of jdbc connection
/P: Optional, specify the port of jdbc connection
/u: Optional, specify the username of jdbc connection.
/p: Optional, specify the password of jdbc connection
/db: Optional, specify the database of jdbc connection
/schema: Optional, specify the schema which is used for extracting metadata.
```

When you use this feature, you should put the jdbc driver to your java classpath, and use java -cp command to load the jdbc driver jar.

Currently, gsp able to connect to the following databases with the proper JDBC driver
```
azuresql, greenplum, mysql, netezza, oracle, postgresql, redshift, snowflake, sqlserver, teradata
```


### connect to SQL Server
Tables are under this schema: `AdventureWorksDW2019/dbo`.

```sh
java -cp .;lib/*;external_lib/* gudusoft.gsqlparser.demos.gettablecolumns.runGetTableColumn /t mssql /h localhost /P 1433 /u root /p password /schema AdventureWorksDW2019/dbo /f sample.sql /showDetail
```

### connect to Oracle
Tables are under `HR` schema and connect to database using `orcl` instance.

```sh
java -cp .;lib/*;external_lib/* gudusoft.gsqlparser.demos.gettablecolumns.runGetTableColumn /t oracle /h localhost /P 1521 /u root /p password /db orcl /schema HR /f sample.sql /showDetail
```

### connect to MySQL
Tables are under `employees` database.

```sh
java -cp .;lib/*;external_lib/* gudusoft.gsqlparser.demos.gettablecolumns.runGetTableColumn /t mysql /h localhost /P 3306 /u root /p password /db employees /f sample.sql /showDetail
```
