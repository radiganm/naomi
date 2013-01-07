// @file     XmlUtilities.groovy
// @author   Mac Radigan

package org.radigan.system.utilities

// DOM
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Document
import org.w3c.dom.Element
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.dom.DOMResult
import javax.xml.transform.stream.StreamSource
import javax.xml.transform.stream.StreamResult
// XPATH
import org.apache.xpath.domapi.XPathEvaluatorImpl
import org.w3c.dom.xpath.XPathEvaluator
import org.w3c.dom.xpath.XPathNSResolver
import org.w3c.dom.xpath.XPathResult
//import org.xml.sax.SAXException
// XSL
import javax.xml.transform.TransformerFactory
import net.sf.saxon.TransformerFactoryImpl
import javax.xml.transform.Templates
// list of XSL transformers:
// GOOD // import net.sf.saxon.TransformerFactoryImpl
// OK   // import org.apache.xalan.processor.TransformerFactoryImpl
// BAD  // import com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl
// BAD  // import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl
//import org.ccil.cowan.tagsoup.Parser

public class XmlUtilities {

  public static newDocument(xmldata) {
    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance()
    docBuilderFactory.setNamespaceAware(true)
    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder()
    ByteArrayInputStream bais = new ByteArrayInputStream(xmldata.getBytes())
    Document xml = docBuilder.parse(bais)
    return xml
  }

  public static docToStreamSource(doc) {
    def factory = net.sf.saxon.TransformerFactoryImpl.newInstance()
    def streamResult = new StreamResult( new java.io.ByteArrayOutputStream() )
    def transformer = factory.newTransformer()
    transformer.transform( new DOMSource(doc), streamResult )
    def os = streamResult.getOutputStream()
    def baos = (ByteArrayOutputStream)os
    def is = new ByteArrayInputStream( baos.toByteArray() )
    def streamSource = new StreamSource( is )
    return streamSource
  }

  public static xslt(xml,xsl) {
    def factory = net.sf.saxon.TransformerFactoryImpl.newInstance()
    def domResult = new DOMResult()
    def domSource = new DOMSource(xml);
    def xslStreamSource = docToStreamSource(xsl)
    def templates = factory.newTemplates( xslStreamSource )
    def transformer = templates.newTransformer()
    transformer.transform(domSource, domResult)
    return domResult.getNode()
  }

  public static parseToString(node) {
    def factory = net.sf.saxon.TransformerFactoryImpl.newInstance()
    def transformer = factory.newTransformer()
    def stringWriter = new StringWriter(128);
    transformer.transform(new DOMSource(node), new StreamResult(stringWriter))
    StringBuffer buffer =  stringWriter.getBuffer()
    return buffer.toString()
  }

  public static findNodes(node, xpath) {
    def xpathEvaluator = new XPathEvaluatorImpl( node )
    def xpathNSResolver = xpathEvaluator.createNSResolver( node )
    def xpathResult = (XPathResult)xpathEvaluator.evaluate( xpath, node, xpathNSResolver, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null)
    return xpathResult
  }

  public static findSingleNode(node, xpath) {
    return findNodes(node, xpath).snapshotItem(0)
  }

/*
  public static download(url) {
    def location = url.toURL()
    def sb = new StringBuffer()
    def input = new BufferedReader(new InputStreamReader(location.openStream()))
    def inputLine
    while ((inputLine = input.readLine()) != null) sb.append(inputLine)
    input.close()
    def tagSoupParser = new org.ccil.cowan.tagsoup.Parser()
    def htmlParser = new XmlSlurper(tagSoupParser)
    def xhtml = htmlParser.parseText(sb.toString())
    def cleanHtmlWriter = new StringWriter()
    cleanHtmlWriter << new groovy.xml.StreamingMarkupBuilder().bind( {
      mkp.declareNamespace( 'html': 'http://www.w3.org/1999/xhtml' )
      mkp.yield( xhtml ) }
    )
    return newDocument(cleanHtmlWriter.toString().trim())
  }
*/

}

// *EOF*
