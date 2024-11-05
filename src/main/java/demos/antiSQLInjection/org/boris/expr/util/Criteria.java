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

import java.util.HashMap;
import java.util.Map;

import org.boris.expr.Expr;
import org.boris.expr.ExprArray;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;

public class Criteria
{
    private String[] columnNames;
    private Map<String, Condition>[] conditions;

    public Criteria(String[] columnNames, Map<String, Condition>[] values) {
        this.columnNames = columnNames;
        this.conditions = values;
    }

    public int size() {
        return conditions.length;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public String getColumnName(int index) {
        return columnNames[index];
    }

    public static Criteria valueOf(IEvaluationContext context, ExprArray array)
            throws ExprException {
        int rows = array.rows();
        int cols = array.columns();
        if (rows < 2 || cols < 1) {
            return null;
        }

        // Collect column names
        String[] dc = new String[cols];
        for (int i = 0; i < dc.length; i++) {
            dc[i] = Exprs.getString(context, array.get(0, i));
        }

        // Generate map of column name to value for each row
        Map<String, Condition>[] dr = new HashMap[rows - 1];
        for (int i = 1; i < rows; i++) {
            dr[i - 1] = new HashMap();
            for (int j = 0; j < cols; j++) {
                Expr e = array.get(i, j);
                Condition c = dr[i - 1].get(dc[j]);
                if (c != null) {
                    c.add(Condition.valueOf(e));
                } else {
                    dr[i - 1].put(dc[j], Condition.valueOf(e));
                }
            }
        }

        return new Criteria(dc, dr);
    }

    public boolean matches(Database db, int row) throws ExprException {
        for (int i = 0; i < conditions.length; i++) {
            boolean res = true;
            for (String key : conditions[i].keySet()) {
                Condition c = conditions[i].get(key);
                if (c != null && !c.eval(db.get(row, key))) {
                    res = false;
                    break;
                }
            }
            if (res)
                return true;
        }
        return false;
    }
}
