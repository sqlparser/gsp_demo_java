package gudusoft.gsqlparser.joinConvertTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.demos.joinConvert.JoinConverter;
import junit.framework.TestCase;

/**
 * MantisBT 4685: on a 100-file corpus of real Oracle (+) statements the
 * converter crashed (NPE on argument-less function calls), failed multi-table
 * and mixed-join chains ("This table has no join condition"), silently
 * returned success while (+) remained in the output, or emitted SQL that no
 * longer parsed.
 *
 * The fixes tested here:
 * - null-safe traversal of argument-less calls (SYSDATE, USER, TRIM-style
 *   argument structures that are not in TFunctionCall.getArgs())
 * - (+) markers are stripped only when their predicate is actually moved
 *   into an ON clause, never at collection time
 * - (+) inside BETWEEN operands and inside function calls is converted
 * - tables with no join condition are CROSS JOINed (equivalent for comma
 *   join + WHERE) instead of failing
 * - CTE bodies are converted
 * - contract: convert() never returns 0 while (+) survives in the output,
 *   and never returns 0 with output that fails to re-parse
 *
 * Row-level equivalence of every conversion shape below was verified against
 * a live Oracle 23ai (original vs converted, identical result sets including
 * unmatched and NULL-key rows).
 */
public class JoinConverterMantis4685Test extends TestCase {

    private static JoinConverter convertOk(String sql) {
        JoinConverter jc = new JoinConverter(sql, EDbVendor.dbvoracle);
        int rc = jc.convert();
        assertEquals(jc.getErrorMessage(), 0, rc);
        String out = jc.getQuery().trim();
        assertFalse("(+) must not survive a successful conversion:\n" + out,
                out.matches("(?s).*\\(\\s*\\+\\s*\\).*"));
        TGSqlParser parser = new TGSqlParser(EDbVendor.dbvoracle);
        parser.sqltext = out;
        assertEquals("converted SQL must re-parse:\n" + out, 0, parser.parse());
        return jc;
    }

    public void testArglessFunctionInPlusPredicateNoNpe() {
        // used to throw: getArgs() is null for SYSDATE
        JoinConverter jc = convertOk(
            "SELECT a.id FROM ta a, tb b " +
            "WHERE a.id = b.a_id(+) AND b.created(+) < SYSDATE");
        assertTrue(jc.getQuery().contains("left outer join"));
    }

    public void testPlusInsideTrimBothSides() {
        // TRIM keeps its operand outside getArgs(); used to NPE / not convert
        JoinConverter jc = convertOk(
            "SELECT a.id FROM ta a, tb b " +
            "WHERE trim(b.x(+)) = trim(a.x)");
        assertTrue(jc.getQuery().contains("left outer join tb b on trim(b.x) = trim(a.x)"));
    }

    public void testPlusInsideNestedFunctionInBetween() {
        // 077 shape: trunc(SYSDATE) BETWEEN nvl(trunc(b.s(+)),...) AND nvl(...)
        JoinConverter jc = convertOk(
            "SELECT a.id FROM ta a, tb b WHERE a.id = b.a_id(+) " +
            "AND trunc(SYSDATE) BETWEEN nvl(trunc(b.s_date(+)), SYSDATE - 1) " +
            "AND nvl(trunc(b.e_date(+)), SYSDATE + 1)");
        assertTrue(jc.getQuery().contains("BETWEEN"));
        assertTrue(jc.getQuery().contains("left outer join"));
    }

    public void testBetweenWithPlusOperands() {
        JoinConverter jc = convertOk(
            "SELECT a.id FROM ta a, tb b WHERE a.id = b.a_id(+) " +
            "AND DATE '2024-06-15' BETWEEN b.s_date(+) AND b.e_date(+)");
        String out = jc.getQuery();
        assertTrue(out.contains("left outer join"));
        assertTrue(out.contains("BETWEEN b.s_date AND b.e_date"));
    }

    public void testStandaloneTableBecomesCrossJoin() {
        // 056/095 shape: a table with no join condition at all
        JoinConverter jc = convertOk(
            "SELECT a.id, d.id FROM ta a, tb b, td d " +
            "WHERE a.id = b.a_id(+) AND d.tag = 'k1'");
        String out = jc.getQuery();
        assertTrue(out.contains("cross join td d"));
        // the filter stays in the WHERE clause
        assertTrue(out.contains("WHERE d.tag = 'k1'")
                || out.matches("(?s).*WHERE\\s+d.tag = 'k1'.*"));
    }

