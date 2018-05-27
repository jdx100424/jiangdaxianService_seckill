package com.jiangdaxian.seckill.api;

public interface SeckillApi {
	/**
	 * 获取插入抢购资格
	 * @param seckillActivityMongoId
	 * @param goodsSkuId
	 * @param userId
	 * @throws Exception
	 */
	public void addQualification(String seckillActivityMongoId,Long goodsSkuId,Long userId) throws Exception ;
}
