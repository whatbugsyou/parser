package com.hzl.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzl.entity.Person;
import com.hzl.parser.Parser;

public class App {
	public static void main(String[] args) {
		Person me = new Person();
		//设置基本信息
		me.setAge(23);
		me.setName("hzl");
		me.setSex(false);
		me.setEmail("1050466416@gamil.com");
		me.setPhone("13100252577");
		//设置friends （对象数组）
		Person friend = new Person();
		friend.setAge(10);
		friend.setSex(false);
		friend.setEmail("123@qq.com");
		friend.setPhone("123");
		friend.setName("xiao");
		List<Person> personList = new ArrayList<>();
		personList.add(friend);
		me.setFriends(personList);
		//设置hobby （集合对象）
		HashMap<String, Object> map = new HashMap<>();
		map.put("codedLines", 10000);
		map.put("msg", "ok");
		map.put("info", "haha");
		map.put("isSingle", false);
		me.setHobby(map);
		
		//创建JSON解析器和google的解析器用做对比
		Parser my_parser = new Parser();
		Gson gson = new Gson();
	
		System.out.println("---------toJson");
		//普通对象
		System.out.println("普通对象");
		System.out.println("my_paser:"+my_parser.toJson(me));
		System.out.println("谷歌的gson:"+gson.toJson(me));
		System.out.println("---------");
		//map
		System.out.println("Map");
		System.out.println("my_paser:"+my_parser.toJson(map));
		System.out.println("谷歌的gson:"+gson.toJson(map));
		System.out.println("---------");
		//List
		System.out.println("List");
		System.out.println("my_paser:"+my_parser.toJson(personList));
		System.out.println("谷歌的gson:"+gson.toJson(personList));
		System.out.println("");
		
		System.out.println("---------fromJson");
		String str = "{age:18,name:'bob',phone:'130',friends:[{age:18,name:'bob',phone:'130'}],boss:{age:19,name:'alice',phone:'150'},sex:false}";
		Person my_per = (Person) my_parser.fromJson(str,Person.class);
		Person gson_per = gson.fromJson(str,Person.class);
		System.out.println("JSON对象:"+str);
		System.out.println("my_paser:"+my_parser.toJson(my_per));
		System.out.println("谷歌的gson:"+gson.toJson(gson_per));
		
		String strs = "[{age:18,name:'bob',phone:'130',boss:{age:19,name:'alice',phone:'150',sex:false},sex:false},{age:18,sex:true}]";
		System.out.println("JSON数组:"+strs);
		List<Person> list= (List<Person>) my_parser.fromJson(strs,Person.class);
		List<Person> list1= gson.fromJson(strs,new TypeToken<List<Person>>(){}.getType());
		System.out.println("my_paser:"+my_parser.toJson(list));
		System.out.println("谷歌的gson:"+gson.toJson(list1));
		

	}
}
