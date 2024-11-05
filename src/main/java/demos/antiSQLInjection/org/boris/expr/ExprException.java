/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Peter Smith
 *******************************************************************************/
package org.boris.expr;

public class ExprException extends Exception
{
    public ExprException() {
        super();
    }

    public ExprException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExprException(String message) {
        super(message);
    }

    public ExprException(Throwable cause) {
        super(cause);
    }
}
