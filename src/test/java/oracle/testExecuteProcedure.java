package oracle;
/*
 * Date: 12-5-6
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.stmt.oracle.TOracleExecuteProcedure;
import junit.framework.TestCase;

public class testExecuteProcedure extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE TABLE a (a VARCHAR2(10));\n" +
                "EXECUTE some_package.some_proc('ARG')";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatetable);
        assertTrue(sqlparser.sqlstatements.get(1).sqlstatementtype == ESqlStatementType.sstoracleexecuteprocedure);

        TOracleExecuteProcedure executeProcedure = (TOracleExecuteProcedure)sqlparser.sqlstatements.get(1);
        assertTrue(executeProcedure.getProcedureName().toString().equalsIgnoreCase("some_package.some_proc"));
        assertTrue(executeProcedure.getProcedureParameters().getExpression(0).toString().equalsIgnoreCase("'ARG'"));

    }
}
