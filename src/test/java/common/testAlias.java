package common;
/*
 * Date: 11-9-26
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TAliasClause;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TCreateMaterializedSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testAlias extends TestCase {

    public void test0(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "select * from ( select distinct class, company, riskstate, subcoy, version from policy.class ) as b";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = selectSqlStatement.tables.getTable(0);

        int asClauseNo = 1;

        table.setString (table.toString () + " AS T" + asClauseNo);
        asClauseNo++;

       // System.out.println("sql:"+selectSqlStatement.toString());
        assertTrue(selectSqlStatement.toString().equalsIgnoreCase("select * from ( select distinct class, company, riskstate, subcoy, version from policy.class ) AS T1"));
    }


    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "select * from ( select distinct class, company, riskstate, subcoy, version from policy.class ) ";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = selectSqlStatement.tables.getTable(0);

        int asClauseNo = 1;
        if (table.getTableType ().equals (ETableSource.subquery))
        {
           if (table.getAliasClause () == null)
           {
                table.setString (table.toString () + " AS T" + asClauseNo);
           }
           else
           {
               TAliasClause tac = table.getAliasClause ();
               tac.setString(tac.toString() +asClauseNo);
           }
            asClauseNo++;
        }

         //System.out.println("sql:"+selectSqlStatement.toString());
        assertTrue(selectSqlStatement.toString().trim().equalsIgnoreCase("select * from ( select distinct class, company, riskstate, subcoy, version from policy.class ) AS T1"));

    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "select * from ( select distinct class, company, riskstate, subcoy, version from policy.class ) as b";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = selectSqlStatement.tables.getTable(0);

        int asClauseNo = 1;
        if (table.getTableType ().equals (ETableSource.subquery))
        {
           if (table.getAliasClause () == null)
           {
                table.setString (table.toString () + " AS T" + asClauseNo);
           }
           else
           {
               TAliasClause tac = table.getAliasClause ();
               tac.setString(tac.toString() +asClauseNo);
           }
            asClauseNo++;
        }

        // System.out.println("sql:"+selectSqlStatement.toString());
        assertTrue(selectSqlStatement.toString().trim().equalsIgnoreCase("select * from ( select distinct class, company, riskstate, subcoy, version from policy.class ) as b1"));

    }

//    public void testTeradataColumnAlias(){
//        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
//        sqlparser.sqltext = "SELECT \n" +
//                "t1.work_ord_group_id\n" +
//                "FROM vt_fixed_detail_build_6";
//        assertTrue(sqlparser.parse() == 0);
//        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
//        TResultColumn resultColumn = selectSqlStatement.getResultColumnList().getResultColumn(0);
//        System.out.println(resultColumn.getColumnAlias());
//    }

    public void testAlias(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE MATERIALIZED VIEW SYS_TEST.TEST\n" +
                "    AS SELECT\n" +
                "       (tableAlias1.column1) columnAlias1,\n" +
                "       (tableAlias2.column2) columnAlias2\n" +
                "    FROM\n" +
                "       SYS_TEST_A.TABLE1@Database1 tableAlias1,\n" +
                "       SYS_TEST_A.TABLE2@Database3 tableAlias2";
        assertTrue(sqlparser.parse() == 0);

        TCreateMaterializedSqlStatement createMaterializedViewStmt = (TCreateMaterializedSqlStatement)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement subquery = createMaterializedViewStmt.getSubquery();
        assertTrue(subquery.getResultColumnList().size() == 2);
        TResultColumn resultColumn = subquery.getResultColumnList().getResultColumn(0);
       assertTrue(resultColumn.getAliasClause().toString().equalsIgnoreCase("columnAlias1"));
        TTable table = subquery.getTables().getTable(0);
        assertTrue(table.getAliasClause().toString().equalsIgnoreCase("tableAlias1"));

    }
}
