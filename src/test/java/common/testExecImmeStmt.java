package common;

import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.stmt.TExecImmeStmt;
import gudusoft.gsqlparser.stmt.TCommonBlock;


public class testExecImmeStmt extends TestCase {
    private TGSqlParser parser = null;

    protected void setUp() throws Exception {
        super.setUp();
        parser = new TGSqlParser(EDbVendor.dbvoracle);
    }

    protected void tearDown() throws Exception {
        parser = null;
        super.tearDown();
    }

    public void testMember(){

        parser.sqltext = "Execute Immediate\n" +
                "     'Drop table ap_temp_data_driver_6992095';";
        assertTrue(parser.parse() == 0);
        //System.out.println(parser.sqlstatements.get(0).sqlstatementtype);
        TExecImmeStmt stmt = (TExecImmeStmt)parser.sqlstatements.get(0);
        assertTrue(stmt.getDynamicStringExpr().toString().compareToIgnoreCase("'Drop table ap_temp_data_driver_6992095'") == 0);

        stmt = null;
      
        parser.sqltext ="DECLARE\n" +
                "plsql_block VARCHAR2(500);\n" +
                "new_deptid NUMBER(4);\n" +
                "new_dname VARCHAR2(30) := 'Advertising';\n" +
                "new_mgrid NUMBER(6) := 200;\n" +
                "new_locid NUMBER(4) := 1700;\n" +
                "BEGIN\n" +
                "-- Dynamic PL/SQL block invokes subprogram:\n" +
                "plsql_block := 'BEGIN create_dept(:a, :b, :c, :d); END;';\n" +
                "\n" +
                "EXECUTE IMMEDIATE plsql_block\n" +
                "USING IN OUT new_deptid, new_dname, new_mgrid, new_locid;\n" +
                "END;\n" +
                "/";
        assertTrue(parser.parse() == 0);
        //System.out.println(parser.sqlstatements.get(0).sqlstatementtype);
        TCommonBlock stmt1 = (TCommonBlock)parser.sqlstatements.get(0);
        TExecImmeStmt stmt2 = (TExecImmeStmt)stmt1.getBodyStatements().get(1);

       // System.out.println(stmt2.getDynamicSQL());
       // System.out.println(stmt2.getDynamicStatements().size());

    }
    
}
