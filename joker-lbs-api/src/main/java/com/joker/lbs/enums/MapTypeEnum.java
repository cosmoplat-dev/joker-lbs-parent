package com.joker.lbs.enums;

/**
 * 地图
 * 
 * @author Joker
 *
 */
public enum MapTypeEnum {
	MAPABC("MapABC", "高德"), BAIDU("Baidu", "百度"), MAPBAR("MapBar", "图吧"),

	GOOGLE("Google", "谷歌"), GCJWEB("Gcjweb", "盐都天地图"), AMAP("AMap", "高德"), NNWEB("Nnweb", "南宁地图"), HZWEB("Hzweb",
			"湖州地图"), TJWEB("Tjweb", "天津地图");

	private final String key;
	private final String value;

	private MapTypeEnum(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
