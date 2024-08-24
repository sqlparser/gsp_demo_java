
package demos.sqltranslator;

import demos.sqltranslator.exception.FunctionIdentifierException;
import demos.sqltranslator.exception.SpecialIdentifierException;
import gudusoft.gsqlparser.EDbObjectType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.util.keywordChecker;

public class IdentifierChecker
{

	public static IdentifierRuleResult checkSizeRule( TSourceToken token,
			EDbVendor targetVendor )
	{
		String identifier = token.astext.trim( );
		char[] charArray = identifier.toCharArray( );
		switch ( targetVendor )
		{
			case dbvansi :
				if ( charArray.length > 128 )
				{
					return createSizeRuleResult( token, targetVendor );
				}
				break;
			case dbvmysql :
				if ( charArray.length > 255
						|| ( charArray.length > 64 && !isAliasToken( token ) ) )
				{
					return createSizeRuleResult( token, targetVendor );
				}
				break;
			case dbvmssql :
				if ( charArray.length > 128 )
				{
					return createSizeRuleResult( token, targetVendor );
				}
				break;
			case dbvpostgresql :
				if ( charArray.length > 63 )
				{
					return createSizeRuleResult( token, targetVendor );
				}
				break;
			case dbvoracle :
				if ( identifier.getBytes( ).length > 30 )
				{
					return createSizeRuleResult( token, targetVendor );
				}
				break;
		}
		return null;
	}

	private static IdentifierRuleResult createSizeRuleResult(
			TSourceToken token, EDbVendor targetVendor )
	{
		IdentifierRuleResult id = new IdentifierRuleResult( token, targetVendor );
		id.setRule( IdentifierRule.Size );
		id.setCanTranslate( false );
		return id;
	}

	public static IdentifierRuleResult checkBeginWithRule( TSourceToken token,
			EDbVendor targetVendor )
	{
		if ( isQuotedIdentifier( token ) )
			return null;
		String identifier = removeQuote( token );
		char ch = identifier.charAt( 0 );
		switch ( targetVendor )
		{
			case dbvansi :
				if ( !( ( ch >= 'a' && ch <= 'z' ) || ( ch >= 'A' && ch <= 'Z' ) ) )
					return createBeginWithRuleResult( token, targetVendor );
				break;
			case dbvmysql :
				if ( !( ( ch >= 'a' && ch <= 'z' ) || ( ch >= 'A' && ch <= 'Z' ) || ( ch >= '0' && ch <= '9' ) ) )
					return createBeginWithRuleResult( token, targetVendor );
				break;
			case dbvmssql :
				if ( !( ( ch >= 'a' && ch <= 'z' )
						|| ( ch >= 'A' && ch <= 'Z' )
						|| ch == '_'
						|| ch == '@' || ch == '#' ) )
					return createBeginWithRuleResult( token, targetVendor );
				break;
			case dbvpostgresql :
				if ( !( ( ch >= 'a' && ch <= 'z' ) || ( ch >= 'A' && ch <= 'Z' ) || ch == '_' ) )
					return createBeginWithRuleResult( token, targetVendor );
				break;
			case dbvoracle :
				if ( !( ( ch >= 'a' && ch <= 'z' ) || ( ch >= 'A' && ch <= 'Z' ) ) )
					return createBeginWithRuleResult( token, targetVendor );
				break;
		}
		return null;
	}

	private static IdentifierRuleResult createBeginWithRuleResult(
			TSourceToken token, EDbVendor targetVendor )
	{
		IdentifierRuleResult id = new IdentifierRuleResult( token, targetVendor );
		id.setRule( IdentifierRule.BeginWith );
		id.setCanTranslate( false );
		return id;
	}

	public static IdentifierRuleResult checkSubsequentCharactersRule(
			TSourceToken token, EDbVendor targetVendor )
	{
		if ( isQuotedIdentifier( token ) )
			return null;
		String identifier = removeQuote( token );
		switch ( targetVendor )
		{
			case dbvansi :
				String pattern = "[a-zA-Z][a-zA-Z0-9_]*";
				if ( !identifier.matches( pattern ) )
				{
					return createSubsequentCharactersRuleResult( token,
							targetVendor );
				}
				break;
			case dbvmysql :
				pattern = "[a-zA-Z0-9][a-zA-Z0-9_$]*";
				String numberPattern = "[0-9]+";
				if ( !( identifier.matches( pattern ) && !identifier.matches( numberPattern ) ) )
				{
					return createSubsequentCharactersRuleResult( token,
							targetVendor );
				}
				break;
			case dbvmssql :
				pattern = "[a-zA-Z_@#][a-zA-Z0-9_$@#]*";
				if ( !identifier.matches( pattern ) )
				{
					return createSubsequentCharactersRuleResult( token,
							targetVendor );
				}
				break;
			case dbvpostgresql :
				pattern = "[a-zA-Z_][a-zA-Z0-9_]*";
				if ( !identifier.matches( pattern ) )
				{
					return createSubsequentCharactersRuleResult( token,
							targetVendor );
				}
				break;
			case dbvoracle :
				pattern = "[a-zA-Z][a-zA-Z0-9_$#]*";
				if ( !identifier.matches( pattern ) )
				{
					return createSubsequentCharactersRuleResult( token,
							targetVendor );
				}
				break;
		}
		return null;
	}

