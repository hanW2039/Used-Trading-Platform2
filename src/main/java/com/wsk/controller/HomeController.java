package com.wsk.controller;

import com.wsk.bean.ShopInformationBean;
import com.wsk.pojo.*;
import com.wsk.service.*;
import com.wsk.tool.StringUtils;
import com.wsk.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * wh
 */
@Controller
public class HomeController {
    @Resource
    private ShopInformationService shopInformationService;
    @Resource
    private SpecificeService specificeService;
    @Resource
    private ClassificationService classificationService;
    @Resource
    private AllKindsService allKindsService;
    @Resource
    private ShopContextService shopContextService;
    @Resource
    private UserInformationService userInformationService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserWantService userWantService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model){
        List<AllKinds> allKinds = allKindsService.selectAll();

        List<ShopInformationVO> shopList = new ArrayList<>();
        for(AllKinds kind : allKinds) {
            ShopInformationVO shopInformationVO = new ShopInformationVO(shopInformationService.selectByKindid(kind.getId()),
                    kind.getName(),
                    kind.getName().substring(1));
            shopList.add(shopInformationVO);
        }
        model.addAttribute("shopList", shopList);
        model.addAttribute("allKinds", allKinds);
        return "new/index";
    }

    @RequestMapping(path = "/store", method = RequestMethod.GET)
    public String getBookStore(Model model, Page page) {
        List<AllKinds> allKinds = allKindsService.selectAll();
        page.setRows(shopInformationService.getCounts());
        page.setPath("/store");
        QueryDTO queryDTO = new QueryDTO();
        queryDTO.setLimit(page.getLimit());
        queryDTO.setOffset(page.getOffSet());
        List<ShopInformation> list = shopInformationService.selectByPage(queryDTO);
        model.addAttribute("allKinds", allKinds);
        model.addAttribute("shopList", list);
        return "new/bookStore";
    }

    @RequestMapping(path = "/detail/{id}", method = RequestMethod.GET)
    public String getBookDetail(Model model, @PathVariable("id") String id) {
        ShopInformation shopInformation = shopInformationService.selectByPrimaryKey(Integer.getInteger(id));
        model.addAttribute("shopInformation", shopInformation);
        return "new/bookDetail";
    }
    @RequestMapping(path = "/askBook", method = RequestMethod.GET)
    public String getAskBook(Model model) {

        return "new/askBook";
    }

    @RequestMapping(path = "/myBookSelf", method = RequestMethod.GET)
    public String getMyBookSelf(Model model) {
        UserInformation user = hostHolder.getUser();
        List<ShopInformation> shopInformationList = shopInformationService.selectUserReleaseByUid(user.getId());
        List<UserWant> userWants = userWantService.selectByUid(user.getId());
        model.addAttribute("userWantList", userWants);
        model.addAttribute("shopInformationList", shopInformationList);
        model.addAttribute("user", user);
        return "new/myBookself";
    }

    @RequestMapping(path = "/editBook", method = RequestMethod.GET)
    public String getEditBook(Model model) {
        return "new/editBook";
    }

    @RequestMapping(path = "/sellUpload", method = RequestMethod.GET)
    public String getSellUpload(Model model) {
        return "new/sellUpload";
    }

    //通过分类的第三层id获取全名
    private String getSortName(int sort) {
        StringBuilder stringBuffer = new StringBuilder();
        Specific specific = selectSpecificBySort(sort);
        int cid = specific.getCid();
        Classification classification = selectClassificationByCid(cid);
        int aid = classification.getAid();
        AllKinds allKinds = selectAllKindsByAid(aid);
        stringBuffer.append(allKinds.getName());
        stringBuffer.append("-");
        stringBuffer.append(classification.getName());
        stringBuffer.append("-");
        stringBuffer.append(specific.getName());
//        System.out.println(sort);
        return stringBuffer.toString();
    }

    //获得分类中的第一层
    @RequestMapping(value = "/getAllKinds.do")
    @ResponseBody
    public List<AllKinds> getAllKind() {
        return getAllKinds();
    }

    //获得分类中的第二层，通过第一层的id
    @RequestMapping(value = "/getClassification.do", method = RequestMethod.POST)
    @ResponseBody
    public List<Classification> getClassificationByAid(@RequestParam int id) {
        return selectAllClassification(id);
    }

    //通过第二层的id获取对应的第三层
    @RequestMapping(value = "/getSpecific.do")
    @ResponseBody
    public List<Specific> getSpecificByCid(@RequestParam int id) {
        return selectAllSpecific(id);
    }

    //get the shops counts
    @RequestMapping(value = "/getShopsCounts.do")
    @ResponseBody
    public Map getShopsCounts() {
        Map<String, Integer> map = new HashMap<>();
        int counts = 0;
        try {
            counts = shopInformationService.getCounts();
        } catch (Exception e) {
            e.printStackTrace();
            map.put("counts", counts);
            return map;
        }
        map.put("counts", counts);
        return map;
    }

    @RequestMapping(value = "/getShops.do")
    @ResponseBody
    public List getShops(@RequestParam int start) {
        List<ShopInformation> list = new ArrayList<>();
        try {
            int end = 12;
            list = selectTen(start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
        return list;
    }


    //获取商品，分页,一次性获取end个
    private List<ShopInformation> selectTen(int start, int end) {
        Map map = new HashMap();
        map.put("start", (start - 1) * end);
        map.put("end", end);
        List<ShopInformation> list = shopInformationService.selectTen(map);
        return list;
    }

    //获取最详细的分类，第三层
    private Specific selectSpecificBySort(int sort) {
        return specificeService.selectByPrimaryKey(sort);
    }

    //获得第二层分类
    private Classification selectClassificationByCid(int cid) {
        return classificationService.selectByPrimaryKey(cid);
    }

    //获得第一层分类
    private AllKinds selectAllKindsByAid(int aid) {
        return allKindsService.selectByPrimaryKey(aid);
    }

    //获得第一层所有
    private List<AllKinds> getAllKinds() {
        return allKindsService.selectAll();
    }

    //根据第一层的id获取该层下的第二层
    private List<Classification> selectAllClassification(int aid) {
        return classificationService.selectByAid(aid);
    }

    //根据第二层的id获取其对应的第三层所有id
    private List<Specific> selectAllSpecific(int cid) {
        return specificeService.selectByCid(cid);
    }

    //获得商品总页数
    private int getShopCounts() {
        return shopInformationService.getCounts();
    }

    //获得商品留言总页数
    private int getShopContextCounts(int sid) {
        return shopContextService.getCounts(sid);
    }

    //获得商品留言，10条
    private List<ShopContext> selectShopContextBySid(int sid, int start) {
        return shopContextService.findById(sid, (start - 1) * 10);
    }
}
