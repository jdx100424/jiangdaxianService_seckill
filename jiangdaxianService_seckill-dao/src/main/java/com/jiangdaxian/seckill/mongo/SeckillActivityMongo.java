package com.jiangdaxian.seckill.mongo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 秒杀活动信息
 * 
 * @author dell
 *
 */
@Document(collection = "seckillActivityMongo")
public class SeckillActivityMongo implements Serializable {
	protected String id;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 秒杀活动名称
	 */
	@Field(value = "seckillActivityName")
	private String seckillActivityName;

	@Field(value = "startTime")
	private Date startTime;

	@Field(value = "endTime")
	private Date endTime;

	@Field(value = "createTime")
	private Date createTime;

	@Field(value = "updateTime")
	private Date updateTime;

	// 0，草稿，1，启动，2停止
	@Field(value = "status")
	private Integer status;

	// 秒杀商品详细信息
	@Field(value = "seckillGoodsSkuInfoMongoList")
	private List<SeckillGoodsSkuInfoMongo> seckillGoodsSkuInfoMongoList;
	
	public String getSeckillActivityName() {
		return seckillActivityName;
	}

	public void setSeckillActivityName(String seckillActivityName) {
		this.seckillActivityName = seckillActivityName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<SeckillGoodsSkuInfoMongo> getSeckillGoodsSkuInfoMongoList() {
		return seckillGoodsSkuInfoMongoList;
	}

	public void setSeckillGoodsSkuInfoMongoList(List<SeckillGoodsSkuInfoMongo> seckillGoodsSkuInfoMongoList) {
		this.seckillGoodsSkuInfoMongoList = seckillGoodsSkuInfoMongoList;
	}
}
