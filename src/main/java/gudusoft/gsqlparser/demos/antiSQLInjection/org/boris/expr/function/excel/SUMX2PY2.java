package org.boris.expr.function.excel;


public class SUMX2PY2 extends SUMX2MY2
{
    protected double eval(double x, double y) {
        return Math.pow(x, 2) + Math.pow(y, 2);
    }
}
