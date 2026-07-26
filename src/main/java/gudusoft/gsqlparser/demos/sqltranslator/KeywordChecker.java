
package demos.sqltranslator;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TParseTreeNode;

public class KeywordChecker
{

	public static KeywordCheckResult checkKeyword( TSourceToken token,
			EDbVendor targetVendor )
	{
		if ( token.getDbvendor( ) == EDbVendor.dbvoracle )
		{
			return OracleKeywordChecker.checkKeyword( token, targetVendor );
		}
		else if ( token.getDbvendor( ) == EDbVendor.dbvmssql )
		{
			return MssqlKeywordChecker.checkKeyword( token, targetVendor );
		}
		else if ( token.getDbvendor( ) == EDbVendor.dbvmysql )
		{

		}

		return null;
	}

	protected static KeywordCheckResult createKeywordResult(
			TSourceToken token, EDbVendor targetVendor,
			String translateMessage, boolean canTranslate )
	{
		KeywordCheckResult result = new KeywordCheckResult( token, targetVendor );
		result.setCanTranslate( canTranslate );
		result.setTranslationInfo( translateMessage );
		return result;
	}

	protected static KeywordCheckResult createKeywordResult(
			TSourceToken token, EDbVendor targetVendor, boolean canTranslate,
			String translate )
	{
		KeywordCheckResult result = new KeywordCheckResult( token, targetVendor );
		result.setCanTranslate( canTranslate );
		result.setTranslateResult( translate );
		return result;
	}

	protected static KeywordCheckResult createKeywordResult(
			TSourceToken token, EDbVendor targetVendor, boolean canTranslate,
			String translate, TParseTreeNode node, String translateMessage )
	{
		KeywordCheckResult result = new KeywordCheckResult( token, targetVendor );
		result.setCanTranslate( canTranslate );
		result.setTranslateResult( translate );
		result.setTranslationInfo( translateMessage );
		result.setTreeNode( node );
		return result;
	}

	protected static KeywordCheckResult createKeywordResult(
			TSourceToken token, EDbVendor targetVendor, boolean canTranslate,
			String translate, String translateMessage )
	{
		KeywordCheckResult result = new KeywordCheckResult( token, targetVendor );
		result.setCanTranslate( canTranslate );
		result.setTranslateResult( translate );
		result.setTranslationInfo( translateMessage );
		return result;
	}

	protected static KeywordCheckResult createKeywordResult(
			TSourceToken token, EDbVendor targetVendor, boolean canTranslate )
	{
		KeywordCheckResult result = new KeywordCheckResult( token, targetVendor );
		result.setCanTranslate( canTranslate );
		return result;
	}

}
