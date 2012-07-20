/*
 * ModifyXML.java
 * 
 * Description:
 * 
 * Demonstrates the ability of a Java program to take an existing XML file 
 * and produce a modified version of it using another xml file.
 * 
 * There are three files XML files included in the working directory of this
 * project:
 * 
 * TMS.xml       - A TMS level feature model of a vehicle.
 * GST_Mod.xml   - A dummy GST level modification file where the GST specific changes are stored.
 * GST_Final.xml - The final GST xml file which is the TMS xml with all of the GST changes made.
 * 
 * Author: Trent Armstrong | trent.armstrong@hitachiconsulting.com
 * 
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class ModifyXML {

	// File names of the XML files used.
	private static String TMSxml = "TMS.xml";
	private static String GST_MODxml = "GST_Mod.xml";
	
	
	private static Element TMSrootNode;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			
			// Build the TMS xml document for parsing.
			SAXBuilder builder = new SAXBuilder();
			File xmlFile = new File(TMSxml);

			Document TMSdoc = (Document) builder.build(xmlFile);
			TMSrootNode = TMSdoc.getRootElement();			
			
			// Build the GST xml document for parsing.
			File xmlFileGST = new File(GST_MODxml);

			Document GSTdoc = (Document) builder.build(xmlFileGST);
			Element GSTrootNode = GSTdoc.getRootElement();
			
			
			// Call the method to take the information from the GST xml and
			// modify the existing document.
			mergeXML(GSTrootNode);			
			

			XMLOutputter xmlOutput = new XMLOutputter();

			// Output the merged document to a new file.
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(TMSdoc, new FileWriter("GST_Final.xml"));


			System.out.println("File updated!");
			
		} catch (IOException io) {
			io.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This function uses the GST_Mod file to modify the TMS xml file.
	 * 
	 * @param rootNode The rootNode of the GST_mod file.
	 */
	private static void mergeXML(Element rootNode) {		
		
		Element parent = rootNode.getParentElement();		
		
		// Scan the modifying xml document looking for 'add', 'remove', and 'edit'
		// elements. Below these nodes will be the information to supplement the default
		// TMS xml.
		if (rootNode.getName().equals("add")) {			
			
			addElements(parent.getName(), rootNode.getChildren());
		}
		else if (rootNode.getName().equals("remove")) {
			
			removeElements(parent.getName(), rootNode.getChildren());
		}
		else if (rootNode.getName().equals("edit")) {
			
			editElements(parent.getName(), rootNode.getChildren());
		}
		else if (rootNode.getChildren().size() > 0) {
			for (Element e : rootNode.getChildren())
				mergeXML(e);
		}
			
		
	}
	
	/**
	 * This method adds elements to the XML under the element of the given name.
	 * 
	 * @param elementName The name of the Node to add elements under.
	 * @param elements	  The elements to add.
	 */
	private static void addElements(String elementName, List<Element> elements) {
		
		// Get the node with elementName so we can add children to it.
		Element node = getElement(TMSrootNode, elementName);
		
		// Add the new child elements.
		for (Element e : elements) {
			node.addContent(e.clone());
		}
	}
	
	
	/**
	 * This method removes elements from the XML using the code attribute to find the correct
	 * node to remove.
	 * 
	 * @param elementName
	 * @param elements
	 */
	private static void removeElements(String elementName, List<Element> elements) {
		
		Element currentNode = getElement(TMSrootNode, elementName);
		Element node;
		for (Element e : elements) {
			if ((node = getElementWithCode(currentNode, e.getAttributeValue("code"))) != null) {
				
				// This will remove the node from under it's parent.
				node.detach();				
			}
		}
		
	}
	
	/**
	 * This method edits elements in the TMS xml that are set to be edited in the GST xml.
	 * 
	 * @param elementName
	 * @param elements
	 */
	private static void editElements(String elementName, List<Element> elements) {
		
		Element currentNode = getElement(TMSrootNode, elementName);
		Element node;
		
		for (Element e : elements) {
			if ((node = getElementWithCode(currentNode, e.getAttributeValue("code"))) != null) {
				ArrayList<Element> nodeList = new ArrayList<Element>();
				
				// Remove the original node from the XML.
				nodeList.add(node);
				removeElements(elementName, nodeList);
				nodeList.clear();
				
				// Add the new node to the XML in its place.
				nodeList.add(e);
				addElements(elementName, nodeList);
			}
			
		}
	}
	
	/**
	 * This method returns the node with the given code attribute. It returns null
	 * otherwise.
	 * 
	 * @param rootNode
	 * @return
	 */
	private static Element getElementWithCode(Element rootNode, String code) {
		
		for (Element e : rootNode.getChildren()) {
			if (e.getAttributeValue("code").equals(code))
				return e;
		}
		
		return null;
	}
	
	/**
	 * This method finds and returns the element with the given name. In the situation that
	 * multiple nodes of the same name exist, only the first one is returned. This is 
	 * accomplished via a recursive tree traversal.
	 * 
	 * 
	 * @param root The root node of the xml tree in which the node to find potentially exists.
	 * @param name The name of the node to find.
	 * @return
	 */
	private static Element getElement(Element root, String name) {
		
		if (root.getName().equals(name))
			return root;
		else if (root.getChildren().size() > 0){
			for (Element e : root.getChildren()) {
				Element element = getElement(e, name);
				
				if (element != null)
					return element;
			}
		}
		
		return null;
	}	
	

}
