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
import gudusoft.gsqlparser.dlineage.metadata.Schema;
import gudusoft.gsqlparser.dlineage.metadata.TreeNode;
import junit.framework.TestCase;

public class test3360  extends TestCase{

	public void testSqlServerResultSet() throws IOException {
	    // Given a SQL file with a result set output .
	    EDbVendor vendor = EDbVendor.dbvmssql;
	    DataFlowAnalyzer analyzer = analyseSqlFiles(gspCommon.BASE_SQL_DIR_PRIVATE +"dlineage/mssql/3655.sql", vendor);

	    // When we generate the data flow using the variable and cursor options
	    analyzer.generateDataFlow();
	    dataflow df = analyzer.getDataFlow();
	    Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(vendor, df, false);

	    // Then we receive relationships with processIds
	    List<Relationship> selectRels = Arrays.stream(dataFlow.getRelationships())
	            .filter(r -> r.getEffectType().equals("select"))
	            .collect(Collectors.toList());

	    assertTrue(!selectRels.isEmpty());
	    selectRels.stream().forEach(t->assertTrue(t.getProcedureId()!=null));
	   
	    Schema schema = dataFlow.getDbobjs().getServers().get(0).getDatabases().get(0).getSchemas().get(1);
	    List<String> procedureIds = schema.getProcedures().stream()
	            .map(TreeNode::getId)
	            .collect(Collectors.toList());

	    selectRels.stream().forEach(t->assertTrue(procedureIds.contains(t.getProcedureId())));
	}

	private DataFlowAnalyzer analyseSqlFiles(String path, EDbVendor vendor) throws IOException {
	    File sqlFiles = new File(path);

	    Option option = new Option();
	    // Transform will be enabled only if SQL Derivation is enabled
	    option.setTransform(false);
	    option.setShowConstantTable(false);
	    option.setVendor(vendor);
	    option.setSimpleOutput(true);

	    // Enabling Text format avoids saveXML and parsing the dataflow as a text output only
	    option.setTextFormat(true);

	    // Shows lineage for procedures that are top level selects with no specific target output
	    option.setSimpleShowTopSelectResultSet(true);
	    option.setSimpleShowVariable(true);
	    option.setSimpleShowCursor(true);
	    option.setTraceProcedure(true);

	    return new DataFlowAnalyzer(sqlFiles, option);
	}

}
