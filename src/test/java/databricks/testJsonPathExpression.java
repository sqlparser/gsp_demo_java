package databricks;

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TIndices;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testJsonPathExpression extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdatabricks);

        sqlparser.sqltext = "  SELECT c1:price\n" +
                "    FROM VALUES('{ \"price\": 5 }') AS T(c1);";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get (0);
        TExpression expression = selectSqlStatement.getResultColumnList().getResultColumn(0).getExpr();
        assertTrue(expression.getExpressionType() == EExpressionType.json_access_t);
        TExpression jsonStrExpr = expression.getLeftOperand();
        assertTrue(jsonStrExpr.toString().equalsIgnoreCase("c1"));
        assertTrue(expression.getRightOperand().getExpressionType() == EExpressionType.json_path_t);
         assertTrue(expression.getRightOperand().getJson_path().size() == 1);
         TIndices indices = expression.getRightOperand().getJson_path().get(0);
         assertTrue(indices.getAttributeName().toString().equalsIgnoreCase("price"));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdatabricks);

        sqlparser.sqltext = " SELECT c1:['price']::decimal(5,2)\n" +
                "    FROM VALUES('{ \"price\": 5 }') AS T(c1);";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get (0);
        TExpression expression = selectSqlStatement.getResultColumnList().getResultColumn(0).getExpr();

        assertTrue(expression.getExpressionType() == EExpressionType.typecast_t);
        assertTrue(expression.getTypeName().getDataType() == EDataType.decimal_t);
        expression = expression.getLeftOperand();
        TExpression jsonStrExpr = expression.getLeftOperand();
        assertTrue(jsonStrExpr.toString().equalsIgnoreCase("c1"));
        assertTrue(expression.getRightOperand().getExpressionType() == EExpressionType.json_path_t);
        assertTrue(expression.getRightOperand().getJson_path().size() == 1);
        TIndices indices = expression.getRightOperand().getJson_path().get(0);
        assertTrue(indices.getAttributeName().toString().equalsIgnoreCase("'price'"));
    }

    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdatabricks);

        sqlparser.sqltext = "SELECT c1:item[1].price::double\n" +
                "    FROM VALUES('{ \"item\": [ { \"model\" : \"basic\", \"price\" : 6.12 },\n" +
                "                             { \"model\" : \"medium\", \"price\" : 9.24 } ] }') AS T(c1);";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get (0);
        TExpression expression = selectSqlStatement.getResultColumnList().getResultColumn(0).getExpr();

        assertTrue(expression.getExpressionType() == EExpressionType.typecast_t);
        assertTrue(expression.getTypeName().getDataType() == EDataType.double_t);
        expression = expression.getLeftOperand();
        TExpression jsonStrExpr = expression.getLeftOperand();
        assertTrue(jsonStrExpr.toString().equalsIgnoreCase("c1"));
        assertTrue(expression.getRightOperand().getExpressionType() == EExpressionType.json_path_t);

        assertTrue(expression.getRightOperand().getJson_path().size() == 2);
        TIndices indices = expression.getRightOperand().getJson_path().get(0);
        assertTrue(indices.getAttributeName().toString().equalsIgnoreCase("item"));
        assertTrue(indices.getLowerSubscript().toString().equalsIgnoreCase("1"));

        indices = expression.getRightOperand().getJson_path().get(1);
        assertTrue(indices.getAttributeName().toString().equalsIgnoreCase("price"));
    }

    public void test4(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdatabricks);

        sqlparser.sqltext = "SELECT raw:store.book[*].isbn FROM store_data;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get (0);
        TExpression expression = selectSqlStatement.getResultColumnList().getResultColumn(0).getExpr();

        assertTrue(expression.getExpressionType() == EExpressionType.json_access_t);
        TExpression jsonStrExpr = expression.getLeftOperand();
        assertTrue(jsonStrExpr.toString().equalsIgnoreCase("raw"));

        TExpression jsonPathExpr = expression.getRightOperand();
        assertTrue(jsonPathExpr.getExpressionType() == EExpressionType.json_path_t);


        assertTrue(jsonPathExpr.getJson_path().size() == 3);
        TIndices indices = jsonPathExpr.getJson_path().get(0);
        assertTrue(indices.getAttributeName().toString().equalsIgnoreCase("store"));

        indices = expression.getRightOperand().getJson_path().get(1);
        assertTrue(indices.getAttributeName().toString().equalsIgnoreCase("book"));
        assertTrue(indices.getLowerSubscript().toString().equalsIgnoreCase("*"));

        indices = expression.getRightOperand().getJson_path().get(2);
        assertTrue(indices.getAttributeName().toString().equalsIgnoreCase("isbn"));

    }


    public void test5(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdatabricks);

        sqlparser.sqltext = "SELECT raw:store.basket[*],\n" +
                "         raw:store.basket[*][0] first_of_baskets,\n" +
                "         raw:store.basket[0][*] first_basket,\n" +
                "         raw:store.basket[*][*] all_elements_flattened,\n" +
                "         raw:store.basket[0][2].b subfield\n" +
                "  FROM store_data;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get (0);
        TExpression expression = selectSqlStatement.getResultColumnList().getResultColumn(1).getExpr();

        assertTrue(expression.getExpressionType() == EExpressionType.json_access_t);
        TExpression jsonStrExpr = expression.getLeftOperand();
        assertTrue(jsonStrExpr.toString().equalsIgnoreCase("raw"));

        TExpression jsonPathExpr = expression.getRightOperand();
        assertTrue(jsonPathExpr.getExpressionType() == EExpressionType.json_path_t);


        assertTrue(jsonPathExpr.getJson_path().size() == 2);
        TIndices indices = jsonPathExpr.getJson_path().get(0);
        assertTrue(indices.getAttributeName().toString().equalsIgnoreCase("store"));

        indices = expression.getRightOperand().getJson_path().get(1);
        assertTrue(indices.getAttributeName().toString().equalsIgnoreCase("basket"));
        assertTrue(indices.getLowerSubscript().toString().equalsIgnoreCase("*"));
        assertTrue(indices.getSubscriptList().size() == 1);
        assertTrue(indices.getSubscriptList().getExpression(0).toString().equalsIgnoreCase("0"));


    }


}
