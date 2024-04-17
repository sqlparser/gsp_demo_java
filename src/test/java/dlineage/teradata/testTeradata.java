package dlineage.teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.Option;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Relationship;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.util.CollectionUtil;
import gudusoft.gsqlparser.util.json.JSON;
import junit.framework.TestCase;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class testTeradata extends TestCase {
    public void  test1(){
        //https://e.gitee.com/gudusoft/issues/table?issue=I620EV
        File file = new File(common.gspCommon.BASE_SQL_DIR_PRIVATE +"dataflow/teradata/query_sample.btq");

        EDbVendor vendor = TGSqlParser.getDBVendorByName("teradata");
        Option option = new Option();
        option.setVendor(vendor);
        option.setSimpleOutput(false);
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(file, option);

        dataFlowAnalyzer.generateDataFlow();
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(vendor, flow, false);
        String json = JSON.toJSONString(dataFlow);
        List<Relationship> relationships = Arrays.stream(dataFlow.getRelationships())
                .filter(s -> s.getTarget().getParentName().contains("EDW_SIT_SEM_DATA.FISCAL_CALENDAR")).collect(Collectors.toList());
        assertTrue(!CollectionUtil.isEmpty(relationships) && relationships.size()==57);

        List<Relationship> insertRelationships = relationships.stream().filter(r -> Arrays.stream(r.getSources()).anyMatch(s -> s.getParentName().contains("INSERT-")))
                .collect(Collectors.toList());
        assertTrue(!CollectionUtil.isEmpty(insertRelationships) && insertRelationships.size()==30);

        List<Relationship> updateRelationships = relationships.stream().filter(r -> Arrays.stream(r.getSources()).anyMatch(s -> s.getParentName().contains("UPDATE-")))
                .collect(Collectors.toList());
        assertTrue(!CollectionUtil.isEmpty(updateRelationships) && updateRelationships.size()==27);
    }
    
    public void  testJoin(){
        //https://e.gitee.com/gudusoft/issues/table?issue=I620EV
        File file = new File(common.gspCommon.BASE_SQL_DIR_PRIVATE +"dataflow/teradata/I8ZWLD.sql");

        EDbVendor vendor = TGSqlParser.getDBVendorByName("teradata");
        Option option = new Option();
        option.setVendor(vendor);
        option.setSimpleOutput(false);
        option.setShowJoin(true);
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(file, option);

        dataFlowAnalyzer.generateDataFlow();
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        long count = flow.getErrors().stream().filter(t->t.getErrorMessage().contains("NullPointerException")).count();
        assertTrue(count == 0L);
    }
}
