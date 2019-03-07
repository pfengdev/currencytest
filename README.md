# currencytest
Currency Tests

Please use mvn clean test to run tests.
Using mvn v3.6.0

Assumptions:
I'm assuming the PDF contains the hard requirements. Many of the tests fail against the API, but match the PDF, and I've marked them with "//Fail"
I'm assuming various delimiters are allowed as long as it's MM/DD/YYY, i.e. "2-10-2017" works but so does "2/10/2017"
I would add a test for calculating a leap year, but the number of days is "supposed" to max out at 365.
