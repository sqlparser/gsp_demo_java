package hive;
/*
 * Date: 13-8-15
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TPartitionExtensionClause;
import gudusoft.gsqlparser.stmt.hive.THiveExportTable;
import junit.framework.TestCase;

public class testExportTable extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "export table department to 'hdfs_exports_location/department';";
        assertTrue(sqlparser.parse() == 0);

        THiveExportTable export = (THiveExportTable)sqlparser.sqlstatements.get(0);
        assertTrue(export.getTable().toString().equalsIgnoreCase("department"));
        assertTrue(export.getPath().toString().equalsIgnoreCase("'hdfs_exports_location/department'"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "export table employee partition (emp_country=\"in\", emp_state=\"ka\") to 'hdfs_exports_location/employee';";
        assertTrue(sqlparser.parse() == 0);

        THiveExportTable export = (THiveExportTable)sqlparser.sqlstatements.get(0);
        assertTrue(export.getTable().getTableName().toString().equalsIgnoreCase("employee"));
        TPartitionExtensionClause p = export.getTable().getPartitionExtensionClause();
        assertTrue(p.getKeyValues().size() == 2);
        assertTrue(p.getKeyValues().getExpression(0).getLeftOperand().toString().equalsIgnoreCase("emp_country"));
        assertTrue(p.getKeyValues().getExpression(0).getRightOperand().toString().equalsIgnoreCase("\"in\""));
        assertTrue(p.getKeyValues().getExpression(1).getLeftOperand().toString().equalsIgnoreCase("emp_state"));
        assertTrue(p.getKeyValues().getExpression(1).getRightOperand().toString().equalsIgnoreCase("\"ka\""));
        assertTrue(export.getPath().toString().equalsIgnoreCase("'hdfs_exports_location/employee'"));
    }

}
