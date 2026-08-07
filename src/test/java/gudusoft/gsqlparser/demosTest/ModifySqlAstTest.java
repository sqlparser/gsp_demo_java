package gudusoft.gsqlparser.demosTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.demos.modifySqlAst.ModifySqlAst;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class ModifySqlAstTest extends TestCase {

    public void testRewritesProjectionAndPreservesBooleanMeaning() {
        ModifySqlAst.RewriteResult result =
                ModifySqlAst.rewrite(ModifySqlAst.SAMPLE_SQL, EDbVendor.dbvoracle);

        TGSqlParser parser = new TGSqlParser(EDbVendor.dbvoracle);
        parser.sqltext = result.getRewrittenSql();
        assertEquals(parser.getErrormessage(), 0, parser.parse());
        assertEquals(1, parser.sqlstatements.size());

        TSelectSqlStatement select =
                (TSelectSqlStatement) parser.sqlstatements.get(0);
        TResultColumnList columns = select.getResultColumnList();
        assertEquals(3, columns.size());
        for (int index = 0; index < columns.size(); index++) {
            assertFalse("restricted projection must be removed",
                    "internal_note".equalsIgnoreCase(
                            columns.getResultColumn(index).getColumnNameOnly()));
        }

        TExpression condition = select.getWhereClause().getCondition();
        assertEquals(EExpressionType.logical_and_t, condition.getExpressionType());
        assertEquals(EExpressionType.parenthesis_t,
                condition.getLeftOperand().getExpressionType());
        assertEquals(EExpressionType.logical_or_t,
                condition.getLeftOperand().getLeftOperand().getExpressionType());
        assertEquals("o.tenant_id = ?", condition.getRightOperand().toScript());
        assertEquals("o.internal_note", result.getRemovedProjection());
    }

    public void testRejectsMultipleStatements() {
        try {
            ModifySqlAst.rewrite("SELECT 1 FROM dual; SELECT 2 FROM dual",
                    EDbVendor.dbvoracle);
            fail("multiple statements must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("exactly one statement"));
        }
    }

    public void testAddsWhereClauseWhenInputHasNone() {
        ModifySqlAst.RewriteResult result = ModifySqlAst.rewrite(
                "SELECT o.order_id FROM sales.orders o", EDbVendor.dbvoracle);

        TGSqlParser parser = new TGSqlParser(EDbVendor.dbvoracle);
        parser.sqltext = result.getRewrittenSql();
        assertEquals(parser.getErrormessage(), 0, parser.parse());

        TSelectSqlStatement select =
                (TSelectSqlStatement) parser.sqlstatements.get(0);
        assertEquals("o.tenant_id = ?",
                select.getWhereClause().getCondition().toScript());
    }

    public void testRemovesEveryRestrictedProjection() {
        ModifySqlAst.RewriteResult result = ModifySqlAst.rewrite(
                "SELECT o.internal_note, o.order_id, o.internal_note AS note "
                        + "FROM sales.orders o", EDbVendor.dbvoracle);

        TGSqlParser parser = new TGSqlParser(EDbVendor.dbvoracle);
        parser.sqltext = result.getRewrittenSql();
        assertEquals(parser.getErrormessage(), 0, parser.parse());

        TSelectSqlStatement select =
                (TSelectSqlStatement) parser.sqlstatements.get(0);
        assertEquals(1, select.getResultColumnList().size());
        assertEquals("order_id",
                select.getResultColumnList().getResultColumn(0).getColumnNameOnly());
    }

    public void testRejectsNonSelectStatement() {
        try {
            ModifySqlAst.rewrite("DELETE FROM sales.orders",
                    EDbVendor.dbvoracle);
            fail("non-SELECT statements must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("must be a SELECT"));
        }
    }
}
