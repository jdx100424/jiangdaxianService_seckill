package com.jiangdaxian.seckill.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSONObject;
import com.jiangdaxian.seckill.constant.SeckillConstant;
import com.jiangdaxian.seckill.mongo.SeckillActivityMongo;
import com.jiangdaxian.seckill.mongo.SeckillActivityQualificationUserMongo;
import com.jiangdaxian.seckill.mongo.SeckillGoodsSkuInfoMongo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

@Service
public class SeckillService {
	private static final Logger LOG = LoggerFactory.getLogger(SeckillService.class);
	//秒杀活动表基础信息表
	private final static String SECKILL_ACTIVITY_COLLECTION_NAME = "seckillActivityMongo";
	//秒杀活动表具体商品信息信息表
	private final static String SECKILL_ACTIVITY_QUALIFICATION_USER_COLLECTION_NAME = "SeckillActivityQualificationUserMongo";

	//秒杀活动基本信息缓存,活动ID
	private static final String SECKILL_ACTIVITY = "SECKILL_ACTIVITY:%s";
	
	//秒杀活动，指定商品，指定用户，是否有抢购过
	private static final String SECKILL_ACTIVITY_GOODSSKU_USER_QUALIFICATION = "SECKILL_ACTIVITY_GOODSSKU_USER_QUALIFICATION:%s:%s:%s";
	
	//秒杀活动，指定商品，剩余抢购数
	private static final String SECKILL_ACTIVITY_GOODSSKU_QUALIFICATION_NUM = "SECKILL_ACTIVITY_GOODSSKU_QUALIFICATION_NUM:%s:%s";

	//秒杀活动，指定商品，剩余库存数
	private static final String SECKILL_ACTIVITY_GOODSSKU_STOCK_NUM = "SECKILL_ACTIVITY_GOODSSKU_STOCK_NUM:%s:%s";

	private static final Long EXPIRE_TIME = 60 * 5L;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	@Qualifier("redisTemplate")
	private RedisTemplate redisTemplate;
	@SuppressWarnings("rawtypes")
	@Autowired
	@Qualifier("redisIncrTemplate")
	private RedisTemplate redisIncrTemplate;
	
