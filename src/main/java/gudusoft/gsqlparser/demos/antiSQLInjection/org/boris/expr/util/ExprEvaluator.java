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

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.boris.expr.Expr;
import org.boris.expr.ExprEvaluatable;
import org.boris.expr.parser.ExprParser;

public class ExprEvaluator
{
    public static void main(String[] args) throws Exception {
        SimpleEvaluationContext context = new SimpleEvaluationContext();
        System.out.println("Expr Evaluator v1.0");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                System.out.print(">");
                String line = br.readLine();
                if (line == null)
                    break;
                Expr e = ExprParser.parse(line);
                Exprs.toUpperCase(e);
                if (e instanceof ExprEvaluatable) {
                    e = ((ExprEvaluatable) e).evaluate(context);
                }
                System.out.println(e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
