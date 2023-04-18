package bigquery;

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.stmt.TCallStatement;
import gudusoft.gsqlparser.stmt.TCreateFunctionStmt;
import gudusoft.gsqlparser.stmt.TCreateProcedureStmt;
import junit.framework.TestCase;

public class testCreateProcedure extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);

        sqlparser.sqltext = "CREATE PROCEDURE `#batch_project_id.subscriber_dtl.prc_subscriber_history`(\n" +
                "  target_date DATE, OUT rows_added INT64)\n" +
                "BEGIN\n" +
                "  CREATE TEMP TABLE DataForTargetDate AS\n" +
                "  SELECT t1.id, t1.x, t2.y\n" +
                "  FROM dataset.partitioned_table1 AS t1\n" +
                "  JOIN dataset.partitioned_table2 AS t2\n" +
                "  ON t1.id = t2.id\n" +
                "  WHERE t1.date = target_date\n" +
                "    AND t2.date = target_date;\n" +
                "\n" +
                "  SET rows_added = (SELECT COUNT(*) FROM DataForTargetDate);\n" +
                "\n" +
                "  SELECT id, x, y, target_date  -- note that target_date is a parameter\n" +
                "  FROM DataForTargetDate;\n" +
                "\n" +
                "  DROP TABLE DataForTargetDate;\n" +
                "END;";

        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreateprocedure);
        TCreateProcedureStmt createprocedure = (TCreateProcedureStmt) sqlparser.sqlstatements.get(0);

        TObjectName procedureName = createprocedure.getProcedureName();
        assertTrue(procedureName.toString().equalsIgnoreCase("`#batch_project_id`.`subscriber_dtl`.`prc_subscriber_history`"));
        assertTrue(procedureName.getDatabaseString().equalsIgnoreCase("`#batch_project_id`"));
        assertTrue(procedureName.getSchemaString().equalsIgnoreCase("`subscriber_dtl`"));
        assertTrue(procedureName.getObjectString().equalsIgnoreCase("`prc_subscriber_history`"));
        assertTrue(createprocedure.getParameterDeclarations().getParameterDeclarationItem(1).getParameterName().toString().equalsIgnoreCase("rows_added"));
        assertTrue(createprocedure.getParameterDeclarations().getParameterDeclarationItem(1).getDataType().getDataType() == EDataType.int64_t);
        assertTrue(createprocedure.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstcreatetable);
        assertTrue(createprocedure.getBodyStatements().get(1).sqlstatementtype == ESqlStatementType.sstset);
        assertTrue(createprocedure.getBodyStatements().get(2).sqlstatementtype == ESqlStatementType.sstselect);
        assertTrue(createprocedure.getBodyStatements().get(3).sqlstatementtype == ESqlStatementType.sstdroptable);

    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);

        sqlparser.sqltext = "create procedure `sqlflow-connector`.`underscore_dataset`.`testProc3`( id INT64,OUT newId INT64) BEGIN\n" +
                "  DECLARE oldId INT64 DEFAULT id;\n" +
                "  set newId = null;\n" +
                "  WHILE newId IS NOT NULL DO\n" +
                "    SET newId = oldId + 1;\n" +
                "  END WHILE;\n" +
                " SET newId = (SELECT id FROM underscore_dataset.table1);\n" +
                " call underscore_dataset.testProc1(1,newId);\n" +
                "END";

        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreateprocedure);
        TCreateProcedureStmt createprocedure = (TCreateProcedureStmt) sqlparser.sqlstatements.get(0);

        TObjectName procedureName = createprocedure.getProcedureName();
        assertTrue(procedureName.toString().equalsIgnoreCase("`sqlflow-connector`.`underscore_dataset`.`testProc3`"));
        assertTrue(procedureName.getDatabaseString().equalsIgnoreCase("`sqlflow-connector`"));
        assertTrue(procedureName.getSchemaString().equalsIgnoreCase("`underscore_dataset`"));
        assertTrue(procedureName.getObjectString().equalsIgnoreCase("`testProc3`"));
        assertTrue(createprocedure.getParameterDeclarations().getParameterDeclarationItem(1).getParameterName().toString().equalsIgnoreCase("newId"));
        assertTrue(createprocedure.getParameterDeclarations().getParameterDeclarationItem(1).getDataType().getDataType() == EDataType.int64_t);
        assertTrue(createprocedure.getBodyStatements().get(4).sqlstatementtype == ESqlStatementType.sstcall);
        TCallStatement callStatement = (TCallStatement)createprocedure.getBodyStatements().get(4);
        assertTrue(callStatement.getRoutineName().toString().equalsIgnoreCase("underscore_dataset.testProc1"));
//        assertTrue(createprocedure.getBodyStatements().get(1).sqlstatementtype == ESqlStatementType.sstset);
//        assertTrue(createprocedure.getBodyStatements().get(2).sqlstatementtype == ESqlStatementType.sstselect);
//        assertTrue(createprocedure.getBodyStatements().get(3).sqlstatementtype == ESqlStatementType.sstdroptable);

    }
}
