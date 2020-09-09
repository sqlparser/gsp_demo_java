package test;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;

import gudusoft.gsqlparser.stmt.TAlterTableStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import junit.framework.TestCase;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class testModifySql extends TestCase {

    private TGSqlParser parser = null;

    protected void setUp() throws Exception {
        super.setUp();
        parser = new TGSqlParser(EDbVendor.dbvoracle);
    }

    protected void tearDown() throws Exception {
        parser = null;
        super.tearDown();
    }

    /**
     * <p> column: t1.f1 -> t1.f3 as f1,
     * <p> column: t2.f2 as f2 -> t2.f3
     */
    public void test2(){
        parser.sqltext = "select t1.f1, t2.f2 as f2 from table1 t1 left join table2 t2 on t1.f1 = t2.f2 ";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        select.getResultColumnList().getResultColumn(0).setString("t1.f3 as f1");
        select.getResultColumnList().getResultColumn(1).setString("t2.f3");
        assertTrue(select.toString().equalsIgnoreCase("select t1.f3 as f1, t2.f3 from table1 t1 left join table2 t2 on t1.f1 = t2.f2 "));
        //System.out.println(select.joins.getJoin(0).toString());
    }



    /**
     * change expression in where condition:
     * t1.f2 = 2 -> t1.f2 > 2
     */
    public void test3(){
        parser.sqltext = "select t1.f1 from table1 t1 where t1.f2 = 2 ";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        select.getWhereClause().getCondition().setString("t1.f2 > 2");
        assertTrue(select.toString().equalsIgnoreCase("select t1.f1 from table1 t1 where t1.f2 > 2 "));
       // System.out.println(select.toString());
    }



    /**
     * table2 -> "(tableX join tableY using (id)) as table2"
     */

   public void test5(){
        parser.sqltext = "select table1.col1, table2.col2\n" +
                "from table1, table2\n" +
                "where table1.foo > table2.foo";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);

       TTable t ;
       for(int i=0;i<select.tables.size();i++){
         t = select.tables.getTable(i);
         if (t.toString().compareToIgnoreCase("table2") == 0){
           t.setString("(tableX join tableY using (id)) as table2");
         }
       }

    //   System.out.println(select.toString());

        assertTrue(select.toString().equalsIgnoreCase("select table1.col1, table2.col2\n" +
                "from table1, (tableX join tableY using (id)) as table2\n" +
                "where table1.foo > table2.foo"));
    }





    /**
     * <p> table2 -> "(tableX join tableY using (id)) as table3"
     * <p> table2.col2 -> table3.col2
     * <p> table2.foo -> table3.foo
     */
   public void test6(){
        parser.sqltext = "select table1.col1, table2.col2\n" +
                "from table1, table2\n" +
                "where table1.foo > table2.foo";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);

       TTable t ;
       for(int i=0;i<select.tables.size();i++){
         t = select.tables.getTable(i);
         if (t.toString().compareToIgnoreCase("table2") == 0){
             for(int j=0;j<t.getObjectNameReferences().size();j++){
                if(t.getObjectNameReferences().getObjectName(j).getObjectToken().toString().equalsIgnoreCase("table2")){
                    t.getObjectNameReferences().getObjectName(j).getObjectToken().astext = "table3";
                }
             }
           t.setString("(tableX join tableY using (id)) as table3");
         }
       }

      //System.out.println(select.toString());

        assertTrue(select.toString().equalsIgnoreCase("select table1.col1, table3.col2\n" +
                "from table1, (tableX join tableY using (id)) as table3\n" +
                "where table1.foo > table3.foo"));

    }

    public void testRemoveResultColumn(){
        parser.sqltext = "SELECT A as A_Alias, B AS B_Alias FROM TABLE_X";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        TResultColumnList columns = select.getResultColumnList();

        if (TParseTreeNode.doubleLinkedTokenListToString){
            columns.removeResultColumn(1);
            //System.out.println(select.toString());
            assertTrue(select.toString().equalsIgnoreCase("SELECT A as A_Alias  FROM TABLE_X"));
            TResultColumn resultColumn = new TResultColumn();
            resultColumn.setString("x");
            columns.addResultColumn(resultColumn);
            assertTrue(select.toString().equalsIgnoreCase("SELECT A as A_Alias,x  FROM TABLE_X"));

        }else{
            columns.removeResultColumn(1);
            //System.out.println(select.toString());
            assertTrue(select.toString().equalsIgnoreCase("SELECT A as A_Alias FROM TABLE_X"));

            columns.addResultColumn("x");
            assertTrue(select.toString().equalsIgnoreCase("SELECT A as A_Alias,x FROM TABLE_X"));
        }

    }



    public void testAddResultColumn(){
        parser.sqltext = "SELECT A as A_Alias, B AS B_Alias FROM TABLE_X";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        TResultColumnList columns = select.getResultColumnList();
        if (TParseTreeNode.doubleLinkedTokenListToString){
            TResultColumn resultColumn = new TResultColumn();
            resultColumn.setString("d as d_alias");
            columns.addResultColumn(resultColumn);
        }else{
            columns.addResultColumn("d as d_alias");
        }
        assertTrue(select.toString().equalsIgnoreCase("SELECT A as A_Alias, B AS B_Alias,d as d_alias FROM TABLE_X"));
    }



    public void testRemoveAndAddResultColumn(){
        parser.sqltext = "SELECT A.COLUMN1, B.COLUMN2 from TABLE1 A, TABLE2 B where A.COLUMN1=B.COLUMN1";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        TResultColumnList columns = select.getResultColumnList();
        columns.removeResultColumn(1);
        columns.getResultColumn(0).setString("B.COLUMN3");
        if (TParseTreeNode.doubleLinkedTokenListToString){
            assertTrue(select.toString().equalsIgnoreCase("SELECT B.COLUMN3  from TABLE1 A, TABLE2 B where A.COLUMN1=B.COLUMN1"));

        }else{
            assertTrue(select.toString().equalsIgnoreCase("SELECT B.COLUMN3 from TABLE1 A, TABLE2 B where A.COLUMN1=B.COLUMN1"));
        }
       // System.out.print(select.toString());
    }

    public void testReplaceColumn(){
        parser.sqltext = "SELECT * FROM TABLE_X";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        TResultColumnList columns = select.getResultColumnList();
        if (columns.getResultColumn(0).toString().equalsIgnoreCase("*")){
            columns.getResultColumn(0).setString("TABLE_X.*");
        }
        //System.out.println(select.toString());
        assertTrue(select.toString().equalsIgnoreCase("SELECT TABLE_X.* FROM TABLE_X"));
    }

    public void testRemoveTable1(){
        parser.sqltext = "SELECT * FROM t1,t2 where t1.f1=t2.f2";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        TJoinList joinList = select.joins;
        // let's remove t1 and where clause

        joinList.removeJoin(0);
        if(TParseTreeNode.doubleLinkedTokenListToString){
            select.setWhereClause(null);
        }else{
            select.getWhereClause().setString(" ");
        }


        assertTrue(select.toString().trim().equalsIgnoreCase("SELECT * FROM t2"));
    }



    public void testRemoveTable2(){
        parser.sqltext = "SELECT * FROM t1,t2 where t1.f1=t2.f2";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        TJoinList joinList = select.joins;
        // let's remove t2 and where clause

        joinList.removeJoin(1);
        if (TParseTreeNode.doubleLinkedTokenListToString){
            select.setWhereClause(null);
        }else{
            select.getWhereClause().setString(" ");
        }


        assertTrue(select.toString().trim().equalsIgnoreCase("SELECT * FROM t1"));
    }

    // SELECT * FROM t1,t2 where t1.f1=t2.f2
    // covert to
    // SELECT * FROM t1 left join t2 on t1.f1=t2.f2
    // this includes following steps
    // 1. remove t2
    // 2. replace t1 with t1 left join t2 on t1.f1=t2.f2
    // 3. remove where clause

    // for a detailed demo about how to  Rewrite Oracle propriety joins to ANSI SQL compliant joins.
    // http://www.dpriver.com/blog/list-of-demos-illustrate-how-to-use-general-sql-parser/rewrite-oracle-propriety-joins-to-ansi-sql-compliant-joins/

    // Rewrite SQL Server proprietary joins to ANSI SQL compliant joins.
    // http://www.dpriver.com/blog/list-of-demos-illustrate-how-to-use-general-sql-parser/rewrite-sql-server-propriety-joins-to-ansi-sql-compliant-joins/

    public void testRemoveTable3(){
        parser.sqltext = "SELECT * FROM t1,t2 where t1.f1=t2.f2";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        TJoinList joinList = select.joins;
        // let's remove t2 and where clause
        joinList.removeJoin(1);

        //replace t1 with t1 left join,t2 on t1.f1=t2.f2

        joinList.getJoin(0).setString("t1 left join t2 on t1.f1=t2.f2");
        // remove where clause
        if (TParseTreeNode.doubleLinkedTokenListToString){
            select.setWhereClause(null);
        }else{
            select.getWhereClause().setString(" ");
        }


       // System.out.println(select.toString());
        assertTrue(select.toString().trim().equalsIgnoreCase("SELECT * FROM t1 left join t2 on t1.f1=t2.f2"));
    }





    public void testRemoveCTE(){
        parser.sqltext = "with test as (select id from emp)\n" +
                "select * from test";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        select.getStartToken().setString(""); // remove WITH token
        TSourceToken startToken = select.getCteList().getStartToken();
        TSourceToken endToken = select.getCteList().getEndToken();
        for(int i=startToken.posinlist;i<=endToken.posinlist ;i++){
            startToken.container.get(i).setString(""); // remove all token of cte
        }
      //   System.out.println(select.toString());
        assertTrue(select.toString().trim().equalsIgnoreCase("select * from test"));
    }



    public void testAddWhereClause(){
        parser.sqltext = "SELECT * FROM TABLE_X where f > 0";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        select.addWhereClause("c>1");

       // System.out.println(select.toString());
        assertTrue(select.toString().equalsIgnoreCase("SELECT * FROM TABLE_X where (f > 0) and c>1"));
    }



    public void testAddWhereClauseOR(){
        parser.sqltext = "SELECT * FROM TABLE_X where f > 0";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        select.addWhereClauseOR("c>1");

       // System.out.println(select.toString());
        assertTrue(select.toString().equalsIgnoreCase("SELECT * FROM TABLE_X where (f > 0) or c>1"));
    }



    public void testAddWhereClause1(){
        parser.sqltext = "select count(*) from TableName where NOT a OR NOT b";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        select.addCondition("c=1");
        assertTrue(select.toString().equalsIgnoreCase("select count(*) from TableName where (NOT a OR NOT b) and c=1"));
    }



    public void testAddWhereClause2(){
        parser.sqltext = "SELECT * FROM TABLE_X";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        if (TParseTreeNode.doubleLinkedTokenListToString){
            TWhereClause whereClause = new TWhereClause("where c>1");
            //whereClause.setText("where c>1");
            select.setAnchorNode(select.joins);
            select.setWhereClause(whereClause);
        }else{
            select.addWhereClause("c>1");
        }


       System.out.println(select.toString());
        assertTrue(select.toString().equalsIgnoreCase("SELECT * FROM TABLE_X where c>1"));
    }



    public void testAddWhereClause3(){
        parser.sqltext = "SELECT * FROM TABLE_X group by a";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        if (TParseTreeNode.doubleLinkedTokenListToString){
            TWhereClause whereClause = new TWhereClause("where c>1");
            //whereClause.setText("where c>1");
            select.setAnchorNode(select.joins);
            select.setWhereClause(whereClause);
        }else{
            select.addWhereClause("c>1");
        }

        //System.out.println(select.toString());
        assertTrue(select.toString().equalsIgnoreCase("SELECT * FROM TABLE_X where c>1 group by a"));
    }




