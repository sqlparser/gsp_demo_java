package test.gettablecolumn;

import junit.framework.TestCase;
import gudusoft.gsqlparser.EDbVendor;

/*
* Date: 2010-5-27
* Time: 16:02:30
*/


public class testDbObject extends TestCase {

    public static void testOracle(){
        getObject go = new getObject(EDbVendor.dbvoracle);
        assertTrue(go.run(getObject.compareMode));
    }

    public static void testSQLServer(){
        getObject go = new getObject(EDbVendor.dbvmssql);
        assertTrue(go.run(getObject.compareMode));
    }

//    public  void testMetaDB(){
//        getObject go = new getObject(EDbVendor.dbvoracle);
//        go.showModeFile = "D:\\prg\\gsqlparser\\tmp\\demo.sql";
//        assertTrue(go.run(getObject.showMode));
//    }

}
