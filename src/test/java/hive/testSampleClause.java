package hive;
/*
 * Date: 13-8-12
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TAliasClause;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TTableSample;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testSampleClause extends TestCase {

    public void testSampling(){
          TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
          sqlparser.sqltext ="SELECT *\n" +
                  "FROM source TABLESAMPLE(BUCKET 3 OUT OF 32 ON rand()) s;";
          assertTrue(sqlparser.parse() == 0);

          TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
          assertTrue(select.getResultColumnList().size() == 1);

        assertTrue(select.joins.size() == 1);
        TJoin join = select.joins.getJoin(0);
        assertTrue(join.getKind() == TBaseType.join_source_fake);

        TTable table = join.getTable();
        assertTrue(table.getTableType() == ETableSource.objectname);
        TAliasClause aliasClause = table.getAliasClause();
        assertTrue(aliasClause.getAliasName().toString().equalsIgnoreCase("s"));

        TTableSample tableSample = table.getTableSample();
        assertTrue(tableSample.getBucketNumber().toString().equalsIgnoreCase("3"));
        assertTrue(tableSample.getOutofNumber().toString().equalsIgnoreCase("32"));
        assertTrue(tableSample.getOnExprList().size() == 1);
        assertTrue(tableSample.getOnExprList().getExpression(0).toString().equalsIgnoreCase("rand()"));
        //System.out.println(tableSample.toString());
    }

    public void testSampling2(){
          TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
          sqlparser.sqltext ="SELECT *\n" +
                  "FROM source TABLESAMPLE(0.1 PERCENT) s;";
          assertTrue(sqlparser.parse() == 0);

          TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
          assertTrue(select.getResultColumnList().size() == 1);

        assertTrue(select.joins.size() == 1);
        TJoin join = select.joins.getJoin(0);
        assertTrue(join.getKind() == TBaseType.join_source_fake);

        TTable table = join.getTable();
        assertTrue(table.getTableType() == ETableSource.objectname);
        TAliasClause aliasClause = table.getAliasClause();
        assertTrue(aliasClause.getAliasName().toString().equalsIgnoreCase("s"));

        TTableSample tableSample = table.getTableSample();
        assertTrue(tableSample.getNumerator().toString().equalsIgnoreCase("0.1"));
        assertTrue(tableSample.getPercent().toString().equalsIgnoreCase("PERCENT"));
        //System.out.println(tableSample.toString());
    }

    public void testSampling3(){
          TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
          sqlparser.sqltext ="SELECT *\n" +
                  "FROM source TABLESAMPLE(100M) s;";
          assertTrue(sqlparser.parse() == 0);

          TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
          assertTrue(select.getResultColumnList().size() == 1);

        assertTrue(select.joins.size() == 1);
        TJoin join = select.joins.getJoin(0);
        assertTrue(join.getKind() == TBaseType.join_source_fake);

        TTable table = join.getTable();
        assertTrue(table.getTableType() == ETableSource.objectname);
        TAliasClause aliasClause = table.getAliasClause();
        assertTrue(aliasClause.getAliasName().toString().equalsIgnoreCase("s"));

        TTableSample tableSample = table.getTableSample();
        assertTrue(tableSample.getNumerator().toString().equalsIgnoreCase("100M"));
        assertTrue(tableSample.getPercent() == null);
        //System.out.println(tableSample.toString());
    }

    public void testSampling4(){
          TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
          sqlparser.sqltext ="SELECT * FROM source TABLESAMPLE(10 ROWS);";
          assertTrue(sqlparser.parse() == 0);

          TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
          assertTrue(select.getResultColumnList().size() == 1);

        assertTrue(select.joins.size() == 1);
        TJoin join = select.joins.getJoin(0);
        assertTrue(join.getKind() == TBaseType.join_source_fake);

        TTable table = join.getTable();
        assertTrue(table.getTableType() == ETableSource.objectname);


        TTableSample tableSample = table.getTableSample();
        assertTrue(tableSample.getNumerator().toString().equalsIgnoreCase("10"));
        assertTrue(tableSample.getPercent().toString().equalsIgnoreCase("ROWS"));
        //System.out.println(tableSample.toString());
    }


}
