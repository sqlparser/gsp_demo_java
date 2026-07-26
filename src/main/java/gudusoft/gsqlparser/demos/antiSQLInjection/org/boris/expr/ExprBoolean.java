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


public class ExprBoolean extends ExprNumber
{
    public static final ExprBoolean TRUE = new ExprBoolean(true);
    public static final ExprBoolean FALSE = new ExprBoolean(false);

    public final boolean value;

    public ExprBoolean(boolean value) {
        super(ExprType.Boolean);
        this.value = value;
    }

    public boolean booleanValue() {
        return value;
    }

    public double doubleValue() {
        return intValue();
    }

    public int intValue() {
        return value ? 1 : 0;
    }

    public int hashCode() {
        return value ? 1 : 0;
    }

    public boolean equals(Object obj) {
        return obj instanceof ExprBoolean && value == ((ExprBoolean) obj).value;
    }

    public String toString() {
        return Boolean.toString(value).toUpperCase();
    }
}
