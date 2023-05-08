package com.wsk.controller;

import cn.hutool.core.io.FileUtil;
import com.wsk.bean.GoodsCarBean;
import com.wsk.bean.ShopInformationBean;
import com.wsk.bean.UserWantBean;
import com.wsk.pojo.*;
import com.wsk.response.BaseResponse;
import com.wsk.service.*;
import com.wsk.token.TokenProccessor;
import com.wsk.tool.SaveSession;
import com.wsk.tool.StringUtils;
import com.wsk.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/*import com.wsk.tool.OCR;
import com.wsk.tool.Pornographic;*/

/**
 * Created by wsk1103 on 2017/5/9.
 */
@Controller
@Slf4j
public class UserController {

    @Resource
    private UserInformationService userInformationService;
    @Resource
    private UserPasswordService userPasswordService;
    @Resource
    private UserCollectionService userCollectionService;
    @Resource
    private UserReleaseService userReleaseService;
    @Resource
    private BoughtShopService boughtShopService;
    @Resource
    private UserWantService userWantService;
    @Resource
    private ShopCarService shopCarService;
    @Resource
    private OrderFormService orderFormService;
    @Resource
    private GoodsOfOrderFormService goodsOfOrderFormService;
    @Resource
    private UserStateService userStateService;
    @Resource
    private ShopInformationService shopInformationService;
    @Resource
    private GoodsCarService goodsCarService;
    @Resource
    private SpecificeService specificeService;
    @Resource
    private ClassificationService classificationService;
    @Resource
    private AllKindsService allKindsService;
    @Resource
    private ShopContextService shopContextService;
    @Resource
    private HostHolder hostHolder;

    @RequestMapping(path = "/updateUserInformation")
    public String updateUser(UserInformation userInformation) {
        userInformation.setId(hostHolder.getUser().getId());
        userInformationService.updateByPrimaryKeySelective(userInformation);
//        return "new/myBookSelf";
        return "redirect:/myBookSelf";
    }

    //check the shopping cart,查看购物车
    @RequestMapping(value = "/shopping_cart")
    public String selectShopCar(Model model) {
        UserInformation userInformation = hostHolder.getUser();
        model.addAttribute("userInformation", userInformation);
        int uid = userInformation.getId();
        List<GoodsCar> goodsCars = goodsCarService.selectByUid(uid);
        List<GoodsCarBean> goodsCarBeans = new ArrayList<>();
        for (GoodsCar goodsCar : goodsCars) {
            GoodsCarBean goodsCarBean = new GoodsCarBean();
            goodsCarBean.setUid(goodsCar.getUid());
            goodsCarBean.setSid(goodsCar.getSid());
            goodsCarBean.setModified(goodsCar.getModified());
            goodsCarBean.setId(goodsCar.getId());
            goodsCarBean.setQuantity(goodsCar.getQuantity());
            ShopInformation shopInformation = shopInformationService.selectByPrimaryKey(goodsCar.getSid());
            goodsCarBean.setName(shopInformation.getName());
            goodsCarBean.setRemark(shopInformation.getRemark());
            goodsCarBean.setImage(shopInformation.getImage());
            goodsCarBean.setPrice(shopInformation.getPrice().doubleValue());
//            goodsCarBean.setSort(getSort(shopInformation.getSort()));
            goodsCarBeans.add(goodsCarBean);
        }
        model.addAttribute("list", goodsCarBeans);
        return "new/car";
    }


