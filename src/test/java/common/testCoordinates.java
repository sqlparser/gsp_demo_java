package common;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;

import gudusoft.gsqlparser.stmt.TCreateFunctionStmt;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.*;
import junit.framework.TestCase;


public class testCoordinates extends TestCase {

    public void testCoordinates1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE OR REPLACE FUNCTION totalRecords (emp_id int)\n" +
                "RETURNS integer AS $total$\n" +
                "declare\n" +
                "total integer;\n" +
                "BEGIN\n" +
                "  SELECT TOTAL_SAL into total FROM employee emp where emp.employee_id = emp_id;\n" +
                "  RETURN total;\n" +
                "END; $total$ LANGUAGE plpgsql;";
        assertTrue(sqlparser.parse() == 0);

        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createFunction.getFunctionName().toString().equalsIgnoreCase("totalrecords"));
        assertTrue(createFunction.getProcedureLanguage().toString().equalsIgnoreCase("plpgsql"));

        assertTrue(createFunction.getBodyStatements().size() == 2);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)createFunction.getBodyStatements().get(0);
        TTable table = selectSqlStatement.tables.getTable(0);
//        System.out.println(table.getTableName().getLineNo());
//        System.out.println(table.getTableName().getColumnNo());
        assertTrue(table.getTableName().getLineNo() == 6);
        assertTrue(table.getTableName().getColumnNo() == 36);
    }

    public void testCoordinates11(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE OR REPLACE FUNCTION totalRecords (emp_id int)\n" +
                "RETURNS integer AS $total$ " +
                "declare\n" +
                "total integer;\n" +
                "BEGIN\n" +
                "  SELECT TOTAL_SAL into total FROM employee emp where emp.employee_id = emp_id;\n" +
                "  RETURN total;\n" +
                "END; $total$ LANGUAGE plpgsql;";
        assertTrue(sqlparser.parse() == 0);

        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createFunction.getFunctionName().toString().equalsIgnoreCase("totalrecords"));
        assertTrue(createFunction.getProcedureLanguage().toString().equalsIgnoreCase("plpgsql"));

        assertTrue(createFunction.getBodyStatements().size() == 2);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)createFunction.getBodyStatements().get(0);
        TTable table = selectSqlStatement.tables.getTable(0);
        assertTrue(table.getTableName().getLineNo() == 5);
        assertTrue(table.getTableName().getColumnNo() == 36);
    }

    public void testCoordinates2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE OR REPLACE FUNCTION\n" +
                "foo.func1(integer, OUT f1 integer, OUT f2 text)\n" +
                "RETURNS record\n" +
                "LANGUAGE sql\n" +
                "AS $function$ SELECT $1, CAST($1 AS text) || ' is text' $function$";
        assertTrue(sqlparser.parse() == 0);

        functionVisitor fv = new functionVisitor();
        sqlparser.sqlstatements.get(0).acceptChildren(fv);
        assertTrue(fv.LineNo == 5);
        assertTrue(fv.ColumnNo == 26);

    }

    public void testCoordinates3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE OR REPLACE FUNCTION\n" +
                "foo.func1(integer, OUT f1 integer, OUT f2 text)\n" +
                "RETURNS record\n" +
                "LANGUAGE sql\n" +
                "AS $function$\n" +
                "SELECT $1, CAST($1 AS text) || ' is text' $function$";
        assertTrue(sqlparser.parse() == 0);

        functionVisitor fv = new functionVisitor();
        sqlparser.sqlstatements.get(0).acceptChildren(fv);
        assertTrue(fv.LineNo == 6);
        assertTrue(fv.ColumnNo == 12);

    }

    public void testCoordinates4(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create or replace function get_countries_for_user ( id number )\n" +
                "  returns table (country_code char, country_name varchar)\n" +
                "  as 'select distinct c.country_code, c.country_name\n" +
                "      from user_addresses a, countries c\n" +
                "      where a.user_id = id\n" +
                "      and c.country_code = a.country_code';";
        assertTrue(sqlparser.parse() == 0);

        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createFunction.getFunctionName().toString().equalsIgnoreCase("get_countries_for_user"));

        assertTrue(createFunction.getBodyStatements().size() == 1);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)createFunction.getBodyStatements().get(0);
        TTable table = selectSqlStatement.tables.getTable(0);
        assertTrue(table.getTableName().toString().equalsIgnoreCase("user_addresses"));
        assertTrue(table.getTableName().getLineNo() == 4);
        assertTrue (table.getTableName().getColumnNo() == 12);
        table = selectSqlStatement.tables.getTable(1);
        assertTrue(table.getTableName().toString().equalsIgnoreCase("countries"));
        assertTrue(table.getTableName().getLineNo() == 4);
        assertTrue (table.getTableName().getColumnNo() == 30);
    }

    public void testCoordinates5(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "create function f_sql_greater (float, float)\n" +
                "    returns float\n" +
                "    stable\n" +
                "    as $$\n" +
                "    select case when $1 > $2 then $1\n" +
                "    else $2\n" +
                "    end\n" +
                "    $$ language sql;";
        assertTrue(sqlparser.parse() == 0);

        functionVisitor fv = new functionVisitor();
        sqlparser.sqlstatements.get(0).acceptChildren(fv);
        assertTrue(fv.LineNo == 5);
        assertTrue(fv.ColumnNo == 12);

    }

    public void testPostgresql5(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE OR REPLACE FUNCTION testschema.functionreturnstrigger()\n" +
                " RETURNS trigger\n" +
                " LANGUAGE plpgsql\n" +
                "AS $function$\n" +
                "BEGIN\n" +
                "   IF NEW.last_name <> OLD.last_name THEN\n" +
                "      INSERT INTO TestSchema.\"TestTable\"(ColumnText1)\n" +
                "  SELECT ColumnText2 FROM testschema.\"TestTable\";\n" +
                "   END IF;\n" +
                "   RETURN NEW;\n" +
                "END;\n" +
                "$function$";
        //System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);

        functionVisitor fv = new functionVisitor();
        sqlparser.sqlstatements.get(0).acceptChildren(fv);
        assertTrue(fv.LineNo == 7);
        assertTrue(fv.ColumnNo == 42);

    }

    class functionVisitor extends TParseTreeVisitor {

        public long LineNo, ColumnNo;
        public void preVisit(TFunctionCall functionCall){
            if (functionCall.getFunctionName().toString().equalsIgnoreCase("cast")){
                LineNo = functionCall.getFunctionName().getLineNo();
                ColumnNo = functionCall.getFunctionName().getColumnNo();
//            System.out.println(functionCall.getFunctionName().getLineNo());
//            System.out.println(functionCall.getFunctionName().getColumnNo());
            }
        }
        public void preVisit(TCaseExpression caseExpression){
            LineNo = caseExpression.getLineNo();
            ColumnNo = caseExpression.getColumnNo();
//            System.out.println(caseExpression.getStartToken().toString()+ caseExpression.getLineNo());
//            System.out.println(caseExpression.getColumnNo());
        }

        public void preVisit(TObjectName objectName){
            if (objectName.toString().equalsIgnoreCase("ColumnText1")){
                LineNo = objectName.getLineNo();
                ColumnNo = objectName.getColumnNo();
            }
        }

    }

}
