/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Peter Smith
 *******************************************************************************/
package org.boris.expr.engine;

import org.boris.expr.Expr;
import org.boris.expr.ExprArray;
import org.boris.expr.ExprError;
import org.boris.expr.util.LongMap;

public class GridMap
{
    private LongMap values = new LongMap();

    public void put(Range range, Expr value) {
        GridReference dim1 = range.getDimension1();
        GridReference dim2 = range.getDimension2();
        assert (dim1 != null);
        int x1 = dim1.getColumn();
        int x2 = dim2 == null ? x1 : dim2.getColumn();
        int y1 = dim1.getRow();
        int y2 = dim2 == null ? y1 : dim2.getRow();
        int width = x2 - x1 + 1;
        int height = y2 - y1 + 1;
        assert (width > 0);
        assert (height > 0);
        boolean isArray = value instanceof ExprArray;
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                long k = toId(i, j);
                if (value == null)
                    values.remove(k);
                else
                    values.put(k, isArray ? getElement((ExprArray) value, i -
                            x1, j - y1) : value);
            }
        }
    }

    private Expr getElement(ExprArray array, int x, int y) {
        if (x >= array.columns())
            x = array.columns() - 1;
        if (y >= array.rows())
            y = array.rows() - 1;
        return array.get(y, x);
    }

    public void remove(Range range) {
        put(range, null);
    }

    public Expr get(int x, int y) {
        return (Expr) values.get(toId(x, y));
    }

    public Expr get(Range range) {
        if (range.isArray()) {
            return getArray(range);
        } else {
            GridReference gr = range.getDimension1();
            if (gr == null)
                return ExprError.NAME;
            return get(gr.getColumn(), gr.getRow());
        }
    }

    private Expr getArray(Range range) {
        GridReference dim1 = range.getDimension1();
        GridReference dim2 = range.getDimension2();
        assert (dim1 != null);
        int x1 = dim1.getColumn();
        int x2 = dim2 == null ? x1 : dim2.getColumn();
        int y1 = dim1.getRow();
        int y2 = dim2 == null ? y1 : dim2.getRow();
        int width = x2 - x1 + 1;
        int height = y2 - y1 + 1;
        assert (width > 0);
        assert (height > 0);
        ExprArray arr = new ExprArray(height, width);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                arr.set(j, i, get(x1 + i, y1 + j));
            }
        }

        return arr;
    }

    private long toId(long x, int y) {
        return (x << 32) | y;
    }
}
