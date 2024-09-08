package dlineage;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.Option;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Relationship;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import junit.framework.TestCase;

public class test_IALOHN extends TestCase {
	public void test1() {
		String defaultSchema = "SCHEMACODE";
		File metadataFile = new File(gspCommon.BASE_SQL_DIR_PRIVATE + "sqlscripts/IALOHN", "metadata4.json");
		File parseFile = new File(gspCommon.BASE_SQL_DIR_PRIVATE + "sqlscripts/IALOHN",
				"m_rept_crm_mngbrh_district_ratio_cdm_10500.sql");
		File[] files = new File[] { metadataFile, parseFile };
		Option option = new Option();
		option.setVendor(EDbVendor.dbvgreenplum);
		option.setDefaultSchema(defaultSchema);
		option.setIgnoreTemporaryTable(true);
//        option.setShowConstantTable(true);
		DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(files, option);
		dataFlowAnalyzer.generateDataFlow(true);
		dataflow df = dataFlowAnalyzer.getDataFlow();
		Map<String, String> tableIdMap = df.getTables().stream().collect(Collectors.toMap(t -> t.getId(), t -> t.getName()));
		Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(EDbVendor.dbvgreenplum, df, false);
		Relationship[] rels = Arrays.stream(dataFlow.getRelationships())
				.filter(r -> "SCHEMACODE.SWC_RP_SELFMC_YYBPM_QSC".equals(tableIdMap.get(r.getTarget().getParentId())))
				.collect(Collectors.toList()).toArray(new Relationship[0]);
		assertTrue(rels.length > 0);
		//System.out.println("ok");
	}

}
