/**
 * 
 */
package test.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import dist.util.ArgumentsUtil;

/**
 * @author akiba
 *
 */
@RunWith(Enclosed.class)
public class ArgumentsUtilTest {
	@RunWith(Theories.class)
	public static class 未対応のパラメータを指定 {
		@DataPoints
		public static String[][] UNSUPPORTED_PARAM = {{"-sample", "abc"}
													 ,{"start", "def"}};
		
		@Theory
		public void validateResultがエラーになる(String[] args) {
			ArgumentsUtil arguments = new ArgumentsUtil(args);
			int expected = -1;
			int result = arguments.getValidateResult();
			assertThat(result, is(expected));
		}
	}
	
	@RunWith(Theories.class)
	public static class 値が未指定 {
		@DataPoints
		public static String[][] NOVALUE_PARAM = {{"-start"}
												 ,{"-end"}
												 ,{"-order"}
												 ,{"-encoding"}};
		
		@Theory
		public void validateResultがエラーになる(String[] args) {
			ArgumentsUtil arguments = new ArgumentsUtil(args);
			int expected = -1;
			int result = arguments.getValidateResult();
			assertThat(result, is(expected));
		}
	}
	
	@RunWith(Theories.class)
	public static class 年月日指定の項目にアルファベットを指定 {
		@DataPoints
		public static String[][] ALPHA_PARAM = {{"-start", "abcdefgh"}
												,{"-start", "aaaa1231"}
												,{"-start", "2014z201"}
												,{"-start", "20140h31"}
												,{"-start", "ABC"}
												,{"-end", "201405d1"}
												,{"-end", "201405dd"}
												,{"-end", "abcdefgh"}};
		
		@Theory
		public void validateResultがエラーになる(String[] args) {
			ArgumentsUtil arguments = new ArgumentsUtil(args);
			int expected = -1;
			int result = arguments.getValidateResult();
			assertThat(result, is(expected));
		}
	}
	
	@RunWith(Theories.class)
	public static class 年月日指定の項目に不正なフォーマットを指定 {
		@DataPoints
		public static String[][] INVALID_FORMAT_PARAM = {{"-start", "2014/04/05"}
														,{"-start", "2014-4-1"}
														,{"-end", "2014/04/05"}
														,{"-end", "2014-4-1"}};
		
		@Theory
		public void validateResultがエラーになる(String[] args) {
			ArgumentsUtil arguments = new ArgumentsUtil(args);
			int expected = -1;
			int result = arguments.getValidateResult();
			assertThat(result, is(expected));
		}
	}
	
	@RunWith(Theories.class)
	public static class 年月日指定の項目に不正な日付を指定 {
		@DataPoints
		public static String[][] INVALID_FORMAT_PARAM = {{"-start", "00000000"}
														,{"-start", "99999999"}
														,{"-start", "19691231"}
														,{"-start", "30010101"}
														,{"-start", "20140229"}
														,{"-start", "20150230"}
														,{"-end", "20141301"}
														,{"-end", "20141200"}
														,{"-end", "20150431"}
														,{"-end", "20141232"}};
		
		@Theory
		public void validateResultがエラーになる(String[] args) {
			ArgumentsUtil arguments = new ArgumentsUtil(args);
			int expected = -1;
			int result = arguments.getValidateResult();
			assertThat(result, is(expected));
		}
	}
	
	@RunWith(Theories.class)
	public static class startの年月日指定の項目に正常な値を指定 {
		@DataPoints
		public static String[][] VALID_PARAM = {{"-start", "20140101"}
												,{"-start", "20140228"}
												,{"-start", "20160229"}
												,{"-start", "20141001"}
												,{"-start", "20141231"}
												,{"-start", "19700101"}
												,{"-start", "29991231"}};
		
		@Theory
		public void validateResultが成功になる(String[] args) {
			ArgumentsUtil arguments = new ArgumentsUtil(args);
			int expected = 0;
			int result = arguments.getValidateResult();
			assertThat(result, is(expected));
			Calendar calDate = Calendar.getInstance();
			calDate.setTime(arguments.getStartByDate());
			expected = Integer.parseInt(args[1].substring(0, 4));
			result = calDate.get(Calendar.YEAR);
			assertThat(result, is(expected));
			expected = Integer.parseInt(args[1].substring(4, 6));
			result = calDate.get(Calendar.MONTH) + 1;
			assertThat(result, is(expected));
			expected = Integer.parseInt(args[1].substring(6, 8));
			result = calDate.get(Calendar.DATE);
			assertThat(result, is(expected));
		}
	}
	
	@RunWith(Theories.class)
	public static class endの年月日指定の項目に正常な値を指定 {
		@DataPoints
		public static String[][] VALID_PARAM = {{"-end", "20140101"}
												,{"-end", "20140228"}
												,{"-end", "20160229"}
												,{"-end", "20141001"}
												,{"-end", "20141231"}
												,{"-end", "19700101"}
												,{"-end", "29991231"}};
		
		@Theory
		public void validateResultが成功になる(String[] args) {
			ArgumentsUtil arguments = new ArgumentsUtil(args);
			int expected = 0;
			int result = arguments.getValidateResult();
			assertThat(result, is(expected));
			Calendar calDate = Calendar.getInstance();
			calDate.setTime(arguments.getEndByDate());
			expected = Integer.parseInt(args[1].substring(0, 4));
			result = calDate.get(Calendar.YEAR);
			assertThat(result, is(expected));
			expected = Integer.parseInt(args[1].substring(4, 6));
			result = calDate.get(Calendar.MONTH) + 1;
			assertThat(result, is(expected));
			expected = Integer.parseInt(args[1].substring(6, 8));
			result = calDate.get(Calendar.DATE);
			assertThat(result, is(expected));
		}
	}
}
