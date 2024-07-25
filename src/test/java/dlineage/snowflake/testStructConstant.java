package dlineage.snowflake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.Option;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.RelationshipElement;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.stmt.TCreateProcedureStmt;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;

import junit.framework.TestCase;

public class testStructConstant  extends TestCase {
	
	public void test999_1() throws Exception {

        String sql = "CREATE OR REPLACE PROCEDURE COVID19_PROCESSED.PUBLIC.INSERT_NYT_US_COVID19()\n" +
                "            RETURNS VARCHAR(16777216)\n" +
                "            LANGUAGE SQL\n" +
                "            EXECUTE AS OWNER\n" +
                "            AS '\n" +
                "            BEGIN\n" +
                "                INSERT INTO COVID19_PROCESSED.PUBLIC.NYT_US_COVID19\n" +
                "                SELECT *\n" +
                "                FROM COVID19_STAGE.PUBLIC.NYT_US_COVID19\n" +
                "                WHERE CASES_SINCE_PREV_DAY > 0;\n" +
                "            END;\n" +
                "            ';";

        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlParser.sqltext = sql;
        sqlParser.parse();
        TCreateProcedureStmt createProcedureStmt = (TCreateProcedureStmt) sqlParser.sqlstatements.get(0);
        assertTrue(createProcedureStmt.asCanonical().equalsIgnoreCase("CREATE OR REPLACE PROCEDURE COVID19_PROCESSED.PUBLIC.INSERT_NYT_US_COVID19()\n" +
                "            RETURNS VARCHAR(16777216)\n" +
                "            LANGUAGE SQL\n" +
                "            EXECUTE AS OWNER\n" +
                "            AS '\n" +
                "            BEGIN\n" +
                "                INSERT INTO COVID19_PROCESSED.PUBLIC.NYT_US_COVID19\n" +
                "                SELECT *\n" +
                "                FROM COVID19_STAGE.PUBLIC.NYT_US_COVID19\n" +
                "                WHERE CASES_SINCE_PREV_DAY > 999;\n" +
                "            END;\n" +
                "            '"));

        assertTrue(createProcedureStmt.toString().equalsIgnoreCase("CREATE OR REPLACE PROCEDURE COVID19_PROCESSED.PUBLIC.INSERT_NYT_US_COVID19()\n" +
                "            RETURNS VARCHAR(16777216)\n" +
                "            LANGUAGE SQL\n" +
                "            EXECUTE AS OWNER\n" +
                "            AS '\n" +
                "            BEGIN\n" +
                "                INSERT INTO COVID19_PROCESSED.PUBLIC.NYT_US_COVID19\n" +
                "                SELECT *\n" +
                "                FROM COVID19_STAGE.PUBLIC.NYT_US_COVID19\n" +
                "                WHERE CASES_SINCE_PREV_DAY > 0;\n" +
                "            END;\n" +
                "            '"));

        TInsertSqlStatement insertSqlStatement = (TInsertSqlStatement) createProcedureStmt.getBodyStatements().get(0);
        assertTrue(insertSqlStatement.asCanonical().equalsIgnoreCase("INSERT INTO COVID19_PROCESSED.PUBLIC.NYT_US_COVID19\n" +
                "                SELECT *\n" +
                "                FROM COVID19_STAGE.PUBLIC.NYT_US_COVID19\n" +
                "                WHERE CASES_SINCE_PREV_DAY > 999"));
        assertTrue (insertSqlStatement.toString().equalsIgnoreCase("INSERT INTO COVID19_PROCESSED.PUBLIC.NYT_US_COVID19\n" +
                "                SELECT *\n" +
                "                FROM COVID19_STAGE.PUBLIC.NYT_US_COVID19\n" +
                "                WHERE CASES_SINCE_PREV_DAY > 0"));

    }


    public void test999() throws Exception {

        String sql = "CREATE OR REPLACE PROCEDURE COVID19_PROCESSED.PUBLIC.INSERT_NYT_US_COVID19()\n" +
                "            RETURNS VARCHAR(16777216)\n" +
                "            LANGUAGE SQL\n" +
                "            EXECUTE AS OWNER\n" +
                "            AS '\n" +
                "            BEGIN\n" +
                "                INSERT INTO COVID19_PROCESSED.PUBLIC.NYT_US_COVID19\n" +
                "                SELECT *\n" +
                "                FROM COVID19_STAGE.PUBLIC.NYT_US_COVID19\n" +
                "                WHERE CASES_SINCE_PREV_DAY > 0;\n" +
                "            END;\n" +
                "            ';";

       // System.out.println(sql);

        Option option = new Option();
        option.setVendor(EDbVendor.dbvsnowflake);
        option.setSimpleOutput(false);
        option.setIgnoreRecordSet(false);
        option.setShowConstantTable(true);
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(sql, option);

        dataFlowAnalyzer.generateDataFlow();
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(EDbVendor.dbvsnowflake, flow, false);

        assertEquals(6, dataFlow.getRelationships().length);
        List<String> sourceColumnNames = new ArrayList<>();
        for(int i=0; i<dataFlow.getRelationships().length; i++){
            sourceColumnNames.addAll(Arrays.stream(dataFlow.getRelationships()[i].getSources()).map(RelationshipElement::getColumn).collect(Collectors.toList()));
        }
        assertTrue(!sourceColumnNames.contains("999"));
        assertTrue(sourceColumnNames.contains("0"));
    }

}

