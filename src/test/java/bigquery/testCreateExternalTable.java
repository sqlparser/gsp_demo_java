package bigquery;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TCreateTableOption;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

import static gudusoft.gsqlparser.ECreateTableOption.etoBigQueryExternal;

import java.util.Arrays;

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

    public void testAvro(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);

        sqlparser.sqltext = "CREATE EXTERNAL TABLE dataset.CsvTable OPTIONS (\n" +
                "  format = 'AVRO',\n" +
                "  uris = ['gs://bucket/path1.csv', 'gs://bucket/path2.csv']\n" +
                ");";

        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatetable);
        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TCreateTableOption createTableOption = createTableSqlStatement.getTableOptions().get(0);
        assertTrue(createTableOption.getCreateTableOptionType() == etoBigQueryExternal);
        assertTrue(createTableOption.toString().equalsIgnoreCase("OPTIONS (\n" +
                "  format = 'AVRO',\n" +
                "  uris = ['gs://bucket/path1.csv', 'gs://bucket/path2.csv']\n" +
                ")"));

        assertTrue(createTableOption.getFormat().equalsIgnoreCase("'AVRO'"));

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

    public void test5(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE dataset.CsvTable OPTIONS (\n" +
                "  format = 'CSV',\n" +
                "  uris = ['gs://bucket/path1.csv', 'gs://bucket/path2.csv'],\n" +
                "  field_delimiter = '|',\n" +
                "  max_bad_records = 5,\n" +
                "  hive_partition_uri_prefix='gs://bucket/path',\n" +
                "  expiration_timestamp=TIMESTAMP_ADD(CURRENT_TIMESTAMP(), INTERVAL 48 HOUR),\n" +
                "  description=\"a view that expires in 2 days\",\n" +
                "  allow_jagged_rows=true,\n" +
                "  allow_quoted_newlines=true,\n" +
                "  compression='GZIP',\n" +
                "  enable_logical_types=true,\n" +
                "  encoding='UTF8',\n" +
                "  decimal_target_types=[\"NUMERIC\", \"BIGNUMERIC\"],\n" +
                "  ignore_unknown_values=true,\n" +
                "  null_marker='\\0',\n" +
                "  projection_fields=\"a,b,c\",\n" +
                "  quote=\"$\",\n" +
                "  require_hive_partition_filter=true,\n" +
                "  sheet_range=\"sheet1!A1:B20\",\n" +
                "  skip_leading_rows=10000,\n" +
                ");";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTableSqlStatement.getTableName().toString().equalsIgnoreCase("dataset.CsvTable"));
        TCreateTableOption tableOption = createTableSqlStatement.getTableOptions().get(0);
        assertTrue(tableOption.getFormat().toString().equalsIgnoreCase("'CSV'"));
        assertTrue(Arrays.toString(tableOption.getUris().toArray()).equalsIgnoreCase("['gs://bucket/path1.csv', 'gs://bucket/path2.csv']"));
        assertTrue(tableOption.getFieldDelimiter().equals("'|'"));
        assertTrue(tableOption.getMaxBadRecords() == 5);
        assertTrue(tableOption.getHivePartitionUriPrefix().equalsIgnoreCase("'gs://bucket/path'"));
        assertTrue(tableOption.getExpirationTimestamp().equalsIgnoreCase("TIMESTAMP_ADD(CURRENT_TIMESTAMP(), INTERVAL 48 HOUR)"));
        assertTrue(tableOption.getDescription().equals("\"a view that expires in 2 days\""));
        assertTrue(tableOption.getAllowJaggedRows() == true);
        assertTrue(tableOption.getAllowQuotedNewlines() == true);
        assertTrue(tableOption.getCompression().equalsIgnoreCase("'GZIP'"));
        assertTrue(tableOption.getEnableLogicalTypes() == true);
        assertTrue(tableOption.getEncoding().equalsIgnoreCase("'UTF8'"));
        assertTrue(Arrays.toString(tableOption.getDecimalTargetTypes().toArray()).equalsIgnoreCase("[\"NUMERIC\", \"BIGNUMERIC\"]"));
        assertTrue(tableOption.getIgnoreUnknownValues() == true);
        assertTrue(tableOption.getNullMarker().equalsIgnoreCase("'\\0'"));
        assertTrue(tableOption.getProjectionFields().equalsIgnoreCase("\"a,b,c\""));
        assertTrue(tableOption.getQuote().equalsIgnoreCase("\"$\""));
        assertTrue(tableOption.getRequireHivePartitionFilter() == true);
        assertTrue(tableOption.getSheetRange().equalsIgnoreCase("\"sheet1!A1:B20\""));
        assertTrue(tableOption.getSkipLeadingRows() == 10000);
    }
}
