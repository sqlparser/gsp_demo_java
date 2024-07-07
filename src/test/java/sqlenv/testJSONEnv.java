package sqlenv;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.*;
import junit.framework.TestCase;

public class testJSONEnv extends TestCase {

    static int objectsInDatabase(TSQLCatalog c, ESQLDataObjectType objectType){
        int result = 0;
        for(TSQLSchema s: c.getSchemaList() ) {
            for (TSQLSchemaObject o : s.getSchemaObjectList()) {
                if (o.getDataObjectType() == objectType) {
                    result++;
                }
            }
        }
        return result;
    }

    static int viewsInDatabase(TSQLCatalog c){
        int result = 0;
        for(TSQLSchema s: c.getSchemaList() ) {
            for (TSQLSchemaObject o : s.getSchemaObjectList()) {
                if (o.getDataObjectType() == ESQLDataObjectType.dotTable) {
                    TSQLTable table = (TSQLTable)o;
                    if (table.isView()) result++;
                }
            }
        }
        return result;
    }

    static int tablesInDatabase(TSQLCatalog c){
        int result = 0;
        for(TSQLSchema s: c.getSchemaList() ) {
            for (TSQLSchemaObject o : s.getSchemaObjectList()) {
                if (o.getDataObjectType() == ESQLDataObjectType.dotTable) {
                    TSQLTable table = (TSQLTable)o;
                    if (!table.isView()) result++;
                }
            }
        }
        return result;
    }
    TJSONSQLEnv sqlEnv;

    @Override
    protected void setUp() throws Exception {
       // System.out.println("Setting it up!");
        sqlEnv = new TJSONSQLEnv(EDbVendor.dbvmssql,common.gspCommon.BASE_SQL_DIR_PRIVATE+"/sqlflow/api_json/DBexport20191212.json");
        sqlEnv.initSQLEnv();
    }
    @Override
    protected void tearDown() throws Exception {
       // System.out.println("Running: tearDown");
    }

    public void test1() {
        int totalProcedure = 0,totalView = 0;
        assertTrue(sqlEnv.getCatalogList().size() == 7);

        for (TSQLCatalog c : sqlEnv.getCatalogList()) {
//            System.out.println("Database:" + c.getName() + "(schema:" + c.getSchemaList().size()
//                    + ",table+view:" + (objectsInDatabase(c, ESQLDataObjectType.dotTable))
//                    + ",view:" + (viewsInDatabase(c))
//                    + ",procedure:" + objectsInDatabase(c, ESQLDataObjectType.dotProcedure) + ")");

            totalView = totalView + viewsInDatabase(c);
            for (TSQLSchema s : c.getSchemaList()) {
                for (TSQLSchemaObject o : s.getSchemaObjectList()) {
                    if (o.getDataObjectType() == ESQLDataObjectType.dotProcedure) {
                        totalProcedure++;
                    }
                }
            }
        }
      //  System.out.println("total procedure:" + totalProcedure+", total view:"+totalView+", queries:"+(totalProcedure+totalView));
        assertTrue((totalProcedure+totalView) == 549);

   }

    public void testSearch1() {
        String tableName = "CrmPoc.SF.LAND_StatusChange";
        TSQLTable sqlTable = sqlEnv.searchTable(tableName);
        assertTrue(sqlTable != null);

        TSQLEnv.tableCollationCaseSensitive[EDbVendor.dbvmssql.ordinal()] = true;
        tableName = "DataMart.dbo.loaN";
        sqlTable = sqlEnv.searchTable(tableName);
        assertTrue(sqlTable == null);

        TSQLEnv.tableCollationCaseSensitive[EDbVendor.dbvmssql.ordinal()] = false;
        tableName = "DataMart.dbo.loaN";
        sqlTable = sqlEnv.searchTable(tableName);
        assertTrue(sqlTable != null);

        tableName = "DataMart.[SalesForce].[vwAccounts]";
       // tableName = "DataMart.SalesForce.vwAccounts";
        sqlTable = sqlEnv.searchTable(tableName);
        assertTrue(sqlTable != null);
//        for(TSQLColumn c: sqlTable.getColumnList()){
//            System.out.println(c.getSqlTable().getNameKeepCase()+"."+c.getNameKeepCase());
//        }
    }
}