package dlineage.bigquery;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.Option;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import junit.framework.TestCase;
import java.util.Arrays;
import java.util.stream.Collectors;

public class testReplace extends TestCase {
    public void test1() throws Exception {

        String sql = "WITH orders AS\n" +
                "  (SELECT 5 as order_id,\n" +
                "  \"sprocket\" as item_name,\n" +
                "  200 as quantity)\n" +
                "SELECT * REPLACE (\"widget\" AS item_name)\n" +
                "FROM orders;";
        EDbVendor vendor = TGSqlParser.getDBVendorByName("bigquery");
        Option option = new Option();
        option.setVendor(vendor);
        option.setSimpleShowFunction(true);
        option.setShowConstantTable(true);
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(sql,option);
        dataFlowAnalyzer.generateDataFlow(true);
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(flow, vendor);
        assertTrue(Arrays.stream(dataFlow.getRelationships()).anyMatch(r -> (r.getTarget().getColumn().equalsIgnoreCase("item_name")
                && (r.getSources().length==1 && r.getSources()[0].getColumn().contains("widget")))));

    }

    public void test2() throws Exception {

        String sql = "WITH orders AS\n" +
                "  (SELECT 5 as order_id,\n" +
                "  \"sprocket\" as item_name,\n" +
                "  200 as quantity)\n" +
                "SELECT * REPLACE (quantity/2 AS quantity)\n" +
                "FROM orders;";
        EDbVendor vendor = TGSqlParser.getDBVendorByName("bigquery");
        Option option = new Option();
        option.setVendor(vendor);
        option.setSimpleShowFunction(true);
        option.setShowConstantTable(true);
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(sql,option);
        dataFlowAnalyzer.generateDataFlow(true);
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(flow, vendor);
        assertTrue(Arrays.stream(dataFlow.getRelationships()).anyMatch(r -> (r.getTarget().getColumn().equalsIgnoreCase("quantity")
                && (r.getSources().length==1 && (Arrays.stream(r.getSources()).anyMatch(s -> s.getColumn().equalsIgnoreCase("2")))))));

    }

    public void test3() throws Exception {

        String sql = "create table `project-dev`.TEST_BACKLOG.EMPLOYEE_MASTER\n" +
                "(\n" +
                "    emp_id   INT64,\n" +
                "    name     STRING,\n" +
                "    address  STRING,\n" +
                "    state    STRING,\n" +
                "    city     STRING,\n" +
                "    zipcode  INT64,\n" +
                "    dept_id  INT64,\n" +
                "    state_cd INT64\n" +
                ");\n" +
                "\n" +
                "create table `project-dev`.TEST_BACKLOG.EMPLOYEE_MASTER2\n" +
                "(\n" +
                "    emp_id   INT64,\n" +
                "    name     STRING,\n" +
                "    address  STRING,\n" +
                "    state    STRING,\n" +
                "    city     STRING,\n" +
                "    zipcode  INT64,\n" +
                "    dept_id  INT64,\n" +
                "    state_cd INT64\n" +
                ");\n" +
                "\n" +
                "insert into `project-dev.TEST_BACKLOG.EMPLOYEE_MASTER`\n" +
                "select * replace(zipcode as state_cd)\n" +
                "from `TEST_BACKLOG.EMPLOYEE_MASTER2`";
        EDbVendor vendor = TGSqlParser.getDBVendorByName("bigquery");
        Option option = new Option();
        option.setVendor(vendor);
        option.setSimpleShowFunction(true);
        option.setShowConstantTable(true);
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(sql,option);
        dataFlowAnalyzer.generateDataFlow(true);
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(flow, vendor);
        assertTrue(Arrays.stream(dataFlow.getRelationships()).anyMatch(r -> (r.getTarget().getParentName().contains("INSERT-SELECT")
                && r.getTarget().getColumn().equalsIgnoreCase("state_cd")
                && (r.getSources().length==1 && r.getSources()[0].getColumn().equalsIgnoreCase("zipcode")
                && r.getSources()[0].getParentName().equalsIgnoreCase("`project-dev`.TEST_BACKLOG.EMPLOYEE_MASTER2")))));

    }
}
