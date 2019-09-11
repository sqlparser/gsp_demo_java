
package demos.tracedatalineage;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.TDeclareVariable;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TParameterDeclaration;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TStoredProcedureSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import gudusoft.gsqlparser.stmt.mssql.TMssqlBlock;
import gudusoft.gsqlparser.stmt.mssql.TMssqlDeclare;
import gudusoft.gsqlparser.stmt.mssql.TMssqlExecute;
import gudusoft.gsqlparser.stmt.mssql.TMssqlFetch;
import gudusoft.gsqlparser.stmt.mssql.TMssqlIfElse;
import gudusoft.gsqlparser.stmt.TUseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class traceDataLineage
{

	public static void main( String[] args )
	{
		if ( args.length == 0 )
		{
			System.out.println( "Usage: java traceDataLineage <sql scripts directory path> <output file path>" );
			System.out.println( "sql scripts directory path: The sql files directory will be analyzed." );
			System.out.println( "output file path: Option, write the analysis result to the specified file." );
			return;
		}

		String outputFile = null;
		Writer writer = null;
		String ddlPath = null;
		if ( args.length == 1 )
		{
			ddlPath = args[0];
		}
		else if ( args.length >= 2 )
		{
			ddlPath = args[0];
			outputFile = args[1];
		}
		try
		{
			if ( outputFile != null )
			{
				File file = new File( outputFile );
				if ( file.exists( ) )
					file.delete( );
				writer = new FileWriter( outputFile, true );
			}
			else
			{
				writer = new StringWriter( );
			}

			SqlFileList sqlFileList = new SqlFileList( ddlPath );
			List<String> sqlFiles = sqlFileList.sqlfiles;
			List<InputStream> streams = new ArrayList<InputStream>( );
			for ( int i = 0; i < sqlFiles.size( ); i++ )
			{
				streams.add( new FileInputStream( sqlFiles.get( i ) ) );
			}
			traceDataLineage trace = new traceDataLineage( streams );

			StringBuffer buffer = new StringBuffer( );
			for ( int i = 0; i < trace.getTracedLineage( ).size( ); i++ )
			{
				List<Column> lineage = trace.getTracedLineage( ).get( i );
				for ( int j = 0; j < lineage.size( ); j++ )
				{
					buffer.append( trace.getColumnFullName( lineage.get( j ) ) );
					if ( j < lineage.size( ) - 1 )
					{
						buffer.append( "\t----->\t" );
					}
					else
					{
						buffer.append( "\r\n" );
					}
				}
			}
			writer.append( buffer.toString( ) );
			writer.close( );
			if ( writer instanceof StringWriter )
			{
				System.out.println( ( (StringWriter) writer ).getBuffer( ) );
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
	}

	private String database = "null";
	private HashMap<String, HashMap<String, Table>> datasourceMap = new HashMap<String, HashMap<String, Table>>( );
	private List<List<Column>> tracedLineage = new ArrayList<List<Column>>( );
	private HashMap<String, HashMap<String, Procedure>> procedureMap = new HashMap<String, HashMap<String, Procedure>>( );
	private HashMap<String, List<Statement>> statementMap = new HashMap<String, List<Statement>>( );
	private List<TraceTarget> leafTargets = new ArrayList<TraceTarget>( );

	public List<List<Column>> getTracedLineage( )
	{
		return tracedLineage;
	}

	public traceDataLineage( List<InputStream> streams )
	{
		List<TGSqlParser> parsers = new ArrayList<TGSqlParser>( );
		for ( int i = 0; i < streams.size( ); i++ )
		{
			TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvmssql );
			sqlparser.setSqlInputStream( streams.get( i ) );
			int result = sqlparser.parse( );
			if ( result != 0 )
			{
				System.err.println( sqlparser.getErrormessage( ) );
			}
			else
			{
				parsers.add( sqlparser );
			}
		}

		for ( int i = 0; i < parsers.size( ); i++ )
		{
			analyzeTableSql( parsers.get( i ) );
		}

		for ( int i = 0; i < parsers.size( ); i++ )
		{
			analyzeViewSql( parsers.get( i ) );
		}

		for ( int i = 0; i < parsers.size( ); i++ )
		{
			analyzeProcedureDeclareSql( parsers.get( i ) );
		}

		for ( int i = 0; i < parsers.size( ); i++ )
		{
			analyzeProcedureCallSql( parsers.get( i ) );
		}

		for ( int i = 0; i < parsers.size( ); i++ )
		{
			analyzeStatementCallSql( parsers.get( i ) );
		}

		List list = computeDependencyRelation( );

		for ( int i = 0; i < list.size( ); i++ )
		{
			Object obj = list.get( i );
			if ( obj instanceof Table )
			{
				Table table = (Table) obj;
				Iterator<Column> iter = table.getColumns( )
						.values( )
						.iterator( );
				while ( iter.hasNext( ) )
				{
					Column column = iter.next( );
					boolean exist = false;
					for ( int j = 0; j < tracedLineage.size( ); j++ )
					{
						List<Column> lineage = tracedLineage.get( j );
						if ( lineage.contains( column ) )
						{
							exist = true;
							break;
						}
					}
					if ( !exist )
					{
						leafTargets.clear( );
						TraceTarget model = new TraceTarget( );
						model.setSource( column );
						leafTargets.add( model );
						traceSource( model, list, i );
						for ( int j = 0; j < leafTargets.size( ); j++ )
						{
							List<Column> lineage = getDataLineage( leafTargets.get( j ) );
							if ( lineage.size( ) > 1 )
							{
								tracedLineage.add( lineage );
							}
						}
					}
				}
			}
		}
	}

	private void analyzeProcedureCallSql( TGSqlParser sqlparser )
	{
		database = "null";
		if ( !statementMap.containsKey( database ) )
		{
			statementMap.put( database, new ArrayList<Statement>( ) );
		}
		for ( int i = 0; i < sqlparser.sqlstatements.size( ); i++ )
		{
			Object stmt = sqlparser.sqlstatements.get( i );
			if ( stmt instanceof TUseDatabase)
			{
				database = ( (TUseDatabase) stmt ).toString( )
						.replaceFirst( "(?i)USE", "" )
						.trim( );
			}
			if ( stmt instanceof TStoredProcedureSqlStatement )
			{
				buildProcedureCall( (TStoredProcedureSqlStatement) stmt );
			}
		}
	}

	private void analyzeStatementCallSql( TGSqlParser sqlparser )
	{
		database = "null";
		if ( !statementMap.containsKey( database ) )
		{
			statementMap.put( database, new ArrayList<Statement>( ) );
		}
		for ( int i = 0; i < sqlparser.sqlstatements.size( ); i++ )
		{
			Object stmt = sqlparser.sqlstatements.get( i );
			if ( stmt instanceof TUseDatabase)
			{
				database = ( (TUseDatabase) stmt ).toString( )
						.replaceFirst( "(?i)USE", "" )
						.trim( );
				if ( !statementMap.containsKey( database ) )
				{
					statementMap.put( database, new ArrayList<Statement>( ) );
				}
			}
			if ( stmt instanceof TInsertSqlStatement
					|| stmt instanceof TUpdateSqlStatement )
			{
				buildStatementCall( (TCustomSqlStatement) stmt );
			}
		}
	}

	private void analyzeProcedureDeclareSql( TGSqlParser sqlparser )
	{
		database = "null";
		if ( !procedureMap.containsKey( database ) )
		{
			procedureMap.put( database, new HashMap<String, Procedure>( ) );
		}
		for ( int i = 0; i < sqlparser.sqlstatements.size( ); i++ )
		{
			Object stmt = sqlparser.sqlstatements.get( i );
			if ( stmt instanceof TUseDatabase)
			{
				database = ( (TUseDatabase) stmt ).toString( )
						.replaceFirst( "(?i)USE", "" )
						.trim( );

				if ( !procedureMap.containsKey( database ) )
				{
					procedureMap.put( database,
							new HashMap<String, Procedure>( ) );
				}
			}
			if ( stmt instanceof TStoredProcedureSqlStatement )
			{
				buildProcedure( (TStoredProcedureSqlStatement) stmt );
			}
		}
	}

	private void analyzeTableSql( TGSqlParser sqlparser )
	{
		database = "null";
		if ( !datasourceMap.containsKey( database ) )
		{
			datasourceMap.put( database, new HashMap<String, Table>( ) );
		}
		for ( int i = 0; i < sqlparser.sqlstatements.size( ); i++ )
		{
			Object stmt = sqlparser.sqlstatements.get( i );
			if ( stmt instanceof TUseDatabase)
			{
				database = ( (TUseDatabase) stmt ).toString( )
						.replaceFirst( "(?i)USE", "" )
						.trim( );
				if ( !datasourceMap.containsKey( database ) )
				{
					datasourceMap.put( database, new HashMap<String, Table>( ) );
				}
			}
			if ( stmt instanceof TCreateTableSqlStatement )
			{
				buildTable( (TCreateTableSqlStatement) stmt );
			}
		}
	}

	private void analyzeViewSql( TGSqlParser sqlparser )
	{
		database = "null";
		if ( !datasourceMap.containsKey( database ) )
		{
			datasourceMap.put( database, new HashMap<String, Table>( ) );
		}
		for ( int i = 0; i < sqlparser.sqlstatements.size( ); i++ )
		{
			Object stmt = sqlparser.sqlstatements.get( i );
			if ( stmt instanceof TUseDatabase)
			{
				database = ( (TUseDatabase) stmt ).toString( )
						.replaceFirst( "(?i)USE", "" )
						.trim( );
				if ( !datasourceMap.containsKey( database ) )
				{
					datasourceMap.put( database, new HashMap<String, Table>( ) );
				}
			}
			if ( stmt instanceof TCreateViewSqlStatement )
			{
				buildView( (TCreateViewSqlStatement) stmt );
			}
		}
	}

	private void buildProcedure( TStoredProcedureSqlStatement stmt )
	{
		Procedure procedure = new Procedure( );
		procedure.setName( getProcedureName( stmt ) );
		procedure.setFullName( stmt.getStoredProcedureName( ).toString( ) );

		LinkedHashMap<String, Parameter> params = new LinkedHashMap<String, Parameter>( );
		LinkedHashMap<String, Variable> variables = new LinkedHashMap<String, Variable>( );
		LinkedHashMap<String, Cursor> cursors = new LinkedHashMap<String, Cursor>( );
		if ( stmt.getParameterDeclarations( ) != null )
		{
			for ( int i = 0; i < stmt.getParameterDeclarations( ).size( ); i++ )
			{
				TParameterDeclaration declaration = stmt.getParameterDeclarations( )
						.getParameterDeclarationItem( i );
				Parameter param = new Parameter( );
				param.setName( declaration.getParameterName( ).toString( ) );
				params.put( param.getName( ), param );
				variables.put( param.getName( ), param );
			}
		}
		if ( stmt.getBodyStatements( ) != null )
		{
			for ( int i = 0; i < stmt.getBodyStatements( ).size( ); i++ )
			{
				TCustomSqlStatement bodyStmt = stmt.getBodyStatements( )
						.get( i );
				if ( bodyStmt instanceof TMssqlDeclare )
				{
					TMssqlDeclare declare = ( (TMssqlDeclare) bodyStmt );
					if ( declare.getVariables( ) != null )
					{
						for ( int j = 0; j < declare.getVariables( ).size( ); j++ )
						{
							TDeclareVariable declaration = declare.getVariables( )
									.getDeclareVariable( j );
							Variable variable = new Variable( );
							variable.setName( declaration.getVariableName( )
									.toString( ) );
							variables.put( variable.getName( ), variable );
						}
					}
					Pattern pattern = Pattern.compile( "(?i)declare.+cursor" );
					Matcher match = pattern.matcher( declare.toString( ) );
					if ( match.find( ) )
					{
						Cursor cursor = new Cursor( );
						cursor.setName( match.group( )
								.replaceFirst( "(?i)declare", "" )
								.replaceAll( "(?i)cursor", "" )
								.trim( ) );
						cursor.setProcedure( procedure );
						cursor.setColumns( new LinkedHashMap<String, Column>( ) );
						if ( declare.getSubquery( ) != null )
						{
							TSelectSqlStatement select = declare.getSubquery( );

							for ( int j = 0; j < select.getResultColumnList( )
									.size( ); j++ )
							{
								TResultColumn resultColumn = select.getResultColumnList( )
										.getResultColumn( j );
								List<Column> columns = exprToColumn( resultColumn.getExpr( ),
										select );
								for ( Column column : columns )
								{
									cursor.getColumns( )
											.put( column.getName( ), column );
								}
							}
						}
						cursors.put( cursor.getName( ), cursor );
					}
				}
			}

			for ( int i = 0; i < stmt.getBodyStatements( ).size( ); i++ )
			{
				TCustomSqlStatement bodyStmt = stmt.getBodyStatements( )
						.get( i );
				if ( bodyStmt instanceof TMssqlFetch )
				{
					TMssqlFetch fetch = ( (TMssqlFetch) bodyStmt );
					if ( fetch.getCursorName( ) == null )
						continue;
					String cursorName = fetch.getCursorName( ).toString( );
					Cursor cursor = cursors.get( cursorName );
					if ( cursor.getParams( ) == null )
					{
						cursor.setParams( new LinkedHashMap<String, Variable>( ) );
					}
					if ( fetch.getVariableNames( ) != null )
					{
						for ( int x = 0; x < fetch.getVariableNames( ).size( ); x++ )
						{
							String var = fetch.getVariableNames( )
									.getObjectName( x )
									.toString( );
							Variable variable = variables.get( var );
							cursor.getParams( ).put( variable.getName( ),
									variable );
						}
					}
				}
			}
		}
		procedure.setCursors( cursors );
		procedure.setParams( params );
		procedure.setVariables( variables );

		procedureMap.get( database ).put( procedure.getName( ), procedure );
	}

	private void buildProcedureCall( Procedure procedure,
			TCustomSqlStatement stmt )
	{
		if ( stmt instanceof TMssqlIfElse )
		{
			TMssqlIfElse conditionStmt = (TMssqlIfElse) stmt;
			if ( conditionStmt.getStatements( ) != null )
			{
				for ( int j = 0; j < conditionStmt.getStatements( ).size( ); j++ )
				{
					TCustomSqlStatement subStmt = conditionStmt.getStatements( )
							.get( j );
					buildProcedureCall( procedure, subStmt );
				}
			}
		}
		else if ( stmt instanceof TMssqlBlock )
		{
			TMssqlBlock block = (TMssqlBlock) stmt;
			if ( block.getBodyStatements( ) != null )
			{
				for ( int i = 0; i < block.getBodyStatements( ).size( ); i++ )
				{
					buildProcedureCall( procedure, block.getBodyStatements( )
							.get( i ) );
				}
			}
		}
		else if ( stmt instanceof TMssqlExecute )
		{
			TMssqlExecute exec = (TMssqlExecute) stmt;
			if ( exec.getModuleName( ) != null )
			{
				Procedure target = parseProcedureName( exec.getModuleName( )
						.toString( ) );
				if ( target != null )
				{
					Execute execute = new Execute( );
					execute.setContainer( procedure );
					execute.setTarget( target );
					if ( exec.getParameters( ) != null )
					{
						execute.setVariables( new LinkedHashMap<String, Variable>( ) );
						for ( int i = 0; i < exec.getParameters( ).size( ); i++ )
						{
							String paramName = exec.getParameters( )
									.getExecParameter( i )
									.getParameterValue( )
									.toString( );
							Variable variable = procedure.getVariables( )
									.get( paramName );
							execute.getVariables( ).put( variable.getName( ),
									variable );
						}
					}

					if ( procedure.getExecutes( ) == null )
					{
						procedure.setExecutes( new LinkedHashMap<Procedure, Execute>( ) );
					}
					procedure.getExecutes( )
							.put( execute.getTarget( ), execute );
				}
			}
		}
		else if ( stmt instanceof TInsertSqlStatement )
		{
			TInsertSqlStatement insert = (TInsertSqlStatement) stmt;
			TTable target = insert.getTargetTable( );
			String fullName = target.getFullName( );
			Table table = parseTableName( fullName );
			if ( table != null )
			{
				List<Column> columns = new ArrayList<Column>( );
				if ( insert.getColumnList( ) != null )
				{
					for ( int i = 0; i < insert.getColumnList( ).size( ); i++ )
					{
						String columnName = getColumnName( insert.getColumnList( )
								.getObjectName( i )
								.toString( ) );
						Column column = table.getColumns( ).get( columnName );
						columns.add( column );
					}
				}
				else
				{
					String[] columnNames = (String[]) table.getColumns( )
							.keySet( )
							.toArray( new String[0] );
					for ( int i = 0; i < table.getColumns( ).size( ); i++ )
					{
						String columnName = columnNames[i];
						Column column = table.getColumns( ).get( columnName );
						columns.add( column );
					}
				}
				if ( insert != null && insert.getValues( ) != null )
				{
					TableRelation relation = new TableRelation( );
					relation.setTable( table );
					if ( relation.getRelations( ) == null )
					{
						relation.setRelations( new HashMap( ) );
					}

					for ( int i = 0; i < insert.getValues( ).size( ); i++ )
					{
						TResultColumnList columnList = insert.getValues( )
								.getMultiTarget( i )
								.getColumnList( );
						for ( int j = 0; j < columnList.size( ); j++ )
						{
							String value = columnList.getResultColumn( j )
									.toString( );
							Variable var = procedure.getVariables( )
									.get( value );
							if ( var != null && columns.size( ) > j )
							{
								relation.getRelations( ).put( var,
										columns.get( j ) );
							}
						}
					}
					if ( procedure.getTableRelations( ) == null )
					{
						procedure.setTableRelations( new ArrayList<TableRelation>( ) );
					}
					procedure.getTableRelations( ).add( relation );
				}
				else if ( insert != null
						&& insert.getSubQuery( ) instanceof TSelectSqlStatement )
				{
					TableRelation relation = new TableRelation( );
					relation.setTable( table );
					if ( relation.getRelations( ) == null )
					{
						relation.setRelations( new HashMap( ) );
					}

					TSelectSqlStatement select = (TSelectSqlStatement) insert.getSubQuery( );
					TResultColumnList columnList = select.getResultColumnList( );
					for ( int j = 0; j < columnList.size( ); j++ )
					{
						String value = columnList.getResultColumn( j )
								.toString( );
						Variable var = procedure.getVariables( ).get( value );
						if ( var != null && columns.size( ) > j )
						{
							relation.getRelations( )
									.put( var, columns.get( j ) );
						}
						else
						{
							TResultColumn resultColumn = columnList.getResultColumn( j );
							List<Column> sourceColumns = exprToColumn( resultColumn.getExpr( ),
									select );
							for ( Column column : sourceColumns )
							{
								relation.getRelations( ).put( column,
										columns.get( j ) );
							}
						}
					}
					if ( procedure.getTableRelations( ) == null )
					{
						procedure.setTableRelations( new ArrayList<TableRelation>( ) );
					}
					procedure.getTableRelations( ).add( relation );
				}
			}
		}
		else if ( stmt instanceof TUpdateSqlStatement )
		{
			TUpdateSqlStatement update = (TUpdateSqlStatement) stmt;
			TTable target = update.getTargetTable( );
			String fullName = target.getFullName( );
			Table table = parseTableName( fullName );
			if ( table != null )
			{
				if ( update.getResultColumnList( ) != null )
				{
					TableRelation relation = new TableRelation( );
					relation.setTable( table );
					if ( relation.getRelations( ) == null )
					{
						relation.setRelations( new HashMap( ) );
					}
					for ( int j = 0; j < update.getResultColumnList( ).size( ); j++ )
					{
						TResultColumn result = update.getResultColumnList( )
								.getResultColumn( j );
						TExpression expr = result.getExpr( );
						Variable var = procedure.getVariables( )
								.get( expr.getRightOperand( ).toString( ) );
						Column column = table.getColumns( )
								.get( expr.getLeftOperand( ).toString( ) );
						if ( var != null && column != null )
						{
							relation.getRelations( ).put( var, column );
						}
						else
						{
							List<Column> sourceColumns = exprToColumn( expr.getRightOperand( ),
									update );
							for ( Column temp : sourceColumns )
							{
								relation.getRelations( ).put( temp, column );
							}
						}
					}
					if ( procedure.getTableRelations( ) == null )
					{
						procedure.setTableRelations( new ArrayList<TableRelation>( ) );
					}
					procedure.getTableRelations( ).add( relation );
				}
			}
		}
	}

	private void buildProcedureCall( TStoredProcedureSqlStatement stmt )
	{
		if ( stmt.getBodyStatements( ) != null )
		{
			for ( int i = 0; i < stmt.getBodyStatements( ).size( ); i++ )
			{
				TCustomSqlStatement bodyStmt = stmt.getBodyStatements( )
						.get( i );
				buildProcedureCall( parseProcedureName( stmt ), bodyStmt );
			}
		}
	}

	private void buildStatementCall( TCustomSqlStatement stmt )
	{
		Statement statement = new Statement( );
		if ( stmt instanceof TInsertSqlStatement )
		{
			TInsertSqlStatement insert = (TInsertSqlStatement) stmt;
			TTable target = insert.getTargetTable( );
			String fullName = target.getFullName( );
			Table table = parseTableName( fullName );
			if ( table != null )
			{
				List<Column> columns = new ArrayList<Column>( );
				if ( insert.getColumnList( ) != null )
				{
					for ( int i = 0; i < insert.getColumnList( ).size( ); i++ )
					{
						String columnName = getColumnName( insert.getColumnList( )
								.getObjectName( i )
								.toString( ) );
						Column column = table.getColumns( ).get( columnName );
						columns.add( column );
					}
				}
				else
				{
					String[] columnNames = (String[]) table.getColumns( )
							.keySet( )
							.toArray( new String[0] );
					for ( int i = 0; i < table.getColumns( ).size( ); i++ )
					{
						String columnName = columnNames[i];
						Column column = table.getColumns( ).get( columnName );
						columns.add( column );
					}
				}
				if ( insert != null && insert.getValues( ) != null )
				{
					TableRelation relation = new TableRelation( );
					relation.setTable( table );
					if ( relation.getRelations( ) == null )
					{
						relation.setRelations( new HashMap( ) );
					}
					if ( statement.getTableRelations( ) == null )
					{
						statement.setTableRelations( new ArrayList<TableRelation>( ) );
					}
					statement.getTableRelations( ).add( relation );
				}
				else if ( insert != null
						&& insert.getSubQuery( ) instanceof TSelectSqlStatement )
				{
					TableRelation relation = new TableRelation( );
					relation.setTable( table );
					if ( relation.getRelations( ) == null )
					{
						relation.setRelations( new HashMap( ) );
					}

					TSelectSqlStatement select = (TSelectSqlStatement) insert.getSubQuery( );
					TResultColumnList columnList = select.getResultColumnList( );
					for ( int j = 0; j < columnList.size( ); j++ )
					{
						String value = columnList.getResultColumn( j )
								.toString( );

						TResultColumn resultColumn = columnList.getResultColumn( j );
						List<Column> sourceColumns = exprToColumn( resultColumn.getExpr( ),
								select );
						for ( Column column : sourceColumns )
						{
							relation.getRelations( ).put( column,
									columns.get( j ) );
						}

					}
					if ( statement.getTableRelations( ) == null )
					{
						statement.setTableRelations( new ArrayList<TableRelation>( ) );
					}
					statement.getTableRelations( ).add( relation );
				}
			}
		}
		else if ( stmt instanceof TUpdateSqlStatement )
		{
			TUpdateSqlStatement update = (TUpdateSqlStatement) stmt;
			TTable target = update.getTargetTable( );
			String fullName = target.getFullName( );
			Table table = parseTableName( fullName );
			if ( table != null )
			{
				if ( update.getResultColumnList( ) != null )
				{
					TableRelation relation = new TableRelation( );
					relation.setTable( table );
					if ( relation.getRelations( ) == null )
					{
						relation.setRelations( new HashMap( ) );
					}
					for ( int j = 0; j < update.getResultColumnList( ).size( ); j++ )
					{
						TResultColumn result = update.getResultColumnList( )
								.getResultColumn( j );
						TExpression expr = result.getExpr( );
						Column column = table.getColumns( )
								.get( expr.getLeftOperand( ).toString( ) );

						List<Column> sourceColumns = exprToColumn( expr.getRightOperand( ),
								update );
						for ( Column temp : sourceColumns )
						{
							relation.getRelations( ).put( temp, column );
						}

					}
					if ( statement.getTableRelations( ) == null )
					{
						statement.setTableRelations( new ArrayList<TableRelation>( ) );
					}
					statement.getTableRelations( ).add( relation );
				}
			}
		}
		if ( statement.getTableRelations( ) != null
				&& statement.getTableRelations( ).size( ) > 0 )
		{
			statementMap.get( database ).add( statement );
		}
	}

	private void buildTable( TCreateTableSqlStatement stmt )
	{
		Table table = new Table( );
		table.setDatabase( database );
		table.setName( stmt.getTargetTable( ).getName( ) );
		table.setFullName( stmt.getTargetTable( ).getFullName( ) );
		LinkedHashMap<String, Column> columns = new LinkedHashMap<String, Column>( );
		for ( int i = 0; i < stmt.getColumnList( ).size( ); i++ )
		{
			TColumnDefinition columnDefinition = stmt.getColumnList( )
					.getColumn( i );
			Column column = new Column( );
			column.setContainer( table );
			column.setName( columnDefinition.getColumnName( ).toString( ) );
			column.setType( columnDefinition.getDatatype( ).toString( ) );
			columns.put( column.getName( ), column );
		}
		table.setColumns( columns );
		datasourceMap.get( database ).put( table.getName( ), table );
	}

	private void buildView( TCreateViewSqlStatement stmt )
	{
		View view = new View( );
		view.setDatabase( database );
		view.setName( stmt.getViewName( ).toString( ) );
		LinkedHashMap<String, Column> columns = new LinkedHashMap<String, Column>( );
		if ( stmt.getSubquery( ) != null )
		{
			for ( int i = 0; i < stmt.getSubquery( )
					.getResultColumnList( )
					.size( ); i++ )
			{
				TResultColumn resultColumn = stmt.getSubquery( )
						.getResultColumnList( )
						.getResultColumn( i );
				Column column = new Column( );
				column.setContainer( view );
				if ( resultColumn.getAliasClause( ) != null )
				{
					column.setFullName( resultColumn.getAliasClause( )
							.getAliasName( )
							.toString( ) );
				}
				else
				{
					column.setFullName( resultColumn.toString( ) );
				}
				Table table = getTable( resultColumn, stmt.getSubquery( ) );
				String columnName = getColumnName( resultColumn );
				column.setName( columnName );
				if ( table != null )
				{
					Map<String, Column> columnMap = table.getColumns( );
					if ( columnMap != null )
					{
						Column temp = columnMap.get( columnName );
						if ( temp != null )
						{
							column.setType( temp.getType( ) );
							column.setReferTable( table );
						}
					}
				}
				columns.put( column.getName( ), column );
			}
			view.setColumns( columns );
			datasourceMap.get( database ).put( view.getName( ), view );
		}
	}

	private void computeStmtDependencyRelation( List list )
	{
		for ( int j = 0; j < list.size( ); j++ )
		{
			Object obj = list.get( j );
			if ( obj instanceof Procedure )
			{
				Procedure procedure = (Procedure) obj;
				if ( procedure.getTableRelations( ) != null
						&& procedure.getTableRelations( ).size( ) > 0 )
				{
					for ( int k = 0; k < procedure.getTableRelations( ).size( ); k++ )
					{
						TableRelation relation = procedure.getTableRelations( )
								.get( k );
						Map relationMap = relation.getRelations( );
						Iterator<Map.Entry> iter = relationMap.entrySet( )
								.iterator( );
						while ( iter.hasNext( ) )
						{
							Map.Entry entry = iter.next( );
							if ( entry.getKey( ) instanceof Column
									&& entry.getValue( ) instanceof Column )
							{
								Table sourceTable = ( (Column) entry.getKey( ) ).getContainer( );
								Table targetTable = ( (Column) entry.getValue( ) ).getContainer( );
								if ( !list.contains( sourceTable ) )
								{
									list.add( j, sourceTable );
									j++;
								}
								if ( !list.contains( targetTable ) )
								{
									list.add( j + 1, sourceTable );
								}
							}
						}
					}
				}
			}
		}
	}

	private void computeStatementRelation( List list )
	{
		for ( int j = 0; j < list.size( ); j++ )
		{
			Object obj = list.get( j );
			if ( obj instanceof Statement )
			{
				Statement stmt = (Statement) obj;
				if ( stmt.getTableRelations( ) != null
						&& stmt.getTableRelations( ).size( ) > 0 )
				{
					for ( int k = 0; k < stmt.getTableRelations( ).size( ); k++ )
					{
						TableRelation relation = stmt.getTableRelations( )
								.get( k );
						Map relationMap = relation.getRelations( );
						Iterator<Map.Entry> iter = relationMap.entrySet( )
								.iterator( );
						while ( iter.hasNext( ) )
						{
							Map.Entry entry = iter.next( );
							if ( entry.getKey( ) instanceof Column
									&& entry.getValue( ) instanceof Column )
							{
								Table sourceTable = ( (Column) entry.getKey( ) ).getContainer( );
								Table targetTable = ( (Column) entry.getValue( ) ).getContainer( );
								if ( !list.contains( sourceTable ) )
								{
									list.add( j, sourceTable );
									j++;
								}
								if ( !list.contains( targetTable ) )
								{
									list.add( j + 1, sourceTable );
								}
							}
						}
					}
				}
			}
		}
	}

	private void computeCursorDependencyRelation( List<Table> tables, List list )
	{
		for ( int i = 0; i < tables.size( ); i++ )
		{
			Table table = tables.get( i );
			for ( int j = 0; j < list.size( ); j++ )
			{
				boolean find = false;
				Object obj = list.get( j );
				if ( obj instanceof Procedure )
				{
					Procedure procedure = (Procedure) obj;
					if ( procedure.getCursors( ) != null
							&& procedure.getCursors( ).size( ) > 0 )
					{
						Cursor[] cursors = procedure.getCursors( )
								.values( )
								.toArray( new Cursor[0] );
						for ( int k = 0; k < cursors.length; k++ )
						{
							Table container = cursors[k].getColumns( )
									.values( )
									.iterator( )
									.next( )
									.getContainer( );
							if ( table == container && !list.contains( table ) )
							{
								list.add( j, table );
								find = true;
								break;
							}
						}
					}
				}
				if ( find )
					break;
			}
		}
	}

	private List computeDependencyRelation( )
	{
		Iterator<HashMap<String, Procedure>> iter = procedureMap.values( )
				.iterator( );
		List<Procedure> procedures = new ArrayList<Procedure>( );
		while ( iter.hasNext( ) )
		{
			procedures.addAll( Arrays.asList( iter.next( )
					.values( )
					.toArray( new Procedure[0] ) ) );
		}

		Iterator<HashMap<String, Table>> tableIter = datasourceMap.values( )
				.iterator( );
		List<Table> tables = new ArrayList<Table>( );
		while ( tableIter.hasNext( ) )
		{
			tables.addAll( Arrays.asList( tableIter.next( )
					.values( )
					.toArray( new Table[0] ) ) );
		}

		List list = new ArrayList( );
		for ( int i = 0; i < procedures.size( ); i++ )
		{
			Procedure procedure = procedures.get( i );
			computeProcedureDependencyRelation( list, procedure );
		}

		List[] stmtList = (List[]) statementMap.values( ).toArray( new List[0] );
		List<Statement> statements = new ArrayList<Statement>( );
		for ( int i = 0; i < stmtList.length; i++ )
		{
			statements.addAll( stmtList[i] );
		}
		for ( int i = 0; i < statements.size( ); i++ )
		{
			Statement stmt = statements.get( i );
			list.add( stmt );
		}

		computeStmtDependencyRelation( list );
		computeCursorDependencyRelation( tables, list );
		computeExecuteDependencyRelation( tables, list );
		computeStatementRelation( list );

		for ( int i = 0; i < tables.size( ); i++ )
		{
			if ( !list.contains( tables.get( i ) ) )
			{
				list.add( list.size( ), tables.get( i ) );
			}
		}

		sortTable( tables, list );

		return list;
	}

	private void computeExecuteDependencyRelation( List<Table> tables, List list )
	{
		for ( int i = 0; i < tables.size( ); i++ )
		{
			Table table = tables.get( i );
			for ( int j = list.size( ) - 1; j >= 0; j-- )
			{
				boolean find = false;
				Object obj = list.get( j );
				if ( obj instanceof Procedure )
				{
					Procedure procedure = (Procedure) obj;
					if ( procedure.getTableRelations( ) != null
							&& procedure.getTableRelations( ).size( ) > 0 )
					{
						TableRelation[] relations = procedure.getTableRelations( )
								.toArray( new TableRelation[0] );
						for ( int k = 0; k < relations.length; k++ )
						{
							Table relationTable = relations[k].getTable( );
							if ( table == relationTable
									&& !list.contains( table ) )
							{
								list.add( j + 1, table );
								find = true;
								break;
							}
						}
					}
				}
				if ( find )
					break;
			}
		}
	}

	private void computeProcedureDependencyRelation( List list,
			Procedure procedure )
	{
		if ( procedure.getExecutes( ) == null
				|| procedure.getExecutes( ).size( ) == 0 )
		{
			if ( !list.contains( procedure ) )
			{
				list.add( list.size( ), procedure );
			}
		}
		else
		{
			List<Procedure> targets = Arrays.asList( procedure.getExecutes( )
					.keySet( )
					.toArray( new Procedure[0] ) );
			for ( int j = 0; j < targets.size( ); j++ )
			{
				computeProcedureDependencyRelation( list, targets.get( j ) );
			}

			if ( !list.contains( procedure ) )
			{
				int index = list.size( ) - 1;
				for ( int j = list.size( ) - 1; j >= 0; j-- )
				{
					if ( targets.contains( list.get( j ) ) )
					{
						index = j;
					}
				}
				list.add( index, procedure );
			}
		}
	}

	public String getColumnFullName( Column column )
	{
		Table table = column.getContainer( );
		String database = table.getDatabase( );
		if ( "null".equals( database ) || database == null )
		{
			return table.getName( ) + "." + column.getName( );
		}
		else
			return database + "." + table.getName( ) + "." + column.getName( );
	}

	String getColumnName( String fullName )
	{
		if ( fullName.indexOf( '.' ) > -1 )
		{
			return fullName.substring( fullName.lastIndexOf( '.' ) + 1 );
		}
		else
			return fullName;
	}

	private String getColumnName( TResultColumn resultColumn )
	{
		if ( resultColumn.toString( ).indexOf( '.' ) > -1 )
		{
			return resultColumn.toString( ).substring( resultColumn.toString( )
					.lastIndexOf( '.' ) + 1 );
		}
		else
			return resultColumn.toString( );
	}

	private List<Column> getDataLineage( TraceTarget model )
	{
		List<Column> relationList = new ArrayList<Column>( );
		while ( model != null )
		{
			if ( model.getSource( ) instanceof Column )
			{
				relationList.add( 0, (Column) model.getSource( ) );
			}
			model = model.getParent( );
		}
		return relationList;
	}

	private String getProcedureName( TStoredProcedureSqlStatement stmt )
	{
		String fullName = stmt.getStoredProcedureName( ).toString( );
		if ( fullName.indexOf( '.' ) > -1 )
		{
			return fullName.substring( fullName.lastIndexOf( '.' ) + 1 );
		}
		else
			return fullName;
	}

	private Table getTable( TResultColumn resultColumn,
			TSelectSqlStatement selectStmt )
	{
		if ( resultColumn.toString( ).indexOf( ".." ) > -1 )
		{
			int index = resultColumn.toString( ).lastIndexOf( "." );
			return parseTableName( resultColumn.toString( )
					.substring( 0, index ) );
		}
		else if ( resultColumn.toString( ).indexOf( '.' ) > -1 )
		{
			int index = resultColumn.toString( ).lastIndexOf( "." );
			String tableName = resultColumn.toString( ).substring( 0, index );

			for ( int i = 0; i < selectStmt.tables.size( ); i++ )
			{
				TTable table = selectStmt.tables.getTable( i );
				if ( table.getAliasClause( ) != null )
				{
					if ( table.getAliasClause( )
							.getAliasName( )
							.toString( )
							.trim( )
							.equalsIgnoreCase( tableName ) )
					{
						String fullName = table.getFullName( );
						return parseTableName( fullName );
					}
				}
			}
			return parseTableName( tableName );
		}
		else
			return parseTableName( selectStmt.tables.getTable( 0 )
					.getFullName( ) );
	}

	Table getTable( String column, TCustomSqlStatement stmt )
	{
		if ( column.indexOf( ".." ) > -1 )
		{
			int index = column.lastIndexOf( "." );
			return parseTableName( column.substring( 0, index ) );
		}
		else if ( column.indexOf( '.' ) > -1 )
		{
			int index = column.lastIndexOf( "." );
			String tableName = column.substring( 0, index );

			for ( int i = 0; i < stmt.tables.size( ); i++ )
			{
				TTable table = stmt.tables.getTable( i );
				if ( table.getAliasClause( ) != null )
				{
					if ( table.getAliasClause( )
							.getAliasName( )
							.toString( )
							.trim( )
							.equalsIgnoreCase( tableName ) )
					{
						String fullName = table.getFullName( );
						return parseTableName( fullName );
					}
				}
			}
			return parseTableName( tableName );
		}
		else
			return parseTableName( stmt.tables.getTable( 0 ).getFullName( ) );
	}

	private Procedure parseProcedureName( String fullName )
	{
		if ( fullName.indexOf( ".." ) > -1 )
		{
			int index = fullName.lastIndexOf( ".." );
			String database = fullName.substring( 0, index );
			String name = fullName.substring( index + 2 );
			return procedureMap.get( database ).get( name );
		}
		else if ( fullName.toString( ).indexOf( '.' ) > -1 )
		{
			int index = fullName.lastIndexOf( "." );
			String name = fullName.substring( index + 1 );
			return procedureMap.get( database ).get( name );
		}
		else
		{
			return procedureMap.get( database ).get( fullName );
		}
	}

	private Procedure parseProcedureName( TStoredProcedureSqlStatement stmt )
	{
		String fullName = stmt.getStoredProcedureName( ).toString( );
		return parseProcedureName( fullName );
	}

	private Table parseTableName( String fullName )
	{
		if ( fullName.indexOf( ".." ) > -1 )
		{
			int index = fullName.lastIndexOf( ".." );
			String database = fullName.substring( 0, index );
			String name = fullName.substring( index + 2 );
			return datasourceMap.get( database ).get( name );
		}
		else if ( fullName.toString( ).indexOf( '.' ) > -1 )
		{
			int index = fullName.lastIndexOf( "." );
			String name = fullName.substring( index + 1 );
			return datasourceMap.get( database ).get( name );
		}
		else
		{
			return datasourceMap.get( database ).get( fullName );
		}
	}

	private void sortTable( List<Table> tables, List list )
	{
		for ( int i = 0; i < tables.size( ); i++ )
		{
			Table table = tables.get( i );
			int tableIndex = list.indexOf( table );
			int index = list.size( ) - 1;
			while ( index >= 0 )
			{
				Object obj = list.get( index );
				if ( obj instanceof Table )
				{
					Table refer = ( (Table) obj ).getColumns( )
							.values( )
							.iterator( )
							.next( )
							.getReferTable( );
					if ( table == refer )
					{
						if ( tableIndex > index )
						{
							list.remove( table );
							list.add( index, table );
						}
					}
				}
				index--;
			}
		}
	}

	private void traceSource( TraceTarget target, List list, int index )
	{
		Object source = target.getSource( );
		if ( source instanceof Parameter )
		{
			Procedure procedure = (Procedure) list.get( index );
			if ( procedure.getTableRelations( ) != null )
			{
				for ( int i = 0; i < procedure.getTableRelations( ).size( ); i++ )
				{
					TableRelation relation = procedure.getTableRelations( )
							.get( i );
					if ( relation.getRelations( ).containsKey( source ) )
					{
						Object relationTarget = relation.getRelations( )
								.get( source );
						if ( relationTarget instanceof Column )
						{
							Column column = (Column) relationTarget;
							if ( !target.containsTarget( column ) )
							{
								leafTargets.remove( target );
								TraceTarget model = new TraceTarget( );
								model.setSource( column );
								target.addTarget( model );
								leafTargets.add( model );
								traceSource( model,
										list,
										list.indexOf( column.getContainer( ) ) );
							}
						}
					}
				}
			}
		}
		if ( source instanceof Column )
		{
			Column column = (Column) source;
			for ( int i = index + 1; i < list.size( ); i++ )
			{
				Object obj = list.get( i );
				if ( obj instanceof View )
				{
					View view = (View) obj;
					Table table = view.getColumns( )
							.values( )
							.iterator( )
							.next( )
							.getReferTable( );
					if ( table == column.getContainer( ) )
					{
						Column viewColumn = view.getColumns( )
								.get( column.getName( ) );
						if ( !target.containsTarget( viewColumn ) )
						{
							leafTargets.remove( target );
							TraceTarget model = new TraceTarget( );
							model.setSource( viewColumn );
							target.addTarget( model );
							leafTargets.add( model );
							traceSource( model, list, i );
						}
					}
				}
				if ( obj instanceof Procedure )
				{
					Procedure procedure = (Procedure) obj;
					Iterator<Cursor> iter = procedure.getCursors( )
							.values( )
							.iterator( );
					while ( iter.hasNext( ) )
					{
						Cursor cursor = iter.next( );
						if ( cursor.getColumns( ).values( ).contains( column ) )
						{
							String[] columnNames = cursor.getColumns( )
									.keySet( )
									.toArray( new String[0] );
							int cursorIndex = -1;
							for ( int j = 0; j < columnNames.length; j++ )
							{
								if ( cursor.getColumns( ).get( columnNames[j] ) == column )
								{
									cursorIndex = j;
									break;
								}
							}
							String[] variableNames = cursor.getParams( )
									.keySet( )
									.toArray( new String[0] );
							Variable variable = cursor.getParams( )
									.get( variableNames[cursorIndex] );

							Execute[] executes = procedure.getExecutes( )
									.values( )
									.toArray( new Execute[0] );
							for ( int j = 0; j < executes.length; j++ )
							{
								if ( executes[j].getVariables( )
										.values( )
										.contains( variable ) )
								{
									int paramIndex = Arrays.asList( executes[j].getVariables( )
											.keySet( )
											.toArray( new String[0] ) )
											.indexOf( variable.getName( ) );
									Procedure targetProcedure = executes[j].getTarget( );
									String paramName = targetProcedure.getParams( )
											.keySet( )
											.toArray( new String[0] )[paramIndex];
									Parameter param = targetProcedure.getParams( )
											.get( paramName );
									if ( !target.containsTarget( param ) )
									{
										leafTargets.remove( target );
										TraceTarget model = new TraceTarget( );
										model.setSource( param );
										target.addTarget( model );
										leafTargets.add( model );
										traceSource( model,
												list,
												list.indexOf( targetProcedure ) );
									}
								}
							}
						}
					}
					List<TableRelation> relations = procedure.getTableRelations( );
					if ( relations != null && relations.size( ) > 0 )
					{
						for ( int k = 0; k < relations.size( ); k++ )
						{
							Map relatonMap = relations.get( k ).getRelations( );
							if ( relatonMap.containsKey( column )
									&& relatonMap.get( column ) instanceof Column )
							{
								if ( !target.containsTarget( relatonMap.get( column ) ) )
								{
									leafTargets.remove( target );
									TraceTarget model = new TraceTarget( );
									model.setSource( relatonMap.get( column ) );
									target.addTarget( model );
									leafTargets.add( model );
									Object relatonTarget = relatonMap.get( column );
									traceSource( model,
											list,
											list.indexOf( ( (Column) relatonTarget ).getContainer( ) ) );
								}
							}
						}
					}
				}
				if ( obj instanceof Statement )
				{
					Statement stmt = (Statement) obj;
					List<TableRelation> relations = stmt.getTableRelations( );
					if ( relations != null && relations.size( ) > 0 )
					{
						for ( int k = 0; k < relations.size( ); k++ )
						{
							Map relatonMap = relations.get( k ).getRelations( );
							if ( relatonMap.containsKey( column )
									&& relatonMap.get( column ) instanceof Column )
							{
								if ( !target.containsTarget( relatonMap.get( column ) ) )
								{
									leafTargets.remove( target );
									TraceTarget model = new TraceTarget( );
									model.setSource( relatonMap.get( column ) );
									target.addTarget( model );
									leafTargets.add( model );
									Object relatonTarget = relatonMap.get( column );
									traceSource( model,
											list,
											list.indexOf( ( (Column) relatonTarget ).getContainer( ) ) );
								}
							}
						}
					}
				}
			}
		}
	}

	private List<Column> exprToColumn( TExpression expr,
			TCustomSqlStatement stmt )
	{
		List<Column> columns = new ArrayList<Column>( );

		ColumnsInExpr c = new ColumnsInExpr( this, expr, columns, stmt );
		c.searchColumn( );

		return columns;
	}
}
