package dist.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Properties;

import au.com.bytecode.opencsv.CSVWriter;

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
	
	public Connection getConnection() {
		return connection;
	}
	
	public void GetSctAccessLog() throws Exception {
		System.out.println("---- SctAc  cessLog ----");
		File fileDir = new File("./SctAccessLog.csv");
		
		Writer out = new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream(fileDir), "UTF8"));
		CSVWriter writer = new CSVWriter(out,CSVWriter.DEFAULT_SEPARATOR ,CSVWriter.DEFAULT_QUOTE_CHARACTER,"\r\n");
		
		try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM SctAccessLog")) {
			try (ResultSet rs = ps.executeQuery()) {
				ResultSetMetaData md = rs.getMetaData();
				for (int c = 1; c < md.getColumnCount(); c++) {
					System.out.println(md.getColumnName(c));
				}
				writer.writeAll(rs, true);
			}
		}
		writer.close();
	}
	
	public void GetRevisions() throws Exception {
		System.out.println("---- Revisions ----");
		CSVWriter writer = new CSVWriter(new FileWriter("./Revisions.csv"),CSVWriter.DEFAULT_SEPARATOR ,CSVWriter.DEFAULT_QUOTE_CHARACTER,"\r\n");
		
		try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM Revisions")) {
			try (ResultSet rs = ps.executeQuery()) {
				ResultSetMetaData md = rs.getMetaData();
				for (int c = 1; c < md.getColumnCount(); c++) {
					System.out.println(md.getColumnName(c));
				}
				writer.writeAll(rs,true);
			}
		}
		writer.close();
	}
	
	public void GetDocMeta() throws Exception {
		System.out.println("---- DocMeta ----");
		File fileDir = new File("./DocMeta.csv");
		
		Writer out = new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream(fileDir), "UTF8"));
		CSVWriter writer = new CSVWriter(out,CSVWriter.DEFAULT_SEPARATOR ,CSVWriter.DEFAULT_QUOTE_CHARACTER,"\r\n");
		try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM DocMeta")) {
			try (ResultSet rs = ps.executeQuery()) {
				ResultSetMetaData md = rs.getMetaData();
				for (int c = 1; c < md.getColumnCount(); c++) {
					System.out.println(md.getColumnName(c));
				}
				
				writer.writeAll(rs,true);
				writer.close();
				
			}
		}
	}
	
	
}
