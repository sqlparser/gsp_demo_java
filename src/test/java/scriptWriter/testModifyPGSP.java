package scriptWriter;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.*;
import junit.framework.TestCase;

public class testModifyPGSP extends TestCase
{
    public void testModifyBody() {
        String sql = "create or replace procedure yb_stage.transfer()\n" +
                "language plpgsql  \n" +
                "as $$\n" +
                "begin\n" +
                "  update yb_stage.widetabletest2  \n" +
                "  set r9 = 1000\n" +
                "  where pk_id = 206041839059439594;\n" +
                "end;\n" +
                "$$";

        TGSqlParser parser = new TGSqlParser(EDbVendor.dbvpostgresql);
        parser.sqltext = sql;

        int ret = parser.parse();
        if (ret != 0) {
            System.err.println("Error parsing:" + parser.getErrormessage());
            assertTrue(false);
        }

        TCustomSqlStatement tCustomSqlStatement = parser.getSqlstatements().get(0);
        TCreateProcedureStmt stmt = (TCreateProcedureStmt) tCustomSqlStatement;
        TStatementList bodyStatements = stmt.getBodyStatements();
        TUpdateSqlStatement statement = (TUpdateSqlStatement) bodyStatements.get(0);
        TResultColumn resultColumn = statement.getResultColumnList().getResultColumn(0);
        TExpression expr = resultColumn.getExpr();
        String oldBody = statement.toString();

        //update value
        expr.getRightOperand().setString("2000");

        //Replace sqltext in ${sqltext}$
        String newSql = tCustomSqlStatement.toString().replace(oldBody, statement.toString());
        assertEquals(newSql, "create or replace procedure yb_stage.transfer()\n" +
                "language plpgsql  \n" +
                "as $$\n" +
                "begin\n" +
                "  update yb_stage.widetabletest2  \n" +
                "  set r9 = 2000\n" +
                "  where pk_id = 206041839059439594;\n" +
                "end;\n" +
                "$$");

    }
}
