package gudusoft.gsqlparser.demos.joinConvert;

/*
 * Date: 11-12-1
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JoinConverter {


    enum jointype {
        inner, left, right, cross, join, full
    }

    ;

    class FromClause {

        TTable table;
        TTable joinTable;
        Set<TTable> joinTableOthers;
        String joinClause;
        String condition;
        // lower-cased qualifier names referenced by the ON condition,
        // collected from the expression tree (the text-based
        // joinTableOthers tokenizer misses compact spellings like
        // NVL(a.x,c.x)=b.x)
        Set<String> refTableNames = new HashSet<String>();
    }

    // Collect the table qualifiers (lower-cased) referenced by an
    // expression, iteratively; stops at nested subqueries.
    private void collectQualifierNames(TExpression root, Set<String> out) {
        java.util.Deque<TExpression> stack = new java.util.ArrayDeque<TExpression>();
        if (root != null) {
            stack.push(root);
        }
        while (!stack.isEmpty()) {
            TExpression expr = stack.pop();
            if (expr == null
                    || expr.getExpressionType() == EExpressionType.subquery_t) {
                continue;
            }
            if (expr.getObjectOperand() != null) {
                String prefix = expr.getObjectOperand().getObjectString();
                if (prefix != null && prefix.length() > 0) {
                    out.add(prefix.toLowerCase());
                }
            }
            if (expr.getLeftOperand() != null) stack.push(expr.getLeftOperand());
            if (expr.getRightOperand() != null) stack.push(expr.getRightOperand());
            if (expr.getBetweenOperand() != null) stack.push(expr.getBetweenOperand());
            if (expr.getExprList() != null) {
                for (int i = 0; i < expr.getExprList().size(); i++) {
                    stack.push(expr.getExprList().getExpression(i));
                }
            }
            if (expr.getCaseExpression() != null) {
                TCaseExpression ce = expr.getCaseExpression();
                if (ce.getInput_expr() != null) stack.push(ce.getInput_expr());
                if (ce.getElse_expr() != null) stack.push(ce.getElse_expr());
                if (ce.getWhenClauseItemList() != null) {
                    for (int wi = 0; wi < ce.getWhenClauseItemList().size(); wi++) {
                        TWhenClauseItem wItem = ce.getWhenClauseItemList().getWhenClauseItem(wi);
                        if (wItem.getComparison_expr() != null)
                            stack.push(wItem.getComparison_expr());
                        if (wItem.getReturn_expr() != null)
                            stack.push(wItem.getReturn_expr());
                    }
                }
            }
            if (expr.getFunctionCall() != null) {
                TFunctionCall fc = expr.getFunctionCall();
                if (fc.getArgs() != null) {
                    for (int i = 0; i < fc.getArgs().size(); i++) {
                        stack.push(fc.getArgs().getExpression(i));
                    }
                }
                if (fc.getTrimArgument() != null) {
                    if (fc.getTrimArgument().getStringExpression() != null)
                        stack.push(fc.getTrimArgument().getStringExpression());
                    if (fc.getTrimArgument().getTrimCharacter() != null)
                        stack.push(fc.getTrimArgument().getTrimCharacter());
                }
            }
        }
    }

    class JoinCondition {

        public String lefttable, righttable, leftcolumn, rightcolumn;
        public jointype jt;
        public Boolean used;
        public TExpression lexpr, rexpr, expr;
        // the table carrying the (+) markers (the optional side), when it is
        // unambiguous; used to fix the join direction for one-sided
        // conditions regardless of which side of a pair the table lands on
        public String markedTable;
        // (+) tokens of this condition. They are removed only when the
        // condition is actually moved into an ON clause (markUsed), so an
        // unconvertible predicate keeps its (+) and the post-conversion
        // residue check can report it instead of silently dropping it.
        public List<TSourceToken> plusTokens = new ArrayList<TSourceToken>();
    }

    // For a one-sided (+) condition attached to the (lefttable, righttable)
    // pair: the (+)-marked table is the OPTIONAL side, wherever it sits.
    // "left outer join X" preserves the left/anchor side (X optional),
    // "right outer join X" preserves X (the anchor side optional).
    private void orientByMarkedTable(JoinCondition jc, TTable lefttable,
                                     TTable righttable) {
        if (jc.markedTable == null) {
            return;
        }
        if (isNameOrAliasOfTable(righttable, jc.markedTable)) {
            jc.jt = jointype.left;
        } else if (isNameOrAliasOfTable(lefttable, jc.markedTable)) {
            jc.jt = jointype.right;
        }
    }

    // Does any unconsumed (+) condition involve this table? Such a table
    // must go through real outer-join handling, never a bare cross join.
    private boolean hasPendingPlusCondition(TTable table,
                                            ArrayList<JoinCondition> jrs) {
        for (int i = 0; i < jrs.size(); i++) {
            JoinCondition jc = jrs.get(i);
            if (!jc.used && !jc.plusTokens.isEmpty()
                    && (isNameOrAliasOfTable(table, jc.lefttable)
                    || isNameOrAliasOfTable(table, jc.righttable)
                    || isNameOrAliasOfTable(table, jc.markedTable))) {
                return true;
            }
        }
        return false;
    }

    // Mark a join condition as consumed and only then strip its (+) markers.
    private void markUsed(JoinCondition jc) {
        jc.used = true;
        for (int i = 0; i < jc.plusTokens.size(); i++) {
            jc.plusTokens.get(i).setString("");
        }
    }

    class getJoinConditionVisitor implements IExpressionVisitor {

        Boolean isFirstExpr = true;
        ArrayList<JoinCondition> jrs = new ArrayList<JoinCondition>();

        public ArrayList<JoinCondition> getJrs() {
            return jrs;
        }

        boolean is_compare_condition(EExpressionType t) {
            return ((t == EExpressionType.simple_comparison_t)
                    || (t == EExpressionType.group_comparison_t)
                    || (t == EExpressionType.in_t) || (t == EExpressionType.pattern_matching_t));
        }

        // comparison predicates already turned into a JoinCondition; guards
        // against the operand-level (branch-3) path collecting the same
        // predicate twice
        Set<TExpression> handledConditions =
                Collections.newSetFromMap(new java.util.IdentityHashMap<TExpression, Boolean>());

        // Collect every (+)-marked expression inside expr, descending into
        // operands and function arguments but never into a nested subquery
        // (whose (+) belongs to that query block). preOrderTraverse cannot be
        // used here: it treats function_t as a leaf, and TRIM-style argument
        // structures are not in getArgs() at all.
        void collectPlusExprs(TExpression root, List<TExpression> out) {
            // iterative: WHERE conditions can be arbitrarily deep AND/OR
            // chains and recursion would risk StackOverflowError
            java.util.Deque<TExpression> stack = new java.util.ArrayDeque<TExpression>();
            if (root != null) {
                stack.push(root);
            }
            while (!stack.isEmpty()) {
                TExpression expr = stack.pop();
                if (expr == null
                        || expr.getExpressionType() == EExpressionType.subquery_t) {
                    continue;
                }
                if (expr.isOracleOuterJoin()) {
                    out.add(expr);
                }
                if (expr.getLeftOperand() != null) stack.push(expr.getLeftOperand());
                if (expr.getRightOperand() != null) stack.push(expr.getRightOperand());
                if (expr.getBetweenOperand() != null) stack.push(expr.getBetweenOperand());
                if (expr.getExprList() != null) {
                    for (int i = 0; i < expr.getExprList().size(); i++) {
                        stack.push(expr.getExprList().getExpression(i));
                    }
                }
                if (expr.getCaseExpression() != null) {
                    TCaseExpression ce = expr.getCaseExpression();
                    if (ce.getInput_expr() != null) stack.push(ce.getInput_expr());
                    if (ce.getElse_expr() != null) stack.push(ce.getElse_expr());
                    if (ce.getWhenClauseItemList() != null) {
                        for (int wi = 0; wi < ce.getWhenClauseItemList().size(); wi++) {
                            TWhenClauseItem wItem = ce.getWhenClauseItemList().getWhenClauseItem(wi);
                            if (wItem.getComparison_expr() != null)
                                stack.push(wItem.getComparison_expr());
                            if (wItem.getReturn_expr() != null)
                                stack.push(wItem.getReturn_expr());
                        }
                    }
                }
                if (expr.getFunctionCall() != null) {
                    TFunctionCall fc = expr.getFunctionCall();
                    if (fc.getArgs() != null) {
                        for (int i = 0; i < fc.getArgs().size(); i++) {
                            stack.push(fc.getArgs().getExpression(i));
                        }
                    }
                    if (fc.getTrimArgument() != null) {
                        if (fc.getTrimArgument().getStringExpression() != null)
                            stack.push(fc.getTrimArgument().getStringExpression());
                        if (fc.getTrimArgument().getTrimCharacter() != null)
                            stack.push(fc.getTrimArgument().getTrimCharacter());
                    }
                }
            }
        }

        TExpression getCompareCondition(TExpression expr) {
            if (is_compare_condition(expr.getExpressionType()))
                return expr;
            TExpression parentExpr = expr.getParentExpr();
            if (parentExpr == null)
                return null;
            return getCompareCondition(parentExpr);
        }

        private void analyzeJoinCondition(TExpression expr,
                                          TExpression parent_expr) {
            TExpression slexpr, srexpr, lc_expr = expr;

            if (lc_expr.getGsqlparser().getDbVendor() == EDbVendor.dbvmssql) {
                if (lc_expr.getExpressionType() == EExpressionType.left_join_t
                        || lc_expr.getExpressionType() == EExpressionType.right_join_t) {
                    analyzeMssqlJoinCondition(lc_expr);
                }
            }

            slexpr = lc_expr.getLeftOperand();
            srexpr = lc_expr.getRightOperand();

            if (is_compare_condition(lc_expr.getExpressionType())) {

                if (slexpr.isOracleOuterJoin() || srexpr.isOracleOuterJoin()) {
                    JoinCondition jr = new JoinCondition();
                    jr.used = false;
                    jr.lexpr = slexpr;
                    jr.rexpr = srexpr;
                    jr.expr = expr;
                    if (slexpr.isOracleOuterJoin()) {
                        // If the plus is on the left, the join type is right
                        // out join.
                        jr.jt = jointype.right;
                    }
                    if (srexpr.isOracleOuterJoin()) {
                        // If the plus is on the right, the join type is left
                        // out join.
                        jr.jt = jointype.left;
                    }
                    // gather every (+) in the predicate — a nested one can
                    // accompany the top-level one, e.g.
                    // b.start_date(+) <= NVL(b.end_date(+), SYSDATE).
                    // Only strip them together when they all belong to one
                    // table; otherwise keep the top-level tokens only and let
                    // the residue check flag the unusual cross-table form.
                    List<TExpression> allPlus = new ArrayList<TExpression>();
                    collectPlusExprs(slexpr, allPlus);
                    collectPlusExprs(srexpr, allPlus);
                    Set<String> allPlusTables = new HashSet<String>();
                    int knownPlusOps = 0;
                    for (TExpression op : allPlus) {
                        String t = getExpressionTable(op);
                        if (t != null) {
                            allPlusTables.add(t.toLowerCase());
                            knownPlusOps++;
                        }
                    }
                    if (allPlusTables.size() == 1 && knownPlusOps == allPlus.size()) {
                        for (TExpression op : allPlus) {
                            jr.plusTokens.add(op.getEndToken());
                        }
                        jr.markedTable = allPlusTables.iterator().next();
                    } else {
                        if (slexpr.isOracleOuterJoin())
                            jr.plusTokens.add(slexpr.getEndToken());
                        if (srexpr.isOracleOuterJoin())
                            jr.plusTokens.add(srexpr.getEndToken());
                    }

                    jr.lefttable = getExpressionTable(slexpr);
                    jr.righttable = getExpressionTable(srexpr);

                    jrs.add(jr);
                    handledConditions.add(lc_expr);
                    // System.out.printf( "join condition: %s\n", expr.toString(
                    // ) );
                } else if ((slexpr.getExpressionType() == EExpressionType.simple_object_name_t)
                        && (!slexpr.toString().startsWith(":"))
                        && (!slexpr.toString().startsWith("?"))
                        && (srexpr.getExpressionType() == EExpressionType.simple_object_name_t)
                        && (!srexpr.toString().startsWith(":"))
                        && (!srexpr.toString().startsWith("?"))) {
                    JoinCondition jr = new JoinCondition();
                    jr.used = false;
                    jr.lexpr = slexpr;
                    jr.rexpr = srexpr;
                    jr.expr = expr;
                    jr.jt = jointype.inner;
                    jr.lefttable = getExpressionTable(slexpr);
                    jr.righttable = getExpressionTable(srexpr);
                    jrs.add(jr);
                    // System.out.printf(
                    // "join condition: %s, %s:%d, %s:%d, %s\n",
                    // expr.toString( ),
                    // slexpr.toString( ),
                    // slexpr.getExpressionType( ),
                    // srexpr.toString( ),
                    // srexpr.getExpressionType( ),
                    // srexpr.getObjectOperand( ).getObjectType( ) );
                } else {
                    // No top-level (+) and not a plain column=column pair.
                    // The (+) may sit inside a function on one side, e.g.
                    //   trim(ssr.qpr_id(+)) = trim(sfm.qpr_id)
                    //   nvl(t.col(+), 'X') = u.col
                    // If exactly one side carries (+) markers and they all
                    // belong to one known table, the predicate is a join
                    // condition of that (optional) table.
                    List<TExpression> plusLeft = new ArrayList<TExpression>();
                    List<TExpression> plusRight = new ArrayList<TExpression>();
                    collectPlusExprs(slexpr, plusLeft);
                    collectPlusExprs(srexpr, plusRight);
                    if (plusLeft.isEmpty() != plusRight.isEmpty()) {
                        List<TExpression> plus = plusLeft.isEmpty() ? plusRight
                                : plusLeft;
                        Set<String> plusTables = new HashSet<String>();
                        int known = 0;
                        for (TExpression op : plus) {
                            String t = getExpressionTable(op);
                            if (t != null) {
                                plusTables.add(t.toLowerCase());
                                known++;
                            }
                        }
                        if (plusTables.size() == 1 && known == plus.size()) {
                            JoinCondition jr = new JoinCondition();
                            jr.used = false;
                            jr.lexpr = slexpr;
                            jr.rexpr = srexpr;
                            jr.expr = expr;
                            jr.jt = plusLeft.isEmpty() ? jointype.left
                                    : jointype.right;
                            for (TExpression op : plus) {
                                jr.plusTokens.add(op.getEndToken());
                            }
                            // the (+) side must be attributed to the MARKED
                            // table, not to whichever table happens to appear
                            // first in the wrapping expression
                            // (NVL(a.fallback, b.x(+)) belongs to b, not a)
                            String plusTable = plusTables.iterator().next();
                            jr.markedTable = plusTable;
                            if (plusLeft.isEmpty()) {
                                jr.lefttable = getExpressionTable(slexpr);
                                jr.righttable = plusTable;
                            } else {
                                jr.lefttable = plusTable;
                                jr.righttable = getExpressionTable(srexpr);
                            }
                            jrs.add(jr);
                            handledConditions.add(lc_expr);
                        }
                    }
                }

            } else if (slexpr != null
                    && slexpr.isOracleOuterJoin()
                    && srexpr == null) {
                JoinCondition jr = new JoinCondition();
                jr.used = false;
                jr.lexpr = slexpr;
                jr.rexpr = srexpr;
                jr.expr = expr;

                jr.jt = jointype.right;
                jr.plusTokens.add(slexpr.getEndToken());

                jr.lefttable = getExpressionTable(slexpr);
                jr.righttable = null;
                jr.markedTable = jr.lefttable;

                jrs.add(jr);
            } else if (lc_expr.getExpressionType() == EExpressionType.between_t) {
                // expr1 [NOT] BETWEEN expr2 AND expr3 with (+) on the operands,
                // e.g. :p_date BETWEEN t.start_date(+) AND t.end_date(+)
                // or   u.col BETWEEN t.a(+) AND t.b(+)
                // The whole predicate belongs in the ON clause of the
                // (+)-marked (optional) table's join.
                TExpression[] operands = new TExpression[]{
                        lc_expr.getBetweenOperand(), slexpr, srexpr};
                List<TExpression> plusOps = new ArrayList<TExpression>();
                Set<String> plusTables = new HashSet<String>();
                Set<String> otherTables = new HashSet<String>();
                int knownPlus = 0;
                for (TExpression op : operands) {
                    if (op == null) continue;
                    List<TExpression> plus = new ArrayList<TExpression>();
                    collectPlusExprs(op, plus);
                    if (!plus.isEmpty()) {
                        plusOps.addAll(plus);
                        for (TExpression pe : plus) {
                            String t = getExpressionTable(pe);
                            if (t != null) {
                                plusTables.add(t.toLowerCase());
                                knownPlus++;
                            }
                        }
                    } else {
                        String t = getExpressionTable(op);
                        if (t != null) otherTables.add(t.toLowerCase());
                    }
                }
                if (!plusOps.isEmpty() && plusTables.size() == 1
                        && knownPlus == plusOps.size()
                        && otherTables.size() <= 1) {
                    JoinCondition jr = new JoinCondition();
                    jr.used = false;
                    jr.expr = lc_expr;
                    for (TExpression op : plusOps) {
                        jr.plusTokens.add(op.getEndToken());
                    }
                    String plusTable = plusTables.iterator().next();
                    jr.markedTable = plusTable;
                    if (otherTables.isEmpty()) {
                        // pure filter on the optional side; jointype.right is
                        // flipped to left by getJoinCondition when it attaches
                        // to the (+) table's join (same convention as the
                        // "lefttable.c1(+) = 'Y'" case)
                        jr.lefttable = plusTable;
                        jr.righttable = null;
                        jr.jt = jointype.right;
                    } else {
                        jr.lefttable = otherTables.iterator().next();
                        jr.righttable = plusTable;
                        jr.jt = jointype.left;
                    }
                    jrs.add(jr);
                }
                // otherwise: unsupported form; leave the predicate (and its
                // (+)) untouched — the residue check in convert() reports it
            } else if (lc_expr.isOracleOuterJoin()
                    && parent_expr != null
                    && !is_compare_condition(parent_expr.getExpressionType())) {
                TExpression expression = getCompareCondition(parent_expr);
                if (expression != null && handledConditions.add(expression)) {
                    slexpr = expression.getLeftOperand();
                    srexpr = expression.getRightOperand();

                    JoinCondition jr = new JoinCondition();
                    jr.used = false;
                    jr.lexpr = slexpr;
                    jr.rexpr = srexpr;
                    jr.expr = expression;
                    jr.plusTokens.add(lc_expr.getEndToken());
                    if (slexpr.getEndToken().posinlist >= lc_expr.getStartToken().posinlist) {
                        jr.jt = jointype.right;
                    } else {
                        jr.jt = jointype.left;
                    }

                    jr.lefttable = getExpressionTable(slexpr);
                    jr.righttable = getExpressionTable(srexpr);

                    jrs.add(jr);
                }
            }
        }

        private void analyzeMssqlJoinCondition(TExpression expr) {
            TExpression slexpr = expr.getLeftOperand();
            TExpression srexpr = expr.getRightOperand();

            JoinCondition jr = new JoinCondition();
            jr.used = false;
            jr.lexpr = slexpr;
            jr.rexpr = srexpr;
            jr.expr = expr;
            expr.getOperatorToken().setString("=");
            if (expr.getExpressionType() == EExpressionType.left_join_t) {
                // If the plus is on the left, the join type is right
                // out join.
                jr.jt = jointype.left;
                // remove (+)
                // slexpr.getEndToken( ).setString( "" );
            }
            if (expr.getExpressionType() == EExpressionType.right_join_t) {
                // If the plus is on the right, the join type is left
                // out join.
                jr.jt = jointype.right;
                // srexpr.getEndToken( ).setString( "" );
            }

            jr.lefttable = getExpressionTable(slexpr);
            jr.righttable = getExpressionTable(srexpr);

            jrs.add(jr);

        }

        public boolean exprVisit(TParseTreeNode pNode, boolean isLeafNode) {
            TExpression expr = (TExpression) pNode;
            if (expr.getExpressionType() == EExpressionType.function_t
                    && expr.getFunctionCall() != null
                    && expr.getFunctionCall().getArgs() != null) {
                // note: getArgs() is null for argument-less calls (SYSDATE,
                // USER, ...) — nothing to analyze inside them
                for (int i = 0; i < expr.getFunctionCall().getArgs().size(); i++) {
                    analyzeJoinCondition(expr.getFunctionCall()
                            .getArgs()
                            .getExpression(i), expr);
                    if (isLeafNode) {
                        exprVisit(expr.getFunctionCall()
                                .getArgs()
                                .getExpression(i), isLeafNode);
                    }
                }
            } else {
                analyzeJoinCondition(expr, null);
            }
            return true;

        }

    }

    private String ErrorMessage = "";

    public String getErrorMessage() {
        return ErrorMessage;
    }

    private int ErrorNo;

    private String query;
    private String totalQuery = "";
    private EDbVendor vendor;
    private boolean converted = false;

    public boolean isConverted() {
        return converted;
    }

    public JoinConverter(String sql, EDbVendor vendor) {
        this.query = sql;
        this.vendor = vendor;
    }

    public String getQuery() {
        // remove blank line from query
        String result = this.totalQuery.replaceAll("(?m)^[ \t]*\r?\n", "");
//        String trim = result.trim();
//        char[] chars = trim.toCharArray();
//        if (chars[chars.length - 1] == ',') {
//            result = trim.substring(0, trim.length() - 1);
//        }
        return result;
    }

    public static void main(String[] args) {
        EDbVendor vendor = EDbVendor.dbvmssql;
        String sql = "SELECT *\r\n"
        		+ "FROM	table1 t1,\r\n"
        		+ " 	table2 t2,\r\n"
        		+ "	table3 t3,\r\n"
        		+ "	table4 t4\r\n"
        		+ "WHERE	t3.f1 *= t2.f1\r\n"
        		+ "	AND t1.f12 *= t3.f2\r\n"
        		+ "	AND t3.f3 *= t4.f3";
        JoinConverter joinConverter = new JoinConverter(sql, vendor);
        joinConverter.convert();
        System.out.println(joinConverter.getQuery()
                .trim());
    }


    public int convert() {
        TGSqlParser sqlparser = new TGSqlParser(vendor);
        sqlparser.sqltext = this.query;
        String originalQuery = this.query;
        ErrorNo = sqlparser.parse();
        if (ErrorNo != 0) {
            ErrorMessage = sqlparser.getErrormessage();
            return ErrorNo;
        }
        for(TCustomSqlStatement it:sqlparser.sqlstatements){
            analyzeSelect(it);
            String convertedQuery = it.toString();
            if (!convertedQuery.equals(this.query)) {
                converted = true;
                this.totalQuery += convertedQuery;
            }
            this.query = convertedQuery;
        }
        if (ErrorNo == 0 && vendor == EDbVendor.dbvoracle) {
            verifyConversion(converted ? getQuery() : originalQuery);
        }
        return ErrorNo;
    }

    /**
     * Post-conversion contract check (MantisBT 4685): convert() must never
     * return success while the output still contains an Oracle (+) outer-join
     * operator, and must never return success with output that no longer
     * parses. Both would silently corrupt downstream use.
     */
    private void verifyConversion(String effectiveOutput) {
        TGSqlParser check = new TGSqlParser(vendor);
        check.sqltext = effectiveOutput;
        if (check.parse() != 0) {
            ErrorNo++;
            ErrorMessage += String.format("%sError %d, Message: %s",
                    System.getProperty("line.separator"),
                    ErrorNo,
                    "Converted SQL failed to re-parse: "
                            + check.getErrormessage().trim());
            return;
        }
        final List<TExpression> residue = new ArrayList<TExpression>();
        TParseTreeVisitor visitor = new TParseTreeVisitor() {
            public void preVisit(TExpression expr) {
                if (expr.isOracleOuterJoin()) {
                    residue.add(expr);
                }
            }
        };
        for (TCustomSqlStatement st : check.sqlstatements) {
            st.acceptChildren(visitor);
        }
        for (TExpression expr : residue) {
            ErrorNo++;
            ErrorMessage += String.format("%sError %d, Message: %s",
                    System.getProperty("line.separator"),
                    ErrorNo,
                    "Unconverted Oracle outer join (+) remains at line "
                            + expr.getStartToken().lineNo + ", column "
                            + expr.getStartToken().columnNo + ": "
                            + expr);
        }
    }

    private boolean isNameOfTable(TTable table, String name) {
        // table.getName() is null for derived tables (subquery in FROM)
        return (name == null || table.getName() == null) ? false
                : table.getName().equalsIgnoreCase(name);
    }

    private boolean isAliasOfTable(TTable table, String alias) {
        if (table.getAliasClause() == null) {
            return false;
        } else
            return (alias == null) ? false : table.getAliasClause()
                    .toString()
                    .equalsIgnoreCase(alias);
    }

    private boolean isNameOrAliasOfTable(TTable table, String str) {
        return isAliasOfTable(table, str) || isNameOfTable(table, str);
    }

    private boolean areTableJoined(TTable lefttable, TTable righttable,
                                   ArrayList<JoinCondition> jrs) {

        boolean ret = false;

        for (int i = 0; i < jrs.size(); i++) {
            JoinCondition jc = jrs.get(i);
            if (jc.used) {
                continue;
            }
            ret = isNameOrAliasOfTable(lefttable, jc.lefttable)
                    && isNameOrAliasOfTable(righttable, jc.righttable);
            if (ret)
                break;
            ret = isNameOrAliasOfTable(lefttable, jc.righttable)
                    && isNameOrAliasOfTable(righttable, jc.lefttable);
            if (ret)
                break;
        }

        return ret;
    }

    private boolean areTableJoinedExcludeOuterJoin(TTable lefttable, TTable righttable,
                                                   ArrayList<JoinCondition> jrs) {

        boolean ret = false;

        for (int i = 0; i < jrs.size(); i++) {
            JoinCondition jc = jrs.get(i);
            if (jc.used) {
                continue;
            }
            ret = isNameOrAliasOfTable(lefttable, jc.lefttable)
                    && isNameOrAliasOfTable(righttable, jc.righttable)
                    && jc.jt != jointype.left
                    && jc.jt != jointype.full;
            if (ret)
                break;
            ret = isNameOrAliasOfTable(lefttable, jc.righttable)
                    && isNameOrAliasOfTable(righttable, jc.lefttable)
                    && jc.jt != jointype.right
                    && jc.jt != jointype.full;
            if (ret)
                break;
        }

        return ret;
    }

    private String getJoinType(ArrayList<JoinCondition> jrs) {
        String str = "inner join";
        for (int i = 0; i < jrs.size(); i++) {
            if (jrs.get(i).jt == jointype.left) {
                str = "left outer join";
                break;
            } else if (jrs.get(i).jt == jointype.right) {
                str = "right outer join";
                break;
            } else if (jrs.get(i).jt == jointype.full) {
                str = "full outer join";
                break;
            } else if (jrs.get(i).jt == jointype.cross) {
                str = "cross join";
                break;
            } else if (jrs.get(i).jt == jointype.join) {
                str = "join";
                break;
            }
        }

        return str;
    }

    private ArrayList<JoinCondition> getJoinCondition(TTable lefttable,
                                                      TTable righttable, ArrayList<JoinCondition> jrs) {
        ArrayList<JoinCondition> lcjrs = new ArrayList<JoinCondition>();
        for (int i = 0; i < jrs.size(); i++) {
            JoinCondition jc = jrs.get(i);
            if (jc.used) {
                continue;
            }

            if (isNameOrAliasOfTable(lefttable, jc.lefttable)
                    && isNameOrAliasOfTable(righttable, jc.righttable)) {
                lcjrs.add(jc);
                markUsed(jc);
            } else if (isNameOrAliasOfTable(lefttable, jc.righttable)
                    && isNameOrAliasOfTable(righttable, jc.lefttable)) {
                if (jc.jt == jointype.left)
                    jc.jt = jointype.right;
                else if (jc.jt == jointype.right)
                    jc.jt = jointype.left;

                lcjrs.add(jc);
                markUsed(jc);
            } else if ((jc.lefttable == null)
                    && (isNameOrAliasOfTable(lefttable, jc.righttable) || isNameOrAliasOfTable(righttable,
                    jc.righttable))) {
                // 'Y' = righttable.c1(+)
                orientByMarkedTable(jc, lefttable, righttable);
                lcjrs.add(jc);
                markUsed(jc);
            } else if ((jc.righttable == null)
                    && (isNameOrAliasOfTable(lefttable, jc.lefttable) || isNameOrAliasOfTable(righttable,
                    jc.lefttable))) {
                // lefttable.c1(+) = 'Y'
                if (jc.markedTable != null) {
                    orientByMarkedTable(jc, lefttable, righttable);
                } else {
                    if (jc.jt == jointype.left)
                        jc.jt = jointype.right;
                    else if (jc.jt == jointype.right)
                        jc.jt = jointype.left;
                }
                lcjrs.add(jc);
                markUsed(jc);
            } else if (!jc.plusTokens.isEmpty()
                    && jc.lefttable != null
                    && jc.lefttable.equalsIgnoreCase(jc.righttable)
                    && (isNameOrAliasOfTable(lefttable, jc.lefttable)
                    || isNameOrAliasOfTable(righttable, jc.lefttable))) {
                // same-table (+) filter, e.g. A.CHECK_SEQ(+) = A.MAX_CHECK_SEQ(+):
                // an ON-clause filter of A's outer join (leaving it in the
                // WHERE would discard the NULL-extended rows). Direction from
                // the marked (optional) table — it agrees with the real
                // two-table edge when one exists, and preserves the outer
                // semantics when the filter is the only (+) condition.
                jc.jt = jointype.inner;
                orientByMarkedTable(jc, lefttable, righttable);
                lcjrs.add(jc);
                markUsed(jc);
            }
        }
        return lcjrs;
    }

    // Query blocks already converted in this run. A CTE body is reachable
    // both through getStatements() and through the CTE list (and a UNION
    // branch may be visited through several paths); converting the same
    // block twice corrupts its token stream.
    private final Set<TCustomSqlStatement> analyzedStmts =
            Collections.newSetFromMap(new java.util.IdentityHashMap<TCustomSqlStatement, Boolean>());

    private void analyzeSelect(final TCustomSqlStatement stmt) {
        if (!analyzedStmts.add(stmt)) {
            return;
        }
        if (stmt instanceof TSelectSqlStatement) {
            final TSelectSqlStatement select = (TSelectSqlStatement) stmt;
            // WITH-clause bodies are not part of getStatements(); convert
            // each CTE subquery explicitly (MantisBT 4685: (+) inside a CTE
            // was silently left behind)
            if (select.getCteList() != null) {
                for (int i = 0; i < select.getCteList().size(); i++) {
                    TCTE cte = select.getCteList().getCTE(i);
                    if (cte.getSubquery() != null) {
                        analyzeSelect(cte.getSubquery());
                    }
                }
            }
            if (!select.isCombinedQuery()) {
                for (int i = 0; i < select.getStatements().size(); i++) {
                    if (select.getStatements().get(i) instanceof TSelectSqlStatement) {
                        analyzeSelect((TSelectSqlStatement) select.getStatements()
                                .get(i));
                    }
                }

                if (select.tables.size() == 1) {
                    // A query block whose FROM has a single table has no join
                    // partner, so Oracle ignores any (+) in it (typically a
                    // correlated subquery like
                    //   WHERE pea.email_address_id(+) = outer.primary_email_id
                    // ). Strip the no-op markers so they do not survive as
                    // fake outer joins. preOrderTraverse does not descend
                    // into nested subqueries, which are converted by their
                    // own analyzeSelect pass.
                    if (select.getWhereClause() != null
                            && select.getWhereClause().getCondition() != null) {
                        // the bounded walker also reaches (+) inside function
                        // arguments (TRIM/NVL/...) and stops at nested
                        // subqueries, which preOrderTraverse would miss/skip
                        List<TExpression> noOps = new ArrayList<TExpression>();
                        new getJoinConditionVisitor().collectPlusExprs(
                                select.getWhereClause().getCondition(), noOps);
                        for (TExpression e : noOps) {
                            e.getEndToken().setString("");
                        }
                    }
                    return;
                }

                if (select.getWhereClause() == null) {
                    if (select.tables.size() > 1) {
                        if (!hasJoin(select.joins)) {
                            // cross join
                            String str = getFullNameWithAliasString(select.tables.getTable(0));
                            for (int i = 1; i < select.tables.size(); i++) {
                                str = str
                                        + "\ncross join "
                                        + getFullNameWithAliasString(select.tables.getTable(i));
                            }

                            for (int k = select.joins.size() - 1; k > 0; k--) {
                                //TODO update
                                if (k == 0) {
                                    if (select.joins.size() > 1) {
                                        TSourceToken st = select.joins.getJoin(k).getEndToken().searchToken(",", 1);
                                        if (st != null) {
                                            st.removeFromChain();
                                        }
                                    }
                                } else {
                                    TSourceToken st = select.joins.getJoin(k).getStartToken().searchToken(",", -1);
                                    if (st != null) {
                                        st.removeFromChain();
                                    }
                                }
                                select.joins.removeJoin(k);
                            }
                            select.joins.getJoin(0).setString(str);
                        }
                        else{
                            // cross join
                            String str = getFullNameWithAliasString(select.tables.getTable(0));
                            for (int i = 1; i < select.tables.size(); i++) {
                                str = str
                                        + "\ncross join "
                                        + getFullNameWithAliasString(select.tables.getTable(i));
                            }
                            for (int k = select.joins.size() - 1; k > 0; k--) {
                                select.joins.removeJoin(k);
                            }
                            select.joins.getJoin(0).setString(str);
                        }
                    }
                } else {

                    getJoinConditionVisitor v = new getJoinConditionVisitor();

                    // get join conditions
                    select.getWhereClause()
                            .getCondition()
                            .preOrderTraverse(v);
                    ArrayList<JoinCondition> jrs = v.getJrs();

                    if (select.joins != null && select.joins.size() > 0) {
                        for (int i = 0; i < select.joins.size(); i++) {
                            TJoin join = select.joins.getJoin(i);
                            for (int j = 0; j < join.getJoinItems().size(); j++) {
                                TJoinItem item = join.getJoinItems()
                                        .getJoinItem(j);
                                JoinCondition jr = new JoinCondition();
                                jr.expr = item.getOnCondition();
                                if (null == jr.expr) {
                                    continue;
                                }
                                jr.used = false;
                                jr.lexpr = jr.expr.getLeftOperand();
                                jr.rexpr = jr.expr.getRightOperand();
                                jr.lefttable = getExpressionTable(jr.lexpr);
                                jr.righttable = getExpressionTable(jr.rexpr);
                                if (item.getJoinType() == EJoinType.inner) {
                                    jr.jt = jointype.inner;
                                    jrs.add(jr);
                                }
                                if (item.getJoinType() == EJoinType.left
                                        || item.getJoinType() == EJoinType.leftouter) {
                                    jr.jt = jointype.left;
                                    jrs.add(jr);
                                }
                                if (item.getJoinType() == EJoinType.right
                                        || item.getJoinType() == EJoinType.rightouter) {
                                    jr.jt = jointype.right;
                                    jrs.add(jr);
                                }
                                if (item.getJoinType() == EJoinType.full
                                        || item.getJoinType() == EJoinType.fullouter) {
                                    jr.jt = jointype.full;
                                    jrs.add(jr);
                                }
                                if (item.getJoinType() == EJoinType.join) {
                                    jr.jt = jointype.join;
                                    jrs.add(jr);
                                }
                                if (item.getJoinType() == EJoinType.cross) {
                                    jr.jt = jointype.cross;
                                    jrs.add(jr);
                                }
                            }
                        }
                    }

                    // Nothing Oracle-proprietary to convert and the FROM is
                    // already a single ANSI join chain (comma-separated
                    // tables each get their own TJoin, so joins.size() <= 1
                    // means no comma join here): leave the block untouched.
                    // Rebuilding an ANSI FROM with several join items mangles
                    // it (the rebuild only rewrites the first TJoin).
                    boolean hasPlusCondition = false;
                    for (int i = 0; i < jrs.size(); i++) {
                        if (!jrs.get(i).plusTokens.isEmpty()) {
                            hasPlusCondition = true;
                            break;
                        }
                    }
                    if (!hasPlusCondition && select.joins.size() <= 1) {
                        return;
                    }

                    List<TTable> tables = new ArrayList<TTable>();
                    for (int i = 0; i < select.tables.size(); i++) {
                        tables.add(select.tables.getTable(i));
                    }

                    List<TTable> parentTables = new ArrayList<TTable>();//add by grq 2023.05.07 issue=I70J7M
                    TCustomSqlStatement parentStmt = select;
                    while (parentStmt.getParentStmt() != null) {
                        parentStmt = parentStmt.getParentStmt();
                        if (parentStmt instanceof TSelectSqlStatement) {
                            TSelectSqlStatement temp = (TSelectSqlStatement) parentStmt;
                            for (int i = 0; i < temp.tables.size(); i++) {
                                //edit  by grq 2023.05.07 issue=I70J7M
                                TTable tempTable = temp.tables.getTable(i);
                                parentTables.add(tempTable);
                                tables.add(tempTable);
                                //end by grq
                            }
                        }
                    }

                    // Console.WriteLine(jrs.Count);
                    boolean tableUsed[] = new boolean[tables.size()];
                    for (int i = 0; i < tables.size(); i++) {
                        tableUsed[i] = false;
                    }

                    // make first table to be the left most joined table
                    String fromclause = getFullNameWithAliasString(tables.get(0));

                    tableUsed[0] = true;
                    boolean foundTableJoined;
                    final ArrayList<FromClause> fromClauses = new ArrayList<FromClause>();
                    // cross join
                    TTable prTable = tables.get(0);
                    // Only LOCAL tables (0..select.tables.size()-1 of the
                    // combined list) may be cross joined into this block's
                    // FROM: parentTables entries belong to an enclosing
                    // query, and cross joining one here duplicates it (and,
                    // for a derived table, embeds its whole subquery text a
                    // second time).
                    for (int i = 1; i < select.tables.size(); i++) {
                        TTable lcTable1 = tables.get(i);
                        if (!areTableJoined(prTable, lcTable1, jrs)) {
                            boolean joined = false;
                            boolean acrossJoined = true;
                            for (int j = 1; j < select.tables.size(); j++) {
                                TTable lcTable2 = tables.get(j);
                                if (lcTable1.equals(lcTable2)) {
                                    continue;
                                }
                                if (!areTableJoined(lcTable1, lcTable2, jrs)) {
                                    joined = true;
                                    if (!areTableJoinedExcludeOuterJoin(lcTable1, lcTable2, jrs)) {
                                        for (int k = 0; k < select.tables.size(); k++) {
                                            TTable lcTable3 = tables.get(k);
                                            if (lcTable2.equals(lcTable3)) {
                                                continue;
                                            }
                                            if (areTableJoinedExcludeOuterJoin(lcTable2, lcTable3, jrs)) {
                                                acrossJoined = false;
                                                break;
                                            }
                                        }
                                        if (!acrossJoined) {
                                            break;
                                        }
                                    } else {
                                        acrossJoined = false;
                                        break;
                                    }
                                } else {
                                    acrossJoined = false;
                                    break;
                                }
                            }
                            if (joined && acrossJoined
                                    && !hasPendingPlusCondition(lcTable1, jrs)) {
                                FromClause fc = new FromClause();
                                fc.table = prTable;
                                fc.joinTable = lcTable1;
                                fc.joinClause = "cross join";
                                fc.condition = "";

                                fromClauses.add(fc);
                                tableUsed[i] = true;
                            }
                        }
                    }
                    TTable lastJoinedTable = null;
                    for (; ; ) {
                        foundTableJoined = false;

                        for (int i = 0; i < tables.size(); i++) {
                            TTable lcTable1 = tables.get(i);

                            TTable leftTable = null, rightTable = null;
                            for (int j = i + 1; j < tables.size(); j++) {
                                TTable lcTable2 = tables.get(j);
                                if (areTableJoined(lcTable1, lcTable2, jrs)) {
                                    if (tableUsed[i] && (!tableUsed[j])) {
                                        leftTable = lcTable1;
                                        rightTable = lcTable2;
                                    } else if ((!tableUsed[i]) && tableUsed[j]) {
                                        leftTable = lcTable2;
                                        rightTable = lcTable1;
                                    }
//									System.out.println("leftTable:" + leftTable + ", rightTable:" + rightTable);

                                    if ((leftTable != null)
                                            && (rightTable != null)) {
                                        // Never join a parent-scope table into
                                        // this block when a LOCAL table with
                                        // the same name/alias exists: the name
                                        // belongs to the local instance, and
                                        // joining the parent both duplicates
                                        // the table in this FROM and leaves
                                        // the local one to be cross joined.
                                        if (parentTables.indexOf(rightTable) >= 0) {
                                            String rtName = rightTable.getAliasClause() != null
                                                    ? rightTable.getAliasClause().toString()
                                                    : rightTable.getName();
                                            boolean localShadows = false;
                                            for (int kk = 0; kk < select.tables.size(); kk++) {
                                                if (isNameOrAliasOfTable(select.tables.getTable(kk), rtName)) {
                                                    localShadows = true;
                                                    break;
                                                }
                                            }
                                            if (localShadows) {
                                                leftTable = null;
                                                rightTable = null;
                                                continue;
                                            }
                                        }
                                        //add by grq 2023.05.07 issue=I70J7M
                                        if(parentTables.indexOf(leftTable)<0 && parentTables.indexOf(rightTable)>=0){
                                            boolean aliasHave = false;
                                            for (int k = 0; k < jrs.size(); k++) {
                                                JoinCondition jc = jrs.get(k);
                                                if (jc.used) {
                                                    continue;
                                                }
                                                for(int kk = 0; kk < tables.size(); kk++){
                                                    TTable t = tables.get(kk);
                                                    if(parentTables.indexOf(t)<0){
                                                        if(isNameOrAliasOfTable(t, jc.righttable)){
                                                            if(tableUsed[kk]){
                                                                aliasHave = true;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                                if(aliasHave){
                                                    break;
                                                }
                                            }
                                            if(aliasHave){
                                                continue;
                                            }
                                        }
                                        //end by grq
                                        ArrayList<JoinCondition> lcjrs = getJoinCondition(leftTable,
                                                rightTable,
                                                jrs);
                                        if (lcjrs.isEmpty())
                                            continue;
                                        FromClause fc = new FromClause();
                                        fc.table = leftTable;
                                        fc.joinTable = rightTable;
                                        fc.joinClause = getJoinType(lcjrs);
                                        String condition = "";
                                        for (int k = 0; k < lcjrs.size(); k++) {
                                            condition += lcjrs.get(k).expr.toString();
                                            if (k != lcjrs.size() - 1) {
                                                condition += " and ";
                                            }
                                            TExpression lc_expr = lcjrs.get(k).expr;
                                            collectQualifierNames(lc_expr, fc.refTableNames);
                                            lc_expr.remove2();
                                        }
                                        fc.condition = condition;

                                        fromClauses.add(fc);
                                        tableUsed[i] = true;
                                        tableUsed[j] = true;
                                        lastJoinedTable = rightTable;

                                        foundTableJoined = true;
                                    }
                                }
                            }
                        }

                        if (!foundTableJoined) {
                            // Rescue pass: a table can be tied to the chain
                            // only by one-sided conditions — the other side
                            // being a constant, bind, or unresolvable
                            // unqualified column (e.g. CUS_TYPE = t.col(+)).
                            // Such a table never satisfies areTableJoined, so
                            // attach it here through getJoinCondition's
                            // one-sided-null branches, anchored to the first
                            // already-joined table.
                            // anchor to the table most recently ADDED to the
                            // chain (not the highest-index used one), so a
                            // rescued ON condition that mentions an
                            // unresolvable (unqualified) column of an earlier
                            // chain table stays in scope
                            TTable anchor = lastJoinedTable;
                            if (anchor == null) {
                                for (int i = 0; i < tables.size(); i++) {
                                    if (tableUsed[i]) {
                                        anchor = tables.get(i);
                                        break;
                                    }
                                }
                            }
                            if (anchor != null) {
                                // LOCAL tables only (indexes below
                                // select.tables.size()): parentTables entries
                                // must never be re-attached into this block's
                                // FROM — that duplicates the parent table (or
                                // its whole derived-table text) inside the
                                // nested query.
                                for (int i = 0; i < select.tables.size(); i++) {
                                    if (tableUsed[i]) {
                                        continue;
                                    }
                                    TTable lcTable = tables.get(i);
                                    ArrayList<JoinCondition> lcjrs = getJoinCondition(anchor,
                                            lcTable,
                                            jrs);
                                    if (lcjrs.isEmpty()) {
                                        continue;
                                    }
                                    FromClause fc = new FromClause();
                                    fc.table = anchor;
                                    fc.joinTable = lcTable;
                                    fc.joinClause = getJoinType(lcjrs);
                                    String condition = "";
                                    for (int k = 0; k < lcjrs.size(); k++) {
                                        condition += lcjrs.get(k).expr.toString();
                                        if (k != lcjrs.size() - 1) {
                                            condition += " and ";
                                        }
                                        collectQualifierNames(lcjrs.get(k).expr, fc.refTableNames);
                                        lcjrs.get(k).expr.remove2();
                                    }
                                    fc.condition = condition;
                                    fromClauses.add(fc);
                                    tableUsed[i] = true;
                                    lastJoinedTable = lcTable;
                                    foundTableJoined = true;
                                    break;
                                }
                            }
                        }

                        if (!foundTableJoined) {
                            break;
                        }
                    }

                    // are all join conditions used?
                    for (int i = 0; i < jrs.size(); i++) {
                        JoinCondition jc = jrs.get(i);
                        if (!jc.used) {
                            for (int j = fromClauses.size() - 1; j >= 0; j--) {
                                if ("cross join".equals(fromClauses.get(j).joinClause)) {
                                    // a cross join renders no ON clause; the
                                    // appended predicate would silently vanish
                                    continue;
                                }
                                if (isNameOrAliasOfTable(fromClauses.get(j).joinTable,
                                        jc.lefttable)
                                        || isNameOrAliasOfTable(fromClauses.get(j).joinTable,
                                        jc.righttable)) {
                                    markUsed(jc);
                                    fromClauses.get(j).condition += " and "
                                            + jc.expr.toString();
                                    collectQualifierNames(jc.expr, fromClauses.get(j).refTableNames);
                                    jc.expr.remove2();
                                    break;
                                }
                            }
                        }
                    }

                    // Tables with no recognized join condition: a comma join
                    // plus WHERE filters is equivalent to CROSS JOIN plus the
                    // same WHERE filters, so cross join them and leave their
                    // predicates where they are. Only refuse when an OUTER
                    // (+) condition involving the table could not be placed —
                    // cross joining that away would change the semantics.
                    StringBuilder crossJoins = new StringBuilder();
                    Set<TTable> crossJoinedTables = new HashSet<TTable>();
                    for (int i = 0; i < select.tables.size(); i++) {
                        if (!tableUsed[i]) {
                            TTable lcTable = select.tables.getTable(i);
                            JoinCondition pendingOuter = null;
                            for (int k = 0; k < jrs.size(); k++) {
                                JoinCondition jc = jrs.get(k);
                                if (!jc.used
                                        && jc.jt != jointype.inner
                                        && (isNameOrAliasOfTable(lcTable, jc.lefttable)
                                        || isNameOrAliasOfTable(lcTable, jc.righttable))) {
                                    pendingOuter = jc;
                                    break;
                                }
                            }
                            if (pendingOuter != null) {
                                ErrorNo++;
                                ErrorMessage += String.format("%sError %d, Message: %s",
                                        System.getProperty("line.separator"),
                                        ErrorNo,
                                        "Cannot convert outer join for table "
                                                + getFullNameWithAliasString(lcTable)
                                                + ": unsupported (+) predicate: "
                                                + pendingOuter.expr);
                            } else {
                                crossJoins.append("\ncross join ")
                                        .append(getFullNameWithAliasString(lcTable));
                                crossJoinedTables.add(lcTable);
                                tableUsed[i] = true;
                            }
                        }
                    }

                    Collections.sort(fromClauses,
                            new Comparator<FromClause>() {

                                public int compare(FromClause o1, FromClause o2) {
                                    return indexOf(select, o1.joinTable)
                                            - indexOf(select, o2.joinTable);
                                }

                                private int indexOf(
                                        TSelectSqlStatement select,
                                        TTable joinTable) {
                                    TTableList tables = select.tables;
                                    for (int i = 0; i < tables.size(); i++) {
                                        if (joinTable != null
                                                && tables.getTable(i)
                                                .equals(joinTable))
                                            return i;
                                    }
                                    return -1;
                                }
                            });

                    Collections.sort(fromClauses,
                            new Comparator<FromClause>() {

                                public int compare(FromClause o1, FromClause o2) {
                                    if (o1.table.equals(o2.joinTable))
                                        return 1;
                                    else if (o2.table.equals(o1.joinTable))
                                        return -1;
                                    else
                                        return fromClauses.indexOf(o1)
                                                - fromClauses.indexOf(o2);
                                }
                            });

                    // add other join tables
                    for (int i = 0; i < fromClauses.size(); i++) {
                        FromClause fc = fromClauses.get(i);
                        TTable leftTable = fc.table;
                        TTable joinTable = fc.joinTable;
                        String condition = fc.condition;
                        fc.joinTableOthers = new HashSet<TTable>();
                        String[] conditionArray = condition.replace(".", ".,_,").replace(" ", ",_,").split(",_,");
                        for (String conditionStr : conditionArray) {
                            if (conditionStr.endsWith(".") && conditionStr.length() > 1) {
                                String tableName = conditionStr.substring(0, conditionStr.length() - 1);
                                if (!isNameOrAliasOfTable(leftTable, tableName) && !isNameOrAliasOfTable(joinTable, tableName)) {
                                    for (TTable table : tables) {
                                        if (isNameOrAliasOfTable(table, tableName)) {
                                            fc.joinTableOthers.add(table);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // An ON condition may not reference a table that is
                    // only attached as a trailing cross join — the reference
                    // would be out of scope (and re-ordering the cross join
                    // ahead of an outer join changes Oracle's semantics), so
                    // fail loudly instead of emitting invalid-scope SQL.
                    for (int i = 0; i < fromClauses.size(); i++) {
                        FromClause fc = fromClauses.get(i);
                        for (TTable other : crossJoinedTables) {
                            boolean referenced = false;
                            for (String name : fc.refTableNames) {
                                if (isNameOrAliasOfTable(other, name)) {
                                    referenced = true;
                                    break;
                                }
                            }
                            if (referenced) {
                                ErrorNo++;
                                ErrorMessage += String.format("%sError %d, Message: %s",
                                        System.getProperty("line.separator"),
                                        ErrorNo,
                                        "Join condition '" + fc.condition
                                                + "' references table "
                                                + getFullNameWithAliasString(other)
                                                + " which has no join condition of its own"
                                                + " — unsupported (+) shape");
                            }
                        }
                    }

                    // sort by other join tables
                    Collections.sort(fromClauses,
                            new Comparator<FromClause>() {

                                public int compare(FromClause o1, FromClause o2) {
                                    if (o1.joinTableOthers.contains(o2.table) || o1.joinTableOthers.contains(o2.joinTable))
                                        return 1;
                                    else if (o2.joinTableOthers.contains(o1.table) || o2.joinTableOthers.contains(o1.joinTable))
                                        return -1;
                                    else
                                        return fromClauses.indexOf(o1)
                                                - fromClauses.indexOf(o2);
                                }
                            });
                    // link all join clause
                    for (int i = 0; i < fromClauses.size(); i++) {
                        FromClause fc = fromClauses.get(i);
                        fromclause += "\n"
                                + fc.joinClause
                                + " "
                                + getFullNameWithAliasString(fc.joinTable);
                        if (!"cross join".equals(fc.joinClause)) {
                            fromclause += " on "
                                    + fc.condition;
                        }
                    }
                    fromclause += crossJoins.toString();

                    for (int k = select.joins.size() - 1; k > 0; k--) {
                        select.joins.removeJoin(k);
                    }

                    select.joins.getJoin(0).setString(fromclause);

                    if ((select.getWhereClause()
                            .getCondition()
                            .getStartToken() == null)
                    		|| select.getWhereClause()
                            .getCondition()
                            .toString() == null
                            || (select.getWhereClause()
                            .getCondition()
                            .toString()
                            .trim()
                            .length() == 0)) {
                        // no where condition, remove WHERE keyword
//                        select.getWhereClause().fastSetString(" ");

                        //TODO update
                        select.getWhereClause().removeTokens();
                    } else {
                        select.getWhereClause().getCondition().fastSetString(select.getWhereClause()
                                .getCondition()
                                .toString()
                                .trim());
                    }
                }
            } else {
                analyzeSelect(select.getLeftStmt());
                analyzeSelect(select.getRightStmt());
            }
        } else if (stmt.getStatements() != null) {
            for (int i = 0; i < stmt.getStatements().size(); i++) {
                analyzeSelect(stmt.getStatements().get(i));
            }
        }
    }

    private boolean hasJoin(TJoinList joins) {
        if (joins == null)
            return false;
        for (int i = 0; i < joins.size(); i++) {
            TJoinItemList joinItems = joins.getJoin(i).getJoinItems();
            if (null != joinItems && joinItems.size() > 0) {
                for (int j = 0; j < joinItems.size(); j++) {
                    if (null == joinItems.getJoinItem(j).toString()) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private String getFullNameWithAliasString(TTable table) {
        if (table.getSubquery() != null) {
            if (table.getAliasClause() != null) {
                return table.getSubquery()
                        + " "
                        + table.getAliasClause().toString();
            } else {
                return table.getSubquery().toString();
            }
        } else if (table.getFullName() != null)
            return table.getFullNameWithAliasString();
        else
            return table.toString();
    }

    private String getExpressionTable(TExpression root) {
        // iterative depth-first, left-to-right: returns the first table
        // qualifier (or resolver-known source table) found. Explicit stack —
        // a long concatenation/arithmetic chain must not overflow the JVM
        // stack.
        java.util.Deque<TExpression> stack = new java.util.ArrayDeque<TExpression>();
        if (root != null) {
            stack.push(root);
        }
        while (!stack.isEmpty()) {
            TExpression expr = stack.pop();
            if (expr == null) {
                continue;
            }
            if (expr.getExpressionType() == EExpressionType.function_t) {
                TFunctionCall fc = expr.getFunctionCall();
                if (fc == null) {
                    continue;
                }
                if (fc.getArgs() != null) {
                    for (int i = fc.getArgs().size() - 1; i >= 0; i--) {
                        stack.push(fc.getArgs().getExpression(i));
                    }
                } else if (fc.getTrimArgument() != null) {
                    // TRIM-style calls keep their operand outside getArgs()
                    if (fc.getTrimArgument().getTrimCharacter() != null)
                        stack.push(fc.getTrimArgument().getTrimCharacter());
                    if (fc.getTrimArgument().getStringExpression() != null)
                        stack.push(fc.getTrimArgument().getStringExpression());
                }
                // argument-less call (SYSDATE, USER, ...): nothing inside
            } else if (expr.getObjectOperand() != null) {
                TObjectName objectName = expr.getObjectOperand();
                String prefix = objectName.getObjectString();
                if (prefix != null && prefix.length() > 0) {
                    return prefix;
                }
                // unqualified column: fall back to the table the parser
                // resolved it to (single-table FROM, derived-table output
                // column, ...). An unresolved one means "unknown side" and
                // the search continues with the remaining operands.
                TTable sourceTable = objectName.getSourceTable();
                if (sourceTable != null) {
                    if (sourceTable.getAliasClause() != null) {
                        return sourceTable.getAliasClause().toString();
                    }
                    return sourceTable.getName();
                }
            } else {
                if (expr.getRightOperand() != null)
                    stack.push(expr.getRightOperand());
                if (expr.getLeftOperand() != null)
                    stack.push(expr.getLeftOperand());
            }
        }
        return null;
    }

//    public static void main(String args[]) {
//        // String sqltext = "SELECT e.employee_id,\n" +
//        // "       e.last_name,\n" +
//        // "       e.department_id\n" +
//        // "FROM   employees e,\n" +
//        // "       departments d\n" ;
//
//        // String sqltext = "SELECT e.employee_id,\n"
//        // + "       e.last_name,\n"
//        // + "       e.department_id\n"
//        // + "FROM   employees e,\n"
//        // + "       departments d\n"
//        // + "WHERE  e.department_id = d.department_id";
//        //
//        // sqltext = "SELECT m.*, \n"
//        // + "       altname.last_name  last_name_student, \n"
//        // + "       altname.first_name first_name_student, \n"
//        // + "       ccu.date_joined, \n"
//        // + "       ccu.last_login, \n"
//        // + "       ccu.photo_id, \n"
//        // + "       ccu.last_updated \n"
//        // + "FROM   summit.mstr m, \n"
//        // + "       summit.alt_name altname, \n"
//        // + "       smmtccon.ccn_user ccu \n"
//        // + "WHERE  m.id =?\n"
//        // + "       AND m.id = altname.id(+) \n"
//        // + "       AND m.id = ccu.id(+) \n"
//        // + "       AND altname.grad_name_ind(+) = '*'";
//
//        // sqltext = "SELECT * \n" +
//        // "FROM   summit.mstr m, \n" +
//        // "       summit.alt_name altname, \n" +
//        // "       smmtccon.ccn_user ccu \n" +
//        // //"       uhelp.deg_coll deg \n" +
//        // "WHERE  m.id = ? \n" +
//        // "       AND m.id = altname.id(+) \n" +
//        // "       AND m.id = ccu.id(+) \n" +
//        // "       AND 'N' = ccu.admin(+) \n" +
//        // "       AND altname.grad_name_ind(+) = '*'";
//
//        // sqltext = "SELECT ppp.project_name proj_name, \n" +
//        // "       pr.role_title    user_role \n" +
//        // "FROM   jboss_admin.portal_application_location pal, \n" +
//        // "       jboss_admin.portal_application pa, \n" +
//        // "       jboss_admin.portal_user_app_location_role pualr, \n" +
//        // "       jboss_admin.portal_location pl, \n" +
//        // "       jboss_admin.portal_role pr, \n" +
//        // "       jboss_admin.portal_pep_project ppp, \n" +
//        // "       jboss_admin.portal_user pu \n" +
//        // "WHERE  (pal.application_location_id = pualr.application_location_id \n"
//        // +
//        // "         AND pu.jbp_uid = pualr.jbp_uid \n" +
//        // "         AND pu.username = 'USERID') \n" +
//        // "       AND pal.uidr_uid = pl.uidr_uid \n" +
//        // "       AND pal.application_id = pa.application_id \n" +
//        // "       AND pal.application_id = pr.application_id \n" +
//        // "       AND pualr.role_id = pr.role_id \n" +
//        // "       AND pualr.project_id = ppp.project_id \n" +
//        // "       AND pa.application_id = 'APPID' ";
//
//        // sqltext = "SELECT * \n"
//        // + "FROM   smmtccon.ccn_menu menu, \n"
//        // + "       smmtccon.ccn_page paget \n"
//        // + "WHERE  ( menu.page_id = paget.page_id(+) ) \n"
//        // + "       AND ( NOT enabled = 'N' ) \n"
//        // + "       AND ( ( :parent_menu_id IS NULL \n"
//        // + "               AND menu.parent_menu_id IS NULL ) \n"
//        // + "              OR ( menu.parent_menu_id = :parent_menu_id ) ) \n"
//        // + "ORDER  BY item_seq;";
//        //
//        // sqltext = "select *\n"
//        // + "from  ods_trf_pnb_stuf_lijst_adrsrt2 lst\n"
//        // + "		, ods_stg_pnb_stuf_pers_adr pas\n"
//        // + "		, ods_stg_pnb_stuf_pers_nat nat\n"
//        // + "		, ods_stg_pnb_stuf_adr adr\n"
//        // + "		, ods_stg_pnb_stuf_np prs\n"
//        // + "where \n"
//        // + "		pas.soort_adres = lst.soort_adres\n"
//        // + "	and prs.id = nat.prs_id(+)\n"
//        // + "	and adr.id = pas.adr_id\n"
//        // + "	and prs.id = pas.prs_id\n"
//        // + "  and lst.persoonssoort = 'PERSOON'\n"
//        // + "  and pas.einddatumrelatie is null  ";
//        //
//        // sqltext = "select *\n"
//        // + "		from  ods_trf_pnb_stuf_lijst_adrsrt2 lst\n"
//        // + "				, ods_stg_pnb_stuf_np prs\n"
//        // + "				, ods_stg_pnb_stuf_pers_adr pas\n"
//        // + "				, ods_stg_pnb_stuf_pers_nat nat\n"
//        // + "		 		, ods_stg_pnb_stuf_adr adr\n"
//        // + "		 where \n"
//        // + "				pas.soort_adres = lst.soort_adres\n"
//        // + "			and prs.id(+) = nat.prs_id\n"
//        // + "			and adr.id = pas.adr_id\n"
//        // + "			and prs.id = pas.prs_id\n"
//        // + "		 and lst.persoonssoort = 'PERSOON'\n"
//        // + "		  and pas.einddatumrelatie is null";
//
//        // sqltext = "SELECT ppp.project_name proj_name, \n"
//        // + "       pr.role_title    user_role \n"
//        // + "FROM   jboss_admin.portal_application_location pal, \n"
//        // + "       jboss_admin.portal_application pa, \n"
//        // + "       jboss_admin.portal_user_app_location_role pualr, \n"
//        // + "       jboss_admin.portal_location pl, \n"
//        // + "       jboss_admin.portal_role pr, \n"
//        // + "       jboss_admin.portal_pep_project ppp, \n"
//        // + "       jboss_admin.portal_user pu \n"
//        // +
//        // "WHERE  (pal.application_location_id = pualr.application_location_id \n"
//        // + "         AND pu.jbp_uid = pualr.jbp_uid \n"
//        // + "         AND pu.username = 'USERID') \n"
//        // + "       AND pal.uidr_uid = pl.uidr_uid \n"
//        // + "       AND pal.application_id = pa.application_id \n"
//        // + "       AND pal.application_id = pr.application_id \n"
//        // + "       AND pualr.role_id = pr.role_id \n"
//        // + "       AND pualr.project_id = ppp.project_id \n"
//        // + "       AND pa.application_id = 'APPID'";
//        //
//        // sqltext = "select *\n"
//        // + "from  ods_trf_pnb_stuf_lijst_adrsrt2 lst\n"
//        // + "		, ods_stg_pnb_stuf_np prs\n"
//        // + "		, ods_stg_pnb_stuf_pers_adr pas\n"
//        // + "		, ods_stg_pnb_stuf_pers_nat nat\n"
//        // + "		, ods_stg_pnb_stuf_adr adr\n"
//        // + "where \n"
//        // + "		pas.soort_adres = lst.soort_adres\n"
//        // + "	and prs.id = nat.prs_id(+)\n"
//        // + "	and adr.id = pas.adr_id\n"
//        // + "	and prs.id = pas.prs_id\n"
//        // + "  and lst.persoonssoort = 'PERSOON'\n"
//        // + "   and pas.einddatumrelatie is null";
//
//        // sqltext = "select *\n"
//        // + "from  ods_trf_pnb_stuf_lijst_adrsrt2 lst,\n"
//        // + "       ods_stg_pnb_stuf_np prs,\n"
//        // + "       ods_stg_pnb_stuf_pers_adr pas,\n"
//        // + "       ods_stg_pnb_stuf_pers_nat nat,\n"
//        // + "       ods_stg_pnb_stuf_adr adr\n"
//        // + "where  pas.soort_adres = lst.soort_adres\n"
//        // + "       and prs.id(+) = nat.prs_id\n"
//        // + "       and adr.id = pas.adr_id\n"
//        // + "       and prs.id = pas.prs_id\n"
//        // + "       and lst.persoonssoort = 'PERSOON'\n"
//        // + "       and pas.einddatumrelatie is null\n";
//
//        // sqltext = "SELECT e.employee_id,\n"
//        // + "       e.last_name,\n"
//        // + "       e.department_id\n"
//        // + "FROM   employees e,\n"
//        // + "       departments d\n"
//        // + "WHERE  e.department_id = d.department_id(+)";
//        //
//        // sqltext = "SELECT e.employee_id,\n"
//        // + "       e.last_name,\n"
//        // + "       e.department_id\n"
//        // + "FROM   employees e,\n"
//        // + "       departments d\n"
//        // + "WHERE  e.department_id(+) = d.department_id";
//
//        if (args.length == 0) {
//            System.out.println("Usage: java JoinConverter scriptfile [/t <database type>]");
//            System.out.println("/t: Option, set the database type. Support oracle, mssql, the default type is oracle");
//            // Console.Read();
//            return;
//        }
//
//        List<String> argList = Arrays.asList(args);
//
//        EDbVendor vendor = EDbVendor.dbvoracle;
//
//        int index = argList.indexOf("/t");
//
//        if (index != -1 && args.length > index + 1) {
//            vendor = TGSqlParser.getDBVendorByName(args[index + 1]);
//        }
//
//        String vendorString = EDbVendor.dbvmssql == vendor ? "SQL Server"
//                : "Oracle";
//        System.out.println("SQL with " + vendorString + " propriety joins");
//
//        String sqltext = getFileContent(new File(args[0]));
//        JoinConverter converter = new JoinConverter(sqltext, vendor);
//        if (converter.convert() != 0) {
//            System.out.println(converter.getErrorMessage());
//        } else {
//            System.out.println("\nSQL in ANSI joins");
//            System.out.println(converter.getQuery());
//        }
//    }

    public static String getFileContent(File file) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
            byte[] tmp = new byte[4096];
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            while (true) {
                int r = is.read(tmp);
                if (r == -1)
                    break;
                out.write(tmp, 0, r);
            }
            byte[] bytes = out.toByteArray();
            is.close();
            out.close();
            String content = new String(bytes);
            return content.trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


}
