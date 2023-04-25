package postgresql;


import gudusoft.gsqlparser.EDbVendor;

import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateExtensionStmt;
import junit.framework.TestCase;

public class testCreateExtension extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE EXTENSION hstore SCHEMA addons;";
        assertTrue(sqlparser.parse() == 0);
        TCreateExtensionStmt createExtensionStmt = (TCreateExtensionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createExtensionStmt.getExtensionName().toString().equalsIgnoreCase("hstore"));
        assertTrue(createExtensionStmt.getSchemaName().toString().equalsIgnoreCase("addons"));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "SET search_path = addons;\n" +
                "CREATE EXTENSION hstore;";
        assertTrue(sqlparser.parse() == 0);
        TCreateExtensionStmt createExtensionStmt = (TCreateExtensionStmt)sqlparser.sqlstatements.get(1);
        assertTrue(createExtensionStmt.getExtensionName().toString().equalsIgnoreCase("hstore"));
    }

}
