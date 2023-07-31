package common;

import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;
import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinItem;

public class testJoins extends TestCase {

    private TGSqlParser parser = null;

    protected void setUp() throws Exception {
        super.setUp();
        parser = new TGSqlParser(EDbVendor.dbvmssql);
    }

    protected void tearDown() throws Exception {
        parser = null;
        super.tearDown();
    }

    public void testSearchToken(){

        parser.sqltext = "SELECT * \n" +
                "FROM    \n" +
                "        sysobjects AS so\n" +
                "\n" +
                "--// We are creating one record for each load the suborder is on\n" +
                "LEFT OUTER JOIN\n" +
                "--// We are creating one record for each load the suborder is on\n" +
                "\n" +
                "--// We are creating one record for each load the suborder is on\n" +
                "--// We are creating one record for each load the suborder is on\n" +
                "        (\n" +
                "                syscomments                AS SC WITH (NOLOCK)\n" +
                "        ) ON\n" +
                "                so.id = sc.id;";
        assertTrue(parser.parse() == 0);

        TJoin lcJoin = parser.sqlstatements.get(0).joins.getJoin(0);
    
        // lookup left keyword backword
        TJoinItem lcitem = lcJoin.getJoinItems().getJoinItem(0);
        TSourceToken st = lcitem.getStartToken();
        TSourceToken left  = st.searchToken(TBaseType.rrw_left,-5);
        assertTrue(left.toString().equalsIgnoreCase("left"));

        //System.out.println(lcitem.toString());
    }

    public void testStartToken(){

        parser.sqltext = "SELECT p.name AS product,\n" +
                "       p.listprice AS 'List Price',\n" +
                "       p.discount AS 'discount' \n" +
                "FROM   \n" +
                "  production1.product p \n" +
                "  left /*fdf */ JOIN production2.productsubcategory s ON p.productsubcategoryid = s.productsubcategoryid \n" +
                "WHERE  s.name LIKE product \n" +
                "       AND p.listprice < maxprice;  ";
       // System.out.println(parser.sqltext);

        assertTrue(parser.parse() == 0);

        //System.out.println(parser.sqlstatements.get(0).joins.size());
        TJoin lcJoin = parser.sqlstatements.get(0).joins.getJoin(0);
        TJoinItem lcitem = lcJoin.getJoinItems().getJoinItem(0);


        assertTrue(lcJoin.getStartToken().toString().equalsIgnoreCase("production1"));
        assertTrue(lcJoin.getEndToken().toString().equalsIgnoreCase("productsubcategoryid"));
        assertTrue(lcitem.getStartToken().toString().equalsIgnoreCase("production2"));
        assertTrue(lcitem.getEndToken().toString().equalsIgnoreCase("productsubcategoryid"));
        assertTrue(lcitem.getOnCondition().getStartToken().toString().equalsIgnoreCase("p"));
        assertTrue(lcitem.getOnCondition().getEndToken().toString().equalsIgnoreCase("productsubcategoryid"));

        // get "left" keyword before join
        TSourceToken st = lcitem.getStartToken();
        if (lcitem.getJoinType() == EJoinType.left){

            // search left keyword before the token of lctitem using token code of left keyword.
            // - means search before the first token of lctitem, range is 100 tokens
            // try to search token by token code to gain better performance,
            // token code of most important keywords can be found in syntax like:TBaseType.rrw_XXXX
            // if no token code defined for a keyword, you can search it by text of this keyword by setting targetTokenCode
            // parameter to 0.

            TSourceToken left  = st.searchToken(TBaseType.rrw_left,-100);
            // or
            //TSourceToken left  = st.searchToken("left",-100);

            assertTrue(left.toString().equalsIgnoreCase("left"));
           // System.out.println(left.toString());
        }else{
            // other join types:
            // join_cross:  cross join
            // join_natural: natural join
            // join_inner: [loop|hash|merge|remote] join, or [loop|hash|merge|remote] inner join
            // join_full: full [loop|hash|merge|remote] join
            // join_left: left [loop|hash|merge|remote] join
            // join_right: right [loop|hash|merge|remote] join
            // join_fullouter: full outer [loop|hash|merge|remote] join
            // join_leftouter: left outer [loop|hash|merge|remote] join
            // join_rightouter: right outer [loop|hash|merge|remote] join
            // join_crossapply: cross apply
            //join_outerapply: outer apply
            // [loop|hash|merge|remote]
        }

        TSourceToken onCondition = lcitem.getOnCondition().getStartToken();
        TSourceToken on = onCondition.searchToken(TBaseType.rrw_on,-100);

        assertTrue(on.toString().equalsIgnoreCase("on"));

        parser.sqltext = "select l.city,d.department_name\n" +
                "from locations l join departments d using (location_id)";

        assertTrue(parser.parse() == 0);

        TJoin lcJoin2 = parser.sqlstatements.get(0).joins.getJoin(0);
        TJoinItem lcitem2 = lcJoin2.getJoinItems().getJoinItem(0);

        TSourceToken usingExpr = lcitem2.getUsingColumns().getStartToken();

        TSourceToken using = usingExpr.searchToken(TBaseType.rrw_teradata_using,-100);
//        TSourceToken using = usingExpr.searchToken("using",-100);

        assertTrue(using.toString().equalsIgnoreCase("using"));

    }
    public void testMember1(){

        parser.sqltext = "select f1 from a right join (c join c1 on c.f1 = c1.f1) c2 on a.f1=c2.f1";
        assertTrue(parser.parse() == 0);
        TJoin lcJoin = parser.sqlstatements.get(0).joins.getJoin(0);
        assertTrue(lcJoin.getKind() == TBaseType.join_source_table );
        assertTrue(lcJoin.getTable().toString().compareToIgnoreCase("a") == 0);
        TJoinItem lcitem = lcJoin.getJoinItems().getJoinItem(0);
        assertTrue(lcitem.getKind() == TBaseType.join_source_join );
        assertTrue(lcitem.getJoinType() == EJoinType.right);
        assertTrue(lcitem.getOnCondition().toString().compareToIgnoreCase("a.f1=c2.f1") == 0);

        lcJoin = null;
        
        parser.sqltext = "select f1 from a full join c using(f1,f2)";
        assertTrue(parser.parse() == 0);
        TJoin lcJoin1 = parser.sqlstatements.get(0).joins.getJoin(0);
        assertTrue(lcJoin1.getKind() == TBaseType.join_source_table );
        assertTrue(lcJoin1.getTable().toString().compareToIgnoreCase("a") == 0);
        TJoinItem lcitem1 = lcJoin1.getJoinItems().getJoinItem(0);
        assertTrue(lcitem1.getKind() == TBaseType.join_source_table );
        assertTrue(lcitem1.getJoinType() == EJoinType.full);
        assertTrue(lcitem1.getUsingColumns().toString().compareToIgnoreCase("f1,f2") == 0);

      //  System.out.println(lcitem.getJoin().getAliasClause().toString());

    }
    
