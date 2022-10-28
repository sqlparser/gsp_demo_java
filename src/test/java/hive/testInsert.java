package hive;
/*
 * Date: 13-8-12
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EJoinType;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinItem;
import gudusoft.gsqlparser.nodes.EHiveInsertType;
import gudusoft.gsqlparser.nodes.TMultiTarget;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testInsert extends TestCase {

    public void test0(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = " insert into TABLE empTableName values (1, 'Angel', 10000, 'F', '1990-12-30', TIMESTAMP '1990-12-30 00:00:00');";
        assertTrue(sqlparser.parse() == 0);


        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        TMultiTarget multiTarget = insert.getValues().getMultiTarget(0);
        assertTrue(multiTarget.getColumnList().size() == 6);
    }

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "INSERT OVERWRITE DIRECTORY 's3://bucketname/path/subpath/' SELECT * \n" +
                "FROM hiveTableName;";
          assertTrue(sqlparser.parse() == 0);

        //  System.out.println(sqlparser.sqlstatements.get(0).sqlstatementtype);
        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(insert.getHiveInsertType() == EHiveInsertType.overwriteDirectory);
        assertTrue(insert.getDirectoryName().toString().equalsIgnoreCase("'s3://bucketname/path/subpath/'"));

        TSelectSqlStatement select = insert.getSubQuery();
        assertTrue(select.getResultColumnList().getResultColumn(0).toString().equalsIgnoreCase("*"));
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("hiveTableName"));
     }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "INSERT OVERWRITE TABLE hiveTableName SELECT * FROM s3_import;";
          assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(insert.getHiveInsertType() == EHiveInsertType.overwriteTable);
        assertTrue(insert.tables.getTable(0).toString().equalsIgnoreCase("hiveTableName"));

        TSelectSqlStatement select = insert.getSubQuery();
        assertTrue(select.getResultColumnList().getResultColumn(0).toString().equalsIgnoreCase("*"));
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("s3_import"));
     }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "INSERT OVERWRITE TABLE pv_users\n" +
                "SELECT pv.*, u.gender, u.age\n" +
                "FROM user FULL OUTER JOIN page_view pv ON (pv.userid = u.id)\n"+
                "WHERE pv.date = '2008-03-03';";
          assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(insert.getHiveInsertType() == EHiveInsertType.overwriteTable);
        assertTrue(insert.tables.getTable(0).toString().equalsIgnoreCase("pv_users"));

        TSelectSqlStatement select = insert.getSubQuery();
        TJoin join = select.joins.getJoin(0);
        assertTrue(join.getKind() == TBaseType.join_source_table);
        assertTrue(join.getTable().getTableName().toString().equalsIgnoreCase("user"));
        assertTrue(select.toString().equalsIgnoreCase("SELECT pv.*, u.gender, u.age\n" +
                "FROM user FULL OUTER JOIN page_view pv ON (pv.userid = u.id)\n" +
                "WHERE pv.date = '2008-03-03'"));
        TJoinItem joinItem = join.getJoinItems().getJoinItem(0);
        assertTrue(joinItem.getJoinType() == EJoinType.fullouter);
        assertTrue(joinItem.getTable().toString().equalsIgnoreCase("page_view"));
        assertTrue(joinItem.getOnCondition().toString().equalsIgnoreCase("(pv.userid = u.id)"));
     }

    public void test4(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "INSERT OVERWRITE LOCAL DIRECTORY '/tmp/pv_gender_sum'\n" +
                "SELECT pv_gender_sum.*\n" +
                "FROM pv_gender_sum;";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(insert.getHiveInsertType() == EHiveInsertType.overwriteLocalDirectory);
       // System.out.println(insert.getHiveInsertType());
        assertTrue(insert.getDirectoryName().toString().equalsIgnoreCase("'/tmp/pv_gender_sum'"));

        TSelectSqlStatement select = insert.getSubQuery();
        assertTrue(select.getResultColumnList().getResultColumn(0).toString().equalsIgnoreCase("pv_gender_sum.*"));
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("pv_gender_sum"));
    }

}