    public void testCteBodyIsConverted() {
        // 049/053 shape: (+) inside a WITH body was silently skipped
        JoinConverter jc = convertOk(
            "WITH src AS (SELECT a.id AS aid, b.id AS bid FROM ta a, tb b " +
            "WHERE a.id = b.a_id(+)) SELECT aid FROM src");
        assertTrue(jc.getQuery().contains("left outer join"));
    }

    public void testSingleTableCorrelatedPlusIsStripped() {
        // 001 shape: (+) with no join partner in the block is an Oracle no-op
        convertOk(
            "SELECT a.id, (SELECT MAX(b.id) FROM tb b WHERE b.a_id(+) = a.id) mx " +
            "FROM ta a");
    }

    public void testSameTablePlusFilterGoesToOnClause() {
        JoinConverter jc = convertOk(
            "SELECT a.id FROM ta a, tb b " +
            "WHERE b.a_id(+) = a.id AND b.f(+) = b.g(+)");
        String out = jc.getQuery();
        assertTrue(out.contains("b.f = b.g"));
        // it must be in the ON clause, not in a WHERE
        assertFalse(out.toUpperCase().contains("WHERE"));
    }

    public void testChainedOuterJoins() {
        JoinConverter jc = convertOk(
            "SELECT a.id FROM ta a, tb b, tc c " +
            "WHERE a.id = b.a_id(+) AND b.id = c.b_id(+)");
        String out = jc.getQuery();
        assertTrue(out.indexOf("left outer join tb b") <
                   out.indexOf("left outer join tc c"));
    }

    public void testUnconvertibleUnqualifiedPlusFailsLoudly() {
        // 074 shape: fully unqualified (+) columns over several tables can
        // not be attributed to a table without schema metadata — the
        // converter must fail with a diagnostic, never succeed silently
        String sql = "SELECT stw_class FROM r5activities, r5events, r5patternsequences " +
            "WHERE evt_code = act_event AND psq_code (+) = evt_mp";
        JoinConverter jc = new JoinConverter(sql, EDbVendor.dbvoracle);
        assertTrue(jc.convert() != 0);
        assertTrue(jc.getErrorMessage().contains("(+)"));
    }

    public void testAnsiOnlyBlockIsLeftUntouched() {
        // 088 shape: a pure ANSI FROM must not be rebuilt (rebuilding a
        // multi-join-item chain mangled it)
        String sql = "SELECT s.id FROM (SELECT i.id FROM v_assoc v " +
            "left join scores i ON v.sc_id = i.sc_id " +
            "left join fin f ON f.d_id = i.d_id WHERE i.d_id IS NOT NULL) s, tq q " +
            "WHERE s.id = q.s_id(+)";
        JoinConverter jc = convertOk(sql);
        String out = jc.getQuery();
        // inner ANSI joins survive exactly once
        assertTrue(out.contains("left join scores i"));
        assertTrue(out.contains("left join fin f"));
        assertTrue(out.contains("left outer join tq q"));
    }

    public void testSameTableFilterDoesNotDictateDirection() {
        // review finding: the filter's jt must not override the real edge —
        // a is the optional side here, so the join must preserve b
        JoinConverter jc = convertOk(
            "SELECT a.id FROM ta a, tb b " +
            "WHERE a.f(+) = a.g(+) AND a.id(+) = b.a_id");
        assertTrue(jc.getQuery().contains("right outer join"));
    }

    public void testWrappedMarkerAttributedToMarkedTable() {
        // NVL(a.fallback, b.x(+)) = a.x — the marked table is b, so b is
        // the optional (joined) side, not a
        JoinConverter jc = convertOk(
            "SELECT a.id FROM ta a, tb b WHERE NVL(a.fallback, b.x(+)) = a.x");
        assertTrue(jc.getQuery().contains("left outer join tb b"));
    }

    public void testTopLevelPlusNestedMarkerBothStripped() {
        // b.s_date(+) <= NVL(b.e_date(+), SYSDATE): both markers belong to b
        // and must be stripped together when the predicate moves to ON
        JoinConverter jc = convertOk(
            "SELECT a.id FROM ta a, tb b WHERE a.id = b.a_id(+) " +
            "AND b.s_date(+) <= NVL(b.e_date(+), SYSDATE)");
        assertTrue(jc.getQuery().contains("NVL(b.e_date, SYSDATE)"));
    }

    public void testSingleTableWrappedMarkerStripped() {
        // trim(b.x(+)) = a.x in a single-table correlated block: the marker
        // sits inside a function argument and is still a no-op to strip
        convertOk(
            "SELECT a.id, (SELECT MAX(b.id) FROM tb b WHERE trim(b.x(+)) = a.x) mx " +
            "FROM ta a");
    }

