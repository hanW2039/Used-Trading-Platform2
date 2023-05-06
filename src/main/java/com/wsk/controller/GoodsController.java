package com.wsk.controller;

import com.wsk.pojo.*;
import com.wsk.response.BaseResponse;
import com.wsk.service.*;
import com.wsk.service.Impl.OperationServiceImpl;
import com.wsk.token.TokenProccessor;
import com.wsk.tool.StringUtils;
import com.wsk.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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
    @Resource
    private GoodsCarService goodsCarService;
    @Autowired
    private OperationServiceImpl operationServiceImpl;

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

    @RequestMapping(path = "/buy")
    public String buy(Model model, @RequestParam String ids) {
        String[] split = ids.split(",");
        List<String> idList = Arrays.asList(split);
        UserInformation user = hostHolder.getUser();
        if (null != user) {
            for(String id : idList) {
                // 记录操作数据
                Operation operation = new Operation();
                operation.setType("3");
                operation.setUid(user.getId());
                operation.setSid(Integer.valueOf(id));
                operationServiceImpl.addOperationRecord(operation);
            }
        }
        List<ShopInformation> shopInformationList = new ArrayList<>();
//        goodsCarService.
        BigDecimal sum = BigDecimal.ZERO; // 使用静态常量ZERO来初始化sum为0
        for(String id : idList) {
            ShopInformation key = shopInformationService.selectByPrimaryKey(Integer.valueOf(id));
            shopInformationList.add(key);
            sum = sum.add(key.getPrice()); // 将结果赋值给sum，并且使用add方法获得新的BigDecimal对象
        }
        model.addAttribute("sum", sum.toString());
        model.addAttribute("shopList", shopInformationList);
        model.addAttribute("length",idList.size());
        model.addAttribute("user",user);
         return "new/balance";
    }

    @RequestMapping(path = "/addOne", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse addOne(@RequestBody Integer id) {
        GoodsCar goodsCar = goodsCarService.selectByPrimaryKey(id);
        ShopInformation shopInformation = shopInformationService.selectByPrimaryKey(goodsCar.getSid());
        if(goodsCar.getQuantity() + 1 > shopInformation.getQuantity()) {
            throw new IllegalArgumentException("库存不够");
        }
        goodsCar.setQuantity(goodsCar.getQuantity() + 1);
        goodsCarService.updateByPrimaryKeySelective(goodsCar);
        return BaseResponse.success();
    }
    @RequestMapping(path = "/reduceOne", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse reduceOne(@RequestBody Integer id) {
        GoodsCar goodsCar = goodsCarService.selectByPrimaryKey(id);
        if(goodsCar.getQuantity() <= 1) {
            throw new IllegalArgumentException("不可小于1");
        }
        goodsCar.setQuantity(goodsCar.getQuantity() - 1);
        goodsCarService.updateByPrimaryKeySelective(goodsCar);
        return BaseResponse.success();
    }
    @RequestMapping(path = "/balance")
    public String getBalance(Model model, @RequestBody List<String> ids) {
//        shopInformationService.
        return "new/balance";
    }

    @RequestMapping(path = "/shopCar")
    public String shopCar() {
        return "new/shopCar";
    }

    @RequestMapping(path = "/addToCar")
    @ResponseBody
    public BaseResponse add(@RequestBody Integer id) {
        UserInformation user = hostHolder.getUser();
        if (null != user) {
            // 记录操作数据
            Operation operation = new Operation();
            operation.setType("2");
            operation.setUid(user.getId());
            operation.setSid(Integer.valueOf(id));
            operationServiceImpl.addOperationRecord(operation);
        }
        if (StringUtils.getInstance().isNullOrEmpty(user)) {
            throw new IllegalArgumentException("未登录");
        }
        GoodsCar goodsCar = new GoodsCar();
        goodsCar.setQuantity(1);
        goodsCar.setSid(id);
        goodsCar.setDisplay(1);
        goodsCar.setUid(user.getId());
        goodsCar.setModified(new Date());
        goodsCarService.insertSelective(goodsCar);
        return BaseResponse.success();
    }
}
