package org.boris.expr.function.excel;

import org.boris.expr.ExprException;
import org.boris.expr.function.DoubleInOutFunctionErr;
import org.boris.expr.util.Statistics;

public class NORMSDIST extends DoubleInOutFunctionErr
{
    protected double evaluate(double value) throws ExprException {
        return Statistics.normsDist(value);
    }
}
