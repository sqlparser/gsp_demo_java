
package demos.antiSQLInjection;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TMultiTarget;
import gudusoft.gsqlparser.nodes.TMultiTargetList;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;

import java.util.ArrayList;

/**
 * This is the classed used to check sql injection, it can detect following type
 * of sql injection
 * <p>
 * <ul>
 * <li>syntax error</li>
 * <li>always_true_condition</li>
 * <li>always_false_condition</li>
 * <li>comment_at_the_end_of_statement</li>
 * <li>stacking_queries</li>
 * <li>not_in_allowed_statement</li>
 * <li>union_set</li>
 * </uL>
 * <p>
 */

public class TAntiSQLInjection
{

	private TGSqlParser sqlParser = null;
	private String sqlText = null;
	private ArrayList<TSQLInjection> sqlInjections = null;
	private ArrayList<ESqlStatementType> enabledStatements = null;

	private boolean e_always_true_condition = true;
	private boolean e_always_false_condition = true;
	private boolean e_comment_at_the_end_of_statement = true;
	private boolean e_stacking_queries = true;
	private boolean e_not_in_allowed_statement = true;
	private boolean e_union_set = true;
	private boolean e_piggybacked_statement = true;
	private boolean e_syntax_error = true;

	/**
	 * turn on/off the check of ESQLInjectionType.union_set default is on
	 * 
	 * @param on
	 */
	public void check_union_set( boolean on )
	{
		this.e_union_set = on;
	}

	/**
	 * turn on/off the check of ESQLInjectionType.not_in_allowed_statement
	 * default is on
	 * 
	 * @param on
	 */
	public void check_not_in_allowed_statement( boolean on )
	{
		this.e_not_in_allowed_statement = on;
	}

	/**
	 * turn on/off the check of ESQLInjectionType.stacking_queries default is on
	 * 
	 * @param on
	 */
	public void check_stacking_queries( boolean on )
	{
		this.e_stacking_queries = on;
	}

	/**
	 * turn on/off the check of
	 * ESQLInjectionType.comment_at_the_end_of_statement default is on
	 * 
	 * @param on
	 */
	public void check_comment_at_the_end_of_statement( boolean on )
	{
		this.e_comment_at_the_end_of_statement = on;
	}

	/**
	 * turn on/off the check of ESQLInjectionType.always_false_condition default
	 * is on
	 * 
	 * @param on
	 */
	public void check_always_false_condition( boolean on )
	{
		this.e_always_false_condition = on;
	}

	/**
	 * turn on/off the check of ESQLInjectionType.always_true_condition default
	 * is on
	 * 
	 * @param on
	 */
	public void check_always_true_condition( boolean on )
	{
		this.e_always_true_condition = on;
	}

	/**
	 * turn on/off the check of ESQLInjectionType.piggybacked_statement default
	 * is on
	 * 
	 * @param on
	 */
	public void check_piggybacked_statement( boolean on )
	{
		this.e_piggybacked_statement = on;
	}
	
	/**
	 * turn on/off the check of ESQLInjectionType.piggybacked_statement default
	 * is on
	 * 
	 * @param on
	 */
	public void check_syntax_error( boolean on )
	{
		this.e_syntax_error = on;
	}

	public ArrayList<TSQLInjection> getSqlInjections( )
	{
		if ( this.sqlInjections == null )
		{
			this.sqlInjections = new ArrayList<TSQLInjection>( );
		}
		return sqlInjections;
	}

	public TAntiSQLInjection( EDbVendor dbVendor )
	{
		this.sqlParser = new TGSqlParser( dbVendor );
		this.enabledStatements = new ArrayList<ESqlStatementType>( );
		this.enabledStatements.add( ESqlStatementType.sstselect );
	}

	/**
	 * add a type of sql statement that allowed to be executed in database.
	 * 
	 * @param sqltype
	 */
	public void enableStatement( ESqlStatementType sqltype )
	{
		this.enabledStatements.add( sqltype );
	}

	/**
	 * get a list of sql statement type that allowed to be executed in database.
	 * 
	 * @return
	 */
	public ArrayList<ESqlStatementType> getEnabledStatements( )
	{
		return enabledStatements;
	}

	/**
	 * disable a type of sql statement that allowed to be executed in database.
	 * 
	 * @param sqltype
	 */
	public void disableStatement( ESqlStatementType sqltype )
	{
		for ( int i = this.enabledStatements.size( ) - 1; i >= 0; i-- )
		{
			if ( this.enabledStatements.get( i ) == sqltype )
			{
				this.enabledStatements.remove( i );
			}
		}
	}

