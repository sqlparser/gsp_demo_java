package gudusoft.gsqlparser.db2Tesst;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCommonBlock;
import junit.framework.TestCase;

import static gudusoft.gsqlparser.ESqlStatementType.sstplsql_nullstmt;

public class testBlock extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);

        sqlparser.sqltext = "BEGIN\n" +
                "    null;\n" +
                "END;";

        assertTrue(sqlparser.parse() == 0);
        TCommonBlock block = (TCommonBlock)sqlparser.getSqlstatements().get(0);
        assertTrue(block.getBodyStatements().size() == 1);
        assertTrue(block.getBodyStatements().get(0).sqlstatementtype == sstplsql_nullstmt);

    }
}
