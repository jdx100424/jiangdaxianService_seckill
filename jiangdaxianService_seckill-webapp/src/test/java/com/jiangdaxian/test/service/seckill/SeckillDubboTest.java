package com.jiangdaxian.test.service.seckill;

import java.util.Random;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jiangdaxian.seckill.dubbo.SeckillDubbo;
import com.jiangdaxian.test.BaseTestCase;

public class SeckillDubboTest extends BaseTestCase {
	@Autowired
	private SeckillDubbo seckillDubbo;
	
	@Test
	public void testAddQualification() throws Exception {
		for(int i=0;i<100;i++) {
			new Thread(new Runnable() {
				public void run() {
					try {
						seckillDubbo.addQualification("5b0a87eb77d52838547d8f70", 1L, Long.parseLong(Integer.toString(new Random().nextInt(10000000))));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
		Thread.sleep(1000*60);
	}
	
	
}
