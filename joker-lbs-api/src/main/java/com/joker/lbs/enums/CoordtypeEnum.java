package com.joker.lbs.enums;

public enum CoordtypeEnum {
	gps("wgs84", "GPS经纬度(wgs84)"), baidu("bd09", "百度经纬度坐标(bd09ll)"), gaode("gcj02", "高德经纬度坐标(GCJ02)"), mapbar("mapbar",
			"mapbar地图坐标"), Beijing54("Beijing54", "Beijing54"), Xian80("Xian80", "Xian80");

	private final String key;
	private final String value;

	private CoordtypeEnum(String key, String value) {
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
