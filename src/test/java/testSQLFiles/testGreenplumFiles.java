package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testGreenplumFiles extends TestCase {
    public  void testGreenplum(){
        parsefiles(EDbVendor.dbvgreenplum,common.gspCommon.BASE_SQL_DIR+"greenplum");
    }
}
