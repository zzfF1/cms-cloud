package com.sinosoft.common.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 保存框架Mapper接口
 */
public interface SaveFrameworkMapper extends BaseMapper {

    /**
     * @param tableName
     * @param queryWrapper
     * @return
     */
    //    @Select("<script> ${sql} </script>")
    @Select("SELECT 'Y' FROM ${tableName} ${ew.customSqlSegment} limit 1")
    String checkDataRepeat(@Param("tableName") String tableName, @Param("ew") Wrapper queryWrapper);

    /**
     * @param dictType
     * @param dictVal
     * @return
     */
    @Select("select 'Y' from sys_dict_data where dict_type=#{dictType} and dict_value=#{dictVal} limit 1")
    String existDictVal(@Param("dictType") String dictType, @Param("dictVal") String dictVal);

    /**
     * 查询业务数据
     *
     * @param fields       查询字段
     * @param tables       查询表
     * @param queryWrapper 条件
     * @return 数据
     */
    @Select(value = "select ${fields} from ${tables} ${ew.customSqlSegment}")
    List<Map<String, String>> queryDataList(@Param("fields") String fields, @Param("tables") String tables, @Param("ew") Wrapper queryWrapper);

    /**
     * 查询数据基础字段(risk_code,sign_date)
     *
     * @param tables       数据表
     * @param queryWrapper 条件
     * @return 基础条件结果
     */
    @Select(value = "select commision_sn,risk_code from ${tables} ${ew.customSqlSegment}")
    List<Map<String, String>> queryDataBaseFieldList(@Param("tables") String tables, @Param("ew") Wrapper queryWrapper);

    /**
     * 查询匹配到条件的数据
     *
     * @param tables       数据表
     * @param queryWrapper 条件
     * @return 匹配的主键
     */
    @Select(value = "select commision_sn from ${tables} ${ew.customSqlSegment}")
    List<String> queryMatchDataList(@Param("tables") String tables, @Param("ew") Wrapper queryWrapper);

}
