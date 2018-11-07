package com.joker.lbs.service;

import java.io.Serializable;
import java.util.List;

import com.joker.lbs.dto.BasicLocation;

/**
 * lbs位置服务
 * 
 * @author Joker
 *
 */
public interface ILbsService extends Serializable {

	/**
	 * 坐标转化
	 * 
	 * @param location
	 * @param from
	 *            源坐标类型 CoordtypeEnum.key
	 * @param to
	 *            目的坐标类型 CoordtypeEnum.key
	 * @param key
	 *            申请的key(暂时无用)
	 * @return
	 */
	public <T extends BasicLocation> T coordconvert(T location, String from, String to, String key);

	/**
	 * 批量坐标转化
	 * 
	 * @param locations
	 * @param from
	 *            源坐标类型 CoordtypeEnum.key
	 * @param to
	 *            目的坐标类型 CoordtypeEnum.key
	 * @param key
	 *            申请的key(暂时无用)
	 * @return
	 */
	public <T extends BasicLocation> List<T> coordconvert(List<T> locations, String from, String to, String key);

	/**
	 * 地址服务
	 * 
	 * @param locations
	 * @param coordtype
	 *            坐标类型 CoordtypeEnum.key wgs84 bd09 gcj02
	 * @param key
	 *            申请的key(暂时无用)
	 * @return
	 */
	public <T extends BasicLocation> List<T> geoconvert(List<T> locations, String coordtype, String key);

	/**
	 * 地址服务
	 * 
	 * @param locations
	 * @param coordtype
	 *            坐标类型 CoordtypeEnum.key wgs84 bd09 gcj02
	 * @param key
	 *            申请的key(暂时无用)
	 * @return
	 */
	public <T extends BasicLocation> T geoconvert(T location, String coordtype, String key);

}
