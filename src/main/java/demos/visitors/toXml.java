
package demos.visitors;

import gudusoft.gsqlparser.*;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import demos.utils.SQLUtil;

public class toXml
{

	public static void main( String args[] ) throws IOException
	{
		long t = System.currentTimeMillis( );

		if ( args.length < 1 )
		{
			System.out.println( "Usage: java toXml sqlfile.sql [/t <database type>]" );
			System.out.println( "/t: Option, set the database type. Support oracle, mysql, mssql and db2, the default type is mysql" );
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

			dbVendor = SQLUtil.getVendor(args[index + 1]);

		}
		System.out.println( "Selected SQL dialect: " + dbVendor.toString( ) );

		TGSqlParser sqlparser = new TGSqlParser( dbVendor );
		sqlparser.sqlfilename = args[0];
		String xmlFile = args[0] + ".xml";

		int ret = sqlparser.parse( );
		if ( ret == 0 )
		{
			String xsdfile = "file:/C:/prg/gsp_java/library/doc/xml/sqlschema.xsd";
			xmlVisitor xv2 = new xmlVisitor( xsdfile );
			xv2.run( sqlparser );
			xv2.validXml();
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
