package mysql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

public class testIntDisplayLength extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "create table inttest(intcol int(20), bigintcol bigint(20));";
        assertTrue(sqlparser.parse() == 0);
        TCreateTableSqlStatement createTable  = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TColumnDefinition columnDefinition = createTable.getColumnList().getColumn(0);
        assertTrue(columnDefinition.getColumnName().toString().equalsIgnoreCase("intcol"));
        assertTrue(columnDefinition.getDatatype().getDisplayLength().toString().equalsIgnoreCase("20"));
        columnDefinition = createTable.getColumnList().getColumn(1);
        assertTrue(columnDefinition.getColumnName().toString().equalsIgnoreCase("bigintcol"));
        assertTrue(columnDefinition.getDatatype().getDisplayLength().toString().equalsIgnoreCase("20"));
    }
}
