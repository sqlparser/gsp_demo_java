
package demos.visitors;

import gudusoft.gsqlparser.*;

import java.io.*;
import java.util.Arrays;
import java.util.List;

//import demos.utils.SQLUtil;

public class toXml
{

	public static void main( String args[] ) throws IOException
	{
		long t = System.currentTimeMillis( );

		if ( args.length < 1 )
		{
			System.out.println( "Usage: java toXml sqlfile.sql [/t <database type>]" );
			System.out.println( "/t: Option, set the database type." );
			return;
		}
		File file = new File( args[0] );
		if ( !file.exists( ) )
		{
			System.out.println( "File not exists:" + args[0] );
			return;
		}

		EDbVendor dbVendor = EDbVendor.dbvmssql;

		List<String> argList = Arrays.asList( args );
		int index = argList.indexOf( "/t" );

		if ( index != -1 && args.length > index + 1 )
		{

			dbVendor = TGSqlParser.getDBVendorByName(args[index + 1]);

		}
		System.out.println( "Selected SQL dialect: " + dbVendor.toString( ) );

		//TBaseType.ENABLE_INTERPRETER = true;
		TGSqlParser sqlparser = new TGSqlParser( dbVendor );
		sqlparser.sqlfilename = args[0];
		String xmlFile = args[0] + ".xml";
		sqlparser.setTokenListHandle(new myTokenListHandle());
		int ret = sqlparser.parse( );

		if ( ret == 0 )
		{
			String xsdfile = "file:/C:/prg/gsp_java_maven/doc/xml/sqlschema.xsd";
			xmlVisitor xv2 = new xmlVisitor( xsdfile );
			xv2.run( sqlparser );
			//xv2.validXml();
			xv2.writeToFile( xmlFile );
			System.out.println( xmlFile + " was generated!" );

		}
		else
		{
			System.out.println( sqlparser.getErrormessage( ) );
		}

		System.out.println( "Time Escaped: "
				+ ( System.currentTimeMillis( ) - t ) );
	}

}

class myTokenListHandle implements ITokenListHandle {
	// 把 ${tx_date_yyyymm} 合并为一个token，token code为 TBasetype.ident
	public boolean processTokenList(TSourceTokenList sourceTokenList){
		int startIndex = -1;
		int endIndex = -1;

		for(int i=0;i< sourceTokenList.size();i++) {
			TSourceToken token = sourceTokenList.get(i);

			// Check for '$' followed immediately by '{'
			if (token.tokencode == 36) { // Check for '$'
				if (i + 1 < sourceTokenList.size() && sourceTokenList.get(i + 1).tokencode == 123) { // Check for '{' immediately after '$'
					startIndex = i;
				}
			} else if (token.tokencode == 125 && startIndex != -1) { // Check for '}'
				endIndex = i;

			}


			if (startIndex != -1 && endIndex != -1) {
				TSourceToken firstToken = sourceTokenList.get(startIndex);
				firstToken.tokencode = TBaseType.ident;
				for (int j = startIndex + 1; j <= endIndex; j++) {
					TSourceToken st = sourceTokenList.get(j);
					st.tokenstatus = ETokenStatus.tsdeleted;
					firstToken.setString(firstToken.astext + st.astext);
				}

				//System.out.println("Found variable token: " + firstToken.toStringDebug());

				startIndex = -1;
				endIndex = -1;
			}
		}
		return true;
	}
}
