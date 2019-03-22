package com.itmuch.cloud.study.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import com.ctrip.framework.apollo.Config;
//import com.ctrip.framework.apollo.ConfigService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itmuch.cloud.study.entity.Grade;
import com.itmuch.cloud.study.service.GradeService;
import com.itmuch.cloud.study.service.RedisService;

@RestController
public class UserController {

	@Autowired
	private RedisService service;

	// @Autowired
	// private UserRepository userRepository;

	// @GetMapping("/{id}")
	// public User findById(@PathVariable Long id) {
	// User findOne = this.userRepository.findOne(id);
	// return findOne;
	// }

	@Resource
	private GradeService gradeService;

	/**
	 * http://10.237.148.53:8000/getSome
	 * 
	 * @return
	 */
	@GetMapping("/getSome")
	public String getSome() {
		System.out.println(1111);
		List<Grade> listGrade = gradeService.getByGradeNm("aaaa");
		Gson gson = new GsonBuilder().create();
		return gson.toJson(listGrade);
	}

	/**
	 * http://10.237.148.53:8000/hello
	 * 
	 * @return
	 */
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String getSome1() {
		List<Grade> listGrade = gradeService.getByGradeNm("aaaa");
		Gson gson = new GsonBuilder().create();
		return gson.toJson(listGrade);
	}

	/**
	 * http://10.237.148.53:8000/hello1
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/hello1")
	// required=false 表示url中可以不穿入id参数，此时就使用默认参数
	public String sayHello(@RequestParam(value = "id", required = false, defaultValue = "1") Integer id) {
		return "id:" + id;
	}

	/**
	 * http://10.237.148.53:8000/members {"id":111,"name":"aaa","password":2222}
	 * 
	 * @param grade
	 * @return
	 */
	@PostMapping(path = "/members", consumes = "application/json", produces = "application/json")
	public String addMember(@RequestBody Grade grade) {
		Gson gson = new GsonBuilder().create();
		return gson.toJson(grade);
	}

	/**
	 * http://10.237.148.53:8000/register {"id":111,"name":"aaa","password":2222}
	 * 
	 * @param grade
	 * @return
	 */
	@PostMapping(path = "/register")
	public String registerUser(@RequestBody Grade grade) {
		return grade.toString();
	}

	/**
	 * http://10.237.148.53:8000/register1
	 * 
	 * @param id
	 * @return
	 */
	@PostMapping(path = "/register1")
	public String registerUser1(@RequestParam(value = "id", required = false, defaultValue = "1") Integer id) {
		Grade grade = new Grade();
		grade.setId(111);
		grade.setName("aaa");
		grade.setPassword(2222);
		service.add("kyo123", grade, 120l);
		return id.toString();
	}

	@GetMapping(value = "/flowControl")
	// required=false 表示url中可以不穿入id参数，此时就使用默认参数
	public String flowControl() {
		service.flowControl();
		return "";
	}
	
	@GetMapping(value = "/rankingList")
	// required=false 表示url中可以不穿入id参数，此时就使用默认参数
	public String rankingList(@RequestParam(value = "id", required = false, defaultValue = "1") Integer id) {
		return null;
	}
	
	@GetMapping(value = "/setString")
	public void setString() {
		service.setString();
	}
//	
//	@GetMapping(value = "/rankingList2")
//	// required=false 表示url中可以不穿入id参数，此时就使用默认参数
//	public boolean rankingList2(@RequestParam(value = "id", required = false, defaultValue = "1") Integer id) {
//		service.unlock1("kof", "0c2f6721-486a-42e3-aa72-7e432c3eae0b");
//		return false;
//	}
//	@GetMapping(value = "/testapollo")
//	// required=false 表示url中可以不穿入id参数，此时就使用默认参数
//	public String testapollo(@RequestParam(value = "id", required = false, defaultValue = "1") String id) {
//		Config config = ConfigService.getConfig("TEST1.111111");
//		String someDefaultValue = "127.0.0.1";
//		String value = config.getProperty(id, someDefaultValue);
//		System.out.println("someKey=" + id + ";" + value);
//		return "id:" + value;
//	}
//	
//	public static void main(String[] args) {
////		new ClassPathXmlApplicationContext("application.xml");
////		Config config = ConfigService.getAppConfig(); // config instance is singleton for each namespace and is never
//		Config config = ConfigService.getConfig("TEST1.111111");
//		String someKey = "96k";
//		String someDefaultValue = "127.0.0.1";
//		String value = config.getProperty(someKey, someDefaultValue);
//		System.out.println("someKey=" + someKey + ";" + value);
//	}

}
