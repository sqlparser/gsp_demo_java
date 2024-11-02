package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testSOQLFiles extends TestCase {
    public  void testSoql(){
        parseTest.parsefiles(EDbVendor.dbvsoql, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"salesforce_soql");
    }
}