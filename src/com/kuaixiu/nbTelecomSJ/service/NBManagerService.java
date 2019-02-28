package com.kuaixiu.nbTelecomSJ.service;


import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.kuaixiu.nbTelecomSJ.dao.NBManagerMapper;
import com.kuaixiu.nbTelecomSJ.entity.NBManager;

import com.kuaixiu.order.entity.Order;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NBManager Service
 * @CreateDate: 2019-02-22 下午06:34:26
 * @version: V 1.0
 */
@Service("nBManagerService")
public class NBManagerService extends BaseService<NBManager> {
    private static final Logger log= Logger.getLogger(NBManagerService.class);

    @Autowired
    private NBManagerMapper<NBManager> mapper;


    public NBManagerMapper<NBManager> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 已Excel形式导出列表数据
     *
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expDataExcel(Map<String, Object> params) {
        String templateFileName = params.get("tempFileName") + "";
        String destFileName = params.get("outFileName") + "";

        //获取查询条件
        String queryStartTime = MapUtils.getString(params,"queryStartTime");
        String queryEndTime = MapUtils.getString(params,"queryEndTime");
        String managerName = MapUtils.getString(params,"managerName");
        String managerTel = MapUtils.getString(params,"managerTel");
        String department = MapUtils.getString(params,"department");
        String idStr = MapUtils.getString(params, "ids");

        NBManager nbManager=new NBManager();
        if (StringUtils.isNotBlank(idStr)) {
            String[] ids = StringUtils.split(idStr, ",");
            nbManager.setQueryIds(Arrays.asList(ids));
        }
        nbManager.setQueryStartTime(queryStartTime);
        nbManager.setQueryEndTime(queryEndTime);
        nbManager.setManagerName(managerName);
        nbManager.setManagerTel(managerTel);
        nbManager.setDepartment(department);
        List<NBManager> nbManagers=this.queryList(nbManager);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(NBManager nbManager1:nbManagers){
            nbManager1.setStrCreateTime(sdf.format(nbManager1.getCreateTime()));
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", nbManagers);
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