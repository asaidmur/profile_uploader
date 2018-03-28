package profile.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Profile 
{

	@XmlElement(name = "path")
	private String path;
	
	@XmlElement(name = "keys")
	private Keys keys;

	public Keys getKeys() {
		return keys;
	}

	public void setKeys(Keys keys) {
		this.keys = keys;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
