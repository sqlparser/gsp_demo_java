package gudusoft.gsqlparser.hiveTest;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.nodes.hive.*;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;


public class testFromSelect extends TestCase {

     public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext ="FROM pv_users \n" +
                "INSERT OVERWRITE TABLE pv_gender_sum\n" +
                "  SELECT pv_users.gender, count(DISTINCT pv_users.userid) \n" +
                "  GROUP BY pv_users.gender \n" +
                "INSERT OVERWRITE DIRECTORY '/user/facebook/tmp/pv_age_sum'\n" +
                "  SELECT pv_users.age, count(DISTINCT pv_users.userid) \n" +
                "  GROUP BY pv_users.age; \n" +
                "  ";
       // System.out.println(sqlparser.sqltext);

        assertTrue(sqlparser.parse() == 0);


         assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstinsert);
         TInsertSqlStatement insertSqlStatement = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
         assertTrue(insertSqlStatement.getHiveInsertType() == EHiveInsertType.overwriteTable);
         assertTrue(insertSqlStatement.getTargetTable().getFullName().equalsIgnoreCase("pv_gender_sum"));

         TSelectSqlStatement select  = insertSqlStatement.getSubQuery();
         TJoin join = select.joins.getJoin(0);
         assertTrue(join.getKind() == TBaseType.join_source_fake);
         assertTrue(join.getTable().getFullName().equalsIgnoreCase("pv_users"));
         assertTrue(select.getGroupByClause().getItems().getGroupByItem(0).toString().equalsIgnoreCase("pv_users.gender"));


         TInsertSqlStatement insertSqlStatement2 = insertSqlStatement.getMultiInsertStatements().get(0);
         assertTrue(insertSqlStatement2.getHiveInsertType() == EHiveInsertType.overwriteDirectory);
         assertTrue(insertSqlStatement2.getDirectoryName().toString().equalsIgnoreCase("'/user/facebook/tmp/pv_age_sum'"));

         select  = insertSqlStatement2.getSubQuery();
         join = select.joins.getJoin(0);
         assertTrue(join.getKind() == TBaseType.join_source_fake);
         assertTrue(join.getTable().getFullName().equalsIgnoreCase("pv_users"));
         assertTrue(select.getGroupByClause().getItems().getGroupByItem(0).toString().equalsIgnoreCase("pv_users.age"));

    }

    public void test2(){
       TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
       sqlparser.sqltext ="  FROM (\n" +
               "    FROM pv_users\n" +
               "    MAP ( pv_users.userid, pv_users.date )\n" +
               "    USING 'map_script'\n" +
               "    AS c1, c2, c3\n" +
               "    DISTRIBUTE BY c2\n" +
               "    SORT BY c2, c1) map_output\n" +
               "  INSERT OVERWRITE TABLE pv_users_reduced\n" +
               "    REDUCE ( map_output.c1, map_output.c2, map_output.c3 )\n" +
               "    USING 'reduce_script'\n" +
               "    AS date, count;" ;


       //System.out.println(sqlparser.sqltext);
       assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);

        assertTrue(insert.getHiveInsertType() == EHiveInsertType.overwriteTable);
        assertTrue(insert.tables.getTable(0).toString().equalsIgnoreCase("pv_users_reduced"));

        TSelectSqlStatement reduceSelect = insert.getSubQuery();
        assertTrue(reduceSelect.getTransformClause() != null);
        THiveTransformClause transformClause = reduceSelect.getTransformClause();
        assertTrue(transformClause.getTransformType() == THiveTransformClause.ETransformType.ettReduce);
        assertTrue(transformClause.getExpressionList().size() == 3);
        assertTrue(transformClause.getExpressionList().getExpression(0).toString().equalsIgnoreCase("map_output.c1"));
        assertTrue(transformClause.getExpressionList().getExpression(1).toString().equalsIgnoreCase("map_output.c2"));
        assertTrue(transformClause.getExpressionList().getExpression(2).toString().equalsIgnoreCase("map_output.c3"));
        assertTrue(transformClause.getUsingString().toString().equalsIgnoreCase("'reduce_script'"));

        TAliasClause aliasClause = transformClause.getAliasClause();
        assertTrue(aliasClause.getColumns().size() == 2);
        assertTrue(aliasClause.getColumns().getObjectName(0).toString().equalsIgnoreCase("date"));
        assertTrue(aliasClause.getColumns().getObjectName(1).toString().equalsIgnoreCase("count"));



