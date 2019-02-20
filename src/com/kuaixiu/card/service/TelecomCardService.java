package com.kuaixiu.card.service;

import com.common.base.service.BaseService;
import com.common.paginate.Page;
import com.kuaixiu.card.dao.TelecomCardMapper;
import com.kuaixiu.card.entity.TelecomCard;
import com.system.basic.user.entity.SessionUser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * @Auther: anson
 * @Date: 2018/7/25
 * @Description:
 */
@Service
public class TelecomCardService extends BaseService<TelecomCard> {
    private static final Logger log = Logger.getLogger(TelecomCardService.class);
    @Autowired
    private TelecomCardMapper<TelecomCard> mapper;

    @Override
    public TelecomCardMapper<TelecomCard> getDao() {
        return mapper;
    }

    /**
     * 批量添加卡号
     *
     * @param list
     * @return
     */
    @Transactional
    public int addBatch(List<TelecomCard> list) {
        return mapper.addBatch(list);
    }


    @Transactional
    public int updateByStartIccid(TelecomCard t) {
        return mapper.updateByStartIccid(t);
    }

    /**
     * 查询当天更新未成功推送的号卡  0点开始
     *
     * @param queryStartTime
     * @return
     */
    public List<TelecomCard> queryByTime(String queryStartTime) {
        return mapper.queryByTime(queryStartTime);
    }


    /**
     * 查询未推送成功的号卡
     *
     * @return
     */
    public List<TelecomCard> queryPushFail() {
        return mapper.queryPushFail();
    }


    /**
     * 联合查询列表 带分页
     *
     * @param t
     * @return
     */
    public List<TelecomCard> queryTelecomListForPage(TelecomCard t) {
        return mapper.queryTelecomListForPage(t);
    }

