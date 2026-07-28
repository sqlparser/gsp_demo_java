
package gudusoft.gsqlparser.demosTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.demos.analyzesp.Analyze_SP;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * These read their input from the shared SQL corpus in the gsp_java library
 * repository, which is absent unless that repository is checked out beside this
 * one, so they skip rather than fail when it is missing. See
 * {@link gspCommon} for why, and for the path bug that made them look like
 * output drift for as long as they did.
 *
 * <p>JUnit 4 annotations rather than extending TestCase, deliberately:
 * Assume inside a JUnit 3 TestCase is reported by surefire as an ERROR, not as
 * a skip, so the skip would have been indistinguishable from the failure it
 * replaces.
 */
public class analyzespTest
{

	private String basedir;

	@Before
	public void setUp( )
	{
		Assume.assumeTrue( gspCommon.whySqlFilesMissing( ), gspCommon.sqlFilesAvailable( ) );
		basedir = gspCommon.BASE_SQL_DIR+"private/sqlscripts/analyze_sp";
	}

	@Test
	public void testSample1( )
	{
		File file = new File( basedir + "/sample1.sql" );
		List<File> files = new ArrayList<File>( );
		files.add( file );
		Analyze_SP analyze = new Analyze_SP( files, "|" );
		analyze.setCheckDBObjectRelation( true );
		analyze.analyzeSQL( );

		String result = "[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpKoliKabulCheck|Table|Create|Barkod,IrsaliyeNo,GonderenDepo,AlanDepo,OkutmaTarihi,OkutanKullanici\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpKoliKabulCheck|Table|Insert|\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpKoliKabulCheck|Table|Update|Barkod\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpCurs|Table|Insert|\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|tb_TemaMobileCommandTransaction|Table|Read|TranData,TemaMobileTransactionTipTanimRef,IsDeleted\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpKoliKabulCheck|Table|Read|Barkod\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpCurs|Table|Read|ToplamaId,ToplamaKoliId,UrunBarkod\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpTable|Table|Create|OutputValue\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpTable|Table|Insert|OutputValue\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpTable|Table|Read|OutputValue\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpTable|Table|Drop|\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpCurs|Table|Update|KaydiMiktar\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpCurs|Table|Read|*";
//		System.out.println( analyze.getDBObjectRelationsAnalysisResult( ) );
//		System.out.println("---------");
//		System.out.println( result);

		assertTrue( analyze.getDBObjectRelationsAnalysisResult( ).replace("\r\n", "\n")
				.trim( )
				.equalsIgnoreCase( result ) );
	}
	
	@Test
	public void testSample6( )
	{
		File file = new File( basedir + "/sample6.sql" );
		List<File> files = new ArrayList<File>( );
		files.add( file );
		Analyze_SP analyze = new Analyze_SP( files, "|" );
		analyze.setCheckDBObjectRelation( true );
		analyze.analyzeSQL( );
		//System.out.println( analyze.getDBObjectRelationsAnalysisResult( ) );
		String result = "[Retail]|[Sync].[Write_tb_AltDepoIsEmri]|SP|tb_AltDepoIsEmri|Table|Insert|AltDepoIsEmriRef,AnaDepo,FromAltDepo,ToAltDepo,UrunID,Miktar,KalanMiktar,Tarih,AcilisNedenRef,IptalNedenRef,Durum\n"
                +"[Retail]|[Sync].[Write_tb_AltDepoIsEmri]|SP|tb_AltDepoIsEmri|Table|Update|FromAltDepo,ToAltDepo,UrunID,Miktar,KalanMiktar,Tarih,AcilisNedenRef,IptalNedenRef,Durum\n"
                +"[Retail]|[Sync].[Write_tb_AltDepoIsEmri]|SP|tb_AltDepoIsEmri|Table|Update|FromAltDepo,ToAltDepo,UrunID,Miktar,KalanMiktar,Tarih,AcilisNedenRef,IptalNedenRef,Durum\n"
                +"[Retail]|[Sync].[Write_tb_AltDepoIsEmri]|SP|tb_AltDepoIsEmri|Table|Delete|";
		//System.out.println(result);
		assertTrue( analyze.getDBObjectRelationsAnalysisResult( ).replace("\r\n", "\n")
				.trim( )
				.equalsIgnoreCase( result ) );
	}
	
	@Test
	public void testSample7( )
	{
		File file = new File( basedir + "/sample7.sql" );
		List<File> files = new ArrayList<File>( );
		files.add( file );
		Analyze_SP analyze = new Analyze_SP( files, "|" );
		analyze.setCheckDBObjectRelation( true );
		analyze.analyzeSQL( );
		//System.out.println( analyze.getDBObjectRelationsAnalysisResult( ) );
		String result = "";
		assertTrue( analyze.getDBObjectRelationsAnalysisResult( )
				.trim( )
				.equalsIgnoreCase( result ) );
	}
	
	@Test
	public void testSample8( )
	{
		File file = new File( basedir + "/sample8.sql" );
		List<File> files = new ArrayList<File>( );
		files.add( file );
		Analyze_SP analyze = new Analyze_SP( files, "|" );
		analyze.setCheckDBObjectRelation( true );
		analyze.analyzeSQL( );

		String result = "[Retail]|[dbo].[usp_GetKoliInfoByBarkod]|SP|tb_KargoKoliBaslik|Table|Read|TemaTakipNo,SevkID,Depo,ToplamaID,KoliID\n" +
				"[Retail]|[dbo].[usp_GetKoliInfoByBarkod]|SP|#tmpIrsaliye|Table|Drop|\n" +
				"[Retail]|[dbo].[usp_GetKoliInfoByBarkod]|SP|tb_KargoKoliBaslik|Table|Read|Depo,SevkID,TemaTakipNo\n" +
				"[Retail]|[dbo].[usp_GetKoliInfoByBarkod]|SP|tb_DepoSevkBaslik|Table|Read|FromDepo,SevkID,ToDepo\n" +
				"[Retail]|[dbo].[usp_GetKoliInfoByBarkod]|SP|tb_KargoKoliDetay|Table|Read|TemaTakipNo,UrunID,Miktar\n" +
				"[Retail]|[dbo].[usp_GetKoliInfoByBarkod]|SP|tb_UrunRecete|Table|Read|UrunID1,UrunID2,Miktar\n" +
				"[Retail]|[dbo].[usp_GetKoliInfoByBarkod]|SP|#tmpIrsaliye|Table|Insert|\n" +
				"[Retail]|[dbo].[usp_GetKoliInfoByBarkod]|SP|#tmpIrsaliye|Table|Update|UrunID2,ReceteMiktar";

//		System.out.println( analyze.getDBObjectRelationsAnalysisResult( ) );
//		System.out.println("---------");
//		System.out.println( result);

		assertTrue( analyze.getDBObjectRelationsAnalysisResult( ).replace("\r\n", "\n")
				.trim( )
				.equalsIgnoreCase( result ) );
	}

}
