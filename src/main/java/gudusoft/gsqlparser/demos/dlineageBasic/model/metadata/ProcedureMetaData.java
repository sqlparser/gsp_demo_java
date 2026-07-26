
package demos.dlineageBasic.model.metadata;

import gudusoft.gsqlparser.EDbVendor;

import java.util.LinkedHashMap;

import demos.dlineageBasic.util.SQLUtil;

public class ProcedureMetaData extends LinkedHashMap<String, Object>
{

	private static final long serialVersionUID = -6794926656804029259L;
	private static final String PROP_NAME = "name";
	private static final String PROP_CATALOGNAME = "catalogName";
	private static final String PROP_SCHEMANAME = "schemaName";

	private String name;
	private String schemaName;
	private String catalogName;

	private String displayName;
	private String catalogDisplayName;
	private String schemaDisplayName;

	private boolean isFunction = false;
	private boolean isTrigger = false;

	private boolean strict = false;

	private EDbVendor vendor = EDbVendor.dbvmssql;

	public ProcedureMetaData( EDbVendor vendor, boolean strict )
	{
		this.vendor = vendor;
		this.strict = strict;
		this.isFunction = false;
		this.put( PROP_CATALOGNAME, "" );
		this.put( PROP_SCHEMANAME, "" );
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

		if ( !( obj instanceof ProcedureMetaData ) )
			return false;

		ProcedureMetaData other = (ProcedureMetaData) obj;

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

	public boolean isFunction( )
	{
		return isFunction;
	}

	public void setFunction( boolean isFunction )
	{
		this.isFunction = isFunction;
	}

	public boolean isStrict( )
	{
		return strict;
	}

	public EDbVendor getVendor( )
	{
		return vendor;
	}

	public boolean isTrigger( )
	{
		return isTrigger;
	}

	public void setTrigger( boolean isTrigger )
	{
		this.isTrigger = isTrigger;
	}

}
