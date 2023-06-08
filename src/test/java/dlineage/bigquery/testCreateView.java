package dlineage.bigquery;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.Option;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.util.json.JSON;
import junit.framework.TestCase;

import java.util.Arrays;

public class testCreateView  extends TestCase {
    public void test1() throws Exception {

        String sql = "CREATE TABLE `dev.JDBC_test.Customers`\n" +
                "(\n" +
                "    CUSTOMERNUMBER NUMERIC,\n" +
                "    CUSTOMERFNAME STRING,\n" +
                "    CUSTOMERSNAME STRING\n" +
                ");\n" +
                "\n" +
                "CREATE VIEW dev.JDBC_test.selectStarFile as\n" +
                "SELECT *\n" +
                "FROM `dev.JDBC_test.Customers`;";
        EDbVendor vendor = TGSqlParser.getDBVendorByName("bigquery");
        Option option = new Option();
        option.setVendor(vendor);
        option.setSimpleOutput(true);
        option.setSimpleShowFunction(true);
        option.setShowConstantTable(true);
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(sql,option);
        dataFlowAnalyzer.generateDataFlow(true);
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(flow, vendor);
        String json = JSON.toJSONString(dataFlow);
        assertTrue(Arrays.stream(dataFlow.getRelationships()).anyMatch(r -> (r.getTarget().getParentName().contains("dev.JDBC_test.selectStarFile")
                && r.getTarget().getColumn().equalsIgnoreCase("CUSTOMERNUMBER")
                && (r.getSources().length==1 && r.getSources()[0].getColumn().equalsIgnoreCase("CUSTOMERNUMBER")
                && r.getSources()[0].getParentName().equalsIgnoreCase("dev.JDBC_test.Customers")))));
        assertTrue(Arrays.stream(dataFlow.getRelationships()).anyMatch(r -> (r.getTarget().getParentName().contains("dev.JDBC_test.selectStarFile")
                && r.getTarget().getColumn().equalsIgnoreCase("CUSTOMERFNAME")
                && (r.getSources().length==1 && r.getSources()[0].getColumn().equalsIgnoreCase("CUSTOMERFNAME")
                && r.getSources()[0].getParentName().equalsIgnoreCase("dev.JDBC_test.Customers")))));
        assertTrue(Arrays.stream(dataFlow.getRelationships()).anyMatch(r -> (r.getTarget().getParentName().contains("dev.JDBC_test.selectStarFile")
                && r.getTarget().getColumn().equalsIgnoreCase("CUSTOMERSNAME")
                && (r.getSources().length==1 && r.getSources()[0].getColumn().equalsIgnoreCase("CUSTOMERSNAME")
                && r.getSources()[0].getParentName().equalsIgnoreCase("dev.JDBC_test.Customers")))));

    }
}
