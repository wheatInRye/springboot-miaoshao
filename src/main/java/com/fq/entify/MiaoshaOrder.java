package com.fq.entify;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

@TableName("miaosha_order")
public class MiaoshaOrder {

	@TableId(value = "id", type = IdType.AUTO)
	private long id;
	private long userId;
	private long  orderId;
	private long goodsId;

	public MiaoshaOrder() {
		this.userId = userId;
		this.goodsId = goodsId;
	}
	public MiaoshaOrder(long userId, long goodsId) {
		this.userId = userId;
		this.goodsId = goodsId;
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(long goodsId) {
		this.goodsId = goodsId;
	}
}
