package org.boris.expr.util;


public class StringUtil
{
    public static String removeComma( String string )
    {
        if ( string != null
                && string.length( ) >= 2
                && string.startsWith( "'" ) //$NON-NLS-1$
                && string.endsWith( "'" ) ) //$NON-NLS-1$
        {
            return string.substring( 1, string.length( ) - 1 );
        }
        return string;
    }
}
