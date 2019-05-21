package com.kuaixiu.sjBusiness.dao;

import com.common.base.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SjRegisterForm Mapper
 *
 * @param <T>
 * @CreateDate: 2019-05-21 下午12:04:52
 * @version: V 1.0
 */
public interface SjRegisterFormMapper<T> extends BaseDao<T> {

    List<T> queryByNull();
    List<T> queryByMealId(@Param("mealId")int mealId);
    List<T> queryByModelId(@Param("modelId")int modelId,@Param("mealId")int mealId);
    List<T> queryByPoeId(@Param("poeId")int poeId,@Param("modelId")int modelId,@Param("mealId")int mealId);

    T queryBy4Id(@Param("storageId")int storageId,@Param("poeId")int poeId,
                 @Param("modelId")int modelId,@Param("mealId")int mealId);
}