    public void testOnReferencingCrossJoinedTableFailsLoudly() {
        // NVL(a.x, c.x) = b.x(+): c has no join condition of its own, but the
        // ON needs it in scope — emitting "... left join b on NVL(a.x,c.x)=b.x
        // cross join c" would be invalid-scope SQL, so this must error
        String sql = "SELECT a.id FROM ta a, tb b, tc c " +
            "WHERE NVL(a.x, c.x) = b.x(+)";
        JoinConverter jc = new JoinConverter(sql, EDbVendor.dbvoracle);
        assertTrue(jc.convert() != 0);
        assertTrue(jc.getErrorMessage().contains("unsupported (+) shape"));
    }

    public void testRescuedJoinAnchoredAfterChain() {
        // b is tied only by an unresolvable-side condition; its rescued join
        // must come after the a-c chain so the ON stays in scope
        JoinConverter jc = convertOk(
            "SELECT a.id FROM ta a, tb b, tc c WHERE b.y(+) = x AND a.id = c.aid");
        String out = jc.getQuery();
        assertTrue(out.indexOf("join tc c") < out.indexOf("join tb b"));
    }

    public void testDanglingPlusFilterMakesMarkedTableOptional() {
        // NVL(a.x(+),0)=1 with no real a-b edge: a carries the marker, so a
        // is the optional side — must be a right outer join (verified
        // row-identical on live Oracle; a left join returns extra rows)
        JoinConverter jc = convertOk(
            "SELECT a.id FROM ta a, tb b WHERE NVL(a.x(+),0)=1");
        assertTrue(jc.getQuery().contains("right outer join"));
    }

    public void testRescueAnchoredToLastAddedTable() {
        // chain adds d then b; rescued c must come after BOTH so an
        // unresolvable reference in its ON stays in scope
        JoinConverter jc = convertOk(
            "SELECT a.id FROM ta a, tc c, tb b, td d " +
            "WHERE a.id=d.aid AND d.bid=b.id AND c.y(+)=x");
        String out = jc.getQuery();
        assertTrue(out.indexOf("join tb b") < out.indexOf("join tc c"));
    }

    public void testCompactReferenceToCrossJoinedTableFailsLoudly() {
        // NVL(a.x,c.x)=b.x(+) written WITHOUT spaces: the reference to c is
        // found from the expression tree, not from text tokenizing
        String sql = "SELECT a.id FROM ta a, tb b, tc c WHERE NVL(a.x,c.x)=b.x(+)";
        JoinConverter jc = new JoinConverter(sql, EDbVendor.dbvoracle);
        assertTrue(jc.convert() != 0);
        assertTrue(jc.getErrorMessage().contains("unsupported (+) shape"));
    }

    public void testLocalTableShadowsParentOfSameAlias() {
        // a CTE/subquery whose FROM reuses an alias also present in an outer
        // scope must join its LOCAL table, not pull in the outer one
        // (regression: hcasa was both inner joined [parent] and cross
        // joined [local] in the same FROM)
        JoinConverter jc = convertOk(
            "SELECT o.id FROM (SELECT hcasa.id FROM sites hcasa, accts hca " +
            "WHERE hca.acct_id = hcasa.acct_id AND hca.f = 'Y') o, " +
            "sites hcasa WHERE o.id = hcasa.id(+)");
        String out = jc.getQuery();
        assertFalse(out.contains("cross join sites hcasa"));
    }

    public void testDeepOperandChainsDoNotOverflow() {
        // stays under the 10,000-byte input cap of the published trial
        // parser so a default (non -Plocal) run also passes; the operand
        // walkers are iterative, so depth is bounded by heap, not stack
        StringBuilder sb = new StringBuilder(
            "SELECT a.id FROM ta a, tb b WHERE a.id = b.a_id(+) AND b.x(+) = 'p'");
        for (int i = 0; i < 900; i++) {
            sb.append(" || 'x").append(i).append("'");
        }
        assertTrue(sb.length() < 10000);
        convertOk(sb.toString());
    }

    public void testMssqlStarEqualsStillConverts() {
        // regression guard for the SQL Server *= path
        String sql = "SELECT * FROM table1 t1, table2 t2 WHERE t1.f1 *= t2.f1";
        JoinConverter jc = new JoinConverter(sql, EDbVendor.dbvmssql);
        assertEquals(0, jc.convert());
        assertTrue(jc.getQuery().contains("left outer join"));
    }
}
