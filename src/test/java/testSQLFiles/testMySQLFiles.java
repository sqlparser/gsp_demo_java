package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testMySQLFiles extends TestCase {

    public  void testMySQL(){
        parseTest.parsefiles(EDbVendor.dbvmysql,common.gspCommon.BASE_SQL_DIR+"mysql");
        parseTest.parsefiles(EDbVendor.dbvmysql,common.gspCommon.BASE_SQL_DIR+"java/mysql");
//        parsefiles(EDbVendor.dbvmysql,"C:\\prg\\sofia2.0\\sofia\\big-ds\\testing");
//        parsefiles(EDbVendor.dbvmysql,"C:\\prg\\sofia2.0\\sofia\\big-ds\\training");
//        parsefiles(EDbVendor.dbvmysql,"C:\\prg\\sofia2.0\\sofia\\hotelrs-x");
//        parsefiles(EDbVendor.dbvmysql,"C:\\prg\\sofia2.0\\sofia\\taskfreak-b");
//        parsefiles(EDbVendor.dbvmysql,"C:\\prg\\sofia2.0\\sofia\\wordpress-s");
//          parsefiles(EDbVendor.dbvmysql,"C:\\prg\\sofia2.0\\sofia\\theorganizer-s");
    }


}
