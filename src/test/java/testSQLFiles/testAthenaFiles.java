package testSQLFiles;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testAthenaFiles  extends TestCase {

    public  void testAthena(){
        parsefiles(EDbVendor.dbvathena,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"athena");
        parsefiles(EDbVendor.dbvathena, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"athena");
        //parsefiles(EDbVendor.dbvathena, gspCommon.BASE_SQL_DIR_PRIVATE_ALLVERSIONS +"athena");
        parsefiles(EDbVendor.dbvathena, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA +"athena");
    }
}
