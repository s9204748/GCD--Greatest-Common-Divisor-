package unico.resteasy;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class User {
	
	public User() {}
	
	public User(int i, String name, String email, Date dob) {
		id = i;
		this.name = name;
		this.email = email;
		this.dob = dob;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	private Integer id;
	private String name;
	private String email;
	private Date dob;

	// setters and getters
	public int getId() {
		return id;
	}
}