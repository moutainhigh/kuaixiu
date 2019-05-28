package com.kuaixiu.recycle.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.util.AES;
import com.kuaixiu.card.entity.TelecomCard;
import com.kuaixiu.recycle.dao.RecycleTestMapper;
import com.kuaixiu.recycle.entity.RecycleCheckItems;
import com.kuaixiu.recycle.entity.RecycleTest;

import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import jodd.util.StringUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * RecycleTest Service
 *
 * @CreateDate: 2019-03-12 下午04:08:20
 * @version: V 1.0
 */
@Service("recycleTestService")
public class RecycleTestService extends BaseService<RecycleTest> {
    private static final Logger log = Logger.getLogger(RecycleTestService.class);

    @Autowired
    private RecycleTestMapper<RecycleTest> mapper;


    public RecycleTestMapper<RecycleTest> getDao() {
        return mapper;
    }

    //**********自定义方法***********
    private static final String baseNewUrl = SystemConstant.RECYCLE_NEW_URL;
    private static final String cipherdata = SystemConstant.RECYCLE_REQUEST;
    private static final String baseUrl = SystemConstant.RECYCLE_URL;

    public Map getBrandAndModelByProductId(String productId) {
        Map map = new HashMap();
        try {
            JSONObject requestNews2 = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code2 = new JSONObject();
            code2.put("productid", productId);
            String realCode2 = AES.Encrypt(code2.toString());  //加密
            requestNews2.put(cipherdata, realCode2);
            //发起请求
            String url = baseNewUrl + "getmodelbyid";
            String getResult2 = AES.post(url, requestNews2);
            //对得到结果进行解密
            JSONObject jsonResult2 = getResult(AES.Decrypt(getResult2));
            //将结果中的产品id转为string类型  json解析 long类型精度会丢失
            //防止返回机型信息为空
            if (StringUtil.isNotBlank(jsonResult2.getString("datainfo")) && !(jsonResult2.getJSONObject("datainfo")).isEmpty()) {
                JSONObject jq = jsonResult2.getJSONObject("datainfo");
                map.put("brandname", jq.getString("brandname"));
                map.put("modelname", jq.getString("modelname"));
                map.put("modellogo", jq.getString("modellogo"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map getProductName(String items, String productId1){
        Map map = new HashMap();
        try {
            JSONObject jsonResult = new JSONObject();
            String url = baseNewUrl + "getcheckitems";
            List<Map<String, String>> lists = new ArrayList<>();
            //转换items格式“1,2|2,6|4,15|5,19|6,21|35,114|11,43......”-->“2,6,15,19,21,114,43......”
            if (items.contains("|")) {
                StringBuilder sb = new StringBuilder();
                String[] itemses = items.split("\\|");
                for (int i = 0; i < itemses.length; i++) {
                    String[] item = itemses[i].split(",");
                    sb.append(item[1]);
                    if (itemses.length - 1 != i) {
                        sb.append(",");
                    }
                }
                items = sb.toString();
            }
            JSONObject requestNews = new JSONObject();
            //调用接口需要加密的数据
            JSONObject code = new JSONObject();
            code.put("productid", productId1);
            code.put("items", items);
            String realCode = AES.Encrypt(code.toString());  //加密
            requestNews.put(cipherdata, realCode);
            //发起请求
            String getResult = AES.post(url, requestNews);
            //对得到结果进行解密
            jsonResult = (JSONObject) JSONObject.parse(AES.Decrypt(getResult));
            if (StringUtil.isBlank(jsonResult.getString("datainfo"))) {
                map.put("product_name", "");
                return map;
            }
            StringBuilder sb1 = new StringBuilder();
            JSONArray jsonArray = jsonResult.getJSONArray("datainfo");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jqs = (JSONObject) jsonArray.get(i);
                sb1.append(jqs.getString("itemname"));
                sb1.append("、");
            }
            if (sb1.length() > 1) {
                sb1.deleteCharAt(sb1.length() - 1);
                map.put("product_name", sb1.toString());
            } else {
                map.put("product_name", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public void getImgaePath(String brandId, String ProductId, HttpServletRequest request) throws Exception {
        JSONObject requestNews2 = new JSONObject();
        //调用接口需要加密的数据
        JSONObject code2 = new JSONObject();
        code2.put("pageindex", 1);
        code2.put("pagesize", 500);
        code2.put("categoryid", null);
        code2.put("brandid", brandId);
        code2.put("keyword", null);
        String realCode2 = AES.Encrypt(code2.toString());  //加密
        requestNews2.put(cipherdata, realCode2);
        //发起请求
        String url = baseNewUrl + "getmodellist";
        String getResult2 = AES.post(url, requestNews2);
        //对得到结果进行解密
        JSONObject jsonResult2 = getResult(AES.Decrypt(getResult2));
        //将结果中的产品id转为string类型  json解析 long类型精度会丢失
        //防止返回机型信息为空
        if (StringUtil.isNotBlank(jsonResult2.getString("datainfo")) && !(jsonResult2.getJSONArray("datainfo")).isEmpty()) {
            JSONArray jq = jsonResult2.getJSONArray("datainfo");
            JSONObject jqs = (JSONObject) jq.get(0);
            JSONArray j = jqs.getJSONArray("sublist");
            for (int i = 0; i < j.size(); i++) {
                JSONObject js = j.getJSONObject(i);
                if (ProductId.equals(js.getString("productid"))) {
                    String imgaePath = js.getString("modellogo");
                    //如果该机型的图片地址为空 则使用默认图片地址
                    if (StringUtil.isBlank(imgaePath)) {
                        String realUrl = request.getRequestURL().toString();
                        String domain = realUrl.replace(request.getRequestURI(), "");
                        String path = domain + "/" + SystemConstant.DEFAULTIMAGE;
                        imgaePath = path;
                    }
                    request.setAttribute("imagePath", imgaePath);
                    break;
                }
            }
        }
    }

    /**
     * 返回结果解析
     *
     * @param originalString
     * @return
     */
    public static JSONObject getResult(String originalString) {
        JSONObject result = (JSONObject) JSONObject.parse(originalString);
        if (result.getString("result") != null && !result.getString("result").equals("RESPONSESUCCESS")) {
            throw new SystemException(result.getString("msg"));
        }
        return result;
    }

    @Autowired
    private RecycleCheckItemsService checkItemsService;

    /**
     * 已Excel形式导出列表数据
     *
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expDataExcel(Map<String, Object> params) {
        HttpServletResponse response = (HttpServletResponse) params.get("response");
        //获取登录用户
        SessionUser su = (SessionUser) params.get("user");
        //获取查询条件
        String queryStartTime = MapUtils.getString(params, "query_startTime");
        String queryEndTime = MapUtils.getString(params, "query_endTime");
        String mobile = MapUtils.getString(params, "mobile");
        String brandId = MapUtils.getString(params, "brandId");
        String productId = MapUtils.getString(params, "productId");
        String channel = MapUtils.getString(params, "channel");//渠道
        String isOrder = MapUtils.getString(params, "isOrder");//是否成单
        String isVisit = MapUtils.getString(params, "isVisit");//是否回访

        RecycleCheckItems checkItem = new RecycleCheckItems();
        String idStr = MapUtils.getString(params, "ids");
        if (StringUtils.isNotBlank(idStr)) {
            String[] ids = StringUtils.split(idStr, ",");
            checkItem.setQueryIds(Arrays.asList(ids));
        }
        checkItem.setQueryStartTime(queryStartTime);
        checkItem.setQueryEndTime(queryEndTime);
        checkItem.setLoginMobile(mobile);
        checkItem.setBrandId(brandId);
        checkItem.setProductId(productId);
        if (com.common.wechat.common.util.StringUtils.isNotBlank(isOrder)) {
            checkItem.setIsOrder(isOrder);
        }
        if (com.common.wechat.common.util.StringUtils.isNotBlank(isVisit)) {
            checkItem.setIsVisit(isVisit);
        }
        List<Map<String, Object>> maps = checkItemsService.getDao().queryTestList(checkItem);
        for (Map<String, Object> map : maps) {
            String productId1 = map.get("product_id").toString();
            Map brandAndModel = getBrandAndModelByProductId(productId1);
            map.put("brand", brandAndModel.get("brandname"));
            map.put("model", brandAndModel.get("modelname"));
            Map productName = getProductName(map.get("items").toString(), productId1);
            map.put("product_name", productName.get("product_name"));
        }

        List<List<Map<String, Object>>> lists = new ArrayList<>();
        int size = maps.size() / 10000;
        List<String> listAgent = new ArrayList<>();
        if (maps.size() < 10000) {
            lists.add(maps);
            listAgent.add("集合");
        } else {
            for (int i = 0; i < size; i++) {
                List<Map<String, Object>> list = maps.subList(i * 10000, (i + 1) * 10000);
                lists.add(list);
                listAgent.add("集合" + i);
            }
            //余数
            int lastSize = maps.size() - size * 10000;
            List<Map<String, Object>> list = maps.subList(size * 10000, size * 10000 + lastSize);
            lists.add(list);
            listAgent.add("集合" + size);
        }

        //excel标题
        String[] header = new String[]{"创建时间", "品牌", "机型", "检测项目", "报价（元）",
                "检测手机号", "监测渠道", "是否成单", "回访情况"};

// 导出到多个sheet中--------------------------------------------------------------------------------开始
        // 创建一个EXCEL
        HSSFWorkbook wb = new HSSFWorkbook();
        // 循环经销商，每个经销商一个sheet
        for (int i = 0; i < listAgent.size(); i++) {
            // 第 i 个sheet,以经销商命名
            HSSFSheet sheet = wb.createSheet(listAgent.get(i).toString());
            // 第i个sheet第二行为列名
            HSSFRow rowFirst = sheet.createRow(0);
            // 写标题
            for (int j = 0; j < header.length; j++) {
                // 获取第一行的每一个单元格
                HSSFCell cell = rowFirst.createCell(j);
                // 往单元格里面写入值
                cell.setCellValue(header[j]);
            }
            for (int j = 0; j < lists.get(i).size(); j++) {
                Map<?, ?> map = (Map<?, ?>) lists.get(i).get(j);
                // 创建数据行，从第三行开始
                HSSFRow row = sheet.createRow(j + 1);
                // 设置对应单元格的值
                getRow(row, map);
            }
        }
        // 写出文件（path为文件路径含文件名）
        OutputStream os = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/x-download");
            String filedisplay = "检测记录导出.xls";
            //防止文件名含有中文乱码
            filedisplay = new String(filedisplay.getBytes("gb2312"), "ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + filedisplay);

            os = response.getOutputStream();
            wb.write(os);
        } catch (Exception e) {
            System.out.println("导出文件出错了.....\n" + e.getMessage());
        } finally {
            try {
                os.flush();
                os.close();
            } catch (IOException e) {
                System.out.println("导出文件出错了.....\n" + e.getMessage());
            }
        }
        // 导出到多个sheet中---------------------------------------------------------------------------------结束
    }


    private HSSFRow getRow(HSSFRow row, Map<?, ?> map) {
        // 设置对应单元格的值
        if (map.get("createTime") == null) {
            row.createCell(0).setCellValue("");
        } else {
            row.createCell(0).setCellValue(map.get("createTime").toString());
        }
        if (map.get("brand") == null) {
            row.createCell(1).setCellValue("");
        } else {
            row.createCell(1).setCellValue(map.get("brand").toString());
        }
        if (map.get("model") == null) {
            row.createCell(2).setCellValue("");
        } else {
            row.createCell(2).setCellValue(map.get("model").toString());
        }
        if (map.get("product_name") == null) {
            row.createCell(3).setCellValue("");
        } else {
            row.createCell(3).setCellValue(map.get("product_name").toString());
        }
        if (map.get("price") == null) {
            row.createCell(4).setCellValue("");
        } else {
            row.createCell(4).setCellValue(map.get("price").toString());
        }
        if (map.get("login_mobile") == null) {
            row.createCell(5).setCellValue("");
        } else {
            row.createCell(5).setCellValue(map.get("login_mobile").toString());
        }
        if (map.get("login_mobile") == null) {
            row.createCell(6).setCellValue("");
        } else {
            row.createCell(6).setCellValue(map.get("login_mobile").toString());
        }
        if (map.get("recycle_id") == null) {
            row.createCell(7).setCellValue("否");
        } else {
            row.createCell(7).setCellValue("是");
        }
        if (map.get("test_id") == null) {
            row.createCell(8).setCellValue("待回访");
        } else {
            row.createCell(8).setCellValue("已回访");
        }
        return row;
    }
}