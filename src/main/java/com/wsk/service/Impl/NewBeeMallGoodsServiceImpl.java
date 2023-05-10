/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package com.wsk.service.Impl;


import com.wsk.bean.NewBeeMallGoods;
import com.wsk.dao.ShopInformationMapper;
import com.wsk.pojo.QueryDTO;
import com.wsk.pojo.ShopInformation;
import com.wsk.service.NewBeeMallGoodsService;
import com.wsk.util.PageQueryUtil;
import com.wsk.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lenovo
 */
@Service
public class NewBeeMallGoodsServiceImpl implements NewBeeMallGoodsService {
    @Autowired
    private ShopInformationMapper shopInformationMapper;
    @Override
    public PageResult getNewBeeMallGoodsPage(PageQueryUtil pageUtil) {
        QueryDTO queryDTO = new QueryDTO();
        queryDTO.setOffset(pageUtil.getPage()==1 ? 1 : (pageUtil.getPage()-1)* pageUtil.getLimit());
        queryDTO.setLimit(pageUtil.getLimit());
        List<ShopInformation> shopInformations = shopInformationMapper.selectByQueryDTO(queryDTO);
        List<NewBeeMallGoods> goodsList = new ArrayList<>();
        int total = shopInformationMapper.getCounts();
        for(ShopInformation s : shopInformations) {
            NewBeeMallGoods goods = new NewBeeMallGoods();
            goods.setGoodsId(Long.valueOf(s.getId()));
            goods.setGoodsName(s.getName());
            goods.setGoodsIntro(s.getRemark());
            goods.setGoodsCoverImg(s.getImage());
            goods.setStockNum(s.getQuantity());
            goods.setSellingPrice(s.getPrice().intValue());
            goods.setGoodsSellStatus(s.getStatus());
            goods.setCreateTime(s.getModified());
            goodsList.add(goods);
        }
        PageResult pageResult = new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

//    @Override
//    public String saveNewBeeMallGoods(NewBeeMallGoods goods) {
//        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
//        // 分类不存在或者不是三级分类，则该参数字段异常
//        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
//            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
//        }
//        if (goodsMapper.selectByCategoryIdAndName(goods.getGoodsName(), goods.getGoodsCategoryId()) != null) {
//            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
//        }
//        goods.setGoodsName(NewBeeMallUtils.cleanString(goods.getGoodsName()));
//        goods.setGoodsIntro(NewBeeMallUtils.cleanString(goods.getGoodsIntro()));
//        goods.setTag(NewBeeMallUtils.cleanString(goods.getTag()));
//        if (goodsMapper.insertSelective(goods) > 0) {
//            return ServiceResultEnum.SUCCESS.getResult();
//        }
//        return ServiceResultEnum.DB_ERROR.getResult();
//    }
//
//    @Override
//    public void batchSaveNewBeeMallGoods(List<NewBeeMallGoods> newBeeMallGoodsList) {
//        if (!CollectionUtils.isEmpty(newBeeMallGoodsList)) {
//            goodsMapper.batchInsert(newBeeMallGoodsList);
//        }
//    }
//
//    @Override
//    public String updateNewBeeMallGoods(NewBeeMallGoods goods) {
//        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
//        // 分类不存在或者不是三级分类，则该参数字段异常
//        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
//            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
//        }
//        NewBeeMallGoods temp = goodsMapper.selectByPrimaryKey(goods.getGoodsId());
//        if (temp == null) {
//            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
//        }
//        NewBeeMallGoods temp2 = goodsMapper.selectByCategoryIdAndName(goods.getGoodsName(), goods.getGoodsCategoryId());
//        if (temp2 != null && !temp2.getGoodsId().equals(goods.getGoodsId())) {
//            //name和分类id相同且不同id 不能继续修改
//            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
//        }
//        goods.setGoodsName(NewBeeMallUtils.cleanString(goods.getGoodsName()));
//        goods.setGoodsIntro(NewBeeMallUtils.cleanString(goods.getGoodsIntro()));
//        goods.setTag(NewBeeMallUtils.cleanString(goods.getTag()));
//        goods.setUpdateTime(new Date());
//        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
//            return ServiceResultEnum.SUCCESS.getResult();
//        }
//        return ServiceResultEnum.DB_ERROR.getResult();
//    }
//
//    @Override
//    public NewBeeMallGoods getNewBeeMallGoodsById(Long id) {
//        NewBeeMallGoods newBeeMallGoods = goodsMapper.selectByPrimaryKey(id);
//        if (newBeeMallGoods == null) {
//            NewBeeMallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
//        }
//        return newBeeMallGoods;
//    }

//    @Override
//    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
//        return goodsMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
//    }
//
//    @Override
//    public PageResult searchNewBeeMallGoods(PageQueryUtil pageUtil) {
//        List<NewBeeMallGoods> goodsList = goodsMapper.findNewBeeMallGoodsListBySearch(pageUtil);
//        int total = goodsMapper.getTotalNewBeeMallGoodsBySearch(pageUtil);
//        List<NewBeeMallSearchGoodsVO> newBeeMallSearchGoodsVOS = new ArrayList<>();
//        if (!CollectionUtils.isEmpty(goodsList)) {
//            newBeeMallSearchGoodsVOS = BeanUtil.copyList(goodsList, NewBeeMallSearchGoodsVO.class);
//            for (NewBeeMallSearchGoodsVO newBeeMallSearchGoodsVO : newBeeMallSearchGoodsVOS) {
//                String goodsName = newBeeMallSearchGoodsVO.getGoodsName();
//                String goodsIntro = newBeeMallSearchGoodsVO.getGoodsIntro();
//                // 字符串过长导致文字超出的问题
//                if (goodsName.length() > 28) {
//                    goodsName = goodsName.substring(0, 28) + "...";
//                    newBeeMallSearchGoodsVO.setGoodsName(goodsName);
//                }
//                if (goodsIntro.length() > 30) {
//                    goodsIntro = goodsIntro.substring(0, 30) + "...";
//                    newBeeMallSearchGoodsVO.setGoodsIntro(goodsIntro);
//                }
//            }
//        }
//        PageResult pageResult = new PageResult(newBeeMallSearchGoodsVOS, total, pageUtil.getLimit(), pageUtil.getPage());
//        return pageResult;
//    }
}
