package test;


import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.nodes.*;
import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class testExpression extends TestCase {

    public void testFlattenExpr(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SELECT * from t where a>1 and b=1 or c=2";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getWhereClause().getCondition();
        assertTrue(((TExpression)expr.getFlattedAndOrExprs().get(1)).getAndOrTokenBeforeExpr().toString().equalsIgnoreCase("and"));
        assertTrue(((TExpression)expr.getFlattedAndOrExprs().get(2)).getAndOrTokenBeforeExpr().toString().equalsIgnoreCase("or")); ;

    }

    public void testCollate(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SELECT latincol COLLATE greek_ci_as";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(0);
        TExpression expr2 = column.getExpr();
        assertTrue(expr2.getRightOperand().toString().equalsIgnoreCase("greek_ci_as"));
    }

    public void testFunctionName(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT COUNT(*)";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(0);
        TExpression expr2 = column.getExpr();
        TFunctionCall func = expr2.getFunctionCall();
    }

    public void testNotEqual(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT COUNT(*)\n" +
                "FROM orders\n" +
                "WHERE A NOT = B;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TWhereClause where = select.getWhereClause();
        TExpression expr = where.getCondition();
        //System.out.println(expr.getComparisonOperator().toString());
        assertTrue(expr.getComparisonOperator().toString().equalsIgnoreCase("NOT ="));

    }

    public void testIntervalExpr(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT (end_time - start_time) DAY(4,1) TO SECOND (2)\n" +
                "FROM BillDateTime;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(0);
        TExpression expr = column.getExpr();
        //System.out.println(expr.getExpressionType());
        assertTrue(expr.getExpressionType() == EExpressionType.interval_t);

        sqlparser.sqltext = "SELECT (end_time - start_time) DAY\n" +
                "FROM BillDateTime;";
        assertTrue(sqlparser.parse() == 0);
        select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        column = select.getResultColumnList().getResultColumn(0);
        expr = column.getExpr();
        //System.out.println(expr.getExpressionType());
        assertTrue(expr.getExpressionType() == EExpressionType.interval_t );

        sqlparser.sqltext = "SELECT (end_time - start_time) DAY1\n" +
                "FROM BillDateTime;";
        assertTrue(sqlparser.parse() == 0);
        select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        column = select.getResultColumnList().getResultColumn(0);
        expr = column.getExpr();
        //System.out.println(expr.getExpressionType());
        assertTrue(expr.getExpressionType() == EExpressionType.parenthesis_t);
    }

    public void testInList(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT COUNT(*)\n" +
                "FROM orders\n" +
                "WHERE A in (1,2,3+4);";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TWhereClause where = select.getWhereClause();
        TExpression expr = where.getCondition();
        assertTrue(expr.getExpressionType() == EExpressionType.in_t);
        assertTrue(expr.getRightOperand().getExpressionType() == EExpressionType.list_t);
        TExpressionList expressionList = expr.getRightOperand().getExprList();
        assertTrue(expressionList.getExpression(0).toString().equalsIgnoreCase("1"));
        assertTrue(expressionList.getExpression(1).toString().equalsIgnoreCase("2"));
        assertTrue(expressionList.getExpression(2).toString().equalsIgnoreCase("3+4"));

    }


    public void testExprSearchColumn(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "SELECT name FROM tb1 Where length(trim(name)) = 13 and rownum = 1;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TWhereClause where = select.getWhereClause();
        TExpression expr = where.getCondition();
        TExpressionList expressionList =  expr.searchColumn("name");
       // System.out.println(expressionList.size());
    }

    public void testRemove1(){
        TGSqlParser parser = new TGSqlParser(EDbVendor.dbvoracle);
        TExpression expression = parser.parseExpression("columnA+(columnB*2)+columnC");
        TExpressionList resultList = expression.searchColumn("columnB");

        TExpression columnBExpr = resultList.getExpression(0);
        if (columnBExpr.getParentExpr() != null){
            TExpression parent  = columnBExpr.getParentExpr();
            if (columnBExpr == parent.getLeftOperand()){
                parent.setLeftOperand(null);
            }else if (columnBExpr == parent.getRightOperand()){
                parent.setRightOperand(null);
            }
        }
        assertTrue(expression.toString().equalsIgnoreCase("columnA+(2)+columnC"));

        expression = parser.parseExpression("columnA+(columnB*2)>columnC and columnD=columnE-9");
        resultList = expression.searchColumn("columnA");
        assertTrue(resultList.size() == 1);
        TExpression columnAExpr = resultList.getExpression(0);
        assertTrue(columnAExpr.getExpressionType() == EExpressionType.simple_object_name_t);
        assertTrue(columnAExpr.toString().equalsIgnoreCase("columnA"));


        if (columnAExpr.getParentExpr() != null){
            TExpression parent  = columnAExpr.getParentExpr();
            if (columnAExpr == parent.getLeftOperand()){
                parent.setLeftOperand(null);
            }else if (columnAExpr == parent.getRightOperand()){
                parent.setRightOperand(null);
            }
        }

       // System.out.println(expression.toString());
        assertTrue(expression.toString().equalsIgnoreCase("(columnB*2)>columnC and columnD=columnE-9"));


        parser.sqltext = "select *\n" +
                "from table1 pal, table2 pualr, table3 pu\n" +
                "WHERE  (pal.application_location_id = pualr.application_location_id \n" +
                "         AND pu.jbp_uid = pualr.jbp_uid \n" +
                "         AND pu.username = 'USERID')";
        int ret = parser.parse();
        assertTrue(ret == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)parser.sqlstatements.get(0);

        expression = selectSqlStatement.getWhereClause().getCondition();

        resultList = expression.searchColumn("application_location_id");
        assertTrue(resultList.size() == 2);
        TExpression expression1 = resultList.getExpression(0);
        assertTrue(expression1.getExpressionType() == EExpressionType.simple_object_name_t);
        assertTrue(expression1.toString().equalsIgnoreCase("pal.application_location_id"));

        if (expression1.getParentExpr() != null){
            TExpression parent  = expression1.getParentExpr();
            TExpression parentParent = parent.getParentExpr();
            if (parentParent != null){
                if (parent == parentParent.getLeftOperand()){
                    parentParent.setLeftOperand(null);
                }else if (parent == parentParent.getRightOperand()){
                    parentParent.setRightOperand(null);
                }
            }
        }

       // System.out.println(expression.toString());
       assertTrue(expression.toString().equalsIgnoreCase("(pu.jbp_uid = pualr.jbp_uid \n" +
               "         AND pu.username = 'USERID')"));
    }

    public void testRemoveExprList(){
        TGSqlParser parser = new TGSqlParser(EDbVendor.dbvoracle);
        parser.sqltext = "select *\n" +
                "from table1 pal, table2 pualr, table3 pu\n" +
                "WHERE  pal.application_location_id in (1,2,3,4)";
        int ret = parser.parse();
        assertTrue(ret == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)parser.sqlstatements.get(0);

        TExpression expression = selectSqlStatement.getWhereClause().getCondition();
        assertTrue(expression.getRightOperand().getExpressionType() == EExpressionType.list_t);
        TExpressionList expressionList = expression.getRightOperand().getExprList();
        expressionList.removeItem(0);
        //System.out.println(selectSqlStatement.toString());
        assertTrue(selectSqlStatement.toString().equalsIgnoreCase("select *\n" +
                "from table1 pal, table2 pualr, table3 pu\n" +
                "WHERE  pal.application_location_id in (2,3,4)"));
        expression.setRightOperand(null);
        //System.out.println(selectSqlStatement.toString());
        assertTrue(selectSqlStatement.toString().equalsIgnoreCase("select *\n" +
                "from table1 pal, table2 pualr, table3 pu\n" +
                "WHERE  pal.application_location_id"));
    }

}
