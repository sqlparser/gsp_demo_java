package dataflow;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.Option;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Relationship;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.dlineage.util.XML2Model;
import junit.framework.TestCase;

public class testIAOX42  extends TestCase{

	public void testOracleDataFlow() throws IOException {
        DataFlowAnalyzer analyzer = analyseSqlFiles(gspCommon.BASE_SQL_DIR_PRIVATE +"sqlscripts/IAOX42/solidatus-schema-minimalist.json");

        // When we generate the data flow
        analyzer.generateDataFlow();
        dataflow df = analyzer.getDataFlow();
        
        try {
			XML2Model.saveXML(df, new File("D:\\1.xml"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(EDbVendor.dbvoracle, df, false);

        List<Relationship> relationshipsWithRelevantSources =
                Arrays.stream(dataFlow.getRelationships())
                        .filter(r -> Arrays.stream(r.getSources())
                                .filter(s -> !s.getParentName().equalsIgnoreCase("MIS_T_DAL_ACBAL"))
                                .collect(Collectors.toList())
                                .isEmpty())
                        .collect(Collectors.toList());

        assertTrue(relationshipsWithRelevantSources.size() == 556);

        assertTargetAndSourceRelationshipExists(dataFlow, "MIS_T_AC_MSTR", "PRD_TYP_CODE", "MIS_T_DAL_ACBAL", "PRD_TYP_CODE");
    }

    private void assertTargetAndSourceRelationshipExists(Dataflow dataFlow, String targetTable, String targetColumn,
                                                         String expectedSourceTable, String expectedSourceColumn) {
        Arrays.stream(dataFlow.getRelationships())
                .filter(r -> {
                    String targetTableName = r.getTarget().getParentName();
                    String targetColumnName = r.getTarget().getColumn();
                    String sourceTableName = r.getSources()[0].getParentName();
                    String sourceColumnName = r.getSources()[0].getColumn();

                    return targetTableName.equals(targetTable)
                            && targetColumnName.equals(targetColumn)
                            && sourceTableName.equals(expectedSourceTable)
                            && sourceColumnName.equals(expectedSourceColumn);
                })
                .findFirst()
                .orElseThrow(() -> new AssertionError("Relationship not found for target column: " + targetColumn));
    }

    private DataFlowAnalyzer analyseSqlFiles(String path) throws IOException {
    	 File sqlFile = new File(path);

        EDbVendor vendor = EDbVendor.dbvoracle;
        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(sqlFile, vendor, true);

        Option option = new Option();
        option.setTransform(true);
        option.setShowConstantTable(true);
        option.setVendor(vendor);
        option.setSimpleOutput(true);
        option.setTextFormat(true);

        return dataFlowAnalyzer;
    }

}