	/**
	 * 获取活动信息
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public SeckillActivityMongo getSeckillActivityMongoById(String seckillActivityMongoId) throws Exception {
		String redisName = String.format(SECKILL_ACTIVITY, seckillActivityMongoId);
		Object redisResult = redisTemplate.opsForValue().get(redisName);
		if(redisResult!=null) {
			SeckillActivityMongo seckillActivityMongo = (SeckillActivityMongo) redisResult;
			LOG.info("SeckillService::getSeckillActivityMongoById::param,{},redisResult:{}",seckillActivityMongoId,JSONObject.toJSONString(seckillActivityMongo));
			return seckillActivityMongo;
		}
		
		//mongo
		DBObject dbObject = new BasicDBObject();
		dbObject.put("_id", new ObjectId(seckillActivityMongoId));
		Query query = new BasicQuery(dbObject);
		SeckillActivityMongo seckillActivityMongo = mongoTemplate.findOne(query, SeckillActivityMongo.class,SECKILL_ACTIVITY_COLLECTION_NAME);
		if (null != seckillActivityMongo) {
			redisTemplate.opsForValue().setIfAbsent(redisName,seckillActivityMongo);
			redisTemplate.expire(redisName,EXPIRE_TIME,TimeUnit.SECONDS);
			LOG.info("SeckillService::getSeckillActivityMongoById::param,{},mongoResult:{}",seckillActivityMongoId,JSONObject.toJSONString(seckillActivityMongo));
			return seckillActivityMongo;
		}
		return null;
	}
	
	/**
	 * 查看此用户对于此商品是否已经有抢购过
	 * @param userId
	 * @param goodsSkuId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean checkGoodsSkuIdIsQualification(String seckillActivityMongoId,Long goodsSkuId,Long userId) {
		String redisName = String.format(SECKILL_ACTIVITY_GOODSSKU_USER_QUALIFICATION, seckillActivityMongoId,goodsSkuId,userId);
		Object redisResult = redisTemplate.opsForValue().get(redisName);
		if(redisResult!=null) {
			Boolean isQualification = Boolean.parseBoolean(redisResult.toString());
			LOG.info("SeckillService::checkGoodsSkuIdIsQualification::checkGoodsSkuIdIsQualification:param {},{},{},result:{}",seckillActivityMongoId,goodsSkuId,userId,isQualification);
			//return isQualification;
		}
		
		DBObject dbObject = new BasicDBObject();
		dbObject.put("seckillActivityMongoId", seckillActivityMongoId);
		dbObject.put("goodsSkuId", goodsSkuId);
		dbObject.put("userId", userId);
		Query query = new BasicQuery(dbObject);
		long qualificationNum = mongoTemplate.count(query, SeckillActivityQualificationUserMongo.class,SECKILL_ACTIVITY_QUALIFICATION_USER_COLLECTION_NAME);
		
		Boolean isQualification =false;
		if (qualificationNum>0) {
			isQualification = true;
		}
		LOG.info("SeckillService::checkGoodsSkuIdIsQualification::param {},{},{},mongoResult:{}",seckillActivityMongoId,goodsSkuId,userId,isQualification);
		
		redisTemplate.opsForValue().setIfAbsent(redisName,isQualification);
		redisTemplate.expire(redisName,EXPIRE_TIME,TimeUnit.SECONDS);
		return isQualification;
	}
	
	/**
	 * 获取目前商品剩余抢购数
	 * @param goodsSkuId
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public Integer getGoodsSkuIdQualificationNum(String seckillActivityMongoId,Long goodsSkuId) throws Exception {
		String redisName = String.format(SECKILL_ACTIVITY_GOODSSKU_QUALIFICATION_NUM, seckillActivityMongoId,goodsSkuId);
		Object redisResult = redisIncrTemplate.opsForValue().get(redisName);
		if(redisResult!=null) {
			Integer remainQualificationNum = Integer.parseInt(redisResult.toString());
			LOG.info("SeckillService::getGoodsSkuIdQualificationNum::remainQualificationNum:{},{},result:{}",seckillActivityMongoId,goodsSkuId,remainQualificationNum);
			return remainQualificationNum;
		}
		
		DBObject dbObject = new BasicDBObject();
		dbObject.put("_id", new ObjectId(seckillActivityMongoId));
		dbObject.put("seckillGoodsSkuInfoMongoList.goodsSkuId", goodsSkuId);
		Query query = new BasicQuery(dbObject);
		SeckillActivityMongo seckillActivityMongo = mongoTemplate.findOne(query, SeckillActivityMongo.class,SECKILL_ACTIVITY_COLLECTION_NAME);
		
		Integer remainQualificationNum =0;
		if (null == seckillActivityMongo) {
			throw new Exception("SeckillService::getGoodsSkuIdQualificationNum:: seckillActivityMongo is not allow null");
		}
		List<SeckillGoodsSkuInfoMongo> list = seckillActivityMongo.getSeckillGoodsSkuInfoMongoList();
		for(SeckillGoodsSkuInfoMongo s:list) {
			if(goodsSkuId.equals(s.getGoodsSkuId())) {
				remainQualificationNum = s.getSeckillGoodsSkuInfoNumMongo().getQualificationNum();
				break;
			}
		}
		
		LOG.info("SeckillService::getGoodsSkuIdQualificationNum::{},{},remainQualificationNum:{}",seckillActivityMongoId,goodsSkuId,remainQualificationNum);
		
		redisIncrTemplate.opsForValue().setIfAbsent(redisName,Integer.toString(remainQualificationNum));
		redisIncrTemplate.expire(redisName,EXPIRE_TIME,TimeUnit.SECONDS);
		return remainQualificationNum;
	}
	
	/**
	 * 获取商品剩余库存数
	 * @param goodsSkuId
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public Integer getGoodsSkuIdStockNum(String seckillActivityMongoId,Long goodsSkuId) throws Exception {
		String redisName = String.format(SECKILL_ACTIVITY_GOODSSKU_STOCK_NUM, seckillActivityMongoId,goodsSkuId);
		Object redisResult = redisIncrTemplate.opsForValue().get(redisName);
		if(redisResult!=null) {
			Integer remainStockNum = Integer.parseInt(redisResult.toString());
			LOG.info("SeckillService::getGoodsSkuIdStockNum::remainStockNum:{},{},result:{}",seckillActivityMongoId,goodsSkuId,remainStockNum);
			return remainStockNum;
		}
		
		DBObject dbObject = new BasicDBObject();
		dbObject.put("_id", new ObjectId(seckillActivityMongoId));
		dbObject.put("seckillGoodsSkuInfoMongoList.goodsSkuId", goodsSkuId);
		Query query = new BasicQuery(dbObject);
		SeckillActivityMongo seckillActivityMongo = mongoTemplate.findOne(query, SeckillActivityMongo.class,SECKILL_ACTIVITY_COLLECTION_NAME);
		
		Integer remainStockNum =0;
		if (null == seckillActivityMongo) {
			throw new Exception("SeckillService::getGoodsSkuIdStockNum::seckillActivityMongo is not allow null");
		}
		List<SeckillGoodsSkuInfoMongo> list = seckillActivityMongo.getSeckillGoodsSkuInfoMongoList();
		for(SeckillGoodsSkuInfoMongo s:list) {
			if(goodsSkuId.equals(s.getGoodsSkuId())) {
				remainStockNum = s.getSeckillGoodsSkuInfoNumMongo().getStockNum();
				break;
			}
		}
		
		LOG.info("SeckillService::getGoodsSkuIdStockNum::{},{},remainStockNum:{}",seckillActivityMongoId,goodsSkuId,remainStockNum);
		
		redisIncrTemplate.opsForValue().setIfAbsent(redisName,Integer.toString(remainStockNum));
		redisIncrTemplate.expire(redisName,EXPIRE_TIME,TimeUnit.SECONDS);
		return remainStockNum;
	}
	
	/**
	 * 商品资格数减去指定的数目->redis
	 * @param goodsSkuId
	 * @param num
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public void subGoodsSkuIdQualificationNumWIthRedis(String seckillActivityMongoId,Long goodsSkuId,Integer num) throws Exception {
		Integer remainQualificationNum = getGoodsSkuIdQualificationNum(seckillActivityMongoId,goodsSkuId);
		if(remainQualificationNum<num) {
			throw new Exception("RemainQualificationNum is less num");
		}
		
		String redisName = String.format(SECKILL_ACTIVITY_GOODSSKU_QUALIFICATION_NUM, seckillActivityMongoId,goodsSkuId);
		//减1后，获取剩余的数据，如果小于0，则资格数已经是0，不可再分发
		Integer subNum = num*-1;		
		Long subRemainQualificationNum = redisIncrTemplate.opsForValue().increment(redisName,subNum);
		if(subRemainQualificationNum<0) {
			throw new Exception("RemainQualificationNum is less 0");
		}
	}	
	/**
	 * 商品资格数增加指定的数目->redis
	 * @param seckillActivityMongoId
	 * @param goodsSkuId
	 * @param num
	 */
	@SuppressWarnings("unchecked")
	public void addGoodsSkuIdQualificationNumWIthRedis(String seckillActivityMongoId,Long goodsSkuId,Integer num) {
		String redisName = String.format(SECKILL_ACTIVITY_GOODSSKU_QUALIFICATION_NUM, seckillActivityMongoId,goodsSkuId);
		redisIncrTemplate.opsForValue().increment(redisName, num);
	}	
	
