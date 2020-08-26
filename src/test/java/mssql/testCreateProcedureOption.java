package mssql;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.mssql.TExecuteAsClause;
import gudusoft.gsqlparser.nodes.mssql.TProcedureOption;
import gudusoft.gsqlparser.stmt.mssql.TMssqlBlock;
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

    public void testBeginCatch(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE PROCEDURE Intacct.ValidateResponseXML\n" +
                "(\n" +
                "\t@LoadId\t\t\tINT\n" +
                ",\t@Loaddate\t\tDATE\t= NULL\n" +
                ",\t@ResponseRaw\tVARCHAR(MAX)\n" +
                ",\t@Response\t\tXML OUTPUT\n" +
                ",\t@IsValid\t\tBIT\tOUTPUT\n" +
                ")\n" +
                "AS\n" +
                "BEGIN\n" +
                "\tDECLARE\n" +
                "\t\t@Procedurename\t\t\tNVARCHAR(129) = OBJECT_SCHEMA_NAME(@@Procid)+'.'+OBJECT_NAME(@@Procid)\n" +
                "\t,\t@Tablename\t\t\t\tNVARCHAR(129) = 'None'\n" +
                "\t,\t@Affectedrowcount\t\tINT\n" +
                "\t,\t@Message\t\t\t\tVARCHAR(512)\n" +
                "\t,\t@ErrorId\t\t\t\tUNIQUEIDENTIFIER\t= NEWID()\n" +
                "\t,\t@LineValueStart\t\t\tINT\n" +
                "\t,\t@LineValueEnd\t\t\tINT\n" +
                "\t,\t@CharacterValueStart\tINT\n" +
                "\t,\t@CharacterValueEnd\t\tINT\n" +
                "\t,\t@LineNumber\t\t\t\tINT\n" +
                "\t,\t@CharacterPosition\t\tINT\n" +
                "\t,\t@SuspectLine\t\t\tVARCHAR(2048)\n" +
                "\t,\t@InvalidCharacter\t\tNVARCHAR(1)\n" +
                "\t,\t@Delimiter\t\t\t\tCHAR(1) = CHAR(13)\n" +
                "--\t,\t@Response\t\t\t\tXML\n" +
                "\t,\t@ErrorMessage\t\t\tNVARCHAR(4000)\n" +
                "\t,\t@ErrorNumber\t\t\tINT\n" +
                "\n" +
                "\tSET @IsValid = 0\n" +
                "\tSET @Response = NULL\n" +
                "\n" +
                "\tSET @Message = 'Replacing a few known bad characters in the data set coming in the XML message.';\n" +
                "\n" +
                "\tEXEC ETL_Logging.ETL.LoadLog_Detail_Insert \n" +
                "\t\t@Loadid\t\t\t= @Loadid\n" +
                "\t,\t@Commenttypeid\t= 1\n" +
                "\t,\t@Comment\t\t= @Message\n" +
                "\t,\t@Loaddate\t\t= @Loaddate\n" +
                "\t,\t@Procname\t\t= @Procedurename\n" +
                "\t,\t@Rowsinserted\t= 0\n" +
                "\t,\t@Rowsupdated\t= 0\n" +
                "\t,\t@Rowsdeleted\t= 0\n" +
                "\t,\t@Tableaffected\t= @Tablename;\n" +
                "\t\n" +
                "\t-- We can make this step more generic\n" +
                "\tSET @ResponseRaw = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(@ResponseRaw,CHAR(237),'i'),CHAR(10),''),CHAR(233),'e'),CHAR(241),'n'),CHAR(160),' ')  --fixes í and LF and é and ñ\n" +
                "\n" +
                "\tSET @Message = 'Validating XML message.';\n" +
                "\tEXEC ETL_Logging.ETL.LoadLog_Detail_Insert \n" +
                "\t\t@Loadid\t\t\t= @Loadid\n" +
                "\t,\t@Commenttypeid\t= 1\n" +
                "\t,\t@Comment\t\t= @Message\n" +
                "\t,\t@Loaddate\t\t= @Loaddate\n" +
                "\t,\t@Procname\t\t= @Procedurename\n" +
                "\t,\t@Rowsinserted\t= 0\n" +
                "\t,\t@Rowsupdated\t= 0\n" +
                "\t,\t@Rowsdeleted\t= 0\n" +
                "\t,\t@Tableaffected\t= @Tablename;\n" +
                "\n" +
                "\tBEGIN TRY\n" +
                "\t\tSET @Response = CAST(@ResponseRaw AS XML)\n" +
                "\t\tSET @IsValid = 1\n" +
                "\n" +
                "\t\tSET @Message = 'Response XML message is valid.';\n" +
                "\t\tEXEC ETL_Logging.ETL.LoadLog_Detail_Insert \n" +
                "\t\t\t@Loadid\t\t\t= @Loadid\n" +
                "\t\t,\t@Commenttypeid\t= 1\n" +
                "\t\t,\t@Comment\t\t= @Message\n" +
                "\t\t,\t@Loaddate\t\t= @Loaddate\n" +
                "\t\t,\t@Procname\t\t= @Procedurename\n" +
                "\t\t,\t@Rowsinserted\t= 0\n" +
                "\t\t,\t@Rowsupdated\t= 0\n" +
                "\t\t,\t@Rowsdeleted\t= 0\n" +
                "\t\t,\t@Tableaffected\t= @Tablename;\n" +
                "\tEND TRY\n" +
                "\tBEGIN CATCH\n" +
                "\t\tSET @Message = 'Response XML message is NOT valid. View details in Intacct.XMLParseErrorLog for ErrorId: ' + CAST(@ErrorId AS VARCHAR(50));\n" +
                "\t\tEXEC ETL_Logging.ETL.LoadLog_Detail_Insert \n" +
                "\t\t\t@Loadid\t\t\t= @Loadid\n" +
                "\t\t,\t@Commenttypeid\t= 1\n" +
                "\t\t,\t@Comment\t\t= @Message\n" +
                "\t\t,\t@Loaddate\t\t= @Loaddate\n" +
                "\t\t,\t@Procname\t\t= @Procedurename\n" +
                "\t\t,\t@Rowsinserted\t= 0\n" +
                "\t\t,\t@Rowsupdated\t= 0\n" +
                "\t\t,\t@Rowsdeleted\t= 0\n" +
                "\t\t,\t@Tableaffected\t= @Tablename;\n" +
                "\n" +
                "\t\t-- process xml string based on error message/number, etc.\n" +
                "\t\tSELECT\n" +
                "\t\t\t@ErrorMessage\t= ERROR_MESSAGE()\n" +
                "\t\t,\t@ErrorNumber\t= ERROR_NUMBER()\n" +
                "\n" +
                "\t\tSELECT\n" +
                "\t\t\t@LineValueStart\t= CHARINDEX('line', @ErrorMessage) + 5\n" +
                "\t\t,\t@LineValueEnd\t= CHARINDEX(',', @ErrorMessage)\n" +
                "\n" +
                "\t\tSELECT\n" +
                "\t\t\t@CharacterValueStart\t= CHARINDEX('character', @ErrorMessage, @LineValueEnd + 1) + 10\n" +
                "\t\t,\t@CharacterValueEnd\t\t= CHARINDEX(',', @ErrorMessage, @LineValueEnd + 1)\n" +
                "\n" +
                "\t\tSELECT\n" +
                "\t\t\t@LineNumber\t\t\t= CAST(SUBSTRING(@ErrorMessage, @LineValueStart, @LineValueEnd - @LineValueStart) AS INT)\n" +
                "\t\t,\t@CharacterPosition\t= CAST(SUBSTRING(@ErrorMessage, @CharacterValueStart, @CharacterValueEnd - @CharacterValueStart) AS INT)\n" +
                "\n" +
                "\t\tCREATE TABLE #Temp\n" +
                "\t\t(\n" +
                "\t\t\tRowNumber\tINT IDENTITY (1,1)\n" +
                "\t\t,\tLineValue\tVARCHAR(MAX)\n" +
                "\t\t)\n" +
                "\n" +
                "\t\tINSERT INTO #Temp (LineValue)\n" +
                "\t\tSELECT\n" +
                "\t\t\tvalue\n" +
                "\t\tFROM\n" +
                "\t\t\tSTRING_SPLIT ( @ResponseRaw , @Delimiter ) \n" +
                "\n" +
                "\t\tSELECT\n" +
                "\t\t\t@SuspectLine = LineValue\n" +
                "\t\tFROM\n" +
                "\t\t\t#Temp\n" +
                "\t\tWHERE\n" +
                "\t\t\tRowNumber = @LineNumber\n" +
                "\n" +
                "\t\tINSERT INTO Intacct.XMLParseErrorLog (ErrorId, ErrorTime, ResponseRaw, LineNumber, CharacterPosition, SuspectLine, InvalidCharacter)\n" +
                "\t\tSELECT\n" +
                "\t\t\t@ErrorId\t\t\t\t\t\t\t\t\t\t\tAS ErrorId\n" +
                "\t\t,\tGETDATE()\t\t\t\t\t\t\t\t\t\t\tAS ErrorTime\n" +
                "\t\t,\t@ResponseRaw\t\t\t\t\t\t\t\t\t\tAS ResponseRaw\n" +
                "\t\t,\t@LineNumber\t\t\t\t\t\t\t\t\t\t\tAS LineNumber\n" +
                "\t\t,\t@CharacterPosition\t\t\t\t\t\t\t\t\tAS CharacterPosition\n" +
                "\t\t,\t@SuspectLine\t\t\t\t\t\t\t\t\t\tAS SuspectLine\n" +
                "\t\t,\tSUBSTRING(@SuspectLine, @CharacterPosition + 1, 1)\tAS InvalidCharacter\n" +
                "\tEND CATCH\n" +
                "END";
        assertTrue(sqlparser.parse() == 0);

        TMssqlCreateProcedure createProcedure = (TMssqlCreateProcedure)sqlparser.sqlstatements.get(0);
        assertTrue(createProcedure.getProcedureName().toString().equalsIgnoreCase("Intacct.ValidateResponseXML"));

       // assertTrue(createProcedure.getBodyStatements().size() == 2);
       // System.out.println(createProcedure.getDeclareStatements().size());
        assertTrue(createProcedure.getBodyStatements().size() == 1);
        assertTrue(createProcedure.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstmssqlblock);
        TMssqlBlock block  = (TMssqlBlock)createProcedure.getBodyStatements().get(0);
        assertTrue(block.getBodyStatements().size() == 9);
        assertTrue(block.getBodyStatements().get(8).sqlstatementtype == ESqlStatementType.sstmssqlblock);
        block = (TMssqlBlock)block.getBodyStatements().get(8);
        //System.out.println(block.getBodyStatements().size());
        assertTrue(block.getBodyStatements().size() == 14);
    }

}
