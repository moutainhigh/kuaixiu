<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${ModelPath}.${ProjectName}.dao.${ModelName}Mapper" >
    <!-- Result Map-->
    <resultMap id="BaseResultMap" type="${ModelPath}.${ProjectName}.entity.${ModelName}" >
    <#list columnList as column>
        <result column="${column.Field}" property="${nameFmt(column.Field)}"/>
    </#list>
    </resultMap>
    
    <!-- ${TableName} table all fields -->
    <sql id="Base_Column_List" >
        <#list columnList as column><#if column_index!=0>,</#if><#if column.Jtype=="java.util.Date">date_format(`${column.Field}`,'%Y-%m-%d %H:%i:%s')</#if>${column.Field}</#list>
    </sql>

    <!-- 插入记录 -->
    <insert id="add" parameterType="Object" >
        insert into ${TableName}
        (<#list columnList as column><#if column_index!=0>,</#if>${column.Field}</#list>)
        values
        (<#list columnList as column><#if column_index!=0>,</#if>${r"#{"}${nameFmt(column.Field)}${r"}"}</#list>)
    </insert>

    <!-- 根据id，修改记录-->
    <update id="update" parameterType="Object" >
        update ${TableName} 
        set <#list columnList as column><#if column_index!=0>,</#if>
            ${column.Field}=${r"#{"}${nameFmt(column.Field)}${"}"}</#list>
        where id=${r"#{id}"}
    </update>

    <!-- 删除记录 -->
    <delete id="delete" parameterType="Object">
        delete from ${TableName} where id = ${r"#{id}"}
    </delete>
    
    <!-- 根据id查询  -->
    <select id="queryById"  resultMap="BaseResultMap" parameterType="Object">
        select <include refid="Base_Column_List" />
        from ${TableName} where id = ${r"#{id}"}
    </select>

    <!-- 查询列表无分页 -->
    <select id="queryList" resultMap="BaseResultMap"  parameterType="Object">
        select
        <include refid="Base_Column_List"/>
        from ${TableName}
        where 1=1
        <#list columnList as column>
        <if test="${nameFmt(column.Field)} != null and ${nameFmt(column.Field)} != ''" >
            and ${column.Field} = ${r"#{"}${nameFmt(column.Field)}${"}"}
        </if>
        </#list>
    </select>
    
    <!-- 查询列表 带分页 -->
    <select id="queryListForPage" resultMap="BaseResultMap"  parameterType="Object">
        select
        <include refid="Base_Column_List"/>
        from ${TableName}
        where 1=1
        <#list columnList as column>
        <if test="${nameFmt(column.Field)} != null and ${nameFmt(column.Field)} != ''" >
            and ${column.Field} = ${r"#{"}${nameFmt(column.Field)}${"}"}
        </if>
        </#list>
    </select>

</mapper>   