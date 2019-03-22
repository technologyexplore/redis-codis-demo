package com.plateno.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.itmuch.cloud.study.entity.Grade;
import com.itmuch.cloud.study.service.RedisService;

//@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootRedisApplicationTests {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RedisService service;

	@Test
	public void contextLoads() {
		Grade grade = new Grade();
		grade.setId(111);
		grade.setName("aaa");
		grade.setPassword(2222);
		service.add("kyo123", grade, -1l);

//		logger.info("RedisTest执行完成，return {}", service.getStudent(user.getId()).getId());

		// City city=new City("city1","400500","深圳");
		// service.addCity(city);
	}
}