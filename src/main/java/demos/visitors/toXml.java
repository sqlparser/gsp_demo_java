
package demos.visitors;

import gudusoft.gsqlparser.*;

import java.io.*;
import java.util.Arrays;
import java.util.List;

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
			if ( args[index + 1].equalsIgnoreCase( "mssql" ) )
			{
				dbVendor = EDbVendor.dbvmssql;
			}
			else if ( args[index + 1].equalsIgnoreCase( "db2" ) )
			{
				dbVendor = EDbVendor.dbvdb2;
			}
			else if ( args[index + 1].equalsIgnoreCase( "mysql" ) )
			{
				dbVendor = EDbVendor.dbvmysql;
			}
			else if ( args[index + 1].equalsIgnoreCase( "mssql" ) )
			{
				dbVendor = EDbVendor.dbvmssql;
			}
			else if ( args[index + 1].equalsIgnoreCase( "oracle" ) )
			{
				dbVendor = EDbVendor.dbvoracle;
			}
			else if ( args[index + 1].equalsIgnoreCase( "netezza" ) )
			{
				dbVendor = EDbVendor.dbvnetezza;
			}
			else if ( args[index + 1].equalsIgnoreCase( "teradata" ) )
			{
				dbVendor = EDbVendor.dbvteradata;
			}
			else if ( args[index + 1].equalsIgnoreCase( "mdx" ) )
			{
				dbVendor = EDbVendor.dbvmdx;
			}
			else if ( args[index + 1].equalsIgnoreCase( "postgresql" ) )
			{
				dbVendor = EDbVendor.dbvpostgresql;
			}
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
