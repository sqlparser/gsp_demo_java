
package demos.sqltranslator;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TSourceToken;

public class IdentifierRuleResult
{

	private TSourceToken token;
	private EDbVendor targetVendor;
	private boolean canTranslate;
	private IdentifierRule rule;
	private String originText;
	private String ruleMessage;

	public void setRuleMessage( String ruleMessage )
	{
		this.ruleMessage = ruleMessage;
	}

	public IdentifierRule getRule( )
	{
		return rule;
	}

	public void setRule( IdentifierRule rule )
	{
		this.rule = rule;
	}

	public IdentifierRuleResult( TSourceToken token, EDbVendor targetVendor )
	{
		this.token = token;
		this.originText = token.toString( );
		this.targetVendor = targetVendor;
	}

	public boolean canTranslate( )
	{
		return canTranslate;
	}

	public void setCanTranslate( boolean canTranslate )
	{
		this.canTranslate = canTranslate;
	}

	public TSourceToken getToken( )
	{
		return token;
	}

	public EDbVendor getTargetVendor( )
	{
		return targetVendor;
	}

	public String getRuleMessage( )
	{
		if ( ruleMessage != null )
			return ruleMessage;
		if ( rule == null )
			return null;
		if ( rule == IdentifierRule.Function )
			return rule.getRuleMessage( originText, targetVendor );
		return rule.getRuleMessage( targetVendor );
	}
}
