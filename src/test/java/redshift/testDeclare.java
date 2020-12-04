package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.redshift.TRedshiftDeclare;
import junit.framework.TestCase;

public class testDeclare extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "declare lollapalooza cursor for \n" +
                "select eventname, starttime, pricepaid/qtysold as costperticket, qtysold\n" +
                "from sales, event\n" +
                "where sales.eventid = event.eventid\n" +
                "and eventname='Lollapalooza';";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftDeclare);
        TRedshiftDeclare declare = (TRedshiftDeclare) sqlparser.sqlstatements.get(0);
        assertTrue(declare.getCursorName().toString().equalsIgnoreCase("lollapalooza"));

        TSelectSqlStatement select = declare.getSubquery();
        assertTrue(select.getResultColumnList().getResultColumn(0).toString().equalsIgnoreCase("eventname"));
        TExpression condition  = select.getWhereClause().getCondition();
        assertTrue(condition.getExpressionType() == EExpressionType.logical_and_t);

    }
}