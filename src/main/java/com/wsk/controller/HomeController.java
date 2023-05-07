package com.wsk.controller;

import com.wsk.bean.ShopInformationBean;
import com.wsk.dao.ScoreRecordMapper;
import com.wsk.dao.ShopInformationMapper;
import com.wsk.pojo.*;
import com.wsk.service.*;
import com.wsk.service.Impl.OperationServiceImpl;
import com.wsk.tool.StringUtils;
import com.wsk.util.HostHolder;
import com.wsk.util.cf.CFDataModelUtil;
import com.wsk.util.cf.CFUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.model.DataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * wh
 */
@Controller
@Slf4j
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
    @Resource
    private ShopInformationMapper shopInformationMapper;
    @Autowired
    private OperationServiceImpl operationServiceImpl;
    @Autowired
    private ScoreRecordMapper scoreRecordMapper;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model){
        List<AllKinds> allKinds = allKindsService.selectAll();

        List<ShopInformationVO> shopList = new ArrayList<>();
        for(AllKinds kind : allKinds) {
            ShopInformationVO shopInformationVO = new ShopInformationVO(shopInformationService.selectByKindid(kind.getId()),
                    kind.getName(),
                    kind.getName().substring(1),
                    kind.getId().toString());
            shopList.add(shopInformationVO);
        }
        model.addAttribute("shopList", shopList);
        model.addAttribute("allKinds", allKinds);
        return "new/index";
    }

    @RequestMapping(path = "/indexMore", method = RequestMethod.GET)
    public String getIndexMorePage(Model model, String id){
        List<AllKinds> allKinds = allKindsService.selectAll();
        String name = null;
        for(AllKinds a : allKinds) {
            if(a.getId().toString().equals(id)) {
                name = a.getName();
            }
        }
        List<ShopInformation> shopInformationList = shopInformationMapper.selectByKindidT(Integer.valueOf(id));
        model.addAttribute("shopInformationList",shopInformationList);
        model.addAttribute("allKinds", allKinds);
        model.addAttribute("kind", name);
        return "new/indexmore";
    }

    @RequestMapping(path = "/store", method = RequestMethod.GET)
    public String getBookStore(Model model, Page page, @RequestParam(required = false) String name) {
        if(page.getCurrent() < 1) {
            page.setCurrent(1);
        }
        if(page.getCurrent() > page.getTotal()) {
            page.setCurrent(page.getTotal());
        }
        List<AllKinds> allKinds = allKindsService.selectAll();
        page.setRows(shopInformationService.getCounts());
        if(name == null) {
            page.setPath("/store");
        }else {
            page.setPath("/store?name=" + name);
        }
        QueryDTO queryDTO = new QueryDTO();
        queryDTO.setLimit(page.getLimit());
        queryDTO.setOffset(page.getOffSet());
        if(name != null) {
            queryDTO.setName(name);
        }
        List<ShopInformation> list = shopInformationService.selectByPage(queryDTO);
        List<ShopInformation> recommend = null;
        if(1 == page.getCurrent() && hostHolder.getUser() != null) {
            recommend = recommend();
            if(recommend.size() != 12) {
                for(int i = 0; i < (12 - recommend.size()); i++) {
                    recommend.add(list.get(i));
                }
            }
            list = recommend;
        }

        model.addAttribute("allKinds", allKinds);
        model.addAttribute("shopList", list);
        return "new/bookStore";
    }

    /**
     * 游客展示热点推荐（根据商品被收藏数量降序推荐）
     * 登录用户同时进行
     * 基于用户的协同过滤推荐算法进行推荐（根据评分数据）
     * 和 基于项目（商品）的协同过滤推荐算法进行推荐（根据购买数据）
     */
    private List<ShopInformation> recommend(){
//        System.out.println("***热点推荐开始***");
//        List<Item> hotItems = itemService.findHot(null);
//        request.setAttribute("hotItems", hotItems);
//        System.out.println("***热点推荐结束***");
        //获取当前登录用户对象
        // 定义个性化推荐结果集合
        List<ShopInformation> recommendItems = new ArrayList();
        UserInformation cUser = hostHolder.getUser();
        if(cUser!=null){
            //定义个性化推荐结果
            String cfItemIdsFinal = "";
            log.info("***基于用户的协同过滤推荐算法开始***");
            //用户-商品评分记录矩阵工具类
            CFDataModelUtil cfDataModelUtil = new CFDataModelUtil();
            //实例化协同过滤推荐算法工具类对象
            CFUtil cfUtil = new CFUtil();
            System.out.println("查询所有评分记录");
            //查询所有评分记录
            List<ScoreRecord> scorerecordList = scoreRecordMapper.selectScoreRecords(new ScoreRecord());
            //获取用户-商品评分矩阵
            DataModel model = cfDataModelUtil.getItemScoreDadaModel(scorerecordList, cUser);
            log.info("用户评分矩阵{}", model);
            //调用基于用户的协同过滤推荐算法方法
            String cfItemIds = cfUtil.cfByScoreBaseUser(cUser, model);
            //判断是否存在推荐结果
            if(cfItemIds!=null && !cfItemIds.equals("")){
                cfItemIdsFinal = cfItemIds;
            }
            System.out.println("***基于用户的协同过滤推荐算法结束***");
//            System.out.println("***基于项目（商品）的协同过滤推荐算法开始***");
//            System.out.println("查询所有订单详情记录");
//            //查询所有订单详情记录
//            List<Orderitem> orderitemList = orderitemService.find(null);
//            //调用基于项目（商品）的协同过滤推荐算法方法
//            String cfItemIds2 = cfUtil.cfByOrderitemBaseUser(cUser, orderitemList);
//            //判断是否存在推荐结果
//            if(cfItemIds2!=null && !cfItemIds2.equals("")){
//                if(cfItemIdsFinal!=null && !cfItemIdsFinal.equals("")){
//                    cfItemIdsFinal+=","+cfItemIds2;
//                }else{
//                    cfItemIdsFinal = cfItemIds2;
//                }
//            }
//            System.out.println("***基于项目（商品）的协同过滤推荐算法结束***");
            //将两次推荐结果查询出来
            if(cfItemIdsFinal!=null && !cfItemIdsFinal.equals("")){
                String[] ids = cfItemIdsFinal.split(",");
                for(int i = 0; i < ids.length; i++) {
                    recommendItems.add(shopInformationService.selectByPrimaryKey(Integer.valueOf(ids[i])));
                }
            }
        }
        return recommendItems;
    }

    @RequestMapping(path = "/detail", method = RequestMethod.GET)
    public String getBookDetail(Model model, String id) {
        if (null != hostHolder.getUser()) {
            // 记录操作数据
            Operation operation = new Operation();
            operation.setType("1");
            operation.setUid(hostHolder.getUser().getId());
            operation.setSid(Integer.valueOf(id));
            operationServiceImpl.addOperationRecord(operation);
        }
        ShopInformation shopInformation = shopInformationService.selectByPrimaryKey(Integer.valueOf(id));
        UserInformation userInformation = userInformationService.selectByPrimaryKey(shopInformation.getUid());
        model.addAttribute("userInformation",userInformation);
        model.addAttribute("shopInformation", shopInformation);
        return "new/bookDetail";
    }
    @RequestMapping(path = "/askBook", method = RequestMethod.GET)
    public String getAskBook(Model model, Page page) {
        List<UserWant> userWants = userWantService.selectAll();
        model.addAttribute("userWants", userWants);
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
    public String getEditBook(Model model, Integer id) {
        if(id != null) {
            model.addAttribute("id",id);
        }
        return "new/editBook";
    }

    @RequestMapping(path = "/editAskBook", method = RequestMethod.GET)
    public String getEditAskBook(Model model, Integer id) {
        if(id != null) {
            model.addAttribute("id",id);
        }
        return "new/editAskBook";
    }

    @RequestMapping(path = "/sellUpload", method = RequestMethod.GET)
    public String getSellUpload(Model model) {
        return "new/sellUpload";
    }

    @RequestMapping(path = "/askUpload", method = RequestMethod.GET)
    public String getAskUpload(Model model) {
        return "new/askUpload";
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

    @RequestMapping("/getEditUser")
    public String editUserInformation(Model model){
        model.addAttribute("user", hostHolder.getUser());
        return "new/updateuserinformation";
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
