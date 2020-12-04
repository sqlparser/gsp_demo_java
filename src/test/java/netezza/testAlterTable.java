package netezza;
/*
 * Date: 14-7-29
 */

import gudusoft.gsqlparser.EAlterTableOptionType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TAlterTableOption;
import gudusoft.gsqlparser.stmt.TAlterTableStatement;
import junit.framework.TestCase;

public class testAlterTable extends TestCase {

    public void testRenameTable(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "ALTER TABLE X RENAME TO Y;";
        assertTrue(sqlparser.parse() == 0);

        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("X"));

        TAlterTableOption ato = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(ato.getOptionType() == EAlterTableOptionType.RenameTable);
        assertTrue(ato.getNewTableName().toString().equalsIgnoreCase("Y"));

        sqlparser.sqltext = "ALTER TABLE EVRST_HIST_NK..CLC_D_ACCOUNT_STATUS_HIER RENAME TO EVRST_HIST_NK..CLC_D_ACCOUNT_STATUS_HIER_ARCH";
        assertTrue(sqlparser.parse() == 0);

        alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("EVRST_HIST_NK..CLC_D_ACCOUNT_STATUS_HIER"));

        ato = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(ato.getOptionType() == EAlterTableOptionType.RenameTable);
        assertTrue(ato.getNewTableName().toString().equalsIgnoreCase("EVRST_HIST_NK..CLC_D_ACCOUNT_STATUS_HIER_ARCH"));


    }

}
