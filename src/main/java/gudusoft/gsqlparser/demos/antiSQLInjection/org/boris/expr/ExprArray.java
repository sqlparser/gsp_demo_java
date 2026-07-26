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

import java.util.Arrays;

public class ExprArray extends Expr
{

	private int columns;
	private int rows;
	private Expr[] array;

	public ExprArray( int rows, int columns )
	{
		super( ExprType.Array, false );
		this.array = new Expr[rows * columns];
		this.columns = columns;
		this.rows = rows;
	}

	public ExprArray( Expr[] array )
	{
		super( ExprType.Array, false );
		this.array = array;
		this.columns = array.length;
		this.rows = 1;
	}

	public int rows( )
	{
		return rows;
	}

	public int columns( )
	{
		return columns;
	}

	public int length( )
	{
		return array.length;
	}

	public Expr get( int index )
	{
		return array[index];
	}

	public Expr get( int row, int column )
	{
		return array[row * columns + column];
	}

	public void set( int index, Expr value )
	{
		array[index] = value;
	}

	public void set( int row, int column, Expr value )
	{
		array[row * columns + column] = value;
	}

	public Expr[] getInternalArray( )
	{
		return array;
	}

	public int hashCode( )
	{
		return 567 ^ rows ^ columns ^ array.length;
	}

	public boolean equals( Object obj )
	{
		if ( !( obj instanceof ExprArray ) )
			return false;

		ExprArray a = (ExprArray) obj;
		return a.rows == rows
				&& a.columns == columns
				&& Arrays.equals( a.array, array );
	}
}
