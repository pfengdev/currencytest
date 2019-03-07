package test.java;

import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import test.java.client.Client;
import test.java.pojo.Budget;
import test.java.pojo.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BananaBudgetTest {

	private static final String BASE_URL = "https://bananabudget.azurewebsites.net/?startDate=%s&numberOfDays=%s";

	private static final String VALID_DATE = "02-10-2019";
	private static final String VALID_NUM_DAYS = "20";

	private static final int MAX_NUM = 365;
	private static final int MIN_NUM = 1;

	private static final BigDecimal WEEKEND_COST = new BigDecimal("0.00");
	private static final BigDecimal FIRST_WEEKDAY_COST = new BigDecimal("0.05");
	private static final BigDecimal SECOND_WEEKDAY_COST = new BigDecimal("0.10");
	private static final BigDecimal THIRD_WEEKDAY_COST = new BigDecimal("0.15");
	private static final BigDecimal FOURTH_WEEKDAY_COST = new BigDecimal("0.20");
	private static final BigDecimal REMAINING_WEEKDAY_COST = new BigDecimal("0.25");
	private static final BigDecimal FIRST_WEEK_COST = new BigDecimal("0.25");
	private static final BigDecimal SECOND_WEEK_COST = new BigDecimal("0.50");
	private static final BigDecimal THIRD_WEEK_COST = new BigDecimal("0.75");
	private static final BigDecimal FOURTH_WEEK_COST = new BigDecimal("1.00");
	private static final BigDecimal THIRTY_ONE_DAYS_COST = new BigDecimal("3.25");
	private static final BigDecimal THIRTY_TWO_DAYS_COST = new BigDecimal("3.30");
	private static final BigDecimal ONE_YEAR_COST = new BigDecimal("35.25");
	private static final BigDecimal LEAP_YEAR_COST = new BigDecimal("35.50");

	private static final String INVALID_START_DATE = "Invalid startDate";
	private static final String INVALID_NUM_DAYS = "Invalid numberOfDays";
	private static final String MUST_PROVIDE_PARAMS = "Must provide startDate and numberOfDays";

	private Client client = new Client();

	@BeforeEach
	public void setUp() throws Exception {

	}

	@AfterEach
	public void tearDown() throws Exception {

	}

	private void assertMustProvideParams(ErrorResponse response) {
		assertErrorHelper(response, HttpStatus.BAD_REQUEST, MUST_PROVIDE_PARAMS);
	}

	private void assertInvalidStartDate(ErrorResponse response) {
		assertErrorHelper(response, HttpStatus.BAD_REQUEST, INVALID_START_DATE);
	}

	private void assertInvalidNumDays(ErrorResponse response) {
		assertErrorHelper(response, HttpStatus.BAD_REQUEST, INVALID_NUM_DAYS);
	}

	private void assertCostEquals(ResponseEntity<Budget> response, BigDecimal cost) {
		assertNotNull(response);
		assertEquals(response.getHeaders().getContentType(), MediaType.APPLICATION_JSON_UTF8);
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		Budget budget = (Budget)response.getBody();
		assertEquals(budget.getTotalCostValue(), cost);
	}

	private void assertErrorHelper(ErrorResponse response, HttpStatus statusCode, String error) {
		assertNotNull(response);
		assertEquals(response.getStatusCode(), statusCode);
		assertEquals(response.getError(), error);
	}

	@Test
	public void calculateBudget_HyphenDelimiterInDate_OK() {
		String date = "02-10-2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void calculateBudget_ForwardSlashDelimiterInDate_OK() {
		String date = "02/10/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void calculateBudget_PeriodDelimiterInDate_OK() {
		String date = "02.10.2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void calculateBudget_CommaDelimiterInDate_OK()  {
		String date = "02,10,2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void calculateBudget_MultipleDelimitersInDate_OK()  {
		String date = "02,//10,....2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void calculateBudget_BackSlashDelimiterInDate_MustProvideParams() {
		String date = "02\\10\\2019";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	@Test
	public void calculateBudget_BlankNumDays_MustProvideParams() {
		String blank = "";
		ErrorResponse response = client.getError(String.format(BASE_URL, VALID_DATE, blank));
		assertMustProvideParams(response);
	}

	@Test
	public void calculateBudget_BlankStartDate_MustProvideParams() {
		String blank = "";
		ErrorResponse response = client.getError(String.format(BASE_URL, blank, VALID_NUM_DAYS));
		assertMustProvideParams(response);
	}

	//Fail
	@Test
	public void calculateBudget_AbbrevNameMonthInDate_InvalidStartDate() {
		String date = "Feb.10,2019";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	//Fail
	@Test
	public void calculateBudget_AbbrevMonthNoDelimiterDDYYYY_InvalidStartDate() {
		String date = "Feb082019";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	//Fail
	@Test
	public void calculateBudget_AbbrevMonthNoDelimiterDDYY_InvalidStartDate() {
		String date = "Feb0819";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	//Fail
	@Test
	public void calculateBudget_MDDYYYYDateFormat_InvalidStartDate() {
		String date = "2/10/2019";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	//Fail
	@Test
	public void calculateBudget_MMDYYYYDateFormat_InvalidStartDate() {
		String date = "02/9/2019";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	//Fail
	@Test
	public void calculateBudget_MMDDYYDateFormat_InvalidStartDate() {
		String date = "02/10/19";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	//Fail
	@Test
	public void calculateBudget_MDYYDateFormat_InvalidStartDate() {
		String date = "2/9/19";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	//Fail
	@Test
	public void calculateBudget_YYYYMMDDDateFormat_InvalidStartDate() {
		String date = "2019/10/10";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	@Test
	public void calculateBudget_ThreeDigitsInYear_InvalidStartDate() {
		String date = "01/01/200";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	@Test
	public void calculateBudget_NoDelimiterMMDDYYYY_InvalidStartDate() {
		String date = "01012020";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	//Fail
	@Test
	public void calculateBudget_NoDelimiterMMDDYY_InvalidStartDate() {
		String date = "010120";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	//Fail
	@Test
	public void calculateBudget_NoDelimiterLeadingZeroes_InvalidStartDate() {
		String date = "00012020";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	//Fail
	@Test
	public void calculateBudget_LeadingZeroesInMonth_InvalidStartDate() {
		String date = "0001/01/2020";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	//Fail
	@Test
	public void calculateBudget_LeadingZeroesInDay_InvalidStartDate() {
		String date = "01/0001/2020";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	//Fail
	@Test
	public void calculateBudget_LeadingZeroesInYear_InvalidStartDate() {
		String date = "01/01/0002020";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	@Test
	public void calculateBudget_LeadingWhitespaceInMonth_OK() {
		String date = "    01/01/2020";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void calculateBudget_LeadingWhitespaceInDay_OK() {
		String date = "01/    01/2020";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void calculateBudget_LeadingWhitespaceInYear_OK() {
		String date = "01/01/    2020";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void calculateBudget_MonthIsZero_InvalidStartDate() {
		String date = "00/01/2020";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	@Test
	public void calculateBudget_DayIsZero_InvalidStartDate() {
		String date = "01/00/2020";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	//Fail
	@Test
	public void calculateBudget_YearIsZero_InvalidStartDate() {
		String date = "01/01/0000";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	//Fail
	@Test
	public void calculateBudget_MonthIsNegative_InvalidStartDate() {
		String date = "-00/01/2020";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	@Test
	public void calculateBudget_DayIsNegative_InvalidStartDate() {
		String date = "01/-00/2020";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	@Test
	public void calculateBudget_YearIsNegative_InvalidStartDate() {
		String date = "01/01/-2019";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	@Test
	public void calculateBudget_MonthGreater12_InvalidStartDate() {
		String date = "13/01/2020";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	@Test
	public void calculateBudget_DayIsString_InvalidStartDate() {
		String date = "01/one/2020";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	@Test
	public void calculateBudget_YearIsString_InvalidStartDate() {
		String date = "01/01/twothousand";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	@Test
	public void calculateBudget_LeadingZeroesInNumDays_OK() {
		String numDays = "00020";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, VALID_DATE, numDays));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void calculateBudget_NumDaysIsNegative_InvalidStartDate() {
		String numDays = "-1";
		ErrorResponse response = client.getError(String.format(BASE_URL, VALID_DATE, numDays));
		assertInvalidNumDays(response);
	}

	@Test
	public void calculateBudget_NumDaysLessThanMin_InvalidStartDate() {
		int num = MIN_NUM - 1;
		String numDays = "" + num;
		ErrorResponse response = client.getError(String.format(BASE_URL, VALID_DATE, numDays));
		assertInvalidNumDays(response);
	}

	//Fail
	@Test
	public void calculateBudget_NumDaysGreaterMax_InvalidStartDate() {
		int num = MAX_NUM + 1;
		String numDays = "" + num;
		ErrorResponse response = client.getError(String.format(BASE_URL, VALID_DATE, numDays));
		assertInvalidNumDays(response);
	}

	@Test
	public void calculateBudget_NumDaysIsString_InvalidStartDate() {
		String numDays = "twenty";
		ErrorResponse response = client.getError(String.format(BASE_URL, VALID_DATE, numDays));
		assertInvalidNumDays(response);
	}

	@Test
	public void calculateBudget_28DaysInFeb2019_OK() {
		String date = "02/28/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	//Fail
	@Test
	public void calculateBudget_29DaysInFeb2019_InvalidStartDate() {
		String date = "02/29/2019";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	@Test
	public void calculateBudget_29DaysInFeb2020_OK() {
		String date = "02/29/2020";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	//for jan, mar, may, etc.
	@Test
	public void calculateBudget_31DaysInMonth_OK() {
		String date = "01/31/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		date = "03/31/2019";
		response = client.getBudget(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		date = "05/31/2019";
		response = client.getBudget(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		date = "07/31/2019";
		response = client.getBudget(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		date = "09/31/2019";
		response = client.getBudget(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		date = "011/31/2019";
		response = client.getBudget(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	//for jan, mar, may, etc.
	@Test
	public void calculateBudget_32DaysInMonth_InvalidStartDate() {
		String date = "01/32/2019";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
		date = "03/32/2019";
		response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
		date = "05/32/2019";
		response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
		date = "07/32/2019";
		response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
		date = "09/32/2019";
		response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
		date = "011/32/2019";
		response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	//for apr, jun, aug, etc.
	@Test
	public void calculateBudget_30DaysInMonth_OK() {
		String date = "04/30/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		date = "06/30/2019";
		response = client.getBudget(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		date = "08/30/2019";
		response = client.getBudget(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		date = "10/30/2019";
		response = client.getBudget(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		date = "12/30/2019";
		response = client.getBudget(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	//Fail
	//for apr, jun, aug, etc.
	@Test
	public void calculateBudget_31DaysInMonth_InvalidStartDate() {
		String date = "04/31/2019";
		ErrorResponse response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
		date = "06/31/2019";
		response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
		date = "08/31/2019";
		response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
		date = "10/31/2019";
		response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
		date = "12/31/2019";
		response = client.getError(String.format(BASE_URL, date, VALID_NUM_DAYS));
		assertInvalidStartDate(response);
	}

	@Test
	public void calculateBudget_TestWeekend_CostIsZero() {
		String weekend = "03/09/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, weekend, 1));
		assertCostEquals(response, WEEKEND_COST);
	}

	@Test
	public void calculateBudget_TestFirstWeekdayInFirst7Days_PriceMatches() {
		String weekday = "03/01/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, weekday, 1));
		assertCostEquals(response, FIRST_WEEKDAY_COST);
	}

	@Test
	public void calculateBudget_TestLastWeekdayInFirst7Days_PriceMatches() {
		String weekday = "03/07/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, weekday, 1));
		assertCostEquals(response, FIRST_WEEKDAY_COST);
	}

	@Test
	public void calculateBudget_TestFirstWeekdayInSecond7Days_PriceMatches() {
		String weekday = "03/08/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, weekday, 1));
		assertCostEquals(response, SECOND_WEEKDAY_COST);
	}

	@Test
	public void calculateBudget_TestLastWeekdayInSecond7Days_PriceMatches() {
		String weekday = "03/14/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, weekday, 1));
		assertCostEquals(response, SECOND_WEEKDAY_COST);
	}

	@Test
	public void calculateBudget_TestFirstWeekdayInThird7Days_PriceMatches() {
		String weekday = "03/15/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, weekday, 1));
		assertCostEquals(response, THIRD_WEEKDAY_COST);
	}

	@Test
	public void calculateBudget_TestLastWeekdayInThird7Days_PriceMatches() {
		String weekday = "03/21/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, weekday, 1));
		assertCostEquals(response, THIRD_WEEKDAY_COST);
	}

	@Test
	public void calculateBudget_TestFirstWeekdayInFourth7Days_PriceMatches() {
		String weekday = "03/22/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, weekday, 1));
		assertCostEquals(response, FOURTH_WEEKDAY_COST);
	}

	@Test
	public void calculateBudget_TestLastWeekdayInFourth7Days_PriceMatches() {
		String weekday = "03/28/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, weekday, 1));
		assertCostEquals(response, FOURTH_WEEKDAY_COST);
	}

	@Test
	public void calculateBudget_TestFirstWeekdayRemainingDays_PriceMatches() {
		String weekday = "03/29/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, weekday, 1));
		assertCostEquals(response, REMAINING_WEEKDAY_COST);
	}

	@Test
	public void calculateBudget_TestLastWeekdayRemainingDays_PriceMatches() {
		//March 31 2019 is on a Sunday, using May instead
		String weekday = "05/31/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, weekday, 1));
		assertCostEquals(response, REMAINING_WEEKDAY_COST);
	}

	@Test
	public void calculateBudget_FirstWeek_PriceMatches() {
		String day = "03/01/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, day, 7));
		assertCostEquals(response, FIRST_WEEK_COST);
	}

	@Test
	public void calculateBudget_SecondWeek_PriceMatches() {
		String day = "03/08/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, day, 7));
		assertCostEquals(response, SECOND_WEEK_COST);
	}

	@Test
	public void calculateBudget_ThirdWeek_PriceMatches() {
		String day = "03/15/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, day, 7));
		assertCostEquals(response, THIRD_WEEK_COST);
	}

	@Test
	public void calculateBudget_FourthWeek_PriceMatches() {
		String day = "03/22/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, day, 7));
		assertCostEquals(response, FOURTH_WEEK_COST);
	}

	@Test
	public void calculateBudget_31Days_PriceMatches() {
		String day = "01/01/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, day, 31));
		assertCostEquals(response, THIRTY_ONE_DAYS_COST);
	}

	@Test
	public void calculateBudget_32Days_PriceMatches() {
		String day = "01/01/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, day, 32));
		assertCostEquals(response, THIRTY_TWO_DAYS_COST);
	}

	@Test
	public void calculateBudget_OneYear_PriceMatches() {
		String day = "01/01/2019";
		ResponseEntity<Budget> response = client.getBudget(String.format(BASE_URL, day, 365));
		assertCostEquals(response, ONE_YEAR_COST);
	}


}