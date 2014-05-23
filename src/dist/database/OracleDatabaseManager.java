package dist.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Properties;

public class OracleDatabaseManager {
	
	private Connection connection;
	
	public OracleDatabaseManager(Properties configInfo) {
		
		String address = "jdbc:oracle:thin:@" + configInfo.getProperty("IPAddress") + ":" + configInfo.getProperty("port") + "/" + configInfo.getProperty("dbname");
		
		try {
			connection = DriverManager.getConnection(address, configInfo.getProperty("account"), configInfo.getProperty("password"));
		}
		catch (Exception e) {
			// ????????????
		}
	}
	
	public void GetSctAccessLog() throws Exception {
		System.out.println("---- SctAccessLog ----");
		try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM SctAccessLog")) {
			try (ResultSet rs = ps.executeQuery()) {
				ResultSetMetaData md = rs.getMetaData();
				for (int c = 1; c < md.getColumnCount(); c++) {
					System.out.println(md.getColumnName(c));
				}
			}
		}
	}
	
	public void GetRevisions() throws Exception {
		System.out.println("---- Revisions ----");
		try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM Revisions")) {
			try (ResultSet rs = ps.executeQuery()) {
				ResultSetMetaData md = rs.getMetaData();
				for (int c = 1; c < md.getColumnCount(); c++) {
					System.out.println(md.getColumnName(c));
				}
			}
		}
	}
	
	public void GetDocMeta() throws Exception {
		System.out.println("---- DocMeta ----");
		try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM DocMeta")) {
			try (ResultSet rs = ps.executeQuery()) {
				ResultSetMetaData md = rs.getMetaData();
				for (int c = 1; c < md.getColumnCount(); c++) {
					System.out.println(md.getColumnName(c));
				}
			}
		}
	}
	
	
}
