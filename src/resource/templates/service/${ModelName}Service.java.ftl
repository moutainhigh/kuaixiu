package ${ModelPath}.${ProjectName}.service;


import com.common.base.service.BaseService;
import ${ModelPath}.${ProjectName}.dao.${ModelName}Mapper;
import ${ModelPath}.${ProjectName}.entity.${ModelName};

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ${ModelName} Service
 * @CreateDate: ${CreateDate}
 * @version: V 1.0
 */
@Service("${ModelName?uncap_first}Service")
public class ${ModelName}Service extends BaseService<${ModelName}> {
    private static final Logger log= Logger.getLogger(${ModelName}Service.class);

    @Autowired
    private ${ModelName}Mapper<${ModelName}> mapper;


    public ${ModelName}Mapper<${ModelName}> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}