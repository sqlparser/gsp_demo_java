package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.ExprNumber;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;
import org.boris.expr.util.Financials;

public class VDB extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 5, 7);

        // cost
        Expr eC = evalArg(context, args[0]);
        if (eC instanceof ExprError)
            return eC;
        if (!isNumber(eC))
            return ExprError.VALUE;
        double cost = ((ExprNumber) eC).doubleValue();

        // salvage
        Expr eS = evalArg(context, args[1]);
        if (eS instanceof ExprError)
            return eS;
        if (!isNumber(eS))
            return ExprError.VALUE;
        double salvage = ((ExprNumber) eS).doubleValue();

        // life
        Expr eL = evalArg(context, args[2]);
        if (eL instanceof ExprError)
            return eL;
        if (!isNumber(eL))
            return ExprError.VALUE;
        int life = ((ExprNumber) eL).intValue();

        // start period
        Expr eP0 = evalArg(context, args[3]);
        if (eP0 instanceof ExprError)
            return eP0;
        if (!isNumber(eP0))
            return ExprError.VALUE;
        double start_period = ((ExprNumber) eP0).doubleValue();

        // end period
        Expr eP1 = evalArg(context, args[4]);
        if (eP1 instanceof ExprError)
            return eP1;
        if (!isNumber(eP1))
            return ExprError.VALUE;
        double end_period = ((ExprNumber) eP1).doubleValue();

        // factor
        double factor = 2;
        if (args.length > 5) {
            Expr eF = evalArg(context, args[5]);
            if (eF instanceof ExprError)
                return eF;
            if (!isNumber(eF))
                return ExprError.VALUE;
            factor = ((ExprNumber) eF).doubleValue();
        }

        // no switch
        boolean no_switch = true;
        if (args.length > 6) {
            Expr eN = evalArg(context, args[6]);
            if (eN instanceof ExprError)
                return eN;
            if (!(eN instanceof ExprNumber))
                return ExprError.VALUE;
            no_switch = ((ExprNumber) eN).booleanValue();
        }

        double vdb = Financials.vdb(cost, salvage, life, start_period,
                end_period, factor, no_switch);

        return new ExprDouble(vdb);
    }
}
