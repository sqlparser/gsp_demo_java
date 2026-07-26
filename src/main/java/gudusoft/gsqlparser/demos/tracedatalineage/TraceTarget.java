
package demos.tracedatalineage;

import java.util.ArrayList;
import java.util.List;

public class TraceTarget
{

	private TraceTarget parent;

	private Object source;

	private List<TraceTarget> target = new ArrayList<TraceTarget>( );

	void addTarget( TraceTarget model )
	{
		model.setParent( this );
		this.target.add( model );
	}

	boolean containsTarget( Object source )
	{
		if ( this.target == null )
			return false;
		for ( int i = 0; i < this.target.size( ); i++ )
		{
			if ( this.target.get( i ).source.equals( source ) )
				return true;
		}
		return false;
	}

	public TraceTarget getParent( )
	{
		return parent;
	}

	Object getSource( )
	{
		return source;
	}

	List<TraceTarget> getTarget( )
	{
		return target;
	}

	TraceTarget getTarget( Object source )
	{
		if ( this.target == null )
			return null;
		for ( int i = 0; i < this.target.size( ); i++ )
		{
			if ( this.target.get( i ).source.equals( source ) )
				return this.target.get( i );
		}
		return null;
	}

	void setParent( TraceTarget parent )
	{
		this.parent = parent;
	}

	void setSource( Object source )
	{
		this.source = source;
	}

}
