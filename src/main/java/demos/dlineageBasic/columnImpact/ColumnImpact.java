
package demos.dlineageBasic.columnImpact;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.ESetOperatorType;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.IMetaDatabase;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TAliasClause;
import gudusoft.gsqlparser.nodes.TCaseExpression;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TExpressionList;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TGroupByItem;
import gudusoft.gsqlparser.nodes.TGroupingExpressionItem;
import gudusoft.gsqlparser.nodes.TInExpr;
import gudusoft.gsqlparser.nodes.TIntervalExpression;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinItem;
import gudusoft.gsqlparser.nodes.TMergeWhenClause;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TObjectNameList;
import gudusoft.gsqlparser.nodes.TOrderBy;
import gudusoft.gsqlparser.nodes.TOrderByItem;
import gudusoft.gsqlparser.nodes.TOrderByItemList;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TParseTreeNodeList;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TTableList;
import gudusoft.gsqlparser.nodes.TTrimArgument;
import gudusoft.gsqlparser.nodes.TViewAliasItem;
import gudusoft.gsqlparser.nodes.TWhenClauseItem;
import gudusoft.gsqlparser.nodes.TWhenClauseItemList;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TMergeSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import gudusoft.gsqlparser.util.keywordChecker;

import java.awt.Point;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import demos.dlineageBasic.Dlineage;
import demos.dlineageBasic.metadata.DDLParser;
import demos.dlineageBasic.metadata.MetaDB;
import demos.dlineageBasic.model.metadata.ColumnMetaData;
import demos.dlineageBasic.model.metadata.TableMetaData;
import demos.dlineageBasic.model.view.AliasModel;
import demos.dlineageBasic.model.view.Clause;
import demos.dlineageBasic.model.view.ColumnImpactModel;
import demos.dlineageBasic.model.view.ColumnModel;
import demos.dlineageBasic.model.view.FieldModel;
import demos.dlineageBasic.model.view.ReferenceModel;
import demos.dlineageBasic.model.view.TableModel;
import demos.dlineageBasic.model.xml.columnImpactResult;
import demos.dlineageBasic.model.xml.sourceColumn;
import demos.dlineageBasic.model.xml.table;
import demos.dlineageBasic.model.xml.targetColumn;
import demos.dlineageBasic.util.SQLUtil;
import demos.dlineageBasic.util.XML2Model;
import demos.dlineageBasic.util.XMLUtil;

public class ColumnImpact
{

	public static enum ClauseType {
		connectby, groupby, join, orderby, select, startwith, undefine, where, topselect, createview, createtable, insert, casewhen, casethen, update, updateset, assign, mergematch, mergenotmatch, mergeset, merge
	}

	class columnsInExpr implements IExpressionVisitor
	{

		private List<TColumn> columns;
		private TExpression expr;
		private ColumnImpact impact;
		private int level;
		private TCustomSqlStatement stmt;
		private boolean collectExpr;
		private ClauseType clauseType;
		private TAlias parentAlias;

		public columnsInExpr( ColumnImpact impact, TExpression expr,
				List<TColumn> columns, TCustomSqlStatement stmt, int level,
				boolean collectExpr, ClauseType clauseType, TAlias parentAlias )
		{
			this.stmt = stmt;
			this.impact = impact;
			this.expr = expr;
			this.columns = columns;
			this.level = level;
			this.collectExpr = collectExpr;
			this.clauseType = clauseType;
			this.parentAlias = parentAlias;
		}

		private void addColumnToList( TParseTreeNodeList list,
				TCustomSqlStatement stmt )
		{
			if ( list != null )
			{
				for ( int i = 0; i < list.size( ); i++ )
				{
					Object element = list.getElement( i );

					columnsInExpr visitor = new columnsInExpr( this.impact,
							this.expr,
							this.columns,
							this.stmt,
							this.level,
							this.collectExpr,
							this.clauseType,
							parentAlias );

					if ( element instanceof TGroupByItem )
					{
						visitor.clauseType = ClauseType.groupby;
						( ( (TGroupByItem) element ).getExpr( ) ).inOrderTraverse( visitor );
					}
					if ( element instanceof TOrderByItem )
					{
						visitor.clauseType = ClauseType.orderby;
						( ( (TOrderByItem) element ).getSortKey( ) ).inOrderTraverse( visitor );
					}
					else if ( element instanceof TWhenClauseItem )
					{
						if ( visitor.clauseType == ClauseType.select )
						{
							( ( (TWhenClauseItem) element ).getReturn_expr( ) ).inOrderTraverse( visitor );
						}
						else
						{
							visitor.clauseType = ClauseType.casethen;
							( ( (TWhenClauseItem) element ).getReturn_expr( ) ).inOrderTraverse( visitor );
						}
						if ( !impact.analyzeDlineage )
						{
							visitor.clauseType = ClauseType.casewhen;
							( ( (TWhenClauseItem) element ).getComparison_expr( ) ).inOrderTraverse( visitor );
						}
					}
					else if ( element instanceof TExpression )
					{
						( (TExpression) element ).inOrderTraverse( visitor );
					}
				}
			}
		}

		@SuppressWarnings("deprecation")
		@Override
		public boolean exprVisit( TParseTreeNode pNode, boolean isLeafNode )
		{
			TExpression lcexpr = (TExpression) pNode;
			if ( lcexpr.getExpressionType( ) == EExpressionType.simple_constant_t )
			{
				if ( !isKeyword( lcexpr.toString( ) ) )
				{
					TColumn tempColumn = new TColumn( );
					tempColumn.columnName = lcexpr.toString( );
					tempColumn.location = new Point( (int) lcexpr.getEndToken( ).lineNo,
							(int) lcexpr.getEndToken( ).columnNo );
					tempColumn.offset = lcexpr.getEndToken( ).offset;
					tempColumn.length = lcexpr.getEndToken( ).astext.length( );
					tempColumn.tableNames.add( Dlineage.TABLE_CONSTANT );
					tempColumn.tableFullNames.add( Dlineage.TABLE_CONSTANT
							+ "."
							+ tempColumn.columnName );
					tempColumn.clauseType = clauseType;
					columns.add( tempColumn );
				}
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.simple_object_name_t )
			{
				TColumn column;
				if ( !accessExpressions.containsKey( lcexpr ) )
				{
					column = impact.attrToColumn( lcexpr,
							stmt,
							expr,
							collectExpr,
							clauseType,
							parentAlias );
					accessExpressions.put( lcexpr, column );
				}
				else
				{
					column = accessExpressions.get( lcexpr );
				}
				if ( column != null )
				{
					columns.add( column );
				}
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.multiset_t )
			{
				if ( lcexpr.getSubQuery( ) != null )
				{
					for ( int i = 0; i < lcexpr.getSubQuery( )
							.getResultColumnList( )
							.size( ); i++ )
					{
						impact.linkFieldToTables( parentAlias,
								lcexpr.getSubQuery( )
										.getResultColumnList( )
										.getResultColumn( i ),
								lcexpr.getSubQuery( ),
								level + 1,
								ClauseType.select );
					}
				}
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.between_t )
			{
				lcexpr.getBetweenOperand( ).inOrderTraverse( this );
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.function_t )
			{
				TFunctionCall func = lcexpr.getFunctionCall( );
				if ( func.getAgainstExpr( ) != null )
				{
					func.getAgainstExpr( ).inOrderTraverse( this );
				}
//				if ( func.getBetweenExpr( ) != null )
//				{
//					func.getBetweenExpr( ).inOrderTraverse( this );
//				}
				if ( func.getExpr1( ) != null )
				{
					func.getExpr1( ).inOrderTraverse( this );
				}
				if ( func.getExpr2( ) != null )
				{
					func.getExpr2( ).inOrderTraverse( this );
				}
				if ( func.getExpr3( ) != null )
				{
					func.getExpr3( ).inOrderTraverse( this );
				}
				if ( func.getParameter( ) != null )
				{
					func.getParameter( ).inOrderTraverse( this );
				}
				if ( func.getInExpr( ) != null )
				{
					TInExpr inExpr = func.getInExpr( );
					if ( inExpr.getExprList( ) != null )
					{
						for ( int k = 0; k < inExpr.getExprList( ).size( ); k++ )
						{
							inExpr.getExprList( )
									.getExpression( k )
									.inOrderTraverse( this );
						}
					}
					if ( inExpr.getFunc_expr( ) != null )
					{
						inExpr.getFunc_expr( ).inOrderTraverse( this );
					}
					if ( inExpr.getGroupingExpressionItemList( ) != null )
					{
						for ( int k = 0; k < inExpr.getGroupingExpressionItemList( )
								.size( ); k++ )
						{
							TGroupingExpressionItem item = inExpr.getGroupingExpressionItemList( )
									.getGroupingExpressionItem( k );
							if ( item.getExpr( ) != null )
							{
								item.getExpr( ).inOrderTraverse( this );
							}
							if ( item.getExprList( ) != null )
							{
								item.getExprList( )
										.getExpression( k )
										.inOrderTraverse( this );
							}
						}
					}
				}
//				if ( func.getRangeSize( ) != null )
//				{
//					func.getRangeSize( ).inOrderTraverse( this );
//				}
				if ( func.getXMLElementNameExpr( ) != null )
				{
					func.getXMLElementNameExpr( ).inOrderTraverse( this );
				}
				if ( func.getXMLType_Instance( ) != null )
				{
					func.getXMLType_Instance( ).inOrderTraverse( this );
				}
				if ( func.getXPath_String( ) != null )
				{
					func.getXPath_String( ).inOrderTraverse( this );
				}
				if ( func.getNamespace_String( ) != null )
				{
					func.getNamespace_String( ).inOrderTraverse( this );
				}
				if ( func.getArgs( ) != null )
				{
					for ( int k = 1; k < func.getArgs( ).size( ); k++ )
					{
						TExpression expr = func.getArgs( ).getExpression( k );
						if ( expr != null )
							expr.inOrderTraverse( this );
					}
				}
				if ( func.getTrimArgument( ) != null )
				{
					TTrimArgument args = func.getTrimArgument( );
					TExpression expr = args.getStringExpression( );
					if ( expr != null )
					{
						expr.inOrderTraverse( this );
					}
					expr = args.getTrimCharacter( );
					if ( expr != null )
					{
						expr.inOrderTraverse( this );
					}
				}
				if ( func.getOrderByList( ) != null )
				{
					TOrderByItemList orderByList = func.getOrderByList( );
					for ( int k = 0; k < orderByList.size( ); k++ )
					{
						TExpression expr = orderByList.getOrderByItem( k )
								.getSortKey( );
						if ( expr != null )
							expr.inOrderTraverse( this );
					}
				}
				if ( func.getArgs( ) != null )
				{
					for ( int k = 0; k < func.getArgs( ).size( ); k++ )
					{
						TExpression expr = func.getArgs( ).getExpression( k );
						if ( expr != null )
							expr.inOrderTraverse( this );
					}
				}
				if ( func.getAnalyticFunction( ) != null )
				{
					TParseTreeNodeList list = func.getAnalyticFunction( )
							.getPartitionBy_ExprList( );
					addColumnToList( list, stmt );

					if ( func.getAnalyticFunction( ).getOrderBy( ) != null )
					{
						list = func.getAnalyticFunction( )
								.getOrderBy( )
								.getItems( );
						addColumnToList( list, stmt );
					}
				}
				else if ( func.getWindowDef( ) != null )
				{
					if ( func.getWindowDef( ).getPartitionClause( ) != null )
					{
						TParseTreeNodeList list = func.getWindowDef( )
								.getPartitionClause( )
								.getExpressionList( );
						addColumnToList( list, stmt );
					}
					if ( func.getWindowDef( ).getOrderBy( ) != null )
					{
						TParseTreeNodeList list = func.getWindowDef( )
								.getOrderBy( )
								.getItems( );
						addColumnToList( list, stmt );
					}
				}
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.subquery_t )
			{

				TSelectSqlStatement select = lcexpr.getSubQuery( );
				handleSubquery( select );
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.case_t )
			{
				TCaseExpression expr = lcexpr.getCaseExpression( );
				TExpression conditionExpr = expr.getInput_expr( );
				if ( !impact.analyzeDlineage )
				{
					if ( conditionExpr != null )
					{
						conditionExpr.inOrderTraverse( this );
					}
				}
				TExpression defaultExpr = expr.getElse_expr( );
				if ( defaultExpr != null )
				{
					defaultExpr.inOrderTraverse( this );
				}
				TWhenClauseItemList list = expr.getWhenClauseItemList( );
				addColumnToList( list, stmt );
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.interval_t )
			{
				TIntervalExpression expression = lcexpr.getIntervalExpr( );
				if ( expression != null )
				{
					expression.getExpr( ).inOrderTraverse( this );
				}
			}
			return true;
		}

		private void handleSubquery( TSelectSqlStatement select )
		{
			if ( select.getResultColumnList( ) != null )
			{
				for ( int i = 0; i < select.getResultColumnList( ).size( ); i++ )
				{
					impact.linkFieldToTables( null,
							select.getResultColumnList( ).getResultColumn( i ),
							select,
							level + 1,
							ClauseType.select );
				}
			}
			else if ( select.getSetOperatorType( ) != ESetOperatorType.none )
			{
				handleSubquery( select.getLeftStmt( ) );
				handleSubquery( select.getRightStmt( ) );
			}
		}

		public void searchColumn( )
		{
			if ( this.expr != null )
				this.expr.inOrderTraverse( this );
		}
	}

	class Table
	{

		public String prefixName;
		public String tableAlias;
		public String tableName;
	}

	class TAlias
	{

		private String alias;
		private String column;
		private String aliasDisplayName;
		private String columnDisplayName;
		public Point location;
		public Point fieldLocation;
		public String columnHighlightInfo;
		public String aliasHighlightInfo;

		@Override
		public int hashCode( )
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType( ).hashCode( );
			result = prime
					* result
					+ ( ( alias == null ) ? 0 : alias.hashCode( ) );
			result = prime
					* result
					+ ( ( aliasDisplayName == null ) ? 0
							: aliasDisplayName.hashCode( ) );
			result = prime
					* result
					+ ( ( aliasHighlightInfo == null ) ? 0
							: aliasHighlightInfo.hashCode( ) );
			result = prime
					* result
					+ ( ( column == null ) ? 0 : column.hashCode( ) );
			result = prime
					* result
					+ ( ( columnDisplayName == null ) ? 0
							: columnDisplayName.hashCode( ) );
			result = prime
					* result
					+ ( ( columnHighlightInfo == null ) ? 0
							: columnHighlightInfo.hashCode( ) );
			result = prime
					* result
					+ ( ( fieldLocation == null ) ? 0
							: fieldLocation.hashCode( ) );
			result = prime
					* result
					+ ( ( location == null ) ? 0 : location.hashCode( ) );
			return result;
		}

		@Override
		public boolean equals( Object obj )
		{
			if ( this == obj )
				return true;
			if ( obj == null )
				return false;
			if ( getClass( ) != obj.getClass( ) )
				return false;
			TAlias other = (TAlias) obj;
			if ( !getOuterType( ).equals( other.getOuterType( ) ) )
				return false;
			if ( alias == null )
			{
				if ( other.alias != null )
					return false;
			}
			else if ( !alias.equals( other.alias ) )
				return false;
			if ( aliasDisplayName == null )
			{
				if ( other.aliasDisplayName != null )
					return false;
			}
			else if ( !aliasDisplayName.equals( other.aliasDisplayName ) )
				return false;
			if ( aliasHighlightInfo == null )
			{
				if ( other.aliasHighlightInfo != null )
					return false;
			}
			else if ( !aliasHighlightInfo.equals( other.aliasHighlightInfo ) )
				return false;
			if ( column == null )
			{
				if ( other.column != null )
					return false;
			}
			else if ( !column.equals( other.column ) )
				return false;
			if ( columnDisplayName == null )
			{
				if ( other.columnDisplayName != null )
					return false;
			}
			else if ( !columnDisplayName.equals( other.columnDisplayName ) )
				return false;
			if ( columnHighlightInfo == null )
			{
				if ( other.columnHighlightInfo != null )
					return false;
			}
			else if ( !columnHighlightInfo.equals( other.columnHighlightInfo ) )
				return false;
			if ( fieldLocation == null )
			{
				if ( other.fieldLocation != null )
					return false;
			}
			else if ( !fieldLocation.equals( other.fieldLocation ) )
				return false;
			if ( location == null )
			{
				if ( other.location != null )
					return false;
			}
			else if ( !location.equals( other.location ) )
				return false;
			return true;
		}

		public String getColumn( )
		{
			return column;
		}

		public void setColumn( String column )
		{
			this.columnDisplayName = column;
			this.column = SQLUtil.trimColumnStringQuote( column );
		}

		public String getAlias( )
		{
			return alias;
		}

		public void setAlias( String alias )
		{
			this.aliasDisplayName = alias;
			this.alias = SQLUtil.trimColumnStringQuote( alias );
		}

		public String getAliasDisplayName( )
		{
			return aliasDisplayName;
		}

		public String getColumnDisplayName( )
		{
			return columnDisplayName;
		}

