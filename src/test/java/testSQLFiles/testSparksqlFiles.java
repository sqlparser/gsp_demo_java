package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testSparksqlFiles extends TestCase {
    public  void testSparksql(){
        parsefiles(EDbVendor.dbvsparksql,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"sparksql");
    }
}
