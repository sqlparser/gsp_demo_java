
package demos.sqltranslator;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TParseTreeNode;

public class KeywordCheckResult
{

	private TSourceToken token;
	private EDbVendor targetVendor;
	private boolean canTranslate;
	private String originalText;
	private String originalNodeText;
	private long originalColumnNo;
	private long originalLineNo;
	private String translateResult;
	private String translationInfo;

	public void setTranslationInfo( String translationInfo )
	{
		this.translationInfo = translationInfo;
	}

	public String getTranslateResult( )
	{
		return translateResult;
	}

	public void setTranslateResult( String translateResult )
	{
		this.translateResult = translateResult;
	}

	public KeywordCheckResult( TSourceToken token, EDbVendor targetVendor )
	{
		this.token = token;
		originalText = token.toString( );
		originalColumnNo = token.columnNo;
		originalLineNo = token.lineNo;
		this.targetVendor = targetVendor;
	}

	public boolean canTranslate( )
	{
		return canTranslate;
	}

	public long getOriginalColumnNo( )
	{
		return originalColumnNo;
	}

	public long getOriginalLineNo( )
	{
		return originalLineNo;
	}

	public String getOriginalText( )
	{
		return originalText;
	}

	public EDbVendor getTargetVendor( )
	{
		return targetVendor;
	}

	public TSourceToken getToken( )
	{
		return token;
	}

	public void setCanTranslate( boolean canTranslate )
	{
		this.canTranslate = canTranslate;
	}

	public void translate( )
	{
		if ( translateResult != null )
		{
			if ( node != null )
			{
				node.setString( translateResult );
			}
			else
				token.setString( translateResult );
		}
		else
		{

			if ( node != null )
			{
				node.setString( originalNodeText
						+ " /*need to be translated by handy*/" );
			}
			else
				token.setString( originalText
						+ " /*need to be translated by handy*/" );
		}
	}

	public String getOriginalTreeNodeText( )
	{
		return originalNodeText;
	}

	public TParseTreeNode getTreeNode( )
	{
		return node;
	}

	public String getTranslationInfo( )
	{
		if ( translationInfo != null )
			return translationInfo;
		return getDatabaseName( token.getDbvendor( ) )
				+ "'s keyword "
				+ ( node != null ? originalNodeText : originalText )
				+ " is not supported by "
				+ getDatabaseName( targetVendor );
	}

	public static String getDatabaseName( EDbVendor dbVendor )
	{
		switch ( dbVendor )
		{
			case dbvmssql :
				return "SQL Server";
			case dbvmysql :
				return "MySQL";
			case dbvoracle :
				return "Oracle";
		}
		return dbVendor.name( );
	}

	private TParseTreeNode node;

	public void setTreeNode( TParseTreeNode node )
	{
		this.node = node;
		this.originalNodeText = node.toString( );
	}
}
