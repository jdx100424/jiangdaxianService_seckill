package com.jiangdaxian.seckill.mongo;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 秒杀商品详细信息
 * 
 * @author dell
 *
 */
public class SeckillGoodsSkuInfoMongo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 销售的商品goodsSkuId
	@Field(value = "goodsSkuId")
	private Long goodsSkuId;

	public Long getGoodsSkuId() {
		return goodsSkuId;
	}

	public void setGoodsSkuId(Long goodsSkuId) {
		this.goodsSkuId = goodsSkuId;
	}

	@Field(value = "seckillGoodsSkuInfoNumMongo")
	private SeckillGoodsSkuInfoNumMongo seckillGoodsSkuInfoNumMongo;

	public SeckillGoodsSkuInfoNumMongo getSeckillGoodsSkuInfoNumMongo() {
		return seckillGoodsSkuInfoNumMongo;
	}

	public void setSeckillGoodsSkuInfoNumMongo(SeckillGoodsSkuInfoNumMongo seckillGoodsSkuInfoNumMongo) {
		this.seckillGoodsSkuInfoNumMongo = seckillGoodsSkuInfoNumMongo;
	}

}
