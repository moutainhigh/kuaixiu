package com.kuaixiu.materiel.entity;import com.common.base.entity.BaseEntity;/** * . * * @CreateDate: 2019-03-27 下午04:15:21 * @version: V 1.0 */public class MaterielType extends BaseEntity {        /**     * 序列化Id     */    private static final long serialVersionUID = 1L;    /**     *      */    private Integer id ;    /**     * 品牌id     */    private Integer brandId ;    /**     * 品牌名字     */    private String brandName ;    /**     * 机型id     */    private Integer modelId ;    /**     * 机型名字     */    private String modelName ;    /**     * 物料名字     */    private String materielName ;    /**     * get:     */    public Integer getId(){        return this.id;    }    /**     * set：     */    public void setId(Integer id){        this.id=id;    }    /**     * get:品牌id     */    public Integer getBrandId(){        return this.brandId;    }    /**     * set：品牌id     */    public void setBrandId(Integer brandId){        this.brandId=brandId;    }    /**     * get:品牌名字     */    public String getBrandName(){        return this.brandName;    }    /**     * set：品牌名字     */    public void setBrandName(String brandName){        this.brandName=brandName;    }    /**     * get:机型id     */    public Integer getModelId(){        return this.modelId;    }    /**     * set：机型id     */    public void setModelId(Integer modelId){        this.modelId=modelId;    }    /**     * get:机型名字     */    public String getModelName(){        return this.modelName;    }    /**     * set：机型名字     */    public void setModelName(String modelName){        this.modelName=modelName;    }    /**     * get:物料名字     */    public String getMaterielName(){        return this.materielName;    }    /**     * set：物料名字     */    public void setMaterielName(String materielName){        this.materielName=materielName;    }}