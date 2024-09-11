package dataflow;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Relationship;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Transform;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import junit.framework.TestCase;

public class testIAQE4Y extends TestCase {
	public void testOracleDataFlow() throws IOException {
		DataFlowAnalyzer analyzer = analyseSqlFiles(
				gspCommon.BASE_SQL_DIR_PRIVATE + "sqlscripts/IAQE4Y/sqlserverderivationsimpleupdate.sql");

		// When we generate the data flow
		analyzer.generateDataFlow();
		dataflow df = analyzer.getDataFlow();
		Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(EDbVendor.dbvmssql, df, false);

		List<Relationship> lineTotalRels = Arrays.stream(dataFlow.getRelationships())
				.filter(r -> r.getTarget().getColumn().equalsIgnoreCase("[LineTotal]")).collect(Collectors.toList());

		Relationship lineTotalInsertRel = lineTotalRels.stream()
				.filter(r -> r.getEffectType().equalsIgnoreCase("insert")).findFirst().get();

		Relationship lineTotalUpdateRel = lineTotalRels.stream()
				.filter(r -> r.getEffectType().equalsIgnoreCase("update")).findFirst().get();

		assertNotNull(lineTotalInsertRel);
		assertNotNull(lineTotalUpdateRel);

		Transform[] insertTransforms = lineTotalInsertRel.getSources()[0].getTransforms();
		assertTrue(insertTransforms.length == 1);
		assertTrue(insertTransforms[0].getCode().equals("*"));

		Transform[] updateTransforms = Arrays.stream(lineTotalUpdateRel.getSources())
				.flatMap(s -> Arrays.stream(s.getTransforms())).toArray(Transform[]::new);
		assertTrue(updateTransforms.length == 3);
		assertTrue(Arrays.stream(updateTransforms)
				.allMatch(t -> t.getCode().equalsIgnoreCase("[LineTotal] + [UnitPrice] + [UnitPriceDiscount]")));
	}

	private DataFlowAnalyzer analyseSqlFiles(String path) throws IOException {
		File sqlFile = new File(path);

		EDbVendor vendor = EDbVendor.dbvmssql;
		DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(sqlFile, vendor, true);

		dataFlowAnalyzer.setTransform(true);

		return dataFlowAnalyzer;
	}
}
