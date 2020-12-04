package common;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDropSchemaSqlStatement;
import junit.framework.TestCase;

public class testDropSchema  extends TestCase {

    public void testDropSchema(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "drop schema spence;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstdropschema);
        TDropSchemaSqlStatement dropSchemaSqlStatement = (TDropSchemaSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(dropSchemaSqlStatement.getSchemaName().toString().equalsIgnoreCase("spence"));
    }

}
