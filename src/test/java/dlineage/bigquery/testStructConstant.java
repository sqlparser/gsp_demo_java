package dlineage.bigquery;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.Option;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Relationship;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.RelationshipElement;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.util.json.JSON;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class testStructConstant extends TestCase {
    public void test1() throws Exception {

        String sql = "CREATE TABLE\n" +
                "    `project-dev`.DATASET.SOURCE (\n" +
                "    COL1 STRING,\n" +
                "    COL2 STRING\n" +
                "    );\n" +
                "\n" +
                "CREATE TABLE\n" +
                "    `project-dev`.DATASET.TARGET (\n" +
                "    COL1 STRING,\n" +
                "    COL2 STRING,\n" +
                "    __metadata ARRAY<STRUCT<attribute STRING,\n" +
                "    data_type STRING,\n" +
                "    value STRING>>\n" +
                "    );\n" +
                "INSERT INTO\n" +
                "    `project-dev.DATASET.TARGET`\n" +
                "SELECT\n" +
                "    *,\n" +
                "    [STRUCT(\"creation_date\" AS attribute, \"timestamp\" AS data_type, \"2022-06-06 18:20:06\" AS value),\n" +
                "    STRUCT(\"file_sequence_number_string\" AS attribute, \"string\" AS data_type, \"[000000500]\" AS value) ] AS __metadata\n" +
                "FROM\n" +
                "    `project-dev.DATASET.SOURCE`;";

        EDbVendor vendor = TGSqlParser.getDBVendorByName("bigquery");
        Option option = new Option();
        option.setVendor(vendor);
        option.setSimpleOutput(false);
        option.setIgnoreRecordSet(true);
        option.setShowConstantTable(true);
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(sql, option);

        dataFlowAnalyzer.generateDataFlow();
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(vendor, flow, false);

        assertEquals(5, dataFlow.getRelationships().length);
        List<String> sourceColumnNames = new ArrayList<>();
        sourceColumnNames.addAll(Arrays.stream(dataFlow.getRelationships()[2].getSources()).map(RelationshipElement::getColumn).collect(Collectors.toList()));
        sourceColumnNames.addAll(Arrays.stream(dataFlow.getRelationships()[3].getSources()).map(RelationshipElement::getColumn).collect(Collectors.toList()));
        sourceColumnNames.addAll(Arrays.stream(dataFlow.getRelationships()[4].getSources()).map(RelationshipElement::getColumn).collect(Collectors.toList()));

        assertTrue(sourceColumnNames.contains("\"file_sequence_number_string\""));
        assertTrue(sourceColumnNames.contains("\"string\""));
        assertTrue(sourceColumnNames.contains("\"[000000500]\""));
        assertTrue(sourceColumnNames.contains("\"creation_date\""));
        assertTrue(sourceColumnNames.contains("\"timestamp\""));
        assertTrue(sourceColumnNames.contains("\"2022-06-06 18:20:06\""));

    }
}
