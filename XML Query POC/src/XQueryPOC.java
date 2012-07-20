/*
 * XQueryPOC.java
 * 
 * Description:
 * 
 * Demonstrates the ability of a Java program to query a local XML file 
 * using XQuery. This framework for evaluating XQuery expressions is supported
 * by the Saxon XQJ, an open source implementation.
 * 
 * Author: Trent Armstrong | trent.armstrong@hitachiconsulting.com
 * 
 */

import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;

import net.sf.saxon.xqj.*;

public class XQueryPOC {

	public static void main(String[] args) throws XQException {

		// runToyotaTests();

		runFeatureModelTests();

	}

	/**
	 * Runs a series of demo XQuery expressions against the feeature_model 
	 * xml file and prints results to the console.
	 * 
	 * @throws XQException
	 */
	private static void runFeatureModelTests() throws XQException {

		XQueryPOC xQuerier = new XQueryPOC();
		String xmlFileName = "feature_model.xml";

		// Return the XML corresponding to the engine configuration.
		xQuerier.query("for $x in doc(\"" + xmlFileName	+ "\")/model/features/trim " + 
				       "return $x/engine ");

		System.out.println("-------------------------------");

		// Returns all model names that include that have 4 wheel drive.
		xQuerier.query("for $x in doc(\"" + xmlFileName	+ "\")/model/features/trim/modelCode/feature " +
				       "where fn:contains($x/data(@iffs), '4WD')" +
				       "return $x/data(@name) ");
		
		
		
		/////////////////////////////////////////////////////////////////////
		//
		// The capabilities of XQuery go way beyond the two simple examples
		// demonstrated here. This program exists just to prove the ability
		// to query xml almost as if it were a relational database.
		//
		////////////////////////////////////////////////////////////////////
		 
		 
	}

	// Runs a series of queries on the Toyota.xml file.
	private static void runToyotaTests() throws XQException {
		String xmlFileName = "Toyota.xml";

		XQueryPOC xQuerier = new XQueryPOC();

		// Prints subtrees where the car color is Red.
		xQuerier.query("doc(\"" + xmlFileName + "\")/Toyota/Car[Color='Red']");

		System.out.println("----------------------------------------");

		// Prints subtrees where the car color is Red ordered in alphanumeric
		// order
		// based on Model name using a FLWOR statement. This is similar to a SQL
		// statement.
		xQuerier.query("for $x in doc(\"" + xmlFileName + "\")/Toyota/Car "
				+ "where $x/Color='Red' " + "order by $x/Model " + "return $x");

		System.out.println("----------------------------------------");

		// Returns the VIN numbers of all Red Cars. The VIN Number is an
		// attribute in the
		// xml.
		xQuerier.query("for $x in doc(\"" + xmlFileName + "\")/Toyota/Car "
				+ "where $x/Color='Red' " + "return $x/data(@vin)");

		System.out.println("----------------------------------------");
	}

	// Makes a connection to the XQuery engine and prints the results of the
	// query to the console.
	private void query(String exp) throws XQException {

		// Establish a connection and execute the query.
		XQDataSource dataSource = new SaxonXQDataSource();
		XQConnection conn = dataSource.getConnection();
		XQPreparedExpression expr = conn.prepareExpression(exp);
		XQResultSequence result = expr.executeQuery();

		// Print the results.
		while (result.next()) {
			System.out.println(result.getItemAsString(null));
		}
	}

}