    /**
     * 已Excel形式导出列表数据
     *
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expDataExcel(Map<String, Object> params) {
//        String templateFileName = params.get("tempFileName") + "";
//        String destFileName = params.get("outFileName") + "";
        HttpServletResponse response = (HttpServletResponse) params.get("response");
        //获取登录用户
        SessionUser su = (SessionUser) params.get("user");

        //获取查询条件
        // 获取iccid  是否已寄出  批次号   是否已分配   号卡类型   号卡名称   号卡地市   导入时间区间
        // 分配时间区间   推送电渠状态  转转推送给超人的时间区间  超人推送电渠的时间区间
        String iccid = MapUtils.getString(params, "query_iccid");
        String isUse = MapUtils.getString(params, "query_isUse");
        String batch = MapUtils.getString(params, "query_batchId");
        String isDistribution = MapUtils.getString(params, "query_isDistribution");
        String cardType = MapUtils.getString(params, "query_cardType");
        String cardName = MapUtils.getString(params, "query_cardName");
        String city = MapUtils.getString(params, "query_city");
        String startTime = MapUtils.getString(params, "query_startTime");
        String endTime = MapUtils.getString(params, "query_endTime");
        String startDistributionTime = MapUtils.getString(params, "query_startDistributionTime");
        String endDistributionTime = MapUtils.getString(params, "query_endDistributionTime");
        String pushStatus = MapUtils.getString(params, "query_pushStatus");
        String zhuangStartTime = MapUtils.getString(params, "query_startZhuangTime");
        String zhuangEndTime = MapUtils.getString(params, "query_endZhuangTime");
        String telecomStartTime = MapUtils.getString(params, "query_startTelecomTime");
        String telecomEndTime = MapUtils.getString(params, "query_endTelecomTime");

        TelecomCard telecomCard = new TelecomCard();
        telecomCard.setIccid(iccid);
        telecomCard.setBatch(batch);
        if ("kf014".equals(su.getUserId())) {
            telecomCard.setProvince("台州市");
        } else {
            telecomCard.setProvince(city);
        }
        telecomCard.setQueryStartTime(startTime);
        telecomCard.setQueryEndTime(endTime);
        telecomCard.setQueryStartDistributionTime(startDistributionTime);
        telecomCard.setQueryEndDistributionTime(endDistributionTime);
        if (StringUtils.isNotBlank(isUse)) {
            telecomCard.setIsUse(Integer.parseInt(isUse));
        }
        if (StringUtils.isNotBlank(cardType)) {
            telecomCard.setType(Integer.parseInt(cardType));
        }
        if (StringUtils.isNotBlank(isDistribution)) {
            telecomCard.setIsDistribution(Integer.parseInt(isDistribution));
        }
        if (StringUtils.isNotBlank(cardName)) {
            telecomCard.setCardName(Integer.parseInt(cardName));
        }
        if (StringUtils.isNotBlank(pushStatus)) {
            telecomCard.setIsPush(Integer.parseInt(pushStatus));
        }
        // 转转推送给超人
        if (StringUtils.isNotBlank(zhuangStartTime) && StringUtils.isNotBlank(zhuangEndTime)) {
            telecomCard.setQueryZhuangStartTime(zhuangStartTime);
            telecomCard.setQueryZhuangEndTime(zhuangEndTime);
            // 转转推送给超人的号卡 状态为已使用
            telecomCard.setIsUse(1);
        }
        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        // 超人推送给电渠
        if (StringUtils.isNotBlank(telecomStartTime) && StringUtils.isNotBlank(telecomEndTime)) {
            telecomCard.setQueryTelecomStartTime(telecomStartTime);
            telecomCard.setQueryTelecomEndTime(telecomEndTime);
            maps = getDao().queryTelecomList(telecomCard);
        } else {
//            for(int i=0;i<1000000;i++) {
//                List<Map<String, Object>> maps1 = new ArrayList<Map<String, Object>>();
//                Page page=new Page();
//                page.setStart(i);
//                page.setPageSize(1000);
//                telecomCard.setPage(page);
//                maps1 = getDao().queryListTwoForPage(telecomCard);
//                if(CollectionUtils.isEmpty(maps1)){
//                    break;
//                }
//                maps.addAll(maps1);
//            }
            maps = getDao().queryListTwo(telecomCard);
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
        String[] header = new String[]{"批次", "导入时间", "所属本地网", "ICCID", "号卡类型",
                "号卡名称", "分配时间", "失效时间", "站点ID", "站点名称",
                "联系人", "联系电话", "地址", "订单ID", "发货时间",
                "物流单号", "物流公司", "发货城市"};

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
            String filedisplay = "号卡导出.xls";
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
        if (map.get("batch") == null) {
            row.createCell(0).setCellValue("");
        } else {
            row.createCell(0).setCellValue(map.get("batch").toString());
        }
        if (map.get("in_time") == null) {
            row.createCell(1).setCellValue("");
        } else {
            row.createCell(1).setCellValue(map.get("in_time").toString());
        }
        if (map.get("province") == null) {
            row.createCell(2).setCellValue("");
        } else {
            row.createCell(2).setCellValue(map.get("province").toString());
        }
        if (map.get("iccid") == null) {
            row.createCell(3).setCellValue("");
        } else {
            row.createCell(3).setCellValue(map.get("iccid").toString());
        }
        if (map.get("type") == null) {
            row.createCell(4).setCellValue("");
        } else {
            if ("0".equals(map.get("type").toString())) {
                row.createCell(4).setCellValue("小白卡");
            } else {
                row.createCell(4).setCellValue("即买即通卡");
            }
        }
        if (map.get("card_name") == null) {
            row.createCell(5).setCellValue("");
        } else {
            switch (Integer.valueOf(map.get("card_name").toString())) {
                case 0:
                    row.createCell(5).setCellValue("白金卡");
                    break;
                case 1:
                    row.createCell(5).setCellValue("抖音卡");
                    break;
                case 2:
                    row.createCell(5).setCellValue("鱼卡");
                    break;
                case 3:
                    row.createCell(5).setCellValue("49元不限流量卡");
                    break;
                case 4:
                    row.createCell(5).setCellValue("99元不限流量卡");
                    break;
                case 5:
                    row.createCell(5).setCellValue("199元不限流量卡");
                    break;
            }
        }
        if (map.get("distribution_time") == null) {
            row.createCell(6).setCellValue("");
        } else {
            row.createCell(6).setCellValue(map.get("distribution_time").toString());
        }
        if (map.get("lose_efficacy") == null) {
            row.createCell(7).setCellValue("");
        } else {
            row.createCell(7).setCellValue(map.get("lose_efficacy").toString());
        }
        if (map.get("id") == null) {
            row.createCell(8).setCellValue("");
        } else {
            row.createCell(8).setCellValue(map.get("id").toString());
        }
        if (map.get("station_name") == null) {
            row.createCell(9).setCellValue("");
        } else {
            row.createCell(9).setCellValue(map.get("station_name").toString());
        }
        if (map.get("name") == null) {
            row.createCell(10).setCellValue("");
        } else {
            row.createCell(10).setCellValue(map.get("name").toString());
        }
        if (map.get("tel") == null) {
            row.createCell(11).setCellValue("");
        } else {
            row.createCell(11).setCellValue(map.get("tel").toString());
        }
        if (map.get("address") == null) {
            row.createCell(12).setCellValue("");
        } else {
            row.createCell(12).setCellValue(map.get("address").toString());
        }
        if (map.get("success_order_id") == null) {
            row.createCell(13).setCellValue("");
        } else {
            row.createCell(13).setCellValue(map.get("success_order_id").toString());
        }
        if (map.get("send_time") == null) {
            row.createCell(14).setCellValue("");
        } else {
            row.createCell(14).setCellValue(map.get("send_time").toString());
        }
        if (map.get("express_number") == null) {
            row.createCell(15).setCellValue("");
        } else {
            row.createCell(15).setCellValue(map.get("express_number").toString());
        }
        if (map.get("express_name") == null) {
            row.createCell(16).setCellValue("");
        } else {
            row.createCell(16).setCellValue(map.get("express_name").toString());
        }
        if (map.get("send_city") == null) {
            row.createCell(17).setCellValue("");
        } else {
            row.createCell(17).setCellValue(map.get("send_city").toString());
        }
        return row;
    }

}
