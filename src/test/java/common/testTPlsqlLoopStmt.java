package common;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCommonBlock;
import gudusoft.gsqlparser.stmt.TLoopStmt;
import junit.framework.TestCase;

public class testTPlsqlLoopStmt extends TestCase {

    public void testBasicLoop(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "BEGIN\n" +
                "loop\n" +
                "DBMS_OUTPUT.PUT_LINE(i); -- statements here execute 10 times\n" +
                "END LOOP;\n" +
                "END;\n" +
                "/";
        assertTrue(sqlparser.parse() == 0);

        TCommonBlock block = (TCommonBlock)sqlparser.sqlstatements.get(0);
        TLoopStmt loopStmt = (TLoopStmt)block.getBodyStatements().get(0);
        assertTrue(loopStmt.getKind() == TLoopStmt.basic_loop);

    }

    public void testWhileLoop(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "BEGIN\n" +
                "while i>0 loop\n" +
                "DBMS_OUTPUT.PUT_LINE(i); -- statements here execute 10 times\n" +
                "END LOOP;\n" +
                "END;\n" +
                "/";
        assertTrue(sqlparser.parse() == 0);

        TCommonBlock block = (TCommonBlock)sqlparser.sqlstatements.get(0);
        TLoopStmt loopStmt = (TLoopStmt)block.getBodyStatements().get(0);
        assertTrue(loopStmt.getKind() == TLoopStmt.while_loop);
        assertTrue(loopStmt.getCondition().toString().equalsIgnoreCase("i>0"));
    }

    public void testForLoop(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "BEGIN\n" +
                "FOR i IN REVERSE 1..10 LOOP -- i starts at 10, ends at 1\n" +
                "DBMS_OUTPUT.PUT_LINE(i); -- statements here execute 10 times\n" +
                "END LOOP;\n" +
                "END;\n" +
                "/";
        assertTrue(sqlparser.parse() == 0);

        TCommonBlock block = (TCommonBlock)sqlparser.sqlstatements.get(0);
        TLoopStmt loopStmt = (TLoopStmt)block.getBodyStatements().get(0);
        assertTrue(loopStmt.getKind() == TLoopStmt.for_loop);
        assertTrue(loopStmt.getIndexName().toString().equalsIgnoreCase("i"));
        assertTrue(loopStmt.isReverse());
        assertTrue(loopStmt.getLower_bound().toString().equalsIgnoreCase("1"));
        assertTrue(loopStmt.getUpper_bound().toString().equalsIgnoreCase("10"));
    }

    public void testCursorLoop1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "BEGIN\n" +
                "FOR employee_rec in c1\n" +
                "LOOP\n" +
                "    total_val := total_val + employee_rec.monthly_income;\n" +
                "END LOOP;\n" +
                "END;";
        assertTrue(sqlparser.parse() == 0);

        TCommonBlock block = (TCommonBlock)sqlparser.sqlstatements.get(0);
        TLoopStmt loopStmt = (TLoopStmt)block.getBodyStatements().get(0);
        assertTrue(loopStmt.getKind() == TLoopStmt.cursor_for_loop);
        assertTrue(loopStmt.getRecordName().toString().equalsIgnoreCase("employee_rec"));
        assertTrue(loopStmt.getCursorName().toString().equalsIgnoreCase("c1"));
    }

    public void testCursorLoop2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "BEGIN\n" +
                "   FOR trip_record IN (SELECT bt_id_pk, bt_duration\n" +
                "                      FROM business_trips) LOOP\n" +
                "      -- implicit open/fetch occurs\n" +
                "      IF trip_record.bt_duration = 1 THEN\n" +
                "        DBMS_OUTPUT_LINE ('trip Number ' || trip_record.bt_id_pk\n" +
                "                        || ' is a one day trip');\n" +
                "      END IF;\n" +
                "   END LOOP; -- implicit CLOSE occurs\n" +
                "END;";

        assertTrue(sqlparser.parse() == 0);

        TCommonBlock block = (TCommonBlock)sqlparser.sqlstatements.get(0);
        TLoopStmt loopStmt = (TLoopStmt)block.getBodyStatements().get(0);
        assertTrue(loopStmt.getKind() == TLoopStmt.cursor_for_loop);
        assertTrue(loopStmt.getRecordName().toString().equalsIgnoreCase("trip_record"));
        //assertTrue(loopStmt.getCursorName().toString().equalsIgnoreCase("c1"));
        assertTrue(loopStmt.getSubquery().toString().equalsIgnoreCase("(SELECT bt_id_pk, bt_duration\n" +
                "                      FROM business_trips)"));
    }

    public void testCursorLoop3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "BEGIN\n" +
                "   FOR trip_record IN trip_cursor(12, 3)\n" +
                "                       LOOP\n" +
                "      -- implicit open/fetch occurs\n" +
                "      IF trip_record.bt_duration = 1 THEN\n" +
                "        DBMS_OUTPUT_LINE ('trip Number ' || trip_record.bt_id_pk\n" +
                "                        || ' is a one day trip');\n" +
                "      END IF;\n" +
                "   END LOOP; -- implicit CLOSE occurs\n" +
                "END;";

        assertTrue(sqlparser.parse() == 0);

        TCommonBlock block = (TCommonBlock)sqlparser.sqlstatements.get(0);
        TLoopStmt loopStmt = (TLoopStmt)block.getBodyStatements().get(0);
        assertTrue(loopStmt.getKind() == TLoopStmt.cursor_for_loop);
        assertTrue(loopStmt.getRecordName().toString().equalsIgnoreCase("trip_record"));
        assertTrue(loopStmt.getCursorName().toString().equalsIgnoreCase("trip_cursor"));
        assertTrue(loopStmt.getCursorParameterNames().size() == 2);
        assertTrue(loopStmt.getCursorParameterNames().getExpression(0).toString().equalsIgnoreCase("12"));
        assertTrue(loopStmt.getCursorParameterNames().getExpression(1).toString().equalsIgnoreCase("3"));
    }

}
