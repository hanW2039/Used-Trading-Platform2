package com.wsk.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Auther: Lenovo
 * @Date: 2023/04/19/23:16
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopInformationVO {
    private List<ShopInformation> shopInformationList;

    /**
     * 分类名
     */
    private String name;

    /**
     * 被链接名
     */
    private String linked;
}
