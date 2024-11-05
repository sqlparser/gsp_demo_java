package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.commonTest.gspCommon;
import junit.framework.TestCase;

import static gudusoft.gsqlparser.sqlFilesTest.parseTest.parsefiles;

public class testCouchbaeFiles extends TestCase {

    public  void testCouchbase(){
        parsefiles(EDbVendor.dbvcouchbase, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS + "couchbase");
    }

}

