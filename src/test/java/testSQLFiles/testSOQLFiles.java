package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testSOQLFiles extends TestCase {
    public  void testSoql(){
        parsefiles(EDbVendor.dbvsoql,common.gspCommon.BASE_SQL_DIR+"salesforce_soql");
    }
}