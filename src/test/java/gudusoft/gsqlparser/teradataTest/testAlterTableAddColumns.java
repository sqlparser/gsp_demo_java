package gudusoft.gsqlparser.teradataTest;

import gudusoft.gsqlparser.EAlterTableOptionType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TAlterTableOption;
import gudusoft.gsqlparser.stmt.TAlterTableStatement;
import junit.framework.TestCase;


public class testAlterTableAddColumns extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "ALTER TABLE DB_STAGE_AKG.T50307_S02_AGL_BAL_3 ADD AGL_BAL_AMT DECIMAL(18,4), ADD NETTING_AGL_BAL_AMT DECIMAL(18,4);";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstaltertable);
        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getAlterTableOptionList().size() == 2);
        TAlterTableOption alterTableOption = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(alterTableOption.getOptionType() == EAlterTableOptionType.AddColumn);
        assertTrue(alterTableOption.getColumnDefinitionList().getColumn(0).getColumnName().toString().equalsIgnoreCase("AGL_BAL_AMT"));
        alterTableOption = alterTable.getAlterTableOptionList().getAlterTableOption(1);
        assertTrue(alterTableOption.getOptionType() == EAlterTableOptionType.AddColumn);
        assertTrue(alterTableOption.getColumnDefinitionList().getColumn(0).getColumnName().toString().equalsIgnoreCase("NETTING_AGL_BAL_AMT"));
    }
}
