package com.wsk.controller;

import com.wsk.bean.ShopContextBean;
import com.wsk.bean.ShopInformationBean;
import com.wsk.bean.UserWantBean;
import com.wsk.pojo.*;
import com.wsk.service.*;
import com.wsk.token.TokenProccessor;
import com.wsk.tool.StringUtils;
import com.wsk.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wsk1103 on 2017/5/14.
 */
@Controller
@Slf4j
public class GoodsController {
    @Resource
    private ShopInformationService shopInformationService;
    @Resource
    private ShopContextService shopContextService;
    @Resource
    private UserInformationService userInformationService;
    @Resource
    private SpecificeService specificeService;
    @Resource
    private ClassificationService classificationService;
    @Resource
    private AllKindsService allKindsService;
    @Resource
    private UserWantService userWantService;

    @Autowired
    private HostHolder hostHolder;
    private String uploadPath = "E:\\Graduation Project\\Used-Trading-Platform2\\src\\main\\resources\\mystatic\\images\\";


    @RequestMapping("/sellUpload")
    public String sellUpload(Model model, String name, String kind, String price, String modified,
                             String level, String remark,
                             @RequestParam(value = "image", required = false) MultipartFile image) {
        Map<String, Integer> map = new HashMap<>();
        map.put("数码科技", 1);
        map.put("影音家电", 2);
        map.put("鞋服配饰", 3);
        map.put("运动代步", 4);
        map.put("书籍文具", 5);
        map.put("其他", 6);


        // 获取图片原始文件名
        String originalFilename = image.getOriginalFilename();
        // 文件名使用当前时间
        String imageName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        // 获取上传图片的扩展名(jpg/png/...)
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        // 图片上传的相对路径（因为相对路径放到页面上就可以显示图片）
        String path = imageName + extension;
        // 图片上传的绝对路径
        String url = uploadPath + path;
        File dir = new File(url);
            if (!dir.exists()) {
                dir.mkdirs();
        }

        try{
            // 将图片上传到本地
            image.transferTo(new File(url));
            ShopInformation shopInformation = new ShopInformation();
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM");
            Date date = ft.parse(modified);
            shopInformation.setModified(date);
            shopInformation.setPrice(BigDecimal.valueOf(Long.parseLong(price)));
            shopInformation.setRemark(remark);
            shopInformation.setName(name);
            shopInformation.setKindid(map.get(kind));
            shopInformation.setLevel(Integer.getInteger(level));
            shopInformation.setUid(hostHolder.getUser().getId());
            String headerUrl = "/image/" + path;
            shopInformation.setImage(headerUrl);
            shopInformationService.insertSelective(shopInformation);
        } catch(Exception e) {
            log.info("写入失败");
        }


        return "redirect:/myBookSelf";
    }

    @RequestMapping("/askUpload")
    public String askUpload(Model model, String name, String kind, String modified,
                            String remark,
                             @RequestParam(value = "image", required = false) MultipartFile image) {
        Map<String, Integer> map = new HashMap<>();
        map.put("数码科技", 1);
        map.put("影音家电", 2);
        map.put("鞋服配饰", 3);
        map.put("运动代步", 4);
        map.put("书籍文具", 5);
        map.put("其他", 6);


        // 获取图片原始文件名
        String originalFilename = image.getOriginalFilename();
        // 文件名使用当前时间
        String imageName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        // 获取上传图片的扩展名(jpg/png/...)
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        // 图片上传的相对路径（因为相对路径放到页面上就可以显示图片）
        String path = imageName + extension;
        // 图片上传的绝对路径
        String url = uploadPath + path;
        File dir = new File(url);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try{
            // 将图片上传到本地
            image.transferTo(new File(url));
            UserWant userWant = new UserWant();
            userWant.setRemark(remark);
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM");
            Date date = ft.parse(modified);
            userWant.setModified(date);
            userWant.setName(name);
            userWant.setKindid(map.get(kind));
            userWant.setUid(hostHolder.getUser().getId());
            String headerUrl = "/image/" + path;
            userWant.setImage(headerUrl);

            userWantService.insertSelective(userWant);
        } catch(Exception e) {
            log.info("写入失败");
        }
        return "redirect:/myBookSelf";
    }