		private ColumnImpact getOuterType( )
		{
			return ColumnImpact.this;
		}

	}

	public static class TColumn
	{

		public String expression = "";
		public String columnName;
		public String columnPrex;
		public String orignColumn;
		public Point location;
		public long offset;
		public long length;
		public List<String> tableNames = new ArrayList<String>( );
		public List<String> tableFullNames = new ArrayList<String>( );
		public ClauseType clauseType;
		public String alias;
		public String linkColumnName;
		private boolean isOrphan;

		public String getFullName( String tableName )
		{
			if ( tableName != null )
			{
				return tableName + "." + columnName;
			}
			else
			{
				return columnName;
			}
		}

		public String getOrigName( )
		{
			if ( columnPrex != null )
			{
				return columnPrex + "." + columnName;
			}
			else
			{
				return columnName;
			}
		}

	}

	class TResultEntry
	{

		public ClauseType clause;

		public String targetColumn;
		public TTable targetTable;
		public Point location;
		public TColumn columnObject;
		public boolean isConstant = false;

		public TResultEntry( TColumn columnObject, String column,
				ClauseType clause, Point location )
		{
			this.targetColumn = column;
			this.clause = clause;
			this.location = location;
			this.columnObject = columnObject;
			this.isConstant = true;
		}

		public TResultEntry( TTable table, String column, ClauseType clause,
				Point location )
		{
			this.targetTable = table;
			this.targetColumn = column;
			this.clause = clause;
			this.location = location;
			columnObject = new TColumn( );
			columnObject.columnName = "*";
			updateColumnTableFullName( table, columnObject );
		}

		public TResultEntry( TTable table, TColumn columnObject, String column,
				ClauseType clause, Point location )
		{
			this.targetTable = table;
			this.targetColumn = column;
			this.clause = clause;
			this.location = location;
			this.columnObject = columnObject;
			updateColumnTableFullName( table, this.columnObject );
		}

		private void updateColumnTableFullName( TTable table, TColumn column )
		{
			List<String> fullNames = column.tableFullNames;
			if ( fullNames != null )
			{
				for ( int i = 0; i < fullNames.size( ); i++ )
				{
					String tableName = table.getName( );
					String fullName = fullNames.get( i );
					if ( tableName != null )
					{
						fullName = fullName == null ? "" : fullName.trim( );
						if ( !tableName.equalsIgnoreCase( fullName ) )
						{
							if ( !fullNames.contains( table.getFullName( ) ) )
							{
								fullNames.remove( i );
								fullNames.add( i, table.getFullName( ) );
							}
						}
					}
				}
			}
		}
	}

	class TSourceColumn
	{

		public List<ClauseType> clauses = new ArrayList<ClauseType>( );
		public String name;
		public String tableName;
		public String tableOwner;
		public Map<ClauseType, List<Point>> locations = new LinkedHashMap<ClauseType, List<Point>>( );
		public Set<Point> highlightInfos = new HashSet<Point>( );
		public boolean isNotOrphan = false;
	}

	public TColumn attrToColumn( TExpression lcexpr, TCustomSqlStatement stmt,
			TExpression expr, boolean collectExpr, ClauseType clause,
			TAlias parentAlias )
	{
		TColumn column = attrToColumn( lcexpr, stmt, clause, parentAlias );

		if ( column == null )
			return null;

		if ( collectExpr )
		{
			column.expression = expr.toString( )
					.replace( "\r\n", "\n" )
					.replaceAll( "\n+", " " );
			if ( column.expression.trim( ).length( ) > 0 )
			{
				Stack<TParseTreeNode> tokens = expr.getStartToken( )
						.getNodesStartFromThisToken( );
				if ( tokens != null )
				{
					for ( int i = 0; i < tokens.size( ); i++ )
					{
						TParseTreeNode node = tokens.get( i );
						if ( node instanceof TResultColumn )
						{
							TResultColumn field = (TResultColumn) node;
							if ( field.getAliasClause( ) != null )
							{
								column.alias = field.getAliasClause( )
										.toString( );
							}
						}
					}
				}
			}
		}
		return column;
	}

	private boolean isKeyword( String column )
	{
		EDbVendor vendor = this.vendor;
		List<String> versions = keywordChecker.getAvailableDbVersions( vendor );
		if ( versions == null || versions.isEmpty( ) )
		{
			versions = keywordChecker.getAvailableDbVersions( EDbVendor.dbvansi );
			vendor = EDbVendor.dbvansi;
		}
		return keywordChecker.isKeyword( column,
				vendor,
				versions.get( versions.size( ) - 1 ),
				true );
	}

	/* store the relations of alias to column */
	private Set<TAlias> aliases = new HashSet<TAlias>( );
	private StringBuffer buffer = new StringBuffer( );
	private StringBuffer errorBuffer = new StringBuffer( );
	private boolean isSucess = true;
	private Map<String, LinkedHashMap<TCustomSqlStatement, Boolean>> accessMap = new LinkedHashMap<String, LinkedHashMap<TCustomSqlStatement, Boolean>>( );
	private Map<TExpression, TColumn> accessExpressions = new HashMap<TExpression, TColumn>( );
	private Map<TResultColumn, List<String>> accessColumns = new HashMap<TResultColumn, List<String>>( );
	private Map<TCustomSqlStatement, ClauseType> currentClauseMap = new LinkedHashMap<TCustomSqlStatement, ClauseType>( );
	private String currentSource = null;
	/* store the dependency relations */
	private Map<String, List<TResultEntry>> dependMap = new LinkedHashMap<String, List<TResultEntry>>( );
	private boolean debug = false;
	private boolean showUIInfo = false;
	private int columnNumber = 0;
	private TCustomSqlStatement subquery = null;
	private TGSqlParser sqlparser;
	private IMetaDatabase metaDB;
	private Element fileNode;
	private String database = null;
	private Map<TableMetaData, List<ColumnMetaData>> tableColumns;
	private EDbVendor vendor;
	private boolean strict = false;
	private boolean traceErrorMessage = true;
	private boolean analyzeDlineage = false;
	private boolean ignoreTopSelect = false;
	private String virtualTable = null;

	public void setVirtualTableName( String virtualTable )
	{
		this.virtualTable = virtualTable;
	}

	public void ignoreTopSelect( boolean ingore )
	{
		ignoreTopSelect = ingore;
	}

	public void setTraceErrorMessage( boolean trace )
	{
		traceErrorMessage = trace;
	}

	public void setShowUIInfo( boolean showUIInfo )
	{
		this.showUIInfo = showUIInfo;
	}

	public void setDebug( boolean debug )
	{
		this.debug = debug;
	}

	public boolean isSucess( )
	{
		return isSucess;
	}

	public void setSucess( boolean isSucess )
	{
		this.isSucess = isSucess;
	}

	public void setAnalyzeDlineage( boolean analyzeDlineage )
	{
		this.analyzeDlineage = analyzeDlineage;
	}

	public ColumnImpact( Element fileNode, EDbVendor dbVendor,
			Map<TableMetaData, List<ColumnMetaData>> tableColumns,
			boolean strict )
	{
		this.vendor = dbVendor;
		this.strict = strict;
		this.tableColumns = tableColumns;
		MetaDB metaDB = new MetaDB( tableColumns, strict );
		sqlparser = new TGSqlParser( dbVendor );
		sqlparser.sqltext = removeParenthesis( SQLUtil.getFileContent( fileNode.getAttribute( "name" ) ) );
		// sqlparser.setMetaDatabase( metaDB );
		this.metaDB = metaDB;
		this.fileNode = fileNode;
	}

	public ColumnImpact( String sqlText, EDbVendor dbVendor,
			Map<TableMetaData, List<ColumnMetaData>> tableColumns,
			boolean strict )
	{
		this.vendor = dbVendor;
		this.strict = strict;
		this.tableColumns = tableColumns;
		MetaDB metaDB = new MetaDB( tableColumns, strict );
		sqlparser = new TGSqlParser( dbVendor );
		if ( sqlText == null )
			sqlText = "";
		sqlparser.sqltext = removeParenthesis( sqlText );
		// sqlparser.setMetaDatabase( metaDB );
		this.metaDB = metaDB;
	}

	private String removeParenthesis( String sql )
	{
		String temp = sql.trim( );
		if ( temp.startsWith( "(" ) && temp.endsWith( ")" ) )
		{
			temp = temp.substring( 1, temp.length( ) - 1 );
			return removeParenthesis( temp );
		}
		return sql;
	}

	private TColumn attrToColumn( TExpression attr, TCustomSqlStatement stmt,
			ClauseType clauseType, TAlias parentAlias )
	{
		if ( stmt instanceof TSelectSqlStatement )
		{
			TSelectSqlStatement select = (TSelectSqlStatement) stmt;
			if ( select.tables != null && select.tables.size( ) == 1 )
			{
				TTable table = select.tables.getTable( 0 );
				if ( table.getTableName( ) == null
						&& table.getAliasClause( ) == null
						&& table.getSubquery( ) != null )
				{
					stmt = table.getSubquery( );
				}
			}
		}

		if ( sqlparser.getDbVendor( ) == EDbVendor.dbvteradata )
		{
			if ( ( clauseType == ClauseType.select
					|| clauseType == ClauseType.casewhen || clauseType == ClauseType.casethen )
					&& parentAlias != null )
			{

				if ( !fromStmtTable( stmt, attr.getObjectOperand( ) ) )
				{
					String columnName = SQLUtil.trimColumnStringQuote( attr.getObjectOperand( )
							.getEndToken( )
							.toString( ) );

					TResultColumn resultColumn = getResultColumnByAlias( stmt,
							columnName );

					if ( resultColumn != null )
					{
						if ( resultColumn.getAliasClause( ) != null
								&& !parentAlias.getAliasDisplayName( )
										.equalsIgnoreCase( resultColumn.getColumnAlias( ) ) )
						{
							linkFieldToTables( parentAlias,
									resultColumn,
									stmt,
									0,
									clauseType );
							return null;
						}
					}
				}
			}
		}

		TColumn column = new TColumn( );
		column.clauseType = clauseType;
		column.columnName = SQLUtil.trimColumnStringQuote( attr.getObjectOperand( )
				.getEndToken( )
				.toString( ) );
		column.location = new Point( (int) attr.getObjectOperand( )
				.getEndToken( ).lineNo, (int) attr.getEndToken( ).columnNo );
		column.offset = attr.getEndToken( ).offset;
		column.length = attr.getEndToken( ).astext.length( );

		Stack<TParseTreeNode> tokens = attr.getObjectOperand( )
				.getStartToken( )
				.getNodesStartFromThisToken( );

		if ( tokens != null )
		{
			for ( int i = 0; i < tokens.size( ); i++ )
			{
				TParseTreeNode node = tokens.get( i );
				if ( node instanceof TResultColumn )
				{
					TResultColumn field = (TResultColumn) node;
					if ( field.getAliasClause( ) != null )
					{
						column.alias = field.getAliasClause( ).toString( );
					}
				}
			}
		}

		List<String> segements = SQLUtil.parseNames( attr.toString( ) );
		if ( segements.size( ) > 1 )
		{
			column.columnPrex = SQLUtil.trimColumnStringQuote( attr.toString( )
					.substring( 0,
							attr.toString( ).lastIndexOf( "."
									+ segements.get( segements.size( ) - 1 ) ) ) );

			String tableName = SQLUtil.trimColumnStringQuote( column.columnPrex );
			if ( tableName.indexOf( "." ) > 0 )
			{
				tableName = SQLUtil.trimColumnStringQuote( tableName.substring( tableName.lastIndexOf( "." ) + 1 ) );
			}
			if ( !column.tableNames.contains( tableName ) )
			{
				column.tableNames.add( tableName );
				if ( !column.tableFullNames.contains( tableName ) )
					column.tableFullNames.add( tableName );
			}
		}
		else
		{
			TTableList tables = stmt.tables;
			boolean containColumn = false;
			if ( stmt instanceof TSelectSqlStatement )
			{
				for ( int i = 0; i < tables.size( ); i++ )
				{
					TTable lztable = tables.getTable( i );
					Table table = TLzTaleToTable( lztable );
					String tableName = lztable == null ? null
							: lztable.getFullName( );
					if ( metaDB != null && tableName != null )
					{
						int dotIndex = tableName.lastIndexOf( "." );
						String tableOwner = null;
						String tableRealName = null;
						if ( dotIndex >= 0 )
						{
							if ( lztable != null )
							{
								tableOwner = lztable.getTableName( )
										.getDatabaseString( );
							}
							if ( !isNotEmpty( tableOwner ) )
							{
								tableRealName = tableName;
							}
							else
							{
								tableRealName = tableName.replace( tableOwner
										+ ".", "" );
							}
						}
						else
						{
							tableRealName = tableName;
						}
						String tableDatabaseString = null;
						String tableSchemaString = null;
						String tableNameString = null;
						String[] split = tableRealName.split( "\\." );
						if ( split.length == 3 )
						{
							tableDatabaseString = split[0];
							tableSchemaString = split[1];
							tableNameString = split[2];
						}
						else if ( split.length == 2 )
						{
							tableDatabaseString = tableOwner;
							tableSchemaString = split[0];
							tableNameString = split[1];
						}
						else if ( split.length == 1 )
						{
							tableDatabaseString = tableOwner;
							tableNameString = split[0];
						}
						if ( metaDB.checkColumn( null,
								tableDatabaseString,
								tableSchemaString,
								tableNameString,
								column.getOrigName( ) ) )
						{
							containColumn = true;
							column.tableNames.add( table.tableName );
							if ( lztable != null
									&& !column.tableFullNames.contains( lztable.getFullName( ) ) )
								column.tableFullNames.add( lztable.getFullName( ) );
						}
					}
				}
			}

			for ( int i = 0; i < tables.size( ); i++ )
			{
				TTable lztable = tables.getTable( i );

				boolean[] result = new boolean[]{
					false
				};
				boolean flag = checkSubqueryTableColumns( parentAlias,
						clauseType,
						column,
						lztable,
						result );
				if ( result[0] )
				{
					return null;
				}
				if ( flag )
				{
					containColumn = true;
					continue;
				}

				Table table = TLzTaleToTable( lztable );

				if ( table.tableName == null )
				{
					continue;
				}

				TObjectNameList cteColumnNames = lztable.getCteColomnReferences( );
				if ( cteColumnNames != null && cteColumnNames.size( ) > 0 )
				{
					for ( int j = 0; j < cteColumnNames.size( ); j++ )
					{
						if ( cteColumnNames.getObjectName( j )
								.getColumnNameOnly( )
								.toString( )
								.equalsIgnoreCase( column.columnName ) )
						{
							TObjectName name = cteColumnNames.getObjectName( j );
							TCustomSqlStatement cteStmt = lztable.getCTE( )
									.getPreparableStmt( );
							if ( cteStmt != null
									&& cteStmt.getResultColumnList( ) != null
									&& cteStmt.getResultColumnList( ).size( ) > j )
							{
								linkFieldToTables( parentAlias,
										name,
										cteStmt.getResultColumnList( )
												.getResultColumn( j ),
										cteStmt,
										clauseType );
								return null;
							}
						}
					}
				}

				TObjectNameList columnNames = lztable.getLinkedColumns( );
				for ( int j = 0; j < columnNames.size( ); j++ )
				{
					if ( columnNames.getObjectName( j )
							.getColumnNameOnly( )
							.toString( )
							.equalsIgnoreCase( column.columnName ) )
					{
						column.tableNames.add( table.tableName );
						column.tableFullNames.add( lztable.getFullName( ) );
						containColumn = true;
					}
				}
			}

			if ( !containColumn )
			{
				TTable lztable = tables.getTable( 0 );
				Table table = TLzTaleToTable( lztable );
				if ( table.tableName != null )
				{
					if ( !column.tableNames.contains( table.tableName ) )
					{
						column.tableNames.add( table.tableName );
						if ( lztable != null
								&& !column.tableFullNames.contains( lztable.getFullName( ) ) )
						{
							column.tableFullNames.add( lztable.getFullName( ) );
						}
					}
				}

				if ( tables.size( ) > 1 && stmt instanceof TSelectSqlStatement )
				{
					if ( fileNode != null )
					{
						errorBuffer.append( "Orphan column: "
								+ attr.getObjectOperand( )
										.getEndToken( )
										.toString( )
								+ ", at line: "
								+ column.location.x
								+ ", column:"
								+ column.location.y
								+ "." );
						errorBuffer.append( " File: " )
								.append( fileNode.getAttribute( "name" ) );
						errorBuffer.append( "\r\n" );
					}
					column.isOrphan = true;
				}
				else if ( column.tableNames.isEmpty( ) )
				{
					column.tableNames.add( Dlineage.TABLE_CONSTANT );
					column.tableFullNames.add( Dlineage.TABLE_CONSTANT );
				}
			}
		}

		column.orignColumn = column.columnName;
		return column;
	}

	private boolean checkSubqueryTableColumns( TAlias parentAlias,
			ClauseType clauseType, TColumn column, TSelectSqlStatement stmt,
			boolean[] result )
	{
		if ( stmt.getSetOperatorType( ) != ESetOperatorType.none )
		{
			return checkSubqueryTableColumns( parentAlias,
					clauseType,
					column,
					stmt.getLeftStmt( ),
					result )
					|| checkSubqueryTableColumns( parentAlias,
							clauseType,
							column,
							stmt.getLeftStmt( ),
							result );
		}
		else
		{
			TTableList tables = stmt.getTables( );
			boolean flag = false;
			for ( int i = 0; i < tables.size( ); i++ )
			{
				if ( checkSubqueryTableColumns( parentAlias,
						clauseType,
						column,
						tables.getTable( i ),
						result ) )
				{
					flag = true;
				}
			}
			return flag;
		}
	}

	private boolean checkSubqueryTableColumns( TAlias parentAlias,
			ClauseType clauseType, TColumn column, TTable lztable,
			boolean[] result )
	{
		boolean flag = false;

		TObjectNameList columnNames = lztable.getLinkedColumns( );
		for ( int j = 0; j < columnNames.size( ); j++ )
		{
			if ( SQLUtil.trimColumnStringQuote( columnNames.getObjectName( j )
					.getColumnNameOnly( )
					.toString( ) ).equalsIgnoreCase( column.columnName ) )
			{
				TObjectName name = columnNames.getObjectName( j );

				if ( lztable.equals( name.getSourceTable( ) ) )
				{
					if ( !SQLUtil.isEmpty( name.getSourceTable( ).getName( ) ) )
					{
						column.tableNames.add( name.getSourceTable( ).getName( ) );
						column.tableFullNames.add( name.getSourceTable( )
								.getFullName( ) );
						return true;
					}
					else if ( name.getSourceTable( ).getSubquery( ) != null
							&& name.getSourceColumn( ) != null )
					{
						linkFieldToTables( parentAlias,
								name,
								name.getSourceColumn( ),
								name.getSourceTable( ).getSubquery( ),
								clauseType );
						result[0] = true;
						return true;
					}
				}
			}
		}

		if ( lztable.subquery != null )
		{
			flag = checkSubqueryTableColumns( parentAlias,
					clauseType,
					column,
					lztable.subquery,
					result );
		}

		return flag;
	}

	private boolean fromStmtTable( TCustomSqlStatement stmt,
			TObjectName objectName )
	{
		if ( objectName.getSourceTable( ) != null && stmt.tables != null )
		{
			for ( int i = 0; i < stmt.tables.size( ); i++ )
			{
				if ( stmt.tables.getTable( i )
						.equals( objectName.getSourceTable( ) ) )
				{
					return true;
				}
			}
		}
		return false;
	}

	private TResultColumn getResultColumnByAlias( TCustomSqlStatement stmt,
			String columnName )
	{
		TResultColumnList columns = stmt.getResultColumnList( );
		if ( columns != null )
		{
			for ( int i = 0; i < columns.size( ); i++ )
			{
				TResultColumn column = columns.getResultColumn( i );
				if ( column.getAliasClause( ) != null
						&& columnName.equalsIgnoreCase( column.getAliasClause( )
								.toString( ) ) )
					return column;
				if ( column.getColumnNameOnly( ) != null
						&& columnName.equalsIgnoreCase( column.getColumnNameOnly( ) ) )
					return column;
			}
		}
		return null;
	}

	private TColumn attrToColumn( TAlias parentAlias, TObjectName objectName,
			TCustomSqlStatement stmt, ClauseType clauseType )
	{
		TColumn column = new TColumn( );
		column.clauseType = clauseType;
		column.columnName = SQLUtil.trimColumnStringQuote( objectName.getEndToken( )
				.toString( ) );
		column.location = new Point( (int) objectName.getEndToken( ).lineNo,
				(int) objectName.getEndToken( ).columnNo );
		column.offset = objectName.getEndToken( ).offset;
		column.length = objectName.getEndToken( ).astext.length( );

		Stack<TParseTreeNode> tokens = objectName.getStartToken( )
				.getNodesStartFromThisToken( );

		if ( tokens != null )
		{
			for ( int i = 0; i < tokens.size( ); i++ )
			{
				TParseTreeNode node = tokens.get( i );
				if ( node instanceof TResultColumn )
				{
					TResultColumn field = (TResultColumn) node;
					if ( field.getAliasClause( ) != null )
					{
						column.alias = field.getAliasClause( ).toString( );
					}
				}
			}
		}

		List<String> segements = SQLUtil.parseNames( objectName.toString( ) );
		if ( objectName.toString( ).indexOf( "." ) > 0 )
		{
			column.columnPrex = SQLUtil.trimColumnStringQuote( objectName.toString( )
					.substring( 0,
							objectName.toString( ).lastIndexOf( "."
									+ segements.get( segements.size( ) - 1 ) ) ) );
			String tableName = SQLUtil.trimColumnStringQuote( column.columnPrex );

			if ( tableName.indexOf( "." ) > 0 )
			{
				tableName = SQLUtil.trimColumnStringQuote( tableName.substring( tableName.lastIndexOf( "." ) + 1 ) );
			}
			if ( !column.tableNames.contains( tableName ) )
			{
				column.tableNames.add( tableName );
				if ( !column.tableFullNames.contains( tableName ) )
					column.tableFullNames.add( tableName );
			}
		}
		else
		{
			TTableList tables = stmt.tables;
			boolean containColumn = false;
			if ( stmt instanceof TSelectSqlStatement )
			{
				for ( int i = 0; i < tables.size( ); i++ )
				{
					TTable lztable = tables.getTable( i );
					Table table = TLzTaleToTable( lztable );
					String tableName = lztable == null ? null
							: lztable.getFullName( );
					if ( metaDB != null && tableName != null )
					{
						int dotIndex = tableName.lastIndexOf( "." );
						String tableOwner = null;
						String tableRealName = null;
						if ( dotIndex >= 0 )
						{
							if ( lztable != null )
							{
								tableOwner = lztable.getTableName( )
										.getDatabaseString( );
							}
							if ( !isNotEmpty( tableOwner ) )
							{
								tableRealName = tableName;
							}
							else
							{
								tableRealName = tableName.replace( tableOwner
										+ ".", "" );
							}
						}
						else
						{
							tableRealName = tableName;
						}
						String tableDatabaseString = null;
						String tableSchemaString = null;
						String tableNameString = null;
						String[] split = tableRealName.split( "\\." );
						if ( split.length == 3 )
						{
							tableDatabaseString = split[0];
							tableSchemaString = split[1];
							tableNameString = split[2];
						}
						else if ( split.length == 2 )
						{
							tableDatabaseString = tableOwner;
							tableSchemaString = split[0];
							tableNameString = split[1];
						}
						else if ( split.length == 1 )
						{
							tableDatabaseString = tableOwner;
							tableNameString = split[0];
						}
						if ( metaDB.checkColumn( null,
								tableDatabaseString,
								tableSchemaString,
								tableNameString,
								column.getOrigName( ) ) )
						{
							containColumn = true;
							column.tableNames.add( table.tableName );
							if ( lztable != null
									&& !column.tableFullNames.contains( lztable.getFullName( ) ) )
								column.tableFullNames.add( lztable.getFullName( ) );
						}
					}
				}
			}

			for ( int i = 0; i < tables.size( ); i++ )
			{
				TTable lztable = tables.getTable( i );

				boolean[] result = new boolean[]{
					false
				};
				boolean flag = checkSubqueryTableColumns( parentAlias,
						clauseType,
						column,
						lztable,
						result );
				if ( result[0] )
				{
					return null;
				}
				if ( flag )
				{
					containColumn = true;
					continue;
				}

				Table table = TLzTaleToTable( lztable );

				if ( table.tableName == null )
				{
					continue;
				}

				TObjectNameList cteColumnNames = lztable.getCteColomnReferences( );
				if ( cteColumnNames != null && cteColumnNames.size( ) > 0 )
				{
					for ( int j = 0; j < cteColumnNames.size( ); j++ )
					{
						if ( cteColumnNames.getObjectName( j )
								.getColumnNameOnly( )
								.toString( )
								.equalsIgnoreCase( column.columnName ) )
						{
							TObjectName name = cteColumnNames.getObjectName( j );
							TCustomSqlStatement cteStmt = lztable.getCTE( )
									.getPreparableStmt( );
							if ( cteStmt != null
									&& cteStmt.getResultColumnList( ) != null
									&& cteStmt.getResultColumnList( ).size( ) > j )
							{
								linkFieldToTables( parentAlias,
										name,
										cteStmt.getResultColumnList( )
												.getResultColumn( j ),
										cteStmt,
										clauseType );
								return null;
							}
						}
					}
				}

				TObjectNameList columnNames = lztable.getLinkedColumns( );
				if ( columnNames != null && columnNames.size( ) > 0 )
				{
					for ( int j = 0; j < columnNames.size( ); j++ )
					{
						if ( columnNames.getObjectName( j )
								.getColumnNameOnly( )
								.toString( )
								.equalsIgnoreCase( column.columnName ) )
						{
							column.tableNames.add( table.tableName );
							column.tableFullNames.add( lztable.getFullName( ) );
							containColumn = true;
						}
					}
				}

			}

			if ( !containColumn )
			{
				for ( int i = 0; i < stmt.getResultColumnList( ).size( ); i++ )
				{
					TResultColumn column1 = stmt.getResultColumnList( )
							.getResultColumn( i );
					if ( column1.getAliasClause( ) != null
							&& column1.getColumnAlias( )
									.equalsIgnoreCase( objectName.getColumnNameOnly( ) ) )
					{
						List<TColumn> columns = exprToColumn( column1.getExpr( ),
								stmt,
								0,
								clauseType );
						if ( columns != null && !columns.isEmpty( ) )
						{
							containColumn = true;
							column.orignColumn = column1.getExpr( ).toString( );
							return column;
						}
					}
				}
			}

			if ( !containColumn && tables.size( ) > 0 )
			{
				TTable lztable = tables.getTable( 0 );
				Table table = TLzTaleToTable( lztable );
				if ( table.tableName != null )
				{
					if ( !column.tableNames.contains( table.tableName ) )
					{
						column.tableNames.add( table.tableName );
						if ( lztable != null
								&& !column.tableFullNames.contains( lztable.getFullName( ) ) )
							column.tableFullNames.add( lztable.getFullName( ) );
					}
				}

				if ( tables.size( ) > 1 && stmt instanceof TSelectSqlStatement )
				{
					if ( fileNode != null )
					{
						errorBuffer.append( "Orphan column: "
								+ objectName.getEndToken( ).toString( )
								+ ", at line: "
								+ column.location.x
								+ ", column:"
								+ column.location.y
								+ "." );
						errorBuffer.append( " File: " )
								.append( fileNode.getAttribute( "name" ) );
						errorBuffer.append( "\r\n" );
					}
					column.isOrphan = true;
				}
			}
		}

		column.orignColumn = column.columnName;
		return column;
	}

	private static boolean isNotEmpty( String str )
	{
		return str != null && str.trim( ).length( ) > 0;
	}

	private String buildString( String string, int level )
	{
		StringBuffer buffer = new StringBuffer( );
		for ( int i = 0; i < level; i++ )
		{
			buffer.append( string );
		}
		return buffer.toString( );
	}

	private TCustomSqlStatement containClasuse(
			Map<TCustomSqlStatement, ClauseType> currentClauseMap,
			TCustomSqlStatement select )
	{
		if ( currentClauseMap.containsKey( select ) )
			return select;
		else if ( select.getParentStmt( ) instanceof TCustomSqlStatement )
		{
			return containClasuse( currentClauseMap, select.getParentStmt( ) );
		}
		else
			return null;
	}

	private List<TColumn> exprToColumn( TExpression expr,
			TCustomSqlStatement stmt, int level, ClauseType clauseType )
	{
		List<TColumn> columns = new ArrayList<TColumn>( );

		columnsInExpr c = new columnsInExpr( this,
				expr,
				columns,
				stmt,
				level,
				false,
				clauseType,
				null );
		c.searchColumn( );

		return columns;
	}

	private List<TColumn> exprToColumn( TExpression expr,
			TCustomSqlStatement stmt, int level, ClauseType clauseType,
			TAlias parentAlias )
	{
		List<TColumn> columns = new ArrayList<TColumn>( );

		columnsInExpr c = new columnsInExpr( this,
				expr,
				columns,
				stmt,
				level,
				false,
				clauseType,
				parentAlias );
		c.searchColumn( );

		return columns;
	}

	private List<TColumn> exprToColumn( TExpression expr,
			TCustomSqlStatement stmt, int level, boolean collectExpr,
			ClauseType clauseType, TAlias parentAlias )
	{
		List<TColumn> columns = new ArrayList<TColumn>( );

		columnsInExpr c = new columnsInExpr( this,
				expr,
				columns,
				stmt,
				level,
				collectExpr,
				clauseType,
				parentAlias );
		c.searchColumn( );

		return columns;
	}

	private boolean findColumnInSubQuery( TSelectSqlStatement select,
			String columnName, int level, Point originLocation )
	{
		return findColumnInSubQuery( select,
				(TAliasClause) null,
				columnName,
				level,
				originLocation );
	}

	private boolean findColumnInSubQuery( TSelectSqlStatement select,
			TAliasClause tableAlias, String columnName, int level,
			Point originLocation )
	{
		boolean ret = false;
		String key = ( tableAlias != null ? tableAlias.toString( ) : "" )
				+ ":"
				+ columnName;
		if ( accessMap.get( key ) != null
				&& accessMap.get( key ).containsKey( select ) )
		{
			return accessMap.get( key ).get( select );
		}
		else
		{
			if ( !accessMap.containsKey( key ) )
			{
				accessMap.put( key,
						new LinkedHashMap<TCustomSqlStatement, Boolean>( ) );
			}
			Map<TCustomSqlStatement, Boolean> stmts = accessMap.get( key );
			stmts.put( select, false );
		}
		if ( select.getSetOperatorType( ) != ESetOperatorType.none )
		{
			boolean left = findColumnInSubQuery( select.getLeftStmt( ),
					tableAlias,
					columnName,
					level,
					originLocation );
			boolean right = findColumnInSubQuery( select.getRightStmt( ),
					tableAlias,
					columnName,
					level,
					originLocation );
			ret = left && right;
		}
		else if ( select.getResultColumnList( ) != null )
		{
			// check colum name in select list of subquery
			TResultColumn columnField = null;
			if ( !"*".equals( columnName ) )
			{
				for ( int i = 0; i < select.getResultColumnList( ).size( ); i++ )
				{
					TResultColumn field = select.getResultColumnList( )
							.getResultColumn( i );
					if ( field.getAliasClause( ) != null )
					{
						if ( field.getAliasClause( )
								.toString( )
								.equalsIgnoreCase( columnName ) )
						{
							columnField = field;
							break;
						}
					}
					else
					{
						if ( field.getExpr( ).getExpressionType( ) == EExpressionType.simple_object_name_t )
						{
							TColumn column = attrToColumn( field.getExpr( ),
									select,
									ClauseType.select,
									null );
							if ( column == null )
							{
								continue;
							}
							if ( columnName != null
									&& columnName.equalsIgnoreCase( column.columnName ) )
							{
								columnField = field;
								break;
							}
							else if ( "*".equals( column.columnName )
									&& select.getResultColumnList( ).size( ) == 1 )
							{
								if ( select.tables.getTable( 0 ) != null )
								{
									TObjectNameList columns = select.tables.getTable( 0 )
											.getLinkedColumns( );
									for ( int j = 0; j < columns.size( ); j++ )
									{
										TObjectName queryColumn = columns.getObjectName( j );
										if ( tableAlias != null
												&& queryColumn.getTableString( ) != null
												&& tableAlias.toString( )
														.equalsIgnoreCase( queryColumn.getTableString( ) ) )
										{
											if ( columnName.equalsIgnoreCase( queryColumn.getColumnNameOnly( ) ) )
											{
												column.columnName = queryColumn.getColumnNameOnly( );
												findColumnInTables( column,
														select,
														level,
														ret == false ? columnName
																: null,
														originLocation );
												findColumnsFromClauses( select,
														level + 1 );
												return true;
											}
										}

										if ( tableAlias == null
												&& queryColumn.getTableString( ) == null )
										{
											if ( columnName.equalsIgnoreCase( queryColumn.getColumnNameOnly( ) ) )
											{
												column.columnName = queryColumn.getColumnNameOnly( );
												findColumnInTables( column,
														select,
														level,
														ret == false ? columnName
																: null,
														originLocation );
												findColumnsFromClauses( select,
														level + 1 );
												return true;
											}
										}
									}
								}
								columnField = field;
								break;
							}
						}
					}
				}
			}
			for ( int i = 0; i < select.getResultColumnList( ).size( ); i++ )
			{
				TResultColumn field = select.getResultColumnList( )
						.getResultColumn( i );
				if ( columnField != null && !field.equals( columnField ) )
				{
					continue;
				}
				if ( field.getAliasClause( ) != null )
				{
					ret = "*".equals( columnName )
							|| field.getAliasClause( )
									.toString( )
									.equalsIgnoreCase( columnName );
					if ( ret )
					{
						// let's check where this column come from?
						if ( debug )
						{
							buffer.append( buildString( " ", level )
									+ "--> "
									+ field.getAliasClause( ).toString( )
									+ "(alias)\r\n" );
						}
						linkFieldToTables( null,
								field,
								select,
								level,
								ClauseType.select );
					}
				}
				else
				{
					if ( field.getExpr( ).getExpressionType( ) == EExpressionType.simple_object_name_t )
					{
						TColumn column = attrToColumn( field.getExpr( ),
								select,
								ClauseType.select,
								null );
						if ( column == null )
						{
							continue;
						}
						ret = "*".equals( columnName )
								|| ( columnName != null && columnName.equalsIgnoreCase( column.columnName ) );
						if ( ret || "*".equals( column.columnName ) )
						{
							if ( "*".equals( column.columnName ) )
							{
								column.columnName = columnName;
							}
							findColumnInTables( column,
									select,
									level,
									ret == false ? columnName : null,
									originLocation );
							findColumnsFromClauses( select, level + 1 );
						}
					}
				}

				if ( ret && !"*".equals( columnName ) )
					break;
			}
		}

		if ( !accessMap.containsKey( key ) )
		{
			accessMap.put( key,
					new LinkedHashMap<TCustomSqlStatement, Boolean>( ) );
		}
		Map<TCustomSqlStatement, Boolean> stmts = accessMap.get( key );
		stmts.put( select, ret );

		return ret;
	} // findColumnInSubQuery

	private boolean findColumnInTables( TColumn column, String tableName,
			TCustomSqlStatement select, int level )
	{
		if ( column.clauseType != null )
		{
			return findColumnInTables( column,
					tableName,
					select,
					level,
					column.clauseType );
		}
		else
			return findColumnInTables( column,
					tableName,
					select,
					level,
					ClauseType.undefine );
	}

	private boolean findColumnInTables( TColumn column, String tableName,
			TCustomSqlStatement select, int level, ClauseType clause )
	{
		boolean ret = false;

		if ( Dlineage.TABLE_CONSTANT.equalsIgnoreCase( tableName )
				&& currentSource != null )
		{
			dependMap.get( currentSource ).add( new TResultEntry( column,
					column.columnName,
					clause,
					column.location ) );
			return true;
		}

		TTableList tables = select.tables;

		// merge using

		if ( tables.size( ) == 1 )
		{
			TTable lzTable = tables.getTable( 0 );
			// buffer.AppendLine(lzTable.AsText);
			if ( ( lzTable.getTableType( ) == ETableSource.objectname )
					&& ( tableName == null
							|| ( tableName != null
									&& lzTable.getAliasClause( ) == null && getTableName( lzTable ).equalsIgnoreCase( SQLUtil.trimColumnStringQuote( tableName ) ) ) || ( tableName != null
							&& lzTable.getAliasClause( ) != null && lzTable.getAliasClause( )
							.toString( )
							.equalsIgnoreCase( tableName ) ) ) )
			{
				ret = true;

				if ( debug )
				{
					buffer.append( buildString( " ", level )
							+ "--> "
							+ getTableName( lzTable )
							+ "."
							+ column.columnName
							+ "\r\n" );
				}
				if ( lzTable.getCTE( ) != null )
				{
					if ( debug )
					{
						buffer.append( buildString( " ", level )
								+ "--> WITH CTE\r\n" );
					}
					ret = findColumnInSubQuery( lzTable.getCTE( ).getSubquery( ),
							column.columnName,
							level,
							column.location );
				}
				else
				{
					if ( currentSource != null
							&& dependMap.containsKey( currentSource ) )
					{
						TCustomSqlStatement stmt = containClasuse( currentClauseMap,
								select );
						if ( stmt != null )
						{
							dependMap.get( currentSource )
									.add( new TResultEntry( lzTable,
											column,
											column.columnName,
											currentClauseMap.get( stmt ),
											column.location ) );
						}
						else if ( select instanceof TSelectSqlStatement )
						{
							if ( ClauseType.undefine.equals( clause ) )
								dependMap.get( currentSource )
										.add( new TResultEntry( lzTable,
												column,
												column.columnName,
												ClauseType.select,
												column.location ) );
							else
								dependMap.get( currentSource )
										.add( new TResultEntry( lzTable,
												column,
												column.columnName,
												clause,
												column.location ) );
						}
						else if ( select instanceof TInsertSqlStatement )
						{
							if ( ClauseType.undefine.equals( clause ) )
								dependMap.get( currentSource )
										.add( new TResultEntry( lzTable,
												column,
												column.columnName,
												ClauseType.insert,
												column.location ) );
							else
								dependMap.get( currentSource )
										.add( new TResultEntry( lzTable,
												column,
												column.columnName,
												clause,
												column.location ) );
						}
						else if ( select instanceof TUpdateSqlStatement )
						{
							if ( ClauseType.undefine.equals( clause ) )
								dependMap.get( currentSource )
										.add( new TResultEntry( lzTable,
												column,
												column.columnName,
												ClauseType.updateset,
												column.location ) );
							else
								dependMap.get( currentSource )
										.add( new TResultEntry( lzTable,
												column,
												column.columnName,
												clause,
												column.location ) );
						}
						else if ( select instanceof TMergeSqlStatement )
						{
							if ( ClauseType.undefine.equals( clause ) )
								dependMap.get( currentSource )
										.add( new TResultEntry( lzTable,
												column,
												column.columnName,
												ClauseType.mergeset,
												column.location ) );
							else
								dependMap.get( currentSource )
										.add( new TResultEntry( lzTable,
												column,
												column.columnName,
												clause,
												column.location ) );
						}
						else
						{
							dependMap.get( currentSource )
									.add( new TResultEntry( lzTable,
											column,
											column.columnName,
											ClauseType.undefine,
											column.location ) );
						}
					}
				}
			}
			else if ( select.getParentStmt( ) instanceof TSelectSqlStatement )
			{
				subquery = select;
				ret = findColumnInTables( column,
						tableName,
						select.getParentStmt( ),
						level,
						clause );
				subquery = null;
			}
		}

		if ( ret )
			return ret;

		for ( int x = 0; x < tables.size( ); x++ )
		{
			TTable tempTable = tables.getTable( x );
			TTable lzTable = tempTable;
			if ( tempTable.getLinkTable( ) != null )
			{
				lzTable.setTableName( tempTable.getLinkTable( ).getTableName( ) );
				TAliasClause alias = new TAliasClause( );
				alias.setAliasName( tempTable.getTableName( ) );
				lzTable.setAliasClause( alias );
			}
			switch ( lzTable.getTableType( ) )
			{
				case objectname :
					Table table = TLzTaleToTable( lzTable );
					String alias = table.tableAlias;
					if ( alias != null )
						alias = alias.trim( );
					if ( ( tableName != null )
							&& ( ( tableName.equalsIgnoreCase( alias ) || tableName.equalsIgnoreCase( table.tableName ) ) ) )
					{
						if ( debug )
						{
							buffer.append( buildString( " ", level )
									+ "--> "
									+ table.tableName
									+ "."
									+ column.columnName
									+ "\r\n" );
						}
						if ( lzTable.getCTE( ) != null )
						{
							if ( debug )
							{
								buffer.append( buildString( " ", level )
										+ "--> WITH CTE\r\n" );
							}
							ret = findColumnInSubQuery( lzTable.getCTE( )
									.getSubquery( ),
									column.columnName,
									level,
									column.location );
						}
						else
						{
							if ( dependMap.containsKey( currentSource ) )
							{
								String columnName = column.orignColumn;
								if ( "*".equals( columnName ) )
									columnName = column.columnName;
								if ( currentClauseMap.containsKey( select ) )
								{
									dependMap.get( currentSource )
											.add( new TResultEntry( lzTable,
													column,
													columnName,
													(ClauseType) currentClauseMap.get( select ),
													column.location ) );
								}
								else if ( select instanceof TSelectSqlStatement )
								{
									if ( ClauseType.undefine.equals( clause ) )
										dependMap.get( currentSource )
												.add( new TResultEntry( lzTable,
														column,
														column.columnName,
														ClauseType.select,
														column.location ) );
									else
										dependMap.get( currentSource )
												.add( new TResultEntry( lzTable,
														column,
														column.columnName,
														clause,
														column.location ) );
								}
								else if ( select instanceof TInsertSqlStatement )
								{
									if ( ClauseType.undefine.equals( clause ) )
										dependMap.get( currentSource )
												.add( new TResultEntry( lzTable,
														column,
														column.columnName,
														ClauseType.insert,
														column.location ) );
									else
										dependMap.get( currentSource )
												.add( new TResultEntry( lzTable,
														column,
														column.columnName,
														clause,
														column.location ) );
								}
								else if ( select instanceof TUpdateSqlStatement )
								{
									if ( ClauseType.undefine.equals( clause ) )
										dependMap.get( currentSource )
												.add( new TResultEntry( lzTable,
														column,
														column.columnName,
														ClauseType.update,
														column.location ) );
									else
										dependMap.get( currentSource )
												.add( new TResultEntry( lzTable,
														column,
														column.columnName,
														clause,
														column.location ) );
								}
								else if ( select instanceof TMergeSqlStatement )
								{
									if ( ClauseType.undefine.equals( clause ) )
										dependMap.get( currentSource )
												.add( new TResultEntry( lzTable,
														column,
														column.columnName,
														ClauseType.merge,
														column.location ) );
									else
										dependMap.get( currentSource )
												.add( new TResultEntry( lzTable,
														column,
														column.columnName,
														clause,
														column.location ) );
								}
								else
								{
									dependMap.get( currentSource )
											.add( new TResultEntry( lzTable,
													column,
													columnName,
													ClauseType.undefine,
													column.location ) );
								}
							}
							ret = true;
						}
					}
					break;
				case subquery :
					for ( int i = 0; i < column.tableNames.size( ); i++ )
					{
						String name = column.tableNames.get( i );
						TSelectSqlStatement selectStat = (TSelectSqlStatement) lzTable.getSubquery( );

						if ( selectStat == subquery )
							continue;

						if ( name == null )
						{
							ret = findColumnInSubQuery( selectStat,
									column.columnName,
									level,
									column.location );
							break;
						}

						if ( lzTable.getAliasClause( ) != null
								&& getTableAliasName( lzTable ).equalsIgnoreCase( name ) )
						{
							ret = findColumnInSubQuery( selectStat,
									lzTable.getAliasClause( ),
									column.columnName,
									level,
									column.location );
							break;
						}

						if ( selectStat.getSetOperatorType( ) != ESetOperatorType.none )
						{
							ret = findColumnInSubQuery( selectStat,
									lzTable.getAliasClause( ),
									column.columnName,
									level,
									column.location );
							break;
						}

						boolean flag = false;
						for ( int j = 0; j < selectStat.tables.size( ); j++ )
						{
							if ( selectStat.tables.getTable( j )
									.getAliasClause( ) != null )
							{
								TTable tableItem = selectStat.tables.getTable( j );
								if ( getTableAliasName( tableItem ).equalsIgnoreCase( name )
										|| ( tableItem.getName( ) != null && name.equalsIgnoreCase( tableItem.getName( ) ) ) )
								{
									ret = findColumnInSubQuery( selectStat,
											column.columnName,
											level,
											column.location );
									flag = true;
									break;
								}
							}
							else
							{
								if ( selectStat.tables.getTable( j )
										.getSubquery( ) != null )
								{
									ret = findColumnInSubQuery( selectStat.tables.getTable( j )
											.getSubquery( ),
											column.columnName,
											level,
											column.location );
									flag = true;
									break;
								}
								else if ( selectStat.tables.getTable( j )
										.getTableName( ) != null
										&& selectStat.tables.getTable( j )
												.getTableName( )
												.toString( )
												.equalsIgnoreCase( name ) )
								{
									ret = findColumnInSubQuery( selectStat,
											column.columnName,
											level,
											column.location );
									flag = true;
									break;
								}
							}
						}
						if ( flag )
							break;
					}
					break;
				default :
					break;
			}
			if ( ret )
				break;
		}

		if ( !ret && select.getParentStmt( ) != null )
		{
			subquery = select;
			ret = findColumnInTables( column,
					tableName,
					select.getParentStmt( ),
					level,
					clause );
			subquery = null;
		}

		return ret;
	}

	private String getTableAliasName( TTable lztable )
	{
		if ( lztable == null )
			return null;
		return SQLUtil.trimColumnStringQuote( lztable.getAliasClause( )
				.getAliasName( )
				.toString( ) );
	}

	private String getTableName( TTable lzTable )
	{
		return SQLUtil.trimColumnStringQuote( lzTable.getName( ) );
	}

	private boolean findColumnInTables( TColumn column,
			TCustomSqlStatement select, int level, String columnName,
			Point originLocation )
	{
		boolean ret = false;
		for ( String tableName : column.tableNames )
		{
			if ( columnName != null && metaDB != null )
			{
				int dotIndex = tableName.lastIndexOf( "." );
				String tableOwner = null;
				String tableRealName = null;
				if ( dotIndex >= 0 )
				{
					tableOwner = tableName.substring( 0, dotIndex );
					tableRealName = tableName.replace( tableOwner + ".", "" );
				}
				else
				{
					tableRealName = tableName;
				}
				if ( metaDB.checkColumn( null,
						null,
						tableOwner,
						tableRealName,
						columnName ) )
				{
					column.columnName = columnName;
					if ( originLocation != null )
						column.location = originLocation;
					// column.orignColumn = "*";
					ret |= findColumnInTables( column, tableName, select, level );
				}
				else if ( column.tableNames.size( ) == 1 )
				{
					ret |= findColumnInTables( column, tableName, select, level );
				}
				else
					ret |= false;
			}
			else
				ret |= findColumnInTables( column, tableName, select, level );
		}
		return ret;
	}

	private void findColumnsFromClauses( TCustomSqlStatement select, int level )
	{
		if ( analyzeDlineage )
			return;
		currentClauseMap.put( select, ClauseType.undefine );
		Map<TExpression, ClauseType> clauseTable = new LinkedHashMap<TExpression, ClauseType>( );
		if ( select instanceof TSelectSqlStatement )
		{

			TSelectSqlStatement statement = (TSelectSqlStatement) select;

			if ( statement.getOrderbyClause( ) != null )
			{
				TOrderBy sortList = statement.getOrderbyClause( );
				for ( int i = 0; i < sortList.getItems( ).size( ); i++ )
				{
					TOrderByItem orderBy = sortList.getItems( )
							.getOrderByItem( i );
					TExpression expr = orderBy.getSortKey( );
					clauseTable.put( expr, ClauseType.orderby );
				}
			}

			if ( statement.getWhereClause( ) != null )
			{
				clauseTable.put( statement.getWhereClause( ).getCondition( ),
						ClauseType.where );
			}
			if ( statement.getHierarchicalClause( ) != null
					&& statement.getHierarchicalClause( ).getConnectByList( ) != null )
			{
				for ( int i = 0; i < statement.getHierarchicalClause( )
						.getConnectByList( )
						.size( ); i++ )
				{
					clauseTable.put( statement.getHierarchicalClause( )
							.getConnectByList( )
							.getElement( i )
							.getCondition( ), ClauseType.connectby );
				}
			}
			if ( statement.getHierarchicalClause( ) != null
					&& statement.getHierarchicalClause( ).getStartWithClause( ) != null )
			{
				clauseTable.put( statement.getHierarchicalClause( )
						.getStartWithClause( ), ClauseType.startwith );
			}
			if ( statement.joins != null )
			{
				for ( int i = 0; i < statement.joins.size( ); i++ )
				{
					TJoin join = statement.joins.getJoin( i );
					if ( join.getJoinItems( ) != null )
					{
						for ( int j = 0; j < join.getJoinItems( ).size( ); j++ )
						{
							TJoinItem joinItem = join.getJoinItems( )
									.getJoinItem( j );
							TExpression expr = joinItem.getOnCondition( );
							if ( expr != null )
								clauseTable.put( expr, ClauseType.join );
						}
					}
				}
			}
		}
		else if ( select instanceof TUpdateSqlStatement )
		{
			TUpdateSqlStatement statement = (TUpdateSqlStatement) select;
			if ( statement.getOrderByClause( ) != null )
			{
				TOrderByItemList sortList = statement.getOrderByClause( )
						.getItems( );
				for ( int i = 0; i < sortList.size( ); i++ )
				{
					TOrderByItem orderBy = sortList.getOrderByItem( i );
					TExpression expr = orderBy.getSortKey( );
					clauseTable.put( expr, ClauseType.orderby );
				}
			}
			if ( statement.getWhereClause( ) != null )
			{
				clauseTable.put( statement.getWhereClause( ).getCondition( ),
						ClauseType.where );
			}

			if ( statement.joins != null )
			{
				for ( int i = 0; i < statement.joins.size( ); i++ )
				{
					TJoin join = statement.joins.getJoin( i );
					if ( join.getJoinItems( ) != null )
					{
						for ( int j = 0; j < join.getJoinItems( ).size( ); j++ )
						{
							TJoinItem joinItem = join.getJoinItems( )
									.getJoinItem( j );
							TExpression expr = joinItem.getOnCondition( );
							if ( expr != null )
								clauseTable.put( expr, ClauseType.join );
						}
					}
				}
			}
		}
		else if ( select instanceof TInsertSqlStatement )
		{
			TInsertSqlStatement statement = (TInsertSqlStatement) select;

			if ( statement.getWhereClause( ) != null )
			{
				clauseTable.put( statement.getWhereClause( ).getCondition( ),
						ClauseType.where );
			}

			if ( statement.joins != null )
			{
				for ( int i = 0; i < statement.joins.size( ); i++ )
				{
					TJoin join = statement.joins.getJoin( i );
					if ( join.getJoinItems( ) != null )
					{
						for ( int j = 0; j < join.getJoinItems( ).size( ); j++ )
						{
							TJoinItem joinItem = join.getJoinItems( )
									.getJoinItem( j );
							TExpression expr = joinItem.getOnCondition( );
							if ( expr != null )
								clauseTable.put( expr, ClauseType.join );
						}
					}
				}
			}
		}
		else if ( select instanceof TMergeSqlStatement )
		{
			TMergeSqlStatement statement = (TMergeSqlStatement) select;

			if ( statement.getWhereClause( ) != null )
			{
				clauseTable.put( statement.getWhereClause( ).getCondition( ),
						ClauseType.where );
			}

			if ( statement.joins != null )
			{
				for ( int i = 0; i < statement.joins.size( ); i++ )
				{
					TJoin join = statement.joins.getJoin( i );
					if ( join.getJoinItems( ) != null )
					{
						for ( int j = 0; j < join.getJoinItems( ).size( ); j++ )
						{
							TJoinItem joinItem = join.getJoinItems( )
									.getJoinItem( j );
							TExpression expr = joinItem.getOnCondition( );
							if ( expr != null )
								clauseTable.put( expr, ClauseType.join );
						}
					}
				}
			}

			if ( statement.getCondition( ) != null )
			{
				clauseTable.put( statement.getCondition( ),
						ClauseType.mergematch );
			}

			if ( statement.getMatchedSearchCondition( ) != null )
			{
				clauseTable.put( statement.getMatchedSearchCondition( ),
						ClauseType.mergematch );
			}

			if ( statement.getNotMatchedSearchCondition( ) != null )
			{
				clauseTable.put( statement.getNotMatchedSearchCondition( ),
						ClauseType.mergenotmatch );
			}

			for ( int i = 0; i < statement.getWhenClauses( ).size( ); i++ )
			{
				TMergeWhenClause whenClause = statement.getWhenClauses( )
						.getElement( i );
				if ( whenClause.getUpdateClause( ) != null
						&& whenClause.getUpdateClause( ).getUpdateWhereClause( ) != null )
				{
					clauseTable.put( whenClause.getUpdateClause( )
							.getUpdateWhereClause( ), ClauseType.where );
				}
				else if ( whenClause.getInsertClause( ) != null
						&& whenClause.getInsertClause( ).getInsertWhereClause( ) != null )
				{
					clauseTable.put( whenClause.getInsertClause( )
							.getInsertWhereClause( ), ClauseType.where );
				}
				else if ( whenClause.getDeleteClause( ) != null )
				{

				}
			}

		}

		for ( TExpression expr : clauseTable.keySet( ) )
		{
			currentClauseMap.put( select, clauseTable.get( expr ) );

			if ( debug )
			{
				switch ( currentClauseMap.get( select ) )
				{
					case where :
						buffer.append( buildString( " ", level )
								+ "--> Where Clause\r\n" );
						break;
					case connectby :
						buffer.append( buildString( " ", level )
								+ "--> Connect By Clause\r\n" );
						break;
					case startwith :
						buffer.append( buildString( " ", level )
								+ "--> Start With Clause\r\n" );
						break;
					case orderby :
						buffer.append( buildString( " ", level )
								+ "--> Order By Clause\r\n" );
						break;
					case join :
						buffer.append( buildString( " ", level )
								+ "--> Join\r\n" );
						break;
					default :
						break;
				}

			}

			List<TColumn> columns = exprToColumn( expr,
					select,
					level,
					clauseTable.get( expr ) );
			for ( TColumn column1 : columns )
			{
				for ( String tableName : column1.tableNames )
				{
					if ( debug )
					{

						switch ( currentClauseMap.get( select ) )
						{
							case where :
								buffer.append( buildString( " ", level + 1 )
										+ "--> "
										+ column1.getFullName( tableName )
										+ "(Where)\r\n" );
								break;
							case connectby :
								buffer.append( buildString( " ", level + 1 )
										+ "--> "
										+ column1.getFullName( tableName )
										+ "(Connect By)\r\n" );
								break;
							case startwith :
								buffer.append( buildString( " ", level + 1 )
										+ "--> "
										+ column1.getFullName( tableName )
										+ "(Start With)\r\n" );
								break;
							case orderby :
								buffer.append( buildString( " ", level + 1 )
										+ "--> "
										+ column1.getFullName( tableName )
										+ "(Order By)\r\n" );
								break;
							case join :
								buffer.append( buildString( " ", level + 1 )
										+ "--> "
										+ column1.getFullName( tableName )
										+ "(Join)\r\n" );
								break;
							default :
								break;
						}

					}
					findColumnInTables( column1, tableName, select, level + 2 );
				}

			}
		}
		currentClauseMap.remove( select );
	}

	private void findColumnsFromList( TCustomSqlStatement select, int level,
			TParseTreeNodeList list, ClauseType clauseType )
	{
		if ( list == null )
			return;

		for ( int i = 0; i < list.size( ); i++ )
		{
			Object element = list.getElement( i );
			TExpression lcexpr = null;
			if ( element instanceof TGroupByItem )
			{
				lcexpr = ( (TGroupByItem) element ).getExpr( );
			}
			else if ( element instanceof TOrderByItem )
			{
				lcexpr = ( (TOrderByItem) element ).getSortKey( );
			}
			else if ( element instanceof TExpression )
			{
				lcexpr = (TExpression) element;
			}

			if ( lcexpr != null )
			{
				List<TColumn> columns = exprToColumn( lcexpr,
						select,
						level,
						clauseType );
				for ( TColumn column1 : columns )
				{
					findColumnInTables( column1, select, level + 1, null, null );
					findColumnsFromClauses( select, level + 2 );
				}
			}
		}
	}

	public String getImpactResult( )
	{
		return buffer.toString( );
	}

	public String getErrorMessage( )
	{
		return errorBuffer.toString( );
	}

	public void impactSQL( )
	{
		int ret = 0;
		try
		{
			ret = sqlparser.parse( );
		}
		catch ( Exception e1 )
		{
			StringWriter sw = new StringWriter( );
			PrintWriter pw = new PrintWriter( sw );
			e1.printStackTrace( pw );
			String exceptionAsString = sw.toString( );
			pw.close( );
			errorBuffer.append( e1.getMessage( ) + "\r\n" )
					.append( exceptionAsString );
			return;
		}

		if ( ret != 0 )
		{
			errorBuffer.append( sqlparser.getErrormessage( ) + "\r\n" );
			isSucess = false;
		}
		else
		{
			Document doc = null;
			Element columnImpactResult = null;
			if ( fileNode != null )
			{
				doc = fileNode.getOwnerDocument( );
			}
			else
			{
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance( );
				try
				{
					DocumentBuilder builder = factory.newDocumentBuilder( );
					doc = builder.newDocument( );
					doc.setXmlVersion( "1.0" );
					columnImpactResult = doc.createElement( "columnImpactResult" );
					doc.appendChild( columnImpactResult );
				}
				catch ( ParserConfigurationException e )
				{
					e.printStackTrace( );
				}
			}
			for ( int k = 0; k < sqlparser.sqlstatements.size( ); k++ )
			{
				if ( sqlparser.sqlstatements.get( k ) instanceof TCustomSqlStatement )
				{
					dependMap.clear( );
					aliases.clear( );
					currentSource = null;
					currentClauseMap.clear( );
					accessMap.clear( );
					accessColumns.clear( );
					accessExpressions.clear( );

					TCustomSqlStatement select = sqlparser.sqlstatements.get( k );

					if ( select.toString( ).trim( ).startsWith( "USE" ) )
					{
						TGSqlParser parser = new TGSqlParser( vendor );
						parser.sqltext = select.toString( )
								.trim( )
								+ ";";
						int retValue = parser.parse( );
						if ( retValue == 0 )
						{
							database = new DDLParser( null,
									null,
									select.dbvendor,
									parser,
									true,
									null ).getDatabase( );
						}
						continue;
					}

					columnNumber = 0;
					impactSqlFromStatement( select );

					List<TTable> tableCollections = new ArrayList<TTable>( );
					for ( TAlias alias : aliases )
					{
						Element targetColumn = doc.createElement( "targetColumn" );
						targetColumn.setAttribute( "name",
								alias.getColumnDisplayName( ) );
						targetColumn.setAttribute( "coordinate",
								alias.fieldLocation.x
										+ ","
										+ alias.fieldLocation.y );
						if ( !alias.getAlias( ).equals( alias.getColumn( ) ) )
						{
							targetColumn.setAttribute( "alias",
									alias.getAliasDisplayName( ) );
							if ( showUIInfo )
							{
								targetColumn.setAttribute( "aliasHighlightInfo",
										"" + alias.aliasHighlightInfo );
							}
							targetColumn.setAttribute( "aliasCoordinate", ""
									+ alias.location.x
									+ ","
									+ alias.location.y );
						}
						if ( showUIInfo )
						{
							targetColumn.setAttribute( "columnHighlightInfo",
									"" + alias.columnHighlightInfo );
						}

						if ( columnImpactResult != null )
							columnImpactResult.appendChild( targetColumn );
						else if ( fileNode != null )
							fileNode.appendChild( targetColumn );

						Map<String, TSourceColumn> collections = new LinkedHashMap<String, TSourceColumn>( );

						if ( dependMap.containsKey( alias.getAlias( ) ) )
						{
							List<TResultEntry> results = dependMap.get( alias.getAlias( ) );
							for ( TResultEntry result : results )
							{
								if ( result.columnObject == null )
									continue;
								if ( result.columnObject.columnName == null )
									continue;

								if ( !result.isConstant
										&& result.targetTable.getFullName( ) == null )
									continue;

								String key = null;
								if ( result.isConstant )
								{
									key = SQLUtil.trimColumnStringQuote( ( Dlineage.TABLE_CONSTANT.toLowerCase( )
											+ "." + result.targetColumn ).toLowerCase( ) );
								}
								else if ( "*".equals( result.targetColumn ) )
								{
									if ( result.columnObject.linkColumnName != null )
									{
										key = SQLUtil.trimColumnStringQuote( result.targetTable.getFullName( )
												.toLowerCase( )
												+ "."
												+ result.columnObject.linkColumnName );
									}
									else
									{
										key = SQLUtil.trimColumnStringQuote( result.targetTable.getFullName( )
												.toLowerCase( ) );
									}
								}
								else
								{
									key = SQLUtil.trimColumnStringQuote( ( result.targetTable.getFullName( )
											.toLowerCase( )
											+ "." + result.targetColumn ).toLowerCase( ) );
								}

								TSourceColumn sourceColumn = null;
								if ( collections.containsKey( key ) )
								{
									sourceColumn = collections.get( key );
									if ( !sourceColumn.clauses.contains( result.clause ) )
									{
										sourceColumn.clauses.add( result.clause );
									}

									if ( result.location != null )
									{
										if ( !sourceColumn.locations.containsKey( result.clause ) )
											sourceColumn.locations.put( result.clause,
													new ArrayList<Point>( ) );
										List<Point> ys = sourceColumn.locations.get( result.clause );
										if ( !ys.contains( result.location ) )
											ys.add( result.location );
									}

									if ( result.columnObject != null
											&& showUIInfo )
									{
										sourceColumn.highlightInfos.add( new Point( (int) result.columnObject.offset,
												(int) result.columnObject.length ) );
									}

									if ( result.columnObject != null
											&& !sourceColumn.isNotOrphan )
									{
										sourceColumn.isNotOrphan = !result.columnObject.isOrphan;
									}

								}
								else
								{
									sourceColumn = new TSourceColumn( );
									collections.put( key, sourceColumn );
									if ( result.isConstant )
									{
										sourceColumn.tableOwner = null;
									}
									else
									{
										sourceColumn.tableOwner = result.targetTable.getTableName( )
												.getDatabaseString( );
									}
									if ( !isNotEmpty( sourceColumn.tableOwner ) )
										sourceColumn.tableOwner = database;
									sourceColumn.tableName = getTableNameFromMetadata( result );
									if ( !tableCollections.contains( result.targetTable ) )
									{
										tableCollections.add( result.targetTable );
									}

									sourceColumn.name = getColumnNameFromMetadata( result );

									if ( !sourceColumn.clauses.contains( result.clause ) )
									{
										sourceColumn.clauses.add( result.clause );
									}
									if ( result.location != null )
									{
										if ( !sourceColumn.locations.containsKey( result.clause ) )
											sourceColumn.locations.put( result.clause,
													new ArrayList<Point>( ) );
										List<Point> ys = sourceColumn.locations.get( result.clause );
										if ( !ys.contains( result.location ) )
											ys.add( result.location );
									}

									if ( result.columnObject != null
											&& showUIInfo )
									{
										sourceColumn.highlightInfos.add( new Point( (int) result.columnObject.offset,
												(int) result.columnObject.length ) );
									}

									if ( result.columnObject != null
											&& !sourceColumn.isNotOrphan )
									{
										sourceColumn.isNotOrphan = !result.columnObject.isOrphan;
									}
								}
							}

							Iterator<String> iter = collections.keySet( )
									.iterator( );

							while ( iter.hasNext( ) )
							{
								TSourceColumn sourceColumn = collections.get( iter.next( ) );
								if ( sourceColumn.clauses.size( ) > 0 )
								{
									for ( int j = 0; j < sourceColumn.clauses.size( ); j++ )
									{
										ClauseType clause = sourceColumn.clauses.get( j );
										if ( clause == ClauseType.createview
												|| clause == ClauseType.createtable
												|| clause == ClauseType.insert
												|| clause == ClauseType.updateset
												|| clause == ClauseType.mergeset
												|| clause == ClauseType.topselect )
										{
											Element element = doc.createElement( "linkTable" );
											if ( clause == ClauseType.topselect
													|| clause == ClauseType.createtable )
											{
												element.setAttribute( "type",
														"select" );
											}
											else if ( clause == ClauseType.createview )
											{
												element.setAttribute( "type",
														"view" );
											}
											else if ( clause == ClauseType.insert )
											{
												element.setAttribute( "type",
														"insert" );
											}
											else if ( clause == ClauseType.updateset )
											{
												element.setAttribute( "type",
														"update" );
											}
											else if ( clause == ClauseType.mergeset )
											{
												element.setAttribute( "type",
														"merge" );
											}

											if ( sourceColumn.tableOwner != null )
											{
												element.setAttribute( "tableOwner",
														sourceColumn.tableOwner );
											}
											if ( sourceColumn.tableName != null )
											{
												element.setAttribute( "tableName",
														sourceColumn.tableName );
											}
											if ( sourceColumn.name != null )
											{
												element.setAttribute( "name",
														sourceColumn.name );
											}
											{
												StringBuilder buffer = new StringBuilder( );
												buildLocationString( sourceColumn,
														clause,
														buffer );
												if ( buffer.toString( )
														.length( ) != 0 )
													element.setAttribute( "coordinate",
															buffer.toString( ) );
											}
											if ( showUIInfo )
											{
												StringBuilder buffer = new StringBuilder( );
												buildHighligthString( sourceColumn,
														buffer );
												if ( buffer.toString( )
														.length( ) != 0 )
													element.setAttribute( "highlightInfos",
															buffer.toString( ) );
											}

											if ( !sourceColumn.isNotOrphan )
											{
												element.setAttribute( "orphan",
														"true" );
											}
											if ( !( select instanceof TSelectSqlStatement ) )
											{

												if ( sourceColumn.tableOwner != null )
												{
													element.setAttribute( "tableOwner",
															sourceColumn.tableOwner );
												}
												if ( sourceColumn.tableName != null )
												{
													element.setAttribute( "tableName",
															sourceColumn.tableName );
												}
												if ( sourceColumn.name != null )
												{
													element.setAttribute( "name",
															sourceColumn.name );
												}
												String aliasName = null;
												if ( sourceColumn.name != null )
												{
													aliasName = sourceColumn.name;
												}
												if ( sourceColumn.tableName != null )
												{
													aliasName = sourceColumn.tableName
															+ "."
															+ aliasName;
												}
												if ( sourceColumn.tableOwner != null )
												{
													aliasName = sourceColumn.tableOwner
															+ "."
															+ aliasName;
												}
												targetColumn.setAttribute( "name",
														aliasName );
											}
											targetColumn.appendChild( element );
										}
										else
										{
											Element element = doc.createElement( "sourceColumn" );
											if ( sourceColumn.tableOwner != null )
											{
												element.setAttribute( "tableOwner",
														sourceColumn.tableOwner );
											}
											if ( sourceColumn.tableName != null )
											{
												element.setAttribute( "tableName",
														sourceColumn.tableName );
											}
											if ( sourceColumn.name != null )
											{
												element.setAttribute( "name",
														sourceColumn.name );
											}
											{
												StringBuilder buffer = new StringBuilder( );
												switch ( clause )
												{
													case where :
														buffer.append( "where" );
														break;
													case connectby :
														buffer.append( "connect by" );
														break;
													case startwith :
														buffer.append( "start with" );
														break;
													case orderby :
														buffer.append( "order by" );
														break;
													case join :
														buffer.append( "join" );
														break;
													case select :
														buffer.append( "select" );
														break;
													case update :
														buffer.append( "update" );
														break;
													case assign :
														buffer.append( "assign" );
														break;
													case merge :
														buffer.append( "merge" );
														break;
													case mergematch :
														buffer.append( "merge match" );
														break;
													case mergenotmatch :
														buffer.append( "merge not match" );
														break;
													case groupby :
														buffer.append( "group by" );
														break;
													case casethen :
														buffer.append( "case then" );
														break;
													case casewhen :
														buffer.append( "case when" );
														break;
													default :
														break;
												}
												if ( buffer.toString( )
														.length( ) != 0 )
													element.setAttribute( "clause",
															buffer.toString( ) );
											}
											{
												StringBuilder buffer = new StringBuilder( );
												buildLocationString( sourceColumn,
														clause,
														buffer );
												if ( buffer.toString( )
														.length( ) != 0 )
													element.setAttribute( "coordinate",
															buffer.toString( ) );
											}
											if ( showUIInfo )
											{
												StringBuilder buffer = new StringBuilder( );
												buildHighligthString( sourceColumn,
														buffer );
												if ( buffer.toString( )
														.length( ) != 0 )
													element.setAttribute( "highlightInfos",
															buffer.toString( ) );
											}
											if ( !sourceColumn.isNotOrphan )
											{
												element.setAttribute( "orphan",
														"true" );
											}
											targetColumn.appendChild( element );
										}
									}
								}
							}
						}
					}
					if ( showUIInfo )
					{
						for ( int i = 0; i < tableCollections.size( ); i++ )
						{
							TTable table = tableCollections.get( i );
							Element element = doc.createElement( "table" );
							if ( table != null )
							{
								element.setAttribute( "owner",
										table.getTableName( ).getSchemaString( ) );
								element.setAttribute( "name", table.getName( ) );
								element.setAttribute( "highlightInfo",
										table.getStartToken( ).offset
												+ ","
												+ ( table.getTableName( ) != null ? ( table.getTableName( )
														.getEndToken( ).offset
														- table.getStartToken( ).offset + table.getTableName( )
														.getEndToken( ).astext.length( ) )
														: ( table.getEndToken( ).offset
																- table.getStartToken( ).offset + table.getEndToken( ).astext.length( ) ) ) );
								element.setAttribute( "coordinate",
										table.getStartToken( ).lineNo
												+ ","
												+ table.getStartToken( ).columnNo );
							}
							else
							{
								element.setAttribute( "name",
										Dlineage.TABLE_CONSTANT );
							}
							if ( columnImpactResult != null )
								columnImpactResult.appendChild( element );
							else if ( fileNode != null )
								fileNode.appendChild( element );
						}
					}
				}
			}
			if ( columnImpactResult != null )
			{
				try
				{
					buffer.append( XMLUtil.format( doc, 2 ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( );
				}
			}
		}

		if ( traceErrorMessage && getErrorMessage( ).trim( ).length( ) > 0 )
		{
			System.err.print( getErrorMessage( ) );
		}
	}

	private String getColumnNameFromMetadata( TResultEntry result )
	{

		String columnName = result.columnObject.linkColumnName != null ? result.columnObject.linkColumnName
				: result.targetColumn;
		TableMetaData tableMetaData = null;
		if ( result.isConstant )
		{
			tableMetaData = getConstantTableMetaData( );
		}
		else
		{
			TTable table = result.targetTable;
			tableMetaData = getTableMetaData( table.getTableName( ) );
		}
		ColumnMetaData columnMetaData = getColumnMetaData( tableMetaData,
				columnName );

		if ( columnMetaData != null )
		{
			if ( result.columnObject != null )
			{
				columnMetaData.setOrphan( Boolean.toString( result.columnObject.isOrphan ) );
			}

			return columnMetaData.getDisplayName( );
		}
		return columnName;
	}

	private String getTableNameFromMetadata( TResultEntry result )
	{
		if ( result.isConstant )
		{
			return getConstantTableMetaData( ).getDisplayName( );
		}
		else
		{
			TTable table = result.targetTable;
			TableMetaData tableMetaData = getTableMetaData( table.getTableName( ) );
			if ( tableMetaData != null )
			{
				if ( tableMetaData.getSchemaDisplayName( ) != null )
					return tableMetaData.getSchemaDisplayName( )
							+ "."
							+ tableMetaData.getDisplayName( );
				else
					return tableMetaData.getDisplayName( );
			}
			if ( isNotEmpty( table.getTableName( ).getDatabaseString( ) ) )
			{
				return table.getFullName( ).replace( table.getTableName( )
						.getDatabaseString( ) + ".",
						"" );
			}
			else
				return table.getFullName( );
		}
	}

	private TableMetaData getTableMetaData( TObjectName tableObjectName )
	{
		String tableName = tableObjectName.getTableString( );
		String tableSchema = tableObjectName.getSchemaString( );
		TableMetaData tableMetaData = new TableMetaData( vendor, strict );
		tableMetaData.setName( tableName );
		tableMetaData.setSchemaName( tableSchema );
		if ( isNotEmpty( tableObjectName.getDatabaseString( ) ) )
		{
			tableMetaData.setCatalogName( tableObjectName.getDatabaseString( ) );
		}
		else
			tableMetaData.setCatalogName( database );
		tableMetaData = getTableMetaData( tableMetaData );
		return tableMetaData;
	}

	private TableMetaData getConstantTableMetaData( )
	{
		TableMetaData tableMetaData = new TableMetaData( vendor, strict );
		tableMetaData.setName( Dlineage.TABLE_CONSTANT );
		tableMetaData = getTableMetaData( tableMetaData );
		return tableMetaData;
	}

	private ColumnMetaData getColumnMetaData( TableMetaData tableMetaData,
			String columnName )
	{
		ColumnMetaData columnMetaData = new ColumnMetaData( );
		columnMetaData.setName( columnName );
		columnMetaData.setTable( tableMetaData );

		if ( tableColumns.get( tableMetaData ) == null )
			return null;
		int index = tableColumns.get( tableMetaData ).indexOf( columnMetaData );
		if ( index != -1 )
		{
			columnMetaData = tableColumns.get( tableMetaData ).get( index );
		}
		else
		{
			tableColumns.get( tableMetaData ).add( columnMetaData );
		}
		return columnMetaData;
	}

	private TableMetaData getTableMetaData( TableMetaData tableMetaData )
	{
		List<TableMetaData> tables = Arrays.asList( tableColumns.keySet( )
				.toArray( new TableMetaData[0] ) );
		int index = tables.indexOf( tableMetaData );
		if ( index != -1 )
		{
			return tables.get( index );
		}
		else
		{
			tableColumns.put( tableMetaData, new ArrayList<ColumnMetaData>( ) );
			return tableMetaData;
		}
	}

	private void buildLocationString( TSourceColumn sourceColumn,
			ClauseType clauseType, StringBuilder locationBuffer )
	{
		List<Point> ys = sourceColumn.locations.get( clauseType );
		if ( ys != null )
		{
			for ( int z = 0; z < ys.size( ); z++ )
			{
				locationBuffer.append( ys.get( z ).x + "," + ys.get( z ).y );
				if ( z < ys.size( ) - 1 )
					locationBuffer.append( ";" );
			}
		}
	}

	private void buildHighligthString( TSourceColumn sourceColumn,
			StringBuilder highlightBuffer )
	{
		Set<Point> infos = sourceColumn.highlightInfos;
		if ( infos != null )
		{
			Iterator<Point> iter = infos.iterator( );
			while ( iter.hasNext( ) )
			{
				Point point = iter.next( );
				highlightBuffer.append( point.x + "," + point.y ).append( ";" );
			}
		}
	}

	private void impactSqlFromStatement( TCustomSqlStatement select )
	{
		if ( select instanceof TSelectSqlStatement )
		{
			TSelectSqlStatement stmt = (TSelectSqlStatement) select;
			if ( stmt.getSetOperatorType( ) != ESetOperatorType.none )
			{
				impactSqlFromStatement( stmt.getLeftStmt( ) );
				impactSqlFromStatement( stmt.getRightStmt( ) );
			}
			else
			{
				for ( int i = 0; i < select.getResultColumnList( ).size( ); i++ )
				{
					linkFieldToTables( null, select.getResultColumnList( )
							.getResultColumn( i ), select, 0, ClauseType.select );
				}
			}
		}
		else if ( select instanceof TInsertSqlStatement
				&& ( (TInsertSqlStatement) select ).getSubQuery( ) != null )
		{
			impactSqlFromStatement( ( (TInsertSqlStatement) select ).getSubQuery( ) );
		}
		else if ( select instanceof TCreateViewSqlStatement )
		{
			impactSqlFromStatement( ( (TCreateViewSqlStatement) select ).getSubquery( ) );
		}
		else if ( select instanceof TCreateTableSqlStatement
				&& ( (TCreateTableSqlStatement) select ).getSubQuery( ) != null )
		{
			impactSqlFromStatement( ( (TCreateTableSqlStatement) select ).getSubQuery( ) );
		}
		else if ( select instanceof TMergeSqlStatement )
		{
			TMergeSqlStatement merge = (TMergeSqlStatement) select;
			for ( int i = 0; i < merge.getWhenClauses( ).size( ); i++ )
			{
				TMergeWhenClause whenClause = merge.getWhenClauses( )
						.getElement( i );
				if ( whenClause.getUpdateClause( ) != null )
				{
					for ( int j = 0; j < whenClause.getUpdateClause( )
							.getUpdateColumnList( )
							.size( ); j++ )
					{
						linkFieldToTables( null,
								whenClause.getUpdateClause( )
										.getUpdateColumnList( )
										.getResultColumn( j ),
								select,
								0,
								ClauseType.merge );
					}
				}
				else if ( whenClause.getInsertClause( ) != null )
				{
					if ( whenClause.getInsertClause( ).getValuelist( ) == null )
						continue;

					for ( int j = 0; j < whenClause.getInsertClause( )
							.getValuelist( )
							.size( ); j++ )
					{
						linkFieldToTables( null,
								whenClause.getInsertClause( )
										.getColumnList( )
										.getObjectName( j ),
								whenClause.getInsertClause( )
										.getValuelist( )
										.getResultColumn( j ),
								select,
								ClauseType.merge );
					}
				}
				else if ( whenClause.getDeleteClause( ) != null )
				{

				}
			}
		}
		else if ( select.getResultColumnList( ) != null )
		{
			for ( int i = 0; i < select.getResultColumnList( ).size( ); i++ )
			{
				linkFieldToTables( null, select.getResultColumnList( )
						.getResultColumn( i ), select, 0, ClauseType.undefine );
			}
		}
		else if ( select.getStatements( ) != null )
		{
			for ( int i = 0; i < select.getStatements( ).size( ); i++ )
			{
				impactSqlFromStatement( select.getStatements( ).get( i ) );
			}
		}
	}

	private boolean isPseudocolumn( String column )
	{
		if ( column == null )
			return false;
		if ( "rownum".equalsIgnoreCase( column.trim( ) ) )
			return true;
		else if ( "rowid".equalsIgnoreCase( column.trim( ) ) )
			return true;
		else if ( "nextval".equalsIgnoreCase( column.trim( ) ) )
			return true;
		else if ( "sysdate".equalsIgnoreCase( column.trim( ) ) )
			return true;
		return false;
	}

	private boolean linkFieldToTables( TAlias parentAlias, TExpression field,
			TCustomSqlStatement select, int level, ClauseType clauseType )
	{
		if ( select instanceof TSelectSqlStatement
				&& ( (TSelectSqlStatement) select ).getSetOperatorType( ) != ESetOperatorType.none )
		{
			TSelectSqlStatement stmt = (TSelectSqlStatement) select;
			boolean leftResult = false;
			boolean rightResult = false;
			if ( stmt.getLeftStmt( ) != null )
			{
				leftResult = linkFieldToTables( parentAlias,
						field,
						stmt.getLeftStmt( ),
						level,
						clauseType );
			}
			if ( stmt.getRightStmt( ) != null )
			{
				leftResult = linkFieldToTables( parentAlias,
						field,
						stmt.getRightStmt( ),
						level,
						clauseType );
			}
			return leftResult || rightResult;
		}

		if ( level == 0 )
			accessMap.clear( );
		boolean ret = false;
		// all items in select list was represented by a TLzField Objects
		switch ( field.getExpressionType( ) )
		{
			case simple_object_name_t :
			{
				TColumn column = attrToColumn( field,
						select,
						clauseType,
						parentAlias );

				if ( column == null )
				{
					return false;
				}

				boolean isPseudocolumn = select.dbvendor == EDbVendor.dbvoracle
						&& this.isPseudocolumn( column.columnName );
				if ( level == 0 || parentAlias != null )
				{
					TAlias alias = null;
					if ( parentAlias != null )
					{
						alias = parentAlias;
					}
					else
					{
						alias = new TAlias( );
						alias.setColumn( field.toString( ) );
						alias.setAlias( field.toString( ) );
						alias.location = new Point( (int) field.getStartToken( ).lineNo,
								(int) field.getStartToken( ).columnNo );
						alias.fieldLocation = alias.location;

						alias.columnHighlightInfo = field.getStartToken( ).offset
								+ ","
								+ ( field.getEndToken( ).offset
										- field.getStartToken( ).offset + field.getEndToken( ).astext.length( ) );

						aliases.add( alias );
					}
					currentSource = alias.getAlias( );
					if ( !dependMap.containsKey( currentSource ) )
						dependMap.put( currentSource,
								new ArrayList<TResultEntry>( ) );

					if ( debug && parentAlias == null )
					{
						if ( !alias.getAlias( )
								.equalsIgnoreCase( column.getOrigName( ) ) )
						{
							buffer.append( "\r\nSearch "
									+ alias.getAliasDisplayName( )
									+ ( level == 0 ? ( " <<column_"
											+ ( ++columnNumber ) + ">>" ) : "" )
									+ "\r\n" );
							buffer.append( "--> "
									+ column.getOrigName( )
									+ ( !isPseudocolumn
											&& column.tableNames.size( ) > 1 ? ( " <<GUESS>>" )
											: "" )
									+ "\r\n" );
						}
						else
						{
							buffer.append( "\r\nSearch "
									+ column.getOrigName( )
									+ ( level == 0 ? ( " <<column_"
											+ ( ++columnNumber ) + ">>" ) : "" )
									+ ( !isPseudocolumn
											&& column.tableNames.size( ) > 1 ? ( " <<GUESS>>" )
											: "" )
									+ "\r\n" );
							level -= 1;
						}
					}

				}
				if ( isPseudocolumn )
				{
					break;
				}
				ret = findColumnInTables( column, select, level + 1, null, null );
				findColumnsFromClauses( select, level + 2 );
			}
			default :
				break;
		}
		return ret;
	}

	private boolean linkFieldToTables( TAlias parentAlias,
			TObjectName objectName, TResultColumn field,
			TCustomSqlStatement select, ClauseType clauseType )
	{
		if ( select instanceof TSelectSqlStatement
				&& ( (TSelectSqlStatement) select ).getSetOperatorType( ) != ESetOperatorType.none )
		{
			TSelectSqlStatement stmt = (TSelectSqlStatement) select;
			boolean leftResult = false;
			boolean rightResult = false;
			if ( stmt.getLeftStmt( ) != null )
			{
				leftResult = linkFieldToTables( parentAlias,
						objectName,
						field,
						stmt.getLeftStmt( ),
						clauseType );
			}
			if ( stmt.getRightStmt( ) != null )
			{
				rightResult = linkFieldToTables( parentAlias,
						objectName,
						field,
						stmt.getRightStmt( ),
						clauseType );
			}
			return leftResult || rightResult;
		}
		TAlias alias = parentAlias;
		if ( parentAlias == null )
		{
			alias = new TAlias( );
			alias.setColumn( objectName.toString( ) );
			alias.setAlias( objectName.toString( ) );
			alias.location = new Point( (int) objectName.getStartToken( ).lineNo,
					(int) objectName.getStartToken( ).columnNo );
			alias.fieldLocation = alias.location;
			alias.columnHighlightInfo = objectName.getStartToken( ).offset
					+ ","
					+ ( objectName.getEndToken( ).offset
							- objectName.getStartToken( ).offset + objectName.getEndToken( ).astext.length( ) );
			aliases.add( alias );
		}

		currentSource = alias.getAlias( );
		if ( !dependMap.containsKey( currentSource ) )
			dependMap.put( currentSource, new ArrayList<TResultEntry>( ) );

		TColumn column = attrToColumn( alias, objectName, select, clauseType );

		boolean ret = false;

		if ( column != null )
		{
			ret = findColumnInTables( column, select, 1, null, null );
		}

		findColumnsFromClauses( select, 2 );

		clauseType = ClauseType.assign;
		linkFieldToTables( alias, field, select, 1, clauseType );

		if ( select instanceof TMergeSqlStatement )
		{
			TMergeSqlStatement merge = (TMergeSqlStatement) select;
			TTable table = new TTable( );
			table.setTableName( merge.getTargetTable( ).getTableName( ) );
			TColumn linkColumn = new TColumn( );
			linkColumn.linkColumnName = objectName.getColumnNameOnly( )
					.toString( );
			linkColumn.columnName = SQLUtil.trimColumnStringQuote( currentSource );

			linkColumn.location = new Point( (int) objectName.getStartToken( ).lineNo,
					(int) objectName.getStartToken( ).columnNo );

			linkColumn.offset = objectName.getStartToken( ).offset;
			linkColumn.length = objectName.toString( ).length( );
			linkColumn.clauseType = ClauseType.mergeset;
			TResultEntry resultEntry = new TResultEntry( table,
					linkColumn,
					linkColumn.columnName,
					ClauseType.mergeset,
					linkColumn.location );
			dependMap.get( currentSource ).add( resultEntry );
		}

		return ret;
	}

	private boolean linkFieldToTables( TAlias parentAlias, TResultColumn field,
			TCustomSqlStatement select, int level, ClauseType clauseType )
	{
		if ( select instanceof TSelectSqlStatement
				&& ( (TSelectSqlStatement) select ).getSetOperatorType( ) != ESetOperatorType.none )
		{
			TSelectSqlStatement stmt = (TSelectSqlStatement) select;
			boolean leftResult = false;
			boolean rightResult = false;
			if ( stmt.getLeftStmt( ) != null )
			{
				leftResult = linkFieldToTables( parentAlias,
						field,
						stmt.getLeftStmt( ),
						level,
						clauseType );
			}
			if ( stmt.getRightStmt( ) != null )
			{
				leftResult = linkFieldToTables( parentAlias,
						field,
						stmt.getRightStmt( ),
						level,
						clauseType );
			}
			return leftResult || rightResult;
		}

		if ( !accessColumns.containsKey( field ) )
		{
			List<String> aliases = new ArrayList<String>( );
			if ( parentAlias != null )
			{
				aliases.add( parentAlias.alias );
			}
			else
			{
				aliases.add( null );
			}
			accessColumns.put( field, aliases );
		}
		else
		{
			List<String> aliases = accessColumns.get( field );
			if ( parentAlias != null )
			{
				if ( aliases.contains( parentAlias.alias ) )
				{
					return true;
				}
				else
				{
					aliases.add( parentAlias.alias );
				}
			}
			else
			{
				aliases.add( null );
			}
		}

		if ( level == 0 )
		{
			accessMap.clear( );
		}
		boolean ret = false;
		TColumn tempColumn = null;
		// all items in select list was represented by a TLzField Objects
		switch ( field.getExpr( ).getExpressionType( ) )
		{
			case simple_constant_t :
			{
				if ( !isKeyword( field.getExpr( ).toString( ) ) )
				{
					tempColumn = new TColumn( );
					tempColumn.alias = field.getColumnAlias( );
					tempColumn.columnName = field.getExpr( ).toString( );
					tempColumn.location = new Point( (int) field.getExpr( )
							.getEndToken( ).lineNo, (int) field.getExpr( )
							.getEndToken( ).columnNo );
					tempColumn.offset = field.getExpr( ).getEndToken( ).offset;
					tempColumn.length = field.getExpr( ).getEndToken( ).astext.length( );
					tempColumn.tableNames.add( Dlineage.TABLE_CONSTANT );
					tempColumn.tableFullNames.add( Dlineage.TABLE_CONSTANT
							+ "."
							+ tempColumn.columnName );
					tempColumn.clauseType = clauseType;
				}
				else
				{
					return true;
				}
			}
			case simple_object_name_t :
			{
				TColumn column = null;
				if ( tempColumn == null )
				{
					column = attrToColumn( field.getExpr( ),
							select,
							clauseType,
							parentAlias );
					findColumnsFromClauses( select, 2 );
				}
				else
				{
					column = tempColumn;
				}

				if ( column == null )
				{
					break;
				}
				boolean isPseudocolumn = select.dbvendor == EDbVendor.dbvoracle
						&& this.isPseudocolumn( column.columnName );
				if ( level == 0 || parentAlias != null )
				{
					TAlias alias = null;
					if ( parentAlias != null )
					{
						alias = parentAlias;
					}
					else
					{
						alias = new TAlias( );
						alias.setColumn( field.toString( ) );
						alias.setAlias( field.toString( ) );
						alias.location = new Point( (int) field.getStartToken( ).lineNo,
								(int) field.getStartToken( ).columnNo );
						alias.fieldLocation = alias.location;

						alias.columnHighlightInfo = field.getStartToken( ).offset
								+ ","
								+ ( field.getEndToken( ).offset
										- field.getStartToken( ).offset + field.getEndToken( ).astext.length( ) );

						if ( field.getAliasClause( ) != null )
						{
							alias.setAlias( field.getAliasClause( ).toString( ) );
							alias.setColumn( field.toString( ) );
							TSourceToken startToken = field.getAliasClause( )
									.getAliasName( )
									.getStartToken( );
							alias.location = new Point( (int) startToken.lineNo,
									(int) startToken.columnNo );
							alias.aliasHighlightInfo = field.getAliasClause( )
									.getStartToken( ).offset
									+ ","
									+ ( field.getAliasClause( ).getEndToken( ).offset
											- field.getAliasClause( )
													.getStartToken( ).offset + field.getAliasClause( )
											.getEndToken( ).astext.length( ) );

							alias.columnHighlightInfo = field.getStartToken( ).offset
									+ ","
									+ ( field.getExpr( ).getEndToken( ).offset
											- field.getStartToken( ).offset + field.getExpr( )
											.getEndToken( ).astext.length( ) );
						}
						aliases.add( alias );
					}
					currentSource = alias.getAlias( );
					if ( !dependMap.containsKey( currentSource ) )
						dependMap.put( currentSource,
								new ArrayList<TResultEntry>( ) );

					if ( debug && parentAlias == null )
					{
						if ( !alias.getAlias( )
								.equalsIgnoreCase( column.getOrigName( ) ) )
						{
							buffer.append( "\r\nSearch "
									+ alias.getAliasDisplayName( )
									+ ( level == 0 ? ( " <<column_"
											+ ( ++columnNumber ) + ">>" ) : "" )
									+ "\r\n" );
							buffer.append( "--> "
									+ column.getOrigName( )
									+ ( !isPseudocolumn
											&& column.tableNames.size( ) > 1 ? ( " <<GUESS>>" )
											: "" )
									+ "\r\n" );
						}
						else
						{
							buffer.append( "\r\nSearch "
									+ column.getOrigName( )
									+ ( level == 0 ? ( " <<column_"
											+ ( ++columnNumber ) + ">>" ) : "" )
									+ ( !isPseudocolumn
											&& column.tableNames.size( ) > 1 ? ( " <<GUESS>>" )
											: "" )
									+ "\r\n" );
							level -= 1;
						}
					}

				}
				if ( isPseudocolumn )
				{
					break;
				}
				ret = findColumnInTables( column, select, level + 1, null, null );
				findColumnsFromClauses( select, level + 2 );

				break;
			}
			case subquery_t :
				TAlias alias1 = new TAlias( );
				alias1.setColumn( field.toString( ) );
				alias1.setAlias( field.toString( ) );
				alias1.location = new Point( (int) field.getStartToken( ).lineNo,
						(int) field.getStartToken( ).columnNo );
				alias1.fieldLocation = alias1.location;
				alias1.columnHighlightInfo = field.getStartToken( ).offset
						+ ","
						+ ( field.getEndToken( ).offset
								- field.getStartToken( ).offset + field.getEndToken( ).astext.length( ) );

				if ( field.getAliasClause( ) != null )
				{
					alias1.setAlias( field.getAliasClause( ).toString( ) );
					TSourceToken startToken = field.getAliasClause( )
							.getAliasName( )
							.getStartToken( );
					alias1.setColumn( field.toString( ) );
					alias1.location = new Point( (int) startToken.lineNo,
							(int) startToken.columnNo );

					alias1.aliasHighlightInfo = field.getAliasClause( )
							.getStartToken( ).offset
							+ ","
							+ ( field.getAliasClause( ).getEndToken( ).offset
									- field.getAliasClause( ).getStartToken( ).offset + field.getAliasClause( )
									.getEndToken( ).astext.length( ) );

					alias1.columnHighlightInfo = field.getStartToken( ).offset
							+ ","
							+ ( field.getExpr( ).getEndToken( ).offset
									- field.getStartToken( ).offset + field.getExpr( )
									.getEndToken( ).astext.length( ) );
				}

				if ( level == 0 )
				{
					aliases.add( alias1 );
					if ( debug )
					{
						buffer.append( "\r\nSearch "
								+ alias1.getAliasDisplayName( )
								+ ( level == 0 ? ( " <<column_"
										+ ( ++columnNumber ) + ">>" ) : "" )
								+ "\r\n" );
						// buffer.append( "--> "
						// + field.getExpr( ).getSubQuery( )
						// + "\r\n" );
					}
				}
				TSelectSqlStatement stmt = field.getExpr( ).getSubQuery( );
				List<TSelectSqlStatement> stmtList = new ArrayList<TSelectSqlStatement>( );
				getSelectSqlStatements( stmt, stmtList );
				for ( int i = 0; i < stmtList.size( ); i++ )
				{
					linkFieldToTables( alias1,
							stmtList.get( i )
									.getResultColumnList( )
									.getResultColumn( 0 ),
							stmtList.get( i ),
							level - 1 < 0 ? 0 : level - 1,
							clauseType );
				}
				break;
			default :
				TAlias alias = parentAlias;

				if ( level == 0 )
				{

					if ( select instanceof TMergeSqlStatement )
					{
						TExpression expression = field.getExpr( )
								.getLeftOperand( );
						if ( expression != null )
							linkFieldToTables( null,
									expression,
									select,
									level,
									ClauseType.merge );
					}
					else if ( select instanceof TUpdateSqlStatement )
					{
						TExpression expression = field.getExpr( )
								.getLeftOperand( );
						if ( expression.getExpressionType( ) == EExpressionType.list_t )
						{
							TExpression setExpression = field.getExpr( )
									.getRightOperand( );
							for ( int i = 0; i < expression.getExprList( )
									.size( ); i++ )
							{
								linkFieldToTables( null,
										expression.getExprList( )
												.getExpression( i ),
										select,
										level,
										ClauseType.update );
								if ( setExpression != null
										&& setExpression.getSubQuery( ) != null )
								{
									TSelectSqlStatement query = setExpression.getSubQuery( );
									if ( query.getResultColumnList( ).size( ) > i )
									{
										linkFieldToTables( null,
												query.getResultColumnList( )
														.getResultColumn( i ),
												query,
												level + 1,
												clauseType );
									}
								}
							}
							break;
						}
						else
						{
							linkFieldToTables( null,
									getColumnExpression( expression ),
									select,
									level,
									ClauseType.update );
						}

					}
					else if ( alias == null )
					{
						alias = new TAlias( );
						alias.setColumn( field.toString( ) );
						alias.setAlias( alias.getColumnDisplayName( ) );
						alias.location = new Point( (int) field.getStartToken( ).lineNo,
								(int) field.getStartToken( ).columnNo );
						alias.fieldLocation = alias.location;
						alias.columnHighlightInfo = field.getStartToken( ).offset
								+ ","
								+ ( field.getEndToken( ).offset
										- field.getStartToken( ).offset + field.getEndToken( ).astext.length( ) );

					}

					if ( alias != null && parentAlias == null )
					{
						if ( field.getAliasClause( ) != null )
						{
							alias.setAlias( field.getAliasClause( ).toString( ) );
							alias.setColumn( field.toString( ) );
							TSourceToken startToken = field.getAliasClause( )
									.getAliasName( )
									.getStartToken( );
							alias.location = new Point( (int) startToken.lineNo,
									(int) startToken.columnNo );

							alias.aliasHighlightInfo = field.getAliasClause( )
									.getStartToken( ).offset
									+ ","
									+ ( field.getAliasClause( ).getEndToken( ).offset
											- field.getAliasClause( )
													.getStartToken( ).offset + field.getAliasClause( )
											.getEndToken( ).astext.length( ) );

							alias.columnHighlightInfo = field.getStartToken( ).offset
									+ ","
									+ ( field.getExpr( ).getEndToken( ).offset
											- field.getStartToken( ).offset + field.getExpr( )
											.getEndToken( ).astext.length( ) );
						}
						aliases.add( alias );

						if ( debug )
						{
							buffer.append( "\r\n"
									+ "Search "
									+ alias.getAliasDisplayName( )
									+ ( level == 0 ? ( " <<column_"
											+ ( ++columnNumber ) + ">>" ) : "" )
									+ "\r\n" );
						}

						currentSource = alias.getAlias( );
						if ( !dependMap.containsKey( currentSource ) )
							dependMap.put( currentSource,
									new ArrayList<TResultEntry>( ) );
					}
				}

				if ( select instanceof TUpdateSqlStatement )
				{
					clauseType = ClauseType.assign;
				}
				else if ( select instanceof TMergeSqlStatement )
				{
					clauseType = ClauseType.assign;
				}

				List<TColumn> columns = exprToColumn( field.getExpr( ),
						select,
						level,
						true,
						clauseType,
						alias );

				if ( select instanceof TUpdateSqlStatement )
				{
					if ( columns.size( ) > 0 )
					{
						columns.remove( 0 );
					}
				}
				else if ( select instanceof TMergeSqlStatement )
				{
					if ( columns.size( ) > 0 )
					{
						columns.remove( 0 );
					}
				}
				if ( debug )
				{
					for ( TColumn column1 : columns )
					{
						if ( column1 == null )
							continue;
						if ( level == 0 )
						{
							buffer.append( buildString( " ", level )
									+ "--> "
									+ column1.getOrigName( )
									+ "\r\n" );
						}
					}
				}

				for ( TColumn column1 : columns )
				{
					if ( column1 == null )
						continue;
					if ( level == 0 )
					{
						if ( debug )
						{
							buffer.append( "\r\n"
									+ "Search "
									+ column1.getOrigName( )
									+ "\r\n" );
						}
					}

					findColumnInTables( column1, select, level + 1, null, null );
					findColumnsFromClauses( select, level + 2 );
				}

				if ( field.getExpr( ).getExpressionType( ) == EExpressionType.function_t )
				{
					TFunctionCall func = field.getExpr( ).getFunctionCall( );
					// buffer.AppendLine("function name {0}",
					// func.funcname.AsText);
					if ( func.getFunctionName( )
							.toString( )
							.equalsIgnoreCase( "count" )
							|| func.getFunctionName( )
									.toString( )
									.equalsIgnoreCase( "sum" )
							|| func.getFunctionName( )
									.toString( )
									.equalsIgnoreCase( "row_number" ) )
					{
						if ( debug )
						{
							buffer.append( buildString( " ", level + 1 )
									+ "--> aggregate function "
									+ func.toString( )
									+ "\r\n" );
							for ( int i = 0; i < select.tables.size( ); i++ )
							{
								if ( select.tables.getTable( i ).getSubquery( ) == null )
								{
									buffer.append( buildString( " ", level + 1 )
											+ "--> table "
											+ SQLUtil.trimColumnStringQuote( select.tables.getTable( i )
													.getFullNameWithAliasString( ) )
											+ "\r\n" );
								}
								else
								{
									buffer.append( buildString( " ", level + 1 )
											+ "--> table "
											+ select.tables.getTable( i )
													.toString( )
											+ ( select.tables.getTable( i )
													.getAliasClause( ) != null ? ( " " + select.tables.getTable( i )
													.getAliasClause( )
													.toString( ) )
													: "" )
											+ "\r\n" );
								}
							}
						}
						// check column in function arguments
						int argCount = 0;
						if ( func.getArgs( ) != null )
						{
							for ( int k = 0; k < func.getArgs( ).size( ); k++ )
							{
								TExpression expr = func.getArgs( )
										.getExpression( k );
								if ( expr.toString( ).trim( ).equals( "*" ) )
									continue;
								List<TColumn> columns1 = exprToColumn( expr,
										select,
										level + 1,
										ClauseType.select,
										parentAlias );
								for ( TColumn column1 : columns1 )
								{
									findColumnInTables( column1,
											select,
											level + 1,
											null,
											null );
									findColumnsFromClauses( select, level + 2 );
								}
								argCount++;
							}
						}

						if ( argCount == 0
								&& !"ROW_NUMBER".equalsIgnoreCase( func.getFunctionName( )
										.toString( ) ) )
						{

							Point point = new Point( (int) func.getEndToken( ).lineNo,
									(int) func.getEndToken( ).columnNo );
							if ( func.getArgs( ) != null
									&& func.getArgs( ).size( ) > 0 )
							{
								for ( int k = 0; k < func.getArgs( ).size( ); k++ )
								{
									TExpression expr = func.getArgs( )
											.getExpression( k );
									if ( expr.toString( ).trim( ).equals( "*" ) )
									{
										point = new Point( (int) expr.getStartToken( ).lineNo,
												(int) expr.getStartToken( ).columnNo );
										break;
									}
								}
							}
							if ( dependMap.containsKey( currentSource ) )
							{

								if ( currentClauseMap.containsKey( select ) )
								{
									dependMap.get( currentSource )
											.add( new TResultEntry( select.tables.getTable( 0 ),
													"*",
													currentClauseMap.get( select ),
													point ) );
								}
								else if ( select instanceof TSelectSqlStatement )
								{
									dependMap.get( currentSource )
											.add( new TResultEntry( select.tables.getTable( 0 ),
													"*",
													ClauseType.select,
													point ) );
								}
								else
								{
									dependMap.get( currentSource )
											.add( new TResultEntry( select.tables.getTable( 0 ),
													"*",
													ClauseType.undefine,
													point ) );
								}
							}
						}

						if ( func.getAnalyticFunction( ) != null )
						{
							TParseTreeNodeList list = func.getAnalyticFunction( )
									.getPartitionBy_ExprList( );
							findColumnsFromList( select,
									level + 1,
									list,
									ClauseType.select );

							if ( func.getAnalyticFunction( ).getOrderBy( ) != null )
							{
								list = func.getAnalyticFunction( )
										.getOrderBy( )
										.getItems( );
								findColumnsFromList( select,
										level + 1,
										list,
										ClauseType.select );
							}
						}

						findColumnsFromClauses( select, level + 2 );

					}
				}
				break;
		}
		if ( level == 0 )
		{
			if ( currentSource == null )
			{
				currentSource = getCurrentSource( field.getExpr( ) );
				if ( !dependMap.containsKey( currentSource ) )
					dependMap.put( currentSource, new ArrayList<TResultEntry>( ) );
			}

			if ( isTopSelectStmt( select ) && !ignoreTopSelect )
			{
				TTable table = new TTable( );
				table.setTableName( getVirtualTable( select ) );
				TColumn column = new TColumn( );
				if ( parentAlias != null )
				{
					column.linkColumnName = parentAlias.getAliasDisplayName( );
					column.columnName = SQLUtil.trimColumnStringQuote( currentSource );
				}
				else
				{
					column.linkColumnName = field.getAliasClause( ) != null ? field.getAliasClause( )
							.toString( )
							: ( isNotEmpty( field.getColumnNameOnly( ) ) ? field.getColumnNameOnly( )
									: field.toString( ) );
					column.columnName = SQLUtil.trimColumnStringQuote( currentSource );
				}
				if ( parentAlias != null )
				{
					column.location = parentAlias.location;

					column.offset = Long.parseLong( parentAlias.columnHighlightInfo.split( "," )[0] );
					column.length = Long.parseLong( parentAlias.columnHighlightInfo.split( "," )[1] );;
				}
				else if ( field.getAliasClause( ) != null )
				{
					column.location = new Point( (int) field.getAliasClause( )
							.getStartToken( ).lineNo,
							(int) field.getAliasClause( ).getStartToken( ).columnNo );

					column.offset = field.getAliasClause( ).getStartToken( ).offset;
					column.length = field.getAliasClause( )
							.getAliasName( )
							.toString( )
							.length( );
				}
				else if ( isNotEmpty( field.getColumnNameOnly( ) ) )
				{
					column.location = new Point( (int) field.getFieldAttr( )
							.getStartToken( ).lineNo,
							(int) field.getFieldAttr( ).getStartToken( ).columnNo );

					column.offset = field.getFieldAttr( ).getStartToken( ).offset;
					column.length = field.getFieldAttr( ).toString( ).length( );
				}
				else
				{
					column.location = new Point( (int) field.getStartToken( ).lineNo,
							(int) field.getStartToken( ).columnNo );

					column.offset = field.getStartToken( ).offset;
					column.length = field.toString( ).length( );
				}

				column.clauseType = ClauseType.topselect;
				TResultEntry resultEntry = new TResultEntry( table,
						column,
						column.columnName,
						ClauseType.topselect,
						column.location );
				dependMap.get( currentSource ).add( resultEntry );
			}
			else if ( select.getParentStmt( ) instanceof TCreateViewSqlStatement )
			{
				TCreateViewSqlStatement createView = (TCreateViewSqlStatement) select.getParentStmt( );
				TTable table = new TTable( );
				table.setTableName( createView.getViewName( ) );
				TColumn column = new TColumn( );
				if ( parentAlias != null )
				{
					column.linkColumnName = parentAlias.getAliasDisplayName( );
					column.columnName = SQLUtil.trimColumnStringQuote( currentSource );
				}
				else
				{
					column.linkColumnName = field.getAliasClause( ) != null ? field.getAliasClause( )
							.toString( )
							: ( isNotEmpty( field.getColumnNameOnly( ) ) ? field.getColumnNameOnly( )
									: field.toString( ) );
					column.columnName = SQLUtil.trimColumnStringQuote( currentSource );
				}
				if ( parentAlias != null )
				{
					column.location = parentAlias.location;
				}
				else if ( field.getAliasClause( ) != null )
				{
					column.location = new Point( (int) field.getAliasClause( )
							.getStartToken( ).lineNo,
							(int) field.getAliasClause( ).getStartToken( ).columnNo );
				}
				else if ( createView.getViewAliasClause( ) != null )
				{
					for ( int i = 0; i < select.getResultColumnList( ).size( ); i++ )
					{
						if ( select.getResultColumnList( )
								.getResultColumn( i )
								.equals( field ) )
						{
							TViewAliasItem item = createView.getViewAliasClause( )
									.getViewAliasItemList( )
									.getViewAliasItem( i );
							column.location = new Point( (int) item.getStartToken( ).lineNo,
									(int) item.getStartToken( ).columnNo );
						}
					}
				}
				else if ( isNotEmpty( field.getColumnNameOnly( ) ) )
				{
					column.location = new Point( (int) field.getFieldAttr( )
							.getEndToken( ).lineNo, (int) field.getFieldAttr( )
							.getEndToken( ).columnNo );
				}
				else
				{
					column.location = new Point( (int) createView.getViewName( )
							.getEndToken( ).lineNo,
							(int) createView.getViewName( ).getEndToken( ).columnNo );
				}
				column.offset = createView.getViewName( ).getEndToken( ).offset;
				column.length = createView.getViewName( ).getEndToken( ).astext.length( );
				column.clauseType = ClauseType.createview;
				TResultEntry resultEntry = new TResultEntry( table,
						column,
						column.columnName,
						ClauseType.createview,
						column.location );
				dependMap.get( currentSource ).add( resultEntry );
			}
			else if ( select.getParentStmt( ) instanceof TCreateTableSqlStatement
					&& ( (TCreateTableSqlStatement) select.getParentStmt( ) ).getSubQuery( ) != null )
			{
				TCreateTableSqlStatement createTable = (TCreateTableSqlStatement) select.getParentStmt( );
				TTable table = new TTable( );
				table.setTableName( createTable.getTableName( ) );
				TColumn column = new TColumn( );
				if ( parentAlias != null )
				{
					column.linkColumnName = parentAlias.getAliasDisplayName( );
					column.columnName = SQLUtil.trimColumnStringQuote( currentSource );
				}
				else
				{
					column.linkColumnName = field.getAliasClause( ) != null ? field.getAliasClause( )
							.toString( )
							: ( isNotEmpty( field.getColumnNameOnly( ) ) ? field.getColumnNameOnly( )
									: field.toString( ) );
					column.columnName = SQLUtil.trimColumnStringQuote( currentSource );
				}
				if ( parentAlias != null )
				{
					column.location = parentAlias.location;
				}
				else if ( field.getAliasClause( ) != null )
				{
					column.location = new Point( (int) field.getAliasClause( )
							.getStartToken( ).lineNo,
							(int) field.getAliasClause( ).getStartToken( ).columnNo );
				}
				else if ( createTable.getColumnList( ) != null
						&& createTable.getColumnList( ).size( ) > 0 )
				{
					for ( int i = 0; i < select.getResultColumnList( ).size( ); i++ )
					{
						if ( select.getResultColumnList( )
								.getResultColumn( i )
								.equals( field ) )
						{
							TColumnDefinition item = createTable.getColumnList( )
									.getColumn( i );
							column.location = new Point( (int) item.getStartToken( ).lineNo,
									(int) item.getStartToken( ).columnNo );
						}
					}
				}
				else if ( isNotEmpty( field.getColumnNameOnly( ) ) )
				{
					column.location = new Point( (int) field.getFieldAttr( )
							.getEndToken( ).lineNo, (int) field.getFieldAttr( )
							.getEndToken( ).columnNo );
				}
				else
				{
					column.location = new Point( (int) createTable.getTableName( )
							.getEndToken( ).lineNo,
							(int) createTable.getTableName( ).getEndToken( ).columnNo );
				}
				column.offset = createTable.getTableName( ).getEndToken( ).offset;
				column.length = createTable.getTableName( ).getEndToken( ).astext.length( );
				column.clauseType = ClauseType.createtable;
				TResultEntry resultEntry = new TResultEntry( table,
						column,
						column.columnName,
						ClauseType.createtable,
						column.location );
				dependMap.get( currentSource ).add( resultEntry );
			}
			else if ( select instanceof TUpdateSqlStatement
					&& ( field.getExpr( ).getLeftOperand( ) == null || field.getExpr( )
							.getLeftOperand( )
							.getExprList( ) == null ) )
			{
				TUpdateSqlStatement update = (TUpdateSqlStatement) select;
				TTable table = new TTable( );
				if ( update.getTargetTable( ).getLinkTable( ) != null )
				{
					table.setTableName( update.getTargetTable( )
							.getLinkTable( )
							.getTableName( ) );
					TAliasClause alias = new TAliasClause( );
					alias.setAliasName( update.getTargetTable( ).getTableName( ) );
					table.setAliasClause( alias );
				}
				else
				{
					table.setTableName( update.getTargetTable( ).getTableName( ) );
				}
				TColumn column = new TColumn( );
				TExpression columnExpression = getColumnExpression( field.getExpr( ) );
				if ( parentAlias != null )
				{
					column.linkColumnName = parentAlias.getAliasDisplayName( );
					column.columnName = SQLUtil.trimColumnStringQuote( currentSource );
				}
				else
				{
					column.linkColumnName = columnExpression.getObjectOperand( ) != null ? columnExpression.getObjectOperand( )
							.getColumnNameOnly( )
							: columnExpression.toString( );
					column.columnName = SQLUtil.trimColumnStringQuote( currentSource );
				}
				if ( parentAlias != null )
				{
					column.location = parentAlias.location;

					column.offset = Long.parseLong( parentAlias.columnHighlightInfo.split( "," )[0] );
					column.length = Long.parseLong( parentAlias.columnHighlightInfo.split( "," )[1] );;
				}
				else
				{
					column.location = new Point( (int) columnExpression.getStartToken( ).lineNo,
							(int) columnExpression.getStartToken( ).columnNo );

					column.offset = columnExpression.getStartToken( ).offset;
					column.length = columnExpression.toString( ).length( );
				}
				column.clauseType = ClauseType.updateset;
				TResultEntry resultEntry = new TResultEntry( table,
						column,
						column.columnName,
						ClauseType.updateset,
						column.location );
				dependMap.get( currentSource ).add( resultEntry );
			}
			else if ( select instanceof TMergeSqlStatement )
			{
				TMergeSqlStatement merge = (TMergeSqlStatement) select;
				TTable table = new TTable( );
				table.setTableName( findTableName( merge.getTargetTable( ) ) );
				TColumn column = new TColumn( );
				if ( parentAlias != null )
				{
					column.linkColumnName = parentAlias.getAliasDisplayName( );
					column.columnName = SQLUtil.trimColumnStringQuote( currentSource );
				}
				else
				{
					column.linkColumnName = field.getExpr( )
							.getLeftOperand( )
							.getObjectOperand( ) != null ? field.getExpr( )
							.getLeftOperand( )
							.getObjectOperand( )
							.getColumnNameOnly( )
							.toString( ) : field.getExpr( )
							.getLeftOperand( )
							.toString( );
					column.columnName = SQLUtil.trimColumnStringQuote( currentSource );
				}
				if ( parentAlias != null )
				{
					column.location = parentAlias.location;

					column.offset = Long.parseLong( parentAlias.columnHighlightInfo.split( "," )[0] );
					column.length = Long.parseLong( parentAlias.columnHighlightInfo.split( "," )[1] );;
				}
				else
				{
					column.location = new Point( (int) field.getExpr( )
							.getLeftOperand( )
							.getStartToken( ).lineNo, (int) field.getExpr( )
							.getLeftOperand( )
							.getStartToken( ).columnNo );

					column.offset = field.getExpr( )
							.getLeftOperand( )
							.getStartToken( ).offset;
					column.length = field.getExpr( )
							.getLeftOperand( )
							.toString( )
							.length( );
				}
				column.clauseType = ClauseType.mergeset;
				TResultEntry resultEntry = new TResultEntry( table,
						column,
						column.columnName,
						ClauseType.mergeset,
						column.location );
				dependMap.get( currentSource ).add( resultEntry );
			}
			else if ( select.getParentStmt( ) instanceof TInsertSqlStatement )
			{
				TInsertSqlStatement insertStmt = (TInsertSqlStatement) select.getParentStmt( );

				if ( insertStmt.getColumnList( ) != null
						&& select.getResultColumnList( ) != null )
				{
					if ( insertStmt.getColumnList( ).size( ) == select.getResultColumnList( )
							.size( ) )
					{
						for ( int i = 0; i < insertStmt.getColumnList( ).size( ); i++ )
						{
							if ( select.getResultColumnList( )
									.getResultColumn( i )
									.equals( field ) )
							{
								TTable table = new TTable( );
								table.setTableName( insertStmt.getTargetTable( )
										.getTableName( ) );
								TColumn column = new TColumn( );

								column.columnName = SQLUtil.trimColumnStringQuote( currentSource );

								TObjectName columnName = insertStmt.getColumnList( )
										.getObjectName( i );

								column.linkColumnName = ( isNotEmpty( columnName.getColumnNameOnly( ) ) ? columnName.getColumnNameOnly( )
										: columnName.toString( ) );

								column.location = new Point( (int) columnName.getStartToken( ).lineNo,
										(int) columnName.getStartToken( ).columnNo );
								column.offset = columnName.getStartToken( ).offset;
								column.length = columnName.toString( ).length( );
								column.clauseType = ClauseType.insert;
								TResultEntry resultEntry = new TResultEntry( table,
										column,
										column.columnName,
										ClauseType.insert,
										column.location );
								dependMap.get( currentSource )
										.add( resultEntry );
							}
						}
					}
					else if ( "*".equals( select.getResultColumnList( )
							.getResultColumn( 0 )
							.toString( ) ) )
					{
						for ( int i = 0; i < insertStmt.getColumnList( ).size( ); i++ )
						{
							if ( select.getResultColumnList( )
									.getResultColumn( 0 )
									.equals( field ) )
							{
								TTable table = new TTable( );
								table.setTableName( insertStmt.getTargetTable( )
										.getTableName( ) );
								TColumn column = new TColumn( );

								column.columnName = SQLUtil.trimColumnStringQuote( currentSource );

								TObjectName columnName = insertStmt.getColumnList( )
										.getObjectName( i );

								column.linkColumnName = ( isNotEmpty( columnName.getColumnNameOnly( ) ) ? columnName.getColumnNameOnly( )
										: columnName.toString( ) );

								column.location = new Point( (int) columnName.getStartToken( ).lineNo,
										(int) columnName.getStartToken( ).columnNo );
								column.offset = columnName.getStartToken( ).offset;
								column.length = columnName.toString( ).length( );
								column.clauseType = ClauseType.insert;
								TResultEntry resultEntry = new TResultEntry( table,
										column,
										column.columnName,
										ClauseType.insert,
										column.location );
								dependMap.get( currentSource )
										.add( resultEntry );
							}
						}
					}
				}
				else if ( insertStmt.getColumnList( ) == null )
				{
					for ( int i = 0; i < select.getResultColumnList( ).size( ); i++ )
					{
						if ( select.getResultColumnList( )
								.getResultColumn( i )
								.equals( field ) )
						{
							if ( insertStmt.getTargetTable( ).getSubquery( ) != null )
							{
								continue;
							}
							TableMetaData tableMetaData = getTableMetaData( insertStmt.getTargetTable( )
									.getTableName( ) );

							List<ColumnMetaData> columns = tableColumns.get( tableMetaData );
							if ( columns != null
									&& columns.size( ) == select.getResultColumnList( )
											.size( ) )
							{
								TTable table = new TTable( );
								table.setTableName( insertStmt.getTargetTable( )
										.getTableName( ) );
								TColumn column = new TColumn( );

								column.columnName = SQLUtil.trimColumnStringQuote( currentSource );

								column.linkColumnName = columns.get( i )
										.getName( );

								column.location = new Point( (int) field.getStartToken( ).lineNo,
										(int) field.getStartToken( ).columnNo );
								column.offset = field.getStartToken( ).offset;
								column.length = field.toString( ).length( );
								column.clauseType = ClauseType.insert;
								TResultEntry resultEntry = new TResultEntry( table,
										column,
										column.columnName,
										ClauseType.insert,
										column.location );
								dependMap.get( currentSource )
										.add( resultEntry );

							}
							else if ( columns == null || columns.size( ) == 0 )
							{
								TTable table = new TTable( );
								table.setTableName( insertStmt.getTargetTable( )
										.getTableName( ) );
								TColumn column = new TColumn( );

								column.columnName = SQLUtil.trimColumnStringQuote( currentSource );

								if ( field.getColumnAlias( ) != null
										&& !"".equals( field.getColumnAlias( ) ) )
								{
									column.linkColumnName = field.getColumnAlias( );
								}
								else if ( field.getColumnNameOnly( ) != null
										&& !"".equals( field.getColumnNameOnly( ) ) )
								{
									column.linkColumnName = field.getColumnNameOnly( );
								}
								else
								{
									continue;
								}

								column.location = new Point( (int) field.getStartToken( ).lineNo,
										(int) field.getStartToken( ).columnNo );
								column.offset = field.getStartToken( ).offset;
								column.length = field.toString( ).length( );
								column.clauseType = ClauseType.insert;
								TResultEntry resultEntry = new TResultEntry( table,
										column,
										column.columnName,
										ClauseType.insert,
										column.location );
								dependMap.get( currentSource )
										.add( resultEntry );
							}
						}
					}
				}
			}
		}

		if ( select.getParentStmt( ) instanceof TUpdateSqlStatement )
		{
			TUpdateSqlStatement update = (TUpdateSqlStatement) select.getParentStmt( );
			TTable table = new TTable( );
			table.setTableName( update.getTargetTable( ).getTableName( ) );
			TColumn column = new TColumn( );

			for ( int j = 0; j < update.getResultColumnList( ).size( ); j++ )
			{
				TResultColumn resultColumn = update.getResultColumnList( )
						.getResultColumn( j );
				TExpression leftExpr = resultColumn.getExpr( ).getLeftOperand( );
				TExpression rightExpr = resultColumn.getExpr( )
						.getRightOperand( );
				if ( leftExpr.getExpressionType( ) == EExpressionType.list_t )
				{
					TExpressionList setExpression = leftExpr.getExprList( );

					TCustomSqlStatement stmt = select;
					if ( rightExpr.getExpressionType( ) == EExpressionType.subquery_t )
					{
						stmt = rightExpr.getSubQuery( );
					}

					for ( int k = 0; k < stmt.getResultColumnList( ).size( ); k++ )
					{
						if ( field == stmt.getResultColumnList( )
								.getResultColumn( k ) )
						{
							column.linkColumnName = setExpression.getExpression( k )
									.toString( );
							column.columnName = SQLUtil.trimColumnStringQuote( currentSource );

							column.location = new Point( (int) setExpression.getExpression( k )
									.getStartToken( ).lineNo,
									(int) setExpression.getExpression( k )
											.getStartToken( ).columnNo );

							column.offset = setExpression.getExpression( k )
									.getStartToken( ).offset;
							column.length = setExpression.getExpression( k )
									.toString( )
									.length( );
							column.clauseType = ClauseType.updateset;
							TResultEntry resultEntry = new TResultEntry( table,
									column,
									column.columnName,
									ClauseType.updateset,
									column.location );
							dependMap.get( currentSource ).add( resultEntry );
						}
					}
				}
			}
		}
		return ret;
	}

	private TObjectName findTableName( TTable targetTable )
	{
		if ( targetTable.getTableName( ) != null )
			return targetTable.getTableName( );
		else if ( targetTable.getSubquery( ) != null
				&& targetTable.getSubquery( ).tables != null
				&& targetTable.getSubquery( ).tables.size( ) > 0 )
			return findTableName( targetTable.getSubquery( ).tables.getTable( 0 ) );
		return null;
	}

	private TExpression getColumnExpression( TExpression expr )
	{
		if ( expr.getExpressionType( ) == EExpressionType.simple_object_name_t )
		{
			return expr;
		}
		else if ( expr.getLeftOperand( ) != null )
		{
			return getColumnExpression( expr.getLeftOperand( ) );
		}
		else
			return expr;
	}

	private String getCurrentSource( TExpression expr )
	{
		if ( expr.getExpressionType( ) == EExpressionType.simple_object_name_t )
		{
			return expr.toString( );
		}
		else if ( expr.getLeftOperand( ) != null )
		{
			return getCurrentSource( expr.getLeftOperand( ) );
		}
		else
			return null;
	}

	private boolean isTopSelectStmt( TCustomSqlStatement select )
	{
		if ( !( select instanceof TSelectSqlStatement ) )
			return false;
		else
		{
			if ( select.getParentStmt( ) == null )
				return true;
			TCustomSqlStatement parent = select.getParentStmt( );
			if ( !( parent instanceof TSelectSqlStatement ) )
				return false;
			TSelectSqlStatement parentSelectSqlStatement = (TSelectSqlStatement) parent;
			if ( parentSelectSqlStatement.getSetOperatorType( ) == ESetOperatorType.none )
				return false;
			if ( parentSelectSqlStatement.getLeftStmt( ) == select
					|| parentSelectSqlStatement.getRightStmt( ) == select )
			{
				return isTopSelectStmt( parentSelectSqlStatement );
			}
			else
				return false;
		}
	}

	private TCustomSqlStatement getTopSelect( TCustomSqlStatement select )
	{
		if ( !( select instanceof TSelectSqlStatement ) )
			return null;
		else
		{
			if ( select.getParentStmt( ) == null )
				return select;
			TCustomSqlStatement parent = select.getParentStmt( );
			if ( !( parent instanceof TSelectSqlStatement ) )
				return null;
			TSelectSqlStatement parentSelectSqlStatement = (TSelectSqlStatement) parent;
			if ( parentSelectSqlStatement.getSetOperatorType( ) == ESetOperatorType.none )
				return null;
			if ( parentSelectSqlStatement.getLeftStmt( ) == select
					|| parentSelectSqlStatement.getRightStmt( ) == select )
			{
				return getTopSelect( parentSelectSqlStatement );
			}
			else
				return null;
		}
	}

	private TObjectName getVirtualTable( TCustomSqlStatement stmt )
	{
		final TSourceToken virtualToken = new TSourceToken( );
		virtualToken.setString( virtualTable == null ? SQLUtil.generateVirtualTableName( getTopSelect( stmt ) )
				: virtualTable );
		TObjectName objectName = new TObjectName( ) {

			public String toString( )
			{
				return virtualToken.toString( );
			}
		};

		objectName.init( virtualToken, null );
		return objectName;
	}

	private void getSelectSqlStatements( TSelectSqlStatement select,
			List<TSelectSqlStatement> stmtList )
	{
		if ( select.getSetOperatorType( ) != ESetOperatorType.none )
		{
			getSelectSqlStatements( select.getLeftStmt( ), stmtList );
			getSelectSqlStatements( select.getRightStmt( ), stmtList );
		}
		else
		{
			stmtList.add( select );
		}
	}

	private Table TLzTaleToTable( TTable lztable )
	{
		Table table = new Table( );
		if ( lztable != null )
		{
			if ( lztable.getSubquery( ) == null
					&& lztable.getTableName( ) != null
					&& lztable.getFullName( ) != null )
			{
				table.tableName = SQLUtil.trimColumnStringQuote( getTableName( lztable ) );
				if ( lztable.getTableName( ).toString( ).indexOf( "." ) > 0 )
				{
					table.prefixName = SQLUtil.trimColumnStringQuote( lztable.getTableName( )
							.toString( )
							.substring( 0, lztable.getFullName( ).indexOf( '.' ) ) );
				}
			}

			if ( lztable.getAliasClause( ) != null )
			{
				table.tableAlias = SQLUtil.trimColumnStringQuote( lztable.getAliasClause( )
						.toString( ) );
			}
		}
		return table;
	}

	public ColumnImpactModel generateModel( )
	{
		ColumnImpactModel model = new ColumnImpactModel( );
		if ( !isSucess( ) || getImpactResult( ).trim( ).length( ) == 0 )
			return model;
		columnImpactResult result = XML2Model.loadXML( columnImpactResult.class,
				getImpactResult( ) );
		List<targetColumn> columns = result.getColumns( );
		if ( columns != null )
		{
			for ( int i = 0; i < columns.size( ); i++ )
			{
				targetColumn field = columns.get( i );
				AliasModel alias = null;
				FieldModel fieldModel = null;
				if ( field.getAlias( ) != null )
				{
					alias = new AliasModel( );
					alias.setName( field.getAlias( ) );

					FieldModel aliasField = new FieldModel( );
					int index = field.getName( ).lastIndexOf( '.' );
					if ( index != -1 )
					{
						aliasField.setName( field.getName( )
								.substring( index + 1 ) );
						aliasField.setSchema( field.getName( ).substring( 0,
								index ) );
					}
					else
					{
						aliasField.setName( field.getName( ) );
					}
					alias.setField( aliasField );

					try
					{
						alias.setHighlightInfo( field.getAliasHighlightInfo( ) );
						aliasField.setHighlightInfo( field.getColumnHighlightInfo( ) );
						alias.setCoordinate( field.getAliasCoordinate( ) );
						aliasField.setCoordinate( field.getCoordinate( ) );
					}
					catch ( Exception e )
					{

					}

					model.addAlias( alias );
				}
				else
				{
					fieldModel = new FieldModel( );
					int index = field.getName( ).lastIndexOf( '.' );
					if ( index != -1 )
					{
						fieldModel.setName( field.getName( )
								.substring( index + 1 ) );
						fieldModel.setSchema( field.getName( ).substring( 0,
								index ) );
					}
					else
					{
						fieldModel.setName( field.getName( ) );
					}
					fieldModel.setHighlightInfo( field.getColumnHighlightInfo( ) );
					fieldModel.setCoordinate( field.getCoordinate( ) );
					model.addField( fieldModel );
				}
				List<sourceColumn> sources = field.getColumns( );
				for ( int j = 0; sources != null && j < sources.size( ); j++ )
				{
					sourceColumn source = sources.get( j );

					if ( "true".equals( source.getOrphan( ) ) )
						continue;

					if ( !model.containsTable( source.getTableOwner( ),
							source.getTableName( ) ) )
					{
						TableModel tableModel = new TableModel( );
						tableModel.setSchema( source.getTableOwner( ) );
						tableModel.setName( source.getTableName( ) );
						tableModel.setHighlightInfo( getTableHighlightInfo( result,
								tableModel ) );
						tableModel.setCoordinate( getTableCoordinate( result,
								tableModel ) );
						model.addTable( tableModel );
					}
					TableModel table = model.getTable( source.getTableOwner( ),
							source.getTableName( ) );

					if ( source.getName( ) == null
							|| "".equals( source.getName( ) ) )
					{
						ReferenceModel ref = new ReferenceModel( );
						ref.setTable( table );
						if ( source.getClause( ) != null
								&& source.getClause( )
										.toUpperCase( )
										.indexOf( "SELECT" ) != -1 )
						{
							ref.setClause( Clause.SELECT );
						}
						if ( source.getClause( ) != null
								&& source.getClause( )
										.toUpperCase( )
										.indexOf( "UPDATE" ) != -1 )
						{
							ref.setClause( Clause.UPDATE );
						}
						if ( source.getClause( ) != null
								&& source.getClause( )
										.toUpperCase( )
										.indexOf( "MERGE" ) != -1 )
						{
							ref.setClause( Clause.MERGE );
						}
						if ( alias != null )
						{
							ref.setReferenceType( ReferenceModel.TYPE_ALIAS_TABLE );
							ref.setAlias( alias );
							model.addReference( ref );

						}
						else if ( fieldModel != null )
						{
							ref.setReferenceType( ReferenceModel.TYPE_FIELD_TABLE );
							ref.setField( fieldModel );
							model.addReference( ref );
						}
						ColumnModel column = new ColumnModel( "*", table );
						column.setHighlightInfos( source.getHighlightInfos( ) );
						column.setCoordinate( source.getCoordinate( ) );
						if ( !table.containsColumn( column ) )
						{
							table.addColumn( column );
						}
						ref.setColumn( column );
					}
					else
					{
						ColumnModel column = new ColumnModel( source.getName( ),
								table );
						column.setHighlightInfos( source.getHighlightInfos( ) );
						column.setCoordinate( source.getCoordinate( ) );
						if ( !table.containsColumn( column ) )
						{
							table.addColumn( column );
						}
						ReferenceModel ref = new ReferenceModel( );
						if ( source.getClause( ) != null
								&& source.getClause( )
										.toUpperCase( )
										.indexOf( "SELECT" ) != -1 )
						{
							ref.setClause( Clause.SELECT );
						}

						if ( source.getClause( ) != null
								&& source.getClause( )
										.toUpperCase( )
										.indexOf( "UPDATE" ) != -1 )
						{
							ref.setClause( Clause.UPDATE );
						}

						if ( source.getClause( ) != null
								&& source.getClause( )
										.toUpperCase( )
										.indexOf( "MERGE" ) != -1 )
						{
							ref.setClause( Clause.MERGE );
						}
						ref.setColumn( column );
						if ( alias != null )
						{
							ref.setReferenceType( ReferenceModel.TYPE_ALIAS_COLUMN );
							ref.setAlias( alias );
							model.addReference( ref );
						}
						else if ( fieldModel != null )
						{
							ref.setReferenceType( ReferenceModel.TYPE_FIELD_COLUMN );
							ref.setField( fieldModel );
							model.addReference( ref );
						}
					}
				}
			}
		}
		return model;
	}

	private String getTableFullName( table table )
	{
		if ( table.getOwner( ) != null
				&& table.getOwner( ).trim( ).length( ) > 0 )
			return table.getOwner( ) + "." + table.getName( );
		else
			return table.getName( );
	}

	private String getTableHighlightInfo( columnImpactResult result,
			TableModel tableModel )
	{
		List<table> tables = result.getTables( );
		for ( int i = 0; i < tables.size( ); i++ )
		{
			table table = tables.get( i );
			if ( getTableFullName( table ).equals( tableModel.getFullName( ) ) )
			{
				return table.getHighlightInfo( );
			}
		}
		return null;
	}

	private String getTableCoordinate( columnImpactResult result,
			TableModel tableModel )
	{
		List<table> tables = result.getTables( );
		for ( int i = 0; i < tables.size( ); i++ )
		{
			table table = tables.get( i );
			if ( getTableFullName( table ).equals( tableModel.getFullName( ) ) )
			{
				return table.getCoordinate( );
			}
		}
		return null;
	}

}
