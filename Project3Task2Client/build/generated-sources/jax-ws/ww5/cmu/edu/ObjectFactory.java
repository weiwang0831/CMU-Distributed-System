
package ww5.cmu.edu;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ww5.cmu.edu package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ParseXMLResponse_QNAME = new QName("http://edu.cmu.ww5/", "parseXMLResponse");
    private final static QName _ParseXML_QNAME = new QName("http://edu.cmu.ww5/", "parseXML");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ww5.cmu.edu
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ParseXMLResponse }
     * 
     */
    public ParseXMLResponse createParseXMLResponse() {
        return new ParseXMLResponse();
    }

    /**
     * Create an instance of {@link ParseXML }
     * 
     */
    public ParseXML createParseXML() {
        return new ParseXML();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParseXMLResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://edu.cmu.ww5/", name = "parseXMLResponse")
    public JAXBElement<ParseXMLResponse> createParseXMLResponse(ParseXMLResponse value) {
        return new JAXBElement<ParseXMLResponse>(_ParseXMLResponse_QNAME, ParseXMLResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParseXML }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://edu.cmu.ww5/", name = "parseXML")
    public JAXBElement<ParseXML> createParseXML(ParseXML value) {
        return new JAXBElement<ParseXML>(_ParseXML_QNAME, ParseXML.class, null, value);
    }

}
