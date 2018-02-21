/**
 * 
 */
package net.mdp3.java.rpi.ledtable.effects;

import net.mdp3.java.util.xml.DomXml;
import net.mdp3.java.util.xml.XmlHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Mikel
 *
 */
public enum EffectMode implements DomXml {
	OFF(                                          "Off"),
	PULSE1(PulseEffect.class,                     "Pulse Effect"),
	//PULSE2(PulseEffect.class, "Pulse 2 Not Available"),
	RAW,
	SET_COLOR(SetColorEffect.class,               "Set Color"),
	//PULSE_RANDOM(PulseEffect.class),
	IMAGE(                                        "Set Image"),//TODO Create Set Image Effect
	ANIMATION(AnimationEffect.class, 			  "Animate"),
	//MIDI(MidiEffect.class, 						  "Midi Reactive Effect (NA)"),
	RAINBOW(RainbowEffect.class, 				  "Rainbow Colors"),
	IMAGE_SCAN(ImageScanEffect.class, 			  "Image Scan"),
	//ANIM_TRANS(TransparencyAnimationEffect.class, "Image with Transparency"),
	RANDOM_FILL(RandomFillEffect.class, 		  "Random Fill");
	
	public static final String NODE_NAME = "EffectMode";
	public static final String NODE_MODE_NAME = "ModeName";
	public static final String NODE_MODE_DESC = "ModeDescName";
	public static final String NODE_MODE_CLASS = "ModeClass";
	
	Class<? extends Effect> effectClass;
	String name = "";
	
	private EffectMode() {
		this(null, "");
	}
	
	private EffectMode(String name) {
		this(null, name);
	}
	
	private EffectMode(Class<? extends Effect> effectClass) {
		this(effectClass, "");
	}
	
	private EffectMode(Class<? extends Effect> effectClass, String name) {
		this.effectClass = effectClass;
		this.name = name;
	}
	
	public Class<? extends Effect> getEffectClass() {
		return this.effectClass;
	}
	
	public String getDescName() {
		//if (this.name != null && this.name.length() > 0)
			return this.name;
		//else return this.name();
	}

	@Override
	public Element toXml(Document doc) {
		Element effectModeE = doc.createElement(EffectMode.NODE_NAME);
		
		effectModeE.appendChild(XmlHelper.newTextElement(doc, EffectMode.NODE_MODE_NAME, this.name()));
		if (this.getDescName() != null)
			effectModeE.appendChild(XmlHelper.newTextElement(doc, EffectMode.NODE_MODE_DESC, this.getDescName()));
		if (this.getEffectClass() != null)
			effectModeE.appendChild(XmlHelper.newTextElement(doc, EffectMode.NODE_MODE_CLASS, this.getEffectClass().getName()));
		
		return effectModeE;
	}

	@Override
	public void fromXml(Element e) throws Exception {
		//Not implemented
	}
}
