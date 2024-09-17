package gudusoft.gsqlparser.gettablecolumnTest;

import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.sqlenv.TSQLCatalog;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.sqlenv.TSQLSchema;
import gudusoft.gsqlparser.sqlenv.TSQLTable;
import junit.framework.TestCase;

class TOracleEnv extends TSQLEnv {

    public TOracleEnv(){
        super(EDbVendor.dbvoracle);
        initSQLEnv();
    }

    @Override
    public void initSQLEnv() {

        // add a new database: master
        TSQLCatalog sqlCatalog = createSQLCatalog("orcl");
        // add a new schema: dbo
        TSQLSchema sqlSchema = sqlCatalog.createSchema("scott");
        //add a new table:
        TSQLTable aTab = sqlSchema.createTable("tab2");
        aTab.addColumn("col1");

        aTab = sqlSchema.createTable("departments");
        aTab.addColumn("department_name");

        aTab = sqlSchema.createTable("CDS_H_KUNDEN_OBJEKT");
        aTab.addColumn("C_BANK");
        aTab.addColumn("STATUSCODE");

        aTab = sqlSchema.createTable("CDS_H_ZINSEN");
        aTab.addColumn("BANKSTELLE");
        aTab.addColumn("HIST_DATUM");
        aTab.addColumn("KONTONUMMER");
        aTab.addColumn("RUBRIK");

        aTab = sqlSchema.createTable("DWH_OTF_GESCHAEFTE");
        aTab.addColumn("agentur");
        aTab.addColumn("anzahl");
        aTab.addColumn("bankstelle");
        aTab.addColumn("betrag");
        aTab.addColumn("betrag_frw");
        aTab.addColumn("datum_verfall");
        aTab.addColumn("kategorie");
        aTab.addColumn("rubrik");
        aTab.addColumn("snb_code");
        aTab.addColumn("waehrungscode_iso");
        aTab.addColumn("zinssatz");

    }
}

class TSQLServerEnv extends TSQLEnv {

    public TSQLServerEnv(){
        super(EDbVendor.dbvmssql);
        initSQLEnv();
    }

    @Override
    public void initSQLEnv() {

        // add a new database: master
        TSQLCatalog sqlCatalog = createSQLCatalog("master");
        // add a new schema: dbo
        TSQLSchema sqlSchema = sqlCatalog.createSchema("dbo");
        //add a new table: aTab
        TSQLTable aTab = sqlSchema.createTable("aTab");
        aTab.addColumn("Quantity1");

        //add a new table: bTab
        TSQLTable bTab = sqlSchema.createTable("bTab");
        bTab.addColumn("Quantity2");

        //add a new table: cTab
        TSQLTable cTab = sqlSchema.createTable("cTab");
        cTab.addColumn("Quantity");

        TSQLTable tab = sqlSchema.createTable("sysforeignkeys");
        tab.addColumn("fkey");
        tab.addColumn("fkeyid");
        tab.addColumn("keyNo");
        tab.addColumn("rkey");
        tab.addColumn("rkeyid");

        tab = sqlSchema.createTable("employee");
        tab.addColumn("max_lvl");
        tab.addColumn("min_lvl");

    }
}

class TSQLServerEnvSearchUpLevel extends TSQLEnv {

    public TSQLServerEnvSearchUpLevel(){
        super(EDbVendor.dbvmssql);
        initSQLEnv();
    }

    @Override
    public void initSQLEnv() {

        // add a new database: master
        TSQLCatalog sqlCatalog = createSQLCatalog("master");
        // add a new schema: dbo
        TSQLSchema sqlSchema = sqlCatalog.createSchema("dbo");
        //add a new table: cTab
        TSQLTable ExecutionLogStorage = sqlSchema.createTable("ExecutionLogStorage");
        ExecutionLogStorage.addColumn("UserName");

    }
}

class TOracleEnv2 extends TSQLEnv {

    public TOracleEnv2(){
        super(EDbVendor.dbvoracle);
        initSQLEnv();
    }

    @Override
    public void initSQLEnv() {

        TSQLCatalog sqlCatalog = createSQLCatalog("orcl");
        TSQLSchema sqlSchema = sqlCatalog.createSchema("scott");
        //add a new table:
        TSQLTable aTab = sqlSchema.createTable("deb_rfa_dpd_history");
        aTab.addColumn("dpd_100");
        aTab.addColumn("dpd_100_delinquency_bucket");
    }
}

