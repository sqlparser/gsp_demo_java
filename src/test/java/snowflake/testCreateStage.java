package snowflake;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.snowflake.TCreateStageStmt;
import junit.framework.TestCase;

public class testCreateStage extends TestCase {

    public void testQualified(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create or replace stage STAGING.stage_07020728_MOCK_DATA_2";
        assertTrue(sqlparser.parse() == 0);

        TCreateStageStmt createStageStmt = (TCreateStageStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createStageStmt.getStageNameOnly().equalsIgnoreCase("stage_07020728_MOCK_DATA_2"));
        assertTrue(createStageStmt.getStageName().toString().equalsIgnoreCase("STAGING.stage_07020728_MOCK_DATA_2"));
        assertTrue(createStageStmt.getNameSpace().toString().equalsIgnoreCase("STAGING"));
        assertTrue(createStageStmt.getNameSpace().getSchemaToken().toString().equalsIgnoreCase("STAGING"));
    }

    public void testQualified2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create or replace stage DATAMAX_ETL.STAGING.stage_07020728_MOCK_DATA_2";
        assertTrue(sqlparser.parse() == 0);

        TCreateStageStmt createStageStmt = (TCreateStageStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createStageStmt.getStageNameOnly().equalsIgnoreCase("stage_07020728_MOCK_DATA_2"));
        assertTrue(createStageStmt.getStageName().toString().equalsIgnoreCase("DATAMAX_ETL.STAGING.stage_07020728_MOCK_DATA_2"));
        assertTrue(createStageStmt.getNameSpace().toString().equalsIgnoreCase("DATAMAX_ETL.STAGING"));
        assertTrue(createStageStmt.getNameSpace().getDatabaseToken().toString().equalsIgnoreCase("DATAMAX_ETL"));
        assertTrue(createStageStmt.getNameSpace().getSchemaToken().toString().equalsIgnoreCase("STAGING"));
    }

    public void testAmazonS3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create or replace stage my_int_stage\n" +
                "  copy_options = (on_error='skip_file');";
        assertTrue(sqlparser.parse() == 0);

        TCreateStageStmt createStageStmt = (TCreateStageStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createStageStmt.getStageName().toString().equalsIgnoreCase("my_int_stage"));
        // System.out.println(stageLocation.getStageName().toString());
    }

    public void testGoogleCloud(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create or replace stage my_ext_stage\n" +
                "  url='gcs://load/files/'\n" +
                "  storage_integration = myint;";
        assertTrue(sqlparser.parse() == 0);

        TCreateStageStmt createStageStmt = (TCreateStageStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createStageStmt.getStageName().toString().equalsIgnoreCase("my_ext_stage"));
        assertTrue(createStageStmt.getExternalStageURL().equalsIgnoreCase("'gcs://load/files/'"));
    }

    public void testMicrosoftAzure(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create or replace stage my_ext_stage\n" +
                "  url='azure://myaccount.blob.core.windows.net/load/files/'\n" +
                "  storage_integration = myint;";
        assertTrue(sqlparser.parse() == 0);

        TCreateStageStmt createStageStmt = (TCreateStageStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createStageStmt.getStageName().toString().equalsIgnoreCase("my_ext_stage"));
        assertTrue(createStageStmt.getExternalStageURL().equalsIgnoreCase("'azure://myaccount.blob.core.windows.net/load/files/'"));
    }

    public void testMicrosoftAzureFileFormat(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create or replace stage mystage\n" +
                "  url='azure://myaccount.blob.core.windows.net/mycontainer/files/'\n" +
                "  credentials=(azure_sas_token='?sv=2016-05-31&ss=b&srt=sco&sp=rwdl&se=2018-06-27T10:05:50Z&st=2017-06-27T02:05:50Z&spr=https,http&sig=bgqQwoXwxzuD2GJfagRg7VOS8hzNr3QLT7rhS8OFRLQ%3D')\n" +
                "  encryption=(type='AZURE_CSE' master_key = 'kPxX0jzYfIamtnJEUTHwq80Au6NbSgPH5r4BDDwOaO8=')\n" +
                "  file_format = ( FORMAT_NAME  = my_csv_format);";
        assertTrue(sqlparser.parse() == 0);

        TCreateStageStmt createStageStmt = (TCreateStageStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createStageStmt.getStageName().toString().equalsIgnoreCase("mystage"));
        assertTrue(createStageStmt.getExternalStageURL().equalsIgnoreCase("'azure://myaccount.blob.core.windows.net/mycontainer/files/'"));
        assertTrue(createStageStmt.getFileFormatName().equalsIgnoreCase("my_csv_format"));
    }


    public void testAmazonS3FileFormat(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create or replace stage my_csv_stage\n" +
                "  file_format = mycsvformat\n" +
                "  url = 's3://snowflake-docs';";
        assertTrue(sqlparser.parse() == 0);

        TCreateStageStmt createStageStmt = (TCreateStageStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createStageStmt.getStageName().toString().equalsIgnoreCase("my_csv_stage"));
        assertTrue(createStageStmt.getExternalStageURL().equalsIgnoreCase("'s3://snowflake-docs'"));
        assertTrue(createStageStmt.getFileFormatName().equalsIgnoreCase("mycsvformat"));
        // System.out.println(stageLocation.getStageName().toString());
    }

}
