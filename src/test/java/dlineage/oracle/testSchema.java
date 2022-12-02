package dlineage.oracle;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Relationship;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.RelationshipElement;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.dlineage.metadata.Database;
import gudusoft.gsqlparser.dlineage.metadata.Schema;
import gudusoft.gsqlparser.dlineage.metadata.Table;
import junit.framework.TestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class testSchema extends TestCase{
    public void test1() {
        //solidatus: 0002525: Relationships parent has object in incorrect schema
        //https://e.gitee.com/gudusoft/issues/table?issue=I61BYG
        EDbVendor oracle = TGSqlParser.getDBVendorByName("oracle");
        File file = new File(common.gspCommon.BASE_SQL_DIR_PRIVATE +"dataflow/oracle/I61BYG.json");
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(file, oracle, true);

        dataFlowAnalyzer.generateDataFlow();
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(flow, oracle);

        Relationship rel1 = Arrays.stream(dataFlow.getRelationships())
                .filter(r -> Arrays.stream(r.getSources()).anyMatch(s -> s.getColumn().contains("TARGET_VALUE_1")))
                .filter(r -> r.getTarget().getParentName().contains("VTX_RAY_MY_CR_FACILITY")).collect(Collectors.toList()).get(0);

        assertTrue(rel1.getSources().length==3);
        RelationshipElement target_value_1 = Arrays.stream(rel1.getSources()).filter(s -> s.getColumn().contains("TARGET_VALUE_1")).collect(Collectors.toList()).get(0);

        assertTrue(target_value_1.getParentName().contains("CR_RAY_MY_PARAMETERS"));
        String parentId = target_value_1.getParentId();

        List<Table> tables = new ArrayList<>();
        List<Schema> schemas = new ArrayList<>();
        dataFlow.getDbobjs().getServers()
                .forEach(s -> {
                    for(Database db: s.getDatabases()){
                        for (Schema sc : db.getSchemas()) {
                            if (sc.getTables() == null){
                                continue;
                            }
                            sc.getTables()
                                    .forEach(t -> {
                                        // Get schema and table of the parent id in the relationship
                                        if (t.getId().equals(parentId)) {
                                            tables.add(t);
                                            schemas.add(sc);
                                        }
                                    });
                        }
                    }
                });

        // Inside the metadata json the table CR_RAY_MY_PARAMETERS is in the RFO schema.
        /*
                    "database":"ORCLPDB1.LOCALDOMAIN",
                    "isView":"false",
                    "name":"CR_RAY_MY_PARAMETERS",
                    "schema":"RFO"
         */
        assertTrue(schemas.get(0).getName().equalsIgnoreCase("RFO"));
        // Therefore the relationship should map to the correct schema.

    }
}
