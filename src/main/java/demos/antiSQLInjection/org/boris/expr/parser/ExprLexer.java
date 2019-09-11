/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Peter Smith
 *******************************************************************************/

package org.boris.expr.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class ExprLexer
{

	private TokenReader reader;
	private int lastChar;

	public ExprLexer( BufferedReader reader )
	{
		this.reader = new TokenReader( reader );
	}

	public ExprLexer( Reader reader )
	{
		this( new BufferedReader( reader ) );
	}

	public ExprLexer( String str )
	{
		this( new StringReader( str ) );
	}

	public ExprToken next( ) throws IOException
	{
		if ( lastChar == 0 || Character.isWhitespace( lastChar ) )
			lastChar = reader.ignoreWhitespace( );

		return readToken( );
	}

	private ExprToken readToken( ) throws IOException
	{
		if ( Character.isDigit( lastChar ) || lastChar == '.' )
		{
			return readNumber( );
		}

		switch ( lastChar )
		{
		// case '\"':
		// return readString();
			case '(' :
				lastChar = 0;
				return ExprToken.OPEN_BRACKET;
			case ')' :
				lastChar = 0;
				return ExprToken.CLOSE_BRACKET;
			case '+' :
				lastChar = 0;
				return ExprToken.PLUS;
			case '-' :
				lastChar = 0;
				return ExprToken.MINUS;
			case '*' :
				lastChar = 0;
				return ExprToken.MULTIPLY;
			case '/' :
				lastChar = 0;
				return ExprToken.DIVIDE;
			case ',' :
				lastChar = 0;
				return ExprToken.COMMA;
			case '&' :
				lastChar = 0;
				return ExprToken.STRING_CONCAT;
			case '<' :
			case '>' :
			case '=' :
				return readComparisonOperator( );
			case '\'' :
				return readString( );
			case '{' :
				lastChar = 0;
				return ExprToken.OPEN_BRACE;
			case '}' :
				lastChar = 0;
				return ExprToken.CLOSE_BRACE;
			case ';' :
				lastChar = 0;
				return ExprToken.SEMI_COLON;
			case '^' :
				lastChar = 0;
				return ExprToken.POWER;
			case -1 :
			case 0xffff :
				return null;
		}

		if ( !Character.isJavaIdentifierStart( lastChar ) )
		{
			throw new IOException( "Invalid token found: " + lastChar );
		}

		return readVariableOrFunction( );
	}

	private ExprToken readQuotedVariable( ) throws IOException
	{
		StringBuilder sb = new StringBuilder( );

		sb.append( '\'' );
		while ( lastChar != -1 )
		{
			lastChar = reader.read( );
			if ( lastChar == '\'' )
			{
				lastChar = reader.read( );
				if ( lastChar == '\'' )
				{
					sb.append( '\'' );
				}
				else
				{
					break;
				}
			}
			else
			{
				sb.append( (char) lastChar );
			}
		}
		sb.append( '\'' );

		// Now read the rest of the variable
		while ( isVariablePart( lastChar ) )
		{
			sb.append( (char) lastChar );
			lastChar = reader.read( );
		}

		return new ExprToken( ExprTokenType.Variable, sb.toString( ) );
	}

	private ExprToken readVariableOrFunction( ) throws IOException
	{
		StringBuilder sb = new StringBuilder( );

		int beforeChar = 0;
		while ( isVariablePart( lastChar, beforeChar ) )
		{
			sb.append( (char) lastChar );
			beforeChar = lastChar;
			lastChar = reader.read( );
		}

		if ( Character.isWhitespace( lastChar ) )
		{
			lastChar = reader.ignoreWhitespace( );
		}

		boolean isNot = false;
		String not = null;

		if ( sb.toString( ).equalsIgnoreCase( "not" ) )
		{
			isNot = true;
			not = sb.toString( );
			sb = new StringBuilder( );
			while ( isVariablePart( lastChar ) )
			{
				sb.append( (char) lastChar );
				lastChar = reader.read( );
			}

			if ( Character.isWhitespace( lastChar ) )
			{
				lastChar = reader.ignoreWhitespace( );
			}
		}

		if ( lastChar == '(' )
		{
			lastChar = 0;
			if ( sb.toString( ).equalsIgnoreCase( "in" ) )
			{
				if ( isNot )
					return new ExprToken( ExprTokenType.NotIn, not
							+ " "
							+ sb.toString( ) );
				else
					return new ExprToken( ExprTokenType.In, sb.toString( ) );
			}
			return new ExprToken( ExprTokenType.Function, sb.toString( ) );
		}
		else
		{
			if ( sb.toString( ).equalsIgnoreCase( "as" ) )
				return new ExprToken( ExprTokenType.As, sb.toString( ) );
			else
				return new ExprToken( ExprTokenType.Variable, sb.toString( ) );
		}
	}

	private boolean isVariablePart( int lastChar )
	{
		return Character.isJavaIdentifierPart( lastChar )
				|| lastChar == '!'
				|| lastChar == ':';
	}

	private boolean isVariablePart( int lastChar, int beforeChar )
	{
		return Character.isJavaIdentifierPart( lastChar )
				|| lastChar == '!'
				|| lastChar == ':'
				|| ( isVariablePart( beforeChar ) && lastChar == '.' );
	}

	private ExprToken readString( ) throws IOException
	{
		String str = unescapeString( reader );
		lastChar = 0;
		return new ExprToken( ExprTokenType.String, str );
	}

	private ExprToken readNumber( ) throws IOException
	{
		StringBuilder sb = new StringBuilder( ); // Todo, more efficient number
		// builder (ie. shift bits)
		sb.append( (char) lastChar );
		lastChar = reader.read( );
		boolean decimal = false;
		while ( Character.isDigit( lastChar ) || '.' == lastChar )
		{
			sb.append( (char) lastChar );
			if ( lastChar == '.' )
				decimal = true;
			lastChar = reader.read( );
		}

		if ( lastChar == 'E' || lastChar == 'e' )
		{
			sb.append( (char) lastChar );
			lastChar = reader.read( );
			if ( lastChar == '-' || lastChar == '+' )
			{
				sb.append( (char) lastChar );
				lastChar = reader.read( );
			}
			while ( Character.isDigit( lastChar ) )
			{
				sb.append( (char) lastChar );
				lastChar = reader.read( );
			}
		}

		String val = sb.toString( );
		if ( decimal )
		{
			return new ExprToken( val, Double.parseDouble( val ) );
		}
		else
		{
			try
			{
				return new ExprToken( val, Integer.parseInt( val ) );
			}
			catch ( NumberFormatException e )
			{
				// Catch very large numbers
				return new ExprToken( val, Double.parseDouble( val ) );
			}
		}
	}

	public static String escapeString( String str )
	{
		StringBuilder sb = new StringBuilder( );
		int len = str.length( );
		for ( int i = 0; i < len; i++ )
		{
			char c = str.charAt( i );
			switch ( c )
			{
				case '\"' :
					sb.append( "\"\"" );
					break;
				default :
					sb.append( c );
					break;
			}
		}
		return sb.toString( );
	}

	private String unescapeString( TokenReader r ) throws IOException
	{
		StringBuilder sb = new StringBuilder( );
		char c = 0;
		while ( c != '\'' )
		{
			c = (char) r.read( );
			switch ( c )
			{
				case '\'' :
					int v = r.peek( );
					if ( v == '\'' )
					{
						r.read( );
						sb.append( '\'' );
						c = 0;
					}
					break;
				default :
					sb.append( c );
					break;
			}
		}
		return sb.toString( );
	}

	private ExprToken readComparisonOperator( ) throws IOException
	{
		int current = lastChar;
		int peek = reader.peek( );
		lastChar = 0;
		if ( current == '<' )
		{
			if ( peek == '=' )
			{
				reader.read( );
				return ExprToken.LESS_THAN_EQUAL;
			}
			else if ( peek == '>' )
			{
				reader.read( );
				return ExprToken.NOT_EQUAL;
			}
			else
			{
				return ExprToken.LESS_THAN;
			}
		}
		else if ( current == '>' )
		{
			if ( peek == '=' )
			{
				reader.read( );
				return ExprToken.GREATER_THAN_EQUAL;
			}
			else
			{
				return ExprToken.GREATER_THAN;
			}
		}
		else if ( current == '=' )
		{
			return ExprToken.EQUAL;
		}
		return null;
	}
}
