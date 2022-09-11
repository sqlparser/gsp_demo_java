package netezza;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSetCatalogStmt;
import junit.framework.TestCase;

public class testSetCatalogStmt extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "SET CATALOG \"netschema\";";
        assertTrue(sqlparser.parse() == 0);
        TSetCatalogStmt setCatalogStmt = (TSetCatalogStmt)sqlparser.sqlstatements.get(0);
        assertTrue(setCatalogStmt.getCatalogName().toString().equalsIgnoreCase("\"netschema\""));


    }
}
