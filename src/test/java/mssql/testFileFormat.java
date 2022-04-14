package mssql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EFileFormat;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.snowflake.TCreateFileFormatStmt;
import junit.framework.TestCase;

public class testFileFormat extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvazuresql);
        sqlparser.sqltext = "CREATE EXTERNAL FILE FORMAT jsonFileFormat\n" +
                "WITH (\n" +
                "FORMAT_TYPE = JSON,\n" +
                "DATA_COMPRESSION = 'org.apache.hadoop.io.compress.SnappyCodec'\n" +
                ");";
        assertTrue(sqlparser.parse() == 0);

        TCreateFileFormatStmt createFileFormatStmt = (TCreateFileFormatStmt)sqlparser.sqlstatements.get(0);

        assertTrue(createFileFormatStmt.getFileFormatName().toString().equalsIgnoreCase("jsonFileFormat"));
        assertTrue(createFileFormatStmt.getFileFormat() == EFileFormat.json);
        assertTrue(createFileFormatStmt.getDataCompression().equalsIgnoreCase("'org.apache.hadoop.io.compress.SnappyCodec'"));
    }
}
