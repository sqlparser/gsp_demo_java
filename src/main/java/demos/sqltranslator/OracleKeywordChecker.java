
package demos.sqltranslator;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TForUpdate;
import gudusoft.gsqlparser.nodes.THierarchical;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TParseTreeNodeList;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import demos.joinConvert.JoinConverter;

import java.util.Stack;

public class OracleKeywordChecker extends KeywordChecker
{

	public static KeywordCheckResult checkKeyword( TSourceToken token,
			EDbVendor targetVendor )
	{
		if ( token.toString( ).equalsIgnoreCase( "UID" ) )
		{

			switch ( targetVendor )
			{
				case dbvmssql :
					return createKeywordResult( token,
							targetVendor,
							true,
							"SUSER_SID()" );
				default :
					return createKeywordResult( token, targetVendor, false );
			}
		}
		else if ( token.toString( ).equalsIgnoreCase( "ROWID" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createKeywordResult( token, targetVendor, false );
				default :
					return createKeywordResult( token, targetVendor, false );
			}
		}
		else if ( token.toString( ).equalsIgnoreCase( "ROWNUM" ) )
		{

			switch ( targetVendor )
			{
				case dbvmssql :
					return createKeywordResult( token, targetVendor, false );
				default :
					return createKeywordResult( token, targetVendor, false );
			}
		}
		else if ( token.toString( ).equalsIgnoreCase( "UID" ) )
		{

			switch ( targetVendor )
			{
				case dbvmssql :
					return createKeywordResult( token,
							targetVendor,
							true,
							"SUSER_SID()" );
				default :
					return createKeywordResult( token, targetVendor, false );
			}
		}
		else if ( token.toString( ).equalsIgnoreCase( "CONNECT" ) )
		{
			Stack<TParseTreeNode> list = token.getNodesStartFromThisToken( );
			for ( int j = 0; j < list.size( ); j++ )
			{
				TParseTreeNode node = (TParseTreeNode) list.get( j );
				if ( node instanceof THierarchical )
				{
					switch ( targetVendor )
					{
						case dbvmssql :
							String result = "Oracle uses CONNECT BY statements for hierarchical queries, while SQL Server implements hierarchical queries by using common table expressions.";
							String translator = node.toString( )
									+ " /*need to be translated by handy*/";
							return createKeywordResult( token,
									targetVendor,
									false,
									translator,
									node,
									result );
						default :
							result = "Oracle uses CONNECT BY statements for hierarchical queries.";
							translator = node.toString( )
									+ " /*need to be translated by handy*/";

							return createKeywordResult( token,
									targetVendor,
									false,
									translator,
									node,
									result );
					}
				}
			}
		}
		else if ( token.toString( ).equalsIgnoreCase( "FOR" ) )
		{
			Stack<TParseTreeNode> list = token.getNodesStartFromThisToken( );
			for ( int j = 0; j < list.size( ); j++ )
			{
				TParseTreeNode node = (TParseTreeNode) list.get( j );
				if ( node instanceof TForUpdate )
				{
					switch ( targetVendor )
					{
						case dbvmssql :
							String result = "The Oracle Select For Update statement allows you to lock the records in the cursor result set. Use the locking hint UPDLOCK in SQL Server SELECT statement.";
							String translator = node.toString( )
									+ " /*need to be translated by handy*/";
							return createKeywordResult( token,
									targetVendor,
									false,
									translator,
									node,
									result );
						default :
							result = "The Oracle Select For Update statement allows you to lock the records in the cursor result set.";
							translator = node.toString( )
									+ " /*need to be translated by handy*/";

							return createKeywordResult( token,
									targetVendor,
									false,
									translator,
									node,
									result );
					}
				}
			}
		}
		else if ( token.toString( ).equalsIgnoreCase( "MINUS" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String result = "Oracle can combine multiple queries using the set operator MINUS, SQL Server doesn't support it.";
					String translator = token.toString( )
							+ " /*need to be translated by handy*/";
					return createKeywordResult( token,
							targetVendor,
							false,
							translator,
							result );
				default :
					result = "Oracle can combine multiple queries using the set operator MINUS.";
					translator = token.toString( )
							+ " /*need to be translated by handy*/";
					return createKeywordResult( token,
							targetVendor,
							false,
							translator,
							result );
			}
		}
		else if ( token.toString( ).equalsIgnoreCase( "TYPE" ) )
		{
			int pos = token.posinlist - 1;
			if ( pos != -1 )
			{
				if ( token.container.get( pos ).toString( ).equals( "%" ) )
				{
					switch ( targetVendor )
					{
						case dbvmssql :
							String result = "The %TYPE attribute of Oracle provides the datatype of a variable or database column. There is no equivalent for Oracle's %TYPE attribute in T-SQL.";
							String translator = token.toString( )
									+ " /*need to be translated by handy*/";
							return createKeywordResult( token,
									targetVendor,
									false,
									translator,
									result );
						default :
							result = "The %TYPE attribute of Oracle provides the datatype of a variable or database column.";
							translator = token.toString( )
									+ " /*need to be translated by handy*/";
							return createKeywordResult( token,
									targetVendor,
									false,
									translator,
									result );
					}
				}
			}
		}
		else if ( token.toString( ).equalsIgnoreCase( "ROWTYPE" ) )
		{
			int pos = token.posinlist - 1;
			if ( pos != -1 )
			{
				if ( token.container.get( pos ).toString( ).equals( "%" ) )
				{
					switch ( targetVendor )
					{
						case dbvmssql :
							String result = "The %ROWTYPE attribute provides a record type that represents a row in a table or view. There is no equivalent for Oracle's %ROWTYPE attribute in T-SQL.";
							String translator = token.toString( )
									+ " /*need to be translated by handy*/";
							return createKeywordResult( token,
									targetVendor,
									false,
									translator,
									result );
						default :
							result = "The %ROWTYPE attribute provides a record type that represents a row in a table or view.";
							translator = token.toString( )
									+ " /*need to be translated by handy*/";
							return createKeywordResult( token,
									targetVendor,
									false,
									translator,
									result );
					}
				}
			}
		}
		else if ( token.toString( ).matches( "(?i)\\(\\s*\\+s*\\)" ) )
		{

			Stack<TParseTreeNode> list = token.getNodesEndWithThisToken( );
			for ( int j = 0; j < list.size( ); j++ )
			{
				TParseTreeNode node = (TParseTreeNode) list.get( j );
				if ( node instanceof TSelectSqlStatement )
				{
					String result = "The special outer join syntax with the (+) qualifier.";
					JoinConverter convert = new JoinConverter( node.toString( ), targetVendor);
					convert.convert( );
					String translator = convert.getQuery( );
					return createKeywordResult( token,
							targetVendor,
							true,
							translator,
							node,
							result );
				}
			}
		}
		return null;
	}

}
