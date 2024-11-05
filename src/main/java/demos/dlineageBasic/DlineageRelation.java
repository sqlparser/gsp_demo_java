package demos.dlineageBasic;

import gudusoft.gsqlparser.EDbVendor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import gudusoft.gsqlparser.TGSqlParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import demos.dlineageBasic.model.ddl.schema.column;
import demos.dlineageBasic.model.ddl.schema.database;
import demos.dlineageBasic.model.ddl.schema.table;
import demos.dlineageBasic.model.metadata.ColumnMetaData;
import demos.dlineageBasic.model.metadata.MetaScanner;
import demos.dlineageBasic.model.metadata.ProcedureMetaData;
import demos.dlineageBasic.model.xml.columnImpactResult;
import demos.dlineageBasic.model.xml.linkTable;
import demos.dlineageBasic.model.xml.sourceColumn;
import demos.dlineageBasic.model.xml.sourceProcedure;
import demos.dlineageBasic.model.xml.targetColumn;
import demos.dlineageBasic.model.xml.targetProcedure;
import demos.dlineageBasic.util.XML2Model;
import demos.dlineageBasic.util.XMLUtil;

public class DlineageRelation
{

	public columnImpactResult generateColumnImpact( Dlineage dlineage,
			StringBuffer errorMessage )
	{

		PrintStream pw = null;
		ByteArrayOutputStream sw = null;
		PrintStream systemSteam = System.err;;

		sw = new ByteArrayOutputStream( );
		pw = new PrintStream( sw );
		System.setErr( pw );

		String impactResult = dlineage.getColumnImpactResult( );
		if ( pw != null )
		{
			pw.close( );
		}

		System.setErr( systemSteam );

		if ( sw != null )
		{
			if ( errorMessage != null )
				errorMessage.append( sw.toString( ).trim( ) );
		}

		return getColumnImpactResult( impactResult );
	}

	public List<ColumnMetaData[]> collectDlineageRelations( Dlineage dlineage,
			columnImpactResult impactResult )
	{
		List<ColumnMetaData[]> relations = new ArrayList<ColumnMetaData[]>( );
		if ( dlineage == null || impactResult == null )
			return relations;
		database[] dataMetaInfos = dlineage.getDataMetaInfos( );
		if ( dataMetaInfos == null )
			return relations;

		MetaScanner scanner = new MetaScanner( dlineage );

		List<targetColumn> targetColumns = impactResult.getColumns( );
		if ( impactResult != null && targetColumns != null )
		{
			for ( int z = 0; z < targetColumns.size( ); z++ )
			{
				targetColumn target = targetColumns.get( z );
				if ( target.getLinkTable( ) != null
						&& target.getColumns( ) != null )
				{
					List<linkTable> links = target.getLinkTable( );
					for ( int i = 0; i < links.size( ); i++ )
					{
						linkTable link = links.get( i );

						for ( int j = 0; j < target.getColumns( ).size( ); j++ )
						{
							sourceColumn source = target.getColumns( ).get( j );

							if ( "true".equalsIgnoreCase( source.getOrphan( ) ) )
								continue;

							if ( source.getClause( ) != null )
							{
								if ( "select".equalsIgnoreCase( link.getType( ) ) )
								{
									if ( !"select".equalsIgnoreCase( source.getClause( ) ) )
										continue;
								}
								if ( "view".equalsIgnoreCase( link.getType( ) ) )
								{
									if ( !"select".equalsIgnoreCase( source.getClause( ) ) )
										continue;
								}
								if ( "insert".equalsIgnoreCase( link.getType( ) ) )
								{
									if ( !"select".equalsIgnoreCase( source.getClause( ) ) )
										continue;
								}
								if ( "update".equalsIgnoreCase( link.getType( ) ) )
								{
									if ( !"assign".equalsIgnoreCase( source.getClause( ) )
											&& !"select".equalsIgnoreCase( source.getClause( ) ) )
										continue;
								}
								if ( "merge".equalsIgnoreCase( link.getType( ) ) )
								{
									if ( !"assign".equalsIgnoreCase( source.getClause( ) )
											&& !"select".equalsIgnoreCase( source.getClause( ) ) )
										continue;
								}
							}

							String sourceTableName = null;
							if ( source.getTableOwner( ) != null
									&& !"unknown".equalsIgnoreCase( source.getTableOwner( ) ) )
							{
								sourceTableName = source.getTableOwner( )
										+ "."
										+ source.getTableName( );
							}
							else
							{
								sourceTableName = source.getTableName( );
							}

							ColumnMetaData sourceColumn = scanner.getColumnMetaData( sourceTableName
									+ "."
									+ source.getName( ) );

							String targetTableName = null;
							if ( link.getTableOwner( ) != null
									&& !"unknown".equalsIgnoreCase( link.getTableOwner( ) ) )
							{
								targetTableName = link.getTableOwner( )
										+ "."
										+ link.getTableName( );
							}
							else
							{
								targetTableName = link.getTableName( );
							}

							ColumnMetaData targetColumn = scanner.getColumnMetaData( targetTableName,
									link.getName( ) );

							if ( sourceColumn != null && targetColumn != null )
							{
								relations.add( new ColumnMetaData[]{
										targetColumn, sourceColumn
								} );
							}
							else
							{
								if ( sourceColumn == null )
								{
									System.err.println( sourceTableName
											+ "."
											+ source.getName( )
											+ " should not be null." );
								}

								if ( targetColumn == null )
								{
									System.err.println( targetTableName
											+ "."
											+ link.getName( )
											+ " should not be null." );
								}
							}
						}
					}
				}
			}
		}

		return relations;
	}

