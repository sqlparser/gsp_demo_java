package redshift;

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.redshift.TRedshiftPrepare;
import junit.framework.TestCase;


public class testPrepare extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "PREPARE prep_select_plan (char) \n" +
                "AS select * from temp1 where c1 = $1;";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftPrepare);
        TRedshiftPrepare prepare = (TRedshiftPrepare) sqlparser.sqlstatements.get(0);
        assertTrue(prepare.getPlanName().toString().equalsIgnoreCase("prep_select_plan"));
        assertTrue(prepare.getDatatypeList().size() == 1);
        assertTrue(prepare.getDatatypeList().getTypeName(0).getDataType() == EDataType.char_t);
        assertTrue(prepare.getStatement().sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement select = (TSelectSqlStatement)prepare.getStatement();
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("temp1"));

    }
}