	private static IdentifierRuleResult createSubsequentCharactersRuleResult(
			TSourceToken token, EDbVendor targetVendor )
	{
		IdentifierRuleResult id = new IdentifierRuleResult( token, targetVendor );
		id.setRule( IdentifierRule.SubsequentCharacters );
		id.setCanTranslate( false );
		return id;
	}

	public static IdentifierRuleResult checkKeywordRule( TSourceToken token,
			EDbVendor targetVendor )
	{
		if ( isQuotedIdentifier( token ) )
			return null;
		String identifier = removeQuote( token );
		switch ( targetVendor )
		{
			case dbvansi :
				if ( keywordChecker.isKeyword( identifier,
						EDbVendor.dbvansi,
						"SQL2008",
						true ) )
				{
					return createKeywordRuleResult( token, targetVendor );
				}
				break;
			case dbvmysql :
				if ( keywordChecker.isKeyword( identifier,
						EDbVendor.dbvmysql,
						"6.0",
						true ) )
				{
					return createKeywordRuleResult( token, targetVendor );
				}
				break;
			case dbvmssql :
				if ( keywordChecker.isKeyword( identifier,
						EDbVendor.dbvmssql,
						"12.0",
						true ) )
				{
					return createKeywordRuleResult( token, targetVendor );
				}
				break;
			case dbvpostgresql :
				if ( keywordChecker.isKeyword( identifier,
						EDbVendor.dbvpostgresql,
						"9.2",
						true ) )
				{
					return createKeywordRuleResult( token, targetVendor );
				}
				break;
			case dbvoracle :
				if ( keywordChecker.isKeyword( identifier,
						EDbVendor.dbvoracle,
						"11.2",
						true ) )
				{
					return createKeywordRuleResult( token, targetVendor );
				}
				break;
		}
		return null;
	}

	private static IdentifierRuleResult createKeywordRuleResult(
			TSourceToken token, EDbVendor targetVendor )
	{
		IdentifierRuleResult id = new IdentifierRuleResult( token, targetVendor );
		id.setRule( IdentifierRule.Keyword );
		id.setCanTranslate( true );
		return id;
	}

	private static IdentifierRuleResult createFunctionRuleResult(
			TSourceToken token, EDbVendor targetVendor, boolean canTranslate )
	{
		IdentifierRuleResult id = new IdentifierRuleResult( token, targetVendor );
		id.setRule( IdentifierRule.Function );
		id.setCanTranslate( canTranslate );
		return id;
	}

	private static IdentifierRuleResult createSpecialRuleResult(
			TSourceToken token, EDbVendor targetVendor, boolean canTranslate,
			String translationInfo )
	{
		IdentifierRuleResult id = new IdentifierRuleResult( token, targetVendor );
		id.setRule( IdentifierRule.Speical );
		id.setCanTranslate( canTranslate );
		id.setRuleMessage( translationInfo );
		return id;
	}

	private static boolean isAliasToken( TSourceToken token )
	{
		if ((token.getDbObjectType() == EDbObjectType.alias)||(token.getDbObjectType() == EDbObjectType.table_alias)) return true;

		int dbObjType = token.getDbObjType( );
		if ( dbObjType != TObjectName.ttobjColumnAlias
			//	&& dbObjType != TObjectName.ttObjTableAlias
			//	&& dbObjType != TObjectName.ttobjAliasName
		)
		{
			return false;
		}
		return true;
	}

	private static String removeQuote( TSourceToken token )
	{
		String content = token.astext.trim( );
		if ( ( content.startsWith( "`" ) && content.endsWith( "`" ) )
				|| ( content.startsWith( "\"" ) && content.endsWith( "\"" ) )
				|| ( content.startsWith( "[" ) && content.endsWith( "]" ) ) )
		{
			content = content.substring( 1, content.length( ) - 1 );
		}
		return content;
	}

	public static boolean isQuotedIdentifier( TSourceToken token )
	{
		String content = token.astext.trim( );
		if ( ( content.startsWith( "`" ) && content.endsWith( "`" ) )
				|| ( content.startsWith( "\"" ) && content.endsWith( "\"" ) )
				|| ( content.startsWith( "[" ) && content.endsWith( "]" ) ) )
		{
			return true;
		}
		return false;
	}

