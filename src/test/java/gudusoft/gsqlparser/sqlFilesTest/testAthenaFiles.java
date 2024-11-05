package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testAthenaFiles  extends TestCase {

    public  void testAthena(){
        parseTest.parsefiles(EDbVendor.dbvathena, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS + "athena");
        parseTest.parsefiles(EDbVendor.dbvathena, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA + "athena");
        //parsefiles(EDbVendor.dbvathena, gspCommon.BASE_SQL_DIR_PRIVATE_ALLVERSIONS +"athena");
        parseTest.parsefiles(EDbVendor.dbvathena, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA + "athena");
    }
}
