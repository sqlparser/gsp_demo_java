package dataflow;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.util.SQLUtil;
import junit.framework.TestCase;

public class testDataflow extends TestCase {
	public void test1() {

		File baseDir = new File(gspCommon.BASE_SQL_DIR, "dataflow");
		File[] sqlDirs = baseDir.listFiles();

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
				String dataflow = analyzer.generateDataFlow();
				File dataflowFile = new File(sqlfiles[j].getParentFile(),
						sqlfiles[j].getName().replace(".sql", ".xml"));
				if (!dataflowFile.exists()) {
					try {
						SQLUtil.writeToFile(dataflowFile, dataflow);
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
				assertEquals(dataflow, SQLUtil.readFile(dataflowFile));
			}
		}
	}
}
