package gudusoft.gsqlparser.hanaTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateProcedureStmt;
import junit.framework.TestCase;

public class testCreateProcedure extends TestCase {
    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhana);
        sqlparser.sqltext = "CREATE PROCEDURE upsert_proc (IN v_isbn VARCHAR(20))\n" +
                "LANGUAGE SQLSCRIPT AS\n" +
                "BEGIN\n" +
                "    DECLARE found INT = 1;\n" +
                "    SELECT count(*) INTO found FROM books WHERE isbn = :v_isbn;\n" +
                "    IF :found = 0\n" +
                "    THEN\n" +
                "        INSERT INTO books\n" +
                "        VALUES (:v_isbn, 'In-Memory Data Management', 1, 1,\n" +
                "                '2011', 42.75, 'EUR');\n" +
                "    ELSE\n" +
                "        UPDATE books SET price = 42.75 WHERE isbn =:v_isbn;\n" +
                "    END IF;\n" +
                "END;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreateprocedure);
        TCreateProcedureStmt createProcedureStmt = (TCreateProcedureStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createProcedureStmt.getBodyStatements().size() == 3);
        for(TCustomSqlStatement sqlStatement: createProcedureStmt.getBodyStatements()){
           // System.out.println(sqlStatement.sqlstatementtype);
        }
    }
}
