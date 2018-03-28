package profile.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class Configuration
{

	@XmlElement(name = "profile")
	private List<Profile> profile;

	
	public List<Profile> getProfiles() 
	{
		return profile;
	}

	public void setProfiles(List<Profile> profile) 
	{
		this.profile = profile;
	}

}
