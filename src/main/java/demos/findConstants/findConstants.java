
package demos.findConstants;

import demos.utils.SQLUtil;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETokenType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.TSourceTokenList;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class findConstants
{

	private String errorMessage = "";

	public String getErrorMessage( )
	{
		return errorMessage;
	}

	private int errorNo;

	private String query;
	private EDbVendor vendor;
	private List<String> constants = new ArrayList<String>( );

	public findConstants( String sql, EDbVendor vendor )
	{
		this.query = sql;
		this.vendor = vendor;
	}

	public String getConstants( )
	{
		String result = Arrays.deepToString( constants.toArray( new String[0] ) );
		result = result.substring( 1, result.length( ) - 1 );
		return result;
	}

	public int find( )
	{
		constants.clear( );
		TGSqlParser sqlparser = new TGSqlParser( vendor );
		sqlparser.sqltext = this.query;
		errorNo = sqlparser.parse( );
		if ( errorNo != 0 )
		{
			errorMessage = sqlparser.getErrormessage( );
			return errorNo;
		}

		findSourceTokenList( sqlparser.sourcetokenlist );

		return errorNo;
	}

	private void findSourceTokenList( TSourceTokenList sourcetokenlist )
	{
		if ( sourcetokenlist == null )
			return;

		for ( int i = 0; i < sourcetokenlist.size( ); i++ )
		{
			TSourceToken token = sourcetokenlist.get( i );
			if ( token.tokentype == ETokenType.ttnumber
					|| token.tokentype == ETokenType.ttsqstring )
			{
				constants.add( token.astext );
			}
		}

	}

	public static void main( String[] args )
	{
		if ( args.length == 0 )
		{
			System.out.println( "Usage: java findConstants scriptfile [/t <database type>]" );
			System.out.println( "/t: Option, set the database type. Support oracle, mssql, the default type is oracle" );
			return;
		}

		List<String> argList = Arrays.asList( args );

		EDbVendor vendor = EDbVendor.dbvoracle;

		int index = argList.indexOf( "/t" );

		if ( index != -1 && args.length > index + 1 )
		{
			vendor = TGSqlParser.getDBVendorByName(args[index + 1]);
		}

		String sqltext = SQLUtil.getFileContent( new File( args[0] ) );
		findConstants findConstants = new findConstants( sqltext, vendor );
		if ( findConstants.find( ) != 0 )
		{
			System.out.println( findConstants.getErrorMessage( ) );
		}
		else
		{
			System.out.println( "string literals and numeric constants:" );
			System.out.println( findConstants.getConstants( ) );
		}
	}

}
