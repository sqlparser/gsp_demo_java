package netezza;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSetSchemaStmt;
import junit.framework.TestCase;

public class testSetSchemaStmt extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "SET SCHEMA \"netschema\"";
        assertTrue(sqlparser.parse() == 0);
        TSetSchemaStmt setCatalogStmt = (TSetSchemaStmt)sqlparser.sqlstatements.get(0);
        assertTrue(setCatalogStmt.getSchemaName().toString().equalsIgnoreCase("\"netschema\""));


    }
}