    public void testMember(){
        // test TJoin.getKind(), TJoin.getTable(), TJoin.getJoin()

        parser.sqltext = "select f from t1";
        assertTrue(parser.parse() == 0);
        assertTrue(parser.sqlstatements.get(0).joins.getJoin(0).getKind() == TBaseType.join_source_fake );
        assertTrue(parser.sqlstatements.get(0).joins.getJoin(0).getTable().toString().compareToIgnoreCase("t1") == 0);

        parser.sqltext = "select f from t as t1 join t2 on t1.f1 = t2.f1";
        assertTrue(parser.parse() == 0);
        assertTrue(parser.sqlstatements.get(0).joins.getJoin(0).getKind() == TBaseType.join_source_table );
        assertTrue(parser.sqlstatements.get(0).joins.getJoin(0).getTable().toString().compareToIgnoreCase("t") == 0);
        assertTrue(parser.sqlstatements.get(0).joins.getJoin(0).getTable().getAliasClause().toString().compareToIgnoreCase("t1") == 0);


        parser.sqltext = "select a_join.f1  from (a as a_alias left join a1 on a1.f1 = a_alias.f1 ) as a_join  join b on a_join.f1 = b.f1;";
        assertTrue(parser.parse() == 0);
        TJoin lcJoin = parser.sqlstatements.get(0).joins.getJoin(0);
        //System.out.println(lcJoin.getKind());
        assertTrue(lcJoin.getKind() == TBaseType.join_source_join );

        assertTrue(lcJoin.getJoin().toString().compareToIgnoreCase("(a as a_alias left join a1 on a1.f1 = a_alias.f1 )") == 0);
        assertTrue(lcJoin.getJoin().getAliasClause().toString().compareToIgnoreCase("a_join") == 0);
        assertTrue(lcJoin.getJoin().getJoinItems().getJoinItem(0).getJoinType() == EJoinType.left);

        assertTrue(lcJoin.getJoinItems().getJoinItem(0).getJoinType() == EJoinType.join);

        parser.sqltext = "select a_join.f1  from (a as a_alias left join a1 on a1.f1 = a_alias.f1 ) as a_join";
        assertTrue(parser.parse() == 0);
        TJoin lcJoin1 = parser.sqlstatements.get(0).joins.getJoin(0);
        assertTrue(lcJoin1.getKind() == TBaseType.join_source_join );
        assertTrue(lcJoin1.getJoin().toString().compareToIgnoreCase("(a as a_alias left join a1 on a1.f1 = a_alias.f1 )") == 0);
        assertTrue(lcJoin1.getJoin().getAliasClause().toString().compareToIgnoreCase("a_join") == 0);

    }

