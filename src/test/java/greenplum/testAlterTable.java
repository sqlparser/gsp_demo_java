package greenplum;
/*
 * Date: 13-12-26
 */

import gudusoft.gsqlparser.EAlterTableOptionType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TAlterTableOption;
import gudusoft.gsqlparser.stmt.TAlterTableStatement;
import junit.framework.TestCase;

public class testAlterTable extends TestCase {

    public void testRenameColumn(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgreenplum);
        sqlparser.sqltext = "ALTER TABLE distributors RENAME COLUMN address TO city;";
        assertTrue(sqlparser.parse() == 0);
        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("distributors"));

        TAlterTableOption ato = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(ato.getOptionType() == EAlterTableOptionType.RenameColumn);
        assertTrue(ato.getColumnName().toString().equalsIgnoreCase("address"));
        assertTrue(ato.getNewColumnName().toString().equalsIgnoreCase("city"));
    }

    public void testSetSchema(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgreenplum);
        sqlparser.sqltext = "ALTER TABLE myschema.distributors SET SCHEMA yourschema;";
        assertTrue(sqlparser.parse() == 0);
        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("myschema.distributors"));

        TAlterTableOption ato = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(ato.getOptionType() == EAlterTableOptionType.setSchema);
        assertTrue(ato.getSchemaName().toString().equalsIgnoreCase("yourschema"));
    }
}
