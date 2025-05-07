package com.sinosoft.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sinosoft.common.mybatis.annotation.DataColumn;
import com.sinosoft.common.mybatis.annotation.DataPermission;
import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.system.domain.SysUser;
import com.sinosoft.system.domain.SysUserPwdExpire;
import com.sinosoft.system.domain.vo.SysUserExportVo;
import com.sinosoft.system.domain.vo.SysUserVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 过期 数据层
 *
 * @author zzf
 */
public interface SysUserPwdExpireMapper extends BaseMapperPlus<SysUserPwdExpire, SysUserPwdExpire> {
    /**
     * 查询所有过期的密码记录
     *
     * @param currentTime 当前时间
     * @return 过期记录列表
     */
    List<SysUserPwdExpire> selectExpiredPasswords(@Param("currentTime") Date currentTime);

    /**
     * 查询即将过期的密码记录
     *
     * @param currentTime 当前时间
     * @param warningTime 警告时间
     * @return 即将过期记录列表
     */
    List<SysUserPwdExpire> selectSoonToExpirePasswords(@Param("currentTime") Date currentTime,
                                                       @Param("warningTime") Date warningTime);

    /**
     * 批量更新过期状态
     *
     * @param userIds    用户ID列表
     * @param updateTime 更新时间
     * @return 影响行数
     */
    int batchUpdateExpiredStatus(@Param("userIds") List<Long> userIds,
                                 @Param("updateTime") Date updateTime);

    /**
     * 批量更新警告标志
     *
     * @param userIds     用户ID列表
     * @param warningTime 警告时间
     * @param updateTime  更新时间
     * @return 影响行数
     */
    int batchUpdateWarningFlag(@Param("userIds") List<Long> userIds,
                               @Param("warningTime") Date warningTime,
                               @Param("updateTime") Date updateTime);
}