//        FROM (
//                FROM pv_users
//                MAP ( pv_users.userid, pv_users.date )
//                USING 'map_script'
//        AS c1, c2, c3
//        DISTRIBUTE BY c2
//        SORT BY c2, c1) map_output

        // table is type of subquery of the above sql
        TTable table = reduceSelect.tables.getTable(0);
        assertTrue(table.getAliasClause().toString().equalsIgnoreCase("map_output"));

        assertTrue(table.getTableType() == ETableSource.subquery);
        TSelectSqlStatement select1 = table.getSubquery();
        TTable table1 = select1.tables.getTable(0);
        assertTrue(table1.getTableName().toString().equalsIgnoreCase("pv_users"));


        assertTrue(select1.getTransformClause() != null);
        transformClause = select1.getTransformClause();
        assertTrue(transformClause.getTransformType() == THiveTransformClause.ETransformType.ettMap);

        assertTrue(transformClause.getExpressionList().size() == 2);
        assertTrue(transformClause.getExpressionList().getExpression(0).toString().equalsIgnoreCase("pv_users.userid"));
        assertTrue(transformClause.getExpressionList().getExpression(1).toString().equalsIgnoreCase("pv_users.date"));
        assertTrue(transformClause.getUsingString().toString().equalsIgnoreCase("'map_script'"));

        aliasClause = transformClause.getAliasClause();
        assertTrue(aliasClause.getColumns().size() == 3);
        assertTrue(aliasClause.getColumns().getObjectName(0).toString().equalsIgnoreCase("c1"));
        assertTrue(aliasClause.getColumns().getObjectName(1).toString().equalsIgnoreCase("c2"));
        assertTrue(aliasClause.getColumns().getObjectName(2).toString().equalsIgnoreCase("c3"));

        TDistributeBy distributeBy = select1.getDistributeBy();
        assertTrue(distributeBy.getExpressionList().size() == 1);
        assertTrue(distributeBy.getExpressionList().getExpression(0).toString().equalsIgnoreCase("c2"));

        TSortBy sortBy = select1.getSortBy();
        assertTrue(sortBy.getItems().size() == 2);
        assertTrue(sortBy.getItems().getOrderByItem(0).getSortKey().toString().equalsIgnoreCase("c2"));
        assertTrue(sortBy.getItems().getOrderByItem(0).getSortType() == TBaseType.srtNone);
        assertTrue(sortBy.getItems().getOrderByItem(1).getSortKey().toString().equalsIgnoreCase("c1"));
        assertTrue(sortBy.getItems().getOrderByItem(0).getSortType() == TBaseType.srtNone);

    }

    public void test3(){
       TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
       sqlparser.sqltext = " FROM (\n" +
               "    FROM pv_users\n" +
               "    MAP pv_users.userid, pv_users.date\n" +
               "    USING 'map_script'\n" +
               "    AS dt, uid\n" +
               "    CLUSTER BY dt) map_output\n" +
               "  INSERT OVERWRITE TABLE pv_users_reduced\n" +
               "    REDUCE map_output.dt, map_output.uid\n" +
               "    USING 'reduce_script'\n" +
               "    AS date, count;\n";

       assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(insert.getHiveInsertType() == EHiveInsertType.overwriteTable);
        assertTrue(insert.tables.getTable(0).toString().equalsIgnoreCase("pv_users_reduced"));
        TSelectSqlStatement subquery = insert.getSubQuery();


       TTable table = subquery.tables.getTable(0);
       assertTrue(table.getAliasClause().toString().equalsIgnoreCase("map_output"));


      assertTrue(subquery.getTransformClause() != null);

      THiveTransformClause transformClause = subquery.getTransformClause();
      assertTrue(transformClause.getTransformType() == THiveTransformClause.ETransformType.ettReduce);
      assertTrue(transformClause.getExpressionList().size() == 2);
      assertTrue(transformClause.getExpressionList().getExpression(0).toString().equalsIgnoreCase("map_output.dt"));
      assertTrue(transformClause.getExpressionList().getExpression(1).toString().equalsIgnoreCase("map_output.uid"));
      assertTrue(transformClause.getUsingString().toString().equalsIgnoreCase("'reduce_script'"));

      TAliasClause aliasClause = transformClause.getAliasClause();
      assertTrue(aliasClause.getColumns().size() ==2 );
      assertTrue(aliasClause.getColumns().getObjectName(0).toString().equalsIgnoreCase("date"));
      assertTrue(aliasClause.getColumns().getObjectName(1).toString().equalsIgnoreCase("count"));
    }

    public void test4(){
       TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
       sqlparser.sqltext = "FROM (\n" +
               "    FROM src\n" +
               "    SELECT TRANSFORM(src.KEY, src.value) ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.TypedBytesSerDe'\n" +
               "    USING '/bin/cat'\n" +
               "    AS (tkey, tvalue) ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.TypedBytesSerDe'\n" +
               "    RECORDREADER 'org.apache.hadoop.hive.ql.exec.TypedBytesRecordReader'\n" +
               "  ) tmap\n" +
               "  INSERT OVERWRITE TABLE dest1 SELECT tkey, tvalue;";

       assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        //TInsertSqlStatement insert = (TInsertSqlStatement)select.getHiveBodyList().get(0);
        assertTrue(insert.getHiveInsertType() == EHiveInsertType.overwriteTable);
        assertTrue(insert.tables.getTable(0).toString().equalsIgnoreCase("dest1"));
        TSelectSqlStatement subquery = insert.getSubQuery();
        assertTrue(subquery.getResultColumnList().size() == 2);
        assertTrue(subquery.getResultColumnList().getResultColumn(0).toString().equalsIgnoreCase("tkey"));
        assertTrue(subquery.getResultColumnList().getResultColumn(1).toString().equalsIgnoreCase("tvalue"));


       TTable table = subquery.tables.getTable(0);
      assertTrue(table.getAliasClause().toString().equalsIgnoreCase("tmap"));


      assertTrue(table.getTableType() == ETableSource.subquery);
      TSelectSqlStatement subquery1 = table.getSubquery();
      assertTrue(subquery1.tables.getTable(0).toString().equalsIgnoreCase("src"));

      TSelectSqlStatement select1 = subquery1;

      assertTrue(select1.getTransformClause() != null);
      THiveTransformClause transformClause = select1.getTransformClause();
      assertTrue(transformClause.getTransformType() == THiveTransformClause.ETransformType.ettSelect);
      assertTrue(transformClause.getExpressionList().size() == 2);
      assertTrue(transformClause.getExpressionList().getExpression(0).toString().equalsIgnoreCase("src.KEY"));
      assertTrue(transformClause.getExpressionList().getExpression(1).toString().equalsIgnoreCase("src.value"));

     THiveRowFormat inrf = transformClause.getInRowFormat();
     assertTrue(inrf.getRowFormatType() == THiveRowFormat.ERowFormatType.serde);
     assertTrue(inrf.getRowFormatName().toString().equalsIgnoreCase("'org.apache.hadoop.hive.contrib.serde2.TypedBytesSerDe'"));

     THiveRowFormat outrf = transformClause.getOutRowFormat();
     assertTrue(outrf.getRowFormatType() == THiveRowFormat.ERowFormatType.serde);
     assertTrue(outrf.getRowFormatName().toString().equalsIgnoreCase("'org.apache.hadoop.hive.contrib.serde2.TypedBytesSerDe'"));

    THiveRecordReader outrr = transformClause.getOutRecordReader();
    assertTrue(outrr.getStringLiteral().toString().equalsIgnoreCase("'org.apache.hadoop.hive.ql.exec.TypedBytesRecordReader'"));

    TAliasClause aliasClause = transformClause.getAliasClause();
    assertTrue(aliasClause.getColumns().size() == 2);
    assertTrue(aliasClause.getColumns().getObjectName(0).toString().equalsIgnoreCase("tkey"));

    assertTrue(transformClause.getUsingString().toString().equalsIgnoreCase("'/bin/cat'"));
    }


    public void test5(){
       TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
       sqlparser.sqltext = "FROM page_view_stg pvs\n" +
               "INSERT OVERWRITE TABLE page_view PARTITION(dt='2008-06-08', country)\n" +
               "SELECT pvs.viewTime, pvs.userid, pvs.page_url, pvs.referrer_url, null, null, pvs.ip, pvs.country";

       assertTrue(sqlparser.parse() == 0);


       TInsertSqlStatement  insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
       assertTrue(insert.getHiveInsertType() == EHiveInsertType.overwriteTable);
       TTable table =  insert.getTargetTable();
       TPartitionExtensionClause partition = table.getPartitionExtensionClause();

       assertTrue(table.getTableName().toString().equalsIgnoreCase("page_view"));
       assertTrue(partition.getKeyValues().getExpression(0).getLeftOperand().toString().equalsIgnoreCase("dt"));

       TSelectSqlStatement subquery = insert.getSubQuery();
       assertTrue(subquery.getResultColumnList().size() == 8);

        //TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(subquery.tables.getTable(0).getTableName().toString().equalsIgnoreCase("page_view_stg"));

    }

     public void test6(){
       TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
       sqlparser.sqltext = "FROM pv_users\n" +
               "MAP pv_users.userid, pv_users.date\n" +
               "USING 'map_script'\n" +
               "AS dt, uid\n" +
               "CLUSTER BY dt;";

       assertTrue(sqlparser.parse() == 0);

       TSelectSqlStatement mapSelect = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
       assertTrue(mapSelect.tables.getTable(0).getTableName().toString().equalsIgnoreCase("pv_users"));
       assertTrue(mapSelect.getTransformClause() != null);
       THiveTransformClause transformClause = mapSelect.getTransformClause();
       assertTrue(transformClause.getTransformType() == THiveTransformClause.ETransformType.ettMap);
       assertTrue(transformClause.getExpressionList().getExpression(0).toString().equalsIgnoreCase("pv_users.userid"));
       assertTrue(transformClause.getUsingString().toString().equalsIgnoreCase("'map_script'"));
       assertTrue(mapSelect.getClusterBy().getExpressionList().getExpression(0).toString().equalsIgnoreCase("dt"));
     }
}
