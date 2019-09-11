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

public class ExprTypes
{
    public static void assertType(Expr value, ExprType... types)
            throws ExprException {
        for (ExprType t : types) {
            if (t.equals(value.type))
                return;
        }
        throw new ExprException("Unexpected type: " + value.type);
    }
}
