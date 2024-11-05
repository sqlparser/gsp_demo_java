package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testMySQLFiles extends TestCase {

    public  void testMySQL(){
        parseTest.parsefiles(EDbVendor.dbvmysql, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS + "mysql");
        parseTest.parsefiles(EDbVendor.dbvmysql, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA + "mysql");
        parseTest.parsefiles(EDbVendor.dbvmysql, gspCommon.BASE_SQL_DIR_PRIVATE_ALLVERSIONS + "mysql");
        parseTest.parsefiles(EDbVendor.dbvmysql, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA + "mysql");
//        parsefiles(EDbVendor.dbvmysql,"C:\\prg\\sofia2.0\\sofia\\big-ds\\testing");
//        parsefiles(EDbVendor.dbvmysql,"C:\\prg\\sofia2.0\\sofia\\big-ds\\training");
//        parsefiles(EDbVendor.dbvmysql,"C:\\prg\\sofia2.0\\sofia\\hotelrs-x");
//        parsefiles(EDbVendor.dbvmysql,"C:\\prg\\sofia2.0\\sofia\\taskfreak-b");
//        parsefiles(EDbVendor.dbvmysql,"C:\\prg\\sofia2.0\\sofia\\wordpress-s");
//          parsefiles(EDbVendor.dbvmysql,"C:\\prg\\sofia2.0\\sofia\\theorganizer-s");
    }


}
