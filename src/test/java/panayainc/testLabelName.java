package panayainc;
/*
 * Date: 11-6-20
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.oracle.TBasicStmt;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlGotoStmt;
import junit.framework.TestCase;

public class testLabelName extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "DECLARE\n" +
                "\n" +
                "   done  BOOLEAN;\n" +
                "\n" +
                "BEGIN\n" +
                "\n" +
                "    FOR i IN 1..50 LOOP\n" +
                "\n" +
                "      IF done THEN\n" +
                "\n" +
                "         GOTO end_loop;\n" +
                "\n" +
                "      END IF;\n" +
                "\n" +
                "   <<end_loop>> \n" +
                "\n" +
                "   NULL;\n" +
                "\n" +
                "   END LOOP; \n" +
                "\n" +
                "END;";
        assertTrue(sqlparser.parse() == 0);

        for(int i=0;i<sqlparser.sqlstatements.size();i++){
            iterateStmt(sqlparser.sqlstatements.get(i));
        }

    }

    protected static void iterateStmt(TCustomSqlStatement stmt){
        //System.out.println(stmt.sqlstatementtype.toString());
        switch (stmt.sqlstatementtype){
            case sstplsql_gotostmt:
                TPlsqlGotoStmt gotoStmt = (TPlsqlGotoStmt)stmt;
               // System.out.println("goto label: "+gotoStmt.getGotolabelName());
                break;
            case sstplsql_procbasicstmt:
                TBasicStmt basicStmt = (TBasicStmt)stmt;
                //System.out.println("label: "+basicStmt.getLabelName());

                break;
            default:
        }
        for (int i=0;i<stmt.getStatements().size();i++){
           iterateStmt(stmt.getStatements().get(i));
        }
    }


}
