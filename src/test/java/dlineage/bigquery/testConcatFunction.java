package dlineage.bigquery;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.Option;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Relationship;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.RelationshipElement;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import junit.framework.TestCase;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class testConcatFunction   extends TestCase {
    public void test1() throws Exception {
        String sql = "create table `project-dev.dataset-dev.ALL_NAMES`\n" +
                "(\n" +
                "    ID INT64,\n" +
                "    First_Name STRING,\n" +
                "    Last_Name STRING,\n" +
                "    Full_Name STRING\n" +
                ");\n" +
                "\n" +
                "create table `project-dev.dataset-dev.NAME_LIST_1`\n" +
                "(\n" +
                "    ID INT64,\n" +
                "    First_Name STRING,\n" +
                "    Last_Name STRING\n" +
                ");\n" +
                "\n" +
                "create table `project-dev.dataset-dev.NAME_LIST_2`\n" +
                "(\n" +
                "    ID INT64,\n" +
                "    First_Name STRING,\n" +
                "    Last_Name STRING\n" +
                ");\n" +
                "\n" +
                "INSERT INTO `project-dev.dataset-dev.ALL_NAMES`\n" +
                "With Full_List AS (\n" +
                "SELECT * FROM `dataset-dev.NAME_LIST_1`\n" +
                "UNION ALL\n" +
                "SELECT * FROM `dataset-dev.NAME_LIST_2`\n" +
                ")\n" +
                "SELECT\n" +
                " *, CONCAT(First_Name, Last_Name) AS Full_Name\n" +
                "FROM Full_List;";
        EDbVendor vendor = TGSqlParser.getDBVendorByName("bigquery");
        Option option = new Option();
        option.setVendor(vendor);
        option.setSimpleOutput(true);
        option.setSimpleShowFunction(true);
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(sql,option);
        dataFlowAnalyzer.generateDataFlow(true);
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(flow, vendor);

        List<Relationship> list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase("CONCAT")
                        && r.getTarget().getColumn().equalsIgnoreCase("CONCAT")
                        && Arrays.stream(r.getSources()).anyMatch(s -> s.getParentName().contains("project-dev.dataset-dev.NAME_LIST_1") && s.getColumn().equalsIgnoreCase("First_Name"))))
                .collect(Collectors.toList());
        assertTrue(list.size()==1);

        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase("CONCAT")
                        && r.getTarget().getColumn().equalsIgnoreCase("CONCAT")
                        && Arrays.stream(r.getSources()).anyMatch(s -> s.getParentName().contains("project-dev.dataset-dev.NAME_LIST_2") && s.getColumn().equalsIgnoreCase("First_Name"))))
                .collect(Collectors.toList());
        assertTrue(list.size()==1);

        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase("CONCAT")
                        && r.getTarget().getColumn().equalsIgnoreCase("CONCAT")
                        && Arrays.stream(r.getSources()).anyMatch(s -> s.getParentName().contains("project-dev.dataset-dev.NAME_LIST_1") && s.getColumn().equalsIgnoreCase("Last_Name"))))
                .collect(Collectors.toList());
        assertTrue(list.size()==1);

        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase("CONCAT")
                        && r.getTarget().getColumn().equalsIgnoreCase("CONCAT")
                        && Arrays.stream(r.getSources()).anyMatch(s -> s.getParentName().contains("project-dev.dataset-dev.NAME_LIST_2") && s.getColumn().equalsIgnoreCase("Last_Name"))))
                .collect(Collectors.toList());
        assertTrue(list.size()==1);

    }
}
