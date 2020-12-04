package sybase;
/*
 * Date: 14-6-12
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.mssql.TMssqlBlock;
import gudusoft.gsqlparser.stmt.mssql.TMssqlCreateProcedure;
import junit.framework.TestCase;

public class testCreateProcedure extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsybase);
        sqlparser.sqltext = "CREATE PROC dbo.proc1\n" +
                "AS\n" +
                "\n" +
                "\n" +
                "\n" +
                "SET FLUSHMESSAGE ON\n" +
                "SET NOCOUNT ON\n" +
                "\n" +
                "BEGIN\n" +
                "\n" +
                "      \n" +
                "    select * from schema1.table1\n" +
                "SET FLUSHMESSAGE OFF\n" +
                "SET NOCOUNT OFF\n" +
                "\n" +
                "END";
        int i = sqlparser.parse() ;
        assertTrue(i == 0);

        TMssqlCreateProcedure p = (TMssqlCreateProcedure)sqlparser.sqlstatements.get(0);
        assertTrue(p.getProcedureName().toString().equalsIgnoreCase("dbo.proc1"));
        assertTrue(p.getBodyStatements().size() == 3);
        //System.out.println(p.getBodyStatements().get(0).sqlstatementtype);
        assertTrue(p.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstmssqlset);
        assertTrue(p.getBodyStatements().get(1).sqlstatementtype == ESqlStatementType.sstmssqlset);
        assertTrue(p.getBodyStatements().get(2).sqlstatementtype == ESqlStatementType.sstmssqlblock);
        assertTrue(((TMssqlBlock)(p.getBodyStatements().get(2))).getBodyStatements().size() == 3);
    }

}
