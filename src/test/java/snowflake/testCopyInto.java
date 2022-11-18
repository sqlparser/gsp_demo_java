package snowflake;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TStageLocation;
import gudusoft.gsqlparser.stmt.snowflake.TSnowlflakeCopyIntoStmt;
import junit.framework.TestCase;

public class testCopyInto extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "copy into mycsvtable\n" +
                "  from @my_csv_stage/tutorials/dataloading/contacts1.csv\n" +
                "  on_error = 'skip_file';";
        //System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);

        TSnowlflakeCopyIntoStmt copyIntoStmt = (TSnowlflakeCopyIntoStmt)sqlparser.sqlstatements.get(0);
        assertTrue(copyIntoStmt.getCopyIntoType() == TSnowlflakeCopyIntoStmt.COPY_INTO_TABLE);
        assertTrue(copyIntoStmt.getTableName().toString().equalsIgnoreCase("mycsvtable"));
        TStageLocation stageLocation = copyIntoStmt.getStageLocation();
        assertTrue(stageLocation.getStageLocationType() == TStageLocation.EStageLocationType.internalNamed);
        assertTrue(stageLocation.getStageName().toString().equalsIgnoreCase("my_csv_stage"));
        assertTrue(stageLocation.getPath().toString().equals("/tutorials/dataloading/contacts1.csv"));
       // System.out.println(stageLocation.getPath());
    }

    public void testPattern(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "copy into mycsvtable\n" +
                "  from @my_csv_stage/tutorials/dataloading/\n" +
                "  pattern='.*contacts[1-5].csv'\n" +
                "  on_error = 'skip_file';";
        assertTrue(sqlparser.parse() == 0);

        TSnowlflakeCopyIntoStmt copyIntoStmt = (TSnowlflakeCopyIntoStmt)sqlparser.sqlstatements.get(0);
        assertTrue(copyIntoStmt.getCopyIntoType() == TSnowlflakeCopyIntoStmt.COPY_INTO_TABLE);
        assertTrue(copyIntoStmt.getTableName().toString().equalsIgnoreCase("mycsvtable"));
        TStageLocation stageLocation = copyIntoStmt.getStageLocation();
        assertTrue(stageLocation.getStageLocationType() == TStageLocation.EStageLocationType.internalNamed);
        assertTrue(stageLocation.getStageName().toString().equalsIgnoreCase("my_csv_stage"));
        assertTrue(stageLocation.getPath().toString().equals("/tutorials/dataloading"));
        assertTrue(copyIntoStmt.getRegex_pattern().equalsIgnoreCase("'.*contacts[1-5].csv'"));
    }

    public void testFileFormat(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "copy into mytable from @~/staged\n" +
                "file_format = (format_name = 'mycsv');";
        assertTrue(sqlparser.parse() == 0);

        TSnowlflakeCopyIntoStmt copyIntoStmt = (TSnowlflakeCopyIntoStmt)sqlparser.sqlstatements.get(0);
        assertTrue(copyIntoStmt.getCopyIntoType() == TSnowlflakeCopyIntoStmt.COPY_INTO_TABLE);
        assertTrue(copyIntoStmt.getTableName().toString().equalsIgnoreCase("mytable"));
        TStageLocation stageLocation = copyIntoStmt.getStageLocation();
        assertTrue(stageLocation.getStageLocationType() == TStageLocation.EStageLocationType.internalUser);
        //assertTrue(stageLocation.getStageName().toString().equalsIgnoreCase("~"));
        assertTrue(stageLocation.getPath().toString().equals("/staged"));
        assertTrue(copyIntoStmt.getFileFormatName().equalsIgnoreCase("'mycsv'"));
    }

    public void testFileFormatType(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "copy into mytable from @~/staged\n" +
                "file_format = (type = csv);";
        assertTrue(sqlparser.parse() == 0);

        TSnowlflakeCopyIntoStmt copyIntoStmt = (TSnowlflakeCopyIntoStmt)sqlparser.sqlstatements.get(0);
        assertTrue(copyIntoStmt.getCopyIntoType() == TSnowlflakeCopyIntoStmt.COPY_INTO_TABLE);
        assertTrue(copyIntoStmt.getTableName().toString().equalsIgnoreCase("mytable"));
        assertTrue(copyIntoStmt.getFileFormatType().equalsIgnoreCase("csv"));
    }

    public void testFromS3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "copy into mytable\n" +
                "  from 's3://mybucket/data/files'\n" +
                "  storage_integration = myint\n" +
                "  encryption=(master_key = 'eSxX0jzYfIamtnBKOEOwq80Au6NbSgPH5r4BDDwOaO8=')\n" +
                "  file_format = (format_name = my_csv_format);";
        assertTrue(sqlparser.parse() == 0);

        TSnowlflakeCopyIntoStmt copyIntoStmt = (TSnowlflakeCopyIntoStmt)sqlparser.sqlstatements.get(0);
        assertTrue(copyIntoStmt.getCopyIntoType() == TSnowlflakeCopyIntoStmt.COPY_INTO_TABLE);
        assertTrue(copyIntoStmt.getTableName().toString().equalsIgnoreCase("mytable"));

        TStageLocation stageLocation = copyIntoStmt.getStageLocation();
        assertTrue(stageLocation.getStageLocationType() == TStageLocation.EStageLocationType.location);


        assertTrue(copyIntoStmt.getStageLocation().getExternalLocation().toString().equalsIgnoreCase("'s3://mybucket/data/files'"));
        assertTrue(copyIntoStmt.getFileFormatName().equalsIgnoreCase("my_csv_format"));
    }

    public void testFromFiles(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "copy into load1 from @%load1/data1/\n" +
                "    files=('test1.csv', 'test2.csv')\n" +
                "    force=true;";
        assertTrue(sqlparser.parse() == 0);

        TSnowlflakeCopyIntoStmt copyIntoStmt = (TSnowlflakeCopyIntoStmt)sqlparser.sqlstatements.get(0);
        assertTrue(copyIntoStmt.getCopyIntoType() == TSnowlflakeCopyIntoStmt.COPY_INTO_TABLE);
        assertTrue(copyIntoStmt.getTableName().toString().equalsIgnoreCase("load1"));
        TStageLocation stageLocation = copyIntoStmt.getStageLocation();
        assertTrue(stageLocation.getStageLocationType() == TStageLocation.EStageLocationType.internalTable);

        assertTrue(copyIntoStmt.getStageLocation().getTableName().toString().equalsIgnoreCase("load1"));
        assertTrue(copyIntoStmt.getStageLocation().getPath().toString().equalsIgnoreCase("/data1"));
        assertTrue(copyIntoStmt.getFileList().size()==2);
        assertTrue(copyIntoStmt.getFileList().get(0).equalsIgnoreCase("'test1.csv'"));
        assertTrue(copyIntoStmt.getFileList().get(1).equalsIgnoreCase("'test2.csv'"));
    }

    public void testIntoLocation(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "copy into 's3://mybucket/unload/'\n" +
                "  from mytable\n" +
                "  credentials = (aws_key_id='xxxx' aws_secret_key='xxxxx' aws_token='xxxxxx')\n" +
                "  file_format = (format_name = my_csv_format);";
        assertTrue(sqlparser.parse() == 0);

        TSnowlflakeCopyIntoStmt copyIntoStmt = (TSnowlflakeCopyIntoStmt)sqlparser.sqlstatements.get(0);
        assertTrue(copyIntoStmt.getCopyIntoType() == TSnowlflakeCopyIntoStmt.COPY_INTO_LOCATION);
        assertTrue(copyIntoStmt.getTableName().toString().equalsIgnoreCase("mytable"));
        assertTrue(copyIntoStmt.getStageLocation().getExternalLocation().toString().equalsIgnoreCase("'s3://mybucket/unload/'"));
    }


}
