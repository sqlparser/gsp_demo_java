package common;
/*
 * Date: 2010-11-4
 * Time: 17:21:00
 */

import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinList;
import gudusoft.gsqlparser.stmt.TDeleteSqlStatement;

public class testTDeleteSqlStatement extends TestCase {

    public void testTeradata1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "DELETE FROM TestTable1, TestTable2\n" +
                "WHERE TestTable1.mycirc = TestTable2.mycirc;";
        assertTrue(sqlparser.parse() == 0);

        TDeleteSqlStatement delete = (TDeleteSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = delete.getTargetTable();
        assertTrue(table.toString().equalsIgnoreCase("TestTable1"));

        TJoinList fromTables = delete.joins;
        assertTrue(fromTables.size() == 1);
        assertTrue(fromTables.getJoin(0).getTable().toString().equalsIgnoreCase("TestTable2"));
    }

    public void testTeradata2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "DELETE FROM employee AS managers\n" +
                "WHERE managers.deptno = employee.deptno\n" +
                "AND managers.jobtitle IN ('Manager', 'Vice Pres')\n" +
                "AND employee.yrsexp > managers.yrsexp;";
        assertTrue(sqlparser.parse() == 0);

        TDeleteSqlStatement delete = (TDeleteSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = delete.getTargetTable();
        assertTrue(table.toString().equalsIgnoreCase("employee"));
        assertTrue(table.getAliasClause().toString().equalsIgnoreCase("managers"));

        TJoinList fromTables = delete.joins;
        assertTrue(fromTables.size() == 0);
        
    }

    public void testMySQLDeleteToken(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "DELETE from score WHERE event_id = 14;";
        boolean b = sqlparser.parse() == 0;
        assertTrue(sqlparser.getErrormessage(),b);

        TDeleteSqlStatement deletestmt = (TDeleteSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(deletestmt.getDeleteToken().toString().equalsIgnoreCase("DELETE"));
    }

}
