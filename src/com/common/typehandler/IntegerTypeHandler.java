package com.common.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * Integer类型装换.
 * 
 * @author: lijx
 * @CreateDate: 2016-8-30 上午9:47:33
 * @version: V 1.0
 */
public class IntegerTypeHandler implements TypeHandler<Integer> {

    /* (non-Javadoc)
     * @see org.apache.ibatis.type.TypeHandler#setParameter(java.sql.PreparedStatement, int, java.lang.Object, org.apache.ibatis.type.JdbcType)
     */
    @Override
    public void setParameter(PreparedStatement ps, int i, Integer parameter,
            JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter);
    }

    /* (non-Javadoc)
     * @see org.apache.ibatis.type.TypeHandler#getResult(java.sql.ResultSet, java.lang.String)
     */
    @Override
    public Integer getResult(ResultSet rs, String columnName)
            throws SQLException {
        return rs.getInt(columnName);
    }

    /* (non-Javadoc)
     * @see org.apache.ibatis.type.TypeHandler#getResult(java.sql.ResultSet, int)
     */
    @Override
    public Integer getResult(ResultSet rs, int columnIndex) throws SQLException {
        System.out.println(2);
        return rs.getInt(columnIndex);
    }

    /* (non-Javadoc)
     * @see org.apache.ibatis.type.TypeHandler#getResult(java.sql.CallableStatement, int)
     */
    @Override
    public Integer getResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        return cs.getInt(columnIndex);
    }
}
