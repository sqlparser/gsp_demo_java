
package demos.dlineage.dataflow.model;

import demos.dlineage.util.Pair3;
import demos.dlineage.util.SQLUtil;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TParseTreeNode;

public class Constant
{

	protected int id;

	protected String fullName;
	protected String name;

	protected Pair3<Long, Long, String> startPosition;
	protected Pair3<Long, Long, String> endPosition;

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

		this.startPosition = new Pair3<Long, Long, String>( startToken.lineNo,
				startToken.columnNo, ModelBindingManager.getGlobalHash());
		this.endPosition = new Pair3<Long, Long, String>( endToken.lineNo,
				endToken.columnNo + endToken.astext.length( ), ModelBindingManager.getGlobalHash());
	}

	public int getId( )
	{
		return id;
	}

	public String getFullName( )
	{
		return fullName;
	}

	public Pair3<Long, Long, String> getStartPosition( )
	{
		return startPosition;
	}

	public Pair3<Long, Long, String> getEndPosition( )
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
