package profile.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Keys {
	
	@XmlElement(name = "key_field")
	private List<String> key_field;

	public List<String> getKeyFields() {
		return key_field;
	}

	public void setKeyField(List<String> key_field) {
		this.key_field = key_field;
	}


}
