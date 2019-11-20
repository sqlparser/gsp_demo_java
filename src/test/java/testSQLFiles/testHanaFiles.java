package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testHanaFiles extends TestCase {
    public  void testHana(){
        parsefiles(EDbVendor.dbvhana,test.gspCommon.BASE_SQL_DIR+"hana");
    }
}
