
package demos.dlineage.dataflow.model.xml;

import demos.dlineage.util.Pair;

public class PositionUtil
{

	public static Pair<Integer, Integer> getStartPos( String coordinate,
			int index )
	{
		if ( coordinate != null )
		{
			String[] splits = coordinate.replace( "[", "" )
					.replace( "]", "" )
					.split( "," );
			if ( splits.length % 4 == 0 )
			{
				return new Pair<Integer, Integer>(
						Integer.parseInt( splits[index * 4 + 0].trim( ) ),
						Integer.parseInt( splits[index * 4 + 1].trim( ) ) );
			}
		}
		return null;
	}

	public static Pair<Integer, Integer> getEndPos( String coordinate,
			int index )
	{
		if ( coordinate != null )
		{
			String[] splits = coordinate.replace( "[", "" )
					.replace( "]", "" )
					.split( "," );
			if ( splits.length % 4 == 0 )
			{
				return new Pair<Integer, Integer>(
						Integer.parseInt( splits[index * 4 + 2].trim( ) ),
						Integer.parseInt( splits[index * 4 + 3].trim( ) ) );
			}
		}
		return null;
	}

	public static int getOccurrencesNumber( String coordinate )
	{
		if ( coordinate != null )
		{
			String[] splits = coordinate.replace( "[", "" )
					.replace( "]", "" )
					.split( "," );
			if ( splits.length % 4 == 0 )
			{
				return splits.length / 4;
			}
		}
		return 0;
	}
}
