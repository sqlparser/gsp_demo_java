
package demos.analyzesp.sybase;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableEffectType;
import gudusoft.gsqlparser.ETokenType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.TSourceTokenList;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.TColumnDefinitionList;
import gudusoft.gsqlparser.nodes.TExpressionList;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TObjectNameList;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TParseTreeNodeList;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TDeleteSqlStatement;
import gudusoft.gsqlparser.stmt.TDropTableSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import gudusoft.gsqlparser.stmt.TUseDatabase;
import gudusoft.gsqlparser.stmt.mssql.TMssqlBlock;
import gudusoft.gsqlparser.stmt.mssql.TMssqlCreateProcedure;
import gudusoft.gsqlparser.stmt.mssql.TMssqlDeclare;
import gudusoft.gsqlparser.stmt.mssql.TMssqlDropTable;
import gudusoft.gsqlparser.stmt.mssql.TMssqlExecute;
import gudusoft.gsqlparser.stmt.mssql.TMssqlIfElse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

public class Analyze_SP
{

	public static void main( String[] args )
	{
		if ( args.length == 0 )
		{
			System.out.println( "Usage: java Analyze_SP scriptfile [/o <output file path>] [/d <csv delimiter character>]" );
			System.out.println( "/o: Option, write the output stream to the specified file." );
			System.out.println( "/d: Option, set the csv delimiter character. The default delimiter character instanceof '|'." );
			System.out.println( "/a: Option, check all items." );
			System.out.println( "/r: Option, check the database object relations in the store procedure." );
			System.out.println( "/f: Option, check the built-in functions in the store procedure." );
			System.out.println( "/t: Option, check if the store procedure contains try catch clause." );
			return;
		}

		List array = Arrays.asList( args );

		List<File> files = new ArrayList<File>( );

		for ( int i = 0; i < array.size( ); i++ )
		{
			File file = new File( array.get( i ).toString( ) );
			if ( file.exists( ) )
				files.add( file );
			else
				break;
		}

		String outputFile = null;

		int index = array.indexOf( "/o" );

		if ( index != -1 && args.length > index + 1 )
		{
			outputFile = args[index + 1];
		}

		String delimiter = "|";

		index = array.indexOf( "/d" );

		if ( index != -1 && args.length > index + 1 )
		{
			delimiter = args[index + 1];
		}

		boolean checkTryCatchClause = false;
		boolean checkBuiltInFunction = false;
		boolean checkDBObjectRelations = false;

		if ( array.indexOf( "/a" ) != -1 )
		{
			checkTryCatchClause = true;
			checkBuiltInFunction = true;
			checkDBObjectRelations = true;
		}
		else
		{
			if ( array.indexOf( "/f" ) != -1 )
			{
				checkBuiltInFunction = true;
			}
			if ( array.indexOf( "/t" ) != -1 )
			{
				checkTryCatchClause = true;
			}
			if ( array.indexOf( "/r" ) != -1 )
			{
				checkDBObjectRelations = true;
			}
		}

		if ( checkBuiltInFunction == false && checkTryCatchClause == false )
		{
			checkDBObjectRelations = true;
		}

		Analyze_SP impact = new Analyze_SP( files, delimiter );
		impact.setCheckBuiltInFunction( checkBuiltInFunction );
		impact.setCheckTryCatchClause( checkTryCatchClause );
		impact.setCheckDBObjectRelation( checkDBObjectRelations );
		impact.analyzeSQL( );

		PrintStream writer = null;
		if ( outputFile != null )
		{
			try
			{
				writer = new PrintStream( new FileOutputStream( outputFile,
						false ) );
				System.setOut( writer );
			}
			catch ( FileNotFoundException e )
			{
				e.printStackTrace( );
			}

		}

		if ( impact.checkObjectRelation )
		{
			System.out.println( "Name of Analyzed Object"
					+ delimiter
					+ "Object Type"
					+ delimiter
					+ "Object Used"
					+ delimiter
					+ "Object Type"
					+ delimiter
					+ "Usage Type"
					+ delimiter
					+ "Columns" );
			System.out.println( impact.getDBObjectRelationsAnalysisResult( ) );
		}
		if ( impact.checkBuiltInFunction )
		{
			System.out.println( "File Name"
					+ delimiter
					+ "Built-in Function"
					+ delimiter
					+ "Line Number"
					+ delimiter
					+ "Column Number"
					+ delimiter
					+ "Usage Type"
					+ delimiter );
			System.out.println( impact.getBuiltInFunctionAnalysisResult( ) );
		}
		if ( impact.checkTryCatchClause )
		{
			System.out.println( "File Name"
					+ delimiter
					+ "Procedure"
					+ delimiter
					+ "With Try Catch" );
			System.out.println( impact.getTryCatchClauseAnalysisResult( ) );
		}

		if ( writer != null )
		{
			writer.close( );
		}

	}

