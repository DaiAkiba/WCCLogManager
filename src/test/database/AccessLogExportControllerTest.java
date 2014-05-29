/**
 * 
 */
package test.database;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.operation.DatabaseOperation;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.InputSource;

import au.com.bytecode.opencsv.CSVReader;
import dist.config.ConfigurationController;
import dist.database.AccessLogExportController;
import dist.database.OracleDatabaseManager;

/**
 * @author akiba
 *
 */
public class AccessLogExportControllerTest {
	private static AccessLogExportController exportController;
	private static ConfigurationController config;
	private static OracleDatabaseManager database;
	private static IDatabaseConnection iConnection;
	private static File partialData;
	
	private final static String EXPORT_FILE_NAME = "exportTest.csv";
	private final static int TEST_DATA_LINE_COUNT = 4;
	private final static int EVENT_DATE_COLUMN_INDEX = 3;
	
	@BeforeClass
	public static void DBのバックアップ取得してテストデータを投入する() throws Exception {
		// DBのバックアップを取得する
		config = new ConfigurationController("data/properties.xml");
		database = new OracleDatabaseManager(config.getProperites());
		
		iConnection = new DatabaseConnection(database.getConnection());
		
		QueryDataSet partialDataSet = new QueryDataSet(iConnection);
		partialDataSet.addTable("SctAccessLog");
		
		partialData = File.createTempFile("AccessLog", "xml");
		FlatXmlDataSet.write(partialDataSet, new FileOutputStream(partialData));
		
		// テストデータを投入する
		FlatXmlProducer xmlProducer = new FlatXmlProducer(new InputSource("data/SctAccessLogTestData.xml"));
		IDataSet dataSet = new FlatXmlDataSet(xmlProducer);
		DatabaseOperation.CLEAN_INSERT.execute(iConnection, dataSet);
	}
	
	@AfterClass
	public static void バックアップしたDBを戻してテスト用ファイルを削除する() throws Exception {
		FlatXmlProducer xmlProducer = new FlatXmlProducer(new InputSource(partialData.getPath()));
		IDataSet dataSet = new FlatXmlDataSet(xmlProducer);
		DatabaseOperation.CLEAN_INSERT.execute(iConnection, dataSet);
		
		File fileExportFile = new File(EXPORT_FILE_NAME);
		fileExportFile.delete();
		
		database.getConnection().close();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void ファイル名を指定しないと例外が発生する() throws Exception {
		exportController = new AccessLogExportController("", "");
		Connection connection = null;
		exportController.export(connection, "20140401", "20140531", "");
	}
	
	@Test(expected = NullPointerException.class)
	public void 接続情報がNullの場合は例外が発生する() throws Exception {
		exportController = new AccessLogExportController(".", EXPORT_FILE_NAME);
		Connection connection = null;
		exportController.export(connection, "20140401", "20140531", "");
	}

	@Test
	public void ディレクトリパスの終端がスラッシュなしの場合にアクセスログをエクスポートできる() throws Exception {
		exportController = new AccessLogExportController(".",EXPORT_FILE_NAME);
		exportController.export(database.getConnection(), "20140401", "20140531", "");
		int expected = TEST_DATA_LINE_COUNT;
		int result = getExportedLineCount();
		assertThat(result, is(expected));
	}
	
	@Test
	public void ディレクトリパスの終端がスラッシュありの場合にアクセスログをエクスポートできる() throws Exception {
		exportController = new AccessLogExportController("./",EXPORT_FILE_NAME);
		exportController.export(database.getConnection(), "20140401", "20140531", "");
		int expected = TEST_DATA_LINE_COUNT;
		int result = getExportedLineCount();
		assertThat(result, is(expected));
	}
	
	@Test
	public void エクスポートしたファイルの先頭行が列名になっている() throws Exception {
		exportController = new AccessLogExportController("./",EXPORT_FILE_NAME);
		exportController.export(database.getConnection(), "20140401", "20140531", "");
		List<String[]> exportedData = new ArrayList<String[]>();
		getExportedData(exportedData);
		String expected = "SCTDATESTAMP";
		String result = exportedData.get(0)[0];
		assertThat(result, is(expected));
		
		expected = "EVENTDATE";
		result = exportedData.get(0)[EVENT_DATE_COLUMN_INDEX];
		assertThat(result, is(expected));
	}
	
	@Test
	public void eventDateによるソートができる() throws Exception {
		exportController = new AccessLogExportController("./",EXPORT_FILE_NAME);
		
		exportController.export(database.getConnection(), "20140401", "20140531", "DESC");
		List<String[]> exportedData = new ArrayList<String[]>();
		getExportedData(exportedData);
		String expected = getExpectedDateString(2014,5,27);
		String result = exportedData.get(1)[EVENT_DATE_COLUMN_INDEX];
		assertThat(result, is(expected));
		
		exportController.export(database.getConnection(), "20140401", "20140531", "ASC");
		getExportedData(exportedData);
		expected = getExpectedDateString(2014,4,10);
		result = exportedData.get(1)[EVENT_DATE_COLUMN_INDEX];
		assertThat(result, is(expected));
	}
	
	@Test
	public void 取得範囲の指定ができる() throws Exception {
		exportController = new AccessLogExportController("./",EXPORT_FILE_NAME);
		
		exportController.export(database.getConnection(), "20140501", "20140531", "");
		List<String[]> exportedData = new ArrayList<String[]>();
		getExportedData(exportedData);
		String expected = getExpectedDateString(2014,5,27);
		String result = exportedData.get(1)[EVENT_DATE_COLUMN_INDEX];
		assertThat(result, is(expected));
		
		exportController.export(database.getConnection(), "20140401", "20140430", "");
		getExportedData(exportedData);
		expected = getExpectedDateString(2014,4,10);
		result = exportedData.get(1)[EVENT_DATE_COLUMN_INDEX];
		assertThat(result, is(expected));
	}
	
	private int getExportedLineCount() throws Exception {
		CSVReader reader = new CSVReader(new FileReader(new File(EXPORT_FILE_NAME)));
		List<String[]> exportedData = reader.readAll();
		reader.close();
		return exportedData.size();
	}
	
	private void getExportedData(List<String[]> exportedData) throws Exception {
		exportedData.clear();
		CSVReader reader = new CSVReader(new FileReader(new File(EXPORT_FILE_NAME)));
		exportedData.addAll(reader.readAll());
		reader.close();
	}
	
	private String getExpectedDateString(int year, int month, int day) {
		Calendar calDate = Calendar.getInstance();
		calDate.set(year,month-1,day,0,0,0);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyy HH:mm:ss");
		
		return dateFormat.format(new Date(calDate.getTimeInMillis()));
		
	}
}
