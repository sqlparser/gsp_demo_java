
package demos.dlineageBasic.metadata;

import java.util.ArrayList;
import java.util.List;

import demos.dlineageBasic.model.metadata.ProcedureMetaData;
import demos.dlineageBasic.model.xml.procedure;
import demos.dlineageBasic.model.xml.procedureImpactResult;
import demos.dlineageBasic.model.xml.sourceProcedure;
import demos.dlineageBasic.model.xml.targetProcedure;
import demos.dlineageBasic.util.Pair;
import demos.dlineageBasic.util.SQLUtil;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.stmt.TCallStatement;
import gudusoft.gsqlparser.stmt.TStoredProcedureSqlStatement;
import gudusoft.gsqlparser.stmt.TUseDatabase;
import gudusoft.gsqlparser.stmt.mssql.TMssqlExecute;

public class ProcedureRelationScanner
{

	private Pair<procedureImpactResult, List<ProcedureMetaData>> procedures;
	private boolean strict = false;
	private String database = null;
	private EDbVendor vendor = EDbVendor.dbvmssql;

	public ProcedureRelationScanner(
			Pair<procedureImpactResult, List<ProcedureMetaData>> procedures,
			EDbVendor vendor, TGSqlParser parser, boolean strict, String database )
	{
		this.strict = strict;
		this.vendor = vendor;
		this.database = database;
		this.procedures = procedures;
		checkDDL( parser );
	}

	private void checkDDL( TGSqlParser sqlparser )
	{
		TStatementList stmts = sqlparser.sqlstatements;
		parseStatementList( stmts );
	}

	private void parseStatementList( TStatementList stmts )
	{
		for ( int i = 0; i < stmts.size( ); i++ )
		{
			TCustomSqlStatement stmt = stmts.get( i );
			parseStatement( stmt );
		}
	}

	private void parseStatement( TCustomSqlStatement stmt )
	{

		if ( stmt instanceof TUseDatabase )
		{
			TUseDatabase use = (TUseDatabase) stmt;
			database = use.getDatabaseName( ).toString( );
		}
		else if ( stmt instanceof TStoredProcedureSqlStatement )
		{
			TStoredProcedureSqlStatement procedureStmt = (TStoredProcedureSqlStatement) stmt;
			parseProcedureStmt( procedureStmt );
		}
	}

	private void parseProcedureStmt( TStoredProcedureSqlStatement procedureStmt )
	{
		if(procedureStmt.getStoredProcedureName( ) == null)
        {
            return;
        }
		ProcedureMetaData procedureMetaData = getProcedureMetaData( procedureStmt.getStoredProcedureName( ) );
		procedureMetaData = getProcedureMetaData( procedureMetaData, true );

		TObjectName procedureName = procedureStmt.getStoredProcedureName( );
		procedure procedure = new procedure( );
		procedure.setName( procedureMetaData.getDisplayName( ) );
		procedure.setOwner( getOwnerString( procedureMetaData ) );
		procedure.setCoordinate( procedureName.getStartToken( ).lineNo
				+ ","
				+ procedureName.getStartToken( ).columnNo );
		procedure.setHighlightInfo( procedureName.getStartToken( ).offset
				+ ","
				+ ( procedureName.getEndToken( ).offset
						- procedureName.getStartToken( ).offset + procedureName.getEndToken( ).astext.length( ) ) );
		getProcedureList( procedures.first ).add( procedure );

		parseProcedureLineage( procedureStmt, procedureMetaData, procedure );

	}

	private List<procedure> getProcedureList( procedureImpactResult impactResult )
	{
		if ( impactResult.getProcedures( ) == null )
		{
			impactResult.setProcedures( new ArrayList<procedure>( ) );
		}
		return impactResult.getProcedures( );
	}

