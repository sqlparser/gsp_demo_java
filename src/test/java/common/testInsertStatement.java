package common;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EInsertSource;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testInsertStatement  extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "INSERT INTO tbl_temp2 (\"date\") SELECT tbl_temp1.date FROM tbl_temp1 WHERE tbl_temp1.fld_order_id > 100";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstinsert);
        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "INSERT INTO test_13 (id, name) SELECT * FROM test_111 WHERE NOT EXISTS (SELECT *   FROM test_2  WHERE test_111.id = test_2.id);";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstinsert);
        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement subquery = insert.getSubQuery();
        assertTrue(subquery.getTables().getTable(0).toString().equalsIgnoreCase("test_111"));
    }


    public void testInsertExec(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "INSERT into dbo.targetTable(product_name, model_year, list_price) exec dbo.udfProductInYear";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstinsert);
        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        // This means the source from an exec statement
        assertTrue(insert.getInsertSource() == EInsertSource.execute);
        assertTrue(insert.getExecuteStmt().getModuleName().toString().equalsIgnoreCase("dbo.udfProductInYear"));
    }

}
