/**
 * 
 */
package dist.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * @author akiba
 *
 */
public class AccessLogExportController {
	File exportFilePath;
	private CSVWriter csvWriter;
	
	public AccessLogExportController(String strExportFilePath) {
		exportFilePath = new File(strExportFilePath);
	}
	
	public int export(Connection connection) throws Exception {
		csvWriter = setupCsvWriter();
		
		try (PreparedStatement statement = connection.prepareStatement(makeSQL())) {
			try (ResultSet resultSet = statement.executeQuery()) {
				csvWriter.writeAll(resultSet, true);
			}
		}
		
		csvWriter.close();
		
		return 0;
	}
	
	private CSVWriter setupCsvWriter() throws Exception {
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(exportFilePath), "UTF8"));
		
		return new CSVWriter(writer,CSVWriter.DEFAULT_SEPARATOR ,CSVWriter.DEFAULT_QUOTE_CHARACTER,"\r\n");
	}
	
	private String makeSQL() {
		String strSQL = "SELECT * FROM DocMeta";
		return strSQL;
	}
}
