
package demos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import demos.analyzesp.Analyze_SP;

public class analyzespTest extends TestCase
{

	private String basedir;

	protected void setUp( )
	{
		basedir = common.gspCommon.BASE_SQL_DIR+"sqlscripts/analyze_sp";
	}

	public void testSample1( )
	{
		File file = new File( basedir + "/sample1.sql" );
		List<File> files = new ArrayList<File>( );
		files.add( file );
		Analyze_SP analyze = new Analyze_SP( files, "|" );
		analyze.setCheckDBObjectRelation( true );
		analyze.analyzeSQL( );

		String result = "[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpKoliKabulCheck|Table|Create|Barkod,IrsaliyeNo,GonderenDepo,AlanDepo,OkutmaTarihi,OkutanKullanici\r\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpKoliKabulCheck|Table|Insert|\r\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpKoliKabulCheck|Table|Update|Barkod\r\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpCurs|Table|Insert|\r\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|tb_TemaMobileCommandTransaction|Table|Read|TranData,TemaMobileTransactionTipTanimRef,IsDeleted\r\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpKoliKabulCheck|Table|Read|Barkod\r\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpCurs|Table|Read|ToplamaId,ToplamaKoliId,UrunBarkod\r\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpTable|Table|Create|OutputValue\r\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpTable|Table|Insert|OutputValue\r\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpTable|Table|Read|OutputValue\r\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpTable|Table|Drop|\r\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpCurs|Table|Update|KaydiMiktar\r\n"
                +"[Retail]|[dbo].[usp_CreateUrunIcerikForKoliKabul]|SP|#tmpCurs|Table|Read|*";
//		System.out.println( analyze.getDBObjectRelationsAnalysisResult( ) );
//		System.out.println("---------");
//		System.out.println( result);

		assertTrue( analyze.getDBObjectRelationsAnalysisResult( )
				.trim( )
				.equalsIgnoreCase( result ) );
	}
	
	public void testSample6( )
	{
		File file = new File( basedir + "/sample6.sql" );
		List<File> files = new ArrayList<File>( );
		files.add( file );
		Analyze_SP analyze = new Analyze_SP( files, "|" );
		analyze.setCheckDBObjectRelation( true );
		analyze.analyzeSQL( );
		//System.out.println( analyze.getDBObjectRelationsAnalysisResult( ) );
		String result = "[Retail]|[Sync].[Write_tb_AltDepoIsEmri]|SP|tb_AltDepoIsEmri|Table|Insert|AltDepoIsEmriRef,AnaDepo,FromAltDepo,ToAltDepo,UrunID,Miktar,KalanMiktar,Tarih,AcilisNedenRef,IptalNedenRef,Durum\r\n"
                +"[Retail]|[Sync].[Write_tb_AltDepoIsEmri]|SP|tb_AltDepoIsEmri|Table|Update|FromAltDepo,ToAltDepo,UrunID,Miktar,KalanMiktar,Tarih,AcilisNedenRef,IptalNedenRef,Durum\r\n"
                +"[Retail]|[Sync].[Write_tb_AltDepoIsEmri]|SP|tb_AltDepoIsEmri|Table|Update|FromAltDepo,ToAltDepo,UrunID,Miktar,KalanMiktar,Tarih,AcilisNedenRef,IptalNedenRef,Durum\r\n"
                +"[Retail]|[Sync].[Write_tb_AltDepoIsEmri]|SP|tb_AltDepoIsEmri|Table|Delete|";
		//System.out.println(result);
		assertTrue( analyze.getDBObjectRelationsAnalysisResult( )
				.trim( )
				.equalsIgnoreCase( result ) );
	}
	
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
	
	public void testSample8( )
	{
		File file = new File( basedir + "/sample8.sql" );
		List<File> files = new ArrayList<File>( );
		files.add( file );
		Analyze_SP analyze = new Analyze_SP( files, "|" );
		analyze.setCheckDBObjectRelation( true );
		analyze.analyzeSQL( );

		String result = "[Retail]|[dbo].[usp_GetKoliInfoByBarkod]|SP|tb_KargoKoliBaslik|Table|Read|TemaTakipNo,SevkID,Depo,ToplamaID,KoliID\r\n"
                +"[Retail]|[dbo].[usp_GetKoliInfoByBarkod]|SP|#tmpIrsaliye|Table|Drop|\r\n"
                +"[Retail]|[dbo].[usp_GetKoliInfoByBarkod]|SP|tb_KargoKoliBaslik|Table|Read|Depo,SevkID,TemaTakipNo\r\n"
                +"[Retail]|[dbo].[usp_GetKoliInfoByBarkod]|SP|tb_DepoSevkBaslik|Table|Read|FromDepo,SevkID,ToDepo\r\n"
                +"[Retail]|[dbo].[usp_GetKoliInfoByBarkod]|SP|tb_KargoKoliDetay|Table|Read|TemaTakipNo,UrunID,Miktar\r\n"
                +"[Retail]|[dbo].[usp_GetKoliInfoByBarkod]|SP|tb_UrunRecete|Table|Read|UrunID1,UrunID2,Miktar\r\n"
                +"[Retail]|[dbo].[usp_GetKoliInfoByBarkod]|SP|#tmpIrsaliye|Table|Insert|\r\n"
                +"[Retail]|[dbo].[usp_GetKoliInfoByBarkod]|SP|#tmpIrsaliye|Table|Update|UrunID2,ReceteMiktar\r\n"
                +"[Retail]|[dbo].[usp_GetKoliInfoByBarkod]|SP|#tmpIrsaliye|Table|Read|UrunID2,AsortiMiktar,ReceteMiktar,TemaTakipNo\r\n"
                +"[Retail]|[dbo].[usp_GetKoliInfoByBarkod]|SP|tb_Urun|Table|Read|UrunID,Barkod";

//		System.out.println( analyze.getDBObjectRelationsAnalysisResult( ) );
//		System.out.println("---------");
//		System.out.println( result);

		assertTrue( analyze.getDBObjectRelationsAnalysisResult( )
				.trim( )
				.equalsIgnoreCase( result ) );
	}

}
