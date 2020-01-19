package snowflake;

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
}
