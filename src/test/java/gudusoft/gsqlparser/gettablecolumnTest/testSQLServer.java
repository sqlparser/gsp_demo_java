package gudusoft.gsqlparser.gettablecolumnTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.util.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;


/**
 * all testcases in this file are migrated to yaml file used by TSQLResolver2
 */

public class testSQLServer extends TestCase {

    static void doTestFile(String inputFile, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvmssql);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.showDatatype = true;
        getTableColumn.listStarColumn = true;
        getTableColumn.showColumnsOfCTE = false;
        getTableColumn.runFile(inputFile);
        // System.out.println(getTableColumn.outList.toString().trim());
        assertTrue(getTableColumn.outList.toString().trim().equalsIgnoreCase(desireResult));
    }

    static void doTest(String inputQuery, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvmssql);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.runText(inputQuery);
//        System.out.println(inputQuery);
//        System.out.println("Desired result:\n\n"+desireResult);
//        System.out.println("Actual result:\n\n"+getTableColumn.outList.toString().trim());

        assertTrue(getTableColumn.outList.toString().trim().equalsIgnoreCase(desireResult));
    }

    public  void test2() {
        doTest("CREATE TRIGGER [Production].[uWorkOrder] ON [Production].[WorkOrder]\n" +
                        "    AFTER UPDATE AS\n" +
                        "BEGIN\n" +
                        "    DECLARE @Count int;\n" +
                        "\n" +
                        "    SET @Count = @@ROWCOUNT;\n" +
                        "    IF @Count = 0\n" +
                        "        RETURN;\n" +
                        "\n" +
                        "    SET NOCOUNT ON;\n" +
                        "\n" +
                        "    BEGIN TRY\n" +
                        "        IF UPDATE([ProductID]) OR UPDATE([OrderQty])\n" +
                        "            BEGIN\n" +
                        "                INSERT INTO [Production].[TransactionHistory](\n" +
                        "                                                              [ProductID]\n" +
                        "                                                             ,[ReferenceOrderID]\n" +
                        "                                                             ,[TransactionType]\n" +
                        "                                                             ,[TransactionDate]\n" +
                        "                                                             ,[Quantity])\n" +
                        "                SELECT\n" +
                        "                    inserted.[ProductID]\n" +
                        "                     ,inserted.[WorkOrderID]\n" +
                        "                     ,'W'\n" +
                        "                     ,GETDATE()\n" +
                        "                     ,inserted.[OrderQty]\n" +
                        "                FROM inserted;\n" +
                        "            END;\n" +
                        "    END TRY\n" +
                        "    BEGIN CATCH\n" +
                        "        EXECUTE [dbo].[uspPrintError];\n" +
                        "\n" +
                        "        -- Rollback any active or uncommittable transactions before\n" +
                        "        -- inserting information in the ErrorLog\n" +
                        "        IF @@TRANCOUNT > 0\n" +
                        "            BEGIN\n" +
                        "                ROLLBACK TRANSACTION;\n" +
                        "            END\n" +
                        "\n" +
                        "        EXECUTE [dbo].[uspLogError];\n" +
                        "    END CATCH;\n" +
                        "END",
                "Tables:\n" +
                        "[Production].[TransactionHistory]\n" +
                        "[Production].[WorkOrder]\n" +
                        "\n" +
                        "Fields:\n" +
                        "[Production].[TransactionHistory].[ProductID]\n" +
                        "[Production].[TransactionHistory].[Quantity]\n" +
                        "[Production].[TransactionHistory].[ReferenceOrderID]\n" +
                        "[Production].[TransactionHistory].[TransactionDate]\n" +
                        "[Production].[TransactionHistory].[TransactionType]\n" +
                        "[Production].[WorkOrder].[OrderQty]\n" +
                        "[Production].[WorkOrder].[ProductID]\n" +
                        "[Production].[WorkOrder].[WorkOrderID]");
    }

    public  void test1() {
        doTest("CREATE TRIGGER [Production].[uWorkOrder] ON [Production].[WorkOrder]\n" +
                        "    AFTER UPDATE AS\n" +
                        "BEGIN\n" +
                        "    DECLARE @Count int;\n" +
                        "\n" +
                        "    SET @Count = @@ROWCOUNT;\n" +
                        "    IF @Count = 0\n" +
                        "        RETURN;\n" +
                        "\n" +
                        "    SET NOCOUNT ON;\n" +
                        "\n" +
                        "    BEGIN TRY\n" +
                        "        IF UPDATE([ProductID2]) OR UPDATE([OrderQty2])\n" +
                        "            BEGIN\n" +
                        "                INSERT INTO [Production].[TransactionHistory](\n" +
                        "                                                              [ProductID]\n" +
                        "                                                             ,[ReferenceOrderID]\n" +
                        "                                                             ,[TransactionType]\n" +
                        "                                                             ,[TransactionDate]\n" +
                        "                                                             ,[Quantity])\n" +
                        "                SELECT\n" +
                        "                    inserted.[ProductID]\n" +
                        "                     ,inserted.[WorkOrderID]\n" +
                        "                     ,'W'\n" +
                        "                     ,GETDATE()\n" +
                        "                     ,inserted.[OrderQty]\n" +
                        "                FROM inserted;\n" +
                        "            END;\n" +
                        "    END TRY\n" +
                        "    BEGIN CATCH\n" +
                        "        EXECUTE [dbo].[uspPrintError];\n" +
                        "\n" +
                        "        -- Rollback any active or uncommittable transactions before\n" +
                        "        -- inserting information in the ErrorLog\n" +
                        "        IF @@TRANCOUNT > 0\n" +
                        "            BEGIN\n" +
                        "                ROLLBACK TRANSACTION;\n" +
                        "            END\n" +
                        "\n" +
                        "        EXECUTE [dbo].[uspLogError];\n" +
                        "    END CATCH;\n" +
                        "END",
                "Tables:\n" +
                        "[Production].[TransactionHistory]\n" +
                        "[Production].[WorkOrder]\n" +
                        "\n" +
                        "Fields:\n" +
                        "[Production].[TransactionHistory].[ProductID]\n" +
                        "[Production].[TransactionHistory].[Quantity]\n" +
                        "[Production].[TransactionHistory].[ReferenceOrderID]\n" +
                        "[Production].[TransactionHistory].[TransactionDate]\n" +
                        "[Production].[TransactionHistory].[TransactionType]\n" +
                        "[Production].[WorkOrder].[OrderQty2]\n" +
                        "[Production].[WorkOrder].[OrderQty]\n" +
                        "[Production].[WorkOrder].[ProductID2]\n" +
                        "[Production].[WorkOrder].[ProductID]\n" +
                        "[Production].[WorkOrder].[WorkOrderID]");
    }



    public  void testSearchTopLevel() {
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

    public  void testColumnInSubquery() {

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


    public  void testTableFunction() {
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
                // Note: reasons_ID is NOT in the CTE's SELECT list (only rid, POS, c_desc)
                // so CR.reasons_ID in the ON clause cannot be traced to dbo.reasons
                "Tables:\n" +
                        "[dbo].[reasons]\n" +
                        "dbo.d_errors\n" +
                        "\n" +
                        "Fields:\n" +
                        "(table-valued function:STRING_SPLIT).value\n" +
                        "[dbo].[reasons].[IsDeleted]\n" +
                        "[dbo].[reasons].c_desc\n" +
                        "[dbo].[reasons].reasons_ID1\n" +
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

    public static void testPivot() {
        doTest("USE [InformerStg]\n" +
                        "GO\n" +
                        "\n" +
                        "CREATE VIEW [ETL].[ODS_ADDRESS]\n" +
                        "AS\n" +
                        "\n" +
                        "/*----------------------------------------------------------------------------------------------------\n" +
                        "\tAuthor:\tAndrew Higgins\n" +
                        "\tDescription:\t\n" +
                        "\t\tReporting view that mimics the structure of an ODS_production table using Informer data\n" +
                        "\tModification Log:\n" +
                        "\t\tDate:\t\tMade by:\t\tDescription:\n" +
                        "\t\t07/20/2015\tAndrew Higgins\tInitial Procedure Creation\t\t\n" +
                        "\t\t07/22/2015\tAndrew Higgins\tData Type Adjustment\n" +
                        "\t\t07/28/2015\tAndrew Higgins\tRemoved the ZIP_FIVE field per Linda Dykens - not in source\n" +
                        "\t\t2017-03-10\tAndrew Higgins\tAdded STATE_DESC, multivalue PIVOT\n" +
                        "\t\t2017-03-28\tAndrew Higgins\tAdded NULLIF\n" +
                        "\t\t2018-05-15\tKevin Barrett\tAdded TRY_CONVERTs only to ADDRESS_LINE_1, ADDRESS_LINE_2 and ADDRESS_LINE_3\n" +
                        "\t\t2019-11-05\tAndrew Higgins\tAdded RESIDENT fields\n" +
                        "\t\t2019-01-08  Ron Cavuto\t\tTicket 21946 - add separate address line cols. Splitting by comma causes errors and Entrinsik contains fields for address lines 1 and 2\n" +
                        "\t\t2020-01-30  Ranjit Chennam  LTRIM on ZIP\n" +
                        "\t\t2020-04-01  Ranjit Chennam  CTE and PIVOT on ADDRESSLINES to populate to Correct ADDRESS_LINE1,ADDRESSLINE2,ADDRESS_LINE3\n" +
                        "----------------------------------------------------------------------------------------------------*/\n" +
                        "WITH STATELIST AS\n" +
                        "    (\n" +
                        "        SELECT\n" +
                        "            STATES.STATES_ID\n" +
                        "           ,STATES.STATES_DESC\n" +
                        "        FROM Imported.STATES\n" +
                        "        WHERE STATES.IsDeleted = 0\n" +
                        "    )\n" +
                        "\n" +
                        ", [ADDRESS_LINES_CTE]\n" +
                        "    AS (\n" +
                        "           SELECT\n" +
                        "                ADDRESS_ID\n" +
                        "               ,[ss].[value]         \n" +
                        "\t\t\t   ,ID = ROW_NUMBER() OVER (PARTITION BY  ADDRESS_ID ORDER BY  ADDRESS_ID)\n" +
                        "\t\t\t   FROM\n" +
                        "                Imported.ADDRESS\n" +
                        "               CROSS APPLY  STRING_SPLIT(REPLACE(ADDRESS_LINES, '|~|',CHAR(7)),CHAR(7)) ss\n" +
                        "\t\t\t\t)\n" +
                        "\t\t\t\t\n" +
                        "\n" +
                        "SELECT\n" +
                        "\t A.ADDRESS_ID\n" +
                        "\t--,a.ADDRESS_LINES\n" +
                        "\t,ADDRESS_LINE_1 = CAST(NULLIF(AL.ADDRESS_LINE1,'') AS VARCHAR(40))\t\t\t--LEFT(a.address_line1,40)\n" +
                        "\t,ADDRESS_LINE_2 = CAST(NULLIF(LTRIM(AL.ADDRESS_LINE2),'') AS VARCHAR(40))--LEFT(a.address_line2,40)\n" +
                        "\t,ADDRESS_LINE_3 = CAST(NULLIF(LTRIM(AL.ADDRESS_LINE3),'') AS VARCHAR(40))--LEFT(CASE WHEN a.address_line2 IS NOT NULL THEN LTRIM(REPLACE(RIGHT(a.ADDRESS_LINES,LEN(a.ADDRESS_LINES) - (CHARINDEX(a.address_line2,a.ADDRESS_LINES) + LEN(a.address_line2) - 1)),',','')) ELSE\tNULL END,40)\n" +
                        "\t,A.CITY\n" +
                        "\t,A.COUNTRY\n" +
                        "\t,A.STATE\n" +
                        "\t,STATE_DESC     = s.STATES_DESC\n" +
                        "\t,LTRIM(A.ZIP) AS ZIP\n" +
                        "\t,A.ADDRESS_ADD_DATE\n" +
                        "\t,A.ADDRESS_CHANGE_DATE\n" +
                        "\t,N19_ADR_RESIDENT1 = TRY_CAST(CASE WHEN CHARINDEX('|~|',A.RESIDENTS)>0 THEN SUBSTRING(A.RESIDENTS,0,CHARINDEX('|~|',A.RESIDENTS,1)) ELSE A.RESIDENTS END AS VARCHAR(10))\n" +
                        "\t,N19_ADR_RESIDENT2 = TRY_CAST(CASE WHEN CHARINDEX('|~|',A.RESIDENTS)>0 THEN LTRIM(SUBSTRING(A.RESIDENTS,CHARINDEX('|~|',A.RESIDENTS,1)+3,CHARINDEX('|~|',A.RESIDENTS,CHARINDEX('|~|',A.RESIDENTS,2))-1)) ELSE NULL END AS VARCHAR(10))\n" +
                        "\n" +
                        "FROM Imported.ADDRESS A\n" +
                        "     LEFT OUTER JOIN\n" +
                        "     STATELIST s ON\n" +
                        "         A.STATE = s.STATES_ID\n" +
                        "\t\t LEFT JOIN (SELECT [p].[ADDRESS_ID]\n" +
                        "                      ,[p].[3] AS ADDRESS_LINE3\n" +
                        "                      ,[p].[2] AS ADDRESS_LINE2\n" +
                        "                      ,[p].[1] AS ADDRESS_LINE1 FROM [ADDRESS_LINES_CTE]\n" +
                        "\t\t\t\tPIVOT(MAX(value) FOR id IN ([1],[2],[3])) p\t) AL\n" +
                        "\t\t\t\tON\tA.[ADDRESS_ID]= AL.[ADDRESS_ID]\n" +
                        "\n" +
                        "WHERE A.IsDeleted = 0\n" +
                        "\t\t\t\t\n" +
                        "\t\t\t\t\n",
                "Tables:\n" +
                        "Imported.ADDRESS\n" +
                        "Imported.STATES\n" +
                        "p(piviot_table)\n" +
                        "\n" +
                        "Fields:\n" +
                        "(pivot-table:p(piviot_table)).[1]\n" +
                        "(pivot-table:p(piviot_table)).[2]\n" +
                        "(pivot-table:p(piviot_table)).[3]\n" +
                        "(table-valued function:STRING_SPLIT).[value]\n" +
                        "Imported.ADDRESS.[ADDRESS_ID]\n" +
                        "Imported.ADDRESS.ADDRESS_ADD_DATE\n" +
                        "Imported.ADDRESS.ADDRESS_CHANGE_DATE\n" +
                        "Imported.ADDRESS.ADDRESS_ID\n" +
                        "Imported.ADDRESS.ADDRESS_LINES\n" +
                        "Imported.ADDRESS.CITY\n" +
                        "Imported.ADDRESS.COUNTRY\n" +
                        "Imported.ADDRESS.IsDeleted\n" +
                        "Imported.ADDRESS.RESIDENTS\n" +
                        "Imported.ADDRESS.STATE\n" +
                        "Imported.ADDRESS.ZIP\n" +
                        "Imported.STATES.IsDeleted\n" +
                        "Imported.STATES.STATES_DESC\n" +
                        "Imported.STATES.STATES_ID");
    }
    public static void testTempTable() {
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlParser.sqltext = "SELECT Emplid, SUBSTRING(RTRIM(Name),LEN(RTRIM(Name))-6, 7)  AS Other_ID\n" +
                "\t\tINTO #DupNames\n" +
                "\t\tFROM [SnapShot].UAHEPRD.PS_NAMES\n" +
                "\t\tWHERE (First_Name LIKE '%DUPLICATE%ID%' OR First_Name = 'DUPLICATE')\n" +
                "\t\t\tAND Last_Name = 'CANCEL'\n" +
                "\t\t\tAND NAME_TYPE = 'PRI'";
        int i = sqlParser.parse();
        assertTrue(i == 0);
        TSelectSqlStatement select = (TSelectSqlStatement) sqlParser.sqlstatements.get(0);
        assertTrue(select.getSyntaxHints().size() == 0);
    }

    public static void testWhereCurrentOf() {
        doTest(" DELETE FROM HumanResources.EmployeePayHistory WHERE CURRENT OF complex_cursor;",
                "Tables:\n" +
                        "HumanResources.EmployeePayHistory\n" +
                        "\n" +
                        "Fields:");
    }

}
