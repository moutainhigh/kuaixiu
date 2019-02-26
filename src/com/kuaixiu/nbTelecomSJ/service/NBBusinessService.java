package com.kuaixiu.nbTelecomSJ.service;


import com.common.base.service.BaseService;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.nbTelecomSJ.dao.NBBusinessMapper;
import com.kuaixiu.nbTelecomSJ.entity.NBBusiness;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NBBusiness Service
 * @CreateDate: 2019-02-23 上午11:53:31
 * @version: V 1.0
 */
@Service("nBBusinessService")
public class NBBusinessService extends BaseService<NBBusiness> {
    private static final Logger log= Logger.getLogger(NBBusinessService.class);

    @Autowired
    private NBBusinessMapper<NBBusiness> mapper;


    public NBBusinessMapper<NBBusiness> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    public NBBusiness queryByOpenId(String openId){
        NBBusiness nbBusiness=new NBBusiness();
        List<NBBusiness> nbBusinesses=getDao().queryByOpenId(openId);
        if(CollectionUtils.isEmpty(nbBusinesses)){
            return null;
        }
        return nbBusinesses.get(0);
    }


    /**
     * 已Excel形式导出列表数据
     *
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expSummaryOrderDataExcel(Map<String, Object> params) {
        String templateFileName = params.get("tempFileName") + "";
        String destFileName = params.get("outFileName") + "";

        //获取查询条件
        String queryStartTime = MapUtils.getString(params,"queryStartTime");
        String queryEndTime = MapUtils.getString(params,"queryEndTime");
        String countyId = MapUtils.getString(params,"countyId");
        String officeId = MapUtils.getString(params,"officeId");
        String areaId = MapUtils.getString(params,"areaId");
        String companyName = MapUtils.getString(params,"companyName");
        String landline = MapUtils.getString(params,"landline");
        String broadband = MapUtils.getString(params,"broadband");
        String addressType = MapUtils.getString(params,"addressType");
        String demand = MapUtils.getString(params,"demand");

        NBBusiness nbBusiness = new NBBusiness();
        nbBusiness.setQueryStartTime(queryStartTime);
        nbBusiness.setQueryEndTime(queryEndTime);
        if (StringUtils.isNotBlank(countyId)) {
            nbBusiness.setCountyId(Integer.valueOf(countyId));
        }
        if (StringUtils.isNotBlank(officeId)) {
            nbBusiness.setOfficeId(Integer.valueOf(officeId));
        }
        if (StringUtils.isNotBlank(areaId)) {
            nbBusiness.setAreaId(Integer.valueOf(areaId));
        }
        nbBusiness.setCompanyName(companyName);
        nbBusiness.setLandline(landline);
        nbBusiness.setBroadband(broadband);
        if (StringUtils.isNotBlank(addressType)) {
            nbBusiness.setAddressType(Integer.valueOf(addressType));
        }
        if (StringUtils.isNotBlank(demand)) {
            nbBusiness.setDemand(Integer.valueOf(demand));
        }

        List<Map<String, Object>> list = getDao().queryListMap(nbBusiness);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", list);
        XLSTransformer transformer = new XLSTransformer();
        try {
            transformer.transformXLS(templateFileName, map, destFileName);
        } catch (ParsePropertyException e) {
            log.error("文件导出--ParsePropertyException", e);
        } catch (InvalidFormatException e) {
            log.error("文件导出--InvalidFormatException", e);
        } catch (IOException e) {
            log.error("文件导出--IOException", e);
        }
    }

}