	public String generateDlineageRelation( Dlineage dlineage,
			columnImpactResult impactResult )
	{
		if ( dlineage == null || impactResult == null )
			return null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance( );
		Document doc = null;
		Element dlineageRelation = null;
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder( );
			doc = builder.newDocument( );
			doc.setXmlVersion( "1.0" );
			dlineageRelation = doc.createElement( "dlineageRelation" );
			doc.appendChild( dlineageRelation );
		}
		catch ( ParserConfigurationException e )
		{
			e.printStackTrace( );
		}

		if ( doc != null )
		{
			appendTables( dlineage, dlineageRelation );
			appendProcedures( dlineage, dlineageRelation );
			appendColumnRelations( impactResult, dlineageRelation );
			appendProcedureRelations( dlineage, dlineageRelation );

			try
			{
				return XMLUtil.format( doc, 2 );
			}
			catch ( Exception e )
			{
				e.printStackTrace( );
			}

		}
		return null;
	}

	private void appendProcedureRelations( Dlineage dlineage,
			Element dlineageRelation )
	{
		List<targetProcedure> targetProcedures = dlineage.getProcedures( ).first.getTargetProcedures( );
		if ( targetProcedures != null && !targetProcedures.isEmpty( ) )
		{
			for ( int z = 0; z < targetProcedures.size( ); z++ )
			{
				targetProcedure target = targetProcedures.get( z );
				if ( target.getSourceProcedures( ) != null )
				{
					for ( int j = 0; j < target.getSourceProcedures( ).size( ); j++ )
					{
						sourceProcedure source = target.getSourceProcedures( )
								.get( j );

						Element relationNode = dlineageRelation.getOwnerDocument( )
								.createElement( "relation" );

						Element sourceNode = dlineageRelation.getOwnerDocument( )
								.createElement( "source" );

						if ( source.getOwner( ) != null )
						{
							sourceNode.setAttribute( "owner", source.getOwner( ) );
						}

						sourceNode.setAttribute( "procedure", source.getName( ) );
						sourceNode.setAttribute( "coordinate",
								source.getCoordinate( ) );

						Element targetNode = dlineageRelation.getOwnerDocument( )
								.createElement( "target" );

						if ( target.getOwner( ) != null )
						{
							targetNode.setAttribute( "owner", target.getOwner( ) );
						}

						targetNode.setAttribute( "procedure", target.getName( ) );

						targetNode.setAttribute( "coordinate",
								target.getCoordinate( ) );

						relationNode.appendChild( sourceNode );
						relationNode.appendChild( targetNode );

						boolean append = true;
						for ( int k = 0; k < dlineageRelation.getChildNodes( )
								.getLength( ); k++ )
						{
							if ( dlineageRelation.getChildNodes( )
									.item( k )
									.isEqualNode( relationNode ) )
							{
								append = false;
								break;
							}
						}
						if ( append )
							dlineageRelation.appendChild( relationNode );
					}
				}
			}
		}

	}

	private void appendColumnRelations( columnImpactResult impactResult,
			Element dlineageRelation )
	{
		List<targetColumn> targetColumns = impactResult.getColumns( );
		if ( impactResult != null && targetColumns != null )
		{
			for ( int z = 0; z < targetColumns.size( ); z++ )
			{
				targetColumn target = targetColumns.get( z );
				if ( target.getLinkTable( ) != null
						&& target.getColumns( ) != null )
				{
					List<linkTable> links = target.getLinkTable( );
					for ( int i = 0; i < links.size( ); i++ )
					{
						linkTable link = links.get( i );
						for ( int j = 0; j < target.getColumns( ).size( ); j++ )
						{
							sourceColumn source = target.getColumns( ).get( j );

							if ( "true".equalsIgnoreCase( source.getOrphan( ) ) )
								continue;

							if ( source.getClause( ) != null )
							{
								if ( "select".equalsIgnoreCase( link.getType( ) ) )
								{
									if ( !"select".equalsIgnoreCase( source.getClause( ) ) )
										continue;
								}
								if ( "view".equalsIgnoreCase( link.getType( ) ) )
								{
									if ( !"select".equalsIgnoreCase( source.getClause( ) ) )
										continue;
								}
								if ( "insert".equalsIgnoreCase( link.getType( ) ) )
								{
									if ( !"select".equalsIgnoreCase( source.getClause( ) ) )
										continue;
								}
								if ( "update".equalsIgnoreCase( link.getType( ) ) )
								{
									if ( !"assign".equalsIgnoreCase( source.getClause( ) )
											&& !"select".equalsIgnoreCase( source.getClause( ) ) )
										continue;
								}
								if ( "merge".equalsIgnoreCase( link.getType( ) ) )
								{
									if ( !"assign".equalsIgnoreCase( source.getClause( ) )
											&& !"select".equalsIgnoreCase( source.getClause( ) ) )
										continue;
								}
							}

							Element relationNode = dlineageRelation.getOwnerDocument( )
									.createElement( "relation" );

							Element sourceNode = dlineageRelation.getOwnerDocument( )
									.createElement( "source" );

							if ( source.getTableOwner( ) != null
									&& !"unknown".equalsIgnoreCase( source.getTableOwner( ) ) )
							{
								sourceNode.setAttribute( "table",
										source.getTableOwner( )
												+ "."
												+ source.getTableName( ) );
							}
							else
							{
								sourceNode.setAttribute( "table",
										source.getTableName( ) );
							}
							sourceNode.setAttribute( "column", source.getName( ) );
							sourceNode.setAttribute( "coordinate",
									source.getCoordinate( ) );

							Element targetNode = dlineageRelation.getOwnerDocument( )
									.createElement( "target" );

							if ( link.getTableOwner( ) != null
									&& !"unknown".equalsIgnoreCase( link.getTableOwner( ) ) )
							{
								targetNode.setAttribute( "table",
										link.getTableOwner( )
												+ "."
												+ link.getTableName( ) );
							}
							else
							{
								targetNode.setAttribute( "table",
										link.getTableName( ) );
							}
							targetNode.setAttribute( "column", link.getName( ) );

							if ( target.getAliasCoordinate( ) != null )
							{
								targetNode.setAttribute( "coordinate",
										target.getAliasCoordinate( ) );
							}
							else
							{
								targetNode.setAttribute( "coordinate",
										link.getCoordinate( ) );
							}
							relationNode.appendChild( sourceNode );
							relationNode.appendChild( targetNode );

							boolean append = true;
							for ( int k = 0; k < dlineageRelation.getChildNodes( )
									.getLength( ); k++ )
							{
								if ( dlineageRelation.getChildNodes( )
										.item( k )
										.isEqualNode( relationNode ) )
								{
									append = false;
									break;
								}
							}
							if ( append )
								dlineageRelation.appendChild( relationNode );
						}
					}
				}
			}
		}
	}

	private void appendTables( Dlineage dlineage, Element dlineageRelation )
	{
		if ( dlineage.getDataMetaInfos( ) == null )
			return;
		for ( int i = 0; i < dlineage.getDataMetaInfos( ).length; i++ )
		{
			database db = dlineage.getDataMetaInfos( )[i];
			for ( int j = 0; j < db.getTables( ).size( ); j++ )
			{
				table currentTable = db.getTables( ).get( j );
				if ( currentTable.getColumns( ) == null
						|| currentTable.getColumns( ).isEmpty( ) )
					continue;

				Element tableNode = dlineageRelation.getOwnerDocument( )
						.createElement( "table" );
				if ( db.getName( ) != null
						&& !"unknown".equalsIgnoreCase( db.getName( ) ) )
				{

					tableNode.setAttribute( "name", db.getName( )
							+ "."
							+ currentTable.getName( ) );
				}
				else
				{
					tableNode.setAttribute( "name", currentTable.getName( ) );
				}

				if ( currentTable.getIsView( ) != null
						&& Boolean.TRUE == Boolean.valueOf( currentTable.getIsView( ) ) )
				{
					tableNode.setAttribute( "isView", currentTable.getIsView( ) );
				}

				for ( int k = 0; k < currentTable.getColumns( ).size( ); k++ )
				{
					column column = currentTable.getColumns( ).get( k );
					Element columnNode = dlineageRelation.getOwnerDocument( )
							.createElement( "column" );
					columnNode.setAttribute( "name", column.getName( ) );
					if ( column.getType( ) != null )
						columnNode.setAttribute( "type", column.getType( ) );
					if ( column.getSize( ) != null )
						columnNode.setAttribute( "size", column.getSize( ) );

					if ( column.getPrimaryKey( ) != null )
						columnNode.setAttribute( "primaryKey",
								column.getPrimaryKey( ) );
					if ( column.getRequired( ) != null )
						columnNode.setAttribute( "required",
								column.getRequired( ) );
					if ( column.getAutoIncrement( ) != null )
						columnNode.setAttribute( "autoIncrement",
								column.getAutoIncrement( ) );
					if ( column.getDefaultValue( ) != null )
						columnNode.setAttribute( "default",
								column.getDefaultValue( ) );
					if ( column.getDescription( ) != null )
						columnNode.setAttribute( "description",
								column.getDescription( ) );
					tableNode.appendChild( columnNode );
				}
				dlineageRelation.appendChild( tableNode );
			}
		}
	}

	private void appendProcedures( Dlineage dlineage, Element dlineageRelation )
	{
		if ( dlineage.getProcedures( ) != null
				&& !dlineage.getProcedures( ).second.isEmpty( ) )
		{
			for ( int i = 0; i < dlineage.getProcedures( ).second.size( ); i++ )
			{
				ProcedureMetaData procedure = dlineage.getProcedures( ).second.get( i );

				Element procedureNode = dlineageRelation.getOwnerDocument( )
						.createElement( "procedure" );
				procedureNode.setAttribute( "name",
						procedure.getDisplayFullName( ) );

				if ( procedure.isFunction( ) )
				{
					procedureNode.setAttribute( "isFunction",
							procedure.getDisplayFullName( ) );
				}

				if ( procedure.isTrigger( ) )
				{
					procedureNode.setAttribute( "isTrigger",
							procedure.getDisplayFullName( ) );
				}
				dlineageRelation.appendChild( procedureNode );
			}
		}

	}

	private columnImpactResult getColumnImpactResult( String result )
	{
		try
		{
			String[] results = result.split( "\n" );
			StringBuffer buffer = new StringBuffer( );
			for ( int i = 0; i < results.length; i++ )
			{
				String line = results[i];
				if ( line.indexOf( "columnImpactResult" ) != -1
						|| line.indexOf( "targetColumn" ) != -1
						|| line.indexOf( "sourceColumn" ) != -1
						|| line.indexOf( "linkTable" ) != -1 )
				{
					buffer.append( line ).append( "\n" );
				}
			}
			return XML2Model.loadXML( columnImpactResult.class,
					buffer.toString( ) );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	public static void main( String[] args )
	{
		if ( args.length < 1 )
		{
			System.out.println( "Usage: java DlineageRelation [/f <path_to_sql_file>] [/d <path_to_directory_includes_sql_files>] [/t <database type>] [/o <output file path>]" );
			System.out.println( "/f: Option, specify the sql file path to analyze dlineage relation." );
			System.out.println( "/d: Option, specify the sql directory path to analyze dlineage relation." );
			System.out.println( "/t: Option, set the database type. Support oracle, mysql, mssql, db2, netezza, teradata, informix, sybase, postgresql, hive, greenplum and redshift, the default type is oracle" );
			System.out.println( "/o: Option, write the output stream to the specified file." );
			System.out.println( "/log: Option, generate a dlineage.log file to log information." );
			return;
		}

		File sqlFiles = null;

		List<String> argList = Arrays.asList( args );

		if ( argList.indexOf( "/f" ) != -1
				&& argList.size( ) > argList.indexOf( "/f" ) + 1 )
		{
			sqlFiles = new File( args[argList.indexOf( "/f" ) + 1] );
			if ( !sqlFiles.exists( ) || !sqlFiles.isFile( ) )
			{
				System.out.println( sqlFiles + " is not a valid file." );
				return;
			}
		}
		else if ( argList.indexOf( "/d" ) != -1
				&& argList.size( ) > argList.indexOf( "/d" ) + 1 )
		{
			sqlFiles = new File( args[argList.indexOf( "/d" ) + 1] );
			if ( !sqlFiles.exists( ) || !sqlFiles.isDirectory( ) )
			{
				System.out.println( sqlFiles + " is not a valid directory." );
				return;
			}
		}
		else
		{
			System.out.println( "Please specify a sql file path or directory path to analyze dlineage." );
			return;
		}

		EDbVendor vendor = EDbVendor.dbvoracle;

		int index = argList.indexOf( "/t" );

		if ( index != -1 && args.length > index + 1 )
		{
			vendor = TGSqlParser.getDBVendorByName(args[index + 1]);
		}

		String outputFile = null;

		index = argList.indexOf( "/o" );

		if ( index != -1 && args.length > index + 1 )
		{
			outputFile = args[index + 1];
		}

		FileOutputStream writer = null;
		if ( outputFile != null )
		{
			try
			{
				writer = new FileOutputStream( outputFile );
				System.setOut( new PrintStream( writer ) );
			}
			catch ( FileNotFoundException e )
			{
				e.printStackTrace( );
			}
		}

		DlineageRelation relation = new DlineageRelation( );

		Dlineage dlineage = new Dlineage( sqlFiles, vendor, false, false );

		StringBuffer errorBuffer = new StringBuffer( );
		columnImpactResult impactResult = relation.generateColumnImpact( dlineage,
				errorBuffer );
		String result = relation.generateDlineageRelation( dlineage,
				impactResult );

		if ( result != null )
		{
			System.out.println( result );

			if ( writer != null )
			{
				System.err.println( result );
			}
		}

		try
		{
			if ( writer != null )
			{
				writer.close( );
			}
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}

		boolean log = argList.indexOf( "/log" ) != -1;
		PrintStream pw = null;
		ByteArrayOutputStream sw = null;
		PrintStream systemSteam = System.err;

		try
		{
			sw = new ByteArrayOutputStream( );
			pw = new PrintStream( sw );
			System.setErr( pw );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}

		if ( errorBuffer.length( ) > 0 )
		{
			System.err.println( "Error log:\n" + errorBuffer );
		}

		if ( sw != null )
		{
			String errorMessage = sw.toString( ).trim( );
			if ( errorMessage.length( ) > 0 )
			{
				if ( log )
				{
					try
					{
						pw = new PrintStream( new File( ".",
								"dlineageRelation.log" ) );
						pw.print( errorMessage );
					}
					catch ( FileNotFoundException e )
					{
						e.printStackTrace( );
					}
				}

				System.setErr( systemSteam );
				System.err.println( errorMessage );
			}
		}
	}
}
