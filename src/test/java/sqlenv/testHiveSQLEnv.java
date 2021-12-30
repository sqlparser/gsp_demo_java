package sqlenv;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.sqlenv.TSQLCatalog;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.sqlenv.TSQLSchema;
import gudusoft.gsqlparser.sqlenv.TSQLTable;
import junit.framework.TestCase;

public class testHiveSQLEnv extends TestCase {

    public void test1() {
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvhive);
        sqlParser.setSqlfilename(gspCommon.BASE_SQL_DIR_PRIVATE_JAVA +"hive/prosiebensat1/gsp_orphan_column.sql");
        sqlParser.setSqlEnv(new THiveEnv());
        assertTrue(sqlParser.parse() == 0);
        assertTrue(sqlParser.getSqlstatements().get(0).getSyntaxHints().size() == 0);
    }
}

class THiveEnv extends TSQLEnv {

    public THiveEnv(){
        super(EDbVendor.dbvhive);
        initSQLEnv();
    }

    @Override
    public void initSQLEnv() {

        // add a new database
        TSQLCatalog sqlCatalog = createSQLCatalog("pharos_business_vault");
        // hive don't have schema, we use a default schema
        TSQLSchema sqlSchema = sqlCatalog.createSchema("default");

        //add a new table: cTab
        TSQLTable ExecutionLogStorage = sqlSchema.createTable("b_content_datamart_bv");
        ExecutionLogStorage.addColumn("a_beginn_pe");
        ExecutionLogStorage.addColumn("a_perspektive_verbrauch");
    }
}