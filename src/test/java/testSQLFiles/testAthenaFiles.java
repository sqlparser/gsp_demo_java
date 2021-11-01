package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testAthenaFiles  extends TestCase {

    public  void testAthena(){
        parsefiles(EDbVendor.dbvathena,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"athena");
    }
}
