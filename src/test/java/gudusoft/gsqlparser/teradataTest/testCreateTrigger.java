package gudusoft.gsqlparser.teradataTest;


import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TCreateTriggerStmt;
import junit.framework.TestCase;

public class testCreateTrigger extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "REPLACE TRIGGER IDW_APP_DPR.TR_UPD_WELL_DAILY_OVR AFTER \n" +
                "UPDATE ON IDW_APP_DPR.DPRUPDATES\n" +
                "REFERENCING NEW AS NEW_WDO\n" +
                "FOR EACH ROW\n" +
                "(\n" +
                "DELETE IDW_STAGE_RT.IDW_WELL_DAILY_OVERRIDE ALL;\n" +
                "\n" +
                "INSERT INTO IDW_STAGE_RT.IDW_WELL_DAILY_OVERRIDE (UWI, UWI_SIDETRACK,\n" +
                "ACTIVE_IND, PROD_DATE, OIL_TOTAL, OIL_BLOWCASE, GAS_HP, GAS_LP,\n" +
                "EFFECTIVE_DATE, EXPIRY_DATE, PPDM_GUID, SOURCE, ROW_CHANGED_BY,\n" +
                "ROW_CHANGED_DATE, ROW_CREATED_BY, ROW_CREATED_DATE,\n" +
                "ROW_QUALITY_ID, JOB_RUN_ID, GAS_FUEL, GAS_LIFT) \n" +
                "VALUES \n" +
                "(\n" +
                "NEW_WDO.WELLID, \n" +
                "'99', \n" +
                "'Y', \n" +
                "NEW_WDO.PRODDATE, \n" +
                "NEW_WDO.TANKOIL, \n" +
                "NEW_WDO.BCOIL,\n" +
                "NEW_WDO.HPGAS, \n" +
                "NEW_WDO.LPGAS, \n" +
                "CURRENT_TIMESTAMP(0), \n" +
                "CAST('9999-12-31 00:00:00'  AS TIMESTAMP(0)), \n" +
                "NULL, \n" +
                "'DPR',\n" +
                "NEW_WDO.ROW_CHANGED_BY, \n" +
                "CURRENT_TIMESTAMP(0), \n" +
                "NEW_WDO.ROW_CREATED_BY, \n" +
                "NEW_WDO.ROW_CREATED_DATE,\n" +
                "'192', \n" +
                "CURRENT_TIMESTAMP(FORMAT 'YYYYMMDDHHMISS') (VARCHAR(16)),\n" +
                "NEW_WDO.GASFUEL,\n" +
                "NEW_WDO.GASLIFT);\n" +
                "\n" +
                "INSERT INTO IDW_CORE_EXT_T.IDW_WELL_DAILY_OVERRIDE (UWI, UWI_SIDETRACK,\n" +
                "ACTIVE_IND, PROD_DATE, OIL_TOTAL, OIL_BLOWCASE, GAS_HP, GAS_LP,\n" +
                "EFFECTIVE_DATE, EXPIRY_DATE, PPDM_GUID, SOURCE, ROW_CHANGED_BY,\n" +
                "ROW_CHANGED_DATE, ROW_CREATED_BY, ROW_CREATED_DATE,\n" +
                "ROW_QUALITY_ID, JOB_RUN_ID, GAS_FUEL, GAS_LIFT)\n" +
                "SELECT  \n" +
                "S.UWI, \n" +
                "S.UWI_SIDETRACK, \n" +
                "S.ACTIVE_IND, \n" +
                "S.PROD_DATE, \n" +
                "S.OIL_TOTAL, \n" +
                "S.OIL_BLOWCASE,\n" +
                "S.GAS_HP, \n" +
                "S.GAS_LP, \n" +
                "S.EFFECTIVE_DATE, \n" +
                "S.EXPIRY_DATE, \n" +
                "S.PPDM_GUID, \n" +
                "S.SOURCE,\n" +
                "S.ROW_CHANGED_BY, \n" +
                "CURRENT_TIMESTAMP(0), \n" +
                "S.ROW_CREATED_BY, \n" +
                "S.ROW_CREATED_DATE,\n" +
                "S.ROW_QUALITY_ID, \n" +
                "CURRENT_TIMESTAMP(FORMAT 'YYYYMMDDHHMISS') (VARCHAR(16)),\n" +
                "S.GAS_FUEL, \n" +
                "S.GAS_LIFT\n" +
                "FROM     IDW_STAGE_RT.IDW_WELL_DAILY_OVERRIDE S\n" +
                "INNER JOIN \n" +
                "IDW_CORE_EXT_T.IDW_WELL_DAILY_OVERRIDE C\n" +
                "ON \n" +
                "S.UWI = C.UWI\n" +
                "AND S.PROD_DATE = C.PROD_DATE\n" +
                "AND C.ACTIVE_IND = 'Y'\n" +
                "WHERE \n" +
                "(\n" +
                "CASE WHEN (\n" +
                "((S.OIL_TOTAL IS NULL AND C.OIL_TOTAL IS NULL) OR (S.OIL_TOTAL = C.OIL_TOTAL)) \n" +
                "AND ((S.OIL_BLOWCASE IS NULL AND C.OIL_BLOWCASE IS NULL) OR (S.OIL_BLOWCASE = C.OIL_BLOWCASE)) \n" +
                "AND ((S.GAS_HP IS NULL AND C.GAS_HP IS NULL) OR (S.GAS_HP = C.GAS_HP)) \n" +
                "AND ((S.GAS_LP IS NULL AND C.GAS_LP IS NULL)  OR (S.GAS_LP = C.GAS_LP)) \n" +
                "AND ((S.GAS_FUEL IS NULL AND C.GAS_FUEL IS NULL)  OR (S.GAS_FUEL = C.GAS_FUEL))\n" +
                "AND ((S.GAS_LIFT IS NULL AND C.GAS_LIFT IS NULL)  OR (S.GAS_LIFT = C.GAS_LIFT))\n" +
                ") THEN 'N' \n" +
                "ELSE  'Y' \n" +
                "END ) = 'Y';\n" +
                "\n" +
                "\n" +
                "UPDATE  IDW_CORE_EXT_T.IDW_WELL_DAILY_OVERRIDE\n" +
                "FROM \n" +
                "(\n" +
                "SELECT \n" +
                "UWI, \n" +
                "UWI_SIDETRACK, \n" +
                "ACTIVE_IND, \n" +
                "PROD_DATE, \n" +
                "OIL_TOTAL, \n" +
                "OIL_BLOWCASE,\n" +
                "GAS_HP, \n" +
                "GAS_LP, \n" +
                "EFFECTIVE_DATE, \n" +
                "EXPIRY_DATE, \n" +
                "PPDM_GUID, \n" +
                "SOURCE,\n" +
                "ROW_CHANGED_BY, \n" +
                "ROW_CREATED_BY, \n" +
                "ROW_CREATED_DATE,\n" +
                "ROW_QUALITY_ID,\n" +
                "GAS_FUEL,\n" +
                "GAS_LIFT\n" +
                "FROM     IDW_STAGE_RT.IDW_WELL_DAILY_OVERRIDE)  S \n" +
                "SET \n" +
                "ACTIVE_IND = 'N',\n" +
                "EXPIRY_DATE = CURRENT_TIMESTAMP(0),\n" +
                "ROW_CHANGED_DATE = CURRENT_TIMESTAMP(0)\n" +
                "WHERE \n" +
                "S.UWI = IDW_CORE_EXT_T.IDW_WELL_DAILY_OVERRIDE.UWI\n" +
                "AND S.PROD_DATE = IDW_CORE_EXT_T.IDW_WELL_DAILY_OVERRIDE.PROD_DATE\n" +
                "AND IDW_CORE_EXT_T.IDW_WELL_DAILY_OVERRIDE.ACTIVE_IND = 'Y'\n" +
                "AND (\n" +
                "CASE WHEN (\n" +
                "((S.OIL_TOTAL IS NULL \n" +
                " AND IDW_CORE_EXT_T.IDW_WELL_DAILY_OVERRIDE.OIL_TOTAL IS NULL) OR (S.OIL_TOTAL = IDW_CORE_EXT_T.IDW_WELL_DAILY_OVERRIDE.OIL_TOTAL)) \n" +
                " AND ((S.OIL_BLOWCASE IS NULL AND IDW_CORE_EXT_T.IDW_WELL_DAILY_OVERRIDE.OIL_BLOWCASE IS NULL)  OR (S.OIL_BLOWCASE = IDW_CORE_EXT_T.IDW_WELL_DAILY_OVERRIDE.OIL_BLOWCASE)) \n" +
                " AND ((S.GAS_HP IS NULL AND IDW_CORE_EXT_T.IDW_WELL_DAILY_OVERRIDE.GAS_HP IS NULL) OR (S.GAS_HP = IDW_CORE_EXT_T.IDW_WELL_DAILY_OVERRIDE.GAS_HP)) \n" +
                " AND ((S.GAS_LP IS NULL AND IDW_CORE_EXT_T.IDW_WELL_DAILY_OVERRIDE.GAS_LP IS NULL)  OR (S.GAS_LP = IDW_CORE_EXT_T.IDW_WELL_DAILY_OVERRIDE.GAS_LP)) \n" +
                " AND ((S.GAS_FUEL IS NULL AND IDW_CORE_EXT_T.IDW_WELL_DAILY_OVERRIDE.GAS_FUEL IS NULL)  OR (S.GAS_FUEL = IDW_CORE_EXT_T.IDW_WELL_DAILY_OVERRIDE.GAS_FUEL))\n" +
                "AND ((S.GAS_LIFT IS NULL AND IDW_CORE_EXT_T.IDW_WELL_DAILY_OVERRIDE.GAS_LIFT IS NULL)  OR (S.GAS_LIFT = IDW_CORE_EXT_T.IDW_WELL_DAILY_OVERRIDE.GAS_LIFT))\n" +
                ") THEN 'N' \n" +
                "ELSE  'Y' \n" +
                "END ) = 'Y';\n" +
                ");";

        //System.out.println(sqlparser.sqltext);

        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatetrigger);
        TCreateTriggerStmt createTriggerStmt = (TCreateTriggerStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createTriggerStmt.getTriggerName().toString().equalsIgnoreCase("IDW_APP_DPR.TR_UPD_WELL_DAILY_OVR"));
        assertTrue(createTriggerStmt.getTables().getTable(0).toString().endsWith("IDW_APP_DPR.DPRUPDATES"));

        // dml trigger
        TTriggeringClause triggeringClause = createTriggerStmt.getTriggeringClause();
        assertTrue(triggeringClause instanceof TSimpleDmlTriggerClause);
        TSimpleDmlTriggerClause dmlTriggerClause = (TSimpleDmlTriggerClause)triggeringClause;
        assertTrue(dmlTriggerClause.getActionTime() == ETriggerActionTime.tatAfter);
        assertTrue(dmlTriggerClause.getGranularity() == ETriggerGranularity.forEachRow);

        // dml event clause
        TDmlEventClause dmlEventClause = (TDmlEventClause)dmlTriggerClause.getEventClause();
        TDmlEventItem dmlEventItem = (TDmlEventItem)dmlEventClause.getEventItems().get(0);
        assertTrue(dmlEventItem.getDmlType() == ESqlStatementType.sstupdate);
        assertTrue(dmlEventItem.getEventName().equalsIgnoreCase("update"));

       // referencing clause
        TTriggerReferencingClause referencingClause = dmlTriggerClause.getReferencingClause();
        assertTrue(referencingClause.getReferencingItems().size() == 1);
        TTriggerReferencingItem referencingItem = referencingClause.getReferencingItems().get(0);
        assertTrue(referencingItem.getTriggerReferencingType() == ETriggerReferencingType.rtNew);
        assertTrue(referencingItem.getCorrelationName().toString().equalsIgnoreCase("NEW_WDO"));

        assertTrue(createTriggerStmt.getBodyStatements().size() == 4);
        assertTrue(createTriggerStmt.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstdelete);
        assertTrue(createTriggerStmt.getBodyStatements().get(1).sqlstatementtype == ESqlStatementType.sstinsert);
        assertTrue(createTriggerStmt.getBodyStatements().get(2).sqlstatementtype == ESqlStatementType.sstinsert);
        assertTrue(createTriggerStmt.getBodyStatements().get(3).sqlstatementtype == ESqlStatementType.sstupdate);

    }
}
