package gudusoft.gsqlparser.snowflakeTest;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TAlterTableOption;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.stmt.TAlterTableStatement;
import junit.framework.TestCase;

public class TestAlterTable extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "ALTER TABLE t ADD COLUMN\n" +
                "    c1 number,\n" +
                "    c2 number;";
        assertTrue(sqlparser.parse() == 0);

        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        TAlterTableOption alterTableOption = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(alterTableOption.getOptionType() == EAlterTableOptionType.AddColumn);
        assertTrue(alterTableOption.getColumnDefinitionList().size() == 2);
        TColumnDefinition columnDefinition = alterTableOption.getColumnDefinitionList().getColumn(1);
        assertTrue(columnDefinition.getColumnName().toString().equalsIgnoreCase("c2"));
        assertTrue(columnDefinition.getDatatype().getDataType() == EDataType.number_t);
    }

    public void testRenameTable(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "alter table t2 rename to t3;";
        assertTrue(sqlparser.parse() == 0);

        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        TAlterTableOption alterTableOption = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(alterTableOption.getOptionType() == EAlterTableOptionType.RenameTable);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("t2"));
        assertTrue(alterTableOption.getNewTableName().toString().equalsIgnoreCase("t3"));
    }

    public void testSwapTable(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "alter table t2 swap with t3;";
        assertTrue(sqlparser.parse() == 0);

        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        TAlterTableOption alterTableOption = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(alterTableOption.getOptionType() == EAlterTableOptionType.swapWith);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("t2"));
        assertTrue(alterTableOption.getNewTableName().toString().equalsIgnoreCase("t3"));
    }
}
