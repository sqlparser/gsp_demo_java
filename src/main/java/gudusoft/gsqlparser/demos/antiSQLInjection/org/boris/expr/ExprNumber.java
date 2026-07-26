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

public abstract class ExprNumber extends Expr
{
    ExprNumber(ExprType type) {
        super(type, false);
    }

    public void validate() throws ExprException {
    }

    public boolean booleanValue() {
        return intValue() != 0;
    }

    public abstract int intValue();

    public abstract double doubleValue();
}
