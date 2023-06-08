package oracle;
/*
 * Date: 14-6-20
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TAlterViewStatement;
import gudusoft.gsqlparser.stmt.TExecImmeStmt;
import junit.framework.TestCase;

public class testExecImmediate extends TestCase {

   public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "EXECUTE IMMEDIATE \n" +
                "\t'SELECT /*+ PARALLEL 4 */ count(1) FROM SCHEMA.TABLE_SAMPLE (P'||TO_CHAR(v_processDt,'YYYYMMDD')||')'\n" +
                "  INTO var;";
        assertTrue(sqlparser.parse() == 0);

       //System.out.println(sqlparser.sqlstatements.get(0).sqlstatementtype);
       assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstplsql_execimmestmt);
       TExecImmeStmt execImmeStmt = (TExecImmeStmt)sqlparser.sqlstatements.get(0);
       assertTrue(execImmeStmt.getIntoVariables().getExpression(0).toString().equalsIgnoreCase("var"));
     //  assertTrue(execImmeStmt.getDynamicStatements().get(0).sqlstatementtype == ESqlStatementType.sstselect);
       //TSelectSqlStatement select = (TSelectSqlStatement)execImmeStmt.getDynamicStatements().get(0);
       //System.out.println(select.toString());
    }

    public void test2(){

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        // sqlparser.sqltext = "EXECUTE IMMEDIATE q'[alter view SCHEMATEMP.vls_master_d compile  ]';--' ";
        sqlparser.sqltext = "EXECUTE IMMEDIATE 'alter view SCHEMATEMP.vls_master_d compile  ';--' ";
         assertTrue(sqlparser.parse() == 0);

        //System.out.println(sqlparser.sqlstatements.get(0).sqlstatementtype);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstplsql_execimmestmt);
        TExecImmeStmt execImmeStmt = (TExecImmeStmt)sqlparser.sqlstatements.get(0);
        //System.out.println(execImmeStmt.getDynamicStatements().get(0).toString());
        //assertTrue(execImmeStmt.getIntoVariables().getExpression(0).toString().equalsIgnoreCase("var"));
        assertTrue(execImmeStmt.getDynamicStatements().get(0).sqlstatementtype == ESqlStatementType.sstalterview);
        TAlterViewStatement cv = (TAlterViewStatement)execImmeStmt.getDynamicStatements().get(0);
        //System.out.println(cv.getViewName().toString());
        assertTrue(cv.getViewName().toString().equalsIgnoreCase("SCHEMATEMP.vls_master_d"));
     }

    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "EXECUTE IMMEDIATE 'INSERT INTO XXX VALUES (''AA'', ''BB'')';";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstplsql_execimmestmt);
        TExecImmeStmt execImmeStmt = (TExecImmeStmt) sqlparser.sqlstatements.get(0);
        //System.out.println(execImmeStmt.getDynamicSQL().toString());
        assertTrue(execImmeStmt.getDynamicSQL().toString().equalsIgnoreCase("INSERT INTO XXX VALUES ('AA', 'BB')"));
    }

    public void test4(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "EXECUTE IMMEDIATE Q'[INSERT INTO XXX VALUES ('AA', 'BB')]';";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstplsql_execimmestmt);
        TExecImmeStmt execImmeStmt = (TExecImmeStmt) sqlparser.sqlstatements.get(0);
        //System.out.println(execImmeStmt.getDynamicSQL().toString());
        assertTrue(execImmeStmt.getDynamicSQL().toString().equalsIgnoreCase("INSERT INTO XXX VALUES ('AA', 'BB')"));
    }

}