class TOracleEnvStarColumn1 extends TSQLEnv {

    public TOracleEnvStarColumn1(){
        super(EDbVendor.dbvoracle);
        initSQLEnv();
    }

    @Override
    public void initSQLEnv() {
        TSQLCatalog sqlCatalog = createSQLCatalog("default");
        TSQLSchema sqlSchema = sqlCatalog.createSchema("default");
        TSQLTable aTab = sqlSchema.createTable("some_table");
        aTab.addColumn("c123");
        TSQLTable bTab = sqlSchema.createTable("other_table");
        bTab.addColumn("c1");
    }
}

public class testSQLEnv extends TestCase {

    public static void test1(){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvmssql);
        getTableColumn.isConsole = false;
        getTableColumn.listStarColumn = true;
        getTableColumn.setSqlEnv(new TSQLServerEnv());

        getTableColumn.runText("SELECT Quantity,b.Time,c.Description\n" +
                "FROM\n" +
                "(SELECT ID2,Time FROM bTab) b\n" +
                "INNER JOIN aTab a on a.ID=b.ID\n" +
                "INNER JOIN cTab c on a.ID=c.ID\n");
        String strActual = getTableColumn.outList.toString();
        //System.out.println(strActual);
        assertTrue(strActual.trim().equalsIgnoreCase("Tables:\n" +
                "aTab\n" +
                "bTab\n" +
                "cTab\n" +
                "\n" +
                "Fields:\n" +
                "aTab.ID\n" +
                "bTab.ID2\n" +
                "bTab.Time\n" +
                "cTab.Description\n" +
                "cTab.ID\n" +
                "cTab.Quantity"));
    }

    public static void testOracle1(){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvoracle);
        getTableColumn.isConsole = false;
        getTableColumn.listStarColumn = true;
        if (!TBaseType.ENABLE_RESOLVER){
            getTableColumn.setSqlEnv(new TOracleEnv2());
        }


        getTableColumn.runText("select b.pb_id\n" +
                "                 , b.pb_source_code\n" +
                "                 , b.time_key as hist_time_key\n" +
                "                 , ot.time_key as curr_time_key\n" +
                "                 , dpd_100\n" +
                "                 , dpd_100_delinquency_bucket as bucket_100\n" +
                "                 , ot.time_id\n" +
                "                 , to_char(add_months(ot.time_id, -6),'J') as time_key_6m\n" +
                "             from deb_rfa_product_bridge pb\n" +
                "             left join deb_rfa_dpd_history b\n" +
                "                     on pb.pb_id = b.pb_id\n" +
                "                    and pb.pb_source_code = b.pb_source_code\n" +
                "             cross join ods_time ot\n" +
                "            where b.time_key >= to_char (add_months (ot.time_id, -12),'J')\n" +
                "              and ot.time_id = (select process_current_date\n" +
                "                                  from etl_current_date\n" +
                "                                 where process_code = 'DEB_RFA_ODS')");
        String strActual = getTableColumn.outList.toString();
