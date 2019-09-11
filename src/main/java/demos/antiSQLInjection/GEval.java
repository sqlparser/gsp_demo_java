
package demos.antiSQLInjection;

import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.nodes.ENodeType;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TConstant;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TWhereClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

import java.util.Stack;

import org.boris.expr.Expr;
import org.boris.expr.ExprBoolean;
import org.boris.expr.ExprEvaluatable;
import org.boris.expr.parser.ExprParser;
import org.boris.expr.util.Exprs;
import org.boris.expr.util.SimpleEvaluationContext;

/**
 * GEval used to evaluate condition in where clause
 * <p>
 * Usage:
 * <p>
 * GEval e = new GEval()
 * <p>
 * e.value(condition,context)
 * <p>
 * This class help to find out expression that always return true or false which
 * will be
 * <p>
 * used as a sql injection.
 * <p>
 * If expression can't be evaluated, then an unknown value was returned.
 * <p>
 * <p>
 * How this Evaluator works:
 * <p>
 * This Evaluator use Postfix expression evaluation to calculate value of an
 * expression
 * <p>
 * TExpression.postOrderTraverse function traverses the expression in post fix
 * order, and GEval work
 * <p>
 * as a tree visitor to evaluate value of this expression
 * <p>
 * Check this article to found out how postfix expression evaluation this works:
 * <p>
 * http://scriptasylum.com/tutorials/infix_postfix/algorithms/postfix-evaluation
 * /index.htm
 * <p>
 * <p>
 * Supported expression syntax:
 * <ul>
 * <li>column-name > 1, an unknown value was returned.</li>
 * <li>column-name > 1 or 1=1, always return true.</li>
 * <li>column-name > 1 and 1=2, always return false</li>
 * <li>column-name > 1 and 1+2-8/4 = 1, always return true</li>
 * <li>column-name > 1 or 2 between 1 and 3, always return true</li>
 * <li>column-name > 1 or 'abc' like 'ab%', always return true</li>
 * <li>null is null, always return true</li>
 * <li>exists (select 1 from tab where 1<2), always return true</li>
 * </ul>
 * <p>
 * <p>
 * In condition was not supported yet, so
 * <p>
 * 1 in (1,2,3), will return unknown value
 * <p>
 * <p>
 * you can modify this evaluator to meet your own requirements.
 * 
 */

public class GEval
{

	public GEval( )
	{
	}

	/**
	 * Evaluate a expression.
	 * 
	 * @param expr
	 *            , condition need to be evaluated.
	 * @param context
	 *            , not used in current version
	 * @return
	 */

	public Object value( TExpression expr, GContext context )
	{
		evalVisitor ev = new evalVisitor( context );
		expr.postOrderTraverse( ev );
		return ev.getValue( );
	}

}

class evalVisitor implements IExpressionVisitor
{

	public evalVisitor( GContext context )
	{
		this.exprs = new Stack( );
		this.context = context;
	}

	private Object value;

	public Object getValue( )
	{
		if ( value == null )
		{
			return ( (TExpression) exprs.pop( ) ).getVal( );
		}
		return value;
	}

	private Stack exprs = null;
	private GContext context = null;