	/**
	 * Check is sql was injected or not.
	 * 
	 * @param sql
	 * @return if return true, use this.getSqlInjections() to get detailed
	 *         information about sql injection.
	 */
	public boolean isInjected( String sql )
	{
		boolean ret = false;
		this.sqlText = sql;
		this.sqlParser.sqltext = this.sqlText;
		this.getSqlInjections( ).clear( );
		int i = this.sqlParser.parse( );
		if ( i == 0 )
		{
			ret = ret | isInjected_always_false_condition( );
			ret = ret | isInjected_always_true_condition( );
			ret = ret | isInjected_comment_at_the_end_statement( );
			ret = ret | isInjected_stacking_queries( );
			ret = ret | isInjected_allowed_statement( );
			ret = ret | isInjected_union_set( );
			ret = ret | isInjected_piggybacked_statement( );
		}
		else if(e_syntax_error)
		{
			TSQLInjection s = new TSQLInjection( ESQLInjectionType.syntax_error );
			s.setDescription( this.sqlParser.getErrormessage( ) );
			this.getSqlInjections( ).add( s );
			ret = true;
		}

		return ret;
	}

	private boolean isInjected_always_true_condition( )
	{
		boolean ret = false;
		if ( !this.e_always_true_condition )
		{
			return false;
		}
		if ( this.sqlParser.sqlstatements.size( ) == 0 )
		{
			return ret;
		}
		if ( this.sqlParser.sqlstatements.get( 0 ).getWhereClause( ) != null )
		{
			TExpression condition = this.sqlParser.sqlstatements.get( 0 )
					.getWhereClause( )
					.getCondition( );
			GEval e = new GEval( );
			Object t = e.value( condition, null );
			if ( t instanceof Boolean )
			{
				if ( ( (Boolean) t ).booleanValue( ) == Boolean.TRUE )
				{
					this.getSqlInjections( )
							.add( new TSQLInjection( ESQLInjectionType.always_true_condition ) );
					ret = true;
				}
			}
		}
		return ret;
	}

	private boolean isInjected_always_false_condition( )
	{
		boolean ret = false;
		if ( !this.e_always_false_condition )
		{
			return false;
		}
		if ( this.sqlParser.sqlstatements.size( ) == 0 )
		{
			return ret;
		}
		if ( this.sqlParser.sqlstatements.get( 0 ).getWhereClause( ) != null )
		{
			TExpression condition = this.sqlParser.sqlstatements.get( 0 )
					.getWhereClause( )
					.getCondition( );
			GEval e = new GEval( );
			Object t = e.value( condition, null );
			if ( t instanceof Boolean )
			{
				if ( ( (Boolean) t ).booleanValue( ) == Boolean.FALSE )
				{
					this.getSqlInjections( )
							.add( new TSQLInjection( ESQLInjectionType.always_false_condition ) );
					ret = true;
				}
			}
		}
		return ret;
	}

	private boolean isInjected_comment_at_the_end_statement( )
	{
		boolean ret = false;
		if ( !this.e_comment_at_the_end_of_statement )
		{
			return false;
		}
		TSourceToken st = this.sqlParser.sourcetokenlist.get( this.sqlParser.sourcetokenlist.size( ) - 1 );
		if ( ( st.tokencode == TBaseType.cmtdoublehyphen )
				|| ( st.tokencode == TBaseType.cmtslashstar ) )
		{
			this.getSqlInjections( )
					.add( new TSQLInjection( ESQLInjectionType.comment_at_the_end_of_statement ) );
			ret = true;
		}
		return ret;
	}

	private boolean isInjected_stacking_queries( )
	{
		boolean ret = false;
		if ( !this.e_stacking_queries )
		{
			return false;
		}
		if ( this.sqlParser.sqlstatements.size( ) > 1 )
		{
			this.getSqlInjections( )
					.add( new TSQLInjection( ESQLInjectionType.stacking_queries ) );
			ret = true;
		}
		return ret;
	}

	private boolean isInjected_allowed_statement( )
	{
		boolean ret = false;
		if ( !this.e_not_in_allowed_statement )
		{
			return false;
		}
		for ( int j = 0; j < this.sqlParser.sqlstatements.size( ); j++ )
		{
			if ( !this.isAllowedStatement( this.sqlParser.sqlstatements.get( j ).sqlstatementtype ) )
			{

				TSQLInjection s = new TSQLInjection( ESQLInjectionType.not_in_allowed_statement );
				s.setDescription( this.sqlParser.sqlstatements.get( j ).sqlstatementtype.toString( ) );
				this.getSqlInjections( ).add( s );

				ret = ret | true;
			};

		}
		return ret;
	}

