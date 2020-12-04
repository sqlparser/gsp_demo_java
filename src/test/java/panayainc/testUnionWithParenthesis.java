package panayainc;
/*
 * Date: 11-11-28
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCursorDeclStmt;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateProcedure;
import junit.framework.TestCase;

public class testUnionWithParenthesis extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "Procedure GSI_REFRESH_DQ_DASHBOARD is\n" +
                "cursor cur1 is\n" +
                "(\n" +
                "select\n" +
                "acpid, ownerid,\n" +
                "owner, plnum2, acpstatus,\n" +
                "statusname, oracleid, fileas,\n" +
                "start_date, end_date,\n" +
                "hours_submitted\n" +
                "from xxcust.gsi_ps_data_quality_list1_v\n" +
                "Minus\n" +
                "select\n" +
                "acpid, ownerid,\n" +
                "owner, plnum2, acpstatus,\n" +
                "statusname, oracleid, fileas,\n" +
                "start_date, end_date,\n" +
                "hours_submitted\n" +
                "from xxcust.gsi_ps_data_quality_list1\n" +
                ")\n" +
                "union\n" +
                "(\n" +
                "select\n" +
                "acpid, ownerid,\n" +
                "owner, plnum2, acpstatus,\n" +
                "statusname, oracleid, fileas,\n" +
                "start_date, end_date,\n" +
                "hours_submitted\n" +
                "from xxcust.gsi_ps_data_quality_list1\n" +
                "Minus\n" +
                "select\n" +
                "acpid, ownerid,\n" +
                "owner, plnum2, acpstatus,\n" +
                "statusname, oracleid, fileas,\n" +
                "start_date, end_date,\n" +
                "hours_submitted\n" +
                "from xxcust.gsi_ps_data_quality_list1_v\n" +
                ")\n" +
                ";\n" +
                "\n" +
                "\n" +
                "begin\n" +
                "\n" +
                "for rec1 in cur1 loop\n" +
                "begin\n" +
                "delete from xxcust.gsi_ps_data_quality_list1 where acpid = rec1.acpid;\n" +
                "Insert into xxcust.gsi_ps_data_quality_list1\n" +
                "Select * from xxcust.gsi_ps_data_quality_list1_v where acpid = rec1.acpid;\n" +
                "exception\n" +
                "when others then\n" +
                "null;\n" +
                "end;\n" +
                "end loop;\n" +
                "commit;\n" +
                "\n" +
                "end GSI_REFRESH_DQ_DASHBOARD;";

        assertTrue(sqlparser.parse() == 0);

        TPlsqlCreateProcedure createProcedure = (TPlsqlCreateProcedure)sqlparser.sqlstatements.get(0);
        TCursorDeclStmt cursorDeclStmt =  (TCursorDeclStmt)createProcedure.getDeclareStatements().get(0);
        TSelectSqlStatement selectSqlStatement = cursorDeclStmt.getSubquery();
        assertTrue(selectSqlStatement.isCombinedQuery());
        assertTrue(selectSqlStatement.getSetOperator() == TSelectSqlStatement.setOperator_union);
        TSelectSqlStatement leftStmt = selectSqlStatement.getLeftStmt();
        TSelectSqlStatement righStmt = selectSqlStatement.getRightStmt();

        assertTrue(leftStmt.isCombinedQuery());
        assertTrue(leftStmt.getSetOperator() == TSelectSqlStatement.setOperator_minus);
        TSelectSqlStatement leftleftStmt = leftStmt.getLeftStmt();
        TSelectSqlStatement righleftStmt = leftStmt.getRightStmt();

        assertTrue(righStmt.isCombinedQuery());
        assertTrue(righStmt.getSetOperator() == TSelectSqlStatement.setOperator_minus);
        TSelectSqlStatement leftrightStmt = righStmt.getLeftStmt();
        TSelectSqlStatement rightrighStmt = righStmt.getRightStmt();

       // System.out.println(leftStmt.toString());
        //System.out.println("\n\nright:\n\n");
        //System.out.println(righStmt.toString());
        //System.out.println(selectSqlStatement.toString());
    }
}
