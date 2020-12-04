package postgresql;
/*
 * Date: 11-6-22
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TCTE;
import gudusoft.gsqlparser.nodes.TCTEList;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import junit.framework.TestCase;

public class testCTE extends TestCase {

    public void testInsert(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "WITH upd AS (\n" +
                "  UPDATE employees SET sales_count = sales_count + 1 WHERE id =\n" +
                "    (SELECT sales_person FROM accounts WHERE name = 'Acme Corporation')\n" +
                "    RETURNING *\n" +
                ")\n" +
                "INSERT INTO employees_log SELECT *, current_timestamp FROM upd;";

        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insertSqlStatement = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        //System.out.println(insertSqlStatement.getInsertToken().toString());

        TCTEList cteList = insertSqlStatement.getCteList();
        assertTrue(cteList.size() == 1);

        TCTE cte = cteList.getCTE(0);
        assertTrue(cte.getTableName().toString().equalsIgnoreCase("upd"));

        TUpdateSqlStatement updateSqlStatement = cte.getUpdateStmt();
        assertTrue(updateSqlStatement.getTargetTable().toString().equalsIgnoreCase("employees"));

        TResultColumnList resultColumnList = updateSqlStatement.getResultColumnList();
        assertTrue(resultColumnList.size() == 1);

        TResultColumn resultColumn = resultColumnList.getResultColumn(0);
        assertTrue(resultColumn.getExpr().getLeftOperand().toString().equalsIgnoreCase("sales_count"));
        assertTrue(resultColumn.getExpr().getRightOperand().toString().equalsIgnoreCase("sales_count + 1"));

    }

}
