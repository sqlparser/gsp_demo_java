
package demos.sqltranslator;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TTypeName;

public class DataTypeCheckResult
{

	private TTypeName type;
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

	public DataTypeCheckResult( TTypeName type, EDbVendor targetVendor )
	{
		this.type = type;
		originalText = type.toString( );
		originalColumnNo = type.getColumnNo( );
		originalLineNo = type.getLineNo( );
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

	public TTypeName getType( )
	{
		return type;
	}

	public void setCanTranslate( boolean canTranslate )
	{
		this.canTranslate = canTranslate;
	}

	public void translate( )
	{
		if ( translateResult != null )
		{
			type.setString( translateResult );
		}
		else
		{
			type.setString( originalText + " /*need to be translated by handy*/" );
		}
	}
}
