
package gudusoft.gsqlparser.demosTest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import gudusoft.gsqlparser.demos.tracedatalineage.Column;
import gudusoft.gsqlparser.demos.tracedatalineage.traceDataLineage;
import junit.framework.TestCase;

/**
 * testDDL1/testDDL2 used to reference fixture files under
 * /demos/tracedatalineage/test/ddl(2)/ that never shipped with this repo, so
 * both methods had every line commented out and asserted nothing. Replaced
 * with a self-contained test against inline SQL. See
 * https://github.com/sqlparser/gsp_demo_java/issues/43.
 */
public class traceDataLineageTest extends TestCase
{

	public void testInsertSelectLineage( )
	{
		List<InputStream> streams = new ArrayList<InputStream>( );
		streams.add( sql( "CREATE TABLE source_tbl (id INT, amount INT);"
				+ "CREATE TABLE target_tbl (id INT, total INT);" ) );
		streams.add( sql( "INSERT INTO target_tbl (id, total) "
				+ "SELECT id, amount FROM source_tbl;" ) );

		traceDataLineage trace = new traceDataLineage( streams );

		assertEquals( 2, trace.getTracedLineage( ).size( ) );

		List<Column> idLineage = trace.getTracedLineage( ).get( 0 );
		assertEquals( 2, idLineage.size( ) );
		assertEquals( "source_tbl.id", trace.getColumnFullName( idLineage.get( 0 ) ) );
		assertEquals( "target_tbl.id", trace.getColumnFullName( idLineage.get( 1 ) ) );

		List<Column> amountLineage = trace.getTracedLineage( ).get( 1 );
		assertEquals( 2, amountLineage.size( ) );
		assertEquals( "source_tbl.amount", trace.getColumnFullName( amountLineage.get( 0 ) ) );
		assertEquals( "target_tbl.total", trace.getColumnFullName( amountLineage.get( 1 ) ) );
	}

	private static InputStream sql( String text )
	{
		return new ByteArrayInputStream( text.getBytes( ) );
	}
}
