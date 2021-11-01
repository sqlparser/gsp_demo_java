package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testCouchbaeFiles extends TestCase {

    public  void testCouchbase(){
        parsefiles(EDbVendor.dbvcouchbase,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"couchbase");
    }

}