    //添加到购物车
    @RequestMapping(value = "/addCar")
    public String insertGoodsCar(Integer id) {
        UserInformation userInformation = hostHolder.getUser();
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "new/login";
        }
        int uid = userInformation.getId();
        GoodsCar goodsCar = new GoodsCar();
        goodsCar.setDisplay(1);
        goodsCar.setModified(new Date());
        goodsCar.setQuantity(1);
        goodsCar.setUid(uid);
        goodsCar.setSid(id);
        goodsCarService.insertSelective(goodsCar);
        return "redirect:/store";
    }


    //删除购物车的商品
    @RequestMapping(value = "/deleteCar")
    public String deleteCar(@RequestParam int id, @RequestParam int sid) {
        UserInformation userInformation = hostHolder.getUser();
        int uid = userInformation.getId();
        GoodsCar goodsCar = new GoodsCar();
        goodsCar.setDisplay(0);
        goodsCar.setId(id);
        goodsCar.setSid(sid);
        goodsCar.setUid(uid);
        goodsCarService.updateByPrimaryKeySelective(goodsCar);
        return "redirect:/shopping_cart";
    }

    //发布商品
    @RequestMapping(value = "/insertGoods.do", method = RequestMethod.POST)
    public String insertGoods(@RequestParam String name, @RequestParam int level,
                              @RequestParam String remark, @RequestParam double price,
                              @RequestParam int sort, @RequestParam int quantity,
                              @RequestParam String token, @RequestParam(required = false) MultipartFile image,
                              @RequestParam int action, @RequestParam(required = false) int id,
                              HttpServletRequest request, Model model) {
        String goodsToken = (String) request.getSession().getAttribute("goodsToken");
//        String publishProductToken = TokenProccessor.getInstance().makeToken();
//        request.getSession().setAttribute("token",publishProductToken);
        //防止重复提交
        if (StringUtils.getInstance().isNullOrEmpty(goodsToken) || !goodsToken.equals(token)) {
            return "redirect:publish_product.do?error=1";
        } else {
            request.getSession().removeAttribute("goodsToken");
        }
//        //从session中获得用户的基本信息
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        model.addAttribute("userInformation", userInformation);
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            //如果用户不存在，
            return "redirect:/login.do";
        }
        name = StringUtils.getInstance().replaceBlank(name);
        remark = StringUtils.getInstance().replaceBlank(remark);
        //judge the data`s format
        if (StringUtils.getInstance().isNullOrEmpty(name) || StringUtils.getInstance().isNullOrEmpty(level) || StringUtils.getInstance().isNullOrEmpty(remark) || StringUtils.getInstance().isNullOrEmpty(price)
                || StringUtils.getInstance().isNullOrEmpty(sort) || StringUtils.getInstance().isNullOrEmpty(quantity) || name.length() > 25 || remark.length() > 122) {
            model.addAttribute("message", "请输入正确的格式!!!!!");
            model.addAttribute("token", goodsToken);
            request.getSession().setAttribute("goodsToken", goodsToken);
            return "page/publish_product";
        }
        //插入
        if (action == 1) {
            if (StringUtils.getInstance().isNullOrEmpty(image)) {
                model.addAttribute("message", "请选择图片!!!");
                model.addAttribute("token", goodsToken);
                request.getSession().setAttribute("goodsToken", goodsToken);
                return "redirect:publish_product.do?error=请插入图片";
            }
            String random;
            String path = "D:\\", save;
            random = "toImage\\" + StringUtils.getInstance().getRandomChar() + System.currentTimeMillis() + ".jpg";
            StringBuilder thumbnails = new StringBuilder();
            thumbnails.append(path);
            thumbnails.append("toImage/thumbnails/");
            StringBuilder wsk = new StringBuilder();
            wsk.append(StringUtils.getInstance().getRandomChar()).append(System.currentTimeMillis()).append(".jpg");
            thumbnails.append(wsk);
//        String fileName = "\\" + random + ".jpg";
            File file = new File(path, random);
            if (!file.exists()) {
                FileUtil.mkdir(file);
//                file.mkdir();
            }
            try {
                image.transferTo(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
/*            String pornograp = Pornographic.CheckPornograp("D:\\" + random);
            if (pornograp.equals("色情图片")) {
                return "redirect:publish_product?error=不能使用色情图片";
            }
            if (!OCR.isOk2(pornograp)) {
                return "redirect:publish_product?error=图片不能含有敏感文字";
            }*/
            //创建缩略图文件夹
//            File thumbnailsFile = new File(thumbnails.toString());
//            if (!thumbnailsFile.exists()) {
//                FileUtil.mkdir(thumbnailsFile);
////                thumbnailsFile.mkdir();
//            }
            if (StringUtils.getInstance().thumbnails(path + random, thumbnails.toString())) {
                save = "/toImage/thumbnails/" + wsk;
            } else {
                return "redirect:publish_product.do?error=生成缩略图失败";
            }
            //begin insert the shopInformation to the MySQL
            ShopInformation shopInformation = new ShopInformation();
            shopInformation.setName(name);
            shopInformation.setLevel(level);
            shopInformation.setRemark(remark);
            shopInformation.setPrice(new BigDecimal(price));
            shopInformation.setSort(sort);
            shopInformation.setQuantity(quantity);
            shopInformation.setModified(new Date());
            shopInformation.setImage(random);//This is the other uniquely identifies
            shopInformation.setThumbnails(save);
//        shopInformation.setUid(4);
            int uid = (int) request.getSession().getAttribute("uid");
            shopInformation.setUid(uid);
            try {
                int result = shopInformationService.insertSelective(shopInformation);
                //插入失败？？？
                if (result != 1) {
                    model.addAttribute("message", "请输入正确的格式!!!!!");
                    model.addAttribute("token", goodsToken);
                    request.getSession().setAttribute("goodsToken", goodsToken);
                    return "page/publish_product";
                }
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("token", goodsToken);
                model.addAttribute("message", "请输入正确的格式!!!!!");
                request.getSession().setAttribute("goodsToken", goodsToken);
                return "page/publish_product";
            }
            int sid = shopInformationService.selectIdByImage(random);// get the id which is belongs shopInformation
            //将发布的商品的编号插入到用户的发布中
            UserRelease userRelease = new UserRelease();
            userRelease.setModified(new Date());
            userRelease.setSid(sid);
            userRelease.setUid(uid);
            try {
                int result = userReleaseService.insertSelective(userRelease);
                //如果关联失败，删除对应的商品和商品图片
                if (result != 1) {
                    //if insert failure,transaction rollback.
                    shopInformationService.deleteByPrimaryKey(sid);
//                shopPictureService.deleteByPrimaryKey(spid);
                    model.addAttribute("token", goodsToken);
                    model.addAttribute("message", "请输入正确的格式!!!!!");
                    request.getSession().setAttribute("goodsToken", goodsToken);
                    return "page/publish_product";
                }
            } catch (Exception e) {
                //if insert failure,transaction rollback.
                shopInformationService.deleteByPrimaryKey(sid);
                e.printStackTrace();
                model.addAttribute("token", goodsToken);
                model.addAttribute("message", "请输入正确的格式!!!!!");
                request.getSession().setAttribute("goodsToken", goodsToken);
                return "page/publish_product";
            }
            shopInformation.setId(sid);
            goodsToken = TokenProccessor.getInstance().makeToken();
            request.getSession().setAttribute("goodsToken", goodsToken);
            model.addAttribute("token", goodsToken);
            model.addAttribute("shopInformation", shopInformation);
            model.addAttribute("userInformation", userInformation);
            String sb = getSort(sort);
            model.addAttribute("sort", sb);
            model.addAttribute("action", 2);
            return "redirect:/my_publish_product_page.do";
        } else if (action == 2) {//更新商品
            ShopInformation shopInformation = new ShopInformation();
            shopInformation.setModified(new Date());
            shopInformation.setQuantity(quantity);
            shopInformation.setSort(sort);
            shopInformation.setPrice(new BigDecimal(price));
            shopInformation.setRemark(remark);
            shopInformation.setLevel(level);
            shopInformation.setName(name);
            shopInformation.setId(id);
            try {
                int result = shopInformationService.updateByPrimaryKeySelective(shopInformation);
                if (result != 1) {
                    return "redirect:publish_product.do";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:publish_product.do";
            }
            goodsToken = TokenProccessor.getInstance().makeToken();
            request.getSession().setAttribute("goodsToken", goodsToken);
            model.addAttribute("token", goodsToken);
            shopInformation = shopInformationService.selectByPrimaryKey(id);
            model.addAttribute("userInformation", userInformation);
            model.addAttribute("shopInformation", shopInformation);
            model.addAttribute("action", 2);
            model.addAttribute("sort", getSort(sort));
        }
        return "redirect:/my_publish_product_page.do";
    }

    //从发布的商品直接跳转到修改商品
    @RequestMapping(value = "/modifiedMyPublishProduct.do")
    public String modifiedMyPublishProduct(HttpServletRequest request, Model model,
                                           @RequestParam int id) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        }
        String goodsToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("goodsToken", goodsToken);
        model.addAttribute("token", goodsToken);
        ShopInformation shopInformation = shopInformationService.selectByPrimaryKey(id);
        model.addAttribute("userInformation", userInformation);
        model.addAttribute("shopInformation", shopInformation);
        model.addAttribute("action", 2);
        model.addAttribute("sort", getSort(shopInformation.getSort()));
        return "page/publish_product";
    }

    //发表留言
    @RequestMapping(value = "/insertShopContext.do")
    @ResponseBody
    public Map insertShopContext(@RequestParam int id, @RequestParam String context, @RequestParam String token,
                                 HttpServletRequest request) {
        String goodsToken = (String) request.getSession().getAttribute("goodsToken");
        Map<String, String> map = new HashMap<>();
        map.put("result", "1");
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            map.put("result", "2");
            return map;
        }
        if (StringUtils.getInstance().isNullOrEmpty(goodsToken) || !token.equals(goodsToken)) {
            return map;
        }
        ShopContext shopContext = new ShopContext();
        shopContext.setContext(context);
        Date date = new Date();
        shopContext.setModified(date);
        shopContext.setSid(id);
        int uid = (int) request.getSession().getAttribute("uid");
        shopContext.setUid(uid);
        try {
            int result = shopContextService.insertSelective(shopContext);
            if (result != 1) {
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return map;
        }
        map.put("result", "1");
        map.put("username", userInformation.getUsername());
        map.put("context", context);
        map.put("time", StringUtils.getInstance().DateToString(date));
        return map;
    }

    //下架商品
    @RequestMapping(value = "/deleteShop.do")
    public String deleteShop(HttpServletRequest request, Model model, @RequestParam int id) {
//        Map<String, Integer> map = new HashMap<>();
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        } else {
            model.addAttribute("userInformation", userInformation);
        }
        ShopInformation shopInformation = new ShopInformation();
        shopInformation.setModified(new Date());
        shopInformation.setDisplay(0);
        shopInformation.setId(id);
        try {
            int result = shopInformationService.updateByPrimaryKeySelective(shopInformation);
            if (result != 1) {
                return "redirect:my_publish_product_page.do";
            }
            return "redirect:my_publish_product_page.do";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:my_publish_product_page.do";
        }
    }

    //查看发布的所有商品总数
    @RequestMapping(value = "/getReleaseShopCounts.do")
    @ResponseBody
    public Map getReleaseShopCounts(HttpServletRequest request) {
        Map<String, Integer> map = new HashMap<>();
        if (StringUtils.getInstance().isNullOrEmpty(request.getSession().getAttribute("userInformation"))) {
            map.put("counts", -1);
            return map;
        }
        int counts = getReleaseCounts((Integer) request.getSession().getAttribute("uid"));
        map.put("counts", counts);
        return map;
    }

    //查看我的发布的商品
    @RequestMapping(value = "/my_publish_product_page.do")
    public String getReleaseShop(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/login.do";
        } else {
            model.addAttribute("userInformation", userInformation);
        }
        int uid = (int) request.getSession().getAttribute("uid");
        List<ShopInformation> shopInformations = shopInformationService.selectUserReleaseByUid(uid);
        List<ShopInformationBean> list = new ArrayList<>();
        String stringBuffer;
