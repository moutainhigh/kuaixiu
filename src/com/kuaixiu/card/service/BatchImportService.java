package com.kuaixiu.card.service;

import com.common.base.service.BaseService;
import com.kuaixiu.card.dao.BatchImportMapper;
import com.kuaixiu.card.entity.BatchImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: anson
 * @Date: 2018/7/26
 * @Description:
 */
@Service
public class BatchImportService extends BaseService<BatchImport> {

    @Autowired
    private BatchImportMapper<BatchImport>  mapper;

    @Override
    public BatchImportMapper<BatchImport> getDao() {
        return mapper;
    }


    /**
     * 查找所有地市  不重复
     * @return
     */
    public List<String> queryProvince(){
         return mapper.queryProvince();
    }

}
