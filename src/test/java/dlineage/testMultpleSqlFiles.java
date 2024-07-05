package dlineage;

import java.io.File;
import java.util.List;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.DbObjectPosition;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Coordinate;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.procedure;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.table;
import gudusoft.gsqlparser.dlineage.util.Pair;
import gudusoft.gsqlparser.dlineage.util.SqlInfoHelper;
import junit.framework.TestCase;

public class testMultpleSqlFiles extends TestCase {

	public void test1() {
		//File sqlfiles = new File("D:/develop/git/gsp_sqlfiles/TestCases/dlineage/mysql/547");
		File sqlfiles = new	 File(common.gspCommon.BASE_SQL_DIR+"private/dlineage/mysql/547");
		DataFlowAnalyzer analyzer = new DataFlowAnalyzer(sqlfiles, EDbVendor.dbvmysql, false);
		analyzer.generateDataFlow();
		dataflow dataflow = analyzer.getDataFlow();
		String sqlInfoJson = SqlInfoHelper.getSqlInfoJson(analyzer);
		SqlInfoHelper helper = new SqlInfoHelper(sqlInfoJson);

		table basic_vw = dataflow.getViews().get(0);
		Coordinate[][] viewCoordinates = SqlInfoHelper.parseCoordinateString(basic_vw.getCoordinate());

		DbObjectPosition viewPosition = helper.getSelectedDbObjectInfo(viewCoordinates[0][0], viewCoordinates[0][1]);
		String fileName = viewPosition.getFile();
		List<Pair<Integer, Integer>> positions = viewPosition.getPositions();
		assertEquals(fileName, "1.sql");
		assertEquals(positions.get(0), new Pair<Integer, Integer>(1, 1));
		assertEquals(positions.get(1), new Pair<Integer, Integer>(1, 90));

		procedure Basic_Sal = dataflow.getProcedures().get(0);
		Coordinate[][] procedureCoordinates = SqlInfoHelper.parseCoordinateString(Basic_Sal.getCoordinate());

		DbObjectPosition procedurePosition = helper.getSelectedDbObjectInfo(procedureCoordinates[0][0],
				procedureCoordinates[0][1]);
		fileName = procedurePosition.getFile();
		positions = procedurePosition.getPositions();
		Pair<Integer, Integer> startPosition = positions.get(0);
		Pair<Integer, Integer> endPosition = positions.get(1);
		assertEquals(fileName, "2.sql");
		assertEquals(startPosition, new Pair<Integer, Integer>(3, 1));
		assertEquals(endPosition, new Pair<Integer, Integer>(5, 6));

		DbObjectPosition procedureStatementPosition = helper.getSelectedDbObjectStatementInfo(EDbVendor.dbvmysql,
				procedureCoordinates[0][0], procedureCoordinates[0][1]);
		String sql = procedureStatementPosition.getSql();
		String stmt = "CREATE PROCEDURE `Basic_Sal`()BEGIN\r\n" 
				+ "        SELECT * FROM basic;\r\n" 
				+ "END;";
		int index = procedureStatementPosition.getIndex();
		positions = procedureStatementPosition.getPositions();
		startPosition = positions.get(0);
		endPosition = positions.get(1);
		assertEquals(index, 1);
		assertEquals(sql.trim().replaceAll("\r?\n", "\n"), stmt.trim().replaceAll("\r?\n", "\n"));
		assertEquals(startPosition, new Pair<Integer, Integer>(1, 1));
		assertEquals(endPosition, new Pair<Integer, Integer>(3, 6));
	}
}