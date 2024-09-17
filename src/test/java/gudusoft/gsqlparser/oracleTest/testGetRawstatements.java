package gudusoft.gsqlparser.oracleTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testGetRawstatements extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE TABLE myTable\n" +
                "(\n" +
                "  Col1 int\n" +
                ")\n" +
                "\n" +
                "INSERT INTO myTable (Col1)\n" +
                "SELECT Something\n" +
                "  FROM Somewhere;\n" +
                "\n" +
                "SELECT Col1\n" +
                "  FROM myTable\n";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.size() == 3);
    }


}

