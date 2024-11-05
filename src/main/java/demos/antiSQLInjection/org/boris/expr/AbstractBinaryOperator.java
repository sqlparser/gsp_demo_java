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

import org.boris.expr.util.Reflect;

public abstract class AbstractBinaryOperator extends ExprEvaluatable implements
        IBinaryOperator
{
    protected Expr lhs;
    protected Expr rhs;

    public AbstractBinaryOperator(ExprType type, Expr lhs, Expr rhs) {
        super(type);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Expr getLHS() {
        return lhs;
    }

    public void setLHS(Expr lhs) {
        this.lhs = lhs;
    }

    public Expr getRHS() {
        return rhs;
    }

    public void setRHS(Expr rhs) {
        this.rhs = rhs;
    }

    public void validate() throws ExprException {
        if (lhs == null)
            throw new ExprException("LHS of operator missing");
        lhs.validate();
        if (rhs == null)
            throw new ExprException("RHS of operator missing");
        rhs.validate();
    }

    public int hashCode() {
        int hc = type.ordinal();
        if (lhs != null)
            hc ^= lhs.hashCode();
        if (rhs != null)
            hc ^= rhs.hashCode();
        return hc;
    }

    public boolean equals(Object obj) {
        if (!obj.getClass().equals(getClass()))
            return false;

        AbstractBinaryOperator b = (AbstractBinaryOperator) obj;
        return Reflect.equals(b.lhs, lhs) && Reflect.equals(b.rhs, rhs);
    }
}
