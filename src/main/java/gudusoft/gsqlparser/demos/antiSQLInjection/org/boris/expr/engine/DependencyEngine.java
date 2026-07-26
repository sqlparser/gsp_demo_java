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

import java.util.Iterator;

import org.boris.expr.Expr;
import org.boris.expr.ExprEvaluatable;
import org.boris.expr.ExprException;
import org.boris.expr.ExprVariable;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.parser.IParserVisitor;
import org.boris.expr.util.Edge;
import org.boris.expr.util.Graph;
import org.boris.expr.util.GraphCycleException;
import org.boris.expr.util.GraphTraversalListener;

public class DependencyEngine extends AbstractCalculationEngine implements
        IParserVisitor, IEvaluationContext, GraphTraversalListener
{
    private Graph graph = new Graph();

    public DependencyEngine(EngineProvider provider) {
        super(provider);
        this.graph.setIncludeEdges(false);
    }

    public void calculate(boolean force) throws ExprException {
        if (autoCalculate && !force)
            return;

        graph.sort();
        Iterator i = graph.iterator();
        while (i.hasNext()) {
            Range r = (Range) i.next();
            Expr input = inputs.get(r);
            if (input instanceof ExprEvaluatable) {
                Expr eval = ((ExprEvaluatable) input).evaluate(this);
                provider.valueChanged(r, eval);
                values.put(r, eval);
            }
        }
    }

    public void set(Range range, String expression) throws ExprException {
        validateRange(range);

        // If null then remove all references
        if (expression == null) {
            rawInputs.remove(range);
            values.remove(range);
            inputs.remove(range);
            updateDependencies(range, null);
            return;
        }

        rawInputs.put(range, expression);

        Expr expr = parseExpression(expression);

        // Update the dependency graph
        updateDependencies(range, expr);

        // Set the inputs
        provider.inputChanged(range, expr);
        inputs.put(range, expr);

        // Always evaluate the expression entered
        if (expr.evaluatable) {
            Expr eval = ((ExprEvaluatable) expr).evaluate(this);
            provider.valueChanged(range, eval);
            values.put(range, eval);
        } else {
            provider.valueChanged(range, expr);
            values.put(range, expr);
        }

        // Recalculate the dependencies if required
        if (autoCalculate) {
            graph.traverse(range, this);
        }
    }

    private void updateDependencies(Range range, Expr expr)
            throws ExprException {
        graph.clearInbounds(range);
        ExprVariable[] vars = ExprVariable.findVariables(expr);
        for (ExprVariable var : vars) {
            Range source = (Range) var.getAnnotation();
            try {
                addDependencies(source, range);
            } catch (GraphCycleException ex) {
                for (ExprVariable v : vars) {
                    removeDependencies((Range) v.getAnnotation(), range);
                }
                throw new ExprException(ex);
            }
        }
    }

    private void addDependencies(Range source, Range target)
            throws GraphCycleException {
        if (source.isArray()) {
            Range[] r = source.split();
            for (Range rs : r) {
                graph.add(new Edge(rs, target));
            }
        } else {
            graph.add(new Edge(source, target));
        }
    }

    private void removeDependencies(Range source, Range target) {
        if (source.isArray()) {
            Range[] r = source.split();
            for (Range rs : r) {
                graph.remove(new Edge(rs, target));
            }
        } else {
            graph.remove(new Edge(source, target));
        }
    }

    public void traverse(Object node) {
        // FIXME : broken on range dependencies - need to think about
        // a range element pointing to an element of a calced ExprArray...
        Range r = (Range) node;
        Expr input = inputs.get(r);
        if (input instanceof ExprEvaluatable) {
            try {
                Expr eval = ((ExprEvaluatable) input).evaluate(this);
                provider.valueChanged(r, eval);
                values.put(r, eval);
            } catch (ExprException e) {
                e.printStackTrace();
                // TODO: handle
            }
        }
    }
}
