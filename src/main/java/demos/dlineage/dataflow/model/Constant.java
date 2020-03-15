
package demos.dlineage.dataflow.model;

import demos.dlineage.util.Pair;
import demos.dlineage.util.SQLUtil;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TParseTreeNode;

public class Constant
{

	protected int id;

	protected String fullName;
	protected String name;

	protected Pair<Long, Long> startPosition;
	protected Pair<Long, Long> endPosition;

	protected TParseTreeNode constant;

	public Constant( TParseTreeNode constant )
	{
		if ( constant == null )
			throw new IllegalArgumentException( "Constant arguments can't be null." );

		id = ++ModelBindingManager.get( ).TABLE_COLUMN_ID;

		this.constant = constant;

		TSourceToken startToken = constant.getStartToken( );
		TSourceToken endToken = constant.getEndToken( );

		this.name = constant.toString( );
		
	    this.name = SQLUtil.trimColumnStringQuote(name);

		this.fullName = constant.toString( );

		this.startPosition = new Pair<Long, Long>( startToken.lineNo,
				startToken.columnNo );
		this.endPosition = new Pair<Long, Long>( endToken.lineNo,
				endToken.columnNo + endToken.astext.length( ) );
	}

	public int getId( )
	{
		return id;
	}

	public String getFullName( )
	{
		return fullName;
	}

	public Pair<Long, Long> getStartPosition( )
	{
		return startPosition;
	}

	public Pair<Long, Long> getEndPosition( )
	{
		return endPosition;
	}

	public TParseTreeNode getConstantObject( )
	{
		return constant;
	}

	public String getName( )
	{
		return name;
	}

}
