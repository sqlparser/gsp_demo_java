package sybase;
/*
 * Date: 13-9-2
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.mssql.TMssqlBlock;
import gudusoft.gsqlparser.stmt.mssql.TMssqlCreateProcedure;
import junit.framework.TestCase;

public class testDumpTran extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsybase);
        sqlparser.sqltext = "create procedure po_aaa\n" +
                "as\n" +
                "Begin\n" +
                "\n" +
                "dump tran db_policy with no_log\n" +
                "\n" +
                "End";

        int i = sqlparser.parse() ;
        assertTrue(i == 0);
        TMssqlCreateProcedure procedure = (TMssqlCreateProcedure)sqlparser.sqlstatements.get(0);
        assertTrue(procedure.getProcedureName().toString().equalsIgnoreCase("po_aaa"));
        assertTrue(procedure.getBodyStatements().size() == 1);
        assertTrue(procedure.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstmssqlblock);
        TMssqlBlock block = (TMssqlBlock)procedure.getBodyStatements().get(0);
        assertTrue(block.getBodyStatements().size() == 1);
        assertTrue(block.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstsybasedumpTran);
       // System.out.println(block.getBodyStatements().get(0).sqlstatementtype);

    }

}