	public boolean exprVisit( TParseTreeNode pNode, boolean isLeafNode )
	{
		if ( value != null )
			return true;
		TExpression expr = (TExpression) pNode;
		switch ( ( expr.getExpressionType( ) ) )
		{
			case simple_source_token_t :
				if ( expr.getSourcetokenOperand( )
						.toString( )
						.equalsIgnoreCase( "null" ) )
				{
					expr.setVal( null );
				}
				else
				{
					expr.setVal( new UnknownValue( ) );
				}
				break;
			case simple_object_name_t :
				// this.objectOperand.setObjectType(TObjectName.ttobjVariable);
				expr.setVal( new UnknownValue( ) );
				break;
			case simple_constant_t :
				try
				{
					expr.setVal( eval_constant( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case arithmetic_plus_t :
				try
				{
					expr.setVal( eval_add( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case arithmetic_minus_t :
				try
				{
					expr.setVal( eval_subtract( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case arithmetic_times_t :
				try
				{
					expr.setVal( eval_mul( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case arithmetic_divide_t :
				try
				{
					expr.setVal( eval_divide( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case parenthesis_t :
				expr.setVal( ( (TExpression) exprs.pop( ) ).getVal( ) );
				// leftOperand.doParse(psql,plocation);
				break;
			case concatenate_t :
				try
				{
					expr.setVal( eval_concatenate( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case unary_plus_t :
				expr.setVal( ( (TExpression) exprs.pop( ) ).getVal( ) );
				break;
			case unary_minus_t :
				try
				{
					Long l = Coercion.coerceLong( ( (TExpression) exprs.pop( ) ).getVal( ) );
					expr.setVal( -l );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case assignment_t :
				try
				{
					eval_assignment( expr );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case group_t :
				if ( expr.getRightOperand( ).getSubQuery( ) != null )
				{
					checkSubquery( expr.getRightOperand( ).getSubQuery( ) );
				}
				expr.setVal( new UnknownValue( ) );
				// inExpr.doParse(psql,plocation);
				break;
			case list_t :
				expr.setVal( new UnknownValue( ) );
				// exprList.doParse(psql,plocation);
				break;
			case function_t :
				expr.setVal( computeFunction( expr ) );
				break;
			case new_structured_type_t :
				// functionCall.doParse(psql,plocation);
				expr.setVal( new UnknownValue( ) );
				break;
			case cursor_t :
				expr.setVal( new UnknownValue( ) );
				break;
			case subquery_t :
				checkSubquery( expr.getSubQuery( ) );
				expr.setVal( new UnknownValue( ) );
				break;
			case case_t :
				expr.setVal( new UnknownValue( ) );
				break;
			case pattern_matching_t :
				try
				{
					expr.setVal( eval_like( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case exists_t :
				try
				{
					expr.setVal( eval_exists_condition( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case new_variant_type_t :
				expr.setVal( new UnknownValue( ) );
				break;
			case unary_prior_t :
				try
				{
					expr.setVal( eval_unknown_one_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				// expr.setVal(new UnknownValue());
				break;
			case unary_bitwise_not_t :
				try
				{
					expr.setVal( eval_unknown_one_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				// expr.setVal(new UnknownValue());
				break;
			case sqlserver_proprietary_column_alias_t :
				try
				{
					expr.setVal( eval_unknown_two_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				// expr.setVal(new UnknownValue());
				break;
			case arithmetic_modulo_t :
				try
				{
					expr.setVal( eval_mod( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case bitwise_exclusive_or_t :
				try
				{
					expr.setVal( eval_unknown_two_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				// expr.setVal(new UnknownValue());
				break;
			case bitwise_or_t :
				try
				{
					expr.setVal( eval_unknown_two_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case bitwise_and_t :
				try
				{
					expr.setVal( eval_unknown_two_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case bitwise_xor_t :
				try
				{
					expr.setVal( eval_unknown_two_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case exponentiate_t :
				try
				{
					expr.setVal( eval_unknown_two_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case scope_resolution_t :
				// expr.setVal(new UnknownValue());
				break;
			case at_time_zone_t :
				try
				{
					expr.setVal( eval_unknown_two_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case at_local_t :
				try
				{
					expr.setVal( eval_unknown_one_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case day_to_second_t :
				try
				{
					expr.setVal( eval_unknown_one_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case year_to_month_t :
				try
				{
					expr.setVal( eval_unknown_one_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case simple_comparison_t :
				try
				{
					expr.setVal( eval_simple_comparison_conditions( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case group_comparison_t :
				try
				{
					expr.setVal( eval_group_comparison_conditions( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case in_t :
				try
				{
					expr.setVal( eval_in_conditions( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
					// File | Settings | File Templates.
				}
				break;
			case floating_point_t :
				try
				{
					expr.setVal( eval_unknown_one_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case logical_and_t :
				try
				{
					expr.setVal( eval_logical_conditions_and( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case logical_or_t :
				try
				{
					expr.setVal( eval_logical_conditions_or( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case logical_not_t :
				try
				{
					expr.setVal( eval_logical_conditions_not( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case logical_xor_t :
				try
				{
					expr.setVal( eval_unknown_two_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case null_t :
				try
				{
					expr.setVal( eval_isnull( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				// leftOperand.doParse(psql,plocation);
				break;
			case between_t :
				try
				{
					expr.setVal( eval_between( expr ) );
				}
				catch ( Exception e )
				{
					expr.setVal( new UnknownValue( ) );
				}
				break;
			case is_of_type_t :
				try
				{
					expr.setVal( eval_unknown_one_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case collate_t : // sql server
				try
				{
					expr.setVal( eval_unknown_two_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				// leftOperand.doParse(psql,plocation);
				// rightOperand.doParse(psql,plocation);
				break;
			case left_join_t :
				try
				{
					expr.setVal( eval_unknown_two_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case right_join_t :
				// leftOperand.doParse(psql,plocation);
				// rightOperand.doParse(psql,plocation);
				try
				{
					expr.setVal( eval_unknown_two_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			case ref_arrow_t :
				try
				{
					expr.setVal( eval_unknown_two_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				// leftOperand.doParse(psql,plocation);
				// rightOperand.doParse(psql,plocation);
				break;
			case typecast_t :
				try
				{
					expr.setVal( eval_unknown_one_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				// leftOperand.doParse(psql,plocation);
				break;
			case arrayaccess_t :
				expr.setVal( new UnknownValue( ) );
				// arrayAccess.doParse(psql,plocation);
				break;
			case unary_connect_by_root_t :
				try
				{
					expr.setVal( eval_unknown_one_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				// rightOperand.doParse(psql,plocation);
				break;
			case interval_t :
				expr.setVal( new UnknownValue( ) );
				// intervalExpr.doParse(psql,plocation);
				break;
			case unary_binary_operator_t :
				try
				{
					expr.setVal( eval_unknown_one_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				// rightOperand.doParse(psql,plocation);
				break;
			case left_shift_t :
			case right_shift_t :
				try
				{
					expr.setVal( eval_unknown_one_operand( expr ) );
				}
				catch ( Exception e )
				{
					e.printStackTrace( ); // To change body of catch statement
											// use
											// File | Settings | File Templates.
				}
				break;
			default :
				;
		} // switch

		exprs.push( expr );
		return true;
	}

	private Object computeFunction( TExpression expr )
	{
		if ( expr != null && expr.toString( ) != null )
		{
			return computeFunction( expr.toString( ) );
		}
		return new UnknownValue( );
	}

	private Object computeFunction( String expr )
	{
		try
		{
			SimpleEvaluationContext context = new SimpleEvaluationContext( );
			Expr e = ExprParser.parse( expr );
			Exprs.toUpperCase( e );
			if ( e instanceof ExprEvaluatable )
			{
				e = ( (ExprEvaluatable) e ).evaluate( context );
			}
			if ( e != null )
				return e.toString( );
		}
		catch ( Exception e )
		{
			// e.printStackTrace( );
		}
		return new UnknownValue( );
	}

	private void checkSubquery( TSelectSqlStatement select )
	{
		if ( select != null && select.getWhereClause( ) != null )
		{
			Object value = new GEval( ).value( select.getWhereClause( )
					.getCondition( ), context );
			if ( value instanceof Boolean )
			{
				this.value = value;
			}
		}
	}

	Object eval_unknown_one_operand( TExpression expr ) throws Exception
	{
		exprs.pop( );
		return new UnknownValue( );
	}

	Object eval_unknown_two_operand( TExpression expr ) throws Exception
	{
		exprs.pop( );
		exprs.pop( );
		return new UnknownValue( );
	}

	void eval_assignment( TExpression expr ) throws Exception
	{
		Object right = ( (TExpression) exprs.pop( ) ).getVal( );
		TExpression left = (TExpression) exprs.pop( );
		left.setVal( right );
	}

	Object eval_constant( TExpression expr ) throws Exception
	{
		Object ret = null;
		TConstant constant = expr.getConstantOperand( );
		if ( ( constant.getNodeType( ) == ENodeType.T_Constant.getId( ) && isString( constant.toString( ) ) )
				|| constant.getNodeType( ) == ENodeType.T_Constant_String.getId( ) )
		{
			if ( constant.getStartToken( )
					.toString( )
					.equalsIgnoreCase( "NULL" ) )
			{
				ret = "NULL";
			}
			else
			{
				String s = constant.getStartToken( )
						.toString( )
						.substring( 1,
								constant.getStartToken( ).toString( ).length( ) - 1 );
				ret = s;
			}
		}
		else if ( ( constant.getNodeType( ) == ENodeType.T_Constant.getId( ) && isInteger( constant.toString( ) ) )
				|| constant.getNodeType( ) == ENodeType.T_Constant_Integer.getId( ) )
		{
			Long v = Coercion.coerceInteger( constant.getStartToken( )
					.toString( ) );
			if ( constant.getSign( ) != null )
			{
				if ( constant.getSign( ).toString( ).equalsIgnoreCase( "-" ) )
				{
					v = -v;
				}
			}
			ret = v;
		}
		else if ( ( constant.getNodeType( ) == ENodeType.T_Constant.getId( ) && isFloat( constant.toString( ) ) )
				|| constant.getNodeType( ) == ENodeType.T_Constant_Float.getId( ) )
		{
			Double d = Coercion.coerceDouble( constant.getStartToken( )
					.toString( ) );
			ret = d;
		}
		return ret;
	}

	private boolean isString( String string )
	{
		if ( string.equalsIgnoreCase( "NULL" ) )
		{
			return true;
		}
		else
		{
			if ( string.startsWith( "\'" ) && string.endsWith( "\'" ) )
				return true;
		}
		return false;
	}

	private boolean isInteger( String string )
	{
		try
		{
			Integer.parseInt( string );
		}
		catch ( NumberFormatException e )
		{
			return false;
		}
		return true;
	}

	private boolean isFloat( String string )
	{
		try
		{
			Double.parseDouble( string );
		}
		catch ( NumberFormatException e )
		{
			return false;
		}
		return true;
	}

	Object eval_exists_condition( TExpression expr ) throws Exception
	{
		// check condition in subquery
		TSelectSqlStatement select = expr.getSubQuery( );
		TWhereClause where = select.getWhereClause( );
		if ( where == null )
		{
			return Boolean.TRUE;
		}

		TExpression condition = where.getCondition( );
		GEval e = new GEval( );
		return e.value( condition, null );
	}

	Object eval_like( TExpression expr ) throws Exception
	{
		if ( expr.getLikeEscapeOperand( ) != null )
		{
			// exprs.pop();
		}
		Object right = ( (TExpression) exprs.pop( ) ).getVal( );
		Object left = ( (TExpression) exprs.pop( ) ).getVal( );

		if ( ( left instanceof UnknownValue )
				|| ( right instanceof UnknownValue ) )
		{
			return new UnknownValue( );
		}

		if ( right.toString( ).startsWith( "%" ) )
		{
			if ( right.toString( ).endsWith( "%" ) )
			{
				// 'abc' like '%abc%'
				String c = right.toString( ).substring( 1,
						right.toString( ).length( ) - 1 );
				if ( left.toString( ).contains( c ) )
				{
					if ( expr.getOperatorToken( )
							.toString( )
							.equalsIgnoreCase( "not" ) )
					{
						return Boolean.FALSE;
					}
					else
					{
						return Boolean.TRUE;
					}
				}
				else
				{
					if ( expr.getOperatorToken( )
							.toString( )
							.equalsIgnoreCase( "not" ) )
					{
						return Boolean.TRUE;
					}
					else
					{
						return Boolean.FALSE;
					}
				}
			}
			else
			{
				// 'abc' like '%abc'
				String c = right.toString( ).substring( 1,
						right.toString( ).length( ) );
				if ( left.toString( ).endsWith( c ) )
				{
					if ( expr.getOperatorToken( )
							.toString( )
							.equalsIgnoreCase( "not" ) )
					{
						return Boolean.FALSE;
					}
					else
					{
						return Boolean.TRUE;
					}
				}
				else
				{
					if ( expr.getOperatorToken( )
							.toString( )
							.equalsIgnoreCase( "not" ) )
					{
						return Boolean.TRUE;
					}
					else
					{
						return Boolean.FALSE;
					}
				}
			}
		}
		else if ( right.toString( ).endsWith( "%" ) )
		{
			// 'abc' like 'abc%'
			String c = right.toString( ).substring( 0,
					right.toString( ).length( ) - 1 );
			if ( left.toString( ).startsWith( c ) )
			{
				if ( expr.getOperatorToken( )
						.toString( )
						.equalsIgnoreCase( "not" ) )
				{
					return Boolean.FALSE;
				}
				else
				{
					return Boolean.TRUE;
				}
			}
			else
			{
				if ( expr.getOperatorToken( )
						.toString( )
						.equalsIgnoreCase( "not" ) )
				{
					return Boolean.TRUE;
				}
				else
				{
					return Boolean.FALSE;
				}
			}
		}
		else
		{
			// 'abc' like 'abc'
			if ( right.toString( ).equalsIgnoreCase( left.toString( ) ) )
			{
				if ( expr.getOperatorToken( )
						.toString( )
						.equalsIgnoreCase( "not" ) )
				{
					return Boolean.FALSE;
				}
				else
				{
					return Boolean.TRUE;
				}
			}
			else
			{
				if ( expr.getOperatorToken( )
						.toString( )
						.equalsIgnoreCase( "not" ) )
				{
					return Boolean.TRUE;
				}
				else
				{
					return Boolean.FALSE;
				}
			}
		}
	}

	Object eval_notequal( TExpression expr ) throws Exception
	{
		Object right = ( (TExpression) exprs.pop( ) ).getVal( );
		Object left = ( (TExpression) exprs.pop( ) ).getVal( );

		if ( ( left instanceof UnknownValue )
				|| ( right instanceof UnknownValue ) )
		{
			return new UnknownValue( );
		}

		if ( left == null && right == null )
		{
			/*
			 * first, the possibility that both *are* null
			 */

			return Boolean.FALSE;
		}
		else if ( left == null || right == null )
		{
			/*
			 * otherwise, both aren't, so it's clear L != R
			 */
			return Boolean.TRUE;
		}
		else if ( left.getClass( ).equals( right.getClass( ) ) )
		{
			return ( left.equals( right ) ) ? Boolean.FALSE : Boolean.TRUE;
		}
		else if ( left instanceof Float
				|| left instanceof Double
				|| right instanceof Float
				|| right instanceof Double )
		{
			Object result = computeFunction( left.toString( )
					+ "<>"
					+ right.toString( ) );
			return Boolean.parseBoolean( result.toString( ) ) == Boolean.TRUE;
		}
		else if ( left instanceof Number
				|| right instanceof Number
				|| left instanceof Character
				|| right instanceof Character )
		{
			Object result = computeFunction( left.toString( )
					+ "<>"
					+ right.toString( ) );
			return Boolean.parseBoolean( result.toString( ) ) == Boolean.TRUE;
		}
		else if ( left instanceof Boolean || right instanceof Boolean )
		{
			return ( Coercion.coerceBoolean( left ).equals( Coercion.coerceBoolean( right ) ) ) ? Boolean.FALSE
					: Boolean.TRUE;
		}
		else if ( left instanceof java.lang.String || right instanceof String )
		{
			return ( left.toString( ).equals( right.toString( ) ) ) ? Boolean.FALSE
					: Boolean.TRUE;
		}

		return ( left.equals( right ) ) ? Boolean.FALSE : Boolean.TRUE;

	}

	Object eval_ge( TExpression expr ) throws Exception
	{
		Object right = ( (TExpression) exprs.pop( ) ).getVal( );
		Object left = ( (TExpression) exprs.pop( ) ).getVal( );

		if ( ( left instanceof UnknownValue )
				|| ( right instanceof UnknownValue ) )
		{
			return new UnknownValue( );
		}

		if ( left == right )
		{
			return Boolean.TRUE;
		}
		else if ( ( left == null ) || ( right == null ) )
		{
			return Boolean.FALSE;
		}
		else if ( Coercion.isFloatingPoint( left )
				|| Coercion.isFloatingPoint( right ) )
		{
			Object result = computeFunction( left.toString( )
					+ ">="
					+ right.toString( ) );
			return Boolean.parseBoolean( result.toString( ) ) == Boolean.TRUE;
		}
		else if ( Coercion.isNumberable( left )
				|| Coercion.isNumberable( right ) )
		{
			Object result = computeFunction( left.toString( )
					+ ">="
					+ right.toString( ) );
			return Boolean.parseBoolean( result.toString( ) ) == Boolean.TRUE;
		}
		else if ( left instanceof String || right instanceof String )
		{
			String leftString = left.toString( );
			String rightString = right.toString( );

			return leftString.compareTo( rightString ) >= 0 ? Boolean.TRUE
					: Boolean.FALSE;
		}
		else if ( left instanceof Comparable )
		{
			return ( (Comparable) left ).compareTo( right ) >= 0 ? Boolean.TRUE
					: Boolean.FALSE;
		}
		else if ( right instanceof Comparable )
		{
			return ( (Comparable) right ).compareTo( left ) <= 0 ? Boolean.TRUE
					: Boolean.FALSE;
		}

		throw new Exception( "Invalid comparison : GE " );
	}

	Object eval_le( TExpression expr ) throws Exception
	{
		Object right = ( (TExpression) exprs.pop( ) ).getVal( );
		Object left = ( (TExpression) exprs.pop( ) ).getVal( );

		if ( ( left instanceof UnknownValue )
				|| ( right instanceof UnknownValue ) )
		{
			return new UnknownValue( );
		}

		if ( left == right )
		{
			return Boolean.TRUE;
		}
		else if ( ( left == null ) || ( right == null ) )
		{
			return Boolean.FALSE;
		}
		else if ( Coercion.isFloatingPoint( left )
				|| Coercion.isFloatingPoint( right ) )
		{
			Object result = computeFunction( left.toString( )
					+ "<="
					+ right.toString( ) );
			return Boolean.parseBoolean( result.toString( ) ) == Boolean.TRUE;
		}
		else if ( Coercion.isNumberable( left )
				|| Coercion.isNumberable( right ) )
		{
			Object result = computeFunction( left.toString( )
					+ "<="
					+ right.toString( ) );
			return Boolean.parseBoolean( result.toString( ) ) == Boolean.TRUE;
		}
		else if ( left instanceof String || right instanceof String )
		{
			String leftString = left.toString( );
			String rightString = right.toString( );

			return leftString.compareTo( rightString ) <= 0 ? Boolean.TRUE
					: Boolean.FALSE;
		}
		else if ( left instanceof Comparable )
		{
			return ( (Comparable) left ).compareTo( right ) <= 0 ? Boolean.TRUE
					: Boolean.FALSE;
		}
		else if ( right instanceof Comparable )
		{
			return ( (Comparable) right ).compareTo( left ) >= 0 ? Boolean.TRUE
					: Boolean.FALSE;
		}

		throw new Exception( "Invalid comparison : LE " );
	}

	Object eval_lt( TExpression expr ) throws Exception
	{
		Object right = ( (TExpression) exprs.pop( ) ).getVal( );
		Object left = ( (TExpression) exprs.pop( ) ).getVal( );

		if ( ( left instanceof UnknownValue )
				|| ( right instanceof UnknownValue ) )
		{
			return new UnknownValue( );
		}

		if ( ( left == right ) || ( left == null ) || ( right == null ) )
		{
			return Boolean.FALSE;
		}
		else if ( Coercion.isFloatingPoint( left )
				|| Coercion.isFloatingPoint( right ) )
		{
			Object result = computeFunction( left.toString( )
					+ "<"
					+ right.toString( ) );
			return Boolean.parseBoolean( result.toString( ) ) == Boolean.TRUE;
		}
		else if ( Coercion.isNumberable( left )
				|| Coercion.isNumberable( right ) )
		{
			Object result = computeFunction( left.toString( )
					+ "<"
					+ right.toString( ) );
			return Boolean.parseBoolean( result.toString( ) ) == Boolean.TRUE;
		}
		else if ( left instanceof String || right instanceof String )
		{
			String leftString = left.toString( );
			String rightString = right.toString( );

			return leftString.compareTo( rightString ) < 0 ? Boolean.TRUE
					: Boolean.FALSE;
		}
		else if ( left instanceof Comparable )
		{
			return ( (Comparable) left ).compareTo( right ) < 0 ? Boolean.TRUE
					: Boolean.FALSE;
		}
		else if ( right instanceof Comparable )
		{
			return ( (Comparable) right ).compareTo( left ) > 0 ? Boolean.TRUE
					: Boolean.FALSE;
		}

		throw new Exception( "Invalid comparison : LT " );
	}

	Object eval_gt( TExpression expr ) throws Exception
	{
		Object right = ( (TExpression) exprs.pop( ) ).getVal( );
		Object left = ( (TExpression) exprs.pop( ) ).getVal( );

		if ( ( left instanceof UnknownValue )
				|| ( right instanceof UnknownValue ) )
		{
			return new UnknownValue( );
		}

		if ( ( left == right ) || ( left == null ) || ( right == null ) )
		{
			return Boolean.FALSE;
		}
		else if ( Coercion.isFloatingPoint( left )
				|| Coercion.isFloatingPoint( right ) )
		{
			Object result = computeFunction( left.toString( )
					+ ">"
					+ right.toString( ) );
			return Boolean.parseBoolean( result.toString( ) ) == Boolean.TRUE;
		}
		else if ( Coercion.isNumberable( left )
				|| Coercion.isNumberable( right ) )
		{
			Object result = computeFunction( left.toString( )
					+ ">"
					+ right.toString( ) );
			return Boolean.parseBoolean( result.toString( ) ) == Boolean.TRUE;
		}
		else if ( left instanceof String || right instanceof String )
		{
			String leftString = left.toString( );
			String rightString = right.toString( );

			return leftString.compareTo( rightString ) > 0 ? Boolean.TRUE
					: Boolean.FALSE;
		}
		else if ( left instanceof Comparable )
		{
			return ( (Comparable) left ).compareTo( right ) > 0 ? Boolean.TRUE
					: Boolean.FALSE;
		}
		else if ( right instanceof Comparable )
		{
			return ( (Comparable) right ).compareTo( left ) < 0 ? Boolean.TRUE
					: Boolean.FALSE;
		}

		throw new Exception( "Invalid comparison : GT " );

	}

	Object eval_equal( TExpression expr ) throws Exception
	{
		Object right = ( (TExpression) exprs.pop( ) ).getVal( );
		Object left = ( (TExpression) exprs.pop( ) ).getVal( );

		if ( ( left instanceof UnknownValue )
				|| ( right instanceof UnknownValue ) )
		{
			return new UnknownValue( );
		}

		if ( left == null && right == null )
		{
			/*
			 * if both are null L == R
			 */
			return Boolean.TRUE;
		}
		else if ( left == null || right == null )
		{
			/*
			 * we know both aren't null, therefore L != R
			 */
			return Boolean.FALSE;
		}
		else if ( left.getClass( ).equals( right.getClass( ) ) )
		{
			return left.equals( right ) ? Boolean.TRUE : Boolean.FALSE;
		}
		else if ( left instanceof Float
				|| left instanceof Double
				|| right instanceof Float
				|| right instanceof Double )
		{
			Object result = computeFunction( left.toString( )
					+ "="
					+ right.toString( ) );
			return Boolean.parseBoolean( result.toString( ) ) == Boolean.TRUE;
		}
		else if ( left instanceof Number
				|| right instanceof Number
				|| left instanceof Character
				|| right instanceof Character )
		{
			Object result = computeFunction( left.toString( )
					+ "="
					+ right.toString( ) );
			return Boolean.parseBoolean( result.toString( ) ) == Boolean.TRUE;
		}
		else if ( left instanceof Boolean || right instanceof Boolean )
		{
			return Coercion.coerceBoolean( left )
					.equals( Coercion.coerceBoolean( right ) ) ? Boolean.TRUE
					: Boolean.FALSE;
		}
		else if ( left instanceof java.lang.String || right instanceof String )
		{
			return left.toString( ).equals( right.toString( ) ) ? Boolean.TRUE
					: Boolean.FALSE;
		}

		return left.equals( right ) ? Boolean.TRUE : Boolean.FALSE;
	}

	Object eval_logical_conditions_not( TExpression expr ) throws Exception
	{
		Object left = ( (TExpression) exprs.pop( ) ).getVal( );

		if ( left instanceof UnknownValue )
		{
			return new UnknownValue( );
		}
		Boolean b = Coercion.coerceBoolean( left );

		if ( b != null )
		{
			return b.booleanValue( ) ? Boolean.FALSE : Boolean.TRUE;
		}

		throw new Exception( "expression not boolean valued" );
	}

	Object eval_logical_conditions_or( TExpression expr ) throws Exception
	{
		Object right = ( (TExpression) exprs.pop( ) ).getVal( );
		Object left = ( (TExpression) exprs.pop( ) ).getVal( );

		if ( right instanceof UnknownValue )
		{
			if ( left instanceof UnknownValue )
			{
				TExpression leftExpr = expr.getLeftOperand( );
				TExpression rightExpr = expr.getRightOperand( );

				//handle issue #552: select 1 & 1 where col1=col1 or col1 is null
				if ( leftExpr.getExpressionType( ) == EExpressionType.null_t
						&& rightExpr.getExpressionType( ) == EExpressionType.simple_comparison_t )
				{
					if ( rightExpr.getLeftOperand( )
							.toString( )
							.equals( leftExpr.getLeftOperand( ).toString( ) )
							&& rightExpr.getLeftOperand( )
									.toString( )
									.equals( rightExpr.getRightOperand( )
											.toString( ) ) )
					{
						return true;
					}
				}
				else if ( rightExpr.getExpressionType( ) == EExpressionType.null_t
						&& leftExpr.getExpressionType( ) == EExpressionType.simple_comparison_t )
				{
					if ( leftExpr.getLeftOperand( )
							.toString( )
							.equals( rightExpr.getLeftOperand( ).toString( ) )
							&& leftExpr.getLeftOperand( )
									.toString( )
									.equals( leftExpr.getRightOperand( )
											.toString( ) ) )
					{
						return true;
					}
				}

				return new UnknownValue( );
			}
			else
			{
				boolean leftValue = Coercion.coerceBoolean( left )
						.booleanValue( );
				if ( leftValue )
				{
					return Boolean.TRUE;
				}
				else
				{
					return new UnknownValue( );
				}
			}
		}
		else if ( left instanceof UnknownValue )
		{
			boolean rightValue = Coercion.coerceBoolean( right ).booleanValue( );
			if ( rightValue )
			{
				return Boolean.TRUE;
			}
			else
			{
				return new UnknownValue( );
			}
		}
		else
		{
			boolean leftValue = Coercion.coerceBoolean( left ).booleanValue( );
			boolean rightValue = Coercion.coerceBoolean( right ).booleanValue( );

			return ( leftValue || rightValue ) ? Boolean.TRUE : Boolean.FALSE;
		}
	}

	Object eval_logical_conditions_and( TExpression expr ) throws Exception
	{
		Object right = ( (TExpression) exprs.pop( ) ).getVal( );
		Object left = ( (TExpression) exprs.pop( ) ).getVal( );

		if ( right instanceof UnknownValue )
		{
			if ( left instanceof UnknownValue )
			{
				return new UnknownValue( );
			}
			else
			{
				boolean leftValue = Coercion.coerceBoolean( left )
						.booleanValue( );
				if ( !leftValue )
				{
					return Boolean.FALSE;
				}
				else
				{
					return new UnknownValue( );
				}
			}
		}
		else if ( left instanceof UnknownValue )
		{
			boolean rightValue = Coercion.coerceBoolean( right ).booleanValue( );
			if ( !rightValue )
			{
				return Boolean.FALSE;
			}
			else
			{
				return new UnknownValue( );
			}
		}
		else
		{
			boolean leftValue = Coercion.coerceBoolean( left ).booleanValue( );
			boolean rightValue = Coercion.coerceBoolean( right ).booleanValue( );

			return ( leftValue && rightValue ) ? Boolean.TRUE : Boolean.FALSE;
		}

	}

	Object eval_in_conditions( TExpression expr ) throws Exception
	{
		Object result = computeFunction( expr );
		if ( ExprBoolean.TRUE.toString( ).equals( result )
				|| ExprBoolean.FALSE.toString( ).equals( result ) )
			return Boolean.parseBoolean( result.toString( ) ) == Boolean.TRUE;
		else
			return new UnknownValue( );
	}

	Object eval_group_comparison_conditions( TExpression expr )
			throws Exception
	{
		Object right = ( (TExpression) exprs.pop( ) ).getVal( );
		Object left = ( (TExpression) exprs.pop( ) ).getVal( );

		return new UnknownValue( );
	}

	Object eval_simple_comparison_conditions( TExpression expr )
			throws Exception
	{
		TExpression topExpr = (TExpression) exprs.peek( );
		Object right = topExpr.getVal( );
		int index = exprs.indexOf( topExpr );
		Object left = ( (TExpression) exprs.elementAt( index - 1 ) ).getVal( );

		if ( ( left instanceof UnknownValue )
				|| ( right instanceof UnknownValue ) )
		{
			exprs.pop( );
			exprs.pop( );
			return new UnknownValue( );
		}

		if ( expr.getComparisonOperator( ).tokencode == (int) '=' )
		{
			return eval_equal( expr );
		}
		else if ( expr.getComparisonOperator( ).tokencode == TBaseType.not_equal )
		{
			return eval_notequal( expr );
		}
		else if ( expr.getComparisonOperator( ).tokencode == (int) '>' )
		{
			return eval_gt( expr );
		}
		else if ( expr.getComparisonOperator( ).tokencode == (int) '<' )
		{
			return eval_lt( expr );
		}
		else if ( expr.getComparisonOperator( ).tokencode == TBaseType.less_equal )
		{
			return eval_le( expr );
		}
		else if ( expr.getComparisonOperator( ).tokencode == TBaseType.great_equal )
		{
			return eval_ge( expr );
		}
		else
		{
			exprs.pop( );
			exprs.pop( );
			return new UnknownValue( );
		}

	}

	Object eval_between( TExpression expr ) throws Exception
	{
		Object right = ( (TExpression) exprs.pop( ) ).getVal( );
		Object left = ( (TExpression) exprs.pop( ) ).getVal( );
		GEval v = new GEval( );
		Object between = v.value( expr.getBetweenOperand( ), null );

		if ( ( between instanceof UnknownValue )
				|| ( left instanceof UnknownValue )
				|| ( right instanceof UnknownValue ) )
		{
			return new UnknownValue( );
		}
		else
		{
			return computeFunction( between.toString( )
					+ ">="
					+ left.toString( )
					+ "&&"
					+ between.toString( )
					+ "<="
					+ right.toString( ) );
		}

	}

	Object eval_isnull( TExpression expr ) throws Exception
	{
		Object left = ( (TExpression) exprs.pop( ) ).getVal( );
		if ( left instanceof UnknownValue )
		{
			return new UnknownValue( );
		}
		else
		{
			if ( left == null )
			{
				if ( expr.getOperatorToken( )
						.toString( )
						.equalsIgnoreCase( "not" ) )
				{
					return Boolean.FALSE; // null is not null, return false
				}
				else
				{
					return Boolean.TRUE; // null is null, return true
				}
			}
			else if ( left == Boolean.FALSE )
			{
				if ( expr.getOperatorToken( )
						.toString( )
						.equalsIgnoreCase( "not" ) )
				{
					return Boolean.TRUE; // not null is not null, return false
				}
				else
				{
					return Boolean.FALSE; // not null is null, return false
				}
			}
			else
			{
				return new UnknownValue( );
			}
		}
	}

	Object eval_mod( TExpression expr ) throws Exception
	{
		Object right = ( (TExpression) exprs.pop( ) ).getVal( );
		Object left = ( (TExpression) exprs.pop( ) ).getVal( );

		if ( left == null && right == null )
		{
			return new Byte( (byte) 0 );
		}

		return computeFunction( left.toString( ) + "%" + right.toString( ) );
	}

	Object eval_divide( TExpression expr ) throws Exception
	{
		Object right = ( (TExpression) exprs.pop( ) ).getVal( );
		Object left = ( (TExpression) exprs.pop( ) ).getVal( );
		if ( left == null && right == null )
		{
			return new Byte( (byte) 0 );
		}

		return computeFunction( left.toString( ) + "/" + right.toString( ) );

	}

	Object eval_mul( TExpression expr ) throws Exception
	{
		Object right = ( (TExpression) exprs.pop( ) ).getVal( );
		Object left = ( (TExpression) exprs.pop( ) ).getVal( );

		/*
		 * the spec says 'and', I think 'or'
		 */
		if ( left == null && right == null )
		{
			return new Byte( (byte) 0 );
		}

		return computeFunction( left.toString( ) + "*" + right.toString( ) );
	}

	Object eval_subtract( TExpression expr ) throws Exception
	{
		Object right = ( (TExpression) exprs.pop( ) ).getVal( );
		Object left = ( (TExpression) exprs.pop( ) ).getVal( );

		/*
		 * the spec says 'and', I think 'or'
		 */
		if ( left == null && right == null )
		{
			return new Byte( (byte) 0 );
		}

		return computeFunction( left.toString( ) + "-" + right.toString( ) );

	}

	Object eval_concatenate( TExpression expr ) throws Exception
	{
		Object right = ( (TExpression) exprs.pop( ) ).getVal( );
		Object left = ( (TExpression) exprs.pop( ) ).getVal( );

		return left.toString( ).concat( right.toString( ) );
	}

	Object eval_add( TExpression expr ) throws Exception
	{
		Object right = ( (TExpression) exprs.pop( ) ).getVal( );
		Object left = ( (TExpression) exprs.pop( ) ).getVal( );

		/*
		 * the spec says 'and'
		 */
		if ( left == null && right == null )
		{
			return new Long( 0 );
		}

		return computeFunction( left.toString( ) + "+" + right.toString( ) );
	}

}

class Coercion
{

	/**
	 * Coerce to a Boolean.
	 * 
	 * @param val
	 *            Object to be coerced.
	 * @return The Boolean coerced value, or null if none possible.
	 */
	public static Boolean coerceBoolean( Object val )
	{
		if ( val == null )
		{
			return Boolean.FALSE;
		}
		else if ( val instanceof Boolean )
		{
			return (Boolean) val;
		}
		else if ( val instanceof String )
		{
			return Boolean.valueOf( (String) val );
		}
		else
		{
			return Boolean.valueOf( val.toString( ) );
		}
	}

	/**
	 * Coerce to a Integer.
	 * 
	 * @param val
	 *            Object to be coerced.
	 * @return The Integer coerced value.
	 * @throws Exception
	 *             If Integer coercion fails.
	 */
	public static Long coerceInteger( Object val ) throws Exception
	{
		if ( val == null )
		{
			return new Long( 0 );
		}
		else if ( val instanceof String )
		{
			if ( "".equals( val ) )
			{
				return new Long( 0 );
			}
			return Long.parseLong( (String) val );
		}
		else if ( val instanceof Character )
		{
			return new Long( ( (Character) val ).charValue( ) );
		}
		else if ( val instanceof Boolean )
		{
			throw new Exception( "Boolean->Integer coercion exception" );
		}
		else if ( val instanceof Number )
		{
			return new Long( ( (Number) val ).longValue( ) );
		}

		throw new Exception( "Integer coercion exception" );
	}

	/**
	 * Coerce to a Long.
	 * 
	 * @param val
	 *            Object to be coerced.
	 * @return The Long coerced value.
	 * @throws Exception
	 *             If Long coercion fails.
	 */
	public static Long coerceLong( Object val ) throws Exception
	{
		if ( val == null )
		{
			return new Long( 0 );
		}
		else if ( val instanceof String )
		{
			if ( "".equals( val ) )
			{
				return new Long( 0 );
			}
			return Long.valueOf( (String) val );
		}
		else if ( val instanceof Character )
		{
			return new Long( ( (Character) val ).charValue( ) );
		}
		else if ( val instanceof Boolean )
		{
			throw new Exception( "Boolean->Long coercion exception" );
		}
		else if ( val instanceof Number )
		{
			return new Long( ( (Number) val ).longValue( ) );
		}

		throw new Exception( "Long coercion exception" );
	}

	/**
	 * Coerce to a Double.
	 * 
	 * @param val
	 *            Object to be coerced.
	 * @return The Double coerced value.
	 * @throws Exception
	 *             If Double coercion fails.
	 */
	public static Double coerceDouble( Object val ) throws Exception
	{
		if ( val == null )
		{
			return new Double( 0 );
		}
		else if ( val instanceof String )
		{
			if ( "".equals( val ) )
			{
				return new Double( 0 );
			}

			return new Double( (String) val );
		}
		else if ( val instanceof Character )
		{
			int i = ( (Character) val ).charValue( );

			return new Double( Double.parseDouble( String.valueOf( i ) ) );
		}
		else if ( val instanceof Boolean )
		{
			throw new Exception( "Boolean->Double coercion exception" );
		}
		else if ( val instanceof Double )
		{
			return (Double) val;
		}
		else if ( val instanceof Number )
		{
			return new Double( Double.parseDouble( String.valueOf( val ) ) );
		}

		throw new Exception( "Double coercion exception" );
	}

	/**
	 * Is Object a floating point number.
	 * 
	 * @param o
	 *            Object to be analyzed.
	 * @return true if it is a Float or a Double.
	 */
	public static boolean isFloatingPoint( final Object o )
	{
		return o instanceof Float || o instanceof Double;
	}

	/**
	 * Is Object a whole number.
	 * 
	 * @param o
	 *            Object to be analyzed.
	 * @return true if Integer, Long, Byte, Short or Character.
	 */
	public static boolean isNumberable( final Object o )
	{
		return o instanceof Integer
				|| o instanceof Long
				|| o instanceof Byte
				|| o instanceof Short
				|| o instanceof Character;
	}

}
