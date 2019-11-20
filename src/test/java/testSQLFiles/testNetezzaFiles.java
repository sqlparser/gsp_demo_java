package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testNetezzaFiles extends TestCase {


    public  void testNetezza(){
        parsefiles(EDbVendor.dbvnetezza,test.gspCommon.BASE_SQL_DIR+"netezza");
        parsefiles(EDbVendor.dbvnetezza,test.gspCommon.BASE_SQL_DIR+"java/netezza");
    }


}
