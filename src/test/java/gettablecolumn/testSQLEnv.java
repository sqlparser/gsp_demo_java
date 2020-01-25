package gettablecolumn;

import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
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
        tab.addColumn("keyno");

        tab = sqlSchema.createTable("employee");
        tab.addColumn("max_lvl");
        tab.addColumn("min_lvl");

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

}
