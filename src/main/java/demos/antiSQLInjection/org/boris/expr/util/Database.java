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

// A table of data in a grid with named columns
public class Database
{
    private String[] columnNames;
    private Map<String, Expr>[] values;

    public Database(String[] columnNames, Map<String, Expr>[] values) {
        this.columnNames = columnNames;
        this.values = values;
    }

    public int size() {
        return values.length;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public String getColumnName(int index) {
        return columnNames[index];
    }

    public Expr get(int row, String name) {
        return values[row].get(name);
    }

    public static Database valueOf(IEvaluationContext context, ExprArray array)
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
        Map<String, Expr>[] dr = new HashMap[rows - 1];
        for (int i = 1; i < rows; i++) {
            dr[i - 1] = new HashMap();
            for (int j = 0; j < cols; j++) {
                dr[i - 1].put(dc[j], array.get(i, j));
            }
        }

        return new Database(dc, dr);
    }
}
