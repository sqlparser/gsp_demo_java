package erdiagram;

import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.Option;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import junit.framework.TestCase;
import java.io.File;
import java.util.stream.Collectors;

public class testMsSQL    extends TestCase {
    public void  test1(){
        File file = new File(common.gspCommon.BASE_SQL_DIR_PRIVATE +"erdiagram/voyager_sqlserver.sql");
        Option option = new Option();
        option.setVendor(TGSqlParser.getDBVendorByName("mssql"));
        option.setShowERDiagram(true);
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(file,option);
        dataFlowAnalyzer.generateDataFlow();
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        assertTrue(flow.getRelationships().stream().filter(t->"er".equals(t.getType())).collect(Collectors.toList()).size()==6);
    }
}