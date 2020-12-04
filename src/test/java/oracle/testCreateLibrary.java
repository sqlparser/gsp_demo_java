package oracle;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.stmt.oracle.TOracleCreateLibraryStmt;
import junit.framework.TestCase;

public class testCreateLibrary extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE LIBRARY app_lib as '${ORACLE_HOME}/lib/app_lib.so' AGENT 'sales.hq.acme.example.com';";
        assertTrue(sqlparser.parse() == 0);

        TOracleCreateLibraryStmt libraryStmt = (TOracleCreateLibraryStmt)sqlparser.sqlstatements.get(0);
        assertTrue(libraryStmt.getLibraryName().toString().equalsIgnoreCase("app_lib"));
        assertTrue(libraryStmt.getFileName().toString().equalsIgnoreCase("'${ORACLE_HOME}/lib/app_lib.so'"));
        assertTrue(libraryStmt.getDbLink().toString().equalsIgnoreCase("'sales.hq.acme.example.com'"));
    }

    public void testGetSchemaName(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "create library USER1.\"app_lib\" as '${ORACLE_HOME}/lib/app_lib.so' agent 'sales.hq.acme.example.com';";
        assertTrue(sqlparser.parse() == 0);

        TOracleCreateLibraryStmt libraryStmt = (TOracleCreateLibraryStmt)sqlparser.sqlstatements.get(0);
        TObjectName libraryName = libraryStmt.getLibraryName();
        assertTrue(libraryName.toString().equalsIgnoreCase("USER1.\"app_lib\""));
        assertTrue(libraryName.getSchemaToken().toString().equalsIgnoreCase("USER1"));
    }
}