    public void testDo1(){
        parser.sqltext = "select f from t1,t2";
        assertTrue(parser.parse() == 0);
        assertTrue(parser.sqlstatements.get(0).joins.getJoin(0).getTable().toString().compareToIgnoreCase("t1") == 0  );
        assertTrue(parser.sqlstatements.get(0).joins.getJoin(1).getTable().toString().compareToIgnoreCase("t2") == 0  );

        parser.sqltext = "select f from t1 join t2 on t1.f1 = t2.f1";
        assertTrue(parser.parse() == 0);
        assertTrue(parser.sqlstatements.get(0).joins.getJoin(0).getTable().toString().compareToIgnoreCase("t1") == 0  );
        assertTrue(parser.sqlstatements.get(0).joins.getJoin(0).getJoinItems().getJoinItem(0).getTable().toString().compareToIgnoreCase("t2") == 0  );
        assertTrue(parser.sqlstatements.get(0).joins.getJoin(0).getJoinItems().getJoinItem(0).getOnCondition().toString().compareToIgnoreCase("t1.f1 = t2.f1") == 0  );

        parser.sqltext = "select f from t1 join t2 on t1.f1 = t2.f1 join t3 on t1.f1 = t3.f1";
        assertTrue(parser.parse() == 0);
        assertTrue(parser.sqlstatements.get(0).joins.getJoin(0).getTable().toString().compareToIgnoreCase("t1") == 0  );
        assertTrue(parser.sqlstatements.get(0).joins.getJoin(0).getJoinItems().getJoinItem(0).getTable().toString().compareToIgnoreCase("t2") == 0  );
        assertTrue(parser.sqlstatements.get(0).joins.getJoin(0).getJoinItems().getJoinItem(0).getOnCondition().toString().compareToIgnoreCase("t1.f1 = t2.f1") == 0  );
        assertTrue(parser.sqlstatements.get(0).joins.getJoin(0).getJoinItems().getJoinItem(1).getTable().toString().compareToIgnoreCase("t3") == 0  );
        assertTrue(parser.sqlstatements.get(0).joins.getJoin(0).getJoinItems().getJoinItem(1).getOnCondition().toString().compareToIgnoreCase("t1.f1 = t3.f1") == 0  );

        parser.sqltext = "select f from t1 join t2 using (f1,f2)";
        assertTrue(parser.parse() == 0);
        assertTrue(parser.sqlstatements.get(0).joins.getJoin(0).getTable().toString().compareToIgnoreCase("t1") == 0  );
        assertTrue(parser.sqlstatements.get(0).joins.getJoin(0).getJoinItems().getJoinItem(0).getTable().toString().compareToIgnoreCase("t2") == 0  );
        assertTrue(parser.sqlstatements.get(0).joins.getJoin(0).getJoinItems().getJoinItem(0).getUsingColumns().toString().compareToIgnoreCase("f1,f2") == 0  );
    }

