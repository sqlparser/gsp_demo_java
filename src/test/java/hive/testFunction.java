package hive;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: tako
 * Date: 13-8-11
 * Time: 下午5:59
 * To change this template use File | Settings | File Templates.
 */
public class testFunction extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "SELECT create_union(0, key), " +
                "create_union(if(key<100, 0, 1), 2.0, value), " +
                "create_union(1, \"a\", struct(2, \"b\")) FROM src LIMIT 2;";
          assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr0 = select.getResultColumnList().getResultColumn(0).getExpr();
        TExpression expr1 = select.getResultColumnList().getResultColumn(1).getExpr();
        TExpression expr2 = select.getResultColumnList().getResultColumn(2).getExpr();

        assertTrue(expr0.getExpressionType() == EExpressionType.function_t);
        assertTrue(expr1.getFunctionCall().getFunctionName().toString().equalsIgnoreCase("create_union"));

        TExpression arg = expr1.getFunctionCall().getArgs().getExpression(0);
        assertTrue(arg.toString().equalsIgnoreCase("if(key<100, 0, 1)"));
        TFunctionCall iff = arg.getFunctionCall();
        assertTrue(iff.getFunctionName().toString().equalsIgnoreCase("if"));
        assertTrue(iff.getArgs().size() == 3);
        TExpression argofarg = iff.getArgs().getExpression(0);
        assertTrue(argofarg.getExpressionType() == EExpressionType.simple_comparison_t);


    }

    public void testCast(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "select cast(t as boolean) from decimal_2;";
          assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr0 = select.getResultColumnList().getResultColumn(0).getExpr();
        TFunctionCall f = expr0.getFunctionCall();
        assertTrue(f.getFunctionType() == EFunctionType.cast_t );
        assertTrue(f.getExpr1().toString().equalsIgnoreCase("t"));
        assertTrue(f.getTypename().getDataType() == EDataType.boolean_t);
    }

}
