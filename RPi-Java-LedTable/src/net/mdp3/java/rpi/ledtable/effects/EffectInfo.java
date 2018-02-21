/**
 * 
 */
package net.mdp3.java.rpi.ledtable.effects;

import java.util.ArrayList;
import java.util.Arrays;

import net.mdp3.java.util.xml.DomXml;
import net.mdp3.java.util.xml.XmlHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Data Container for Effect Info and parameter info
 * 
 * @author Mikel
 *
 */
public class EffectInfo implements DomXml {
	private String effectName;
	private String effectDesc;
	
	private ArrayList<EffectInfoParameter> paramInfo = new ArrayList<EffectInfoParameter>();
	
	public static final String NODE_NAME = "EffectInfo";
	public static final String NODE_EFFECT_NAME = "EffectName";
	public static final String NODE_EFFECT_DESC = "EffectDesc";
	
	public EffectInfo(String name, String desc, EffectInfoParameter[] paramInfo) {
		this.setEffectName(name);
		this.setEffectDesc(desc);
		this.setParamInfo(paramInfo);
	}

	public EffectInfoParameter[] getParamInfoAr() {
		return (EffectInfoParameter[]) paramInfo.toArray();
	}
	
	public ArrayList<EffectInfoParameter> getParamInfo() {
		return this.paramInfo;
	}

	public void setParamInfo(EffectInfoParameter[] paramInfo) {
		if (paramInfo != null && paramInfo.length > 0)
			this.paramInfo.addAll(Arrays.asList(paramInfo));
	}
	
	public void setParamInfo(ArrayList<EffectInfoParameter> paramInfoList) {
		if (paramInfoList != null)
			this.paramInfo = paramInfoList;
	}
	
	public void addParameterInfo(EffectInfoParameter newParam) {
		this.paramInfo.add(newParam);
	}
	
	public String getEffectDesc() {
		return effectDesc;
	}

	public void setEffectDesc(String effectDesc) {
		this.effectDesc = effectDesc;
	}

	public String getEffectName() {
		return effectName;
	}

	public void setEffectName(String effectName) {
		this.effectName = effectName;
	}
	
	public String toString() {
		String ret = "";
		
		ret += "Name: " + this.getEffectName();
		ret += "\nDesc: " + this.getEffectDesc();
		
		if (this.paramInfo.size() > 0) {
			ret += "\n\nParameter Info: \n";
			
			for (EffectInfoParameter eip : this.paramInfo) {
				ret += "\n" + eip.toString();
			}
		}
		
		return ret;
	}

	@Override
	public Element toXml(Document doc) {
		Element effectInfoE = doc.createElement(EffectInfo.NODE_NAME);
		
		effectInfoE.appendChild(XmlHelper.newTextElement(doc, EffectInfo.NODE_EFFECT_NAME, this.effectName));
		effectInfoE.appendChild(XmlHelper.newTextElement(doc, EffectInfo.NODE_EFFECT_DESC, this.effectDesc));
		
		Element eipListE = doc.createElement(EffectInfoParameter.NODE_NAME + "List");
		
		for (EffectInfoParameter eip : this.getParamInfo())
			eipListE.appendChild(eip.toXml(doc));
		
		effectInfoE.appendChild(eipListE);
		
		return effectInfoE;
	}

	@Override
	public void fromXml(Element e) throws Exception {
		//Not Used
		throw new Exception("Effect Info fromXml not implemented");
	}
}
