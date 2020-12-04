
package demos;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;
import demos.sqltranslator.IdentifierCheckResult;
import demos.sqltranslator.IdentifierRule;
import demos.sqltranslator.IdentifierRuleResult;
import demos.sqltranslator.SqlTranslator;

public class SqlTranslatorTest extends TestCase
{

	public void testIdentifierSizeRule( )
	{
		String sql = "select a_very_very_long_and_not_valid_name from abc";
		SqlTranslator translator = new SqlTranslator( sql,
				EDbVendor.dbvmssql,
				EDbVendor.dbvoracle,
				false );
		assertTrue( translator.getIdentifierCheckResults( ).length == 1 );
		IdentifierCheckResult checkResult = translator.getIdentifierCheckResults( )[0];
		assertTrue( checkResult.getCheckRuleResults( ).length == 1 );
		IdentifierRuleResult result = checkResult.getCheckRuleResults( )[0];
		assertTrue( result.getRule( ) == IdentifierRule.Size );
		assertTrue( !result.canTranslate( ) );
	}

	public void testIdentifierBeginWithRule( )
	{
		String sql = "create table #testa(f8 float)";
		SqlTranslator translator = new SqlTranslator( sql,
				EDbVendor.dbvmssql,
				EDbVendor.dbvoracle,
				false );
		assertTrue( translator.getIdentifierCheckResults( ).length == 1 );
		IdentifierCheckResult checkResult = translator.getIdentifierCheckResults( )[0];
		assertTrue( checkResult.getCheckRuleResults( ).length == 2 );
		IdentifierRuleResult result = checkResult.getCheckRuleResults( )[0];
		assertTrue( result.getRule( ) == IdentifierRule.BeginWith );
		assertTrue( !result.canTranslate( ) );
	}

	public void testIdentifierSubsequentCharactersRule( )
	{
		String sql = "create table #testa(f8 float)";
		SqlTranslator translator = new SqlTranslator( sql,
				EDbVendor.dbvmssql,
				EDbVendor.dbvoracle,
				false );
		assertTrue( translator.getIdentifierCheckResults( ).length == 1 );
		IdentifierCheckResult checkResult = translator.getIdentifierCheckResults( )[0];
		assertTrue( checkResult.getCheckRuleResults( ).length == 2 );
		IdentifierRuleResult result = checkResult.getCheckRuleResults( )[1];
		assertTrue( result.getRule( ) == IdentifierRule.SubsequentCharacters );
		assertTrue( !result.canTranslate( ) );
	}

	public void testIdentifierKeywordRule( )
	{
		String sql = "create table WHENEVER(f8 float)";
		SqlTranslator translator = new SqlTranslator( sql,
				EDbVendor.dbvmssql,
				EDbVendor.dbvoracle,
				false );
		assertTrue( translator.getIdentifierCheckResults( ).length == 1 );
		IdentifierCheckResult checkResult = translator.getIdentifierCheckResults( )[0];
		assertTrue( checkResult.getCheckRuleResults( ).length == 1 );
		IdentifierRuleResult result = checkResult.getCheckRuleResults( )[0];
		assertTrue( result.getRule( ) == IdentifierRule.Keyword );
		assertTrue( result.canTranslate( ) );
		checkResult.translate( );
		assertTrue( result.getToken( ).toString( ).equalsIgnoreCase( "\"WHENEVER\"" ) );
	}

	public void testQuotedIdentifierRule( )
	{
		String sql = "create table [WHENEVER](f8 float)";
		SqlTranslator translator = new SqlTranslator( sql,
				EDbVendor.dbvmssql,
				EDbVendor.dbvoracle,
				false );
		assertTrue( translator.getIdentifierCheckResults( ).length == 1 );
		IdentifierCheckResult checkResult = translator.getIdentifierCheckResults( )[0];
		assertTrue( checkResult.getCheckRuleResults( ).length == 1 );
		IdentifierRuleResult result = checkResult.getCheckRuleResults( )[0];
		assertTrue( result.getRule( ) == IdentifierRule.QuotedIdentifier );
		assertTrue( result.canTranslate( ) );
		checkResult.translate( );
		assertTrue( result.getToken( ).toString( ).equalsIgnoreCase( "\"WHENEVER\"" ) );
	}
}
