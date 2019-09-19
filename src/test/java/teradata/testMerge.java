package test.teradata;
/*
 * Date: 14-8-3
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TMergeInsertClause;
import gudusoft.gsqlparser.nodes.TMergeWhenClause;
import gudusoft.gsqlparser.stmt.TMergeSqlStatement;
import junit.framework.TestCase;

public class testMerge extends TestCase {

    public void test1(){
          TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
          sqlparser.sqltext = "merge into agg_daily_cap_usage t1\n" +
                  "using vt_stg_cap_usage t2\n" +
                  "on t1.seat_id=t2.seat_id and t1.date_sk = t2.date_sk\n" +
                  "when not matched then\n" +
                  "insert\n" +
                  "( t2.seat_id, t2.date_sk, t2.login_flag, t2.profile_view, t2.inmail_sent, t2.search_usagelog)\n" +
                  ";";
          assertTrue(sqlparser.parse() == 0);
          assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmerge);
        TMergeSqlStatement merge = (TMergeSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(merge.getWhenClauses().size() == 1);
        TMergeWhenClause wc = merge.getWhenClauses().getElement(0);
        TMergeInsertClause mic = wc.getInsertClause();
        assertTrue(mic.getValuelist().size() == 6);
        assertTrue(mic.getValuelist().getResultColumn(0).getExpr().toString().equalsIgnoreCase("t2.seat_id"));
        assertTrue(mic.getValuelist().getResultColumn(5).getExpr().toString().equalsIgnoreCase("t2.search_usagelog"));

    }


}
