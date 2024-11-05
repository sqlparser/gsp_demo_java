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

public enum ExprType
{
    Double,
    Integer,
    Boolean,
    String,
    Addition,
    Subtraction,
    Multiplication,
    Division,
    Function,
    Variable,
    Expression,
    StringConcat,
    Error,
    Array,
    Missing,
    LessThan,
    LessThanOrEqualTo,
    GreaterThan,
    GreaterThanOrEqualTo,
    NotEqual,
    Equal,
    Power,
    Unary,
    In
}
