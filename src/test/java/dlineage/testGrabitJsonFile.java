package dlineage;

import java.io.File;
import java.util.Arrays;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.DbObjectPosition;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Coordinate;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.error;
import gudusoft.gsqlparser.dlineage.util.SqlInfoHelper;
import junit.framework.TestCase;

public class testGrabitJsonFile extends TestCase {

	public void test1() {
		//File sqlfiles = new File("D:/develop/git/gsp_sqlfiles/TestCases/grabit/mssql/561.json");
		File sqlfiles = new	 File(common.gspCommon.BASE_SQL_DIR+"grabit/mssql/561.json");
		DataFlowAnalyzer analyzer = new DataFlowAnalyzer(sqlfiles, EDbVendor.dbvmssql, false);
		analyzer.generateDataFlow();
		dataflow dataflow = analyzer.getDataFlow();
		String sqlInfoJson = SqlInfoHelper.getSqlInfoJson(analyzer);
		SqlInfoHelper helper = new SqlInfoHelper(sqlInfoJson);

		error error = dataflow.getErrors().get(0);
		String coordinate = error.getCoordinate();
		Coordinate[][] coordinates = SqlInfoHelper.parseCoordinateString(coordinate);

		DbObjectPosition position = helper.getSelectedDbObjectInfo(coordinates[0][0], coordinates[0][1]);
		assertEquals("[[336,5], [336,8]]", Arrays.deepToString(position.getPositions().toArray()));
		assertEquals("sys", position.getSql().split("\n", -1)[336 - 1].substring(5 - 1, 8 - 1));
	}
}