package teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.teradata.TTeradataSetSession;
import junit.framework.TestCase;



public class testSetSession extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SET SESSION DATABASE database_name;";
        assertTrue(sqlparser.parse() == 0);
        System.out.println(sqlparser.sqlstatements.get(0).sqlstatementtype);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstteradatasetsession);
        TTeradataSetSession set = (TTeradataSetSession)sqlparser.sqlstatements.get(0);
        assertTrue(set.getSetSessionType() == TTeradataSetSession.ESetSessionType.database);
        assertTrue(set.getDatabaseName().toString().equalsIgnoreCase("database_name"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SS DATABASE database_name;";
        assertTrue(sqlparser.parse() == 0);
        System.out.println(sqlparser.sqlstatements.get(0).sqlstatementtype);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstteradatasetsession);
        TTeradataSetSession set = (TTeradataSetSession)sqlparser.sqlstatements.get(0);
        assertTrue(set.getSetSessionType() == TTeradataSetSession.ESetSessionType.database);
        assertTrue(set.getDatabaseName().toString().equalsIgnoreCase("database_name"));
    }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "DATABASE database_name;";
        assertTrue(sqlparser.parse() == 0);
        System.out.println(sqlparser.sqlstatements.get(0).sqlstatementtype);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstteradatasetsession);
        TTeradataSetSession set = (TTeradataSetSession)sqlparser.sqlstatements.get(0);
        assertTrue(set.getSetSessionType() == TTeradataSetSession.ESetSessionType.database);
        assertTrue(set.getDatabaseName().toString().equalsIgnoreCase("database_name"));
    }

}
