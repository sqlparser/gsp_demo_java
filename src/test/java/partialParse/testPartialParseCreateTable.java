package partialParse;
/*
 * Date: 12-11-2
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testPartialParseCreateTable extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "create table COURT \n" +
                "(\n" +
                "COURT_CODE VARCHAR2(1) not null,\n" +
                "COURT_DESC VARCHAR2(255) default 'XNA' not null,\n" +
                "SOURCE_SYSTEM VARCHAR2(10) not null,\n" +
                "DW_DELETED_FLAG CHAR not null,\n" +
                "DW_CURRENT_FLAG CHAR not null\n" +
                ")\n" +
                "pctfree 5\n" +
                "partition by range\n" +
                "(DW_CURRENT_FLAG)\n" +
                "(\n" +
                "partition PARTITION_HISTORY values less than ('Y')\n" +
                ",partition PARTITION_CURRENT values less than (maxvalue)\n" +
                ")\n" +
                "enable row movement;";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TColumnDefinition columnDefinition = createTableSqlStatement.getColumnList().getColumn(1);
        assertTrue(columnDefinition.getColumnName().toString().equalsIgnoreCase("COURT_DESC"));
        assertTrue(columnDefinition.getDatatype().toString().equalsIgnoreCase("VARCHAR2(255)"));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);
        sqlparser.sqltext = "CREATE TABLE \"USA\".\"PAY_BEHV_LKUP_S\" ( \"MAX_ACT_FINAL_SCORE\",\n" +
                "\"MAX_FLAT_REV_SCORE\", \"MIN_ACT_FINAL_SCORE\", \"MIN_FLAT_REV_SCORE\",\n" +
                "\"PAY_BHAV_CD\" ) AS ( SELECT * FROM USA.PAY_BEHV_LKUP) DATA INITIALLY DEFERRED\n" +
                "REFRESH DEFERRED ENABLE QUERY OPTIMIZATION MAINTAINED BY SYSTEM DATA CAPTURE\n" +
                "NONE IN CONUSA_B1692 PARTITIONING KEY ( MAX_ACT_FINAL_SCORE ) USING HASHING";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTableSqlStatement.getColumnList().getColumn(0).toString().equalsIgnoreCase("\"MAX_ACT_FINAL_SCORE\""));
        TSelectSqlStatement select = createTableSqlStatement.getSubQuery();
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("USA.PAY_BEHV_LKUP"));
       // TColumnDefinition columnDefinition = createTableSqlStatement.getColumnList().getColumn(1);
       // assertTrue(columnDefinition.getColumnName().toString().equalsIgnoreCase("COURT_DESC"));
       // assertTrue(columnDefinition.getDatatype().toString().equalsIgnoreCase("VARCHAR2(255)"));
    }

}
