package com.joker.lbs.service.impl.baidu;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.joker.lbs.dto.BasicLocation;
import com.joker.lbs.service.IBasicLocationAllService;
import com.joker.lbs.service.IBasicLocationService;

/**
 * 百度 坐标转化、取地址服务
 * 
 * @author xs guo
 *
 */
@Service(BaiduDeflectorAddressService.BEAN_NAME)
public class BaiduDeflectorAddressService implements IBasicLocationAllService {

	public final static String BEAN_NAME = "baiduDeflectorAddressService";
	@Resource(name = BaiduDeflectorService.BEAN_NAME)
	private IBasicLocationService deflectorService = null;
	@Resource(name = BaiduAddressService.BEAN_NAME)
	private IBasicLocationService addressService = null;

	public <T extends BasicLocation> T disposeOne(T postion) {

		if (postion != null) {
			// 如果经纬度为0，需要进行偏转
			if (postion.getLatitudeDone() == 0 && postion.getLongitudeDone() == 0) {
				postion = deflectorService.disposeOne(postion);
			}

			postion = addressService.disposeOne(postion);
		}
		return postion;
	}

	public <T extends BasicLocation> List<T> disposeList(List<T> positions) {
		if (positions != null) {

			List<T> needDeflectorLists = Lists.newArrayList();
			List<T> notNeedDeflectorLists = Lists.newArrayList();
			List<T> allDeflectorLists = Lists.newArrayList();
			for (T t : positions) {
				if (t.getLatitudeDone() == 0 && t.getLongitudeDone() == 0) {
					needDeflectorLists.add(t);
				} else {
					t.setDone(true);
					notNeedDeflectorLists.add(t);
				}
			}

			allDeflectorLists.addAll(notNeedDeflectorLists);

			if (CollectionUtils.isNotEmpty(needDeflectorLists)) {
				needDeflectorLists = deflectorService.disposeList(needDeflectorLists);
				allDeflectorLists.addAll(needDeflectorLists);
			}

			allDeflectorLists = addressService.disposeList(allDeflectorLists);
			return allDeflectorLists;
		}
		return positions;
	}

	public <T extends BasicLocation> List<T> disposeList(List<T> positions, int type) {
		List<T> returnList = Lists.newArrayList();
		switch (type) {
		case IBasicLocationService.DISPOSE_MULTI_BY_SINGLE_POINT:
			T positon = null;
			for (int i = 0; i < positions.size(); i++) {
				positon = positions.get(i);
				returnList.add(disposeOne(positon));
			}
			break;
		case IBasicLocationService.DISPOSE_MULTI_BY_MULTI_POINT:
			returnList.addAll(disposeList(positions));
			break;
		default:
			returnList.addAll(disposeList(positions));
			break;
		}
		return returnList;
	}

}
