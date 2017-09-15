

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Bhuvanesh Rajakarthikeyan ID:1001410051 on 4/27/2017.
 */
public class WeatherClient extends JFrame {
    private static JFrame mainFrame;
    private static JPanel controlPanel;
    private static JScrollPane scrollPane;
    private static JTextArea taMsg;
    private static JButton bGetUpdates,bRefresh;
    private static JLabel jlbl,lblLat,lblLong;
    private static JTextField txtLat,txtLong;
    private static String refreshLatitude,refreshLongitude;
    public static void main(String[] args)
    {
        WeatherClient obj =  new WeatherClient();
    }
    public WeatherClient()
    {
        prepareGUI();                              //GUI is reused from Lab 2 Assignment
    }


    //Prepare the SOAP request to be sent.
    //Get latitude and longitude as inputs, builds a SOAP message and returns the SOAP message.
    private static SOAPMessage constructSOAPRequest(String latitude, String longitude) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // SOAP Envelope that contains the namespace declarations that are required
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        soapEnvelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        soapEnvelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        soapEnvelope.addNamespaceDeclaration("SOAP-ENC", "http://schemas.xmlsoap.org/soap/encoding/");
        soapEnvelope.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");


        // SOAP Body that contains the operation to be called and the inputs that are needed.
        SOAPBody soapBody = soapEnvelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("NDFDgen");       //SOAP operation to be called
        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("latitude");     //Input elements are added as Child inside the NDFDgen
        soapBodyElem1.addTextNode(latitude);                                       //One by one each inputs that are defined in
        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("longitude");          //WebService details are added as child to NDFDgen operation
        soapBodyElem2.addTextNode(longitude);
        SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("product");
        soapBodyElem3.addTextNode("time-series");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");       //The start and end date are to be sent in XSD:DateTime
        String date = sdf.format(new Date());                                                   //format, hence it is converted and added as input.
        SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("startTime");
        soapBodyElem4.addTextNode(date);
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
        String date1 = sdf.format(cal.getTime());
        SOAPElement soapBodyElem5 = soapBodyElem.addChildElement("endTime");
        soapBodyElem5.addTextNode(date1);
        SOAPElement soapBodyElem6 = soapBodyElem.addChildElement("Unit");
        soapBodyElem6.addTextNode("m");
        SOAPElement soapBodyElem7 = soapBodyElem.addChildElement("weatherParameters");      //This input weatherParameters contains child elements for itself
        SOAPElement e7temp=soapBodyElem7.addChildElement("dew");                               //which defines what all variables are needed to be sent in SOAP response
        e7temp.addTextNode("TRUE");                                                          //Dew Point temperature,
        SOAPElement e7pop12=soapBodyElem7.addChildElement("pop12");                            //12 hour probability of precipitaion
        e7pop12.addTextNode("TRUE");                                                               //Cloud cover amount and wind speed
        SOAPElement e7sky=soapBodyElem7.addChildElement("sky");                                        //are the variables that are included in this SOAP request .
        e7sky.addTextNode("TRUE");
        SOAPElement e7wspd=soapBodyElem7.addChildElement("wspd");
        e7wspd.addTextNode("TRUE");

        soapMessage.saveChanges();                                  //Updates this SOAPMessage object with all the above changes that have been made to it.

        /* Print the request message */
        System.out.print("Request SOAP Message: \n");
        soapMessage.writeTo(System.out);
        System.out.println();

