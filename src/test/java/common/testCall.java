package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.sqlenv.ESQLDataObjectType;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.stmt.TCallStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testCall extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CALL DB_NAME.SCHEMA_NAME.PROC_NAME();";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcall);
        TCallStatement callStatement = (TCallStatement)sqlparser.sqlstatements.get(0);
        //System.out.println(callStatement.getRoutineName().toString());
        assertTrue(callStatement.getRoutineName().getDatabaseString().equalsIgnoreCase("DB_NAME"));
        assertTrue(callStatement.getRoutineName().getSchemaString().equalsIgnoreCase("SCHEMA_NAME"));
        assertTrue(callStatement.getRoutineName().getObjectString().equalsIgnoreCase("PROC_NAME"));
    }

    public void testTeradata(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        // there is only database or schema name in teradata, they can't appear both.
        // so, this testcase seems invalid.
        sqlparser.sqltext = "CALL DB_NAME.SCHEMA_NAME.PROC_NAME();";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcall);
        TCallStatement callStatement = (TCallStatement)sqlparser.sqlstatements.get(0);


//        //System.out.println(callStatement.getRoutineName().toString());
//        assertTrue(callStatement.getRoutineName().getDatabaseString().equalsIgnoreCase("DB_NAME"));
//        assertTrue(callStatement.getRoutineName().getSchemaString().equalsIgnoreCase("SCHEMA_NAME"));
//        assertTrue(callStatement.getRoutineName().getObjectString().equalsIgnoreCase("PROC_NAME"));
    }
}
