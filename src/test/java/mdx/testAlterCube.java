package mdx;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.mdx.TMdxAlterCube;
import junit.framework.TestCase;

public class testAlterCube extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmdx);
        sqlparser.sqltext = "ALTER CUBE CURRENTCUBE UPDATE DIMENSION Measures, Default_Member = [__No measures defined]";
        int i = sqlparser.parse();
        assertTrue( i== 0);
        TMdxAlterCube alterCube = (TMdxAlterCube)sqlparser.sqlstatements.get(0);
        assertTrue(alterCube.isCurrentCube());
    }
}
