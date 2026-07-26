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

public class ExprToken
{
    public static final ExprToken OPEN_BRACKET = new ExprToken(
            ExprTokenType.OpenBracket, "(");
    public static final ExprToken CLOSE_BRACKET = new ExprToken(
            ExprTokenType.CloseBracket, ")");
    public static final ExprToken PLUS = new ExprToken(ExprTokenType.Plus, "+");
    public static final ExprToken MINUS = new ExprToken(ExprTokenType.Minus,
            "-");
    public static final ExprToken MULTIPLY = new ExprToken(
            ExprTokenType.Multiply, "*");
    public static final ExprToken DIVIDE = new ExprToken(ExprTokenType.Divide,
            "/");
    public static final ExprToken COMMA = new ExprToken(ExprTokenType.Comma,
            ",");
    public static final ExprToken STRING_CONCAT = new ExprToken(
            ExprTokenType.StringConcat, "&");
    public static final ExprToken LESS_THAN = new ExprToken(
            ExprTokenType.LessThan, "<");
    public static final ExprToken LESS_THAN_EQUAL = new ExprToken(
            ExprTokenType.LessThanOrEqualTo, "<=");
    public static final ExprToken GREATER_THAN = new ExprToken(
            ExprTokenType.GreaterThan, ">");
    public static final ExprToken GREATER_THAN_EQUAL = new ExprToken(
            ExprTokenType.GreaterThanOrEqualTo, ">=");
    public static final ExprToken NOT_EQUAL = new ExprToken(
            ExprTokenType.NotEqual, "<>");
    public static final ExprToken EQUAL = new ExprToken(ExprTokenType.Equal,
            "=");
    public static final ExprToken OPEN_BRACE = new ExprToken(
            ExprTokenType.OpenBrace, "{");
    public static final ExprToken CLOSE_BRACE = new ExprToken(
            ExprTokenType.CloseBrace, "}");
    public static final ExprToken SEMI_COLON = new ExprToken(
            ExprTokenType.SemiColon, ";");
    public static final ExprToken POWER = new ExprToken(ExprTokenType.Power,
            "^");

    public final ExprTokenType type;
    public final String val;
    public final double doubleValue;
    public final int integerValue;

    public ExprToken(ExprTokenType type, String val) {
        this.type = type;
        this.val = val;
        this.doubleValue = 0.;
        this.integerValue = 0;
    }

    public ExprToken(String val, double doubleValue) {
        this.type = ExprTokenType.Decimal;
        this.val = val;
        this.doubleValue = doubleValue;
        this.integerValue = 0;
    }

    public ExprToken(String val, int integerValue) {
        this.type = ExprTokenType.Integer;
        this.val = val;
        this.doubleValue = 0.;
        this.integerValue = integerValue;
    }

    public String toString() {
        return type.toString() + ":" + val;
    }
}
