
package demos.gettablecolumns;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.IMetaDatabase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TLog;
import gudusoft.gsqlparser.sqlenv.TGreenplumSQLDataSource;
import gudusoft.gsqlparser.sqlenv.TMssqlSQLDataSource;
import gudusoft.gsqlparser.sqlenv.TMysqlSQLDataSource;
import gudusoft.gsqlparser.sqlenv.TNetezzaSQLDataSource;
import gudusoft.gsqlparser.sqlenv.TOracleSQLDataSource;
import gudusoft.gsqlparser.sqlenv.TPostgreSQLDataSource;
import gudusoft.gsqlparser.sqlenv.TRedshiftSQLDataSource;
import gudusoft.gsqlparser.sqlenv.TSnowflakeSQLDataSource;
import gudusoft.gsqlparser.sqlenv.TTeradataSQLDataSource;

import gudusoft.gsqlparser.sqlenv.TSQLCatalog;
import gudusoft.gsqlparser.sqlenv.TSQLDataSource;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.sqlenv.TSQLSchema;
import gudusoft.gsqlparser.sqlenv.TSQLTable;
import gudusoft.gsqlparser.sqlenv.parser.TJSONSQLEnvParser;
import gudusoft.gsqlparser.util.SQLUtil;

class sampleMetaDB implements IMetaDatabase
{

//	String columns[][] = {
//			{
//					"server", "db", "schema", "promotion", "promo_desc"
//			}, {
//					"server", "db", "schema", "sales", "dollars"
//			},
//			{
//
//					"server", "db", "schema", "TBL_BBGPFOLIOMAP_REP", "M_TP_BUY"
//
//			},
//			{
//					"server", "db", "schema", "TBL_BBGPFOLIOMAP_REP", "M_ANDRES"
//			},
//			{
//					"server", "db", "schema", "TBL_BBGPOS_REP", "M_TP_BUY"
//			},
//			{
//					"server", "db", "schema", "TBL_BBGPOS_REP", "M_TP_PFOLIO"
//			}
//	};

	String columns[][] = {
			{
					"server", "db", "schema", "TBL_BBGPOS_REP", "M_TP_PFOLIO"
			}, {
			"server", "db", "schema", "A_FXPOSITIONDEL_REP", "M_FXDELTA"
			}, {
			"server", "db", "schema", "A_FXPOSITIONDEL_REP", "M_CLOSING_E"
			}, {
			"server", "db", "schema", "A_FXPOSITIONDEL_REP", "M_FXDELTA_Z"
			}
	};

	public boolean checkColumn( String server, String database, String schema,
			String table, String column )
	{
		boolean bServer, bDatabase, bSchema, bTable, bColumn, bRet = false;
		for ( int i = 0; i < columns.length; i++ )
		{
			if ( ( server == null ) || ( server.length( ) == 0 ) )
			{
				bServer = true;
			}
			else
			{
				bServer = columns[i][0].equalsIgnoreCase( server );
			}
			if ( !bServer )
				continue;

			if ( ( database == null ) || ( database.length( ) == 0 ) )
			{
				bDatabase = true;
			}
			else
			{
				bDatabase = columns[i][1].equalsIgnoreCase( database );
			}
			if ( !bDatabase )
				continue;

			if ( ( schema == null ) || ( schema.length( ) == 0 ) )
			{
				bSchema = true;
			}
			else
			{
				bSchema = columns[i][2].equalsIgnoreCase( schema );
			}

			if ( !bSchema )
				continue;

			bTable = columns[i][3].equalsIgnoreCase( table );
			if ( !bTable )
				continue;

			bColumn = columns[i][4].equalsIgnoreCase( column );
			if ( !bColumn )
				continue;

			bRet = true;
			break;

		}

		return bRet;
	}

}

public class runGetTableColumn
{
	private static  File[] listFiles( File sqlFiles )
	{
		List<File> children = new ArrayList<File>( );
		if ( sqlFiles != null )
			listFiles( sqlFiles, children );
		return children.toArray( new File[0] );
	}

