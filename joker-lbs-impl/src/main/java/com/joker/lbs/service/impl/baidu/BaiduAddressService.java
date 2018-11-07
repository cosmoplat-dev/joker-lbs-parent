package com.joker.lbs.service.impl.baidu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.joker.lbs.config.BMapConfig;
import com.joker.lbs.dto.BasicLocation;
import com.joker.lbs.service.impl.AbstractLocationService;
import com.joker.lbs.util.CloneUtils;

import net.sf.json.JSONObject;

/**
 * 百度 地址服务
 * 
 * @author Joker
 *
 */
@Service(BaiduAddressService.BEAN_NAME)
public class BaiduAddressService extends AbstractLocationService {
	public final static String BEAN_NAME = "baiduAddressService";
	private static final Logger log = LoggerFactory.getLogger(BaiduAddressService.class);
	private final String emptyStr = StringUtils.EMPTY;
	private final String noInfoStr = "暂无";

	@Autowired
	private BMapConfig bMapConfig;

	/* 这个是百度的key，是组装参数需要的 */
	protected String a_k = null;
	/* 这个是百度服务器的url */
	protected String urlStr = null;
	protected String saveUrlStr = null;

	/**
	 * 最大处理数
	 */
	protected int maxDeflectNumber = 0;

	@PostConstruct
	public void init() {
		a_k = bMapConfig.getGeoconvert().getKey();
		urlStr = bMapConfig.getGeoconvert().getUrl();
		maxDeflectNumber = Integer.valueOf(bMapConfig.getGeoconvert().getNumber());
	}

	protected <T extends BasicLocation> String getParam(T position) {

		String param = String.format("ak=%s&output=json&callback=renderReverse&pois=1&location=%f,%f", this.a_k,
				position.getLatitudeDone(), position.getLongitudeDone());
		return param;
	}

	public <T extends BasicLocation> List<T> disposeList(final List<T> positions) {
		List<T> returnList = Lists.newArrayList();
		T postion = null;
		for (int i = 0; i < positions.size(); i++) {
			postion = disposeOne(positions.get(i));
			returnList.add(postion);
		}
		return returnList;

	}

	protected String read(HttpURLConnection httpConnection) {
		StringBuffer sb = new StringBuffer("");
		BufferedReader br = null;
		try {
			// BufferedReader中文乱码问题的解决方法
			br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(), "utf-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e1) {
			log.error("read", e1);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.error("read", e);
				}
			}
		}
		return sb.toString();
	}

	protected <T extends BasicLocation> T parse(T baiduPosition, String json) {
		T returnPosition;
		JSONObject jsonObject = null;
		String formatted_address = emptyStr;// 结构化地址信息
		String sematic_description = emptyStr;// 当前位置结合POI的语义化结果描述。
		String str = emptyStr;
		String provinceStr = emptyStr;
		String cityStr = emptyStr;
		// String districtStr = emptyStr;
		// String roadStr = emptyStr;
		// String poiStr = emptyStr;
		// JSONObject addressComponentObject = null;
		try {
			log.info("BaiduAddressService.parse.json= " + json);
			if (StringUtils.isEmpty(json)) {
				return baiduPosition;
			}
			int indexLeft = json.indexOf("(");
			int indexMid = json.indexOf("{");
			int indexRight = json.indexOf(")");
			int jsonLen = json.length();
			if (indexLeft >= 0 && indexLeft < indexMid && indexRight == jsonLen - 1) {
				json = json.substring(indexLeft + 1, jsonLen - 1);
			}
			jsonObject = JSONObject.fromObject(json);
			if (jsonObject.isEmpty() || jsonObject.isNullObject()) {
				return baiduPosition;
			}
			jsonObject = (JSONObject) jsonObject.get("result");
			if (jsonObject.isEmpty() || jsonObject.isNullObject()) {
				return baiduPosition;
			}

			formatted_address = (String) jsonObject.get("formatted_address");

			sematic_description = (String) jsonObject.get("sematic_description");

			/* 获取省 */
			/*
			 * addressComponentObject = (JSONObject)
			 * jsonObject.get("addressComponent"); if
			 * (addressComponentObject.isEmpty() ||
			 * addressComponentObject.isNullObject()) { return baiduPosition; }
			 * 
			 * provinceStr = addressComponentObject.getString("province");
			 * 
			 * cityStr = addressComponentObject.getString("city");
			 * 
			 * districtStr = addressComponentObject.getString("district");
			 * 
			 * roadStr = addressComponentObject.getString("street");
			 * 
			 * poiStr = addressComponentObject.getString("street_number");
			 */
		} catch (ClassCastException e) {
			log.error("parse", e);
		}
		if (provinceStr != null && cityStr != null && provinceStr.equals(cityStr)) {
			provinceStr = emptyStr;
		}
		// str = provinceStr + cityStr + districtStr + roadStr + poiStr;
		str = formatted_address + sematic_description;
		if (str.equals(emptyStr)) {
			str = noInfoStr;
		}
		log.info("BaiduAddressService.parse.address =  " + str);
		returnPosition = CloneUtils.shallowClone(baiduPosition);
		returnPosition.setAddress(str);
		return returnPosition;
	}

	public <T extends BasicLocation> List<T> disposeList(List<T> positions, int type) {
		return disposeList(positions);
	}

	protected <T extends BasicLocation> List<T> parse(List<T> positions, String result) {
		return null;
	}

	protected <T extends BasicLocation> String getParam(List<T> positions) {
		return null;
	}

	protected String getApiUrl() {
		return this.urlStr;
	}

	protected int getMaxDisposeNum() {
		return this.maxDeflectNumber;
	}

	protected String getApiSaveUrl() {
		return this.saveUrlStr;
	}

}
