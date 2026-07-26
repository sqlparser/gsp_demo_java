
package demos.sqltranslator;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETokenType;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.TSourceTokenList;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TParseTreeNodeList;
import gudusoft.gsqlparser.nodes.TTypeName;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class SqlTranslator
{

	private EDbVendor targetVendor;

	private boolean translate;

	/**
	 * @param args
	 */
	public static void main( String[] args )
	{

		if ( args.length < 3 )
		{
			System.out.println( "Usage: java SqlTranslator scriptfile [source] [target] [/t] [/d] [/o <output file path>]" );
			System.out.println( "source: Must, set the source database type. Support oracle, mysql, mssql." );
			System.out.println( "target: Must, set the target database type. Support oracle, mysql, mssql." );
			System.out.println( "/t: Option, translate the sql file." );
			System.out.println( "/o: Option, write the output stream to the specified file." );
			System.out.println( "/d: Option, write the transltion detail result to the specified file." );
			return;
		}

		EDbVendor sourceVendor = null;
		if ( args[1].equalsIgnoreCase( "mssql" ) )
		{
			sourceVendor = EDbVendor.dbvmssql;
		}
		else if ( args[1].equalsIgnoreCase( "oracle" ) )
		{
			sourceVendor = EDbVendor.dbvoracle;
		}
		else if ( args[1].equalsIgnoreCase( "mysql" ) )
		{
			sourceVendor = EDbVendor.dbvmysql;
		}
		else
		{
			System.out.println( "The source database type only support oracle, mysql, mssql.\r\n" );
			System.out.println( "Usage: java SqlTranslator scriptfile [source] [target] [/t] [/d] [/o <output file path>]" );
			System.out.println( "source: Must, set the source database type. Support oracle, mysql, mssql." );
			System.out.println( "target: Must, set the target database type. Support oracle, mysql, mssql." );
			System.out.println( "/t: Option, translate the sql file." );
			System.out.println( "/o: Option, write the output stream to the specified file." );
			System.out.println( "/d: Option, write the transltion detail result to the specified file." );
			return;
		}

		EDbVendor targetVendor = null;
		if ( args[2].equalsIgnoreCase( "mssql" ) )
		{
			targetVendor = EDbVendor.dbvmssql;
		}
		else if ( args[2].equalsIgnoreCase( "oracle" ) )
		{
			targetVendor = EDbVendor.dbvoracle;
		}
		else if ( args[2].equalsIgnoreCase( "mysql" ) )
		{
			targetVendor = EDbVendor.dbvmysql;
		}
		else
		{
			System.out.println( "The target database type only support oracle, mysql, mssql.\r\n" );
			System.out.println( "Usage: java SqlTranslator scriptfile [source] [target] [/t] [/d] [/o <output file path>]" );
			System.out.println( "source: Must, set the source database type. Support oracle, mysql, mssql." );
			System.out.println( "target: Must, set the target database type. Support oracle, mysql, mssql." );
			System.out.println( "/t: Option, translate the sql file." );
			System.out.println( "/o: Option, write the output stream to the specified file." );
			System.out.println( "/d: Option, write the transltion detail result to the specified file." );
			return;
		}

		List<String> argList = Arrays.asList( args );

		boolean translate = argList.indexOf( "/t" ) != -1;

		boolean printDetail = argList.indexOf( "/d" ) != -1;

		String outputFile = null;

		int index = argList.indexOf( "/o" );

		if ( index != -1 && args.length > index + 1 )
		{
			outputFile = args[index + 1];
		}

		FileOutputStream writer = null;

		SqlTranslator transltor = new SqlTranslator( new File( args[0] ),
				sourceVendor,
				targetVendor,
				translate );

		if ( outputFile != null )
		{
			try
			{
				writer = new FileOutputStream( outputFile );
				System.setOut( new PrintStream( writer ) );
			}
			catch ( FileNotFoundException e )
			{
				e.printStackTrace( );
			}
		}

		if ( transltor.getCheckResult( false ) != null )
		{
			if ( translate )
			{
				if ( outputFile == null )
				{
					System.out.println( transltor.getCheckResult( false ) );
				}
				else if ( printDetail )
				{
					System.out.println( "/****************************************************************************************************************************" );
					System.out.println( transltor.getCheckResult( false )
							.trim( ) );
					System.out.println( "****************************************************************************************************************************/" );
				}

				if ( transltor.getTranslateResult( ) != null )
					System.out.print( transltor.getTranslateResult( ) );
			}
			else
			{
				System.out.print( transltor.getCheckResult( false ) );
			}
		}
		try
		{
			if ( writer != null )
			{
				writer.close( );
			}
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
	}

	private String getTranslateResult( )
	{
		if ( sqlparser != null && sqlparser.getErrorCount( ) == 0 )
		{
			return getSqlText( );
		}
		return null;
	}

	private String checkResult;

	private String getCheckResult( boolean force )
	{
		if ( checkResult != null && !force )
		{
			return checkResult;
		}

		if ( sqlparser != null && sqlparser.getErrorCount( ) == 0 )
		{
			StringBuffer buffer = new StringBuffer( );
			int totalIdentifier = 0;
			int translateIdentifier = 0;
			if ( identifierCheckResults.size( ) > 0 )
			{
				for ( int i = 0; i < identifierCheckResults.size( ); i++ )
				{
					IdentifierCheckResult result = identifierCheckResults.get( i );
					totalIdentifier++;
					if ( result.canTranslate( ) )
						translateIdentifier++;
					buffer.append( "Identifier "
							+ result.getOriginalText( )
							+ " need to be translated.\r\n" );
					buffer.append( "Location: "
							+ result.getOriginalLineNo( )
							+ ", "
							+ result.getOriginalColumnNo( )
							+ "\r\n" );
					buffer.append( "Transltion reason: "
							+ result.getTranslationInfo( )
							+ "\r\n" );
					if ( result.canTranslate( ) )
					{
						buffer.append( "Tool can translate: "
								+ ( result.canTranslate( ) ? "Yes" : "No" )
								+ "\r\n" );
						buffer.append( "Translation result: "
								+ result.getTranslateResult( )
								+ "\r\n" );
					}
					else
					{
						buffer.append( "Tool can translate: "
								+ ( result.canTranslate( ) ? "Yes" : "No" )
								+ "\r\n" );
					}

					buffer.append( "\r\n" );
				}
			}

			int totalKeyword = 0;
			int translateKeyword = 0;
			if ( keywordCheckResults.size( ) > 0 )
			{
				if ( identifierCheckResults.size( ) > 0 )
					buffer.append( "\r\n" );
				for ( int i = 0; i < keywordCheckResults.size( ); i++ )
				{
					KeywordCheckResult result = keywordCheckResults.get( i );
					totalKeyword++;
					if ( result.canTranslate( ) )
						translateKeyword++;
					if ( result.getTreeNode( ) instanceof TCustomSqlStatement )
					{
						buffer.append( "Statement " )
								.append( result.getOriginalTreeNodeText( ) )
								.append( " need to be translated.\r\n" );
						buffer.append( "Keyword Location: "
								+ result.getOriginalLineNo( )
								+ ", "
								+ result.getOriginalColumnNo( )
								+ "\r\n" );

					}
					else
					{
						buffer.append( ( isKeyword( result.getToken( ) ) ? "Keyword "
								: "Keyword " )
								+ ( result.getOriginalTreeNodeText( ) != null ? result.getOriginalTreeNodeText( )
										: result.getOriginalText( ) )
								+ " need to be translated.\r\n" );

						buffer.append( "Location: "
								+ result.getOriginalLineNo( )
								+ ", "
								+ result.getOriginalColumnNo( )
								+ "\r\n" );

					}

					buffer.append( "Transltion reason: "
							+ result.getTranslationInfo( )
							+ "\r\n" );
					if ( result.canTranslate( ) )
					{
						buffer.append( "Tool can translate: "
								+ ( result.canTranslate( ) ? "Yes" : "No" )
								+ "\r\n" );
						buffer.append( "Translation result: "
								+ result.getTranslateResult( )
								+ "\r\n" );
					}
					else
					{
						buffer.append( "Tool can translate: "
								+ ( result.canTranslate( ) ? "Yes" : "No" )
								+ "\r\n" );
					}

					buffer.append( "\r\n" );
				}
			}

			int totalDataType = 0;
			int translateDataType = 0;

			if ( dataTypeCheckResults.size( ) > 0 )
			{
				if ( keywordCheckResults.size( ) > 0 )
					buffer.append( "\r\n" );
				for ( int i = 0; i < dataTypeCheckResults.size( ); i++ )
				{
					DataTypeCheckResult result = dataTypeCheckResults.get( i );
					totalDataType++;
					if ( result.canTranslate( ) )
						translateDataType++;

					buffer.append( "Data type "
							+ result.getOriginalText( )
							+ " need to be translated.\r\n" );
					buffer.append( "Location: "
							+ result.getOriginalLineNo( )
							+ ", "
							+ result.getOriginalColumnNo( )
							+ "\r\n" );
					if ( result.canTranslate( ) )
					{
						buffer.append( "Tool can translate: "
								+ ( result.canTranslate( ) ? "Yes" : "No" )
								+ "\r\n" );
						String translate = result.getTranslateResult( );
						if ( translate != null )
						{
							buffer.append( "Translation result: "
									+ result.getTranslateResult( )
									+ "\r\n" );
						}
					}
					else
					{
						buffer.append( "Tool can translate: "
								+ ( result.canTranslate( ) ? "Yes" : "No" )
								+ "\r\n" );
					}

					buffer.append( "\r\n" );
				}
			}

			int totalFunction = 0;
			int translateFunction = 0;

			if ( functionCheckResults.size( ) > 0 )
			{
				if ( dataTypeCheckResults.size( ) > 0 )
					buffer.append( "\r\n" );
				for ( int i = 0; i < functionCheckResults.size( ); i++ )
				{
					FunctionCheckResult result = functionCheckResults.get( i );
					totalFunction++;
					if ( result.canTranslate( ) )
						translateFunction++;

					buffer.append( "Function "
							+ result.getOriginalText( )
							+ " need to be translated.\r\n" );
					buffer.append( "Location: "
							+ result.getOriginalLineNo( )
							+ ", "
							+ result.getOriginalColumnNo( )
							+ "\r\n" );
					if ( result.canTranslate( ) )
					{
						buffer.append( "Tool can translate: "
								+ ( result.canTranslate( ) ? "Yes" : "No" )
								+ "\r\n" );
						String translate = result.getTranslateResult( );
						if ( translate != null )
						{
							buffer.append( "Translation result: "
									+ result.getTranslateResult( )
									+ "\r\n" );
						}
					}
					else
					{
						buffer.append( "Tool can translate: "
								+ ( result.canTranslate( ) ? "Yes" : "No" )
								+ "\r\n" );
					}

					buffer.append( "\r\n" );

				}
			}

			StringBuffer totalInfo = new StringBuffer( );
			if ( sqlparser.getSqlfilename( ) != null )
			{
				totalInfo.append( "Check the input sql file "
						+ sqlparser.getSqlfilename( )
						+ "\r\n" );
			}
			totalInfo.append( "Found "
					+ totalIdentifier
					+ " identifier"
					+ ( totalIdentifier > 1 ? "s" : "" )
					+ " need to be translated, "
					+ translateIdentifier
					+ " identifier"
					+ ( translateIdentifier > 1 ? "s" : "" )
					+ ( translate ? " had been translated by tool, "
							: " can be translated by tool, " )
					+ ( totalIdentifier - translateIdentifier )
					+ " identifier"
					+ ( ( totalIdentifier - translateIdentifier ) > 1 ? "s"
							: "" )
					+ " need to be translated by handy.\r\n" );

			totalInfo.append( "Found "
					+ totalKeyword
					+ " keyword"
					+ ( totalKeyword > 1 ? "s" : "" )
					+ " need to be translated, "
					+ translateKeyword
					+ " keyword"
					+ ( translateKeyword > 1 ? "s" : "" )
					+ ( translate ? " had been translated by tool, "
							: " can be translated by tool, " )
					+ ( totalKeyword - translateKeyword )
					+ " keyword"
					+ ( ( totalKeyword - translateKeyword ) > 1 ? "s" : "" )
					+ " need to be translated by handy.\r\n" );

			totalInfo.append( "Found "
					+ totalDataType
					+ " data type"
					+ ( totalDataType > 1 ? "s" : "" )
					+ " need to be translated, "
					+ translateDataType
					+ " data type"
					+ ( translateDataType > 1 ? "s" : "" )
					+ ( translate ? " had been translated by tool, "
							: " can be translated by tool, " )
					+ ( totalDataType - translateDataType )
					+ " data type"
					+ ( ( totalDataType - translateDataType ) > 1 ? "s" : "" )
					+ " need to be translated by handy.\r\n" );

			totalInfo.append( "Found "
					+ totalFunction
					+ " function"
					+ ( totalFunction > 1 ? "s" : "" )
					+ " need to be translated, "
					+ translateFunction
					+ " function"
					+ ( translateDataType > 1 ? "s" : "" )
					+ ( translate ? " had been translated by tool, "
							: " can be translated by tool, " )
					+ ( totalFunction - translateFunction )
					+ " function"
					+ ( ( totalFunction - translateFunction ) > 1 ? "s" : "" )
					+ " need to be translated by handy.\r\n\r\n" );

			buffer.insert( 0, totalInfo );

			checkResult = buffer.toString( );
			return checkResult;
		}
		checkResult = null;
		return checkResult;
	}

	public SqlTranslator( File file, EDbVendor sourceVendor,
			EDbVendor targetVendor, boolean translate )
	{
		this.targetVendor = targetVendor;
		this.translate = translate;
		sqlparser = new TGSqlParser( sourceVendor );
		sqlparser.sqlfilename = file.getAbsolutePath( );
		checkSQL( sqlparser );
		convertAndTranslate( );
	}

	public SqlTranslator( String sql, EDbVendor sourceVendor,
			EDbVendor targetVendor, boolean translator )
	{
		this.targetVendor = targetVendor;
		this.translate = translator;
		sqlparser = new TGSqlParser( sourceVendor );
		sqlparser.sqltext = sql;
		checkSQL( sqlparser );
		convertAndTranslate( );
	}

	private void convertAndTranslate( )
	{
		getCheckResult( true );

		if ( translate )
		{
			boolean convertJoin = false;
			for ( int i = 0; i < keywordCheckResults.size( ); i++ )
			{
				KeywordCheckResult result = keywordCheckResults.get( i );
				if ( result.getTreeNode( ) instanceof TSelectSqlStatement )
				{
					result.translate( );
					convertJoin = true;
				}
			}
			if ( convertJoin )
			{
				sqlparser.sqltext = getSqlText( );
				identifierCheckResults.clear( );
				dataTypeCheckResults.clear( );
				functionCheckResults.clear( );
				keywordCheckResults.clear( );

				checkSQL( sqlparser );
			}

			FunctionCheckResult result = null;
			while ( ( result = getFunctionTranslate( ) ) != null )
			{
				result.translate( );
				sqlparser.sqltext = getSqlText( );
				identifierCheckResults.clear( );
				dataTypeCheckResults.clear( );
				functionCheckResults.clear( );
				keywordCheckResults.clear( );

				checkSQL( sqlparser );
			}

			translate( sqlparser );
		}
	}

	private FunctionCheckResult getFunctionTranslate( )
	{
		for ( int i = 0; i < functionCheckResults.size( ); i++ )
		{
			FunctionCheckResult result = functionCheckResults.get( i );
			if ( result.canTranslate( ) )
				return result;
		}
		return null;
	}

	private String getSqlText( )
	{
		StringBuffer buffer = new StringBuffer( );
		for ( int i = 0; i < sqlparser.sourcetokenlist.size( ); i++ )
		{
			buffer.append( sqlparser.sourcetokenlist.get( i ).toString( ) );
		}
		return buffer.toString( );
	}

	private void checkSQL( TGSqlParser sqlparser )
	{
		int ret = sqlparser.parse( );

		if ( ret != 0 )
		{
			System.err.println( sqlparser.getErrormessage( ) );
		}
		else
		{
			TSourceTokenList tokenList = sqlparser.sourcetokenlist;
			checkIdentifier( tokenList );
			checkDataType( tokenList );
			checkFunction( tokenList );
		}

	}

	private void checkDataType( TSourceTokenList tokenList )
	{
		for ( int i = 0; i < tokenList.size( ); i++ )
		{
			TSourceToken token = tokenList.get( i );
			if ( token.tokencode == TBaseType.rrw_create
					&& token.stmt instanceof TCreateTableSqlStatement )
			{
				TCreateTableSqlStatement stmt = (TCreateTableSqlStatement) token.stmt;
				for ( int j = 0; j < stmt.getColumnList( ).size( ); j++ )
				{
					TColumnDefinition column = stmt.getColumnList( )
							.getColumn( j );
					TTypeName dataType = column.getDatatype( );
					DataTypeCheckResult result = DataTypeChecker.checkDataType( dataType,
							targetVendor );
					if ( result != null )
					{
						dataTypeCheckResults.add( result );
					}
				}
			}
		}
	}

	private List<IdentifierCheckResult> identifierCheckResults = new ArrayList<IdentifierCheckResult>( );
	private List<DataTypeCheckResult> dataTypeCheckResults = new ArrayList<DataTypeCheckResult>( );
	private List<FunctionCheckResult> functionCheckResults = new ArrayList<FunctionCheckResult>( );
	private List<KeywordCheckResult> keywordCheckResults = new ArrayList<KeywordCheckResult>( );

	public IdentifierCheckResult[] getIdentifierCheckResults( )
	{
		if ( identifierCheckResults.isEmpty( ) )
			return null;
		return identifierCheckResults.toArray( new IdentifierCheckResult[0] );
	}

	private TGSqlParser sqlparser;

	private void checkIdentifier( TSourceTokenList tokenList )
	{
		for ( int i = 0; i < tokenList.size( ); i++ )
		{
			TSourceToken token = tokenList.get( i );
			if ( token.tokentype == ETokenType.ttidentifier
					|| IdentifierChecker.isQuotedIdentifier( token ) )
			{
				IdentifierCheckResult result = IdentifierChecker.checkIdentifier( token,
						targetVendor );
				if ( result.needTranslate( ) )
				{
					identifierCheckResults.add( result );
				}
			}
			else if ( isKeyword( token ) )
			{
				KeywordCheckResult result = KeywordChecker.checkKeyword( token,
						targetVendor );
				if ( result != null )
				{
					keywordCheckResults.add( result );
				}
			}
		}
	}

	private boolean isKeyword( TSourceToken token )
	{
		return token.tokentype == ETokenType.ttkeyword;
	}

	private void checkFunction( TSourceTokenList tokenList )
	{
		for ( int i = 0; i < tokenList.size( ); i++ )
		{
			TSourceToken token = tokenList.get( i );
			if ( token.getDbObjType( ) == TObjectName.ttobjFunctionName )
			{
				Stack<TParseTreeNode> list = token.getNodesStartFromThisToken( );
				for ( int j = 0; j < list.size( ); j++ )
				{
					TParseTreeNode node = (TParseTreeNode) list.get( j );
					if ( node instanceof TFunctionCall )
					{
						FunctionCheckResult result = FunctionChecker.checkFunction( (TFunctionCall) node,
								targetVendor );
						if ( result != null )
						{
							functionCheckResults.add( result );
						}
					}
				}
			}
		}
	}

	private void translate( TGSqlParser sqlparser )
	{
		if ( sqlparser != null && sqlparser.getErrorCount( ) == 0 )
		{
			for ( int i = 0; i < identifierCheckResults.size( ); i++ )
			{
				IdentifierCheckResult result = identifierCheckResults.get( i );
				result.translate( );
			}

			for ( int i = 0; i < keywordCheckResults.size( ); i++ )
			{
				KeywordCheckResult result = keywordCheckResults.get( i );
				result.translate( );
			}

			for ( int i = 0; i < dataTypeCheckResults.size( ); i++ )
			{
				DataTypeCheckResult result = dataTypeCheckResults.get( i );
				result.translate( );
			}

			for ( int i = 0; i < functionCheckResults.size( ); i++ )
			{
				FunctionCheckResult result = functionCheckResults.get( i );
				result.translate( );
			}
		}
	}
}
