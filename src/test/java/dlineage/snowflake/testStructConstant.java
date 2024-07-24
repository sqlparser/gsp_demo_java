package dlineage.snowflake;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.Option;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.RelationshipElement;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import junit.framework.TestCase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class testStructConstant  extends TestCase {
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

