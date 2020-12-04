package mysql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TMySQLCreateTableOption;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

public class testCreateTable extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "CREATE TABLE testtable (\n" +
                "testcolumn date default NULL\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='This is a test!!';";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
//        for(int i=0;i<createTableSqlStatement.getMySQLTableOptionList().size();i++){
//            TMySQLCreateTableOption option = createTableSqlStatement.getMySQLTableOptionList().getElement(i);
//            System.out.printf("name=%s, value=%s\n",option.getOptionName(),option.getOptionValue());
//        }

        TMySQLCreateTableOption option0 = createTableSqlStatement.getMySQLTableOptionList().getElement(0);
        TMySQLCreateTableOption option1 = createTableSqlStatement.getMySQLTableOptionList().getElement(1);
        TMySQLCreateTableOption option2 = createTableSqlStatement.getMySQLTableOptionList().getElement(2);

        assertTrue(option0.getOptionName().equalsIgnoreCase("ENGINE"));
        assertTrue(option0.getOptionValue().equalsIgnoreCase("InnoDB"));
        assertTrue(option1.getOptionName().equalsIgnoreCase("charset"));
        assertTrue(option1.getOptionValue().equalsIgnoreCase("utf8"));
        assertTrue(option2.getOptionName().equalsIgnoreCase("COMMENT"));
        assertTrue(option2.getOptionValue().equalsIgnoreCase("'This is a test!!'"));
    }

}
