package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testStmtDelimiter extends TestCase {

    public void test3() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "CREATE FUNCTION PAC4_USER.\"PS_FIRST_ERROR_BEGIN\"(\n" +
                "\n" +
                "    PI_KAISHA_CD VARCHAR(16383),\n" +
                "\n" +
                "    PI_SESSION_ID VARCHAR(16383),\n" +
                "\n" +
                "    PO_REPLACE_STRING VARCHAR(16383)\n" +
                "\n" +
                ")\n" +
                "\n" +
                "RETURNS VARCHAR(16383)\n" +
                "\n" +
                "BEGIN\n" +
                "\n" +
                "    DECLARE W_LOOP_COUNT DECIMAL(4,0) DEFAULT 1;\n" +
                "\n" +
                "    DECLARE PO_DEN_NO VARCHAR(30) DEFAULT NULL;\n" +
                "\n" +
                "    DECLARE EXIT HANDLER FOR NOT FOUND,SQLEXCEPTION\n" +
                "\n" +
                "        BEGIN\n" +
                "\n" +
                "            SET PO_DEN_NO = PI_KAISHA_CD;\n" +
                "\n" +
                "            RETURN PO_DEN_NO;\n" +
                "\n" +
                "        END;\n" +
                "\n" +
                "    SET W_LOOP_COUNT = W_LOOP_COUNT + 1;\n" +
                "\n" +
                "    RETURN PO_DEN_NO;\n" +
                "\n" +
                "END;\n" +
                "\n" +
                "-- sqlflow-delimiter\n" +
                "\n" +
                "create function func_26093_b(x int, y int) returns int\n" +
                "begin\n" +
                "set @invoked := @invoked + 1;\n" +
                "return x;\n" +
                "end\n" +
                "\n" +
                "\n" +
                "-- sqlflow-delimiter\n" +
                "\n" +
                "create aggregate function encsum returns string soname 'udf_example.so';\n" +
                "\n" +
                "-- sqlflow-delimiter\n" +
                "\n" +
                "\n" +
                "create function encmul returns string soname 'udf_example.so'";

        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.size() ==4);

    }

        public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE FUNCTION check_password(uname TEXT, pass TEXT)\n" +
                "RETURNS BOOLEAN AS $$\n" +
                "DECLARE passed BOOLEAN;\n" +
                "BEGIN\n" +
                "        SELECT  (pwd = $2) INTO passed\n" +
                "        FROM    pwds\n" +
                "        WHERE   username = $1;\n" +
                "\n" +
                "        RETURN passed;\n" +
                "END;\n" +
                "$$  LANGUAGE plpgsql\n" +
                "    SECURITY DEFINER\n" +
                "\n" +
                "-- sqlflow-delimiter\n" +
                "\n" +
                "SELECT  (pwd = $2) INTO passed\n" +
                "FROM    pwds\n" +
                "WHERE   username = $1;\n" +
                "\t\n" +
                "-- sqlflow-delimiter\n" +
                "\t\n" +
                "CREATE FUNCTION dup(int) RETURNS TABLE(f1 int, f2 text)\n" +
                "    AS $$ SELECT $1, CAST($1 AS text) || ' is text' $$\n" +
                "    LANGUAGE SQL;\n" +
                "\t\n" +
                "-- sqlflow_delimiter\n" +
                "\n" +
                "CREATE OR REPLACE FUNCTION test_data_it.function3(double precision, double precision)\n" +
                " RETURNS double precision\n" +
                " LANGUAGE sql\n" +
                " STABLE\n" +
                "AS $function$\n" +
                "    select case when $1 > $2 then $1\n" +
                "        else $2\n" +
                "    end\n" +
                "$function$\t\n" +
                "\n" +
                "-- sqlflow_delimiter";

        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.size() ==4);
    }
        public void test0(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "REPLACE PROCEDURE S1.IF_CHECK(IN RUN_ID DECIMAL(12,0))\n" +
                "  BEGIN\n" +
                "\t  DECLARE EXIT HANDLER FOR incorrectStep,incorrectInput\n" +
                "\t  BEGIN\n" +
                "\t\t  INSERT INTO PROD_INT_SL_META.Error_Log(ERRORTIME,SQL_CODE,SQL_STATE) VALUES(CURRENT_TIMESTAMP(0),:SQLCODE,:SQLSTATE);\n" +
                "\t\t  CALL PROD_INT_SL_SHARED_META.DETAIL_LOG_ENTRY_EE (CURRENT_TIMESTAMP(0));\n" +
                "\t\t  END;\n" +
                "\t\t  DECLARE EXIT HANDLER FOR inValidOperation\n" +
                "\t\t  BEGIN\n" +
                "\t\t  INSERT INTO PROD_INT_SL_META.Error_Log(ERRORTIME,SQL_CODE,SQL_STATE) VALUES(CURRENT_TIMESTAMP(0),:SQLCODE,:SQLSTATE);\n" +
                "\t\t  CALL PROD_INT_SL_SHARED_META.DETAIL_LOG_ENTRY_EE (CURRENT_TIMESTAMP(0));\n" +
                "\t  END;\n" +
                "\n" +
                "\t  DECLARE EXIT HANDLER FOR SQLEXCEPTION\n" +
                "\t\tCALL PROD_INT_SL_SHARED_META.DETAIL_LOG_ENTRY_EE (CURRENT_TIMESTAMP(0));\n" +
                "\t  \n" +
                "\t  CALL PROD_INT_SL_SHARED_META.DETAIL_LOG_ENTRY_FF (CURRENT_TIMESTAMP(6));\n" +
                "  END;\n" +
                "  \n" +
                "-- sqlflow-delimiter\n" +
                "\n" +
                "REPLACE PROCEDURE foodmart.delStmtCur()\n" +
                "BEGIN\n" +
                "INSERT INTO foodmart.tem_dep\n" +
                " (\n" +
                "  department_id, department_name\n" +
                ")\n" +
                "\n" +
                "SELECT\n" +
                " CAST ( ((10 * foodmart.employee.salary)\n" +
                "/foodmart.employee.employee_id)\n" +
                "AS DECIMAL (18,2)) AS department_id\n" +
                ",first_name AS department_name\n" +
                "FROM foodmart.employee where employee_id < 10;\n" +
                "END;\n" +
                "\n" +
                "-- sqlflow-delimiter\n" +
                "\n" +
                "\tCreate PROCEDURE S1.FOR_CURSOR()\n" +
                "         BEGIN\n" +
                "          DECLARE id integer;\n" +
                "          \n" +
                "          FOR for_loop_var AS cur CURSOR FOR\n" +
                "          SELECT department_id000, department_description111 FROM foodmart.department\n" +
                "          DO\n" +
                "          SET id = for_loop_var.department_id;\n" +
                "          if(id < 10) then\n" +
                "          REWIND cur;\n" +
                "          End if;\n" +
                "          END FOR;\n" +
                "         END;\n" +
                "\n" +
                "\n" +
                "-- sqlflow-delimiter\n" +
                "\n" +
                "INSERT INTO PROD_INT_SL_META.Error_Log(ERRORTIME,SQL_CODE,SQL_STATE) VALUES(CURRENT_TIMESTAMP(0),:SQLCODE,:SQLSTATE);  \n" +
                "\n" +
                "\n" +
                "-- sqlflow-delimiter\n";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.size() ==4);

    }
}
