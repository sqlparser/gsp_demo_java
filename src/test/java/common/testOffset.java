package common;
/*
 * Date: 14-11-3
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.stmt.mssql.TMssqlSetRowCount;
import junit.framework.TestCase;

public class testOffset extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SET ROWCOUNT 4;";
        assertTrue(sqlparser.parse() == 0);

        TMssqlSetRowCount setRowCount = (TMssqlSetRowCount)sqlparser.sqlstatements.get(0);
        TSourceToken stStart = setRowCount.getStartToken();
        TSourceToken stEnd = setRowCount.getEndToken();
        assertTrue((stStart.lineNo == 1)&&(stStart.columnNo == 1)&&(stStart.offset == 0));
        assertTrue((stEnd.lineNo == 1)&&(stEnd.columnNo == 15)&&(stEnd.offset == 14));
//        System.out.println("x:"+stStart.lineNo+",y:"+stStart.columnNo+",offset:"+stStart.offset);
//        System.out.println("x:"+stEnd.lineNo+",y:"+stEnd.columnNo+",offset:"+stEnd.offset);

        sqlparser.sqltext = "SET ROWCOUNT 4;";
        assertTrue(sqlparser.parse() == 0);

        setRowCount = (TMssqlSetRowCount)sqlparser.sqlstatements.get(0);
        stStart = setRowCount.getStartToken();
        stEnd = setRowCount.getEndToken();
        assertTrue((stStart.lineNo == 1)&&(stStart.columnNo == 1)&&(stStart.offset == 0));
        assertTrue((stEnd.lineNo == 1)&&(stEnd.columnNo == 15)&&(stEnd.offset == 14));
//        System.out.println("x:"+stStart.lineNo+",y:"+stStart.columnNo+",offset:"+stStart.offset);
//        System.out.println("x:"+stEnd.lineNo+",y:"+stEnd.columnNo+",offset:"+stEnd.offset);
    }

}
