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


public class ExprMissing extends Expr
{
    public static final ExprMissing MISSING = new ExprMissing();

    public ExprMissing() {
        super(ExprType.Missing, false);
    }

    public String toString() {
        return "";
    }
}
