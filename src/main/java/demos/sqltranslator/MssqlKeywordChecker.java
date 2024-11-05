
package demos.sqltranslator;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TParseTreeNodeList;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TDeleteSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MssqlKeywordChecker extends KeywordChecker
{

	public static KeywordCheckResult checkKeyword( TSourceToken token,
			EDbVendor targetVendor )
	{

		if ( token.toString( ).equalsIgnoreCase( "COMPUTE" ) )
		{
			String result = "SQL Server's keyword COMPUTE is not supported by "
					+ KeywordCheckResult.getDatabaseName( targetVendor )
					+ ". Attach the two sets of results using the UNION clause.";
			switch ( targetVendor )
			{
				case dbvoracle :
					return createKeywordResult( token,
							targetVendor,
							result,
							false );
				default :
					return createKeywordResult( token,
							targetVendor,
							result,
							false );
			}
		}
		if ( token.toString( ).equalsIgnoreCase( "SELECT" ) )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					Stack<TParseTreeNode> list = token.getNodesStartFromThisToken( );
					for ( int j = 0; j < list.size( ); j++ )
					{
						TParseTreeNode node = (TParseTreeNode) list.get( j );
						if ( node instanceof TSelectSqlStatement )
						{
							TSelectSqlStatement select = (TSelectSqlStatement) node;

							if ( select.getIntoClause( ) != null )
							{
								String result = "SELECT INTO statement is not supported by ANSI. Replace these statements with INSERT...SELECT statements in Oracle.";

								Pattern pattern = Pattern.compile( "\\s+INTO(.+?)\\s+FROM\\s+",
										Pattern.CASE_INSENSITIVE );
								String content = node.toString( );
								Matcher matcher = pattern.matcher( content );
								if ( matcher.find( ) )
								{
									String text = matcher.group( )
											.replaceFirst( "\\s*", "" )
											.replaceAll( "(?i)FROM\\s+", "" );
									String translator = "INSERT "
											+ text
											+ content.replace( text, "" );
									return createKeywordResult( token,
											targetVendor,
											true,
											translator,
											node,
											result );
								}
							}
							if ( ( (TSelectSqlStatement) node ).tables.size( ) == 0 )
							{
								String result = "Oracle does not support SELECTs without FROM clauses. However, Oracle provides the DUAL table which always contains one row.";

								TSourceToken lastToken = null;
								for ( int i = 0; i < select.getResultColumnList( )
										.size( ); i++ )
								{
									TSourceToken endToken = select.getResultColumnList( )
											.getResultColumn( i )
											.getEndToken( );
									if ( lastToken != null )
									{
										if ( endToken.posinlist > lastToken.posinlist )
										{
											lastToken = endToken;
										}
									}
									else
									{
										lastToken = endToken;
									}
								}

								StringBuffer buffer = new StringBuffer( );
								int start = select.getStartToken( ).posinlist;
								int end = select.getEndToken( ).posinlist;
								for ( int i = start; i <= end; i++ )
								{
									if ( i == lastToken.posinlist )
									{
										buffer.append( select.sourcetokenlist.get( i )
												.toString( ) );
										buffer.append( " FROM DUAL" );
									}
									else
									{
										buffer.append( select.sourcetokenlist.get( i )
												.toString( ) );
									}
								}
								return createKeywordResult( token,
										targetVendor,
										true,
										buffer.toString( ),
										node,
										result );
							}
							Map<TSourceToken, TExpression> expressionMap = new HashMap<TSourceToken, TExpression>( );
							for ( int i = 0; i < select.getResultColumnList( )
									.size( ); i++ )
							{
								TResultColumn column = select.getResultColumnList( )
										.getResultColumn( i );
								if ( column.getExpr( ) != null )
								{
									TExpression expr = column.getExpr( );
									if ( expr.getExpressionType( ) == EExpressionType.sqlserver_proprietary_column_alias_t )
									{
										expressionMap.put( expr.getStartToken( ),
												expr );
									}
								}
							}

							if ( !expressionMap.isEmpty( ) )
							{
								String result = "Oracle does not support the SQL Server column alias syntax.";
								int start = select.getStartToken( ).posinlist;
								int end = select.getEndToken( ).posinlist;
								StringBuffer buffer = new StringBuffer( );
								for ( int i = start; i <= end; i++ )
								{
									TSourceToken current = select.sourcetokenlist.get( i );
									if ( expressionMap.containsKey( current ) )
									{
										TExpression expr = expressionMap.get( current );
										buffer.append( expr.getRightOperand( )
												.toString( )
												+ " "
												+ expr.getLeftOperand( )
														.toString( ) );
										i += ( expr.getEndToken( ).posinlist - expr.getStartToken( ).posinlist );
									}
									else
										buffer.append( select.sourcetokenlist.get( i )
												.toString( ) );
								}
								return createKeywordResult( token,
										targetVendor,
										true,
										buffer.toString( ),
										node,
										result );
							}
						}
					}

			}
		}
		if ( token.toString( ).equalsIgnoreCase( "DELETE" ) )
		{
			Stack<TParseTreeNode> list = token.getNodesStartFromThisToken( );
			for ( int j = 0; j < list.size( ); j++ )
			{
				TParseTreeNode node = (TParseTreeNode) list.get( j );
				if ( node instanceof TDeleteSqlStatement )
				{
					TDeleteSqlStatement delete = (TDeleteSqlStatement) node;
					String from = delete.tables.toString( );
					Pattern pattern = Pattern.compile( "\\s+FROM\\s+",
							Pattern.CASE_INSENSITIVE );
					Matcher matcher = pattern.matcher( from );
					if ( matcher.find( ) )
					{
						String result = "Remove the second FROM clause from the DELETE statements. Convert the following Microsoft SQL Server or Sybase Adaptive Server query.";
						String firstTable = from.replaceAll( "(?i)\\s+FROM\\s+.+", "" );
						String secondTable = from.replaceFirst( "\\S+", "" );
						StringBuffer buffer = new StringBuffer( );
						buffer.append( "DELETE "
								+ firstTable
								+ " WHERE ROWID IN ( SELECT "
								+ firstTable
								+ ".ROWID" )
								.append( secondTable )
								.append( delete.toString( )
										.substring( delete.toString( )
												.indexOf( from )
												+ from.length( ) ) )
								.append( ")" );
						return createKeywordResult( token,
								targetVendor,
								true,
								buffer.toString( ),
								node,
								result );
					}
				}
			}
		}
		return null;
	}

}
