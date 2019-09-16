package com.hzl.entity;

import java.util.List;
import java.util.Map;

public class Person {
	private String name;
	private int age;
	private boolean sex;
	private String email;
	private String phone;
	private List<Person> friends;
	private Person boss;
	
	//setter and getter
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	private Map<String, Object> hobby;

	public Person getBoss() {
		return boss;
	}

	public void setBoss(Person boss) {
		this.boss = boss;
	}

	public Map<String, Object> getHobby() {
		return hobby;
	}

	public void setHobby(Map<String, Object> hobby) {
		this.hobby = hobby;
	}

	public boolean getSex() {
		return sex;
	}

	public void setSex(boolean sex) {
		this.sex = sex;
	}

	public List<Person> getFriends() {
		return friends;
	}

	public void setFriends(List<Person> friends) {
		this.friends = friends;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
