package common;
/*
 * Date: 11-4-3
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

import gudusoft.gsqlparser.nodes.TCallSpec;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateFunction;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreatePackage;
import junit.framework.TestCase;

public class testPLSQLCallSpec extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "package body a is\n" +
                "\n" +
                "function XXPCK_STR2(key in varchar2, value in varchar2)  return varchar2 is\n" +
                "language java name 'oracle.apps.fnd.security.WebSessionManagerProc.decrypt(java.lang.String,java.lang.String) return java.lang.String';\n" +
                "\n" +
                "end;";
        assertTrue(sqlparser.parse() == 0);

        TPlsqlCreatePackage pkg = (TPlsqlCreatePackage)sqlparser.sqlstatements.get(0);
        TPlsqlCreateFunction f = (TPlsqlCreateFunction)pkg.getDeclareStatements().get(0);
        TCallSpec cs = f.getCallSpec();
        assertTrue(cs.getLang().equalsIgnoreCase("java"));
        assertTrue(cs.getDeclaration().toString().equalsIgnoreCase("'oracle.apps.fnd.security.WebSessionManagerProc.decrypt(java.lang.String,java.lang.String) return java.lang.String'"));
        //System.out.println(f.toString());
    }

}
