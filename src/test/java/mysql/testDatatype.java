package mysql;

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.TMySQLCreateTableOption;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

public class testDatatype extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "CREATE TABLE test (\n" +
                "    column1 BOOLEAN\n" +
                ");";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TColumnDefinition cd = createTable.getColumnList().getColumn(0);
        assertTrue(cd.getDatatype().getDataType() == EDataType.boolean_t);
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "CREATE TABLE test (\n" +
                "    column1 TINYINT\n" +
                ");";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TColumnDefinition cd = createTable.getColumnList().getColumn(0);
        assertTrue(cd.getDatatype().getDataType() == EDataType.tinyint_t);
    }

    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "CREATE TABLE `test` (\n" +
                "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `name` varchar(20) CHARACTER SET gbk NOT NULL,\n" +
                "  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',\n" +
                "  `updated_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TColumnDefinition cd = createTable.getColumnList().getColumn(1);
        assertTrue(cd.getDatatype().getDataType() == EDataType.varchar_t);
        assertTrue(cd.getDatatype().getCharsetName().equalsIgnoreCase("gbk"));

        TMySQLCreateTableOption option = createTable.getMySQLTableOptionList().getElement(0);
        assertTrue(option.getOptionName().equalsIgnoreCase("ENGINE"));
        assertTrue(option.getOptionValue().equalsIgnoreCase("InnoDB"));

        option = createTable.getMySQLTableOptionList().getElement(1);
        assertTrue(option.getOptionName().equalsIgnoreCase("AUTO_INCREMENT"));
        assertTrue(option.getOptionValue().equalsIgnoreCase("2"));

        option = createTable.getMySQLTableOptionList().getElement(2);
        assertTrue(option.getOptionName().equalsIgnoreCase("CHARSET"));
        assertTrue(option.getOptionValue().equalsIgnoreCase("utf8"));
    }

}