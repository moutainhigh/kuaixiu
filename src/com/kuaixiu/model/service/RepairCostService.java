package com.kuaixiu.model.service;


import com.common.base.service.BaseService;
import com.kuaixiu.model.dao.RepairCostMapper;
import com.kuaixiu.model.entity.RepairCost;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RepairCost Service
 * @CreateDate: 2016-08-26 下午10:40:42
 * @version: V 1.0
 */
@Service("repairCostService")
public class RepairCostService extends BaseService<RepairCost> {
    private static final Logger log= Logger.getLogger(RepairCostService.class);

    @Autowired
    private RepairCostMapper<RepairCost> mapper;


    public RepairCostMapper<RepairCost> getDao() {
        return mapper;
    }

    //**********自定义方法***********
    
    /**
     * 根据机型id查询机型价格
     * @param mid
     * @return
     * @CreateDate: 2016-9-2 下午7:19:08
     */
    public List<RepairCost> queryListByModelId(String mid){
        RepairCost t = new RepairCost();
        t.setModelId(mid);
        return getDao().queryList(t);
    }
    

    /**
     * 根据机型id和项目id查询机型价格
     * @param mid
     * @return
     * @CreateDate: 2016-9-2 下午7:19:08
     */
    public RepairCost queryByMidAndPid(String mid, String pid){
        return getDao().queryByMidAndPid(mid, pid);
    }


    /**
     * 根据机型id查询维修价格
     * @param mid
     * @return
     * @CreateDate: 2016-9-3 下午4:31:59
     */
    public int deleteByModelId(String mid){
        return getDao().deleteByModelId(mid);
    }

}