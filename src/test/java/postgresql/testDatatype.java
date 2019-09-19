package test.postgresql;
/*
 * Date: 13-6-14
 */

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

public class testDatatype extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE TABLE \"table1\" (\n" +
                "    \"column2\" date\n" +
                ");";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TColumnDefinition cd = createTable.getColumnList().getColumn(0);
        assertTrue(cd.getDatatype().getDataType() == EDataType.date_t);
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE TABLE \"table1\" (\n" +
                "    \"column1\" numeric(5,2)\n" +
                ");";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TColumnDefinition cd = createTable.getColumnList().getColumn(0);
        assertTrue(cd.getDatatype().getDataType() == EDataType.numeric_t);
        assertTrue(cd.getDatatype().getPrecision().toString().equalsIgnoreCase("5"));
        assertTrue(cd.getDatatype().getScale().toString().equalsIgnoreCase("2"));
    }

    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE TABLE Flights\n" +
                "(\n" +
                "    DEPART_TIME TIME,\n" +
                "    ID SERIAL\n" +
                ")";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TColumnDefinition cd = createTable.getColumnList().getColumn(0);
        assertTrue(cd.getDatatype().getDataType() == EDataType.time_t);

        cd = createTable.getColumnList().getColumn(1);
        assertTrue(cd.getDatatype().getDataType() == EDataType.serial_t);
    }

    public void test4(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE TABLE \"table\" (\n" +
                "    \"column\" character(2)\n" +
                ");";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TColumnDefinition cd = createTable.getColumnList().getColumn(0);
        //System.out.println(cd.getDatatype().getDataType().toString());
        assertTrue(cd.getDatatype().getDataType() == EDataType.char_t);
    }

    public void test5(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "create table test(column TEXT);";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TColumnDefinition cd = createTable.getColumnList().getColumn(0);
       // System.out.println(cd.getDatatype().getDataType().toString());
        assertTrue(cd.getDatatype().getDataType() == EDataType.text_t);
    }

    public void test6(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE TABLE test (\n" +
                "     id character \n" +
                ");";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TColumnDefinition cd = createTable.getColumnList().getColumn(0);
        assertTrue(cd.getDatatype().getDataType() == EDataType.char_t);
    }

    public void test9(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE TABLE test (\n" +
                "     id bigint, \n" +
                "     id1 bigserial, \n" +
                "     id2 money, \n" +
                "     id3 bytea \n" +
                ");";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TColumnDefinition cd = createTable.getColumnList().getColumn(0);
        TColumnDefinition cd1 = createTable.getColumnList().getColumn(1);
        TColumnDefinition cd2 = createTable.getColumnList().getColumn(2);
        TColumnDefinition cd3 = createTable.getColumnList().getColumn(3);
        assertTrue(cd.getDatatype().getDataType() == EDataType.bigint_t);
        assertTrue(cd1.getDatatype().getDataType() == EDataType.bigserial_t);
        assertTrue(cd2.getDatatype().getDataType() == EDataType.money_t);
        assertTrue(cd3.getDatatype().getDataType() == EDataType.bytea_t);
    }
}
