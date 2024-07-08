package dataflow;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.util.SQLUtil;
import junit.framework.TestCase;

public class testDataflow extends TestCase {
	public void test1() {
		File privateDir = new File(gspCommon.BASE_SQL_DIR, "private/dataflow");
		File publicDir = new File(gspCommon.BASE_SQL_DIR, "public/dataflow");
		List<File> sqlDirList = new ArrayList<>();
		if(privateDir.exists() && privateDir.listFiles()!=null){
			sqlDirList.addAll(Arrays.asList(privateDir.listFiles()));
		}
		if(publicDir.exists() && publicDir.listFiles()!=null){
			sqlDirList.addAll(Arrays.asList(publicDir.listFiles()));
		}

		File[] sqlDirs = sqlDirList.toArray(new File[0]);

		for (int i = 0; i < sqlDirs.length; i++) {
			File vendorDir = sqlDirs[i];
			if(vendorDir.getName().startsWith("."))
				continue;
			EDbVendor vendor = EDbVendor.valueOf("dbv" + vendorDir.getName());
			File[] sqlfiles = vendorDir.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".sql");
				}
			});

			for (int j = 0; j < sqlfiles.length; j++) {
				DataFlowAnalyzer analyzer = new DataFlowAnalyzer(sqlfiles[j], vendor, false);
				analyzer.setShowConstantTable(true);
				analyzer.getOption().setTraceProcedure(true);
				String dataflow = analyzer.generateDataFlow();
				File dataflowFile = new File(sqlfiles[j].getParentFile(),
						sqlfiles[j].getName().replace(".sql", ".xml"));
				if (!dataflowFile.exists())
				{
					try {
						SQLUtil.writeToFile(dataflowFile, dataflow.trim());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(!dataflow.equals( SQLUtil.readFile(dataflowFile))){
					File dataflowFile1 = new File(sqlfiles[j].getParentFile(),
							sqlfiles[j].getName().replace(".sql", "1.xml"));
					if (!dataflowFile1.exists()) {
						try {
							SQLUtil.writeToFile(dataflowFile1, dataflow);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				assertTrue(TBaseType.compareStringBuilderToFile(new StringBuilder(dataflow), dataflowFile.getAbsolutePath()));
			}
		}
	}
}
