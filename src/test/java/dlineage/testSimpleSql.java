package dlineage;

import java.util.List;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.DbObjectPosition;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Coordinate;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.column;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.dlineage.util.Pair;
import gudusoft.gsqlparser.dlineage.util.SqlInfoHelper;
import junit.framework.TestCase;

public class testSimpleSql extends TestCase {

	public void test1() {
		String sql = "select first from employee;\nselect second from employee;";
		DataFlowAnalyzer analyzer = new DataFlowAnalyzer(sql, EDbVendor.dbvmysql, true);
		analyzer.generateDataFlow();
		dataflow dataflow = analyzer.getDataFlow();
		
		//Collect the sql information, and user should persist it.
		String sqlInfoJson = SqlInfoHelper.getSqlInfoJson(analyzer);
		
		//Initialize the SqlInfoHelper by the sql information
		SqlInfoHelper helper = new SqlInfoHelper(sqlInfoJson);
		
		column firstColumn = dataflow.getTables().get(0).getColumns().get(0);
		Coordinate[][] firstColumnCoordinates = SqlInfoHelper.parseCoordinateString(firstColumn.getCoordinate());

		DbObjectPosition firstColumnPosition = helper.getSelectedDbObjectInfo(firstColumnCoordinates[0][0], firstColumnCoordinates[0][1]);
		List<Pair<Integer, Integer>> positions = firstColumnPosition.getPositions();
		assertEquals(positions.get(0), new Pair<Integer, Integer>(1, 8));
		assertEquals(positions.get(1), new Pair<Integer, Integer>(1, 13));
		
		firstColumnPosition = helper.getSelectedDbObjectStatementInfo(EDbVendor.dbvmysql, firstColumnCoordinates[0][0], firstColumnCoordinates[0][1]);
		//DbObjectPosition.getIndex() return the stmt index of sql, base 0.
		assertEquals(firstColumnPosition.getIndex(), 0);
		positions = firstColumnPosition.getPositions();
		//Column position, base 1
		assertEquals(positions.get(0), new Pair<Integer, Integer>(1, 8));
		assertEquals(positions.get(1), new Pair<Integer, Integer>(1, 13));
		

		column secondColumn = dataflow.getTables().get(0).getColumns().get(1);
		Coordinate[][] secondColumnCoordinates = SqlInfoHelper.parseCoordinateString(secondColumn.getCoordinate());

		DbObjectPosition secondColumnPosition = helper.getSelectedDbObjectInfo(secondColumnCoordinates[0][0], secondColumnCoordinates[0][1]);
		positions = secondColumnPosition.getPositions();
		assertEquals(positions.get(0), new Pair<Integer, Integer>(2, 8));
		assertEquals(positions.get(1), new Pair<Integer, Integer>(2, 14));
		
		secondColumnPosition = helper.getSelectedDbObjectStatementInfo(EDbVendor.dbvmysql, secondColumnCoordinates[0][0], secondColumnCoordinates[0][1]);
		//DbObjectPosition.getIndex() return the stmt index of sql, base 0.
		assertEquals(secondColumnPosition.getIndex(), 1);
		positions = secondColumnPosition.getPositions();
		//Column position, base 1
		assertEquals(positions.get(0), new Pair<Integer, Integer>(1, 8));
		assertEquals(positions.get(1), new Pair<Integer, Integer>(1, 14));
	}
}