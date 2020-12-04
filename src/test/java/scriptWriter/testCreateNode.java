
package scriptWriter;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.nodes.oracle.TInvokerRightsClause;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.para.styleenums.TCaseOption;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import gudusoft.gsqlparser.stmt.*;
import gudusoft.gsqlparser.stmt.mssql.TMssqlBlock;
import gudusoft.gsqlparser.stmt.mssql.TMssqlDeclare;
import gudusoft.gsqlparser.stmt.mssql.TMssqlExecute;
import gudusoft.gsqlparser.stmt.mssql.TMssqlIfElse;
import gudusoft.gsqlparser.stmt.mssql.TMssqlPrint;
import gudusoft.gsqlparser.stmt.mssql.TMssqlRaiserror;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateProcedure;
import junit.framework.TestCase;

import java.util.ArrayList;


public class testCreateNode extends TestCase
{

	private TGSqlParser OracleParser = null;
	private TGSqlParser SQLServerParser = null;


	protected void setUp() throws Exception {
		super.setUp();
		OracleParser = new TGSqlParser(EDbVendor.dbvoracle);
		SQLServerParser = new TGSqlParser(EDbVendor.dbvmssql);

	}

	protected void tearDown() throws Exception {
		OracleParser = null;
		SQLServerParser = null;
		super.tearDown();
	}

	public void testCreateSourceToken( )
	{
		TSourceToken st = new TSourceToken("AToken");
		assertTrue(st.toScript().equalsIgnoreCase("AToken"));

		//TGSqlParser sqlParser= new TGSqlParser(EDbVendor.dbvmssql);
	}

	public void testCreateObjectname( )
	{
		// use new constructor to create an object name
		TObjectName tableName = new TObjectName(new TSourceToken("ATable"), EDbObjectType.table);
		assertTrue(tableName.toScript().equalsIgnoreCase("ATable"));

		TObjectName columnName = new TObjectName(EDbObjectType.column,new TSourceToken("ATable"),new TSourceToken("AColumn"));
		assertTrue(columnName.toScript().equalsIgnoreCase("ATable.AColumn"));

		// use parseObjectName() method to create a three parts object name
		TGSqlParser sqlParser= new TGSqlParser(EDbVendor.dbvmssql);
		columnName = sqlParser.parseObjectName("scott.emp.salary");
		assertTrue(columnName.toScript().equalsIgnoreCase("scott.emp.salary"));
	}

	public void testCreateConstant( )
	{
		// use new constructor to create constant
		TConstant numberConstant = new TConstant(ELiteralType.etNumber,new TSourceToken("9.1"));
		assertTrue(numberConstant.toScript().equalsIgnoreCase("9.1"));

		// use parseConstant() method to create constant
		TGSqlParser sqlParser= new TGSqlParser(EDbVendor.dbvmssql);
		numberConstant = sqlParser.parseConstant("9.1");
		assertTrue(numberConstant.toScript().equalsIgnoreCase("9.1"));
	}

