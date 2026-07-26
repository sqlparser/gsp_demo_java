package gudusoft.gsqlparser.demos;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;

/**
 * Demonstrates how to compute vendor-aware, format-insensitive SQL hashes and
 * normalized SQL text from parsed statements using TCustomSqlStatement.
 *
 * What this demo shows:
 * - Parsing a multi-statement SQL script
 * - For each statement (and nested subqueries):
 *   - Get a stable queryId (hierarchical id)
 *   - Get default sqlHash (Profile A: identity-safe)
 *   - Compute sqlHash with Profile B (grouping-friendly, masks date literals)
 *   - View normalized SQL text for both profiles
 * - How to build a statementKey = sqlHash + "#" + queryId
 */
public class SqlHashDemo {

	public static void main(String[] args) {
		String sql = String.join("\n",
				"-- Example 1: simple predicate with a date literal",
				"SELECT id, name FROM users WHERE created_at >= '2025-09-14';",
				"",
				"-- Example 2: INSERT ... SELECT with transform and filter",
				"INSERT INTO tgt (id, name)",
				"SELECT u.id, UPPER(u.name)",
				"FROM users u",
				"WHERE u.active = 1;",
				"",
				"-- Example 3: nested subqueries",
				"SELECT * FROM (",
				"  SELECT id",
				"  FROM t",
				"  WHERE EXISTS (SELECT 1 FROM u)",
				") a;"
		);

		// Choose a vendor to drive keyword set and identifier case rules
		EDbVendor vendor = EDbVendor.dbvpostgresql;
		TGSqlParser parser = new TGSqlParser(vendor);
		parser.setSqltext(sql);
		int ret = parser.parse();
		if (ret != 0) {
			System.out.println("Parse error: " + parser.getErrormessage());
			return;
		}

		System.out.println("Vendor: " + vendor);
		for (int i = 0; i < parser.sqlstatements.size(); i++) {
			TCustomSqlStatement stmt = parser.sqlstatements.get(i);
			printStatement(stmt, "");
		}
	}

	private static void printStatement(TCustomSqlStatement stmt, String indent) {
		System.out.println(indent + "Statement type: " + stmt.sqlstatementtype);
		System.out.println(indent + "queryId:        " + safe(stmt.getQueryId()));

		// Default sqlHash uses Profile A (identity-safe)
		String hashA = stmt.getSqlHash(false);
		System.out.println(indent + "sqlHash (A):    " + hashA);

		// Profile B (grouping-friendly): masks date/time string literals to '1970-01-01'
		String normB = stmt.toNormalizedSql(TCustomSqlStatement.SqlNormalizationProfile.GROUPING_FRIENDLY);
		String hashB = stmt.computeSqlHash(TCustomSqlStatement.SqlNormalizationProfile.GROUPING_FRIENDLY, "sqlHash.norm.v1");
		System.out.println(indent + "sqlHash (B):    " + hashB);

		// Show normalized SQL for both profiles
		String normA = stmt.toNormalizedSql(TCustomSqlStatement.SqlNormalizationProfile.IDENTITY_SAFE);
		System.out.println(indent + "normalized (A): " + preview(normA));
		System.out.println(indent + "normalized (B): " + preview(normB));

		// A typical grouping key for lineage edges
		System.out.println(indent + "statementKey:   " + hashA + "#" + safe(stmt.getQueryId()));

		// Recurse into nested statements (subqueries)
		if (stmt.getStatements() != null && stmt.getStatements().size() > 0) {
			for (int j = 0; j < stmt.getStatements().size(); j++) {
				TCustomSqlStatement child = stmt.getStatements().get(j);
				System.out.println(indent + "- Subquery:");
				printStatement(child, indent + "  ");
			}
		}
	}

	private static String safe(String s) {
		return s == null ? "<null>" : s;
	}

	private static String preview(String s) {
		if (s == null) return "<null>";
		String trimmed = s.trim().replaceAll("\n+", " ");
		return trimmed.length() > 140 ? trimmed.substring(0, 140) + "..." : trimmed;
	}
}


