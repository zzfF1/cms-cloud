package com.sinosoft.system.mapper;

import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.system.domain.SysUserPwdExpire;
import com.sinosoft.system.domain.SysUserPwdHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 过期 数据层
 *
 * @author zzf
 */
public interface SysUserPwdHistoryMapper extends BaseMapperPlus<SysUserPwdHistory, SysUserPwdHistory> {
    /**
     * 查询用户最近N次的密码历史
     *
     * @param userId 用户ID
     * @param limit  限制数量
     * @return 密码历史列表
     */
    List<SysUserPwdHistory> selectRecentPasswordHistory(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 删除超出保留数量的历史记录
     *
     * @param userId    用户ID
     * @param keepCount 保留数量
     * @return 影响行数
     */
    int deleteExcessHistory(@Param("userId") Long userId, @Param("keepCount") int keepCount);
}
