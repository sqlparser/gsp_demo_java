package postgresql;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDropTableSqlStatement;
import junit.framework.TestCase;

public class testDropTable extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "DROP TABLE films, distributors;";
        assertTrue(sqlparser.parse() == 0);

        TDropTableSqlStatement dropTable = (TDropTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(dropTable.getTableName().toString().equalsIgnoreCase("films"));
        assertTrue(dropTable.getTableNameList().getObjectName(1).toString().equalsIgnoreCase("distributors"));

    }
}
