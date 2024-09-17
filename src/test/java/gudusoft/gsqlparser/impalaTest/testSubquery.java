package gudusoft.gsqlparser.impalaTest;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testSubquery extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvimpala);
        sqlparser.sqltext = "create table test.concat \n" +
                "as   select concat(heroes.name,' vs. ',villains.name) as battle \n" +
                "from heroes \n" +
                "join villains  \n" +
                "where heroes.era = villains.era and heroes.planet = villains.planet";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement select =  createTable.getSubQuery();
        System.out.println(select.toString());
    }

}
