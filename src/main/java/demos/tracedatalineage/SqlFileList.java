
package demos.tracedatalineage;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class SqlFileList
{

	public final List<String> sqlfiles = new ArrayList<String>( );
	private String subfix = ".sql";

	public SqlFileList( String dir )
	{
		this( dir, false );
	}

	public SqlFileList( String dir, boolean includeSubDir )
	{
		File parent = new File( dir );
		if ( parent.exists( ) && parent.isDirectory( ) )
			traverseSqlFiles( parent, includeSubDir );
	}

	public SqlFileList( String dir, String subfix )
	{
		this( dir, subfix, false );
	}

	public SqlFileList( String dir, String subfix, boolean includeSubDir )
	{
		this.subfix = subfix;
		File parent = new File( dir );
		if ( parent.exists( ) && parent.isDirectory( ) )
			traverseSqlFiles( parent, includeSubDir );
	}

	private void traverseSqlFiles( File parent, boolean includeSubDir )
	{
		File[] files = parent.listFiles( new FileFilter( ) {

			public boolean accept( File file )
			{
				if ( file.getName( ).toLowerCase( ).endsWith( subfix ) )
				{
					sqlfiles.add( file.getAbsolutePath( ) );
				}

				if ( file.isDirectory( ) )
					return true;

				return false;
			}
		} );

		if ( includeSubDir )
		{
			if ( files != null && files.length > 0 )
			{
				for ( File file : files )
				{
					traverseSqlFiles( file, includeSubDir );
				}
			}
		}
	}

}
