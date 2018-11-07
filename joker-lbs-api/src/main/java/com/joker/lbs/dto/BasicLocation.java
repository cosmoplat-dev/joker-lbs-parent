package com.joker.lbs.dto;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 基础postion
 * 
 * @author Joker
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasicLocation implements Cloneable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1013906685679092140L;
	/**
	 * 对象标识
	 */
	private String id;
	/**
	 * 源坐标系
	 */
	private String fromCoordtype;
	/**
	 * 纬度
	 */
	private double latitude;
	/**
	 * 经度
	 */
	private double longitude;

	/**
	 * 是否已经偏转
	 */
	private boolean done = false;
	/**
	 * 目的坐标系
	 */
	private String toCoordtype;

	/**
	 * 偏转后纬度
	 */
	private double latitudeDone = 0;
	/**
	 * 偏转后经度
	 */
	private double longitudeDone = 0;

	/**
	 * 地址
	 */
	private String address = null;

	public BasicLocation() {
	}

	public BasicLocation(String fromCoordtype, double latitude, double longitude, String toCoordtype) {
		this.fromCoordtype = fromCoordtype;
		this.latitude = latitude;
		this.longitude = longitude;
		this.toCoordtype = toCoordtype;
	}

	public BasicLocation(String id, String fromCoordtype, double latitude, double longitude, String toCoordtype) {
		this.id = id;
		this.fromCoordtype = fromCoordtype;
		this.latitude = latitude;
		this.longitude = longitude;
		this.toCoordtype = toCoordtype;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public double getLatitudeDone() {
		return latitudeDone;
	}

	public void setLatitudeDone(double latitudeDone) {
		this.latitudeDone = latitudeDone;
	}

	public double getLongitudeDone() {
		return longitudeDone;
	}

	public void setLongitudeDone(double longitudeDone) {
		this.longitudeDone = longitudeDone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFromCoordtype() {
		return fromCoordtype;
	}

	public void setFromCoordtype(String fromCoordtype) {
		this.fromCoordtype = fromCoordtype;
	}

	public String getToCoordtype() {
		return toCoordtype;
	}

	public void setToCoordtype(String toCoordtype) {
		this.toCoordtype = toCoordtype;
	}

	/**
	 * 设置坐标转化结果
	 * 
	 * @param done
	 * @param longitudeDone
	 * @param latitudeDone
	 */
	public void setCoordconvert(boolean done, String toCoordtype, double longitudeDone, double latitudeDone) {
		this.setToCoordtype(toCoordtype);
		this.setDone(done);
		this.setLatitudeDone(latitudeDone);
		this.setLongitudeDone(longitudeDone);
	}

	public void setGeoconvert() {

	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
