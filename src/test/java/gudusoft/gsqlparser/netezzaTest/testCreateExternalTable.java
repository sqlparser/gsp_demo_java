package gudusoft.gsqlparser.netezzaTest;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

public class testCreateExternalTable extends TestCase {

    public void testCreateTable1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE '/export/home/nz/student.csv' USING (delimiter ',') AS SELECT * FROM student;";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTableSqlStatement.isExternal());
        assertTrue(createTableSqlStatement.getTableName().toString().equalsIgnoreCase("'/export/home/nz/student.csv'"));
        assertTrue(createTableSqlStatement.getSubQuery().getTables().getTable(0).getTableName().toString().equalsIgnoreCase("student"));
        assertTrue(createTableSqlStatement.getExternalTableOption().getOptionText().equalsIgnoreCase("USING (delimiter ',')"));
    }

}

