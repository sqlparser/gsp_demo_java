package gettablecolumn;

import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.TSQLCatalog;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.sqlenv.TSQLSchema;
import gudusoft.gsqlparser.sqlenv.TSQLTable;
import junit.framework.TestCase;

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