    @RequestMapping(path = "/update")
    public String update(Model model, String name, String kind, String price, String modified,
                             String level, String remark, String id,
                             @RequestParam(value = "image", required = false) MultipartFile image) {
        Map<String, Integer> map = new HashMap<>();
        map.put("数码科技", 1);
        map.put("影音家电", 2);
        map.put("鞋服配饰", 3);
        map.put("运动代步", 4);
        map.put("书籍文具", 5);
        map.put("其他", 6);


        String url = null;
        String path = null;
        ShopInformation shopInformation = new ShopInformation();
        if(!image.isEmpty()) {
            // 获取图片原始文件名
            String originalFilename = image.getOriginalFilename();
            // 文件名使用当前时间
            String imageName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
            // 获取上传图片的扩展名(jpg/png/...)
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            // 图片上传的相对路径（因为相对路径放到页面上就可以显示图片）
            path = imageName + extension;
            // 图片上传的绝对路径
            url = uploadPath + path;
            File dir = new File(url);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                // 将图片上传到本地
                image.transferTo(new File(url));
            }catch (Exception e) {
                log.info("写入失败");
            }
            String headerUrl = "/image/" + path;
            shopInformation.setImage(headerUrl);
        }
        try{
            if(!org.apache.commons.lang3.StringUtils.isBlank(modified)) {
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM");
                Date date = ft.parse(modified);
                shopInformation.setModified(date);
            }
            if(!org.apache.commons.lang3.StringUtils.isBlank(price)) {
                shopInformation.setPrice(BigDecimal.valueOf(Long.parseLong(price)));
            }
            if(!org.apache.commons.lang3.StringUtils.isBlank(remark)) {
                shopInformation.setRemark(remark);
            }
            if(!org.apache.commons.lang3.StringUtils.isBlank(name)) {
                shopInformation.setName(name);
            }
            if(!org.apache.commons.lang3.StringUtils.isBlank(kind)) {
                shopInformation.setKindid(map.get(kind));
            }
            if(!org.apache.commons.lang3.StringUtils.isBlank(level)) {
                shopInformation.setLevel(Integer.getInteger(level));
            }
            shopInformation.setId(Integer.valueOf(id));
            shopInformationService.updateByPrimaryKeySelective(shopInformation);
        } catch(Exception e) {
            log.info("写入失败");
        }


