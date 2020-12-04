package hive;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.nodes.hive.THiveHintClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;


public class testJoin extends TestCase {

    public void testJoin(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext ="SELECT page_views.*\n" +
                "FROM page_views JOIN dim_users\n" +
                "  ON (page_views.user_id = dim_users.id AND page_views.date >= '2008-03-01' AND page_views.date <= '2008-03-31')";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(select.getResultColumnList().size() == 1);

        assertTrue(select.joins.size() == 1);
        TJoin join = select.joins.getJoin(0);
        assertTrue(join.getKind() == TBaseType.join_source_table);
        assertTrue(join.getTable().toString().equalsIgnoreCase("page_views"));
        TJoinItem joinItem = join.getJoinItems().getJoinItem(0);
        assertTrue(joinItem.getJoinType() == EJoinType.join);
        assertTrue(joinItem.getTable().toString().equalsIgnoreCase("dim_users"));
        TExpression joinCondition = joinItem.getOnCondition();
        assertTrue(joinCondition.getExpressionType() == EExpressionType.parenthesis_t);
        joinCondition = joinCondition.getLeftOperand();
        assertTrue(joinCondition.getExpressionType() == EExpressionType.logical_and_t);
        //System.out.println(joinCondition.toString());
    }

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "SELECT a.val, b.val, c.val FROM a JOIN b ON (a.key = b.key1) JOIN c ON (c.key = b.key2);";
          assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(select.joins.size() == 1);
        TJoin join = select.joins.getJoin(0);
        assertTrue(join.getKind() == TBaseType.join_source_table);

        TTable table = join.getTable();
        assertTrue(table.getTableType() == ETableSource.objectname);
        assertTrue(table.getFullName().equalsIgnoreCase("a"));

        assertTrue(join.getJoinItems().size() == 2);
        TJoinItem joinItem = join.getJoinItems().getJoinItem(0);
        assertTrue(joinItem.getTable().toString().equalsIgnoreCase("b"));
        TJoinItem joinItem2 = join.getJoinItems().getJoinItem(1);
        assertTrue(joinItem2.getJoinType() == EJoinType.join);
        TExpression joinCondition = joinItem2.getOnCondition();
        assertTrue(joinCondition.getExpressionType() == EExpressionType.parenthesis_t);
        assertTrue(joinCondition.getLeftOperand().toString().equalsIgnoreCase("c.key = b.key2"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "select /*+MAPJOIN(smallTableTwo)*/ idOne, idTwo, value FROM\n" +
                "  ( select /*+MAPJOIN(smallTableOne)*/ idOne, idTwo, value FROM\n" +
                "    bigTable JOIN smallTableOne on (bigTable.idOne = smallTableOne.idOne)                                                   \n" +
                "  ) firstjoin                                                             \n" +
                "  JOIN                                                                  \n" +
                "  smallTableTwo on (firstjoin.idTwo = smallTableTwo.idTwo)    ;";
          assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(select.joins.size() == 1);
        TJoin join = select.joins.getJoin(0);
        assertTrue(join.getKind() == TBaseType.join_source_table);

        TTable table = join.getTable();
        assertTrue(table.getTableType() == ETableSource.subquery);
        TJoinItem joinItem = join.getJoinItems().getJoinItem(0);
        assertTrue(joinItem.getJoinType() == EJoinType.join);
        assertTrue(joinItem.getTable().getFullName().equalsIgnoreCase("smallTableTwo"));
       // assertTrue(table.getFullName().equalsIgnoreCase("a"));

        TSelectSqlStatement subquery = table.getSubquery();
        THiveHintClause hintClause = subquery.getHiveHintClause();
        assertTrue(hintClause.getHintList().getElement(0).getHintName().toString().equalsIgnoreCase("MAPJOIN"));

        join = subquery.joins.getJoin(0);
        assertTrue(join.getTable().getFullName().equalsIgnoreCase("bigTable"));
    }

    public void testLeftJoin(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext ="SELECT a.val1, a.val2, b.val, c.val\n" +
                "  FROM a\n" +
                "  JOIN b ON (a.KEY = b.KEY)\n" +
                "  LEFT OUTER JOIN c ON (a.KEY = c.KEY);";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(select.getResultColumnList().size() == 4);

        assertTrue(select.joins.size() == 1);
        TJoin join = select.joins.getJoin(0);
        assertTrue(join.getKind() == TBaseType.join_source_table);
        assertTrue(join.getTable().toString().equalsIgnoreCase("a"));
        TJoinItem joinItem = join.getJoinItems().getJoinItem(0);
        assertTrue(joinItem.getJoinType() == EJoinType.join);
        assertTrue(joinItem.getTable().toString().equalsIgnoreCase("b"));
        TExpression joinCondition = joinItem.getOnCondition();
        assertTrue(joinCondition.getExpressionType() == EExpressionType.parenthesis_t);
        assertTrue(joinCondition.toString().equalsIgnoreCase("(a.KEY = b.KEY)"));
        joinCondition = joinCondition.getLeftOperand();
        assertTrue(joinCondition.getExpressionType() == EExpressionType.simple_comparison_t);
        assertTrue(joinCondition.toString().equalsIgnoreCase("a.KEY = b.KEY"));


        joinItem = join.getJoinItems().getJoinItem(1);
        assertTrue(joinItem.getJoinType() == EJoinType.leftouter);
        assertTrue(joinItem.getTable().toString().equalsIgnoreCase("c"));
        joinCondition = joinItem.getOnCondition();
        assertTrue(joinCondition.getExpressionType() == EExpressionType.parenthesis_t);
        joinCondition = joinCondition.getLeftOperand();
        assertTrue(joinCondition.getExpressionType() == EExpressionType.simple_comparison_t);
    }

}
