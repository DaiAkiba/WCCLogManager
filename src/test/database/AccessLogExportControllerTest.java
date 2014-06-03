/**
 * 
 */
package test.database;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lib.custom.opencsv.CSVReader;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.operation.DatabaseOperation;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.InputSource;

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
	private final static String FILE_ENCODE_STRING = "MS932";
	private final static String RETURN_CODE_STRING = "\r\n";
	private final static int HEADER_LINE_COUNT = 1;
	private final static int TEST_DATA_LINE_COUNT = 7;
	private final static int TEST_LINE_COUNT = HEADER_LINE_COUNT + TEST_DATA_LINE_COUNT;
	private final static int EVENT_DATE_COLUMN_INDEX = 3;
	
	@BeforeClass
	public static void setUp() throws Exception {
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
	
	@Before
	public void beforeEachTest() {
		File fileExportFile = new File(EXPORT_FILE_NAME);
		if (fileExportFile.exists()) {
			fileExportFile.delete();
		}
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		FlatXmlProducer xmlProducer = new FlatXmlProducer(new InputSource(partialData.getPath()));
		IDataSet dataSet = new FlatXmlDataSet(xmlProducer);
		DatabaseOperation.CLEAN_INSERT.execute(iConnection, dataSet);
		
		File fileExportFile = new File(EXPORT_FILE_NAME);
		fileExportFile.delete();
		
		database.getConnection().close();
	}
	
	@Test
	public void 取得開始日が不正の場合はエラーが返る() throws Exception {
		exportController = new AccessLogExportController("./",EXPORT_FILE_NAME);

		int expected = -1;
		int result = exportController.export(database.getConnection(), "abc", "20140131", "");
		assertThat(result, is(expected));
		
		File fileExportFile = new File(EXPORT_FILE_NAME);
		boolean expectedFile = false;
		boolean resultFile = fileExportFile.exists();
		assertThat(resultFile, is(expectedFile));
	}
	
	@Test
	public void 取得終了日が不正の場合はエラーが返る() throws Exception {
		exportController = new AccessLogExportController("./",EXPORT_FILE_NAME);

		int expected = -1;
		int result = exportController.export(database.getConnection(), "20140101", "abc", "");
		assertThat(result, is(expected));
		
		File fileExportFile = new File(EXPORT_FILE_NAME);
		boolean expectedFile = false;
		boolean resultFile = fileExportFile.exists();
		assertThat(resultFile, is(expectedFile));
	}

	@Test
	public void ソート順が不正の場合はエラーが返る() throws Exception {
		exportController = new AccessLogExportController("./",EXPORT_FILE_NAME);

		int expected = -1;
		int result = exportController.export(database.getConnection(), "20140101", "20140131", "sample");
		assertThat(result, is(expected));
		
		File fileExportFile = new File(EXPORT_FILE_NAME);
		boolean expectedFile = false;
		boolean resultFile = fileExportFile.exists();
		assertThat(resultFile, is(expectedFile));
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
	public void ファイルフォーマットはMS932で改行コードはCRLFである() throws Exception {
		exportController = new AccessLogExportController(".",EXPORT_FILE_NAME);
		exportController.export(database.getConnection(), "20140401", "20140531", "");
		List<String[]> exportedData = new ArrayList<String[]>();
		getExportedData(exportedData);
		boolean expected = true;
		boolean result = exportedData.get(1)[30].equals(new String(exportedData.get(1)[30].getBytes(FILE_ENCODE_STRING), FILE_ENCODE_STRING));
		assertThat(result, is(expected));
		
		String expectedReturnCode = RETURN_CODE_STRING;
		String resultReturnCode = getReturnCode();
		System.out.println(exportedData.get(0)[0]);
		assertThat(resultReturnCode, is(expectedReturnCode));
	}
	
	@Test
	public void ディレクトリパスの終端がスラッシュなしの場合にアクセスログをエクスポートできる() throws Exception {
		exportController = new AccessLogExportController(".",EXPORT_FILE_NAME);
		exportController.export(database.getConnection(), "20140401", "20140531", "");
		int expected = TEST_LINE_COUNT;
		int result = getExportedLineCount();
		assertThat(result, is(expected));
	}
	
	@Test
	public void ディレクトリパスの終端がスラッシュありの場合にアクセスログをエクスポートできる() throws Exception {
		exportController = new AccessLogExportController("./",EXPORT_FILE_NAME);
		exportController.export(database.getConnection(), "20140401", "20140531", "");
		int expected = TEST_LINE_COUNT;
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
	public void ソートを指定しないとeventDateの昇順でソートされる() throws Exception {
		exportController = new AccessLogExportController("./",EXPORT_FILE_NAME);
		
		exportController.export(database.getConnection(), "20140401", "20140531", "");
		List<String[]> exportedData = new ArrayList<String[]>();
		getExportedData(exportedData);
		String expected = getExpectedDateString(2014,4,10);
		String result = exportedData.get(1)[EVENT_DATE_COLUMN_INDEX];
		assertThat(result, is(expected));
		
		expected = getExpectedDateString(2014,5,27);
		result = exportedData.get(TEST_DATA_LINE_COUNT)[EVENT_DATE_COLUMN_INDEX];
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
		
		exportController.export(database.getConnection(), "20140501", "20140531", "DESC");
		List<String[]> exportedData = new ArrayList<String[]>();
		getExportedData(exportedData);
		String expected = getExpectedDateString(2014,5,27);
		String result = exportedData.get(1)[EVENT_DATE_COLUMN_INDEX];
		assertThat(result, is(expected));
		
		int expectedLineCount = HEADER_LINE_COUNT + 2;
		int resultLineCount = exportedData.size();
		assertThat(resultLineCount, is(expectedLineCount));
		
		exportController.export(database.getConnection(), "20140401", "20140430", "");
		getExportedData(exportedData);
		expected = getExpectedDateString(2014,4,10);
		result = exportedData.get(1)[EVENT_DATE_COLUMN_INDEX];
		assertThat(result, is(expected));
		
		expectedLineCount = HEADER_LINE_COUNT + 5;
		resultLineCount = exportedData.size();
		assertThat(resultLineCount, is(expectedLineCount));
	}
	
	@Test
	public void 取得開始日と取得終了日を含むデータが取得できる() throws Exception {
		exportController = new AccessLogExportController("./",EXPORT_FILE_NAME);
		
		exportController.export(database.getConnection(), "20140427", "20140430", "");
		List<String[]> exportedData = new ArrayList<String[]>();
		getExportedData(exportedData);
		String expected = getExpectedDateString(2014,4,27);
		String result = exportedData.get(1)[EVENT_DATE_COLUMN_INDEX];
		assertThat(result, is(expected));
		
		expected = "30-4-2014 23:59:59 JST";
		result = exportedData.get(4)[EVENT_DATE_COLUMN_INDEX];
		assertThat(result, is(expected));
	}
	
	@Test
	public void 取得開始日と取得終了日が同じでもデータが取得できる() throws Exception {
		exportController = new AccessLogExportController("./",EXPORT_FILE_NAME);
		
		exportController.export(database.getConnection(), "20140427", "20140427", "");
		List<String[]> exportedData = new ArrayList<String[]>();
		getExportedData(exportedData);
		int expected = HEADER_LINE_COUNT + 1;
		int result = getExportedLineCount();
		assertThat(result, is(expected));
	}
	
	@Test
	public void データがない場合は列名だけ取得できる() throws Exception {
		exportController = new AccessLogExportController("./",EXPORT_FILE_NAME);
		exportController.export(database.getConnection(), "20140101", "20140131", "");
		List<String[]> exportedData = new ArrayList<String[]>();
		getExportedData(exportedData);
		String expected = "SCTDATESTAMP";
		String result = exportedData.get(0)[0];
		assertThat(result, is(expected));
		
		int expectedLineCount = HEADER_LINE_COUNT;
		int resultLineCount = exportedData.size();
		assertThat(resultLineCount, is(expectedLineCount));
	}
	
	private int getExportedLineCount() throws Exception {
		CSVReader reader = new CSVReader(new FileReader(new File(EXPORT_FILE_NAME)));
		List<String[]> exportedData = reader.readAll();
		reader.close();
		return exportedData.size();
	}
	
	private void getExportedData(List<String[]> exportedData) throws Exception {
		exportedData.clear();
		Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(EXPORT_FILE_NAME), FILE_ENCODE_STRING));
		CSVReader csvReader = new CSVReader(reader);
		exportedData.addAll(csvReader.readAll());
		csvReader.close();
	}
	
	private String getExpectedDateString(int year, int month, int day) {
		Calendar calDate = Calendar.getInstance();
		calDate.set(year,month-1,day,0,0,0);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyy HH:mm:ss z");
		
		return dateFormat.format(new Date(calDate.getTimeInMillis()));
		
	}
	
	private String getReturnCode() throws Exception {
		InputStreamReader in = new InputStreamReader(new FileInputStream(EXPORT_FILE_NAME), FILE_ENCODE_STRING);
		
		int code = 0;
		int next = 0;
		
		while((code = in.read()) != -1){
			if (code == 0x0d || code == 0x0a) {
				next = in.read();
				break;
			}
		}
		in.close();
		
		return String.format("%c%c", code,next);
		
	}
}
