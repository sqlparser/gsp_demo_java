package testSQLFiles;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testImpalaFiles extends TestCase {
    public  void testImpala(){
        parsefiles(EDbVendor.dbvimpala,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"impala");
        parsefiles(EDbVendor.dbvimpala, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"impala");
        parsefiles(EDbVendor.dbvimpala, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA +"impala");
    }
}
