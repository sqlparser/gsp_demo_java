package postgresql;
/*
 * Date: 11-5-18
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TLockingClause;
import gudusoft.gsqlparser.nodes.TObjectNameList;
import gudusoft.gsqlparser.nodes.TPTNodeList;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testLockingClause extends TestCase {

    public void testForUpdate(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select * from t for update of a,b";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TPTNodeList <TLockingClause> lockClauses = select.getLockingClauses();
        for (int i=0;i<lockClauses.size();i++){
            TLockingClause l = lockClauses.getElement(i);
            TObjectNameList objs = l.getLockObjects();
            assertTrue(objs.getObjectName(0).toString().equalsIgnoreCase("a"));
            assertTrue(objs.getObjectName(1).toString().equalsIgnoreCase("b"));
        }
    }

    public void testForReadOnly(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select * from t for read only";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TPTNodeList <TLockingClause> lockClauses = select.getLockingClauses();
        for (int i=0;i<lockClauses.size();i++){
            TLockingClause l = lockClauses.getElement(i);
            assertTrue(l.toString().equalsIgnoreCase("for read only"));
        }
    }

}
