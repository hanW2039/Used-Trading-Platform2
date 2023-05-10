/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package com.wsk.service.Impl;

import com.wsk.bean.MallUser;
import com.wsk.dao.UserInformationMapper;
import com.wsk.pojo.QueryDTO;
import com.wsk.pojo.UserInformation;
import com.wsk.service.NewBeeMallUserService;
import com.wsk.util.PageQueryUtil;
import com.wsk.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewBeeMallUserServiceImpl implements NewBeeMallUserService {
    @Autowired
    private UserInformationMapper userInformationMapper;

    @Override
    public PageResult getNewBeeMallUsersPage(PageQueryUtil pageUtil) {
        QueryDTO queryDTO = new QueryDTO();
        queryDTO.setOffset(pageUtil.getPage()==1 ? 1 : (pageUtil.getPage()-1)* pageUtil.getLimit());
        queryDTO.setLimit(pageUtil.getLimit());
        List<UserInformation> userInformations = userInformationMapper.selectAll(queryDTO);
        List<MallUser> mallUsers = new ArrayList<>();
        for(UserInformation u : userInformations) {
            MallUser mallUser = new MallUser();
            mallUser.setNickName(u.getUsername());
            mallUser.setUserId(Long.valueOf(u.getId()));
            mallUser.setLoginName(u.getPhone());
            mallUser.setLockedFlag(u.getLockedflag());
            mallUser.setIsDeleted(u.getIsdeleted());
            mallUser.setCreateTime(u.getModified());
            mallUsers.add(mallUser);
        }
        int total = userInformationMapper.getCount();
        PageResult pageResult = new PageResult(mallUsers, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

//    @Override
//    public String register(String loginName, String password) {
//        if (mallUserMapper.selectByLoginName(loginName) != null) {
//            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
//        }
//        MallUser registerUser = new MallUser();
//        registerUser.setLoginName(loginName);
//        registerUser.setNickName(loginName);
//        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
//        registerUser.setPasswordMd5(passwordMD5);
//        if (mallUserMapper.insertSelective(registerUser) > 0) {
//            return ServiceResultEnum.SUCCESS.getResult();
//        }
//        return ServiceResultEnum.DB_ERROR.getResult();
//    }
//
//    @Override
//    public String login(String loginName, String passwordMD5, HttpSession httpSession) {
//        MallUser user = mallUserMapper.selectByLoginNameAndPasswd(loginName, passwordMD5);
//        if (user != null && httpSession != null) {
//            if (user.getLockedFlag() == 1) {
//                return ServiceResultEnum.LOGIN_USER_LOCKED.getResult();
//            }
//            //昵称太长 影响页面展示
//            if (user.getNickName() != null && user.getNickName().length() > 7) {
//                String tempNickName = user.getNickName().substring(0, 7) + "..";
//                user.setNickName(tempNickName);
//            }
//            NewBeeMallUserVO newBeeMallUserVO = new NewBeeMallUserVO();
//            BeanUtil.copyProperties(user, newBeeMallUserVO);
//            //设置购物车中的数量
//            httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, newBeeMallUserVO);
//            return ServiceResultEnum.SUCCESS.getResult();
//        }
//        return ServiceResultEnum.LOGIN_ERROR.getResult();
//    }
//
//    @Override
//    public NewBeeMallUserVO updateUserInfo(MallUser mallUser, HttpSession httpSession) {
//        NewBeeMallUserVO userTemp = (NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
//        MallUser userFromDB = mallUserMapper.selectByPrimaryKey(userTemp.getUserId());
//        if (userFromDB != null) {
//            if (StringUtils.hasText(mallUser.getNickName())) {
//                userFromDB.setNickName(NewBeeMallUtils.cleanString(mallUser.getNickName()));
//            }
//            if (StringUtils.hasText(mallUser.getAddress())) {
//                userFromDB.setAddress(NewBeeMallUtils.cleanString(mallUser.getAddress()));
//            }
//            if (StringUtils.hasText(mallUser.getIntroduceSign())) {
//                userFromDB.setIntroduceSign(NewBeeMallUtils.cleanString(mallUser.getIntroduceSign()));
//            }
//            if (mallUserMapper.updateByPrimaryKeySelective(userFromDB) > 0) {
//                NewBeeMallUserVO newBeeMallUserVO = new NewBeeMallUserVO();
//                BeanUtil.copyProperties(userFromDB, newBeeMallUserVO);
//                httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, newBeeMallUserVO);
//                return newBeeMallUserVO;
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public Boolean lockUsers(Integer[] ids, int lockStatus) {
//        if (ids.length < 1) {
//            return false;
//        }
//        return mallUserMapper.lockUserBatch(ids, lockStatus) > 0;
//    }
}
