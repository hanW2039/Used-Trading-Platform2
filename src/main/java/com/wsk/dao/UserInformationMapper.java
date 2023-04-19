package com.wsk.dao;

import com.wsk.pojo.UserInformation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    /**
     * 根据邮箱查询用户
     * @param email
     * @return
     */
    UserInformation selectUserInformationByEmail(String email);

    /**
     * 修改激活状态
     * @param phone
     * @param status
     */
    void updateStatus(@Param("phone") String phone, @Param("status") String status);

    int updateByPrimaryKeySelective(UserInformation record);

    int updateByPrimaryKey(UserInformation record);

    int selectIdByPhone(String phone);

    List<UserInformation> getAllForeach(List<Integer> list);
}