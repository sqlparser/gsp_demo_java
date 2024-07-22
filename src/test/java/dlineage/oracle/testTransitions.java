package dlineage.oracle;

import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.Option;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import junit.framework.TestCase;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public class testTransitions extends TestCase {
    public void  test1(){
        File[] files = {
                new File(common.gspCommon.BASE_SQL_DIR_PRIVATE +"dataflow/oracle/I5ZBWU/create_departments_table.sql"),
                new File(common.gspCommon.BASE_SQL_DIR_PRIVATE +"dataflow/oracle/I5ZBWU/create_employees_table.sql"),
                new File(common.gspCommon.BASE_SQL_DIR_PRIVATE +"dataflow/oracle/I5ZBWU/create_employees_dump_table.sql"),
                new File(common.gspCommon.BASE_SQL_DIR_PRIVATE +"dataflow/oracle/I5ZBWU/dump_by_location_procedureless.sql")
        };

        // Create DataFlowAnalyzer
        Option option = new Option();
        option.setVendor(TGSqlParser.getDBVendorByName("oracle"));
        option.setSimpleOutput(true);
        option.setDefaultSchema("HR");
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(files,option);
        dataFlowAnalyzer.generateDataFlow();
        dataflow flow = dataFlowAnalyzer.getDataFlow();

        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(TGSqlParser.getDBVendorByName("oracle"), flow, false);

        //Assert on number of transitions
//        assertTrue(Objects.isNull(dataFlow.getErrors()) || dataFlow.getErrors().length == 0);
        assertTrue(dataFlow.getRelationships().length == 11);
        //All relationships should have a related process
        assertFalse(Arrays.stream(dataFlow.getRelationships())
                .anyMatch(r -> r.getProcessId() == null));

    }
}
