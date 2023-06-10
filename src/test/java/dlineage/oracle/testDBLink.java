package dlineage.oracle;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Relationship;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.dlineage.metadata.Table;
import junit.framework.TestCase;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class testDBLink  extends TestCase {

    public void  test1(){
        File file = new File(common.gspCommon.BASE_SQL_DIR_PRIVATE +"dataflow/oracle/mviewDblink.json");

        // Create DataFlowAnalyzer
        EDbVendor vendor = TGSqlParser.getDBVendorByName("oracle");
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(file, vendor, true);

        dataFlowAnalyzer.generateDataFlow();
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(vendor, flow, false);

        Relationship dbLinkRel = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> Arrays.stream(r.getSources()).anyMatch(s -> s.getParentName().contains("@")))
                .collect(Collectors.toList()).get(0);

        //Parent ID = 71 in 2.6.1.6
        assertTrue(dbLinkRel.getSources()[0].getParentName().equalsIgnoreCase("\"HR\".\"EMPLOYEES\"@\"LD_PDB1_SOL.LOCALDOMAIN\""));

        //Parent ID = 66
        assertTrue(dbLinkRel.getTarget().getParentName().equalsIgnoreCase("\"DB2\".\"LOCOMOTIVES\""));
    }
    public void test2() {
        File file = new File(common.gspCommon.BASE_SQL_DIR_PRIVATE +"dataflow/oracle/mviewDblink.json");
        EDbVendor vendor = TGSqlParser.getDBVendorByName("oracle");
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(file, vendor, true);

        dataFlowAnalyzer.generateDataFlow();
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(vendor, flow, false);

        //CREATE MATERIALIZED VIEW \"DB2\".\"LOCOMOTIVES\" as SELECT cars.NAME,
        // employees.FIRST_NAME from CARS cars, \"HR\".\"EMPLOYEES\"@\"LD_PDB1_SOL.LOCALDOMAIN\" employees

        List<Relationship> dbLinkRelationships = Arrays.stream(dataFlow.getRelationships()).filter(s -> s.getTarget().getParentName().contains("\"DB2\".\"LOCOMOTIVES\"")).collect(Collectors.toList());

        assertTrue(dbLinkRelationships.size() == 2);

        // Relationship parent name and ID should be the same.
        Relationship rel1 = dbLinkRelationships.get(0);
        assertTrue(rel1.getSources().length == 1);
        assertTrue(rel1.getSources()[0].getParentName().equalsIgnoreCase("CARS"));
        String rel1SourceParentId = rel1.getSources()[0].getParentId();

        Relationship rel2 = dbLinkRelationships.get(1);
        assertTrue(rel2.getSources().length==1);
        assertTrue(rel2.getSources()[0].getParentName().equalsIgnoreCase("\"HR\".\"EMPLOYEES\"@\"LD_PDB1_SOL.LOCALDOMAIN\""));
        String rel2SourceParentId = rel2.getSources()[0].getParentId();

        Table tableObject1 = dataFlow.getDbobjs().getServers().get(0).getDatabases().get(0).getSchemas().get(0).getTables().get(0);
        assertTrue(tableObject1.getId().equals(rel1SourceParentId));
        assertTrue(tableObject1.getDisplayName().equalsIgnoreCase("DB2.CARS"));
        assertTrue(tableObject1.getDbLink() == null);

        Table tableObject2 = dataFlow.getDbobjs().getServers().get(0).getDatabases().get(1).getSchemas().get(0).getTables().get(0);
        assertTrue(tableObject2.getId().equals(rel2SourceParentId));
        assertTrue(tableObject2.getDisplayName().equalsIgnoreCase("\"HR\".\"EMPLOYEES\"@\"LD_PDB1_SOL.LOCALDOMAIN\""));
        assertTrue(tableObject2.getDbLink().equals("\"LD_PDB1_SOL.LOCALDOMAIN\""));
    }
}
