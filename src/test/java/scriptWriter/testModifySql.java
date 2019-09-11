package test.scriptWriter;


import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.scriptWriter.TScriptGenerator;
import gudusoft.gsqlparser.stmt.TAlterTableStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import junit.framework.TestCase;

public class testModifySql extends TestCase {

    private TGSqlParser parser = null;
    private TScriptGenerator scriptGenerator = null;

    protected void setUp() throws Exception {
        super.setUp();
        parser = new TGSqlParser(EDbVendor.dbvoracle);
    }

    protected void tearDown() throws Exception {
        parser = null;
        super.tearDown();
    }


    public void testSimpleConverter(){
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlParser.sqltext = "SELECT * FROM t1,t2 where t1.f1=t2.f2";
        assertTrue(sqlParser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlParser.sqlstatements.get(0);
        TJoinList joinList = select.joins;
        // remove table: t2
        joinList.removeJoin(1);
        // remove where clause
        select.setWhereClause(null);
        // add join condition
        TJoinItem joinItem = new TJoinItem();
        joinList.getJoin(0).getJoinItems().addJoinItem(joinItem);
        joinItem.setJoinType(EJoinType.left);
        TTable joinTable = new TTable();
        joinItem.setTable(joinTable);
        joinTable.setTableName(sqlParser.parseObjectName("t2"));
        joinItem.setOnCondition(sqlParser.parseExpression("t1.f1=t2.f2"));
       // System.out.println(select.toScript());

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                , select.toScript()
                , "SELECT *\n" +
                        "FROM   t1\n" +
                        "       LEFT JOIN t2\n" +
                        "       ON t1.f1 = t2.f2"
        ));
    }


    public void testModifySelectList(){
        parser.sqltext = "select t1.f1, t2.f2 as f2 from table1 t1 left join table2 t2 on t1.f1 = t2.f2 ";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);

        select.getResultColumnList().removeResultColumn(1);
        select.getResultColumnList().removeResultColumn(0);

        TResultColumn resultColumn1 = new TResultColumn();
        resultColumn1.setExpr(parser.parseExpression("t1.f3"));
        TAliasClause aliasClause1 = new TAliasClause();
        aliasClause1.setAliasName(parser.parseObjectName("f1"));
        aliasClause1.setHasAs(true);
        resultColumn1.setAliasClause(aliasClause1);
        select.getResultColumnList().addResultColumn(resultColumn1);

        TResultColumn resultColumn2 = new TResultColumn();
        resultColumn2.setExpr(parser.parseExpression("t2.f3"));
        select.getResultColumnList().addResultColumn(resultColumn2);
        // System.out.println(scriptGenerator.generateScript(select,true));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                , select.toScript()
                ,"SELECT t1.f3 AS f1,\n" +
                        "       t2.f3\n" +
                        "FROM   table1 t1\n" +
                        "       LEFT JOIN table2 t2\n" +
                        "       ON t1.f1 = t2.f2"
        ));

    }
    
    public void testFromClaueJoinTable(){
        parser.sqltext = "select table1.col1, table2.col2\n" +
                "from table1, table2\n" +
                "where table1.foo > table2.foo";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);

        select.joins.removeJoin(1);

        TJoin join = new TJoin();
        select.joins.addJoin(join);
        join.setNestedParen(1);
        TTable table1 = new TTable();
        table1.setTableName(parser.parseObjectName("tableX"));
        join.setTable(table1);

        TJoinItem joinItem = new TJoinItem();
        join.getJoinItems().addJoinItem(joinItem);
        joinItem.setJoinType(EJoinType.join);
        TTable table2 = new TTable();
        table2.setTableName(parser.parseObjectName("tableY"));
        joinItem.setTable(table2);


        TObjectNameList usingColumns = new TObjectNameList();
        usingColumns.addObjectName(parser.parseObjectName("id"));
        joinItem.setUsingColumns(usingColumns);

        TAliasClause aliasClause = new TAliasClause();
        aliasClause.setAliasName(parser.parseObjectName("table2"));
        aliasClause.setHasAs(true);
        join.setAliasClause(aliasClause);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                               ,select.toScript()
        ,"SELECT table1.col1,\n" +
                "       table2.col2\n" +
                "FROM   table1, (\n" +
                "       tablex JOIN \n" +
                "       tabley USING (ID)) AS table2\n" +
                "WHERE  table1.foo > table2.foo"
        ));

    }

    public void testRemoveResultColumnInSelectList(){
        parser.sqltext = "SELECT A as A_Alias, B AS B_Alias FROM TABLE_X";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        TResultColumnList columns = select.getResultColumnList();
        columns.removeResultColumn(1);
        TResultColumn resultColumn = new TResultColumn();
        resultColumn.setExpr(parser.parseExpression("x"));
        columns.addResultColumn(resultColumn);
        // System.out.println(scriptGenerator.generateScript(select, true));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT a AS a_alias,\n" +
                "       x\n" +
                "FROM   table_x"
                ));

    }

    public void testAddResultColumnInSelectList(){
        parser.sqltext = "SELECT A as A_Alias, B AS B_Alias FROM TABLE_X";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        TResultColumnList columns = select.getResultColumnList();

        TResultColumn resultColumn = new TResultColumn();
        resultColumn.setExpr(parser.parseExpression("d"));
        columns.addResultColumn(resultColumn);
        TAliasClause aliasClause = new TAliasClause();
        aliasClause.setAliasName(parser.parseObjectName("d_alias"));
        aliasClause.setHasAs(true);
        resultColumn.setAliasClause(aliasClause);

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT a AS a_alias,\n" +
                        "       b AS b_alias,\n" +
                        "       d AS d_alias\n" +
                        "FROM   table_x"
        ));

    }

    public void testRemoveTableInFromClauseAndRemoveWhereClause(){
        parser.sqltext = "SELECT * FROM t1,t2 where t1.f1=t2.f2";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        TJoinList joinList = select.joins;
        joinList.removeJoin(0);
        select.setWhereClause(null);

        // System.out.println(scriptGenerator.generateScript(select, true));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT *\n" +
                        "FROM   t2"
        ));

    }

    public void testRemoveTableAndAddJoinClause(){
        parser.sqltext = "SELECT * FROM t1,t2 where t1.f1=t2.f2";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        TJoinList joinList = select.joins;
        // let's remove t2 and where clause
        joinList.removeJoin(1);

        TJoinItem joinItem = new TJoinItem();
        joinList.getJoin(0).getJoinItems().addJoinItem(joinItem);
        joinItem.setJoinType(EJoinType.left);
        TTable joinTable = new TTable();
        joinItem.setTable(joinTable);
        joinTable.setTableName(parser.parseObjectName("t2"));
        joinItem.setOnCondition(parser.parseExpression("t1.f1=t2.f2"));

        // remove where clause
        select.setWhereClause(null);

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT *\n" +
                        "FROM   t1\n" +
                        "       LEFT JOIN t2\n" +
                        "       ON t1.f1 = t2.f2"
        ));
    }


    public void testRemoveCTE(){
        parser.sqltext = "with test as (select id from emp)\n" +
                "select * from test";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        select.setCteList(null);

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT *\n" +
                        "FROM   test"
        ));


    }

    public void testAddNewConditionInWhereClause2(){

        parser.sqltext = "SELECT * FROM TABLE_X where f > 0";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);

        TExpression expression1 = parser.parseExpression("c1>1");
        TExpression expression2 = new TExpression();
        expression2.setExpressionType(EExpressionType.logical_and_t);
        expression2.setLeftOperand(select.getWhereClause().getCondition());
        expression2.setRightOperand(expression1);
        select.getWhereClause().setCondition(expression2);

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                , select.toScript()
                , "SELECT *\n" +
                        "FROM   table_x\n" +
                        "WHERE  f > 0\n" +
                        "       AND c1 > 1"
        ));

    }

    public void testAddORConditionInWhereClause(){
        parser.sqltext = "SELECT * FROM TABLE_X where f > 0";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);

        TExpression expression1 = parser.parseExpression("c1>1");
        TExpression expression2 = new TExpression();
        expression2.setExpressionType(EExpressionType.logical_or_t);
        TExpression parensExpr = new TExpression();
        parensExpr.setExpressionType( EExpressionType.parenthesis_t );
        parensExpr.setLeftOperand(select.getWhereClause().getCondition());
        expression2.setLeftOperand(parensExpr);
        expression2.setRightOperand(expression1);

        select.getWhereClause().setCondition(expression2);

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT *\n" +
                        "FROM   table_x\n" +
                        "WHERE  ( f > 0 )\n" +
                        "       OR c1 > 1"
        ));
    }

    public void testAddNewConditionInWhereClause(){
        parser.sqltext = "select count(*) from TableName where NOT a OR NOT b";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);

        TExpression expression1 = parser.parseExpression("c1=1");


        TExpression expression2 = new TExpression();
        expression2.setExpressionType(EExpressionType.logical_and_t);
        TExpression parensExpr = new TExpression();
        parensExpr.setExpressionType( EExpressionType.parenthesis_t );
        parensExpr.setLeftOperand(select.getWhereClause().getCondition());
        expression2.setLeftOperand(parensExpr);
        expression2.setRightOperand(expression1);

        select.getWhereClause().setCondition(expression2);

        // System.out.println(scriptGenerator.generateScript(select,true));

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT count(*)\n" +
                        "FROM   tablename\n" +
                        "WHERE  ( NOT a\n" +
                        "         OR NOT b )\n" +
                        "       AND c1 = 1"
        ));


    }

    public void testAddWhereClause2(){
        parser.sqltext = "SELECT * FROM TABLE_X";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        TWhereClause whereClause = new TWhereClause();
        select.setWhereClause(whereClause);
        whereClause.setCondition(parser.parseExpression("c>1"));

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT *\n" +
                        "FROM   table_x\n" +
                        "WHERE  c > 1"
        ));


    }

    public void testAddWhereClauseBeforeGrouBy(){
        parser.sqltext = "SELECT * FROM TABLE_X group by a";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);

        TWhereClause whereClause = new TWhereClause();
        select.setWhereClause(whereClause);
        whereClause.setCondition(parser.parseExpression("c>1"));

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT   *\n" +
                        "FROM     table_x\n" +
                        "WHERE    c > 1\n" +
                        "GROUP BY a"
        ));

    }

    public void testAddWhereClauseAfterJoin(){
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
        TJoinItem item = joinList.getJoin( 0 ).getJoinItems( ).getJoinItem( 0 );

        TExpression expression1 = parser.parseExpression("1=1");

        TExpression expression2 = new TExpression();
        expression2.setExpressionType(EExpressionType.logical_and_t);
        TExpression parensExpr = new TExpression();
        parensExpr.setExpressionType( EExpressionType.parenthesis_t );
        parensExpr.setLeftOperand(item.getOnCondition());
        expression2.setLeftOperand(parensExpr);
        expression2.setRightOperand(expression1);

        item.setOnCondition(expression2);

        TWhereClause whereClause = new TWhereClause();
        whereClause.setCondition(parser.parseExpression("c>1"));
        select.setWhereClause(whereClause);

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT tablea.itema1,\n" +
                        "       tableb.itemb1\n" +
                        "FROM   tablea\n" +
                        "       INNER JOIN tableb\n" +
                        "       ON (tableb.itemb2 = tablea.itema2) AND 1 = 1\n" +
                        "       INNER JOIN ( SELECT tablec.itemc1 FROM tablec WHERE tablec.itemc3 = 'ABC' GROUP BY tablec.itemc1) unnamedjoin\n" +
                        "       ON unnamedjoin.itemc1 = tableb.itemb2\n" +
                        "WHERE  c > 1"
        ));

    }

    public void testRemoveWhereClause(){
        parser.sqltext = "SELECT * FROM TABLE_X where a>1 order by a";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        select.setWhereClause(null);
        // System.out.println(scriptGenerator.generateScript(select,true));

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT   *\n" +
                        "FROM     table_x\n" +
                        "ORDER BY a"
        ));

    }

    public void testAddOrderByClause(){
        parser.sqltext = "SELECT * FROM TABLE_X";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);

        TOrderBy orderBy = new TOrderBy();
        select.setOrderbyClause(orderBy);
        TOrderByItem orderByItem = new TOrderByItem();
        orderBy.getItems().addElement(orderByItem);
        orderByItem.setSortKey(parser.parseExpression("a"));
        orderByItem.setSortOrder(ESortType.desc);

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT   *\n" +
                        "FROM     table_x\n" +
                        "ORDER BY a DESC"
        ));



        parser.sqltext = "SELECT * FROM TABLE_X where a>1";
        assertTrue(parser.parse() == 0);
        select = (TSelectSqlStatement)parser.sqlstatements.get(0);

        orderBy = new TOrderBy();
        select.setOrderbyClause(orderBy);
        orderByItem = new TOrderByItem();
        orderBy.getItems().addElement(orderByItem);
        orderByItem.setSortKey(parser.parseExpression("a"));
        orderByItem.setSortOrder(ESortType.desc);

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT   *\n" +
                        "FROM     table_x\n" +
                        "WHERE    a > 1\n" +
                        "ORDER BY a DESC"
        ));



        parser.sqltext = "SELECT * FROM TABLE_X where a>1 group by a having count(*) > 1";
        assertTrue(parser.parse() == 0);
        select = (TSelectSqlStatement)parser.sqlstatements.get(0);

        orderBy = new TOrderBy();
        select.setOrderbyClause(orderBy);
        orderByItem = new TOrderByItem();
        orderBy.getItems().addElement(orderByItem);
        orderByItem.setSortKey(parser.parseExpression("a"));
        orderByItem.setSortOrder(ESortType.asc);

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT   *\n" +
                        "FROM     table_x\n" +
                        "WHERE    a > 1\n" +
                        "GROUP BY a\n" +
                        "HAVING  count(*) > 1\n" +
                        "ORDER BY a ASC"
        ));


        parser.sqltext = "SELECT * FROM TABLE_X where a>1 group by a having count(*) > 1 order by c desc";
        assertTrue(parser.parse() == 0);
        select = (TSelectSqlStatement)parser.sqlstatements.get(0);

        orderByItem = new TOrderByItem();
        orderBy.getItems().addElement(orderByItem);
        orderByItem.setSortKey(parser.parseExpression("a"));
        orderByItem.setSortOrder(ESortType.asc);
        select.getOrderbyClause().getItems().addOrderByItem(orderByItem);
        //  System.out.println(scriptGenerator.generateScript(select,true));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT   *\n" +
                        "FROM     table_x\n" +
                        "WHERE    a > 1\n" +
                        "GROUP BY a\n" +
                        "HAVING  count(*) > 1\n" +
                        "ORDER BY c DESC,\n" +
                        "         a ASC"
        ));


        parser.sqltext = "SELECT * FROM TABLE_X";
        assertTrue(parser.parse() == 0);
        select = (TSelectSqlStatement)parser.sqlstatements.get(0);

        TWhereClause whereClause = new TWhereClause();
        whereClause.setCondition(parser.parseExpression("a>1 and b>2"));
        select.setWhereClause(whereClause);
        //select.addWhereClause("a>1 and b>2") ;

        orderBy = new TOrderBy();
        select.setOrderbyClause(orderBy);
        orderByItem = new TOrderByItem();
        orderBy.getItems().addElement(orderByItem);
        orderByItem.setSortKey(parser.parseExpression("a"));
        orderByItem.setSortOrder(ESortType.desc);

        //System.out.println(scriptGenerator.generateScript(select,true));

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT   *\n" +
                        "FROM     table_x\n" +
                        "WHERE    a > 1\n" +
                        "         AND b > 2\n" +
                        "ORDER BY a DESC"
        ));


    }


    public void testRemoveItemInOrderByClause(){
        parser.sqltext = "SELECT * FROM TABLE_X order by a,b";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        select.getOrderbyClause().removeOrderByItem(1);

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT   *\n" +
                        "FROM     table_x\n" +
                        "ORDER BY a"
        ));


        select.setOrderbyClause(null);

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT *\n" +
                        "FROM   table_x"
        ));


    }

    public void testReplaceOrderByItemAndAddSortType(){
        parser.sqltext = "SELECT * FROM TABLE_X order by a";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        select.getOrderbyClause().getItems().removeElementAt(0);
        TOrderBy orderBy = select.getOrderbyClause();

        TOrderByItem orderByItem = new TOrderByItem();
        orderBy.getItems().addElement(orderByItem);
        orderByItem.setSortKey(parser.parseExpression("b"));
        orderByItem.setSortOrder(ESortType.asc);

        orderByItem = new TOrderByItem();
        orderBy.getItems().addElement(orderByItem);
        orderByItem.setSortKey(parser.parseExpression("a1"));
        orderByItem.setSortOrder(ESortType.desc);



        //System.out.println(scriptGenerator.generateScript(select,true));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT   *\n" +
                        "FROM     table_x\n" +
                        "ORDER BY b ASC,\n" +
                        "         a1 DESC"
        ));


    }

    public void testRemoveSetClauseInUpdate(){
        parser.sqltext = "UPDATE BLA SET A=2, B=3 WHERE X=5";
        assertTrue(parser.parse() == 0);

        TUpdateSqlStatement updateSqlStatement = (TUpdateSqlStatement)parser.sqlstatements.get(0);
        TResultColumnList setClauses = updateSqlStatement.getResultColumnList();
        setClauses.removeResultColumn(0);
        // System.out.println(scriptGenerator.generateScript(updateSqlStatement, true));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,updateSqlStatement.toScript()
                ,"UPDATE bla\n" +
                        "SET    b=3\n" +
                        "WHERE  x = 5"
        ));

    }

    public void testModifyJoinCondition(){
        parser.sqltext = "select * from t1 inner join t2 on t1.col1 = t2.col2";
        assertTrue(parser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)parser.sqlstatements.get(0);
        TJoin join = selectSqlStatement.joins.getJoin(0);
        TTable table = join.getTable();
        table.setTableName(parser.parseObjectName("t2"));
        TJoinItem joinItem = join.getJoinItems().getJoinItem(0);
        table = joinItem.getTable();
        table.setTableName(parser.parseObjectName("t1"));
        joinItem.setOnCondition(parser.parseExpression("t1.col3 = t2.col5"));

        // System.out.println(scriptGenerator.generateScript(selectSqlStatement, true));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,selectSqlStatement.toScript()
                ,"SELECT *\n" +
                        "FROM   t2\n" +
                        "       INNER JOIN t1\n" +
                        "       ON t1.col3 = t2.col5"
        ));


    }

    public void testRemoveJoin(){

        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlParser.sqltext = "SELECT X, Y, Z FROM A JOIN B ON A.X=B.Y";
        assertTrue(sqlParser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlParser.sqlstatements.get(0);
        TJoin join = selectSqlStatement.joins.getJoin(0);
        join.getJoinItems().removeElementAt(0);

//        System.out.println(scriptGenerator.generateScript(selectSqlStatement, true));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,selectSqlStatement.toScript()
                ,"SELECT x,\n" +
                        "       y,\n" +
                        "       z\n" +
                        "FROM   a"
        ));


    }


    public void testModifyTableInFromClause(){
        parser.sqltext = "select * from t1";
        assertTrue(parser.parse() == 0);

        TTable table = parser.sqlstatements.get(0).tables.getTable(0);
        table.setTableName(parser.parseObjectName("newt"));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,parser.sqlstatements.get(0).toScript()
                ,"SELECT *\n" +
                        "FROM   newt"
        ));

    }

    public void testAddTableAlias(){
        parser.sqltext = "select * from t1";
        assertTrue(parser.parse() == 0);

        TTable table = parser.sqlstatements.get(0).tables.getTable(0);
        TAliasClause aliasClause = new TAliasClause();
        aliasClause.setHasAs(true);
        aliasClause.setAliasName(parser.parseObjectName("foo"));
        table.setAliasClause(aliasClause);

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,parser.sqlstatements.get(0).toScript()
                ,"SELECT *\n" +
                        "FROM   t1 AS foo"
        ));
        //assertTrue(parser.sqlstatements.get(0).toString().trim().equalsIgnoreCase("select * from t1 AS foo"));

    }

    public void testModifyTableInCreateTable(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvimpala);
        sqlparser.sqltext = "create table if not exists campaign_1 ( id int, name string )";
        int ret = sqlparser.parse();
        TCustomSqlStatement stmt = sqlparser.sqlstatements.get(0);
        TTable table = stmt.tables.getTable(0);
        table.setTableName(parser.parseObjectName("prefix_." + table.getTableName().toString()));

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,table.toScript()
                ,"prefix_.campaign_1"
        ));

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
        groupBy.setHavingClause(null);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,selectSqlStatement.toScript()
                ,"SELECT   c.ID AS \"SMS.ID\"\n" +
                        "FROM     summit.cntrb_detail c\n" +
                        "WHERE    c.cntrb_date >= '$GivingFromDate$'\n" +
                        "         AND c.cntrb_date <= '$GivingThruDate$'\n" +
                        "GROUP BY c.ID"
        ));


    }

    public void testAlterTable_new(){
        TGSqlParser lcparser = new TGSqlParser(EDbVendor.dbvoracle);
        lcparser.sqltext = "ALTER TABLE P_CAP \n" +
                "ADD CONSTRAINT FK_P_CAP_R_PH_111_P_CEL \n" +
                "FOREIGN KEY (CAP_CEL) REFERENCES P_CEL (CEL_COD);";
        assertTrue(lcparser.parse() == 0);
        TAlterTableStatement at = (TAlterTableStatement) lcparser.sqlstatements.get(0);
        TAlterTableOption alterTableOption = at.getAlterTableOptionList().getAlterTableOption(0);
        TConstraint constraint =  alterTableOption.getConstraintList().getConstraint(0);
        assertTrue(constraint.getConstraint_type() == EConstraintType.foreign_key);

        constraint.getReferencedColumnList().addObjectName(parser.parseObjectName("CEL_NEWID"));
        //System.out.println(scriptGenerator.generateScript(at, true));

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,at.toScript()
                ,"ALTER TABLE p_cap \n" +
                        "  ADD CONSTRAINT fk_p_cap_r_ph_111_p_cel FOREIGN KEY (cap_cel) REFERENCES p_cel(cel_cod,cel_newid)"
        ));
    }

    public void testAddRefernceColumnInAlterTable2(){
        TGSqlParser lcparser = new TGSqlParser(EDbVendor.dbvoracle);
        lcparser.sqltext = "ALTER TABLE P_CAP \n" +
                "ADD CONSTRAINT FK_P_CAP_R_PH_111_P_CEL \n" +
                "FOREIGN KEY (CAP_CEL) REFERENCES P_CEL (CEL_COD);";
        assertTrue(lcparser.parse() == 0);
        TAlterTableStatement at = (TAlterTableStatement) lcparser.sqlstatements.get(0);
        TAlterTableOption alterTableOption = at.getAlterTableOptionList().getAlterTableOption(0);
        TConstraint constraint =  alterTableOption.getConstraintList().getConstraint(0);
        assertTrue(constraint.getConstraint_type() == EConstraintType.foreign_key);

        constraint.getReferencedColumnList().insertElementAt(parser.parseObjectName("cel_newid"),0);

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,at.toScript()
                ,"ALTER TABLE p_cap \n" +
                        "  ADD CONSTRAINT fk_p_cap_r_ph_111_p_cel FOREIGN KEY (cap_cel) REFERENCES p_cel(cel_newid,cel_cod)"
        ));


    }

    public void testAddWhereClause(){
        TGSqlParser lcparser = new TGSqlParser(EDbVendor.dbvoracle);
        lcparser.sqltext = "SELECT * FROM TABLE_X";
        assertTrue(lcparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)lcparser.sqlstatements.get(0);
        TWhereClause whereClause = new TWhereClause();
        select.setWhereClause(whereClause);
        whereClause.setCondition(parser.parseExpression("f > 0"));
        //System.out.println(scriptGenerator.generateScript(select,true));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT *\n" +
                        "FROM   table_x\n" +
                        "WHERE  f > 0"
        ));


    }

    public void testRemoveAdditionalParenthesisOfSubquery(){
        TSelectSqlStatement select = null, subquery = null;
        parser.sqltext = "select * from ((select * from some_table where some_column < ?)) some_view where a_column = something";
        assertTrue(parser.parse() == 0);
        select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        subquery = select.tables.getTable(0).subquery;
        subquery.setParenthesisCount(1);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT *\n" +
                        "FROM   (SELECT *\n" +
                        "        FROM   some_table\n" +
                        "        WHERE  some_column < ?) some_view\n" +
                        "WHERE  a_column = something"
        ));



        parser.sqltext = "(((select a from b)) order by a)";
        assertTrue(parser.parse() == 0);
        select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        select.setParenthesisCount(0);

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"(( SELECT   a\n" +
                        "   FROM     b))\n" +
                        "   ORDER BY a"
        ));



        parser.sqltext = "((((select a from b)) order by a))";
        assertTrue(parser.parse() == 0);
        select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        select.setParenthesisCount(1);
        select.setParenthesisCountBeforeOrder(1);

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"(( SELECT   a\n" +
                        "   FROM     b)\n" +
                        "   ORDER BY a)"
        ));

        //assertTrue(parser.sqlstatements.get(0).toString().equalsIgnoreCase("( ( select a from b ) order by a )"));

        parser.sqltext = "select * from user_table where ((username like '%admin%'));";
        assertTrue(parser.parse() == 0);
        select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        TExpression expression = select.getWhereClause().getCondition();
        select.getWhereClause().setCondition(expression.getLeftOperand());

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT *\n" +
                        "FROM   user_table\n" +
                        "WHERE  ( username LIKE '%admin%' )"
        ));

    }
    public void testSetNewWhereCondition(){
        parser.sqltext = "select t1.f1 from table1 t1 where t1.f2 = 2 ";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        select.getWhereClause().setCondition(parser.parseExpression("t1.f2>2"));
        // System.out.println(scriptGenerator.generateScript(select,true));

        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
                ,select.toScript()
                ,"SELECT t1.f1\n" +
                        "FROM   table1 t1\n" +
                        "WHERE  t1.f2 > 2"
        ));
    }

    public void testModifyHavingClause(){
        TGSqlParser SQLParser = new TGSqlParser(EDbVendor.dbvmysql);
        SQLParser.sqltext = "select\n" +
                "\n" +
                "   fiscalyear(cps.receiveDateActual) as \"year\", fiscalquarter(cps.receivedateactual) as \"qtr\",\n" +
                "\n" +
                "    concat(fiscalyear(cps.receiveDateActual),'Q',fiscalquarter(cps.receivedateactual)) as \"yearQtr\",\n" +
                "\n" +
                "    emspartner.name as \"emsPartner\",\n" +
                "\n" +
                "    max(emspartner.sysnotesunid) as \"emsPartnerUnid\",\n" +
                "\n" +
                "    sum(If(Date(cps.ReceiveDateActual) <= Date(lot.EmsDeliveryCommit),1,0)) as \"onTime\",\n" +
                "\n" +
                "    sum(If(Date(cps.ReceiveDateActual) > Date(lot.EmsDeliveryCommit),1,0)) as \"late\",\n" +
                "\n" +
                "    avg(If(Date(cps.ReceiveDateActual) <= Date(lot.EmsDeliveryCommit),0,datediff(cps.ReceiveDateActual,lot.EmsDeliveryCommit))) as \"daysLateAvg\",   \n" +
                "\n" +
                "    count(*) as \"total\",\n" +
                "\n" +
                "    lot.sysroles\n" +
                "\n" +
                "from\n" +
                "\n" +
                "               cerpproductsheet cps\n" +
                "\n" +
                "    join cerplot lot on (lot.sysnotesunid = cps.lotunid)\n" +
                "\n" +
                "    join cerpsysappcode emspartner on (emspartner.sysnotesunid = lot.boardVendorUnid)\n" +
                "\n" +
                "where\n" +
                "\n" +
                "               lot.emsDeliveryCommit is not null\n" +
                "\n" +
                "    and cps.receivedateactual is not null\n" +
                "\n" +
                "group by\n" +
                "\n" +
                "               fiscalyear(cps.receiveDateActual) desc, fiscalquarter(cps.receivedateactual) desc, emspartner.name\n" +
                "\n" +
                "order by\n" +
                "\n" +
                "               year";

        int iRet = SQLParser.parse();
        TSelectSqlStatement tselect = (TSelectSqlStatement)SQLParser.sqlstatements.get(0);

        TGroupBy groupByClause = tselect.getGroupByClause();

        TExpression expression = groupByClause.getHavingClause();

        if (expression == null){
            expression = SQLParser.parseExpression("1=1 and column1>10");
            groupByClause.setHavingClause(expression);
        } else {
            //expression.addANDCondition(theWhereOrHavingClause);
        }

       // System.out.println(tselect.toScript());
        SQLParser = null;
    }

    public void test4Having(){
        TGSqlParser SQLParser = new TGSqlParser(EDbVendor.dbvmysql);
        SQLParser.sqltext = "select a, b, c, count(*) from mytable group by a, b, c";

        int iRet = SQLParser.parse();
        TSelectSqlStatement tselect = (TSelectSqlStatement)SQLParser.sqlstatements.get(0);

        TGroupBy groupByClause = tselect.getGroupByClause();

        TExpression expression = groupByClause.getHavingClause();
        String having = "b='x'";
        if (expression == null){

            expression = SQLParser.parseExpression(having);
            if (expression == null){
                System.out.println("can't parse expression:"+having);
            }else{
                groupByClause.setHavingClause(expression);
            }

        } else {
            //expression.addANDCondition(theWhereOrHavingClause);
        }

        SQLParser = null;
    }

    public void testOrderByItem(){
        TGSqlParser SQLParser = new TGSqlParser(EDbVendor.dbvmysql);
        SQLParser.sqltext = "select a, b, c from mytable order by c";

        int iRet = SQLParser.parse();
        TSelectSqlStatement select = (TSelectSqlStatement)SQLParser.sqlstatements.get(0);

        TOrderBy orderBy = select.getOrderbyClause();

        TOrderByItem orderByItem = new TOrderByItem();
        orderByItem.setSortKey(parser.parseExpression("a"));
        orderByItem.setSortOrder(ESortType.desc);

        orderBy.getItems().addElement(orderByItem);
        //System.out.println(select.toScript());

        SQLParser = null;
    }

    public void testPredicateRegexp(){
        TGSqlParser SQLParser = new TGSqlParser(EDbVendor.dbvmysql);
        SQLParser.sqltext = "select a, b, c, count(*) from mytable group by a, b, c having a='X'";

        int iRet = SQLParser.parse();
        TSelectSqlStatement select = (TSelectSqlStatement)SQLParser.sqlstatements.get(0);

        TGroupBy groupByClause = select.getGroupByClause();

        TExpression expression = groupByClause.getHavingClause();
        String having = "b regexp 'foo.*'";
        TExpression newExpr = SQLParser.parseExpression(having+" and "+expression.toString());
        if (newExpr == null){
            System.out.println("can't parse expression:"+having);
            return;
        }

        groupByClause.setHavingClause(newExpr);
        //System.out.println(select.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmysql
                ,select.toScript()
                ,"select \n" +
                        "a,b,c,count(*)\n" +
                        " from \n" +
                        "mytable\n" +
                        " group  by a,b,c having b regexp 'foo.*'  and  a = 'X'"
        ));
        SQLParser = null;
    }


    public void testFunctionGroupConcat(){
        TGSqlParser SQLParser = new TGSqlParser(EDbVendor.dbvmysql);
        SQLParser.sqltext = "select a, b, c, count(*), group_concat(distinct d separator ' ') from mytable group by a, b, c";

        int iRet = SQLParser.parse();
        TSelectSqlStatement select = (TSelectSqlStatement)SQLParser.sqlstatements.get(0);

        TGroupBy groupByClause = select.getGroupByClause();

        TExpression expression = groupByClause.getHavingClause();
        String having = "b regexp 'foo.*'";
        if (expression != null){
            having = expression.toString() +" and "+ having;
        }
        TExpression newExpr = SQLParser.parseExpression(having);
        if (newExpr == null){
            System.out.println("can't parse expression:"+having);
            return;
        }

        groupByClause.setHavingClause(newExpr);
       // System.out.println(select.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmysql
                ,select.toScript()
                ,"select \n" +
                        "a,b,c,count(*),group_concat( distinct d separator ' ')\n" +
                        " from \n" +
                        "mytable\n" +
                        " group  by a,b,c having b regexp 'foo.*'"
        ));
        SQLParser = null;
    }


}
