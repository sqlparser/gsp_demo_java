package gudusoft.gsqlparser.demosTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.demos.checksyntax.OfflineSyntaxCheck;
import junit.framework.TestCase;

public class OfflineSyntaxCheckTest extends TestCase {

    public void testAcceptsValidSqlAndCountsStatements() {
        OfflineSyntaxCheck.ValidationResult result = OfflineSyntaxCheck.validate(
                OfflineSyntaxCheck.SAMPLE_SQL, EDbVendor.dbvoracle);

        assertTrue(result.getErrorMessage(), result.isValid());
        assertEquals(1, result.getStatementCount());
        assertEquals(EDbVendor.dbvoracle, result.getVendor());
    }

    public void testRejectsInvalidSqlWithParserDiagnostic() {
        OfflineSyntaxCheck.ValidationResult result = OfflineSyntaxCheck.validate(
                "SELECT o.order_id,\nFROM sales.orders o;", EDbVendor.dbvoracle);

        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().trim().length() > 0);
        assertEquals(0, result.getStatementCount());
    }

    public void testDialectSelectionChangesTheResult() {
        String sqlServerSql =
                "SELECT TOP 5 [order_id] FROM [sales].[orders] ORDER BY [created_at] DESC;";

        OfflineSyntaxCheck.ValidationResult sqlServer = OfflineSyntaxCheck.validate(
                sqlServerSql, EDbVendor.dbvmssql);
        OfflineSyntaxCheck.ValidationResult oracle = OfflineSyntaxCheck.validate(
                sqlServerSql, EDbVendor.dbvoracle);

        assertTrue(sqlServer.getErrorMessage(), sqlServer.isValid());
        assertFalse("Oracle grammar must reject SQL Server TOP/bracket syntax", oracle.isValid());
    }

    public void testValidatesTheCompleteScript() {
        OfflineSyntaxCheck.ValidationResult result = OfflineSyntaxCheck.validate(
                "SELECT 1 FROM dual; SELECT 2 FROM dual;", EDbVendor.dbvoracle);

        assertTrue(result.getErrorMessage(), result.isValid());
        assertEquals(2, result.getStatementCount());
    }

    public void testRejectsEmptyInputBeforeParsing() {
        OfflineSyntaxCheck.ValidationResult result = OfflineSyntaxCheck.validate(
                "  \n  ", EDbVendor.dbvoracle);

        assertFalse(result.isValid());
        assertEquals("SQL input is empty.", result.getErrorMessage());
    }

    public void testRejectsUnknownDialect() {
        try {
            OfflineSyntaxCheck.resolveVendor("not-a-database");
            fail("unknown dialect must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Unsupported database dialect"));
        }
    }
}
