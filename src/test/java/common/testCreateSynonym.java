package common;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateSynonymStmt;
import junit.framework.TestCase;

public class testCreateSynonym extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE SYNONYM pr FOR payroll";
        assertTrue(sqlparser.parse() == 0);

        TCreateSynonymStmt synonymStmt = (TCreateSynonymStmt)sqlparser.sqlstatements.get(0);
        assertTrue(synonymStmt.getSynonymName().toString().equalsIgnoreCase("pr"));
    }

    public void testPublic(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE OR REPLACE PUBLIC SYNONYM mySyn FOR myPackage";
        assertTrue(sqlparser.parse() == 0);

        TCreateSynonymStmt synonymStmt = (TCreateSynonymStmt)sqlparser.sqlstatements.get(0);
        assertTrue(synonymStmt.isPublic());
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE SYNONYM pr FOR payroll";
        assertTrue(sqlparser.parse() == 0);

        TCreateSynonymStmt synonymStmt = (TCreateSynonymStmt)sqlparser.sqlstatements.get(0);
        assertTrue(synonymStmt.getSynonymName().toString().equalsIgnoreCase("pr"));
    }
}
