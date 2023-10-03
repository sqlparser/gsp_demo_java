package gaussdb;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.*;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreatePackage;
import junit.framework.TestCase;

public class testGetStmts extends TestCase {

    public void testCreateFunction(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgaussdb);
        sqlparser.sqlfilename = gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"gaussdb/sql grammar/create function.sql";
        assertTrue(sqlparser.getrawsqlstatements() == 0);
        assertTrue(sqlparser.getSqlstatements().size() == 31);
        assertTrue(sqlparser.sqlstatements.get(1) instanceof TCreateFunctionStmt);
        assertTrue(sqlparser.sqlstatements.get(1).getStartToken().lineNo == 13);
        assertTrue(sqlparser.sqlstatements.get(25) instanceof TCreateFunctionStmt);
        assertTrue(sqlparser.sqlstatements.get(25).getStartToken().lineNo == 112);
        assertTrue(sqlparser.sqlstatements.get(26) instanceof TCommonBlock);
        assertTrue(sqlparser.sqlstatements.get(26).getStartToken().lineNo == 133);
    }

    public void testCreatePackage(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgaussdb);
        sqlparser.sqlfilename = gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"gaussdb/sql grammar/create package.sql";
        assertTrue(sqlparser.getrawsqlstatements() == 0);
        assertTrue(sqlparser.getSqlstatements().size() == 9);
        assertTrue(sqlparser.sqlstatements.get(1) instanceof TPlsqlCreatePackage);
        assertTrue(sqlparser.sqlstatements.get(1).getStartToken().lineNo == 7);
        assertTrue(sqlparser.sqlstatements.get(3) instanceof TPlsqlCreatePackage);
        assertTrue(sqlparser.sqlstatements.get(3).getStartToken().lineNo == 14);
        assertTrue(sqlparser.sqlstatements.get(7) instanceof TCommonBlock);
        assertTrue(sqlparser.sqlstatements.get(7).getStartToken().lineNo == 36);
    }

    public void testCreateProcedure(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgaussdb);
        sqlparser.sqlfilename = gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"gaussdb/sql grammar/create procedure.sql";
        assertTrue(sqlparser.getrawsqlstatements() == 0);
        assertTrue(sqlparser.getSqlstatements().size() == 16);
        assertTrue(sqlparser.sqlstatements.get(0) instanceof TCreateProcedureStmt);
        assertTrue(sqlparser.sqlstatements.get(0).getStartToken().lineNo == 6);
        assertTrue(sqlparser.sqlstatements.get(7) instanceof TCreateProcedureStmt);
        assertTrue(sqlparser.sqlstatements.get(7).getStartToken().lineNo == 34);
        assertTrue(sqlparser.sqlstatements.get(10) instanceof TCreateProcedureStmt);
        assertTrue(sqlparser.sqlstatements.get(10).getStartToken().lineNo == 46);
    }

    public void testCreateTrigger(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgaussdb);
        sqlparser.sqlfilename = gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"gaussdb/sql grammar/create trigger.sql";
        assertTrue(sqlparser.getrawsqlstatements() == 0);
        assertTrue(sqlparser.getSqlstatements().size() == 23);
        assertTrue(sqlparser.sqlstatements.get(2) instanceof TCreateFunctionStmt);
        assertTrue(sqlparser.sqlstatements.get(2).getStartToken().lineNo == 11);
        assertTrue(sqlparser.sqlstatements.get(5) instanceof TCreateTriggerStmt);
        assertTrue(sqlparser.sqlstatements.get(5).getStartToken().lineNo == 39);
        assertTrue(sqlparser.sqlstatements.get(7) instanceof TCreateTriggerStmt);
        assertTrue(sqlparser.sqlstatements.get(7).getStartToken().lineNo == 51);
        assertTrue(sqlparser.sqlstatements.get(22) instanceof TDropTriggerSqlStatement);
        assertTrue(sqlparser.sqlstatements.get(22).getStartToken().lineNo == 83);
    }

}
