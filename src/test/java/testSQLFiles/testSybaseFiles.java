package testSQLFiles;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testSybaseFiles extends TestCase {
    public  void testSybase(){
        parsefiles(EDbVendor.dbvsybase,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"sybase");
        parsefiles(EDbVendor.dbvsybase, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"sybase");
        parsefiles(EDbVendor.dbvsybase, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA +"sybase");
    }
}
