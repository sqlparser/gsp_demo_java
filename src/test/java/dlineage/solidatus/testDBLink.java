package dlineage.solidatus;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Relationship;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.dlineage.metadata.Table;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class testDBLink extends TestCase {

    public void test1() {
        String sql = "create procedure CARS_HR_COUNTRIES\n" +
                " is\n" +
                "begin\n" +
                " insert into CARS (id, name)\n" +
                " select REGION_ID, COUNTRY_NAME from\n" +
                " SOLIDATUS.HR_COUNTRIES@LD_PDB1_SOL.LOCALDOMAIN;\n" +
                "end;";

        // System.out.println(sql);
        EDbVendor vendor = TGSqlParser.getDBVendorByName("oracle");
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(sql,
                vendor, true);

        dataFlowAnalyzer.generateDataFlow();
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(vendor, flow, false);

        // Get relationships that contain a DBLink in it.
        List<Relationship> dbLinkRelationships = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> Arrays.stream(r.getSources()).anyMatch(s -> s.getParentName().contains("@")))
                .collect(Collectors.toList());

        // 2 Relationships should exist
        assertTrue(dbLinkRelationships.size() == 2);

        // Relationship parent name and ID should be the same.
        Relationship rel1 = dbLinkRelationships.get(0);
        assertTrue(rel1.getSources().length == 1);
        assertTrue(rel1.getSources()[0].getParentName().toString().equalsIgnoreCase("SOLIDATUS.HR_COUNTRIES@LD_PDB1_SOL.LOCALDOMAIN"));
        String rel1SourceParentId = rel1.getSources()[0].getParentId();

        Relationship rel2 = dbLinkRelationships.get(1);
        assertTrue(rel2.getSources().length == 1);
        assertTrue(rel2.getSources()[0].getParentName().toString().equalsIgnoreCase("SOLIDATUS.HR_COUNTRIES@LD_PDB1_SOL.LOCALDOMAIN"));
        String rel2SourceParentId = rel2.getSources()[0].getParentId();

        // Relationships point to the correct Table object
        Table dbLinkTableObject = dataFlow.getDbobjs().getServers().get(0).getDatabases().get(0).getSchemas().get(0).getTables().get(0);
        assertTrue(dbLinkTableObject.getId().equalsIgnoreCase(rel1SourceParentId));
        assertTrue(dbLinkTableObject.getId().equalsIgnoreCase(rel2SourceParentId));
        // DbLink table name should be consistent with the parent name
        assertTrue(dbLinkTableObject.getName().toString().equalsIgnoreCase("HR_COUNTRIES"));
        assertTrue(dbLinkTableObject.getDbLink().toString().equalsIgnoreCase("\"LD_PDB1_SOL.LOCALDOMAIN\""));
    }



}
