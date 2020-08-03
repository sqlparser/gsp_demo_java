package mssql;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.mssql.TExecuteAsClause;
import gudusoft.gsqlparser.nodes.mssql.TProcedureOption;
import gudusoft.gsqlparser.stmt.mssql.TMssqlCreateProcedure;
import gudusoft.gsqlparser.stmt.mssql.TMssqlExecuteAs;
import junit.framework.TestCase;


public class testCreateProcedureOption extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE PROCEDURE dbo.usp_myproc \n" +
                "  WITH EXECUTE AS CALLER\n" +
                "AS \n" +
                "    SELECT SUSER_NAME(), USER_NAME();\n" +
                "    EXECUTE AS USER = 'guest';\n" +
                "    SELECT SUSER_NAME(), USER_NAME();\n" +
                "    REVERT;\n" +
                "    SELECT SUSER_NAME(), USER_NAME();\n" +
                "    DBCC CHECKIDENT (\"HumanResources.Employee\", RESEED, 30);";
        assertTrue(sqlparser.parse() == 0);

        TMssqlCreateProcedure createProcedure = (TMssqlCreateProcedure)sqlparser.sqlstatements.get(0);
        TProcedureOption option = createProcedure.getProcedureOptions().getElement(0);
        assertTrue(option.getOptionType() == EProcedureOptionType.potExecuteAs);
        TExecuteAsClause asClause = option.getExecuteAsClause();
        assertTrue(asClause.getExecuteAsOption() == EExecuteAsOption.eaoCaller);

        //System.out.println(createProcedure.getBodyStatements().size());
        assertTrue(createProcedure.getBodyStatements().get(1).sqlstatementtype == ESqlStatementType.sstmssqlexecuteas);
        TMssqlExecuteAs executeAs = (TMssqlExecuteAs)createProcedure.getBodyStatements().get(1);
        assertTrue(executeAs.getExecuteAsOption() == EExecuteAsOption.eaoUser);
        assertTrue(executeAs.getUserName().equalsIgnoreCase("'guest'"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "create proc dbo.outer\n" +
                "        @var1 nvarchar(255) = ''\n" +
                "        as\n" +
                "        set nocount on\n" +
                "        exec dbo.inner\n" +
                "        @var1";
        assertTrue(sqlparser.parse() == 0);

        TMssqlCreateProcedure createProcedure = (TMssqlCreateProcedure)sqlparser.sqlstatements.get(0);
        assertTrue(createProcedure.getProcedureName().toString().equalsIgnoreCase("dbo.outer"));

        assertTrue(createProcedure.getBodyStatements().size() == 2);
        //System.out.println(createProcedure.getBodyStatements().get(0).sqlstatementtype);
        assertTrue(createProcedure.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstmssqlset);
        assertTrue(createProcedure.getBodyStatements().get(1).sqlstatementtype == ESqlStatementType.sstmssqlexec);
    }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE PROCEDURE [dbo].[LockSnapshotForUpgrade]\n" +
                "@SnapshotDataID as uniqueidentifier,\n" +
                "@IsPermanentSnapshot as bit\n" +
                "AS\n" +
                "if @IsPermanentSnapshot = 1\n" +
                "BEGIN\n" +
                "   SELECT ChunkName from ChunkData with (XLOCK)\n" +
                "   WHERE SnapshotDataID = @SnapshotDataID\n" +
                "END ELSE BEGIN\n" +
                "   SELECT ChunkName from [ReportServerTempDB].dbo.ChunkData with (XLOCK)\n" +
                "   WHERE SnapshotDataID = @SnapshotDataID\n" +
                "END;\n" +
                "\n" +
                "CREATE VIEW [dbo].ExtendedDataSets\n" +
                "AS\n" +
                "SELECT\n" +
                "    ID, LinkID, [Name], ItemID\n" +
                "FROM DataSets\n" +
                "UNION ALL\n" +
                "SELECT\n" +
                "    ID, LinkID, [Name], ItemID\n" +
                "FROM [ReportServerTempDB].dbo.TempDataSets;\n" +
                "CREATE VIEW [dbo].[ExecutionLog3]\n" +
                "AS\n" +
                "SELECT\n" +
                "    InstanceName,\n" +
                "    COALESCE(CASE(ReportAction)\n" +
                "        WHEN 11 THEN AdditionalInfo.value('(AdditionalInfo/SourceReportUri)[1]', 'nvarchar(max)')\n" +
                "        ELSE C.Path\n" +
                "        END, 'Unknown') AS ItemPath,\n" +
                "    UserName,\n" +
                "    ExecutionId,\n" +
                "    CASE(RequestType)\n" +
                "        WHEN 0 THEN 'Interactive'\n" +
                "        WHEN 1 THEN 'Subscription'\n" +
                "        WHEN 2 THEN 'Refresh Cache'\n" +
                "        ELSE 'Unknown'\n" +
                "        END AS RequestType,\n" +
                "    -- SubscriptionId,\n" +
                "    Format,\n" +
                "    Parameters,\n" +
                "    CASE(ReportAction)\n" +
                "        WHEN 1 THEN 'Render'\n" +
                "        WHEN 2 THEN 'BookmarkNavigation'\n" +
                "        WHEN 3 THEN 'DocumentMapNavigation'\n" +
                "        WHEN 4 THEN 'DrillThrough'\n" +
                "        WHEN 5 THEN 'FindString'\n" +
                "        WHEN 6 THEN 'GetDocumentMap'\n" +
                "        WHEN 7 THEN 'Toggle'\n" +
                "        WHEN 8 THEN 'Sort'\n" +
                "        WHEN 9 THEN 'Execute'\n" +
                "        WHEN 10 THEN 'RenderEdit'\n" +
                "        WHEN 11 THEN 'ExecuteDataShapeQuery'\n" +
                "        WHEN 12 THEN 'RenderMobileReport'\n" +
                "        WHEN 13 THEN 'ConceptualSchema'\n" +
                "        WHEN 14 THEN 'QueryData'\n" +
                "        WHEN 15 THEN 'ASModelStream'\n" +
                "        WHEN 16 THEN 'RenderExcelWorkbook'\n" +
                "        WHEN 17 THEN 'GetExcelWorkbookInfo'\n" +
                "        ELSE 'Unknown'\n" +
                "        END AS ItemAction,\n" +
                "    TimeStart,\n" +
                "    TimeEnd,\n" +
                "    TimeDataRetrieval,\n" +
                "    TimeProcessing,\n" +
                "    TimeRendering,\n" +
                "    CASE(Source)\n" +
                "        WHEN 1 THEN 'Live'\n" +
                "        WHEN 2 THEN 'Cache'\n" +
                "        WHEN 3 THEN 'Snapshot'\n" +
                "        WHEN 4 THEN 'History'\n" +
                "        WHEN 5 THEN 'AdHoc'\n" +
                "        WHEN 6 THEN 'Session'\n" +
                "        WHEN 7 THEN 'Rdce'\n" +
                "        ELSE 'Unknown'\n" +
                "        END AS Source,\n" +
                "    Status,\n" +
                "    ByteCount,\n" +
                "    [RowCount],\n" +
                "    AdditionalInfo\n" +
                "FROM ExecutionLogStorage EL WITH(NOLOCK)\n" +
                "LEFT OUTER JOIN Catalog C WITH(NOLOCK) ON (EL.ReportID = C.ItemID);\n" +
                "\n" +
                "CREATE VIEW [dbo].[ExecutionLog]\n" +
                "AS\n" +
                "SELECT\n" +
                "    [InstanceName],\n" +
                "    [ReportID],\n" +
                "    [UserName],\n" +
                "    CASE ([RequestType])\n" +
                "        WHEN 1 THEN CONVERT(BIT, 1)\n" +
                "        ELSE CONVERT(BIT, 0)\n" +
                "    END AS [RequestType],\n" +
                "    [Format],\n" +
                "    [Parameters],\n" +
                "    [TimeStart],\n" +
                "    [TimeEnd],\n" +
                "    [TimeDataRetrieval],\n" +
                "    [TimeProcessing],\n" +
                "    [TimeRendering],\n" +
                "    CASE([Source])\n" +
                "        WHEN 6 THEN 3\n" +
                "        ELSE [Source]\n" +
                "    END AS Source,      -- Session source doesn't exist in yukon, mark source as snapshot\n" +
                "                        -- for in-session requests\n" +
                "    [Status],\n" +
                "    [ByteCount],\n" +
                "    [RowCount]\n" +
                "FROM [ExecutionLogStorage] WITH (NOLOCK)\n" +
                "WHERE [ReportAction] = 1 -- Backwards compatibility log only contains render requests;\n" +
                "\n" +
                "CREATE VIEW [dbo].ExtendedDataSources\n" +
                "AS\n" +
                "SELECT\n" +
                "    DSID, ItemID, SubscriptionID, Name, Extension, Link,\n" +
                "    CredentialRetrieval, Prompt, ConnectionString,\n" +
                "    OriginalConnectionString, OriginalConnectStringExpressionBased,\n" +
                "    UserName, Password, Flags, Version, DSIDNum\n" +
                "FROM DataSource\n" +
                "UNION ALL\n" +
                "SELECT\n" +
                "    DSID, ItemID, NULL as [SubscriptionID], Name, Extension, Link,\n" +
                "    CredentialRetrieval, Prompt, ConnectionString,\n" +
                "    OriginalConnectionString, OriginalConnectStringExpressionBased,\n" +
                "    UserName, Password, Flags, Version, null\n" +
                "FROM [ReportServerTempDB].dbo.TempDataSources;\n" +
                "\n" +
                "CREATE VIEW [dbo].[ExecutionLog2]\n" +
                "AS\n" +
                "SELECT \n" +
                "\tInstanceName, \n" +
                "\tCOALESCE(C.Path, 'Unknown') AS ReportPath, \n" +
                "\tUserName,\n" +
                "\tExecutionId, \n" +
                "\tCASE(RequestType)\n" +
                "\t\tWHEN 0 THEN 'Interactive'\n" +
                "\t\tWHEN 1 THEN 'Subscription'\n" +
                "\t\tELSE 'Unknown'\n" +
                "\t\tEND AS RequestType, \n" +
                "\t-- SubscriptionId, \n" +
                "\tFormat, \n" +
                "\tParameters, \n" +
                "\tCASE(ReportAction)\t\t\n" +
                "\t\tWHEN 1 THEN 'Render'\n" +
                "\t\tWHEN 2 THEN 'BookmarkNavigation'\n" +
                "\t\tWHEN 3 THEN 'DocumentMapNavigation'\n" +
                "\t\tWHEN 4 THEN 'DrillThrough'\n" +
                "\t\tWHEN 5 THEN 'FindString'\n" +
                "\t\tWHEN 6 THEN 'GetDocumentMap'\n" +
                "\t\tWHEN 7 THEN 'Toggle'\n" +
                "\t\tWHEN 8 THEN 'Sort'\n" +
                "\t\tELSE 'Unknown'\n" +
                "\t\tEND AS ReportAction,\n" +
                "\tTimeStart, \n" +
                "\tTimeEnd, \n" +
                "\tTimeDataRetrieval, \n" +
                "\tTimeProcessing, \n" +
                "\tTimeRendering,\n" +
                "\tCASE(Source)\n" +
                "\t\tWHEN 1 THEN 'Live'\n" +
                "\t\tWHEN 2 THEN 'Cache'\n" +
                "\t\tWHEN 3 THEN 'Snapshot' \n" +
                "\t\tWHEN 4 THEN 'History'\n" +
                "\t\tWHEN 5 THEN 'AdHoc'\n" +
                "\t\tWHEN 6 THEN 'Session'\n" +
                "\t\tWHEN 7 THEN 'Rdce'\n" +
                "\t\tELSE 'Unknown'\n" +
                "\t\tEND AS Source,\n" +
                "\tStatus,\n" +
                "\tByteCount,\n" +
                "\t[RowCount],\n" +
                "\tAdditionalInfo\n" +
                "FROM ExecutionLogStorage EL WITH(NOLOCK)\n" +
                "LEFT OUTER JOIN Catalog C WITH(NOLOCK) ON (EL.ReportID = C.ItemID);";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.size() == 6);

    }


}
