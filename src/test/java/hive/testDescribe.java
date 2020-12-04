package hive;
/*
 * Date: 13-8-16
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TPartitionExtensionClause;
import gudusoft.gsqlparser.nodes.hive.EHiveDescOption;
import gudusoft.gsqlparser.nodes.hive.EHiveDescribleType;
import gudusoft.gsqlparser.nodes.hive.THiveDescTablePartition;
import gudusoft.gsqlparser.stmt.hive.THiveDescribe;
import junit.framework.TestCase;

public class testDescribe extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "DESCRIBE EXTENDED TABLE1;";
        assertTrue(sqlparser.parse() == 0);

        THiveDescribe describe = (THiveDescribe)sqlparser.sqlstatements.get(0);
        assertTrue(describe.getDescribleType() == EHiveDescribleType.dtTablePartition);
        assertTrue(describe.getDescOption() == EHiveDescOption.doExtended);
        THiveDescTablePartition tablePartition = describe.getTablePartition();
        assertTrue(tablePartition.getPartition() == null);
        assertTrue(tablePartition.getDescTabType().toString().equalsIgnoreCase("TABLE1"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "DESCRIBE EXTENDED TABLE1 PARTITION(ds='2008-04-09', hr=11);";
        assertTrue(sqlparser.parse() == 0);

        THiveDescribe describe = (THiveDescribe)sqlparser.sqlstatements.get(0);
        assertTrue(describe.getDescribleType() == EHiveDescribleType.dtTablePartition);
        assertTrue(describe.getDescOption() == EHiveDescOption.doExtended);
        THiveDescTablePartition tablePartition = describe.getTablePartition();
        TPartitionExtensionClause  partition = tablePartition.getPartition();
        assertTrue(partition.getKeyValues().size() == 2);
        assertTrue(partition.getKeyValues().getExpression(0).getLeftOperand().toString().equalsIgnoreCase("ds"));
        assertTrue(partition.getKeyValues().getExpression(0).getRightOperand().toString().equalsIgnoreCase("'2008-04-09'"));
        assertTrue(partition.getKeyValues().getExpression(1).getLeftOperand().toString().equalsIgnoreCase("hr"));
        assertTrue(partition.getKeyValues().getExpression(1).getRightOperand().toString().equalsIgnoreCase("11"));
        assertTrue(tablePartition.getDescTabType().toString().equalsIgnoreCase("TABLE1"));
    }

}
