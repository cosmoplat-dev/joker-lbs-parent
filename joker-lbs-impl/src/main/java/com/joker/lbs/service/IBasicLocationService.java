package com.joker.lbs.service;

import java.util.List;

import com.joker.lbs.dto.BasicLocation;

/**
 * 
 * @author Joker
 *
 */
public interface IBasicLocationService {
	/* 处理一组点的方式，第一种是直接给一组点，另一种是给一个点，多次发送请求 */
	public final static int DISPOSE_MULTI_BY_MULTI_POINT = 1;// 单个点
	public final static int DISPOSE_MULTI_BY_SINGLE_POINT = 2;// 一组点

	/**
	 * @author Joker
	 * @功能：处理一个点 @说明：
	 */
	<T extends BasicLocation> T disposeOne(T position);

	/**
	 * @author Joker
	 * @功能：处理一组点，采用默认的方式处理 批量发送 @说明：
	 */
	<T extends BasicLocation> List<T> disposeList(List<T> positions);

	/**
	 * @author Joker
	 * @功能：用户指定处理方式来处理一组点 @说明：
	 */
	<T extends BasicLocation> List<T> disposeList(List<T> positions, int type);
}
