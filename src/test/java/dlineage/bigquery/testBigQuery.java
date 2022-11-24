package dlineage.bigquery;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.Option;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Relationship;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
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
        assertTrue(SQL_CONSTANTS.getSources()[0].getColumn().equalsIgnoreCase("[]"));
    }
}
