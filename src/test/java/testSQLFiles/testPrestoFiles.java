package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testPrestoFiles extends TestCase {
    public  void testPrestosql(){
        parsefiles(EDbVendor.dbvpresto,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"presto");
    }
}
