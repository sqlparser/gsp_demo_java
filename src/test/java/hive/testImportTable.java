package hive;
/*
 * Date: 13-8-15
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

import gudusoft.gsqlparser.stmt.hive.THiveImportTable;
import junit.framework.TestCase;

public class testImportTable extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "import table department from 'hdfs_exports_location/department' \n" +
                "       location 'import_target_location/department';";
        assertTrue(sqlparser.parse() == 0);

        THiveImportTable importTable = (THiveImportTable)sqlparser.sqlstatements.get(0);
        assertTrue(!importTable.isExternal());
        assertTrue(importTable.getTable().toString().equalsIgnoreCase("department"));
        assertTrue(importTable.getPath().toString().equalsIgnoreCase("'hdfs_exports_location/department'"));
        assertTrue(importTable.getTableLocation().toString().equalsIgnoreCase("'import_target_location/department'"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "import from 'hdfs_exports_location/department';";
        assertTrue(sqlparser.parse() == 0);

        THiveImportTable importTable = (THiveImportTable)sqlparser.sqlstatements.get(0);
        assertTrue(!importTable.isExternal());
        assertTrue(importTable.getTable() == null);
        assertTrue(importTable.getPath().toString().equalsIgnoreCase("'hdfs_exports_location/department'"));
        assertTrue(importTable.getTableLocation() == null);
    }

}
