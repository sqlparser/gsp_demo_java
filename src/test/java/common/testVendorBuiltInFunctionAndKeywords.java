package common;
/*
 * Date: 14-9-2
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.util.functionChecker;
import junit.framework.TestCase;

public class testVendorBuiltInFunctionAndKeywords extends TestCase {

    public void testFunctionDBVersion(){
        EDbVendor db = EDbVendor.dbvoracle;
        int i = functionChecker.getAvailableDbVersions(db).size();
//        for(int k=0;k<i;k++)
//          System.out.println(functionChecker.getAvailableDbVersions(db).get(k));
     }

    public void testFunction(){
//        String fn = "avg", dbversion = "11.2" ;
//        EDbVendor dbVendor = EDbVendor.dbvoracle;
//
//        if (functionChecker.isBuiltInFunction(fn,dbVendor,dbversion)){
//            System.out.println(fn+" is built-in function in "+dbVendor.toString()+" of version " +dbversion);
//        }else{
//            System.out.println(fn+" is not built-in function in "+dbVendor.toString()+" of version " +dbversion);
//        }
     }

    public void testKeywords(){
//        EDbVendor db = EDbVendor.dbvmssql;
//        int i = keywordChecker.getAvailableDbVersions(db).size();
//        for(int k=0;k<i;k++)
//          System.out.print(keywordChecker.getAvailableDbVersions(db).get(k)+", ");
     }
}
