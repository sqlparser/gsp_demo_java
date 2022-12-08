package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.redshift.EAlterGroup;
import gudusoft.gsqlparser.stmt.TAlterGroup;
import junit.framework.TestCase;

public class testAlterGroup extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "alter group admin_group add user dwuser;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstAlterGroup);
        TAlterGroup group = (TAlterGroup) sqlparser.sqlstatements.get(0);
        assertTrue(group.getAlterGroupType() == EAlterGroup.eagAddUser);
        assertTrue(group.getGroupName().toString().endsWith("admin_group"));
        assertTrue(group.getUserList().getObjectName(0).toString().endsWith("dwuser"));
    }

    public void test2() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "alter group admin_group rename to administrators;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstAlterGroup);
        TAlterGroup group = (TAlterGroup) sqlparser.sqlstatements.get(0);
        assertTrue(group.getAlterGroupType() == EAlterGroup.eagRename);
        assertTrue(group.getGroupName().toString().endsWith("admin_group"));
        assertTrue(group.getNewGroupName().toString().endsWith("administrators"));
    }

}