package com.jiangdaxian.test.service.seckill;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.jiangdaxian.seckill.mongo.SeckillActivityMongo;
import com.jiangdaxian.seckill.mongo.SeckillGoodsSkuInfoMongo;
import com.jiangdaxian.seckill.mongo.SeckillGoodsSkuInfoNumMongo;
import com.jiangdaxian.seckill.service.SeckillService;
import com.jiangdaxian.test.BaseTestCase;

public class SeckillServiceTest extends BaseTestCase {
	@Autowired
	private SeckillService seckillService;
	
	@Test
	public void testGetSeckillActivityMongoById() throws Exception {
		System.out.println(JSONObject.toJSONString(seckillService.getSeckillActivityMongoById("5b096d2077d528171c087061")));
		System.out.println(JSONObject.toJSONString(seckillService.getSeckillActivityMongoById("5b0a366c77d5284294a94b9f")));

	}
	
	@Test
	public void testInsertSeckillActivityMongo() throws Exception {
		SeckillActivityMongo seckillActivityMongo = new SeckillActivityMongo();
		seckillActivityMongo.setSeckillActivityName("jdx20180528测试秒杀活动(2)");
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		
		seckillActivityMongo.setStartTime(date);
		seckillActivityMongo.setEndTime(c.getTime());
		seckillActivityMongo.setCreateTime(date);
		seckillActivityMongo.setUpdateTime(date);
		seckillActivityMongo.setStatus(1);
		
		List<SeckillGoodsSkuInfoMongo> seckillGoodsSkuInfoMongoList = new ArrayList<SeckillGoodsSkuInfoMongo>();
		SeckillGoodsSkuInfoMongo seckillGoodsSkuInfoMongoOne = new SeckillGoodsSkuInfoMongo();
		seckillGoodsSkuInfoMongoOne.setGoodsSkuId(10000L);
		SeckillGoodsSkuInfoNumMongo seckillGoodsSkuInfoNumMongo = new SeckillGoodsSkuInfoNumMongo();
		seckillGoodsSkuInfoNumMongo.setGoodsSkuName("秒杀商品1");
		seckillGoodsSkuInfoNumMongo.setQualificationNum(30);
		seckillGoodsSkuInfoNumMongo.setStockNum(30);
		seckillGoodsSkuInfoNumMongo.setPrice(new BigDecimal(50));
		seckillGoodsSkuInfoMongoOne.setSeckillGoodsSkuInfoNumMongo(seckillGoodsSkuInfoNumMongo);
		
		SeckillGoodsSkuInfoMongo seckillGoodsSkuInfoMongoTwo = new SeckillGoodsSkuInfoMongo();
		seckillGoodsSkuInfoMongoTwo.setGoodsSkuId(20000L);
		SeckillGoodsSkuInfoNumMongo seckillGoodsSkuInfoNumMongoTwo = new SeckillGoodsSkuInfoNumMongo();
		seckillGoodsSkuInfoNumMongoTwo.setGoodsSkuName("秒杀商品2");
		seckillGoodsSkuInfoNumMongoTwo.setQualificationNum(50);
		seckillGoodsSkuInfoNumMongoTwo.setStockNum(50);
		seckillGoodsSkuInfoNumMongoTwo.setPrice(new BigDecimal(80));
		seckillGoodsSkuInfoMongoTwo.setSeckillGoodsSkuInfoNumMongo(seckillGoodsSkuInfoNumMongoTwo);

		seckillGoodsSkuInfoMongoList.add(seckillGoodsSkuInfoMongoOne);
		seckillGoodsSkuInfoMongoList.add(seckillGoodsSkuInfoMongoTwo);
		
		seckillActivityMongo.setSeckillGoodsSkuInfoMongoList(seckillGoodsSkuInfoMongoList);
		
		System.out.println(seckillService.insertSeckillActivityMongo(seckillActivityMongo));
	}
	
	@Test
	public void TestCheckGoodsSkuIdIsQualification(){
		Boolean result = seckillService.checkGoodsSkuIdIsQualification("5b0a87eb77d52838547d8f73", 2L, 2222L);
		System.out.println(result);
	}
	
	@Test
	public void TestGetGoodsSkuIdQualificationNum() throws Exception{
		Integer result = seckillService.getGoodsSkuIdQualificationNum("5b0a87eb77d52838547d8f73", 2L);
		System.out.println(result);
	}
	
	@Test
	public void TestGetGoodsSkuIdStockNum() throws Exception{
		Integer result = seckillService.getGoodsSkuIdStockNum("5b0a87eb77d52838547d8f73", 2L);
		System.out.println(result);
	}
	
	@Test
	public void testSubGoodsSkuIdQualificationNumWIthMongo() throws Exception{
		seckillService.subGoodsSkuIdQualificationNumWIthMongo("5b0a87eb77d52838547d8f73", 1L,1);
	}
}
