
package demos.dlineageBasic.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import demos.dlineageBasic.model.ddl.schema.column;
import demos.dlineageBasic.model.ddl.schema.database;
import demos.dlineageBasic.model.ddl.schema.table;
import demos.dlineageBasic.model.metadata.ColumnMetaData;
import demos.dlineageBasic.model.metadata.TableMetaData;

public class DDLSchema
{

	private Serializer serializer = new Persister( );
	private String result;
	private database[] databases;

	public String getSchemaXML( )
	{
		return result;
	}

	public database[] getDataMetaInfos( )
	{
		return databases;
	}

	private Map<TableMetaData, List<ColumnMetaData>> tableColumns;

	public DDLSchema( Map<TableMetaData, List<ColumnMetaData>> tableColumns)
	{
		this.tableColumns = tableColumns;
		databases = collectDDLInfo( );
		if ( databases != null )
		{
			try
			{
				StringWriter sw = new StringWriter( );
				PrintWriter writer = new PrintWriter( sw );
				writer.println( "<?xml version=\"1.0\"?>" );
				writer.println( "<!DOCTYPE database SYSTEM \"http://db.apache.org/torque/dtd/database.dtd\">" );
				if ( databases.length > 1 )
				{
					writer.println( "<dbSchema>" );
				}

				writer.print( getDatabaseSchema( databases ) );

				if ( databases.length > 1 )
				{
					writer.println( "</dbSchema>" );
				}
				result = sw.toString( );
				writer.close( );
			}
			catch ( Exception e )
			{
				e.printStackTrace( );
			}
		}
		if ( result == null )
		{
			result = "";
		}
	}

	private String getDatabaseSchema( database[] databases ) throws IOException
	{
		StringWriter sw = new StringWriter( );
		PrintWriter writer = new PrintWriter( sw );
		for ( int i = 0; i < databases.length; i++ )
		{
			database db = databases[i];
			try
			{
				serializer.write( db, writer );
				writer.println( );
			}
			catch ( Exception e )
			{
				e.printStackTrace( );
			}
		}
		String schema = sw.toString( );
		writer.close( );

		if ( databases.length > 1 )
		{
			sw = new StringWriter( );
			writer = new PrintWriter( sw );
			BufferedReader reader = new BufferedReader( new StringReader( schema ) );
			String line = null;
			while ( ( line = reader.readLine( ) ) != null )
			{
				writer.print( "   " );
				writer.println( line );
			}
			schema = sw.toString( );
			writer.close( );
		}
		return schema;
	}

	private database[] collectDDLInfo( )
	{
		Map<String, database> databaseMap = new LinkedHashMap<String, database>( );
		if ( tableColumns != null )
		{
			Iterator<TableMetaData> tableIter = tableColumns.keySet( )
					.iterator( );
			while ( tableIter.hasNext( ) )
			{
				TableMetaData tableMetadata = tableIter.next( );

				String databaseName = tableMetadata.getCatalogDisplayName( );
				if ( databaseName == null )
				{
					databaseName = "unknown";
				}
				if ( !databaseMap.containsKey( databaseName ) )
				{
					database datasource = new database( );
					datasource.setName( databaseName );
					databaseMap.put( databaseName, datasource );
				}

				database datasource = databaseMap.get( databaseName );
				table table = new table( );
				if ( tableMetadata.getSchemaDisplayName( ) != null )
					table.setName( tableMetadata.getSchemaDisplayName( )
							+ "."
							+ tableMetadata.getDisplayName( ) );
				else
					table.setName( tableMetadata.getDisplayName( ) );

				if ( tableMetadata.getParent() !=null )
				{
					table.setParent( tableMetadata.getParent() );
				}
				
				if ( tableMetadata.isView( ) )
				{
					table.setIsView( String.valueOf( Boolean.TRUE ) );
				}
				if ( tableMetadata.getIndices( ) != null
						&& tableMetadata.getIndices( ).size( ) > 0 )
				{
					table.setIndices( tableMetadata.getIndices( ) );
				}
				if ( tableMetadata.getUniques( ) != null
						&& tableMetadata.getUniques( ).size( ) > 0 )
				{
					table.setUniques( tableMetadata.getUniques( ) );
				}
				if ( tableMetadata.getForeignKeys( ) != null
						&& tableMetadata.getForeignKeys( ).size( ) > 0 )
				{
					table.setForeignKeys( tableMetadata.getForeignKeys( ) );
				}
				datasource.getTables( ).add( table );
				Collections.sort( datasource.getTables( ) );

				List<ColumnMetaData> columnMetadatas = tableColumns.get( tableMetadata );
				if ( columnMetadatas != null )
				{
					for ( int i = 0; i < columnMetadatas.size( ); i++ )
					{
						ColumnMetaData columnMetadata = columnMetadatas.get( i );
						column column = new column( );
						column.setName( columnMetadata.getDisplayName( ) );
						column.setType( columnMetadata.getType( ) );
						column.setSize( columnMetadata.getSize( ) );
						column.setPrimaryKey( columnMetadata.isPrimaryKey( ) );
						column.setDescription( columnMetadata.getComment( ) );
						column.setDefaultValue( columnMetadata.getDefaultValue( ) );
						column.setRequired( columnMetadata.isRequired( ) );
						column.setAutoIncrement( columnMetadata.isAutoIncrease( ) );
						table.getColumns( ).add( column );
					}
				}
			}
		}
		if ( databaseMap.values( ) != null )
			return databaseMap.values( ).toArray( new database[0] );
		else
			return null;
	}
}
