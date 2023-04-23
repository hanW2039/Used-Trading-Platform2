package com.wsk.dao;

import com.wsk.pojo.QueryDTO;
import com.wsk.pojo.ShopInformation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface ShopInformationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ShopInformation record);

    int insertSelective(ShopInformation record);

    ShopInformation selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ShopInformation record);

    int updateByPrimaryKey(ShopInformation record);

    List<ShopInformation> selectTen(Map map);

    List<ShopInformation> selectOffShelf(Integer uid, Integer start);

    int getCountsOffShelf(Integer uid);

    int getCounts();

    int selectIdByImage(String image);

    List<ShopInformation> selectByName(String name);

    /**
     * 商品查询
     * @param queryDTO
     * @return
     */
    List<ShopInformation> selectByQueryDTO(QueryDTO queryDTO);

    List<ShopInformation> selectByKindid(Integer Id);
    List<ShopInformation> selectByKindidT(Integer Id);
    /**
     * 选择用户的发布商品
     * @param uid
     * @return
     */
    @Select("select * from shopinformation where uid=#{uid} and display=1 order by id desc limit 18")
    List<ShopInformation> selectUserReleaseByUid(int uid);

    /**
     * 分页查询
     * @param queryDTO
     * @return
     */
    List<ShopInformation> selectByPage(QueryDTO queryDTO);

}