
package test.scriptWriter;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETokenType;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceTokenList;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.para.styleenums.TCaseOption;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import junit.framework.TestCase;

public class testScriptGenerator extends TestCase
{


	public void testPostgresqlAnd( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvpostgresql );
		sqlparser.sqltext = "SELECT * from transmission where position_search && '{3456788}';";

		sqlparser.parse( );

		//System.out.println(sqlparser.sqlstatements.get(0).toScript());
		//assertTrue(verifyScript(EDbVendor.dbvmysql,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));

	}

	public void testMySQLFunctionAddDate( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvmysql );
		sqlparser.sqltext = "SELECT adddate(date, 10*10) as date from program";

		sqlparser.parse( );

		//System.out.println(sqlparser.sqlstatements.get(0).toScript());
		assertTrue(verifyScript(EDbVendor.dbvmysql,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));

	}

	public void testMySQLAssignmentOperator( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvmysql );
		sqlparser.sqltext = "select  @csum := 0  from  dual";

		sqlparser.parse( );

		//System.out.println(sqlparser.sqlstatements.get(0).toScript());
		assertTrue(verifyScript(EDbVendor.dbvmysql,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));

	}

	public void testBindVar( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select emp_id, emp_dept\n" +
				"into :b0 :b1,\n" +
				":b2 :b3\n" +
				"from T1\n" +
				"where rownum < 2;";

		sqlparser.parse( );

		//System.out.println(sqlparser.sqlstatements.get(0).toScript());
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));

//		TScriptGenerator scriptGenerator = new TScriptGenerator( EDbVendor.dbvoracle );
//		scriptGenerator.generateScript( sqlparser.sqlstatements.get( 0 ) );
//		assertTrue( scriptGenerator.verifyScript( sqlparser.sqlstatements.get( 0 ) ) );
	}

	public void testCrossApply( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "SELECT d.department_name, v.employee_id, v.last_name\n"
				+ "  FROM departments d CROSS APPLY (SELECT * FROM employees e\n"
				+ "                                  WHERE e.department_id = d.department_id) v";

		sqlparser.parse( );

		//System.out.println(sqlparser.sqlstatements.get(0).toScript());
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));

