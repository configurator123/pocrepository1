/*
 * XPathPOC.java
 * 
 * Description:
 * 
 * Demonstrates the ability of a Java program to query a local XML file 
 * using XPath queries.
 * 
 * Author: Trent Armstrong | trent.armstrong@hitachiconsulting.com
 * 
 */

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XPathPOC {

	// Executes several XPath queries.
	public static void main(String[] args) throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException {
		
		XPathPOC process = new XPathPOC();

		// List the info for all Tundra Models
		process.query("//Car[Model='Tundra']/*/text()");

		System.out.println("--------------");

		// List all red cars.
		process.query("//Car[Color='Red']/*/text()");

		System.out.println("--------------");

		// List the VIN number attribute for all red cars.
		process.query("//Car[Color='Red']/@vin");

	}

	/**
	 * This method builds a DOM representation of the Toyota xml file
	 * and evaluates the XPath expression given to it. The results are
	 * printed to the console.
	 * 
	 * @param exp The XPath expression to evaluate against the xml file.
	 * 
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public void query(String exp) throws ParserConfigurationException,
			SAXException, IOException, XPathExpressionException {

		// Read and parse the xml file into a DOM representation.
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc = null;
		XPathExpression expr = null;
		builder = factory.newDocumentBuilder();
		doc = builder.parse("Toyota.xml");

		// Create a XPathFactory
		XPathFactory xFactory = XPathFactory.newInstance();

		// Create a XPath object
		XPath xpath = xFactory.newXPath();

		// Compile the XPath expression
		expr = xpath.compile(exp);

		// Run the query and get a nodeset
		Object result = expr.evaluate(doc, XPathConstants.NODESET);

		// Cast the result to a DOM NodeList and print the results.
		NodeList nodes = (NodeList) result;
		for (int i = 0; i < nodes.getLength(); i++) {
			System.out.println(nodes.item(i).getNodeValue());
		}

	}

}
