package gudusoft.gsqlparser.demos.modifySqlAst;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.nodes.TWhereClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

/**
 * Demonstrates a small, fail-closed SQL policy gate built on the GSP AST.
 *
 * <p>The policy accepts one SELECT statement, removes the sensitive
 * {@code internal_note} projection when present, adds a server-controlled
 * tenant predicate, regenerates SQL, and parses the result again before it can
 * be handed to a database driver.</p>
 */
public final class ModifySqlAst {

    public static final String SAMPLE_SQL =
            "SELECT o.order_id,\n"
                    + "       o.customer_id,\n"
                    + "       o.total_amount,\n"
                    + "       o.internal_note\n"
                    + "FROM sales.orders o\n"
                    + "WHERE o.status = 'OPEN' OR o.status = 'PENDING'\n"
                    + "ORDER BY o.created_at DESC";

    private static final String RESTRICTED_COLUMN = "internal_note";
    private static final String TENANT_PREDICATE = "o.tenant_id = ?";

    private ModifySqlAst() {
    }

    public static void main(String[] args) {
        RewriteResult result = rewrite(SAMPLE_SQL, EDbVendor.dbvoracle);

        System.out.println("Original SQL:");
        System.out.println(result.getOriginalSql());
        System.out.println();
        System.out.println("Policy decisions:");
        System.out.println("- Accepted exactly one SELECT statement");
        System.out.println("- Removed restricted projection: " + result.getRemovedProjection());
        System.out.println("- Added server-controlled tenant predicate: " + TENANT_PREDICATE);
        System.out.println();
        System.out.println("Rewritten SQL:");
        System.out.println(result.getRewrittenSql());
        System.out.println();
        System.out.println("Validation: regenerated SQL parsed successfully as one SELECT statement.");
    }

    /**
     * Applies the demo policy and returns SQL regenerated from the modified AST.
     * The tenant value remains a bind placeholder and must be supplied by trusted
     * application code when the SQL is executed.
     */
    public static RewriteResult rewrite(String sql, EDbVendor vendor) {
        TSelectSqlStatement select = parseOneSelect(sql, vendor, "Input SQL");

        String removedProjection = removeRestrictedProjection(select);
        addTenantPredicate(select, vendor);

        String rewrittenSql = select.toScript();
        parseOneSelect(rewrittenSql, vendor, "Regenerated SQL");

        return new RewriteResult(sql, rewrittenSql, removedProjection);
    }

    private static TSelectSqlStatement parseOneSelect(String sql,
                                                      EDbVendor vendor,
                                                      String description) {
        TGSqlParser parser = new TGSqlParser(vendor);
        parser.sqltext = sql;

        if (parser.parse() != 0) {
            throw new IllegalArgumentException(description + " did not parse: "
                    + parser.getErrormessage());
        }
        if (parser.sqlstatements.size() != 1) {
            throw new IllegalArgumentException(description
                    + " must contain exactly one statement.");
        }
        if (parser.sqlstatements.get(0).sqlstatementtype != ESqlStatementType.sstselect) {
            throw new IllegalArgumentException(description + " must be a SELECT statement.");
        }

        return (TSelectSqlStatement) parser.sqlstatements.get(0);
    }

    private static String removeRestrictedProjection(TSelectSqlStatement select) {
        TResultColumnList columns = select.getResultColumnList();
        String removedProjection = null;

        for (int index = columns.size() - 1; index >= 0; index--) {
            TResultColumn column = columns.getResultColumn(index);
            if (RESTRICTED_COLUMN.equalsIgnoreCase(column.getColumnNameOnly())) {
                if (removedProjection == null) {
                    removedProjection = column.toScript();
                }
                columns.removeResultColumn(index);
            }
        }

        return removedProjection == null ? "not present" : removedProjection;
    }

    private static void addTenantPredicate(TSelectSqlStatement select, EDbVendor vendor) {
        TExpression tenantCondition = TGSqlParser.parseExpression(vendor, TENANT_PREDICATE);
        if (tenantCondition == null) {
            throw new IllegalStateException("The configured tenant predicate did not parse.");
        }

        TWhereClause whereClause = select.getWhereClause();
        if (whereClause == null || whereClause.getCondition() == null) {
            select.addWhereClause(TENANT_PREDICATE);
            return;
        }

        // Parenthesize the existing condition before adding AND. Without this
        // node, "A OR B" plus a tenant filter could become "A OR (B AND tenant)".
        TExpression originalCondition = whereClause.getCondition();
        TExpression parenthesized =
                new TExpression(EExpressionType.parenthesis_t, originalCondition, null);
        TExpression combined = new TExpression(
                EExpressionType.logical_and_t, parenthesized, tenantCondition);
        whereClause.setCondition(combined);
    }

    public static final class RewriteResult {
        private final String originalSql;
        private final String rewrittenSql;
        private final String removedProjection;

        private RewriteResult(String originalSql,
                              String rewrittenSql,
                              String removedProjection) {
            this.originalSql = originalSql;
            this.rewrittenSql = rewrittenSql;
            this.removedProjection = removedProjection;
        }

        public String getOriginalSql() {
            return originalSql;
        }

        public String getRewrittenSql() {
            return rewrittenSql;
        }

        public String getRemovedProjection() {
            return removedProjection;
        }
    }
}