	private StringBuilder relationBuffer = new StringBuilder( );
	private StringBuilder functionBuffer = new StringBuilder( );
	private StringBuilder tryCatchBuffer = new StringBuilder( );
	private Map spInfoMap = new HashMap( );
	private List<String> files = new ArrayList<String>( );
	private String delimiter;
	private boolean checkBuiltInFunction, checkTryCatchClause,
			checkObjectRelation;

	public Analyze_SP( List<File> sqlFiles, String delimiter )
	{
		this.delimiter = delimiter;
		if ( sqlFiles.size( ) > 0 )
		{
			for ( int i = 0; i < sqlFiles.size( ); i++ )
			{
				files.add( sqlFiles.get( i ).getAbsolutePath( ) );
				spInfo sp = new spInfo( );
				sp.file = sqlFiles.get( i ).getAbsolutePath( );
				spInfoMap.put( sqlFiles.get( i ).getAbsolutePath( ), sp );

			}
		}
	}

	void analyzeProcedure( procedureInfo procedureInfo,
			TMssqlCreateProcedure procedure )
	{
		for ( int i = 0; i < procedure.getStatements( ).size( ); i++ )
		{
			TCustomSqlStatement stmt = procedure.getStatements( ).get( i );
			analyzeSqlStatement( procedureInfo, stmt );
		}
	}

