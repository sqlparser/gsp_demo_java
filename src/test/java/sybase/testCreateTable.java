package sybase;
/*
 * Date: 13-8-30
 */

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

public class testCreateTable extends TestCase {

    public void test1(){
    TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsybase);
    sqlparser.sqltext = "create table temp_letter_log (\n" +
            "            alfs_cycle_date datetime null,\n" +
            "            alfs_doc_id char(5) null,\n" +
            "            alfs_count integer null)";
    assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("temp_letter_log"));
        assertTrue(createTable.getColumnList().size() == 3);
        TColumnDefinition columnDefinition = createTable.getColumnList().getColumn(0);
        assertTrue(columnDefinition.getColumnName().toString().equalsIgnoreCase("alfs_cycle_date"));
        assertTrue(columnDefinition.isNull());
        assertTrue(columnDefinition.getDatatype().getDataType() == EDataType.datetime_t);
    }

}
