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

public class testStruct  extends TestCase {
    public void test1() throws Exception {

        String sql = "CREATE TABLE `solidatus-dev.JDBC_test.EMPLOYEE_ADDRESS`\n" +
                "(\n" +
                "  Emp_id STRING,\n" +
                "  Name STRING,\n" +
                "  Address STRUCT<Zipcode STRING, State STRING, City STRING>,\n" +
                "  Dt_of_birth STRING,\n" +
                "  Salary STRING,\n" +
                "  st_date STRING,\n" +
                "  amount STRING\n" +
                ");\n" +
                "\n" +
                "INSERT INTO `solidatus-dev.JDBC_test.EMPLOYEE_INFO` (emp_id,name,state,city,zipcode)\n" +
                "select emp_id,name,state,city,zipcode from `solidatus-dev.JDBC_test.EMPLOYEE_ADDRESS`, UNNEST(address);";

        EDbVendor vendor = TGSqlParser.getDBVendorByName("bigquery");
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(sql,vendor,true);
        dataFlowAnalyzer.generateDataFlow(true);
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(flow, vendor);

        List<Relationship> list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase("solidatus-dev.JDBC_test.EMPLOYEE_INFO")
                        && r.getTarget().getColumn().equalsIgnoreCase("state")))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        RelationshipElement element = list.get(0).getSources()[0];
        assertTrue(element.getParentName().equalsIgnoreCase("solidatus-dev.JDBC_test.EMPLOYEE_ADDRESS") && element.getColumn().equalsIgnoreCase("Address.state"));

        //
        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase("solidatus-dev.JDBC_test.EMPLOYEE_INFO")
                        && r.getTarget().getColumn().equalsIgnoreCase("zipcode")))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        element = list.get(0).getSources()[0];
        assertTrue(element.getParentName().equalsIgnoreCase("solidatus-dev.JDBC_test.EMPLOYEE_ADDRESS") && element.getColumn().equalsIgnoreCase("Address.zipcode"));

        //
        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase("solidatus-dev.JDBC_test.EMPLOYEE_INFO")
                        && r.getTarget().getColumn().equalsIgnoreCase("city")))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        element = list.get(0).getSources()[0];
        assertTrue(element.getParentName().equalsIgnoreCase("solidatus-dev.JDBC_test.EMPLOYEE_ADDRESS") && element.getColumn().equalsIgnoreCase("Address.city"));

    }
}

