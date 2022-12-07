package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateGroup;
import junit.framework.TestCase;


public class testCreateGroup extends TestCase {

    public void testTable() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "create group admin_group with user admin;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftCreateGroup);
        TCreateGroup createGroup = (TCreateGroup) sqlparser.sqlstatements.get(0);
        assertTrue(createGroup.getGroupName().toString().equalsIgnoreCase("admin_group"));
        assertTrue(createGroup.getUserList().getObjectName(0).toString().equalsIgnoreCase("admin"));
    }
}