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
import java.util.Iterator;

public class CSV
{
    public static String[] parseLine(String line) {
        return parseLine(line, ',', false);
    }

    public static String[] parseLine(String line, char delim, boolean hasQuotes) {
        ArrayList items = new ArrayList();
        StringBuilder sb = new StringBuilder();
        boolean inQuote = false;
        int length = line.length();

        for (int i = 0; i < length; i++) {
            char c = line.charAt(i);

            if ((c == delim) && !inQuote) {
                items.add(sb.toString());
                sb.setLength(0);
            } else if (hasQuotes && (c == '\"')) {
                inQuote = !inQuote;
            } else {
                sb.append(c);
            }
        }

        items.add(sb.toString());

        return (String[]) items.toArray(new String[0]);
    }

    public static String toCSV(Iterator it) {
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext())
                sb.append(',');
        }
        return sb.toString();
    }

    public static String toCSV(String[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1)
                sb.append(",");
        }
        return sb.toString();
    }
}