	public void analyzeSQL( )
	{
		for ( int i = 0; i < files.size( ); i++ )
		{
			TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvsybase );
			sqlparser.sqlfilename = files.get( i );
			int ret = sqlparser.parse( );
			if ( ret != 0 )
			{
				System.out.println( "Parse file "
						+ sqlparser.sqlfilename
						+ " failed." );
				System.out.println( sqlparser.getErrormessage( ) );
				continue;
			}
			spInfo sp = (spInfo) spInfoMap.get( files.get( i ) );
			analyzeSQL( sp, sqlparser );
		}
	}

	protected void analyzeSQL( spInfo spInfo, TGSqlParser sqlparser )
	{
		procedureInfo procedureInfo = new procedureInfo( );
		spInfo.procedures.add( procedureInfo );
		for ( int i = 0; i < sqlparser.sqlstatements.size( ); i++ )
		{
			TCustomSqlStatement sql = sqlparser.sqlstatements.get( i );
			if ( sql instanceof TUseDatabase )
			{
				spInfo.db = ( (TUseDatabase) sql ).getDatabaseName( ).toString( );

			}
			else if ( sql instanceof TMssqlCreateProcedure )
			{
				procedureInfo.name = ( (TMssqlCreateProcedure) sql ).getProcedureName( )
						.toString( );
				procedureInfo.objectType = objectType.SP;
				if ( checkObjectRelation )
				{
					analyzeProcedure( procedureInfo,
							(TMssqlCreateProcedure) sql );
				}
				if ( checkTryCatchClause )
				{
					checkTryCatchClause( procedureInfo,
							(TMssqlCreateProcedure) sql );
				}
			}
			else if ( procedureInfo != null )
			{
				analyzeSqlStatement( procedureInfo, sql );
			}

			if ( checkBuiltInFunction )
			{
				checkFunction( spInfo, sql.sourcetokenlist );
			}
		}
	}

	private void analyzeSqlStatement( procedureInfo procedureInfo,
			TCustomSqlStatement stmt )
	{
		if ( stmt instanceof TMssqlBlock )
		{
			TMssqlBlock block = (TMssqlBlock) stmt;
			if ( block.getBodyStatements( ) != null )
			{
				for ( int i = 0; i < block.getBodyStatements( ).size( ); i++ )
				{
					analyzeSqlStatement( procedureInfo,
							block.getBodyStatements( ).get( i ) );
				}
			}
		}
		else if ( stmt instanceof TMssqlIfElse )
		{
			TMssqlIfElse ifElse = (TMssqlIfElse) stmt;
			if ( ifElse.getStmt( ) != null )
			{
				analyzeSqlStatement( procedureInfo, ifElse.getStmt( ) );
			}
			if ( ifElse.getCondition( ) != null )
			{

			}
			if ( ifElse.getElseStmt( ) != null )
			{
				analyzeSqlStatement( procedureInfo, ifElse.getElseStmt( ) );
			}
		}
		else if ( stmt instanceof TMssqlDeclare )
		{
			TMssqlDeclare declareStmt = (TMssqlDeclare) stmt;
			if ( declareStmt.getSubquery( ) != null
					&& declareStmt.getSubquery( ).toString( ).trim( ).length( ) > 0 )
			{
				analyzeSqlStatement( procedureInfo, declareStmt.getSubquery( ) );
			}
		}
		else if ( stmt instanceof TMssqlExecute
				&& ( (TMssqlExecute) stmt ).getModuleName( ) != null )
		{
			TMssqlExecute executeStmt = (TMssqlExecute) stmt;
			operateInfo operateInfo = new operateInfo( );
			operateInfo.objectType = objectType.SP;
			operateInfo.objectUsed = executeStmt.getModuleName( )
					.toString( )
					.trim( );
			operateInfo.usageType = usageType.Exec;
			procedureInfo.operates.add( operateInfo );
		}
		else if ( stmt instanceof TCreateTableSqlStatement )
		{
			TCreateTableSqlStatement createStmt = (TCreateTableSqlStatement) stmt;
			TColumnDefinitionList columns = createStmt.getColumnList( );
			operateInfo operateInfo = new operateInfo( );
			operateInfo.objectType = objectType.Table;
			operateInfo.objectUsed = createStmt.getTargetTable( )
					.getFullName( )
					.trim( );
			operateInfo.usageType = usageType.Create;
			for ( int i = 0; i < columns.size( ); i++ )
			{
				TColumnDefinition column = columns.getColumn( i );
				operateInfo.columns.add( column.getColumnName( ).toString( ) );
			}
			procedureInfo.operates.add( operateInfo );

		}
		else if ( stmt instanceof TInsertSqlStatement )
		{
			TInsertSqlStatement insertStmt = (TInsertSqlStatement) stmt;
			TObjectNameList columns = insertStmt.getColumnList( );
			operateInfo operateInfo = new operateInfo( );
			operateInfo.objectType = objectType.Table;
			operateInfo.objectUsed = insertStmt.getTargetTable( )
					.getFullName( )
					.trim( );
			operateInfo.usageType = usageType.Insert;
			if ( columns != null )
			{
				for ( int i = 0; i < columns.size( ); i++ )
				{
					TObjectName column = columns.getObjectName( i );
					operateInfo.columns.add( column.toString( ) );
				}
			}
			procedureInfo.operates.add( operateInfo );

			// if (insertStmt.ExecStmt != null)
			// {
			// analyzeSqlStatement(procedureInfo, insertStmt.ExecStmt);
			// }
		}
		else if ( stmt instanceof TUpdateSqlStatement )
		{
			TUpdateSqlStatement updateStmt = (TUpdateSqlStatement) stmt;
			TResultColumnList columns = updateStmt.getResultColumnList( );
			operateInfo operateInfo = new operateInfo( );
			operateInfo.objectType = objectType.Table;
			operateInfo.objectUsed = updateStmt.getTargetTable( )
					.getFullName( )
					.trim( );
			operateInfo.usageType = usageType.Update;
			for ( int i = 0; i < columns.size( ); i++ )
			{
				TResultColumn column = columns.getResultColumn( i );
				operateInfo.columns.add( column.getExpr( )
						.getLeftOperand( )
						.toString( ) );
			}
			procedureInfo.operates.add( operateInfo );
		}
		else if ( stmt instanceof TDeleteSqlStatement )
		{
			TDeleteSqlStatement deleteStmt = (TDeleteSqlStatement) stmt;
			operateInfo operateInfo = new operateInfo( );
			operateInfo.objectType = objectType.Table;
			operateInfo.objectUsed = deleteStmt.getTargetTable( )
					.getFullName( )
					.trim( );
			operateInfo.usageType = usageType.Delete;
			procedureInfo.operates.add( operateInfo );
		}
		else if ( stmt instanceof TMssqlDropTable )
		{
			TMssqlDropTable dropStmt = (TMssqlDropTable) stmt;
			operateInfo operateInfo = new operateInfo( );
			operateInfo.objectType = objectType.Table;
			operateInfo.objectUsed = dropStmt.getTargetTable( )
					.getFullName( )
					.trim( );
			operateInfo.usageType = usageType.Drop;
			procedureInfo.operates.add( operateInfo );
		}
		else if ( stmt instanceof TDropTableSqlStatement )
		{
			TDropTableSqlStatement dropStmt = (TDropTableSqlStatement) stmt;
			operateInfo operateInfo = new operateInfo( );
			operateInfo.objectType = objectType.Table;
			operateInfo.objectUsed = dropStmt.getTableName( )
					.toString( )
					.trim( );
			operateInfo.usageType = usageType.Drop;
			procedureInfo.operates.add( operateInfo );
		}
		else if ( stmt instanceof TSelectSqlStatement )
		{
			TSelectSqlStatement selectStmt = (TSelectSqlStatement) stmt;
			List<columnInfo> columnInfos = new ArrayList<columnInfo>( );
			List<tableInfo> tableInfos = new ArrayList<tableInfo>( );
			tableTokensInStmt( columnInfos, tableInfos, selectStmt );
			Map columnMap = new HashMap( );
			for ( int i = 0; i < columnInfos.size( ); i++ )
			{
				columnInfo column = columnInfos.get( i );
				tableInfo table = column.table;
				if ( columnMap.containsKey( table ) )
				{
					List<columnInfo> columns = (List<columnInfo>) columnMap.get( table );
					boolean flag = false;
					for ( columnInfo temp : columns )
					{
						if ( temp.toString( )
								.equalsIgnoreCase( column.toString( ) ) )
						{
							flag = true;
							break;
						}
					}
					if ( !flag )
					{
						columns.add( column );
					}
				}
				else
				{
					List<columnInfo> columns = new ArrayList<columnInfo>( );
					columnMap.put( table, columns );
					columns.add( column );
				}
			}
			for ( int i = 0; i < tableInfos.size( ); i++ )
			{
				operateInfo operateInfo = new operateInfo( );
				operateInfo.objectType = objectType.Table;
				operateInfo.objectUsed = tableInfos.get( i ).toString( );
				if ( tableInfos.get( i ).stmt instanceof TSelectSqlStatement
						&& ( (TSelectSqlStatement) tableInfos.get( i ).stmt ).getIntoClause( ) != null )
					operateInfo.usageType = usageType.Insert;
				else
					operateInfo.usageType = usageType.Read;
				if ( columnMap.containsKey( tableInfos.get( i ) ) )
				{
					for ( columnInfo column : (List<columnInfo>) columnMap.get( tableInfos.get( i ) ) )
					{
						operateInfo.columns.add( column.toString( ) );
						operateInfo.objectUsed = column.table.toString( );
					}
				}
				procedureInfo.operates.add( operateInfo );
			}
		}
	}

	protected void checkFunction( spInfo spInfo, TSourceTokenList tokenList )
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
						builtInFunctionInfo function = new builtInFunctionInfo( );
						function.function = token.astext;
						function.lineNo = token.lineNo;
						function.columnNo = token.columnNo;
						TCustomSqlStatement stmt = token.stmt;
						if ( stmt == null )
						{
							boolean flag = false;
							for ( int k = token.posinlist - 1; k >= 0; k-- )
							{
								TSourceToken before = node.getGsqlparser( ).sourcetokenlist.get( k );
								if ( token.getNodesStartFromThisToken( ) != null )
								{
									for ( int z = 0; z < before.getNodesStartFromThisToken( )
											.size( ); z++ )
									{
										if ( before.getNodesStartFromThisToken( )
												.get( z ) instanceof TCustomSqlStatement )
										{
											TCustomSqlStatement tempStmt = (TCustomSqlStatement) before.getNodesStartFromThisToken( )
													.get( z );
											if ( tempStmt.getStartToken( ).posinlist <= token.posinlist
													&& tempStmt.getEndToken( ).posinlist >= token.posinlist )
											{
												stmt = tempStmt;
												flag = true;
												break;
											}
										}
									}
								}
								if ( flag )
									break;
							}
						}
						if ( stmt instanceof TInsertSqlStatement )
						{
							function.stmtType = usageType.Insert;
						}
						else if ( stmt instanceof TSelectSqlStatement )
						{
							function.stmtType = usageType.Read;
						}
						else if ( stmt instanceof TUpdateSqlStatement )
						{
							function.stmtType = usageType.Update;
						}
						else if ( stmt instanceof TDeleteSqlStatement )
						{
							function.stmtType = usageType.Delete;
						}
						else if ( stmt instanceof TMssqlDropTable )
						{
							function.stmtType = usageType.Drop;
						}
						else if ( stmt instanceof TDropTableSqlStatement )
						{
							function.stmtType = usageType.Drop;
						}
						else if ( stmt instanceof TMssqlExecute )
						{
							function.stmtType = usageType.Exec;
						}
						else if ( stmt instanceof TMssqlCreateProcedure )
						{
							function.stmtType = usageType.Create;
						}
						spInfo.functions.add( function );
					}
				}
			}
		}
	}

	protected void checkTryCatchClause( procedureInfo procedureInfo,
			TMssqlCreateProcedure procedure )
	{
		TSourceTokenList tokenList = procedure.sourcetokenlist;
		for ( int i = 0; i < tokenList.size( ); i++ )
		{
			TSourceToken token = tokenList.get( i );
			if ( token.tokentype == ETokenType.ttkeyword
					&& token.astext.trim( ).equalsIgnoreCase( "try" ) )
			{
				procedureInfo.hasTryCatch = true;
			}
		}
	}

	public String getDBObjectRelationsAnalysisResult( )
	{
		if ( relationBuffer.length( ) == 0 && files != null )
		{
			for ( String file : files )
			{
				spInfo spInfo = (spInfo) spInfoMap.get( file );
				for ( procedureInfo procedure : spInfo.procedures )
				{
					for ( operateInfo info : procedure.operates )
					{
						StringBuilder builder = new StringBuilder( );
						for ( int i = 0; i < info.columns.size( ); i++ )
						{
							builder.append( info.columns.get( i ) );
							if ( i < info.columns.size( ) - 1 )
							{
								builder.append( "," );
							}
						}
						relationBuffer.append( procedure.name )
								.append( delimiter )
								.append( procedure.objectType )
								.append( delimiter )
								.append( getTableName( spInfo, info ) )
								.append( delimiter )
								.append( info.objectType )
								.append( delimiter )
								.append( info.usageType )
								.append( delimiter )
								.append( builder )
								.append( "\r\n" );
					}

				}
			}
		}
		return relationBuffer.toString( );
	}

	private String getTableName( spInfo spInfo, operateInfo info )
	{
		return spInfo.db != null && info.objectType == objectType.Table ? ( info.objectUsed.toLowerCase( )
				.startsWith( spInfo.db.toLowerCase( ) ) ? info.objectUsed
				: ( spInfo.db + ".." + info.objectUsed ) )
				: info.objectUsed;
	}

	public String getTryCatchClauseAnalysisResult( )
	{
		if ( tryCatchBuffer.length( ) == 0 && files != null )
		{
			for ( String file : files )
			{
				spInfo spInfo = (spInfo) spInfoMap.get( file );
				for ( procedureInfo procedure : spInfo.procedures )
				{
					tryCatchBuffer.append( new File( file ).getName( ) )
							.append( delimiter )
							.append( procedure.name )
							.append( delimiter )
							.append( procedure.hasTryCatch ? "Yes" : "No" )
							.append( "\r\n" );
				}
			}
		}
		return tryCatchBuffer.toString( );
	}

	public String getBuiltInFunctionAnalysisResult( )
	{
		if ( functionBuffer.length( ) == 0 && files != null )
		{
			for ( String file : files )
			{
				spInfo spInfo = (spInfo) spInfoMap.get( file );
				for ( builtInFunctionInfo function : spInfo.functions )
				{
					functionBuffer.append( new File( file ).getName( ) )
							.append( delimiter )
							.append( function.function )
							.append( delimiter )
							.append( function.lineNo )
							.append( delimiter )
							.append( function.columnNo )
							.append( delimiter )
							.append( function.stmtType )
							.append( "\r\n" );
				}
			}
		}
		return functionBuffer.toString( );
	}

	public void setCheckBuiltInFunction( boolean checkBuiltInFunction )
	{
		this.checkBuiltInFunction = checkBuiltInFunction;
	}

	public void setCheckDBObjectRelation( boolean checkObjectRelation )
	{
		this.checkObjectRelation = checkObjectRelation;
	}

	public void setCheckTryCatchClause( boolean checkTryCatchClause )
	{
		this.checkTryCatchClause = checkTryCatchClause;
	}

	protected void tableTokensInStmt( List<columnInfo> columnInfos,
			List<tableInfo> tableInfos, TCustomSqlStatement stmt )
	{
		for ( int i = 0; i < stmt.tables.size( ); i++ )
		{
			if ( stmt.tables.getTable( i ).isBaseTable( ) )
			{
				if ( ( stmt.dbvendor == EDbVendor.dbvmssql )
						&& ( ( stmt.tables.getTable( i ).getFullName( ).equalsIgnoreCase( "deleted" ) ) || ( stmt.tables.getTable( i )
								.getFullName( ).equalsIgnoreCase( "inserted" ) ) ) )
				{
					continue;
				}

				if ( stmt.tables.getTable( i ).getEffectType( ) == ETableEffectType.tetSelectInto )
				{
					continue;
				}
				
				tableInfo tableInfo = new tableInfo( );
				tableInfo.fullName = stmt.tables.getTable( i ).getFullName( );
				tableInfos.add( tableInfo );

				for ( int j = 0; j < stmt.tables.getTable( i ).getLinkedColumns()
						.size( ); j++ )
				{

					columnInfo columnInfo = new columnInfo( );
					columnInfo.table = tableInfo;
					columnInfo.column = stmt.tables.getTable( i ).getLinkedColumns().getObjectName(j);
					columnInfos.add( columnInfo );
				}
			}
		}

		if ( stmt instanceof TSelectSqlStatement
				&& ( (TSelectSqlStatement) stmt ).getIntoClause( ) != null )
		{
			TExpressionList tables = ( (TSelectSqlStatement) stmt ).getIntoClause( )
					.getExprList( );
			for ( int j = 0; j < tables.size( ); j++ )
			{
				tableInfo tableInfo = new tableInfo( );
				tableInfo.fullName = tables.getExpression( j ).toString( );
				tableInfo.stmt = stmt;
				tableInfos.add( tableInfo );
			}
		}

		for ( int i = 0; i < stmt.getStatements( ).size( ); i++ )
		{
			tableTokensInStmt( columnInfos, tableInfos, stmt.getStatements( )
					.get( i ) );
		}
	}

}

class builtInFunctionInfo
{

	public String function;
	public long lineNo, columnNo;
	public usageType stmtType;
}

class columnInfo
{

	public tableInfo table;
	public TObjectName column;

	public String toString( )
	{
		return column == null ? "" : column.getColumnNameOnly( ).trim( );
	}
};

enum objectType {
	SP, Table
};

class operateInfo
{

	public String objectUsed;
	public objectType objectType;
	public usageType usageType;
	public List<String> columns = new ArrayList<String>( );
}

class procedureInfo
{

	public String name;
	public objectType objectType;
	public List<operateInfo> operates = new ArrayList<operateInfo>( );
	public boolean hasTryCatch;

	public procedureInfo( )
	{
		objectType = objectType.Table;
	}
}

class spInfo
{

	public String file;
	public String db;
	public List<procedureInfo> procedures = new ArrayList<procedureInfo>( );
	public List<builtInFunctionInfo> functions = new ArrayList<builtInFunctionInfo>( );
}

class tableInfo
{

	public String fullName;

	public TCustomSqlStatement stmt;

	public String toString( )
	{
		return ( fullName == null ? "" : fullName.trim( ) );
	}
}

enum usageType {
	Exec, Read, Insert, Update, Create, Delete, Drop
}