	public void testCreateFunction( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvmssql);
		TFunctionCall functionCall = sqlParser.parseFunctionCall("fx(a1,a2)");
		assertTrue( functionCall.getFunctionName( )
				.toScript()
				.equalsIgnoreCase( "fx" ) );
	}


	public void testCreateSubquery( ) {
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvmssql);

		String subQueryStr = "	SELECT *\r\n"
				+ "	FROM CompanyData.dbo.Customers_33\r\n"
				+ "	UNION ALL\r\n"
				+ "	SELECT *\r\n"
				+ "	FROM Server2.CompanyData.dbo.Customers_66\r\n"
				+ "	UNION ALL\r\n"
				+ "	SELECT *\r\n"
				+ "	FROM Server3.CompanyData.dbo.Customers_99";

		TSelectSqlStatement subquery = sqlParser.parseSubquery(subQueryStr);
	}

	public void testCreateTable( )
	{

		TCreateTableSqlStatement createTable = new TCreateTableSqlStatement( EDbVendor.dbvoracle );
		
		TTable table = new TTable( );
		table.setTableName( OracleParser.parseObjectName( "newTable" ) );
		createTable.setTargetTable( table );

		TColumnDefinitionList columns = new TColumnDefinitionList( );
		createTable.setColumnList( columns );

		TColumnDefinition column1 = new TColumnDefinition( );
		columns.addColumn( column1 );
		column1.setColumnName( OracleParser.parseObjectName("column1") );
		TTypeName datatype1 = new TTypeName( );
		datatype1.setDataType( EDataType.number_t );
		datatype1.setPrecision(OracleParser.parseConstant("10"));
		datatype1.setScale( OracleParser.parseConstant("2") );
		column1.setDatatype( datatype1 );

		TConstraintList constraintList1 = new TConstraintList( );
		column1.setConstraints( constraintList1 );
		TConstraint constraint1 = new TConstraint( );
		constraintList1.addConstraint( constraint1 );
		constraint1.setConstraint_type( EConstraintType.primary_key );

		TColumnDefinition column2 = new TColumnDefinition( );
		columns.addColumn( column2 );
		column2.setColumnName( OracleParser.parseObjectName("column2") );
		TTypeName datatype2 = new TTypeName( );
		datatype2.setDataType( EDataType.char_t );
		datatype2.setLength( OracleParser.parseConstant("10") );
		column2.setDatatype( datatype2 );

		TConstraintList constraintList2 = new TConstraintList( );
		column2.setConstraints( constraintList2 );
		TConstraint constraint2 = new TConstraint( );
		constraintList2.addConstraint( constraint2 );
		constraint2.setConstraint_type( EConstraintType.notnull );

		TColumnDefinition column3 = new TColumnDefinition( );
		columns.addColumn( column3 );
		column3.setColumnName( OracleParser.parseObjectName("title") );
		TTypeName datatype3 = new TTypeName( );
		datatype3.setDataType( EDataType.varchar_t );
		datatype3.setLength( OracleParser.parseConstant("20") );
		column3.setDatatype( datatype3 );

		TConstraintList constraintList3 = new TConstraintList( );
		column3.setConstraints( constraintList3 );
		TConstraint constraint3 = new TConstraint( );
		constraintList3.addConstraint( constraint3 );
		constraint3.setConstraint_type( EConstraintType.default_value );
		constraint3.setDefaultExpression(OracleParser.parseExpression("'manager'"));

		TColumnDefinition column4 = new TColumnDefinition( );
		columns.addColumn( column4 );
		column4.setColumnName( OracleParser.parseObjectName("column4") );
		TTypeName datatype4 = new TTypeName( );
		datatype4.setDataType( EDataType.integer_t );
		column4.setDatatype( datatype4 );

		TConstraintList constraintList4 = new TConstraintList( );
		column4.setConstraints( constraintList4 );
		TConstraint constraint4 = new TConstraint( );
		constraintList4.addConstraint( constraint4 );
		constraint4.setConstraint_type( EConstraintType.reference );
		constraint4.setReferencedObject( OracleParser.parseObjectName("table2") );

		TObjectNameList referencedColumns = new TObjectNameList( );
		referencedColumns.addObjectName( OracleParser.parseObjectName("ref_column") );
		constraint4.setReferencedColumnList( referencedColumns );

		TConstraintList tableConstraints = new TConstraintList( );
		createTable.setTableConstraints( tableConstraints );
		TConstraint tableConstraint = new TConstraint( );
		tableConstraints.addConstraint( tableConstraint );
		tableConstraint.setConstraint_type( EConstraintType.foreign_key );
		tableConstraint.setReferencedObject( OracleParser.parseObjectName("table3") );

		TPTNodeList<TColumnWithSortOrder> Columns = new TPTNodeList<TColumnWithSortOrder>( );
		Columns.addNode( new TColumnWithSortOrder(
				OracleParser.parseObjectName( "column1" ) ) );
		Columns.addNode( new TColumnWithSortOrder(
				OracleParser.parseObjectName( "column2" ) ) );
		tableConstraint.setColumnList( Columns );

		TObjectNameList referencedColumns2 = new TObjectNameList( );
		referencedColumns2.addObjectName( OracleParser.parseObjectName("ref_column1") );
		referencedColumns2.addObjectName( OracleParser.parseObjectName("ref_column2") );
		tableConstraint.setReferencedColumnList( referencedColumns2 );

		// System.out.println(scriptGenerator.generateScript(createTable,
		// true));
		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
				,createTable.toScript()
				,"CREATE TABLE newtable(column1 NUMBER (10,2) PRIMARY KEY,\n"
								+ "                      column2 CHAR (10) NOT NULL,\n"
								+ "                      title   VARCHAR (20) DEFAULT 'manager',\n"
								+ "                      column4 INTEGER REFERENCES table2(ref_column),\n"
								+ "                      FOREIGN KEY (column1,column2) REFERENCES table3(ref_column1,ref_column2) )"
		));

	}

	public void testMssqlCreateTrigger( )
	{
		TCreateTriggerStmt createTrigger = new TCreateTriggerStmt( EDbVendor.dbvmssql );

		createTrigger.setTriggerName(SQLServerParser.parseObjectName("reminder"));

		TSimpleDmlTriggerClause dmlTriggerClause = new TSimpleDmlTriggerClause();
		dmlTriggerClause.setActionTime(ETriggerActionTime.tatFor);

		TDmlEventClause dmlEventClause = new TDmlEventClause();
		dmlEventClause.setTableName(SQLServerParser.parseObjectName("titles"));
		ArrayList<TTriggerEventItem> dmlEventItems = new ArrayList<TTriggerEventItem>();
		TDmlEventItem dmlEventItem1 = new TDmlEventItem();
		dmlEventItem1.setDmlType(ESqlStatementType.sstinsert);
		TDmlEventItem dmlEventItem2 = new TDmlEventItem();
		dmlEventItem2.setDmlType(ESqlStatementType.sstupdate);
		dmlEventItems.add(dmlEventItem1);
		dmlEventItems.add(dmlEventItem2);
		dmlEventClause.setEventItems(dmlEventItems);

		dmlTriggerClause.setEventClause(dmlEventClause);
		createTrigger.setTriggeringClause(dmlTriggerClause);


		TStatementList stmts = new TStatementList( );

		TMssqlRaiserror error = new TMssqlRaiserror( EDbVendor.dbvmssql );
		error.setMessageText( SQLServerParser.parseExpression("50009") );
		error.setSeverity( SQLServerParser.parseExpression("16") );
		error.setState(SQLServerParser.parseExpression("10"));

		stmts.add( error );

		createTrigger.setBodyStatements( stmts );

		String createTriggerQuery = "CREATE TRIGGER reminder\r\n"
				+ "ON titles\r\n"
				+ "FOR INSERT , UPDATE\r\n"
				+ "AS RAISERROR (50009,16,10)";

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql
				,createTrigger.toScript()
				,createTriggerQuery
		));

	}

	public void testMssqlCreateTrigger1( )
	{
		TCreateTriggerStmt createTrigger = new TCreateTriggerStmt( EDbVendor.dbvmssql );
		createTrigger.setTriggerName(SQLServerParser.parseObjectName("reminder"));

		TSimpleDmlTriggerClause dmlTriggerClause = new TSimpleDmlTriggerClause();
		dmlTriggerClause.setActionTime(ETriggerActionTime.tatInsteadOf);

		TDmlEventClause dmlEventClause = new TDmlEventClause();
		dmlEventClause.setTableName(SQLServerParser.parseObjectName("titles"));
		ArrayList<TTriggerEventItem> dmlEventItems = new ArrayList<TTriggerEventItem>();
		TDmlEventItem dmlEventItem1 = new TDmlEventItem();
		dmlEventItem1.setDmlType(ESqlStatementType.sstinsert);
		TDmlEventItem dmlEventItem2 = new TDmlEventItem();
		dmlEventItem2.setDmlType(ESqlStatementType.sstupdate);
		dmlEventItems.add(dmlEventItem1);
		dmlEventItems.add(dmlEventItem2);
		dmlEventClause.setEventItems(dmlEventItems);

		dmlTriggerClause.setEventClause(dmlEventClause);
		createTrigger.setTriggeringClause(dmlTriggerClause);

		TStatementList stmts = new TStatementList( );

		TMssqlRaiserror error = new TMssqlRaiserror( EDbVendor.dbvmssql );
		error.setMessageText( SQLServerParser.parseExpression("50009") );
		error.setSeverity( SQLServerParser.parseExpression("16") );
		error.setState(SQLServerParser.parseExpression("10"));

		stmts.add( error );

		createTrigger.setBodyStatements( stmts );

		String createTriggerQuery = "CREATE TRIGGER reminder\r\n"
				+ "ON titles\r\n"
				+ "instead of INSERT , UPDATE\r\n"
				+ "AS RAISERROR (50009,16,10)";

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql
				,createTrigger.toScript()
				,createTriggerQuery
		));


	}

	public void testMssqlAlterTrigger2( )
	{
		TCreateTriggerStmt createTrigger = new TCreateTriggerStmt( EDbVendor.dbvmssql );

		createTrigger.setTriggerName(SQLServerParser.parseObjectName("reminder"));
		createTrigger.setAlterTrigger( true );

		TSimpleDmlTriggerClause dmlTriggerClause = new TSimpleDmlTriggerClause();
		dmlTriggerClause.setActionTime(ETriggerActionTime.tatInsteadOf);

		TDmlEventClause dmlEventClause = new TDmlEventClause();
		dmlEventClause.setTableName(SQLServerParser.parseObjectName("titles"));
		ArrayList<TTriggerEventItem> dmlEventItems = new ArrayList<TTriggerEventItem>();
		TDmlEventItem dmlEventItem1 = new TDmlEventItem();
		dmlEventItem1.setDmlType(ESqlStatementType.sstinsert);
		TDmlEventItem dmlEventItem2 = new TDmlEventItem();
		dmlEventItem2.setDmlType(ESqlStatementType.sstupdate);
		dmlEventItems.add(dmlEventItem1);
		dmlEventItems.add(dmlEventItem2);
		dmlEventClause.setEventItems(dmlEventItems);

		dmlTriggerClause.setEventClause(dmlEventClause);
		createTrigger.setTriggeringClause(dmlTriggerClause);


		TStatementList stmts = new TStatementList( );

		TMssqlRaiserror error = new TMssqlRaiserror( EDbVendor.dbvmssql );
		error.setMessageText( SQLServerParser.parseExpression("50009") );
		error.setSeverity( SQLServerParser.parseExpression("16") );
		error.setState(SQLServerParser.parseExpression("10"));

		stmts.add( error );

		createTrigger.setBodyStatements( stmts );

		String createTriggerQuery = "ALTER TRIGGER reminder\r\n"
				+ "ON titles\r\n"
				+ "instead of INSERT , UPDATE\r\n"
				+ "AS RAISERROR (50009,16,10)";

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql
				,createTrigger.toScript()
				,createTriggerQuery
		));

	}

	public void testMssqlCreateTrigger3( )
	{
		TCreateTriggerStmt createTrigger = new TCreateTriggerStmt( EDbVendor.dbvmssql );
		createTrigger.setTriggerName(SQLServerParser.parseObjectName("reminder"));

		TSimpleDmlTriggerClause dmlTriggerClause = new TSimpleDmlTriggerClause();
		dmlTriggerClause.setActionTime(ETriggerActionTime.tatInsteadOf);

		TDmlEventClause dmlEventClause = new TDmlEventClause();
		dmlEventClause.setTableName(SQLServerParser.parseObjectName("titles"));
		ArrayList<TTriggerEventItem> dmlEventItems = new ArrayList<TTriggerEventItem>();
		TDmlEventItem dmlEventItem1 = new TDmlEventItem();
		dmlEventItem1.setDmlType(ESqlStatementType.sstinsert);
		TDmlEventItem dmlEventItem2 = new TDmlEventItem();
		dmlEventItem2.setDmlType(ESqlStatementType.sstupdate);
		dmlEventItems.add(dmlEventItem1);
		dmlEventItems.add(dmlEventItem2);
		dmlEventClause.setEventItems(dmlEventItems);

		dmlTriggerClause.setEventClause(dmlEventClause);
		createTrigger.setTriggeringClause(dmlTriggerClause);

		TMssqlIfElse ifstmt = new TMssqlIfElse( EDbVendor.dbvmssql );
		ifstmt.setCondition(SQLServerParser.parseExpression("update(col1)"));
		TMssqlRaiserror error = new TMssqlRaiserror( EDbVendor.dbvmssql );
		error.setMessageText(SQLServerParser.parseExpression("50009"));
		error.setSeverity( SQLServerParser.parseExpression("16") );
		error.setState( SQLServerParser.parseExpression("10") );
		ifstmt.setStmt( error );

		createTrigger.getBodyStatements( ).add( ifstmt );

		String createTriggerQuery = "CREATE TRIGGER reminder\r\n"
				+ "ON titles\r\n"
				+ "instead of INSERT , UPDATE\r\n"
				+ "AS if update(col1) RAISERROR (50009,16,10)";

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql
				,createTrigger.toScript()
				,createTriggerQuery
		));

	}

	public void testMssqlCreateTrigger4( )
	{
		TCreateTriggerStmt createTrigger = new TCreateTriggerStmt( EDbVendor.dbvmssql );
		createTrigger.setTriggerName(SQLServerParser.parseObjectName("reminder"));

		TSimpleDmlTriggerClause dmlTriggerClause = new TSimpleDmlTriggerClause();
		dmlTriggerClause.setActionTime(ETriggerActionTime.tatInsteadOf);

		TDmlEventClause dmlEventClause = new TDmlEventClause();
		dmlEventClause.setTableName(SQLServerParser.parseObjectName("titles"));
		ArrayList<TTriggerEventItem> dmlEventItems = new ArrayList<TTriggerEventItem>();
		TDmlEventItem dmlEventItem1 = new TDmlEventItem();
		dmlEventItem1.setDmlType(ESqlStatementType.sstinsert);
		TDmlEventItem dmlEventItem2 = new TDmlEventItem();
		dmlEventItem2.setDmlType(ESqlStatementType.sstupdate);
		dmlEventItems.add(dmlEventItem1);
		dmlEventItems.add(dmlEventItem2);
		dmlEventClause.setEventItems(dmlEventItems);

		dmlTriggerClause.setEventClause(dmlEventClause);
		createTrigger.setTriggeringClause(dmlTriggerClause);

		TMssqlIfElse ifstmt = new TMssqlIfElse( EDbVendor.dbvmssql );
		ifstmt.setCondition(SQLServerParser.parseExpression("update(col1)"));
		TMssqlRaiserror error = new TMssqlRaiserror( EDbVendor.dbvmssql );
		error.setMessageText(SQLServerParser.parseExpression("50009"));
		error.setSeverity( SQLServerParser.parseExpression("16") );
		error.setState( SQLServerParser.parseExpression("10") );
		TMssqlBlock block = new TMssqlBlock( EDbVendor.dbvmssql );
		block.getBodyStatements( ).add( error );
		ifstmt.setStmt( block );

		createTrigger.getBodyStatements( ).add( ifstmt );

		String createTriggerQuery = "CREATE TRIGGER reminder\r\n"
				+ "ON titles\r\n"
				+ "instead of INSERT , UPDATE\r\n"
				+ "AS if update(col1)\r\n"
				+ "  begin \r\n"
				+ "  RAISERROR (50009,16,10)\r\n"
				+ " end";

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql
				,createTrigger.toScript()
				,createTriggerQuery
		));

	}

	public void testMssqlCreateTrigger5( )
	{
		TCreateTriggerStmt createTrigger = new TCreateTriggerStmt( EDbVendor.dbvmssql );
		createTrigger.setTriggerName(SQLServerParser.parseObjectName("reminder"));

		TSimpleDmlTriggerClause dmlTriggerClause = new TSimpleDmlTriggerClause();
		dmlTriggerClause.setActionTime(ETriggerActionTime.tatInsteadOf);

		TDmlEventClause dmlEventClause = new TDmlEventClause();
		dmlEventClause.setTableName(SQLServerParser.parseObjectName("titles"));
		ArrayList<TTriggerEventItem> dmlEventItems = new ArrayList<TTriggerEventItem>();
		TDmlEventItem dmlEventItem0 = new TDmlEventItem();
		dmlEventItem0.setDmlType(ESqlStatementType.sstdelete);
		TDmlEventItem dmlEventItem1 = new TDmlEventItem();
		dmlEventItem1.setDmlType(ESqlStatementType.sstinsert);
		TDmlEventItem dmlEventItem2 = new TDmlEventItem();
		dmlEventItem2.setDmlType(ESqlStatementType.sstupdate);
		dmlEventItems.add(dmlEventItem0);
		dmlEventItems.add(dmlEventItem1);
		dmlEventItems.add(dmlEventItem2);
		dmlEventClause.setEventItems(dmlEventItems);

		dmlTriggerClause.setEventClause(dmlEventClause);
		createTrigger.setTriggeringClause(dmlTriggerClause);


		TMssqlExecute exec = new TMssqlExecute( EDbVendor.dbvmssql );
		exec.setModuleName(SQLServerParser.parseObjectName("master..xp_sendmail"));
		TExecParameterList params = new TExecParameterList( );
		TExecParameter param1 = new TExecParameter( );
		param1.setParameterValue( SQLServerParser.parseExpression("'MaryM'") );
		params.addExecParameter( param1 );
		TExecParameter param2 = new TExecParameter( );
		param2.setParameterValue(SQLServerParser.parseExpression("'Don''t forget to print a report for the distributors.'"));
		params.addExecParameter( param2 );
		exec.setParameters( params );
		createTrigger.getBodyStatements( ).add( exec );

		String createTriggerQuery = "CREATE TRIGGER reminder\r\n"
				+ "ON titles\r\n"
				+ "instead of DELETE , INSERT , UPDATE\r\n"
				+ "AS EXEC master..xp_sendmail 'MaryM',\r\n"
				+ "      'Don''t forget to print a report for the distributors.'";

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql
				,createTrigger.toScript()
				,createTriggerQuery
		));

	}

	public void testMssqlCreateTrigger6( )
	{
		TCreateTriggerStmt createTrigger = new TCreateTriggerStmt( EDbVendor.dbvmssql );
		createTrigger.setTriggerName(SQLServerParser.parseObjectName("employee_insupd"));

		TSimpleDmlTriggerClause dmlTriggerClause = new TSimpleDmlTriggerClause();
		dmlTriggerClause.setActionTime(ETriggerActionTime.tatFor);

		TDmlEventClause dmlEventClause = new TDmlEventClause();
		dmlEventClause.setTableName(SQLServerParser.parseObjectName("employee"));
		ArrayList<TTriggerEventItem> dmlEventItems = new ArrayList<TTriggerEventItem>();
		TDmlEventItem dmlEventItem1 = new TDmlEventItem();
		dmlEventItem1.setDmlType(ESqlStatementType.sstinsert);
		TDmlEventItem dmlEventItem2 = new TDmlEventItem();
		dmlEventItem2.setDmlType(ESqlStatementType.sstupdate);
		dmlEventItems.add(dmlEventItem1);
		dmlEventItems.add(dmlEventItem2);
		dmlEventClause.setEventItems(dmlEventItems);

		dmlTriggerClause.setEventClause(dmlEventClause);
		createTrigger.setTriggeringClause(dmlTriggerClause);


		TMssqlDeclare declare = new TMssqlDeclare( EDbVendor.dbvmssql );
		TDeclareVariableList vars = new TDeclareVariableList( );
		TDeclareVariable var = new TDeclareVariable( );
		var.setVariableName( SQLServerParser.parseObjectName("@min_lvl") );
		TTypeName datatype = new TTypeName( );
		datatype.setDataType( EDataType.tinyint_t );
		var.setDatatype( datatype );
		vars.addDeclareVariable( var );

		TDeclareVariable var1 = new TDeclareVariable( );
		var1.setVariableName( SQLServerParser.parseObjectName("@max_lvl") );
		TTypeName datatype1 = new TTypeName( );
		datatype1.setDataType( EDataType.tinyint_t );
		var1.setDatatype( datatype1 );
		vars.addDeclareVariable( var1 );

		TDeclareVariable var2 = new TDeclareVariable( );
		var2.setVariableName( SQLServerParser.parseObjectName("@emp_lvl") );
		TTypeName datatype2 = new TTypeName( );
		datatype2.setDataType( EDataType.tinyint_t );
		var2.setDatatype( datatype2 );
		vars.addDeclareVariable( var2 );

		TDeclareVariable var3 = new TDeclareVariable( );
		var3.setVariableName( SQLServerParser.parseObjectName("@job_id") );
		TTypeName datatype3 = new TTypeName( );
		datatype3.setDataType( EDataType.smallint_t );
		var3.setDatatype( datatype3 );
		vars.addDeclareVariable( var3 );

		declare.setVariables( vars );

		createTrigger.getBodyStatements( ).add( declare );
		createTrigger.getBodyStatements( )
				.add( SQLServerParser.parseSubquery ( "SELECT @min_lvl = min_lvl,\r\n"
						+ "   @max_lvl = max_lvl,\r\n"
						+ "   @emp_lvl = i.job_lvl,\r\n"
						+ "   @job_id = i.job_id\r\n"
						+ "FROM employee e INNER JOIN inserted i ON e.emp_id = i.emp_id\r\n"
						+ "   JOIN jobs j ON j.job_id = i.job_id" ) );

		TMssqlIfElse ifElse = new TMssqlIfElse( EDbVendor.dbvmssql );
		ifElse.setCondition( SQLServerParser.parseExpression("(@job_id = 1) and (@emp_lvl <> 10)") );

		TMssqlBlock block = new TMssqlBlock( EDbVendor.dbvmssql );
		TMssqlRaiserror error = new TMssqlRaiserror( EDbVendor.dbvmssql );
		error.setMessageText( new TExpression(SQLServerParser.parseConstant("'Job id 1 expects the default level of 10.'") ));
		error.setSeverity( new TExpression(SQLServerParser.parseConstant("16")) );
		error.setState( new TExpression(SQLServerParser.parseConstant("1")) );
		block.getBodyStatements( ).add( error );

		// TMssqlRollback rollback = new TMssqlRollback( EDbVendor.dbvmssql );
		// rollback.setTrans_or_work( new TSourceToken( "transaction" ) );
		// block.getBodyStatements( ).add( rollback );

		ifElse.setStmt( block );

		TMssqlIfElse ifStmt = new TMssqlIfElse( EDbVendor.dbvmssql );
		ifStmt.setCondition( SQLServerParser.parseExpression("NOT(@emp_lvl BETWEEN @min_lvl AND @max_lvl)") );

		TMssqlBlock block1 = new TMssqlBlock( EDbVendor.dbvmssql );
		TMssqlRaiserror error1 = new TMssqlRaiserror( EDbVendor.dbvmssql );
		error1.setMessageText(new TExpression(SQLServerParser.parseConstant("'The level for job_id:%d should be between %d and %d.'")) );
		error1.setSeverity( new TExpression(SQLServerParser.parseConstant("16")) );
		error1.setState( new TExpression(SQLServerParser.parseConstant("1")) );
		TExpressionList expressions = new TExpressionList( );
		expressions.addExpression( new TExpression(SQLServerParser.parseObjectName("@job_id")) );
		expressions.addExpression( new TExpression(SQLServerParser.parseObjectName("@min_lvl")) );
		expressions.addExpression( new TExpression(SQLServerParser.parseObjectName("@max_lvl")) );
		error1.setArgs( expressions );
		block1.getBodyStatements( ).add( error1 );

		// TMssqlRollback rollback1 = new TMssqlRollback( EDbVendor.dbvmssql );
		// rollback1.setTrans_or_work( new TSourceToken( "transaction" ) );
		// block1.getBodyStatements( ).add( rollback );
		ifStmt.setStmt( block1 );

		ifElse.setElseStmt( ifStmt );
		createTrigger.getBodyStatements( ).add( ifElse );

		String createTriggerQuery = "CREATE TRIGGER employee_insupd\r\n"
				+ "ON employee\r\n"
				+ "FOR INSERT , UPDATE\r\n"
				+ "AS\r\n"
				+ "DECLARE @min_lvl tinyint,\r\n"
				+ "   @max_lvl tinyint,\r\n"
				+ "   @emp_lvl tinyint,\r\n"
				+ "   @job_id smallint ;\r\n"
				+ "SELECT @min_lvl = min_lvl,\r\n"
				+ "   @max_lvl = max_lvl,\r\n"
				+ "   @emp_lvl = i.job_lvl,\r\n"
				+ "   @job_id = i.job_id\r\n"
				+ "FROM employee e INNER JOIN inserted i ON e.emp_id = i.emp_id\r\n"
				+ "   JOIN jobs j ON j.job_id = i.job_id;\r\n"
				+ "IF (@job_id = 1) and (@emp_lvl <> 10)\r\n"
				+ "BEGIN \r\n"
				+ "   RAISERROR ('Job id 1 expects the default level of 10.',16,1)\r\n"
				// + "   ROLLBACK TRANSACTION\r\n"
				+ "END \r\n"
				+ "ELSE \r\n"
				+ "IF NOT(@emp_lvl BETWEEN @min_lvl AND @max_lvl)\r\n"
				+ "BEGIN \r\n"
				+ "   RAISERROR ('The level for job_id:%d should be between %d and %d.',"
				+ "16,1,@job_id,@min_lvl,@max_lvl)\r\n"
				// + "   ROLLBACK TRANSACTION\r\n"
				+ "END ;";

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql
				,createTrigger.toScript()
				,createTriggerQuery
		));
	}

	public void testMssqlCreateTrigger7( )
	{
		TCreateTriggerStmt createTrigger = new TCreateTriggerStmt( EDbVendor.dbvmssql );
		createTrigger.setTriggerName(SQLServerParser.parseObjectName("trig1"));

		TSimpleDmlTriggerClause dmlTriggerClause = new TSimpleDmlTriggerClause();
		dmlTriggerClause.setActionTime(ETriggerActionTime.tatFor);

		TDmlEventClause dmlEventClause = new TDmlEventClause();
		dmlEventClause.setTableName(SQLServerParser.parseObjectName("authors"));
		ArrayList<TTriggerEventItem> dmlEventItems = new ArrayList<TTriggerEventItem>();
		TDmlEventItem dmlEventItem0 = new TDmlEventItem();
		dmlEventItem0.setDmlType(ESqlStatementType.sstdelete);
		TDmlEventItem dmlEventItem1 = new TDmlEventItem();
		dmlEventItem1.setDmlType(ESqlStatementType.sstinsert);
		TDmlEventItem dmlEventItem2 = new TDmlEventItem();
		dmlEventItem2.setDmlType(ESqlStatementType.sstupdate);
		dmlEventItems.add(dmlEventItem0);
		dmlEventItems.add(dmlEventItem1);
		dmlEventItems.add(dmlEventItem2);
		dmlEventClause.setEventItems(dmlEventItems);

		dmlTriggerClause.setEventClause(dmlEventClause);
		createTrigger.setTriggeringClause(dmlTriggerClause);


		createTrigger.getBodyStatements( )
				.add( SQLServerParser.parseSubquery("SELECT a.au_lname, a.au_fname, x.info\r\n"
						+ "FROM authors a INNER JOIN does_not_exist x\r\n"
						+ "   ON a.au_id = x.au_id") );

		String createTriggerQuery = "CREATE TRIGGER trig1\r\n"
				+ "on authors\r\n"
				+ "FOR DELETE , INSERT , UPDATE\r\n"
				+ "AS\r\n"
				+ "   SELECT a.au_lname, a.au_fname, x.info\r\n"
				+ "   FROM authors a INNER JOIN does_not_exist x\r\n"
				+ "      ON a.au_id = x.au_id";

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql
				,createTrigger.toScript()
				,createTriggerQuery
		));
	}

	public void testMssqlCreateTrigger8( )
	{
		TCreateTriggerStmt createTrigger = new TCreateTriggerStmt( EDbVendor.dbvmssql );
		createTrigger.setTriggerName(SQLServerParser.parseObjectName("trig2"));

		TSimpleDmlTriggerClause dmlTriggerClause = new TSimpleDmlTriggerClause();
		dmlTriggerClause.setActionTime(ETriggerActionTime.tatFor);

		TDmlEventClause dmlEventClause = new TDmlEventClause();
		dmlEventClause.setTableName(SQLServerParser.parseObjectName("authors"));
		ArrayList<TTriggerEventItem> dmlEventItems = new ArrayList<TTriggerEventItem>();
		TDmlEventItem dmlEventItem1 = new TDmlEventItem();
		dmlEventItem1.setDmlType(ESqlStatementType.sstinsert);
		TDmlEventItem dmlEventItem2 = new TDmlEventItem();
		dmlEventItem2.setDmlType(ESqlStatementType.sstupdate);
		dmlEventItems.add(dmlEventItem1);
		dmlEventItems.add(dmlEventItem2);
		dmlEventClause.setEventItems(dmlEventItems);

		dmlTriggerClause.setEventClause(dmlEventClause);
		createTrigger.setTriggeringClause(dmlTriggerClause);

		TMssqlDeclare declare = new TMssqlDeclare( EDbVendor.dbvmssql );
		TDeclareVariableList vars = new TDeclareVariableList( );
		TDeclareVariable var = new TDeclareVariable( );
		var.setVariableName( SQLServerParser.parseObjectName("@fax") );
		TTypeName datatype = new TTypeName( );
		datatype.setDataType(EDataType.varchar_t);
		datatype.setLength( SQLServerParser.parseConstant("12") );
		var.setDatatype( datatype );
		vars.addDeclareVariable( var );
		declare.setVariables( vars );

		createTrigger.getBodyStatements( ).add( declare );

		createTrigger.getBodyStatements( )
				.add( SQLServerParser.parseSubquery("SELECT @fax = phone\r\n"
						+ "FROM authors") );

		String createTriggerQuery = "CREATE TRIGGER trig2\r\n"
				+ "ON authors\r\n"
				+ "FOR INSERT , UPDATE\r\n"
				+ "AS\r\n"
				+ "   DECLARE @fax varchar (12);\r\n"
				+ "   SELECT @fax = phone\r\n"
				+ "   FROM authors;";

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql
				, createTrigger.toScript()
				, createTriggerQuery
		));
	}

	public void testMssqlCreateTrigger9( )
	{
		TCreateTriggerStmt createTrigger = new TCreateTriggerStmt( EDbVendor.dbvmssql );
		createTrigger.setTriggerName(SQLServerParser.parseObjectName("updEmployeeData"));

		TSimpleDmlTriggerClause dmlTriggerClause = new TSimpleDmlTriggerClause();
		dmlTriggerClause.setActionTime(ETriggerActionTime.tatFor);

		TDmlEventClause dmlEventClause = new TDmlEventClause();
		dmlEventClause.setTableName(SQLServerParser.parseObjectName("employeeData"));
		ArrayList<TTriggerEventItem> dmlEventItems = new ArrayList<TTriggerEventItem>();
		TDmlEventItem dmlEventItem2 = new TDmlEventItem();
		dmlEventItem2.setDmlType(ESqlStatementType.sstupdate);
		dmlEventItems.add(dmlEventItem2);
		dmlEventClause.setEventItems(dmlEventItems);

		dmlTriggerClause.setEventClause(dmlEventClause);
		createTrigger.setTriggeringClause(dmlTriggerClause);


		TMssqlIfElse ifElse = new TMssqlIfElse( EDbVendor.dbvmssql );
		ifElse.setCondition( SQLServerParser.parseExpression("(COLUMNS_UPDATED() & 14) > 0") );

		TMssqlBlock block = new TMssqlBlock( EDbVendor.dbvmssql );

		TInsertSqlStatement insert = new TInsertSqlStatement( EDbVendor.dbvmssql );
		TTable insertTable = new TTable( );
		insertTable.setTableName( SQLServerParser.parseObjectName("auditEmployeeData") );
		insert.setTargetTable( insertTable );

		TObjectNameList columnNameList = new TObjectNameList( );
		insert.setColumnList( columnNameList );
		columnNameList.addObjectName( SQLServerParser.parseObjectName("audit_log_type") );
		columnNameList.addObjectName( SQLServerParser.parseObjectName("audit_emp_id") );
		columnNameList.addObjectName( SQLServerParser.parseObjectName("audit_emp_bankAccountNumber") );
		columnNameList.addObjectName( SQLServerParser.parseObjectName("audit_emp_salary") );
		columnNameList.addObjectName( SQLServerParser.parseObjectName("audit_emp_SSN") );

		insert.setSubQuery( SQLServerParser.parseSubquery("SELECT 'OLD',\r\n"
				+ "   del.emp_id,\r\n"
				+ "   del.emp_bankAccountNumber,\r\n"
				+ "   del.emp_salary,\r\n"
				+ "   del.emp_SSN\r\n"
				+ "FROM deleted del") );

		block.getBodyStatements( ).add( insert );

		TInsertSqlStatement insert1 = new TInsertSqlStatement( EDbVendor.dbvmssql );
		TTable insertTable1 = new TTable( );
		insertTable1.setTableName( SQLServerParser.parseObjectName("auditEmployeeData") );
		insert1.setTargetTable( insertTable1 );

		TObjectNameList columnNameList1 = new TObjectNameList( );
		insert1.setColumnList( columnNameList1 );
		columnNameList1.addObjectName( SQLServerParser.parseObjectName("audit_log_type") );
		columnNameList1.addObjectName( SQLServerParser.parseObjectName("audit_emp_id") );
		columnNameList1.addObjectName( SQLServerParser.parseObjectName("audit_emp_bankAccountNumber") );
		columnNameList1.addObjectName( SQLServerParser.parseObjectName("audit_emp_salary") );
		columnNameList1.addObjectName( SQLServerParser.parseObjectName("audit_emp_SSN") );

		insert1.setSubQuery( SQLServerParser.parseSubquery("SELECT 'NEW',\r\n"
				+ "   ins.emp_id,\r\n"
				+ "   ins.emp_bankAccountNumber,\r\n"
				+ "   ins.emp_salary,\r\n"
				+ "   ins.emp_SSN\r\n"
				+ "FROM inserted ins") );
		block.getBodyStatements( ).add( insert1 );

		ifElse.setStmt( block );
		createTrigger.getBodyStatements( ).add( ifElse );

		String createTriggerQuery = "CREATE TRIGGER updEmployeeData\r\n"
				+ "ON employeeData\r\n"
				+ "FOR update\r\n"
				+ "AS\r\n"
				+ "   IF (COLUMNS_UPDATED() & 14) > 0\r\n"
				+ "      BEGIN \r\n"
				+ "      INSERT INTO auditEmployeeData\r\n"
				+ "         (audit_log_type,\r\n"
				+ "         audit_emp_id,\r\n"
				+ "         audit_emp_bankAccountNumber,\r\n"
				+ "         audit_emp_salary,\r\n"
				+ "         audit_emp_SSN)\r\n"
				+ "         SELECT 'OLD',\r\n"
				+ "            del.emp_id,\r\n"
				+ "            del.emp_bankAccountNumber,\r\n"
				+ "            del.emp_salary,\r\n"
				+ "            del.emp_SSN\r\n"
				+ "         FROM deleted del;\r\n"
				+ "      INSERT INTO auditEmployeeData\r\n"
				+ "         (audit_log_type,\r\n"
				+ "         audit_emp_id,\r\n"
				+ "         audit_emp_bankAccountNumber,\r\n"
				+ "         audit_emp_salary,\r\n"
				+ "         audit_emp_SSN)\r\n"
				+ "         SELECT 'NEW',\r\n"
				+ "            ins.emp_id,\r\n"
				+ "            ins.emp_bankAccountNumber,\r\n"
				+ "            ins.emp_salary,\r\n"
				+ "            ins.emp_SSN\r\n"
				+ "         FROM inserted ins;\r\n"
				+ "   END";

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql
				, createTrigger.toScript()
				, createTriggerQuery
		));
	}

	public void testMssqlCreateTrigger10( )
	{
		TCreateTriggerStmt createTrigger = new TCreateTriggerStmt( EDbVendor.dbvmssql );
		createTrigger.setTriggerName(SQLServerParser.parseObjectName("tr1"));

		TSimpleDmlTriggerClause dmlTriggerClause = new TSimpleDmlTriggerClause();
		dmlTriggerClause.setActionTime(ETriggerActionTime.tatFor);

		TDmlEventClause dmlEventClause = new TDmlEventClause();
		dmlEventClause.setTableName(SQLServerParser.parseObjectName("Customers"));
		ArrayList<TTriggerEventItem> dmlEventItems = new ArrayList<TTriggerEventItem>();
		TDmlEventItem dmlEventItem2 = new TDmlEventItem();
		dmlEventItem2.setDmlType(ESqlStatementType.sstupdate);
		dmlEventItems.add(dmlEventItem2);
		dmlEventClause.setEventItems(dmlEventItems);

		dmlTriggerClause.setEventClause(dmlEventClause);
		createTrigger.setTriggeringClause(dmlTriggerClause);


		TMssqlIfElse ifStmt = new TMssqlIfElse( EDbVendor.dbvmssql );
		ifStmt.setCondition(SQLServerParser.parseExpression("( (SUBSTRING(COLUMNS_UPDATED(),1,1)=power(2,(3 - 1))\r\n"
				+ "   + power(2,(5 - 1)))\r\n"
				+ "   AND (SUBSTRING(COLUMNS_UPDATED(),2,1)=power(2,(1 - 1)))\r\n   )"));

		TMssqlPrint print = new TMssqlPrint( EDbVendor.dbvmssql );
		TExpressionList expressionList = new TExpressionList( );
		expressionList.addExpression( SQLServerParser.parseExpression("'Columns 3, 5 and 9 updated'") );
		print.setMessages( expressionList );
		ifStmt.setStmt( print );

		createTrigger.getBodyStatements( ).add( ifStmt );

		String createTriggerQuery = "CREATE TRIGGER tr1 ON Customers\r\n"
				+ "FOR UPDATE\r\nAS\r\n"
				+ "   IF ( (SUBSTRING(COLUMNS_UPDATED(),1,1)=power(2,(3 - 1))\r\n"
				+ "      + power(2,(5 - 1)))\r\n"
				+ "      AND (SUBSTRING(COLUMNS_UPDATED(),2,1)=power(2,(1 - 1)))\r\n"
				+ "      )\r\n"
				+ "   PRINT 'Columns 3, 5 and 9 updated'";

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql
				, createTrigger.toScript()
				, createTriggerQuery
		));
	}

	public void testCreateView( )
	{
		TCreateViewSqlStatement createView = new TCreateViewSqlStatement( EDbVendor.dbvoracle );

		createView.setViewName(SQLServerParser.parseObjectName("vNessusTargetHostExtract"));

		String subQuery = "SELECT     LoadKey, vcHost, CASE WHEN iPluginid = 12053 THEN SUBSTRING(vcResult,CHARINDEX('resolves as',vcResult) + 12,(DATALENGTH(vcResult) - 1)\n"
				+ "                      - (CHARINDEX('resolves as',vcResult) + 12)) ELSE 'No registered hostname' END AS vcHostName, vcport, LoadedOn, iRecordTypeID,\n"
				+ "                      iAgentProcessID, iTableID\n"
				+ "FROM         dbo.vNessusResultExtract";
		createView.setSubquery( OracleParser.parseSubquery(subQuery) );

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
				,createView.toScript()
				,"CREATE VIEW vNessusTargetHostExtract\nAS \n"
						+ subQuery
		));

	}

	public void testQualifiedNameWithServer(){
		String query = "select * FROM Server2.CompanyData.dbo.Customers_66";
		SQLServerParser.sqltext = query;
		SQLServerParser.parse();
		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
				, SQLServerParser.sqlstatements.get(0).toScript()
				, SQLServerParser.sqlstatements.get(0).toScript().toString()
		));
	}

	public void testCreateView2( )
	{
		TCreateViewSqlStatement createView = new TCreateViewSqlStatement( EDbVendor.dbvmssql );

		createView.setViewName(SQLServerParser.parseObjectName("Customers"));

		String subQuery = "	SELECT *\r\n"
				+ "	FROM CompanyData.dbo.Customers_33\r\n"
				+ "	UNION ALL\r\n"
				+ "	SELECT *\r\n"
				+ "	FROM Server2.CompanyData.dbo.Customers_66\r\n"
				+ "	UNION ALL\r\n"
				+ "	SELECT *\r\n"
				+ "	FROM Server3.CompanyData.dbo.Customers_99";
		createView.setSubquery( SQLServerParser.parseSubquery(subQuery) );
		//System.out.print(createView.toScript());

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql
				, createView.toScript()
				, "CREATE VIEW Customers\nAS \n"
						+ subQuery
		));

	}

	public void testCreateIndex( )
	{
		TCreateIndexSqlStatement createIndex = new TCreateIndexSqlStatement( EDbVendor.dbvmssql );

		createIndex.setIndexName(SQLServerParser.parseObjectName("IX_TransactionHistory_ReferenceOrderID"));
		createIndex.setNonClustered( true );
		createIndex.setTableName(SQLServerParser.parseObjectName("Production.TransactionHistory"));
		TOrderByItemList items = new TOrderByItemList( );
		TOrderByItem item = new TOrderByItem( );
		item.setSortKey( new TExpression(SQLServerParser.parseObjectName("ReferenceOrderID") ));
		items.addOrderByItem( item );
		createIndex.setColumnNameList(items);

		createIndex.setFilegroupOrPartitionSchemeName( SQLServerParser.parseObjectName("TransactionsPS1") );
		createIndex.setPartitionSchemeColumns(new TObjectNameList());
		createIndex.getPartitionSchemeColumns( )
				.addObjectName(SQLServerParser.parseObjectName("TransactionDate"));

		String createIndexQuery = "CREATE NONCLUSTERED INDEX IX_TransactionHistory_ReferenceOrderID\r\n"
				+ "ON Production.TransactionHistory (ReferenceOrderID)\r\nON TransactionsPS1 (TransactionDate)";

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql
				,createIndex.toScript()
				,createIndexQuery
		));
	}

	public void testCreateUniqueIndex( )
	{
		TCreateIndexSqlStatement createIndex = new TCreateIndexSqlStatement( EDbVendor.dbvmssql );

		createIndex.setIndexName(SQLServerParser.parseObjectName("AK_UnitMeasure_Name"));
		createIndex.setIndexType( EIndexType.itUnique );
//		createIndex.getCreateIndexNode( )
//				.setTableName( sqlParser.parseObjectName( "Production.UnitMeasure" ) );
		createIndex.setTableName(SQLServerParser.parseObjectName("Production.UnitMeasure"));
		TOrderByItemList items = new TOrderByItemList( );
		TOrderByItem item = new TOrderByItem( );
		item.setSortKey( new TExpression(SQLServerParser.parseObjectName("Name") ));
		items.addOrderByItem( item );
//		createIndex.getCreateIndexNode( ).setColumnNameList( items );
		createIndex.setColumnNameList(items);

		String createIndexQuery = "CREATE UNIQUE INDEX AK_UnitMeasure_Name\n"
				+ "    ON Production.UnitMeasure (Name)";
//		System.out.print(scriptGenerator.generateScript( createIndex, true ));

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql
				,createIndex.toScript()
				,createIndexQuery
		));

	}

	public void testCreateViewWithAlias( )
	{
		TCreateViewSqlStatement createView = new TCreateViewSqlStatement( EDbVendor.dbvoracle );

		createView.setViewName(OracleParser.parseObjectName("test1"));
		createView.setStReplace( new TSourceToken( "replace" ) );

		TViewAliasItemList itemList = new TViewAliasItemList( );
		TViewAliasItem item = new TViewAliasItem( );
		item.setAlias( OracleParser.parseObjectName("account_name_alias") );
		itemList.addViewAliasItem(item);
		TViewAliasItem item1 = new TViewAliasItem( );
		item1.setAlias(OracleParser.parseObjectName("account_number_alias"));
		itemList.addViewAliasItem( item1 );

		TViewAliasClause aliasClause = new TViewAliasClause( );
		aliasClause.setViewAliasItemList( itemList );

		createView.setViewAliasClause( aliasClause );;

		String subQuery = "select account_name, account_number from \n"
				+ "AP10_BANK_ACCOUNTS t";
		createView.setSubquery( OracleParser.parseSubquery(subQuery) );

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql
				,createView.toScript()
				,"CREATE OR REPLACE VIEW test1(account_name_alias, account_number_alias)\nAS \n"
						+ subQuery
		));

	}

