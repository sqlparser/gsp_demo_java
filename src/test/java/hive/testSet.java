package hive;
/*
 * Date: 13-8-15
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;

import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.hive.THiveVariable;
import gudusoft.gsqlparser.stmt.hive.THiveSet;
import junit.framework.TestCase;

public class testSet extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "set system:xxx=5;";
        assertTrue(sqlparser.parse() == 0);

        THiveSet set = (THiveSet)sqlparser.sqlstatements.get(0);
        TExpression expression = set.getExpr();
        assertTrue(expression.getLeftOperand().toString().equalsIgnoreCase("system:xxx"));
        assertTrue(expression.getRightOperand().toString().equalsIgnoreCase("5"));

    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "set system:yyy=${system:xxx};";
        assertTrue(sqlparser.parse() == 0);

        THiveSet set = (THiveSet)sqlparser.sqlstatements.get(0);
        TExpression expression = set.getExpr();
        assertTrue(expression.getLeftOperand().toString().equalsIgnoreCase("system:yyy"));
        assertTrue(expression.getRightOperand().toString().equalsIgnoreCase("${system:xxx}"));

        TExpression right = expression.getRightOperand();
        assertTrue(right.getExpressionType() == EExpressionType.hive_variable_t);
        THiveVariable v = right.getHive_variable();
        assertTrue(v.getVarName().toString().equalsIgnoreCase("system"));
        assertTrue(v.getVarProperty().toString().equalsIgnoreCase("xxx"));
    }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "set c=${hiveconf:${hiveconf:b}};";
        assertTrue(sqlparser.parse() == 0);

        THiveSet set = (THiveSet)sqlparser.sqlstatements.get(0);
        TExpression expression = set.getExpr();
        assertTrue(expression.getLeftOperand().toString().equalsIgnoreCase("c"));
        assertTrue(expression.getRightOperand().toString().equalsIgnoreCase("${hiveconf:${hiveconf:b}}"));
        TExpression right = expression.getRightOperand();
        assertTrue(right.getExpressionType() == EExpressionType.hive_variable_t);
        THiveVariable v = right.getHive_variable();
        assertTrue(v.getVarName().toString().equalsIgnoreCase("hiveconf"));
        assertTrue(v.getVarProperty() == null);
        assertTrue(v.getNestedVar().toString().equalsIgnoreCase("${hiveconf:b}"));
        v = v.getNestedVar();
        assertTrue(v.getVarName().toString().equalsIgnoreCase("hiveconf"));
        assertTrue(v.getVarProperty().toString().equalsIgnoreCase("b"));

    }

}
