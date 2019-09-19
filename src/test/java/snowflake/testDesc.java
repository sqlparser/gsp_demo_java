package test.snowflake;


import gudusoft.gsqlparser.EDbObjectType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDescribeStmt;
import junit.framework.TestCase;

public class testDesc extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "desc file format my_csv_format;";
        assertTrue(sqlparser.parse() == 0);

        TDescribeStmt describeStmt = (TDescribeStmt)sqlparser.sqlstatements.get(0);
        assertTrue(describeStmt.getDbObjectType() == EDbObjectType.file_format);
        assertTrue(describeStmt.getDbObjectName().toString().equalsIgnoreCase("my_csv_format"));
    }
}
