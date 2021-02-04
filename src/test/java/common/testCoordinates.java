package common;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TFunctionCall;

import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TCreateFunctionStmt;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.postgresql.TPostgresqlCreateFunction;
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

        TPostgresqlCreateFunction createFunction = (TPostgresqlCreateFunction)sqlparser.sqlstatements.get(0);
        assertTrue(createFunction.getFunctionName().toString().equalsIgnoreCase("totalrecords"));
        assertTrue(createFunction.getProcedureLanguage().toString().equalsIgnoreCase("plpgsql"));

        assertTrue(createFunction.getBodyStatements().size() == 2);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)createFunction.getBodyStatements().get(0);
        TTable table = selectSqlStatement.tables.getTable(0);
        assertTrue(table.getTableName().getLineNo() == 6);
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

    }

}
