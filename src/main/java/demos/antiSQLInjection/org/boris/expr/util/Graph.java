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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph implements Iterable
{
    private boolean wantsEdges = true;
    private Set nodes = new HashSet();
    private Set edges = new HashSet();
    private Map outbounds = new HashMap();
    private Map inbounds = new HashMap();
    private List ordered = null;
    private Set traversed = null;

    public void setIncludeEdges(boolean include) {
        this.wantsEdges = include;
    }

    public void add(Object node) {
        nodes.add(node);
    }

    public Set getInbounds(Object node) {
        return (Set) inbounds.get(node);
    }

    public Set getOutbounds(Object node) {
        return (Set) outbounds.get(node);
    }

    public void clearOutbounds(Object node) {
        Set s = (Set) outbounds.get(node);
        if (s != null) {
            Iterator i = s.iterator();
            while (i.hasNext())
                remove((Edge) i.next());
        }
    }

    public void clearInbounds(Object node) {
        Set s = (Set) inbounds.get(node);
        if (s != null) {
            Iterator i = s.iterator();
            while (i.hasNext())
                remove((Edge) i.next());
        }
    }

    public void remove(Object node) {
        nodes.remove(node);
        clearInbounds(node);
        clearOutbounds(node);
    }

    public void add(Edge e) throws GraphCycleException {
        checkCycle(e);
        nodes.add(e.source);
        nodes.add(e.target);
        edges.add(e);
        Set in = (Set) inbounds.get(e.target);
        if (in == null)
            inbounds.put(e.target, in = new HashSet());
        in.add(e);
        Set out = (Set) outbounds.get(e.source);
        if (out == null)
            outbounds.put(e.source, out = new HashSet());
        out.add(e);
    }

    public void checkCycle(Edge e) throws GraphCycleException {
        HashSet visited = new HashSet();
        visited.add(e.source);
        checkCycle(e, visited);
    }

    private void checkCycle(Edge e, HashSet visited) throws GraphCycleException {
        if (visited.contains(e.target)) {
            throw new GraphCycleException("Circular reference found: " +
                    e.source + " - " + e.target);
        }
        visited.add(e.target);
        Set out = (Set) outbounds.get(e.target);
        if (out != null) {
            Iterator i = out.iterator();
            while (i.hasNext()) {
                checkCycle((Edge) i.next(), visited);
            }
        }
    }

    public void remove(Edge e) {
        edges.remove(e);
        Set in = (Set) inbounds.get(e.target);
        if (in != null)
            in.remove(e);
        Set out = (Set) outbounds.get(e.source);
        if (out != null)
            out.remove(e);
    }

    public void sort() {
        ordered = new ArrayList();
        traversed = new HashSet();
        Iterator i = nodes.iterator();
        Set remains = new HashSet(nodes);

        // First traverse nodes without inbounds
        while (i.hasNext()) {
            Object o = i.next();
            Set in = (Set) inbounds.get(o);
            if (in == null || in.isEmpty()) {
                traverse(o);
                remains.remove(o);
            }
        }

        // Now traverse the rest
        i = remains.iterator();
        while (i.hasNext()) {
            Object o = i.next();
            if (!traversed.contains(o)) {
                traverse(o);
            }
        }
    }

    private void traverse(Object node) {
        Set in = (Set) inbounds.get(node);
        if (in != null) {
            Iterator i = in.iterator();

            // if all inbounds haven't been traversed we must stop
            while (i.hasNext()) {
                Edge e = (Edge) i.next();
                if (!traversed.contains(e.source))
                    return;
                else if (wantsEdges)
                    ordered.add(e);

            }
        }

        if (!traversed.contains(node)) {
            traversed.add(node);
            ordered.add(node);
        }

        Set out = (Set) outbounds.get(node);
        if (out == null || out.isEmpty()) {
            return;
        }

        Set avoid = new HashSet();

        Iterator i = out.iterator();
        while (i.hasNext()) {
            Edge e = (Edge) i.next();
            if (!traversed.contains(e)) {
                if (traversed.contains(e.target)) {
                    avoid.add(e.target);
                }
            }
        }

        i = out.iterator();
        while (i.hasNext()) {
            Object n = ((Edge) i.next()).target;
            if (!avoid.contains(n)) {
                traverse(n);
            }
        }
    }

    public void clear() {
        edges.clear();
        inbounds.clear();
        outbounds.clear();
        nodes.clear();
        traversed = null;
        ordered.clear();
    }

    public Iterator iterator() {
        if (ordered == null)
            sort();
        return ordered.iterator();
    }

    public void traverse(Object node, GraphTraversalListener listener) {
        HashSet subgraph = new HashSet();
        walk(node, subgraph);
        HashSet hs = new HashSet();
        hs.add(node);
        traverse(node, listener, hs, subgraph);
    }

    private void walk(Object node, Set traversed) {
        traversed.add(node);
        Set out = (Set) outbounds.get(node);
        if (out != null) {
            Iterator i = out.iterator();
            while (i.hasNext()) {
                Edge e = (Edge) i.next();
                walk(e.target, traversed);
            }
        }
    }

    private void traverse(Object node, GraphTraversalListener listener,
            Set traversed, Set subgraph) {
        Set edges = (Set) outbounds.get(node);
        if (edges != null) {
            Iterator i = edges.iterator();
            while (i.hasNext()) {
                Edge e = (Edge) i.next();
                Set ins = (Set) inbounds.get(e.target);
                Iterator j = ins.iterator();
                boolean traverse = true;
                while (j.hasNext()) {
                    Edge in = (Edge) j.next();
                    if (subgraph.contains(in.source) &&
                            !traversed.contains(in.source) &&
                            !node.equals(in.source)) {
                        traverse = false;
                        break;
                    }
                }
                if (traverse) {
                    listener.traverse(e.target);
                    traversed.add(e.target);
                    traverse(e.target, listener, traversed, subgraph);
                }
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator i = edges.iterator();
        while (i.hasNext()) {
            sb.append(i.next());
            sb.append("\n");
        }
        return sb.toString();
    }
}
