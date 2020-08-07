package snowflake;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.snowflake.TCreateFileFormatStmt;
import junit.framework.TestCase;

public class testCreateFileFormat extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE FILE FORMAT IF NOT EXISTS TestFormat11\n" +
                "TYPE = CSV\n" +
                "FIELD_DELIMITER = 'c';";
        assertTrue(sqlparser.parse() == 0);

        TCreateFileFormatStmt createFileFormatStmt = (TCreateFileFormatStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createFileFormatStmt.getFileFormatName().toString().equalsIgnoreCase("TestFormat11"));
         assertTrue(createFileFormatStmt.getFormatOptions().toString().trim().equalsIgnoreCase("TYPE = CSV\n" +
                 "FIELD_DELIMITER = 'c'"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE FILE FORMAT IF NOT EXISTS TestFormat\n" +
                "TYPE = CSV\n" +
                "COMMENT = 'Test comment';";
        assertTrue(sqlparser.parse() == 0);

        TCreateFileFormatStmt createFileFormatStmt = (TCreateFileFormatStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createFileFormatStmt.getFileFormatName().toString().equalsIgnoreCase("TestFormat"));
        assertTrue(createFileFormatStmt.getFormatOptions().toString().trim().equalsIgnoreCase("TYPE = CSV\n" +
                "COMMENT = 'Test comment'"));
        //System.out.println(createFileFormatStmt.getFormatOptions().toString().trim());
    }
}
