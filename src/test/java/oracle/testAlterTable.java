package oracle;
/*
 * Date: 12-1-19
 */

import gudusoft.gsqlparser.EAlterTableOptionType;
import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TAlterTableOption;
import gudusoft.gsqlparser.nodes.TAlterTableOptionList;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.stmt.TAlterTableStatement;
import junit.framework.TestCase;

public class testAlterTable extends TestCase {

    public void testRename(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "ALTER TABLE FOO RENAME TO BAR";
        assertTrue(sqlparser.parse() == 0);

        TAlterTableStatement alterTableStatement = (TAlterTableStatement)sqlparser.sqlstatements.get(0);

        assertTrue(alterTableStatement.getTableName().toString().equalsIgnoreCase("foo"));

        TAlterTableOptionList l = alterTableStatement.getAlterTableOptionList();
        TAlterTableOption o = l.getAlterTableOption(0);
        assertTrue(o.getOptionType() == EAlterTableOptionType.RenameTable);
        assertTrue(o.getNewTableName().toString().equalsIgnoreCase("bar"));

    }

    public void testAddColumn(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "ALTER TABLE TS_TRS_SVS ADD TA_PRT_TRS_COR_TRS number(10)";
        assertTrue(sqlparser.parse() == 0);

        TAlterTableStatement alterTableStatement = (TAlterTableStatement)sqlparser.sqlstatements.get(0);

        assertTrue(alterTableStatement.getTableName().toString().equalsIgnoreCase("TS_TRS_SVS"));

        TAlterTableOptionList l = alterTableStatement.getAlterTableOptionList();
        TAlterTableOption o = l.getAlterTableOption(0);
        assertTrue(o.getOptionType() == EAlterTableOptionType.AddColumn);
        TColumnDefinition columnDefinition = o.getColumnDefinitionList().getColumn(0);
        assertTrue(columnDefinition.getColumnName().toString().equalsIgnoreCase("TA_PRT_TRS_COR_TRS"));
        assertTrue(columnDefinition.getDatatype().getDataType() == EDataType.number_t);

    }

}
