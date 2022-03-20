package mysql;


import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.stmt.TCreateFunctionStmt;
import junit.framework.TestCase;

public class testCreateFucntion extends TestCase {

 public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "CREATE FUNCTION `func1`(p1 INT) RETURNS varchar(8000)\n" +
                "READS SQL DATA\n" +
                "DETERMINISTIC\n" +
                "BEGIN\n" +
                "DECLARE v_LIMITSTR VARCHAR(20);\n" +
                "IF p1 = -1 THEN\n" +
                "SET v_LIMITSTR = \"\";\n" +
                "ELSE\n" +
                "SET v_LIMITSTR = CONCAT(\"LIMIT \",p1);\n" +
                "END IF;\n" +
                "RETURN CONCAT(\"SELECT t1.c1, t2.c1, CONCAT('v=',t2.c1,';f=',t1.c1), CONCAT('v=',t2.c7,';f=',t0.c1),\n" +
                "t0.c2 AS `Test` FROM t0, t1, t2, t3\n" +
                "WHERE t0.c1 = t1.c6 AND\n" +
                "t1.c7 = t2.c7 AND\n" +
                "t1.c6 = t3.c6 AND\n" +
                "t0.c2 >= t3.c8 AND\n" +
                "t0.c3='weekend' AND\n" +
                "t0.c4='chickenrun' AND\n" +
                "t0.c5 = (SELECT MAX(c5) FROM t4 WHERE c3='weekend' AND c4='chickenrun')\n" +
                "ORDER BY t0.c2 DESC\", v_LIMITSTR, \";\") ;\n" +
                "END;";
        assertTrue(sqlparser.parse() == 0);
     TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);
        assertTrue(sqlStatement.toString().trim().equalsIgnoreCase("CREATE FUNCTION `func1`(p1 INT) RETURNS varchar(8000)\n" +
                "READS SQL DATA\n" +
                "DETERMINISTIC\n" +
                "BEGIN\n" +
                "DECLARE v_LIMITSTR VARCHAR(20);\n" +
                "IF p1 = -1 THEN\n" +
                "SET v_LIMITSTR = \"\";\n" +
                "ELSE\n" +
                "SET v_LIMITSTR = CONCAT(\"LIMIT \",p1);\n" +
                "END IF;\n" +
                "RETURN CONCAT(\"SELECT t1.c1, t2.c1, CONCAT('v=',t2.c1,';f=',t1.c1), CONCAT('v=',t2.c7,';f=',t0.c1),\n" +
                "t0.c2 AS `Test` FROM t0, t1, t2, t3\n" +
                "WHERE t0.c1 = t1.c6 AND\n" +
                "t1.c7 = t2.c7 AND\n" +
                "t1.c6 = t3.c6 AND\n" +
                "t0.c2 >= t3.c8 AND\n" +
                "t0.c3='weekend' AND\n" +
                "t0.c4='chickenrun' AND\n" +
                "t0.c5 = (SELECT MAX(c5) FROM t4 WHERE c3='weekend' AND c4='chickenrun')\n" +
                "ORDER BY t0.c2 DESC\", v_LIMITSTR, \";\") ;\n" +
                "END;"));
    }


public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "create aggregate function encsum returns string soname 'udf_example.so'";
        assertTrue(sqlparser.parse() == 0);
        TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);
        assertTrue(sqlStatement.sqlstatementtype == ESqlStatementType.sstcreatefunction);
        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlStatement;
        assertTrue(createFunction.getReturnDataType().getDataType() == EDataType.string_t);
        assertTrue(createFunction.getSharedLibraryName().equalsIgnoreCase("'udf_example.so'"));
}

}
