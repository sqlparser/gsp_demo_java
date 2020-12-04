package antiSQLInjection;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import junit.framework.TestCase;
import demos.antiSQLInjection.TAntiSQLInjection;

public class antiSQLInjectionTest extends TestCase
{

	public void testSyntaxError( )
	{
		String sqltext = "select col1 from table1 where ";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		assertTrue( anti.getSqlInjections( )
				.get( 0 )
				.getType( )
				.toString( )
				.equalsIgnoreCase( "syntax_error" ) );
	}

	public void testAlways_true_condition( )
	{
		String sqltext = "select col1 from table1 where col1 > 1 or 1=1";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		assertTrue( anti.getSqlInjections( )
				.get( 0 )
				.getType( )
				.toString( )
				.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testAlways_false_condition( )
	{
		String sqltext = "select col1 from table1 where col1 > 1 and 1=2";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		assertTrue( anti.getSqlInjections( )
				.get( 0 )
				.getType( )
				.toString( )
				.equalsIgnoreCase( "always_false_condition" ) );
	}

	public void testComment_at_the_end_of_statement( )
	{
		String sqltext = "select col1 from table1 where col1 > 1; -- comment at the end of sql statement, maybe a sql injection";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		assertTrue( anti.getSqlInjections( )
				.get( 0 )
				.getType( )
				.toString( )
				.equalsIgnoreCase( "comment_at_the_end_of_statement" ) );
	}

	public void testStacking_queries( )
	{
		String sqltext = "select col1 from table1 where col1 > 1; drop table t1;";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		assertTrue( anti.getSqlInjections( )
				.get( 0 )
				.getType( )
				.toString( )
				.equalsIgnoreCase( "stacking_queries" ) );
	}

	public void testUnion_set( )
	{
		String sqltext = "select col1 from table1 where col1 > 1 union select col2 from table2";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		anti.check_union_set( true );
		assertTrue( anti.isInjected( sqltext ) );
		assertTrue( anti.getSqlInjections( )
				.get( 0 )
				.getType( )
				.toString( )
				.equalsIgnoreCase( "union_set" ) );
	}

	public void testNot_in_allowed_statement( )
	{
		String sqltext = "select col1 from table1 where col1 > 1; update table2 set col1=1";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		assertTrue( anti.getSqlInjections( )
				.get( 0 )
				.getType( )
				.toString( )
				.equalsIgnoreCase( "stacking_queries" ) );
		assertTrue( anti.getSqlInjections( )
				.get( 1 )
				.getType( )
				.toString( )
				.equalsIgnoreCase( "not_in_allowed_statement" ) );

		anti.enableStatement( ESqlStatementType.sstupdate );
		anti.check_stacking_queries( false );

		assertTrue( !anti.isInjected( sqltext ) );

	}

	public void testFunctionLower( )
	{
		String sqltext = "select col1 from table1 where lower('ABC')='abc'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionUpper( )
	{
		String sqltext = "select col1 from table1 where upper('abc')='ABC'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionAbs( )
	{
		String sqltext = "select col1 from table1 where ABS(-15)=15";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionAcos( )
	{
		String sqltext = "select col1 from table1 where ACOS(1)=0";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionAsin( )
	{
		String sqltext = "select col1 from table1 where ASIN(.5)>.5235 and ASIN(.5)-.5235<.0001";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionAtan( )
	{
		String sqltext = "select col1 from table1 where ATAN(0)=0";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionAtan2( )
	{
		String sqltext = "select col1 from table1 where ATAN2(.3, .2)>.982793723 and ATAN2(.3, .2)-.982793723<.00000001";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionBitAnd( )
	{
		String sqltext = "select col1 from table1 where BITAND(6,3)=2";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionCeil( )
	{
		String sqltext = "select col1 from table1 where CEIL(268651.8)=268652";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionCos( )
	{
		String sqltext = "select col1 from table1 where COS(0)=1";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionCosh( )
	{
		String sqltext = "select col1 from table1 where COSH(0)=1";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionExp( )
	{
		String sqltext = "select col1 from table1 where EXP(4)>54.59815 and EXP(4)-54.59815<.00001";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionFloor( )
	{
		String sqltext = "select col1 from table1 where FLOOR(15.7)=15";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionLn( )
	{
		String sqltext = "select col1 from table1 where LN(95)>4.55387689 and LN(95)-4.55387689<.00000001";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionLog( )
	{
		String sqltext = "select col1 from table1 where LOG(10,100)=2";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionMod( )
	{
		String sqltext = "select col1 from table1 where MOD(11,4)=3 ";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionNanvl( )
	{
		String sqltext = "select col1 from table1 where NANVL(0/0,1)=1";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionPower( )
	{
		String sqltext = "select col1 from table1 where POWER(3,2)=9";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionRemainder( )
	{
		String sqltext = "select col1 from table1 where REMAINDER(1.235E+003,1.235E+003+5.859E-005)>5.858E-005 and REMAINDER(1.235E+003,1.235E+003+5.859E-005)-5.858E-005<.001E-005";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionRound( )
	{
		String sqltext = "select col1 from table1 where ROUND(15.193,1)=15.2";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where ROUND(15.193,-1)=20";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where ROUND(15.193,0)= ROUND(15.193)";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionSign( )
	{
		String sqltext = "select col1 from table1 where SIGN(-15)=-1";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where SIGN(15)=1";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where SIGN(0/0)=1";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionSin( )
	{
		String sqltext = "select col1 from table1 where SIN(0)=0";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionSinh( )
	{
		String sqltext = "select col1 from table1 where SINH(0)=0";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionSqrt( )
	{
		String sqltext = "select col1 from table1 where SQRT(25)=5";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionTan( )
	{
		String sqltext = "select col1 from table1 where TAN(0)=0";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionTanh( )
	{
		String sqltext = "select col1 from table1 where TANH(0)=0";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionTrunc( )
	{
		String sqltext = "select col1 from table1 where TRUNC(15.79,1)=15.7";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where TRUNC(15.79)=15";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where TRUNC(15.79,-1)=10";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionWidth_bucket( )
	{
		String sqltext = "select col1 from table1 where WIDTH_BUCKET(90, 100, 5000, 10)=0";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where WIDTH_BUCKET(5000, 100, 5000, 10)=11";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where WIDTH_BUCKET(14, 5, 30, 5)=2";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionAscii( )
	{
		String sqltext = "select col1 from table1 where ASCII('ABC')=65";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionInstr( )
	{
		String sqltext = "select col1 from table1 where INSTR('CORPORATE FLOOR','OR', 3, 2)=14";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where INSTR('CORPORATE FLOOR','OR', -3, 2)=2";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionLength( )
	{
		String sqltext = "select col1 from table1 where LENGTH('CANDIDE')=7";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionChr( )
	{
		String sqltext = "select col1 from table1 where CHR(67)||CHR(65)||CHR(84)='CAT'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionConcat( )
	{
		String sqltext = "select col1 from table1 where CONCAT(CONCAT('Hall', ' job category is '), 'SA_REP')='Hall job category is SA_REP'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionInitcap( )
	{
		String sqltext = "select col1 from table1 where INITCAP('the soap')='The Soap'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionLPad( )
	{
		String sqltext = "select col1 from table1 where LPAD('ORACLE',10)='    ORACLE'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where LPAD('Page 1',15,'*.')='*.*.*.*.*Page 1'";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where LPAD('ORACLE',4)='ORAC'";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionRPad( )
	{
		String sqltext = "select col1 from table1 where RPAD('ORACLE',10)='ORACLE    '";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where RPAD('Page 1',15,'*.')='Page 1*.*.*.*.*'";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where RPAD('ORACLE',4)='ORAC'";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionLTrim( )
	{
		String sqltext = "select col1 from table1 where LTRIM('<===>ORACLE<===>', '<>=')='ORACLE<===>'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where LTRIM('   ORACLE   ')='ORACLE   '";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionRTrim( )
	{
		String sqltext = "select col1 from table1 where RTRIM('<===>ORACLE<===>', '<>=')='<===>ORACLE'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where RTRIM('   ORACLE   ')='   ORACLE'";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionSubstr( )
	{
		String sqltext = "select col1 from table1 where SUBSTR('ABCDEFG',3,4)='CDEF'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where SUBSTR('ABCDEFG',-5,4)='CDEF'";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionTranslate( )
	{
		String sqltext = "select col1 from table1 where TRANSLATE('ORACLE DATABASE', 'OLE', '01')='0RAC1 DATABAS'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where TRANSLATE('SQL*Plus Users Guide',' */','___')='SQL_Plus_Users_Guide'";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionReplace( )
	{
		String sqltext = "select col1 from table1 where REPLACE('JACK and JUE','J','BL') ='BLACK and BLUE'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionGreatest( )
	{
		String sqltext = "select col1 from table1 where GREATEST ('HARRY', 'HARRIOT', 'HAROLD') ='HARRY'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where GREATEST (100, '99', '101')=101";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionLeast( )
	{
		String sqltext = "select col1 from table1 where LEAST ('HARRY', 'HARRIOT', 'HAROLD') ='HAROLD'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where LEAST (100, '99', '101')=99";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testOperator( )
	{
		String sqltext = "select col1 from table1 where 3*4>=12 and 3*4<=12 and 3-2=1 and 1+2=3 and 4/2=2 and 3>2 and 2<3";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionDegrees( )
	{
		String sqltext = "select col1 from table1 where DEGREES((PI()/2)) = 90";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionRand( )
	{
		String sqltext = "select col1 from table1 where RAND(20)<=20";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where RAND()*20<=20";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionCot( )
	{
		String sqltext = "select col1 from table1 where ABS(COT(124.1332))<0.040312 and 0.040312-ABS(COT(124.1332))<0.000001";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionLog10( )
	{
		String sqltext = "select col1 from table1 where LOG10(100)=2";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionPI( )
	{
		String sqltext = "select col1 from table1 where PI()>3.14159265358979 and PI()<3.14159265358980";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionRadians( )
	{
		String sqltext = "select col1 from table1 where ABS(RADIANS(-45.01))<0.785573 and ABS(RADIANS(-45.01))>0.78557";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionSquare( )
	{
		String sqltext = "select col1 from table1 where Square(2.0)=4.0";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionCharIndex( )
	{
		String sqltext = "select col1 from table1 where CHARINDEX('vital', 'Reflectors are vital safety components of your bicycle.',5)=16";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionConcat_SQLServer( )
	{
		String sqltext = "select col1 from table1 where CONCAT ( 'Happy ', 'Birthday ', 11, '/', '25' )='Happy Birthday 11/25'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionLeft( )
	{
		String sqltext = "select col1 from table1 where LEFT('abcdefg',2)='ab'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionLen( )
	{
		String sqltext = "select col1 from table1 where LEN(' str str ')=8";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionReverse( )
	{
		String sqltext = "select col1 from table1 where REVERSE('Roberto')='otreboR'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionReplicate( )
	{
		String sqltext = "select col1 from table1 where REPLICATE('0', 4)='0000'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionRight( )
	{
		String sqltext = "select col1 from table1 where RIGHT('http://www.163.com',11)='www.163.com'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionStr( )
	{
		String sqltext = "select col1 from table1 where STR(123.45, 6, 1)=' 123.5'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where STR(123.45, 2, 2)='**'";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where STR (FLOOR (123.45), 8, 3)=' 123.000'";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionSpace( )
	{
		String sqltext = "select col1 from table1 where SPACE(2)='  '";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionStuff( )
	{
		String sqltext = "select col1 from table1 where STUFF('abcdef', 2, 3, 'ijklmn')='aijklmnef'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where STUFF('abcdef', 2, 30, 'ijklmn')='aijklmn'";
		anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionSubstring( )
	{
		String sqltext = "select col1 from table1 where SUBSTRING('abcdef', 2, 3)='bcd'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionCRC32( )
	{
		String sqltext = "select col1 from table1 where CRC32('MySQL')=3259397556";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvoracle );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionLog2( )
	{
		String sqltext = "select col1 from table1 where LOG2(65536)=16";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvmysql );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionOct( )
	{
		String sqltext = "select col1 from table1 where OCT(12)='14'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvmysql );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionTruncate( )
	{
		String sqltext = "select col1 from table1 where TRUNCATE(1.223,1)=1.2";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvmysql );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionBin( )
	{
		String sqltext = "select col1 from table1 where BIN(12)='1100'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvmysql );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionBit_length( )
	{
		String sqltext = "select col1 from table1 where BIT_LENGTH('text')=32";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvmysql );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionChar_Mysql( )
	{
		String sqltext = "select col1 from table1 where CHAR(77,77.3,'77.3')='MMM'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvmysql );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionConcat_ws( )
	{
		String sqltext = "select col1 from table1 where CONCAT_WS(',','First name','Last Name',NULL)='First name,Last Name'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvmysql );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionConcat_Mysql( )
	 {
	 String sqltext =
	 "select col1 from table1 where CONCAT('My', NULL, 'QL')=NULL";
	 TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvmysql );
	 assertTrue( anti.isInjected( sqltext ) );
	 String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
	 assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	
	 sqltext = "select col1 from table1 where CONCAT(14.3)='14.3'";
	 anti = new TAntiSQLInjection( EDbVendor.dbvmysql );
	 assertTrue( anti.isInjected( sqltext ) );
	 type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
	 assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}
	
	 public void testFunctionElt( )
	 {
	 String sqltext =
	 "select col1 from table1 where ELT(5, 'ej', 'Heja', 'hej', 'foo')=NULL";
	 TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvmysql );
	 assertTrue( anti.isInjected( sqltext ) );
	 String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
	 assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	
	 sqltext =
	 "select col1 from table1 where ELT(4, 'ej', 'Heja', 'hej', 'foo')='foo'";
	 anti = new TAntiSQLInjection( EDbVendor.dbvmysql );
	 assertTrue( anti.isInjected( sqltext ) );
	 type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
	 assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	 }
	 

	public void testFunctionField( )
	{
		String sqltext = "select col1 from table1 where FIELD('ej', 'Hej', 'ej', 'Heja', 'hej', 'foo')=2";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvmysql );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where FIELD('20', '2', '20.0', '200')=2";
		anti = new TAntiSQLInjection( EDbVendor.dbvmysql );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}

	public void testFunctionHex( )
	{
		String sqltext = "select col1 from table1 where HEX(255)='FF'";
		TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvmysql );
		assertTrue( anti.isInjected( sqltext ) );
		String type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );

		sqltext = "select col1 from table1 where HEX('abc')=616263";
		anti = new TAntiSQLInjection( EDbVendor.dbvmysql );
		assertTrue( anti.isInjected( sqltext ) );
		type = anti.getSqlInjections( ).get( 0 ).getType( ).toString( );
		assertTrue( type.equalsIgnoreCase( "always_true_condition" ) );
	}


}