	private boolean isInjected_piggybacked_statement( )
	{
		boolean ret = false;
		if ( !this.e_piggybacked_statement )
		{
			return false;
		}
		if ( this.sqlParser.sqlstatements.size( ) == 0 )
		{
			return ret;
		}
		if ( this.sqlParser.getDbVendor( ) != EDbVendor.dbvmysql )
		{
			return ret;
		}
		if ( this.sqlParser.sqlstatements.get( 0 ) instanceof TInsertSqlStatement )
		{
			TMultiTargetList values = ( (TInsertSqlStatement) this.sqlParser.sqlstatements.get( 0 ) ).getValues( );
			if ( values != null )
			{
				for ( int i = 0; i < values.size( ); i++ )
				{
					TMultiTarget target = values.getMultiTarget( i );
					if ( target == null )
						continue;
					TResultColumnList columns = target.getColumnList( );
					for ( int j = 0; j < columns.size( ); j++ )
					{
						TExpression expression = columns.getResultColumn( j )
								.getExpr( );
						if ( expression.getExpressionType( ) == EExpressionType.contains_t )
							continue;
						if ( isInjectedPiggybackedExpression( expression ) )
						{
							this.getSqlInjections( )
									.add( new TSQLInjection( ESQLInjectionType.piggybacked_statement ) );
							return true;
						}
					}
				}
			}
		}
		else if ( this.sqlParser.sqlstatements.get( 0 ) instanceof TUpdateSqlStatement )
		{
			TResultColumnList columns = ( (TUpdateSqlStatement) this.sqlParser.sqlstatements.get( 0 ) ).getResultColumnList( );
			for ( int j = 0; j < columns.size( ); j++ )
			{
				TExpression expression = columns.getResultColumn( j ).getExpr( );
				if ( expression.getExpressionType( ) == EExpressionType.assignment_t )
				{
					if ( isInjectedPiggybackedExpression( expression.getRightOperand( ) ) )
					{
						this.getSqlInjections( )
								.add( new TSQLInjection( ESQLInjectionType.piggybacked_statement ) );
						return true;
					}
				}
			}
		}
		else if ( this.sqlParser.sqlstatements.get( 0 ).getWhereClause( ) != null )
		{
			if ( isInjectedPiggybackedExpression( this.sqlParser.sqlstatements.get( 0 )
					.getWhereClause( )
					.getCondition( ) ) )
			{
				this.getSqlInjections( )
						.add( new TSQLInjection( ESQLInjectionType.piggybacked_statement ) );
				return true;
			}
		}
		return ret;
	}

	class piggybackedExpr implements IExpressionVisitor
	{

		private boolean piggybacked = false;

		public boolean exprVisit( TParseTreeNode pNode, boolean isLeafNode )
		{
			if ( pNode instanceof TExpression )
			{
				TExpression expr = (TExpression) pNode;
				if ( ( expr.toString( ).matches( "'\\s*'" ) || expr.toString( )
						.matches( "\"\\s*\"" ) )
						&& ( expr.getParentExpr( ) != null && isLogicExpression( expr.getParentExpr( ) ) ) )
				{
					piggybacked = true;
				}
			}
			return true;
		}

		private boolean isLogicExpression( TExpression expression )
		{
			EExpressionType exprType = expression.getExpressionType( );
			return exprType == EExpressionType.logical_and_t
					|| exprType == EExpressionType.logical_not_t
					|| exprType == EExpressionType.logical_or_t
					|| exprType == EExpressionType.logical_t
					|| exprType == EExpressionType.logical_xor_t;
		}

		public piggybackedExpr checkExpression( TExpression expression )
		{
			expression.inOrderTraverse( this );
			return this;
		}

		public boolean isPiggybacked( )
		{
			return piggybacked;
		}

	}

	private boolean isInjectedPiggybackedExpression( TExpression expression )
	{
		return new piggybackedExpr( ).checkExpression( expression )
				.isPiggybacked( );
	}

	private boolean isInjected_union_set( )
	{
		boolean ret = false;
		if ( !this.e_union_set )
		{
			return false;
		}
		if ( this.sqlParser.sqlstatements.size( ) == 0 )
		{
			return ret;
		}
		TCustomSqlStatement stmt = this.sqlParser.sqlstatements.get( 0 );
		if ( stmt.sqlstatementtype != ESqlStatementType.sstselect )
		{
			return ret;
		}
		TSelectSqlStatement select = (TSelectSqlStatement) stmt;
		if ( select.isCombinedQuery( ) )
		{
			this.getSqlInjections( )
					.add( new TSQLInjection( ESQLInjectionType.union_set ) );
			ret = true;
		}
		return ret;
	}

	private boolean isAllowedStatement( ESqlStatementType pType )
	{
		boolean ret = false;
		for ( int i = 0; i < this.enabledStatements.size( ); i++ )
		{
			if ( this.enabledStatements.get( i ) == pType )
			{
				ret = true;
				break;
			}
		}
		return ret;
	}

}
