package postgresql;


import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testJSON extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select '[{\"a\":\"foo\"},{\"b\":\"bar\"},{\"c\":\"baz\"}]'::json->2;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select  =  (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getResultColumnList().getResultColumn(0).getExpr();
        assertTrue(expr.getExpressionType() == EExpressionType.json_get_object);
        TExpression lexpr = expr.getLeftOperand();
        assertTrue(lexpr.getExpressionType() == EExpressionType.typecast_t);
        assertTrue(lexpr.getTypeName().getDataType() == EDataType.json_t);
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select '[1,2,3]'::json->>2;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select  =  (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getResultColumnList().getResultColumn(0).getExpr();
        assertTrue(expr.getExpressionType() == EExpressionType.json_get_text);
        TExpression lexpr = expr.getLeftOperand();
        assertTrue(lexpr.getExpressionType() == EExpressionType.typecast_t);
        assertTrue(lexpr.getTypeName().getDataType() == EDataType.json_t);
    }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select '{\"a\": {\"b\":{\"c\": \"foo\"}}}'::json#>'{a,b}';";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select  =  (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getResultColumnList().getResultColumn(0).getExpr();
        assertTrue(expr.getExpressionType() == EExpressionType.json_get_object_at_path);
        TExpression lexpr = expr.getLeftOperand();
        assertTrue(lexpr.getExpressionType() == EExpressionType.typecast_t);
        assertTrue(lexpr.getTypeName().getDataType() == EDataType.json_t);
    }

    public void test4(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select '{\"a\":[1,2,3],\"b\":[4,5,6]}'::json#>>'{a,2}';";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select  =  (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getResultColumnList().getResultColumn(0).getExpr();
        assertTrue(expr.getExpressionType() == EExpressionType.json_get_text_at_path);
        TExpression lexpr = expr.getLeftOperand();
        assertTrue(lexpr.getExpressionType() == EExpressionType.typecast_t);
        assertTrue(lexpr.getTypeName().getDataType() == EDataType.json_t);
    }

    public void test_left_contain(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select '{\"a\":1, \"b\":2}'::jsonb @> '{\"b\":2}'::jsonb;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select  =  (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getResultColumnList().getResultColumn(0).getExpr();
        //System.out.println(expr.getExpressionType().toString());
        assertTrue(expr.getExpressionType() == EExpressionType.json_left_contain);
        TExpression lexpr = expr.getLeftOperand();
        assertTrue(lexpr.getExpressionType() == EExpressionType.typecast_t);
        assertTrue(lexpr.getTypeName().getDataType() == EDataType.jsonb_t);
    }

    public void test_right_contain(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select '{\"b\":2}'::jsonb <@ '{\"a\":1, \"b\":2}'::jsonb;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select  =  (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getResultColumnList().getResultColumn(0).getExpr();
        //System.out.println(expr.getExpressionType().toString());
        assertTrue(expr.getExpressionType() == EExpressionType.json_right_contain);
        TExpression lexpr = expr.getLeftOperand();
        assertTrue(lexpr.getExpressionType() == EExpressionType.typecast_t);
        assertTrue(lexpr.getTypeName().getDataType() == EDataType.jsonb_t);
    }

    public void test_exist(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select '{\"a\":1, \"b\":2}'::jsonb ? 'b';";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select  =  (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getResultColumnList().getResultColumn(0).getExpr();
        //System.out.println(expr.getExpressionType().toString());
        assertTrue(expr.getExpressionType() == EExpressionType.json_exist);
        TExpression lexpr = expr.getLeftOperand();
        assertTrue(lexpr.getExpressionType() == EExpressionType.typecast_t);
        assertTrue(lexpr.getTypeName().getDataType() == EDataType.jsonb_t);
    }

    public void test_exist_any(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select '{\"a\":1, \"b\":2, \"c\":3}'::jsonb ?| array['b', 'c'];";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select  =  (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getResultColumnList().getResultColumn(0).getExpr();
        //System.out.println(expr.getExpressionType().toString());
        assertTrue(expr.getExpressionType() == EExpressionType.json_any_exist);
        TExpression lexpr = expr.getLeftOperand();
        assertTrue(lexpr.getExpressionType() == EExpressionType.typecast_t);
        assertTrue(lexpr.getTypeName().getDataType() == EDataType.jsonb_t);
    }

    public void test_exist_all(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select '[\"a\", \"b\"]'::jsonb ?& array['a', 'b'];";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select  =  (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getResultColumnList().getResultColumn(0).getExpr();
        //System.out.println(expr.getExpressionType().toString());
        assertTrue(expr.getExpressionType() == EExpressionType.json_all_exist);
        TExpression lexpr = expr.getLeftOperand();
        assertTrue(lexpr.getExpressionType() == EExpressionType.typecast_t);
        assertTrue(lexpr.getTypeName().getDataType() == EDataType.jsonb_t);
    }
}