//        System.out.println("select b.pb_id\n" +
//                "                 , b.pb_source_code\n" +
//                "                 , b.time_key as hist_time_key\n" +
//                "                 , ot.time_key as curr_time_key\n" +
//                "                 , dpd_100\n" +
//                "                 , dpd_100_delinquency_bucket as bucket_100\n" +
//                "                 , ot.time_id\n" +
//                "                 , to_char(add_months(ot.time_id, -6),'J') as time_key_6m\n" +
//                "             from deb_rfa_product_bridge pb\n" +
//                "             left join deb_rfa_dpd_history b\n" +
//                "                     on pb.pb_id = b.pb_id\n" +
//                "                    and pb.pb_source_code = b.pb_source_code\n" +
//                "             cross join ods_time ot\n" +
//                "            where b.time_key >= to_char (add_months (ot.time_id, -12),'J')\n" +
//                "              and ot.time_id = (select process_current_date\n" +
//                "                                  from etl_current_date\n" +
//                "                                 where process_code = 'DEB_RFA_ODS')");
//        System.out.println(strActual);

        String resultWithOldAlgo = "Tables:\n" +
                "deb_rfa_dpd_history\n" +
                "deb_rfa_product_bridge\n" +
                "etl_current_date\n" +
                "ods_time\n" +
                "\n" +
                "Fields:\n" +
                "deb_rfa_dpd_history.dpd_100\n" +
                "deb_rfa_dpd_history.dpd_100_delinquency_bucket\n" +
                "deb_rfa_dpd_history.pb_id\n" +
                "deb_rfa_dpd_history.pb_source_code\n" +
                "deb_rfa_dpd_history.time_key\n" +
                "deb_rfa_product_bridge.pb_id\n" +
                "deb_rfa_product_bridge.pb_source_code\n" +
                "etl_current_date.process_code\n" +
                "etl_current_date.process_current_date\n" +
                "ods_time.time_id\n" +
                "ods_time.time_key";
        String resultWithNewAlgo = "Tables:\n" +
                "deb_rfa_dpd_history\n" +
                "deb_rfa_product_bridge\n" +
                "etl_current_date\n" +
                "ods_time\n" +
                "\n" +
                "Fields:\n" +
                "deb_rfa_dpd_history.pb_id\n" +
                "deb_rfa_dpd_history.pb_source_code\n" +
                "deb_rfa_dpd_history.time_key\n" +
                "deb_rfa_product_bridge.dpd_100\n" +
                "deb_rfa_product_bridge.dpd_100_delinquency_bucket\n" +
                "deb_rfa_product_bridge.pb_id\n" +
                "deb_rfa_product_bridge.pb_source_code\n" +
                "etl_current_date.process_code\n" +
                "etl_current_date.process_current_date\n" +
                "ods_time.time_id\n" +
                "ods_time.time_key";

        String resultStr="";
        if (TBaseType.ENABLE_RESOLVER){
            resultStr = resultWithNewAlgo;
        }else{
            resultStr = resultWithOldAlgo;
        }
        assertTrue(strActual.trim().equalsIgnoreCase(resultStr));
    }

    public static void testSelectStarColumn1(){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvoracle);
        getTableColumn.isConsole = false;
        getTableColumn.listStarColumn = true;
        getTableColumn.setSqlEnv(new TOracleEnvStarColumn1());

        getTableColumn.runText("select c1, c123 from\n" +
                "(  -- subquery\n" +
                "    select *\n" +
                "    from some_table o1, other_table o2\n" +
                ")");
        String strActual = getTableColumn.outList.toString();
//        System.out.println(strActual);
        assertTrue(strActual.trim().equalsIgnoreCase("Tables:\n" +
                "other_table\n" +
                "some_table\n" +
                "\n" +
                "Fields:\n" +
                "other_table.*\n" +
                "other_table.c1\n" +
                "some_table.*\n" +
                "some_table.c123"));
    }



    public static void testSearchUpLevel(){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvmssql);
        getTableColumn.isConsole = false;
        getTableColumn.listStarColumn = true;
        getTableColumn.setSqlEnv(new TSQLServerEnvSearchUpLevel());

        getTableColumn.runText("insert into [dbo].[ExecutionLogStorage]\n" +
                "            ([InstanceName],\n" +
                "             [ReportID],\n" +
                "             [UserName]\n" +
                "             )\n" +
                "        select top (1024) with ties\n" +
                "            [InstanceName],\n" +
                "            [ReportID],\n" +
                "            [UserName]\n" +
                "         from [dbo].[ExecutionLog_Old] WITH (XLOCK)\n" +
                "         order by TimeStart ;");
        String strActual = getTableColumn.outList.toString();
//        System.out.println(strActual);
        assertTrue(strActual.trim().equalsIgnoreCase("Tables:\n" +
                "[dbo].[ExecutionLog_Old]\n" +
                "[dbo].[ExecutionLogStorage]\n" +
                "\n" +
                "Fields:\n" +
                "[dbo].[ExecutionLog_Old].[InstanceName]\n" +
                "[dbo].[ExecutionLog_Old].[ReportID]\n" +
                "[dbo].[ExecutionLog_Old].[UserName]\n" +
                "[dbo].[ExecutionLog_Old].TimeStart\n" +
                "[dbo].[ExecutionLogStorage].[InstanceName]\n" +
                "[dbo].[ExecutionLogStorage].[ReportID]\n" +
                "[dbo].[ExecutionLogStorage].[UserName]"));
    }

}
