package formatsql;
/*
 * Date: 11-3-22
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.para.styleenums.TAlignStyle;
import gudusoft.gsqlparser.pp.para.styleenums.TLinefeedsCommaOption;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import junit.framework.TestCase;

public class testCreateFunctionProcedure extends TestCase {

    public static void testBEStyle_Function_leftBEOnNewline(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "CREATE FUNCTION dbo.Fn_gettoporders(@custid AS INT,                                    @n      AS INT,                                    @test   AS CHAR)  RETURNS TABLE \n" +
                 "AS \n" +
                 "  RETURN     SELECT TOP(@n) *     FROM   sales.salesorderheader     WHERE  customerid = @custid \n" +
                 "    ORDER  BY totaldue DESC ";

         sqlparser.parse();
         option.beStyleFunctionLeftBEOnNewline = true;
         option.beStyleFunctionLeftBEIndentSize = 3;
         option.beStyleBlockIndentSize = 3;
         String result = FormatterFactory.pp(sqlparser, option);
         assertTrue(result.trim().equalsIgnoreCase("CREATE FUNCTION dbo.Fn_gettoporders\n" +
                 "   (@custid AS INT,\n" +
                 "    @n      AS INT,\n" +
                 "    @test   AS CHAR\n" +
                 ") \n" +
                 "RETURNS TABLE \n" +
                 "AS \n" +
                 "   RETURN \n" +
                 "      SELECT   TOP(@n) *\n" +
                 "      FROM     sales.salesorderheader\n" +
                 "      WHERE    customerid = @custid\n" +
                 "      ORDER BY totaldue DESC"));
        // System.out.println(result);
     }

    public static void testBEStyle_Function_rightBEOnNewline(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "CREATE FUNCTION dbo.Fn_gettoporders(@custid AS INT,                                    @n      AS INT,                                    @test   AS CHAR)  RETURNS TABLE \n" +
                 "AS \n" +
                 "  RETURN     SELECT TOP(@n) *     FROM   sales.salesorderheader     WHERE  customerid = @custid \n" +
                 "    ORDER  BY totaldue DESC ";

         sqlparser.parse();
        option.beStyleFunctionLeftBEOnNewline = true;
       option.beStyleFunctionLeftBEIndentSize = 4;
         option.beStyleFunctionRightBEOnNewline = true;
        option.beStyleFunctionRightBEIndentSize = 2;
         String result = FormatterFactory.pp(sqlparser, option);

        //System.out.println(result);
        assertTrue(result.trim().equalsIgnoreCase("CREATE FUNCTION dbo.Fn_gettoporders\n" +
                "    (@custid AS INT,\n" +
                "     @n      AS INT,\n" +
                "     @test   AS CHAR\n" +
                "  ) \n" +
                "RETURNS TABLE \n" +
                "AS \n" +
                "  RETURN \n" +
                "    SELECT   TOP(@n) *\n" +
                "    FROM     sales.salesorderheader\n" +
                "    WHERE    customerid = @custid\n" +
                "    ORDER BY totaldue DESC"));
     }




    public static void testBEStyle_Function_FirstParamInNewline(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "CREATE FUNCTION dbo.Fn_gettoporders(@custid AS INT,                                    @n      AS INT,                                    @test   AS CHAR)  RETURNS TABLE \n" +
                 "AS \n" +
                 "  RETURN     SELECT TOP(@n) *     FROM   sales.salesorderheader     WHERE  customerid = @custid \n" +
                 "    ORDER  BY totaldue DESC ";

         sqlparser.parse();
        option.beStyleFunctionLeftBEOnNewline = true;
       option.beStyleFunctionLeftBEIndentSize = 4;
         option.beStyleFunctionRightBEOnNewline = true;
        option.beStyleFunctionRightBEIndentSize = 2;
        option.beStyleFunctionFirstParamInNewline = true;
         String result = FormatterFactory.pp(sqlparser, option);

       // System.out.println(result);
        assertTrue(result.trim().equalsIgnoreCase("CREATE FUNCTION dbo.Fn_gettoporders\n" +
                "    (\n" +
                "      @custid AS INT,\n" +
                "      @n      AS INT,\n" +
                "      @test   AS CHAR\n" +
                "  ) \n" +
                "RETURNS TABLE \n" +
                "AS \n" +
                "  RETURN \n" +
                "    SELECT   TOP(@n) *\n" +
                "    FROM     sales.salesorderheader\n" +
                "    WHERE    customerid = @custid\n" +
                "    ORDER BY totaldue DESC"));

     }

    public static void testParameters_Style(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "CREATE FUNCTION dbo.Fn_gettoporders(@custid AS INT,                                    @n      AS INT,                                    @test   AS CHAR)  RETURNS TABLE \n" +
                 "AS \n" +
                 "  RETURN     SELECT TOP(@n) *     FROM   sales.salesorderheader     WHERE  customerid = @custid \n" +
                 "    ORDER  BY totaldue DESC ";

         sqlparser.parse();
         option.parametersStyle = TAlignStyle.AsWrapped;
        option.beStyleFunctionRightBEOnNewline = false;

         String result = FormatterFactory.pp(sqlparser, option);
        //System.out.println(result);
        assertTrue(result.trim().equalsIgnoreCase("CREATE FUNCTION dbo.Fn_gettoporders(@custid AS INT, @n AS INT, @test AS CHAR) \n" +
                "RETURNS TABLE \n" +
                "AS \n" +
                "  RETURN \n" +
                "    SELECT   TOP(@n) *\n" +
                "    FROM     sales.salesorderheader\n" +
                "    WHERE    customerid = @custid\n" +
                "    ORDER BY totaldue DESC"));
     }

    public static void testParameters_Comma(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "CREATE FUNCTION dbo.Fn_gettoporders(@custid AS INT,                                    @n      AS INT,                                    @test   AS CHAR)  RETURNS TABLE \n" +
                 "AS \n" +
                 "  RETURN     SELECT TOP(@n) *     FROM   sales.salesorderheader     WHERE  customerid = @custid \n" +
                 "    ORDER  BY totaldue DESC ";

         sqlparser.parse();
         option.parametersStyle = TAlignStyle.AsStacked;
         option.parametersComma = TLinefeedsCommaOption.LfbeforeCommaWithSpace;
        option.beStyleFunctionRightBEOnNewline = false;
         String result = FormatterFactory.pp(sqlparser, option);
        //System.out.println(result);
        assertTrue(result.trim().equalsIgnoreCase("CREATE FUNCTION dbo.Fn_gettoporders(@custid AS INT\n" +
                "                                    , @n    AS INT\n" +
                "                                    , @test AS CHAR) \n" +
                "RETURNS TABLE \n" +
                "AS \n" +
                "  RETURN \n" +
                "    SELECT   TOP(@n) *\n" +
                "    FROM     sales.salesorderheader\n" +
                "    WHERE    customerid = @custid\n" +
                "    ORDER BY totaldue DESC"));
     }

    public static void testMultiStatements(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "CREATE FUNCTION dbo.fn_FindReports (@InEmpID INTEGER)\n" +
                 "RETURNS @retFindReports TABLE \n" +
                 "(\n" +
                 "    EmployeeID int primary key NOT NULL,\n" +
                 "    Name nvarchar(255) NOT NULL,\n" +
                 "    Title nvarchar(50) NOT NULL,\n" +
                 "    EmployeeLevel int NOT NULL,\n" +
                 "    Sort nvarchar (255) NOT NULL\n" +
                 ")\n" +
                 "--Returns a result set that lists all the employees who report to the \n" +
                 "--specific employee directly or indirectly.*/\n" +
                 "AS\n" +
                 "BEGIN\n" +
                 "   WITH DirectReports(Name, Title, EmployeeID, EmployeeLevel, Sort) AS\n" +
                 "    (SELECT CONVERT(Varchar(255), c.FirstName + ' ' + c.LastName),\n" +
                 "        e.Title,\n" +
                 "        e.EmployeeID,\n" +
                 "        1,\n" +
                 "        CONVERT(Varchar(255), c.FirstName + ' ' + c.LastName)\n" +
                 "     FROM HumanResources.Employee AS e\n" +
                 "          JOIN Person.Contact AS c ON e.ContactID = c.ContactID \n" +
                 "     WHERE e.EmployeeID = @InEmpID\n" +
                 "   UNION ALL\n" +
                 "     SELECT CONVERT(Varchar(255), REPLICATE ('| ' , EmployeeLevel) +\n" +
                 "        c.FirstName + ' ' + c.LastName),\n" +
                 "        e.Title,\n" +
                 "        e.EmployeeID,\n" +
                 "        EmployeeLevel + 1,\n" +
                 "        CONVERT (Varchar(255), RTRIM(Sort) + '| ' + FirstName + ' ' + \n" +
                 "                 LastName)\n" +
                 "     FROM HumanResources.Employee as e\n" +
                 "          JOIN Person.Contact AS c ON e.ContactID = c.ContactID\n" +
                 "          JOIN DirectReports AS d ON e.ManagerID = d.EmployeeID\n" +
                 "    )\n" +
                 "-- copy the required columns to the result of the function \n" +
                 "   INSERT @retFindReports\n" +
                 "   SELECT EmployeeID, Name, Title, EmployeeLevel, Sort\n" +
                 "   FROM DirectReports \n" +
                 "   RETURN\n" +
                 "END;\n" +
                 "GO\n" +
                 "-- Example invocation\n" +
                 "SELECT EmployeeID, Name, Title, EmployeeLevel\n" +
                 "FROM dbo.fn_FindReports(109)\n" +
                 "ORDER BY Sort;";

         sqlparser.parse();
         option.parametersStyle = TAlignStyle.AsStacked;
         option.parametersComma = TLinefeedsCommaOption.LfbeforeCommaWithSpace;
        option.beStyleFunctionRightBEOnNewline = false;
         String result = FormatterFactory.pp(sqlparser, option);
        //System.out.println("sql statements inside stored procedure not format correctly");
       //assertTrue("sql statements inside stored procedure not format correctly",false);
     }

    public static void testCreateProcedure_includes_create_index(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "CREATE PROCEDURE [dbo].[GenerateTempBondDetail]\n" +
                 "AS\n" +
                 "BEGIN\n" +
                 "       CREATE CLUSTERED INDEX ##PK_BondDetail2\n" +
                 "               ON dbo.##BondDetail2( Index )\n" +
                 "               ON [PRIMARY]\n" +
                 "END";

         sqlparser.parse();
         option.parametersStyle = TAlignStyle.AsStacked;
         option.parametersComma = TLinefeedsCommaOption.LfbeforeCommaWithSpace;
        option.beStyleFunctionRightBEOnNewline = false;
         String result = FormatterFactory.pp(sqlparser, option);
        //System.out.println(result);
        assertTrue(result.trim().equalsIgnoreCase("CREATE PROCEDURE [dbo].[GenerateTempBondDetail] \n" +
                "AS \n" +
                "  BEGIN \n" +
                "    CREATE CLUSTERED INDEX ##pk_bonddetail2 ON dbo.##bonddetail2( index ) ON [PRIMARY] \n" +
                "  END"));
     }


    public static void testBigScript(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "create procedure MSupdatescdata\n" +
                 "     @logical_record_parent_nickname int,\n" +
                 "            @logical_record_parent_rowguid uniqueidentifier,\n" +
                 "            @replnick binary(6),\n" +
                 "            @parent_row_inserted bit output\n" +
                 "as\n" +
                 "    declare @logical_record_parent_oldmaxversion int,\n" +
                 "            @logical_record_lineage varbinary(311),\n" +
                 "            @logical_record_parent_regular_lineage varbinary(311),\n" +
                 "            @logical_record_parent_gencur bigint,\n" +
                 "            @rows_updated int,\n" +
                 "            @error int,\n" +
                 "            @logical_record_parent_pubid uniqueidentifier,\n" +
                 "            @logical_record_parent_objid int\n" +
                 "\n" +
                 "    select top 1 @logical_record_parent_pubid = pubid, @logical_record_parent_objid = objid\n" +
                 "    from dbo.sysmergearticles\n" +
                 "    where nickname = @logical_record_parent_nickname\n" +
                 "    if (isnull(permissions(@logical_record_parent_objid), 0) & 0x1b = 0 and {fn ISPALUSER(@logical_record_parent_pubid)} <> 1)\n" +
                 "    begin\n" +
                 "        raiserror(15247, 11, -1)\n" +
                 "        return 1\n" +
                 "    end\n" +
                 "   \n" +
                 "    select @parent_row_inserted = 0\n" +
                 "   \n" +
                 "    if @logical_record_parent_rowguid is null\n" +
                 "        return 0\n" +
                 "   \n" +
                 "    select top 1    @logical_record_parent_oldmaxversion = maxversion_at_cleanup\n" +
                 "    from dbo.sysmergearticles\n" +
                 "    where nickname = @logical_record_parent_nickname\n" +
                 "\n" +
                 "    exec sys.sp_MSmerge_getgencur @logical_record_parent_nickname, 1, @logical_record_parent_gencur output\n" +
                 "   \n" +
                 "    update dbo.MSmerge_tombstone\n" +
                 "        set logical_record_lineage = { fn UPDATELINEAGE(logical_record_lineage, @replnick, @logical_record_parent_oldmaxversion+1) }\n" +
                 "        where tablenick = @logical_record_parent_nickname\n" +
                 "        and rowguid = @logical_record_parent_rowguid\n" +
                 "    select @rows_updated = @@rowcount, @error = @@error\n" +
                 "   \n" +
                 "    if @error <> 0\n" +
                 "        return 1\n" +
                 "       \n" +
                 "    if @rows_updated = 1\n" +
                 "        return 0 -- no need to insert tombstone if rows_updated = 0. This proc is never called to insert a parent's tombstone.\n" +
                 "   \n" +
                 "    update dbo.MSmerge_contents\n" +
                 "        set logical_record_lineage = { fn UPDATELINEAGE(logical_record_lineage, @replnick, @logical_record_parent_oldmaxversion+1) }\n" +
                 "        where tablenick = @logical_record_parent_nickname\n" +
                 "        and rowguid = @logical_record_parent_rowguid\n" +
                 "    select @rows_updated = @@rowcount, @error = @@error\n" +
                 "   \n" +
                 "    if @error <> 0\n" +
                 "        return 1\n" +
                 "       \n" +
                 "    if @rows_updated = 0\n" +
                 "    begin\n" +
                 "        select @logical_record_lineage = { fn UPDATELINEAGE(0x0, @replnick, @logical_record_parent_oldmaxversion+1) }\n" +
                 "       \n" +
                 "        -- if no cleanup done yet, use 1 as the version.\n" +
                 "        if @logical_record_parent_oldmaxversion = 1\n" +
                 "            select @logical_record_parent_regular_lineage = { fn UPDATELINEAGE(0x0, @replnick, 1) }\n" +
                 "        else\n" +
                 "            select @logical_record_parent_regular_lineage = @logical_record_lineage\n" +
                 "           \n" +
                 "        insert into dbo.MSmerge_contents (tablenick, rowguid, lineage, colv1, generation, partchangegen,                                           \n" +
                 "                logical_record_parent_rowguid, logical_record_lineage)\n" +
                 "            values (@logical_record_parent_nickname, @logical_record_parent_rowguid, @logical_record_parent_regular_lineage,\n" +
                 "                0x00, @logical_record_parent_gencur, NULL, @logical_record_parent_rowguid, @logical_record_lineage)\n" +
                 "        if @@error <> 0\n" +
                 "            return 1\n" +
                 "       \n" +
                 "        select @parent_row_inserted = 1\n" +
                 "    end\n" +
                 "   \n" +
                 "    return 0\n" +
                 "\n" +
                 "\n" +
                 "\n" +
                 "create procedure cainfo\n" +
                 "    (@publisher            sysname,\n" +
                 "     @publisher_db        sysname,\n" +
                 "     @publication         sysname,\n" +
                 "     @datasource_type    int = 0,               \n" +
                 "     @server_name        sysname    = NULL,      \n" +
                 "     @db_name            sysname = NULL,       \n" +
                 "     @datasource_path    nvarchar(255) = NULL, \n" +
                 "     @compatlevel        int = 10)           \n" +
                 "   \n" +
                 "as\n" +
                 "    declare    @retcode           int\n" +
                 "    declare    @repid             uniqueidentifier\n" +
                 "    declare    @pubid             uniqueidentifier\n" +
                 "    declare    @schemaguid        uniqueidentifier\n" +
                 "    declare    @replnick          binary(6)\n" +
                 "    declare    @subscription_type int\n" +
                 "    declare    @validation_level  int\n" +
                 "    declare    @reptype           int\n" +
                 "    declare    @priority          real\n" +
                 "    declare    @schversion        int\n" +
                 "    declare    @status            int\n" +
                 "    declare    @resync_gen        bigint\n" +
                 "    declare    @replicastate      uniqueidentifier\n" +
                 "    declare    @sync_type         tinyint\n" +
                 "    declare    @description       nvarchar(255)\n" +
                 "    declare    @dynamic_snapshot_received int\n" +
                 "    declare    @distributor       sysname\n" +
                 "    declare    @dometadata_cleanup int\n" +
                 "    declare    @REPLICA_STATUS_Deleted tinyint\n" +
                 "    declare    @REPLICA_STATUS_BeforeRestore tinyint\n" +
                 "    declare    @REPLICA_STATUS_AttachFailed tinyint\n" +
                 "    declare    @retention_period_unit tinyint\n" +
                 "    declare    @islocalpubid bit, @islocalsubid bit, @cleanedup_unsent_changes bit\n" +
                 "    declare    @last_sync_time datetime, @num_time_units_since_last_sync int\n" +
                 "    declare    @supportability_mode int\n" +
                 "\n" +
                 "    select @publisher_db = RTRIM(@publisher_db)\n" +
                 "    select @db_name = RTRIM(@db_name)\n" +
                 "  \n" +
                 "    exec @retcode = sys.sp_MSmerge_validate_publication_presence @publication, @publisher_db, @publisher, @pubid output\n" +
                 "    if @retcode <> 0 or @@error <> 0\n" +
                 "        return 1\n" +
                 "\n" +
                 "    set @REPLICA_STATUS_Deleted= 2\n" +
                 "    set @REPLICA_STATUS_BeforeRestore= 7\n" +
                 "    set @REPLICA_STATUS_AttachFailed= 6\n" +
                 "\n" +
                 "    if (@server_name is NULL)\n" +
                 "        SET @server_name = publishingservername()\n" +
                 "\n" +
                 "    if (@db_name is NULL)\n" +
                 "        set @db_name = db_name()\n" +
                 "      \n" +
                 "    select @retention_period_unit = retention_period_unit from dbo.sysmergepublications\n" +
                 "    where pubid = @pubid\n" +
                 "\n" +
                 "    SELECT @repid = subid, @replnick = replnickname, @priority = priority, @reptype = subscriber_type,\n" +
                 "        @subscription_type = subscription_type , @status = status, @replicastate = replicastate,\n" +
                 "        @schversion = schemaversion, @schemaguid = schemaguid,\n" +
                 "        @sync_type = sync_type, @description = description, @priority = priority,\n" +
                 "        @dometadata_cleanup = case when sys.fn_add_units_to_date(-1, @retention_period_unit, getdate()) > metadatacleanuptime then 1\n" +
                 "                                    else 0\n" +
                 "                              end,\n" +
                 "        @last_sync_time = last_sync_date,\n" +
                 "        @supportability_mode = supportability_mode\n" +
                 "        FROM dbo.sysmergesubscriptions\n" +
                 "        WHERE UPPER(subscriber_server) collate database_default = UPPER(@server_name) collate database_default\n" +
                 "            and db_name = @db_name and pubid = @pubid\n" +
                 "            and status <> @REPLICA_STATUS_Deleted\n" +
                 "            and status <> @REPLICA_STATUS_BeforeRestore\n" +
                 "            and status <> @REPLICA_STATUS_AttachFailed\n" +
                 "    if @repid is NULL\n" +
                 "    begin\n" +
                 "        RAISERROR(20021, 16, -1)\n" +
                 "        return (1)\n" +
                 "    end\n" +
                 "\n" +
                 "    select @validation_level=validation_level, @resync_gen=resync_gen\n" +
                 "    from dbo.MSmerge_replinfo\n" +
                 "    where repid = @repid\n" +
                 "\n" +
                 "    select @distributor = NULL\n" +
                 "    select @publication = NULL\n" +
                 "    if @repid = @pubid\n" +
                 "    begin\n" +
                 "        select @publication = name, @distributor = distributor from dbo.sysmergepublications\n" +
                 "            where pubid = @pubid\n" +
                 "    end\n" +
                 "  \n" +
                 "    select @num_time_units_since_last_sync = sys.fn_datediff_units(@retention_period_unit, getdate(), @last_sync_time)\n" +
                 "\n" +
                 "    if @repid <> @pubid\n" +
                 "    begin\n" +
                 "        update s\n" +
                 "        set s.application_name = p.program_name\n" +
                 "        from sys.dm_exec_sessions p, dbo.sysmergesubscriptions s where p.session_id = @@spid and s.subid = @repid\n" +
                 "        if @@error<>0\n" +
                 "            return 1\n" +
                 "    end\n" +
                 "\n" +
                 "    if @compatlevel >= 90\n" +
                 "    begin\n" +
                 "        select @islocalpubid = sys.fn_MSmerge_islocalpubid(@pubid),\n" +
                 "            @islocalsubid = sys.fn_MSmerge_islocalsubid(@repid),\n" +
                 "            @cleanedup_unsent_changes = 0\n" +
                 "          \n" +
                 "        if @islocalsubid = 0\n" +
                 "            select @cleanedup_unsent_changes = cleanedup_unsent_changes\n" +
                 "            from dbo.sysmergesubscriptions\n" +
                 "            where subid = @repid    -- right replica row\n" +
                 "        else\n" +
                 "        begin\n" +
                 "            if @islocalpubid = 0\n" +
                 "                select @cleanedup_unsent_changes = cleanedup_unsent_changes\n" +
                 "                from dbo.sysmergesubscriptions\n" +
                 "                where pubid = @pubid\n" +
                 "                and subid = @pubid    -- we are interested in the cleanedup_unsent_changes bit from the publisher replica row\n" +
                 "            else\n" +
                 "                select @cleanedup_unsent_changes = 0 -- no way to tell which subscriber we are syncing with\n" +
                 "        end\n" +
                 "      \n" +
                 "        select @repid, @replnick,\n" +
                 "               @reptype, @subscription_type, @priority, @schversion, @schemaguid, @status, @replicastate,\n" +
                 "               @sync_type, @description, @publication, @distributor, @validation_level, @resync_gen, @dometadata_cleanup,\n" +
                 "               @pubid, @cleanedup_unsent_changes, @num_time_units_since_last_sync, @supportability_mode\n" +
                 "    end\n" +
                 "    else\n" +
                 "    begin\n" +
                 "        select @repid, {fn REPLNICK_90_TO_80(@replnick)},\n" +
                 "               @reptype, @subscription_type, @priority, @schversion, @schemaguid, @status, @pubid,\n" +
                 "               @sync_type, @description, @publication, @distributor, @validation_level, @resync_gen, @dometadata_cleanup\n" +
                 "\n" +
                 "    end\n" +
                 "\n" +
                 "    return (0)\n" +
                 "\n" +
                 "\n" +
                 "create procedure MSgrdata\n" +
                 "    (@tablenick int,\n" +
                 "     @rowguid uniqueidentifier,\n" +
                 "     @lineage varbinary(311) output,\n" +
                 "     @colv varbinary(2953) output,\n" +
                 "     @pubid uniqueidentifier = NULL,\n" +
                 "     @compatlevel int = 10)\n" +
                 "as\n" +
                 "    begin\n" +
                 "        if @compatlevel < 90\n" +
                 "        begin\n" +
                 "            declare @iscoltracked int\n" +
                 "            declare @cCols int\n" +
                 "            set @iscoltracked= sys.fn_fIsColTracked(@tablenick)\n" +
                 "            if @iscoltracked = 1\n" +
                 "            begin\n" +
                 "                set @cCols= sys.fn_cColvEntries_80(@pubid, @tablenick)\n" +
                 "            end\n" +
                 "        end\n" +
                 "        select\n" +
                 "               @lineage= case when @compatlevel >= 90 then lineage else {fn LINEAGE_90_TO_80(lineage)} end,\n" +
                 "               @colv= case when @compatlevel >= 90 or @iscoltracked = 0 then colv1 else {fn COLV_90_TO_80(colv1, @cCols)} end\n" +
                 "            from dbo.MSmerge_contents\n" +
                 "            with (serializable)\n" +
                 "            where tablenick = @tablenick and rowguid = @rowguid\n" +
                 "    end\n" +
                 "\n" +
                 "create procedure sp_MSpttweight\n" +
                 " @tablenick   int,\n" +
                 " @rowguid   uniqueidentifier,\n" +
                 " @acknowledge_only bit,\n" +
                 " @rowvector   varbinary(11)= null\n" +
                 "as\n" +
                 " set nocount on\n" +
                 "  \n" +
                 " declare @METADATA_TYPE_DeleteLightweight tinyint\n" +
                 " declare @METADATA_TYPE_DeleteLightweightProcessed tinyint\n" +
                 " declare @pubnick tinyint \n" +
                 " declare @removefrompartialvector binary(1)\n" +
                 " declare @versionzerovector binary(11)  \n" +
                 " update dbo.MSmerge_rowtrack\n" +
                 "  set rowvector= case @acknowledge_only\n" +
                 "        when 1 then rowvector -- For a simple acknowledge, leave the vector alone.\n" +
                 "        else @rowvector\n" +
                 "        end,\n" +
                 "      changed= sys.fn_MSdayasnumber(getdate()) \n" +
                 "  where\n" +
                 "   tablenick = @tablenick and\n" +
                 "   rowguid = @rowguid and\n" +
                 "   sync_cookie = @pubnick and\n" +
                 "   (\n" +
                 "    1=@acknowledge_only or\n" +
                 "    (\n" +
                 "     {fn GETMAXVERSION(\n" +
                 "       case\n" +
                 "        when rowvector is null then @versionzerovector\n" +
                 "        when rowvector = @removefrompartialvector then @versionzerovector\n" +
                 "        else rowvector\n" +
                 "       end)}\n" +
                 "     <= {fn GETMAXVERSION(isnull(@rowvector, @versionzerovector))}\n" +
                 "    )\n" +
                 "   )\n" +
                 "\n" +
                 " begin\n" +
                 "  update dbo.MSmerge_rowtrack\n" +
                 "   set sync_cookie= null,\n" +
                 "    changetype= case changetype\n" +
                 "        when @METADATA_TYPE_DeleteLightweight then @METADATA_TYPE_DeleteLightweightProcessed\n" +
                 "        else changetype\n" +
                 "       end,\n" +
                 "    changed= sys.fn_MSdayasnumber(getdate())\n" +
                 "   where\n" +
                 "    sync_cookie = @pubnick\n" +
                 "   begin\n" +
                 "   update dbo.MSmerge_rowtrack\n" +
                 "    set rowvector= @rowvector\n" +
                 "    where\n" +
                 "     tablenick = @tablenick and\n" +
                 "     (\n" +
                 "      {fn GETMAXVERSION(\n" +
                 "        case\n" +
                 "         when rowvector is null then @versionzerovector\n" +
                 "         else rowvector\n" +
                 "        end)}\n" +
                 "      <= {fn GETMAXVERSION(isnull(@rowvector, @versionzerovector))}\n" +
                 "     )\n" +
                 "\n" +
                 "  end\n" +
                 " end\n" +
                 "\n" +
                 "\n" +
                 "create procedure sp_setap\n" +
                 "    @rolename   sysname,        -- name app role\n" +
                 "    @fCreateCookie bit = 0\n" +
                 "as\n" +
                 "    set nocount on\n" +
                 "    if (@fCreateCookie = 1)\n" +
                 "  setuser @rolename \n" +
                 "    else\n" +
                 "  setuser @rolename \n" +
                 "    if (@@error <> 0)\n" +
                 "        return (1)\n" +
                 "   \n" +
                 "return (0) \n" +
                 "\n" +
                 "\n" +
                 "create procedure MSrepl_b_s\n" +
                 "as\n" +
                 "    declare @sync_bit int\n" +
                 "    declare @dist_bit int\n" +
                 " \n" +
                 "    begin\n" +
                 "             update MSrepl_backup_lsns set next_xact_id = t2.xact_id, next_xact_seqno = m.xact_seqno from\n" +
                 "            (select tm.publisher_database_id, max(tm.xact_seqno) from            \n" +
                 "                ((select t.publisher_database_id, max(substring(t.xact_seqno, 1, 10)) from\n" +
                 "                      MSrepl_transactions t where\n" +
                 "                      not t.xact_id = 0x0\n" +
                 "                      group by t.publisher_database_id, substring(t.xact_seqno, 1, 10)\n" +
                 "                      having count(t.xact_seqno) < 2)\n" +
                 "                union \n" +
                 "                (select t.publisher_database_id, max(substring(xact_seqno, 1, 10)) from\n" +
                 "                      MSrepl_transactions t where\n" +
                 "                      not t.xact_id = 0x0\n" +
                 "                      and t.xact_id = substring(t.xact_seqno, 1, 10)\n" +
                 "                      group by t.publisher_database_id )\n" +
                 "                )as tm(publisher_database_id, xact_seqno)\n" +
                 "    group by tm.publisher_database_id\n" +
                 "                )as m(publisher_database_id, xact_seqno),\n" +
                 "            MSrepl_transactions t2         \n" +
                 "    \n" +
                 "    end\n" +
                 "   \n" +
                 "    return 0\n" +
                 "\n" +
                 "\n" +
                 "create procedure MSrestorefk(\n" +
                 "    @program_name   sysname = null\n" +
                 ")as\n" +
                 "begin\n" +
                 "    set nocount on\n" +
                 "    declare @retcode                    int,\n" +
                 "            @parent_name                sysname,\n" +
                 "            @parent_schema              sysname,\n" +
                 "            @referenced_object_id       int\n" +
                 "    begin\n" +
                 "       select key_constraints.name\n" +
                 "                      from sys.index_columns index_columns\n" +
                 "                    inner join sys.key_constraints key_constraints\n" +
                 "                   on indexes.name = key_constraints.name\n" +
                 "                      where indexes.object_id = @referenced_object_id and indexes.index_id not in\n" +
                 "                            (select index_columns.index_id\n" +
                 "                             from sys.index_columns index_columns\n" +
                 "          left join\n" +
                 "           (select referenced_column_name, constraint_column_id\n" +
                 "          from dbo.MSsavedforeignkeycolumns\n" +
                 "          where program_name = @program_name\n" +
                 "           and parent_schema = @parent_schema) foreignkeycolumns (referenced_column_name, constraint_column_id)\n" +
                 "                                  on columns.name = foreignkeycolumns.referenced_column_name\n" +
                 "                                  and index_columns.key_ordinal = foreignkeycolumns.constraint_column_id\n" +
                 "                              where index_columns.object_id = @referenced_object_id\n" +
                 "                              and foreignkeycolumns.referenced_column_name is null\n" +
                 "        )\n" +
                 "      \n" +
                 "     \n" +
                 "   end\n" +
                 "end\n" +
                 "\n" +
                 "\n" +
                 "CREATE PROCEDURE MSindex\n" +
                 " @tablename nvarchar(517), @index_name nvarchar(258) = NULL\n" +
                 "AS\n" +
                 "BEGIN\n" +
                 "  DECLARE @table_id int\n" +
                 "   DECLARE @pagesize int\n" +
                 " \n" +
                 "  SELECT @table_id = id\n" +
                 "  FROM dbo.sysobjects\n" +
                 "  WHERE (id = object_id(@tablename))\n" +
                 "    AND ((OBJECTPROPERTY(id, N'IsTable') = 1) OR (OBJECTPROPERTY(id, N'IsView') = 1))\n" +
                 "  CHECKPOINT\n" +
                 " \n" +
                 "\n" +
                 "END\n" +
                 "\n" +
                 "\n" +
                 "create procedure vupgrade\n" +
                 "as\n" +
                 "        begin\n" +
                 "            alter table dbo.sysmergearticles add default 0 for compensate_for_errors\n" +
                 "        end                   \n" +
                 "        begin\n" +
                 "          alter table dbo.MSmerge_errorlineage alter column lineage varbinary(311) not null\n" +
                 "          update dbo.MSmerge_errorlineage set lineage= {fn LINEAGE_80_TO_90(lineage)}\n" +
                 "        end\n" +
                 "\n" +
                 "CREATE UNIQUE CLUSTERED INDEX ucMSrepl_transactions ON dbo.MSrepl_transactions\n" +
                 "         (publisher_database_id, xact_seqno)\n" +
                 "         WITH STATISTICS_NORECOMPUTE\n" +
                 "         \n" +
                 "         ";

         sqlparser.parse();
         String result = FormatterFactory.pp(sqlparser, option);
        //  System.out.println("not formatted correctly");
        //assertTrue("not formatted correctly",false);
     }

}