	public static IdentifierRuleResult checkQuotedIdentifierRule(
			TSourceToken token, EDbVendor targetVendor )
	{
		String content = token.astext.trim( );
		if ( ( content.startsWith( "`" ) && content.endsWith( "`" ) )
				&& targetVendor != EDbVendor.dbvmysql )
		{
			return createQuotedIdentifierRuleResult( token, targetVendor );
		}
		if ( ( content.startsWith( "[" ) && content.endsWith( "]" ) )
				&& targetVendor != EDbVendor.dbvmssql )
		{
			return createQuotedIdentifierRuleResult( token, targetVendor );
		}
		return null;
	}

	private static IdentifierRuleResult createQuotedIdentifierRuleResult(
			TSourceToken token, EDbVendor targetVendor )
	{
		IdentifierRuleResult id = new IdentifierRuleResult( token, targetVendor );
		id.setRule( IdentifierRule.QuotedIdentifier );
		id.setCanTranslate( true );
		return id;
	}

	public static IdentifierCheckResult checkIdentifier( TSourceToken token,
			EDbVendor targetVendor )
	{
		IdentifierCheckResult checkResult = new IdentifierCheckResult( token,
				targetVendor );
		checkResult.addCheckRuleResult( checkSizeRule( token, targetVendor ) );
		checkResult.addCheckRuleResult( checkBeginWithRule( token, targetVendor ) );
		checkResult.addCheckRuleResult( checkSubsequentCharactersRule( token,
				targetVendor ) );
		checkResult.addCheckRuleResult( checkQuotedIdentifierRule( token,
				targetVendor ) );
		try
		{
			if ( isFunction( token, targetVendor ) != null )
			{
				checkResult.addCheckRuleResult( createFunctionRuleResult( token,
						targetVendor,
						true ) );
			}
			else if ( isSpecial( token, targetVendor ) != null )
			{
				checkResult.addCheckRuleResult( createSpecialRuleResult( token,
						targetVendor,
						true,
						null ) );
			}
			else
			{
				checkResult.addCheckRuleResult( checkKeywordRule( token,
						targetVendor ) );
			}
		}
		catch ( FunctionIdentifierException e )
		{
			checkResult.addCheckRuleResult( createFunctionRuleResult( token,
					targetVendor,
					false ) );
		}
		catch ( SpecialIdentifierException e )
		{
			checkResult.addCheckRuleResult( createSpecialRuleResult( token,
					targetVendor,
					false,
					e.getMessage( ) ) );
		}

		if ( checkResult.canTranslate( ) )
		{
			checkResult.setTranslateResult( getTranslateResult( checkResult ) );
		}

		return checkResult;
	}

	public static String getTranslateResult( IdentifierCheckResult checkResult )
	{
		TSourceToken token = checkResult.getToken( );
		if ( checkResult.canTranslate( ) )
		{
			IdentifierRuleResult[] results = checkResult.getCheckRuleResults( );
			if ( results.length == 1 )
			{
				try
				{
					if ( results[0].getRule( ) == IdentifierRule.Function )
					{
						String result = isFunction( results[0].getToken( ),
								results[0].getTargetVendor( ) );
						if ( result != null )
							return result;
					}
				}
				catch ( Exception e )
				{
					return null;
				}

				switch ( checkResult.getTargetVendor( ) )
				{
					case dbvmysql :
						return "`" + removeQuote( token ) + "`";
					case dbvmssql :
						return "[" + removeQuote( token ) + "]";
					case dbvoracle :
					case dbvpostgresql :
					case dbvansi :
						return "\"" + removeQuote( token ) + "\"";
				}
			}
		}
		return null;
	}

	private static String isFunction( TSourceToken token, EDbVendor targetVendor )
			throws FunctionIdentifierException
	{
		if ( token.toString( ).equalsIgnoreCase( "CURRENT_DATE" )
				&& token.getDbvendor( ) == EDbVendor.dbvoracle )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return "SYSDATETIME()";
			}
			throw new FunctionIdentifierException( );
		}
		if ( token.toString( ).equalsIgnoreCase( "LOCALTIMESTAMP" )
				&& token.getDbvendor( ) == EDbVendor.dbvoracle )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return "SYSDATETIME()";
			}
			throw new FunctionIdentifierException( );
		}
		if ( token.toString( ).equalsIgnoreCase( "SYSTIMESTAMP" )
				&& token.getDbvendor( ) == EDbVendor.dbvoracle )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return "SYSDATETIMEOFFSET()";
			}
			throw new FunctionIdentifierException( );
		}

		return null;
	}

	private static String isSpecial( TSourceToken token, EDbVendor targetVendor )
			throws SpecialIdentifierException
	{
		if ( token.toString( ).equalsIgnoreCase( "DUAL" )
				&& token.getDbvendor( ) == EDbVendor.dbvoracle )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					throw new SpecialIdentifierException( "Dual is a table that is created by Oracle together with data dictionary. There is no DUAL table in SQL Server." );
				default :
					throw new SpecialIdentifierException( "Dual is a table that is created by Oracle together with data dictionary." );
			}
		}
		return null;
	}
}
