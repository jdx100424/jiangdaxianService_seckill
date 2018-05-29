package com.jiangdaxian.seckill.dubbo;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiangdaxian.autoid.util.AutoIdUtil;
import com.jiangdaxian.redis.RedisLock;
import com.jiangdaxian.seckill.api.SeckillApi;
import com.jiangdaxian.seckill.constant.SeckillConstant;
import com.jiangdaxian.seckill.mongo.SeckillActivityMongo;
import com.jiangdaxian.seckill.service.SeckillService;

@Service("seckillDubbo")
public class SeckillDubbo implements SeckillApi {
	
	@Autowired
	private RedisLock redisLock;
	@Autowired
	private SeckillService seckillService;
	@Autowired
	private AutoIdUtil autoIdUtil;
	
	//String seckillActivityMongoId,Long goodsSkuId,Long userId
	private static final String USER_REDIS_LOCK = "USER_SECKILL_QUALIFICATION_REDIS_LOCK:%s:%s:%s";
	private static final Logger LOG = LoggerFactory.getLogger(SeckillService.class);
	private static final Integer DEFAULT_NUM = 1;
	
	public void addQualification(String seckillActivityMongoId,Long goodsSkuId,Long userId) throws Exception {
		String msg = seckillActivityMongoId + "," + goodsSkuId + "," + userId;
		LOG.info("SeckillDubbo::addQualification::param,{}",msg);
		String lock = String.format(USER_REDIS_LOCK,seckillActivityMongoId,goodsSkuId,userId);
		try {
			redisLock.lockByIncr(lock);
			Date nowDate = new Date();
			SeckillActivityMongo seckillActivityMongo = seckillService.getSeckillActivityMongoById(seckillActivityMongoId);
			if(seckillActivityMongo==null) {
				throw new Exception("SeckillDubbo::addQualification::getSeckillActivityMongoById is null,"+msg);
			}
			//状态是否OK
			if(SeckillConstant.STATUS_OK.equals(seckillActivityMongo.getStatus())==false) {
				throw new Exception("SeckillDubbo::addQualification::status is not run,"+msg);
			}
			
			//是否符合时间条件等？
			if(seckillActivityMongo.getStartTime().before(nowDate)==false || seckillActivityMongo.getEndTime().after(nowDate)==false) {
				throw new Exception("SeckillDubbo::addQualification::time is depart,"+msg);
			}
			//是否已经抢购过？
			boolean isQualification = seckillService.checkGoodsSkuIdIsQualification(seckillActivityMongoId, goodsSkuId, userId);
			if(isQualification) {
				throw new Exception("SeckillDubbo::addQualification::isQualification,"+msg);
			}
			//资格是否满足?
			Integer qualificationNum = seckillService.getGoodsSkuIdQualificationNum(seckillActivityMongoId, goodsSkuId);
			if(qualificationNum==null || qualificationNum<DEFAULT_NUM) {
				throw new Exception("SeckillDubbo::addQualification::qualificationNum is 0,"+msg);
			}
			//redis资格-1
			seckillService.subGoodsSkuIdQualificationNumWIthRedis(seckillActivityMongoId, goodsSkuId,DEFAULT_NUM);
		
			Integer stockNum = seckillService.getGoodsSkuIdStockNum(seckillActivityMongoId, goodsSkuId);
			//库存数是否满足?
			if(stockNum==null || stockNum<DEFAULT_NUM) {
				seckillService.addGoodsSkuIdQualificationNumWIthRedis(seckillActivityMongoId, goodsSkuId,DEFAULT_NUM);
				throw new Exception("SeckillDubbo::addQualification::stockNum is 0,"+msg);
			}
			
			//mongo资格是否扣减成功？
			try {
				seckillService.subGoodsSkuIdQualificationNumWIthMongo(seckillActivityMongoId, goodsSkuId,DEFAULT_NUM);
			}catch(Exception e) {
				seckillService.addGoodsSkuIdQualificationNumWIthRedis(seckillActivityMongoId, goodsSkuId,DEFAULT_NUM);
				throw new Exception("SeckillDubbo::addQualification::sub Qualification error,"+msg);
			}
			
			//资格表增加此用户
			seckillService.addUserGoodsSkuIdQualification(seckillActivityMongoId, goodsSkuId,userId);
		
			//抢到后获取一个随机ID
			Long getId = autoIdUtil.getNextId("jdxProjectName", "jdxTableName");
			LOG.info("get Qualification success, get id is :{},param:{},{},{}",getId,seckillActivityMongoId, goodsSkuId, userId);
		}catch(Exception e) {
			LOG.error(e.getMessage(),e);
			throw e;
		}finally {
			redisLock.unlock(lock);
		}
	}
	/*
	public void redisLock(String s) {
		try {
			redisLock.lockByIncr(s);
			Thread.sleep(2000);
			System.out.println(new java.util.Date() + ","+ Thread.currentThread().getName());
		}catch(Exception e) {
			LOG.error(e.getMessage(),e);
		}finally {
			redisLock.unlock(s);
		}
	}*/
}
