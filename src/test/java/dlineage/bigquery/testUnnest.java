package dlineage.bigquery;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Relationship;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.RelationshipElement;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class testUnnest  extends TestCase {
    public void test1() throws Exception {

        String sql = "create table project-dev.TEST_BACKLOG.EMPLOYEE_ADDRESS_NESTED (Emp_id INT64,Name STRING," +
                "Address ARRAY<STRUCT<State STRING, City STRING, Zipcode INT64>> );\n" +
                "INSERT INTO `Project-dev.TEST_BACKLOG.EMPLOYEE_INFO` (emp_id,name,state,city,zipcode)\n" +
                "select emp_id,name,state,city,zipcode from `Project-dev.TEST_BACKLOG.EMPLOYEE_ADDRESS_NESTED`, UNNEST(address)";
        EDbVendor vendor = TGSqlParser.getDBVendorByName("bigquery");
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(sql,vendor,true);
        dataFlowAnalyzer.generateDataFlow(true);
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(flow, vendor);

        List<Relationship> list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_INFO")
                        && r.getTarget().getColumn().equalsIgnoreCase("state")))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        RelationshipElement element = list.get(0).getSources()[0];
        assertTrue(element.getColumn().equalsIgnoreCase("address.state"));
        RelationshipElement finalElement = element;
        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase(finalElement.getParentName())
                        && r.getTarget().getColumn().equalsIgnoreCase(finalElement.getColumn())))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        element = list.get(0).getSources()[0];
        assertTrue(element.getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_ADDRESS_NESTED") && element.getColumn().equalsIgnoreCase("Address.state"));

        //
        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_INFO")
                        && r.getTarget().getColumn().equalsIgnoreCase("city")))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        element = list.get(0).getSources()[0];
        assertTrue(element.getColumn().equalsIgnoreCase("address.city"));
        RelationshipElement finalElement2 = element;
        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase(finalElement2.getParentName())
                        && r.getTarget().getColumn().equalsIgnoreCase(finalElement2.getColumn())))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        element = list.get(0).getSources()[0];
        assertTrue(element.getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_ADDRESS_NESTED") && element.getColumn().equalsIgnoreCase("Address.city"));

        //
        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_INFO")
                        && r.getTarget().getColumn().equalsIgnoreCase("Zipcode")))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        element = list.get(0).getSources()[0];
        assertTrue(element.getColumn().equalsIgnoreCase("address.Zipcode"));
        RelationshipElement finalElement3 = element;
        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase(finalElement3.getParentName())
                        && r.getTarget().getColumn().equalsIgnoreCase(finalElement3.getColumn())))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        element = list.get(0).getSources()[0];
        assertTrue(element.getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_ADDRESS_NESTED") && element.getColumn().equalsIgnoreCase("Address.Zipcode"));
    }

    public void test2() throws Exception {

        String sql = "create table project-dev.TEST_BACKLOG.EMPLOYEE_ADDRESS_NESTED (Emp_id INT64,Name STRING," +
                "Address ARRAY<STRUCT<State STRING, City STRING, Zipcode INT64>> );\n" +
                "INSERT INTO `Project-dev.TEST_BACKLOG.EMPLOYEE_INFO` (emp_id,name,state,city,zipcode)\n" +
                "select emp_id,name,\n" +
                "(select state from UNNEST(address)),\n" +
                "(select city from UNNEST(address)),\n" +
                "(select zipcode from UNNEST(address)) from `Project-dev.TEST_BACKLOG.EMPLOYEE_ADDRESS_NESTED`";
        EDbVendor vendor = TGSqlParser.getDBVendorByName("bigquery");
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(sql,vendor,true);
        dataFlowAnalyzer.generateDataFlow(true);
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(flow, vendor);

        List<Relationship> list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_INFO")
                        && r.getTarget().getColumn().equalsIgnoreCase("state")))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        RelationshipElement element = list.get(0).getSources()[0];
        assertTrue(element.getColumn().equalsIgnoreCase("address.state"));
        RelationshipElement finalElement = element;
        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase(finalElement.getParentName())
                        && r.getTarget().getColumn().equalsIgnoreCase(finalElement.getColumn())))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        element = list.get(0).getSources()[0];
        assertTrue(element.getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_ADDRESS_NESTED") && element.getColumn().equalsIgnoreCase("Address.state"));

        //
        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_INFO")
                        && r.getTarget().getColumn().equalsIgnoreCase("city")))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        element = list.get(0).getSources()[0];
        assertTrue(element.getColumn().equalsIgnoreCase("address.city"));
        RelationshipElement finalElement2 = element;
        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase(finalElement2.getParentName())
                        && r.getTarget().getColumn().equalsIgnoreCase(finalElement2.getColumn())))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        element = list.get(0).getSources()[0];
        assertTrue(element.getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_ADDRESS_NESTED") && element.getColumn().equalsIgnoreCase("Address.city"));

        //
        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_INFO")
                        && r.getTarget().getColumn().equalsIgnoreCase("Zipcode")))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        element = list.get(0).getSources()[0];
        assertTrue(element.getColumn().equalsIgnoreCase("address.Zipcode"));
        RelationshipElement finalElement3 = element;
        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase(finalElement3.getParentName())
                        && r.getTarget().getColumn().equalsIgnoreCase(finalElement3.getColumn())))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        element = list.get(0).getSources()[0];
        assertTrue(element.getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_ADDRESS_NESTED") && element.getColumn().equalsIgnoreCase("Address.Zipcode"));

    }
}
