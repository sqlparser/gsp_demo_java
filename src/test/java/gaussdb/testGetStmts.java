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
        sqlparser.sqlfilename = gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"gaussdb/sql grammar/CREATE FUNCTION.sql";
        assertTrue(sqlparser.getrawsqlstatements() == 0);
        assertTrue(sqlparser.getSqlstatements().size() == 29);
        assertTrue(sqlparser.sqlstatements.get(1) instanceof TCreateFunctionStmt);
        assertTrue(sqlparser.sqlstatements.get(1).getStartToken().lineNo == 13);
        assertTrue(sqlparser.sqlstatements.get(23) instanceof TCreateFunctionStmt);
        assertTrue(sqlparser.sqlstatements.get(23).getStartToken().lineNo == 104);
        assertTrue(sqlparser.sqlstatements.get(28) instanceof TCommonBlock);
        assertTrue(sqlparser.sqlstatements.get(28).getStartToken().lineNo == 162);
    }

    public void testCreatePackage(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgaussdb);
        sqlparser.sqlfilename = gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"gaussdb/sql grammar/CREATE PACKAGE.sql";
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
        sqlparser.sqlfilename = gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"gaussdb/sql grammar/CREATE PROCEDURE.sql";
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
        sqlparser.sqlfilename = gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"gaussdb/sql grammar/CREATE TRIGGER.sql";
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