	public ProcedureMetaData getProcedureMetaData(
			ProcedureMetaData parentProcedure, TObjectName procedureName )
	{
		ProcedureMetaData procedureMetaData = new ProcedureMetaData( vendor,
				strict );
		procedureMetaData.setName( procedureName.getPartString( ) == null ? procedureName.getObjectString( )
				: procedureName.getPartString( ) );
		if ( procedureName.getSchemaString( ) != null )
		{
			procedureMetaData.setSchemaName( procedureName.getSchemaString( ) );
		}
		else
		{
			procedureMetaData.setSchemaName( parentProcedure.getSchemaName( ) );
			procedureMetaData.setSchemaDisplayName( parentProcedure.getSchemaDisplayName( ) );
		}

		if ( isNotEmpty( procedureName.getDatabaseString( ) ) )
		{
			procedureMetaData.setCatalogName( procedureName.getDatabaseString( ) );
		}
		else
		{
			procedureMetaData.setCatalogName( parentProcedure.getCatalogName( ) );
			procedureMetaData.setCatalogDisplayName( parentProcedure.getCatalogDisplayName( ) );
		}
		return procedureMetaData;
	}

	private ProcedureMetaData getProcedureMetaData( TObjectName procedureName )
	{
		ProcedureMetaData procedureMetaData = new ProcedureMetaData( vendor,
				strict );
		procedureMetaData.setName( procedureName.getPartString( ) == null ? procedureName.getObjectString( )
				: procedureName.getPartString( ) );
		procedureMetaData.setSchemaName( procedureName.getSchemaString( ) );
		if ( isNotEmpty( procedureName.getDatabaseString( ) ) )
		{
			procedureMetaData.setCatalogName( procedureName.getDatabaseString( ) );
		}
		else
			procedureMetaData.setCatalogName( database );
		return procedureMetaData;
	}

	class functionVisitor extends TParseTreeVisitor
	{

		private ProcedureMetaData parentProcedure;
		private targetProcedure targetProcedure;

		public functionVisitor( ProcedureMetaData parentProcedure,
				procedure procedure )
		{
			this.parentProcedure = parentProcedure;
			this.targetProcedure = new targetProcedure( );
			targetProcedure.setCoordinate( procedure.getCoordinate( ) );
			targetProcedure.setHighlightInfo( procedure.getHighlightInfo( ) );
			targetProcedure.setName( procedure.getName( ) );
			targetProcedure.setOwner( procedure.getOwner( ) );
			getTargetProcedureList( procedures.first ).add( targetProcedure );
		}

		private List<targetProcedure> getTargetProcedureList(
				procedureImpactResult impactResult )
		{
			if ( impactResult.getTargetProcedures( ) == null )
			{
				impactResult.setTargetProcedures( new ArrayList<targetProcedure>( ) );
			}
			return impactResult.getTargetProcedures( );
		}

		public void preVisit( TFunctionCall node )
		{
			if ( node.getFunctionName( ) != null )
			{
				TObjectName procedureName = node.getFunctionName( );
				ProcedureMetaData procedureMetaData = getProcedureMetaData( procedureName );
				setProcedureDlinage( procedureMetaData, procedureName );
			}
		}

		private ProcedureMetaData getProcedureMetaData(
				TObjectName procedureName )
		{
			ProcedureMetaData procedureMetaData = ProcedureRelationScanner.this.getProcedureMetaData( parentProcedure,
					procedureName );
			procedureMetaData = ProcedureRelationScanner.this.getProcedureMetaData( procedureMetaData,
					false );
			if ( procedureMetaData == null )
				return null;
			if ( procedureMetaData.getCatalogName( ) == null )
			{
				procedureMetaData.setCatalogName( parentProcedure.getCatalogName( ) );
				procedureMetaData.setCatalogDisplayName( parentProcedure.getCatalogDisplayName( ) );
			}
			if ( procedureMetaData.getSchemaName( ) == null )
			{
				procedureMetaData.setSchemaName( parentProcedure.getSchemaName( ) );
				procedureMetaData.setSchemaDisplayName( parentProcedure.getSchemaDisplayName( ) );
			}
			return procedureMetaData;
		}

