package com.jiangdaxian.seckill.mongo;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 秒杀活动用户资格
 * 
 * @author dell
 *
 */
@Document(collection = "seckillActivityQualificationUserMongo")
public class SeckillActivityQualificationUserMongo implements Serializable {

	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Field(value = "userId")
	private Long userId;

	@Field(value = "createTime")
	private Date createTime;

	@Field(value = "updateTime")
	private Date updateTime;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Field(value = "seckillActivityMongoId")
	private String seckillActivityMongoId;

	@Field(value = "goodsSkuId")
	private Long goodsSkuId;
	
	public Long getGoodsSkuId() {
		return goodsSkuId;
	}

	public void setGoodsSkuId(Long goodsSkuId) {
		this.goodsSkuId = goodsSkuId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getSeckillActivityMongoId() {
		return seckillActivityMongoId;
	}

	public void setSeckillActivityMongoId(String seckillActivityMongoId) {
		this.seckillActivityMongoId = seckillActivityMongoId;
	}
}