//
    public void testAddConditionAfterJoin(){
        parser.sqltext = "SELECT tableA.itemA1, tableB.itemB1\n"
                + " FROM tableA\n"
                + " INNER JOIN tableB\n"
                + " ON tableB.itemB2 = tableA.itemA2\n"
                + " INNER JOIN (\n"
                + "   SELECT tableC.itemC1\n"
                + "   FROM tableC\n"
                + "   WHERE tableC.itemC3='ABC'\n"
                + "   GROUP BY tableC.itemC1\n"
                + ") unNamedJoin\n"
                + " ON unNamedJoin.itemC1 = tableB.itemB2\n";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);

        TJoinList joinList = select.joins;
        TJoinItem item0 = joinList.getJoin( 0 ).getJoinItems( ).getJoinItem( 0 );
        if (TParseTreeNode.doubleLinkedTokenListToString){
            item0.getOnCondition().setString("("+item0.getOnCondition()+")"+" and 1=1");
            TWhereClause whereClause = new TWhereClause("where c>1");
            //whereClause.setText("where c>1");
            select.setAnchorNode(select.joins);
            select.setWhereClause(whereClause);
            //select.setWhereClause(whereClause);

        }else{
            item0.getOnCondition( ).addANDCondition( "1=1" );
            select.addWhereClause("c>1");
        }

       // System.out.println(select.toString().trim());
        assertTrue(select.toString().trim().equalsIgnoreCase("SELECT tableA.itemA1, tableB.itemB1\n" +
                " FROM tableA\n" +
                " INNER JOIN tableB\n" +
                " ON (tableB.itemB2 = tableA.itemA2) and 1=1\n" +
                " INNER JOIN (\n" +
                "   SELECT tableC.itemC1\n" +
                "   FROM tableC\n" +
                "   WHERE tableC.itemC3='ABC'\n" +
                "   GROUP BY tableC.itemC1\n" +
                ") unNamedJoin\n" +
                " ON unNamedJoin.itemC1 = tableB.itemB2 where c>1"));

    }




    public void testRemoveWhereClause(){
        parser.sqltext = "SELECT * FROM TABLE_X where a>1 order by a";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        if (TParseTreeNode.doubleLinkedTokenListToString){
            select.setWhereClause(null);
        }else {
            select.getWhereClause().setString(" ");
        }

       // System.out.println(select.toString());
        assertTrue(select.toString().equalsIgnoreCase("SELECT * FROM TABLE_X  order by a"));
    }



    public void testAddNewOrderBy(){
        parser.sqltext = "SELECT * FROM TABLE_X";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        if (TParseTreeNode.doubleLinkedTokenListToString){
            TOrderBy orderBy = new TOrderBy("order by a desc");
            //orderBy.setText("order by a desc");
            select.setOrderbyClause(orderBy);
        }else{
            select.addOrderBy("a desc");
        }

        assertTrue(select.toString().equalsIgnoreCase("SELECT * FROM TABLE_X order by a desc"));

        parser.sqltext = "SELECT * FROM TABLE_X where a>1";
        assertTrue(parser.parse() == 0);
        select = (TSelectSqlStatement)parser.sqlstatements.get(0);

        if (TParseTreeNode.doubleLinkedTokenListToString){
            TOrderBy orderBy = new TOrderBy("order by a desc");
            //orderBy.setText("order by a desc");
            select.setOrderbyClause(orderBy);
        }else{
            select.addOrderBy("a desc");
        }
        assertTrue(select.toString().equalsIgnoreCase("SELECT * FROM TABLE_X where a>1 order by a desc"));

        parser.sqltext = "SELECT * FROM TABLE_X where a>1 group by a having count(*) > 1";
        assertTrue(parser.parse() == 0);
        select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        if (TParseTreeNode.doubleLinkedTokenListToString){
            TOrderBy orderBy = new TOrderBy("order by a asc");
            //orderBy.setText("order by a asc");
            select.setOrderbyClause(orderBy);
        }else{
            select.addOrderBy("a asc");
        }

        assertTrue(select.toString().equalsIgnoreCase("SELECT * FROM TABLE_X where a>1 group by a having count(*) > 1 order by a asc"));

        parser.sqltext = "SELECT * FROM TABLE_X where a>1 group by a having count(*) > 1 order by c desc";
        assertTrue(parser.parse() == 0);
        select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        if (TParseTreeNode.doubleLinkedTokenListToString){
            TOrderByItem orderByItem = new TOrderByItem();
            orderByItem.setText("a asc");
            select.getOrderbyClause().getItems().addOrderByItem(orderByItem);
        }else{
            select.addOrderBy("a asc");
        }

        assertTrue(select.toString().equalsIgnoreCase("SELECT * FROM TABLE_X where a>1 group by a having count(*) > 1 order by c desc,a asc"));

        parser.sqltext = "SELECT * FROM TABLE_X";
        assertTrue(parser.parse() == 0);
        select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        if (TParseTreeNode.doubleLinkedTokenListToString){
            TWhereClause whereClause = new TWhereClause("where a>1 and b>2");
            //whereClause.setText("where a>1 and b>2");
            select.setWhereClause(whereClause);
            TOrderBy orderBy = new TOrderBy("order by a desc");
            //orderBy.setText("order by a desc");
            select.setOrderbyClause(orderBy);

        }else{
            select.addWhereClause("a>1 and b>2") ;
            select.addOrderBy("a desc");
        }
        assertTrue(select.toString().equalsIgnoreCase("SELECT * FROM TABLE_X where a>1 and b>2 order by a desc"));

       // System.out.println(select.toString());
    }



    public void testAddOrderBy(){
        parser.sqltext = "SELECT * FROM TABLE_X order by a";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        if(TParseTreeNode.doubleLinkedTokenListToString){
            TOrderByItem orderByItem = new TOrderByItem();
            orderByItem.setText("b");
            select.getOrderbyClause().getItems().addOrderByItem(orderByItem);
        }else{
            select.getOrderbyClause().addOrderByItem("b");
        }


        //System.out.println(select.toString());
        assertTrue(select.toString().equalsIgnoreCase("SELECT * FROM TABLE_X order by a,b"));
    }

    public void testRemoveOrderBy(){
        parser.sqltext = "SELECT * FROM TABLE_X order by a,b";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        if (TParseTreeNode.doubleLinkedTokenListToString){
            select.getOrderbyClause().getItems().removeItem(1);
            assertTrue(select.toString().equalsIgnoreCase("SELECT * FROM TABLE_X order by a"));

            select.getOrderbyClause().getItems().removeItem(0);
            select.setOrderbyClause(null);
            assertTrue(select.toString().trim().equalsIgnoreCase("SELECT * FROM TABLE_X"));

        }
    }


    public void testReplaceOrderBy1(){
        parser.sqltext = "SELECT * FROM TABLE_X order by a";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        select.getOrderbyClause().getItems().getOrderByItem(0).setString("b asc,c desc");
        //select.getOrderbyClause().addOrderByItem("c");

        //System.out.println(select.toString());
        assertTrue(select.toString().equalsIgnoreCase("SELECT * FROM TABLE_X order by b asc,c desc"));
    }


    public void testReplaceOrderBy2(){
        parser.sqltext = "SELECT * FROM TABLE_X order by a";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        select.getOrderbyClause().getItems().getOrderByItem(0).setString("b asc");
        if(TParseTreeNode.doubleLinkedTokenListToString){
            TOrderByItem orderByItem1 = new TOrderByItem();
            orderByItem1.setText("c desc");
            select.getOrderbyClause().getItems().addOrderByItem(orderByItem1);

            TOrderByItem orderByItem2 = new TOrderByItem();
            orderByItem2.setText("d desc");
            select.getOrderbyClause().getItems().addOrderByItem(orderByItem2);
        }else{
            select.getOrderbyClause().addOrderByItem("c desc");
            select.getOrderbyClause().addOrderByItem("d desc");
        }

       // System.out.println(select.toString());
        assertTrue(select.toString().equalsIgnoreCase("SELECT * FROM TABLE_X order by b asc,c desc,d desc"));
    }

    public void testRemoveSetClauseInUpdate(){
        parser.sqltext = "UPDATE BLA SET A=2, B=3 WHERE X=5";
        assertTrue(parser.parse() == 0);

        TUpdateSqlStatement updateSqlStatement = (TUpdateSqlStatement)parser.sqlstatements.get(0);
        TResultColumnList setClauses = updateSqlStatement.getResultColumnList();
        setClauses.removeResultColumn(1); // the second set expression
        if (TParseTreeNode.doubleLinkedTokenListToString){
            assertTrue(updateSqlStatement.toString().equalsIgnoreCase("UPDATE BLA SET A=2  WHERE X=5"));

        }else{
            assertTrue(updateSqlStatement.toString().equalsIgnoreCase("UPDATE BLA SET A=2 WHERE X=5"));
        }
    }



    public void testModifyJoin(){
        parser.sqltext = "select * from t1 inner join t2 on t1.col1 = t2.col2";
        assertTrue(parser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)parser.sqlstatements.get(0);
        selectSqlStatement.joins.getJoin(0).setString("t2 left join t1 on t1.col3 = t2.col5");
        assertTrue(selectSqlStatement.toString().equalsIgnoreCase("select * from t2 left join t1 on t1.col3 = t2.col5"));
    }


    public void testModifyTable(){
        parser.sqltext = "select * from t1";
        assertTrue(parser.parse() == 0);

        TTable table = parser.sqlstatements.get(0).tables.getTable(0);
        table.setString("newt");
        assertTrue(parser.sqlstatements.get(0).toString().equalsIgnoreCase("select * from newt"));
    }

    public void testModifyTable2(){
        parser.sqltext = "SELECT * from employee e";
        assertTrue(parser.parse() == 0);

        TTable table = parser.sqlstatements.get(0).tables.getTable(0);
        table.getTableName().setString("new_employee");
        assertTrue(parser.sqlstatements.get(0).toString().equalsIgnoreCase("SELECT * from new_employee e"));
        table.getAliasClause().setString("new_alias");
        assertTrue(parser.sqlstatements.get(0).toString().equalsIgnoreCase("SELECT * from new_employee new_alias"));
    }

    public void testAddTableAlias(){
        parser.sqltext = "select * from t1";
        assertTrue(parser.parse() == 0);

        TTable table = parser.sqlstatements.get(0).tables.getTable(0);
        table.setString(table.toString()+ " AS foo ");
        assertTrue(parser.sqlstatements.get(0).toString().trim().equalsIgnoreCase("select * from t1 AS foo"));
        //System.out.print(parser.sqlstatements.get(0).toString());
    }



    public void testModifyTableInCreate(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvimpala);
        sqlparser.sqltext = "create table if not exists campaign_1 ( id int, name string )";
        int ret = sqlparser.parse();
        TCustomSqlStatement stmt = sqlparser.sqlstatements.get(0);
        TTable table = stmt.tables.getTable(0);
        table.setString("prefix_." + table.toString() + " " + table.getAliasName());;
        assertTrue(table.toString().trim().equalsIgnoreCase("prefix_.campaign_1"));
    }



    public void testRemoveHavingClause(){
        parser.sqltext = "SELECT\n" +
                "c.ID AS \"SMS.ID\"\n" +
                "FROM\n" +
                "SUMMIT.cntrb_detail c\n" +
                "where\n" +
                "c.cntrb_date >='$GivingFromDate$'\n" +
                "and c.cntrb_date<='$GivingThruDate$'\n" +
                "group by c.id\n" +
                "having sum(c.amt) >= '$GivingFromAmount$' and sum(c.amt) <= '$GivingThruAmount$'";
        assertTrue(parser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)parser.sqlstatements.get(0);
        TGroupBy groupBy = selectSqlStatement.getGroupByClause();
        TExpression having = groupBy.getHavingClause();
        having.setString(" ");
        groupBy.getHAVING().setString(" ");
        assertTrue(selectSqlStatement.toString().trim().equalsIgnoreCase("SELECT\n" +
                "c.ID AS \"SMS.ID\"\n" +
                "FROM\n" +
                "SUMMIT.cntrb_detail c\n" +
                "where\n" +
                "c.cntrb_date >='$GivingFromDate$'\n" +
                "and c.cntrb_date<='$GivingThruDate$'\n" +
                "group by c.id"));
        //System.out.println(selectSqlStatement.toString());
    }




    public void testAlterTable(){
        TGSqlParser lcparser = new TGSqlParser(EDbVendor.dbvoracle);
        lcparser.sqltext = "ALTER TABLE P_CAP \n" +
                "ADD CONSTRAINT FK_P_CAP_R_PH_111_P_CEL \n" +
                "FOREIGN KEY (CAP_CEL) REFERENCES P_CEL (CEL_COD);";
        assertTrue(lcparser.parse() == 0);
        TAlterTableStatement at = (TAlterTableStatement) lcparser.sqlstatements.get(0);
        TAlterTableOption alterTableOption = at.getAlterTableOptionList().getAlterTableOption(0);
        TConstraint constraint =  alterTableOption.getConstraintList().getConstraint(0);
        assertTrue(constraint.getConstraint_type() == EConstraintType.foreign_key);
        TObjectName refColumn = constraint.getReferencedColumnList().getObjectName(0);
        refColumn.setString(refColumn.toString()+",CEL_NEWID");
        //System.out.println(at.toString());
        assertTrue(at.toString().equalsIgnoreCase("ALTER TABLE P_CAP \n" +
                "ADD CONSTRAINT FK_P_CAP_R_PH_111_P_CEL \n" +
                "FOREIGN KEY (CAP_CEL) REFERENCES P_CEL (CEL_COD,CEL_NEWID);"));
    }




    public void testAlterTable2(){
        TGSqlParser lcparser = new TGSqlParser(EDbVendor.dbvoracle);
        lcparser.sqltext = "ALTER TABLE P_CAP \n" +
                "ADD CONSTRAINT FK_P_CAP_R_PH_111_P_CEL \n" +
                "FOREIGN KEY (CAP_CEL) REFERENCES P_CEL (CEL_COD);";
        assertTrue(lcparser.parse() == 0);
        TAlterTableStatement at = (TAlterTableStatement) lcparser.sqlstatements.get(0);
        TAlterTableOption alterTableOption = at.getAlterTableOptionList().getAlterTableOption(0);
        TConstraint constraint =  alterTableOption.getConstraintList().getConstraint(0);
        assertTrue(constraint.getConstraint_type() == EConstraintType.foreign_key);
        TObjectName refColumn = constraint.getReferencedColumnList().getObjectName(0);

        if(TParseTreeNode.doubleLinkedTokenListToString){
            TObjectName columnName0 = new TObjectName();
            columnName0.setText("CEL_NEWID");
            constraint.getReferencedColumnList().insertElementAt(columnName0,0);
        }else{
            TDummy dummy  = new TDummy();
            dummy.setGsqlparser(lcparser);
            dummy.setString("CEL_NEWID,");
            dummy.addAllMyTokensToTokenList(refColumn.getStartToken().container,refColumn.getStartToken().posinlist );
        }

       // System.out.println(at.toString());
        assertTrue(at.toString().equalsIgnoreCase("ALTER TABLE P_CAP \n" +
                "ADD CONSTRAINT FK_P_CAP_R_PH_111_P_CEL \n" +
                "FOREIGN KEY (CAP_CEL) REFERENCES P_CEL (CEL_NEWID,CEL_COD);"));
    }



    public void testAddWhere(){
        TGSqlParser lcparser = new TGSqlParser(EDbVendor.dbvoracle);
        lcparser.sqltext = "SELECT * FROM TABLE_X";
        assertTrue(lcparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)lcparser.sqlstatements.get(0);

        if (TParseTreeNode.doubleLinkedTokenListToString){
            TWhereClause whereClause = new TWhereClause("where f > 0");
            // whereClause.setText("where f > 0");
            select.setAnchorNode(select.joins);
            select.setWhereClause(whereClause);
           // System.out.println(select.toString());

        }else{
            TSourceToken prevToken = select.joins.getEndToken();

            TDummy dummy  = new TDummy();
            dummy.setGsqlparser(lcparser);
            dummy.setString(" where f > 0");
            dummy.addAllMyTokensToTokenList(prevToken.container,prevToken.posinlist + 1 );

            for(int i=0;i<prevToken.getNodesEndWithThisToken().size();i++){
                TParseTreeNode node = prevToken.getNodesEndWithThisToken().get(i);
                if (!((node instanceof TJoinList)
                        ||(node instanceof TJoin)
                        ||(node instanceof TJoinItemList)
                        ||(node instanceof TJoinItem)
                ))
                {
                    // change all end token of parse tree node except the table from join clause
                    node.setEndToken(dummy.getEndToken());
                }
            }
        }

        assertTrue(select.toString().equalsIgnoreCase("SELECT * FROM TABLE_X where f > 0"));


    }


    public void testRemoveParenthesis(){
        parser.sqltext = "select * from ((select * from some_table where some_column < ?)) some_view where a_column = something";
        assertTrue(parser.parse() == 0);
        removeDuplicatedParenthesis(parser.sourcetokenlist);
        assertTrue(parser.sqlstatements.get(0).toString().equalsIgnoreCase("select * from ( select * from some_table where some_column < ? ) some_view where a_column = something"));

        parser.sqltext = "select * from ((select a from b) union (select a from d))";
        assertTrue(parser.parse() == 0);
        removeDuplicatedParenthesis(parser.sourcetokenlist);
        assertTrue(parser.sqlstatements.get(0).toString().equalsIgnoreCase("select * from ((select a from b) union (select a from d))"));

        parser.sqltext = "((select a from b) order by a)";
        assertTrue(parser.parse() == 0);
        removeDuplicatedParenthesis(parser.sourcetokenlist);
        assertTrue(parser.sqlstatements.get(0).toString().equalsIgnoreCase("((select a from b) order by a)"));

        parser.sqltext = "(((select a from b)) order by a)";
        assertTrue(parser.parse() == 0);
        removeDuplicatedParenthesis(parser.sourcetokenlist);
        assertTrue(parser.sqlstatements.get(0).toString().equalsIgnoreCase("(( select a from b ) order by a)"));

        parser.sqltext = "((((select a from b)) order by a))";
        assertTrue(parser.parse() == 0);
        removeDuplicatedParenthesis(parser.sourcetokenlist);
        assertTrue(parser.sqlstatements.get(0).toString().equalsIgnoreCase("( ( select a from b ) order by a )"));

        parser.sqltext = "select * from user_table where ((username like '%admin%'));";
        assertTrue(parser.parse() == 0);
        removeDuplicatedParenthesis(parser.sourcetokenlist);
        assertTrue(parser.sqlstatements.get(0).toString().equalsIgnoreCase("select * from user_table where ( username like '%admin%' );"));

    }



    private int removeDuplicatedParenthesis(TSourceTokenList sourceTokenList){
        int cnt = 0;
        TSourceToken st = null, prevEndToken = null;
        boolean inParenthesis = false;
        for(int i=0;i<sourceTokenList.size();i++){
            st = sourceTokenList.get(i);
            if (st.isnonsolidtoken()) continue;
            if ((st.tokencode == '(')&&(st.getLinkToken() != null)){
                if (inParenthesis){
                    if (st.getLinkToken() == prevEndToken.prevSolidToken()){
                        //this is duplicated token, remove this token
                        st.setString(" ");
                        st.getLinkToken().setString(" ");
                        cnt++;
                    }
                    prevEndToken = st.getLinkToken();
                }else {
                    inParenthesis = true;
                    prevEndToken = st.getLinkToken();
                }
            }else {
                inParenthesis = false;
                prevEndToken = null;
            }
        }
        return cnt;
    }

    protected static void iterateStmt(TCustomSqlStatement stmt){
        //System.out.println(stmt.sqlstatementtype.toString());
        if (stmt.sqlstatementtype == ESqlStatementType.sstselect){
            int parenthesisNum  = 0 ;
            if (stmt.getEndToken().tokencode == ')'){
                parenthesisNum++;
                TSourceToken st = stmt.getEndToken();
                TSourceToken pst = st.prevSolidToken();

                while (pst != null){
                        if (pst.tokencode == ')'){
                            parenthesisNum++;
                            pst = pst.prevSolidToken();
                        }else{
                            break;
                        }
                }
            }

            if (parenthesisNum > 1){
                TSourceToken beginSt = stmt.getStartToken();
                TSourceToken endSt = stmt.getEndToken();
                int i = parenthesisNum - 1;
                while ((i>0)&&(beginSt!=null)&&(endSt != null)){
                    beginSt.setString(" ");
                    beginSt = beginSt.nextSolidToken();
                    endSt.setString(" ");
                    endSt = endSt.prevSolidToken();
                    i--;
                }
            }

        }
        for (int i=0;i<stmt.getStatements().size();i++){
           iterateStmt(stmt.getStatements().get(i));
        }
    }

}
