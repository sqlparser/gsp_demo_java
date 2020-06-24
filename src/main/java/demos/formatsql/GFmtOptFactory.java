
package gudusoft.gsqlparser.pp.para;

import java.util.Hashtable;
import java.util.Map;

public class GFmtOptFactory
{

	/**
	 * all the instances, the key is the session id.
	 */
	private volatile static Map<String, GFmtOpt> objs = new Hashtable<String, GFmtOpt>( );

	/**
	 * create an new instance
	 * 
	 * @param sessionId
	 *            the session id
	 * @return the new option instance
	 */
	public static GFmtOpt newInstance( String sessionId )
	{
		if ( !objs.containsKey( sessionId ) )
		{
			synchronized ( GFmtOptFactory.class )
			{
				if ( !objs.containsKey( sessionId ) )
				{
					objs.put( sessionId, new GFmtOpt( sessionId ) );
				}
			}
		}

		return objs.get( sessionId );
	}

	public static GFmtOpt newInstance( )
	{
		return newInstance( Long.valueOf( Thread.currentThread( ).getId( ) )
				.toString( ) );
	}

}
