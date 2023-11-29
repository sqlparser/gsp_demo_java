package testSQLFiles;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testSparksqlFiles extends TestCase {
    public  void testSparksql(){
        parsefiles(EDbVendor.dbvsparksql,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"sparksql");
        parsefiles(EDbVendor.dbvsparksql, gspCommon.BASE_SQL_DIR_PRIVATE_ALLVERSIONS +"sparksql");
        parsefiles(EDbVendor.dbvsparksql, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"sparksql");
    }
}
