
package demos.visitors;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EDeclareType;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.EParameterMode;
import gudusoft.gsqlparser.ETriggerDmlType;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.nodes.hive.THiveTablePartition;
import gudusoft.gsqlparser.nodes.mdx.EMdxExpSyntax;
import gudusoft.gsqlparser.nodes.mdx.IMdxIdentifierSegment;
import gudusoft.gsqlparser.nodes.mdx.TMdxAxis;
import gudusoft.gsqlparser.nodes.mdx.TMdxAxisNode;
import gudusoft.gsqlparser.nodes.mdx.TMdxBinOpNode;
import gudusoft.gsqlparser.nodes.mdx.TMdxCaseNode;
import gudusoft.gsqlparser.nodes.mdx.TMdxExpNode;
import gudusoft.gsqlparser.nodes.mdx.TMdxFloatConstNode;
import gudusoft.gsqlparser.nodes.mdx.TMdxFunctionNode;
import gudusoft.gsqlparser.nodes.mdx.TMdxIdentifierNode;
import gudusoft.gsqlparser.nodes.mdx.TMdxIntegerConstNode;
import gudusoft.gsqlparser.nodes.mdx.TMdxPropertyNode;
import gudusoft.gsqlparser.nodes.mdx.TMdxSetNode;
import gudusoft.gsqlparser.nodes.mdx.TMdxStringConstNode;
import gudusoft.gsqlparser.nodes.mdx.TMdxTupleNode;
import gudusoft.gsqlparser.nodes.mdx.TMdxUnaryOpNode;
import gudusoft.gsqlparser.nodes.mdx.TMdxWhenNode;
import gudusoft.gsqlparser.nodes.mdx.TMdxWhereNode;
import gudusoft.gsqlparser.nodes.mdx.TMdxWithMemberNode;
import gudusoft.gsqlparser.nodes.mdx.TMdxWithNode;
import gudusoft.gsqlparser.nodes.mdx.TMdxWithSetNode;
import gudusoft.gsqlparser.nodes.teradata.TDataConversion;
import gudusoft.gsqlparser.nodes.teradata.TDataConversionItem;
import gudusoft.gsqlparser.nodes.teradata.TIndexDefinition;
import gudusoft.gsqlparser.stmt.*;
import gudusoft.gsqlparser.stmt.db2.TCreateVariableStmt;
import gudusoft.gsqlparser.stmt.db2.TDb2HandlerDeclaration;
import gudusoft.gsqlparser.stmt.db2.TDb2SetVariableStmt;
import gudusoft.gsqlparser.stmt.mdx.TMdxSelect;
import gudusoft.gsqlparser.stmt.mssql.TMssqlBlock;
import gudusoft.gsqlparser.stmt.mssql.TMssqlCommit;
import gudusoft.gsqlparser.stmt.mssql.TMssqlCreateFunction;
import gudusoft.gsqlparser.stmt.mssql.TMssqlCreateProcedure;
import gudusoft.gsqlparser.stmt.mssql.TMssqlCreateTrigger;
import gudusoft.gsqlparser.stmt.mssql.TMssqlCreateXmlSchemaCollectionStmt;
import gudusoft.gsqlparser.stmt.mssql.TMssqlDeclare;
import gudusoft.gsqlparser.stmt.mssql.TMssqlExecute;
import gudusoft.gsqlparser.stmt.mssql.TMssqlGo;
import gudusoft.gsqlparser.stmt.mssql.TMssqlIfElse;
import gudusoft.gsqlparser.stmt.mssql.TMssqlPrint;
import gudusoft.gsqlparser.stmt.mssql.TMssqlReturn;
import gudusoft.gsqlparser.stmt.mssql.TMssqlRollback;
import gudusoft.gsqlparser.stmt.mssql.TMssqlSaveTran;
import gudusoft.gsqlparser.stmt.mssql.TMssqlSet;
import gudusoft.gsqlparser.stmt.mysql.TMySQLDeallocatePrepareStmt;
import gudusoft.gsqlparser.stmt.oracle.TBasicStmt;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlContinue;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateFunction;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreatePackage;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateProcedure;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateTrigger;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateType;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateTypeBody;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateType_Placeholder;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlExecImmeStmt;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlForallStmt;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlGotoStmt;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlNullStmt;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlRecordTypeDefStmt;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlTableTypeDefStmt;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlVarrayTypeDefStmt;
import gudusoft.gsqlparser.stmt.oracle.TSqlplusCmdStatement;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Iterator;
import java.util.Stack;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import gudusoft.gsqlparser.stmt.teradata.TTeradataCreateMacro;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class xmlVisitor extends TParseTreeVisitor
{

	public static final String TAG_SQLSCRIPT = "sqlscript";
	public static final String TAG_SELECT_STATEMENT = "select_statement";
	public static final String TAG_COLUMN_REFERENCED_EXPR = "column_referenced_expr";
	public static final String TAG_CONSTANT_EXPR = "constant_expr";
	public static final String TAG_FUNCTIONCALL_EXPR = "functionCall_expr";
	public static final String TAG_FUNCTIONCALL = "functionCall";
	public static final String TAG_FUNCTIONNAME = "functionName";
	public static final String TAG_FUNCTIONARGS = "functionArgs";
	public static final String TAG_GENERIC_FUNCTION = "generic_function";
	public static final String TAG_CAST_FUNCTION = "cast_function";
	public static final String TAG_CONVERT_FUNCTION = "convert_function";
	public static final String TAG_TREAT_FUNCTION = "treat_function";
	public static final String TAG_CONTAINS_FUNCTION = "contains_function";
	public static final String TAG_FREETEXT_FUNCTION = "freetext_function";
	public static final String TAG_TRIM_FUNCTION = "trim_function";
	public static final String TAG_EXTRACT_FUNCTION = "extract_function";
	public static final String TAG_SUBQUERY_EXPR = "subquery_expr";
	public static final String TAG_EXISTS_EXPR = "exists_expr";
	public static final String TAG_COMPARISON_EXPR = "comparison_expr";
	public static final String TAG_LIST_EXPR = "list_expr";
	public static final String TAG_IN_EXPR = "in_expr";
	public static final String TAG_LIKE_EXPR = "like_expr";
	public static final String TAG_BETWEEN_EXPR = "between_expr";
	public static final String TAG_NOT_EXPR = "not_expr";
	public static final String TAG_UNARY_EXPR = "unary_expr";
	public static final String TAG_BINARY_EXPR = "binary_expr";
	public static final String TAG_PARENTHESIS_EXPR = "parenthesis_expr";
	public static final String TAG_NULL_EXPR = "null_expr";
	public static final String TAG_ROW_CONSTRUCTOR_EXPR = "row_constructor_expr";
	public static final String TAG_ARRAY_CONSTRUCTOR_EXPR = "array_constructor_expr";
	public static final String TAG_CASE_EXPR = "case_expr";
	public static final String TAG_ASSIGNMENT_EXPR = "assignment_expr";
	public static final String TAG_UNKNOWN_EXPR = "unknown_expr";

	public static final String TAG_EXPRESSION = "expression";
	public static final String TAG_OBJECTNAME = "objectName";
	public static final String TAG_FULLNAME = "full_name";
	public static final String TAG_LITERAL = "literal";
	public static final String TAG_STATEMENT_LIST = "statement_list";

	public static final int TOP_STATEMENT = 1;

	public static final String ATTR_EXPR_TYPE = "expr_type";

	private Element e_sqlScript = null;
	private Document xmldoc = null;
	private StringBuffer buffer = null;
	private int level = 0;
	private Element e_parent = null;
	private Stack elementStack = null;
	private String current_expression_tag = null;
	private String current_expression_list_tag = null;
	private String current_objectName_tag = null;
	private String current_statement_list_tag = null;
	private String current_objectName_list_tag = null;
	private String current_query_expression_tag = null;
	private String current_functionCall_tag = null;
	private String current_table_reference_tag = null;
	private String current_join_table_reference_tag = null;
	private String current_parameter_declaration_tag = null;
	private String current_datatype_tag = null;
	private EDbVendor dbVendor;

	public void run( TGSqlParser sqlParser )
	{
		dbVendor = sqlParser.getDbVendor( );
		e_sqlScript = xmldoc.createElement( "sqlscript" );
		e_sqlScript.setAttribute( "dbvendor", sqlParser.getDbVendor( )
				.toString( ) );
		e_sqlScript.setAttribute( "stmt_count",
				String.valueOf( sqlParser.sqlstatements.size( ) ) );
		e_sqlScript.setAttribute( "xmlns",
				"http://www.sqlparser.com/xml/sqlschema/1.0" );
		e_sqlScript.setAttribute( "xmlns:xsi",
				"http://www.w3.org/2001/XMLSchema-instance" );
		e_sqlScript.setAttribute( "xsi:schemaLocation",
				"http://www.sqlparser.com/xml/sqlschema/1.0 " + xsdfile );
		xmldoc.appendChild( e_sqlScript );

		Element e_statement = null;
		elementStack.push( e_sqlScript );
		for ( int i = 0; i < sqlParser.sqlstatements.size( ); i++ )
		{
			e_statement = xmldoc.createElement( "statement" );
			e_statement.setAttribute( "type",
					sqlParser.sqlstatements.get( i ).sqlstatementtype.toString( ) );
			e_sqlScript.appendChild( e_statement );
			elementStack.push( e_statement );
			sqlParser.sqlstatements.get( i ).setDummyTag( TOP_STATEMENT );
			sqlParser.sqlstatements.get( i ).accept( this );
			elementStack.pop( );
		}
		elementStack.pop( );
	}

	public boolean validXml( )
	{

		boolean result = false;

		try
		{
			Source xmlFile = null;
			try
			{
				URL schemaFile = new URL( xsdfile );
				InputStream stream = new ByteArrayInputStream( getFormattedXml( ).toString( )
						.trim( )
						.getBytes( ) );
				xmlFile = new StreamSource( stream );
				SchemaFactory schemaFactory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
				Schema schema = schemaFactory.newSchema( schemaFile );
				Validator validator = schema.newValidator( );
				validator.validate( xmlFile );
				result = true;
				// System.out.println(xmlFile.getSystemId() + " is valid");
			}
			catch ( SAXException e )
			{

				System.out.println( xmlFile.getSystemId( ) + " is NOT valid" );
				System.out.println( "Reason: " + e.getLocalizedMessage( ) );
			}

		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}

		return result;
	}

	public String getFormattedXml( )
	{
		try
		{
			return format( xmldoc, 2 );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}

		return "";
	}

	public void writeToFile( String filename )
	{

		try
		{
			Writer out = new OutputStreamWriter( new FileOutputStream( filename ) );
			try
			{
				out.write( getFormattedXml( ) );
			}
			finally
			{
				out.close( );
			}

			// // write(testFile,buffer.toString());
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}

	}

	public final static String crlf = "\r\n";
	StringBuilder sb;
	private String xsdfile = null;

	private int sequenceId = 0;

	protected String getSequenceId(){
		sequenceId++;
		return   Integer.toString(sequenceId);
	}

	public xmlVisitor( String pXsdfile )
	{
		sb = new StringBuilder( 1024 );
		buffer = new StringBuffer( 1024 );
		elementStack = new Stack( );

		xsdfile = pXsdfile;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance( );
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder( );
			xmldoc = builder.newDocument( );
			xmldoc.setXmlVersion( "1.0" );

		}
		catch ( ParserConfigurationException e )
		{
			e.printStackTrace( );
		}

	}

	void appendEndTag( String tagName )
	{
		sb.append( String.format( "</%s>" + crlf, tagName ) );
	}

	void appendStartTag( String tagName )
	{
		sb.append( String.format( "<%s>" + crlf, tagName ) );
	}

	String getTagName( TParseTreeNode node )
	{
		return node.getClass( ).getSimpleName( );
	}

	void appendStartTag( TParseTreeNode node )
	{

		if ( node instanceof TStatementList )
		{
			appendStartTagWithCount( node, ( (TStatementList) node ).size( ) );
		}
		else if ( node instanceof TParseTreeNodeList )
		{
			appendStartTagWithCount( node, ( (TParseTreeNodeList) node ).size( ) );
		}
		else
		{
			sb.append( String.format( "<%s>" + crlf, getTagName( node ) ) );
		}
	}

	void appendStartTagWithIntProperty( TParseTreeNode node,
			String propertyName, int propertyValue )
	{
		sb.append( String.format( "<%s " + propertyName + "='%d'>" + crlf,
				getTagName( node ),
				propertyValue ) );
	}

	void appendStartTagWithIntProperty( TParseTreeNode node,
			String propertyName, EExpressionType propertyValue )
	{
		sb.append( String.format( "<%s " + propertyName + "='%s'>" + crlf,
				getTagName( node ),
				propertyValue.name( ) ) );
	}

	void appendStartTagWithIntProperty( TParseTreeNode node,
			String propertyName, String propertyValue )
	{
		sb.append( String.format( "<%s " + propertyName + "='%s'>" + crlf,
				getTagName( node ),
				propertyValue ) );
	}

	void appendStartTagWithIntProperty( TParseTreeNode node,
			String propertyName, int propertyValue, String propertyName2,
			String propertyValue2 )
	{
		sb.append( String.format( "<%s "
				+ propertyName
				+ "='%d' "
				+ propertyName2
				+ "='%s'"
				+ ">"
				+ crlf, getTagName( node ), propertyValue, propertyValue2 ) );
	}

	void appendStartTagWithIntProperty( TParseTreeNode node,
			String propertyName, String propertyValue, String propertyName2,
			String propertyValue2 )
	{
		sb.append( String.format( "<%s "
				+ propertyName
				+ "='%s' "
				+ propertyName2
				+ "='%s'"
				+ ">"
				+ crlf, getTagName( node ), propertyValue, propertyValue2 ) );
	}

	void appendStartTagWithIntProperty( TParseTreeNode node,
			String propertyName, String propertyValue, String propertyName2,
			String propertyValue2, String propertyName3, String propertyValue3 )
	{
		sb.append( String.format( "<%s "
				+ propertyName
				+ "='%s' "
				+ propertyName2
				+ "='%s' "
				+ propertyName3
				+ "='%s'"
				+ ">"
				+ crlf,
				getTagName( node ),
				propertyValue,
				propertyValue2,
				propertyValue3 ) );
	}

	void appendStartTagWithProperties( TParseTreeNode node,
			String propertyName, String propertyValue, String propertyName2,
			String propertyValue2 )
	{
		sb.append( String.format( "<%s "
				+ propertyName
				+ "='%s' "
				+ propertyName2
				+ "='%s'"
				+ ">"
				+ crlf, getTagName( node ), propertyValue, propertyValue2 ) );
	}

	void appendEndTag( TParseTreeNode node )
	{
		sb.append( String.format( "</%s>" + crlf, getTagName( node ) ) );
	}

	void appendStartTagWithCount( TParseTreeNode node, int count )
	{
		appendStartTagWithIntProperty( node, "size", count );
	}

	// process parse tree nodes



	public void preVisit( TDb2HandlerDeclaration stmt ){
		e_parent = (Element) elementStack.peek( );
		Element e_declare = xmldoc.createElement( "declare_statement" );
		e_parent.appendChild( e_declare );
		elementStack.push( e_declare );

		stmt.getStmt().accept(this);

		elementStack.pop( );
	}

	public void preVisit( TSignalStmt stmt ){
		e_parent = (Element) elementStack.peek( );
		Element e_signal_stmt = xmldoc.createElement( "signal_statement" );
		e_signal_stmt.setAttribute("is_resignal",(stmt.getStartToken().astext.equalsIgnoreCase("resignal"))?"yes":"no");
		e_parent.appendChild( e_signal_stmt );
		elementStack.push( e_signal_stmt );
		if (stmt.getStateValue() != null){
			addElementOfNode("state_value",stmt.getStateValue());
		}else if (stmt.getConditionName() != null){
			current_objectName_tag = "condition_name";
			stmt.getConditionName().accept(this);
		}
		if (stmt.getSignalInformations() != null){
			addElementOfNode("signal_informations",stmt.getSignalInformations());
		}

		elementStack.pop( );
	}

	public void preVisit( TIterateStmt stmt ){
		e_parent = (Element) elementStack.peek( );
		Element e_iterate_stmt = xmldoc.createElement( "iterate_statement" );
		e_parent.appendChild( e_iterate_stmt );
		elementStack.push( e_iterate_stmt );

		addElementOfNode("label_name", stmt.getLabelName());

		elementStack.pop( );
	}

	public void preVisit( TDb2SetVariableStmt stmt ){
		e_parent = (Element) elementStack.peek( );
		Element e_set_varaible = xmldoc.createElement( "set_variable_statement" );
		e_parent.appendChild( e_set_varaible );
		elementStack.push( e_set_varaible );
		for(int i=0;i<stmt.getAssignments().size();i++) {
			TSetAssignment assignStmt = (TSetAssignment)stmt.getAssignments().getElement(i);
			assignStmt.accept(this);
		}
		elementStack.pop( );
	}

	public void preVisit( TSetAssignment stmt ){
		e_parent = (Element) elementStack.peek( );
		Element e_set_assignment = xmldoc.createElement( "set_assignment" );
		e_parent.appendChild( e_set_assignment );
		elementStack.push( e_set_assignment );
		stmt.getParameterName().accept(this);
		stmt.getParameterValue().accept(this);
		elementStack.pop( );
	}

	public void preVisit( TSetStmt stmt ){
		e_parent = (Element) elementStack.peek( );
		Element e_set_assignment = xmldoc.createElement( "set_statement" );
		e_parent.appendChild( e_set_assignment );
		elementStack.push( e_set_assignment );
		if (stmt.getAssignments() != null){
			for(int i=0;i<stmt.getAssignments().size();i++) {
				TSetAssignment assignStmt = (TSetAssignment)stmt.getAssignments().getElement(i);
				assignStmt.accept(this);
			}
		}
		if (stmt.getVariableName() != null){
			stmt.getVariableName().accept(this);
		}
		if (stmt.getVariableNameList() != null){
			stmt.getVariableNameList().accept(this);
		}
		if (stmt.getVariableValue() != null){
			stmt.getVariableValue().accept(this);
		}
		if (stmt.getVariableValueList() != null){
			stmt.getVariableValueList().accept(this);
		}
		elementStack.pop( );
	}



	public void preVisit( TConstant node )
	{
		Element e_literal = xmldoc.createElement( TAG_LITERAL );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_literal );
		e_literal.setAttribute("type",node.getLiteralType().toString());

		Element e_value = xmldoc.createElement( "value" );
		e_literal.appendChild( e_value );
		e_value.setTextContent( node.toString( ) );
		// e_literal.
		// appendStartTag(node);
		// sb.append(node.toString());
	}

	public void postVisit( TConstant node )
	{
		appendEndTag( node );
	}

	public void preVisit( TSelectDistinct node )
	{
		Element e_distinct_clause = xmldoc.createElement( "distinct_clause" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_distinct_clause );
		elementStack.push( e_distinct_clause );
		e_distinct_clause.setAttribute( "distinct_type",
				node.getUniqueRowFilter( ).toString( ) );

		if ( node.getExpressionList( ) != null )
		{
			for ( int i = 0; i < node.getExpressionList( ).size( ); i++ )
			{
				node.getExpressionList( ).getExpression( i ).accept( this );
			}
		}

		elementStack.pop( );
	}

	public void preVisit( TTopClause node )
	{
		Element e_top_clause = xmldoc.createElement( "top_clause" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_top_clause );
		elementStack.push( e_top_clause );
		e_top_clause.setAttribute( "percent",
				String.valueOf( node.isPercent( ) ) );
		e_top_clause.setAttribute( "with_ties",
				String.valueOf( node.isWithties( ) ) );

		if ( node.getExpr( ) != null )
		{
			node.getExpr( ).accept( this );
		}

		elementStack.pop( );
	}

	public void preVisit( TIntoClause node )
	{
		Element e_top_clause = xmldoc.createElement( "into_clause" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_top_clause );
		elementStack.push( e_top_clause );

		if ( node.getExprList( ) != null )
		{
			node.getExprList( ).accept( this );
		}

		elementStack.pop( );
	}

	public void postVisit( TTopClause node )
	{
		appendEndTag( node );
	}


	public void preVisit( TValueClause node ){
		Element e_value_clause = xmldoc.createElement( "value_clause" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_value_clause );
		elementStack.push( e_value_clause );

		for(int i=0;i<node.getRows().size();i++){
			Element e_value_rows = xmldoc.createElement( "value_row" );
			e_value_clause.appendChild(e_value_rows);
			elementStack.push(e_value_rows);
			node.getRows().get(i).accept(this);
			elementStack.pop();
		}

		if ( node.getNameList( ) != null )
		{
			Element e_into_names = xmldoc.createElement( "into_names" );
			e_value_clause.appendChild(e_into_names);
			elementStack.push(e_into_names);
			node.getNameList( ).accept( this );
			elementStack.pop();
		}

		elementStack.pop( );
	}

	public void preVisit( TValueRowItem node ){
		Element e_valueRow_item = xmldoc.createElement( "value_item" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_valueRow_item );
		elementStack.push( e_valueRow_item );
		if (node.getExpr() != null){
			node.getExpr().accept(this);
		}else if (node.getExprList() != null){
			node.getExprList().accept(this);
		}
		elementStack.pop( );
	}

	public void preVisit( TSelectSqlStatement node )
	{
		// sb.append(String.format("<%s setOperator='%d'>"+crlf,getTagName(node),node.getSetOperator())
		// );
		// appendStartTagWithProperties(node,
		// "setOperator",node.getSetOperatorType().toString()
		// , "isAll", String.valueOf(node.isAll())) ;

		Element e_query_expression;

		boolean cteVisited = false;

		if ( node.getDummyTag( ) == TOP_STATEMENT )
		{
			// if (elementStack.size() == 2){ // tag stack: sqlscript/statement
			e_parent = (Element) elementStack.peek( );
			Element e_select = xmldoc.createElement( "select_statement" );
			e_parent.appendChild( e_select );
			elementStack.push( e_select );

			if ( node.getCteList( ) != null )
			{
				node.getCteList( ).accept( this );
				cteVisited = true;
			}
		}

		String query_expression_tag = "query_expression";
		if ( current_query_expression_tag != null )
		{
			query_expression_tag = current_query_expression_tag;
			current_query_expression_tag = null;
		}
		e_parent = (Element) elementStack.peek( );
		e_query_expression = xmldoc.createElement( query_expression_tag );
		e_query_expression.setAttribute( "is_parenthesis",
				String.valueOf( node.getParenthesisCount( ) > 0 ) );
		e_parent.appendChild( e_query_expression );
		elementStack.push( e_query_expression );

		if (( node.getCteList( ) != null ) && (!cteVisited))
		{
			node.getCteList( ).accept( this );
		}

		if ( node.isCombinedQuery( ) )
		{

			Element e_binary_query_expression = xmldoc.createElement( "binary_query_expression" );
			e_binary_query_expression.setAttribute( "set_operator",
					node.getSetOperatorType( ).toString( ) );
			e_binary_query_expression.setAttribute( "is_all",
					String.valueOf( node.isAll( ) ) );

			e_query_expression.appendChild( e_binary_query_expression );
			elementStack.push( e_binary_query_expression );
			current_query_expression_tag = "first_query_expression";
			node.getLeftStmt( ).accept( this );
			current_query_expression_tag = "second_query_expression";
			node.getRightStmt( ).accept( this );
			elementStack.pop( );

			if ( node.getOrderbyClause( ) != null )
			{
				node.getOrderbyClause( ).accept( this );
			}

			if ( node.getLimitClause( ) != null )
			{
				node.getLimitClause( ).accept( this );
			}

			if ( node.getForUpdateClause( ) != null )
			{
				node.getForUpdateClause( ).accept( this );
			}

			if ( node.getComputeClause( ) != null )
			{
				node.getComputeClause( ).accept( this );
			}

			// this.postVisit(node);
			elementStack.pop( );

			return;
		}

		e_parent = (Element) elementStack.peek( );

		Element e_query_specification = xmldoc.createElement( "query_specification" );
		e_parent.appendChild( e_query_specification );
		e_parent = e_query_specification;
		elementStack.push( e_query_specification );

		// if (node.getCteList() != null){
		// node.getCteList().accept(this);
		// }

		if ( node.getValueClause( ) != null )
		{
			// DB2 values constructor
			node.getValueClause().accept(this);

			elementStack.pop( ); // query specification
			elementStack.pop( ); // query expression

			return;
		}
		if ( node.getTopClause( ) != null )
		{
			node.getTopClause( ).accept( this );
		}

		if ( node.getSelectDistinct( ) != null )
		{
			node.getSelectDistinct( ).accept( this );
		}

		if ( node.getResultColumnList( ) != null )
		{
			Element e_select_list = xmldoc.createElement( "select_list" );
			e_parent.appendChild( e_select_list );
			elementStack.push( e_select_list );
			for ( int i = 0; i < node.getResultColumnList( ).size( ); i++ )
			{
				node.getResultColumnList( ).getElement( i ).accept( this );
			}
			elementStack.pop( );
		}
		else
		{
			// hive transform clause with no select list
		}

		if ( node.getIntoClause( ) != null )
		{
			node.getIntoClause( ).accept( this );
		}

		if ( node.joins.size( ) > 0 )
		{
			Element e_from_clause = xmldoc.createElement( "from_clause" );
			e_parent = (Element) elementStack.peek( );
			e_parent.appendChild( e_from_clause );
			elementStack.push( e_from_clause );
			node.joins.accept( this );
			elementStack.pop( );
		}

		if ( node.getWhereClause( ) != null )
		{
			node.getWhereClause( ).accept( this );
		}

		if ( node.getHierarchicalClause( ) != null )
		{
			node.getHierarchicalClause( ).accept( this );
		}

		if ( node.getGroupByClause( ) != null )
		{
			node.getGroupByClause( ).accept( this );
		}

		if ( node.getQualifyClause( ) != null )
		{
			node.getQualifyClause( ).accept( this );
		}

		if ( node.getOrderbyClause( ) != null )
		{
			node.getOrderbyClause( ).accept( this );
		}

		if ( node.getLimitClause( ) != null )
		{
			node.getLimitClause( ).accept( this );
		}

		if ( node.getForUpdateClause( ) != null )
		{
			node.getForUpdateClause( ).accept( this );
		}

		if ( node.getComputeClause( ) != null )
		{
			node.getComputeClause( ).accept( this );
		}

		elementStack.pop( ); // query specification
		elementStack.pop( ); // query expression

	}

	public void postVisit( TSelectSqlStatement node )
	{
		if ( node.getDummyTag( ) == TOP_STATEMENT )
		{
			// if (elementStack.size() == 3) {
			Element tmp = (Element) elementStack.peek( );
			if ( tmp.getTagName( ).equalsIgnoreCase( "select_statement" ) )
				elementStack.pop( ); // tag stack:
										// sqlscript/statement/select_statement
		}
		appendEndTag( node );
	}

	public void preVisit( TResultColumnList node )
	{
		appendStartTag( node );
		for ( int i = 0; i < node.size( ); i++ )
		{
			node.getResultColumn( i ).accept( this );
		}
	}

	public void postVisit( TResultColumnList node )
	{
		appendEndTag( node );
	}

	public void preVisit( TResultColumn node )
	{
		// appendStartTag(node);
		e_parent = (Element) elementStack.peek( );
		Element e_result_column = xmldoc.createElement( "result_column" );
		e_parent.appendChild( e_result_column );
		elementStack.push( e_result_column );

		node.getExpr( ).accept( this );
		if ( node.getAliasClause( ) != null )
		{
			node.getAliasClause( ).accept( this );
		}
		elementStack.pop( );
	}

	public void postVisit( TResultColumn node )
	{

		// appendEndTag(node);
	}

	public void preVisit( TExpression node )
	{
		String tag_name = TAG_EXPRESSION;
		if ( current_expression_tag != null )
		{
			tag_name = current_expression_tag;
			current_expression_tag = null;
		}
		Element e_expression = xmldoc.createElement( tag_name );

		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_expression );
		elementStack.push( e_expression );
		e_expression.setAttribute( ATTR_EXPR_TYPE, node.getExpressionType( )
				.toString( ) );

		switch ( node.getExpressionType( ) )
		{
			case simple_object_name_t :
				e_expression = xmldoc.createElement( TAG_COLUMN_REFERENCED_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				elementStack.push( e_expression );
				current_objectName_tag = null;
				node.getObjectOperand( ).accept( this );
				elementStack.pop( );
				break;
			case simple_constant_t :
				e_expression = xmldoc.createElement( TAG_CONSTANT_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				elementStack.push( e_expression );
				node.getConstantOperand( ).accept( this );
				elementStack.pop( );
				break;
			case new_structured_type_t :
			case type_constructor_t :
			case function_t :
				e_expression = xmldoc.createElement( TAG_FUNCTIONCALL_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				elementStack.push( e_expression );
				node.getFunctionCall( ).accept( this );
				elementStack.pop( );
				break;
			case cursor_t :
			case multiset_t :
			case subquery_t :
				e_expression = xmldoc.createElement( TAG_SUBQUERY_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				elementStack.push( e_expression );
				node.getSubQuery( ).accept( this );
				elementStack.pop( );
				break;
			case exists_t :
				e_expression = xmldoc.createElement( TAG_EXISTS_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				elementStack.push( e_expression );
				node.getSubQuery( ).accept( this );
				elementStack.pop( );
				break;
			case assignment_t :
				e_expression = xmldoc.createElement( TAG_ASSIGNMENT_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				elementStack.push( e_expression );
				current_expression_tag = "first_expr";
				node.getLeftOperand( ).accept( this );
				current_expression_tag = "second_expr";
				node.getRightOperand( ).accept( this );
				elementStack.pop( );
				break;
			case simple_comparison_t :
				e_expression = xmldoc.createElement( TAG_COMPARISON_EXPR );
				e_expression.setAttribute( "type", node.getComparisonOperator( )
						.toString( ) );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				elementStack.push( e_expression );

				if ( node.getSubQuery( ) != null )
				{
					node.getExprList( ).accept( this );
					node.getSubQuery( ).accept( this );
				}
				else
				{
					current_expression_tag = "first_expr";
					node.getLeftOperand( ).accept( this );
					current_expression_tag = "second_expr";
					node.getRightOperand( ).accept( this );
				}
				elementStack.pop( );
				break;
			case group_comparison_t :
				e_expression = xmldoc.createElement( TAG_COMPARISON_EXPR );
				e_expression.setAttribute( "type", node.getComparisonOperator( )
						.toString( ) );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				elementStack.push( e_expression );

				if ( node.getQuantifier( ) != null )
				{
					e_expression.setAttribute( "quantifier",
							node.getQuantifier( ).toString( ) );
				}

				current_expression_tag = "first_expr";
				if ( node.getExprList( ) != null )
				{
					node.getExprList( ).accept( this );
				}
				else
				{
					node.getLeftOperand( ).accept( this );
				}

				current_expression_tag = "second_expr";
				node.getRightOperand( ).accept( this );
				elementStack.pop( );
				break;
			case in_t :
				e_expression = xmldoc.createElement( TAG_IN_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				e_expression.setAttribute( "not",
						( node.getNotToken( ) != null ) ? "true" : "false" );
				elementStack.push( e_expression );
				current_expression_tag = "first_expr";
				if ( node.getExprList( ) != null )
				{
					node.getExprList( ).accept( this );
				}
				else
				{
					node.getLeftOperand( ).accept( this );
				}

				current_expression_tag = "second_expr";
				node.getRightOperand( ).accept( this );
				elementStack.pop( );
				break;
			case collection_constructor_list_t :
			case collection_constructor_multiset_t :
			case collection_constructor_set_t :
			case list_t :
				e_expression = xmldoc.createElement( TAG_LIST_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				elementStack.push( e_expression );

				if ( node.getExprList( ) != null )
				{
					node.getExprList( ).accept( this );
				}
				elementStack.pop( );
				break;
			case pattern_matching_t :
				e_expression = xmldoc.createElement( TAG_LIKE_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				elementStack.push( e_expression );
				current_expression_tag = "first_expr";
				node.getLeftOperand( ).accept( this );
				current_expression_tag = "second_expr";
				node.getRightOperand( ).accept( this );
				if ( node.getLikeEscapeOperand( ) != null )
				{
					current_expression_tag = "third_expr";
					node.getLikeEscapeOperand( ).accept( this );
				}
				elementStack.pop( );
				break;
			case between_t :
				e_expression = xmldoc.createElement( TAG_BETWEEN_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				elementStack.push( e_expression );
				current_expression_tag = "first_expr";
				node.getBetweenOperand( ).accept( this );
				current_expression_tag = "second_expr";
				node.getLeftOperand( ).accept( this );
				current_expression_tag = "third_expr";
				node.getRightOperand( ).accept( this );
				elementStack.pop( );
				break;
			case logical_not_t :
				e_expression = xmldoc.createElement( TAG_NOT_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				elementStack.push( e_expression );
				current_expression_tag = "first_expr";
				node.getRightOperand( ).accept( this );
				elementStack.pop( );
				break;
			case null_t :
				e_expression = xmldoc.createElement( TAG_NULL_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				e_expression.setAttribute( "not",
						( node.getNotToken( ) != null ) ? "true" : "false" );
				elementStack.push( e_expression );
				current_expression_tag = "first_expr";
				node.getLeftOperand( ).accept( this );
				elementStack.pop( );
				break;
			case parenthesis_t :
				e_expression = xmldoc.createElement( TAG_PARENTHESIS_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				elementStack.push( e_expression );
				current_expression_tag = "first_expr";
				node.getLeftOperand( ).accept( this );
				elementStack.pop( );
				break;
			case at_local_t :
			case day_to_second_t :
			case year_to_month_t :
			case floating_point_t :
			case is_of_type_t :
			case unary_factorial_t :
				e_expression = xmldoc.createElement( TAG_UNARY_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				elementStack.push( e_expression );
				current_expression_tag = "first_expr";
				node.getLeftOperand( ).accept( this );
				elementStack.pop( );
				break;
			case typecast_t :
				e_expression = xmldoc.createElement( TAG_UNARY_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				elementStack.push( e_expression );
				current_expression_tag = "first_expr";
				node.getLeftOperand( ).accept( this );
				addElementOfNode("datatype",node.getTypeName());
				elementStack.pop( );
				break;
			case unary_plus_t :
			case unary_minus_t :
			case unary_prior_t :
			case unary_connect_by_root_t :
			case unary_binary_operator_t :
			case unary_squareroot_t :
			case unary_cuberoot_t :
			case unary_factorialprefix_t :
			case unary_absolutevalue_t :
			case unary_bitwise_not_t :
				e_expression = xmldoc.createElement( TAG_UNARY_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				elementStack.push( e_expression );
				current_expression_tag = "first_expr";
				node.getRightOperand( ).accept( this );
				elementStack.pop( );
				break;
			case arithmetic_plus_t :
			case arithmetic_minus_t :
			case arithmetic_times_t :
			case arithmetic_divide_t :
			case power_t :
			case range_t :
			case concatenate_t :
			case period_ldiff_t :
			case period_rdiff_t :
			case period_p_intersect_t :
			case period_p_normalize_t :
			case contains_t :
			case arithmetic_modulo_t :
			case bitwise_exclusive_or_t :
			case bitwise_or_t :
			case bitwise_and_t :
			case bitwise_xor_t :
			case exponentiate_t :
			case scope_resolution_t :
			case at_time_zone_t :
			case member_of_t :
			case logical_and_t :
			case logical_or_t :
			case logical_xor_t :
			case is_t :
			case collate_t :
			case left_join_t :
			case right_join_t :
			case ref_arrow_t :
			case left_shift_t :
			case right_shift_t :
			case bitwise_shift_left_t :
			case bitwise_shift_right_t :
			case multiset_union_t :
			case multiset_union_distinct_t :
			case multiset_intersect_t :
			case multiset_intersect_distinct_t :
			case multiset_except_t :
			case multiset_except_distinct_t :
			case json_get_text :
			case json_get_text_at_path :
			case json_get_object :
			case json_get_object_at_path :
			case json_left_contain :
			case json_right_contain :
			case json_exist :
			case json_any_exist :
			case json_all_exist :
			case sqlserver_proprietary_column_alias_t :
			case submultiset_t :
				e_expression = xmldoc.createElement( TAG_BINARY_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				if ( node.getOperatorToken( ) != null )
				{
					e_expression.setAttribute( "operator",
							node.getOperatorToken( ).toString( ) );
				}
				elementStack.push( e_expression );
				current_expression_tag = "first_expr";
				node.getLeftOperand( ).accept( this );
				current_expression_tag = "second_expr";
				node.getRightOperand( ).accept( this );
				elementStack.pop( );
				break;
			case row_constructor_t :
				e_expression = xmldoc.createElement( TAG_ROW_CONSTRUCTOR_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				elementStack.push( e_expression );

				if ( node.getExprList( ) != null )
				{
					node.getExprList( ).accept( this );
				}
				elementStack.pop( );
				break;
			case array_constructor_t :
				e_expression = xmldoc.createElement( TAG_ARRAY_CONSTRUCTOR_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				elementStack.push( e_expression );
				if ( node.getSubQuery( ) != null )
				{
					node.getSubQuery( ).accept( this );
				}

				if ( node.getExprList( ) != null )
				{
					node.getExprList( ).accept( this );
				}
				elementStack.pop( );
				break;
			case case_t :
				node.getCaseExpression( ).accept( this );
				break;
			case arrayaccess_t :
				e_expression = xmldoc.createElement( TAG_UNKNOWN_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				e_expression.setTextContent( node.toString( ) );
				// node.getArrayAccess().accept(this);
				break;
			case interval_t :
				e_expression = xmldoc.createElement( TAG_UNKNOWN_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				e_expression.setTextContent( node.toString( ) );
				// node.getIntervalExpr().accept(this);
				break;
			case array_access_expr_t : // hive
				e_expression = xmldoc.createElement( "array_access_expr_t" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				elementStack.push( e_expression );

				Element e_precedenceField = xmldoc.createElement( "precedence_field" );
				e_expression.appendChild( e_precedenceField );
				elementStack.push( e_precedenceField );
				node.getLeftOperand( ).accept( this );
				elementStack.pop( );

				Element e_index = xmldoc.createElement( "array_index" );
				e_expression.appendChild( e_index );
				elementStack.push( e_index );
				node.getRightOperand( ).accept( this );
				elementStack.pop( );

				elementStack.pop( );
				break;
			default :
				e_expression = xmldoc.createElement( TAG_UNKNOWN_EXPR );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_expression );
				e_expression.setTextContent( node.toString( ) );
				break;
		}

		if ((node.getDataConversions()!=null)&&(node.getDataConversions().size()>0)){
			//addElementOfString("data_conversion",node.getDataConversions().toString());
			Element e_data_conversion = xmldoc.createElement( "data_conversions" );
			e_expression.appendChild( e_data_conversion );
			elementStack.push( e_data_conversion );
			for(int i=0;i<node.getDataConversions().size();i++){
				node.getDataConversions().get(i).accept(this);
			}
			//node.getDataConversions( ).accept( this );
			elementStack.pop( );
		}

		elementStack.pop( );
	}

	public void preVisit( TDataConversion node )
	{
		Element e_data_conversion = xmldoc.createElement( "data_conversion" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_data_conversion );
		elementStack.push( e_data_conversion );
		for(int i=0;i<node.getDataConversionItems().size();i++){
			node.getDataConversionItems().get(i).accept(this);
		}
		elementStack.pop( );
	}

	public void preVisit( TDataConversionItem node )
	{
		Element e_data_conversion = xmldoc.createElement( "data_conversion_item" );
		e_data_conversion.setAttribute( "type", node.getDataConversionType().toString() );

		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_data_conversion );
		elementStack.push( e_data_conversion );
		switch (node.getDataConversionType()){
			case dataType:
				node.getDataType().accept(this);
				break;
			case dataAttribute:
				node.getDatatypeAttribute().accept(this);
				break;
		}
		elementStack.pop( );
	}

	public void preVisit( TDatatypeAttribute node )
	{
		Element e_datatype_attribute = xmldoc.createElement( "datatype_attribute" );
		e_datatype_attribute.setAttribute( "type", node.getAttributeType().toString() );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_datatype_attribute );
		elementStack.push( e_datatype_attribute );
		addElementOfString("datatypeAttribute",node.toString());
		elementStack.pop( );
	}

	public void postVisit( TExpression node )
	{
		appendEndTag( node );
	}

	public void preVisit( TAliasClause node )
	{
		Element e_alias_clause = xmldoc.createElement( "alias_clause" );
		e_alias_clause.setAttribute( "with_as",
				( node.getAsToken( ) != null ) ? "true" : "false" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_alias_clause );
		elementStack.push( e_alias_clause );
		current_objectName_tag = "object_name";
		node.getAliasName( ).accept( this );
		current_objectName_tag = null;
		if (node.getColumns() != null){
			Element e_columns = xmldoc.createElement( "columns" );
			e_alias_clause.appendChild( e_columns );
			elementStack.push(e_columns);
			node.getColumns().acceptChildren(this);
			elementStack.pop();
		}
		elementStack.pop( );
		// sb.append(node.toString());
	}

	public void preVisit( TInExpr node )
	{
		appendStartTag( node );
		if ( node.getSubQuery( ) != null )
		{
			node.getSubQuery( ).accept( this );
		}
		else if ( node.getGroupingExpressionItemList( ) != null )
		{
			node.getGroupingExpressionItemList( ).accept( this );
		}
		else
		{
			sb.append( node.toString( ) );
		}
	}

	public void postVisit( TInExpr node )
	{
		appendEndTag( node );
	}

	public void preVisit( TExpressionList node )
	{
		// appendStartTag(node);
		Element e_expression_list;
		e_parent = (Element) elementStack.peek( );
		if ( current_expression_list_tag == null )
		{
			e_expression_list = xmldoc.createElement( "expression_list" );
		}
		else
		{
			e_expression_list = xmldoc.createElement( current_expression_list_tag );
			current_expression_list_tag = null;
		}

		e_parent.appendChild( e_expression_list );

		elementStack.push( e_expression_list );

		for ( int i = 0; i < node.size( ); i++ )
		{
			node.getExpression( i ).accept( this );
		}
		elementStack.pop( );
	}

	public void postVisit( TExpressionList node )
	{
		appendEndTag( node );
	}

	public void preVisit( TGroupingExpressionItem node )
	{
		appendStartTag( node );
		if ( node.getExpr( ) != null )
		{
			node.getExpr( ).accept( this );
		}
		else if ( node.getExprList( ) != null )
		{
			node.getExprList( ).accept( this );
		}
	}

	public void postVisit( TGroupingExpressionItem node )
	{
		appendEndTag( node );
	}

	public void preVisit( TGroupingExpressionItemList node )
	{
		appendStartTag( node );
	}

	public void postVisit( TGroupingExpressionItemList node )
	{
		appendEndTag( node );
	}

	public void postVisit( TAliasClause node )
	{
		appendEndTag( node );
	}

	public void preVisit( TJoin node )
	{

		if ( ( node.getJoin( ) != null )
				&& ( node.getJoinItems( ).size( ) == 0 ) )
		{
			// SELECT ALL A.CATEGORYNAME,
			// sum(B.UNITSONORDER)
			// FROM (NORTHWIND.CATEGORIES A
			// INNER JOIN NORTHWIND.PRODUCTS B
			// ON A.CATEGORYID=B.CATEGORYID)
			preVisit( node.getJoin( ) );
			return;
		}
		switch ( node.getKind( ) )
		{
			case TBaseType.join_source_fake :
				node.getTable( ).accept( this );
				break;
			case TBaseType.join_source_table :
			case TBaseType.join_source_join :

				String tag_name = "table_reference";

				if ( current_join_table_reference_tag != null )
				{
					tag_name = current_join_table_reference_tag;
					current_join_table_reference_tag = null;
				}

				Element e_table_reference = xmldoc.createElement( tag_name );
				e_table_reference.setAttribute( "type", "join" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_table_reference );
				elementStack.push( e_table_reference );

				int nest = 0;
				for ( int i = node.getJoinItems( ).size( ) - 1; i >= 0; i-- )
				{
					TJoinItem joinItem = node.getJoinItems( ).getJoinItem( i );
					Element e_joined_table_reference = xmldoc.createElement( "joined_table" );
					e_joined_table_reference.setAttribute( "type",
							joinItem.getJoinType( ).toString( ) );
					e_parent = (Element) elementStack.peek( );
					e_parent.appendChild( e_joined_table_reference );
					elementStack.push( e_joined_table_reference );
					nest++;

					Element e_first_table_reference = null;
					if ( i != 0 )
					{
						e_first_table_reference = xmldoc.createElement( "first_table_reference" );
						e_first_table_reference.setAttribute( "type", "join" );
						e_parent = (Element) elementStack.peek( );
						e_parent.appendChild( e_first_table_reference );

					}
					else
					{
						if ( node.getKind( ) == TBaseType.join_source_table )
						{
							current_table_reference_tag = "first_table_reference";
							node.getTable( ).accept( this );
						}
						else if ( node.getKind( ) == TBaseType.join_source_join )
						{
							current_join_table_reference_tag = "first_table_reference";
							preVisit( node.getJoin( ) );
						}
					}

					if ( joinItem.getTable( ) != null )
					{
						current_table_reference_tag = "second_table_reference";
						joinItem.getTable( ).accept( this );
					}
					else if ( joinItem.getJoin( ) != null )
					{
						current_join_table_reference_tag = "second_table_reference";
						preVisit( joinItem.getJoin( ) );
					}

					if ( joinItem.getOnCondition( ) != null )
					{
						current_expression_tag = "join_condition";
						joinItem.getOnCondition( ).accept( this );
					}

					if ( i != 0 )
					{
						elementStack.push( e_first_table_reference );
						nest++;
					}

				}

				for ( int i = 0; i < nest; i++ )
				{
					elementStack.pop( );
				}

				elementStack.pop( ); // e_table_reference
				break;
		// case TBaseType.join_source_join:
		// node.getJoin().accept(this);
		// node.getJoinItems().accept(this);
		// break;
		}

		// if (node.getAliasClause() != null){
		// node.getAliasClause().accept(this);
		// }

	}

	public void preVisit( TJoinList node )
	{
		for ( int i = 0; i < node.size( ); i++ )
		{
			node.getJoin( i ).accept( this );
		}
	}

	public void preVisit( TJoinItem node )
	{
		appendStartTagWithIntProperty( node, "jointype", node.getJoinType( )
				.toString( ) );
		if ( node.getKind( ) == TBaseType.join_source_table )
		{
			node.getTable( ).accept( this );
		}
		else if ( node.getKind( ) == TBaseType.join_source_join )
		{
			node.getJoin( ).accept( this );
		}

		if ( node.getOnCondition( ) != null )
		{
			node.getOnCondition( ).accept( this );
		}

		if ( node.getUsingColumns( ) != null )
		{
			node.getUsingColumns( ).accept( this );
		}
	}

	public void postVisit( TJoinItem node )
	{
		appendEndTag( node );
	}

	public void preVisit( TJoinItemList node )
	{
		appendStartTag( node );
		for ( int i = 0; i < node.size( ); i++ )
		{
			node.getJoinItem( i ).accept( this );
		}
	}

	public void preVisit( TUnpivotInClauseItem node )
	{
		appendStartTag( node );
		outputNodeData( node );
	}

	public void preVisit( TUnpivotInClause node )
	{
		appendStartTag( node );
		for ( int i = 0; i < node.getItems( ).size( ); i++ )
		{
			node.getItems( ).getElement( i ).accept( this );
		}

	}

	public void preVisit( TPivotInClause node )
	{
		appendStartTag( node );
		if ( node.getItems( ) != null )
			node.getItems( ).accept( this );
		if ( node.getSubQuery( ) != null )
			node.getSubQuery( ).accept( this );

	}

	public void preVisit( TPivotedTable node )
	{
		// appendStartTag(node);
		// node.getJoins().accept(this);
		Element e_pivoted_table_reference, e_table_reference;
		TPivotClause pivotClause;

		e_pivoted_table_reference = xmldoc.createElement( "pivoted_table_reference" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_pivoted_table_reference );
		elementStack.push( e_pivoted_table_reference );

		for ( int i = node.getPivotClauseList( ).size( ) - 1; i >= 0; i-- )
		{
			pivotClause = node.getPivotClauseList( ).getElement( i );
			if ( pivotClause.getAliasClause( ) != null )
			{
				pivotClause.getAliasClause( ).accept( this );
			}
			pivotClause.accept( this );

			if ( i == 0 )
			{
				e_table_reference = xmldoc.createElement( "table_reference" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_table_reference );
				elementStack.push( e_table_reference );

				node.getTableSource( ).accept( this );
				elementStack.pop( );
			}
			else
			{
				e_table_reference = xmldoc.createElement( "table_reference" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_table_reference );
				elementStack.push( e_table_reference );

				e_pivoted_table_reference = xmldoc.createElement( "pivoted_table_reference" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_pivoted_table_reference );
				elementStack.push( e_pivoted_table_reference );

			}

		}

		for ( int i = node.getPivotClauseList( ).size( ) - 1; i >= 0; i-- )
		{
			if ( i == 0 )
			{
			}
			else
			{
				elementStack.pop( );
				elementStack.pop( );
			}
		}
		elementStack.pop( );

	}

	public void preVisit( TPivotClause node )
	{
		e_parent = (Element) elementStack.peek( );
		e_parent.setAttribute( "type",
				( node.getType( ) == TPivotClause.pivot ) ? "pivot" : "unpivot" );
		if ( node.getAggregation_function( ) != null )
		{
			// Element e_aggregate_function =
			// xmldoc.createElement("aggregateFunction");
			// e_parent = (Element)elementStack.peek();
			// e_parent.appendChild(e_aggregate_function);
			// elementStack.push(e_aggregate_function);
			current_functionCall_tag = "aggregateFunction";
			node.getAggregation_function( ).accept( this );
			// elementStack.pop();
		}
		if ( node.getValueColumn( ) != null )
		{
			current_objectName_tag = "valueColumn";
			node.getValueColumn( ).accept( this );
		}
		if ( node.getValueColumnList( ) != null )
		{
			for ( int i = 0; i < node.getValueColumnList( ).size( ); i++ )
			{
				node.getValueColumnList( ).getObjectName( i ).accept( this );
			}
		}
		if ( node.getPivotColumn( ) != null )
		{
			current_objectName_tag = "pivotColumn";
			node.getPivotColumn( ).accept( this );
		}
		if ( node.getPivotColumnList( ) != null )
		{
			current_objectName_list_tag = "inColumns";
			node.getPivotColumnList( ).accept( this );
			// for(int i=0;i<node.getPivotColumnList().size();i++){
			// node.getPivotColumnList().getObjectName(i).accept(this);
			// }
		}

		if ( node.getAggregation_function_list( ) != null )
		{
			node.getAggregation_function_list( ).accept( this );
		}

		if ( node.getIn_result_list( ) != null )
		{
			node.getIn_result_list( ).accept( this );
		}

		if ( node.getPivotInClause( ) != null )
		{
			node.getPivotInClause( ).accept( this );
		}

		if ( node.getUnpivotInClause( ) != null )
		{
			node.getUnpivotInClause( ).accept( this );
		}

		// if (node.getAliasClause() != null){
		// node.getAliasClause().accept(this);
		// }

	}

	public void preVisit( TTable node )
	{
		// appendStartTagWithIntProperty(node,"type",node.getTableType().toString());
		String tag_name = "table_reference";
		if ( current_table_reference_tag != null )
		{
			tag_name = current_table_reference_tag;
			current_table_reference_tag = null;
		}
		Element e_table_reference = xmldoc.createElement( tag_name );
		e_table_reference.setAttribute( "type", node.getTableType( ).toString( ) );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_table_reference );
		elementStack.push( e_table_reference );

		switch ( node.getTableType( ) )
		{
			case objectname :
			{
				Element e_named_table_reference = xmldoc.createElement( "named_table_reference" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_named_table_reference );
				elementStack.push( e_named_table_reference );
				current_objectName_tag = "table_name";
				node.getTableName( ).accept( this );
				current_objectName_tag = null;

				elementStack.pop( );
				// sb.append(node.toString().replace(">","&#62;").replace("<","&#60;"));
				break;
			}
			case tableExpr :
			{
				e_table_reference = xmldoc.createElement( "expr_table_reference" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_table_reference );
				elementStack.push( e_table_reference );

				current_expression_tag = "table_expr";
				node.getTableExpr( ).accept( this );

				elementStack.pop( );
				break;
			}
			case subquery :
			{
				e_table_reference = xmldoc.createElement( "query_table_reference" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_table_reference );
				elementStack.push( e_table_reference );
				node.getSubquery( ).accept( this );
				elementStack.pop( );
				break;
			}
			case function :
			{
				e_table_reference = xmldoc.createElement( "functionCall_table_reference" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_table_reference );
				elementStack.push( e_table_reference );
				current_functionCall_tag = "func_expr";
				node.getFuncCall( ).accept( this );
				elementStack.pop( );
				break;
			}
			case pivoted_table :
			{
				node.getPivotedTable( ).accept( this );
				break;
			}
			case output_merge :
			{
//				e_table_reference = xmldoc.createElement( "not_decode_reference" );
//				e_parent = (Element) elementStack.peek( );
//				e_parent.appendChild( e_table_reference );
//				elementStack.push( e_table_reference );
				 node.getOutputMerge().accept(this);
//				e_table_reference.setTextContent( node.getOutputMerge( )
//						.toString( ) );
//				elementStack.pop( );
				break;
			}
			case containsTable :
			{
				e_table_reference = xmldoc.createElement( "named_table_reference" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_table_reference );
				elementStack.push( e_table_reference );
				node.getContainsTable( ).accept( this );
				elementStack.pop( );

				break;
			}

			case openrowset :
			{
				e_table_reference = xmldoc.createElement( "named_table_reference" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_table_reference );
				elementStack.push( e_table_reference );
				node.getOpenRowSet( ).accept( this );
				elementStack.pop( );
				break;
			}

			case openxml :
			{
				e_table_reference = xmldoc.createElement( "named_table_reference" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_table_reference );
				elementStack.push( e_table_reference );
				node.getOpenXML( ).accept( this );
				elementStack.pop( );
				break;
			}

			case opendatasource :
			{
				e_table_reference = xmldoc.createElement( "named_table_reference" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_table_reference );
				elementStack.push( e_table_reference );
				node.getOpenDatasource( ).accept( this );
				elementStack.pop( );
				break;
			}

			case openquery :
			{
				e_table_reference = xmldoc.createElement( "named_table_reference" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_table_reference );
				elementStack.push( e_table_reference );
				node.getOpenquery( ).accept( this );
				elementStack.pop( );
				break;
			}

			case datachangeTable :
			{
				e_table_reference = xmldoc.createElement( "named_table_reference" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_table_reference );
				elementStack.push( e_table_reference );
				node.getDatachangeTable( ).accept( this );
				elementStack.pop( );
				break;
			}
			case rowList :
			{
				e_table_reference = xmldoc.createElement( "named_table_reference" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_table_reference );
				elementStack.push( e_table_reference );
				node.getValueClause( ).accept( this );
				elementStack.pop( );
				break;
			}
			case xmltable :
			{
				e_table_reference = xmldoc.createElement( "named_table_reference" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_table_reference );
				elementStack.push( e_table_reference );
				node.getXmlTable( ).accept( this );
				elementStack.pop( );
				break;
			}

			case informixOuter :
			{
				e_table_reference = xmldoc.createElement( "named_table_reference" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_table_reference );
				elementStack.push( e_table_reference );
				node.getOuterClause( ).accept( this );
				elementStack.pop( );
				break;
			}

			case table_ref_list :
			{
				e_table_reference = xmldoc.createElement( "named_table_reference" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_table_reference );
				elementStack.push( e_table_reference );
				node.getFromTableList( ).accept( this );
				elementStack.pop( );
				break;
			}
			case hiveFromQuery :
			{
				e_table_reference = xmldoc.createElement( "named_table_reference" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_table_reference );
				elementStack.push( e_table_reference );
				node.getHiveFromQuery( ).accept( this );
				elementStack.pop( );
				break;
			}
			case externalTable:
				{
					e_table_reference = xmldoc.createElement( "named_table_reference" );
					e_parent = (Element) elementStack.peek( );
					e_parent.appendChild( e_table_reference );
					elementStack.push( e_table_reference );
					current_objectName_tag = "table_name";
					node.getTableName( ).accept( this );
					current_objectName_tag = null;

					node.getColumnDefinitions().accept(this);

					elementStack.pop( );
					// sb.append(node.toString().replace(">","&#62;").replace("<","&#60;"));
					break;
				}
			default :
				e_table_reference = xmldoc.createElement( "named_table_reference" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_table_reference );
				elementStack.push( e_table_reference );
				sb.append( node.toString( )
						.replace( ">", "&#62;" )
						.replace( "<", "&#60;" ) );
				elementStack.pop( );
				break;

		}

		if (node.getAliasClause() != null){
			node.getAliasClause().accept(this);
		}

		if ( node.getTableHintList( ) != null )
		{
			appendStartTag( "tablehints" );
			for ( int i = 0; i < node.getTableHintList( ).size( ); i++ )
			{
				TTableHint tableHint = node.getTableHintList( ).getElement( i );
				tableHint.accept( this );
			}
			appendEndTag( "tablehints" );
		}

		elementStack.pop( );
	}

	public void postVisit( TTable node )
	{
		appendEndTag( node );
	}

	public void preVisit( TTableHint node )
	{
		appendStartTag( node );
		sb.append( node.toString( ) );
	}

	public void postVisit( TTableHint node )
	{
		appendEndTag( node );
	}

	public void preVisit( TObjectName node )
	{
		Element e_object_name;
		String tag_name = TAG_OBJECTNAME;
		e_parent = (Element) elementStack.peek( );
		if ( current_objectName_tag != null )
		{
			tag_name = current_objectName_tag;
			current_objectName_tag = null;
		}
		e_object_name = xmldoc.createElement( tag_name );
		e_parent.appendChild( e_object_name );

		Element e_identifier = xmldoc.createElement( TAG_FULLNAME );
		e_object_name.appendChild( e_identifier );
		//e_object_name.setAttribute("id", getSequenceId());
		e_object_name.setAttribute( "object_type", node.getDbObjectType( )
				.toString( ) );
		e_identifier.setTextContent( node.toString( ) );
		if ( node.getServerToken( ) != null )
		{
			Element e_server = xmldoc.createElement( "server_name" );
			e_object_name.appendChild( e_server );
			e_server.setTextContent( node.getServerToken( ).toString( ) );
		}
		if (( node.getDatabaseToken( ) != null )||(node.getImplictDatabaseString() != null))
		{
			Element e_database = xmldoc.createElement( "database_name" );
			e_object_name.appendChild( e_database );
			if (node.getDatabaseToken() != null){
				e_database.setTextContent( node.getDatabaseToken( ).toString( ) );
			}else{
				e_database.setTextContent( node.getImplictDatabaseString( ) );
			}

		}
		if (( (node.getSchemaToken( ) != null )&&(!node.isImplicitSchema()))||(node.getImplictSchemaString() != null))
		{
			Element e_schema = xmldoc.createElement( "schema_name" );
			e_object_name.appendChild( e_schema );
			if ((node.getSchemaToken() != null)&&(!node.isImplicitSchema())){
				e_schema.setTextContent( node.getSchemaToken( ).toString( ) );
			}else{
				e_schema.setTextContent( node.getImplictSchemaString());
			}
		}
		if ( node.getObjectToken( ) != null )
		{
			Element e_object = xmldoc.createElement( "object_name" );
			e_object_name.appendChild( e_object );
			e_object.setTextContent( node.getObjectToken( ).toString( ) );
		}
		if ( node.getPartToken( ) != null )
		{
			Element e_part = xmldoc.createElement( "part_name" );
			e_object_name.appendChild( e_part );
			e_part.setTextContent( node.getPartToken( ).toString( ) );
		}

		if (node.getSourceTable() != null){
			String sourceTable = node.getSourceTable().getTableName().toString();
			if (node.getSourceTable().getAliasClause() != null){
				sourceTable = sourceTable + ", alias is: "+node.getSourceTable().getAliasClause().toString();

			}
			addElementOfString("source_table",sourceTable);
		}
	}

	public void postVisit( TObjectName node )
	{
		appendEndTag( node );
	}

	public void preVisit( TObjectNameList node )
	{
		Element e_objectName_list;
		e_parent = (Element) elementStack.peek( );
		if ( current_objectName_list_tag == null )
		{
			e_objectName_list = xmldoc.createElement( "objectName_list" );
		}
		else
		{
			e_objectName_list = xmldoc.createElement( current_objectName_list_tag );
			current_objectName_list_tag = null;
		}

		e_parent.appendChild( e_objectName_list );

		elementStack.push( e_objectName_list );

		for ( int i = 0; i < node.size( ); i++ )
		{
			node.getObjectName( i ).accept( this );
		}
		elementStack.pop( );
	}

	public void preVisit( TWhereClause node )
	{
		// appendStartTag(node);
		Element e_where = xmldoc.createElement( "where_clause" );
		e_parent = (Element) elementStack.peek( );
		// if (current_objectName_list_tag == null){
		// e_objectName_list = xmldoc.createElement("objectName_list");
		// }else{
		// e_objectName_list =
		// xmldoc.createElement(current_objectName_list_tag);
		// }

		e_parent.appendChild( e_where );

		elementStack.push( e_where );
		current_expression_tag = "condition";
		node.getCondition( ).accept( this );
		current_expression_tag = null;
		elementStack.pop( );
	}

	public void postVisit( TWhereClause node )
	{
		appendEndTag( node );
	}

	public void preVisit( THierarchical node )
	{

		Element e_hierarchical = xmldoc.createElement( "hierarchial_clause" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_hierarchical );
		elementStack.push( e_hierarchical );

		if ( node.getConnectByList( ) != null )
		{
			for ( int i = 0; i < node.getConnectByList( ).size( ); i++ )
			{
				node.getConnectByList( ).getElement( i ).accept( this );
			}
		}

		if ( node.getStartWithClause( ) != null )
		{
			Element e_start_with = xmldoc.createElement( "start_with_clause" );
			e_hierarchical.appendChild( e_start_with );
			elementStack.push( e_start_with );
			node.getStartWithClause( ).accept( this );
			elementStack.pop( );
		}

		elementStack.pop( );

	}

	public void preVisit( TConnectByClause node )
	{
		Element e_connect_by = xmldoc.createElement( "connect_by_clause" );
		e_connect_by.setAttribute( "nocycle",
				String.valueOf( node.isNoCycle( ) ) );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_connect_by );
		elementStack.push( e_connect_by );
		node.getCondition( ).accept( this );
		elementStack.pop( );

	}

	public void preVisit( TRollupCube node )
	{
		current_expression_list_tag = "rollup_list";
		if ( node.getOperation( ) == TRollupCube.cube )
		{
			current_expression_list_tag = "cube_list";
		}
		node.getItems( ).accept( this );

	}

	public void preVisit( TGroupBy node )
	{
		appendStartTag( node );
		if ( node.getItems( ) != null )
		{
			Element e_group_by = xmldoc.createElement( "group_by_clause" );
			e_group_by.setAttribute("withRollup",node.isRollupModifier()?"true":"false");
			e_group_by.setAttribute("withCube",node.isCubeModifier()?"true":"false");
			e_parent = (Element) elementStack.peek( );
			e_parent.appendChild( e_group_by );
			elementStack.push( e_group_by );
			node.getItems( ).accept( this );
			elementStack.pop( );
		}
		if ( node.getHavingClause( ) != null )
		{
			current_expression_tag = "having_clause";
			node.getHavingClause( ).accept( this );
		}
	}

	public void preVisit( TGroupByItem node )
	{
		Element e_group_by = xmldoc.createElement( "grouping_element" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_group_by );
		elementStack.push( e_group_by );

		if ( node.getExpr( ) != null )
		{
			TExpression ge = node.getExpr( );
			if ( ( ge.getExpressionType( ) == EExpressionType.list_t )
					&& ( ge.getExprList( ) == null ) )
			{
				Element e_grand_total = xmldoc.createElement( "grand_total" );
				e_group_by.appendChild( e_grand_total );

			}
			else
			{
				current_expression_tag = "grouping_expression";
				ge.accept( this );
			}

			// current_expression_tag = "grouping_expression";
			// node.getExpr().accept(this);
		}
		else if ( node.getGroupingSet( ) != null )
		{
			node.getGroupingSet( ).accept( this );
		}
		else if ( node.getRollupCube( ) != null )
		{
			node.getRollupCube( ).accept( this );
		}

		elementStack.pop( );
	}

	//
	public void preVisit( TGroupingSet node )
	{
		Element e_grouping_sets = xmldoc.createElement( "grouping_sets_specification" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_grouping_sets );
		elementStack.push( e_grouping_sets );
		Element e_grouping_set_item;
		for ( int i = 0; i < node.getItems( ).size( ); i++ )
		{
			e_grouping_set_item = xmldoc.createElement( "grouping_set_item" );
			e_parent = (Element) elementStack.peek( );
			e_parent.appendChild( e_grouping_set_item );
			elementStack.push( e_grouping_set_item );

			if ( node.getItems( )
					.getGroupingSetItem( i )
					.getGrouping_expression( ) != null )
			{
				TExpression ge = node.getItems( )
						.getGroupingSetItem( i )
						.getGrouping_expression( );
				if ( ( ge.getExpressionType( ) == EExpressionType.list_t )
						&& ( ge.getExprList( ) == null ) )
				{
					Element e_grand_total = xmldoc.createElement( "grand_total" );
					e_grouping_set_item.appendChild( e_grand_total );

				}
				else
				{
					current_expression_tag = "grouping_expression";
					ge.accept( this );
				}
			}
			else if ( node.getItems( )
					.getGroupingSetItem( i )
					.getRollupCubeClause( ) != null )
			{
				TRollupCube rollupCube = node.getItems( )
						.getGroupingSetItem( i )
						.getRollupCubeClause( );
				rollupCube.accept( this );
			}
			elementStack.pop( );
		}
		elementStack.pop( );
	}

	public void postVisit( TGroupingSet node )
	{
	}

	public void preVisit( TGroupByItemList node )
	{
		// appendStartTag(node);
		for ( int i = 0; i < node.size( ); i++ )
		{
			node.getGroupByItem( i ).accept( this );
		}
	}

	public void postVisit( TGroupByItemList node )
	{
		appendEndTag( node );
	}

	public void preVisit( TOrderBy node )
	{
		Element e_order_by = xmldoc.createElement( "order_by_clause" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_order_by );
		elementStack.push( e_order_by );
		node.getItems( ).accept( this );
		elementStack.pop( );

	}

	public void preVisit( TOrderByItem node )
	{
		Element e_order_by_item = xmldoc.createElement( "order_by_item" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_order_by_item );
		elementStack.push( e_order_by_item );
		if ( node.getSortKey( ) != null )
		{
			current_expression_tag = "sort_key";
			node.getSortKey( ).accept( this );
		}
		e_order_by_item.setAttribute( "sort_order", node.getSortOrder( )
				.toString( ) );
		// if (node.getSortOrder() != ESortType.none){
		// Element e_sort_order = xmldoc.createElement("sort_order");
		// e_sort_order.setTextContent(node.getSortOrder().toString());
		// e_order_by_item.appendChild(e_sort_order);
		// }
		elementStack.pop( );
	}

	public void preVisit( TOrderByItemList node )
	{
		appendStartTag( node );
		for ( int i = 0; i < node.size( ); i++ )
		{
			node.getOrderByItem( i ).accept( this );
		}
	}

	public void postVisit( TOrderByItemList node )
	{
		appendEndTag( node );
	}

	public void preVisit( TForUpdate node )
	{
	}

	public void preVisit( TStatementList node )
	{
		// appendStartTag(node);

		Element e_statement_list;
		e_parent = (Element) elementStack.peek( );
		if ( current_statement_list_tag == null )
		{
			e_statement_list = xmldoc.createElement( TAG_STATEMENT_LIST );
		}
		else
		{
			e_statement_list = xmldoc.createElement( current_statement_list_tag );
			current_statement_list_tag = null;
		}
		e_statement_list.setAttribute( "count", String.valueOf( node.size( ) ) );
		e_parent.appendChild( e_statement_list );

		// elementStack.push(e_statement_list);
		for ( int i = 0; i < node.size( ); i++ )
		{
			Element e_statement = xmldoc.createElement( "statement" );
			e_statement.setAttribute( "type",
					node.get( i ).sqlstatementtype.toString( ) );
			e_statement_list.appendChild( e_statement );
			elementStack.push( e_statement );
			node.get( i ).setDummyTag( TOP_STATEMENT );
			node.get( i ).accept( this );
			elementStack.pop( );
		}

	}

	void doDeclare_Body_Exception( TCommonStoredProcedureSqlStatement node )
	{

		if ( node.getDeclareStatements( ) != null )
		{
			appendStartTag( "declare" );
			node.getDeclareStatements( ).accept( this );
			appendEndTag( "declare" );
		}

		if ( node.getBodyStatements( ) != null )
		{
			appendStartTag( "body" );
			node.getBodyStatements( ).accept( this );
			appendEndTag( "body" );
		}

		if ( node.getExceptionClause( ) != null )
		{
			node.getExceptionClause( ).accept( this );
		}

	}

	public void preVisit( TPlsqlCreatePackage node )
	{
		Element e_create_package = null;
		switch ( node.getKind( ) )
		{
			case TBaseType.kind_define :
			case TBaseType.kind_create :
				e_create_package = xmldoc.createElement( "create_package_statement" );
				break;
			case TBaseType.kind_create_body :
				e_create_package = xmldoc.createElement( "create_package_body_statement" );
				break;
		}

		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_create_package );
		elementStack.push( e_create_package );
		current_objectName_tag = "package_name";
		node.getPackageName( ).accept( this );
		if ( node.getEndlabelName( ) != null )
		{
			current_objectName_tag = "end_package_name";
			node.getEndlabelName( ).accept( this );
		}
		if ( node.getDeclareStatements( ).size( ) > 0 )
		{
			current_statement_list_tag = "declare_section";
			node.getDeclareStatements( ).accept( this );
		}

		elementStack.pop( );

		// if (node.getParameterDeclarations() != null)
		// node.getParameterDeclarations().accept(this);
		// if ( node.getBodyStatements().size() > 0)
		// node.getBodyStatements().accept(this);
		// if (node.getExceptionClause() != null)
		// node.getExceptionClause().accept(this);

	}

	public void preVisit( TMssqlCreateFunction node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_function = xmldoc.createElement( "create_function_statement" );
		e_parent.appendChild( e_function );
		elementStack.push( e_function );
		// doFunctionSpecification(node);

		e_parent = (Element) elementStack.peek( );
		Element e_function_spec = xmldoc.createElement( "function_specification_statement" );
		e_parent.appendChild( e_function_spec );
		elementStack.push( e_function_spec );
		current_objectName_tag = "function_name";
		node.getFunctionName( ).accept( this );
		if ( node.getEndlabelName( ) != null )
		{
			current_objectName_tag = "end_function_name";
			node.getEndlabelName( ).accept( this );
		}

		if ( node.getReturnDataType( ) != null )
		{
			current_datatype_tag = "return_datatype";
			node.getReturnDataType( ).accept( this );
		}

		if ( node.getParameterDeclarations( ) != null )
		{
			node.getParameterDeclarations( ).accept( this );
		}

		if ( node.getBodyStatements( ).size( ) > 0 )
		{
			current_statement_list_tag = "body_statement_list";
			node.getBodyStatements( ).accept( this );
		}

		elementStack.pop( );

		elementStack.pop( );

	}

	public void preVisit( TCreateFunctionStmt node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_function = xmldoc.createElement( "create_function_statement" );
		e_parent.appendChild( e_function );
		elementStack.push( e_function );
		// doFunctionSpecification(node);

		e_parent = (Element) elementStack.peek( );
		Element e_function_spec = xmldoc.createElement( "function_specification_statement" );
		e_parent.appendChild( e_function_spec );
		elementStack.push( e_function_spec );
		current_objectName_tag = "function_name";
		node.getFunctionName( ).accept( this );
		if ( node.getEndlabelName( ) != null )
		{
			current_objectName_tag = "end_function_name";
			node.getEndlabelName( ).accept( this );
		}

		if ( node.getReturnDataType( ) != null )
		{
			current_datatype_tag = "return_datatype";
			node.getReturnDataType( ).accept( this );
		}

		if ( node.getParameterDeclarations( ) != null )
		{
			node.getParameterDeclarations( ).accept( this );
		}

		if ( node.getBodyStatements( ).size( ) > 0 )
		{
			current_statement_list_tag = "body_statement_list";
			node.getBodyStatements( ).accept( this );
		}

		elementStack.pop( );

		elementStack.pop( );

	}

	public void preVisit( TCreateDatabaseSqlStatement stmt )
	{
		Element e_use_database = xmldoc.createElement( "create_database_statement" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_use_database );
		elementStack.push( e_use_database );
		current_objectName_tag = "database_name";
		stmt.getDatabaseName( ).accept( this );
		elementStack.pop( );
	}

	public void preVisit( TCreateSchemaSqlStatement stmt )
	{
		Element e_create_schema = xmldoc.createElement( "create_schema_statement" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_create_schema );
		elementStack.push( e_create_schema );
		e_create_schema.setAttribute("externalSchema",stmt.isExternalSchema()?"true":"false");
		current_objectName_tag = "schema_name";
		stmt.getSchemaName( ).accept( this );
		if ( stmt.getOwnerName( ) != null )
		{
			current_objectName_tag = "owner_name";
			stmt.getOwnerName( ).accept( this );
		}
		if ( stmt.getBodyStatements( ).size( ) > 0 )
		{
			stmt.getBodyStatements( ).accept( this );
		}

		if (stmt.getFromSource() != TCreateSchemaSqlStatement.EFromSource.NA){
			addElementOfString("from_source",stmt.getFromSource().toString());
		}

		if (stmt.getSourceDatabase() != null){
			addElementOfNode("source_database",stmt.getSourceDatabase());
			if (stmt.getSourceSchema() != null){
				addElementOfNode("source_schema",stmt.getSourceSchema());
			}
		}


		elementStack.pop( );
	}

	public void preVisit( TUseDatabase stmt )
	{
		Element e_use_database = xmldoc.createElement( "use_database_statement" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_use_database );
		elementStack.push( e_use_database );
		current_objectName_tag = "database_name";
		stmt.getDatabaseName( ).accept( this );
		elementStack.pop( );
	}

	public void preVisit( TMssqlBlock node )
	{
		Element e_block = xmldoc.createElement( "block_statement" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_block );
		elementStack.push( e_block );

		if ( node.getBodyStatements( ).size( ) > 0 )
		{
			current_statement_list_tag = "body_statement_list";
			node.getBodyStatements( ).accept( this );
		}

		elementStack.pop( );

	}

	private void doProcedureSpecification( TPlsqlCreateProcedure node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_procedure_spec = xmldoc.createElement( "procedure_specification_statement" );
		e_parent.appendChild( e_procedure_spec );
		elementStack.push( e_procedure_spec );
		current_objectName_tag = "procedure_name";
		node.getProcedureName( ).accept( this );

		if ( node.getEndlabelName( ) != null )
		{
			current_objectName_tag = "end_procedure_name";
			node.getEndlabelName( ).accept( this );
		}

		if ( node.getParameterDeclarations( ) != null )
			node.getParameterDeclarations( ).accept( this );
		if ( node.getInnerStatements( ).size( ) > 0 )
			node.getInnerStatements( ).accept( this );
		if ( node.getDeclareStatements( ).size( ) > 0 )
		{
			current_statement_list_tag = "declaration_section";
			node.getDeclareStatements( ).accept( this );
		}

		if ( node.getBodyStatements( ).size( ) > 0 )
		{
			current_statement_list_tag = "begen_end_block";
			node.getBodyStatements( ).accept( this );
		}

		if ( node.getExceptionClause( ) != null )
			node.getExceptionClause( ).accept( this );

		elementStack.pop( );

	}

	public void preVisit( TPlsqlCreateProcedure node )
	{
		Element e_create_procedure = null;
		e_parent = (Element) elementStack.peek( );

		switch ( node.getKind( ) )
		{
			case TBaseType.kind_define :
				doProcedureSpecification( node );
				break;
			case TBaseType.kind_declare :
				Element e_procedure_declare = xmldoc.createElement( "procedure_declare_statement" );
				e_parent.appendChild( e_procedure_declare );
				elementStack.push( e_procedure_declare );
				current_objectName_tag = "procedure_name";
				node.getProcedureName( ).accept( this );
				if ( node.getParameterDeclarations( ) != null )
					node.getParameterDeclarations( ).accept( this );
				elementStack.pop( );

				break;
			case TBaseType.kind_create :
				e_create_procedure = xmldoc.createElement( "create_procedure_statement" );
				e_parent.appendChild( e_create_procedure );
				elementStack.push( e_create_procedure );
				doProcedureSpecification( node );
				elementStack.pop( );
				break;
		}
	}

	private void doFunctionSpecification( TPlsqlCreateFunction node )
	{
		if ( node.isWrapped( ) )
			return;
		e_parent = (Element) elementStack.peek( );
		Element e_function_spec = xmldoc.createElement( "function_specification_statement" );
		e_parent.appendChild( e_function_spec );
		elementStack.push( e_function_spec );
		current_objectName_tag = "function_name";
		node.getFunctionName( ).accept( this );
		if ( node.getEndlabelName( ) != null )
		{
			current_objectName_tag = "end_function_name";
			node.getEndlabelName( ).accept( this );
		}
		current_datatype_tag = "return_datatype";
		node.getReturnDataType( ).accept( this );

		if ( node.getParameterDeclarations( ) != null )
		{
			node.getParameterDeclarations( ).accept( this );
		}
		if ( node.getDeclareStatements( ).size( ) > 0 )
		{
			current_statement_list_tag = "declaration_section";
			node.getDeclareStatements( ).accept( this );
		}
		if ( node.getBodyStatements( ).size( ) > 0 )
		{
			current_statement_list_tag = "begin_end_block";
			node.getBodyStatements( ).accept( this );
		}
		if ( node.getExceptionClause( ) != null )
			node.getExceptionClause( ).accept( this );
		elementStack.pop( );
	}

	public void preVisit( TPlsqlCreateFunction node )
	{
		Element e_function = null;
		e_parent = (Element) elementStack.peek( );
		switch ( node.getKind( ) )
		{
			case TBaseType.kind_create :
				e_function = xmldoc.createElement( "create_function_statement" );
				e_parent.appendChild( e_function );
				elementStack.push( e_function );
				doFunctionSpecification( node );
				elementStack.pop( );
				break;
			case TBaseType.kind_declare :
				Element e_function_declare = xmldoc.createElement( "function_declare_statement" );
				e_parent.appendChild( e_function_declare );
				elementStack.push( e_function_declare );
				current_objectName_tag = "function_name";
				node.getFunctionName( ).accept( this );
				if ( node.getParameterDeclarations( ) != null )
					node.getParameterDeclarations( ).accept( this );
				elementStack.pop( );
				break;
			case TBaseType.kind_define :
				doFunctionSpecification( node );
				break;
		}
	}

	public void preVisit( TCommonBlock node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_block_stmt = xmldoc.createElement( "plsql_block_statement" );
		e_parent.appendChild( e_block_stmt );
		elementStack.push( e_block_stmt );
		if ( node.getLabelName( ) != null )
		{
			current_objectName_tag = "label_name";
			node.getLabelName( ).accept( this );
		}
		// doDeclare_Body_Exception(node);
		current_statement_list_tag = "declaration_section";
		if ( node.getDeclareStatements( ).size( ) > 0 )
			node.getDeclareStatements( ).accept( this );
		current_statement_list_tag = "body_section";
		if ( node.getBodyStatements( ).size( ) > 0 )
			node.getBodyStatements( ).accept( this );

		if ( node.getExceptionClause( ) != null )
			node.getExceptionClause( ).accept( this );
		elementStack.pop( );

	}

	public void preVisit( TExceptionClause node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_exception_clause = xmldoc.createElement( "exception_clause" );
		e_parent.appendChild( e_exception_clause );
		elementStack.push( e_exception_clause );
		node.getHandlers( ).accept( this );
		elementStack.pop( );
	}

	public void preVisit( TExceptionHandler node )
	{
		Element e_exception_handler;
		e_parent = (Element) elementStack.peek( );
		e_exception_handler = xmldoc.createElement( "exception_handler" );
		e_parent.appendChild( e_exception_handler );
		elementStack.push( e_exception_handler );
		current_objectName_list_tag = "exception_name_list";
		node.getExceptionNames( ).accept( this );
		node.getStatements( ).accept( this );
		elementStack.pop( );
	}

	public void preVisit( TExceptionHandlerList node )
	{
		for ( int i = 0; i < node.size( ); i++ )
		{
			node.getExceptionHandler( i ).accept( this );
		}

	}

	public void preVisit( TAlterTableOption node )
	{
		// appendStartTag(node);
		Element e_alter_table_option = xmldoc.createElement( "alter_table_option" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_alter_table_option );
		elementStack.push( e_alter_table_option );
		e_alter_table_option.setAttribute( "alter_type", node.getOptionType( )
				.toString( ) );

		// appendStartTagWithIntProperty(node, "alterOption",
		// node.getOptionType().toString());
		Element e_option = null;
		switch ( node.getOptionType( ) )
		{
			case AddColumn :
				e_option = xmldoc.createElement( "add_column_option" );
				e_alter_table_option.appendChild( e_option );
				elementStack.push( e_option );
				node.getColumnDefinitionList( ).accept( this );
				elementStack.pop( );
				break;
			case AlterColumn :
				e_option = xmldoc.createElement( "alter_column_option" );
				e_alter_table_option.appendChild( e_option );
				elementStack.push( e_option );
				node.getColumnName( ).accept( this );
				elementStack.pop( );
				break;
			case ChangeColumn :
				e_option = xmldoc.createElement( "change_column_option" );
				e_alter_table_option.appendChild( e_option );
				elementStack.push( e_option );
				node.getColumnName( ).accept( this );
				elementStack.pop( );
				break;
			case DropColumn :
				e_option = xmldoc.createElement( "drop_column_option" );
				e_alter_table_option.appendChild( e_option );
				elementStack.push( e_option );
				for ( int i = 0; i < node.getColumnNameList( ).size( ); i++ )
				{
					current_objectName_tag = "column_name";
					node.getColumnNameList( ).getObjectName( i ).accept( this );
				}
				elementStack.pop( );

				break;
			case ModifyColumn :
				e_option = xmldoc.createElement( "modify_column_option" );
				e_alter_table_option.appendChild( e_option );
				elementStack.push( e_option );
				node.getColumnDefinitionList( ).accept( this );
				elementStack.pop( );
				break;
			case RenameColumn :
				e_option = xmldoc.createElement( "rename_column_option" );
				e_alter_table_option.appendChild( e_option );
				elementStack.push( e_option );
				current_objectName_tag = "column_name";
				node.getColumnName( ).accept( this );
				current_objectName_tag = "new_column_name";
				node.getNewColumnName( ).accept( this );
				elementStack.pop( );
				break;
			case AddConstraint :
				e_option = xmldoc.createElement( "add_constraint_option" );
				e_alter_table_option.appendChild( e_option );
				elementStack.push( e_option );
				node.getConstraintList( ).accept( this );
				elementStack.pop( );
				break;
			case switchPartition :
				e_option = xmldoc.createElement( "switch_partition_option" );
				e_alter_table_option.appendChild( e_option );
				elementStack.push( e_option );
				current_objectName_tag = "new_table_name";
				node.getNewTableName( ).accept( this );
				if ( node.getPartitionExpression1( ) != null )
				{
					current_expression_tag = "source_partition_number";
					node.getPartitionExpression1( ).accept( this );
				}
				if ( node.getPartitionExpression2( ) != null )
				{
					current_expression_tag = "target_partition_number";
					node.getPartitionExpression2( ).accept( this );
				}
				elementStack.pop( );
				break;
			case setLocation:
				addElementOfNode("table_location",node.getTableLocation());
				break;
			case addPartitionSpecList:
				for(int i=0;i<node.getPartitionSpecList().size();i++){
					node.getPartitionSpecList().get(i).accept(this);
				}
				break;
			default :
				e_option = xmldoc.createElement( "not_implemented_option" );
				e_alter_table_option.appendChild( e_option );
				elementStack.push( e_option );
				e_option.setTextContent( node.toString( ) );
				elementStack.pop( );
		}

		elementStack.pop( );
	}


	public void preVisit( TPartitionExtensionClause node ) {

		Element e_partition = xmldoc.createElement("partition_clause");
		e_parent = (Element) elementStack.peek();
		e_parent.appendChild(e_partition);
		elementStack.push(e_partition);
		if (node.getPartitionName() != null){
			addElementOfNode("partition_name",node.getPartitionName());
		}
		if (node.getKeyValues() != null){
			Element e_partition_list = xmldoc.createElement("partition_list");
			e_partition.appendChild(e_partition_list);
			elementStack.push(e_partition_list);

			for(int i=0;i<node.getKeyValues().size();i++){
				TExpression e = node.getKeyValues().getExpression(i);
				//addElementOfNode("column_name",e.getLeftOperand());
				addElementOfString("column_name",e.getLeftOperand().toString());
				addElementOfString("column_value",e.getRightOperand().toString());
			}
			elementStack.pop();
		}

		if (node.getPartitionLocation() != null){
			addElementOfNode("partition_location",node.getPartitionLocation());
		}

		elementStack.pop();
	}

	public void preVisit( TAlterTableStatement stmt )
	{

		Element e_alter_table = xmldoc.createElement( "alter_table_statement" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_alter_table );
		elementStack.push( e_alter_table );

		current_objectName_tag = "table_name";
		stmt.getTableName( ).accept( this );

		if ( stmt.getAlterTableOptionList( ) != null )
		{
			Element e_alter_table_option_list = xmldoc.createElement( "alter_table_option_list" );
			e_alter_table_option_list.setAttribute( "count",
					String.valueOf( stmt.getAlterTableOptionList( ).size( ) ) );
			e_alter_table.appendChild( e_alter_table_option_list );
			elementStack.push( e_alter_table_option_list );
			for ( int i = 0; i < stmt.getAlterTableOptionList( ).size( ); i++ )
			{
				stmt.getAlterTableOptionList( )
						.getAlterTableOption( i )
						.accept( this );
			}
			elementStack.pop( );
		}
		//
		// if (stmt.getMySQLTableOptionList() != null){
		// stmt.getMySQLTableOptionList().accept(this);
		// }

		elementStack.pop( );

	}

	public void preVisit( TTypeName node )
	{
		String tag_name = "datatype";
		e_parent = (Element) elementStack.peek( );
		if ( current_datatype_tag != null )
		{
			tag_name = current_datatype_tag;
			current_datatype_tag = null;
		}
		Element e_datatype = xmldoc.createElement( tag_name );
		e_datatype.setAttribute( "type", node.getDataType( ).toString( ) );
		if ( node.getPrecision( ) != null )
		{
			e_datatype.setAttribute( "precision", node.getPrecision( )
					.toString( ) );
		}
		if ( node.getScale( ) != null )
		{
			e_datatype.setAttribute( "scale", node.getScale( ).toString( ) );
		}
		e_parent.appendChild( e_datatype );
		Element e_value = xmldoc.createElement( "value" );
		e_value.setTextContent( node.toString( ) );
		e_datatype.appendChild( e_value );

	}

	public void preVisit( TColumnDefinition node )
	{

		Element e_column = xmldoc.createElement( "column_definition" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_column );
		elementStack.push( e_column );
		e_column.setAttribute( "explict_nullable", String.valueOf( node.isNull( ) ) );
		current_objectName_tag = "column_name";
		node.getColumnName( ).accept( this );

		if ( node.getDatatype( ) != null )
		{
			node.getDatatype( ).accept( this );
		}

		if (node.getDefaultExpression() != null){
			Element e_defualt_expr = xmldoc.createElement( "default_expression" );
			e_column.appendChild( e_defualt_expr );
			elementStack.push( e_defualt_expr );
			node.getDefaultExpression().accept( this );
			elementStack.pop( );
		}

		if ( ( node.getConstraints( ) != null )
				&& ( node.getConstraints( ).size( ) > 0 ) )
		{
			Element e_constraint_list = xmldoc.createElement( "column_constraint_list" );
			e_column.appendChild( e_constraint_list );
			elementStack.push( e_constraint_list );
			node.getConstraints( ).accept( this );
			elementStack.pop( );
		}

		elementStack.pop( );
	}

	public void preVisit( TColumnDefinitionList node )
	{
		appendStartTag( node );
		for ( int i = 0; i < node.size( ); i++ )
		{
			node.getColumn( i ).accept( this );
		}
	}

	public void preVisit( TMergeWhenClause node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_merge_action = xmldoc.createElement( "merge_action" );
		e_parent.appendChild( e_merge_action );
		elementStack.push( e_merge_action );

		if ( node.getCondition( ) != null )
		{
			current_expression_tag = "search_condition";
			node.getCondition( ).accept( this );
		}

		if ( node.getUpdateClause( ) != null )
		{
			node.getUpdateClause( ).accept( this );
		}

		if ( node.getInsertClause( ) != null )
		{
			node.getInsertClause( ).accept( this );
		}

		if ( node.getDeleteClause( ) != null )
		{
			node.getDeleteClause( ).accept( this );
		}

		elementStack.pop( );

	}

	public void preVisit( TMergeUpdateClause node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_merge_update_action = xmldoc.createElement( "merge_update_action" );
		e_parent.appendChild( e_merge_update_action );
		elementStack.push( e_merge_update_action );

		if ( node.getUpdateColumnList( ) != null )
		{
			// node.getUpdateColumnList().accept(this);
			for ( int i = 0; i < node.getUpdateColumnList( ).size( ); i++ )
			{
				current_expression_tag = "assignment_set_clause";
				node.getUpdateColumnList( )
						.getResultColumn( i )
						.getExpr( )
						.accept( this );
			}
		}

		if ( node.getUpdateWhereClause( ) != null )
		{
			node.getUpdateWhereClause( ).accept( this );
		}

		if ( node.getDeleteWhereClause( ) != null )
		{
			node.getDeleteWhereClause( ).accept( this );
		}
		elementStack.pop( );

	}

	public void preVisit( TMergeInsertClause node )
	{
		// appendStartTag(node);
		e_parent = (Element) elementStack.peek( );
		Element e_merge_insert_action = xmldoc.createElement( "merge_insert_action" );
		e_parent.appendChild( e_merge_insert_action );
		elementStack.push( e_merge_insert_action );

		if ( node.getColumnList( ) != null )
		{
			current_objectName_list_tag = "column_list_reference";
			node.getColumnList( ).accept( this );
		}

		if ( node.getValuelist( ) != null )
		{
			e_parent = (Element) elementStack.peek( );
			Element e_row_values = xmldoc.createElement( "row_values" );
			e_parent.appendChild( e_row_values );
			elementStack.push( e_row_values );
			for ( int i = 0; i < node.getValuelist( ).size( ); i++ )
			{
				node.getValuelist( )
						.getResultColumn( i )
						.getExpr( )
						.accept( this );
			}
			// node.getValuelist().accept(this);
			elementStack.pop( );
		}

		if ( node.getInsertWhereClause( ) != null )
		{
			node.getInsertWhereClause( ).accept( this );
		}

		elementStack.pop( );

	}

	public void preVisit( TMergeDeleteClause node )
	{
		// appendStartTag(node);
		e_parent = (Element) elementStack.peek( );
		Element e_merge_delete_action = xmldoc.createElement( "merge_delete_action" );
		e_parent.appendChild( e_merge_delete_action );
		elementStack.push( e_merge_delete_action );

		elementStack.pop( );

	}

	public void preVisit( TConstraint node )
	{

		Element e_constraint = xmldoc.createElement( "constraint" );
		if ( node.getConstraintName( ) != null )
		{
			e_constraint.setAttribute( "name", node.getConstraintName( )
					.toString( ) );
		}

		e_constraint.setAttribute( "type", node.getConstraint_type( )
				.toString( ) );
		e_constraint.setAttribute( "clustered",
				String.valueOf( node.isClustered( ) ) );
		e_constraint.setAttribute( "nonclustered",
				String.valueOf( node.isNonClustered( ) ) );

		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_constraint );
		elementStack.push( e_constraint );

		// appendStartTagWithIntProperty(node,"type",node.getConstraint_type().toString(),"name",(node.getConstraintName()
		// != null) ? node.getConstraintName().toString():"");
		switch ( node.getConstraint_type( ) )
		{
			case notnull :
				break;
			case unique :
				e_constraint.setAttribute( "xsi:type", "unique_constriant_type" );
				e_constraint.setAttribute( "is_primary_key", "false" );
				if ( node.getColumnList( ) != null )
				{
					for ( int i = 0; i < node.getColumnList( ).size( ); i++ )
					{
						Element e_column = xmldoc.createElement( "column" );
						e_constraint.appendChild( e_column );
						e_column.setAttribute( "name", node.getColumnList( )
								.getElement( i )
								.getColumnName( )
								.toString( ) );
					}
				}
				break;
			case check :
				e_constraint.setAttribute( "xsi:type", "check_constriant_type" );
				current_expression_tag = "check_condition";
				if ( node.getCheckCondition( ) != null )
				{
					node.getCheckCondition( ).accept( this );
				}
				else
				{
					// db2 functional dependency
				}

				break;
			case primary_key :
				e_constraint.setAttribute( "xsi:type", "unique_constriant_type" );
				e_constraint.setAttribute( "is_primary_key", "true" );
				if ( node.getColumnList( ) != null )
				{
					for ( int i = 0; i < node.getColumnList( ).size( ); i++ )
					{
						Element e_column = xmldoc.createElement( "column" );
						e_constraint.appendChild( e_column );
						e_column.setAttribute( "name", node.getColumnList( )
								.getElement( i )
								.getColumnName( )
								.toString( ) );
					}
				}
				break;
			case foreign_key :
			case reference :
				e_constraint.setAttribute( "xsi:type",
						"foreign_key_constriant_type" );
				if ( node.getColumnList( ) != null )
				{
					for ( int i = 0; i < node.getColumnList( ).size( ); i++ )
					{
						Element e_column = xmldoc.createElement( "column" );
						e_constraint.appendChild( e_column );
						e_column.setAttribute( "name", node.getColumnList( )
								.getElement( i )
								.getColumnName( )
								.toString( ) );
					}
				}
				if ( node.getReferencedObject( ) != null )
				{
					// Element e_referenced_table =
					// xmldoc.createElement("referenced_table");
					// e_constraint.appendChild(e_referenced_table);
					// elementStack.push(e_referenced_table);
					current_objectName_tag = "referenced_table";
					node.getReferencedObject( ).accept( this );
					// elementStack.pop();
				}
				if ( node.getReferencedColumnList( ) != null )
				{
					for ( int i = 0; i < node.getReferencedColumnList( ).size( ); i++ )
					{
						Element e_column = xmldoc.createElement( "referenced_column" );
						e_constraint.appendChild( e_column );
						e_column.setAttribute( "name",
								node.getReferencedColumnList( )
										.getObjectName( i )
										.toString( ) );
					}
				}
				break;
			case default_value :
				e_constraint.setAttribute( "xsi:type",
						"default_constriant_type" );
				current_expression_tag = "default_value";
				node.getDefaultExpression( ).accept( this );
				break;
			default :
				break;
		}

		elementStack.pop( );
	}

	public void preVisit( TConstraintList node )
	{
		// appendStartTag(node);
		for ( int i = 0; i < node.size( ); i++ )
		{
			node.getConstraint( i ).accept( this );
		}

	}

	public void preVisit( TCreateMaterializedSqlStatement stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_create_view = xmldoc.createElement( "create_materialize_view_statement" );
		e_parent.appendChild( e_create_view );
		elementStack.push( e_create_view );

		current_objectName_tag = "view_name";
		stmt.getViewName( ).accept( this );

		if ( stmt.getViewAliasClause( ) != null )
		{
			Element e_column_list = xmldoc.createElement( "column_list" );
			e_create_view.appendChild( e_column_list );
			elementStack.push( e_column_list );
			for ( int i = 0; i < stmt.getViewAliasClause( )
					.getViewAliasItemList( )
					.size( ); i++ )
			{
				TViewAliasItem viewAliasItem = stmt.getViewAliasClause( )
						.getViewAliasItemList( )
						.getViewAliasItem( i );
				if ( viewAliasItem.getAlias( ) == null )
					continue;
				viewAliasItem.getAlias( ).accept( this );
			}
			elementStack.pop( );
		}
		stmt.getSubquery( ).setDummyTag( TOP_STATEMENT );
		stmt.getSubquery( ).accept( this );
		elementStack.pop( );
	}

	public void preVisit( TCreateViewSqlStatement stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_create_view = xmldoc.createElement( "create_view_statement" );
		e_parent.appendChild( e_create_view );
		elementStack.push( e_create_view );
		if ( stmt.getViewAttributeList( ) != null )
		{
			for ( int i = 0; i < stmt.getViewAttributeList( ).size( ); i++ )
			{
				e_create_view.setAttribute( stmt.getViewAttributeList( )
						.getObjectName( i )
						.toString( )
						.toLowerCase( ), "true" );
			}
		}
		current_objectName_tag = "view_name";
		stmt.getViewName( ).accept( this );

		if ( stmt.getViewAliasClause( ) != null )
		{
			Element e_column_list = xmldoc.createElement( "column_list" );
			e_create_view.appendChild( e_column_list );
			elementStack.push( e_column_list );
			for ( int i = 0; i < stmt.getViewAliasClause( )
					.getViewAliasItemList( )
					.size( ); i++ )
			{
				TViewAliasItem viewAliasItem = stmt.getViewAliasClause( )
						.getViewAliasItemList( )
						.getViewAliasItem( i );
				if ( viewAliasItem.getAlias( ) == null )
					continue;
				viewAliasItem.getAlias( ).accept( this );
			}
			elementStack.pop( );
		}
		stmt.getSubquery( ).setDummyTag( TOP_STATEMENT );
		stmt.getSubquery( ).accept( this );
		elementStack.pop( );
	}

	public void postVisit( TCreateViewSqlStatement stmt )
	{

	}

	public void preVisit( TMssqlCreateTrigger stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_create_trigger = xmldoc.createElement( "create_trigger_statement" );
		e_parent.appendChild( e_create_trigger );
		elementStack.push( e_create_trigger );
		current_objectName_tag = "trigger_name";
		stmt.getTriggerName( ).accept( this );
		current_table_reference_tag = "onTable";
		stmt.getOnTable( ).accept( this );
		Element e_timing_point = xmldoc.createElement( "timing_point" );
		e_timing_point.setTextContent( stmt.getTimingPoint( ).toString( ) );
		e_create_trigger.appendChild( e_timing_point );
		for ( Iterator it = stmt.getDmlTypes( ).iterator( ); it.hasNext( ); )
		{
			ETriggerDmlType dmlType = (ETriggerDmlType) it.next( );
			Element e_dmltype = xmldoc.createElement( "dml_type" );
			e_dmltype.setTextContent( dmlType.toString( ) );
			e_create_trigger.appendChild( e_dmltype );
		}

		// e_create_trigger.setAttribute("dmlType",stmt.getDmlTypes().);

		current_statement_list_tag = "body_statement_list";
		if ( stmt.getBodyStatements( ).size( ) > 0 )
			stmt.getBodyStatements( ).accept( this );

		elementStack.pop( );
	}

	public void preVisit( TCreateSequenceStmt stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_create_sequence = xmldoc.createElement( "create_sequence_statement" );
		e_parent.appendChild( e_create_sequence );
		elementStack.push( e_create_sequence );
		current_objectName_tag = "sequence_name";
		stmt.getSequenceName( ).accept( this );
		if ( stmt.getOptions( ) != null )
		{
			for ( int i = 0; i < stmt.getOptions( ).size( ); i++ )
			{
				TSequenceOption sequenceOption = stmt.getOptions( )
						.get( i );
				switch ( sequenceOption.getSequenceOptionType( ) )
				{
					case start :
					case startWith :
						e_create_sequence.setAttribute( "start_with",
								sequenceOption.getOptionValue( ).toString( ) );
						break;
					case restart :
					case restartWith :
						e_create_sequence.setAttribute( "restart_with",
								sequenceOption.getOptionValue( ).toString( ) );
						break;
					case increment :
					case incrementBy :
						e_create_sequence.setAttribute( "increment_by",
								sequenceOption.getOptionValue( ).toString( ) );
						break;
					case minValue :
						e_create_sequence.setAttribute( "min_value",
								sequenceOption.getOptionValue( ).toString( ) );
						break;
					case maxValue :
						e_create_sequence.setAttribute( "max_value",
								sequenceOption.getOptionValue( ).toString( ) );
						break;
					case cycle :
						e_create_sequence.setAttribute( "cycle", "true" );
						break;
					case noCycle :
						e_create_sequence.setAttribute( "nocycle", "true" );
						break;
					case cache :
						e_create_sequence.setAttribute( "cache",
								sequenceOption.getOptionValue( ).toString( ) );
						break;
					case noCache :
						e_create_sequence.setAttribute( "nocache", "true" );
						break;
					case order :
						e_create_sequence.setAttribute( "order", "true" );
						break;
					case noOrder :
						e_create_sequence.setAttribute( "noorder", "true" );
						break;
					default :
						break;
				}

			}
		}
		elementStack.pop( );
	}

	public void preVisit( TCreateSynonymStmt stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_create_synonym = xmldoc.createElement( "create_synonym_statement" );
		e_parent.appendChild( e_create_synonym );
		elementStack.push( e_create_synonym );
		current_objectName_tag = "synonym_name";
		stmt.getSynonymName( ).accept( this );
		current_objectName_tag = "for_name";
		stmt.getForName( ).accept( this );
		elementStack.pop( );
	}

	public void preVisit( TCreateVariableStmt stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_create_variable = xmldoc.createElement( "create_variable_statement" );
		e_parent.appendChild( e_create_variable );
		elementStack.push( e_create_variable );
		current_objectName_tag = "variable_name";
		stmt.getVariableName( ).accept( this );
		stmt.getVariableDatatype( ).accept( this );
		elementStack.pop( );
	}

	public void preVisit( TCreateAliasStmt stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_create_alias = xmldoc.createElement( "create_alias_statement" );
		e_parent.appendChild( e_create_alias );
		elementStack.push( e_create_alias );
		current_objectName_tag = "alias_name";
		stmt.getAliasName( ).accept( this );
		e_create_alias.setAttribute( "object_type", stmt.getAliasType( )
				.toString( ) );
		switch ( stmt.getAliasType( ) )
		{
			case table :
				current_objectName_tag = "table_name";
				stmt.getTableAlias( ).accept( this );
				break;
			case module :
				current_objectName_tag = "module_name";
				stmt.getModuleAlias( ).accept( this );
				break;
			case sequence :
				current_objectName_tag = "sequence_name";
				stmt.getSequenceAlias( ).accept( this );
				break;
		}
		elementStack.pop( );
	}

	public void preVisit( TSetDatabaseObjectStmt stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_set_database = xmldoc.createElement( "set_database_object_statement" );
		e_parent.appendChild( e_set_database );
		e_set_database.setAttribute( "object_type", stmt.getObjectType( )
				.toString( ) );
		elementStack.push( e_set_database );
		current_objectName_tag = "object_name";
		stmt.getDatabaseObjectName( ).accept( this );
		elementStack.pop( );
	}

	public void preVisit( TExecParameter node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_exec_parameter = xmldoc.createElement( "exec_parameter" );
		e_parent.appendChild( e_exec_parameter );
		elementStack.push( e_exec_parameter );
		if ( node.getParameterName( ) != null )
		{
			current_objectName_tag = "parameter_name";
			node.getParameterName( ).accept( this );
		}
		current_expression_tag = "parameter_value";
		node.getParameterValue( ).accept( this );
		elementStack.pop( );
	}

	public void preVisit( TMssqlExecute stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_execute = xmldoc.createElement( "execute_statement" );
		e_parent.appendChild( e_execute );
		elementStack.push( e_execute );

		switch ( stmt.getExecType( ) )
		{
			case TBaseType.metExecSp :
				current_objectName_tag = "module_name";
				stmt.getModuleName( ).accept( this );
				if ( stmt.getParameters( ) != null )
				{
					for ( int i = 0; i < stmt.getParameters( ).size( ); i++ )
					{
						stmt.getParameters( )
								.getExecParameter( i )
								.accept( this );
					}
				}
				break;
			default :
				break;
		}

		elementStack.pop( );
	}

	public void preVisit( TMssqlDeclare stmt )
	{

		e_parent = (Element) elementStack.peek( );
		Element e_declare_varaible = xmldoc.createElement( "declare_variable_statement" );
		e_declare_varaible.setAttribute("declare_type",stmt.getDeclareType().toString());
		e_declare_varaible.setAttribute("with_return_only",String.valueOf(stmt.isWithReturnOnly()));
		e_parent.appendChild( e_declare_varaible );
		elementStack.push( e_declare_varaible );
		switch ( stmt.getDeclareType( ) )
		{
			case variable :
				if ( stmt.getDeclareType( ) == EDeclareType.variable )
				{
					stmt.getVariables( ).accept( this );
				}
				break;
			case cursor:
				current_objectName_tag = "cursor_name";
				stmt.getCursorName().accept(this);
				stmt.getSubquery().accept(this);
				break;
			case handlers:
				if (stmt.getHandleBlock() != null){
					stmt.getHandleBlock().accept(this);
				}else
					stmt.getBodyStatements().accept(this);
				break;
			case continueHandlers:
			case exitHandlers:
				if (stmt.getHandlerForConditions() != null){
					e_parent = (Element) elementStack.peek( );
					Element e_handler_for_conditions = xmldoc.createElement( "handler_for_conditions" );
					e_parent.appendChild( e_handler_for_conditions );
					elementStack.push( e_handler_for_conditions );


					for(int i=0;i<stmt.getHandlerForConditions().size();i++){
						stmt.getHandlerForConditions().get(i).accept(this);
					}

					elementStack.pop( );

				}
				if (stmt.getHandleBlock() != null){
					stmt.getHandleBlock().accept(this);
				}else
					stmt.getBodyStatements().accept(this);
				break;
			case conditions:
				current_objectName_tag = "condition_name";
				stmt.getConditionName().accept(this);
				if (stmt.getStateValue() != null){
					addElementOfNode("state_value",stmt.getStateValue());
				}else if (stmt.getErrorCode() != null){
					addElementOfNode("state_value",stmt.getErrorCode());
				}
				break;
			default :
				// if (stmt.getSubquery() != null)
				// stmt.getSubquery().accept(this);
				break;
		}
		elementStack.pop( );

	}


	public void preVisit( THandlerForCondition node ) {
		e_parent = (Element) elementStack.peek( );
		Element e_declare_handler_for = xmldoc.createElement( "declare_handler_for" );
		e_declare_handler_for.setAttribute("type",node.getHandlerForType().toString());
		e_parent.appendChild( e_declare_handler_for );
		elementStack.push( e_declare_handler_for );
		switch (node.getHandlerForType()){
			case conditionName:
				addElementOfNode("condition_name",node.getConditionName());
				break;
			case sqlstate:
				addElementOfNode("sqlstate_code",node.getSqlstateCode());
				break;
		}

		elementStack.pop( );

	}

	public void preVisit( TMssqlSet stmt )
	{
		Element e_set_command;
		e_parent = (Element) elementStack.peek( );
		switch ( stmt.getSetType( ) )
		{
			case TBaseType.mstUnknown :
				e_set_command = xmldoc.createElement( "mssql_set_command" );
				e_parent.appendChild( e_set_command );
				elementStack.push( e_set_command );
				e_set_command.setTextContent( stmt.toString( ) );
				elementStack.pop( );
				break;
			case TBaseType.mstLocalVar :
				Element e_set_variable = xmldoc.createElement( "mssql_set_variable_statement" );
				e_parent.appendChild( e_set_variable );
				elementStack.push( e_set_variable );
				current_objectName_tag = "variable_name";
				stmt.getVarName( ).accept( this );
				current_expression_tag = "variable_value";
				stmt.getVarExpr( ).accept( this );
				elementStack.pop( );
				break;
			case TBaseType.mstLocalVarCursor :
				e_set_command = xmldoc.createElement( "mssql_set_command" );
				e_parent.appendChild( e_set_command );
				elementStack.push( e_set_command );
				e_set_command.setTextContent( stmt.toString( ) );
				elementStack.pop( );
				break;
			case TBaseType.mstSetCmd :
				e_set_command = xmldoc.createElement( "mssql_set_command" );
				e_parent.appendChild( e_set_command );
				elementStack.push( e_set_command );
				e_set_command.setTextContent( stmt.toString( ) );
				elementStack.pop( );
				break;
			case TBaseType.mstXmlMethod :
				e_set_command = xmldoc.createElement( "mssql_set_command" );
				e_parent.appendChild( e_set_command );
				elementStack.push( e_set_command );
				e_set_command.setTextContent( stmt.toString( ) );
				elementStack.pop( );
				break;
			case TBaseType.mstSybaseLocalVar :
				e_set_command = xmldoc.createElement( "mssql_set_command" );
				e_parent.appendChild( e_set_command );
				elementStack.push( e_set_command );
				e_set_command.setTextContent( stmt.toString( ) );
				elementStack.pop( );
				break;
			default :
				e_set_command = xmldoc.createElement( "mssql_set_command" );
				e_parent.appendChild( e_set_command );
				elementStack.push( e_set_command );
				e_set_command.setTextContent( stmt.toString( ) );
				elementStack.pop( );
				break;

		}

		// appendStartTagWithIntProperty(stmt,"type",stmt.getSetType());
		// if (stmt.getSetType() == TBaseType.mstLocalVar){
		//
		// appendStartTagWithIntProperty(stmt,
		// "variableName",
		// stmt.getVarName().toString(),
		// "value",
		// stmt.getVarExpr().toString());
		//
		// }
	}

	public void preVisit( TMergeSqlStatement stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_merge = xmldoc.createElement( "merge_statement" );
		e_parent.appendChild( e_merge );
		elementStack.push( e_merge );

		if ( stmt.getCteList( ) != null )
		{
			stmt.getCteList( ).accept( this );
		}

		current_table_reference_tag = "target_table";
		stmt.getTargetTable( ).accept( this );

		addElementOfNode("using_clause",stmt.getUsingTable( ));
//		current_table_reference_tag = "source_table";
//		stmt.getUsingTable( ).accept( this );

		current_expression_tag = "search_condition";
		stmt.getCondition( ).accept( this );

		// if (stmt.getColumnList() != null) stmt.getColumnList().accept(this);
		if ( stmt.getWhenClauses( ) != null )
		{
			for ( int i = 0; i < stmt.getWhenClauses( ).size( ); i++ )
			{
				TMergeWhenClause whenClause = stmt.getWhenClauses( )
						.getElement( i );
				whenClause.accept( this );
			}
			// stmt.getWhenClauses().accept(this);
		}
		if (stmt.getOutputClause() != null)
		 stmt.getOutputClause().accept(this);
		// if (stmt.getErrorLoggingClause() != null)
		// stmt.getErrorLoggingClause().accept(this);
		elementStack.pop( );
	}


	public void preVisit( TOutputClause node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_output_clause = xmldoc.createElement( "output_clause" );
		e_parent.appendChild( e_output_clause );
		elementStack.push( e_output_clause );
		for(int i=0;i<node.getSelectItemList().size();i++){
			node.getSelectItemList().getElement(i).accept(this);
		}
		elementStack.pop( );

	}

	public void preVisit( TCreateIndexSqlStatement stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_create_index = xmldoc.createElement( "create_index_statement" );
		e_parent.appendChild( e_create_index );
		elementStack.push( e_create_index );
		// e_create_index.setAttribute("clustered",stmt.get);
		if ( stmt.getIndexName( ) != null )
		{
			current_objectName_tag = "index_name";
			stmt.getIndexName( ).accept( this );
		}
		else
		{
			// teradata allow empty index name
		}
		current_objectName_tag = "on_name";
		stmt.getTableName( ).accept( this );

		Element e_column_list = xmldoc.createElement( "column_with_sort_list" );
		e_create_index.appendChild( e_column_list );
		for ( int i = 0; i < stmt.getColumnNameList( ).size( ); i++ )
		{
			TOrderByItem orderByItem = stmt.getColumnNameList( )
					.getOrderByItem( i );
			Element e_column = xmldoc.createElement( "column_with_sort" );
			e_column.setAttribute( "sort_order", orderByItem.getSortOrder( )
					.toString( ) );
			e_column_list.appendChild( e_column );
			elementStack.push( e_column );
			current_expression_tag = "column_expr";
			orderByItem.getSortKey( ).accept( this );
			elementStack.pop( );
		}

		elementStack.pop( );
	}

	public void preVisit( TCreateTableSqlStatement stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_create_table = xmldoc.createElement( "create_table_statement" );
		e_create_table.setAttribute("source_type",stmt.getTableSourceType().toString());
		e_create_table.setAttribute("is_external",stmt.isExternal()?"true":"false");
		e_parent.appendChild( e_create_table );
		elementStack.push( e_create_table );
		current_table_reference_tag = "table_name";
		stmt.getTargetTable( ).accept( this );

		if ( stmt.getColumnList( ).size( ) > 0 )
		{
			Element e_column_list = xmldoc.createElement( "column_definition_list" );
			e_create_table.appendChild( e_column_list );
			elementStack.push( e_column_list );
			stmt.getColumnList( ).accept( this );
			elementStack.pop( );
		}

		if ( ( stmt.getTableConstraints( ) != null )
				&& ( stmt.getTableConstraints( ).size( ) > 0 ) )
		{
			Element e_constraint_list = xmldoc.createElement( "table_constraint_list" );
			e_create_table.appendChild( e_constraint_list );
			elementStack.push( e_constraint_list );
			stmt.getTableConstraints( ).accept( this );
			elementStack.pop( );
		}

		if ( stmt.getSubQuery( ) != null )
		{
			current_query_expression_tag = "subquery";
			stmt.getSubQuery( ).accept( this );
		}

		switch (stmt.getTableSourceType()){
			case like:
				Element e_like_table = xmldoc.createElement( "like_table" );
				e_create_table.appendChild( e_like_table );
				elementStack.push( e_like_table );
				stmt.getLikeTableName().accept(this);
				elementStack.pop( );

				break;
		}

		if (stmt.getTableOptions() != null){
			for(TCreateTableOption option:stmt.getTableOptions()){
				option.accept(this);
			}
		}

		if (stmt.getHiveTablePartition() != null){
			stmt.getHiveTablePartition().accept(this);
		}
		if (stmt.getTableLocation() != null){
			addElementOfNode("table_location",stmt.getTableLocation());
		}

		if (stmt.getIndexDefinitions() != null){
			Element e_element = xmldoc.createElement( "index_definition_list" );
			e_parent = (Element) elementStack.peek( );
			e_parent.appendChild( e_element );
			elementStack.push(e_element);
			for(int i=0;i<stmt.getIndexDefinitions().size();i++){
				stmt.getIndexDefinitions().get(i).accept(this);
			}
			elementStack.pop();
		}

		elementStack.pop( );
	}


	public void preVisit( TIndexDefinition node ) {
		e_parent = (Element) elementStack.peek();
		Element e_index_definition = xmldoc.createElement("index_definition");
		e_index_definition.setAttribute("primary", String.valueOf(node.isPrimary()));
		e_index_definition.setAttribute("unique", String.valueOf(node.isUnique()));
		e_index_definition.setAttribute("all", String.valueOf(node.isAll()));
		e_parent.appendChild(e_index_definition);
		elementStack.push(e_index_definition);

		if (node.getIndexName() != null){
			node.getIndexName().accept(this);
		}

		if (node.getIndexColumns() != null){
			node.getIndexColumns().accept(this);
		}

		if (node.getPartitionExprList() != null){
			addElementOfNode("partion_clause", node.getPartitionExprList());
		}

		elementStack.pop();
	}

	public void preVisit( THiveTablePartition node ) {
		e_parent = (Element) elementStack.peek();
		Element e_table_partition = xmldoc.createElement("table_partition");
		e_parent.appendChild(e_table_partition);
		elementStack.push(e_table_partition);
		node.getColumnDefList().accept(this);
		elementStack.pop();
	}

	public void preVisit( TCreateTableOption node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_create_table_option = xmldoc.createElement( "create_table_option" );
		e_create_table_option.setAttribute("type",node.getCreateTableOptionType().toString());
		e_parent.appendChild( e_create_table_option );
		elementStack.push( e_create_table_option );

		switch (node.getCreateTableOptionType()){
			case etoWithLocation:
				node.getStageLocation().accept(this);
				break;
		}

		elementStack.pop();
	}

	public void preVisit( TStageLocation node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_stage = xmldoc.createElement( "stage_location" );
		e_stage.setAttribute("type",node.getStageLocationType().toString());

		e_parent.appendChild( e_stage );
		elementStack.push( e_stage );
		if (node.getStageName() != null){
			addElementOfNode("stage_name",node.getStageName());
		}
		if (node.getNameSpace() != null){
			addElementOfNode("namespace",node.getNameSpace());
		}
		elementStack.pop();
	}



	public void preVisit( TDropIndexSqlStatement stmt )
	{
	}

	public void preVisit( TLeaveStmt stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_leave = xmldoc.createElement( "leave_statement" );
		e_parent.appendChild( e_leave );

		elementStack.push( e_leave );
		current_objectName_tag = "label_name";
		stmt.getCursorName( ).accept( this );

		elementStack.pop( );
	}


	public void preVisit( TDropTableSqlStatement stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_drop = xmldoc.createElement( "drop_table_statement" );
		e_drop.setAttribute("if_exists",String.valueOf(stmt.isIfExists()));
		e_drop.setAttribute("table_kind",String.valueOf(stmt.getTableKind()));

		e_parent.appendChild( e_drop );
		elementStack.push( e_drop );
		current_objectName_tag = "table_name";
		stmt.getTableName( ).accept( this );
		elementStack.pop( );
	}

	public void preVisit( TTruncateStatement stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_truncate = xmldoc.createElement( "truncate_table_statement" );
		e_parent.appendChild( e_truncate );
		elementStack.push( e_truncate );
		current_objectName_tag = "table_name";
		stmt.getTableName( ).accept( this );
		elementStack.pop( );
	}

	public void preVisit( TDropViewSqlStatement stmt )
	{
		appendStartTagWithIntProperty( stmt, "name", stmt.getViewName( )
				.toString( ) );
	}

	public void preVisit( TDeleteSqlStatement stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_delete = xmldoc.createElement( "delete_statement" );
		e_parent.appendChild( e_delete );
		elementStack.push( e_delete );

		if ( stmt.getCteList( ) != null )
		{
			stmt.getCteList( ).accept( this );
		}

		if ( stmt.getTopClause( ) != null )
		{
			stmt.getTopClause( ).accept( this );
		}

		current_table_reference_tag = "target_table";
		stmt.getTargetTable( ).accept( this );

		if ( stmt.joins.size( ) > 0 )
		{

			Element e_from_clause = xmldoc.createElement( "from_clause" );
			e_parent = (Element) elementStack.peek( );
			e_parent.appendChild( e_from_clause );
			elementStack.push( e_from_clause );
			stmt.joins.accept( this );
			elementStack.pop( );

		}

		if ( stmt.getOutputClause( ) != null )
		{
			stmt.getOutputClause( ).accept( this );
		}

		if ( stmt.getWhereClause( ) != null )
		{
			stmt.getWhereClause( ).accept( this );
		}

		if ( stmt.getReturningClause( ) != null )
		{
			stmt.getReturningClause( ).accept( this );
		}

		elementStack.pop( );

	}

	public void postVisit( TDeleteSqlStatement stmt )
	{

	}

	public void preVisit( TUpdateSqlStatement stmt )
	{

		e_parent = (Element) elementStack.peek( );
		Element e_update = xmldoc.createElement( "update_statement" );
		e_parent.appendChild( e_update );
		elementStack.push( e_update );

		if ( stmt.getCteList( ) != null )
		{
			stmt.getCteList( ).accept( this );
		}

		if ( stmt.getTopClause( ) != null )
		{
			stmt.getTopClause( ).accept( this );
		}

		current_table_reference_tag = "target_table";
		stmt.getTargetTable( ).accept( this );

		for ( int i = 0; i < stmt.getResultColumnList( ).size( ); i++ )
		{
			current_expression_tag = "set_clause";
			stmt.getResultColumnList( )
					.getResultColumn( i )
					.getExpr( )
					.accept( this );
		}

		if ( stmt.joins.size( ) > 0 )
		{
			Element e_from_clause = xmldoc.createElement( "from_clause" );
			e_parent = (Element) elementStack.peek( );
			e_parent.appendChild( e_from_clause );
			elementStack.push( e_from_clause );
			stmt.joins.accept( this );
			elementStack.pop( );
		}

		if ( stmt.getWhereClause( ) != null )
		{
			stmt.getWhereClause( ).accept( this );
		}

		if ( stmt.getOrderByClause( ) != null )
		{
			stmt.getOrderByClause( ).accept( this );
		}

		if ( stmt.getLimitClause( ) != null )
		{
			stmt.getLimitClause( ).accept( this );
		}

		if ( stmt.getOutputClause( ) != null )
		{
			stmt.getOutputClause( ).accept( this );
		}

		if ( stmt.getReturningClause( ) != null )
		{
			stmt.getReturningClause( ).accept( this );
		}

		elementStack.pop( );

	}

	public void preVisit( TWithinGroup withinGroup )
	{
		Element e_functionCall = (Element) elementStack.peek( );
		Element e_within_group = xmldoc.createElement( "within_group" );
		e_functionCall.appendChild( e_within_group );
		elementStack.push( e_within_group );
		withinGroup.getOrderBy( ).accept( this );
		elementStack.pop( );

	}

	public void preVisit( TKeepDenseRankClause keepDenseRankClause )
	{
		Element e_functionCall = (Element) elementStack.peek( );
		Element e_keepDenseRank = xmldoc.createElement( "keep_dense_rank" );
		e_functionCall.appendChild( e_keepDenseRank );
		e_keepDenseRank.setAttribute( "first",
				String.valueOf( keepDenseRankClause.isFirst( ) ) );
		e_keepDenseRank.setAttribute( "last",
				String.valueOf( keepDenseRankClause.isLast( ) ) );
		elementStack.push( e_keepDenseRank );
		keepDenseRankClause.getOrderBy( ).accept( this );
		elementStack.pop( );
	}

	public void preVisit( TWindowDef windowDef )
	{
		// TWindowDef windowDef = node.getWindowDef();
		Element e_functionCall = (Element) elementStack.peek( );

		if ( windowDef.getWithinGroup( ) != null )
		{
			windowDef.getWithinGroup( ).accept( this );
		}

		if ( windowDef.getKeepDenseRankClause( ) != null )
		{
			windowDef.getKeepDenseRankClause( ).accept( this );
		}

		if ( windowDef.isIncludingOverClause( ) )
		{
			Element e_overClause = xmldoc.createElement( "over_clause" );
			elementStack.push( e_overClause );
			e_functionCall.appendChild( e_overClause );
			if ( windowDef.getPartitionClause( ) != null )
			{
				Element e_partition = xmldoc.createElement( "partition_clause" );
				e_overClause.appendChild( e_partition );
				elementStack.push( e_partition );
				current_expression_list_tag = "partitions";
				windowDef.getPartitionClause( )
						.getExpressionList( )
						.accept( this );
				elementStack.pop( );
			}

			if ( windowDef.getOrderBy( ) != null )
			{
				windowDef.getOrderBy( ).accept( this );
			}

			if ( windowDef.getWindowFrame( ) != null )
			{
				TWindowFrame windowFrame = windowDef.getWindowFrame( );
				Element e_winFrame = xmldoc.createElement( "window_frame" );
				e_overClause.appendChild( e_winFrame );
				e_winFrame.setAttribute( "type", windowFrame.getLimitRowType( )
						.toString( ) );
				elementStack.push( e_winFrame );
				windowFrame.getStartBoundary( ).accept( this );
				if ( windowFrame.getEndBoundary( ) != null )
				{
					windowFrame.getEndBoundary( ).accept( this );
				}
				elementStack.pop( );
			}

			elementStack.pop( );// e_overClause
		}

	}

	public void preVisit( TFunctionCall node )
	{
		String tag_name = TAG_FUNCTIONCALL;
		if ( current_functionCall_tag != null )
		{
			tag_name = current_functionCall_tag;
			current_functionCall_tag = null;
		}
		e_parent = (Element) elementStack.peek( );
		Element e_functionCall = xmldoc.createElement( tag_name );
		e_parent.appendChild( e_functionCall );
		e_functionCall.setAttribute( "type", node.getFunctionType( ).toString( ) );
		e_functionCall.setAttribute( "aggregateType", node.getAggregateType( )
				.toString( ) );
		e_functionCall.setAttribute( "builtIn",
				( node.isBuiltIn( dbVendor ) ) ? "true" : "false" );

		elementStack.push( e_functionCall );
		current_objectName_tag = TAG_FUNCTIONNAME;
		node.getFunctionName( ).accept( this );
		current_objectName_tag = null;

		current_expression_list_tag = TAG_FUNCTIONARGS;
		Element e_expression_list = null;
		Element e_function = null;

		switch ( node.getFunctionType( ) )
		{
			case unknown_t :
				e_function = xmldoc.createElement( TAG_GENERIC_FUNCTION );
				e_functionCall.appendChild( e_function );
				elementStack.push( e_function );
				if ( node.getArgs( ) != null )
				{
					node.getArgs( ).accept( this );
				}
				elementStack.pop( );
				break;
			case udf_t :
			case case_n_t :
			case chr_t :
				e_function = xmldoc.createElement( TAG_GENERIC_FUNCTION );
				e_functionCall.appendChild( e_function );
				elementStack.push( e_function );
				if ( node.getArgs( ) != null )
				{
					node.getArgs( ).accept( this );
				}
				if ( node.getAnalyticFunction( ) != null )
				{
					node.getAnalyticFunction( ).accept( this );
				}
				elementStack.pop( );
				break;
			case cast_t :
				e_function = xmldoc.createElement( TAG_CAST_FUNCTION );
				e_functionCall.appendChild( e_function );
				elementStack.push( e_function );
				node.getExpr1( ).accept( this );
				node.getTypename( ).accept( this );
				elementStack.pop( );
				break;
			case convert_t :
				e_function = xmldoc.createElement( TAG_CONVERT_FUNCTION );
				e_functionCall.appendChild( e_function );
				elementStack.push( e_function );
				if ( node.getTypename( ) != null )
				{
					node.getTypename( ).accept( this );
				}
				else
				{
					// convert in MySQL have no datatype argument
				}
				node.getParameter( ).accept( this );

				elementStack.pop( );

				break;
			case trim_t :
				e_function = xmldoc.createElement( TAG_TRIM_FUNCTION );
				e_functionCall.appendChild( e_function );
				elementStack.push( e_function );
				if ( node.getTrimArgument( ) != null )
				{
					// node.getTrimArgument().accept(this);
					TTrimArgument trimArgument = node.getTrimArgument( );
					if ( trimArgument.getBoth_trailing_leading( ) != null )
					{
						Element e_trim_style = xmldoc.createElement( "style" );
						e_trim_style.setTextContent( trimArgument.getBoth_trailing_leading( )
								.toString( ) );
						e_function.appendChild( e_trim_style );
					}
					if ( trimArgument.getTrimCharacter( ) != null )
					{
						current_expression_tag = "char_expr";
						trimArgument.getTrimCharacter( ).accept( this );
					}
					current_expression_tag = "source_expr";
					trimArgument.getStringExpression( ).accept( this );
				}
				elementStack.pop( );

				break;
			case extract_t :
				e_function = xmldoc.createElement( TAG_EXTRACT_FUNCTION );
				e_functionCall.appendChild( e_function );
				elementStack.push( e_function );
				if ( node.getArgs( ) != null )
				{ // extract xml
					current_expression_list_tag = "functionArgs";
					node.getArgs( ).accept( this );
				}
				else
				{
					Element e_time = xmldoc.createElement( "time" );
					e_time.setTextContent( node.getExtract_time_token( )
							.toString( ) );
					e_function.appendChild( e_time );

					if ( node.getExpr1( ) != null )
					{
						node.getExpr1( ).accept( this );
					}
				}

				elementStack.pop( );
				break;
			case treat_t :
				e_function = xmldoc.createElement( TAG_TREAT_FUNCTION );
				e_functionCall.appendChild( e_function );
				elementStack.push( e_function );
				node.getExpr1( ).accept( this );
				node.getTypename( ).accept( this );
				elementStack.pop( );
				break;
			case contains_t :
				e_function = xmldoc.createElement( TAG_CONTAINS_FUNCTION );
				e_functionCall.appendChild( e_function );
				elementStack.push( e_function );
				current_expression_tag = "column_reference";
				current_expression_list_tag = null;
				node.getExpr1( ).accept( this );
				current_expression_tag = "value_expression";
				node.getExpr2( ).accept( this );
				elementStack.pop( );
				break;
			case freetext_t :
				e_function = xmldoc.createElement( TAG_CONTAINS_FUNCTION );
				e_functionCall.appendChild( e_function );
				elementStack.push( e_function );
				current_expression_tag = "column_reference";
				current_expression_list_tag = null;
				node.getExpr1( ).accept( this );
				current_expression_tag = "value_expression";
				node.getExpr2( ).accept( this );
				elementStack.pop( );
				break;
			case substring_t :
				e_function = xmldoc.createElement( "substring_function" );
				e_functionCall.appendChild( e_function );
				elementStack.push( e_function );
				current_expression_tag = "string_expression";
				node.getExpr1( ).accept( this );
				if ( node.getExpr2( ) != null )
				{
					current_expression_tag = "from_expression";
					node.getExpr2( ).accept( this );
				}
				if ( node.getExpr3( ) != null )
				{
					current_expression_tag = "for_expression";
					node.getExpr3( ).accept( this );
				}
				elementStack.pop( );
				break;
			case range_n_t :
			case position_t :
			case xmlquery_t :
			case xmlcast_t :
			case match_against_t :
			case adddate_t :
			case date_add_t :
			case subdate_t :
			case date_sub_t :
			case timestampadd_t :
			case timestampdiff_t :
				Element e_not_support = xmldoc.createElement( "not_decode_function" );
				e_functionCall.appendChild( e_not_support );
				e_not_support.setTextContent( node.toString( ) );
				break;
			case struct_t://bigquery
				addElementOfNode("field_values",node.getFieldValues());
				if (node.getFieldDefs() != null){
					addElementOfNode("field_defs",node.getFieldDefs());
				}
				break;
			default :
				e_function = xmldoc.createElement( TAG_GENERIC_FUNCTION );
				e_functionCall.appendChild( e_function );
				elementStack.push( e_function );
				if ( node.getArgs( ) != null )
				{
					node.getArgs( ).accept( this );
				}
				elementStack.pop( );
				break;
		}

		if ( node.getWindowDef( ) != null )
		{
			node.getWindowDef( ).accept( this );
		}
		current_expression_list_tag = null;
		elementStack.pop( );

	}

	public void preVisit( TWindowFrameBoundary boundary )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_boundary = xmldoc.createElement( "window_frame_boundary" );
		e_parent.appendChild( e_boundary );
		e_boundary.setAttribute( "type", boundary.getBoundaryType( ).toString( ) );
		if ( boundary.getBoundaryNumber( ) != null )
		{
			e_boundary.setAttribute( "offset_value",
					boundary.getBoundaryNumber( ).toString( ) );
		}

		// elementStack.push(e_insert);
	}

	public void preVisit( TInsertSqlStatement stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_insert = xmldoc.createElement( "insert_statement" );
		e_parent.appendChild( e_insert );
		e_insert.setAttribute( "insertSource", stmt.getInsertSource( )
				.toString( ) );

		elementStack.push( e_insert );

		if ( stmt.getCteList( ) != null )
		{
			stmt.getCteList( ).accept( this );
		}

		if ( stmt.getTargetTable( ) != null )
		{
			current_table_reference_tag = "target_table";
			stmt.getTargetTable( ).accept( this );
		}
		else
		{
			// hive insert may have no target table
		}

		if ( stmt.getColumnList( ) != null )
		{
			current_objectName_list_tag = "column_list";
			stmt.getColumnList( ).accept( this );
		}

		//
		// if (stmt.getTopClause() != null){
		// stmt.getTopClause().accept(this);
		// }
		//
		//
		//
		// if (stmt.getOutputClause() != null){
		// stmt.getOutputClause().accept(this);
		// }
		//
		switch ( stmt.getInsertSource( ) )
		{
			case values :
				Element e_insert_values = xmldoc.createElement( "insert_values" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_insert_values );
				elementStack.push( e_insert_values );
				Element e_row_values;
				for ( int i = 0; i < stmt.getValues( ).size( ); i++ )
				{
					e_row_values = xmldoc.createElement( "row_values" );
					e_insert_values.appendChild( e_row_values );
					elementStack.push( e_row_values );
					TMultiTarget multiTarget = stmt.getValues( )
							.getMultiTarget( i );

					for ( int j = 0; j < multiTarget.getColumnList( ).size( ); j++ )
					{
						if ( multiTarget.getColumnList( )
								.getResultColumn( j )
								.isPlaceHolder( ) )
							continue; // teradata allow empty value
						multiTarget.getColumnList( )
								.getResultColumn( j )
								.getExpr( )
								.accept( this );
					}

					elementStack.pop( ); // e_row_values
				}
				elementStack.pop( ); // e_insert_values

				break;
			case subquery :
				current_query_expression_tag = "insert_query";
				stmt.getSubQuery( ).accept( this );
				break;
			case values_empty :
				break;
			case values_function :
				// stmt.getFunctionCall().accept(this);
				break;
			case values_oracle_record :
				// stmt.getRecordName().accept(this);
				break;
			case set_column_value :
				// stmt.getSetColumnValues().accept(this);
				break;
			case execute :
				Element e_insert_execute = xmldoc.createElement( "insert_execute" );
				e_parent = (Element) elementStack.peek( );
				e_parent.appendChild( e_insert_execute );
				elementStack.push( e_insert_execute );
				stmt.getExecuteStmt( ).accept( this );
				elementStack.pop( );
				break;
			default :
				break;
		}
		//
		// if (stmt.getReturningClause() != null){
		// stmt.getReturningClause().accept(this);
		// }
		elementStack.pop( );
	}

	public void postVisit( TInsertSqlStatement stmt )
	{
		appendEndTag( stmt );
	}

	public void preVisit( TMultiTarget node )
	{
		appendStartTag( node );
		if ( node.getColumnList( ) != null )
		{
			node.getColumnList( ).accept( this );
		}

		if ( node.getSubQuery( ) != null )
		{
			node.getSubQuery( ).accept( this );
		}
	}

	public void preVisit( TMultiTargetList node )
	{
		appendStartTag( node );
		for ( int i = 0; i < node.size( ); i++ )
		{
			node.getMultiTarget( i ).accept( this );
		}
	}

	public void postVisit( TMultiTargetList node )
	{
		appendEndTag( node );
	}

	public void preVisit( TCTE node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_cte = xmldoc.createElement( "cte" );
		e_parent.appendChild( e_cte );
		elementStack.push( e_cte );
		current_objectName_tag = "expression_name";
		node.getTableName( ).accept( this );

		if ( node.getColumnList( ) != null )
		{
			current_objectName_list_tag = "column_list";
			node.getColumnList( ).accept( this );
		}
		if ( node.getSubquery( ) != null )
		{
			node.getSubquery( ).setDummyTag( TOP_STATEMENT );
			node.getSubquery( ).accept( this );
		}
		else if ( node.getUpdateStmt( ) != null )
		{
			node.getUpdateStmt( ).accept( this );
		}
		else if ( node.getInsertStmt( ) != null )
		{
			node.getInsertStmt( ).accept( this );
		}
		else if ( node.getDeleteStmt( ) != null )
		{
			node.getDeleteStmt( ).accept( this );
		}

		elementStack.pop( );
	}

	public void postVisit( TCTE node )
	{

	}

	public void preVisit( TCTEList node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_cte_list = xmldoc.createElement( "cte_list" );
		e_parent.appendChild( e_cte_list );
		elementStack.push( e_cte_list );
		for ( int i = 0; i < node.size( ); i++ )
		{
			node.getCTE( i ).accept( this );
		}
		elementStack.pop( );
	}

	public void postVisit( TCTEList node )
	{
		appendEndTag( node );
	}

	public void preVisit( TAssignStmt stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_assign_stmt = xmldoc.createElement( "assignment_statement" );
		e_assign_stmt.setAttribute("type",stmt.getAssignType().toString());
		e_parent.appendChild( e_assign_stmt );
		elementStack.push( e_assign_stmt );

		switch (stmt.getAssignType()){
			case normal:
				current_expression_tag = "left";
				if (stmt.getVariableName() != null){
					stmt.getVariableName().accept(this);
				}else{
					stmt.getLeft( ).accept( this );
				}

				current_expression_tag = "right";
				stmt.getExpression( ).accept( this );
				break;
			case variableAssignment:
				addElementOfNode("variable",stmt.getVariableName());
				stmt.getExpression( ).accept( this );
				break;
			case cursorAssignment:
				addElementOfNode("variable",stmt.getVariableName());
				stmt.getQuery().accept(this);
				break;
			case resultsetAssignment:
				break;
		}
		elementStack.pop( );
	}

	public void preVisit( TMssqlCreateXmlSchemaCollectionStmt stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_create_xml_schema_collection_stmt = xmldoc.createElement( "create_xml_schema_collection_statement" );
		e_parent.appendChild( e_create_xml_schema_collection_stmt );
		elementStack.push( e_create_xml_schema_collection_stmt );
		current_objectName_tag = "schema_name";
		stmt.getSchemaName( ).accept( this );
		current_expression_tag = "expr";
		stmt.getExpr( ).accept( this );
		elementStack.pop( );
	}

	public void preVisit( TIfStmt node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_if_stmt = xmldoc.createElement( "if_statement" );
		e_parent.appendChild( e_if_stmt );
		elementStack.push( e_if_stmt );
		current_expression_tag = "condition";
		node.getCondition( ).accept( this );
		current_statement_list_tag = "then_statement_list";
		node.getThenStatements( ).accept( this );
		if ( node.getElseifStatements( ).size( ) > 0 )
		{
			Element e_elsif_cause_list = xmldoc.createElement( "elsif_clause_list" );
			e_if_stmt.appendChild( e_elsif_cause_list );
			elementStack.push( e_elsif_cause_list );
			for ( int i = 0; i < node.getElseifStatements( ).size( ); i++ )
			{
				TElsifStmt elsifStmt = (TElsifStmt) node.getElseifStatements( )
						.get( i );

				Element e_elsif_cause = xmldoc.createElement( "elsif_clause" );
				e_elsif_cause_list.appendChild( e_elsif_cause );
				elementStack.push( e_elsif_cause );
				current_expression_tag = "condition";
				elsifStmt.getCondition( ).accept( this );
				elsifStmt.getThenStatements( ).accept( this );
				elementStack.pop( );
			}

			elementStack.pop( );
		}
		if ( node.getElseStatements( ).size( ) > 0 )
		{
			current_statement_list_tag = "else_statement_list";
			node.getElseStatements( ).accept( this );
		}

		elementStack.pop( );
	}

	public void preVisit( TMssqlIfElse node )
	{
		e_parent = (Element) elementStack.peek( );

		Element e_if_stmt = xmldoc.createElement( "if_statement" );
		e_parent.appendChild( e_if_stmt );
		elementStack.push( e_if_stmt );
		if ( node.getCondition( ) != null )
		{
			current_expression_tag = "condition";
			node.getCondition( ).accept( this );
		}

		current_statement_list_tag = "then_statement_list";
		TStatementList ifList = new TStatementList( );
		ifList.add( node.getStmt( ) );
		ifList.accept( this );

		if ( node.getElseStmt( ) != null )
		{
			current_statement_list_tag = "else_statement_list";
			TStatementList elseList = new TStatementList( );
			elseList.add( node.getElseStmt( ) );
			elseList.accept( this );
		}
		elementStack.pop( );
	}

	public void preVisit( TBasicStmt node )
	{
		// appendStartTag(node);
		// outputNodeData(node);
		e_parent = (Element) elementStack.peek( );
		Element e_basic_stmt = xmldoc.createElement( "plsql_basic_statement" );
		e_parent.appendChild( e_basic_stmt );
		elementStack.push( e_basic_stmt );
		node.getExpr( ).accept( this );
		elementStack.pop( );

	}

	public void preVisit( TCaseStmt node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_case_stmt = xmldoc.createElement( "case_stmt" );
		e_parent.appendChild( e_case_stmt );
		if ( node.getCaseExpr( ) != null )
		{
			elementStack.push( e_case_stmt );
			node.getCaseExpr( ).accept( this );
			elementStack.pop( );
		}
	}

	public void preVisit( TCaseExpression node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_case_expr = xmldoc.createElement( TAG_CASE_EXPR );
		e_parent.appendChild( e_case_expr );
		elementStack.push( e_case_expr );

		if ( node.getInput_expr( ) != null )
		{
			current_expression_tag = "input_expression";
			node.getInput_expr( ).accept( this );
		}

		Element e_when_then;// = xmldoc.createElement("when_then_clause");
		for ( int i = 0; i < node.getWhenClauseItemList( ).size( ); i++ )
		{
			e_when_then = xmldoc.createElement( "when_then_clause" );
			e_case_expr.appendChild( e_when_then );
			elementStack.push( e_when_then );
			current_expression_tag = "search_expression";
			node.getWhenClauseItemList( )
					.getWhenClauseItem( i )
					.getComparison_expr( )
					.accept( this );
			current_expression_tag = "result_expression";
			if ( node.getWhenClauseItemList( )
					.getWhenClauseItem( i )
					.getReturn_expr( ) != null )
			{
				node.getWhenClauseItemList( )
						.getWhenClauseItem( i )
						.getReturn_expr( )
						.accept( this );
			}
			else if ( node.getWhenClauseItemList( )
					.getWhenClauseItem( i )
					.getStatement_list( ) != null )
			{
				Element result_expression = xmldoc.createElement( current_expression_tag );
				e_when_then.appendChild( result_expression );
				elementStack.push( result_expression );
				node.getWhenClauseItemList( )
						.getWhenClauseItem( i )
						.getStatement_list( )
						.accept( this );
				elementStack.pop( );
			}
			elementStack.pop( );
		}

		if ( node.getElse_expr( ) != null )
		{
			current_expression_tag = "else_expression";
			node.getElse_expr( ).accept( this );
		}

		if ( node.getElse_statement_list( ).size( ) > 0 )
		{
			node.getElse_statement_list( ).accept( this );
		}
		elementStack.pop( );
	}

	public void preVisit( TWhenClauseItemList node )
	{
		appendStartTag( node );
		for ( int i = 0; i < node.size( ); i++ )
		{
			node.getWhenClauseItem( i ).accept( this );
		}

	}

	public void postVisit( TWhenClauseItemList node )
	{
		appendEndTag( node );
	}

	public void preVisit( TWhenClauseItem node )
	{
		appendStartTag( node );
		node.getComparison_expr( ).accept( this );
		if ( node.getReturn_expr( ) != null )
		{
			node.getReturn_expr( ).accept( this );
		}
		else if ( node.getStatement_list( ).size( ) > 0 )
		{
			node.getStatement_list( ).accept( this );
		}
	}

	public void addElementOfString( String tagName, String str )
	{
		Element e_literal = xmldoc.createElement( tagName );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_literal );

		Element e_value = xmldoc.createElement( "value" );
		e_literal.appendChild( e_value );
		e_value.setTextContent( str );
	}

	public void addElementOfNode( String tagName, TParseTreeNode node )
	{
		Element e_element = xmldoc.createElement( tagName );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_element );
		elementStack.push(e_element);
		node.accept(this);
		elementStack.pop();
	}


	public void preVisit( TMySQLDeallocatePrepareStmt node ) {
		e_parent = (Element) elementStack.peek();
		Element e_prepare_stmt = xmldoc.createElement("deallocate_prepare_statement");
		e_parent.appendChild(e_prepare_stmt);
		elementStack.push(e_prepare_stmt);
		current_objectName_tag = "stmt_name";
		node.getStmtName().accept(this);
		elementStack.pop( );
	}

	public void preVisit( TPrepareStmt node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_prepare_stmt = xmldoc.createElement( "prepare_statement" );
		e_parent.appendChild( e_prepare_stmt );
		elementStack.push( e_prepare_stmt );
		switch (node.dbvendor){
			case dbvpresto:
			case dbvhana:
				if (node.getParentStmt() != null){
					node.getPreparableStmt().accept(this);
				}
				break;
			default:
				current_objectName_tag = "stmt_name";
				node.getStmtName().accept( this );
				if (node.getPreparableStmt() != null){
					node.getPreparableStmt().accept(this);
				}
				if (node.getPreparableStmtStr().length() > 0){
					addElementOfString("stmt_value",node.getPreparableStmtStr());
				}
				break;
		}

		elementStack.pop( );
	}

	public void preVisit( TCloseStmt node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_close_stmt = xmldoc.createElement( "close_statement" );
		e_parent.appendChild( e_close_stmt );
		elementStack.push( e_close_stmt );
		current_objectName_tag = "cursor_name";
		node.getCursorName( ).accept( this );
		elementStack.pop( );
	}

	public void postVisit( TCloseStmt node )
	{
		appendEndTag( node );
	}

	public void preVisit( TPlsqlCreateTrigger stmt )
	{

		e_parent = (Element) elementStack.peek( );
		Element e_create_trigger = xmldoc.createElement( "create_trigger_statement" );
		e_parent.appendChild( e_create_trigger );
		elementStack.push( e_create_trigger );
		current_objectName_tag = "trigger_name";
		stmt.getTriggerName( ).accept( this );

		// current_table_reference_tag = "onTable";
		// stmt.getOnTable().accept(this);
		// Element e_timing_point = xmldoc.createElement("timing_point");
		// e_timing_point.setTextContent(stmt.getTimingPoint().toString());
		// e_create_trigger.appendChild(e_timing_point);
		// for(Iterator it = stmt.getDmlTypes().iterator();it.hasNext();){
		// ETriggerDmlType dmlType = (ETriggerDmlType)it.next();
		// Element e_dmltype = xmldoc.createElement("dml_type");
		// e_dmltype.setTextContent(dmlType.toString());
		// e_create_trigger.appendChild(e_dmltype);
		// }

		// e_create_trigger.setAttribute("dmlType",stmt.getDmlTypes().);

		Element e_statement_list = xmldoc.createElement( "body_statement_list" );
		e_statement_list.setAttribute( "count", "1" );
		e_create_trigger.appendChild( e_statement_list );

		Element e_statement = xmldoc.createElement( "statement" );
		e_statement.setAttribute( "type",
				stmt.getTriggerBody( ).sqlstatementtype.toString( ) );
		e_statement_list.appendChild( e_statement );
		elementStack.push( e_statement );
		stmt.getTriggerBody( ).setDummyTag( TOP_STATEMENT );
		stmt.getTriggerBody( ).accept( this );
		elementStack.pop( );

		elementStack.pop( );

		// appendStartTagWithIntProperty(node,"name",node.getTriggerName().toString());
		// node.getEventClause().accept(this);
		// if (node.getFollowsTriggerList() != null)
		// node.getFollowsTriggerList().accept(this);
		// if (node.getWhenCondition() != null)
		// node.getWhenCondition().accept(this);
		// node.getTriggerBody().accept(this);

	}

	public void preVisit( TTypeAttribute node )
	{
		appendStartTag( node );
		node.getAttributeName( ).accept( this );
		node.getDatatype( ).accept( this );
	}

	public void postVisit( TTypeAttribute node )
	{
		appendEndTag( node );
	}

	public void preVisit( TTypeAttributeList node )
	{
		appendStartTag( node );
		for ( int i = 0; i < node.size( ); i++ )
		{
			node.getAttributeItem( i ).accept( this );
		}
	}

	public void postVisit( TTypeAttributeList node )
	{
		appendEndTag( node );
	}

	public void preVisit( TPlsqlCreateTypeBody stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_create_type_body = xmldoc.createElement( "create_type_body_statement" );
		e_parent.appendChild( e_create_type_body );
		elementStack.push( e_create_type_body );
		current_objectName_tag = "type_name";
		stmt.getTypeName( ).accept( this );
		stmt.getBodyStatements( ).accept( this );
		elementStack.pop( );
	}

	public void preVisit( TPlsqlVarrayTypeDefStmt node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_varray = xmldoc.createElement( "declare_varray_type" );
		e_parent.appendChild( e_varray );
		elementStack.push( e_varray );
		e_varray.setAttribute( "notnull", String.valueOf( node.isNotNull( ) ) );
		current_objectName_tag = "type_name";
		node.getTypeName( ).accept( this );
		current_datatype_tag = "element_type";
		node.getElementDataType( ).accept( this );
		Element e_size_limit = xmldoc.createElement( "size_limit" );
		e_varray.appendChild( e_size_limit );
		e_size_limit.setTextContent( node.getSizeLimit( ).toString( ) );

		elementStack.pop( );
	}

	public void preVisit( TPlsqlTableTypeDefStmt node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_nested_table = xmldoc.createElement( "declare_nested_table_type" );
		e_parent.appendChild( e_nested_table );
		elementStack.push( e_nested_table );
		current_objectName_tag = "type_name";
		node.getTypeName( ).accept( this );
		current_datatype_tag = "element_type";
		node.getElementDataType( ).accept( this );
		elementStack.pop( );
	}

	public void preVisit( TPlsqlCreateType node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_object_type = xmldoc.createElement( "declare_object_type" );
		e_parent.appendChild( e_object_type );
		elementStack.push( e_object_type );

		current_objectName_tag = "type_name";
		node.getTypeName( ).accept( this );

		if ( node.getAttributes( ) != null )
		{
			for ( int i = 0; i < node.getAttributes( ).size( ); i++ )
			{
				Element e_attribute_type = xmldoc.createElement( "attribute_type" );
				e_object_type.appendChild( e_attribute_type );
				elementStack.push( e_attribute_type );
				current_objectName_tag = "attribute_name";
				node.getAttributes( )
						.getAttributeItem( i )
						.getAttributeName( )
						.accept( this );
				node.getAttributes( )
						.getAttributeItem( i )
						.getDatatype( )
						.accept( this );
				elementStack.pop( );
			}
		}
		elementStack.pop( );
	}

	public void preVisit( TPlsqlCreateType_Placeholder node )
	{
		TPlsqlCreateType createType = null;
		e_parent = (Element) elementStack.peek( );
		Element e_create_type = xmldoc.createElement( "oracle_create_type_statement" );
		e_parent.appendChild( e_create_type );
		elementStack.push( e_create_type );
		e_create_type.setAttribute( "createdType", node.getCreatedType( )
				.toString( ) );

		Element e_object_type;

		switch ( node.getCreatedType( ) )
		{
			case octIncomplete :
				createType = node.getObjectStatement( );
				Element e_imcomplelte_object_type = xmldoc.createElement( "declare_incomplete_object_type" );
				e_create_type.appendChild( e_imcomplelte_object_type );
				elementStack.push( e_imcomplelte_object_type );
				current_objectName_tag = "type_name";
				createType.getTypeName( ).accept( this );
				elementStack.pop( );
				break;
			case octObject :
				node.getObjectStatement( ).accept( this );
				break;
			case octNestedTable :
				node.getNestedTableStatement( ).accept( this );
				break;
			case octVarray :
				node.getVarrayStatement( ).accept( this );
				break;
			default :
				break;

		}

		// switch(node.getKind()){
		// case TBaseType.kind_define:
		// case TBaseType.kind_create:
		// createType = node.getObjectStatement();
		// current_objectName_tag="type_name";
		// createType.getTypeName().accept(this);
		//
		// e_object_type = xmldoc.createElement("object_type");
		// e_create_type.appendChild(e_object_type);
		// elementStack.push(e_object_type);
		// if (createType.getAttributes() != null){
		// for(int i=0;i<createType.getAttributes().size();i++){
		// Element e_attribute_type = xmldoc.createElement("attribute_type");
		// e_object_type.appendChild(e_attribute_type);
		// elementStack.push(e_attribute_type);
		// current_objectName_tag="attribute_name";
		// createType.getAttributes().getAttributeItem(i).getAttributeName().accept(this);
		// createType.getAttributes().getAttributeItem(i).getDatatype().accept(this);
		// elementStack.pop();
		// }
		// }
		// elementStack.pop();
		//
		// break;
		// case TBaseType.kind_create_incomplete:
		// createType = node.getObjectStatement();
		// current_objectName_tag="type_name";
		// createType.getTypeName().accept(this);
		// Element e_imcomplelte_object_type =
		// xmldoc.createElement("incomplete_type");
		// e_create_type.appendChild(e_imcomplelte_object_type);
		// break;
		// case TBaseType.kind_create_varray:
		// node.getVarrayStatement().accept(this);
		// break;
		// case TBaseType.kind_create_nested_table:
		// node.getNestedTableStatement().accept(this);
		// break;
		// }

		elementStack.pop( );
	}

	void outputNodeData( TParseTreeNode node )
	{
		sb.append( node.toString( ) );
	}

	public void preVisit( TMssqlCommit node )
	{
		if ( node.getTransactionName( ) != null )
		{
			appendStartTagWithIntProperty( node,
					"transactionName",
					node.getTransactionName( ).toString( ) );
		}
		else
		{
			appendStartTag( node );
		}
		sb.append( node.toString( ) );
	}

	public void postVisit( TMssqlCommit node )
	{
		appendEndTag( node );
	}

	public void preVisit( TMssqlRollback node )
	{
		if ( node.getTransactionName( ) != null )
		{
			appendStartTagWithIntProperty( node,
					"transactionName",
					node.getTransactionName( ).toString( ) );
		}
		else
		{
			appendStartTag( node );
		}
		sb.append( node.toString( ) );
	}

	public void preVisit( TMssqlSaveTran node )
	{
		if ( node.getTransactionName( ) != null )
		{
			appendStartTagWithIntProperty( node,
					"transactionName",
					node.getTransactionName( ).toString( ) );
		}
		else
		{
			appendStartTag( node );
		}
		sb.append( node.toString( ) );
	}

	public void postVisit( TMssqlSaveTran node )
	{
		appendEndTag( node );
	}

	public void preVisit( TMssqlGo node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_go = xmldoc.createElement( "go_statement" );
		e_parent.appendChild( e_go );
	}

	public void preVisit( TMssqlPrint node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_print = xmldoc.createElement( "print_statement" );
		e_parent.appendChild( e_print );
		elementStack.push( e_print );
		node.getMessages( ).accept( this );
		elementStack.pop( );
	}

	public void preVisit( TMssqlCreateProcedure node )
	{

		e_parent = (Element) elementStack.peek( );
		Element e_create_procedure = xmldoc.createElement( "create_procedure_statement" );
		e_parent.appendChild( e_create_procedure );
		elementStack.push( e_create_procedure );

		Element e_procedure_spec = xmldoc.createElement( "procedure_specification_statement" );
		e_create_procedure.appendChild( e_procedure_spec );
		elementStack.push( e_procedure_spec );
		current_objectName_tag = "procedure_name";
		node.getProcedureName( ).accept( this );

		if ( node.getParameterDeclarations( ) != null )
			node.getParameterDeclarations( ).accept( this );

		if ( node.getBodyStatements( ).size( ) > 0 )
		{
			current_statement_list_tag = "body_statement_list";
			node.getBodyStatements( ).accept( this );
		}

		elementStack.pop( );
		elementStack.pop( );
	}

	public void preVisit( TTeradataCreateMacro stmt ){
		e_parent = (Element) elementStack.peek( );
		Element e_create_macro = xmldoc.createElement( "create_macro_statement" );
		e_parent.appendChild( e_create_macro );
		elementStack.push( e_create_macro );

		current_objectName_tag = "marco_name";
		stmt.getMacroName().accept( this );


		if ( stmt.getBodyStatements( ).size( ) > 0 )
		{
			current_statement_list_tag = "body_statement_list";
			stmt.getBodyStatements( ).accept( this );
		}


		elementStack.pop( );

	}


	public void preVisit( TRepeatStmt stmt ) {
		e_parent = (Element) elementStack.peek( );
		Element e_repeat_stmt = xmldoc.createElement( "repeat_stmt" );
		if (stmt.getLabelName() != null){
			e_repeat_stmt.setAttribute("label",stmt.getLabelNameStr());
		}
		e_parent.appendChild( e_repeat_stmt );
		elementStack.push( e_repeat_stmt );

		addElementOfNode("condition",stmt.getCondition()	);
		current_statement_list_tag = "repeat_body";
		stmt.getBodyStatements( ).accept( this );

		elementStack.pop( );
	}

	public void preVisit( TBlockSqlNode node ) {
		e_parent = (Element) elementStack.peek( );
		Element e_begin_end = xmldoc.createElement( "plsql_block" );
		if (node.getLabelName() != null){
			e_begin_end.setAttribute("label",node.getLabelNameStr());
		}
		e_parent.appendChild( e_begin_end );
		elementStack.push( e_begin_end );
		if (node.getDeclareStatements().size() > 0){
			current_statement_list_tag = "declare_statement_list";
			node.getDeclareStatements().accept(this);
		}
		if ( node.getBodyStatements( ).size( ) > 0 )
		{
			current_statement_list_tag = "body_statement_list";
			node.getBodyStatements( ).accept( this );
		}

		elementStack.pop( );
	}

	public void preVisit( TCreateProcedureStmt node )
	{

		e_parent = (Element) elementStack.peek( );
		Element e_create_procedure = xmldoc.createElement( "create_procedure_statement" );
		e_parent.appendChild( e_create_procedure );
		elementStack.push( e_create_procedure );

		Element e_procedure_spec = xmldoc.createElement( "procedure_specification_statement" );
		e_create_procedure.appendChild( e_procedure_spec );
		elementStack.push( e_procedure_spec );
		current_objectName_tag = "procedure_name";
		node.getProcedureName( ).accept( this );

		if ( node.getParameterDeclarations( ) != null )
			node.getParameterDeclarations( ).accept( this );

		if (node.getReturnDataType() != null){
			addElementOfNode("return_type", node.getReturnDataType());
		}

		if (node.getBlockBody() != null){
			node.getBlockBody().accept(this);
		}else{
			if (node.getDeclareStatements().size() > 0){
				current_statement_list_tag = "declare_statement_list";
				node.getDeclareStatements().accept(this);
			}
			if ( node.getBodyStatements( ).size( ) > 0 )
			{
				current_statement_list_tag = "body_statement_list";
				node.getBodyStatements( ).accept( this );
			}
		}

		elementStack.pop( );
		elementStack.pop( );
	}

	public void preVisit( TParameterDeclarationList list )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_parameter_list = xmldoc.createElement( "parameter_declaration_list" );
		e_parent.appendChild( e_parameter_list );
		elementStack.push( e_parameter_list );
		for ( int i = 0; i < list.size( ); i++ )
		{
			list.getParameterDeclarationItem( i ).accept( this );
		}
		elementStack.pop( );
	}

	public void preVisit( TParameterDeclaration node )
	{
		// appendStartTag(node);
		String tag_name = "parameter_declaration";
		if ( current_parameter_declaration_tag != null )
		{
			tag_name = current_parameter_declaration_tag;
			current_parameter_declaration_tag = null;
		}
		e_parent = (Element) elementStack.peek( );
		Element e_parameter = xmldoc.createElement( tag_name );
		e_parent.appendChild( e_parameter );
		elementStack.push( e_parameter );
		if (node.getParameterName() != null){ // netezza may not specify parameter name
			current_objectName_tag = "name";
			node.getParameterName( ).accept( this );
		}
		node.getDataType( ).accept( this );
		if ( node.getDefaultValue( ) != null )
		{
			current_expression_tag = "default_value";
			node.getDefaultValue( ).accept( this );
		}
		if ( node.getParameterMode( ) != EParameterMode.defaultValue )
		{
			Element e_mode = xmldoc.createElement( "mode" );
			e_mode.setTextContent( node.getParameterMode( ).toString( ) );
			e_parameter.appendChild( e_mode );
		}
		elementStack.pop( );
	}

	public void preVisit( TMssqlCreateType stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_create_type = xmldoc.createElement( "mssql_create_type" );
		e_parent.appendChild( e_create_type );
		elementStack.push( e_create_type );
		current_objectName_tag = "type_name";
		stmt.getType_name( ).accept( this );
		if ( stmt.getBase_type( ) != null )
		{
			current_datatype_tag = "base_type";
			stmt.getBase_type( ).accept( this );
		}
		if ( stmt.getExternalName( ) != null )
		{
			current_objectName_tag = "external_name";
			stmt.getExternalName( ).accept( this );
		}
		if ( stmt.isNull( ) )
		{
			e_create_type.setAttribute( "null", String.valueOf( stmt.isNull( ) ) );
		}

		if ( stmt.isNotNull( ) )
		{
			e_create_type.setAttribute( "notnull",
					String.valueOf( stmt.isNotNull( ) ) );
		}
		elementStack.pop( );
	}

	public void preVisit( TDeclareVariable node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_variable = xmldoc.createElement( "variable" );
		e_parent.appendChild( e_variable );
		elementStack.push( e_variable );

		current_objectName_tag = "variable_name";
		node.getVariableName( ).accept( this );
		if ( node.getDatatype( ) != null )
			node.getDatatype( ).accept( this );

		if ( node.getDefaultValue( ) != null )
		{
			current_expression_tag = "default_value";
			node.getDefaultValue( ).accept( this );
		}

		elementStack.pop( );

	}

	public void postVisit( TDeclareVariable node )
	{
		appendEndTag( node );
	}

	public void preVisit( TDeclareVariableList node )
	{

		for ( int i = 0; i < node.size( ); i++ )
		{
			node.getDeclareVariable( i ).accept( this );
		}

	}

	public void postVisit( TDeclareVariableList node )
	{
		appendEndTag( node );
	}

	public void preVisit( TVarDeclStmt node )
	{
		Element e_var_decl_stmt = null;
		e_parent = (Element) elementStack.peek( );

		switch ( node.getDeclareType( ) )
		{
			case constant :
				e_var_decl_stmt = xmldoc.createElement( "constant_declaration_statement" );
				e_parent.appendChild( e_var_decl_stmt );
				elementStack.push( e_var_decl_stmt );
				current_objectName_tag = "constant_name";
				node.getElementName( ).accept( this );
				node.getDataType( ).accept( this );
				current_expression_tag = "default_value";
				node.getDefaultValue( ).accept( this );
				elementStack.pop( );
				break;
			case variable :
				e_var_decl_stmt = xmldoc.createElement( "variable_declaration_statement" );
				e_parent.appendChild( e_var_decl_stmt );
				elementStack.push( e_var_decl_stmt );
				current_objectName_tag = "variable_name";
				node.getElementName( ).accept( this );
				if (node.getDataType() != null){
					node.getDataType( ).accept( this );
				}
				if ( node.getDefaultValue( ) != null )
				{
					current_expression_tag = "default_value";
					node.getDefaultValue( ).accept( this );
				}
				elementStack.pop( );
				break;
			case exception :
				e_var_decl_stmt = xmldoc.createElement( "exception_declaration_statement" );
				e_parent.appendChild( e_var_decl_stmt );
				elementStack.push( e_var_decl_stmt );
				current_objectName_tag = "exception_name";
				node.getElementName( ).accept( this );
				elementStack.pop( );
				break;
			case subtype :
				e_var_decl_stmt = xmldoc.createElement( "subtype_definition_statement" );
				e_parent.appendChild( e_var_decl_stmt );
				elementStack.push( e_var_decl_stmt );
				current_objectName_tag = "subtype_name";
				node.getElementName( ).accept( this );
				node.getDataType( ).accept( this );
				elementStack.pop( );
				break;
			default :
				e_var_decl_stmt = xmldoc.createElement( "var_decl_stmt" );
				e_parent.appendChild( e_var_decl_stmt );
				e_var_decl_stmt.setAttribute( "type", node.getDeclareType( )
						.toString( ) );
				e_var_decl_stmt.setTextContent( node.toString( ) );
				break;
		}

		// elementStack.pop();
	}

	public void postVisit( TVarDeclStmt node )
	{
		appendEndTag( node );
	}

	public void preVisit( TRaiseStmt node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_raise_stmt = xmldoc.createElement( "raise_statement" );
		e_parent.appendChild( e_raise_stmt );
		elementStack.push( e_raise_stmt );
		if ( node.getExceptionName( ) != null )
		{
			current_objectName_tag = "exception_name";
			node.getExceptionName( ).accept( this );
		}
		elementStack.pop( );
	}

	public void preVisit( TReturnStmt node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_return_stmt = xmldoc.createElement( "return_statement" );
		e_parent.appendChild( e_return_stmt );
		elementStack.push( e_return_stmt );
		if ( node.getExpression( ) != null )
		{
			current_objectName_tag = "expression";
			node.getExpression( ).accept( this );
		}
		elementStack.pop( );
	}

	public void preVisit( TMssqlReturn node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_return_stmt = xmldoc.createElement( "return_statement" );
		e_parent.appendChild( e_return_stmt );
		elementStack.push( e_return_stmt );
		if ( node.getReturnExpr( ) != null )
		{
			current_objectName_tag = "expression";
			node.getReturnExpr( ).accept( this );
		}else if (node.getSubquery() != null){
			node.getSubquery().accept(this);
		}
		elementStack.pop( );
	}

	public void preVisit( TPlsqlRecordTypeDefStmt stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_record_type_stmt = xmldoc.createElement( "record_type_definition_statement" );
		e_parent.appendChild( e_record_type_stmt );
		elementStack.push( e_record_type_stmt );
		current_objectName_tag = "type_name";
		stmt.getTypeName( ).accept( this );

		Element e_field_declarations = xmldoc.createElement( "field_declaration_list" );
		e_record_type_stmt.appendChild( e_field_declarations );
		elementStack.push( e_field_declarations );
		for ( int i = 0; i < stmt.getFieldDeclarations( ).size( ); i++ )
		{
			current_parameter_declaration_tag = "record_field_declaration";
			stmt.getFieldDeclarations( )
					.getParameterDeclarationItem( i )
					.accept( this );
		}
		elementStack.pop( );

		elementStack.pop( );
	}

	public void preVisit( TSqlplusCmdStatement stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_raise_stmt = xmldoc.createElement( "sqlplus_command" );
		e_parent.appendChild( e_raise_stmt );
	}

	public void preVisit( TCursorDeclStmt stmt )
	{
		e_parent = (Element) elementStack.peek( );
		switch ( stmt.getCursorKind( ) )
		{
			case  typeDefinition: //TCursorDeclStmt.kind_ref_cursor_type_definition :
				Element e_stmt = xmldoc.createElement( "ref_cursor_type_definition_statement" );
				e_parent.appendChild( e_stmt );
				elementStack.push( e_stmt );
				current_objectName_tag = "type_name";
				stmt.getCursorTypeName( ).accept( this );
				if ( stmt.getRowtype( ) != null )
				{
					stmt.getRowtype( ).accept( this );
				}
				elementStack.pop( );
				break;
			case declaration: //TCursorDeclStmt.kind_cursor_declaration :
				Element e_cursor_stmt = xmldoc.createElement( "cursor_declaration_statement" );
				e_parent.appendChild( e_cursor_stmt );
				elementStack.push( e_cursor_stmt );
				current_objectName_tag = "cursor_name";
				stmt.getCursorName( ).accept( this );
				if ( stmt.getCursorParameterDeclarations( ) != null )
				{
					stmt.getCursorParameterDeclarations( ).accept( this );
				}
				if ( stmt.getRowtype( ) != null )
				{
					current_datatype_tag = "return_type";
					stmt.getRowtype( ).accept( this );
				}
				stmt.getSubquery( ).setDummyTag( TOP_STATEMENT );
				stmt.getSubquery( ).accept( this );
				elementStack.pop( );
				break;
			case specification:
				Element e_cursor_decl_stmt = xmldoc.createElement( "cursor_decl_stmt" );
				e_parent.appendChild( e_cursor_decl_stmt );
				elementStack.push( e_cursor_decl_stmt );
				current_objectName_tag = "cursor_name";
				stmt.getCursorName( ).accept( this );

				//e_cursor_decl_stmt.setTextContent( stmt.toString( ) );

				Element returnType = xmldoc.createElement( "return_type" );
				e_cursor_decl_stmt.appendChild( returnType );
				elementStack.push( returnType );
				stmt.getRowtype().accept(this);
				elementStack.pop( );

				elementStack.pop( );
				break;
			case body:
				Element e_cursor_decl_body_stmt = xmldoc.createElement( "cursor_decl_stmt" );
				e_parent.appendChild( e_cursor_decl_body_stmt );
				e_cursor_decl_body_stmt.setTextContent( stmt.toString( ) );
				break;
			case resultsetName:
				Element e_cursor_resultsetName = xmldoc.createElement( "cursor_decl_resultset" );
				addElementOfNode("resultset",stmt.getResultsetName());
				break;
		}
	}



	public void preVisit( TGetDiagStmt stmt ) {

		Element e_stmt = xmldoc.createElement( "get_diagnostics_statement" );
		e_parent = (Element) elementStack.peek();
		e_parent.appendChild( e_stmt );
		elementStack.push( e_stmt );
		elementStack.pop( );

	}

	public void preVisit( TWhileStmt stmt ) {

		Element e_stmt = xmldoc.createElement( "while_statement" );
		e_parent = (Element) elementStack.peek();
		e_parent.appendChild( e_stmt );
		elementStack.push( e_stmt );
		e_stmt.setAttribute("label",stmt.getLabelNameStr());
		addElementOfNode("condition",stmt.getCondition());
		stmt.getBodyStatements().accept(this);
		elementStack.pop( );

	}

	public void preVisit( TLoopStmt stmt )
	{
		Element e_stmt = null;
		e_parent = (Element) elementStack.peek( );
		switch ( stmt.getKind( ) )
		{
			case TLoopStmt.basic_loop :
				e_stmt = xmldoc.createElement( "loop_statement" );
				break;
			case TLoopStmt.cursor_for_loop :
				e_stmt = xmldoc.createElement( "cursor_for_loop_statement" );
				break;
			case TLoopStmt.for_loop :
				e_stmt = xmldoc.createElement( "for_loop_statement" );
				break;
			case TLoopStmt.while_loop :
				e_stmt = xmldoc.createElement( "while_statement" );
				break;
		}

		if ( e_stmt != null )
		{
			e_parent.appendChild( e_stmt );
			elementStack.push( e_stmt );
			e_stmt.setAttribute("label",stmt.getLabelNameStr());
			if ( stmt.getRecordName( ) != null )
			{
				current_objectName_tag = "record";
				stmt.getRecordName( ).accept( this );

				if ( stmt.getSubquery( ) != null )
				{
					current_query_expression_tag = "select_statement";
					stmt.getSubquery( ).accept( this );
				}
				else if ( stmt.getCursorName( ) != null )
				{
					current_objectName_tag = "cursor";
					stmt.getCursorName( ).accept( this );
					if ( stmt.getCursorParameterNames( ) != null )
					{
						current_expression_list_tag = "cursor_parameter_list";
						stmt.getCursorParameterNames( ).accept( this );
					}
				}
			}
			if ( stmt.getCondition( ) != null )
			{
				current_expression_tag = "condition";
				stmt.getCondition( ).accept( this );
			}
			if ( stmt.getBodyStatements( ) != null )
			{
				current_statement_list_tag = "loop";
				stmt.getBodyStatements( ).accept( this );
			}
			elementStack.pop( );
		}

	}

	public void preVisit( TPlsqlContinue stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_continue_stmt = xmldoc.createElement( "continue_statement" );
		e_parent.appendChild( e_continue_stmt );
		elementStack.push( e_continue_stmt );
		if ( stmt.getLabelName( ) != null )
		{
			current_objectName_tag = "label_name";
			stmt.getLabelName( ).accept( this );
		}
		if ( stmt.getCondition( ) != null )
		{
			current_expression_tag = "condition";
			stmt.getCondition( ).accept( this );
		}

		elementStack.pop( );

	}

	public void preVisit( TPlsqlExecImmeStmt stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_execute_immediate_stmt = xmldoc.createElement( "execute_immediate_statement" );
		e_parent.appendChild( e_execute_immediate_stmt );
		elementStack.push( e_execute_immediate_stmt );

		if ( stmt.getDynamicSQL( ) != null )
		{
			Element e_dynamic_sql = xmldoc.createElement( "dynamic_sql_stmt" );
			e_dynamic_sql.setTextContent( stmt.getDynamicSQL( ) );
			e_execute_immediate_stmt.appendChild( e_dynamic_sql );
		}

		if ( stmt.getDynamicStringExpr( ) != null )
		{
			Element e_dynamic_string = xmldoc.createElement( "dynamic_string" );
			elementStack.push( e_dynamic_string );
			e_execute_immediate_stmt.appendChild( e_dynamic_string );
			preVisit( stmt.getDynamicStringExpr( ) );
			elementStack.pop( );
		}

		if ( stmt.getDynamicStatements( ) != null )
		{
			preVisit( stmt.getDynamicStatements( ) );
		}

		elementStack.pop( );

	}

	public void preVisit( TExecuteSqlStatement stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_execute = xmldoc.createElement( "execute_statement" );
		e_execute.setAttribute( "executeType", stmt.getExecuteType( ).toString( ) );


		e_parent.appendChild( e_execute );
		elementStack.push( e_execute );
		switch (stmt.getExecuteType()){
			case module:
				current_objectName_tag = "module_name";
				stmt.getModuleName().accept(this);
				if (stmt.getParameters() != null){
					current_expression_list_tag = "module_parameters";
					stmt.getParameters().accept(this);
				}
				break;
			default:
				break;
		}

		if ( stmt.getStmt( ) != null )
		{
			stmt.getStmt().accept(this);
		}
		elementStack.pop( );
	}

	public void preVisit( TExitStmt stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_continue_stmt = xmldoc.createElement( "exit_statement" );
		e_parent.appendChild( e_continue_stmt );
		elementStack.push( e_continue_stmt );
		if ( stmt.getExitlabelName( ) != null )
		{
			current_objectName_tag = "label_name";
			stmt.getExitlabelName( ).accept( this );
		}
		current_expression_tag = "condition";
		if ( stmt.getWhenCondition( ) != null )
		{
			stmt.getWhenCondition( ).accept( this );
		}

		elementStack.pop( );
	}

	public void preVisit( TFetchStmt stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_continue_stmt = xmldoc.createElement( "fetch_statement" );
		e_parent.appendChild( e_continue_stmt );
		elementStack.push( e_continue_stmt );
		current_objectName_tag = "cursor_name";
		stmt.getCursorName( ).accept( this );
		current_expression_list_tag = "into_list";
		if (stmt.dbvendor == EDbVendor.dbvmysql){
			stmt.getVariableNameObjectList().accept(this);
		}else{
			stmt.getVariableNames( ).accept( this );
		}


		elementStack.pop( );

	}

	public void preVisit( TPlsqlGotoStmt stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_goto_stmt = xmldoc.createElement( "goto_statement" );
		e_parent.appendChild( e_goto_stmt );
		elementStack.push( e_goto_stmt );
		current_objectName_tag = "label_name";
		stmt.getGotolabelName( ).accept( this );

	}

	public void preVisit( TPlsqlNullStmt stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_full_stmt = xmldoc.createElement( "plsql_null_statement" );
		e_parent.appendChild( e_full_stmt );
		e_full_stmt.setTextContent( stmt.toString( ) );
	}

	public void preVisit( TCommentOnSqlStmt stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_comment_on_stmt = xmldoc.createElement( "comment_on_statement" );
		e_parent.appendChild( e_comment_on_stmt );
		e_comment_on_stmt.setAttribute( "object_type", stmt.getDbObjectType( )
				.toString( ) );
		elementStack.push( e_comment_on_stmt );
		current_objectName_tag = "object_name";
		stmt.getObjectName( ).accept( this );
		elementStack.pop( );
		Element e_string = xmldoc.createElement( "comment_message" );
		e_comment_on_stmt.appendChild( e_string );
		e_string.setTextContent( stmt.getMessage( ).toString( ) );
	}

	public void preVisit( TOpenStmt stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_open_stmt = xmldoc.createElement( "open_statement" );
		e_parent.appendChild( e_open_stmt );
		elementStack.push( e_open_stmt );
		current_objectName_tag = "cursor_name";
		stmt.getCursorName( ).accept( this );
		if ( stmt.getCursorParameterNames( ) != null )
		{
			current_expression_list_tag = "parameter_list";
			stmt.getCursorParameterNames( ).accept( this );
		}
		elementStack.pop( );
	}

	public void preVisit( TOpenforStmt stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_open_for_stmt = xmldoc.createElement( "open_for_statement" );
		e_parent.appendChild( e_open_for_stmt );
		elementStack.push( e_open_for_stmt );
		current_objectName_tag = "variable_name";
		stmt.getCursorVariableName( ).accept( this );
		if ( stmt.getSubquery( ) != null )
		{
			stmt.getSubquery( ).setDummyTag( TOP_STATEMENT );
			stmt.getSubquery( ).accept( this );
		}
		if ( stmt.getDynamic_string( ) != null )
		{
			Element e_string = xmldoc.createElement( "dynamic_string" );
			e_open_for_stmt.appendChild( e_string );
			e_string.setTextContent( stmt.getDynamic_string( ).toString( ) );
		}

		elementStack.pop( );
	}

	public void preVisit( TForStmt stmt ) {
		e_parent = (Element) elementStack.peek();
		Element e_for_stmt = xmldoc.createElement("for_statement");
		e_parent.appendChild(e_for_stmt);
		elementStack.push(e_for_stmt);
		if (stmt.getLoopName() != null){
			e_for_stmt.setAttribute("loop_name",stmt.getLoopName().toString());
		}
		if (stmt.getCursorName() != null){
			e_for_stmt.setAttribute("cursor_name",stmt.getCursorName().toString());
		}
		if (stmt.getSubquery() != null){
			stmt.getSubquery().accept(this);
		}
		if (stmt.getBodyStatements().size() > 0){
			stmt.getBodyStatements().accept(this);
		}

		elementStack.pop( );

	}
		public void preVisit( TPlsqlForallStmt stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_forall_stmt = xmldoc.createElement( "forall_statement" );
		e_parent.appendChild( e_forall_stmt );
		elementStack.push( e_forall_stmt );
		current_objectName_tag = "index_name";
		stmt.getIndexName( ).accept( this );

		Element e_bounds_clause = xmldoc.createElement( "bounds_clause" );
		e_forall_stmt.appendChild( e_bounds_clause );
		elementStack.push( e_bounds_clause );
		switch ( stmt.getBound_clause_kind( ) )
		{
			case TPlsqlForallStmt.bound_clause_kind_normal :
				e_bounds_clause.setAttribute( "type", "normal" );
				break;
			case TPlsqlForallStmt.bound_clause_kind_indices_of :
				e_bounds_clause.setAttribute( "type", "indeces_of" );
				break;
			case TPlsqlForallStmt.bound_clause_kind_values_of :
				e_bounds_clause.setAttribute( "type", "values_of" );
				break;
		}
		if ( stmt.getLower_bound( ) != null )
		{
			current_expression_tag = "lower_bound";
			stmt.getLower_bound( ).accept( this );
		}
		if ( stmt.getUpper_bound( ) != null )
		{
			current_expression_tag = "upper_bound";
			stmt.getUpper_bound( ).accept( this );
		}
		if ( stmt.getCollectionName( ) != null )
		{
			current_objectName_tag = "collection_name";
			stmt.getCollectionName( ).accept( this );
		}
		if ( stmt.getCollecitonNameExpr( ) != null )
		{
			current_expression_tag = "collection_expr";
			stmt.getCollecitonNameExpr( ).accept( this );
		}
		elementStack.pop( );

		Element e_statement = xmldoc.createElement( "statement" );
		e_statement.setAttribute( "type",
				stmt.getStatement( ).sqlstatementtype.toString( ) );
		e_forall_stmt.appendChild( e_statement );
		elementStack.push( e_statement );
		stmt.getStatement( ).setDummyTag( TOP_STATEMENT );
		stmt.getStatement( ).accept( this );
		elementStack.pop( );

		elementStack.pop( );

	}

	public void preVisit( TCallStatement stmt )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_call_stmt = xmldoc.createElement( "call_statement" );
		e_parent.appendChild( e_call_stmt );
		elementStack.push( e_call_stmt );
		current_objectName_tag = "routine_name";
		stmt.getRoutineName( ).accept( this );
		if ( stmt.getArgs( ) != null )
		{
			current_expression_list_tag = "parameter_list";
			stmt.getArgs( ).accept( this );
		}
		elementStack.pop( );

	}

	public void preVisit( TMdxSelect node )
	{
		e_parent = (Element) elementStack.peek( );
		Element e_select = xmldoc.createElement( "mdx_select" );
		e_parent.appendChild( e_select );
		elementStack.push( e_select );

		if ( node.getWiths( ) != null )
		{
			for ( int i = 0; i < node.getWiths( ).size( ); i++ )
			{
				node.getWiths( ).getElement( i ).accept( this );
			}
		}

		if ( node.getAxes( ) != null )
		{
			for ( int i = 0; i < node.getAxes( ).size( ); i++ )
			{
				TMdxAxisNode mdxAxis = node.getAxes( ).getElement( i );
				mdxAxis.accept( this );
			}
		}

		if ( node.getCube( ) != null )
		{
			Element e_cube_clause = xmldoc.createElement( "cube_clause" );
			e_select.appendChild( e_cube_clause );
			elementStack.push( e_cube_clause );

			Element e_cube_name = xmldoc.createElement( "cube_name" );
			e_cube_clause.appendChild( e_cube_name );
			e_cube_name.setTextContent( node.getCube( ).toString( ) );
				
			elementStack.pop( );
		}

		if ( node.getWhere( ) != null )
		{
			node.getWhere( ).accept( this );
		}

		elementStack.pop( );
	}

	public void preVisit( TMdxWithNode node )
	{
		Element e_with_clause = xmldoc.createElement( "with_clause" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_with_clause );
		elementStack.push( e_with_clause );

		if ( node instanceof TMdxWithMemberNode )
		{
			preVisit( (TMdxWithMemberNode) node );
		}
		else if ( node instanceof TMdxWithSetNode )
		{
			preVisit( (TMdxWithSetNode) node );
		}

		elementStack.pop( );
	}

	public void preVisit( TMdxWithMemberNode node )
	{
		Element e_mdx_with_member = xmldoc.createElement( "mdx_with_member" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_mdx_with_member );
		elementStack.push( e_mdx_with_member );

		if ( node.getNameNode( ) != null )
		{
			Element e_member_name = xmldoc.createElement( "member_name" );
			e_mdx_with_member.appendChild( e_member_name );
			e_member_name.setTextContent( node.getNameNode( ).toString( ) );
		}

		if ( node.getExprNode( ) != null )
		{
			Element e_value_expr = xmldoc.createElement( "value_expr" );
			e_mdx_with_member.appendChild( e_value_expr );
			elementStack.push( e_value_expr );
			handleMdxExpr( node.getExprNode( ) );
			elementStack.pop( );
		}

		elementStack.pop( );
	}

	public void preVisit( TMdxWithSetNode node )
	{
		Element e_mdx_set_member = xmldoc.createElement( "mdx_with_set" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_mdx_set_member );
		elementStack.push( e_mdx_set_member );

		if ( node.getNameNode( ) != null )
		{
			Element e_set_name = xmldoc.createElement( "set_name" );
			e_mdx_set_member.appendChild( e_set_name );
			e_set_name.setTextContent( node.getNameNode( ).toString( ) );
		}

		if ( node.getExprNode( ) != null )
		{
			Element e_value_expr = xmldoc.createElement( "value_expr" );
			e_mdx_set_member.appendChild( e_value_expr );
			elementStack.push( e_value_expr );
			handleMdxExpr( node.getExprNode( ) );
			elementStack.pop( );
		}

		elementStack.pop( );
	}

	public void preVisit( TMdxWhereNode node )
	{
		Element e_where_clause = xmldoc.createElement( "where_clause" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_where_clause );
		elementStack.push( e_where_clause );

		Element e_expr = xmldoc.createElement( "expr" );
		e_where_clause.appendChild( e_expr );
		elementStack.push( e_expr );

		if ( node.getFilter( ) != null )
		{
			handleMdxExpr( node.getFilter( ) );
		}

		elementStack.pop( );
		elementStack.pop( );
	}

	public void preVisit( TMdxAxisNode node )
	{
		Element e_axis_clause = xmldoc.createElement( "axis_clause" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_axis_clause );
		elementStack.push( e_axis_clause );

		Element e_expr = xmldoc.createElement( "expr" );
		e_axis_clause.appendChild( e_expr );
		elementStack.push( e_expr );

		if ( node.getExpNode( ) != null )
		{
			handleMdxExpr( node.getExpNode( ) );
		}

		elementStack.pop( );

		if ( node.getAxis( ) != null )
		{
			preVisit( node.getAxis( ) );
		}

		elementStack.pop( );
	}

	public void preVisit( TMdxFunctionNode node )
	{
		e_parent = (Element) elementStack.peek( );

		Element e_mdx_function = xmldoc.createElement( "mdx_function" );
		e_parent.appendChild( e_mdx_function );
		elementStack.push( e_mdx_function );

		Element e_function_name = xmldoc.createElement( "function_name" );
		e_mdx_function.appendChild( e_function_name );
		elementStack.push( e_function_name );

		Element e_segment = xmldoc.createElement( "segment" );
		e_function_name.appendChild( e_segment );
		elementStack.push( e_segment );
		preVisit( node.getFunctionSegment( ) );
		elementStack.pop( );

		elementStack.pop( );

		if ( node.getArguments( ).size( ) > 0 )
		{
			Element e_function_args = xmldoc.createElement( "function_args" );
			e_mdx_function.appendChild( e_function_args );
			elementStack.push( e_function_args );

			for ( int i = 0; i < node.getArguments( ).size( ); i++ )
			{
				if ( node.getExpSyntax( ).equals( EMdxExpSyntax.Method )
						&& i == 0 )
				{
					continue;
				}
				TMdxExpNode element = node.getArguments( ).getElement( i );
				Element e_mdx_expr = xmldoc.createElement( "mdx_expr" );
				e_function_args.appendChild( e_mdx_expr );
				elementStack.push( e_mdx_expr );
				handleMdxExpr( element );
				elementStack.pop( );
			}

			elementStack.pop( );
		}

		if ( node.getExpSyntax( ).equals( EMdxExpSyntax.Method ) )
		{
			TMdxExpNode element = node.getArguments( ).getElement( 0 );
			Element e_mdx_expr = xmldoc.createElement( "object_expr" );
			e_mdx_function.appendChild( e_mdx_expr );
			elementStack.push( e_mdx_expr );
			handleMdxExpr( element );
			elementStack.pop( );
		}

		e_mdx_function.setAttribute( "expr_syntax", node.getExpSyntax( ).name( ) );

		elementStack.pop( );
	}

	private void handleMdxExpr( TMdxExpNode element )
	{
		if ( element instanceof TMdxUnaryOpNode )
		{
			( (TMdxUnaryOpNode) element ).accept( this );
		}
		else if ( element instanceof TMdxBinOpNode )
		{
			( (TMdxBinOpNode) element ).accept( this );
		}
		else
		{
			e_parent = (Element) elementStack.peek( );
			Element e_mdx_value_primary_expr = xmldoc.createElement( "mdx_value_primary_expr" );
			e_parent.appendChild( e_mdx_value_primary_expr );
			elementStack.push( e_mdx_value_primary_expr );
			element.accept( this );
			elementStack.pop( );
		}
	}

	public void preVisit( TMdxPropertyNode node )
	{
		e_parent = (Element) elementStack.peek( );

		Element e_mdx_function = xmldoc.createElement( "mdx_property" );
		e_parent.appendChild( e_mdx_function );
		elementStack.push( e_mdx_function );

		Element e_function_name = xmldoc.createElement( "function_name" );
		e_function_name.setTextContent( node.getFunctionName( ) );
		e_mdx_function.appendChild( e_function_name );

		if ( node.getArguments( ).size( ) > 0 )
		{
			Element e_function_args = xmldoc.createElement( "function_args" );
			e_mdx_function.appendChild( e_function_args );
			elementStack.push( e_function_args );

			for ( int i = 0; i < node.getArguments( ).size( ); i++ )
			{
				TMdxExpNode element = node.getArguments( ).getElement( i );
				Element e_mdx_expr = xmldoc.createElement( "mdx_expr" );
				e_function_args.appendChild( e_mdx_expr );
				elementStack.push( e_mdx_expr );
				handleMdxExpr( element );
				elementStack.pop( );
			}

			elementStack.pop( );
		}

		e_mdx_function.setAttribute( "expr_syntax", node.getExpSyntax( ).name( ) );

		elementStack.pop( );
	}

	public void preVisit( TMdxAxis node )
	{
		Element e_on_axis = xmldoc.createElement( "on_axis" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_on_axis );
		elementStack.push( e_on_axis );

		Element e_string_value = xmldoc.createElement( "string_value" );
		e_string_value.setTextContent( node.toString( ) );
		e_on_axis.appendChild( e_string_value );

		elementStack.pop( );
	}

	public void preVisit( TMdxSetNode node )
	{
		e_parent = (Element) elementStack.peek( );

		Element e_mdx_set = xmldoc.createElement( "mdx_set" );
		e_parent.appendChild( e_mdx_set );
		elementStack.push( e_mdx_set );

		Element e_mdx_exprs = xmldoc.createElement( "mdx_exprs" );
		e_mdx_set.appendChild( e_mdx_exprs );
		elementStack.push( e_mdx_exprs );

		for ( int i = 0; i < node.getTupleList( ).size( ); i++ )
		{
			TMdxExpNode element = node.getTupleList( ).getElement( i );
			Element e_mdx_expr = xmldoc.createElement( "mdx_expr" );
			e_mdx_exprs.appendChild( e_mdx_expr );
			elementStack.push( e_mdx_expr );
			handleMdxExpr( element );
			elementStack.pop( );
		}

		elementStack.pop( );
		elementStack.pop( );
	}

	public void preVisit( TMdxTupleNode node )
	{
		e_parent = (Element) elementStack.peek( );

		Element e_mdx_tuple = xmldoc.createElement( "mdx_tuple" );
		e_parent.appendChild( e_mdx_tuple );
		elementStack.push( e_mdx_tuple );

		Element e_mdx_exprs = xmldoc.createElement( "mdx_members" );
		e_mdx_tuple.appendChild( e_mdx_exprs );
		elementStack.push( e_mdx_exprs );

		for ( int i = 0; i < node.getExprList( ).size( ); i++ )
		{
			TMdxExpNode element = node.getExprList( ).getElement( i );
			Element e_mdx_expr = xmldoc.createElement( "mdx_expr" );
			e_mdx_exprs.appendChild( e_mdx_expr );
			elementStack.push( e_mdx_expr );
			handleMdxExpr( element );
			elementStack.pop( );
		}

		elementStack.pop( );
		elementStack.pop( );
	}

	public void preVisit( TMdxBinOpNode node )
	{
		e_parent = (Element) elementStack.peek( );

		Element e_mdx_binary_expr = xmldoc.createElement( "mdx_binary_expr" );
		e_parent.appendChild( e_mdx_binary_expr );
		elementStack.push( e_mdx_binary_expr );

		Element e_left_expr = xmldoc.createElement( "left_expr" );
		e_mdx_binary_expr.appendChild( e_left_expr );
		elementStack.push( e_left_expr );
		handleMdxExpr( node.getLeftExprNode( ) );
		elementStack.pop( );

		Element e_right_expr = xmldoc.createElement( "right_expr" );
		e_mdx_binary_expr.appendChild( e_right_expr );
		elementStack.push( e_right_expr );
		handleMdxExpr( node.getRightExprNode( ) );
		elementStack.pop( );

		e_mdx_binary_expr.setAttribute( "operator", node.getOperator( )
				.toString( ) );

		elementStack.pop( );
	}

	public void preVisit( TMdxUnaryOpNode node )
	{
		e_parent = (Element) elementStack.peek( );

		Element e_mdx_unary_expr = xmldoc.createElement( "mdx_unary_expr" );
		e_parent.appendChild( e_mdx_unary_expr );
		elementStack.push( e_mdx_unary_expr );

		Element e_expr = xmldoc.createElement( "expr" );
		e_mdx_unary_expr.appendChild( e_expr );
		elementStack.push( e_expr );
		handleMdxExpr( node.getExpNode( ) );
		elementStack.pop( );

		e_mdx_unary_expr.setAttribute( "operator", node.getOperator( )
				.toString( ) );

		elementStack.pop( );
	}

	public void preVisit( TMdxCaseNode node )
	{
		e_parent = (Element) elementStack.peek( );

		Element e_mdx_case = xmldoc.createElement( "mdx_case" );
		e_parent.appendChild( e_mdx_case );
		elementStack.push( e_mdx_case );

		if ( node.getCondition( ) != null )
		{
			Element e_condition_expr = xmldoc.createElement( "condition_expr" );
			e_mdx_case.appendChild( e_condition_expr );
			elementStack.push( e_condition_expr );
			handleMdxExpr( node.getCondition( ) );
			elementStack.pop( );
		}

		if ( node.getWhenList( ) != null )
		{
			Element e_when_then_list = xmldoc.createElement( "when_then_list" );
			e_mdx_case.appendChild( e_when_then_list );
			elementStack.push( e_when_then_list );
			for ( int i = 0; i < node.getWhenList( ).size( ); i++ )
			{
				TMdxWhenNode whenNode = (TMdxWhenNode) node.getWhenList( )
						.getElement( i );
				whenNode.accept( this );
			}
			elementStack.pop( );
		}

		if ( node.getElseExpr( ) != null )
		{
			Element e_else_value = xmldoc.createElement( "else_value" );
			e_mdx_case.appendChild( e_else_value );
			elementStack.push( e_else_value );
			handleMdxExpr( node.getElseExpr( ) );
			elementStack.pop( );
		}

		elementStack.pop( );
	}

	public void preVisit( TMdxWhenNode node )
	{
		Element e_mdx_when_then = xmldoc.createElement( "mdx_when_then" );
		e_parent = (Element) elementStack.peek( );
		e_parent.appendChild( e_mdx_when_then );
		elementStack.push( e_mdx_when_then );

		if ( node.getWhenExpr( ) != null )
		{
			Element e_when_expr = xmldoc.createElement( "when_expr" );
			e_mdx_when_then.appendChild( e_when_expr );
			elementStack.push( e_when_expr );
			handleMdxExpr( node.getWhenExpr( ) );
			elementStack.pop( );
		}

		if ( node.getThenExpr( ) != null )
		{
			Element e_then_value = xmldoc.createElement( "then_value" );
			e_mdx_when_then.appendChild( e_then_value );
			elementStack.push( e_then_value );
			handleMdxExpr( node.getWhenExpr( ) );
			elementStack.pop( );
		}

		elementStack.pop( );
	}

	public void preVisit( TMdxIdentifierNode node )
	{
		e_parent = (Element) elementStack.peek( );

		Element e_mdx_identifier = xmldoc.createElement( "mdx_identifier" );
		e_parent.appendChild( e_mdx_identifier );
		elementStack.push( e_mdx_identifier );

		for ( int i = 0; i < node.getSegments( ).size( ); i++ )
		{
			Element e_segment = xmldoc.createElement( "segment" );
			e_mdx_identifier.appendChild( e_segment );
			elementStack.push( e_segment );

			IMdxIdentifierSegment segment = node.getSegments( ).getElement( i );
			preVisit( segment );

			elementStack.pop( );
		}
		elementStack.pop( );
	}

	public void preVisit( TMdxStringConstNode node )
	{
		e_parent = (Element) elementStack.peek( );

		Element e_mdx_constant = xmldoc.createElement( "mdx_constant" );
		e_parent.appendChild( e_mdx_constant );
		elementStack.push( e_mdx_constant );

		Element e_string_value = xmldoc.createElement( "string_value" );
		e_string_value.setTextContent( node.toString( ) );
		e_mdx_constant.appendChild( e_string_value );

		e_mdx_constant.setAttribute( "kind", "String" );

		elementStack.pop( );
	}

	public void preVisit( TMdxIntegerConstNode node )
	{
		e_parent = (Element) elementStack.peek( );

		Element e_mdx_constant = xmldoc.createElement( "mdx_constant" );
		e_parent.appendChild( e_mdx_constant );
		elementStack.push( e_mdx_constant );

		Element e_string_value = xmldoc.createElement( "string_value" );
		e_string_value.setTextContent( node.toString( ) );
		e_mdx_constant.appendChild( e_string_value );

		e_mdx_constant.setAttribute( "kind", "Integer" );

		elementStack.pop( );
	}

	public void preVisit( TMdxFloatConstNode node )
	{
		e_parent = (Element) elementStack.peek( );

		Element e_mdx_constant = xmldoc.createElement( "mdx_constant" );
		e_parent.appendChild( e_mdx_constant );
		elementStack.push( e_mdx_constant );

		Element e_string_value = xmldoc.createElement( "string_value" );
		e_string_value.setTextContent( node.toString( ) );
		e_mdx_constant.appendChild( e_string_value );

		e_mdx_constant.setAttribute( "kind", "Float" );

		elementStack.pop( );
	}

	private void preVisit( IMdxIdentifierSegment segment )
	{
		e_parent = (Element) elementStack.peek( );

		if ( segment.getName( ) != null )
		{
			Element e_name_segment = xmldoc.createElement( "name_segment" );
			e_parent.appendChild( e_name_segment );
			e_name_segment.setAttribute( "value", segment.getName( ) );
			if ( segment.getQuoting( ) != null )
			{
				e_name_segment.setAttribute( "quoting", segment.getQuoting( )
						.name( ) );
			}
		}

		if ( segment.getKeyParts( ) != null )
		{
			Element e_key_segment = xmldoc.createElement( "key_segment" );
			e_parent.appendChild( e_key_segment );
			elementStack.push( e_key_segment );

			for ( int j = 0; j < segment.getKeyParts( ).size( ); j++ )
			{
				preVisit( (IMdxIdentifierSegment) segment.getKeyParts( )
						.getElement( j ) );
			}
			elementStack.pop( );
		}

	}

	private String format( Document doc, int indent ) throws Exception
	{
		DOMSource domSource = new DOMSource( doc );
		Transformer transformer = TransformerFactory.newInstance( )
				.newTransformer( );
		transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
		transformer.setOutputProperty( OutputKeys.METHOD, "xml" );
		transformer.setOutputProperty( OutputKeys.ENCODING, "UTF-8" );
		transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount",
				String.valueOf( indent ) );
		transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
		StringWriter sw = new StringWriter( );
		StreamResult sr = new StreamResult( sw );
		transformer.transform( domSource, sr );
		String result = sw.toString( ).trim( );
		sw.close( );
		return result;
	}

}