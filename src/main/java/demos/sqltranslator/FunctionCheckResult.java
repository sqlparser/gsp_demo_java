
package demos.sqltranslator;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TFunctionCall;

public class FunctionCheckResult
{

	private TFunctionCall function;
	private EDbVendor targetVendor;
	private boolean canTranslate;
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

	public FunctionCheckResult( TFunctionCall function, EDbVendor targetVendor )
	{
		this.function = function;
		originalText = function.toString( );
		originalColumnNo = function.getColumnNo( );
		originalLineNo = function.getLineNo( );
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

	public TFunctionCall getFunction( )
	{
		return function;
	}

	public void setCanTranslate( boolean canTranslate )
	{
		this.canTranslate = canTranslate;
	}

	public void translate( )
	{
		if ( translateResult != null )
		{
			function.setString( translateResult );
		}
		else
		{
			function.setString( originalText
					+ " /*need to be translated by handy*/" );
		}
	}
}
