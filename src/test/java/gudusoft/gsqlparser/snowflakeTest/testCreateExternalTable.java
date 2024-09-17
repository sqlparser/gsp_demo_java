package gudusoft.gsqlparser.snowflakeTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TStageLocation;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

public class testCreateExternalTable extends TestCase {

    public void testAmazonS3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create or replace external table ext_twitter_feed\n" +
                "  with location = @mystage/daily/\n" +
                "  auto_refresh = true\n" +
                "  file_format = (type = parquet)\n" +
                "  pattern='.*sales.*[.]parquet';";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTableSqlStatement.isExternal());
        assertTrue(createTableSqlStatement.getTableName().toString().equalsIgnoreCase("ext_twitter_feed"));
        TStageLocation stageLocation = createTableSqlStatement.getStageLocation();
        assertTrue(stageLocation.getStageName().toString().equalsIgnoreCase("mystage"));
        assertTrue(stageLocation.getPath().getPathList().getObjectName(0).toString().equalsIgnoreCase("daily"));
        assertTrue(createTableSqlStatement.getRegex_pattern().equalsIgnoreCase("'.*sales.*[.]parquet'"));
        assertTrue(createTableSqlStatement.getFileFormatType().equalsIgnoreCase("parquet"));
       // System.out.println(stageLocation.getStageName().toString());
    }

    public void testMicrosoftAzure(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create or replace external table ext_twitter_feed\n" +
                " integration = 'MY_AZURE_INT'\n" +
                " with location = @mystage/daily/\n" +
                " auto_refresh = true\n" +
                " file_format = (type = parquet)\n" +
                " pattern='.*sales.*[.]parquet';";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTableSqlStatement.isExternal());
        assertTrue(createTableSqlStatement.getTableName().toString().equalsIgnoreCase("ext_twitter_feed"));
        TStageLocation stageLocation = createTableSqlStatement.getStageLocation();
        assertTrue(stageLocation.getStageName().toString().equalsIgnoreCase("mystage"));
        assertTrue(stageLocation.getPath().getPathList().getObjectName(0).toString().equalsIgnoreCase("daily"));
        assertTrue(createTableSqlStatement.getRegex_pattern().equalsIgnoreCase("'.*sales.*[.]parquet'"));
        assertTrue(createTableSqlStatement.getFileFormatType().equalsIgnoreCase("parquet"));
        // System.out.println(stageLocation.getStageName().toString());
    }

    public void testAmazonS3partitionedTable(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create external table exttable_part(\n" +
                " date_part date as to_date(split_part(metadata$filename, '/', 3)\n" +
                "   || '/' || split_part(metadata$filename, '/', 4)\n" +
                "   || '/' || split_part(metadata$filename, '/', 5), 'YYYY/MM/DD'),\n" +
                " timestamp bigint as (value:timestamp::bigint),\n" +
                " col2 varchar as (value:col2::varchar))\n" +
                " partition by (date_part)\n" +
                " location=@exttable_part_stage/logs/\n" +
                " auto_refresh = true\n" +
                " file_format = (type = parquet);";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTableSqlStatement.isExternal());
        assertTrue(createTableSqlStatement.getTableName().toString().equalsIgnoreCase("exttable_part"));
        assertTrue(createTableSqlStatement.getColumnList().getColumn(0).getColumnName().toString().equalsIgnoreCase("date_part"));
        assertTrue(createTableSqlStatement.getColumnList().getColumn(1).getColumnName().toString().equalsIgnoreCase("timestamp"));
        assertTrue(createTableSqlStatement.getColumnList().getColumn(2).getColumnName().toString().equalsIgnoreCase("col2"));
        TStageLocation stageLocation = createTableSqlStatement.getStageLocation();
        assertTrue(stageLocation.getStageName().toString().equalsIgnoreCase("exttable_part_stage"));
        assertTrue(stageLocation.getPath().getPathList().getObjectName(0).toString().equalsIgnoreCase("logs"));
        assertTrue(createTableSqlStatement.getFileFormatType().equalsIgnoreCase("parquet"));
        assertTrue(createTableSqlStatement.getPartitionColumnList().getObjectName(0).toString().equalsIgnoreCase("date_part"));
        // System.out.println(stageLocation.getStageName().toString());
    }

    public void testMicrosoftAzurepartitionedTable(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create external table exttable_part(\n" +
                "  date_part date as to_date(split_part(metadata$filename, '/', 3)\n" +
                "    || '/' || split_part(metadata$filename, '/', 4)\n" +
                "    || '/' || split_part(metadata$filename, '/', 5), 'YYYY/MM/DD'),\n" +
                "  timestamp bigint as (value:timestamp::bigint),\n" +
                "  col2 varchar as (value:col2::varchar))\n" +
                "  partition by (date_part)\n" +
                "  integration = 'MY_INT'\n" +
                "  location=@exttable_part_stage/logs/\n" +
                "  auto_refresh = true\n" +
                "  file_format = (type = parquet);";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTableSqlStatement.isExternal());
        assertTrue(createTableSqlStatement.getTableName().toString().equalsIgnoreCase("exttable_part"));
        assertTrue(createTableSqlStatement.getColumnList().getColumn(0).getColumnName().toString().equalsIgnoreCase("date_part"));
        assertTrue(createTableSqlStatement.getColumnList().getColumn(1).getColumnName().toString().equalsIgnoreCase("timestamp"));
        assertTrue(createTableSqlStatement.getColumnList().getColumn(2).getColumnName().toString().equalsIgnoreCase("col2"));
        TStageLocation stageLocation = createTableSqlStatement.getStageLocation();
        assertTrue(stageLocation.getStageName().toString().equalsIgnoreCase("exttable_part_stage"));
        assertTrue(stageLocation.getPath().getPathList().getObjectName(0).toString().equalsIgnoreCase("logs"));
        assertTrue(createTableSqlStatement.getFileFormatType().equalsIgnoreCase("parquet"));
        assertTrue(createTableSqlStatement.getPartitionColumnList().getObjectName(0).toString().equalsIgnoreCase("date_part"));
        // System.out.println(stageLocation.getStageName().toString());
    }

    public void testAWSJson(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create or replace external table ext_table\n" +
                "     with location = @mystage/path1/\n" +
                "     file_format = (type = json)\n" +
                "     aws_sns_topic = 'arn:aws:sns:us-west-2:001234567890:s3_mybucket';";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTableSqlStatement.isExternal());
        assertTrue(createTableSqlStatement.getTableName().toString().equalsIgnoreCase("ext_table"));
        TStageLocation stageLocation = createTableSqlStatement.getStageLocation();
        assertTrue(stageLocation.getStageName().toString().equalsIgnoreCase("mystage"));
        assertTrue(stageLocation.getPath().getPathList().getObjectName(0).toString().equalsIgnoreCase("path1"));
        assertTrue(createTableSqlStatement.getFileFormatType().equalsIgnoreCase("json"));
        assertTrue(createTableSqlStatement.getAwsSnsTopic().equalsIgnoreCase("'arn:aws:sns:us-west-2:001234567890:s3_mybucket'"));
        // System.out.println(stageLocation.getStageName().toString());
    }

}