//		TScriptGenerator scriptGenerator = new TScriptGenerator( EDbVendor.dbvoracle );
//		scriptGenerator.generateScript( sqlparser.sqlstatements.get( 0 ) );
//		assertTrue( scriptGenerator.verifyScript( sqlparser.sqlstatements.get( 0 ) ) );
	}


	public void testIsOfType( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select a from b\n"
				+ "where c is of type(only scott.tn)";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testForeignReferences( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "CREATE TABLE registered_students (\n"
				+ "  student_id NUMBER(5) NOT NULL,\n"
				+ "  department CHAR(3)   NOT NULL,\n"
				+ "  course     NUMBER(3) NOT NULL,\n"
				+ "  grade      CHAR(1),\n"
				+ "  CONSTRAINT rs_grade\n"
				+ "    CHECK (grade IN ('A', 'B', 'C', 'D', 'E')),\n"
				+ "  CONSTRAINT rs_student_id\n"
				+ "    FOREIGN KEY (student_id) REFERENCES students (id),\n"
				+ "  CONSTRAINT rs_department_course\n"
				+ "    FOREIGN KEY (department, course)\n"
				+ "    REFERENCES classes (department, course)\n"
				+ "  )";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle
				, sqlparser.sqlstatements.get(0).toString()
				, sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testForUpdateOf( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select all department_id\n"
				+ "from employees\n"
				+ "for update of scott.employees.ename;";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testDatabaseLink( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select emp.e@usa b from emp";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testDatabaseLink2( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select user@!, sysdate@! from dual ;";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testAnalyticFunction3( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "SELECT last_name, salary, department_id,\n"
				+ "   PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY salary1 DESC) \n"
				+ "      OVER (PARTITION BY department_id) \"Percentile_Cont\",\n"
				+ "   PERCENT_RANK() \n"
				+ "      OVER (PARTITION BY department_id ORDER BY salary DESC) \n"
				+ "\"Percent_Rank\"\n"
				+ "FROM employees WHERE department_id IN (30, 60);";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testAnalyticFunction4( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "SELECT empno,\n"
				+ "       deptno,\n"
				+ "       sal,\n"
				+ "       MIN(sal) KEEP (DENSE_RANK FIRST ORDER BY sal) OVER (PARTITION BY deptno) \"Lowest\",\n"
				+ "       MAX(sal) KEEP (DENSE_RANK LAST ORDER BY sal) OVER (PARTITION BY deptno) \"Highest\"\n"
				+ "FROM   emp\n"
				+ "ORDER BY deptno, sal;";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testAnalyticFunction5( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "SELECT empno,\n"
				+ "       deptno,\n"
				+ "       sal,\n"
				+ "       DENSE_RANK() OVER (PARTITION BY deptno ORDER BY sal) \"rank\"\n"
				+ "FROM   emp;";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testAnalyticFunction6( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "SELECT empno,\n"
				+ "              deptno,\n"
				+ "              sal,\n"
				+ "              RANK() OVER (PARTITION BY deptno ORDER BY sal) \"rank\"\n"
				+ "       FROM   emp;";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testAnalyticFunction7( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "SELECT empno, deptno, TO_CHAR(hiredate, 'YYYY') YEAR,\n"
				+ "COUNT(*) OVER (PARTITION BY TO_CHAR(hiredate, 'YYYY')\n"
				+ "ORDER BY hiredate ROWS BETWEEN 3 PRECEDING AND 1 FOLLOWING) FROM_P3_TO_F1,\n"
				+ "COUNT(*) OVER (PARTITION BY TO_CHAR(hiredate, 'YYYY')\n"
				+ "ORDER BY hiredate ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) FROM_PU_TO_C,\n"
				+ "COUNT(*) OVER (PARTITION BY TO_CHAR(hiredate, 'YYYY')\n"
				+ "ORDER BY hiredate ROWS BETWEEN 3 PRECEDING AND 1 PRECEDING) FROM_P2_TO_P1,\n"
				+ "COUNT(*) OVER (PARTITION BY TO_CHAR(hiredate, 'YYYY')\n"
				+ "ORDER BY hiredate ROWS BETWEEN 1 FOLLOWING AND 3 FOLLOWING) FROM_F1_TO_F3\n"
				+ "FROM emp\n"
				+ "ORDER BY hiredate;";

		sqlparser.parse( );
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testGroupBy1( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "SELECT DECODE(GROUPING(department_name), 1, 'All Departments',\n"
				+ "      department_name) AS department_name,\n"
				+ "   DECODE(GROUPING(job_id), 1, 'All Jobs', job_id) AS job_id,\n"
				+ "   COUNT(*) \"Total Empl\", AVG(salary) * 12 \"Average Sal\"\n"
				+ "   FROM employees e, departments d\n"
				+ "   WHERE d.department_id = e.department_id\n"
				+ "   GROUP BY CUBE (department_name, job_id)";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testKeepDenseRank( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "SELECT department_id,\n"
				+ "MIN(salary) KEEP (DENSE_RANK FIRST ORDER BY commission_pct) \"Worst\",\n"
				+ "MAX(salary) KEEP (DENSE_RANK LAST ORDER BY commission_pct) \"Best\"\n"
				+ "   FROM employees\n"
				+ "   GROUP BY department_id;";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testDeleteNestedTable( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "DELETE TABLE(SELECT h.people FROM hr_info h\n"
				+ "   WHERE h.department_id = 280) p\n"
				+ "   WHERE p.salary > 1700;";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void test11( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "SELECT salary FROM employees\n"
				+ "versions between scn minvalue and maxvalue\n"
				+ "ORDER BY 1,2;";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testOracleJoin2( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select a from b \n"
				+ "where waehrungscode_iso        = TO_NUMBER(e.code(+))";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testAnalyticFunction2( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "SELECT manager_id, last_name, hire_date, salary,\n"
				+ "   AVG(salary) OVER (PARTITION BY manager_id ORDER BY hire_date \n"
				+ "   ROWS BETWEEN 1 PRECEDING AND 1 FOLLOWING) AS c_mavg\n"
				+ "   FROM employees;";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	// public void testPivot(){
	// TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
	// sqlparser.sqltext = "SELECT * FROM\n" +
	// "(SELECT EXTRACT(YEAR FROM order_date) year, order_mode, order_total FROM orders)\n"
	// +
	// "PIVOT\n" +
	// "(SUM(order_total) FOR order_mode IN ('direct' AS Store, 'online' AS Internet));";
	//
	// sqlparser.parse();
	// TScriptGenerator scriptGenerator = new
	// TScriptGenerator(EDbVendor.dbvoracle);
	// System.out.println(scriptGenerator.generateScript(sqlparser.sqlstatements.get(0)));
	// // scriptGenerator.generateScript(sqlparser.sqlstatements.get(0));
	// assertTrue(scriptGenerator.verifyScript(sqlparser.sqlstatements.get(0).sourcetokenlist));
	// }

	public void testCreateTableDefault2( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "create table myTable (\n"
				+ "myColumn number  default null  null \n"
				+ ");";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testCreateTableDefault( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "create table myTable (\n"
				+ "myColumn number  default null not null\n"
				+ ");";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testCreateViewDefault( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "CREATE VIEW vNessusTargetHostExtract \n"
				+ "AS \n"
				+ "SELECT     LoadKey, vcHost, CASE WHEN iPluginid = 12053 THEN SUBSTRING(vcResult, CHARINDEX('resolves as', vcResult) + 12, (DATALENGTH(vcResult) - 1) \n"
				+ "                      - (CHARINDEX('resolves as', vcResult) + 12)) ELSE 'No registered hostname' END AS vcHostName, vcport, LoadedOn, iRecordTypeID, \n"
				+ "                      iAgentProcessID, iTableID \n"
				+ "FROM         dbo.vNessusResultExtract;";
		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}


	//	public void testMssqlCreateFunction( )
//	{
//		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvmssql );
//		sqlparser.sqltext = "create  function dbo.ufnGetStock(@ProductID int )\r\n"
//				+ "     returns  int \r\n"
//				+ " as \r\n"
//				+ " begin \r\n"
//				+ " declare @ret int ;\r\n"
//				+ " select \r\n"
//				+ "@ret=SUM(p.Quantity)\r\n"
//				+ " from \r\n"
//				+ "Production.ProductInventory p\r\n"
//				+ " where p.ProductID = @ProductID  and  p.LocationID = '6';\r\n"
//				+ " if (@ret is  null )\r\n"
//				+ "     set @ret=0;\r\n"
//				+ " return @ret;\r\n"
//				+ " end ";
//		sqlparser.parse( );
//		TScriptGenerator scriptGenerator = new TScriptGenerator( EDbVendor.dbvmssql );
//		System.out.println(scriptGenerator.generateScript( sqlparser.sqlstatements.get( 0 ) ));
//		assertTrue( scriptGenerator.verifyScript( sqlparser.sqlstatements.get( 0 ).sourcetokenlist ) );
//	}
//
	public void testOracleCreateProcedure( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "CREATE PROCEDURE evaluate(my_empno NUMBER) \r\n"
				+ "AUTHID CURRENT_USER AS \r\n"
				+ "my_ename VARCHAR2 (15); \r\n"
				+ "BEGIN \r\n"
				+ "SELECT ename INTO my_ename FROM emp WHERE empno = my_empno;\r\n"
				+ "END ;";
		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testDropIndex( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvmssql );
		sqlparser.sqltext = "DROP INDEX IX_SalesPerson_SalesQuota_SalesYTD ON Sales.SalesPerson;";
		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvmssql,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testUseDatabase( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvmssql );
		sqlparser.sqltext = "USE AdventureWorks;";
		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvmssql,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testDelete( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "delete from department\n"
				+ "where department_name = 'Finance';";

		sqlparser.parse( );
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testJoinNested( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select a_join.f1\n"
				+ "from ((a as a_alias left outer join a1 on a1.f1 = a_alias.f1) ) as a_join\n"
				+ "join b on a_join.f1 = b.f1;";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testJoinNested2( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select * \n"
				+ "FROM (a AS alias_a \n"
				+ "   RIGHT JOIN ((b left outer join f on (b.f1=f.f2)) LEFT JOIN c \n"
				+ "\t\tON (b.b1 = c.c1) AND (b.b2 = c.c2)) \n"
				+ "\tON (a.a1 = b.b3) AND (a.a2 = b.b4)) b;";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	// public void testHavingGroup(){
	// TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
	// sqlparser.sqltext = "select a,c\n" +
	// "from b\n" +
	// "having avg(c) > 10\n" +
	// "group by a";
	//
	// sqlparser.parse();
	// TScriptGenerator scriptGenerator = new
	// TScriptGenerator(EDbVendor.dbvoracle);
	// System.out.println(scriptGenerator.generateScript(sqlparser.sqlstatements.get(0)));
	// // scriptGenerator.generateScript(sqlparser.sqlstatements.get(0));
	// assertTrue(scriptGenerator.verifyScript(sqlparser.sqlstatements.get(0).sourcetokenlist));
	// }

	public void testSelectAlias( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select last_name as name ,commission_pct comm,\n"
				+ "salary*12 \"Annual Salary\"\n"
				+ "from employees;";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testComment( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select a\n"
				+ "from b --s\n"
				+ "--ss\n"
				+ "where a in (1, 1>2 and c>d);";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testForUpdate( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select * from abc order by a for update nowait;";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testConcatenate( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "SELECT /*+ use_hash(KUO) */\n"
				+ "          C_BANK\n"
				+ "       || '|'\n"
				+ "from t  ";

		sqlparser.parse();
		//System.out.println(sqlparser.sqlstatements.get(0).toScript());
		assertTrue(verifyScript(EDbVendor.dbvoracle, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testInlist( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select ANZ_MA\n"
				+ "from t \n"
				+ "WHERE   funktionscode IN ('U', 'H') ";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testAnalyticFunction( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select ROW_NUMBER() OVER \n"
				+ "\t(PARTITION BY c_mandant, ma_parkey, me_parkey \n"
				+ "\t\tORDER BY c_mandant, ma_parkey, me_parkey)  ANZ_MA\n"
				+ "from t ";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testCase( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select CASE WHEN EXISTS (SELECT 1\n"
				+ "                           FROM CDS_H_GRUPPE  GRP1\n"
				+ "                          WHERE GRP1.c_mandant = c_mandant\n"
				+ "                            AND GRP1.hist_datum    = ADD_MONTHS(LAST_DAY(TRUNC(SYSDATE)), -1)\n"
				+ "                            AND GRP1.funktionscode = 'H'\n"
				+ "                            AND GRP1.parkey1       = ma_parkey)\n"
				+ "              THEN 1\n"
				+ "          ELSE NULL\n"
				+ "       END MA_ME\n"
				+ "from t";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testSelectPivot( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "SELECT * FROM pivot_table\n"
				+ "  UNPIVOT (yearly_total FOR order_mode IN (store AS 'direct', internet AS 'online'))\n"
				+ "  ORDER BY year, order_mode;";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testSelectWithParensOfUnion2( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "(( \n"
				+ "  select add_months(trunc(sysdate), -1) as dt\n"
				+ "  from   dual\n"
				+ "  union all\n"
				+ "  select cte.dt+1 \n"
				+ "  from   cte \n"
				+ "  where  cte.dt+1 < sysdate\n"
				+ ") order by 1)\n"
				+ "\n";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testSelectWithParensOfUnion( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "( \n"
				+ "  select add_months(trunc(sysdate), -1) as dt\n"
				+ "  from   dual\n"
				+ "  union all\n"
				+ "  select cte.dt+1 \n"
				+ "  from   cte \n"
				+ "  where  cte.dt+1 < sysdate\n"
				+ ") order by 1\n"
				+ "\n";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testSelectWithParens2( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "SELECT B.* FROM ((SELECT 2 FROM DUAL) B)";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testSelectWithParens( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "((select a from b\n"
				+ "where a>c)\n"
				+ "order by 1)";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testCTE( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "with cte (dt) as ( \n"
				+ "  select add_months(trunc(sysdate), -1) as dt\n"
				+ "  from   dual\n"
				+ "  union all\n"
				+ "  select cte.dt+1 \n"
				+ "  from   cte \n"
				+ "  where  cte.dt+1 < sysdate\n"
				+ ")\n"
				+ "  select * from cte;";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testSet2( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select 'sing' as \"My dream\", 3 a_dummy\n"
				+ "from dual\n"
				+ "union\n"
				+ "select 'I''d like to teach',1\n"
				+ "from dual\n"
				+ "union\n"
				+ "select 'the world to',2\n"
				+ "from dual\n"
				+ "order by 2;";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testSet1( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select employee_id,job_id\n"
				+ "from employees\n"
				+ "union\n"
				+ "select employee_id,job_id\n"
				+ "from job_history;";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testGroupBy( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select department_id,avg(salary)\n"
				+ "from employees\n"
				+ "group by department_id\n"
				+ "having avg(salary) > 8000\n"
				+ "order by sum(salary);";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testHierarchical( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "SELECT employee_id, last_name, manager_id\n"
				+ "   FROM employees\n"
				+ "   CONNECT BY PRIOR employee_id = manager_id;";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testJoin3( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select t1.f1\n"
				+ "from my.table1 t1\n"
				+ " right outer join (\n"
				+ " \t\t\t\t\t\t\t(my.table2 t2\n"
				+ " \t\t\t\t\t\t\t\tleft outer join my.table3 t3\n"
				+ " \t\t\t\t\t\t\t\t\ton (t2.f1 = t3.f2)\n"
				+ " \t\t\t\t\t\t\t)\n"
				+ " \t\t\t\t\t\tleft outer join (my.table4 t4\n"
				+ " \t\t\t\t\t\t\t\t\t\t\t\t\tfull outer join my.table5 t5\n"
				+ " \t\t\t\t\t\t\t\t\t\t\t\t\t\ton (t4.f1 = t5.f1)\n"
				+ " \t\t\t\t\t\t\t\t\t\t\t ) t4alias\n"
				+ " \t\t\t\t\t\t\ton (t4.b1 = t2.c1)\n"
				+ " \t\t\t\t\t\t)\n"
				+ " on (t1.a1 = t3.b3);";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testJoin2( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select t1.f1\n"
				+ "from my.table1 t1\n"
				+ " join (my.table2 t2\n"
				+ " left outer join my.table3 t3\n"
				+ " on t2.f1 = t3.f1) as joinalias1\n"
				+ " on t1.f1 = t2.f1;";

		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testJoin( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select t1.f1\n"
				+ "from my.table1 t1\n"
				+ " join my.table2 t2 on t1.f1 = t2.f1\n"
				+ " left outer join my.table3 t3 on t2.f1 = t3.f1";
		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void testOracleJoin( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "Select t1.f1\n"
				+ "from my.table1 t1,my.table2 t2\n"
				+ "where t1.f1 = t2.f1\t";
		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}

	public void test1( )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqltext = "select fx(1,2)+y from t";
		sqlparser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));
	}


	static public boolean verifyScript(EDbVendor dbVendor, String src, String target){
		return  verifyScript(dbVendor,src,target,0);
//		TGSqlParser sourceParser = new TGSqlParser(dbVendor );
//		TGSqlParser targetParser = new TGSqlParser(dbVendor );
//		sourceParser.sqltext = src;
//		sourceParser.tokenizeSqltext();
//
//		targetParser.sqltext = target;
//		targetParser.tokenizeSqltext();
//
//		return  verifyTokens(sourceParser.getSourcetokenlist(),targetParser.getSourcetokenlist(),false);

	}

	static public boolean verifyScript(EDbVendor dbVendor, String src, String target, long lineOffset){
		if ((src == null)||(src.length()==0)) return false;
		if ((target == null)||(target.length()==0)) return false;
		TGSqlParser sourceParser = new TGSqlParser(dbVendor );
		TGSqlParser targetParser = new TGSqlParser(dbVendor );
		sourceParser.sqltext = src;
		sourceParser.tokenizeSqltext();

		targetParser.sqltext = target;
		targetParser.tokenizeSqltext();

		return  verifyTokens(sourceParser.getSourcetokenlist(),targetParser.getSourcetokenlist(),false,lineOffset);

	}

	static private boolean verifyTokens(TSourceTokenList originalTokens, TSourceTokenList targetTokens, boolean partialChecking, long lineOffset){
		boolean result = true;
		int old = 0;
		boolean startParenthesis = false;
		int nestedParenthesis = 0;

		for(int i=0;i<targetTokens.size();i++) {
			if (targetTokens.get(i).tokentype == ETokenType.ttkeyword){
				// must a space after keyword
				if (i!=targetTokens.size()-1){
					if ((targetTokens.get(i+1).tokencode == TBaseType.lexnewline)
							|| (targetTokens.get(i+1).tokencode == TBaseType.lexspace)
							|| (targetTokens.get(i+1).tokencode < 127)
							) {
						continue;
					}else {
						System.out.print("lack space after keyword:"+targetTokens.get(i).toString());
						result = false;
						break;
					}
				}
			}

			if (targetTokens.get(i).tokentype == ETokenType.ttidentifier){
				// must a space between identifier and keyword/identifier
				if (i!=0){
					if ((targetTokens.get(i-1).tokentype == ETokenType.ttkeyword)
							|| (targetTokens.get(i-1).tokentype == ETokenType.ttidentifier)) {
						System.out.print("lack space between identifier and keyword:"+targetTokens.get(i).toString());
						result = false;
						break;
					}else {
						continue;
					}
				}
			}

		}

		if (! result) return  result;

		for(int i=0;i<originalTokens.size();i++){
			if ((originalTokens.get(i).tokencode == TBaseType.lexnewline)
					||(originalTokens.get(i).tokencode == TBaseType.lexspace)
					||(originalTokens.get(i).tokentype == ETokenType.ttsimplecomment)
					||(originalTokens.get(i).tokentype == ETokenType.ttbracketedcomment)
					||(originalTokens.get(i).tokentype == ETokenType.ttsemicolon)
					) {
				continue;
			}

			if (partialChecking){
				if (originalTokens.get(i).tokencode == '(') {
					startParenthesis = true;
					nestedParenthesis++;
				}else if (originalTokens.get(i).tokencode == ')'){
					if (nestedParenthesis > 0) nestedParenthesis--;
					if ((nestedParenthesis == 0) && startParenthesis){
						result = true;
						break;
					}
				}
			}

			result = false;
			for(int j=old;j<targetTokens.size();j++){
				if ((targetTokens.get(j).tokencode == TBaseType.lexnewline)
						||(targetTokens.get(j).tokencode == TBaseType.lexspace)
						||(targetTokens.get(j).tokentype == ETokenType.ttsimplecomment)
						||(targetTokens.get(j).tokentype == ETokenType.ttbracketedcomment)
						||(targetTokens.get(j).tokentype == ETokenType.ttsemicolon)
						) {
					continue;
				}

				if((originalTokens.get(i).tokencode == TBaseType.outer_join)&&(targetTokens.get(j).tokencode == TBaseType.outer_join)){
					result = true;
				}else{
					result = originalTokens.get(i).toString().equalsIgnoreCase(targetTokens.get(j).toString());
				}

				old = j +1;
				break;
			}

			if (! result) {
				long lineNo = originalTokens.get(i).lineNo+lineOffset - 1;
				System.out.print("source token:"+originalTokens.get(i).toString()+"("+lineNo+","+originalTokens.get(i).columnNo+")");
				System.out.print(", target token:"+targetTokens.get(old-1).toString()+"("+targetTokens.get(old-1).lineNo+","+targetTokens.get(old-1).columnNo+")");
				break;
			}
//            if (! result) break;
		}


		return  result;
	}

	static public String formatSql( EDbVendor dbVendor, String inputQuery )
	{
		String Result = inputQuery;
		TGSqlParser sqlparser = new TGSqlParser( dbVendor );
		sqlparser.sqltext = inputQuery;
		int ret = sqlparser.parse( );
		if ( ret == 0 )
		{
			GFmtOpt option = GFmtOptFactory.newInstance();
			option.caseFuncname = TCaseOption.CoNoChange;
			Result = FormatterFactory.pp(sqlparser, option);
		}
		return Result;
	}


	/**
	 *  when {@link gudusoft.gsqlparser.stmt.TInsertSqlStatement#valueType} = TBaseType.vt_values_function,
	 *  user {@link gudusoft.gsqlparser.stmt.TInsertSqlStatement#functionCall} to build values clause.
	 */
	public void testInsert( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlParser.sqltext = "insert into a values f.b(10);";
		sqlParser.parse();
		//System.out.println(sqlParser.sqlstatements.get(0).toScript());
		assertTrue(verifyScript(EDbVendor.dbvoracle, sqlParser.sqlstatements.get(0).toString(), sqlParser.sqlstatements.get(0).toScript()));
	}

	/**
	 * refine class {@link gudusoft.gsqlparser.nodes.TConstant} to make it easy to handle in scriptWriter
	 */
	public void testCastTimestamp( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlParser.sqltext = "select CAST(timestamp '2000-03-28 08:00:00' AS date)  from dual;";
		sqlParser.parse();
		//System.out.println(sqlParser.sqlstatements.get(0).toScript());
		assertTrue(verifyScript(EDbVendor.dbvoracle, sqlParser.sqlstatements.get(0).toString(), sqlParser.sqlstatements.get(0).toScript()));
	}


	public void testGenericDatatype( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlParser.sqltext = "Create table datatype\n" +
				"(fld0 generictype,\n" +
				" fld1 character(2),\n" +
				" fld1 char(2),\n" +
				" fld2 varchar(1),\n" +
				" fld2 varchar2(1),\n" +
				" fld3 nchar(1),\n" +
				" fld4 nvarchar2(1),\n" +
				" fld5 number,\n" +
				" fld6 number(9),\n" +
				" fld7 number(9,2),\n" +
				" fld71 integer,\n" +
				" fld72 int,\n" +
				" fld711 real,\n" +
				" fld73 smallint,\n" +
				" fld74 numeric,\n" +
				" fld75 numeric(9),\n" +
				" fld75 numeric(9,10),\n" +
				" fld76 decimal,\n" +
				" fld76 decimal(2),\n" +
				" fld76 decimal(9,3),\n" +
				" fld77 dec,\n" +
				" fld77 dec(9),\n" +
				" fld77 dec(9,8),\n" +
				" fld78 float,\n" +
				" fld79 float(9),\n" +
				" fld710 double precision,\n" +
				" fld8 long,\n" +
				" fld9 long raw,\n" +
				" fld10 raw(8),\n" +
				" fld11 date,\n" +
				" fld12 timestamp,\n" +
				" fld13 timestamp(10) with local time zone)";

		sqlParser.parse();
//		System.out.println( sqlParser.sqlstatements.get(0).toScript());
		assertTrue(verifyScript(EDbVendor.dbvoracle, sqlParser.sqlstatements.get(0).toString(), sqlParser.sqlstatements.get(0).toScript()));
	}

	public void testDatatypeInterval( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlParser.sqltext = "Create table datatype\n" +
				"(fld0 generictype,\n" +
				" fld14 interval year to month,\n" +
				" fld15 interval day(9) to second(333)\n" +
				");";
		sqlParser.parse();
		//System.out.println(sqlParser.sqlstatements.get(0).toScript());
		assertTrue(verifyScript(EDbVendor.dbvoracle, sqlParser.sqlstatements.get(0).toString(), sqlParser.sqlstatements.get(0).toScript()));
	}

	public void testIntervalExpr( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlParser.sqltext = "SELECT (SYSTIMESTAMP - order_date) DAY TO SECOND from orders;";
		sqlParser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle, sqlParser.sqlstatements.get(0).toString(), sqlParser.sqlstatements.get(0).toScript()));
		sqlParser.sqltext = "SELECT (SYSTIMESTAMP - order_date) YEAR TO month from orders;";
		sqlParser.parse();
		assertTrue(verifyScript(EDbVendor.dbvoracle, sqlParser.sqlstatements.get(0).toString(), sqlParser.sqlstatements.get(0).toScript()));
	}

	public void testCreateGlobalTable( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlParser.sqltext = "CREATE GLOBAL TEMPORARY TABLE EMP (EMPID NUMBER(10,0));";
		sqlParser.parse();
		//System.out.println(sqlParser.sqlstatements.get(0).toScript());
		assertTrue(verifyScript(EDbVendor.dbvoracle, sqlParser.sqlstatements.get(0).toString(), sqlParser.sqlstatements.get(0).toScript()));
	}


	/**
	 * SQL Server compute clause.
	 */
	public void testComputeClause( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvmssql);
		sqlParser.sqltext = "SELECT CustomerID, OrderDate, SubTotal, TotalDue\n" +
				"FROM Sales.SalesOrderHeader\n" +
				"WHERE SalesPersonID = 35\n" +
				"ORDER BY OrderDate \n" +
				"COMPUTE SUM(SubTotal), SUM(TotalDue);";
		int iRet = sqlParser.parse();
		assertTrue(sqlParser.getErrormessage(),iRet==0);

		assertTrue(verifyScript(EDbVendor.dbvmssql, sqlParser.sqlstatements.get(0).toString(), sqlParser.sqlstatements.get(0).toScript()));
	}

	public void testComputeClause2( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvmssql);
		sqlParser.sqltext = "SELECT Title = CONVERT(char(20), title), type, price, advance\n" +
				"FROM Titles\n" +
				"WHERE ytd_sales IS NOT NULL\n" +
				"  AND type LIKE '%cook%'\n" +
				"ORDER BY type DESC\n" +
				"COMPUTE AVG(price), SUM(advance) BY type\n" +
				"COMPUTE SUM(price), SUM(advance)\n" +
				";";
		int iRet = sqlParser.parse();
		assertTrue(sqlParser.getErrormessage(),iRet==0);
		//System.out.println(sqlParser.sqlstatements.get(0).toScript());
		assertTrue(verifyScript(EDbVendor.dbvmssql, sqlParser.sqlstatements.get(0).toString(), sqlParser.sqlstatements.get(0).toScript()));
	}


	public void testCreateTableIdentity( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvmssql);
		sqlParser.sqltext = "Create Table dbo.[TDataPool_2018] (\n" +
				"\tUserID int NOT NULL identity(1,1) )";
		int iRet = sqlParser.parse();
		assertTrue(sqlParser.getErrormessage(),iRet==0);
		//System.out.println(sqlParser.sqlstatements.get(0).toScript());
		assertTrue(verifyScript(EDbVendor.dbvmssql, sqlParser.sqlstatements.get(0).toString(), sqlParser.sqlstatements.get(0).toScript()));
	}



	public void testCreateTableClusterKey( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvmssql);
		sqlParser.sqltext = "CREATE TABLE AppUser\n" +
				"(\n" +
				"UserID int NOT NULL identity(1,1) ,\n" +
				"FirstName varchar(100) NOT NULL ,\n" +
				"MiddleInitial char(3) NULL ,\n" +
				"LastName varchar(100) NULL ,\n" +
				"LoginName varchar(100) NULL ,\n" +
				"UserDescription varchar(100) NULL ,\n" +
				"Password varchar(100) NULL ,\n" +
				"CustomerID int NULL ,\n" +
				"CreatedDt datetime constraint def_date_19 default getdate(),\n" +
				"PRIMARY KEY CLUSTERED (UserID ASC)\n" +
				");";
		int iRet = sqlParser.parse();
		assertTrue(sqlParser.getErrormessage(),iRet==0);
		//System.out.println(sqlParser.sqlstatements.get(0).toScript());
		assertTrue(verifyScript(EDbVendor.dbvmssql, sqlParser.sqlstatements.get(0).toString(), sqlParser.sqlstatements.get(0).toScript()));
	}


	// test cases below are not passed.

	/**
	 * having clause without group by
	 */
	public void testGroupbyHaving( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlParser.sqltext = "SELECT COUNT(*) FROM DUAL HAVING COUNT(*) >= 1";
		sqlParser.parse();
		//System.out.println(sqlParser.sqlstatements.get(0).toScript());
		assertTrue(verifyScript(EDbVendor.dbvoracle, sqlParser.sqlstatements.get(0).toString(), sqlParser.sqlstatements.get(0).toScript()));
	}

	/**
	 * force/noforce clause, use {@link gudusoft.gsqlparser.stmt.TCreateViewSqlStatement#isForce() } and {@link gudusoft.gsqlparser.stmt.TCreateViewSqlStatement#isNoForce() }
	 * to determine this clause.
	 *
	 * with read only clause, use {@link gudusoft.gsqlparser.stmt.TCreateViewSqlStatement#getRestrictionClause()} to add this clause.
	 */
	public void testNoforce( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlParser.sqltext = "create or replace noforce view sctoo.empvu80(ab,dss,dd)\n" +
				"as select employee_id,last_name,salary\n" +
				"from employees\n" +
				"where employee_id = 80\n" +
				"with read only;";
		sqlParser.parse();
		//System.out.println(sqlParser.sqlstatements.get(0).toScript());
		assertTrue(verifyScript(EDbVendor.dbvoracle, sqlParser.sqlstatements.get(0).toString(), sqlParser.sqlstatements.get(0).toScript()));
	}

	public void testInsertWhen(){
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlParser.sqltext = "INSERT\n" +
		"WHEN MOD(deptno,2)=0 THEN\n" +
		"INTO even_employees (empno1, ename1)\n" +
		"VALUES (empno, ename)\n" +
		"WHEN MOD(deptno,2)=1 THEN\n" +
		"INTO uneven_employees (empno2, ename2)\n" +
		"VALUES (empno, ename)\n" +
		"ELSE\n" +
		"INTO unknow_employees (empno3, ename3)\n" +
		"VALUES (empno, ename)\n" +
		"SELECT empno, ename, deptno FROM emp";
		sqlParser.parse();
		//System.out.println(sqlParser.sqlstatements.get(0).toScript());
		assertTrue(verifyScript(EDbVendor.dbvoracle, sqlParser.sqlstatements.get(0).toString(), sqlParser.sqlstatements.get(0).toScript()));
	}
	
	public void testInsertAll( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlParser.sqltext = "INSERT ALL\n" +
				"WHEN order_total < 1000000 THEN\n" +
				"INTO small_orders\n" +
				"WHEN order_total > 1000000 AND order_total < 2000000 THEN\n" +
				"INTO medium_orders\n" +
				"WHEN order_total > 2000000 THEN\n" +
				"INTO large_orders\n" +
				"SELECT order_id, order_total, sales_rep_id, customer_id\n" +
				"FROM orders";
		sqlParser.parse();
		//System.out.println(sqlParser.sqlstatements.get(0).toScript());
		assertTrue(verifyScript(EDbVendor.dbvoracle, sqlParser.sqlstatements.get(0).toString(), sqlParser.sqlstatements.get(0).toScript()));
	}

	public void testInsertFirst( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlParser.sqltext = "INSERT FIRST\n" +
				"WHEN ottl < 100000 THEN\n" +
				"INTO small_orders\n" +
				"VALUES(oid, ottl, sid, cid)\n" +
				"WHEN ottl > 100000 and ottl < 200000 THEN\n" +
				"INTO medium_orders\n" +
				"VALUES(oid, ottl, sid, cid)\n" +
				"WHEN ottl > 290000 THEN\n" +
				"INTO special_orders\n" +
				"WHEN ottl > 200000 THEN\n" +
				"INTO large_orders\n" +
				"VALUES(oid, ottl, sid, cid)\n" +
				"SELECT o.order_id oid, o.customer_id cid, o.order_total ottl,\n" +
				"o.sales_rep_id sid, c.credit_limit cl, c.cust_email cem\n" +
				"FROM orders o, customers c\n" +
				"WHERE o.customer_id = c.customer_id;";
		sqlParser.parse();
		//System.out.println(sqlParser.sqlstatements.get(0).toScript());
		assertTrue(verifyScript(EDbVendor.dbvoracle, sqlParser.sqlstatements.get(0).toString(), sqlParser.sqlstatements.get(0).toScript()));
	}

	/**
	 * support key actions in reference clause.
	 * see {@link gudusoft.gsqlparser.nodes.TConstraint#getKeyActions()}
	 */
	public void testOnDeleteCascade( )
	{
		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlParser.sqltext = "create table new_employees\n" +
				"(employee_id  number not null,\n" +
				"hire_date date default sysdate,\n" +
				"start_date timestamp(7) references scott.dept(start_date),\n" +
				"end_date timestamp(7) references dept.end_date on delete cascade,\n" +
				"end_date timestamp(7) references dept.end_date on update set null,\n" +
				"check (start_date>end_date),\n" +
				"constraint abc unique(a,b),\n" +
				"primary key(a),\n" +
				"foreign key(a,b) references dept(c,d) on delete set null\n" +
				");";
		sqlParser.parse();
		//System.out.println(sqlParser.sqlstatements.get(0).toScript());
		assertTrue(verifyScript(EDbVendor.dbvoracle, sqlParser.sqlstatements.get(0).toString(), sqlParser.sqlstatements.get(0).toScript()));
	}

//	public void testMySQLCreateTable( )
//	{
//		TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvmysql);
//		sqlParser.sqltext = "CREATE TABLE `DEPT_MANAGER_TBL` ( \n" +
//				" `EMP_NO` INT(4) unsigned zerofill NOT NULL DEFAULT 1000, \n" +
//				" `DEPT_NO` CHAR(4) CHARACTER SET latin1 COLLATE latin1_german1_ci NOT NULL, \n" +
//				" `TO_DATE` GEOMETRY NOT NULL , `FROM_DATE` DATE NOT NULL, \n" +
//				" PRIMARY KEY (`EMP_NO`, `DEPT_NO`)\n" +
//				") COLLATE=utf8_unicode_ci;";
//		sqlParser.parse();
//		System.out.println(sqlParser.sqlstatements.get(0).toScript());
//		//assertTrue(verifyScript(EDbVendor.dbvmysql, sqlParser.sqlstatements.get(0).toString(), sqlParser.sqlstatements.get(0).toScript()));
//	}

}