package netezza;

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateProcedureStmt;
import junit.framework.TestCase;

public class testCreateProcedure extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE OR REPLACE PROCEDURE test_p() RETURNS INT4 LANGUAGE NZPLSQL AS\n" +
                "  BEGIN_PROC\n" +
                "  DECLARE\n" +
                "  words varchar;\n" +
                "  BEGIN\n" +
                "  words := 'This string is quoted';\n" +
                "  -- This comment is the match for below BEGIN_PROC\n" +
                "  insert into va values ('END_PROC');\n" +
                "  END;\n" +
                "  END_PROC;";

        assertTrue(sqlparser.parse() == 0);
        TCreateProcedureStmt createProcedureStmt = (TCreateProcedureStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createProcedureStmt.getProcedureName().toString().equalsIgnoreCase("test_p"));
        assertTrue(createProcedureStmt.getReturnDataType().getDataType()  == EDataType.int4_t);
        assertTrue(createProcedureStmt.getBodyStatements().size() == 2);
        assertTrue(createProcedureStmt.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sst_assignstmt);
        assertTrue(createProcedureStmt.getBodyStatements().get(1).sqlstatementtype == ESqlStatementType.sstinsert);
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE OR REPLACE PROCEDURE exec_nzplsql_block(text) RETURNS BOOLEAN\n" +
                "LANGUAGE NZPLSQL AS\n" +
                "BEGIN_PROC\n" +
                "    DECLARE lRet BOOLEAN;\n" +
                "    DECLARE sid INTEGER;\n" +
                "    DECLARE nm varchar;\n" +
                "    DECLARE cr varchar;\n" +
                "BEGIN\n" +
                "    sid := current_sid;\n" +
                "    nm := 'any_block' || sid || '()';\n" +
                "    cr = 'CREATE OR REPLACE PROCEDURE ' || nm ||\n" +
                "        ' RETURNS BOOL LANGUAGE NZPLSQL AS BEGIN_PROC '\n" +
                "        || \\$1 || ' END_PROC';\n" +
                "    EXECUTE IMMEDIATE cr;\n" +
                "    EXECUTE IMMEDIATE 'SELECT ' || nm;\n" +
                "    EXECUTE IMMEDIATE 'DROP PROCEDURE ' || nm;\n" +
                "    RETURN TRUE;\n" +
                "END;\n" +
                "END_PROC;";
        assertTrue(sqlparser.parse() == 0);
        TCreateProcedureStmt createProcedureStmt = (TCreateProcedureStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createProcedureStmt.getProcedureName().toString().equalsIgnoreCase("exec_nzplsql_block"));
        assertTrue(createProcedureStmt.getReturnDataType().getDataType()  == EDataType.bool_t);
        //System.out.println(createProcedureStmt.getReturnDataType().getDataType());
        assertTrue(createProcedureStmt.getBodyStatements().size() == 7);
        assertTrue(createProcedureStmt.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sst_assignstmt);
        assertTrue(createProcedureStmt.getBodyStatements().get(3).sqlstatementtype == ESqlStatementType.sstplsql_execimmestmt);
        //System.out.println(createProcedureStmt.getBodyStatements().get(3).sqlstatementtype);
    }
}