	/**
	 * 商品资格数减去指定的数目->mongo
	 * @param goodsSkuId
	 * @param num
	 * @return
	 * @throws Exception 
	 */
	public void subGoodsSkuIdQualificationNumWIthMongo(String seckillActivityMongoId,Long goodsSkuId,Integer num) throws Exception {
		SeckillActivityMongo seckillActivityMongo = getSeckillActivityMongoById(seckillActivityMongoId);
		if(seckillActivityMongo==null) {
			throw new Exception("seckillActivityMongo is null");
		}
		
		int part = -1;
		List<SeckillGoodsSkuInfoMongo> list = seckillActivityMongo.getSeckillGoodsSkuInfoMongoList();
		for(int i=0;i<list.size();i++) {
			if(goodsSkuId.equals(list.get(i).getGoodsSkuId())){
				part = i;
				break;
			}
		}
		
		if(part<0) {
			throw new Exception("SeckillService::subGoodsSkuIdQualificationNumWIthMongo::part must > -1");
		}
		
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(new ObjectId(seckillActivityMongoId)));
		query.addCriteria(Criteria.where("status").is(SeckillConstant.STATUS_OK));
		query.addCriteria(Criteria.where("startTime").gte(seckillActivityMongo.getStartTime()));
		query.addCriteria(Criteria.where("endTime").lte(seckillActivityMongo.getEndTime()));
		query.addCriteria(Criteria.where("seckillGoodsSkuInfoMongoList."+part+".goodsSkuId").is(goodsSkuId));
		query.addCriteria(Criteria.where("seckillGoodsSkuInfoMongoList."+part+".seckillGoodsSkuInfoNumMongo.qualificationNum").gte(num));
		Update update = new Update().inc("seckillGoodsSkuInfoMongoList."+part+".seckillGoodsSkuInfoNumMongo.qualificationNum",num*-1);
		
