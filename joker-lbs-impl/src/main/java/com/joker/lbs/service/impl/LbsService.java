package com.joker.lbs.service.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.joker.lbs.dto.BasicLocation;
import com.joker.lbs.enums.CoordtypeEnum;
import com.joker.lbs.service.IBasicLocationAllService;
import com.joker.lbs.service.ILbsService;
import com.joker.lbs.util.MapCoordinateConvertUtils;
import com.joker.lbs.worker.common.ProcessRequest;
import com.joker.lbs.worker.common.ProcessResult;
import com.joker.lbs.worker.thread.GuavaExecutorService;
import com.joker.lbs.worker.thread.VCallable;
import com.joker.lbs.worker.thread.VExecutorService;

@Component("lbsService")
public class LbsService implements ILbsService, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1302218199535257922L;

	private static final Logger log = LoggerFactory.getLogger(LbsService.class);

	/**
	 * 多线程处理偏转
	 */
	private VExecutorService coordconvertService = null;
	/**
	 * 多线程处理地址
	 */
	private VExecutorService geoconvertService = null;
	@Resource
	private IBasicLocationAllService aMapDeflectorAddressService;

	@Resource
	private IBasicLocationAllService baiduDeflectorAddressService;

	private static final Integer threadNums = 5;

	@PostConstruct
	public void init() {
		coordconvertService = new GuavaExecutorService();
		geoconvertService = new GuavaExecutorService();
	}

	@PreDestroy
	public void dostory() {
		if (coordconvertService != null) {
			coordconvertService.shutdown();
		}
		if (geoconvertService != null) {
			geoconvertService.shutdown();
		}
	}

	/**
	 * 动态执行坐标转化
	 * 
	 * @param lon
	 *            经度
	 * @param lat
	 *            纬度
	 * @param from
	 *            源坐标类型
	 * @param to
	 *            目的坐标类型
	 * @return
	 */
	private double[] invokeCoordconvert(double lon, double lat, String from, String to) {
		double[] result = null;
		Class<MapCoordinateConvertUtils> clazz = MapCoordinateConvertUtils.class;
		try {
			Method declaredMethod = clazz.getDeclaredMethod(MapCoordinateConvertUtils.getDeclaredMethod(from, to),
					double.class, double.class);

			double[] newloglat = (double[]) declaredMethod.invoke(clazz, lon, lat);
			result = new double[2];
			result[0] = newloglat[0];
			result[1] = newloglat[1];
		} catch (Exception e) {
			log.error("LbsService.invokeCoordconvert", e);
		}
		return result;
	}

	public <T extends BasicLocation> T coordconvert(T location, String from, String to, String key) {
		if (location != null) {
			location.setFromCoordtype(from);
			location.setToCoordtype(to);
			double[] coordconvert = invokeCoordconvert(location.getLongitude(), location.getLatitude(), from, to);
			if (coordconvert != null) {
				location.setDone(true);
				location.setLongitudeDone(coordconvert[0]);
				location.setLatitudeDone(coordconvert[1]);
			}
		}
		return location;
	}

	/**
	 * 多线程进行经纬度偏转
	 */
	@SuppressWarnings("unchecked")
	public <T extends BasicLocation> List<T> coordconvert(List<T> locations, final String from, final String to,
			final String key) {
		List<T> results = Lists.newArrayList();
		long t1 = System.currentTimeMillis();
		if (CollectionUtils.isNotEmpty(locations)) {

			log.info("LbsService.coordconvert -->size = {}", locations.size());
			try {
				int size = locations.size();
				if (size < threadNums) {
					for (T t : locations) {
						t = coordconvert(t, from, to, key);
						results.add(t);
					}
				} else {
					List<VCallable> callLists = Lists.newArrayList();
					ProcessRequest request = null;
					for (int i = 0; i <= threadNums; i++) {
						log.info("LbsService.coordconvert -->start = {} , end= {}", size / threadNums * i,
								i == threadNums ? size : size / threadNums * (i + 1));
						request = new ProcessRequest(locations.subList(size / threadNums * i,
								i == threadNums ? size : size / threadNums * (i + 1)));

						callLists.add(new VCallable(request) {

							public ProcessResult custom(ProcessRequest request) {
								List<T> newResults = Lists.newArrayList();
								List<T> list = (List<T>) request.getObj();
								for (T t : list) {
									t = coordconvert(t, from, to, key);
									newResults.add(t);
								}
								return new ProcessResult(newResults);
							}
						});
					}

					List<ProcessResult> processResults = coordconvertService.orderSolve(callLists);
					for (ProcessResult processResult : processResults) {
						results.addAll((List<T>) processResult.getObj());
					}

				}

			} catch (Exception e) {
				log.error("LbsService.coordconvert", e);
			}
			long t2 = System.currentTimeMillis();
			log.info("LbsService.coordconvert -->size = {} , end= {}", locations.size(), (t2 - t1));
		}
		return results;
	}

	/**
	 * 多线程进行地址转换
	 */
	@SuppressWarnings("unchecked")
	public <T extends BasicLocation> List<T> geoconvert(List<T> locations, final String coordtype, final String key) {
		List<T> results = Lists.newArrayList();
		long t1 = System.currentTimeMillis();
		if (CollectionUtils.isNotEmpty(locations)) {
			log.info("LbsService.geoconvert -->size = {}", locations.size());
			try {
				int size = locations.size();
				if (size < threadNums) {
					for (T t : locations) {
						results.add(geoconvert(t, coordtype, key));
					}
				} else {

					List<VCallable> callLists = Lists.newArrayList();
					ProcessRequest request = null;
					for (int i = 0; i <= threadNums; i++) {
						request = new ProcessRequest(locations.subList(size / threadNums * i,
								i == threadNums ? size : size / threadNums * (i + 1)));

						callLists.add(new VCallable(request) {

							public ProcessResult custom(ProcessRequest request) {
								List<T> newResults = Lists.newArrayList();
								List<T> list = (List<T>) request.getObj();
								for (T t : list) {
									t = geoconvert(t, coordtype, key);
									newResults.add(t);
								}
								return new ProcessResult(newResults);
							}
						});
					}

					List<ProcessResult> processResults = geoconvertService.orderSolve(callLists);
					for (ProcessResult processResult : processResults) {
						results.addAll((List<T>) processResult.getObj());
					}
				}

			} catch (Exception e) {
				log.error("LbsService.geoconvert", e);
			}
			long t2 = System.currentTimeMillis();
			log.info("LbsService.geoconvert -->size = {} , end= {}", results.size(), (t2 - t1));
		}
		return results;
	}

	public <T extends BasicLocation> T geoconvert(T location, String coordtype, String key) {
		T newLocation = location;
		if (location != null) {
			location.setFromCoordtype(coordtype);
			if (CoordtypeEnum.gaode.getKey().equals(coordtype)) {// 高德

				location.setCoordconvert(true, CoordtypeEnum.gaode.getKey(), location.getLongitude(),
						location.getLatitude());
				newLocation = aMapDeflectorAddressService.disposeOne(location);

			} else if (CoordtypeEnum.baidu.getKey().equals(coordtype)) {// 百度

				location.setCoordconvert(true, CoordtypeEnum.baidu.getKey(), location.getLongitude(),
						location.getLatitude());
				newLocation = baiduDeflectorAddressService.disposeOne(location);
			} else if (CoordtypeEnum.gps.getKey().equals(coordtype)) {// wgs84
				location.setToCoordtype(CoordtypeEnum.baidu.getKey());
				double[] coordconvert = invokeCoordconvert(location.getLongitude(), location.getLatitude(), coordtype,
						CoordtypeEnum.baidu.getKey());
				if (coordconvert != null) {
					location.setDone(true);
					location.setLongitudeDone(coordconvert[0]);
					location.setLatitudeDone(coordconvert[1]);
				}
				newLocation = baiduDeflectorAddressService.disposeOne(location);

			} else {
				log.error("LbsService.geoconvert:不支持坐标系",
						"coordtype:" + coordtype + ",location=" + newLocation.toString());
			}

		}
		return newLocation;
	}

}