        return soapMessage;
    }

    //Method to parse the SOAP response and display the values in GUI
    private static void ProcessandDisplaySOAPResponse(SOAPMessage soapResponse) throws Exception {

        SOAPBody soapbody = soapResponse.getSOAPBody();
        java.util.Iterator updates = soapbody.getChildElements();
        String value="";
        while (updates.hasNext()) {                             //The loop gets the XML information from SOAP response
            System.out.println();                                   //and stores it in String value object.
            // The listing and its ID
            SOAPElement update = (SOAPElement)updates.next();
            String status = update.getAttribute("id");
            System.out.println("XML SOAP Response: "+status );
            java.util.Iterator i = update.getChildElements();
            while( i.hasNext() ){
                SOAPElement e = (SOAPElement)i.next();
                String name = e.getLocalName();
                value = e.getValue();                                       //getValue retrieves the XML info from dwmlOut of SOAPResponse
                System.out.println( value);
            }
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();        //Create Document Builder to parse XML
        InputSource is = new InputSource(new StringReader(value));         //Create InputSource using Value object created above
        Document doc=builder.parse(is);                                       //Parse it and store it as Document object for easy retrieval
        //Now the values are retrieved using the Tag names of each weather variable and stored in Strings
        String temp =doc.getElementsByTagName("temperature").item(0).getTextContent().trim()+"\n";  //Temperature value is got from XML
        temp=temp.split("\n")[0]+": "+temp.split("\n")[1].trim()+" degree Celsius";              //and then formatted
        String windsp = doc.getElementsByTagName("wind-speed").item(0).getTextContent().trim()+"\n"; //Wind Speed value is got from XML
        windsp=windsp.split("\n")[0]+": "+windsp.split("\n")[1].trim()+" m/s";                          //and then formatted
        String cca = doc.getElementsByTagName("cloud-amount").item(0).getTextContent().trim()+"\n";  //Cloud Cover Amount value is got from XML
        cca=cca.split("\n")[0]+": "+cca.split("\n")[1].trim()+"%";                                      //and then formatted
        String probprec = doc.getElementsByTagName("probability-of-precipitation").item(0).getTextContent().trim()+"\n";  //12 Hour Probability of Precipitation value is got from XML
        probprec=probprec.split("\n")[0]+": "+probprec.split("\n")[1].trim()+"%";                                       //and then formatted
        //Now the above four strings contains the value got as SOAP response in Formatted manner
        //Append all the values to GUI.
        taMsg.append(temp);
        taMsg.append("\n"+cca);
        taMsg.append("\n"+windsp);
        taMsg.append("\n"+probprec);
    }


    //Draws the GUI using Swing components
    private void prepareGUI() {
        mainFrame = new JFrame("My Weather Client ");
        mainFrame.setBounds(0,0,490, 500);  // Overall Size

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                //When the GUI window is closed, the system exits.
                mainFrame.dispose();
                System.exit(0);
            }
        });
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setLocation(dim.width/2-mainFrame.getSize().width/2, dim.height/2-mainFrame.getSize().height/2-100);
        controlPanel = new JPanel();
        controlPanel.setLayout(null);

        taMsg = new JTextArea();
        taMsg.setEditable(false);
        scrollPane = new JScrollPane();
        scrollPane.setViewportView(taMsg);
        scrollPane.setBounds(30, 65, 430, 250);   //Control Text Area Size through this
        mainFrame.add(controlPanel);
        controlPanel.add(scrollPane);
        lblLat=new JLabel("Latitude:");
        lblLat.setBounds(30,330,200,25);
        controlPanel.add(lblLat);
        lblLong=new JLabel("Longitude:");
        lblLong.setBounds(250,330,200,25);
        controlPanel.add(lblLong);
        txtLat=new JTextField();
        txtLat.setBounds(30,350,200,25);
        controlPanel.add(txtLat);
        txtLong=new JTextField();
        txtLong.setBounds(250,350,200,25);
        controlPanel.add(txtLong);
        bGetUpdates = new JButton("Get Updates");
        bGetUpdates.setBounds(30, 400, 110, 25);
        controlPanel.add(bGetUpdates);
        bRefresh = new JButton("Refresh");
        bRefresh.setBounds(160, 400, 110, 25);
        controlPanel.add(bRefresh);
        jlbl=new JLabel("Weather Updates... ");
        jlbl.setBounds(30, 20, 149, 25);
        controlPanel.add(jlbl);
        mainFrame.setVisible(true);
        //GetUpdates button gets the updates for the inputs entered by the user
        //Create SOAP connection, send SOAP request and print the response, then close the connection everytime the button is pressed
        bGetUpdates.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //Create SOAP Connection to send SOAP request
                    //In this action method, the weather updates for location given as inputs by the user is retrieved
//                    Sample Input Points
//                    Latitude,Longitude
//                    61.18, -149.19
//                    32.66, -97.1
//                    38.99, -77.02
//                    39.70, -104.80
//                    47.6, -122.30
//                    19.69, -155.08
                    taMsg.setText("\nRetrieving updates..");taMsg.update(taMsg.getGraphics());
                    SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
                    SOAPConnection soapConnection = soapConnectionFactory.createConnection();
                    // Send SOAP Message to SOAP Server
                    String url = "https://graphical.weather.gov:443/xml/SOAP_server/ndfdXMLserver.php";   //SOAPServer Web Service URL
                    //Construct the SOAP request and call using the above URL, the soapResponse is returned
                    SOAPMessage soapResponse = soapConnection.call(constructSOAPRequest(txtLat.getText(), txtLong.getText()),url);
                    taMsg.append("\nUpdates for Latitude:"+txtLat.getText()+" Longitude: "+ txtLong.getText()+"\n");
                    refreshLatitude=txtLat.getText();                //Latitude and Longitude are stored for use when Refresh button is pressed
                    refreshLongitude=txtLong.getText();
                    //Process the above SOAP Response using this method.
                    ProcessandDisplaySOAPResponse(soapResponse);
                    soapConnection.close();
                }
                catch (Exception ex)
                {
                    taMsg.append("Connection Timed Out.. Please try again..");
                    ex.printStackTrace();
                }
            }
        });
        //Refresh button refreshes the updates for the inputs already stored
        //Create SOAP connection, send SOAP request and print the response, then close the connection everytime the button is pressed
        bRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //Create SOAP Connection to send SOAP request
                    //In this action method, the weather updates for location stored in refreshLatitude and refreshLongitude is retrieved
                    taMsg.append("\n\nRefreshing..");taMsg.update(taMsg.getGraphics());
                    SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
                    SOAPConnection soapConnection = soapConnectionFactory.createConnection();
                    // Send SOAP Message to SOAP Server
                    String url = "https://graphical.weather.gov:443/xml/SOAP_server/ndfdXMLserver.php";   //SOAPServer Web Service URL
                    //Construct the SOAP request and call using the above URL, the soapResponse is returned
                    SOAPMessage soapResponse = soapConnection.call(constructSOAPRequest(refreshLatitude,refreshLongitude),url);
                    taMsg.append("\nUpdates for Latitude:"+refreshLatitude+" Longitude: "+ refreshLongitude+"\n");
                    //Process the above SOAP Response using this method.
                    ProcessandDisplaySOAPResponse(soapResponse);
                    soapConnection.close();
                }
                catch (Exception ex)
                {
                    taMsg.append("Connection Timed Out.. Please try again..");
                    ex.printStackTrace();
                }
            }
        });
    }

}

