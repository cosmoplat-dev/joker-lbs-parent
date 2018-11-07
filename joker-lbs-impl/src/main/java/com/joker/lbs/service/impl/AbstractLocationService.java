package com.joker.lbs.service.impl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.joker.lbs.dto.BasicLocation;
import com.joker.lbs.service.IBasicLocationService;

public abstract class AbstractLocationService implements IBasicLocationService {

	private static final Logger log = LoggerFactory.getLogger(AbstractLocationService.class);

	/**
	 * 超时
	 */
	protected final int timeout = 1000 * 5;

	public void save(BasicLocation position) {
		if (position != null) {
			HttpURLConnection httpConnection = null;
			try {
				httpConnection = getConnection(getApiSaveUrl());

				String param = saveParam(position);
				write(httpConnection, param);
				read(httpConnection);
			} catch (Exception e) {
				log.error("disposeOne", e);
			} finally {
				httpConnection.disconnect();
			}

		}
	}

	private String saveParam(BasicLocation position) {
		String param = "lng=" + position.getLongitude() + "&lat=" + position.getLatitude() + "&bs="
				+ position.getAddress();
		return param;
	}

	public <T extends BasicLocation> T disposeOne(T position) {
		if (position != null) {
			HttpURLConnection httpConnection = null;
			try {
				httpConnection = getConnection(getApiUrl());
				String param = getParam(position);
				write(httpConnection, param);
				String string = read(httpConnection);
				return parse(position, string);
			} catch (Exception e) {
				log.error("disposeOne", e);
			} finally {
				httpConnection.disconnect();
			}

		}
		return position;
	}

	public <T extends BasicLocation> List<T> disposeList(final List<T> positions) {

		List<T> results = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(positions)) {
			log.info("【偏转取地址】需更新点数=" + positions.size());
			int start = 0;
			List<T> subList = null;
			while (start < positions.size()) {
				int limit = start + getMaxDisposeNum();
				subList = positions.subList(start, limit > positions.size() ? positions.size() : limit);
				HttpURLConnection httpConnection = null;
				try {
					httpConnection = getConnection(getApiUrl());
					String param = getParam(subList);
					write(httpConnection, param);
					String string = read(httpConnection);
					results.addAll(parse(subList, string));
				} catch (Exception e) {
					log.error("disposeList", e);
				} finally {
					httpConnection.disconnect();
				}

				start = limit;
				log.info("【偏转取地址】已偏转更新点数=" + subList.size());
			}

		}
		return results;
	}

	/**
	 * @author xs guo
	 * @功能：获取与给定url之间的连接 @说明：
	 */
	protected final HttpURLConnection getConnection(String urlStr) {
		URL url = null;
		HttpURLConnection httpConnection = null;
		try {
			url = new URL(urlStr);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setRequestMethod("POST");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			httpConnection.setConnectTimeout(timeout);
			httpConnection.setReadTimeout(timeout);
			httpConnection.connect();
		} catch (MalformedURLException e) {
			log.error("getConnection", e);
		} catch (IOException e) {
			log.error("getConnection", e);
		}
		return httpConnection;
	}

	/**
	 * @author xs guo
	 * @功能：把我们封装好的参数传递到服务器 @说明：
	 */
	protected final void write(HttpURLConnection httpConnection, String param) {
		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(new BufferedOutputStream(httpConnection.getOutputStream()));
			osw.write(param);
			osw.flush();
		} catch (IOException e) {
			log.error("write", e);
		} finally {
			if (osw != null) {
				try {
					osw.close();
				} catch (Exception e) {
					log.error("write", e);
				}
			}
		}
	}

	/**
	 * @author xs guo
	 * @功能：读取高传回来的字节流，然后封装成字符串
	 * @说明：由于使用bufferreader读取有问题，然后重写了这个方法，现在这个方法是直接读取字节流，然后转化成字符串
	 * @修改：读xml和读json的方法还不一样
	 */
	abstract protected String read(HttpURLConnection httpConnection);

	/**
	 * @author xs guo
	 * @功能：分析一个点处理后的json或者xml
	 * @说明：本来是设计成都分析json的，后来发现json在偏转的时候，容易出问题，所以偏转就继续使用xml
	 */
	abstract protected <T extends BasicLocation> T parse(T position, String result);

	/**
	 * @author xs guo
	 * @功能：分析一组点处理后的json或者xml @说明：
	 */
	abstract protected <T extends BasicLocation> List<T> parse(final List<T> positions, String result);

	/**
	 * @author xs guo
	 * @功能：获取处理一个点的参数 @说明：
	 */
	abstract protected <T extends BasicLocation> String getParam(T position);

	/**
	 * @author xs guo
	 * @功能：获取处理一组点的参数 @说明：
	 */
	abstract protected <T extends BasicLocation> String getParam(List<T> positions);

	/**
	 * 获取服务url
	 * 
	 * @return
	 */
	abstract protected String getApiUrl();

	/**
	 * 获取服务url
	 * 
	 * @return
	 */
	abstract protected String getApiSaveUrl();

	/**
	 * 获取最大处理数量
	 * 
	 * @return
	 */
	abstract protected int getMaxDisposeNum();
}
