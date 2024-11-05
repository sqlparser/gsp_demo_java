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


public class ExprDouble extends ExprNumber
{
    public static final ExprDouble ZERO = new ExprDouble(0);
    public static final ExprDouble PI = new ExprDouble(Math.PI);
    public static final ExprDouble E = new ExprDouble(Math.E);

    public final double value;

    public ExprDouble(double value) {
        super(ExprType.Double);
        this.value = value;
    }

    public int intValue() {
        return (int) value;
    }

    public double doubleValue() {
        return value;
    }

    public String toString() {
        return Double.toString(value);
    }

    public int hashCode() {
        return (int) value * 100;
    }

    public boolean equals(Object obj) {
        return obj instanceof ExprDouble &&
                Math.abs(value - ((ExprDouble) obj).value) < 1.0e-10;
    }
}
