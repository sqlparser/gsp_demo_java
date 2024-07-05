package scriptWriter;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testTimeStampExpr extends TestCase {
    public void test1( ) {
        String sql = "select DATE (TIMESTAMP (concat (add_months (t1.deprn_start_date,(CAST(t1.life_in_months AS INT))),substr (TIMESTAMP (t1.deprn_start_date),11)))) AS deprn_end_date from tablea t1";
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvdatabricks);
        sqlParser.sqltext = sql;
        int ret = sqlParser.parse();
        assertTrue(ret == 0);
        TSelectSqlStatement select = (TSelectSqlStatement) sqlParser.sqlstatements.get(0);
        String finalSql = select.toScript();
        finalSql = finalSql.replaceAll("\r?\n", "").replaceAll(" ", "").toUpperCase();
        sql = sql.replaceAll("\r?\n", "").replaceAll(" ", "").toUpperCase();
        //System.out.println(sql);
       //System.out.println(finalSql);
        assertTrue(finalSql.equals(sql));
    }

    public void test2( ) {
        String sql = "select * from tablea t6 where t6.date_effective <= CAST(TIMESTAMP (concat(last_day(CAST(concat(concat(concat('2022','-'),'11'),'-01') AS DATE)),substr(TIMESTAMP (CAST(concat(concat(concat('2022','-'),'11'),'-01') AS DATE)),11))) AS TIMESTAMP)";
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvdatabricks);
        sqlParser.sqltext = sql;
        int ret = sqlParser.parse();
        assertTrue(ret == 0);
        TSelectSqlStatement select = (TSelectSqlStatement) sqlParser.sqlstatements.get(0);
        String finalSql = select.toScript();
        finalSql = finalSql.replaceAll("\r?\n", "").replaceAll(" ", "").toUpperCase();
        sql = sql.replaceAll("\r?\n", "").replaceAll(" ", "").toUpperCase();
        assertTrue(finalSql.equals(sql));
    }

    public void test3( ) {
        String sql = "select * from tablea t8 where t8.date_retired <= TIMESTAMP (concat(last_day(CAST(concat(concat(concat('2022','-'),'11'),'-01') AS DATE)),substr(TIMESTAMP (CAST(concat(concat(concat('2022','-'),'11'),'-01') AS DATE)),11)))";
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvdatabricks);
        sqlParser.sqltext = sql;
        int ret = sqlParser.parse();
        assertTrue(ret == 0);
        TSelectSqlStatement select = (TSelectSqlStatement) sqlParser.sqlstatements.get(0);
        String finalSql = select.toScript();
        finalSql = finalSql.replaceAll("\r?\n", "").replaceAll(" ", "").toUpperCase();
        sql = sql.replaceAll("\r?\n", "").replaceAll(" ", "").toUpperCase();
        assertTrue(finalSql.equals(sql));
    }

    public void test5( ) {
        String sql = "select * from tablea t0 where t0.snapshot_date <= DATE (TIMESTAMP (concat (date_add (CAST (concat (concat (concat (concat ('2022','-'),'12'),'-'),'01') AS DATE), " +
                "(CAST (60 AS INT))),substr (TIMESTAMP (CAST (concat (concat (concat (concat ('2022','-'),'12'),'-'),'01') AS DATE)),11))))";
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvdatabricks);
        sqlParser.sqltext = sql;
        int ret = sqlParser.parse();
        assertTrue(ret == 0);
        TSelectSqlStatement select = (TSelectSqlStatement) sqlParser.sqlstatements.get(0);
        String finalSql = select.toScript();
        finalSql = finalSql.replaceAll("\r?\n", "").replaceAll(" ", "").toUpperCase();
        sql = sql.replaceAll("\r?\n", "").replaceAll(" ", "").toUpperCase();
        assertTrue(finalSql.equals(sql));
    }

    public void test6( ) {
        String sql = "select * from tablea t0 , tableb t1 where t1.id=t0.id and from_unixtime (unix_timestamp (DATE (TIMESTAMP (concat (date_add (t0.snapshot_date, (CAST (-1 AS INT))),substr (TIMESTAMP (t0.snapshot_date),11))))),'yyyy-MM-dd') = SUBSTR (from_unixtime (unix_timestamp (t1.period_close_date),'yyyy-MM-dd HH:mm:ss.SSS000000'),0 +1,10 - (0))";
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvdatabricks);
        sqlParser.sqltext = sql;
        int ret = sqlParser.parse();
        assertTrue(ret == 0);
        TSelectSqlStatement select = (TSelectSqlStatement) sqlParser.sqlstatements.get(0);
        String finalSql = select.toScript();
        finalSql = finalSql.replaceAll("\r?\n", "").replaceAll(" ", "").toUpperCase();
        sql = sql.replaceAll("\r?\n", "").replaceAll(" ", "").toUpperCase();
        assertTrue(finalSql.equals(sql));
    }

}
