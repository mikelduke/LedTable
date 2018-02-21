package net.mdp3.java.rpi.ledtable.effects;

import net.mdp3.java.util.xml.DomXml;
import net.mdp3.java.util.xml.XmlHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Data container for Effect Parameter Info
 * 
 * @author Mikel
 *
 */
public class EffectInfoParameter implements DomXml {
	private String paramName;
	private String paramDesc;
	private EffectInfoParameterType paramDataType;
	private String[] paramValues;
	
	private int minValue = 0;
	private int maxValue = 0;

	public static final String NODE_NAME = "EffectInfoParameter";
	public static final String NODE_EIP_NAME = "Name";
	public static final String NODE_EIP_DESC = "Desc";
	public static final String NODE_EIP_DATATYPE = "DataType";
	public static final String NODE_EIP_MIN = "MinValue";
	public static final String NODE_EIP_MAX = "MaxValue";
	public static final String NODE_EIP_VALUE = "Value"; //TODO Implement taking an array of values
	
	public static enum EffectInfoParameterType {
		INT,
		BOOL,
		STRING,
		SELECT, //probably be used for most string eips, switches to use String Array
		FOLDER,
		FILE
	}
	
	//TODO Add taking in values list for String types
	public EffectInfoParameter(String name, String desc, EffectInfoParameterType dataType) {
		this(name, desc, dataType, 0, 0);
	}
	
	public EffectInfoParameter(String name, String desc, String[] valuesAr) {
		this(name, desc, EffectInfoParameterType.SELECT, 0, 0);
		this.setValues(valuesAr);
	}
	
	public EffectInfoParameter(String name, String desc, EffectInfoParameterType dataType, int min, int max) {
		this.setParamName(name);
		this.setParamDesc(desc);
		this.setParamDataType(dataType);
		this.setMinValue(min);
		this.setMaxValue(max);
	}
	
	public void setValues(String[] values) {
		this.paramValues = values;
	}
	
	public String[] getValues() {
		return this.paramValues;
	}
	
	public int getMaxValue() {
		return maxValue;
	}
	
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}
	
	public int getMinValue() {
		return minValue;
	}
	
	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}
	
	public EffectInfoParameterType getParamDataType() {
		return paramDataType;
	}
	
	public void setParamDataType(EffectInfoParameterType paramDataType) {
		this.paramDataType = paramDataType;
	}
	
	public String getParamDesc() {
		return paramDesc;
	}
	
	public void setParamDesc(String paramDesc) {
		this.paramDesc = paramDesc;
	}
	
	public String getParamName() {
		return paramName;
	}
	
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	
	public String toString() {
		String ret = "";
		
		ret += "Name: " + this.getParamName();
		ret += "\nDesc: " + this.getParamDesc();
		ret += "\nData Type: " + this.getParamDataType();
		
		if (this.getMinValue() != 0 || this.getMaxValue() != 0) {
			ret += "\nMin Value: " + this.getMinValue();
			ret += "\nMax Value: " + this.getMaxValue();
		}
		
		return ret;
	}

	@Override
	public Element toXml(Document doc) {
		Element eipE = doc.createElement(EffectInfoParameter.NODE_NAME);
		
		eipE.appendChild(XmlHelper.newTextElement(doc, EffectInfoParameter.NODE_EIP_NAME, this.paramName));
		eipE.appendChild(XmlHelper.newTextElement(doc, EffectInfoParameter.NODE_EIP_DESC, this.paramDesc));
		eipE.appendChild(XmlHelper.newTextElement(doc, EffectInfoParameter.NODE_EIP_DATATYPE, this.paramDataType.toString()));
		
		if (this.minValue != 0 || this.maxValue != 0) {
			eipE.appendChild(XmlHelper.newTextElement(doc, EffectInfoParameter.NODE_EIP_MIN, this.minValue));
			eipE.appendChild(XmlHelper.newTextElement(doc, EffectInfoParameter.NODE_EIP_MAX, this.maxValue));
		} else {
			if (this.paramValues != null && this.paramValues.length > 0) {
				Element eipValuesListE = doc.createElement(EffectInfoParameter.NODE_EIP_VALUE + "List");
				
				for (String value : this.paramValues) {
					eipValuesListE.appendChild(XmlHelper.newTextElement(doc, NODE_EIP_VALUE, value));
				}
				
				eipE.appendChild(eipValuesListE);
			}
		}
		
		return eipE;
	}

	@Override
	public void fromXml(Element e) throws Exception {
		//Not Used
		throw new Exception("fromXml for EffectInfoParameter is not used");
	}
}