        return "redirect:/myBookSelf";
    }

    @RequestMapping(path = "/updateAsk")
    public String updateAsk(Model model, String name, String kind, String modified,
                         String remark, String id, String price, String quantity,
                         @RequestParam(value = "image", required = false) MultipartFile image) {
        Map<String, Integer> map = new HashMap<>();
        map.put("数码科技", 1);
        map.put("影音家电", 2);
        map.put("鞋服配饰", 3);
        map.put("运动代步", 4);
        map.put("书籍文具", 5);
        map.put("其他", 6);
        String url = null;
        String path = null;
        UserWant userWant = new UserWant();
        if(!image.isEmpty()) {
            // 获取图片原始文件名
            String originalFilename = image.getOriginalFilename();
            // 文件名使用当前时间
            String imageName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
            // 获取上传图片的扩展名(jpg/png/...)
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            // 图片上传的相对路径（因为相对路径放到页面上就可以显示图片）
            path = imageName + extension;
            // 图片上传的绝对路径
            url = uploadPath + path;
            File dir = new File(url);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                // 将图片上传到本地
                image.transferTo(new File(url));
            }catch (Exception e) {
                log.info("写入失败");
            }
            String headerUrl = "/image/" + path;
            userWant.setImage(headerUrl);
        }

        try{
            if(!org.apache.commons.lang3.StringUtils.isBlank(remark)) {
                userWant.setRemark(remark);
            }
            if(!org.apache.commons.lang3.StringUtils.isBlank(modified)) {
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM");
                Date date = ft.parse(modified);
                userWant.setModified(date);
            }
            if(!org.apache.commons.lang3.StringUtils.isBlank(name)) {
                userWant.setName(name);
            }
            if(!org.apache.commons.lang3.StringUtils.isBlank(kind)) {
                userWant.setKindid(map.get(kind));
            }

            if(!org.apache.commons.lang3.StringUtils.isBlank(id)) {
                userWant.setId(Integer.valueOf(id));
            }
            if(!org.apache.commons.lang3.StringUtils.isBlank(price)) {
                userWant.setPrice(BigDecimal.valueOf(Long.valueOf(price)));
            }
            if(!org.apache.commons.lang3.StringUtils.isBlank(quantity)) {
                userWant.setQuantity(Integer.valueOf(quantity));
            }
            userWantService.updateByPrimaryKeySelective(userWant);
        } catch(Exception e) {
            log.info("写入失败");
        }
        return "redirect:/myBookSelf";
    }

    @RequestMapping(path="/image/{fileName}",method=RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        FileInputStream fis = null;
        //服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 解析文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
        //想要图片
        response.setContentType("image/" + suffix);
        try {
            fis = new FileInputStream(fileName);
            OutputStream os = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int b =0;
            while((b = fis.read(buffer)) != -1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            log.error("读取头像失败" + e.getMessage());
        }finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    log.info("fis关闭失败");
                }
            }
        }
    }

    @RequestMapping(path = "/delete", method = RequestMethod.GET)
    public String delete(Integer id) {
        shopInformationService.deleteByPrimaryKey(id);
        return "redirect:/myBookSelf";
    }

    //进入到发布商品页面
    @RequestMapping(value = "/publish_product.do", method = RequestMethod.GET)
    public String publish(HttpServletRequest request, Model model) {
        //先判断用户有没有登录
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            //如果没有登录
            return "redirect:/login.do";
        } else {
            model.addAttribute("userInformation", userInformation);
        }
        //如果登录了，判断该用户有没有经过认证
        try {
            String realName = userInformation.getRealname();
            String sno = userInformation.getSno();
            String dormitory = userInformation.getDormitory();
            if (StringUtils.getInstance().isNullOrEmpty(realName) || StringUtils.getInstance().isNullOrEmpty(sno) || StringUtils.getInstance().isNullOrEmpty(dormitory)) {
                //没有
                model.addAttribute("message", "请先认证真实信息");
                return "redirect:personal_info.do";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/login.do";
        }
        String goodsToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("goodsToken", goodsToken);
        model.addAttribute("shopInformation", new ShopInformation());
        model.addAttribute("action", 1);
        model.addAttribute("token", goodsToken);
        return "page/publish_product";
    }

    //模糊查询商品
    @RequestMapping(value = "/findShopByName.do")
    public String findByName(HttpServletRequest request, Page page, Model model, @RequestParam String name) {
        if(page.getCurrent() < 1) {
            page.setCurrent(1);
        }
        if(page.getCurrent() > page.getTotal()) {
            page.setCurrent(page.getTotal());
        }
        List<ShopInformation> shopInformations = shopInformationService.selectByName(name);
//            if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
//                userInformation = new UserInformation();
//                model.addAttribute("userInformation", userInformation);
//            } else {
//                model.addAttribute("userInformation", userInformation);
//            }
        model.addAttribute("shopInformation", shopInformations);
        return "new/bookStore";
    }

    //进入查看商品详情
    @RequestMapping(value = "/selectById.do")
    public String selectById(@RequestParam int id,
                             HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            userInformation = new UserInformation();
            model.addAttribute("userInformation", userInformation);
        }
        try {
            ShopInformation shopInformation = shopInformationService.selectByPrimaryKey(id);
            model.addAttribute("shopInformation", shopInformation);
            List<ShopContext> shopContexts = shopContextService.selectById(id);
            List<ShopContextBean> shopContextBeans = new ArrayList<>();
            for (ShopContext s : shopContexts) {
                ShopContextBean shopContextBean = new ShopContextBean();
                UserInformation u = userInformationService.selectByPrimaryKey(s.getUid());
                shopContextBean.setContext(s.getContext());
                shopContextBean.setId(s.getId());
                shopContextBean.setModified(s.getModified());
                shopContextBean.setUid(u.getId());
                shopContextBean.setUsername(u.getUsername());
                shopContextBeans.add(shopContextBean);
            }
            String sort = getSort(shopInformation.getSort());
            String goodsToken = TokenProccessor.getInstance().makeToken();
            request.getSession().setAttribute("goodsToken", goodsToken);
            model.addAttribute("token", goodsToken);
            model.addAttribute("sort", sort);
            model.addAttribute("userInformation", userInformation);
            model.addAttribute("shopContextBeans", shopContextBeans);
            return "page/product_info";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }
    }

    //进入到求购商城
    @RequestMapping(value = "/require_mall.do")
    public String requireMall(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            userInformation = new UserInformation();
            model.addAttribute("userInformation", userInformation);
        } else {
            model.addAttribute("userInformation", userInformation);
        }
        List<UserWant> userWants = userWantService.selectAll();
        List<UserWantBean> list = new ArrayList<>();
        for (UserWant userWant : userWants) {
            UserWantBean u = new UserWantBean();
            u.setSort(getSort(userWant.getSort()));
            u.setRemark(userWant.getRemark());
            u.setQuantity(userWant.getQuantity());
            u.setPrice(userWant.getPrice().doubleValue());
            u.setUid(userWant.getUid());
            u.setId(userWant.getId());
            u.setModified(userWant.getModified());
            u.setName(userWant.getName());
            list.add(u);
        }
        model.addAttribute("list", list);
        return "page/require_mall";
    }

    //通过id查看商品的详情
    @RequestMapping(value = "/findShopById.do")
    @ResponseBody
    public ShopInformation findShopById(@RequestParam int id) {
        return shopInformationService.selectByPrimaryKey(id);
    }

//    //通过分类选择商品
//    @RequestMapping(value = "/selectBySort.do")
//    @ResponseBody
//    public List<ShopInformation> selectBySort(@RequestParam int sort) {
//        return shopInformationService.selectBySort(sort);
//    }

    //分页查询
    @RequestMapping(value = "/selectByCounts.do")
    @ResponseBody
    public List<ShopInformation> selectByCounts(@RequestParam int counts) {
        Map<String, Integer> map = new HashMap<>();
        map.put("start", (counts - 1) * 12);
        map.put("end", 12);
        return shopInformationService.selectTen(map);
    }
//    //通过id查看商品详情
//    @RequestMapping(value = "/showShop")
//    public String showShop(@RequestParam int id, HttpServletRequest request, Model model) {
//        ShopInformation shopInformation =
//    }

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

    private String getSort(int sort) {
        StringBuilder sb = new StringBuilder();
        Specific specific = selectSpecificBySort(sort);
        int cid = specific.getCid();
        Classification classification = selectClassificationByCid(cid);
        int aid = classification.getAid();
        AllKinds allKinds = selectAllKindsByAid(aid);
        String allName = allKinds.getName();
        sb.append(allName);
        sb.append("-");
        sb.append(classification.getName());
        sb.append("-");
        sb.append(specific.getName());
        return sb.toString();
    }

}