		WriteResult writeResult = mongoTemplate.updateFirst(query, update, SeckillActivityMongo.class);
		if(writeResult==null || writeResult.getN()<1) {
			throw new Exception("SeckillService::subGoodsSkuIdQualificationNumWIthMongo::update QualificationNum fail");
		}
		
	}	

	/**
	 * 增加用户资格数记录 
	 * @param goodsSkuId
	 * @param userId
	 * @param num
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public void addUserGoodsSkuIdQualification(String seckillActivityMongoId,Long goodsSkuId,Long userId) throws Exception {
		DBObject dbObject = new BasicDBObject();
		dbObject.put("seckillActivityMongoId", seckillActivityMongoId);
		dbObject.put("goodsSkuId", goodsSkuId);
		dbObject.put("userId", userId);
		Query query = new BasicQuery(dbObject);
		SeckillActivityQualificationUserMongo seckillActivityQualificationUserMongo = mongoTemplate.findOne(query, SeckillActivityQualificationUserMongo.class,SECKILL_ACTIVITY_QUALIFICATION_USER_COLLECTION_NAME);
		if(seckillActivityQualificationUserMongo!=null) {
			throw new Exception("SeckillService::addUserGoodsSkuIdQualification::not allow null");
		}
		
		seckillActivityQualificationUserMongo = new SeckillActivityQualificationUserMongo();
		Date d = new Date();
		seckillActivityQualificationUserMongo.setCreateTime(d);
		seckillActivityQualificationUserMongo.setGoodsSkuId(goodsSkuId);
		seckillActivityQualificationUserMongo.setSeckillActivityMongoId(seckillActivityMongoId);
		seckillActivityQualificationUserMongo.setUpdateTime(d);
		seckillActivityQualificationUserMongo.setUserId(userId);
		
		mongoTemplate.insert(seckillActivityQualificationUserMongo,SECKILL_ACTIVITY_QUALIFICATION_USER_COLLECTION_NAME);
	
		String redisName = String.format(SECKILL_ACTIVITY_GOODSSKU_USER_QUALIFICATION, seckillActivityMongoId,goodsSkuId,userId);
		redisTemplate.opsForValue().setIfAbsent(redisName,true);
		redisTemplate.expire(redisName,EXPIRE_TIME,TimeUnit.SECONDS);
	}
	
	/**
	 * 插入活动，并且返回MONGO的_id
	 * @param seckillActivityMongo
	 * @return
	 * @throws Exception 
	 */
	public Object insertSeckillActivityMongo(SeckillActivityMongo seckillActivityMongo) throws Exception {
		if(seckillActivityMongo==null) {
			throw new Exception("seckillActivityMongo is not allow null");
		}
		mongoTemplate.insert(seckillActivityMongo,SECKILL_ACTIVITY_COLLECTION_NAME);
		return seckillActivityMongo.getId();
	}
}
