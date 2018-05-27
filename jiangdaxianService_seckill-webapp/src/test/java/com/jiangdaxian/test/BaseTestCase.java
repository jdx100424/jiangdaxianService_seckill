/**
 * 
 */
package com.jiangdaxian.test;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiangdaxian.base.WebApplication;

/**
 * @Title:
 * @Description:
 * @Company:Comall
 * @Author:Zheng Shiyu
 * @Created Date:2016年7月3日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=WebApplication.class)// 指定spring-boot的启动类 
public class BaseTestCase {
	private static ObjectMapper mapper = new ObjectMapper();

	protected static void print(Object o) {
		try {
			System.out.println("print========\n" + mapper.writeValueAsString(o));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
