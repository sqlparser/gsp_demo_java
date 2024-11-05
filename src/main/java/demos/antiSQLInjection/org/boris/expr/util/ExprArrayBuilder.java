/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Peter Smith
 *******************************************************************************/
package org.boris.expr.util;

import java.util.ArrayList;
import java.util.List;

import org.boris.expr.Expr;
import org.boris.expr.ExprArray;
import org.boris.expr.ExprMissing;

public class ExprArrayBuilder
{
    private int cols = -1;
    private int rows = 0;
    private List<Expr> values = new ArrayList();

    public void addRow(Expr[] vals) {
        if (cols == -1) {
            cols = vals.length;
        }

        for (int i = 0; i < cols; i++) {
            if (i < vals.length) {
                values.add(vals[i]);
            } else {
                values.add(ExprMissing.MISSING);
            }
        }

        rows++;
    }

    public ExprArray toArray() {
        ExprArray a = new ExprArray(rows, cols);
        for (int i = 0; i < a.length(); i++) {
            a.set(i, values.get(i));
        }
        return a;
    }
}
