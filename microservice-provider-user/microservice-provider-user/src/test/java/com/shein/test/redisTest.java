package com.shein.test;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.itmuch.cloud.study.ProviderUserApplication;
import com.itmuch.cloud.study.service.RedisService;

//获取启动类，加载配置，确定装载 Spring 程序的装载方法，它回去寻找 主配置启动类（被 @SpringBootApplication 注解的）
//让 JUnit 运行 Spring 的测试环境， 获得 Spring 环境的上下文的支持
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ProviderUserApplication.class) // 指定spring-boot的启动类
public class redisTest {

	@Autowired
	private RedisService redisService;

	@Test
	public void flowControlTest() {
		redisService.initializationRedisFlowData();
//		redisService.flowControl();
	}

	@Test
	public void setTest() {
		redisService.initializationRedisSetData();
		redisService.setHandel();
	}

	@Test
	public void rankListTest() {
		redisService.initializationRedisRanklistData();
		redisService.rankingList();
	}

	@Test
	public void getLockTest() {
		redisService.getLockOld("lockkey1");
		// redisService.getLockNew("lockkey2");
	}

	@Test
	public void unLockTest() {
		redisService.unlockOld("lockkey1", "60ea759d-dde0-4094-84fa-4773d63f5cf9");
	}

	@Test
	public void setStringTest() {
		redisService.setString1();
	}
}
