/**
 * 
 */
package test.database;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.sql.Connection;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import dist.database.OracleDatabaseManager;

/**
 * @author akiba
 *
 */
public class OracleDatabaseManagerTest {
	private Properties connectionInfo = new Properties();
	private OracleDatabaseManager databaseManager;
	
	@Before
	public void 接続情報クリア() {
		if (!connectionInfo.isEmpty()) {
			connectionInfo.clear();
		}
	}
	
	private void setupValidConnectionInfo() {
		setValidAddressInfo();
		setValidAuthInfo();
	}
	
	private void setValidAddressInfo() {
		connectionInfo.setProperty("IPAddress", "192.168.56.199");
		connectionInfo.setProperty("port", "xxxx");
		connectionInfo.setProperty("dbname", "xxx");
	}
	
	private void setValidAuthInfo() {
		connectionInfo.setProperty("account", "xxxx");
		connectionInfo.setProperty("password", "xxxx");
	}
	
	private void setupInvalidAddressConnectionInfo() {
		connectionInfo.setProperty("IPAddress", "192.168.56.10");
	}
	
	private void setupInvalidAuthInfo() {
		setValidAddressInfo();
		setInvalidPasswordAuthInfo();
	}
	
	private void setInvalidPasswordAuthInfo() {
		connectionInfo.setProperty("account", "xxxx");
		connectionInfo.setProperty("password", "xxx");
	}
	
	@Test
	public void 正しく接続してConnectionオブジェクトが取得できる() {
		setupValidConnectionInfo();
		databaseManager = new OracleDatabaseManager(connectionInfo);
		Boolean expected = true;
		Connection result = databaseManager.getConnection();
		assertThat(result.toString().contains("Connection"), is(expected));
	}
	
	@Test
	public void 接続先が不正の場合Connectionオブジェクトがnullとなる() {
		setupInvalidAddressConnectionInfo();
		databaseManager = new OracleDatabaseManager(connectionInfo);
		Connection expected = null;
		Connection result = databaseManager.getConnection();
		assertThat(result, is(expected));
	}
	
	@Test
	public void DB認証に失敗した場合Connectionオブジェクトがnullとなる() {
		setupInvalidAuthInfo();
		databaseManager = new OracleDatabaseManager(connectionInfo);
		Connection expected = null;
		Connection result = databaseManager.getConnection();
		assertThat(result, is(expected));
	}

}
