package db2;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateProcedureStmt;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import junit.framework.TestCase;


public class testCreateProcedure extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);

        sqlparser.sqltext = "CREATE PROCEDURE CREATE_T_EMP()\n" +
                "   LANGUAGE SQL\n" +
                "BEGIN\n" +
                "DECLARE SQLCODE INT;\n" +
                "DECLARE l_sqlcode INT DEFAULT 0;\n" +
                "\n" +
                "  DECLARE CONTINUE HANDLER FOR NOT FOUND\n" +
                "    SET l_sqlcode = SQLCODE; \n" +
                "\n" +
                "        INSERT INTO PROJECT (PROJNO, PROJNAME, DEPTNO, RESPEMP, PRSTDATE) \n" +
                "        VALUES('HG0023', 'NEW NETWORK', 'E11', '200280', CURRENT DATE); \n" +
                "END";

        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreateprocedure);
        TCreateProcedureStmt procedure = (TCreateProcedureStmt)sqlparser.sqlstatements.get(0);
       // System.out.print(procedure.getBodyStatements().size());
        assertTrue(procedure.getDeclareStatements().size() == 3);
        assertTrue(procedure.getDeclareStatements().get(2).toString().equalsIgnoreCase("DECLARE CONTINUE HANDLER FOR NOT FOUND\n" +
                "    SET l_sqlcode = SQLCODE"));
        assertTrue(procedure.getBodyStatements().size() == 1);

        assertTrue(procedure.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstinsert);
        TInsertSqlStatement insert = (TInsertSqlStatement)procedure.getBodyStatements().get(0);
       // System.out.print(insert.toString());
       // assertTrue(insert.toString() != null);
        assertTrue(insert.toString().equalsIgnoreCase("INSERT INTO PROJECT (PROJNO, PROJNAME, DEPTNO, RESPEMP, PRSTDATE) \n" +
                "        VALUES('HG0023', 'NEW NETWORK', 'E11', '200280', CURRENT DATE)"));


    }
}