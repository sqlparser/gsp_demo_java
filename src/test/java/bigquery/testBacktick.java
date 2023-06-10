package bigquery;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TTableList;
import junit.framework.Assert;
import junit.framework.TestCase;


public class testBacktick extends TestCase {


    public void test2600_1() {
        String query = "select col1,co2 from `database.schema`.table_name where col>0;";

        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlParser.setSqlCharset("UTF-8");
        sqlParser.sqltext = query;
        int iRet = sqlParser.parse();

        if (iRet == 0) {
            TStatementList statements = sqlParser.sqlstatements;
            TTableList tables = statements.get(0).getTables();
            TTable table = tables.getTable(0);

            // table name
            Assert.assertEquals("table_name", table.getTableName().getObjectToken().toString());
            // db name
            Assert.assertEquals("`database`", table.getTableName().getDatabaseToken().toString());
            // schema name
            Assert.assertEquals("`schema`", table.getTableName().getSchemaToken().toString());
        }
    }

    public void test2600_2() {
        String query = "select col1,co2 from database.`schema.table_name` where col>0;";

        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlParser.setSqlCharset("UTF-8");
        sqlParser.sqltext = query;
        int iRet = sqlParser.parse();

        if (iRet == 0) {
            TStatementList statements = sqlParser.sqlstatements;
            TTableList tables = statements.get(0).getTables();
            TTable table = tables.getTable(0);

            // table name
            Assert.assertEquals("`table_name`", table.getTableName().getObjectToken().toString());
            // db name
            Assert.assertEquals("database", table.getTableName().getDatabaseToken().toString());
            // schema name
            Assert.assertEquals("`schema`", table.getTableName().getSchemaToken().toString());
        }
    }


    public void test1() {
        String query = "SELECT `table1`.`field1` as `field1` FROM `db_name.schema_name`.`table1` `table1`";

        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlParser.setSqlCharset("UTF-8");
        sqlParser.sqltext = query;
        int iRet = sqlParser.parse();

        if (iRet == 0) {
            TStatementList statements = sqlParser.sqlstatements;
            TTableList tables = statements.get(0).getTables();
            TTable table = tables.getTable(0);

            // table name
            Assert.assertEquals("`table1`", table.getTableName().getObjectToken().toString());
            // db name
            Assert.assertEquals("`db_name`", table.getTableName().getDatabaseToken().toString());
            // schema name
            Assert.assertEquals("`schema_name`", table.getTableName().getSchemaToken().toString());
        }
    }

    public void testexample2(){

        String query = "SELECT `table1`.`field1` as `field1` FROM `db_name`.`schema_name`.`table1` `table1`";

        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlParser.setSqlCharset("UTF-8");
        sqlParser.sqltext = query;
        int iRet = sqlParser.parse();

        if( iRet == 0 ){
            TStatementList statements = sqlParser.sqlstatements;
            TTableList tables = statements.get(0).getTables();
            TTable table = tables.getTable(0);

            // table name
            Assert.assertEquals("`table1`" , table.getTableName().getObjectToken().toString());
            // db name
            Assert.assertEquals("`db_name`" , table.getTableName().getDatabaseToken().toString());
            // schema name
            Assert.assertEquals("`schema_name`" , table.getTableName().getSchemaToken().toString());
        }
    }


    public void testexample1(){

        String query = "SELECT table1.field1 as field1 FROM db_name.schema_name.table1 table1";

        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlParser.setSqlCharset("UTF-8");
        sqlParser.sqltext = query;
        int iRet = sqlParser.parse();

        if( iRet == 0 ){
            TStatementList statements = sqlParser.sqlstatements;
            TTableList tables = statements.get(0).getTables();
            TTable table = tables.getTable(0);

            // table name
            Assert.assertEquals("table1" , table.getTableName().getObjectToken().toString());
            // db name
            Assert.assertEquals("db_name" , table.getTableName().getDatabaseToken().toString());
            // schema name
            Assert.assertEquals("schema_name" , table.getTableName().getSchemaToken().toString());
        }
    }
}
