package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.commonTest.gspCommon;
import junit.framework.TestCase;

public class testPrestoFiles extends TestCase {
    public  void testPrestosql(){
        parseTest.parsefiles(EDbVendor.dbvpresto, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS + "presto");
    }
}
