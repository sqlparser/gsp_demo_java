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


public class ExprInteger extends ExprNumber
{
    public static final ExprInteger ZERO = new ExprInteger(0);

    public final int value;

    public ExprInteger(int value) {
        super(ExprType.Integer);
        this.value = value;
    }

    public int intValue() {
        return value;
    }

    public double doubleValue() {
        return value;
    }

    public int hashCode() {
        return value;
    }

    public boolean equals(Object obj) {
        return obj instanceof ExprInteger && value == ((ExprInteger) obj).value;
    }

    public String toString() {
        return Integer.toString(value);
    }
}