	private static void listFiles( File rootFile, List<File> children )
	{
		if ( rootFile.isFile( ) )
			children.add( rootFile );
		else
		{
			File[] files = rootFile.listFiles( );
			for ( int i = 0; i < files.length; i++ )
			{
				listFiles( files[i], children );
			}
		}
	}


	public static void main( String args[] )
	{
		long t = System.currentTimeMillis();

		if ( args.length < 2 )
		{
			displayInitInformation( );
			return;
		}

//		List<String> argList = Arrays.asList( args );
//
//		File sqlFile = null;

//		if ( argList.indexOf( "/f" ) != -1
//				&& argList.size( ) > argList.indexOf( "/f" ) + 1 )
//		{
//			sqlFile = new File( args[argList.indexOf( "/f" ) + 1] );
//			if ( !sqlFile.exists( ) || !sqlFile.isFile( ) )
//			{
//				System.out.println( sqlFile + " is not a valid file." );
//				return;
//			}
//		}
//
//		if ( sqlFile == null )
//		{
//			displayInitInformation( );
//			return;
//		}

		File sqlFiles = null;

		List<String> argList = Arrays.asList(args);

		if ( argList.indexOf( "/f" ) != -1
				&& argList.size( ) > argList.indexOf( "/f" ) + 1 )
		{
			sqlFiles = new File( args[argList.indexOf( "/f" ) + 1] );
			if ( !sqlFiles.exists( ) || !sqlFiles.isFile( ) )
			{
				System.out.println( sqlFiles + " is not a valid file." );
				return;
			}
		}
		else if ( argList.indexOf( "/d" ) != -1
				&& argList.size( ) > argList.indexOf( "/d" ) + 1 )
		{
			sqlFiles = new File( args[argList.indexOf( "/d" ) + 1] );
			if ( !sqlFiles.exists( ) || !sqlFiles.isDirectory( ) )
			{
				System.out.println( sqlFiles + " is not a valid directory." );
				return;
			}
		}
		else
		{
			//System.out.println( "Please specify a sql file path or directory path." );
			displayInitInformation( );
			return;
		}

		EDbVendor vendor = EDbVendor.dbvmssql;

		int index = argList.indexOf( "/t" );

		if ( index != -1 && args.length > index + 1 )
		{
			vendor = TGSqlParser.getDBVendorByName(args[index + 1]);

		}

		System.out.println("Processing "+vendor.toString()+"...");

		//TLog.REPORT_LEVEL = TLog.ERROR;

		TGetTableColumn getTableColumn = new TGetTableColumn( vendor );
		getTableColumn.showDetail = false;
		getTableColumn.showSummary = true;
		getTableColumn.showTreeStructure = false;
		getTableColumn.showBySQLClause = false;
		getTableColumn.showJoin = false;
		getTableColumn.showColumnLocation = false;
		getTableColumn.linkOrphanColumnToFirstTable = false;
		getTableColumn.showIndex = false;
		getTableColumn.showDatatype = true;
		getTableColumn.listStarColumn = true;
		getTableColumn.showTableEffect = false;
		getTableColumn.showCTE = false;
		//getTableColumn.setMetaDatabase( new sampleMetaDB());
		
		TSQLEnv sqlenv = null;
		//Get database metadata from sql jdbc
		if (argList.indexOf("/h") != -1 && argList.indexOf("/P") != -1 && argList.indexOf("/u") != -1 && argList.indexOf("/p") != -1) {
		    try {
			String host = args[argList.indexOf("/h") + 1];
			String port = args[argList.indexOf("/P") + 1];
			String user = args[argList.indexOf("/u") + 1];
			String passowrd = args[argList.indexOf("/p") + 1];
			String database = null;
			String schema = null;
			if (argList.indexOf("/db") != -1) {
			    database = args[argList.indexOf("/db") + 1];
			}
			if (argList.indexOf("/schema") != -1) {
			    schema = args[argList.indexOf("/schema") + 1];
			}
			TSQLDataSource datasource = createSQLDataSource(vendor, host, port, user, passowrd, database, schema);
			if (datasource != null) {
			    sqlenv = TSQLEnv.valueOf(datasource);
			}
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
		
		if (sqlenv == null) {
		//	getTableColumn.setSqlEnv(new TSQLServerEnv());
//			TJSONSQLEnvParser jsonsqlEnvParser = new TJSONSQLEnvParser();
//			String jsonText = SQLUtil.getFileContent("f:\\tmp\\meatdata.json");
//
//			TSQLEnv tsqlEnv = jsonsqlEnvParser.parseSQLEnv(vendor, jsonText);
//			System.out.println(tsqlEnv.toString());
//
//			getTableColumn.setSqlEnv(tsqlEnv);
		}
		else{
			getTableColumn.setSqlEnv(sqlenv);
		}


		if ( argList.indexOf( "/showDetail" ) != -1 )
		{
			getTableColumn.showSummary = false;
			getTableColumn.showDetail = true;
		}
		else if ( argList.indexOf( "/showTreeStructure" ) != -1 )
		{
			getTableColumn.showSummary = false;
			getTableColumn.showTreeStructure = true;
		}
		else if ( argList.indexOf( "/showBySQLClause" ) != -1 )
		{
			getTableColumn.showSummary = false;
			getTableColumn.showBySQLClause = true;
		}
		else if ( argList.indexOf( "/showJoin" ) != -1 )
		{
			getTableColumn.showSummary = false;
			getTableColumn.showJoin = true;
		}


		File[] children = listFiles( sqlFiles );

		int total_sql_files = 0, error_sql_flies=0;


		for ( int i = 0; i < children.length; i++ )
		{
			File child = children[i];
			if ( child.isDirectory( ) )
				continue;
			//String content = SQLUtil.getFileContent(child);
			if (child.getAbsolutePath().endsWith(".sql")){
				total_sql_files++;

				System.out.println("\nProcessing: "+child.getAbsolutePath());
				getTableColumn.runFile( child.getAbsolutePath() );
			}
		}


		System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) +",file processed: "+total_sql_files+",syntax errors:"+error_sql_flies );


//		getTableColumn.runFile( sqlFile.getAbsolutePath( ) );
	}

