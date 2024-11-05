
package demos.dlineageBasic.model.metadata;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import demos.dlineageBasic.util.SQLUtil;

@SuppressWarnings("serial")
public class ColumnMetaData extends LinkedHashMap<String, Object>
{

	private static final String PROP_AUTOINCREMENT = "autoIncrement";
	private static final String PROP_TYPENAME = "typeName";
	private static final String PROP_TYPECODE = "typeCode";
	public static final String PROP_TABLENAME = "tableName";
	private static final String PROP_COLUMNDISPLAYSIZE = "columnDisplaySize";
	private static final String PROP_ALIAS = "alias";
	public static final String PROP_CATALOGNAME = "catalogName";
	private static final String PROP_PRECISION = "precision";
	private static final String PROP_SCALE = "scale";
	public static final String PROP_SCHEMANAME = "schemaName";
	private static final String PROP_READONLY = "readOnly";
	private static final String PROP_WRITEABLE = "writeable";
	private static final String PROP_COMMENT = "comment";
	private static final String PROP_NULL = "isNull";
	private static final String PROP_DEFAULTVALUE = "defaultValue";
	private static final String PROP_PRIMARYKEY = "primaryKey";
	private static final String PROP_INDEX = "index";
	private static final String PROP_NOTNULL = "isNotNull";
	private static final String PROP_UNIQUE = "unique";
	private static final String PROP_FOREIGNKEY = "foreignKey";
	private static final String PROP_CHECK = "check";

	private TableMetaData table;
	private List<ColumnMetaData> referColumns = new ArrayList<ColumnMetaData>( );
	private String name;
	private String displayName;

	private String isOrphan;

	public ColumnMetaData( )
	{
		this.put( PROP_ALIAS, "" );
		this.put( PROP_TYPENAME, "" );
		this.put( PROP_TYPECODE, "" );
		this.put( PROP_COLUMNDISPLAYSIZE, "" );
		this.put( PROP_PRECISION, "" );
		this.put( PROP_SCALE, "" );
		this.put( PROP_AUTOINCREMENT, "" );
		this.put( PROP_READONLY, "" );
		this.put( PROP_WRITEABLE, "" );
		this.put( PROP_COMMENT, "" );
		this.put( PROP_NULL, "" );
		this.put( PROP_NOTNULL, "" );
		this.put( PROP_DEFAULTVALUE, "" );
		this.put( PROP_UNIQUE, "" );
		this.put( PROP_CHECK, "" );
		this.put( PROP_PRIMARYKEY, "" );
		this.put( PROP_INDEX, "" );
		this.put( PROP_FOREIGNKEY, "" );
	}

	@Override
	public int hashCode( )
	{
		final int prime = 31;
		int result = prime + ( ( name == null ) ? 0 : name.hashCode( ) );
		result = prime * result + ( ( table == null ) ? 0 : table.hashCode( ) );
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( !( obj instanceof ColumnMetaData ) )
			return false;
		ColumnMetaData other = (ColumnMetaData) obj;
		if ( name == null )
		{
			if ( other.name != null )
				return false;
		}
		else if ( !name.equals( other.name ) )
			return false;
		if ( table == null )
		{
			if ( other.table != null )
				return false;
		}
		else if ( !table.equals( other.table ) )
			return false;
		return true;
	}

	public void setTable( TableMetaData table )
	{
		this.table = table;
	}

	public TableMetaData getTable( )
	{
		return table;
	}

	public String getDisplayName( )
	{
		return displayName;
	}

	public void setDisplayName( String displayName )
	{
		this.displayName = displayName;
	}

	public ColumnMetaData[] getReferColumns( )
	{
		return referColumns.toArray( new ColumnMetaData[0] );
	}

	public void setReferColumns( ColumnMetaData[] referColumns )
	{
		if ( referColumns != null )
		{
			for ( int i = 0; i < referColumns.length; i++ )
			{
				addReferColumn( referColumns[i] );
			}
		}
	}

	public void addReferColumn( ColumnMetaData columnMetaData )
	{
		if ( !referColumns.contains( columnMetaData ) )
			referColumns.add( columnMetaData );
	}

	public String getName( )
	{
		return name;
	}

	public void setName( String name )
	{
		if ( SQLUtil.isEmpty( name ) )
			return;
		displayName = name;
		name = SQLUtil.trimColumnStringQuote( name );
		this.name = name;
	}

	public void setAutoIncrement( boolean autoIncrement )
	{
		this.put( PROP_AUTOINCREMENT, autoIncrement );
	}

	public void setTypeName( String typeName )
	{
		this.put( PROP_TYPENAME, typeName );
	}

