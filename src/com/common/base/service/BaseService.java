package com.common.base.service;

import com.common.base.dao.BaseDao;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 超级服务抽象类.
 * 
 * @param <T> 泛型对象
 * @CreateDate: 2016-6-6 下午2:40:51
 * @version: V 1.0
 */
public abstract class BaseService<T>{
    
    /**
     * 获取数据接口对象
     * @return
     * @CreateDate: 2016-6-6 下午2:42:01
     */
    public abstract BaseDao<T> getDao();

    /**
     * 新增记录
     * @param t
     * @throws Exception
     */
    @Transactional
    public int add(T t) {
        //设置主键.字符类型采用UUID,数字类型采用自增
        //ClassReflectUtil.setIdKeyValue(t, "id", UUID.randomUUID().toString());
        return getDao().add(t);
    }

    /**
     * 修改记录
     * @param t
     * @throws Exception
     */
    @Transactional
    public int saveUpdate(T t) {
        return getDao().update(t);
    }

    /**
     * 删除
     * @param ids
     * @throws Exception
     */
    @Transactional
    public int delete(Object... ids) {
        int rest = 0;
        if(ids != null && ids.length > 0){
            for(Object id : ids ){
                rest += getDao().delete(id);
            }
        }
        return rest;
    }

    

    /**
     * 根据id查找一条记录
     * @param id
     * @return
     * @throws Exception
     */
    public T queryById(Object id) {
        return getDao().queryById(id);
    }

    /**
     * 查询列表 无分页
     * @param t
     * @return
     * @throws Exception
     */
    public List<T> queryList(T t) {
        return getDao().queryList(t);
    }
    
    /**
     * 查询列表 带分页
     * @param t
     * @return
     * @throws Exception
     */
    public List<T> queryListForPage(T t) {
        return getDao().queryListForPage(t);
    }



}
