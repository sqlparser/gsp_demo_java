package postgresql;
/*
 * Date: 13-12-4
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TReturnStmt;
import gudusoft.gsqlparser.stmt.*;
import junit.framework.TestCase;

public class testPlpgsql_return extends TestCase {
        public void test1(){
           TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
           sqlparser.sqltext = "CREATE FUNCTION somefunc() RETURNS integer AS $$\n" +
                   "<< outerblock >>\n" +
                   "DECLARE\n" +
                   "    quantity integer := 30;\n" +
                   "BEGIN\n" +
                   "   RETURN (1, 2, 'three'::text);\n" +
                   "END;\n" +
                   "$$ LANGUAGE plpgsql;";
           assertTrue(sqlparser.parse() == 0);

           TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
          assertTrue(createFunction.getBodyStatements().size() == 1);
          TCustomSqlStatement stmt = createFunction.getBodyStatements().get(0);
          assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_returnstmt);
            TReturnStmt  returnStmt = (TReturnStmt)stmt;
            TExpression returnV = returnStmt.getExpression();
            //System.out.println(returnV.getExpressionType() );
            assertTrue(returnV.getExpressionType() == EExpressionType.list_t);
            assertTrue(returnV.getExprList().size() == 3);
            assertTrue(returnV.getExprList().getExpression(0).toString().equalsIgnoreCase("1"));
            assertTrue(returnV.getExprList().getExpression(1).toString().equalsIgnoreCase("2"));
            assertTrue(returnV.getExprList().getExpression(2).toString().equalsIgnoreCase("'three'::text"));
        }
}
