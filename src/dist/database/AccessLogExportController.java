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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

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
		final String baseQuery = "SELECT * FROM SctAccessLog acl"
				+ " LEFT JOIN Revisions rev on (acl.sc_scs_dID = rev.dID)"
				+ " LEFT JOIN DocMeta doc on (acl.sc_scs_dID = doc.dID)"
				+ " WHERE acl.eventDate > ? AND acl.eventDate < ?"
				+ " ORDER BY acl.eventDate %s";
		String query = String.format(baseQuery, getOrderByArgument(""));
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			String strStartDate = "20140401";
			String strEndDate = "20140531";
			
			statement.setDate(1, getDateByArgument(strStartDate));
			statement.setDate(2, getDateByArgument(strEndDate));
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
	
	private String getOrderByArgument(String orderArgument) {
		String order = "ASC";
		if (orderArgument.length() != 0) {
			order = orderArgument;
		}
		return order;
	}
	
	private Date getDateByArgument(String dateArgument) {
		Integer year = Integer.parseInt(dateArgument.substring(0, 4));
		Integer month = Integer.parseInt(dateArgument.substring(4,6));
		Integer day = Integer.parseInt(dateArgument.substring(6, 8));
		
		Calendar calDate = Calendar.getInstance();
		calDate.set(year,month-1,day,0,0,0);
		
		return new Date(calDate.getTimeInMillis());
	}
}
