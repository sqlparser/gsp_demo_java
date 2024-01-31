package dlineage.bigquery;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.Option;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Relationship;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.dlineage.util.XML2Model;
import junit.framework.TestCase;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class testBigQuery extends TestCase {
    public void  test1(){
        //fix bugs: https://wangz.net/bugs/mantisbt/view.php?id=2534
        File file = new File(common.gspCommon.BASE_SQL_DIR_PRIVATE +"dataflow/bigquery/I61VO9.sql");

        EDbVendor vendor = TGSqlParser.getDBVendorByName("bigquery");
        Option option = new Option();
        option.setVendor(vendor);
        option.setSimpleOutput(true);
        option.setShowConstantTable(true);

        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(file, option);

        dataFlowAnalyzer.generateDataFlow();
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(vendor, flow, false);

        assertTrue(Objects.isNull(dataFlow.getErrors()) || dataFlow.getErrors().length == 0);

        Relationship SQL_CONSTANTS = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> Arrays.stream(r.getSources()).anyMatch(s -> s.getParentName().contains("SQL_CONSTANTS")))
                .collect(Collectors.toList()).get(0);
        assertTrue(Arrays.stream(SQL_CONSTANTS.getSources()).filter(s -> s.getParentName().contains("SQL_CONSTANTS")).findFirst().get().getColumn().equalsIgnoreCase("[]"));
    }

    public void test2() throws Exception {

        String sql = "CREATE TABLE `proj.JDBC_test.Customers`\n" +
                "(\n" +
                " CUSTOMERNUMBER NUMERIC,\n" +
                " CUSTOMERFNAME STRING,\n" +
                " V1 FLOAT64,\n" +
                " V2 FLOAT64\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE `proj.JDBC_test.Orders`\n" +
                "(\n" +
                " CUSTOMERNUMBER NUMERIC,\n" +
                " ORDERNAME STRING,\n" +
                " ORDERNUM FLOAT64,\n" +
                " ORDERALTNUM FLOAT64\n" +
                ");\n" +
                "\n" +
                "INSERT INTO `proj.JDBC_test.Customers` WITH O as\n" +
                " (SELECT CUSTOMERNUMBER,\n" +
                " ORDERNAME,\n" +
                " CASE\n" +
                " WHEN ORDERNAME IN ('A', 'B')\n" +
                " THEN ORDERNUM\n" +
                " ELSE ORDERALTNUM\n" +
                " END\n" +
                " AS ORDERNUM,\n" +
                " CASE\n" +
                " WHEN ORDERNAME IN ('A', 'B')\n" +
                " THEN ORDERALTNUM\n" +
                " ELSE ORDERNUM\n" +
                " END\n" +
                " AS ORDERALTNUM\n" +
                " FROM `proj.JDBC_test.Orders` as O)\n" +
                "select * FROM O;";
        EDbVendor vendor = TGSqlParser.getDBVendorByName("bigquery");
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(sql,vendor,true);
        dataFlowAnalyzer.generateDataFlow(true);
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        flow.setRelationships(flow.getRelationships().stream().filter(t->"fdd".equals(t.getType())).collect(Collectors.toList()));
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(flow, vendor);

        assertTrue(dataFlow.getRelationships().length ==4);

        Relationship v1Col = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> r.getTarget().getColumn().contains("V1"))
                .collect(Collectors.toList())
                .get(0);
        assertTrue(v1Col.getSources().length==2);

        assertTrue(v1Col.getSources()[0].getColumn().equalsIgnoreCase("ORDERALTNUM"));
        assertTrue(v1Col.getSources()[1].getColumn().contains("ORDERNUM"));

        Relationship v2Col = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> r.getTarget().getColumn().contains("V2"))
                .collect(Collectors.toList())
                .get(0);

        assertTrue(v2Col.getSources().length==2);
        assertTrue(v2Col.getSources()[0].getColumn().equalsIgnoreCase("ORDERNUM"));
        assertTrue(v2Col.getSources()[1].getColumn().contains("ORDERALTNUM"));
    }

    public void  test3(){
        //fix bugs: https://e.gitee.com/gudusoft/notifications/referer?issue=I61W7N
        File file = new File(common.gspCommon.BASE_SQL_DIR_PRIVATE +"dataflow/bigquery/I61W7N.sql");

        EDbVendor vendor = TGSqlParser.getDBVendorByName("bigquery");
        Option option = new Option();
        option.setVendor(vendor);
        option.setSimpleOutput(true);
        option.setShowConstantTable(true);

        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(file, option);

        dataFlowAnalyzer.generateDataFlow();
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(vendor, flow, false);

    }
}
