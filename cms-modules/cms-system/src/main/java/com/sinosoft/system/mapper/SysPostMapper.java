package com.sinosoft.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.sinosoft.common.mybatis.annotation.DataColumn;
import com.sinosoft.common.mybatis.annotation.DataPermission;
import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.system.domain.SysPost;
import com.sinosoft.system.domain.vo.SysPostVo;

import java.util.List;

/**
 * 岗位信息 数据层
 *
 * @author zzf
 */
public interface SysPostMapper extends BaseMapperPlus<SysPost, SysPostVo> {

    @DataPermission({
        @DataColumn(key = "deptName", value = "dept_id"),
        @DataColumn(key = "userName", value = "create_by")
    })
    Page<SysPostVo> selectPagePostList(@Param("page") Page<SysPostVo> page, @Param(Constants.WRAPPER) Wrapper<SysPost> queryWrapper);

    /**
     * 查询用户所属岗位组
     *
     * @param userId 用户ID
     * @return 结果
     */
    List<SysPostVo> selectPostsByUserId(Long userId);

}
