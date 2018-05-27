package com.jiangdaxian.seckill.mongo;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 秒杀商品详细信息
 * 
 * @author dell
 *
 */
public class SeckillGoodsSkuInfoNumMongo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Field(value = "goodsSkuName")
	private String goodsSkuName;

	// 抢购资格数
	@Field(value = "qualificationNum")
	private Integer qualificationNum;

	// 库存数
	@Field(value = "stockNum")
	private Integer stockNum;

	// 价格
	@Field(value = "price")
	private BigDecimal price;

	public String getGoodsSkuName() {
		return goodsSkuName;
	}

	public void setGoodsSkuName(String goodsSkuName) {
		this.goodsSkuName = goodsSkuName;
	}

	public Integer getQualificationNum() {
		return qualificationNum;
	}

	public void setQualificationNum(Integer qualificationNum) {
		this.qualificationNum = qualificationNum;
	}

	public Integer getStockNum() {
		return stockNum;
	}

	public void setStockNum(Integer stockNum) {
		this.stockNum = stockNum;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}
