package com.kuaixiu.model.dao;

import com.common.base.dao.BaseDao;
import com.kuaixiu.model.entity.RepairCost;
import org.apache.cxf.binding.corba.wsdl.Object;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * RepairCost Mapper
 *
 * @param <T>
 * @CreateDate: 2016-08-26 下午10:40:42
 * @version: V 1.0
 */
public interface RepairCostMapper<T> extends BaseDao<T> {
    
    /**
     * 根据机型id删除维修价格
     * @param mid
     * @return
     * @CreateDate: 2016-9-3 下午4:31:59
     */
    int deleteByModelId(String mid);
    
    /**
     * 根据机型id和项目id查询机型价格
     * @param mid
     * @return
     * @CreateDate: 2016-9-3 下午4:31:59
     */
    T queryByMidAndPid(@Param("mid")String mid, @Param("pid")String pid);

    List<T> queryProjectForPage(T t);

    List<T> queryProjectList(T t);

}


