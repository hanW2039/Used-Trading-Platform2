package com.wsk.dao;

import com.wsk.pojo.UserInformation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

public interface UserInformationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserInformation record);

    int insertSelective(UserInformation record);

    UserInformation selectByPrimaryKey(Integer id);

    /**
     * 查询用户信息
     * @param phone
     * @return
     */
    UserInformation selectUserInformationByPhone(String phone);

    int updateByPrimaryKeySelective(UserInformation record);

    int updateByPrimaryKey(UserInformation record);

    int selectIdByPhone(String phone);

    List<UserInformation> getAllForeach(List<Integer> list);
}