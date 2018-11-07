package com.joker.lbs.service.impl.baidu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.joker.lbs.config.BMapConfig;
import com.joker.lbs.dto.BasicLocation;
import com.joker.lbs.service.impl.AbstractLocationService;
import com.joker.lbs.util.CloneUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 百度 坐标转化服务
 * 
 * @author Joker
 *
 */
@Service(BaiduDeflectorService.BEAN_NAME)
public class BaiduDeflectorService extends AbstractLocationService {
	public final static String BEAN_NAME = "baiduDeflectorService";
	private static final Logger log = LoggerFactory.getLogger(BaiduDeflectorService.class);

	@Autowired
	private BMapConfig bMapConfig;

	/* 这个是偏转key，是组装参数的是偶需要的 */
	protected String a_k = null;

	/* 这个是偏转服务器的url */
	protected String urlStr = null;
	protected String saveUrlStr = null;

	/**
	 * 最大处理数
	 */
	protected int maxDeflectNumber = 0;

	@PostConstruct
	public void init() {
		a_k = bMapConfig.getCoordconvert().getKey();
		urlStr = bMapConfig.getCoordconvert().getUrl();
		maxDeflectNumber = Integer.valueOf(bMapConfig.getCoordconvert().getNumber());
	}

	protected <T extends BasicLocation> String getParam(T position) {

		String param = String.format("ak=%s&coords=%f,%f&output=json", this.a_k, position.getLongitude(),
				position.getLatitude());
		return param;
	}

	protected <T extends BasicLocation> String getParam(List<T> positions) {
		String param = String.format("ak=%s&output=json", this.a_k);
		param += "&coords=";
		T position = null;
		for (int i = 0; i < positions.size(); i++) {
			position = positions.get(i);
			param += position.getLongitude() + ",";
			param += position.getLatitude() + ";";
		}
		if (param.lastIndexOf(";") != -1) {
			param = param.substring(0, param.lastIndexOf(";"));
		}
		return param;
	}

	protected String read(HttpURLConnection httpConnection) {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
			String line = null;
			sb = new StringBuffer("");
			while ((line = br.readLine()) != null)
				sb.append(line);
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

	public <T extends BasicLocation> List<T> disposeList(List<T> positions, int type) {
		return disposeList(positions);
	}

	protected <T extends BasicLocation> T parse(T position, String res) {
		log.info("【偏转一个点】返回的数据：" + res);
		T returnPosition = CloneUtils.shallowClone(position);
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;
		try {
			jsonObject = JSONObject.fromObject(res);
			if (jsonObject.isEmpty() || jsonObject.isNullObject()) {
				return returnPosition;
			}

			Integer status = (Integer) jsonObject.get("status");
			// 值为0或其它,0表示false；1表示true
			if (status != null && status != 0) {
				log.error("异常", res);
				return returnPosition;
			}
			jsonArray = (JSONArray) jsonObject.getJSONArray("result");

			try {
				jsonObject = jsonArray.getJSONObject(0);
				returnPosition.setDone(true);
				returnPosition.setLongitudeDone(Double.valueOf((Double) jsonObject.get("x")));
				returnPosition.setLatitudeDone(Double.valueOf((Double) jsonObject.get("y")));
			} catch (Exception e) {
				log.error(null, e);
			}
		} catch (Exception e) {
			log.error("parse", e);
		}
		return returnPosition;
	}

	protected <T extends BasicLocation> List<T> parse(List<T> positions, String result) {
		List<T> returnList = Lists.newArrayList();
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;
		try {
			jsonObject = JSONObject.fromObject(result);
			if (jsonObject.isEmpty() || jsonObject.isNullObject()) {
				return positions;
			}

			Integer status = (Integer) jsonObject.get("status");
			// 值为0或其它,0表示false；1表示true
			if (status != null && status != 0) {
				log.error("异常", result);
				return positions;
			}
			jsonArray = (JSONArray) jsonObject.getJSONArray("result");
			T position = null;
			for (int i = 0; i < jsonArray.size(); i++) {
				position = CloneUtils.shallowClone(positions.get(i));
				try {
					jsonObject = jsonArray.getJSONObject(i);
					position.setDone(true);
					position.setLongitudeDone((Double) jsonObject.get("x"));
					position.setLatitudeDone((Double) jsonObject.get("y"));
				} catch (Exception e) {
					log.error(null, e);
				} finally {
					returnList.add(position);
				}
			}
		} catch (ClassCastException e) {
			log.error(null, e);
		}
		return returnList;
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
