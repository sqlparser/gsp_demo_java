package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testSybaseFiles extends TestCase {
    public  void testSybase(){
        parsefiles(EDbVendor.dbvsybase,common.gspCommon.BASE_SQL_DIR+"sybase");
        parsefiles(EDbVendor.dbvsybase,common.gspCommon.BASE_SQL_DIR+"java/sybase");
    }
}