	private static void displayInitInformation( )
	{
		System.out.println( "Usage: java runGetTableColumn [/f <path_to_sql_file>] [/d <path_to_directory_includes_sql_files>] [/t <database type>] [/<show option>]" );
		System.out.println( "/f: specify the sql file path to analyze." );
		System.out.println( "/t: option, set the database type. Support oracle, mysql, mssql, db2, netezza, teradata, informix, sybase, postgresql, hive, greenplum and redshift, the default type is oracle" );
		System.out.println( "/showSummary: default show option, display the summary information." );
		System.out.println( "/showDetail: show option, display the detail information." );
		System.out.println( "/showTreeStructure: show option, display the information as a tree structure." );
		System.out.println( "/showBySQLClause: show option, display the information group by sql clause type." );
		System.out.println( "/showJoin: show option, display the join table and column." );
		System.out.println("/h: option, specify the host of jdbc connection");
		System.out.println("/P: option, specify the port of jdbc connection, note it's capital P.");
		System.out.println("/u: option, specify the username of jdbc connection.");
		System.out.println("/p: option, specify the password of jdbc connection, note it's lowercase P.");
		System.out.println("/db: option, specify the database of jdbc connection.");
		System.out.println("/schema: option, specify the schema which is used for extracting metadata.");
	}
	
