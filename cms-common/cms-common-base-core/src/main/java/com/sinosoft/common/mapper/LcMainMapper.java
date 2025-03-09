package com.sinosoft.common.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.common.domain.LcMain;

import java.util.List;
import java.util.Map;

/**
 * 流程类型定义Mapper接口
 *
 * @author zzf
 * @date 2023-07-02
 */
public interface LcMainMapper extends BaseMapperPlus<LcMain, LcMain> {

    /**
     * 查询数据的流程节点
     *
     * @param sql 查询SQL
     * @return 流程节点集合
     */
    @Select("<script> ${sql} </script>")
    List<String> getDataCurLcId(@Param("sql") String sql);

    /**
     * 流程执行查询SQL
     *
     * @param sql SQL语句
     * @return 查询结果
     */
    @Select("<script> ${sql} </script>")
    List<String> execSqlSelect(@Param("sql") String sql);

    /**
     * @param sql SQL语句
     * @return 执行返回条数
     */
    @Update("<script> ${sql} </script>")
    Integer execUpdate(@Param("sql") String sql);

    /**
     * 修改数据
     *
     * @param tableName    表名
     * @param fieldsVal    修改字段结果
     * @param queryWrapper 条件
     * @return 修改条数
     */
    @Update("update ${tableName} set ${fieldsVal}  ${ew.customSqlSegment} ")
    Integer execUpdateByWrapper(@Param("tableName") String tableName, @Param("fieldsVal") String fieldsVal, @Param("ew") Wrapper queryWrapper);

    /**
     * 执行SQL查询，返回Map结果
     *
     * @param sql SQL语句
     * @return 查询结果
     */
    @Select("<script> ${sql} </script>")
    List<Map<String, Object>> execSqlSelectMap(@Param("sql") String sql);
}
