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

public enum ExprTokenType
{
    Decimal,
    Integer,
    String,
    Variable,
    Function,
    Plus,
    Minus,
    Multiply,
    Divide,
    OpenBracket,
    CloseBracket,
    Comma,
    StringConcat,
    GreaterThan,
    GreaterThanOrEqualTo,
    LessThan,
    LessThanOrEqualTo,
    NotEqual,
    Equal,
    OpenBrace,
    CloseBrace,
    SemiColon,
    Power,
    In,
    NotIn,
    As
}
