
package demos.sqltranslator;

import gudusoft.gsqlparser.EDbVendor;

public enum IdentifierRule {
	Size, SubsequentCharacters, BeginWith, QuotedIdentifier, Keyword, Function, Speical;

	public String getRuleMessage( EDbVendor targetVendor )
	{
		switch ( this )
		{
			case Size :
				return getSizeMessage( targetVendor );
			case SubsequentCharacters :
				return getSubsequentCharactersMessage( targetVendor );
			case BeginWith :
				return getBeginWithMessage( targetVendor );
			case QuotedIdentifier :
				return getQuotedIdentifierMessage( targetVendor );
			case Keyword :
				return getKeywordMessage( targetVendor );
		}
		return null;
	}

	public String getRuleMessage( String identifier, EDbVendor targetVendor )
	{
		switch ( this )
		{
			case Function :
				return getFunctionMessage( identifier, targetVendor );
		}
		return null;
	}

	private String getKeywordMessage( EDbVendor targetVendor )
	{
		return "Keyword can’t be used as nonquoted identifier.";
	}

	private String getFunctionMessage( String identifier, EDbVendor targetVendor )
	{
		return "Identifier " + identifier + " is a special function.";
	}

	private String getQuotedIdentifierMessage( EDbVendor targetVendor )
	{
		switch ( targetVendor )
		{
			case dbvoracle :
				return "The Oracle quoted identifier can only begins and ends with double quotation marks (\").";
			case dbvmssql :
				return "The SQL Server quoted identifier can only begins and ends with bracket marks ([]) or double quotation marks (\").";
			case dbvmysql :
				return "The MySQL quoted identifier can only begins and ends with backtick marks (`) or double quotation marks (\").";
			case dbvpostgresql :
				return "The PostgreSQL quoted identifier can only begins and ends with double quotation marks (\").";
			case dbvansi :
				return "The ANSI SQL quoted identifier can only begins and ends with double quotation marks (\").";

		}
		return null;
	}

	private String getBeginWithMessage( EDbVendor targetVendor )
	{
		switch ( targetVendor )
		{
			case dbvoracle :
				return "The Oracle nonquoted identifier can only begin with a letter.";
			case dbvmssql :
				return "The SQL Server nonquoted identifier can only begin with a letter, or underscore (_), at sign (@), pound sign (#) symbol.";
			case dbvmysql :
				return "The MySQL nonquoted identifier can only begin with a letter or number, cannot be composed entirely of numbers.";
			case dbvpostgresql :
				return "The PostgreSQL nonquoted identifier can only begin with a letter or underscore (_) symbol.";
			case dbvansi :
				return "The ANSI SQL nonquoted identifier can only begin with a letter.";

		}
		return null;
	}

	private String getSubsequentCharactersMessage( EDbVendor targetVendor )
	{
		switch ( targetVendor )
		{
			case dbvoracle :
				return "The Oracle nonquoted identifier can contain only number or character, and the underscore (_), pound sign (#), dollar sign ($) symbols.";
			case dbvmssql :
				return "The SQL Server nonquoted identifier can contain only number or character, and the underscore (_), at sign (@), pound sign (#), dollar sign ($) symbols.";
			case dbvmysql :
				return "The MySQL nonquoted identifier can contain only number or character, and the underscore (_), dollar sign ($) symbols.";
			case dbvpostgresql :
				return "The PostgreSQL nonquoted identifier can contain only number or character, and the underscore (_) symbol.";
			case dbvansi :
				return "The ANSI SQL nonquoted identifier can contain only number or character, and the underscore (_) symbol.";

		}
		return null;
	}

	private String getSizeMessage( EDbVendor targetVendor )
	{
		switch ( targetVendor )
		{
			case dbvoracle :
				return "The Oracle identifier's size limits to 30 bytes (number of characters depends on the character set).";
			case dbvmssql :
				return "The SQL Server identifier's size limits to 128 characters.";
			case dbvmysql :
				return "The MySQL identifier's size limits to 64 characters (aliases may be 255 characters).";
			case dbvpostgresql :
				return "The PostgreSQL identifier's size limits to 63 characters.";
			case dbvansi :
				return "The ANSI SQL identifier's size limits to 128 characters.";

		}
		return null;
	}
}
