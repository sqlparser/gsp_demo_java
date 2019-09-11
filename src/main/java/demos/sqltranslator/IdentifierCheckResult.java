
package demos.sqltranslator;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TSourceToken;

import java.util.ArrayList;
import java.util.List;

public class IdentifierCheckResult
{

	private List<IdentifierRuleResult> results = new ArrayList<IdentifierRuleResult>( );

	private TSourceToken token;
	private EDbVendor targetVendor;
	private String originalText;
	private long originalColumnNo;
	private long originalLineNo;
	private String translateResult;

	public String getTranslateResult( )
	{
		return translateResult;
	}

	public void setTranslateResult( String translateResult )
	{
		this.translateResult = translateResult;
	}
	
	public IdentifierCheckResult( TSourceToken token, EDbVendor targetVendor )
	{
		this.token = token;
		this.targetVendor = targetVendor;
		this.originalText = token.toString( );
		this.originalColumnNo = token.columnNo;
		this.originalLineNo = token.lineNo;
	}

	public void addCheckRuleResult( IdentifierRuleResult result )
	{
		if ( result != null )
			results.add( result );
	}

	public boolean canTranslate( )
	{
		if ( results.size( ) == 0 )
			return false;
		for ( int i = 0; i < results.size( ); i++ )
		{
			if ( !results.get( i ).canTranslate( ) )
				return false;
		}
		return true;
	}

	public IdentifierRuleResult[] getCheckRuleResults( )
	{
		return results.toArray( new IdentifierRuleResult[0] );
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

	public String getTranslationInfo( )
	{
		StringBuffer buffer = new StringBuffer( );
		if ( results.size( ) == 1 )
		{
			buffer.append( results.get( 0 ).getRuleMessage( ) );
		}
		else if ( results.size( ) > 1 )
		{
			buffer.append( "\r\n" );
			for ( int i = 0; i < results.size( ); i++ )
			{
				buffer.append( ( i + 1 )
						+ "."
						+ results.get( i ).getRuleMessage( ) );
				if ( i < results.size( ) - 1 )
					buffer.append( "\r\n" );
			}
		}
		return buffer.toString( );
	}

	public boolean needTranslate( )
	{
		if ( results.size( ) == 0 )
			return false;
		return true;
	}

	public void removeCheckRuleResult( IdentifierRuleResult result )
	{
		if ( result != null )
			results.remove( result );
	}

	public void translate( )
	{
		if ( translateResult != null )
		{
			token.setString( translateResult );
		}
		else
		{
			token.setString( originalText + " /*need to be translated by handy*/" );
		}
	}
}