	private static TSQLDataSource createSQLDataSource(EDbVendor vendor, String host, String port, String user,
													  String passowrd, String database, String schema) {
		try {
			if (vendor == EDbVendor.dbvoracle) {
				TOracleSQLDataSource datasource = new TOracleSQLDataSource(host, port, user, passowrd, database);
				if (schema != null) {
					datasource.setExtractedSchemas(schema);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvmssql) {
				TMssqlSQLDataSource datasource = new TMssqlSQLDataSource(host, port, user, passowrd);
				if (database != null) {
					if (schema != null) {
						datasource.setExtractedDbsSchemas(database + "/" + schema);
					} else {
						datasource.setExtractedDbsSchemas(database);
					}
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvpostgresql) {
				TPostgreSQLDataSource datasource = new TPostgreSQLDataSource(host, port, user, passowrd, database);
				if (schema != null) {
					datasource.setExtractedDbsSchemas(database + "/" + schema);
				} else {
					datasource.setExtractedDbsSchemas(database);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvgreenplum) {
				TGreenplumSQLDataSource datasource = new TGreenplumSQLDataSource(host, port, user, passowrd);
				if (schema != null) {
					datasource.setExtractedDbsSchemas(database + "/" + schema);
				} else {
					datasource.setExtractedDbsSchemas(database);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvredshift) {
				TRedshiftSQLDataSource datasource = new TRedshiftSQLDataSource(host, port, user, passowrd, database);
				if (schema != null) {
					datasource.setExtractedDbsSchemas(database + "/" + schema);
				} else {
					datasource.setExtractedDbsSchemas(database);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvmysql) {
				TMysqlSQLDataSource datasource = new TMysqlSQLDataSource(host, port, user, passowrd);
				if (database != null) {
					if (schema != null) {
						datasource.setExtractedDbsSchemas(database + "/" + schema);
					} else {
						datasource.setExtractedDbsSchemas(database);
					}
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvnetezza) {
				TNetezzaSQLDataSource datasource = new TNetezzaSQLDataSource(host, port, user, passowrd, database);
				if (database != null) {
					if (schema != null) {
						datasource.setExtractedDbsSchemas(database + "/" + schema);
					} else {
						datasource.setExtractedDbsSchemas(database);
					}
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvsnowflake) {
				TSnowflakeSQLDataSource datasource = new TSnowflakeSQLDataSource(host, port, user, passowrd);
				if (database != null) {
					if (schema != null) {
						datasource.setExtractedDbsSchemas(database + "/" + schema);
					} else {
						datasource.setExtractedDbsSchemas(database);
					}
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvteradata) {
				TTeradataSQLDataSource datasource = new TTeradataSQLDataSource(host, port, user, passowrd, database);
				if (database != null) {
					datasource.setExtractedDatabases(database);
				}
				return datasource;
			}
		} catch (Exception e) {
			System.err.println("Connect datasource failed. " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}

class TSQLServerEnv extends TSQLEnv{

	public TSQLServerEnv(){
		super(EDbVendor.dbvmssql);
		initSQLEnv();
	}

	@Override
	public void initSQLEnv() {

		// add a new database: master
		TSQLCatalog sqlCatalog = createSQLCatalog("master");
		// add a new schema: dbo
		TSQLSchema sqlSchema = sqlCatalog.createSchema("dbo");
		//add a new table: aTab
		TSQLTable aTab = sqlSchema.createTable("aTab");
		aTab.addColumn("Quantity1");

		//add a new table: bTab
		//TSQLTable bTab = sqlSchema.createTable("bTab");
		//bTab.addColumn("Quantity2");

//		//add a new table: cTab
//		TSQLTable cTab = sqlSchema.createTable("cTab");
//		cTab.addColumn("Quantity");

		//add a new table: cTab
		TSQLTable ExecutionLogStorage = sqlSchema.createTable("ExecutionLogStorage");
		ExecutionLogStorage.addColumn("UserName");

		TSQLTable tab = sqlSchema.createTable("sysforeignkeys");
		tab.addColumn("fkey");
		tab.addColumn("fkeyid");
		tab.addColumn("keyNo");
		tab.addColumn("rkey");
		tab.addColumn("rkeyid");
	}
}

class THiveEnv extends TSQLEnv{

	public THiveEnv(){
		super(EDbVendor.dbvhive);
		initSQLEnv();
	}

	@Override
	public void initSQLEnv() {

		// add a new database
		TSQLCatalog sqlCatalog = createSQLCatalog("pharos_business_vault");
		// hive don't have schema, we use a default schema
		TSQLSchema sqlSchema = sqlCatalog.createSchema("default");

		//add a new table: cTab
		TSQLTable ExecutionLogStorage = sqlSchema.createTable("b_content_datamart_bv");
		ExecutionLogStorage.addColumn("a_beginn_pe");
		ExecutionLogStorage.addColumn("a_perspektive_verbrauch");
	}
}
