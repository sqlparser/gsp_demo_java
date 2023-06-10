package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EKeyReferenceType;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDropIndexSqlStatement;
import junit.framework.TestCase;

public class testDropIndex extends TestCase {

    public void testDropRestrictType(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "DROP INDEX IF EXISTS \"public\".\"batch_id_idx\" CASCADE;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstdropindex);
        TDropIndexSqlStatement dropIndex = (TDropIndexSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(dropIndex.getIndexName().toString().equalsIgnoreCase("\"public\".\"batch_id_idx\""));
        assertTrue(dropIndex.getDropRestrictType() == EKeyReferenceType.cascade);
    }


    public void testDropSchema(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "DROP UNIQUE INDEX XPKTOA_ACCT_DETL_AGE_T ON idw_rpt_data.TOA_ACCT_DETL_AGE_T;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstdropindex);
        TDropIndexSqlStatement dropIndex = (TDropIndexSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(dropIndex.getIndexName().toString().equalsIgnoreCase("XPKTOA_ACCT_DETL_AGE_T"));
        assertTrue(dropIndex.getTableName().toString().equalsIgnoreCase("idw_rpt_data.TOA_ACCT_DETL_AGE_T"));
    }

}
