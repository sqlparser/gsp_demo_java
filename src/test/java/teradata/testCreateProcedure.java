package test.teradata;


import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TDeclareVariable;
import gudusoft.gsqlparser.stmt.TMergeSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.mssql.TMssqlDeclare;
import gudusoft.gsqlparser.stmt.mssql.TMssqlOpen;
import gudusoft.gsqlparser.stmt.teradata.TTeradataCreateProcedure;
import junit.framework.TestCase;

public class testCreateProcedure extends TestCase {

    public void test1(){

     TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
     sqlparser.sqltext = "create proc merge_salesdetail\n" +
             "as\n" +
             "merge into salesdetail as s\n" +
             "using salesdetailupdates as u \n" +
             "on s.stor_id = u.stor_id and\n" +
             "  s.ord_num = u.ord_num and\n" +
             "  s.title_id = u.title_id\n" +
             "when not matched then   \n" +
             "    insert (stor_id, ord_num, title_id, qty, discount) values(u.stor_id, u.ord_num, u.title_id, u.qty, u.discount) \n" +
             "when matched then   \n" +
             "    update set qty=u.qty, discount=u.discount";
     assertTrue(sqlparser.parse() == 0);

        TTeradataCreateProcedure cp = (TTeradataCreateProcedure)sqlparser.sqlstatements.get(0);
        assertTrue(cp.getProcedureName().toString().equalsIgnoreCase("merge_salesdetail"));

        assertTrue(cp.getBodyStatements().size() == 1);

       //System.out.println(cp.getBodyStatements().get(0).sqlstatementtype.toString());

        assertTrue(cp.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstmerge);
        TMergeSqlStatement merge = (TMergeSqlStatement)cp.getBodyStatements().get(0);
        assertTrue(merge.getTargetTable().toString().equalsIgnoreCase("salesdetail"));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "CREATE PROCEDURE EDW_TABLES_DEV.samplesp2()\n" +
                "              DYNAMIC RESULT SETS 1\n" +
                "BEGIN\n" +
                "\n" +
                "      /* SPL Statements*/\n" +
                "\n" +
                "   DECLARE cur1 CURSOR WITH RETURN ONLY FOR\n" +
                "\n" +
                "   --insert into ITEM_COST select * from ITEM_COST_STG;\n" +
                "\n" +
                "   select * from ITEM_COST;\n" +
                "  \n" +
                "\n" +
                "   open cur1;\n" +
                "\n" +
                "END;";
        assertTrue(sqlparser.parse() == 0);

        TTeradataCreateProcedure cp = (TTeradataCreateProcedure)sqlparser.sqlstatements.get(0);
        assertTrue(cp.getProcedureName().toString().equalsIgnoreCase("EDW_TABLES_DEV.samplesp2"));

        assertTrue(cp.getBodyStatements().size() == 2);

        assertTrue(cp.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstmssqldeclare);
        TMssqlDeclare declare = (TMssqlDeclare)cp.getBodyStatements().get(0);
        assertTrue(declare.getCursorName().toString().equalsIgnoreCase("cur1"));
        TSelectSqlStatement subquery = declare.getSubquery();
        assertTrue(subquery.getTables().getTable(0).toString().equalsIgnoreCase("ITEM_COST"));


        assertTrue(cp.getBodyStatements().get(1).sqlstatementtype == ESqlStatementType.sstmssqlopen);
        TMssqlOpen open = (TMssqlOpen)cp.getBodyStatements().get(1);
        assertTrue(open.getCursorName().toString().equalsIgnoreCase("cur1"));

        //assertTrue(cp.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstmerge);
    }

    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "Create  PROCEDURE EDW_TABLES_DEV.samplesp7(out mytest integer)\n" +
                "DYNAMIC RESULT SETS 1\n" +
                "P1: BEGIN\n" +
                "   DECLARE V1 INTEGER;\n" +
                "   DECLARE EXIT HANDLER FOR SQLEXCEPTION\n" +
                "\n" +
                "   select loc_id  into v1 from EDW_TABLES_DEV.ITEM_COST_STG;\n" +
                "\n" +
                "   delete from EDW_TABLES_DEV.ITEM_COST_STG where loc_id=10;\n" +
                " \n" +
                "   UPDATE EDW_TABLES.ACCT_ACCT_MSG\n" +
                "   SET MSG_CNT = :v1\n" +
                "   where MSG_CNT = :v1;\n" +
                "\n" +
                "  \n" +
                "END P1;";
        assertTrue(sqlparser.parse() == 0);

        TTeradataCreateProcedure cp = (TTeradataCreateProcedure)sqlparser.sqlstatements.get(0);
        assertTrue(cp.getProcedureName().toString().equalsIgnoreCase("EDW_TABLES_DEV.samplesp7"));

        assertTrue(cp.getBodyStatements().size() == 4);
        assertTrue(cp.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstmssqldeclare);

        TMssqlDeclare declare = (TMssqlDeclare)cp.getBodyStatements().get(0);
        assertTrue(declare.getDeclareType() == EDeclareType.variable);
        TDeclareVariable variable = declare.getVariables().getDeclareVariable(0);
        assertTrue(variable.getDatatype().getDataType() == EDataType.integer_t);
        assertTrue(variable.getVariableName().toString().equalsIgnoreCase("V1"));

        assertTrue(cp.getBodyStatements().get(1).sqlstatementtype == ESqlStatementType.sstmssqldeclare);
        declare = (TMssqlDeclare)cp.getBodyStatements().get(1);
        assertTrue(declare.getDeclareType() == EDeclareType.handlers);
        TSelectSqlStatement subquery = declare.getSubquery();
        assertTrue(subquery.getTables().getTable(0).toString().equalsIgnoreCase("EDW_TABLES_DEV.ITEM_COST_STG"));

        assertTrue(cp.getBodyStatements().get(2).sqlstatementtype == ESqlStatementType.sstdelete);
        assertTrue(cp.getBodyStatements().get(3).sqlstatementtype == ESqlStatementType.sstupdate);


    }
}
