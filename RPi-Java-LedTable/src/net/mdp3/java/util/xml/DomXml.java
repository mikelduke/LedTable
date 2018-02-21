/**
 * 
 */
package net.mdp3.java.util.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Mikel
 *
 */
public interface DomXml {
	public Element toXml(Document doc);
	public void fromXml(Element e) throws Exception;
}
