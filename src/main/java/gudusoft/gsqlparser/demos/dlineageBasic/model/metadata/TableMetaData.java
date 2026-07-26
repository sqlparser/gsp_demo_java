
package demos.dlineageBasic.model.metadata;

import gudusoft.gsqlparser.EDbVendor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import demos.dlineageBasic.model.ddl.schema.foreignKey;
import demos.dlineageBasic.model.ddl.schema.index;
import demos.dlineageBasic.model.ddl.schema.unique;
import demos.dlineageBasic.util.SQLUtil;

@SuppressWarnings("serial")
public class TableMetaData extends HashMap<String, Object>
{

	private static final String PROP_NAME = "name";
	private static final String PROP_CATALOGNAME = "catalogName";
	private static final String PROP_SCHEMANAME = "schemaName";
	private static final String PROP_COMMENT = "comment";

	private String name;
	private String schemaName;
	private String catalogName;

	private String displayName;
	private String catalogDisplayName;
	private String schemaDisplayName;

	private String parent;

	private boolean isView = false;

	private List<foreignKey> foreignKeys = new ArrayList<foreignKey>( );

	private List<index> indices = new ArrayList<index>( );

	private List<unique> uniques = new ArrayList<unique>( );

	private boolean strict = false;

	private EDbVendor vendor = EDbVendor.dbvmssql;

	public TableMetaData( EDbVendor vendor, boolean strict )
	{
		this.vendor = vendor;
		this.strict = strict;
		this.isView = false;
		this.put( PROP_CATALOGNAME, "" );
		this.put( PROP_SCHEMANAME, "" );
		this.put( PROP_COMMENT, "" );
	}

	public void setName( String name )
	{
		if ( SQLUtil.isEmpty( name ) )
			return;
		displayName = name;
		name = SQLUtil.trimColumnStringQuote( name );
		this.name = name;
		if ( name != null )
			this.put( PROP_NAME, name );
	}

	public void setCatalogName( String catalogName )
	{
		if ( SQLUtil.isEmpty( catalogName ) )
			return;
		catalogDisplayName = catalogName;
		catalogName = SQLUtil.trimColumnStringQuote( catalogName );
		this.catalogName = catalogName;
		if ( catalogName != null )
			this.put( PROP_CATALOGNAME, catalogName );
	}

	public void setSchemaName( String schemaName )
	{
		if ( SQLUtil.isEmpty( schemaName ) )
			return;
		if ( EDbVendor.dbvmysql == vendor )
		{
			setCatalogName( schemaName );
		}
		else
		{
			schemaDisplayName = schemaName;
			schemaName = SQLUtil.trimColumnStringQuote( schemaName );
			this.schemaName = schemaName;
			if ( schemaName != null )
				this.put( PROP_SCHEMANAME, schemaName );
		}
	}

	public String getDisplayName( )
	{
		return displayName;
	}

	public void setDisplayName( String displayName )
	{
		this.displayName = displayName;
	}

	public String getCatalogDisplayName( )
	{
		return catalogDisplayName;
	}

	public void setCatalogDisplayName( String catalogDisplayName )
	{
		this.catalogDisplayName = catalogDisplayName;
	}

	public String getSchemaDisplayName( )
	{
		return schemaDisplayName;
	}

	public void setSchemaDisplayName( String schemaDisplayName )
	{
		this.schemaDisplayName = schemaDisplayName;
	}

	public void setComment( String comment )
	{
		if ( schemaName != null )
			this.put( PROP_COMMENT, comment );
	}

	public String getName( )
	{
		return (String) this.get( PROP_NAME );
	}

	public String getCatalogName( )
	{
		return (String) this.get( PROP_CATALOGNAME );
	}

	public String getSchemaName( )
	{
		return (String) this.get( PROP_SCHEMANAME );
	}

	public String getComment( )
	{
		return (String) this.get( PROP_COMMENT );
	}

	public String getParent( )
	{
		return parent;
	}

	public void setParent( String parent )
	{
		this.parent = parent;
	}

	@Override
	public int hashCode( )
	{
		final int prime = 31;
		int result = 0;
		result = prime * result + ( ( name == null ) ? 0 : name.hashCode( ) );
		if ( strict )
		{
			result = prime
					* result
					+ ( ( catalogName == null ) ? 0 : catalogName.hashCode( ) );
			result = prime
					* result
					+ ( ( schemaName == null ) ? 0 : schemaName.hashCode( ) );
		}
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;

		if ( !( obj instanceof TableMetaData ) )
			return false;

		TableMetaData other = (TableMetaData) obj;

		if ( strict )
		{
			if ( catalogName == null )
			{
				if ( other.catalogName != null )
					return false;
			}
			else if ( !catalogName.equals( other.catalogName ) )
				return false;

			if ( schemaName == null )
			{
				if ( other.schemaName != null )
					return false;
			}
			else if ( !schemaName.equals( other.schemaName ) )
				return false;
		}

		if ( catalogName != null
				&& other.schemaName != null
				&& !catalogName.equals( other.catalogName ) )
		{
			return false;
		}

		if ( schemaName != null
				&& other.schemaName != null
				&& !schemaName.equals( other.schemaName ) )
		{
			return false;
		}

		if ( name == null )
		{
			if ( other.name != null )
				return false;
		}
		else if ( !name.equals( other.name ) )
			return false;

		return true;
	}

	public String getFullName( )
	{
		String fullName = name;
		if ( schemaName != null )
			fullName = schemaName + "." + fullName;
		if ( catalogName != null )
			fullName = catalogName + "." + fullName;
		return fullName;
	}

	public String getDisplayFullName( )
	{
		String fullName = displayName;
		if ( schemaDisplayName != null )
			fullName = schemaDisplayName + "." + fullName;
		if ( catalogDisplayName != null )
			fullName = catalogDisplayName + "." + fullName;
		return fullName;
	}

	public List<foreignKey> getForeignKeys( )
	{
		return foreignKeys;
	}

	public List<index> getIndices( )
	{
		return indices;
	}

	public List<unique> getUniques( )
	{
		return uniques;
	}

	public boolean isView( )
	{
		return isView;
	}

	public void setView( boolean isView )
	{
		this.isView = isView;
	}
}