		private void setProcedureDlinage( ProcedureMetaData procedureMetaData,
				TObjectName procedureName )
		{
			if ( procedureMetaData == null )
				return;
			sourceProcedure sourceProcedure = new sourceProcedure( );
			sourceProcedure.setName( procedureMetaData.getDisplayName( ) );
			sourceProcedure.setOwner( getOwnerString( procedureMetaData ) );
			sourceProcedure.setCoordinate( procedureName.getStartToken( ).lineNo
					+ ","
					+ procedureName.getStartToken( ).columnNo );
			sourceProcedure.setHighlightInfo( procedureName.getStartToken( ).offset
					+ ","
					+ ( procedureName.getEndToken( ).offset
							- procedureName.getStartToken( ).offset + procedureName.getEndToken( ).astext.length( ) ) );

			getSourceProcedureList( targetProcedure ).add( sourceProcedure );
		}

		private List<sourceProcedure> getSourceProcedureList(
				targetProcedure targetProcedure )
		{
			if ( targetProcedure.getSourceProcedures( ) == null )
				targetProcedure.setSourceProcedures( new ArrayList<sourceProcedure>( ) );
			return targetProcedure.getSourceProcedures( );
		}

		public void preVisit( TCallStatement statement )
		{
			if ( statement.getRoutineName( ) != null )
			{
				TObjectName procedureName = statement.getRoutineName( );
				ProcedureMetaData procedureMetaData = getProcedureMetaData( procedureName );
				setProcedureDlinage( procedureMetaData, procedureName );
			}
		}

		public void preVisit( TMssqlExecute statement )
		{
			if ( statement.getModuleName( ) != null )
			{
				TObjectName procedureName = statement.getModuleName( );
				ProcedureMetaData procedureMetaData = getProcedureMetaData( procedureName );
				setProcedureDlinage( procedureMetaData, procedureName );
			}
		}
	}

	private void parseProcedureLineage(
			TStoredProcedureSqlStatement procedureStmt,
			ProcedureMetaData procedureMetaData, procedure sourceProcedure )
	{
		functionVisitor fv = new functionVisitor( procedureMetaData,
				sourceProcedure );
		procedureStmt.acceptChildren( fv );
	}

	private static boolean isNotEmpty( String str )
	{
		return str != null && str.trim( ).length( ) > 0;
	}

	private ProcedureMetaData getProcedureMetaData(
			ProcedureMetaData procedureMetaData, boolean replace )
	{
		int index = procedures.second.indexOf( procedureMetaData );
		if ( index != -1 )
		{
			if ( replace )
			{
				procedures.second.remove( index );
				procedures.second.add( procedureMetaData );
				return procedureMetaData;
			}
			else
				return procedures.second.get( index );
		}
		else
		{
			if ( replace )
			{
				procedures.second.add( procedureMetaData );
				return procedureMetaData;
			}
			else
				return null;
		}
	}

	public String getDatabase( )
	{
		return database;
	}

	private String getOwnerString( ProcedureMetaData procedureMetaData )
	{
		StringBuffer buffer = new StringBuffer( );
		if ( !SQLUtil.isEmpty( procedureMetaData.getCatalogDisplayName( ) ) )
		{
			buffer.append( procedureMetaData.getCatalogDisplayName( ) )
					.append( "." );
		}
		if ( !SQLUtil.isEmpty( procedureMetaData.getSchemaDisplayName( ) ) )
		{
			buffer.append( procedureMetaData.getSchemaDisplayName( ) );
		}
		return buffer.toString( );
	}

	private String getOwnerString( TObjectName objectName )
	{
		StringBuffer buffer = new StringBuffer( );
		if ( !SQLUtil.isEmpty( objectName.getServerString( ) ) )
		{
			buffer.append( objectName.getServerString( ) ).append( "." );
		}
		if ( !SQLUtil.isEmpty( objectName.getDatabaseString( ) ) )
		{
			buffer.append( objectName.getDatabaseString( ) ).append( "." );
		}
		if ( !SQLUtil.isEmpty( objectName.getSchemaString( ) ) )
		{
			buffer.append( objectName.getSchemaString( ) );
		}
		return buffer.toString( );
	}
}