//	public void testMssqlCreateFunction( )
//	{
//		TMssqlCreateFunction createFunction = new TMssqlCreateFunction( EDbVendor.dbvmssql );
//		TScriptGenerator scriptGenerator = new TScriptGenerator( EDbVendor.dbvmssql );
//		createFunction.setFunctionName( sqlParser.parseObjectName( "dbo.ufnGetStock" ) );
//		TParameterDeclarationList params = new TParameterDeclarationList( );
//		TParameterDeclaration param = new TParameterDeclaration( );
//		param.setParameterName( sqlParser.parseObjectName( "@ProductID" ) );
//		TTypeName dataType = new TTypeName( );
//		dataType.setDataType( EDataType.int_t );
//		param.setDataType( dataType );
//		params.addParameterDeclarationItem( param );
//		createFunction.setParameterDeclarations( params );
//
//		TTypeName returnDataType = new TTypeName( );
//		returnDataType.setDataType( EDataType.int_t );
//		createFunction.setReturnDataType( returnDataType );
//
//		TMssqlBlock block = new TMssqlBlock( EDbVendor.dbvmssql );
//
//		TMssqlDeclare declare = new TMssqlDeclare( EDbVendor.dbvmssql );
//		TDeclareVariableList vars = new TDeclareVariableList( );
//		TDeclareVariable var = new TDeclareVariable( );
//		var.setVariableName( sqlParser.parseObjectName( "@ret" ) );
//		TTypeName datatype = new TTypeName( );
//		datatype.setDataType( EDataType.int_t );
//		var.setDatatype( datatype );
//		vars.addDeclareVariable( var );
//		declare.setVariables( vars );
//
//		block.getBodyStatements( ).add( declare );
//
//		String selectQuery = "SELECT @ret = SUM(p.Quantity)\r\n"
//				+ "    FROM Production.ProductInventory p\r\n"
//				+ "    WHERE p.ProductID = @ProductID\r\n"
//				+ "        AND p.LocationID = '6'";
//		block.getBodyStatements( )
//				.add( scriptGenerator.createSubquery( selectQuery ) );
//
//		TMssqlIfElse ifElse = new TMssqlIfElse( EDbVendor.dbvmssql );
//		ifElse.setCondition( sqlParser.parseExpression( "@ret IS NULL" ) );
//		TMssqlSet setStmt = new TMssqlSet( EDbVendor.dbvmssql );
//		setStmt.setVarName( sqlParser.parseObjectName( "@ret" ) );
//		setStmt.setVarExpr( sqlParser.parseExpression( "0" ) );
//		ifElse.setStmt( setStmt );
//
//		block.getBodyStatements( ).add( ifElse );
//
//		TMssqlReturn returnStmt = new TMssqlReturn( EDbVendor.dbvmssql );
//		returnStmt.setReturnExpr( sqlParser.parseExpression( "@ret" ) );
//		block.getBodyStatements( ).add( returnStmt );
//
//		createFunction.getBodyStatements( ).add( block );
//
//		String createFunctionQuery = "CREATE FUNCTION dbo.ufnGetStock(@ProductID int)\r\n"
//				+ "RETURNS int\r\n"
//				+ "AS\r\n"
//				+ "BEGIN\r\n"
//				+ "    DECLARE @ret int ; \r\n"
//				+ "    SELECT @ret = SUM(p.Quantity)\r\n"
//				+ "    FROM Production.ProductInventory p\r\n"
//				+ "    WHERE p.ProductID = @ProductID\r\n"
//				+ "        AND p.LocationID = '6'; \r\n"
//				+ "     IF @ret IS NULL \r\n"
//				+ "        SET @ret=0; \r\n"
//				+ "    RETURN @ret; \r\n"
//				+ "END";
//		assertTrue( scriptGenerator.generateScript( createFunction, true )
//				.trim( )
//				.equalsIgnoreCase( formatSql( createFunctionQuery,
//						EDbVendor.dbvmssql ).trim( ) ) );
//	}

	public void testOracleCreateProcedure( )
	{
		TPlsqlCreateProcedure createProcedure = new TPlsqlCreateProcedure( EDbVendor.dbvoracle );

		createProcedure.setProcedureName(OracleParser.parseObjectName("evaluate"));

		TParameterDeclarationList params = new TParameterDeclarationList( );
		TParameterDeclaration param = new TParameterDeclaration( );
		param.setParameterName( OracleParser.parseObjectName("my_empno") );
		TTypeName dataType = new TTypeName( );
		dataType.setDataType( EDataType.number_t );
		param.setDataType( dataType );
		params.addParameterDeclarationItem(param);
		createProcedure.setParameterDeclarations(params);

		TInvokerRightsClause invoke = new TInvokerRightsClause( );
		invoke.setDefiner(OracleParser.parseObjectName("current_user"));
		createProcedure.setInvokerRightsClause( invoke );

		TVarDeclStmt variable = new TVarDeclStmt( EDbVendor.dbvoracle );
		variable.setElementName( OracleParser.parseObjectName("my_ename") );
		TTypeName datatype = new TTypeName( );
		datatype.setDataType( EDataType.varchar2_t );
		TConstant c = new TConstant( ELiteralType.integer_et );
		c.setStringValue( "15" );
		datatype.setLength( c );
		variable.setDataType( datatype );
		createProcedure.getDeclareStatements( ).add( variable );

		String selectQuery = "SELECT ename INTO my_ename FROM emp WHERE empno = my_empno;";
		createProcedure.getBodyStatements( )
				.add( OracleParser.parseSubquery(selectQuery) );

		String createProcedureQuery = "CREATE PROCEDURE evaluate(my_empno NUMBER) \r\n"
				+ "AUTHID CURRENT_USER AS \r\n"
				+ "my_ename VARCHAR2 (15); \r\n"
				+ "BEGIN \r\n"
				+ "SELECT ename INTO my_ename FROM emp WHERE empno = my_empno;\r\n"
				+ "END ;";

		//System.out.println(createProcedure.toScript());
		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
				,createProcedure.toScript()
				,createProcedureQuery
		));

	}

	public void testDropIndex( )
	{
		TDropIndexSqlStatement dropIndex = new TDropIndexSqlStatement( EDbVendor.dbvmssql );


		TDropIndexItemList itemList = new TDropIndexItemList( );
		TDropIndexItem item = new TDropIndexItem( );
		item.setIndexName(SQLServerParser.parseObjectName("IX_SalesPerson_SalesQuota_SalesYTD"));
		item.setObjectName( SQLServerParser.parseObjectName("Sales.SalesPerson") );
		itemList.addDropIndexItem( item );
		dropIndex.setDropIndexItemList( itemList );

		String dropIndexQuery = "DROP INDEX IX_SalesPerson_SalesQuota_SalesYTD ON Sales.SalesPerson";
		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql
				,dropIndex.toScript()
				,dropIndexQuery
		));


	}

	public void testUseDatabase( )
	{
		TUseDatabase useDatabase = new TUseDatabase( EDbVendor.dbvmssql );

		useDatabase.setDatabaseName(SQLServerParser.parseObjectName("AdventureWorks"));

		String useDatabaseQuery = "USE AdventureWorks";
		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql
				,useDatabase.toScript()
				,useDatabaseQuery
		));

	}


	public void testOracleIfStmt( )
	{
		TIfStmt ifStmt = new TIfStmt( EDbVendor.dbvoracle );
		TExpression left = new TExpression(OracleParser.parseObjectName("ILevel"));
		TExpression lowExpr = new TExpression(OracleParser.parseConstant("'Low Income'"));
		TExpression avgExpr = new TExpression(OracleParser.parseConstant("'Avg Income'"));
		TExpression highExpr = new TExpression(OracleParser.parseConstant("'High Income'"));

		ifStmt.setCondition(OracleParser.parseExpression("monthly_value <= 4000"));
		ifStmt.getThenStatements( )
				.add( new TAssignStmt(left,lowExpr) );

		TElsifStmt elsIf = new TElsifStmt( );
		elsIf.setCondition( OracleParser.parseExpression("monthly_value > 4000  and  monthly_value <= 7000") );
		elsIf.getThenStatements( )
				.add( new TAssignStmt(left,avgExpr) );
		ifStmt.getElseifStatements( ).add( elsIf );
		ifStmt.getElseStatements( )
				.add( new TAssignStmt(left,highExpr) );

		String ifQuery = "IF monthly_value <= 4000 THEN \r\n"
				+ "    ILevel = 'Low Income'\r\n"
				+ " ELSIF monthly_value > 4000  and  monthly_value <= 7000 THEN \r\n"
				+ "    ILevel = 'Avg Income'\r\n"
				+ " ELSE ILevel = 'High Income'\r\n"
				+ " END  IF";

		//System.out.println(ifStmt.toScript());
		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
				, ifStmt.toScript()
				, ifQuery
		));

	}

