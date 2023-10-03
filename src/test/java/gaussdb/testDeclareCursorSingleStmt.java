package gaussdb;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDeclareCursorStmt;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testDeclareCursorSingleStmt extends TestCase {

    public void testCreateFunction(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgaussdb);
        sqlparser.sqltext = "DECLARE foo CURSOR FOR SELECT 1 UNION SELECT 2;";

        assertTrue(sqlparser.parse() == 0);
        TDeclareCursorStmt stmt = (TDeclareCursorStmt) sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getCursorName().toString().equalsIgnoreCase("foo"));
        TSelectSqlStatement subQuery = stmt.getSubQuery();
        assertTrue(subQuery.isCombinedQuery());
        assertTrue(subQuery.getLeftStmt().getResultColumnList().getResultColumn(0).toString().equalsIgnoreCase("1"));
        assertTrue(subQuery.getRightStmt().getResultColumnList().getResultColumn(0).toString().equalsIgnoreCase("2"));

    }
}
