
package gudusoft.gsqlparser.demosTest;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import gudusoft.gsqlparser.demos.analyzesp.Analyze_SP;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Input comes from {@code src/test/resources/sqlscripts/analyze_sp/}, so these
 * run anywhere the repository is checked out, CI included.
 *
 * <p>They used to read the shared SQL corpus in the gsp_java library repository
 * over a relative path, which only resolved when the two repositories sat side
 * by side. That path was also one directory level short, so it resolved
 * nowhere at all: no input file was found, {@code Analyze_SP} returned an empty
 * string, and comparing that with the expected output failed. Those three
 * failures were written up for a long time as the parser's output drifting away
 * from stale golden strings, which they never were. The four scripts are now
 * checked in beside the test, so there is no sibling to get wrong. See
 * {@code src/test/resources/sqlscripts/analyze_sp/readme.md} for where they
 * came from.
 *
 * <p>JUnit 4 annotations rather than extending {@code TestCase}: kept from when
 * these skipped on a missing corpus, since {@code Assume} inside a JUnit 3
 * {@code TestCase} is reported by surefire 2.12.4 as an error rather than a
 * skip.
 */
public class analyzespTest
{

	private String basedir;

	@Before
	public void setUp( ) throws Exception
	{
		URL dir = analyzespTest.class.getResource( "/sqlscripts/analyze_sp" );
		assertNotNull( "test fixtures are missing from src/test/resources/sqlscripts/analyze_sp",
				dir );
		basedir = new File( dir.toURI( ) ).getPath( );
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
