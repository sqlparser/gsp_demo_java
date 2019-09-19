package test.teradata;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.teradata.TTeradataGive;
import junit.framework.TestCase;

public class testGive extends TestCase {

    public void test1(){
          TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
          sqlparser.sqltext = "GIVE \"perms\" to USERS;";
          assertTrue(sqlparser.parse() == 0);
          assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstteradatagive);
          TTeradataGive give = (TTeradataGive)sqlparser.sqlstatements.get(0);
          assertTrue(give.getDb_or_user_name().toString().equalsIgnoreCase("\"perms\""));
          assertTrue(give.getRecipient_name().toString().equalsIgnoreCase("USERS"));
    }

}
