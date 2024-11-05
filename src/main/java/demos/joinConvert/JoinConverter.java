package demos.joinConvert;

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
    }

    class JoinCondition {

        public String lefttable, righttable, leftcolumn, rightcolumn;
        public jointype jt;
        public Boolean used;
        public TExpression lexpr, rexpr, expr;
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
                        // remove (+)
                        slexpr.getEndToken().setString("");
                    }
                    if (srexpr.isOracleOuterJoin()) {
                        // If the plus is on the right, the join type is left
                        // out join.
                        jr.jt = jointype.left;
                        srexpr.getEndToken().setString("");
                    }

                    jr.lefttable = getExpressionTable(slexpr);
                    jr.righttable = getExpressionTable(srexpr);

                    jrs.add(jr);
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
                    // not a join condition
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
                // remove (+)
                slexpr.getEndToken().setString("");

                jr.lefttable = getExpressionTable(slexpr);
                jr.righttable = null;

                jrs.add(jr);
            } else if (lc_expr.isOracleOuterJoin()
                    && parent_expr != null
                    && !is_compare_condition(parent_expr.getExpressionType())) {
                TExpression expression = getCompareCondition(parent_expr);
                if (expression != null) {
                    slexpr = expression.getLeftOperand();
                    srexpr = expression.getRightOperand();

                    JoinCondition jr = new JoinCondition();
                    jr.used = false;
                    jr.lexpr = slexpr;
                    jr.rexpr = srexpr;
                    jr.expr = expression;
                    lc_expr.getEndToken().setString("");
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
            if (expr.getExpressionType() == EExpressionType.function_t) {
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
        return ErrorNo;
    }

    private boolean isNameOfTable(TTable table, String name) {
        return (name == null) ? false : table.getName()
                .equalsIgnoreCase(name);
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
                jc.used = true;
            } else if (isNameOrAliasOfTable(lefttable, jc.righttable)
                    && isNameOrAliasOfTable(righttable, jc.lefttable)) {
                if (jc.jt == jointype.left)
                    jc.jt = jointype.right;
                else if (jc.jt == jointype.right)
                    jc.jt = jointype.left;

                lcjrs.add(jc);
                jc.used = true;
            } else if ((jc.lefttable == null)
                    && (isNameOrAliasOfTable(lefttable, jc.righttable) || isNameOrAliasOfTable(righttable,
                    jc.righttable))) {
                // 'Y' = righttable.c1(+)
                lcjrs.add(jc);
                jc.used = true;
            } else if ((jc.righttable == null)
                    && (isNameOrAliasOfTable(lefttable, jc.lefttable) || isNameOrAliasOfTable(righttable,
                    jc.lefttable))) {
                // lefttable.c1(+) = 'Y'
                if (jc.jt == jointype.left)
                    jc.jt = jointype.right;
                else if (jc.jt == jointype.right)
                    jc.jt = jointype.left;
                lcjrs.add(jc);
                jc.used = true;
            }
        }
        return lcjrs;
    }

    private void analyzeSelect(final TCustomSqlStatement stmt) {
        if (stmt instanceof TSelectSqlStatement) {
            final TSelectSqlStatement select = (TSelectSqlStatement) stmt;
            if (!select.isCombinedQuery()) {
                for (int i = 0; i < select.getStatements().size(); i++) {
                    if (select.getStatements().get(i) instanceof TSelectSqlStatement) {
                        analyzeSelect((TSelectSqlStatement) select.getStatements()
                                .get(i));
                    }
                }

                if (select.tables.size() == 1)
                    return;

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
                    for (int i = 1; i < tables.size(); i++) {
                        TTable lcTable1 = tables.get(i);
                        if (!areTableJoined(prTable, lcTable1, jrs)) {
                            boolean joined = false;
                            boolean acrossJoined = true;
                            for (int j = 1; j < tables.size(); j++) {
                                TTable lcTable2 = tables.get(j);
                                if (lcTable1.equals(lcTable2)) {
                                    continue;
                                }
                                if (!areTableJoined(lcTable1, lcTable2, jrs)) {
                                    joined = true;
                                    if (!areTableJoinedExcludeOuterJoin(lcTable1, lcTable2, jrs)) {
                                        for (int k = 0; k < tables.size(); k++) {
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
                            if (joined && acrossJoined) {
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
                                            lc_expr.remove2();
                                        }
                                        fc.condition = condition;

                                        fromClauses.add(fc);
                                        tableUsed[i] = true;
                                        tableUsed[j] = true;

                                        foundTableJoined = true;
                                    }
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
                                if (isNameOrAliasOfTable(fromClauses.get(j).joinTable,
                                        jc.lefttable)
                                        || isNameOrAliasOfTable(fromClauses.get(j).joinTable,
                                        jc.righttable)) {
                                    fromClauses.get(j).condition += " and "
                                            + jc.expr.toString();
                                    jc.used = true;
                                    jc.expr.remove2();
                                    break;
                                }
                            }
                        }
                    }

                    for (int i = 0; i < select.tables.size(); i++) {
                        if (!tableUsed[i]) {
                            ErrorNo++;
                            ErrorMessage += String.format("%sError %d, Message: %s",
                                    System.getProperty("line.separator"),
                                    ErrorNo,
                                    "This table has no join condition: "
                                            + select.tables.getTable(i)
                                            .getFullName());
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

    private String getExpressionTable(TExpression expr) {
        if (expr.getExpressionType() == EExpressionType.function_t) {
            for (int i = 0; i < expr.getFunctionCall().getArgs().size(); i++) {
                String table = getExpressionTable(expr.getFunctionCall()
                        .getArgs()
                        .getExpression(i));
                if (table != null)
                    return table;
            }
        } else if (expr.getObjectOperand() != null)
            return expr.getObjectOperand().getObjectString();
        else if (expr.getLeftOperand() != null
                && expr.getLeftOperand().getObjectOperand() != null)
            return expr.getLeftOperand().getObjectOperand().getObjectString();
        else if (expr.getRightOperand() != null
                && expr.getRightOperand().getObjectOperand() != null)
            return expr.getRightOperand()
                    .getObjectOperand()
                    .getObjectString();

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
