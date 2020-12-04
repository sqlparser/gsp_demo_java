package redshift;

import gudusoft.gsqlparser.EDbObjectType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.redshift.TRedshiftComment;
import junit.framework.TestCase;


public class testComment extends TestCase {

    public void testTable() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "comment on table event is 'Contains listings of individual events.';";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftComment);
        TRedshiftComment comment = (TRedshiftComment) sqlparser.sqlstatements.get(0);
        assertTrue(comment.getDbObjectType() == EDbObjectType.table);
        assertTrue(comment.getObjectName().toString().equalsIgnoreCase("event"));
    }

    public void testConstraint() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "comment on CONSTRAINT c1 on event is 'Contains listings of individual events.';";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftComment);
        TRedshiftComment comment = (TRedshiftComment) sqlparser.sqlstatements.get(0);
        assertTrue(comment.getDbObjectType() == EDbObjectType.constraint);
        assertTrue(comment.getObjectName().toString().equalsIgnoreCase("c1"));
        assertTrue(comment.getOnObjectName().toString().equalsIgnoreCase("event"));
        assertTrue(comment.getCommentText().equalsIgnoreCase("'Contains listings of individual events.'"));
    }
}