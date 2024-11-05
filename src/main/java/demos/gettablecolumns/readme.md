## Description
Get all table and columns involved in the input SQL script, tells how table is effected such as select/insert/delete/update,
the clause where the column located such as select list. join condition, shows the datatype if a column is defined in create table
statement.

For more detailed information about how this tools works, please check [this article](http://support.sqlparser.com/tutorials/gsp-demo-get-table-column/).

## Usage
`java runGetTableColumn [/f <path_to_sql_file>] [/t <database type>] [/<show option>]`

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

By connecting to the database to fetch metadata, column `ename` should be linked to the table `emp` correctly.

This is a list of arguments used when connect to a database:
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
java -cp .;lib/*;external_lib/* demos.gettablecolumns.runGetTableColumn /t mssql /h localhost /P 1433 /u root /p password /schema AdventureWorksDW2019/dbo /f sample.sql /showDetail
```

### connect to Oracle
Tables are under `HR` schema and connect to database using `orcl` instance.

```sh
java -cp .;lib/*;external_lib/* demos.gettablecolumns.runGetTableColumn /t oracle /h localhost /P 1521 /u root /p password /db orcl /schema HR /f sample.sql /showDetail
```

### connect to MySQL
Tables are under `employees` database.

```sh
java -cp .;lib/*;external_lib/* demos.gettablecolumns.runGetTableColumn /t mysql /h localhost /P 3306 /u root /p password /db employees /f sample.sql /showDetail
```
