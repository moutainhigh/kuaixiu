package com.system.api;

import java.util.Map;

/**
 * Api服务接口.
 * 
 * @author: lijx
 * @CreateDate: 2016-9-5 下午8:50:06
 * @version: V 1.0
 */
public interface ApiServiceInf {
    
    /**
     * 服务接口实现方法
     * @param params 业务参数
     * @return 接口返回内容,接口调用类会把返回数据赋值给result属性返回给客户
     * @CreateDate: 2016-9-5 下午8:54:41
     */
    Object process(Map<String, String> params);
}
