package gettablecolumn;

import gudusoft.gsqlparser.sqlenv.TSQLCatalog;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.sqlenv.TSQLSchema;
import gudusoft.gsqlparser.sqlenv.TSQLTable;
import junit.framework.TestCase;
import gudusoft.gsqlparser.EDbVendor;

/*
* Date: 2010-5-27
* Time: 16:02:30
*/


public class testDbObject extends TestCase {

    public static void testOracle(){
        getObject go = new getObject(EDbVendor.dbvoracle);
        go.setSqlEnv(new TOracleEnv());
       // assertTrue(go.run(getObject.compareMode));
       // assertTrue(go.run(getObject.showMode));
    }

    public static void testSQLServer(){
        getObject go = new getObject(EDbVendor.dbvmssql);
        go.setSqlEnv(new TSQLServerEnv());
        assertTrue(go.run(getObject.compareMode));
    }

//    public  void testMetaDB(){
//        getObject go = new getObject(EDbVendor.dbvoracle);
//        go.showModeFile = "D:\\prg\\gsqlparser\\tmp\\demo.sql";
//        assertTrue(go.run(getObject.showMode));
//    }

}
