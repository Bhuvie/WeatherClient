Running the Program:

1.	Import the project folder(bhuvanesh0051WC) into the Eclipse IDE.
2.	Run WeatherClient.java as Java Application.
3.	When the application is run, GUI window opens.
4.	When Get Updates button is pressed, the client will give the weather updates for the location entered by the user in the text fields. 
5.	The text area will display the Dew Point Temperature, Cloud Cover amount, Wind Speed and 12-hour probability of precipitation for the given coordinates.
6.	The Refresh button refreshes the weather updates for the Latitude and Longitude that has been entered by the user previously.
7.	Clicking on the Close Window button of the GUI exits the application.

How the Application works:
1.	The application constructs SOAP request and sends it to the web service.
2.	The web service is accessed using the link: http://graphical.weather.gov/xml/SOAP_server/ndfdXMLserver.php
3.	The SOAP request is a SOAP message that contains the operation to be called(NDFDgen) and the inputs that are required for that operation.
4.	The web service replies with SOAP response that contains the required information in XML format. This is parsed using the Document Builder object, so that the values can be retrieved using Document object.
5.	Then the result is appended to the Text Area in the GUI window.

System Requirement:
•	Windows (any version)
•	Java (minimum 1.7)
•	Eclipse IDE

Limitations:
1.	The Latitude and Longitude values should be entered with correct decimal points. The weather client accepts coordinate inputs that are within USA. 
2.	The SOAP Server link is sometimes slow and may throw Connection timeout error.

Assumptions:

1.	It is assumed that the user enters the correct Latitude and Longitude of the location for which the user needs to see the weather updates.
2.	It is assumed the server responds to the SOAP request made in a timely manner. 

Sample Input Points 
Latitude,Longitude
 61.18, -149.19
 32.66, -97.1
38.99, -77.02
39.70, -104.80
47.6, -122.30
19.69, -155.08 
