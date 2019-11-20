package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testCouchbaeFiles extends TestCase {

    public  void testCouchbase(){
        parsefiles(EDbVendor.dbvcouchbase,test.gspCommon.BASE_SQL_DIR+"couchbase");
    }

}

