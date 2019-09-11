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

public class Edge
{
    public final Object source;
    public final Object target;
    public final Object data;

    public Edge(Object source, Object target, Object data) {
        this.source = source;
        this.target = target;
        this.data = data;
    }

    public Edge(Object source, Object target) {
        this.source = source;
        this.target = target;
        this.data = null;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Edge))
            return false;

        Edge e = (Edge) obj;
        boolean same = e.source.equals(source) && e.target.equals(target);
        if (!same)
            return false;
        if (data != null) {
            return data.equals(e.data);
        } else if (e.data != null) {
            return e.data.equals(data);
        }
        return true;
    }

    public int hashCode() {
        int h = source.hashCode() ^ target.hashCode();
        if (data != null)
            h ^= data.hashCode();
        return h;
    }

    public String toString() {
        return "[" + source + "," + target + "]";
    }
}
