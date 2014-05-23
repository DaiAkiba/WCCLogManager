package test.config;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import dist.config.ConfigurationController;

public class ConfigurationControllerTest {
	private static final String strTestFile = "./property.xml";
	private static final String validKey = "name";
	private static final String invalidKey = "account";
	private static final String validValue = "田中";
	private ConfigurationController config;
	
	@BeforeClass
	public static void xmlファイルを作成する() throws Exception {
		Properties properties = new Properties();
		properties.setProperty(validKey, validValue);
		properties.setProperty("fuga", "ふが");

		try (OutputStream os = new FileOutputStream(strTestFile)) {
			properties.storeToXML(os, "コメント");
		}
	}
	
	@AfterClass
	public static void xmlファイルを削除する() {
		File fileTestFile = new File(strTestFile);
		fileTestFile.delete();
	}
	
	@Test
	public void 存在するプロパティを取得する() {
		try {
			config = new ConfigurationController(strTestFile);
			String expected = validValue;
			String result = config.getProperty(validKey);
			assertThat(result, is(expected));
		}
		catch (FileNotFoundException e) {
			fail(e.toString());
		}
	}
	
	@Test
	public void 存在しないプロパティは取得できない() {
		try {
			config = new ConfigurationController(strTestFile);
			String expected = null;
			String result = config.getProperty(invalidKey);
			assertThat(result, is(expected));
		}
		catch (FileNotFoundException e) {
			fail(e.toString());
		}
	}
	
	@Test(expected = FileNotFoundException.class)
	public void 不正なパスを指定すると例外が発生する() throws FileNotFoundException {
		ConfigurationController config = new ConfigurationController("");
	}
	
}
