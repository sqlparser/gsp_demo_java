## DataFlowAnalyzer
Collects end-to-end data lineage in the Data Warehouses environment by analyzing SQL script especially stored procedure like PL/SQL that used in a ETL process.

This tool introduces a new dataflow model to describle the data flow of table/columns. 

For the detailed explanation of the relationship of target and source table/column, and how this tool works,
please check [this article](http://support.sqlparser.com/tutorials/gsp-demo-data-lineage/).

This tool is built from the scratch, includes all features of Dlineage and DlineageRelation demo, and has more features than Dlineage and DlineageRelation.
It is the main part of the backend of [this online SQLFlow](https://gudusoft.com/sqlflow/#/) tool.

The result generated by this tool is in XML format with all dataflow information included.

```
<dlineage>
  <table coordinate="[6,10],[6,19]" id="2" name="scott.emp" type="table">
    <column coordinate="[5,9],[5,15]" id="1" name="deptno"/>
    <column coordinate="[5,23],[5,24]" id="2" name="*"/>
    <column coordinate="[5,39],[5,42]" id="3" name="SAL"/>
    <column coordinate="[7,14],[7,20]" id="4" name="deptno"/>
  </table>
  <table coordinate="[9,10],[9,19]" id="4" name="scott.emp" type="table">
    <column coordinate="[8,15],[8,16]" id="8" name="*"/>
    <column coordinate="[8,35],[8,38]" id="9" name="sal"/>
  </table>
  <resultset coordinate="[7,22],[7,23]" id="1" name="RESULT_OF_a" type="select_list">
    <column coordinate="[5,9],[5,15]" id="5" name="deptno"/>
    <column coordinate="[5,17],[5,33]" id="6" name="num_emp"/>
    <column coordinate="[5,35],[5,51]" id="7" name="sal_sum"/>
  </resultset>
  <resultset coordinate="[9,21],[9,22]" id="3" name="RESULT_OF_b" type="select_list">
    <column coordinate="[8,9],[8,29]" id="10" name="total_count"/>
    <column coordinate="[8,31],[8,49]" id="11" name="total_sal"/>
  </resultset>
  <resultset coordinate="[1,8],[3,38]" id="5" name="RESULT_OF_SELECT-QUERY" type="select_list">
    <column coordinate="[1,8],[1,29]" id="12" name="&quot;Department&quot;"/>
    <column coordinate="[2,8],[2,43]" id="13" name="&quot;Employees&quot;"/>
    <column coordinate="[3,8],[3,38]" id="14" name="&quot;Salary&quot;"/>
  </resultset>
  <relation id="1" type="dataflow">
    <target column="deptno" coordinate="[5,9],[5,15]" id="5" parent_id="1" parent_name="RESULT_OF_a"/>
    <source column="deptno" coordinate="[5,9],[5,15]" id="1" parent_id="2" parent_name="scott.emp"/>
  </relation>
  <relation id="2" type="dataflow">
    <target column="num_emp" coordinate="[5,17],[5,33]" id="6" parent_id="1" parent_name="RESULT_OF_a"/>
    <source column="deptno" coordinate="[5,9],[5,15]" id="1" parent_id="2" parent_name="scott.emp"/>
    <source column="SAL" coordinate="[5,39],[5,42]" id="3" parent_id="2" parent_name="scott.emp"/>
    <source column="deptno" coordinate="[7,14],[7,20]" id="4" parent_id="2" parent_name="scott.emp"/>
  </relation>
  <relation id="4" type="dataflow">
    <target column="sal_sum" coordinate="[5,35],[5,51]" id="7" parent_id="1" parent_name="RESULT_OF_a"/>
    <source column="SAL" coordinate="[5,39],[5,42]" id="3" parent_id="2" parent_name="scott.emp"/>
  </relation>
  <relation id="9" type="dataflow">
    <target column="total_count" coordinate="[8,9],[8,29]" id="10" parent_id="3" parent_name="RESULT_OF_b"/>
    <source column="sal" coordinate="[8,35],[8,38]" id="9" parent_id="4" parent_name="scott.emp"/>
  </relation>
  <relation id="11" type="dataflow">
    <target column="total_sal" coordinate="[8,31],[8,49]" id="11" parent_id="3" parent_name="RESULT_OF_b"/>
    <source column="sal" coordinate="[8,35],[8,38]" id="9" parent_id="4" parent_name="scott.emp"/>
  </relation>
  <relation id="13" type="dataflow">
    <target column="&quot;Department&quot;" coordinate="[1,8],[1,29]" id="12" parent_id="5" parent_name="RESULT_OF_SELECT-QUERY"/>
    <source column="deptno" coordinate="[5,9],[5,15]" id="5" parent_id="1" parent_name="RESULT_OF_a"/>
  </relation>
  <relation id="14" type="dataflow">
    <target column="&quot;Employees&quot;" coordinate="[2,8],[2,43]" id="13" parent_id="5" parent_name="RESULT_OF_SELECT-QUERY"/>
    <source column="num_emp" coordinate="[5,17],[5,33]" id="6" parent_id="1" parent_name="RESULT_OF_a"/>
    <source column="total_count" coordinate="[8,9],[8,29]" id="10" parent_id="3" parent_name="RESULT_OF_b"/>
  </relation>
  <relation id="15" type="dataflow">
    <target column="&quot;Salary&quot;" coordinate="[3,8],[3,38]" id="14" parent_id="5" parent_name="RESULT_OF_SELECT-QUERY"/>
    <source column="sal_sum" coordinate="[5,35],[5,51]" id="7" parent_id="1" parent_name="RESULT_OF_a"/>
    <source column="total_sal" coordinate="[8,31],[8,49]" id="11" parent_id="3" parent_name="RESULT_OF_b"/>
  </relation>
  <relation id="3" type="dataflow_recordset">
    <target column="num_emp" coordinate="[5,17],[5,33]" function="COUNT" id="6" parent_id="1" parent_name="RESULT_OF_a"/>
    <source coordinate="[6,10],[6,19]" source_id="2" source_name="scott.emp"/>
  </relation>
  <relation id="5" type="dataflow_recordset">
    <target column="sal_sum" coordinate="[5,35],[5,51]" function="SUM" id="7" parent_id="1" parent_name="RESULT_OF_a"/>
    <source coordinate="[6,10],[6,19]" source_id="2" source_name="scott.emp"/>
  </relation>
  <relation id="10" type="dataflow_recordset">
    <target column="total_count" coordinate="[8,9],[8,29]" function="COUNT" id="10" parent_id="3" parent_name="RESULT_OF_b"/>
    <source coordinate="[9,10],[9,19]" source_id="4" source_name="scott.emp"/>
  </relation>
  <relation id="12" type="dataflow_recordset">
    <target column="total_sal" coordinate="[8,31],[8,49]" function="SUM" id="11" parent_id="3" parent_name="RESULT_OF_b"/>
    <source coordinate="[9,10],[9,19]" source_id="4" source_name="scott.emp"/>
  </relation>
  <relation id="7" type="dataflow_recordset">
    <target column="num_emp" coordinate="[5,17],[5,33]" function="COUNT" id="6" parent_id="1" parent_name="RESULT_OF_a"/>
    <source column="deptno" coordinate="[7,14],[7,20]" id="4" parent_id="2" parent_name="scott.emp"/>
  </relation>
  <relation id="8" type="dataflow_recordset">
    <target column="sal_sum" coordinate="[5,35],[5,51]" function="SUM" id="7" parent_id="1" parent_name="RESULT_OF_a"/>
    <source column="deptno" coordinate="[7,14],[7,20]" id="4" parent_id="2" parent_name="scott.emp"/>
  </relation>
  <relation id="6" type="impact">
    <target column="deptno" coordinate="[5,9],[5,15]" id="5" parent_id="1" parent_name="RESULT_OF_a"/>
    <source column="deptno" coordinate="[7,14],[7,20]" id="4" parent_id="2" parent_name="scott.emp"/>
  </relation>
</dlineage>
```

### Usage
`java DataFlowAnalyzer [/f <path_to_sql_file>] [/d <path_to_directory_includes_sql_files>] [/s [/text]] [/json] [/traceView] [/t <database type>] [/o <output file path>][/version][/stat]`

- [/s [/text]]
	
	The result generated with this option is the same as [columnImpact](../columnImpact) demo with /s option.
	
	
### Binary version
http://ftp.gudusoft.com/dl/dataflowanalyzer/DataFlowAnalyzer_trial.zip

In order to run this utility, please install Oracle JDK1.8 or higher on your computer correctly.
Then, run this utility like this:

java -jar DataFlowAnalyzer.jar /t mssql /d path_to_directory_includes_sql_files /stat

this is the list of available database after /t option:

access,bigquery,couchbase,dax,db2,greenplum,hana,hive,impala,informix,mdx,mssql,
sqlserver,mysql,netezza,odbc,openedge,oracle,postgresql,postgres,redshift,snowflake,
sybase,teradata,soql,vertica



### Links
- [First version, 2017-8](https://github.com/sqlparser/wings/issues/494)