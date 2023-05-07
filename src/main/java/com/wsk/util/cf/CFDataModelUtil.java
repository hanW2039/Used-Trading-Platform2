package com.wsk.util.cf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wsk.pojo.ScoreRecord;
import com.wsk.pojo.UserInformation;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;


/**
 * 构建用户-商品操作行为矩阵
 */
@Slf4j
public class CFDataModelUtil {

	/**
	 * 构建用户-商品评分矩阵
	 * @param scoreRecordList 商品评分集合
	 * @param cUser 当前用户
	 * @return
	 */
	public DataModel getItemScoreDadaModel(List<ScoreRecord> scoreRecordList,
			UserInformation cUser){
		DataModel model = null;
		log.info("******构建用户-商品评分矩阵开始******");
		// 标记当前登录的用户是否有商品评分记录
		Boolean flag = false;
		//如果数据库中有商品评分记录
		if(scoreRecordList!=null && scoreRecordList.size()>0){
			//定义map保存用户id和用户对不同商品的评分集合
			Map<Integer,List<Preference>> map = new HashMap<Integer,List<Preference>>();
			//遍历矩阵
			for(ScoreRecord scoreRecord : scoreRecordList){
				int userid = scoreRecord.getUid();
				if(userid==cUser.getId()){
					flag = true;
				}
				int sid = scoreRecord.getSid();
				Float preference = Float.valueOf(scoreRecord.getScore().toString());
				List<Preference> preferenceList = null;
				if(map.containsKey(userid)){
					preferenceList = map.get(userid);
				}else{
					preferenceList = new ArrayList<Preference>();
				}
				preferenceList.add(new GenericPreference(userid, sid, preference));
				map.put(userid, preferenceList);
			}
			if(!flag){
				log.info("***当前登录用户没有商品评分记录！***");
			}else{
				//定义用户-商品评分map集合
				FastByIDMap<PreferenceArray> preferences = new FastByIDMap<PreferenceArray>();
				Set<Integer> set = map.keySet();
				for(Integer i:set){
					List<Preference> preferenceList = map.get(i);
					preferences.put(i,new GenericUserPreferenceArray(preferenceList));
				}
				//实例化用户-商品评分矩阵
			    model = new GenericDataModel(preferences);
			}
		}else{
			log.info("******数据库中没有商品评分记录！******");
		}
		log.info("******构建用户-商品评分矩阵结束******");
		return model;
	}
	
}
