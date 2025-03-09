package com.sinosoft.common.mybatis.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sinosoft.common.mybatis.core.domain.Ldmaxno;
import org.apache.ibatis.annotations.Select;

/**
 * 流水号生成工具类 依赖于数据库底层函数<br/>
 * 非业务性要求 尽量不要使用该类来生成流水号
 */
public interface LDMaxNoMapper extends BaseMapper<Ldmaxno> {

    @Select("select CreateMaxNo(#{param1},#{param2})")
    public long findMaxNo(String notype, String nolimit);

    @Select("select CreateMaxNostep(#{param1},#{param2},#{param3})")
    public long findMaxNoStep(String notype, String nolimit, int step);
}
