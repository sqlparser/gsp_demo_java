package hive;
/*
 * Date: 13-8-16
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TPartitionExtensionClause;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TAnalyzeStmt;
import junit.framework.TestCase;

public class testAnalyze extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "ANALYZE TABLE Table1 PARTITION(ds='2008-04-09', hr) COMPUTE STATISTICS noscan;";
        assertTrue(sqlparser.parse() == 0);

        TAnalyzeStmt analyzeTable = (TAnalyzeStmt)sqlparser.sqlstatements.get(0);
        assertTrue(analyzeTable.getTable().getFullName().equalsIgnoreCase("Table1"));
        TTable table = analyzeTable.getTable();
        TPartitionExtensionClause partition = table.getPartitionExtensionClause();
        assertTrue(partition.getKeyValues().size() == 2);
        assertTrue(partition.getKeyValues().getExpression(0).getLeftOperand().toString().equalsIgnoreCase("ds"));
        assertTrue(partition.getKeyValues().getExpression(0).getRightOperand().toString().equalsIgnoreCase("'2008-04-09'"));
        assertTrue(partition.getKeyValues().getExpression(1).getExpressionType() == EExpressionType.simple_object_name_t);
        assertTrue(partition.getKeyValues().getExpression(1).getObjectOperand().toString().equalsIgnoreCase("hr"));
    }
}
