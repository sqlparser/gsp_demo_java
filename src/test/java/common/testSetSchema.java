package common;


import gudusoft.gsqlparser.EDbObjectType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSetDatabaseObjectStmt;
import junit.framework.TestCase;

public class testSetSchema extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);
        sqlparser.sqltext = "SET CURRENT SCHEMA = \"ALGODB5 \"";
        assertTrue(sqlparser.parse() == 0);

        TSetDatabaseObjectStmt setDatabaseObjectStmt = (TSetDatabaseObjectStmt) sqlparser.sqlstatements.get(0);
        assertTrue(setDatabaseObjectStmt.getObjectType() == EDbObjectType.schema);
        assertTrue(setDatabaseObjectStmt.getDatabaseObjectName().toString().equalsIgnoreCase("\"ALGODB5 \""));
    }

    public void test2() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);
        sqlparser.sqltext = "SET SCHEMA = \"ALGODB5 \"";
        assertTrue(sqlparser.parse() == 0);

        TSetDatabaseObjectStmt setDatabaseObjectStmt = (TSetDatabaseObjectStmt) sqlparser.sqlstatements.get(0);
        assertTrue(setDatabaseObjectStmt.getObjectType() == EDbObjectType.schema);
        assertTrue(setDatabaseObjectStmt.getDatabaseObjectName().toString().equalsIgnoreCase("\"ALGODB5 \""));
    }

}
