package demos.removeSpecialConditions;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.TSourceTokenList;
import gudusoft.gsqlparser.nodes.TCTE;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinItem;
import gudusoft.gsqlparser.nodes.TJoinList;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TTableList;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveCondition
{

	private String result;

	private StringBuffer conditionBuffer = new StringBuffer( );
	private StringBuffer trimBuffer = new StringBuffer( );
	private StringBuffer suffixBuffer = new StringBuffer( );
	private StringBuffer prefixBuffer = new StringBuffer( );
	private StringBuffer replaceBuffer = new StringBuffer( );
	private StringBuffer tokenBuffer = new StringBuffer( );

	public RemoveCondition( File sqlFile, EDbVendor vendor,
							List<String> conditionList )
	{
		TGSqlParser sqlparser = new TGSqlParser( vendor );
		sqlparser.setSqlfilename( sqlFile.getAbsolutePath( ) );
		remove( sqlparser, conditionList );
	}

	private String leftParenthese = null;
	private String rightParenthese = null;

	public RemoveCondition( String sql, EDbVendor vendor,
							List<String> conditionList )
	{
		TGSqlParser sqlparser = new TGSqlParser( vendor );
		String noquoteString = removeQuote( sql.trim( ) );
		int index = sql.indexOf( noquoteString );
		if ( index > 0 )
		{
			leftParenthese = sql.substring( 0, index );
			rightParenthese = sql.substring( index + noquoteString.length( ) );
		}
		sqlparser.setSqltext( noquoteString );
		conditionList = removeCondition$(conditionList);
		remove( sqlparser, conditionList );

		if ( leftParenthese != null && rightParenthese != null )
		{
			result = ( leftParenthese + result + rightParenthese );
		}
	}

	private List<String> removeCondition$(List<String> conditionList) {
		List<String> newConditionList = new ArrayList<>();
		for (String condition : conditionList) {
			String trim = condition.trim();
			if (trim.startsWith("$") && trim.endsWith("$")) {
				newConditionList.add(trim.substring(1, trim.length() - 1));
			} else {
				newConditionList.add(condition);
			}
		}
		return newConditionList;
	}

	private String removeQuote( String sql )
	{
		if ( sql.startsWith( "(" ) && sql.endsWith( ")" ) )
		{
			sql = sql.substring( 1, sql.length( ) - 1 ).trim( );
		}
		if ( sql.startsWith( "(" ) && sql.endsWith( ")" ) )
		{
			return removeQuote( sql );
		}
		return sql;
	}

	private void getParserString( TCustomSqlStatement query,
								  List<String> conditionList )
	{
		String oldString = toString( query );
		if ( oldString == null )
			return;
		String newString = remove( query, conditionList );
		if ( newString == null )
			return;
		if ( newString != null && !oldString.equals( newString ) )
		{
			query.setString( newString );
			return;
		}
	}

	public String getRemoveResult( )
	{
		return result.toString( );
	}

	private String getStmtPrefix( TCustomSqlStatement stat,
								  TSourceToken clauseToken )
	{
		TSourceToken startToken = stat.getStartToken( );
		TSourceToken endToken = clauseToken;
//		TSourceTokenList tokenList = startToken.container;
		TSourceTokenList tokenList=new TSourceTokenList();
		tokenList.add(startToken);
		container(startToken,tokenList);
		prefixBuffer.delete( 0, prefixBuffer.length( ) );
		boolean flag = false;
		for ( int i = 0; i < tokenList.size( ); i++ )
		{
			TSourceToken token = tokenList.get( i );
			if ( !flag )
			{
				if ( token == startToken )
					flag = true;
				else
					continue;
			}
			if ( token == endToken || startToken.posinlist + i > endToken.posinlist )
				break;
			prefixBuffer.append( token.toString( ) );
		}
		String prefix = prefixBuffer.toString( );
		return prefix;
	}

	private void container(TSourceToken token,TSourceTokenList list){
		TSourceToken nextTokenInChain = token.getNextTokenInChain();
		if(null != nextTokenInChain){
			list.add(nextTokenInChain);
			container(nextTokenInChain,list);
		}
	}

	private String getStmtSuffix( TCustomSqlStatement stat,
								  TSourceToken clauseToken, boolean removeSpaces )
	{
		TSourceToken startToken = clauseToken;
		TSourceToken endToken = stat.getEndToken( );
		TSourceTokenList tokenList=new TSourceTokenList();
		tokenList.add(clauseToken);
		container(clauseToken,tokenList);
//		TSourceTokenList tokenList = startToken.container;
		suffixBuffer.delete( 0, suffixBuffer.length( ) );
		boolean flag = false;
		for ( int i = 0; i < tokenList.size( ); i++ )
		{
			TSourceToken token = tokenList.get( i );
			if ( !flag )
			{
				if ( token == startToken )
				{
					flag = true;
					// Remove the white space token, replace the where token
					// with the suffix token.
					if ( removeSpaces )
					{
						while ( ++i < tokenList.size( ) )
						{
							token = tokenList.get( i );
							String tokenText = token.toString( );
							if ( tokenText.trim( ).length( ) == 0 )
							{
								if ( token == endToken )
								{
									return suffixBuffer.toString( );
								}
								continue;
							}
							else
							{
								if ( startToken.posinlist + i <= endToken.posinlist )
									suffixBuffer.append( tokenText );
								if ( token == endToken )
								{
									return suffixBuffer.toString( );
								}
								break;
							}
						}
					}
				}
				continue;
			}

			if ( token == endToken || startToken.posinlist + i > endToken.posinlist )
			{
				if ( token == endToken )
					suffixBuffer.append( token.toString( ) );
				break;
			}

			suffixBuffer.append( token.toString( ) );
		}
		return suffixBuffer.toString( );
	}

	String remove( TCustomSqlStatement stat, List<String> conditionList )
	{
		String clauseCondition = null;
		if ( stat.getResultColumnList( ) != null )
		{
			for ( int j = 0; j < stat.getResultColumnList( ).size( ); j++ )
			{
				TResultColumn column = stat.getResultColumnList( )
						.getResultColumn( j );
				if ( column.getExpr( ) != null
						&& column.getExpr( ).getSubQuery( ) instanceof TCustomSqlStatement )
				{
					TCustomSqlStatement query = (TCustomSqlStatement) column.getExpr( )
							.getSubQuery( );
					getParserString( query, conditionList );
				} else if(column.getExpr( ) != null && column.getExpr( ).toString() != null &&
						column.getExpr( ).toString().indexOf("'$")!=-1 &&
						column.getExpr( ).toString().indexOf("$'")!=-1 ){
					//added this specific condition for following example for fixing issue #3037371:
					//System should replace
					//('$Radius_Origin_ZIP$') AS "ZPRCZ"
					//to
					//('') AS "ZPRCZ" in case user does not enter any value for such run time prompts
					String runTimeFieldKey = column.getExpr( ).toString();
					for (Iterator iterator = conditionList.iterator(); iterator
							.hasNext();) {
						String runTimeField = (String) iterator.next();
						runTimeField = "'$"+runTimeField+"$'";
						if(runTimeFieldKey.indexOf(runTimeField)!=-1){
							if(column.getAliasClause()!=null && !"".equalsIgnoreCase(column.getAliasClause().toString().trim())){
								column.setString("' '" + " AS "+column.getAliasClause());
							}else{
								column.setString("' '");
							}
							break;
						}
					}
				}
			}
		}
		if ( stat.getCteList( ) != null )
		{
			for ( int i = 0; i < stat.getCteList( ).size( ); i++ )
			{
				TCTE cte = stat.getCteList( ).getCTE( i );
				if ( cte.getSubquery( ) != null )
				{
					getParserString( cte.getSubquery( ), conditionList );
				}
				if ( cte.getInsertStmt( ) != null )
				{
					getParserString( cte.getInsertStmt( ), conditionList );
				}
				if ( cte.getUpdateStmt( ) != null )
				{
					getParserString( cte.getUpdateStmt( ), conditionList );
				}
				if ( cte.getPreparableStmt( ) != null )
				{
					getParserString( cte.getPreparableStmt( ), conditionList );
				}
				if ( cte.getDeleteStmt( ) != null )
				{
					getParserString( cte.getDeleteStmt( ), conditionList );
				}
			}
		}

		if ( stat instanceof TSelectSqlStatement
				&& ( (TSelectSqlStatement) stat ).getSetOperator( ) != TSelectSqlStatement.SET_OPERATOR_NONE)
		{
			TSelectSqlStatement select = ( (TSelectSqlStatement) stat );
			getParserString( select.getLeftStmt( ), conditionList );
			getParserString( select.getRightStmt( ), conditionList );
			return toString( select );
		}

		if ( stat.getStatements( ) != null && stat.getStatements( ).size( ) > 0 )
		{
			for ( int i = 0; i < stat.getStatements( ).size( ); i++ )
			{
				getParserString( stat.getStatements( ).get( i ), conditionList );
			}
		}
		if ( stat.getReturningClause( ) != null )
		{
			if ( stat.getReturningClause( ).getColumnValueList( ) != null )
			{
				for ( int i = 0; i < stat.getReturningClause( )
						.getColumnValueList( )
						.size( ); i++ )
				{
					if ( stat.getReturningClause( )
							.getColumnValueList( )
							.getExpression( i )
							.getSubQuery( ) != null )
					{
						getParserString( stat.getReturningClause( )
								.getColumnValueList( )
								.getExpression( i )
								.getSubQuery( ), conditionList );
					}
				}
			}
			if ( stat.getReturningClause( ).getVariableList( ) != null )
			{
				for ( int i = 0; i < stat.getReturningClause( )
						.getVariableList( )
						.size( ); i++ )
				{
					if ( stat.getReturningClause( )
							.getVariableList( )
							.getExpression( i )
							.getSubQuery( ) != null )
					{
						getParserString( stat.getReturningClause( )
								.getVariableList( )
								.getExpression( i )
								.getSubQuery( ), conditionList );
					}
				}
			}
		}
		if ( stat instanceof TSelectSqlStatement )
		{
			TTableList list = ( (TSelectSqlStatement) stat ).tables;
			for ( int i = 0; i < list.size( ); i++ )
			{
				TTable table = list.getTable( i );
				if ( table.getSubquery( ) != null )
				{
					getParserString( table.getSubquery( ), conditionList );
				}
				if ( table.getFuncCall( ) != null )
				{
					ExpressionChecker w = new ExpressionChecker( this );
					w.checkFunctionCall( table.getFuncCall( ), conditionList );
				}
			}
		}

		if ( stat instanceof TSelectSqlStatement )
		{
			TJoinList list = ( (TSelectSqlStatement) stat ).joins;
			for ( int i = 0; i < list.size( ); i++ )
			{
				TJoin join = list.getJoin( i );
				for ( int j = 0; j < join.getJoinItems( ).size( ); j++ )
				{
					TJoinItem joinItem = join.getJoinItems( ).getJoinItem( j );
					if ( joinItem.getTable( ) != null )
					{
						if ( joinItem.getTable( ).getSubquery( ) != null )
						{
							getParserString( joinItem.getTable( ).getSubquery( ),
									conditionList );
						}
						if ( joinItem.getTable( ).getFuncCall( ) != null )
						{
							ExpressionChecker w = new ExpressionChecker( this );
							w.checkFunctionCall( joinItem.getTable( )
									.getFuncCall( ), conditionList );
						}
					}
				}
			}
		}

		if ( stat instanceof TSelectSqlStatement )
		{
			TSelectSqlStatement select = (TSelectSqlStatement) stat;
			for ( int i = 0; i < select.getResultColumnList( ).size( ); i++ )
			{
				TResultColumn field = select.getResultColumnList( )
						.getResultColumn( i );
				TExpression expr = field.getExpr( );
				if ( expr != null
						&& expr.getExpressionType( ) == EExpressionType.subquery_t )
				{
					getParserString( expr.getSubQuery( ), conditionList );
				}
			}
		}

		if ( stat.getWhereClause( ) != null
				&& stat.getWhereClause( ).getCondition( ) != null )
		{

			TExpression whereExpression = stat.getWhereClause( ).getCondition( );
			if ( whereExpression.toString( ) == null )
			{
				RemoveCondition removeCondition = new RemoveCondition( stat.toString( ),
						stat.dbvendor,
						conditionList );
				return removeCondition.result;
			}
			else
			{
				String oldString = toString( stat );
				conditionBuffer.delete( 0, conditionBuffer.length( ) );
				ExpressionChecker w = new ExpressionChecker( this );
				w.checkExpression( whereExpression, conditionList );
				String newString = toString( stat );
				if ( !oldString.equals( newString ) )
				{
					if ( whereExpression != null )
					{
						if ( toString( whereExpression ) != null )
						{
							String prefix = getStmtPrefix( stat,
									stat.getWhereClause( ).getStartToken( ) );
							clauseCondition = trim( toString( whereExpression ).trim( ) );
							whereExpression.remove2( );
							String suffix = getStmtSuffix( stat,
									stat.getWhereClause( ).getEndToken( ),
									false );
							conditionBuffer.append( prefix )
									.append( toString( stat.getWhereClause( ) ) )
									.append( clauseCondition )
									.append( suffix );
						}
						else
						{
							String prefix = getStmtPrefix( stat,
									stat.getWhereClause( ).getStartToken( ) );
							String suffix = getStmtSuffix( stat,
									stat.getWhereClause( ).getEndToken( ),
									true );
							conditionBuffer.append( prefix ).append( suffix );
						}
					}
					return conditionBuffer.toString( );
				}
			}
		}
		if ( ( stat instanceof TSelectSqlStatement )
				&& ( (TSelectSqlStatement) stat ).getGroupByClause( ) != null
				&& ( (TSelectSqlStatement) stat ).getGroupByClause( )
				.getHavingClause( ) != null )
		{

			TExpression havingExpression = ( (TSelectSqlStatement) stat ).getGroupByClause( )
					.getHavingClause( );

			if ( havingExpression.toString( ) == null )
			{
				RemoveCondition removeCondition = new RemoveCondition( stat.toString( ),
						stat.dbvendor,
						conditionList );
				return removeCondition.result;
			}
			else
			{
				String oldString = toString( stat );
				conditionBuffer.delete( 0, conditionBuffer.length( ) );
				ExpressionChecker w = new ExpressionChecker( this );
				w.checkExpression( havingExpression, conditionList );
				String newString = toString( stat );
				if ( !oldString.equals( newString ) )
				{
					if ( havingExpression != null )
					{
						if ( toString( havingExpression ) != null )
						{
							String prefix = getStmtPrefix( stat,
									( (TSelectSqlStatement) stat ).getGroupByClause( )
											.getHavingClause( )
											.getStartToken( ) );
							clauseCondition = trim( toString( havingExpression ).trim( ) );
							havingExpression.remove2( );

							if ( toString( havingExpression ) != null )
							{
								String suffix = getStmtSuffix( stat,
										( (TSelectSqlStatement) stat ).getGroupByClause( )
												.getHavingClause( )
												.getEndToken( ),
										false );
								conditionBuffer.append( prefix )
										.append( toString( ( (TSelectSqlStatement) stat ).getGroupByClause( )
												.getHavingClause( ) ) )
										.append( clauseCondition )
										.append( suffix );
							}
							else
							{
								String suffix = getStmtSuffix( stat,
										( (TSelectSqlStatement) stat ).getGroupByClause( )
												.getHAVING( ),
										false );
								conditionBuffer.append( prefix )
										.append( clauseCondition )
										.append( suffix );
							}
						}
						else
						{
							String prefix = getStmtPrefix( stat,
									( (TSelectSqlStatement) stat ).getGroupByClause( )
											.getHAVING( ) );
							String suffix = getStmtSuffix( stat,
									( (TSelectSqlStatement) stat ).getGroupByClause( )
											.getHAVING( ),
									true );
							conditionBuffer.append( prefix ).append( suffix );
						}
					}
					return conditionBuffer.toString( );
				}
			}
		}
		return toString( stat );

	}

	void remove( TGSqlParser sqlparser, List<String> conditionList )
	{
		int i = sqlparser.parse( );
		if ( i == 0 )
		{
			TCustomSqlStatement stat = sqlparser.sqlstatements.get( 0 );
			getParserString( stat, conditionList );
			result = toString( stat );
			if ( result != null )
			{
				result = replaceCondition( result, conditionList );
			}
		}
		else
			System.err.println( sqlparser.getErrormessage( ) );
	}

	/**
	 * added code for fixing issue #3037371
	 */
	private String replaceCondition( String content,
									 List<String> conditionList )
	{
		String[] conditions = new String[0];
		if ( conditionList != null && !conditionList.isEmpty( ) )
		{
			conditions =  conditionList.toArray( new String[0] );
		}
		Pattern pattern = Pattern.compile( "\\$[^$]+\\$",
				Pattern.CASE_INSENSITIVE );
		Matcher matcher = pattern.matcher( content );
		replaceBuffer.delete( 0, replaceBuffer.length( ) );
		while ( matcher.find( ) )
		{
			String condition = matcher.group( ).replaceAll( "\\$", "" ).trim( );
			for ( int i = 0; i < conditions.length; i++ )
			{
				if ( conditions[i].equalsIgnoreCase( condition )
						&& conditionList.contains( conditions[i] ) )
				{
					matcher.appendReplacement( replaceBuffer,
							conditions[i]  );
					break;
				}
			}
		}
		matcher.appendTail( replaceBuffer );
		return replaceBuffer.toString( );
	}

//	public String toString( TParseTreeNode node )
//	{
//		TSourceToken tsourcetoken = node.getStartToken( );
//		if ( tsourcetoken == null )
//			return null;
//		TSourceToken tsourcetoken1 = node.getEndToken( );
//		if ( tsourcetoken1 == null )
//			return null;
//		TSourceTokenList tsourcetokenlist = tsourcetoken.container;
//		if ( tsourcetokenlist == null )
//			return null;
//		int i = tsourcetoken.posinlist;
//		int j = tsourcetoken1.posinlist;
//		tokenBuffer.delete( 0, tokenBuffer.length( ) );
//		for ( int k = i; k <= j; k++ )
//			tokenBuffer.append( tsourcetokenlist.get( k ).toString( ) );
//		return tokenBuffer.toString( );
//	}

	public String toString( TParseTreeNode node )
	{
		TSourceToken tsourcetoken = node.getStartToken( );
		if ( tsourcetoken == null )
			return null;
		TSourceToken tsourcetoken1 = node.getEndToken( );
		if ( tsourcetoken1 == null )
			return null;
		tokenBuffer.delete( 0, tokenBuffer.length( ) );
		tokenBuffer.append( node.toString( ) );
		node.toScript();
		return tokenBuffer.toString( );
	}

	private String trim( String string )
	{
		Pattern pattern = Pattern.compile( "(\n|\r\n)\\s+(\n|\r\n)",
				Pattern.CASE_INSENSITIVE );
		Matcher matcher = pattern.matcher( string );
		trimBuffer.delete( 0, trimBuffer.length( ) );
		while ( matcher.find( ) )
		{
			if ( matcher.group( ).endsWith( "\r\n" ) )
				matcher.appendReplacement( trimBuffer, "\r\n" );
			else
				matcher.appendReplacement( trimBuffer, "\n" );
		}
		matcher.appendTail( trimBuffer );
		return trimBuffer.toString( );
	}
}
