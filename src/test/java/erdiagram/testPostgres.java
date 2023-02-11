package erdiagram;

import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.Option;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.dlineage.metadata.Column;
import junit.framework.TestCase;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class testPostgres   extends TestCase {
    public void test1() {
        File file = new File(common.gspCommon.BASE_SQL_DIR_PRIVATE + "erdiagram/postgres_voyager.sql");
        Option option = new Option();
        option.setVendor(TGSqlParser.getDBVendorByName("postgresql"));
        option.setShowERDiagram(true);
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(file, option);
        dataFlowAnalyzer.generateDataFlow();
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        assertTrue(flow.getRelationships().stream().filter(t->"er".equals(t.getType())).collect(Collectors.toList()).size()==6);
    }

    public void test2() {
        File file = new File(common.gspCommon.BASE_SQL_DIR_PRIVATE + "erdiagram/postgres_index.sql");
        Option option = new Option();
        option.setVendor(TGSqlParser.getDBVendorByName("postgresql"));
        option.setShowERDiagram(true);
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(file, option);
        dataFlowAnalyzer.generateDataFlow();
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(TGSqlParser.getDBVendorByName("postgresql"), flow, false);
        List<Column> column = dataFlow.getDbobjs().getServers().get(0).getDatabases().get(0).getSchemas().get(0).getTables().get(0).getColumns();
        assertTrue(column.get(1).isIndexKey());
        assertTrue(column.get(2).isIndexKey());
    }
}