    public void testMember2(){

        parser.sqltext = "select * \n" +
                "FROM a AS alias_a \n" +
                "   RIGHT JOIN ( (b left outer join f on (b.f1=f.f2)) \n" +
                "   \t\t\t\t\tLEFT JOIN (c full outer join c1 on (c1.f1 = c.f1) and (c1.f2=c.f2)) \n" +
                "\t\t\t\tON (b.b1 = c.c1) AND (b.b2 = c.c2)) \n" +
                "\tON (a.a1 = b.b3) AND (a.a2 = b.b4);";
        assertTrue(parser.parse() == 0);
        //System.out.println(parser.sqltext);
        TJoin lcJoin = parser.sqlstatements.get(0).joins.getJoin(0);
        assertTrue(lcJoin.getKind() == TBaseType.join_source_table );
        assertTrue(lcJoin.getTable().toString().compareToIgnoreCase("a") == 0);
        assertTrue(lcJoin.getTable().getAliasClause().toString().compareToIgnoreCase("alias_a") == 0);
        assertTrue(lcJoin.getJoinItems().size() == 1);

        TJoinItem lcJoinItem = lcJoin.getJoinItems().getJoinItem(0);
        assertTrue(lcJoinItem.getKind() == TBaseType.join_source_join);
        //System.out.println(lcJoinItem.getOnCondition().toString());
        assertTrue(lcJoinItem.getOnCondition().toString().compareToIgnoreCase("(a.a1 = b.b3) AND (a.a2 = b.b4)") == 0);

        TJoin lcJoinItemJoin = lcJoinItem.getJoin();
        assertTrue(lcJoinItemJoin.getKind() == TBaseType.join_source_join);

        assertTrue(lcJoinItemJoin.getJoinItems().getJoinItem(0).getOnCondition().toString().compareToIgnoreCase("(b.b1 = c.c1) AND (b.b2 = c.c2)") == 0);

        TJoin lcJoinItemJoinJoin = lcJoinItemJoin.getJoin();
        assertTrue(lcJoinItemJoinJoin.getKind() == TBaseType.join_source_table);

        assertTrue(lcJoinItemJoinJoin.getTable().toString().compareToIgnoreCase("b") == 0);
        TJoinItem lcJoinItemJoinJoinJoinItem = lcJoinItemJoinJoin.getJoinItems().getJoinItem(0);
        assertTrue(lcJoinItemJoinJoinJoinItem.getJoinType() == EJoinType.leftouter);
        assertTrue(lcJoinItemJoinJoinJoinItem.getTable().toString().compareToIgnoreCase("f") == 0);
        //System.out.println(lcJoinItemJoinJoinJoinItem.getOnCondition().toString());
        assertTrue(lcJoinItemJoinJoinJoinItem.getOnCondition().toString().compareToIgnoreCase("(b.f1=f.f2)") == 0);

        TJoinItem lcJoinItem2 = lcJoinItemJoin.getJoinItems().getJoinItem(0);
        assertTrue(lcJoinItem2.getJoinType() == EJoinType.left);
        assertTrue(lcJoinItem2.getKind() == TBaseType.join_source_join);
        TJoin lcJoin2 = lcJoinItem2.getJoin();
        assertTrue(lcJoin2.getTable().toString().compareToIgnoreCase("c") == 0);
        assertTrue(lcJoin2.getJoinItems().getJoinItem(0).getJoinType() == EJoinType.fullouter);
        assertTrue(lcJoin2.getJoinItems().getJoinItem(0).getOnCondition().toString().compareToIgnoreCase("(c1.f1 = c.f1) and (c1.f2=c.f2)") == 0);
    }

    public void testMySQLNaturalLeftOuter(){
        TGSqlParser mySQLparser = new TGSqlParser(EDbVendor.dbvmysql);
        mySQLparser.sqltext = "SELECT * FROM A NATURAL LEFT OUTER JOIN B;";
        assertTrue(mySQLparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)mySQLparser.sqlstatements.get(0);
        assertTrue(select.joins.getJoin(0).getTable().toString().equalsIgnoreCase("A"));
        assertTrue(select.joins.getJoin(0).getJoinItems().getJoinItem(0).getJoinType() == EJoinType.natural_leftouter);
        assertTrue(select.joins.getJoin(0).getJoinItems().getJoinItem(0).getTable().toString().equalsIgnoreCase("B"));
    }

    public void testMySQLNaturalJoin(){
        TGSqlParser mySQLparser = new TGSqlParser(EDbVendor.dbvmysql);
        mySQLparser.sqltext = "SELECT * FROM (emp NATURAL JOIN salary) NATURAL JOIN customer;";
        assertTrue(mySQLparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)mySQLparser.sqlstatements.get(0);
        assertTrue(select.joins.getJoin(0).getKind() == TBaseType.join_source_join);
        assertTrue(select.joins.getJoin(0).getJoin().getTable().toString().equalsIgnoreCase("emp"));
        TJoinItem ji = select.joins.getJoin(0).getJoin().getJoinItems().getJoinItem(0);
        assertTrue(ji.getJoinType() == EJoinType.natural);
        assertTrue(ji.getTable().toString().equalsIgnoreCase("salary"));

        TJoinItem ji2  = select.joins.getJoin(0).getJoinItems().getJoinItem(0);
        assertTrue(ji2.getJoinType() == EJoinType.natural);
        assertTrue(ji2.getTable().toString().equalsIgnoreCase("customer"));
    }

    public void testModifyJoinCondition(){
        parser.sqltext = "select f from t1 join t2 on t1.f1 = t2.f1";
        assertTrue(parser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement) parser.sqlstatements.get(0);
        TExpression condition = select.joins.getJoin(0).getJoinItems().getJoinItem(0).getOnCondition();
        condition.setString("t1.col3 = t2.col5");
       // System.out.println(select.toString());
        assertTrue(select.toString().equalsIgnoreCase("select f from t1 join t2 on t1.col3 = t2.col5"));

    }
}
