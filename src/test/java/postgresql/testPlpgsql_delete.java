package postgresql;
/*
 * Date: 13-12-4
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDeleteSqlStatement;
import gudusoft.gsqlparser.stmt.TIfStmt;
import gudusoft.gsqlparser.stmt.*;
import junit.framework.TestCase;

public class testPlpgsql_delete extends TestCase {
    public void testSimpleCase(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE OR REPLACE FUNCTION update_emp_view() RETURNS TRIGGER AS $$\n" +
                "BEGIN\n" +
                "---- Perform the required operation on emp, and create a row in emp_audit\n" +
                "-- to reflect the change made to emp.\n" +
                "IF (TG_OP = 'DELETE') THEN\n" +
                "DELETE FROM emp WHERE empname = OLD.empname;\n" +
                "IF NOT FOUND THEN RETURN NULL; END IF;\n" +
                "OLD.last_updated = now();\n" +
                "INSERT INTO emp_audit VALUES('D', user, OLD.\n" +
                "*\n" +
                ");\n" +
                "RETURN OLD;\n" +
                "ELSIF (TG_OP = 'UPDATE') THEN\n" +
                "UPDATE emp SET salary = NEW.salary WHERE empname = OLD.empname;\n" +
                "IF NOT FOUND THEN RETURN NULL; END IF;\n" +
                "NEW.last_updated = now();\n" +
                "INSERT INTO emp_audit VALUES('U', user, NEW.\n" +
                "*\n" +
                ");\n" +
                "RETURN NEW;\n" +
                "ELSIF (TG_OP = 'INSERT') THEN\n" +
                "INSERT INTO emp VALUES(NEW.empname, NEW.salary);\n" +
                "NEW.last_updated = now();\n" +
                "INSERT INTO emp_audit VALUES('I', user, NEW.\n" +
                "*\n" +
                ");\n" +
                "RETURN NEW;\n" +
                "END IF;\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;";
        assertTrue(sqlparser.parse() == 0);

        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createFunction.getBodyStatements().size() == 1);
        TIfStmt ifStmt = (TIfStmt)createFunction.getBodyStatements().get(0);
        assertTrue(ifStmt.getThenStatements().size() == 5);
        TDeleteSqlStatement delete = (TDeleteSqlStatement)ifStmt.getThenStatements().get(0);
        assertTrue(delete.getTargetTable().toString().equalsIgnoreCase("emp"));
        assertTrue(delete.getWhereClause().getCondition().getExpressionType() == EExpressionType.simple_comparison_t);
        assertTrue(delete.getWhereClause().getCondition().getLeftOperand().toString().equalsIgnoreCase("empname"));
    }

}
