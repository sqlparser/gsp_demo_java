package mdx;
/*
 * Date: 11-12-31
 */

import gudusoft.gsqlparser.*;
import junit.framework.TestCase;

public class testRawStatements extends TestCase {

    String rootdir = common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"mdx/";

    public void testMdx1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmdx);

        sqlparser.sqlfilename = rootdir+"case.sql";
        sqlparser.getrawsqlstatements();
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmdxselect);
        assertTrue(sqlparser.sqlstatements.get(1).sqlstatementtype == ESqlStatementType.sstmdxselect);

        sqlparser.sqlfilename = rootdir+"createmember.sql";
        sqlparser.getrawsqlstatements();
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmdxcreatemember);

        sqlparser.sqlfilename = rootdir+"createsessioncube.sql";
        sqlparser.getrawsqlstatements();
        assertTrue(sqlparser.sqlstatements.size() == 1);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmdxcreatesessioncube);

        sqlparser.sqlfilename = rootdir+"createsubcube.sql";
        sqlparser.getrawsqlstatements();
        assertTrue(sqlparser.sqlstatements.size() == 6);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmdxcreatesubcube);
        assertTrue(sqlparser.sqlstatements.get(1).sqlstatementtype == ESqlStatementType.sstmdxselect);
        assertTrue(sqlparser.sqlstatements.get(2).sqlstatementtype == ESqlStatementType.sstmdxcreatesubcube);
        assertTrue(sqlparser.sqlstatements.get(3).sqlstatementtype == ESqlStatementType.sstmdxselect);
        assertTrue(sqlparser.sqlstatements.get(4).sqlstatementtype == ESqlStatementType.sstmdxcreatesubcube);
        assertTrue(sqlparser.sqlstatements.get(5).sqlstatementtype == ESqlStatementType.sstmdxselect);

        sqlparser.sqlfilename = rootdir+"drillthrough.sql";
        sqlparser.getrawsqlstatements();
        assertTrue(sqlparser.sqlstatements.size() == 1);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmdxdrillthrough);

        sqlparser.sqlfilename = rootdir+"scope.sql";
        sqlparser.getrawsqlstatements();
        assertTrue(sqlparser.sqlstatements.size() == 2);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmdxscope);
        assertTrue(sqlparser.sqlstatements.get(1).sqlstatementtype == ESqlStatementType.sstmdxscope);
    }

}
