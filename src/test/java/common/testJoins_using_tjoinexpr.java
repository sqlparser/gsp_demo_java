package common;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testJoins_using_tjoinexpr extends TestCase {

    private TGSqlParser parser = null;

    protected void setUp() throws Exception {
        super.setUp();
        parser = new TGSqlParser(EDbVendor.dbvmssql);
    }

    protected void tearDown() throws Exception {
        parser = null;
        super.tearDown();
    }

    public void testJoin1(){

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
        //System.out.println(parser.sqltext);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement) parser.sqlstatements.get(0);
        assertTrue(selectSqlStatement.getRelations().size() == 1);
        assertTrue(selectSqlStatement.getRelations().get(0).getTableType() == ETableSource.join);
        TJoinExpr join = selectSqlStatement.getRelations().get(0).getJoinExpr();
        assertTrue(join.getJointype() == EJoinType.leftouter);
        assertTrue(join.onCondition.toString().equalsIgnoreCase("so.id = sc.id"));
        TTable left =  join.getLeftTable();
        assertTrue(left.getTableType() == ETableSource.objectname);
        assertTrue(left.getTableName().toString().equalsIgnoreCase("sysobjects"));
        assertTrue(left.getAliasName().equalsIgnoreCase("so"));
        TTable right = join.getRightTable();
        assertTrue(right.getTableName().toString().equalsIgnoreCase("syscomments"));
        assertTrue(right.getAliasName().equalsIgnoreCase("SC"));
    }


    public void testJoin2(){

        parser.sqltext = "select f1 from a right join (c join c1 on c.f1 = c1.f1) c2 on a.f1=c2.f1";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement) parser.sqlstatements.get(0);

        assertTrue(selectSqlStatement.getRelations().size() == 1);
        assertTrue(selectSqlStatement.getRelations().get(0).getTableType() == ETableSource.join);
        TJoinExpr join = selectSqlStatement.getRelations().get(0).getJoinExpr();
        assertTrue(join.getJointype() == EJoinType.right);
        assertTrue(join.onCondition.toString().equalsIgnoreCase("a.f1=c2.f1"));
        TTable left =  join.getLeftTable();
        assertTrue(left.getTableType() == ETableSource.objectname);
        assertTrue(left.getTableName().toString().equalsIgnoreCase("a"));

        TTable right = join.getRightTable();
        assertTrue(right.getTableType() == ETableSource.join);
        assertTrue(right.getAliasName().equalsIgnoreCase("c2"));
        join = right.getJoinExpr();
        assertTrue(join.getJointype() == EJoinType.join);
        left = join.getLeftTable();
        right = join.getRightTable();
        assertTrue(join.onCondition.toString().equalsIgnoreCase("c.f1 = c1.f1"));

        parser.sqltext = "select f1 from a full join c using(f1,f2)";
        assertTrue(parser.parse() == 0);
        selectSqlStatement = (TSelectSqlStatement) parser.sqlstatements.get(0);
        assertTrue(selectSqlStatement.getRelations().size() == 1);
        assertTrue(selectSqlStatement.getRelations().get(0).getTableType() == ETableSource.join);
        join = selectSqlStatement.getRelations().get(0).getJoinExpr();
        assertTrue(join.getJointype() == EJoinType.full);
        assertTrue(join.usingColumns.getObjectName(0).toString().equalsIgnoreCase("f1"));
        assertTrue(join.usingColumns.getObjectName(1).toString().equalsIgnoreCase("f2"));

    }

    public void testJoin3(){
        // test TJoin.getKind(), TJoin.getTable(), TJoin.getJoin()

        parser.sqltext = "select f from t1";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement) parser.sqlstatements.get(0);

        assertTrue(selectSqlStatement.getRelations().size() == 1);
        assertTrue(selectSqlStatement.getRelations().get(0).getTableType() == ETableSource.objectname);
        assertTrue(selectSqlStatement.getRelations().get(0).getTableName().toString().equalsIgnoreCase("t1"));



        parser.sqltext = "select a_join.f1  from (a as a_alias left join a1 on a1.f1 = a_alias.f1 ) as a_join  join b on a_join.f1 = b.f1;";
        assertTrue(parser.parse() == 0);
        selectSqlStatement = (TSelectSqlStatement) parser.sqlstatements.get(0);
        assertTrue(selectSqlStatement.getRelations().size() == 1);

        TJoinExpr join = selectSqlStatement.getRelations().get(0).getJoinExpr();
        assertTrue(join.getJointype() == EJoinType.join);
        assertTrue(join.getOnCondition().toString().equalsIgnoreCase("a_join.f1 = b.f1"));
        TTable left = join.getLeftTable();
        assertTrue(left.getTableType() == ETableSource.join);
        join = left.getJoinExpr();
        assertTrue(join.getJointype() == EJoinType.left);
        TTable right = join.getRightTable();



        parser.sqltext = "select a_join.f1  from (a as a_alias left join a1 on a1.f1 = a_alias.f1 ) as a_join";
        assertTrue(parser.parse() == 0);

        selectSqlStatement = (TSelectSqlStatement) parser.sqlstatements.get(0);
        assertTrue(selectSqlStatement.getRelations().size() == 1);

        TTable table = selectSqlStatement.getRelations().get(0);
        assertTrue(table.getAliasName().equalsIgnoreCase("a_join"));
        assertTrue(table.getTableType() == ETableSource.join);
        join = table.getJoinExpr();
        assertTrue(join.getJointype() == EJoinType.left);

        parser.sqltext = "select a_join.f1  from (a as a_alias left join a1 on a1.f1 = a_alias.f1 )";
        assertTrue(parser.parse() == 0);

        selectSqlStatement = (TSelectSqlStatement) parser.sqlstatements.get(0);
        assertTrue(selectSqlStatement.getRelations().size() == 1);

        table = selectSqlStatement.getRelations().get(0);
        assertTrue(table.getAliasClause() == null);
        assertTrue(table.getTableType() == ETableSource.join);
        join = table.getJoinExpr();
        assertTrue(join.getJointype() == EJoinType.left);

        parser.sqltext = "select a_join.f1  from a as a_alias left join a1 on a1.f1 = a_alias.f1";
        assertTrue(parser.parse() == 0);

        selectSqlStatement = (TSelectSqlStatement) parser.sqlstatements.get(0);
        assertTrue(selectSqlStatement.getRelations().size() == 1);
        assertTrue(selectSqlStatement.getRelations().get(0).getTableType() == ETableSource.join);
        join = selectSqlStatement.getRelations().get(0).getJoinExpr();
        assertTrue(join.getJointype() == EJoinType.left);

    }

    public void testJoin4(){
        parser.sqltext = "select f from t1,t2";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement) parser.sqlstatements.get(0);
        assertTrue(selectSqlStatement.getRelations().size() == 2);
        assertTrue(selectSqlStatement.getRelations().get(0).getTableName().toString().equalsIgnoreCase("t1"));
        assertTrue(selectSqlStatement.getRelations().get(1).getTableName().toString().equalsIgnoreCase("t2"));


        parser.sqltext = "select f from t1 join t2 on t1.f1 = t2.f1 left join t3 on t1.f1 = t3.f1";
        assertTrue(parser.parse() == 0);
        selectSqlStatement = (TSelectSqlStatement) parser.sqlstatements.get(0);
        assertTrue(selectSqlStatement.getRelations().size() == 1);
        TJoinExpr join = selectSqlStatement.getRelations().get(0).getJoinExpr();
        assertTrue(join.getJointype() == EJoinType.left);
        assertTrue(join.getOnCondition().toString().equalsIgnoreCase("t1.f1 = t3.f1"));

        parser.sqltext = "SELECT e1.EMPLOYEE_ID\n" +
                "FROM HR.EMPLOYEES e1 JOIN HR.EMPLOYEES e2 ON e1.EMPLOYEE_ID  = e2.MANAGER_ID,\n" +
                "HR.EMPLOYEES e3,HR.EMPLOYEES e4;";
        assertTrue(parser.parse() == 0);
        selectSqlStatement = (TSelectSqlStatement) parser.sqlstatements.get(0);
        assertTrue(selectSqlStatement.getRelations().size() == 3);
        join = selectSqlStatement.getRelations().get(0).getJoinExpr();
        TTable table0 = selectSqlStatement.getRelations().get(0);
        assertTrue(table0.getTableType() == ETableSource.join);
        join = table0.getJoinExpr();
        assertTrue(join.getJointype() == EJoinType.join);
        assertTrue(join.getOnCondition().toString().equalsIgnoreCase("e1.EMPLOYEE_ID  = e2.MANAGER_ID"));
        assertTrue(join.getLeftTable().getTableType() == ETableSource.objectname);
        assertTrue(join.getRightTable().getTableType() == ETableSource.objectname);
        TTable table1 = selectSqlStatement.getRelations().get(1);
        assertTrue(table1.getTableType() == ETableSource.objectname);
        TTable table2 = selectSqlStatement.getRelations().get(2);
        assertTrue(table2.getTableType() == ETableSource.objectname);
    }

    public void testJoin5(){

        parser.sqltext = "select * \n" +
                "FROM a AS alias_a \n" +
                "   RIGHT JOIN ( (b left outer join f on (b.f1=f.f2)) \n" +
                "   \t\t\t\t\tLEFT JOIN (c full outer join c1 on (c1.f1 = c.f1) and (c1.f2=c.f2)) \n" +
                "\t\t\t\tON (b.b1 = c.c1) AND (b.b2 = c.c2)) \n" +
                "\tON (a.a1 = b.b3) AND (a.a2 = b.b4);";
        assertTrue(parser.parse() == 0);
        //System.out.println(parser.sqltext);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement) parser.sqlstatements.get(0);
        assertTrue(selectSqlStatement.getRelations().size() == 1);
         TTable table = selectSqlStatement.getRelations().get(0);
         assertTrue(table.getTableType() == ETableSource.join);
         TJoinExpr join = table.getJoinExpr();
         assertTrue(join.getJointype() == EJoinType.right);
         assertTrue(join.getOnCondition().toString().equalsIgnoreCase("(a.a1 = b.b3) AND (a.a2 = b.b4)"));
         TTable leftTable  = join.getLeftTable();
         TTable rightTable = join.getRightTable();

         // left
         assertTrue(leftTable.getTableType() == ETableSource.objectname);
         assertTrue(leftTable.getAliasName().equalsIgnoreCase("alias_a"));
         assertTrue(leftTable.getTableName().toString().equalsIgnoreCase ("a"));

         // right
         assertTrue(rightTable.getTableType() == ETableSource.join);
         join = rightTable.getJoinExpr();
         assertTrue(join.getJointype() == EJoinType.left);
         assertTrue(join.getOnCondition().toString().equalsIgnoreCase("(b.b1 = c.c1) AND (b.b2 = c.c2)"));
         leftTable  = join.getLeftTable();
         assertTrue(leftTable.getTableType() == ETableSource.join);
         TJoinExpr join1 = leftTable.getJoinExpr(); // b left outer join f on (b.f1=f.f2)
         assertTrue(join1.getJointype() == EJoinType.leftouter);
         assertTrue(join1.getOnCondition().toString().equalsIgnoreCase("(b.f1=f.f2)"));
         TTable leftTable1 = join1.getLeftTable();
         assertTrue(leftTable1.getTableType() == ETableSource.objectname);
         assertTrue(leftTable1.getTableName().toString().equalsIgnoreCase("b"));
         TTable rightTable1 = join1.getRightTable();
        assertTrue(rightTable1.getTableType() == ETableSource.objectname);
        assertTrue(rightTable1.getTableName().toString().equalsIgnoreCase("f"));

         rightTable = join.getRightTable();
         assertTrue(rightTable.getTableType() == ETableSource.join);
         TJoinExpr join2 = rightTable.getJoinExpr(); // c full outer join c1 on (c1.f1 = c.f1) and (c1.f2=c.f2)
        assertTrue(join2.getJointype() == EJoinType.fullouter);
        assertTrue(join2.getOnCondition().toString().equalsIgnoreCase("(c1.f1 = c.f1) and (c1.f2=c.f2)"));
        leftTable = join2.getLeftTable();
        assertTrue(leftTable.getTableType() == ETableSource.objectname);
        assertTrue(leftTable.getTableName().toString().equalsIgnoreCase("c"));
        rightTable = join2.getRightTable();
        assertTrue(rightTable.getTableType() == ETableSource.objectname);
        assertTrue(rightTable.getTableName().toString().equalsIgnoreCase("c1"));
    }

    public void testJoin6(){
        TGSqlParser mySQLparser = new TGSqlParser(EDbVendor.dbvmysql);
        mySQLparser.sqltext = "SELECT * FROM A NATURAL LEFT OUTER JOIN B;";
        assertTrue(mySQLparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)mySQLparser.sqlstatements.get(0);
        assertTrue(select.getRelations().size() == 1);
        TTable table = select.getRelations().get(0);
        assertTrue(table.getTableType() == ETableSource.join);
        TJoinExpr join = table.getJoinExpr();
        assertTrue(join.getJointype() == EJoinType.natural_leftouter);
        TTable leftTable = join.getLeftTable();
        assertTrue(leftTable.getTableName().toString().equalsIgnoreCase("A"));
        TTable rightTable = join.getRightTable();
        assertTrue(rightTable.getTableName().toString().equalsIgnoreCase("B"));
    }

    public void testJoin7(){
        TGSqlParser mySQLparser = new TGSqlParser(EDbVendor.dbvmysql);
        mySQLparser.sqltext = "SELECT * FROM (emp NATURAL JOIN salary) NATURAL JOIN customer;";
        assertTrue(mySQLparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)mySQLparser.sqlstatements.get(0);

        assertTrue(select.getRelations().size() == 1);
        TTable table = select.getRelations().get(0);
        assertTrue(table.getTableType() == ETableSource.join);
        TJoinExpr join = table.getJoinExpr();
        assertTrue(join.getJointype() == EJoinType.natural);
        TTable leftTable = join.getLeftTable();
        assertTrue(leftTable.getTableType() == ETableSource.join);
        TJoinExpr join1 = leftTable.getJoinExpr();
        assertTrue(join1.getJointype() == EJoinType.natural);
        assertTrue(join1.getLeftTable().toString().equalsIgnoreCase("emp"));
        assertTrue(join1.getRightTable().toString().equalsIgnoreCase("salary"));
        TTable rightTable = join.getRightTable();
        assertTrue(rightTable.getTableName().toString().equalsIgnoreCase("customer"));

    }


}
