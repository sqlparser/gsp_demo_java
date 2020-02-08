package redshift;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TIntoClause;
import gudusoft.gsqlparser.nodes.TParameterDeclaration;
import gudusoft.gsqlparser.stmt.TCreateFunctionStmt;
import gudusoft.gsqlparser.stmt.TCreateProcedureStmt;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

import static gudusoft.gsqlparser.ESqlStatementType.sstplsql_vardecl;

public class TestCreateProcedure extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "CREATE OR REPLACE PROCEDURE test_sp1(f1 int, f2 varchar(20))\n" +
                "AS $$\n" +
                "DECLARE\n" +
                "  min_val int;\n" +
                "BEGIN\n" +
                "  DROP TABLE IF EXISTS tmp_tbl;\n" +
                "  CREATE TEMP TABLE tmp_tbl(id int);\n" +
                "  INSERT INTO tmp_tbl values (f1),(10001),(10002);\n" +
                "  SELECT MIN(id) INTO min_val  FROM tmp_tbl;\n" +
                "  RAISE INFO 'min_val = %, f2 = %', min_val, f2;\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;";
        assertTrue(sqlparser.parse() == 0);
        TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);
        assertTrue(sqlStatement.sqlstatementtype == ESqlStatementType.sstcreateprocedure);

        TCreateProcedureStmt createProcedure = (TCreateProcedureStmt)sqlStatement;
        assertTrue(createProcedure.getProcedureName().toString().equalsIgnoreCase("test_sp1"));
        assertTrue(createProcedure.getParameterDeclarations().size() == 2);
        TParameterDeclaration parameterDeclaration = createProcedure.getParameterDeclarations().getParameterDeclarationItem(0);
        assertTrue(parameterDeclaration.getDataType().getDataType() == EDataType.int_t);
        assertTrue(parameterDeclaration.getParameterName().toString().equalsIgnoreCase("f1"));

        assertTrue(createProcedure.getRoutineLanguage().equalsIgnoreCase("plpgsql"));

        assertTrue(createProcedure.getDeclareStatements().size() == 1);
        assertTrue (createProcedure.getDeclareStatements().get(0).sqlstatementtype==  sstplsql_vardecl);
        assertTrue(createProcedure.getBodyStatements().size() == 5);
        assertTrue(createProcedure.getBodyStatements().get(3).sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)createProcedure.getBodyStatements().get(3);
        assertTrue(selectSqlStatement.getTables().getTable(0).toString().equalsIgnoreCase("tmp_tbl"));

    }

    public void test11(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "CREATE OR REPLACE PROCEDURE test_sp1(f1 int, f2 varchar(20))\n" +
                "AS $$\n" +
                "DECLARE\n" +
                "  min_val int;\n" +
                "BEGIN\n" +
                "  DROP TABLE IF EXISTS tmp_tbl;\n" +
                "  CREATE TEMP TABLE tmp_tbl(id int);\n" +
                "  INSERT INTO tmp_tbl values (f1),(10001),(10002);\n" +
                "  SELECT INTO min_val MIN(id) FROM tmp_tbl;\n" +
                "  RAISE INFO 'min_val = %, f2 = %', min_val, f2;\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;";
        assertTrue(sqlparser.parse() == 0);
        TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);
        assertTrue(sqlStatement.sqlstatementtype == ESqlStatementType.sstcreateprocedure);

        TCreateProcedureStmt createProcedure = (TCreateProcedureStmt)sqlStatement;
        assertTrue(createProcedure.getProcedureName().toString().equalsIgnoreCase("test_sp1"));
        assertTrue(createProcedure.getParameterDeclarations().size() == 2);
        TParameterDeclaration parameterDeclaration = createProcedure.getParameterDeclarations().getParameterDeclarationItem(0);
        assertTrue(parameterDeclaration.getDataType().getDataType() == EDataType.int_t);
        assertTrue(parameterDeclaration.getParameterName().toString().equalsIgnoreCase("f1"));

        assertTrue(createProcedure.getRoutineLanguage().equalsIgnoreCase("plpgsql"));

        assertTrue(createProcedure.getDeclareStatements().size() == 1);
        assertTrue (createProcedure.getDeclareStatements().get(0).sqlstatementtype==  sstplsql_vardecl);
        assertTrue(createProcedure.getBodyStatements().size() == 5);
        assertTrue(createProcedure.getBodyStatements().get(3).sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)createProcedure.getBodyStatements().get(3);
        assertTrue(selectSqlStatement.getTables().getTable(0).toString().equalsIgnoreCase("tmp_tbl"));
        TIntoClause intoClause  = selectSqlStatement.getIntoClause();
        assertTrue(intoClause.getVariableList().getObjectName(0).toString().equalsIgnoreCase("min_val"));

    }


    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "CREATE OR REPLACE PROCEDURE test_sp2(f1 IN int, f2 INOUT varchar(256), out_var OUT varchar(256))\n" +
                "AS $$\n" +
                "DECLARE\n" +
                "  loop_var int;\n" +
                "BEGIN\n" +
                "  IF f1 is null OR f2 is null THEN\n" +
                "    RAISE EXCEPTION 'input cannot be null';\n" +
                "  END IF;\n" +
                "  DROP TABLE if exists my_etl;\n" +
                "  CREATE TEMP TABLE my_etl(a int, b varchar);\n" +
                "    FOR loop_var IN 1..f1 LOOP\n" +
                "        insert into my_etl values (loop_var, f2);\n" +
                "        f2 := f2 || '+' || f2;\n" +
                "    END LOOP;\n" +
                "  SELECT count(*) INTO out_var  from my_etl;\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;";
        assertTrue(sqlparser.parse() == 0);
        TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);
        assertTrue(sqlStatement.sqlstatementtype == ESqlStatementType.sstcreateprocedure);

        TCreateProcedureStmt createProcedure = (TCreateProcedureStmt)sqlStatement;
        assertTrue(createProcedure.getProcedureName().toString().equalsIgnoreCase("test_sp2"));
        assertTrue(createProcedure.getParameterDeclarations().size() == 3);
        TParameterDeclaration parameterDeclaration = createProcedure.getParameterDeclarations().getParameterDeclarationItem(0);
        assertTrue(parameterDeclaration.getDataType().getDataType() == EDataType.int_t);
        assertTrue(parameterDeclaration.getParameterName().toString().equalsIgnoreCase("f1"));
        assertTrue(parameterDeclaration.getParameterMode() == EParameterMode.in);

        assertTrue(createProcedure.getRoutineLanguage().equalsIgnoreCase("plpgsql"));

        assertTrue(createProcedure.getDeclareStatements().size() == 1);
        assertTrue (createProcedure.getDeclareStatements().get(0).sqlstatementtype==  sstplsql_vardecl);
        assertTrue(createProcedure.getBodyStatements().size() == 5);
        assertTrue(createProcedure.getBodyStatements().get(4).sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)createProcedure.getBodyStatements().get(4);
        assertTrue(selectSqlStatement.getTables().getTable(0).toString().equalsIgnoreCase("my_etl"));

    }
}
