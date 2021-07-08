## DataFlowAnalyzer
Collects the end-to-end column-level data lineage in the Data Warehouses environment 
by analyzing SQL script especially stored procedure like PL/SQL.

This tool introduces a new [data lineage model](sqlflow-data-lineage-model-reference.pdf) 
that is compatible with the Apache Atlas type system to describle the data flow of table/columns. 

This tool is built from the scratch, it is the main part of the backend of [the SQLFlow Cloud](https://sqlflow.gudusoft.com).


### Usage
```
Usage: java DataFlowAnalyzer [/f <path_to_sql_file>] [/d <path_to_directory_includes_sql_files>] [/t <database type>] [/tableLineage] [/stat] [/s] [/i] [/ic] [/lof] [/j] [/text] [/json] [/traceView]  [/o <output file path>] [/version] [/h <host> /P <port> /u <username> /p <password> /db <database> [/metadata]]
/f: Option, specify the sql file path to analyze fdd relation.
/d: Option, specify the sql directory path to analyze fdd relation.
/j: Option, analyze the join relation.
/s: Option, simple output, ignore the intermediate results.
/i: Option, ignore all result sets.
/if: Option, ignore functions.
/ic: Option, ignore output coordinates.
/lof: Option, link orphan column to first table.
/traceView: Option, analyze the source tables of views.
/text: Option, print the plain text format output.
/json: Option, print the json format output.
/stat: Option, output the analysis statistic information.
/t: Option, set the database type. Support sparksql,oracle, mysql, mssql, db2, netezza, teradata, informix, sybase, postgresql, hive, greenplum and redshift, the default type is oracle
/o: Option, write the output stream to the specified file.
/log: Option, generate a dataflow.log file to log information.
/h: Option, specify the host of jdbc connection
/P: Option, specify the port of jdbc connection, note it's capital P.
/u: Option, specify the username of jdbc connection.
/p: Option, specify the password of jdbc connection, note it's lowercase P.
/db: Option, specify the database of jdbc connection.
/schema: Option, specify the schema which is used for extracting metadata.
/metadata: Option, output the database metadata information to the file metadata.json.
/tableLineage: Option, output tabel level lineage.
```


- [/s [/text]]
	
	The result generated with this option is the same as [columnImpact](../columnImpact) demo with /s option.
	

Here is the list of available database after /t option:
```
access,bigquery,couchbase,dax,db2,greenplum,hana,hive,impala,informix,mdx,mssql,
sqlserver,mysql,netezza,odbc,openedge,oracle,postgresql,postgres,redshift,snowflake,
sybase,teradata,soql,vertica
```

	
### Connect to the database instance to fetch metadata
This tool can connect to a database to fetch the metadata and present a more accurate data lineage analysis result.
```
	/h: Optional, specify the host of jdbc connection
	/P: Optional, specify the port of jdbc connection
	/u: Optional, specify the username of jdbc connection.
	/p: Optional, specify the password of jdbc connection
	/db: Optional, specify the database of jdbc connection
	/schema: Optional, specify the schema which is used for extracting metadata.
```

When you use this feature, you should put the jdbc driver to your java classpath, and use java -cp command to load the jdbc driver jar.

For example, if you put the jdbc jar to the external_lib directory, the java command is:
```sh
	java -cp .;lib/*;external_lib/* demos.dlineage.DataFlowAnalyzer /h localhost /P 3306 /u root /p password /db sample /t mysql /f sample.sql /s /json 

```
Currently, gsp able to connect to the following databases with the proper JDBC driver
```
oracle, sql server, mysql, postgresql, greenplum, netezza, snowflake.
```

	
### Binary version
http://ftp.gudusoft.com/dl/dataflowanalyzer/DataFlowAnalyzer_trial.zip

In order to run this utility, please install Oracle JDK1.8 or higher on your computer correctly.
Then, run this utility like this:

```
java -jar DataFlowAnalyzer.jar /t mssql /d path_to_directory_includes_sql_files /stat
```



### Links
- [First version, 2017-8](https://github.com/sqlparser/wings/issues/494)