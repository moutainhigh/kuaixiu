package com.kuaixiu.card.service;

import com.common.base.service.BaseService;
import com.kuaixiu.card.dao.TelecomCardMapper;
import com.kuaixiu.card.entity.TelecomCard;
import com.system.basic.user.entity.SessionUser;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

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
        String templateFileName = params.get("tempFileName") + "";
        String destFileName = params.get("outFileName") + "";

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
        List<Map> maps = new ArrayList<Map>();
        // 超人推送给电渠
        if (StringUtils.isNotBlank(telecomStartTime) && StringUtils.isNotBlank(telecomEndTime)) {
            telecomCard.setQueryTelecomStartTime(telecomStartTime);
            telecomCard.setQueryTelecomEndTime(telecomEndTime);
            maps = getDao().queryTelecomList(telecomCard);
        } else {
            maps = getDao().queryListTwo(telecomCard);
        }

        List<List> lists=new ArrayList<>();
        int size=maps.size()/10000;

        List sheetNames = new ArrayList();
        for(int i=0;i<size;i++){
            List list=maps.subList(i*10000,(i+1)*10000);
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("cards", list);
            lists.add(list);

            String sheetName = "集合"+i;
            sheetNames.add(sheetName);
        }



        XLSTransformer transformer = new XLSTransformer();
        OutputStream out = null;
        try {
//            transformer.transformXLS(templateFileName, map, destFileName);

            File template = ResourceUtils.getFile(templateFileName);
            InputStream is = new FileInputStream(template);
            Workbook workbook=transformer.transformMultipleSheetsList(is, lists, sheetNames, "cards", new HashMap(), 0);
            out = new BufferedOutputStream(new FileOutputStream(destFileName));
            workbook.write(out);
        } catch (ParsePropertyException e) {
            log.error("文件导出--ParsePropertyException", e);
        } catch (InvalidFormatException e) {
            log.error("文件导出--InvalidFormatException", e);
        } catch (IOException e) {
            log.error("文件导出--IOException", e);
        } finally {
            try {
                if (out != null){
                    out.close();
                }
                out = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