//	public void testMssqlIfStmt( )
//	{
//		TMssqlIfElse ifStmt = new TMssqlIfElse( EDbVendor.dbvmssql );
//		TScriptGenerator scriptGenerator = new TScriptGenerator( EDbVendor.dbvmssql );
//		ifStmt.setCondition( sqlParser.parseExpression( "EXISTS (SELECT name FROM sys.indexes\r\n"
//				+ "            WHERE name = N'IX_Address_PostalCode')" ) );
//
//		TDropIndexSqlStatement ifDrop = new TDropIndexSqlStatement( EDbVendor.dbvmssql );
//		ifDrop.setIndexName( sqlParser.parseObjectName( "IX_Address_PostalCode" ) );
//		TDropIndexItemList items = new TDropIndexItemList( );
//		TDropIndexItem item = new TDropIndexItem( );
//		item.setObjectName( sqlParser.parseObjectName( "Person.Address" ) );
//		items.addDropIndexItem( item );
//		ifDrop.setDropIndexItemList( items );
//
//		ifStmt.setStmt( ifDrop );
//
//		TDropIndexSqlStatement elseDrop = new TDropIndexSqlStatement( EDbVendor.dbvmssql );
//		elseDrop.setIndexName( sqlParser.parseObjectName( "IX_Address_PostalCode" ) );
//		TDropIndexItemList items1 = new TDropIndexItemList( );
//		TDropIndexItem item1 = new TDropIndexItem( );
//		item1.setObjectName( sqlParser.parseObjectName( "Person.NAME" ) );
//		items1.addDropIndexItem( item1 );
//		elseDrop.setDropIndexItemList( items1 );
//
//		ifStmt.setElseStmt( elseDrop );
//
//		String ifQuery = "IF EXISTS ( SELECT name FROM sys.indexes\r\n"
//				+ "            WHERE name = N'IX_Address_PostalCode')\r\n"
//				+ "    DROP INDEX IX_Address_PostalCode ON Person.Address\r\n"
//				+ "ELSE DROP INDEX IX_Address_PostalCode ON Person.NAME";
//		assertTrue( scriptGenerator.generateScript( ifStmt, true )
//				.trim( )
//				.toLowerCase( )
//				.equals( formatSql( ifQuery.trim( ), EDbVendor.dbvmssql ).toLowerCase( ) ) );
//	}

	String formatSql( String inputQuery, EDbVendor dbVendor )
	{
		String Result = inputQuery;
		TGSqlParser sqlparser = new TGSqlParser( dbVendor );
		sqlparser.sqltext = inputQuery;
		int ret = sqlparser.parse( );
		if ( ret == 0 )
		{
			GFmtOpt option = GFmtOptFactory.newInstance( );
			option.caseFuncname = TCaseOption.CoNoChange;
			Result = FormatterFactory.pp( sqlparser, option );
		}
		return Result;
	}

	public void testInsertSubquery( )
	{
		TInsertSqlStatement insert = new TInsertSqlStatement( EDbVendor.dbvoracle );

		TTable table = new TTable( );
		table.setTableName(OracleParser.parseObjectName("table1"));
		insert.setTargetTable( table );

		TObjectNameList columnNameList = new TObjectNameList( );
		insert.setColumnList( columnNameList );
		columnNameList.addObjectName(OracleParser.parseObjectName("column1"));
		columnNameList.addObjectName(OracleParser.parseObjectName("column2"));

		insert.setSubQuery( OracleParser.parseSubquery("select c1,c1 from table2") );

		// System.out.println(scriptGenerator.generateScript(insert, true));

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql
				,insert.toScript()
				,"INSERT INTO table1\n"
						+ "            (column1,\n"
						+ "             column2)\n"
						+ "SELECT c1,\n"
						+ "       c1\n"
						+ "FROM   table2"
		));


	}

	public void testInsert( )
	{
		TInsertSqlStatement insert = new TInsertSqlStatement( EDbVendor.dbvoracle );

		TTable table = new TTable( );
		table.setTableName(OracleParser.parseObjectName("table1"));
		insert.setTargetTable( table );

		TObjectNameList columnNameList = new TObjectNameList( );
		insert.setColumnList( columnNameList );
		columnNameList.addObjectName(OracleParser.parseObjectName("column1"));
		columnNameList.addObjectName(OracleParser.parseObjectName("column2"));

		TMultiTargetList values = new TMultiTargetList( );
		insert.setValues(values);
		TMultiTarget multiTarget = new TMultiTarget( );
		values.addMultiTarget( multiTarget );

		TResultColumnList resultColumnList = new TResultColumnList( );
		multiTarget.setColumnList( resultColumnList );

		TResultColumn resultColumn1 = new TResultColumn( );
		resultColumnList.addResultColumn( resultColumn1 );
		resultColumn1.setExpr(OracleParser.parseExpression("1"));

		TResultColumn resultColumn2 = new TResultColumn( );
		resultColumnList.addResultColumn( resultColumn2 );
		resultColumn2.setExpr(OracleParser.parseExpression("2"));

		// System.out.println(scriptGenerator.generateScript(insert, true));
		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
				,insert.toScript()
				,"INSERT INTO table1\n"
						+ "            (column1,\n"
						+ "             column2)\n"
						+ "VALUES      (1,\n"
						+ "             2)"
		));

	}

	public void testUpdateMultiTable( )
	{
		TUpdateSqlStatement update = new TUpdateSqlStatement( EDbVendor.dbvmssql );

		TTable table = new TTable( );
		table.setTableName(SQLServerParser.parseObjectName("dbo.Table2"));
		update.setTargetTable( table );

		TResultColumnList resultColumnList = new TResultColumnList( );
		update.setResultColumnList( resultColumnList );

		TResultColumn resultColumn1 = new TResultColumn( );
		resultColumnList.addResultColumn( resultColumn1 );
		TExpression left = SQLServerParser.parseExpression("dbo.Table2.ColB");
		TExpression right = SQLServerParser.parseExpression("dbo.Table2.ColB + dbo.Table1.ColB");
		resultColumn1.setExpr(new TExpression (EExpressionType.assignment_t,
				left,
				right));

		TJoinList joinList = new TJoinList( );
		update.joins = joinList;
		TJoin join = new TJoin( );
		joinList.addJoin( join );
		TTable table1 = new TTable( );
		join.setTable( table1 );
		table1.setTableName(SQLServerParser.parseObjectName("dbo.Table2"));

		TJoinItem joinItem = new TJoinItem( );
		join.getJoinItems( ).addJoinItem(joinItem);
		joinItem.setJoinType(EJoinType.inner);
		TTable joinTable = new TTable( );
		joinItem.setTable( joinTable );

		joinTable.setTableName(SQLServerParser.parseObjectName("dbo.Table1"));
		joinItem.setOnCondition( SQLServerParser.parseExpression("(dbo.Table2.ColA = dbo.Table1.ColA)") );

		// System.out.println(scriptGenerator.generateScript(update, true));

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql
				,update.toScript()
				,"UPDATE dbo.table2\n"
						+ "SET    dbo.table2.colb=dbo.table2.colb + dbo.table1.colb FROM dbo.table2 INNER JOIN dbo.table1 ON (dbo.table2.cola = dbo.table1.cola)"
		));

	}

	public void testUpdate( )
	{
		TUpdateSqlStatement update = new TUpdateSqlStatement( EDbVendor.dbvoracle );

		TTable table = new TTable( );
		table.setTableName(OracleParser.parseObjectName("table1"));
		update.setTargetTable( table );

		TResultColumnList resultColumnList = new TResultColumnList( );
		update.setResultColumnList( resultColumnList );

		TResultColumn resultColumn1 = new TResultColumn( );
		resultColumnList.addResultColumn( resultColumn1 );
		TExpression left = OracleParser.parseExpression("column1");
		TExpression right = OracleParser.parseExpression("1");

		resultColumn1.setExpr( new TExpression (EExpressionType.assignment_t,
				left,
				right));

		TResultColumn resultColumn2 = new TResultColumn( );
		resultColumnList.addResultColumn(resultColumn2);
		TExpression left2 = OracleParser.parseExpression("column2");
		TExpression right2 = OracleParser.parseExpression("1");

		resultColumn2.setExpr( new TExpression( EExpressionType.assignment_t,
				left2,
				right2 ) );

		TWhereClause whereClause = new TWhereClause( );
		update.setWhereClause( whereClause );
		whereClause.setCondition( OracleParser.parseExpression("column3 > 250.00") );

		// System.out.println(scriptGenerator.generateScript(update, true));
		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
				,update.toScript()
				,"UPDATE table1\n"
						+ "SET    column1=1,\n"
						+ "       column2=1\n"
						+ "WHERE  column3 > 250.00"
		));

	}

	public void testDeleteMultiTable( )
	{
		TDeleteSqlStatement delete = new TDeleteSqlStatement( EDbVendor.dbvmssql );
		delete.setFromKeyword( true );

		TTable table = new TTable( );
		table.setTableName(SQLServerParser.parseObjectName("Sales.SalesPersonQuotaHistory "));
		delete.setTargetTable( table );

		TJoinList joinList = new TJoinList( );
		delete.joins = joinList;
		TJoin join = new TJoin( );
		joinList.addJoin( join );
		TTable table1 = new TTable( );
		join.setTable( table1 );
		table1.setTableName(SQLServerParser.parseObjectName("Sales.SalesPersonQuotaHistory"));

		TAliasClause aliasClause = new TAliasClause( );
		table1.setAliasClause( aliasClause );
		aliasClause.setHasAs( true );
		aliasClause.setAliasName(SQLServerParser.parseObjectName("spqh"));

		TJoinItem joinItem = new TJoinItem( );
		join.getJoinItems().addJoinItem(joinItem);
		joinItem.setJoinType( EJoinType.inner );
		TTable joinTable = new TTable( );
		joinItem.setTable(joinTable);
		TAliasClause aliasClause2 = new TAliasClause( );
		joinTable.setAliasClause( aliasClause2 );
		aliasClause2.setHasAs(true);
		aliasClause2.setAliasName( SQLServerParser.parseObjectName("sp") );

		joinTable.setTableName(SQLServerParser.parseObjectName("Sales.SalesPerson"));
		joinItem.setOnCondition( SQLServerParser.parseExpression("spqh.BusinessEntityID = sp.BusinessEntityID") );

		TWhereClause whereClause = new TWhereClause( );
		delete.setWhereClause( whereClause );
		whereClause.setCondition( SQLServerParser.parseExpression("sp.SalesYTD > 2500000.00") );

		// System.out.println(scriptGenerator.generateScript(delete, true));
		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql
				,delete.toScript()
				,"DELETE FROM sales.salespersonquotahistory FROM sales.salespersonquotahistory AS spqh INNER JOIN sales.salesperson AS sp ON spqh.businessentityid = sp.businessentityid\n"
						+ "WHERE       sp.salesytd > 2500000.00"
		));

	}

	public void testDelete( )
	{
		TDeleteSqlStatement delete = new TDeleteSqlStatement( EDbVendor.dbvoracle );
		delete.setFromKeyword( true );

		TTable table = new TTable( );
		table.setTableName(OracleParser.parseObjectName("table1"));
		delete.setTargetTable( table );

		TWhereClause whereClause = new TWhereClause( );
		delete.setWhereClause( whereClause );
		whereClause.setCondition(OracleParser.parseExpression("f1>0"));

		// System.out.println(scriptGenerator.generateScript(delete, true));
		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
				,delete.toScript()
				,"DELETE FROM table1\n"
						+ "WHERE       f1 > 0"
		));

	}

	public void testSelect( )
	{
		TSelectSqlStatement select = new TSelectSqlStatement( EDbVendor.dbvoracle );

		TResultColumnList resultColumnList = new TResultColumnList( );
		select.setResultColumnList( resultColumnList );
		TResultColumn resultColumn1 = new TResultColumn( );
		resultColumnList.addResultColumn( resultColumn1 );
		resultColumn1.setExpr( OracleParser.parseExpression("column1") );

		TResultColumn resultColumn2 = new TResultColumn( );
		resultColumnList.addResultColumn( resultColumn2 );
		resultColumn2.setExpr( OracleParser.parseExpression("column2") );
		TAliasClause aliasClause = new TAliasClause( );
		resultColumn2.setAliasClause( aliasClause );
		aliasClause.setHasAs( true );
		aliasClause.setAliasName( OracleParser.parseObjectName("c_alias") );
		// System.out.println( scriptGenerator.generateScript(select) );

		TJoinList joinList = new TJoinList( );
		select.joins = joinList;
		TJoin join = new TJoin( );
		joinList.addJoin( join );
		TTable table = new TTable( );
		join.setTable( table );
		// table.setTableType(ETableSource.objectname);
		table.setTableName( OracleParser.parseObjectName("table1") );

		TWhereClause whereClause = new TWhereClause( );
		select.setWhereClause( whereClause );
		whereClause.setCondition( OracleParser.parseExpression("f1>0") );

		TGroupBy groupBy = new TGroupBy( );
		select.setGroupByClause( groupBy );
		TGroupByItem groupByItem = new TGroupByItem( );
		groupBy.getItems( ).addGroupByItem( groupByItem );
		groupByItem.setExpr( OracleParser.parseExpression("column1") );
		groupBy.setHavingClause( OracleParser.parseExpression("sum(column2) > 10") );

		TOrderBy orderBy = new TOrderBy( );
		select.setOrderbyClause( orderBy );
		TOrderByItem orderByItem = new TOrderByItem( );
		orderBy.getItems( ).addElement( orderByItem );
		orderByItem.setSortKey( OracleParser.parseExpression("column1") );
		orderByItem.setSortOrder( ESortType.desc );

		TOrderByItem orderByItem2 = new TOrderByItem( );
		orderBy.getItems( ).addElement( orderByItem2 );
		orderByItem2.setSortKey( OracleParser.parseExpression("column3") );
		orderByItem2.setSortOrder( ESortType.asc );

		// System.out.println(scriptGenerator.generateScript(select, true));
		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
				,select.toScript()
				,"SELECT   column1,\n"
						+ "         column2 AS c_alias\n"
						+ "FROM     table1\n"
						+ "WHERE    f1 > 0\n"
						+ "GROUP BY column1\n"
						+ "HAVING  sum(column2) > 10\n"
						+ "ORDER BY column1 DESC,\n"
						+ "         column3 ASC"
		));

	}

	public void testSelectOracleJoin( )
	{
		TSelectSqlStatement select = new TSelectSqlStatement( EDbVendor.dbvoracle );

		TResultColumnList resultColumnList = new TResultColumnList( );
		select.setResultColumnList( resultColumnList );
		TResultColumn resultColumn1 = new TResultColumn( );
		resultColumnList.addResultColumn( resultColumn1 );
		resultColumn1.setExpr(OracleParser.parseExpression("column1"));

		TResultColumn resultColumn2 = new TResultColumn( );
		resultColumnList.addResultColumn( resultColumn2 );
		resultColumn2.setExpr(OracleParser.parseExpression("column2"));
		TAliasClause aliasClause = new TAliasClause( );
		resultColumn2.setAliasClause( aliasClause );
		aliasClause.setHasAs( true );
		aliasClause.setAliasName(OracleParser.parseObjectName("c_alias"));

		TJoinList joinList = new TJoinList( );
		select.joins = joinList;
		TJoin join = new TJoin( );
		joinList.addJoin( join );
		TTable table = new TTable( );
		join.setTable( table );
		table.setTableType(ETableSource.objectname);
		table.setTableName( OracleParser.parseObjectName("table1") );

		TJoin join2 = new TJoin( );
		joinList.addJoin(join2);
		TTable table2 = new TTable( );
		join2.setTable( table2 );
		// table2.setTableType(ETableSource.objectname);
		table2.setTableName(OracleParser.parseObjectName("table2"));

		TWhereClause whereClause = new TWhereClause( );
		select.setWhereClause( whereClause );
		whereClause.setCondition( OracleParser.parseExpression("table1.f1 = table2.f1 and table1.f2 = 0") );

		// System.out.println(scriptGenerator.generateScript(select, true));

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
				,select.toScript()
				,"SELECT column1,\n"
						+ "       column2 AS c_alias\n"
						+ "FROM   table1,\n"
						+ "       table2\n"
						+ "WHERE  table1.f1 = table2.f1\n"
						+ "       AND table1.f2 = 0"
		));


	}

	public void testSelectAnsiJoin( )
	{
		TSelectSqlStatement select = new TSelectSqlStatement( EDbVendor.dbvoracle );

		TResultColumnList resultColumnList = new TResultColumnList( );
		select.setResultColumnList( resultColumnList );
		TResultColumn resultColumn1 = new TResultColumn( );
		resultColumnList.addResultColumn( resultColumn1 );
		resultColumn1.setExpr( OracleParser.parseExpression("column1") );

		TResultColumn resultColumn2 = new TResultColumn( );
		resultColumnList.addResultColumn( resultColumn2 );
		resultColumn2.setExpr( OracleParser.parseExpression("column2") );
		TAliasClause aliasClause = new TAliasClause( );
		resultColumn2.setAliasClause( aliasClause );
		aliasClause.setHasAs( true );
		aliasClause.setAliasName( OracleParser.parseObjectName("c_alias") );

		TJoinList joinList = new TJoinList( );
		select.joins = joinList;
		TJoin join = new TJoin( );
		joinList.addJoin( join );
		TTable table = new TTable( );
		join.setTable( table );
		// table.setTableType(ETableSource.objectname);
		table.setTableName( OracleParser.parseObjectName("table1") );

		TJoinItem joinItem = new TJoinItem( );
		join.getJoinItems( ).addJoinItem( joinItem );
		joinItem.setJoinType( EJoinType.inner );
		TTable joinTable = new TTable( );
		joinItem.setTable( joinTable );
		joinTable.setTableName( OracleParser.parseObjectName("table2") );
		joinItem.setOnCondition( OracleParser.parseExpression("table1.f1 = table2.f1") );

		TJoinItem joinItem3 = new TJoinItem( );
		join.getJoinItems( ).addJoinItem( joinItem3 );
		joinItem3.setJoinType( EJoinType.leftouter );
		TTable joinTable3 = new TTable( );
		joinItem3.setTable( joinTable3 );
		joinTable3.setTableName( OracleParser.parseObjectName("table3") );
		joinItem3.setOnCondition( OracleParser.parseExpression("table3.f1 = table2.f1") );

		TJoinItem joinItem4 = new TJoinItem( );
		join.getJoinItems( ).addJoinItem( joinItem4 );
		joinItem4.setJoinType( EJoinType.rightouter );
		TTable joinTable4 = new TTable( );
		joinItem4.setTable( joinTable4 );
		joinTable4.setTableName( OracleParser.parseObjectName("table4") );
		joinItem4.setOnCondition( OracleParser.parseExpression("table4.f1 = table3.f1") );

		TWhereClause whereClause = new TWhereClause( );
		select.setWhereClause( whereClause );
		whereClause.setCondition( OracleParser.parseExpression("table1.f2 = 0") );

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle
				,select.toScript()
				,"SELECT column1,\n"
						+ "       column2 AS c_alias\n"
						+ "FROM   table1\n"
						+ "       INNER JOIN table2\n"
						+ "       ON table1.f1 = table2.f1\n"
						+ "       LEFT OUTER JOIN table3\n"
						+ "       ON table3.f1 = table2.f1\n"
						+ "       RIGHT OUTER JOIN table4\n"
						+ "       ON table4.f1 = table3.f1\n"
						+ "WHERE  table1.f2 = 0"
		));

		// System.out.println(scriptGenerator.generateScript(select, true));

	}

	public void testCreateOracleAssignStmt( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
		TAssignStmt assign = new TAssignStmt( );

		assign.setLeft(sqlParser.parseExpression("ILevel"));
		assign.setExpression( sqlParser.parseExpression("'Low Income'") );

		//System.out.println(assign.toScript());

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, "ILevel = 'Low Income'", assign.toScript()));

	}

	public void testCreateBinaryExpression( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);

		TExpression left = sqlParser.parseExpression("1");
		TExpression right = sqlParser.parseExpression("2");
		TExpression plus = new TExpression();
		plus.setExpressionType(EExpressionType.arithmetic_plus_t);
		plus.setLeftOperand(left);
		plus.setRightOperand(right);
		//System.out.println(plus.toScript());
		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, "1 + 2 ", plus.toScript()));
	}

	public void testCreateComparisonPredicate( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);

		TExpression left = sqlParser.parseExpression("salary");
		TExpression right = sqlParser.parseExpression("20");
		TExpression plus = new TExpression();
		plus.setExpressionType(EExpressionType.simple_comparison_t);
		plus.setComparisonType(EComparisonType.greaterThanOrEqualTo);
		plus.setLeftOperand(left);
		plus.setRightOperand(right);
		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, "salary >= 20", plus.toScript()));
	}

	public void testCreateAndPredicate( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);

		TExpression left = sqlParser.parseExpression("salary");
		TExpression right = sqlParser.parseExpression("20");

		TExpression left2 = sqlParser.parseExpression("location");
		TExpression right2 = sqlParser.parseExpression("'NY'");

		TExpression c1 = new TExpression();
		c1.setExpressionType(EExpressionType.simple_comparison_t);
		c1.setComparisonType(EComparisonType.greaterThanOrEqualTo);
		c1.setLeftOperand(left);
		c1.setRightOperand(right);

		TExpression c2 = new TExpression();
		c2.setExpressionType(EExpressionType.simple_comparison_t);
		c2.setComparisonType(EComparisonType.equals);
		c2.setLeftOperand(left2);
		c2.setRightOperand(right2);

		TExpression c3 = new TExpression();
		c3.setExpressionType(EExpressionType.logical_and_t);
		c3.setLeftOperand(c1);
		c3.setRightOperand(c2);
		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, "salary >= 20 and location = 'NY'", c3.toScript()));

	}

	public void testCreateSubqueryPredicate( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);

		TExpression left = sqlParser.parseExpression("salary");
		TExpression right = sqlParser.parseExpression("(select sal from emp where empno=1)");
		TExpression subqueryPredicate = new TExpression();
		subqueryPredicate.setExpressionType(EExpressionType.simple_comparison_t);
		subqueryPredicate.setComparisonType(EComparisonType.notLessThan);
		subqueryPredicate.setLeftOperand(left);
		subqueryPredicate.setRightOperand(right);

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, "salary !< (select sal from emp where empno=1)", subqueryPredicate.toScript()));

		subqueryPredicate.setExpressionType(EExpressionType.group_comparison_t);
		subqueryPredicate.setComparisonType(EComparisonType.greaterThanOrEqualTo);
		subqueryPredicate.setQuantifierType(EQuantifierType.all);
		//System.out.println(subqueryPredicate.toScript());

		assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, "salary >=  all (select sal from emp where empno=1)", subqueryPredicate.toScript()));


	}


}