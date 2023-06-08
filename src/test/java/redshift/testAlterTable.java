package redshift;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TAlterTableOption;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.stmt.TAlterTableStatement;
import junit.framework.TestCase;


public class testAlterTable extends TestCase {

    public void testAppendFromTable() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "alter table DATAMAX_DMAX15246GRIFFINRS.STAGING.LINEAGE_ASTEST1 append from\n" +
                "            DATAMAX_DMAX15246GRIFFINRS.STAGING.LINEAGE_ASTEST1_TEMP_newdata ignoreextra;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstaltertable);
        TAlterTableStatement alterTableStmt = (TAlterTableStatement) sqlparser.sqlstatements.get(0);
        assertTrue(alterTableStmt.getTableName().toString().equalsIgnoreCase("DATAMAX_DMAX15246GRIFFINRS.STAGING.LINEAGE_ASTEST1"));
        assertTrue(alterTableStmt.getAlterTableOptionList().size() == 1);
        TAlterTableOption option = alterTableStmt.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(option.getOptionType() == EAlterTableOptionType.appendFrom);
        assertTrue(option.getSourceTableName().toString().equalsIgnoreCase("DATAMAX_DMAX15246GRIFFINRS.STAGING.LINEAGE_ASTEST1_TEMP_newdata"));
        //assertTrue(option.getColumnName().toString().endsWith("feedback_score"));
    }

    public void testAddColumn() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "alter table users\n" +
                "add column feedback_score int\n" +
                "default NULL;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstaltertable);
        TAlterTableStatement table = (TAlterTableStatement) sqlparser.sqlstatements.get(0);
        assertTrue(table.getAlterTableOptionList().size() == 1);
        TAlterTableOption option = table.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(option.getOptionType() == EAlterTableOptionType.AddColumn);

        TColumnDefinition columnDefinition = option.getColumnDefinitionList().getColumn(0);
        assertTrue(columnDefinition.getColumnName().toString().endsWith("feedback_score"));
        assertTrue(columnDefinition.getDatatype().getDataType() == EDataType.int_t);
        assertTrue(columnDefinition.getColumnAttributes().getColumnAttribute(0).getColumnAttributeType() == EColumnAttributeType.defaultValue);
        assertTrue(columnDefinition.getColumnAttributes().getColumnAttribute(0).getDefaultValue().toString().equalsIgnoreCase("NULL"));
    }

    public void testDropColumn() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "alter table users drop column feedback_score;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstaltertable);
        TAlterTableStatement table = (TAlterTableStatement) sqlparser.sqlstatements.get(0);
        assertTrue(table.getAlterTableOptionList().size() == 1);
        TAlterTableOption option = table.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(option.getOptionType() == EAlterTableOptionType.DropColumn);
        assertTrue(option.getColumnName().toString().endsWith("feedback_score"));
    }

    public void testRenameColumn() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "alter table venue\n" +
                "rename column venueseats to venuesize;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstaltertable);
        TAlterTableStatement table = (TAlterTableStatement) sqlparser.sqlstatements.get(0);
        assertTrue(table.getAlterTableOptionList().size() == 1);
        TAlterTableOption option = table.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(option.getOptionType() == EAlterTableOptionType.RenameColumn);
        assertTrue(option.getColumnName().toString().endsWith("venueseats"));
        assertTrue(option.getNewColumnName().toString().endsWith("venuesize"));
    }

    public void testRenameTable() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "alter table users\n" +
                "rename to users_bkup;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstaltertable);
        TAlterTableStatement table = (TAlterTableStatement) sqlparser.sqlstatements.get(0);
        assertTrue(table.getAlterTableOptionList().size() == 1);
        TAlterTableOption option = table.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(option.getOptionType() == EAlterTableOptionType.RenameTable);
        assertTrue(option.getNewTableName().toString().endsWith("users_bkup"));
    }

    public void testOwnTo() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "alter table vdate owner to vuser";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstaltertable);
        TAlterTableStatement table = (TAlterTableStatement) sqlparser.sqlstatements.get(0);
        assertTrue(table.getAlterTableOptionList().size() == 1);
        TAlterTableOption option = table.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(option.getOptionType() == EAlterTableOptionType.ownerTo);
        assertTrue(option.getNewOwnerName().toString().endsWith("vuser"));
    }
}