package testSQLFiles;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testSOQLFiles extends TestCase {
    public  void testSoql(){
        parsefiles(EDbVendor.dbvsoql, gspCommon.BASE_SQL_DIR_PUBLIC +"java/salesforce_soql");
    }
}