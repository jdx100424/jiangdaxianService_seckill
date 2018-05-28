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
		final String seckillActivityMongoId = "5b0b710a77d5282c4cd5f0fe";
		final Long goodsSkuId = 20000L;
		for(int i=0;i<150;i++) {
			new Thread(new Runnable() {
				public void run() {
					try {
						seckillDubbo.addQualification(seckillActivityMongoId, goodsSkuId, Long.parseLong(Integer.toString(new Random().nextInt(150))));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
		Thread.sleep(1000*60);
		
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
	
	
}
