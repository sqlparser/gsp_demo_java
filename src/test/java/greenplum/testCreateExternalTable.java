package greenplum;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

public class testCreateExternalTable extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgreenplum);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE ext_customer\n" +
                "   (id int, name text, sponsor text) \n" +
                "   LOCATION ( 'gpfdist://filehost:8081/*.txt' ) \n" +
                "   FORMAT 'TEXT' ( DELIMITER '|' NULL ' ')\n" +
                "   LOG ERRORS SEGMENT REJECT LIMIT 5;";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTableSqlStatement.isExternal());
        assertTrue(createTableSqlStatement.getTableName().toString().equalsIgnoreCase("ext_customer"));
        assertTrue(createTableSqlStatement.getColumnList().getColumn(0).getColumnName().toString().equalsIgnoreCase("id"));
        assertTrue(createTableSqlStatement.getLocationFiles().size() == 1);
        assertTrue(createTableSqlStatement.getLocationFiles().get(0).toString().equalsIgnoreCase("'gpfdist://filehost:8081/*.txt'"));

    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgreenplum);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE ext_expenses (name text, date date, \n" +
                "amount float4, category text, description text) \n" +
                "LOCATION ( \n" +
                "'file://seghost1/dbfast/external/expenses1.csv',\n" +
                "'file://seghost1/dbfast/external/expenses2.csv',\n" +
                "'file://seghost2/dbfast/external/expenses3.csv',\n" +
                "'file://seghost2/dbfast/external/expenses4.csv',\n" +
                "'file://seghost3/dbfast/external/expenses5.csv',\n" +
                "'file://seghost3/dbfast/external/expenses6.csv' \n" +
                ")\n" +
                "FORMAT 'CSV' ( HEADER );";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTableSqlStatement.isExternal());
        assertTrue(createTableSqlStatement.getTableName().toString().equalsIgnoreCase("ext_expenses"));
        assertTrue(createTableSqlStatement.getColumnList().getColumn(0).getColumnName().toString().equalsIgnoreCase("name"));
        assertTrue(createTableSqlStatement.getLocationFiles().size() == 6);
        assertTrue(createTableSqlStatement.getLocationFiles().get(5).toString().equalsIgnoreCase("'file://seghost3/dbfast/external/expenses6.csv'"));

    }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgreenplum);
        sqlparser.sqltext = "CREATE EXTERNAL WEB TABLE log_output (linenum int, message \n" +
                "text)  EXECUTE '/var/load_scripts/get_log_data.sh' ON HOST \n" +
                " FORMAT 'TEXT' (DELIMITER '|');";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTableSqlStatement.isExternal());
        assertTrue(createTableSqlStatement.isWebTable());
        assertTrue(createTableSqlStatement.getTableName().toString().equalsIgnoreCase("log_output"));
        assertTrue(createTableSqlStatement.getColumnList().getColumn(0).getColumnName().toString().equalsIgnoreCase("linenum"));
        assertTrue(createTableSqlStatement.getExecuteCmd().toString().equalsIgnoreCase("'/var/load_scripts/get_log_data.sh'"));
    }

    public void test4(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgreenplum);
        sqlparser.sqltext = "CREATE WRITABLE EXTERNAL TABLE sales_out (LIKE sales) \n" +
                "   LOCATION ('gpfdist://etl1:8081/sales.out')\n" +
                "   FORMAT 'TEXT' ( DELIMITER '|' NULL ' ')\n" +
                "   DISTRIBUTED BY (txn_id);";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTableSqlStatement.isExternal());
        assertTrue(createTableSqlStatement.isWritable());
        assertTrue(createTableSqlStatement.getTableName().toString().equalsIgnoreCase("sales_out"));
        assertTrue(createTableSqlStatement.getLocationFiles().size() == 1);
        assertTrue(createTableSqlStatement.getLocationFiles().get(0).toString().equalsIgnoreCase("'gpfdist://etl1:8081/sales.out'"));
        assertTrue(createTableSqlStatement.getLikeTableName().toString().equalsIgnoreCase("sales"));
    }

    public void test5(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgreenplum);
        sqlparser.sqltext = "CREATE WRITABLE EXTERNAL WEB TABLE campaign_out \n" +
                "(LIKE campaign) \n" +
                " EXECUTE '/var/unload_scripts/to_adreport_etl.sh'\n" +
                " FORMAT 'TEXT' (DELIMITER '|');";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTableSqlStatement.isExternal());
        assertTrue(createTableSqlStatement.isWritable());
        assertTrue(createTableSqlStatement.getTableName().toString().equalsIgnoreCase("campaign_out"));
        assertTrue(createTableSqlStatement.getLikeTableName().toString().equalsIgnoreCase("campaign"));
        assertTrue(createTableSqlStatement.getExecuteCmd().toString().equalsIgnoreCase("'/var/unload_scripts/to_adreport_etl.sh'"));
    }

}