//            int i=0;
        for (ShopInformation shopInformation : shopInformations) {
//                if (i>=5){
//                    break;
//                }
//                i++;
            stringBuffer = getSort(shopInformation.getSort());
            ShopInformationBean shopInformationBean = new ShopInformationBean();
            shopInformationBean.setId(shopInformation.getId());
            shopInformationBean.setName(shopInformation.getName());
            shopInformationBean.setLevel(shopInformation.getLevel());
            shopInformationBean.setPrice(shopInformation.getPrice().doubleValue());
            shopInformationBean.setRemark(shopInformation.getRemark());
            shopInformationBean.setSort(stringBuffer);
            shopInformationBean.setQuantity(shopInformation.getQuantity());
            shopInformationBean.setTransaction(shopInformation.getTransaction());
            shopInformationBean.setUid(shopInformation.getUid());
            shopInformationBean.setImage(shopInformation.getImage());
            list.add(shopInformationBean);
        }
        model.addAttribute("shopInformationBean", list);
        return "page/personal/my_publish_product_page";
    }

    //更新商品信息


    private String getSort(int sort) {
        StringBuilder sb = new StringBuilder();
        Specific specific = selectSpecificBySort(sort);
        int cid = specific.getCid();
        Classification classification = selectClassificationByCid(cid);
        int aid = classification.getAid();
        AllKinds allKinds = selectAllKindsByAid(aid);
        sb.append(allKinds.getName());
        sb.append("-");
        sb.append(classification.getName());
        sb.append("-");
        sb.append(specific.getName());
        return sb.toString();
    }

    //查看用户收藏的货物的总数
    private int getCollectionCounts(int uid) {
        int counts;
        try {
            counts = userCollectionService.getCounts(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return counts;
    }

    //查看收藏，一次10个
    private List<UserCollection> selectContectionByUid(int uid, int start) {
        try {
            return userCollectionService.selectByUid(uid, (start - 1) * 10);
        } catch (Exception e) {
            e.printStackTrace();
            List<UserCollection> list = new ArrayList<>();
            list.add(new UserCollection());
            return list;
        }
    }

    //查看用户发布的货物的总数
    private int getReleaseCounts(int uid) {
        try {
            return userReleaseService.getCounts(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //查看发布的货物，一次10个
    private List<UserRelease> selectReleaseByUid(int uid, int start) {
        try {
            return userReleaseService.selectByUid(uid, (start - 1) * 10);
        } catch (Exception e) {
            e.printStackTrace();
            List<UserRelease> list = new ArrayList<>();
            list.add(new UserRelease());
            return list;
        }
    }

    //查看用户购买到的物品的总数
    private int getBoughtShopCounts(int uid) {
        try {
            return boughtShopService.getCounts(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //查看用户的购买，10个
    private List<BoughtShop> selectBoughtShopByUid(int uid, int start) {
        try {
            return boughtShopService.selectByUid(uid, (start - 1) * 10);
        } catch (Exception e) {
            e.printStackTrace();
            List<BoughtShop> list = new ArrayList<>();
            list.add(new BoughtShop());
            return list;
        }
    }

    //查看用户的求购总个数
    private int getUserWantCounts(int uid) {
        try {
            return userWantService.getCounts(uid);
        } catch (Exception e) {
            return -1;
        }
    }

    //求购列表10
    private List<UserWant> selectUserWantByUid(int uid) {
        try {
            return userWantService.selectMineByUid(uid);
        } catch (Exception e) {
            e.printStackTrace();
            List<UserWant> list = new ArrayList<>();
            list.add(new UserWant());
            return list;
        }
    }

    //我的购物车总数
    private int getShopCarCounts(int uid) {
        try {
            return shopCarService.getCounts(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //购物车列表  10
    private ShopCar selectShopCarByUid(int uid) {
        try {
            return shopCarService.selectByUid(uid);
        } catch (Exception e) {
            e.printStackTrace();
//            List<ShopCar> list
            return new ShopCar();
        }
    }

    //查看订单总数
    private int getOrderFormCounts(int uid) {
        try {
            return orderFormService.getCounts(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //订单列表 10个
    private List<OrderForm> selectOrderFormByUid(int uid, int start) {
        try {
            return orderFormService.selectByUid(uid, (start - 1) * 10);
        } catch (Exception e) {
            e.printStackTrace();
            List<OrderForm> list = new ArrayList<>();
            list.add(new OrderForm());
            return list;
        }
    }

    //订单中的商品
    private List<GoodsOfOrderForm> selectGoodsOfOrderFormByOFid(int ofid) {
        try {
            return goodsOfOrderFormService.selectByOFid(ofid);
        } catch (Exception e) {
            e.printStackTrace();
            List<GoodsOfOrderForm> list = new ArrayList<>();
            list.add(new GoodsOfOrderForm());
            return list;
        }
    }

    //查看用户的状态
    private UserState selectUserStateByUid(int uid) {
        try {
            return userStateService.selectByUid(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return new UserState();
        }
    }

    //判断该手机号码及其密码是否一一对应
    private boolean getId(String phone, String password, HttpServletRequest request) {
        int uid = userInformationService.selectIdByPhone(phone);
        if (uid == 0 || StringUtils.getInstance().isNullOrEmpty(uid)) {
            return false;
        }
        UserInformation userInformation = userInformationService.selectByPrimaryKey(uid);
        if (null == userInformation) {
            return false;
        }
        password = StringUtils.getInstance().getMD5(password);
        String password2 = userPasswordService.selectByUid(userInformation.getId()).getPassword();
        if (!password.equals(password2)) {
            return false;
        }
        //如果密码账号对应正确，将userInformation存储到session中
        request.getSession().setAttribute("userInformation", userInformation);
        request.getSession().setAttribute("uid", uid);
        SaveSession.getInstance().save(phone, System.currentTimeMillis());
        return true;
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

    public void save(ShopInformation shopInformation, UserRelease userRelease) {
        shopInformationService.insertSelective(shopInformation);
        userReleaseService.insertSelective(userRelease);
    }

    //循环插入商品
    //发布商品
    @RequestMapping(value = "/test")
    public String insertGoods() {

        Random random = new Random();
        ShopInformation shopInformation;
        UserRelease userRelease;
        int level, uid, quantity;
        double price;
        for (int i = 1, k = 1, j = 189; i < 1000; i++, j++, k++) {
            if (k > 94) {
                k = 1;
            }
            level = random.nextInt(10) + 1;
            price = Math.random() * 1000 + 1;
            quantity = random.nextInt(10) + 1;
            uid = random.nextInt(100) + 1;
            shopInformation = new ShopInformation();
            shopInformation.setId(j);
            shopInformation.setName("百年孤独");
            shopInformation.setModified(new Date());
            shopInformation.setLevel(level);
            shopInformation.setRemark("看上的请联系我，QQ：test，微信：test");
//            double price = Math.random()*1000.00+1;
            shopInformation.setPrice(new BigDecimal(price));
            shopInformation.setSort(k);
            shopInformation.setQuantity(quantity);
            shopInformation.setImage("/image/QyBHYiMfYQ4XZFCqxEv0.jpg");
//            int uid = random.nextInt(100)+1;
            shopInformation.setUid(uid);
//            userRelease = new UserRelease();
//            userRelease.setUid(uid);
//            userRelease.setSid(j);
//            userRelease.setModified(new Date());
//            userRelease.setDisplay(1);
            shopInformationService.updateByPrimaryKeySelective(shopInformation);
//            userReleaseService.insertSelective(userRelease);
        }
        System.out.println("success");
        return "page/publish_product";
    }
}
