package sybase;
/*
 * Date: 13-9-2
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TAliasClause;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TTableHint;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testHint extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsybase);
        sqlparser.sqltext = "select f from  db_policy..tpol_cover c (index ipol_cover) ";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = select.tables.getTable(0);
        assertTrue(table.getTableName().toString().equalsIgnoreCase("db_policy..tpol_cover"));
        TAliasClause aliasClause = table.getAliasClause();
        assertTrue(aliasClause != null);
        assertTrue(aliasClause.getAliasName().toString().equalsIgnoreCase("c"));
        TTableHint hint = table.getTableHintList().getElement(0);
        assertTrue(hint.getHint().toString().equalsIgnoreCase("ipol_cover"));
        //System.out.println(hint.getHint().toString());
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsybase);
        sqlparser.sqltext = "select ord_num from salesdetail\n" +
                "     (index salesdetail parallel 3)";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = select.tables.getTable(0);
        assertTrue(table.getTableName().toString().equalsIgnoreCase("salesdetail"));
        assertTrue(table.getTableHintList().size() == 2);
        TTableHint hint = table.getTableHintList().getElement(0);
        assertTrue(hint.getHint().toString().equalsIgnoreCase("salesdetail"));
        hint = table.getTableHintList().getElement(1);
        assertTrue(hint.getHintValue().toString().equalsIgnoreCase("3"));
    }
}
