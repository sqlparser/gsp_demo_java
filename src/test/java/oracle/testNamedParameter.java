package oracle;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.stmt.oracle.TBasicStmt;
import gudusoft.gsqlparser.stmt.TCommonBlock;
import junit.framework.TestCase;

public class testNamedParameter extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "begin\t\n" +
                "\ttest_function_call(a => b, c => d);\n" +
                "end;";
        assertTrue(sqlparser.parse() == 0);
        TCommonBlock block = (TCommonBlock)sqlparser.sqlstatements.get(0);
        TBasicStmt basicStmt  = (TBasicStmt)block.getBodyStatements().get(0);
        TFunctionCall f = basicStmt.getExpr().getFunctionCall();
        assertTrue(f.getArgs().getExpression(0).toString().equalsIgnoreCase("a => b"));
    }
}
