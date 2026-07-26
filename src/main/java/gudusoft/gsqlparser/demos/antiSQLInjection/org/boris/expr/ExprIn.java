/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Peter Smith
 *******************************************************************************/

package org.boris.expr;

public class ExprIn extends AbstractBinaryOperator
{

	private String name;
	private ExprArray rhs;
	private boolean isNot;

	public ExprIn( String name, Expr lhs, ExprArray rhs, boolean not )
	{
		super( ExprType.In, lhs, rhs );
		this.rhs = rhs;
		this.name = name;
		this.isNot = not;
	}

	public String toString( )
	{
		StringBuilder sb = new StringBuilder( );
		sb.append( lhs.toString( ) ).append( " " );
		sb.append( name );
		sb.append( " (" );
		for ( int i = 0; i < ( (ExprArray) rhs ).length( ); i++ )
		{
			if ( i > 0 )
				sb.append( "," );
			sb.append( ( (ExprArray) rhs ).get( i ) );
		}
		sb.append( ")" );
		return sb.toString( );
	}

	public Expr evaluate( IEvaluationContext context ) throws ExprException
	{
		for ( int i = 0; i < rhs.length( ); i++ )
		{
			boolean result = compare( context, lhs, rhs.get( i ) );
			if ( result )
				return bool( isNot ? !result : result );
		}
		return bool( isNot ? true : false );
	}

	protected boolean compare( IEvaluationContext context, Expr lhs, Expr rhs )
			throws ExprException
	{
		Expr l = eval( lhs, context );
		Expr r = eval( rhs, context );

		if ( ( l != null && r != null )
				&& ( l instanceof ExprString || r instanceof ExprString ) )
		{
			return l.toString( ).compareTo( r.toString( ) ) == 0;
		}

		if ( l instanceof ExprNumber && r instanceof ExprNumber )
		{
			return ( ( (ExprNumber) l ).doubleValue( ) - ( (ExprNumber) r ).doubleValue( ) ) == 0;
		}

		throw new ExprException( );
	}
}
