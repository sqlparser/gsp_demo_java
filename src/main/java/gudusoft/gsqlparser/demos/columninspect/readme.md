# ColumnInspect
Lists the details of the column in the specified select list in the specified file and matches the specified database, getting the type of the column from the matched table.

## Usage
```
Usage: java ColumnInspect [/t] [dbname] [/f] [sql file path] [/jdbc] [jdbc command] [/u] [username] [/p] [password] [/db] [database] [/schema] [schema]
/t: required, specify the database type.
/f: required, specify the SQL script file path to analyze.
/u: required, specify the username of jdbc connection.
/p: required, specify the password of jdbc connection.
/jdbc: required, specify the jdbc url of connection.
/db: required, specify the database to which the script to analyze belongs.
/schema: optional, specify the schema to which the script to analyze belongs.
```


Here is the list of available database after /t option:
```
mysql, postgres, oracle, sqlserver
```


### 1.1 connect to SQL Server
Tables are under this schema: `AdventureWorksDW2019/dbo`.

Connect using the specified JDBC URL.

```sh
java -cp .;lib/*;external_lib/* demos.columninspect.ColumnInspect /t mssql /jdbc jdbc:sqlserver://127.0.0.1:1433 /db AdventureWorksDW2019 /schema dbo /u root /p password  /f sample.sql
```

### 1.2 connect to Oracle
Tables are under `HR` schema and connect to database using `orcl` instance.

Connect using the specified JDBC URL.

```sh
java -cp .;lib/*;external_lib/* demos.columninspect.ColumnInspect /t oracle /jdbc jdbc:oracle:thin:@127.0.0.1:1521/orcl /db orcl /schema HR /u root /p password /f sample.sql 
```

### 1.3 connect to MySQL
Tables are under `employees` database.

Connect using the specified JDBC URL.

```sh
java -cp .;lib/*;external_lib/* demos.columninspect.ColumnInspect /t mysql /jdbc jdbc:mysql://127.0.0.1:3306/employees  /u root /p password  /db employees /f sample.sql 
```

### 1.4 connect to Postgresql
Tables are under `kingland` database.

Connect using the specified JDBC URL.

```sh
java -cp .;lib/*;external_lib/* demos.columninspect.ColumnInspect /t postgresql /jdbc jdbc:postgresql://127.0.0.1:5432/kingland  /u root /p password /db kingland  /f sample.sql 
```
