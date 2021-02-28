package bigquery;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TCreateTableOption;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

import static gudusoft.gsqlparser.ECreateTableOption.etoBigQueryExternal;

public class testCreateExternalTable extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);

        sqlparser.sqltext = "CREATE EXTERNAL TABLE dataset.CsvTable OPTIONS (\n" +
                "  format = 'CSV',\n" +
                "  uris = ['gs://bucket/path1.csv', 'gs://bucket/path2.csv']\n" +
                ");";

        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatetable);
        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TCreateTableOption createTableOption = createTableSqlStatement.getTableOptions().get(0);
        assertTrue(createTableOption.getCreateTableOptionType() == etoBigQueryExternal);
        assertTrue(createTableOption.toString().equalsIgnoreCase("OPTIONS (\n" +
                "  format = 'CSV',\n" +
                "  uris = ['gs://bucket/path1.csv', 'gs://bucket/path2.csv']\n" +
                ")"));

    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);

        sqlparser.sqltext = "CREATE OR REPLACE EXTERNAL TABLE dataset.CsvTable\n" +
                "(\n" +
                "  x INT64,\n" +
                "  y STRING\n" +
                ")\n" +
                "OPTIONS (\n" +
                "  format = 'CSV',\n" +
                "  uris = ['gs://bucket/path1.csv'],\n" +
                "  field_delimiter = '|',\n" +
                "  max_bad_records = 5\n" +
                ");";

        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatetable);
        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TCreateTableOption createTableOption = createTableSqlStatement.getTableOptions().get(0);
        assertTrue(createTableOption.getCreateTableOptionType() == etoBigQueryExternal);
        assertTrue(createTableOption.toString().equalsIgnoreCase("OPTIONS (\n" +
                "  format = 'CSV',\n" +
                "  uris = ['gs://bucket/path1.csv'],\n" +
                "  field_delimiter = '|',\n" +
                "  max_bad_records = 5\n" +
                ")"));
    }

    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);

        sqlparser.sqltext = "CREATE EXTERNAL TABLE dataset.AutoHivePartitionedTable\n" +
                "WITH PARTITION COLUMNS\n" +
                "OPTIONS (\n" +
                "  uris=['gs://bucket/path/*'],\n" +
                "  format=csv,\n" +
                "  hive_partition_uri_prefix='gs://bucket/path'\n" +
                ");";

        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatetable);
        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TCreateTableOption createTableOption = createTableSqlStatement.getTableOptions().get(1);
        assertTrue(createTableOption.getCreateTableOptionType() == etoBigQueryExternal);
        assertTrue(createTableOption.toString().equalsIgnoreCase("OPTIONS (\n" +
                "  uris=['gs://bucket/path/*'],\n" +
                "  format=csv,\n" +
                "  hive_partition_uri_prefix='gs://bucket/path'\n" +
                ")"));
    }

    public void test4(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);

        sqlparser.sqltext = "CREATE EXTERNAL TABLE dataset.CustomHivePartitionedTable\n" +
                "WITH PARTITION COLUMNS (\n" +
                "  field_1 STRING, -- column order must match the external path\n" +
                "  field_2 INT64\n" +
                ")\n" +
                "OPTIONS (\n" +
                "  uris=['gs://bucket/path/*'],\n" +
                "  format=csv,\n" +
                "  hive_partition_uri_prefix='gs://bucket/path'\n" +
                ");";

        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatetable);
        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TCreateTableOption createTableOption = createTableSqlStatement.getTableOptions().get(1);
        assertTrue(createTableOption.getCreateTableOptionType() == etoBigQueryExternal);
        assertTrue(createTableOption.toString().equalsIgnoreCase("OPTIONS (\n" +
                "  uris=['gs://bucket/path/*'],\n" +
                "  format=csv,\n" +
                "  hive_partition_uri_prefix='gs://bucket/path'\n" +
                ")"));
    }

}
