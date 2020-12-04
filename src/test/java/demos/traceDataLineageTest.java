
package demos;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import demos.tracedatalineage.Column;
import demos.tracedatalineage.traceDataLineage;

public class traceDataLineageTest extends TestCase
{

	public void testDDL1( )
	{
//		List<InputStream> streams = new ArrayList<InputStream>( );
//		streams.add( traceDataLineageTest.class.getResourceAsStream( "/demos/tracedatalineage/test/ddl/g_sds_sub_dev0 - asd_asset_identifier.sql" ) );
//		streams.add( traceDataLineageTest.class.getResourceAsStream( "/demos/tracedatalineage/test/ddl/sds_bcp_dev0 - asd_wk_asset_identifier.sql" ) );
//		streams.add( traceDataLineageTest.class.getResourceAsStream( "/demos/tracedatalineage/test/ddl/sdsdev0 - asd_asset_identifier.sql" ) );
//		streams.add( traceDataLineageTest.class.getResourceAsStream( "/demos/tracedatalineage/test/ddl/sdsdev0 - asd_wk_asset_identifier.sql" ) );
//		streams.add( traceDataLineageTest.class.getResourceAsStream( "/demos/tracedatalineage/test/ddl/sdsdev0 - sds_m_asd_asset_identifier.sql" ) );
//		streams.add( traceDataLineageTest.class.getResourceAsStream( "/demos/tracedatalineage/test/ddl/sdsdev0 - sds_post_asd_asset_identifier.sql" ) );
//		traceDataLineage trace = new traceDataLineage( streams );
//		assertEquals( trace.getTracedLineage( ).size( ), 25 );
//		for ( int i = 0; i < trace.getTracedLineage( ).size( ); i++ )
//		{
//			List<Column> lineage = trace.getTracedLineage( ).get( i );
//			if ( trace.getColumnFullName( lineage.get( 0 ) )
//					.equals( "sds_bcp_dev0.asd_wk_asset_identifier.cusip_num" ) )
//			{
//				assertEquals( lineage.size( ), 4 );
//				assertEquals( trace.getColumnFullName( lineage.get( 0 ) ),
//						"sds_bcp_dev0.asd_wk_asset_identifier.cusip_num" );
//				assertEquals( trace.getColumnFullName( lineage.get( 1 ) ),
//						"sdsdev0.asd_wk_asset_identifier.cusip_num" );
//				assertEquals( trace.getColumnFullName( lineage.get( 2 ) ),
//						"sdsdev0.asd_asset_identifier.cusip_num" );
//				assertEquals( trace.getColumnFullName( lineage.get( 3 ) ),
//						"g_sds_sub_dev0.asd_asset_identifier.cusip_num" );
//			}
//		}
	}

	public void testDDL2( )
	{
//		List<InputStream> streams = new ArrayList<InputStream>( );
//		streams.add( traceDataLineageTest.class.getResourceAsStream( "/demos/tracedatalineage/test/ddl2/sdsdev0 - bog_pr_yu_qwe_jil_rltnshp.sql" ) );
//		streams.add( traceDataLineageTest.class.getResourceAsStream( "/demos/tracedatalineage/test/ddl2/sdsdev0 - bog_pr_prk_jil_rltnshp.sql" ) );
//		streams.add( traceDataLineageTest.class.getResourceAsStream( "/demos/tracedatalineage/test/ddl2/sdsdev0 - bog_pr_jil_snk.sql" ) );
//		streams.add( traceDataLineageTest.class.getResourceAsStream( "/demos/tracedatalineage/test/ddl2/sdsdev0 - bog_pr_jil.sql" ) );
//		streams.add( traceDataLineageTest.class.getResourceAsStream( "/demos/tracedatalineage/test/ddl2/g_sds_sub_dev0 - sds_tst_prdct.sql" ) );
//		streams.add( traceDataLineageTest.class.getResourceAsStream( "/demos/tracedatalineage/test/ddl2/g_sds_sub_dev0 - sds_tst_pos.sql" ) );
//		streams.add( traceDataLineageTest.class.getResourceAsStream( "/demos/tracedatalineage/test/ddl2/g_sds_sub_dev0 - sds_bld_tst_prdct.sql" ) );
//		streams.add( traceDataLineageTest.class.getResourceAsStream( "/demos/tracedatalineage/test/ddl2/g_sds_sub_dev0 - bog_pr_yu_qwe_jil_rltnshp.sql" ) );
//		streams.add( traceDataLineageTest.class.getResourceAsStream( "/demos/tracedatalineage/test/ddl2/g_sds_sub_dev0 - bog_pr_prk_jil_rltnshp.sql" ) );
//		streams.add( traceDataLineageTest.class.getResourceAsStream( "/demos/tracedatalineage/test/ddl2/g_sds_sub_dev0 - bog_pr_jil_snk.sql" ) );
//		streams.add( traceDataLineageTest.class.getResourceAsStream( "/demos/tracedatalineage/test/ddl2/g_sds_sub_dev0 - bog_pr_jil.sql" ) );
//		traceDataLineage trace = new traceDataLineage(streams );
//		assertEquals( trace.getTracedLineage( ).size( ), 74 );
//		for ( int i = 0; i < trace.getTracedLineage( ).size( ); i++ )
//		{
//			List<Column> lineage = trace.getTracedLineage( ).get( i );
//			if ( trace.getColumnFullName( lineage.get( 0 ) )
//					.equals( "sdsdev0.bog_pr_jil.bsecowid" ) )
//			{
//				assertEquals( lineage.size( ), 3 );
//				assertEquals( trace.getColumnFullName( lineage.get( 0 ) ),
//						"sdsdev0.bog_pr_jil.bsecowid" );
//				assertEquals( trace.getColumnFullName( lineage.get( 1 ) ),
//						"g_sds_sub_dev0.bog_pr_jil.bsecowid" );
//				assertEquals( trace.getColumnFullName( lineage.get( 2 ) ),
//						"g_sds_sub_dev0.sds_tst_prdct.base_cow_id" );
//			}
//		}
	}
}
