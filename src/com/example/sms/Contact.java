package com.example.sms;

public class Contact {
	private long id;
	private String name;
	private String phoneNumber;
	
	/* Constructors */
	/**
	 *  Construct Contact using (id, name, phoneNumber)
	 *  
	 *  Name can be null, when it is a new phoneNumber
	 * 
	 **/
	public Contact(long id, String name, String phoneNumber) {
		super();
		this.id = id;
		this.name = name;
		this.phoneNumber = phoneNumber;
	}
	
	/**
	 *  Construct Contact from a existing Contact object
	 * 
	 **/
	public Contact(Contact ct){
		this(ct.id, ct.name, ct.phoneNumber);
	}
	/* End Constructor */

	
	/* Getter and Setter */
	/**
	 *  Get id of Contact
	 **/
	public long getId() {
		return id;
	}

	/**
	 *  Set id of Contact
	 **/
	public void setId(int id) {
		this.id = id;
	}

	/**
	 *  Get display name of Contact
	 **/
	public String getName() {
		return name;
	}

	/**
	 *  Get name of Contact when available
	 *  Otherwise return the phoneNumber
	 *  
	 **/
	public String getNameOrNumber(){
		return name!=null?name:phoneNumber;
	}
	
	/**
	 *  Set name of Contact
	 **/
	public void setName(String name) {
		this.name = name;
	}

	/**
	 *  Get PhoneNumber of Contact
	 **/
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 *  Set PhoneNumber of Contact
	 **/
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	/* End Getter and Setter */
	
	/**
	 *  Check if this Contact object equals to another Contact object
	 *  by comparing name and phoneNumber
	 **/
	public boolean equals(Contact ct){
		return (ct.name==name)
				&& (ct.phoneNumber==phoneNumber);
	}
}
