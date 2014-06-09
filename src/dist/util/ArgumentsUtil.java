/**
 * 
 */
package dist.util;

import java.sql.Date;
import java.util.Calendar;

/**
 * @author akiba
 *
 */
public class ArgumentsUtil {
	private String strStart;
	private String strEnd;
	private String strOrder;
	private String strEncoding;
	
	private int validateResult = 0;
	
	public ArgumentsUtil(String[] args) {
		try {
			for (int index = 0; index < args.length; index++) {
				if ("-start".equals(args[index])) {
					setStart(args[++index]);
				}
				else if ("-end".equals(args[index])) {
					setEnd(args[++index]);
				}
				else if ("-order".equals(args[index])) {
					setOrder(args[++index]);
				}
				else if ("-encoding".equals(args[index])) {
					setEncoding(args[++index]);
				}
				else {
					validateResult = -1;
					System.err.println("引数指定の誤り：未知の引数が指定されました");
				}
			}
		}
		catch (Exception e) {
			validateResult = -1;
		}
	}
	
	public int getValidateResult() {
		return validateResult;
	}
	
	public Date getStartByDate() {
		return getDateByString(strStart);
	}
	
	public Date getEndByDate() {
		return getDateByString(strEnd);
	}
	
	public String getOrder() {
		return strOrder;
	}
	
	public String getEncoding() {
		return strEncoding;
	}
	
	private int setStart(String start) {
		int result = validateDateArgument(start);
		if (result == 0) {
			strStart = start;
		}
		else {
			validateResult = result;
		}
		return result;
	}
	
	private int setEnd(String end) {
		int result = validateDateArgument(end);
		if (result == 0) {
			strEnd = end;
		}
		else {
			validateResult = result;
		}
		return result;
	}
	
	private int setOrder(String order) {
		int result = validateOrderArgument(order);
		if (result == 0) {
			strOrder = order;
		}
		else {
			validateResult = result;
		}
		return result;
	}
	
	private int setEncoding(String encoding) {
		int result = validateEncodingArgument(encoding);
		if (result == 0) {
			strEncoding = encoding;
		}
		else {
			validateResult = result;
		}
		return result;
	}
	private int validateDateArgument(String strDate) {
		if (strDate.length() != 8) {
			return -1;
		}
		
		try {
			Integer.parseInt(strDate);
		}
		catch (NumberFormatException e) {
			return -1;
		}
		
		IntYMD ymd = getIntYMD(strDate);
		
		if (ymd.year < 1970 || ymd.year > 3000 ||
			ymd.month < 1 || ymd.month > 12 ||
			ymd.day < 1 || ymd.day > getLastDayOfMonth(ymd)) {
			return -1;
		}
		
		return 0;
	}
	
	private int getLastDayOfMonth(IntYMD ymd) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, ymd.year);
		cal.set(Calendar.MONTH, ymd.month-1);
		return cal.getActualMaximum(Calendar.DATE);
	}
	
	private int validateOrderArgument(String strOrder) {
		if (strOrder.matches("DESC") || strOrder.matches("ASC")) {
			return 0;
		}
		return -1;
	}

	private int validateEncodingArgument(String strEncoding) {
		if (strEncoding.matches("MS932") || strEncoding.matches("UTF-8")) {
			return 0;
		}
		return -1;
	}

	private Date getDateByString(String dateArgument) {
		if (dateArgument.length() == 0) {
			return null;
		}
		
		IntYMD ymd = getIntYMD(dateArgument);
		
		Calendar calDate = Calendar.getInstance();
		calDate.set(ymd.year,ymd.month-1,ymd.day,0,0,0);
		
		return new Date(calDate.getTimeInMillis());
	}
	
	class IntYMD {
		int year;
		int month;
		int day;
	}
	
	private IntYMD getIntYMD(String dateString){
		IntYMD intYMD = new IntYMD();
		
		intYMD.year = Integer.parseInt(dateString.substring(0, 4));
		intYMD.month = Integer.parseInt(dateString.substring(4,6));
		intYMD.day = Integer.parseInt(dateString.substring(6, 8));
		
		return intYMD;
	}
}
