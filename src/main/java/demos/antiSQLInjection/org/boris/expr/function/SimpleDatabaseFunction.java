/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Peter Smith
 *******************************************************************************/
package org.boris.expr.function;

import java.util.ArrayList;
import java.util.List;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.util.Criteria;
import org.boris.expr.util.Database;

public abstract class SimpleDatabaseFunction extends AbstractDatabaseFunction
{
    protected final Expr evaluate(IEvaluationContext context, Database db,
            String field, Criteria criteria) throws ExprException {
        List<Expr> results = new ArrayList();
        for (int i = 0; i < db.size(); i++) {
            if (criteria.matches(db, i)) {
                results.add(db.get(i, field));
            }
        }

        return evaluateMatches(context, results.toArray(new Expr[0]));
    }

    protected abstract Expr evaluateMatches(IEvaluationContext context,
            Expr[] matches) throws ExprException;
}
