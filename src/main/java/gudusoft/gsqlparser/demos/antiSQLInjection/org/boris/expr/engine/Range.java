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

import org.boris.expr.ExprArray;
import org.boris.expr.ExprException;
import org.boris.expr.util.Reflect;

public class Range
{
    private String namespace;
    private String dim1Name;
    private GridReference dim1;
    private String dim2Name;
    private GridReference dim2;

    public Range(String namespace, GridReference dim1) {
        this.namespace = namespace;
        this.dim1 = dim1;
    }

    public Range(String namespace, GridReference dim1, GridReference dim2) {
        this.namespace = namespace;
        this.dim1 = dim1;
        this.dim2 = dim2;
    }

    public Range(String namespace, String dim1Name, GridReference dim1,
            String dim2Name, GridReference dim2) {
        this.namespace = namespace;
        this.dim1Name = dim1Name;
        this.dim1 = dim1;
        this.dim2Name = dim2Name;
        this.dim2 = dim2;
    }

    public boolean isArray() {
        return dim2 != null;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getDimension1Name() {
        return dim1Name;
    }

    public GridReference getDimension1() {
        return dim1;
    }

    public void setDimension1(GridReference dim1) {
        this.dim1 = dim1;
    }

    public String getDimension2Name() {
        return dim2Name;
    }

    public GridReference getDimension2() {
        return dim2;
    }

    public void setDimension2(GridReference dim2) {
        this.dim2 = dim2;
    }

    public int hashCode() {
        int hc = 0;
        hc = namespace == null ? 56 : namespace.hashCode();
        if (dim1 != null)
            hc ^= dim1.hashCode();
        else if (dim1Name != null)
            hc ^= dim1Name.hashCode();
        if (dim2 != null)
            hc ^= dim2.hashCode();
        else if (dim2Name != null)
            hc ^= dim2Name.hashCode();
        return hc;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Range))
            return false;
        Range r = (Range) obj;
        if (!Reflect.equals(r.namespace, namespace))
            return false;
        if (!Reflect.equals(r.dim1, dim1))
            return false;
        if (!Reflect.equals(r.dim2, dim2))
            return false;
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (namespace != null) {
            sb.append(namespace);
            sb.append('!');
        }

        if (dim1Name != null) {
            sb.append(dim1Name);
        } else {
            sb.append(dim1);
        }

        if (dim2Name != null) {
            sb.append(':');
            sb.append(dim2Name);
        } else if (dim2 != null) {
            sb.append(':');
            sb.append(dim2);
        }

        return sb.toString();
    }

    public static Range valueOf(String var) throws ExprException {
        if (var == null) {
            throw new ExprException("Invalid empty variable name");
        }
        String namespace = null;
        int ni = var.indexOf('!');
        if (ni != -1) {
            namespace = var.substring(0, ni);
            if (!namespace.startsWith("'") &&
                    !GridReference.isValidVariable(namespace)) {
                throw new ExprException("Invalid namespace: " + namespace);
            }
            var = var.substring(ni + 1);
        }

        String dim1Name = null;
        GridReference dim1 = null;
        String dim2Name = null;
        GridReference dim2 = null;

        int di = var.indexOf(':');
        if (di != -1) {
            dim1Name = var.substring(0, di);
            dim1 = GridReference.valueOf(dim1Name);
            if (dim1 == null && !GridReference.isValidVariable(dim1Name)) {
                throw new ExprException("Invalid variable: " + dim1Name);
            }
            dim2Name = var.substring(di + 1);
            dim2 = GridReference.valueOf(dim2Name);
            if (dim2 == null && !GridReference.isValidVariable(dim2Name)) {
                throw new ExprException("Invalid variable: " + dim2Name);
            }
        } else {
            dim1Name = var;
            dim1 = GridReference.valueOf(var);
            if (dim1 == null && !GridReference.isValidVariable(dim1Name)) {
                throw new ExprException("Invalid variable: " + dim1Name);
            }

        }

        return new Range(namespace, dim1Name, dim1, dim2Name, dim2);
    }

    public static Range toRange(ExprArray array, GridReference offset) {
        if (offset == null) {
            offset = new GridReference(1, 1);
        }

        GridReference dim2 = new GridReference(offset.getColumn() +
                array.columns() - 1, offset.getRow() + array.rows() - 1);

        return new Range(null, offset, dim2);
    }

    public Range[] split() {
        int x1 = dim1.getColumn();
        int x2 = dim2 == null ? x1 : dim2.getColumn();
        int y1 = dim1.getRow();
        int y2 = dim2 == null ? y1 : dim2.getRow();
        int width = x2 - x1 + 1;
        int height = y2 - y1 + 1;
        assert (width > 0 && height > 0);
        Range[] arr = new Range[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                arr[i * width + j] = new Range(namespace, new GridReference(j +
                        x1, i + y1));
            }
        }
        return arr;
    }
}
