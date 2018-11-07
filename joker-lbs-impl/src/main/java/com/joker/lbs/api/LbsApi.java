package com.joker.lbs.api;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.joker.lbs.dto.BasicLocation;
import com.joker.lbs.service.ILbsService;
import com.joker.lbs.util.RestResultDto;

@RestController
@RequestMapping("/nhbapi/rest/lbs")
public class LbsApi {

	private final static Logger log = LoggerFactory.getLogger(LbsApi.class);

	@Resource
	private ILbsService lbsService;

	/**
	 * 坐标转化
	 * http://localhost:9090/nhbapi/rest/lbs/coordconvert/v2?location=120.78007000000001,31.631500000000003&from=wgs84&to=bd09
	 * http://localhost:9090/nhbapi/rest/lbs/coordconvert/v2?from=wgs84&location=114.25191000000001,30.5298;114.25203,30.529680000000003;114.25239,30.529320000000002;114.25257,30.529300000000003;114.25261,30.52934;114.25260000000002,30.529390000000003;114.25239,30.529390000000003;114.25217,30.529500000000002;114.25176,30.529790000000002;114.25157000000002,30.529730000000004;114.25147000000001,30.529510000000002;114.25147000000001,30.529660000000003;114.25166000000002,30.5299;114.25147000000001,30.53008;114.25128000000001,30.530200000000004;114.25144,30.53038;114.25175000000002,30.530610000000003;114.25193000000002,30.53076;114.25213000000001,30.530680000000004;114.25246000000001,30.530400000000004&to=bd09
	 * 
	 * @param location
	 *            经纬度
	 * @param from
	 *            from
	 * @param to
	 *            目的坐标系
	 * @return
	 */
	@RequestMapping(value = "coordconvert/v2")
	public RestResultDto<?> coordconvert2(String location, String from, String to) {

		log.info("LbsApi-->coordconvert:location={},from={},to={}", location, from, to);
		RestResultDto<?> apiReponse = null;
		try {
			String[] xys = location.split(";");
			List<BasicLocation> locations = Lists.newArrayList();
			BasicLocation bl = null;
			for (String xy : xys) {
				bl = new BasicLocation();
				// 纬度
				Double y = Double.valueOf(xy.split(",")[1]);
				// 经度
				Double x = Double.valueOf(xy.split(",")[0]);
				bl.setFromCoordtype(from);
				bl.setLongitude(x);
				bl.setLatitude(y);
				locations.add(bl);
			}
			locations = lbsService.coordconvert(locations, from, to, null);
			apiReponse = RestResultDto.newSuccess(locations);

		} catch (Exception e) {
			apiReponse = RestResultDto.newFalid(e.getMessage());
			log.error("LbsApi.coordconvert", e);
		}

		return apiReponse;
	}

	/**
	 * 自己经纬度转地址
	 * http://localhost:9090/nhbapi/rest/lbs/geoconvert/v2?location=120.78007000000001,31.631500000000003&batch=false&coordtype=wgs84
	 * http://localhost:9090/nhbapi/rest/lbs/geoconvert/v2?coordtype=wgs84&batch=true&location=114.24806000000001,30.529390000000003;114.24818,30.529560000000004;114.24806000000001,30.529380000000003;114.2485,30.52914;114.24868000000001,30.528980000000004;114.24886000000001,30.528940000000002;114.24917,30.52914;114.24936000000001,30.529280000000004;114.24955000000001,30.529420000000002;114.24971000000001,30.529600000000002;114.24997,30.529770000000003;114.25036000000001,30.5301;114.25051,30.53028;114.25066000000001,30.530490000000004;114.25080000000001,30.530670000000004;114.25094000000001,30.530880000000003;114.25114,30.53103;114.25132,30.530910000000002;114.25176,30.530730000000002;114.25196000000001,30.53075
	 * 
	 * @param location
	 *            经纬度
	 * @param batch
	 *            是否批量操作
	 * @param coordtype
	 *            坐标系
	 * @return
	 */
	@RequestMapping(value = "geoconvert/v2")
	public RestResultDto<?> geoconvert2(String location, String batch, String coordtype) {
		log.info("LbsApi-->geoconvert:location={},batch={},coordtype={}", location, batch, coordtype);
		RestResultDto<?> apiReponse = null;
		try {
			if (StringUtils.isEmpty(batch)) {
				batch = "false";
			}
			if (Boolean.valueOf(batch)) {// 判断是否批量操作
				String[] xys = location.split(";");
				List<BasicLocation> locations = Lists.newArrayList();
				BasicLocation bl = null;
				for (String xy : xys) {
					bl = new BasicLocation();
					// 纬度
					Double y = Double.valueOf(xy.split(",")[1]);
					// 经度
					Double x = Double.valueOf(xy.split(",")[0]);
					// 源坐标系
					bl.setFromCoordtype(coordtype);
					bl.setLongitude(x);
					bl.setLatitude(y);
					locations.add(bl);
				}
				locations = lbsService.geoconvert(locations, coordtype, null);
				apiReponse = RestResultDto.newSuccess(locations);

			} else {// 单个经纬度
				// 纬度
				Double y = Double.valueOf(location.split(",")[1]);
				// 经度
				Double x = Double.valueOf(location.split(",")[0]);
				BasicLocation bl = new BasicLocation();
				bl.setFromCoordtype(coordtype);
				bl.setLongitude(x);
				bl.setLatitude(y);
				bl = lbsService.geoconvert(bl, coordtype, null);
				apiReponse = RestResultDto.newSuccess(bl);
			}
		} catch (Exception e) {
			apiReponse = RestResultDto.newFalid(e.getMessage());
			log.error("LbsApi.geoconvert", e);
		}
		return apiReponse;
	}
}
