package dlineage.solidatus;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.Option;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Relationship;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class testStarColumn extends TestCase {

    public void test1() {
        // There exists 3 create table statements
        // With one procedure which inserts all columns from orders_tmp and then one column from customers and two constants.
        String sql = "CREATE TABLE `solidatus-dev.JDBC_test.Orders_tmp`\n" +
                "(\n" +
                " ORDERNUMBER NUMERIC,\n" +
                " CUSTOMERNUMBER NUMERIC,\n" +
                " ORDERQUANTITY NUMERIC,\n" +
                " ORDERDATE DATE,\n" +
                " ORDERCOMPLETE BOOL\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE `solidatus-dev.JDBC_test.Customers`\n" +
                "(\n" +
                " CUSTOMERNUMBER NUMERIC,\n" +
                " CUSTOMERFNAME STRING,\n" +
                " CUSTOMERSNAME STRING,\n" +
                " CUSTOMERADDRESS STRING,\n" +
                " CUSTOMERDATE STRING\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE `solidatus-dev.JDBC_test.Orders_max`\n" +
                "(\n" +
                " ORDERNUMBER NUMERIC,\n" +
                " CUSTOMERNUMBER NUMERIC,\n" +
                " ORDERQUANTITY NUMERIC,\n" +
                " ORDERDATE DATE,\n" +
                " ORDERCOMPLETE BOOL,\n" +
                " CUSTNAME STRING,\n" +
                " O_DATE DATE,\n" +
                " O_VAR STRING\n" +
                ");\n" +
                "\n" +
                "\n" +
                "\n" +
                "CREATE PROCEDURE `solidatus-dev`.JDBC_test.insertIntoSelectStar()\n" +
                "BEGIN\n" +
                " INSERT INTO JDBC_test.Orders_max\n" +
                " SELECT o.*, c.CUSTOMERFNAME, CURRENT_DATE(), 'V1'\n" +
                " FROM JDBC_test.Orders_tmp o\n" +
                " INNER JOIN JDBC_test.Customers c on c.CUSTOMERNUMBER = o.CUSTOMERNUMBER;\n" +
                "END;";

       // System.out.println(sql);

        EDbVendor vendor = TGSqlParser.getDBVendorByName("bigquery");
        Option option = new Option();
        option.setVendor(vendor);
        option.setSimpleOutput(true);
        option.setShowCallRelation(true);
        option.setShowConstantTable(true);
        option.setDefaultServer("solidatus-dev");
        option.setDefaultSchema("JDBC_test");

        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(sql,
                option);
        dataFlowAnalyzer.setShowConstantTable(true);
        dataFlowAnalyzer.setShowCallRelation(true);


        dataFlowAnalyzer.generateDataFlow();
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(vendor, flow, false);

        List<Relationship> customerfnameRels = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> Arrays.stream(r.getSources()).anyMatch(s -> s.getColumn().equalsIgnoreCase("CUSTOMERFNAME")))
                .collect(Collectors.toList());

        // There exists one relationship which has source customerfname
        // assertThat(customerfnameRels).hasSize(1);
        assertTrue(customerfnameRels.size() == 1);
//
//        // There should exist a relationship mapping from CUSTOMER.CUSTOMERFNAME to ORDERS_MAX.CUSTNAME
        Relationship customerNameRel = customerfnameRels.get(0);
        assertTrue(customerNameRel.getTarget().getParentName().toString().equalsIgnoreCase("solidatus-dev.JDBC_test.Orders_max"));
        assertTrue(customerNameRel.getTarget().getColumn().toString().equalsIgnoreCase("CUSTNAME"));

//        // There should exist a relationship mapping from CURRENT_DATE() to ORDERS_MAX.O_DATE
        List<Relationship> constantDates= Arrays.stream(dataFlow.getRelationships()).filter(r -> Arrays.stream(r.getSources()).anyMatch(s -> s.getColumn().equalsIgnoreCase("CURRENT_DATE()"))).collect(Collectors.toList());
        assertTrue(constantDates.size() == 1);
        Relationship constantDate = constantDates.get(0);
        assertTrue(constantDate.getTarget().getColumn().toString().equalsIgnoreCase("O_DATE"));
        assertTrue(constantDate.getTarget().getParentName().toString().equalsIgnoreCase("solidatus-dev.JDBC_test.Orders_max"));

        // There should exist a relationship mapping from "V1" to ORDERS_MAX.O_VAR
        List<Relationship> constantVars= Arrays.stream(dataFlow.getRelationships()).filter(r -> Arrays.stream(r.getSources()).anyMatch(s -> s.getColumn().equalsIgnoreCase("'V1'"))).collect(Collectors.toList());
        assertTrue(constantVars.size() == 1);
        Relationship constantVar = constantVars.get(0);

        assertTrue(constantVar.getTarget().getColumn().toString().equalsIgnoreCase("O_VAR"));
        assertTrue(constantVar.getTarget().getParentName().toString().equalsIgnoreCase("solidatus-dev.JDBC_test.Orders_max"));
    }
}
