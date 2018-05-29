package com.jiangdaxian.test.service.seckill;

import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;

import com.jiangdaxian.seckill.dubbo.SeckillDubbo;
import com.jiangdaxian.seckill.mongo.SeckillActivityQualificationUserMongo;
import com.jiangdaxian.test.BaseTestCase;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class SeckillDubboTest extends BaseTestCase {
	@Autowired
	private SeckillDubbo seckillDubbo;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Test
	public void testAddQualification() throws Exception {
		final String seckillActivityMongoId = "5b0cf8c877d52809186b5cae";
		final Long goodsSkuId = 20000L;
		for(int i=0;i<120;i++) {
			new Thread(new Runnable() {
				public void run() {
					try {
						seckillDubbo.addQualification(seckillActivityMongoId, goodsSkuId, Long.parseLong(Integer.toString(new Random().nextInt(1500000))));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
		Thread.sleep(10000*60);
		
		DBObject dbObject = new BasicDBObject();
		dbObject.put("seckillActivityMongoId", seckillActivityMongoId);
		dbObject.put("goodsSkuId", goodsSkuId);
		Query query = new BasicQuery(dbObject);
		List<SeckillActivityQualificationUserMongo> list = mongoTemplate.find(query, SeckillActivityQualificationUserMongo.class,"SeckillActivityQualificationUserMongo");
		if(list!=null) {
			for(SeckillActivityQualificationUserMongo s:list) {
				System.out.println(s.getUserId());
			}
		}
	}
	
	@Test
	public void testAddQualificationTwo() throws Exception {
		final String seckillActivityMongoId = "5b0ce60477d5281c840eb0a9";
		final Long goodsSkuId = 20000L;
		for(int i=0;i<2;i++) {
			seckillDubbo.addQualification(seckillActivityMongoId, goodsSkuId, Long.parseLong(Integer.toString(new Random().nextInt(15000))));
		}
	}
}
