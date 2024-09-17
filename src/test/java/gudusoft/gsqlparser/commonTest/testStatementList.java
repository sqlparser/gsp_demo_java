package gudusoft.gsqlparser.commonTest;

import gudusoft.gsqlparser.*;
import junit.framework.TestCase;

import java.util.Iterator;

public class testStatementList extends TestCase {

    public void test0(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "SELECT * FROM tab WHERE NOT EXISTS (SELECT 'x' FROM tab2);select a from t;select b from t2";
        assertTrue(sqlparser.parse() == 0);
        Iterator <TCustomSqlStatement> stmtList = sqlparser.sqlstatements.iterator();

        while (stmtList.hasNext()) {
            TCustomSqlStatement topLevelStmt = stmtList.next();
           // TSourceToken token = sqlparser.sqlstatements.next();
            //System.out.println(topLevelStmt);
        }

        for(TCustomSqlStatement stmt: sqlparser.sqlstatements){
           // System.out.println(stmt);
        }
    }

}
