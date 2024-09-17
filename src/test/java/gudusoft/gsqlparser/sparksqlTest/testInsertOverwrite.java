package gudusoft.gsqlparser.sparksqlTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import junit.framework.TestCase;

public class testInsertOverwrite extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsparksql);
        sqlparser.sqltext = "INSERT OVERWRITE DIRECTORY\n" +
                "    USING parquet\n" +
                "    OPTIONS ('path' 's3:///bucket/path/to/report', col1 1, col2 'sum')\n" +
                "    SELECT bar.my_flag,sum(foo.amount) as amount_sum \n" +
                "\tFROM mydb.foo foo \n" +
                "\tleft join mydb.bar bar\n" +
                "\ton foo.bar_fk = bar.pk\n" +
                "\tgroup by bar.my_flag;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstinsert);
        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(insert.getFileFormat().equalsIgnoreCase("parquet"));
        assertTrue(insert.getFileOptions().equalsIgnoreCase("'path' 's3:///bucket/path/to/report', col1 1, col2 'sum'"));
        assertTrue(insert.getDirectoryName().toString().equalsIgnoreCase("'s3:///bucket/path/to/report'"));
    }
}
