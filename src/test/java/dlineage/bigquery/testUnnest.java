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

import javax.xml.bind.util.JAXBSource;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class testUnnest  extends TestCase {
    public void test1() throws Exception {

        String sql = "create table project-dev.TEST_BACKLOG.EMPLOYEE_ADDRESS_NESTED (Emp_id INT64,Name STRING," +
                "Address ARRAY<STRUCT<State STRING, City STRING, Zipcode INT64>> );\n" +
                "INSERT INTO `Project-dev.TEST_BACKLOG.EMPLOYEE_INFO` (emp_id,name,state,city,zipcode)\n" +
                "select emp_id,name,state,city,zipcode from `Project-dev.TEST_BACKLOG.EMPLOYEE_ADDRESS_NESTED`, UNNEST(address)";
        //System.out.println(sql);
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
        assertTrue(element.getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_ADDRESS_NESTED") && element.getColumn().equalsIgnoreCase("Address.array.state"));

        //
        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_INFO")
                        && r.getTarget().getColumn().equalsIgnoreCase("city")))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        element = list.get(0).getSources()[0];
        assertTrue(element.getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_ADDRESS_NESTED") && element.getColumn().equalsIgnoreCase("Address.array.city"));

        //
        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_INFO")
                        && r.getTarget().getColumn().equalsIgnoreCase("Zipcode")))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        element = list.get(0).getSources()[0];
        assertTrue(element.getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_ADDRESS_NESTED") && element.getColumn().equalsIgnoreCase("Address.array.Zipcode"));
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
        assertTrue(element.getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_ADDRESS_NESTED") && element.getColumn().equalsIgnoreCase("Address.array.state"));

        //
        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_INFO")
                        && r.getTarget().getColumn().equalsIgnoreCase("city")))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        element = list.get(0).getSources()[0];
        assertTrue(element.getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_ADDRESS_NESTED") && element.getColumn().equalsIgnoreCase("Address.array.city"));

        //
        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_INFO")
                        && r.getTarget().getColumn().equalsIgnoreCase("Zipcode")))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        element = list.get(0).getSources()[0];
        assertTrue(element.getParentName().equalsIgnoreCase("Project-dev.TEST_BACKLOG.EMPLOYEE_ADDRESS_NESTED") && element.getColumn().equalsIgnoreCase("Address.array.Zipcode"));

    }

    public void test3() throws Exception {
        String sql = "create table solidatus-dev.JDBC_TEST2.INFO (Emp_id INT64,Name STRING,Address STRING,State STRING,City STRING,Zipcode INT64,Dept_id INT64,DOB DATE,Salary INT64,start_date STRING,amount STRING,zip INT64 );\n" +
                "create table solidatus-dev.JDBC_TEST2.ADDRESS_NESTED (Emp_id INT64,Name STRING,Address ARRAY<STRUCT<State STRING, City STRING, Zipcode INT64>> );\n" +
                "\n" +
                "INSERT INTO `solidatus-dev.JDBC_TEST2.INFO` (emp_id,name,state,city,zipcode)\n" +
                "select emp_id,name,\n" +
                "       (select state from UNNEST(address)),\n" +
                "       (select city from UNNEST(address)),\n" +
                "       (select zipcode from UNNEST(address)) from `solidatus-dev.JDBC_TEST2.ADDRESS_NESTED`";
        EDbVendor vendor = TGSqlParser.getDBVendorByName("bigquery");
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(sql,vendor,true);
        dataFlowAnalyzer.generateDataFlow(true);
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(flow, vendor);

        List<Relationship> list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase("solidatus-dev.JDBC_TEST2.INFO")
                        && r.getTarget().getColumn().equalsIgnoreCase("state")))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        RelationshipElement element = list.get(0).getSources()[0];
        assertTrue(element.getParentName().equalsIgnoreCase("solidatus-dev.JDBC_TEST2.ADDRESS_NESTED") && element.getColumn().equalsIgnoreCase("Address.array.state"));

        //
        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase("solidatus-dev.JDBC_TEST2.INFO")
                        && r.getTarget().getColumn().equalsIgnoreCase("city")))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        element = list.get(0).getSources()[0];
        assertTrue(element.getParentName().equalsIgnoreCase("solidatus-dev.JDBC_TEST2.ADDRESS_NESTED") && element.getColumn().equalsIgnoreCase("Address.array.city"));

        //
        list = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> (r.getTarget().getParentName().equalsIgnoreCase("solidatus-dev.JDBC_TEST2.INFO")
                        && r.getTarget().getColumn().equalsIgnoreCase("Zipcode")))
                .collect(Collectors.toList());
        assertTrue(list.size()==1 && list.get(0).getSources().length==1);
        element = list.get(0).getSources()[0];
        assertTrue(element.getParentName().equalsIgnoreCase("solidatus-dev.JDBC_TEST2.ADDRESS_NESTED") && element.getColumn().equalsIgnoreCase("Address.array.Zipcode"));

    }
}