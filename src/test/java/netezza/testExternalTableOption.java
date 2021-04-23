package netezza;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.netezza.TExternalTableOption;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

public class testExternalTableOption extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE '/export/home/nz/student.csv' USING (delimiter ',') AS SELECT * FROM student;";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getDelimiter().equals(","));
    }

    public void test2() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE ext_orders (ord_num INT, ord_dt TIMESTAMP) USING(dataobject('/tmp/order.tbl') \n DELIMITER '|');";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getDelimiter().equals("|"));
    }

    public void test3() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "create external table EXTERNALTABLENAMEHERE (\n" +
                " tbl_id bigint, \n" +
                " date_entered timestamp,\n" +
                " name_of_stuff varchar(100),\n" +
                " spend_amt numeric(16,2)\n" +
                " )\n" +
                "USING (\n" +
                "  DATAOBJECT('C:\\Data Sources\\some_random_file.txt') \n" +
                "  REMOTESOURCE 'odbc'\n" +
                "  DELIMITER '~'\n" +
                "  SKIPROWS 1\n" +
                "  MAXERRORS 1000\n" +
                "  LOGDIR 'C:\\\\' );\n";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getDelimiter().equals("~"));
    }

    public void test4() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "create external table EXTERNALTABLENAMEHERE (\n" +
                " tbl_id bigint, \n" +
                " date_entered timestamp,\n" +
                " name_of_stuff varchar(100),\n" +
                " spend_amt numeric(16,2)\n" +
                " )\n" +
                "USING (\n" +
                "  DATAOBJECT('C:\\Data Sources\\some_random_file.txt') \n" +
                "  REMOTESOURCE 'odbc'\n" +
                "  DELIMITER '\''\n" +
                "  SKIPROWS 1\n" +
                "  MAXERRORS 1000\n" +
                "  LOGDIR 'C:\\\\' );\n";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getDelimiter().equals("'"));
    }

    public void test5() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "create external table EXTERNALTABLENAMEHERE (\n" +
                " tbl_id bigint, \n" +
                " date_entered timestamp,\n" +
                " name_of_stuff varchar(100),\n" +
                " spend_amt numeric(16,2)\n" +
                " )\n" +
                "USING (\n" +
                "  DATAOBJECT('C:\\Data Sources\\some_random_file.txt') \n" +
                "  REMOTESOURCE 'odbc'\n" +
                "  DELIMITER '\"'\n" +
                "  SKIPROWS 1\n" +
                "  MAXERRORS 1000\n" +
                "  LOGDIR 'C:\\\\' );\n";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getDelimiter().equals("\""));
    }

    public void test6() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "create external table EXTERNALTABLENAMEHERE (\n" +
                " tbl_id bigint, \n" +
                " date_entered timestamp,\n" +
                " name_of_stuff varchar(100),\n" +
                " spend_amt numeric(16,2)\n" +
                " )\n" +
                "USING (\n" +
                "  DATAOBJECT('C:\\Data Sources\\some_random_file.txt') \n" +
                "  REMOTESOURCE 'odbc'\n" +
                "  DELIMITER '\t'\n" +
                "  SKIPROWS 1\n" +
                "  MAXERRORS 1000\n" +
                "  LOGDIR 'C:\\\\' );\n";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getDelimiter().equals("\t"));
    }

    public void test7() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "create external table EXTERNALTABLENAMEHERE (\n" +
                " tbl_id bigint, \n" +
                " date_entered timestamp,\n" +
                " name_of_stuff varchar(100),\n" +
                " spend_amt numeric(16,2)\n" +
                " )\n" +
                "USING (\n" +
                "  DATAOBJECT('C:\\Data Sources\\some_random_file.txt') \n" +
                "  REMOTESOURCE 'odbc'\n" +
                "  DELIMITER '\n'\n" +
                "  SKIPROWS 1\n" +
                "  MAXERRORS 1000\n" +
                "  LOGDIR 'C:\\\\' );\n";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getDelimiter().equals("\n"));
    }

    public void test8() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "create external table EXTERNALTABLENAMEHERE (\n" +
                " tbl_id bigint, \n" +
                " date_entered timestamp,\n" +
                " name_of_stuff varchar(100),\n" +
                " spend_amt numeric(16,2)\n" +
                " )\n" +
                "USING (\n" +
                "  DATAOBJECT('C:\\Data Sources\\some_random_file.txt') \n" +
                "  REMOTESOURCE 'odbc'\n" +
                "  DELIMITER 124\n" +
                "  SKIPROWS 1\n" +
                "  MAXERRORS 1000\n" +
                "  LOGDIR 'C:\\\\' );\n";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getDelimiter().equals("124"));
    }

    public void test9() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "create external table EXTERNALTABLENAMEHERE (\n" +
                " tbl_id bigint, \n" +
                " date_entered timestamp,\n" +
                " name_of_stuff varchar(100),\n" +
                " spend_amt numeric(16,2)\n" +
                " )\n" +
                "USING (\n" +
                "  DATAOBJECT('C:\\Data Sources\\some_random_file.txt') \n" +
                "  REMOTESOURCE 'odbc'\n" +
                "  DELIMITER 0xef\n" +
                "  SKIPROWS 1\n" +
                "  MAXERRORS 1000\n" +
                "  LOGDIR 'C:\\\\' );\n";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getDelimiter().equals("0xef"));
    }

    public void test10() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE ext_orders (ord_num INT, ord_dt TIMESTAMP) USING(dataobject('/tmp/order.tbl') DELIMITER '|' DATEDELIM ' ');";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getDateDelim().equals(" "));
    }

    public void test11() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE ext_orders (ord_num INT, ord_dt TIMESTAMP) USING(dataobject('/tmp/order.tbl') DELIMITER '|' DATEDELIM '-');";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getDateDelim().equals("-"));
    }

    public void test12() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE ext_orders (ord_num INT, ord_dt TIMESTAMP) USING(dataobject('/tmp/order.tbl') \n DELIMITER '|' DATEDELIM '/');";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getDateDelim().equals("/"));
    }

    public void test13() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE ext_orders (ord_num INT, ord_dt TIMESTAMP) USING(dataobject('/tmp/order.tbl') DELIMITER '|' DATEDELIM ' 12');";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getDateDelim().equals(" 12"));
    }

    public void test14() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE ext_orders (ord_num INT, ord_dt TIMESTAMP) USING(dataobject('/tmp/order.tbl') DELIMITER '|' DATEDELIM ' 12' LOGDIR '/data/data/HAGDEMO/temp/' RecordDelim '\n\r');";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getRecordDelim().equals("\n\r"));
    }

    public void test15() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE ext_orders (ord_num INT, ord_dt TIMESTAMP) USING(dataobject('/tmp/order.tbl') DELIMITER '|' DATEDELIM ' 12' LOGDIR '/data/data/HAGDEMO/temp/' RecordDelim '\r\n');";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getRecordDelim().equals("\r\n"));
    }

    public void test16() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE ext_orders (ord_num INT, ord_dt TIMESTAMP) USING(dataobject('/tmp/order.tbl') DELIMITER '|' DATEDELIM ' 12' LOGDIR '/data/data/HAGDEMO/temp/' RecordDelim '\r\n' timeDelim ':');";
        assertEquals(0, sqlparser.parse());

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertEquals(":", tExternalTableOption.getTimeDelim());
    }

    public void test17() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE '/export/home/nz/student.csv' USING (delimiter 'A') AS SELECT * FROM student;";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getDelimiter().equals("A"));
    }

    public void test18() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE '/export/home/nz/student.csv' USING (delimiter 'a') AS SELECT * FROM student;";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getDelimiter().equals("a"));
    }

    public void test19() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE '/export/home/nz/student.csv' USING ( compress 'zlib' delimiter 'a') AS SELECT * FROM student;";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getCompress().equals("zlib"));
    }

    public void test20() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE '/export/home/nz/student.csv' USING (compress true delimiter 'a') AS SELECT * FROM student;";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getCompress().equals("true"));
    }

    public void test21() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE '/export/home/nz/student.csv' USING (CRinString 'true' delimiter 'a') AS SELECT * FROM student;";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getcRinString());
    }

    public void test22() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE '/export/home/nz/student.csv' USING (CRinString 'on' delimiter 'a') AS SELECT * FROM student;";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getcRinString());
    }

    public void test23() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE '/export/home/nz/student.csv' USING (CRinString False delimiter 'a') AS SELECT * FROM student;";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertFalse(tExternalTableOption.getcRinString());
    }

    public void test24() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE '/export/home/nz/student.csv' USING (DATAOBJECT ('/var/tmp/test.txt') CRinString False delimiter 'a') AS SELECT * FROM student;";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getDataObject().equals("/var/tmp/test.txt"));
    }


    public void test25() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE '/export/home/nz/student.csv' USING (DATAOBJECT ('/var/tmp/test.txt') " +
                " format 'fixed'" +
                "layout (\n" +
                "Col01 DATE YMD '' bytes 8 nullif &='99991231',\n" +
                "Col09 BOOL Y_N bytes 1 nullif &=' ',\n" +
                "FILLER CHAR(1) Bytes 1, /* Was col10 space */\n" +
                "Col11 TIMESTAMP YMD '' 24HOUR '' bytes 14 nullif &='99991231000000',\n" +
                "Col26 CHAR(15) bytes 15 nullif &=' ', /* 15 spaces */\n" +
                "Col38 CHAR(13) bytes 13 nullif &='****NULL*****' ,\n" +
                "Col48 CHAR(2) bytes 2 nullif &='##' ,\n" +
                "Col50 INT4 bytes 5 nullif &='00000' ,\n" +
                "Col56 CHAR(10) bytes 10 nullif &='0000000000',\n" +
                "Col67 CHAR(3) bytes 3 /* Cannot load this directly, so insert-select statement used */\n" +
                ") delimiter 'a') AS SELECT * FROM student;";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getFormat().equals("fixed"));
    }

    public void test26() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE '/export/home/nz/student.csv' USING (DATAOBJECT ('/var/tmp/test.txt') " +
                " format 'fixed' maxErrors 147 " +
                "layout (\n" +
                "Col01 DATE YMD '' bytes 8 nullif &='99991231',\n" +
                "Col09 BOOL Y_N bytes 1 nullif &=' ',\n" +
                "FILLER CHAR(1) Bytes 1, /* Was col10 space */\n" +
                "Col11 TIMESTAMP YMD '' 24HOUR '' bytes 14 nullif &='99991231000000',\n" +
                "Col26 CHAR(15) bytes 15 nullif &=' ', /* 15 spaces */\n" +
                "Col38 CHAR(13) bytes 13 nullif &='****NULL*****' ,\n" +
                "Col48 CHAR(2) bytes 2 nullif &='##' ,\n" +
                "Col50 INT4 bytes 5 nullif &='00000' ,\n" +
                "Col56 CHAR(10) bytes 10 nullif &='0000000000',\n" +
                "Col67 CHAR(3) bytes 3 /* Cannot load this directly, so insert-select statement used */\n" +
                ") delimiter 'a') AS SELECT * FROM student;";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getMaxErrors() == 147);
    }

    public void test27() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE '/export/home/nz/student.csv' USING (DATAOBJECT ('/var/tmp/test.txt') " +
                " format 'fixed' maxErrors 147 skipRows 100 " +
                "layout (\n" +
                "Col01 DATE YMD '' bytes 8 nullif &='99991231',\n" +
                "Col09 BOOL Y_N bytes 1 nullif &=' ',\n" +
                "FILLER CHAR(1) Bytes 1, /* Was col10 space */\n" +
                "Col11 TIMESTAMP YMD '' 24HOUR '' bytes 14 nullif &='99991231000000',\n" +
                "Col26 CHAR(15) bytes 15 nullif &=' ', /* 15 spaces */\n" +
                "Col38 CHAR(13) bytes 13 nullif &='****NULL*****' ,\n" +
                "Col48 CHAR(2) bytes 2 nullif &='##' ,\n" +
                "Col50 INT4 bytes 5 nullif &='00000' ,\n" +
                "Col56 CHAR(10) bytes 10 nullif &='0000000000',\n" +
                "Col67 CHAR(3) bytes 3 /* Cannot load this directly, so insert-select statement used */\n" +
                ") delimiter 'a') AS SELECT * FROM student;";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TExternalTableOption tExternalTableOption = createTableSqlStatement.getExternalTableOption();
        assertTrue(tExternalTableOption.getSkipRows() == 100);
    }
}