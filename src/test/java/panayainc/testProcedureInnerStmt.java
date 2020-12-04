package panayainc;
/*
 * Date: 11-4-3
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreatePackage;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateProcedure;
import junit.framework.TestCase;

public class testProcedureInnerStmt extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "PACKAGE BODY a IS\n" +
                "\tPROCEDURE main\n" +
                "\t\tprocedure inner is\n" +
                "\t\tbegin\n" +
                "\t\t\tnull;\n" +
                "\t\tend inner;\n" +
                "\tIS\n" +
                "\tBEGIN\n" +
                "\t  null;\n" +
                "\tend main;\n" +
                "end;";
        assertTrue(sqlparser.parse() == 0);

        TPlsqlCreatePackage pkg = (TPlsqlCreatePackage)sqlparser.sqlstatements.get(0);
        TPlsqlCreateProcedure prc = (TPlsqlCreateProcedure)pkg.getDeclareStatements().get(0);
        TCustomSqlStatement stmt =  prc.getInnerStatements().get(0);

        assertTrue(prc.toString().trim().equalsIgnoreCase("PROCEDURE main\n" +
                "\t\tprocedure inner is\n" +
                "\t\tbegin\n" +
                "\t\t\tnull;\n" +
                "\t\tend inner;\n" +
                "\tIS\n" +
                "\tBEGIN\n" +
                "\t  null;\n" +
                "\tend main"));

        assertTrue(stmt.toString().equalsIgnoreCase("procedure inner is\n" +
                "\t\tbegin\n" +
                "\t\t\tnull;\n" +
                "\t\tend inner"));
    }

}
