package gettablecolumn;

import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;


public class testSQLServer extends TestCase {

    static void doTest(String inputQuery, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvmssql);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.runText(inputQuery);
        //System.out.println(inputQuery);
       // System.out.println(getTableColumn.outList.toString().trim());
        assertTrue(getTableColumn.outList.toString().trim().equalsIgnoreCase(desireResult));
       // System.out.println(desireResult);
    }

    public static void testSearchTopLevel() {
        doTest("select\n" +
                        "    case\n" +
                        "        c.[SIZE] when 3 then (\n" +
                        "        select\n" +
                        "            name\n" +
                        "        from\n" +
                        "            (\n" +
                        "            select\n" +
                        "                ename as name\n" +
                        "            from\n" +
                        "                TestCatalog.TestSchema.TestTableEmployee tte\n" +
                        "            where\n" +
                        "                tte.[department id] = c.deptno ) a)\n" +
                        "        when 5 Then c.[Department Location]\n" +
                        "    end deptNameLoc\n" +
                        "from\n" +
                        "    TestCatalog.TestSchema.TestTableDept c",
                "Tables:\n" +
                        "TestCatalog.TestSchema.TestTableDept\n" +
                        "TestCatalog.TestSchema.TestTableEmployee\n" +
                        "\n" +
                        "Fields:\n" +
                        "TestCatalog.TestSchema.TestTableDept.[Department Location]\n" +
                        "TestCatalog.TestSchema.TestTableDept.[SIZE]\n" +
                        "TestCatalog.TestSchema.TestTableDept.deptno\n" +
                        "TestCatalog.TestSchema.TestTableEmployee.[department id]\n" +
                        "TestCatalog.TestSchema.TestTableEmployee.ename");
    }

    public static void testColumnInSubquery() {
        doTest("SELECT\n" +
                        "     [FacilityAccountID] = CAST(enc.FacilityAccountID AS VARCHAR(255))\n" +
                        "    ,[RowSourceDSC] = CAST('Cerner' AS VARCHAR(255))\n" +
                        "    ,[AttendingProviderID] = CAST(CAST(p.PersonnelID AS NUMERIC(38,0)) AS VARCHAR(255))\n" +
                        "    ,[EDWLastModifiedDTS] = CAST(p.EDWLastModifiedDTS AS DATETIME2(7))\n" +
                        "FROM Shared.Clinical.EncounterLink AS enc\n" +
                        "CROSS APPLY\n" +
                        "(\n" +
                        "    SELECT TOP 1\n" +
                        "        a.PersonnelID\n" +
                        "       ,a.EDWLastModifiedDTS\n" +
                        "    FROM Cerner.Encounter.EncounterToPersonnelRelationship AS a\n" +
                        "    WHERE a.EncounterID = enc.SourcePatientEncounterID\n" +
                        "      AND a.ActiveStatusCVCD = 506\n" +
                        "      AND a.EncounterPersonnelRelationshipCVCD = 1206\n" +
                        "      AND a.ContributorSystemCVCD = 16048\n" +
                        "    ORDER BY\n" +
                        "        a.UpdateDTS DESC\n" +
                        ") AS p",
                "Tables:\n" +
                        "Cerner.Encounter.EncounterToPersonnelRelationship\n" +
                        "Shared.Clinical.EncounterLink\n" +
                        "\nFields:\n" +
                        "Cerner.Encounter.EncounterToPersonnelRelationship.ActiveStatusCVCD\n" +
                        "Cerner.Encounter.EncounterToPersonnelRelationship.ContributorSystemCVCD\n" +
                        "Cerner.Encounter.EncounterToPersonnelRelationship.EDWLastModifiedDTS\n" +
                        "Cerner.Encounter.EncounterToPersonnelRelationship.EncounterID\n" +
                        "Cerner.Encounter.EncounterToPersonnelRelationship.EncounterPersonnelRelationshipCVCD\n" +
                        "Cerner.Encounter.EncounterToPersonnelRelationship.PersonnelID\n" +
                        "Cerner.Encounter.EncounterToPersonnelRelationship.UpdateDTS\n" +
                        "Shared.Clinical.EncounterLink.FacilityAccountID\n" +
                        "Shared.Clinical.EncounterLink.SourcePatientEncounterID");
    }


    public static void testCTE() {
        doTest("WITH DataCTE\n" +
                        "AS\n" +
                        "(\n" +
                        "SELECT \n" +
                        "\t ExecCountsCTE.Calendar_Year \n" +
                        "\t,ExecCountsCTE.Calendar_Quarter  \n" +
                        "\t,ExecCountsCTE.Calendar_Quarter_Name \n" +
                        "\t,ExecCountsCTE.Calendar_Month  \n" +
                        "\t,ExecCountsCTE.Calendar_Month_Name \n" +
                        "\t,ErrorCountsCTE.ErrorSeverityCategory \n" +
                        "\t,SUM(ExecCountsCTE.ScriptExecCount)\t\t\t\t\t\t\t\t\t\t\t\t\t\tAS ScriptExecCount\n" +
                        "\t,SUM(ISNULL(ErrorCountsCTE.ErrorCount,0))\t\t\t\t\t\t\t\t\t\t\t\tAS ScriptErrorCount\n" +
                        "\t,CAST(CAST(SUM(ISNULL(ErrorCountsCTE.ErrorCount,0))  AS DECIMAL(20,2))\n" +
                        "\t/ CAST(SUM(ExecCountsCTE.ScriptExecCount)  AS DECIMAL(20,2))\n" +
                        "\tAS DECIMAL(20,9))\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tAS PercentErrors\n" +
                        "FROM \n" +
                        "\t(\n" +
                        "\tSELECT \n" +
                        "\t\t dbo.TealeafPerformance.Calendar_Year \n" +
                        "\t\t,dbo.TealeafPerformance.Calendar_Quarter  \n" +
                        "\t\t,dbo.TealeafPerformance.Calendar_Quarter_Name \n" +
                        "\t\t,dbo.TealeafPerformance.Calendar_Month\n" +
                        "\t\t,dbo.TealeafPerformance.Calendar_Month_Name\n" +
                        "\t\t,COUNT(*) AS ScriptExecCount\n" +
                        "\tFROM \n" +
                        "\t\tdbo.TealeafPerformance\n" +
                        "\tWHERE\n" +
                        "\t\tdbo.TealeafPerformance.PerformanceType = 'RoundTrip'\n" +
                        "\tAND dbo.TealeafPerformance.Application = 'CLAS'\n" +
                        "\tAND dbo.TealeafPerformance.Date >= @BegDate\n" +
                        "\tAND dbo.TealeafPerformance.Date < @EndDate\n" +
                        "\tAND dbo.TealeafPerformance.Date >= '2014-04-01' --valid data only beginning here\n" +
                        "\tGROUP BY\n" +
                        "\t\t dbo.TealeafPerformance.Calendar_Year \n" +
                        "\t\t,dbo.TealeafPerformance.Calendar_Quarter  \n" +
                        "\t\t,dbo.TealeafPerformance.Calendar_Quarter_Name \n" +
                        "\t\t,dbo.TealeafPerformance.Calendar_Month\n" +
                        "\t\t,dbo.TealeafPerformance.Calendar_Month_Name\n" +
                        "\t) ExecCountsCTE\n" +
                        "\tLEFT OUTER JOIN \n" +
                        "\t(\n" +
                        "\tSELECT \n" +
                        "\t\t dbo.Tealeaf.Calendar_Year \n" +
                        "\t\t,dbo.Tealeaf.Calendar_Quarter  \n" +
                        "\t\t,dbo.Tealeaf.Calendar_Quarter_Name \n" +
                        "\t\t,dbo.Tealeaf.Calendar_Month\n" +
                        "\t\t,dbo.Tealeaf.Calendar_Month_Name\n" +
                        "\t\t,CASE\n" +
                        "\t\t\tWHEN (dbo.Tealeaf.ErrorType >= '500' OR dbo.Tealeaf.ErrorType IN ('Legacy','.NET')) THEN 'High Severity'\n" +
                        "\t\t\tWHEN (dbo.Tealeaf.ErrorType >= '400' AND dbo.Tealeaf.ErrorType < '500') THEN 'Low Severity'\n" +
                        "\t\t\tELSE 'Unknown'\n" +
                        "\t\t END AS ErrorSeverityCategory\n" +
                        "\t\t,SUM(ISNULL(dbo.Tealeaf.ErrorCount,0)) AS ErrorCount\n" +
                        "\tFROM \n" +
                        "\t\tdbo.Tealeaf\n" +
                        "\tWHERE\n" +
                        "\t\tdbo.Tealeaf.Application = 'CLAS'\n" +
                        "\tAND dbo.Tealeaf.Date >= @BegDate\n" +
                        "\tAND dbo.Tealeaf.Date < @EndDate\n" +
                        "\tAND dbo.Tealeaf.Date >= '2014-04-01' --valid data only beginning here\n" +
                        "\tGROUP BY\n" +
                        "\t\t dbo.Tealeaf.Calendar_Year \n" +
                        "\t\t,dbo.Tealeaf.Calendar_Quarter  \n" +
                        "\t\t,dbo.Tealeaf.Calendar_Quarter_Name \n" +
                        "\t\t,dbo.Tealeaf.Calendar_Month\n" +
                        "\t\t,dbo.Tealeaf.Calendar_Month_Name\n" +
                        "\t\t,CASE\n" +
                        "\t\t\tWHEN (dbo.Tealeaf.ErrorType >= '500' OR dbo.Tealeaf.ErrorType IN ('Legacy','.NET')) THEN 'High Severity'\n" +
                        "\t\t\tWHEN (dbo.Tealeaf.ErrorType >= '400' AND dbo.Tealeaf.ErrorType < '500') THEN 'Low Severity'\n" +
                        "\t\t\tELSE 'Unknown'\n" +
                        "\t\t END \n" +
                        "\t) ErrorCountsCTE\n" +
                        "\t\tON\tErrorCountsCTE.Calendar_Year = ExecCountsCTE.Calendar_Year\n" +
                        "\t\tAND\tErrorCountsCTE.Calendar_Month = ExecCountsCTE.Calendar_Month\n" +
                        "GROUP BY\n" +
                        "\t ExecCountsCTE.Calendar_Year \n" +
                        "\t,ExecCountsCTE.Calendar_Quarter  \n" +
                        "\t,ExecCountsCTE.Calendar_Quarter_Name \n" +
                        "\t,ExecCountsCTE.Calendar_Month  \n" +
                        "\t,ExecCountsCTE.Calendar_Month_Name \n" +
                        "\t,ErrorCountsCTE.ErrorSeverityCategory \n" +
                        ")\n" +
                        "SELECT\n" +
                        "\t DataCTE.Calendar_Year\n" +
                        "\t,DataCTE.Calendar_Quarter  \n" +
                        "\t,DataCTE.Calendar_Quarter_Name  \n" +
                        "\t,DataCTE.Calendar_Month  \n" +
                        "\t,DataCTE.Calendar_Month_Name \n" +
                        "\t,DataCTE.ErrorSeverityCategory \n" +
                        "\t,DataCTE.ScriptExecCount\n" +
                        "\t,DataCTE.ScriptErrorCount\n" +
                        "\t,DataCTE.PercentErrors\n" +
                        "\t,PercentTotalApply.TotalPercent\n" +
                        "\t,(100.0 - (PercentTotalApply.TotalPercent * 100.0))  AS SuccessPercentile\n" +
                        "FROM\n" +
                        "\tDataCTE\n" +
                        "\tCROSS APPLY\n" +
                        "\t\t(\n" +
                        "\t\tSELECT\n" +
                        "\t\t\t DataCTE2.Calendar_Year \n" +
                        "\t\t\t,DataCTE2.Calendar_Month  \n" +
                        "\t\t\t,DataCTE2.Calendar_Month_Name \n" +
                        "\t\t\t,SUM(DataCTE2.PercentErrors) AS TotalPercent\n" +
                        "\t\tFROM\n" +
                        "\t\t\tDataCTE DataCTE2\n" +
                        "\t\tWHERE\n" +
                        "\t\t\tDataCTE2.Calendar_Year\t\t\t= DataCTE.Calendar_Year\t\n" +
                        "\t\tAND\tDataCTE2.Calendar_Month\t\t\t= DataCTE.Calendar_Month\n" +
                        "\t\tGROUP BY\n" +
                        "\t\t\t DataCTE2.Calendar_Year \n" +
                        "\t\t\t,DataCTE2.Calendar_Month  \n" +
                        "\t\t\t,DataCTE2.Calendar_Month_Name \n" +
                        "\t\t) PercentTotalApply",
                "Tables:\n" +
                        "dbo.Tealeaf\n" +
                        "dbo.TealeafPerformance\n" +
                        "\nFields:\n" +
                        "dbo.Tealeaf.Application\n" +
                        "dbo.Tealeaf.Calendar_Month\n" +
                        "dbo.Tealeaf.Calendar_Month_Name\n" +
                        "dbo.Tealeaf.Calendar_Quarter\n" +
                        "dbo.Tealeaf.Calendar_Quarter_Name\n" +
                        "dbo.Tealeaf.Calendar_Year\n" +
                        "dbo.Tealeaf.Date\n" +
                        "dbo.Tealeaf.ErrorCount\n" +
                        "dbo.Tealeaf.ErrorType\n" +
                        "dbo.TealeafPerformance.Application\n" +
                        "dbo.TealeafPerformance.Calendar_Month\n" +
                        "dbo.TealeafPerformance.Calendar_Month_Name\n" +
                        "dbo.TealeafPerformance.Calendar_Quarter\n" +
                        "dbo.TealeafPerformance.Calendar_Quarter_Name\n" +
                        "dbo.TealeafPerformance.Calendar_Year\n" +
                        "dbo.TealeafPerformance.Date\n" +
                        "dbo.TealeafPerformance.PerformanceType");
    }


    public static void testTableFunction() {
//        System.out.println("CREATE VIEW [dbo].[ERRORS]\n" +
//                "AS\n" +
//                "WITH DESC_CTE\n" +
//                "AS\n" +
//                "    (\n" +
//                "        SELECT  rid\n" +
//                "               ,POS       = ID\n" +
//                "               ,c_desc = LTRIM( RTRIM( value ))\n" +
//                "          FROM  [dbo].[reasons]\n" +
//                "          CROSS APPLY\n" +
//                "                (\n" +
//                "                    SELECT  ID = ROW_NUMBER() OVER (PARTITION BY reasons_ID1\n" +
//                "                                                        ORDER BY\n" +
//                "                                                   reasons_ID2\n" +
//                "                                                   )\n" +
//                "                           ,value\n" +
//                "                      FROM  STRING_SPLIT(REPLACE(c_desc ,'|~|',CHAR(7)),CHAR(7)) PM\n" +
//                "                ) CR\n" +
//                "                                            WHERE [IsDeleted] ='0'\n" +
//                "                                            )\n" +
//                "SELECT [derid],\n" +
//                "       POS =ISNULL(CR.[POS],1) ,\n" +
//                "       [errorcode],\n" +
//                "       [c_desc]= CAST(NULLIF(CR.c_desc,'') AS VARCHAR(300))\n" +
//                "FROM dbo.d_errors CE\n" +
//                "LEFT JOIN DESC_CTE CR\n" +
//                "ON CR.reasons_ID =CE.errorcode\n" +
//                "WHERE 1 = 1\n" +
//                "      AND CE.IsDeleted = 0;");
        doTest("CREATE VIEW [dbo].[ERRORS]\n" +
                        "AS\n" +
                        "WITH DESC_CTE\n" +
                        "AS\n" +
                        "    (\n" +
                        "        SELECT  rid\n" +
                        "               ,POS       = ID\n" +
                        "               ,c_desc = LTRIM( RTRIM( value ))\n" +
                        "          FROM  [dbo].[reasons]\n" +
                        "          CROSS APPLY\n" +
                        "                (\n" +
                        "                    SELECT  ID = ROW_NUMBER() OVER (PARTITION BY reasons_ID1\n" +
                        "                                                        ORDER BY\n" +
                        "                                                   reasons_ID2\n" +
                        "                                                   )\n" +
                        "                           ,value\n" +
                        "                      FROM  STRING_SPLIT(REPLACE(c_desc ,'|~|',CHAR(7)),CHAR(7)) PM\n" +
                        "                ) CR\n" +
                        "                                            WHERE [IsDeleted] ='0'\n" +
                        "                                            )\n" +
                        "SELECT [derid],\n" +
                        "       POS =ISNULL(CR.[POS],1) ,\n" +
                        "       [errorcode],\n" +
                        "       [c_desc]= CAST(NULLIF(CR.c_desc,'') AS VARCHAR(300))\n" +
                        "FROM dbo.d_errors CE\n" +
                        "LEFT JOIN DESC_CTE CR\n" +
                        "ON CR.reasons_ID =CE.errorcode\n" +
                        "WHERE 1 = 1\n" +
                        "      AND CE.IsDeleted = 0;",
                "Tables:\n" +
                        "[dbo].[reasons]\n" +
                        "dbo.d_errors\n" +
                        "\n" +
                        "Fields:\n" +
                        "(table-valued function:STRING_SPLIT).value\n" +
                        "[dbo].[reasons].[IsDeleted]\n" +
                        "[dbo].[reasons].c_desc\n" +
                        "[dbo].[reasons].reasons_ID\n" +
                        "[dbo].[reasons].reasons_ID2\n" +
                        "[dbo].[reasons].rid\n" +
                        "dbo.d_errors.[derid]\n" +
                        "dbo.d_errors.[errorcode]\n" +
                        "dbo.d_errors.errorcode\n" +
                        "dbo.d_errors.IsDeleted");
    }


    public static void testColumnAlias() {
        doTest("select C*2 as C from T",
                "Tables:\n" +
                        "T\n" +
                        "\n" +
                        "Fields:\n" +
                        "T.C");
    }

}
