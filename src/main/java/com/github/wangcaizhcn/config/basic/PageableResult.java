package com.github.wangcaizhcn.config.basic;

import java.util.ArrayList;
import java.util.List;

/**
 * 翻页查询结果统一返回，屏蔽底层不同ORM返回的格式，需要统一组织成该对象
 * @author wangcai
 */
public class PageableResult<T> {

	// 总记录数
	private long total = 0;
	
	// 结果集
	private List<T> data = new ArrayList<>();

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}
	
}