	public void setTypeCode( int typeCode )
	{
		this.put( PROP_TYPECODE, typeCode );
	}

	public void setColumnDisplaySize( String columnDisplaySize )
	{
		this.put( PROP_COLUMNDISPLAYSIZE, columnDisplaySize );
	}

	public void setPrecision( int precision )
	{
		this.put( PROP_PRECISION, precision );
	}

	public void setScale( int scale )
	{
		this.put( PROP_SCALE, scale );
	}

	public void setWriteable( boolean writeable )
	{
		this.put( PROP_WRITEABLE, writeable );
	}

	public void setReadOnly( boolean readOnly )
	{
		this.put( PROP_READONLY, readOnly );
	}

	public void setComment( String comment )
	{
		this.put( PROP_COMMENT, comment );
	}

	public void setNull( boolean isNull )
	{
		this.put( PROP_NULL, isNull );
	}

	public void setPrimaryKey( boolean primaryKey )
	{
		this.put( PROP_PRIMARYKEY, primaryKey );
	}

	public void setIndex( boolean index )
	{
		this.put( PROP_INDEX, index );
	}

	public void setUnique( boolean unique )
	{
		this.put( PROP_UNIQUE, unique );
	}

	public void setNotNull( boolean notNull )
	{
		this.put( PROP_NOTNULL, notNull );
	}

	public void setDefaultVlaue( String defaultValue )
	{
		this.put( PROP_DEFAULTVALUE, defaultValue );
	}

	public void setCheck( boolean check )
	{
		this.put( PROP_CHECK, check );
	}

	public void setForeignKey( boolean foreignKey )
	{
		this.put( PROP_FOREIGNKEY, foreignKey );
	}

	public String getFullName( )
	{
		if ( getTable( ) != null )
			return getTable( ).getFullName( ) + "." + name;
		return name;
	}

	public String getDisplayFullName( )
	{
		if ( getTable( ) != null )
			return getTable( ).getDisplayFullName( ) + "." + displayName;
		return name;
	}

	public String getType( )
	{
		if ( this.get( PROP_TYPENAME ) != null
				&& String.valueOf( this.get( PROP_TYPENAME ) ).length( ) > 0 )
			return (String) this.get( PROP_TYPENAME );
		return null;
	}

	public String getSize( )
	{
		if ( this.get( PROP_COLUMNDISPLAYSIZE ) != null
				&& String.valueOf( this.get( PROP_COLUMNDISPLAYSIZE ) )
						.length( ) > 0 )
			return String.valueOf( this.get( PROP_COLUMNDISPLAYSIZE ) );
		else
			return null;
	}

	public String isPrimaryKey( )
	{
		if ( this.get( PROP_PRIMARYKEY ) != null
				&& String.valueOf( this.get( PROP_PRIMARYKEY ) ).length( ) > 0 )
			return String.valueOf( this.get( PROP_PRIMARYKEY ) );
		else
			return null;
	}

	public String getComment( )
	{
		if ( this.get( PROP_COMMENT ) != null
				&& String.valueOf( this.get( PROP_COMMENT ) ).length( ) > 0 )
			return (String) this.get( PROP_COMMENT );
		else
			return null;
	}

	public String getDefaultValue( )
	{
		if ( this.get( PROP_DEFAULTVALUE ) != null
				&& String.valueOf( this.get( PROP_DEFAULTVALUE ) ).length( ) > 0 )
			return String.valueOf( this.get( PROP_DEFAULTVALUE ) );
		else
			return null;
	}

	public String isRequired( )
	{
		if ( this.get( PROP_NOTNULL ) != null
				&& String.valueOf( this.get( PROP_NOTNULL ) ).length( ) > 0
				&& Boolean.valueOf( String.valueOf( this.get( PROP_NOTNULL ) ) ) )
			return String.valueOf( this.get( PROP_NOTNULL ) );
		else
			return null;
	}

	public String isAutoIncrease( )
	{
		if ( this.get( PROP_AUTOINCREMENT ) != null
				&& String.valueOf( this.get( PROP_AUTOINCREMENT ) ).length( ) > 0
				&& Boolean.valueOf( String.valueOf( this.get( PROP_AUTOINCREMENT ) ) ) )
			return String.valueOf( this.get( PROP_AUTOINCREMENT ) );
		else
			return null;
	}

	public boolean isOrphan( )
	{
		if ( isOrphan == null )
			return false;
		else
			return Boolean.parseBoolean( isOrphan );
	}

	public void setOrphan( String isOrphan )
	{
		if ( "false".equals( this.isOrphan ) || isOrphan == null )
			return;
		this.isOrphan = isOrphan;
	}

}