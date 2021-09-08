package postgresql;
/*
 * Date: 13-12-4
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TExecuteSqlStatement;
import gudusoft.gsqlparser.stmt.*;
import junit.framework.TestCase;

public class testPlpgsql_execute extends TestCase {

   public void test1(){
           TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
           sqlparser.sqltext = "CREATE FUNCTION somefunc() RETURNS integer AS $$\n" +
                   "<< outerblock >>\n" +
                   "DECLARE\n" +
                   "    quantity integer := 30;\n" +
                   "BEGIN\n" +
                   "EXECUTE 'SELECT count(*) FROM '\n" +
                   "    || tabname::regclass\n" +
                   "    || ' WHERE inserted_by = $1 AND inserted <= $2'\n" +
                   "   INTO c\n" +
                   "   USING checked_user, checked_date;\n" +
                   "\n" +
                   "    RETURN quantity;\n" +
                   "END;\n" +
                   "$$ LANGUAGE plpgsql;";
           assertTrue(sqlparser.parse() == 0);

           TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
          assertTrue(createFunction.getBodyStatements().size() == 2);
          TCustomSqlStatement stmt = createFunction.getBodyStatements().get(0);
          assertTrue(stmt.sqlstatementtype == ESqlStatementType.sstExecute);
       TExecuteSqlStatement exec = (TExecuteSqlStatement)stmt;
       assertTrue(exec.getStmtString().toString().equalsIgnoreCase("'SELECT count(*) FROM '\n" +
               "    || tabname::regclass\n" +
               "    || ' WHERE inserted_by = $1 AND inserted <= $2'"));

       assertTrue(exec.getIntoVariable().toString().equalsIgnoreCase("c"));
       assertTrue(exec.getUsingVariables().size() == 2);
       assertTrue(exec.getUsingVariables().getExpression(0).toString().equalsIgnoreCase("checked_user"));
       assertTrue(exec.getUsingVariables().getExpression(1).toString().equalsIgnoreCase("checked_date"));
   }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE OR REPLACE FUNCTION t.mergemodel(_modelid integer)\n" +
                "RETURNS void\n" +
                "LANGUAGE plpgsql\n" +
                "AS $function$\n" +
                "BEGIN\n" +
                "    EXECUTE format ('INSERT INTO InSelections\n" +
                "                                  SELECT * FROM AddInSelections_%s', modelid);\n" +
                "                    \n" +
                "END;\n" +
                "$function$";
        assertTrue(sqlparser.parse() == 0);

        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createFunction.getBodyStatements().size() == 1);
        TCustomSqlStatement stmt = createFunction.getBodyStatements().get(0);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sstExecute);
        TExecuteSqlStatement exec = (TExecuteSqlStatement)stmt;
        assertTrue(exec.getSqlText().equalsIgnoreCase("'INSERT INTO InSelections\n" +
                "                                  SELECT * FROM AddInSelections_PLACEHOLDER'"));
    }

}
