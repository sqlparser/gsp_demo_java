
package demos.dlineage.dataflow.model.xml;

import demos.dlineage.util.Pair;
import demos.dlineage.util.SQLUtil;

public class PositionUtil
{

	public static Pair<Integer, Integer> getStartPos( String coordinate,
			int index )
	{
		if (!SQLUtil.isEmpty(coordinate))
		{
			String firstCoordinate = coordinate.substring(coordinate.indexOf("[")+1, coordinate.indexOf("]"));
			int length = firstCoordinate.split(",").length;
			
			String[] splits = coordinate.replace( "[", "" )
					.replace( "]", "" )
					.split( "," );
			if ( splits.length % (2 * length) == 0 )
			{
				return new Pair<Integer, Integer>(
						Integer.parseInt( splits[index * (2 * length) + 0].trim( ) ),
						Integer.parseInt( splits[index * (2 * length) + 1].trim( ) ) );
			}
		}
		return null;
	}

	public static Pair<Integer, Integer> getEndPos( String coordinate,
			int index )
	{
		if (!SQLUtil.isEmpty(coordinate))
		{
			String firstCoordinate = coordinate.substring(coordinate.indexOf("[")+1, coordinate.indexOf("]"));
			int length = firstCoordinate.split(",").length;
			
			String[] splits = coordinate.replace( "[", "" )
					.replace( "]", "" )
					.split( "," );
			if ( splits.length % (2 * length) == 0 )
			{
				return new Pair<Integer, Integer>(
						Integer.parseInt(splits[index * (2 * length) + length].trim()),
						Integer.parseInt(splits[index * (2 * length) + length + 1].trim()));
			}
		}
		return null;
	}

	public static int getOccurrencesNumber( String coordinate )
	{
		if (!SQLUtil.isEmpty(coordinate))
		{
			String firstCoordinate = coordinate.substring(coordinate.indexOf("[")+1, coordinate.indexOf("]"));
			int length = firstCoordinate.split(",").length;
			
			String[] splits = coordinate.replace( "[", "" )
					.replace( "]", "" )
					.split( "," );
			if ( splits.length % (2 * length) == 0 )
			{
				return splits.length / (2 * length);
			}
		}
		return 0;
	}
}
