package com.sinosoft.notice.mapper;


import com.sinosoft.common.mybatis.core.mapper.BaseMapperPlus;
import com.sinosoft.notice.domain.SysNotificationMerge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知合并详情Mapper接口
 *
 * @author zzf
 * @date 2025-03-07
 */
public interface SysNotificationMergeMapper extends BaseMapperPlus<SysNotificationMerge, SysNotificationMerge> {

    /**
     * 根据父通知ID查询合并详情
     *
     * @param parentId 父通知ID
     * @return 合并详情列表
     */
    List<SysNotificationMerge> selectByParentId(
        @Param("parentId") Long parentId);

    /**
     * 根据父通知ID和子通知ID查询合并详情
     *
     * @param parentId 父通知ID
     * @param childId  子通知ID
     * @return 合并详情对象
     */
    SysNotificationMerge selectByParentAndChildId(
        @Param("parentId") Long parentId,
        @Param("childId") Long childId);

    /**
     * 根据子通知ID查询合并详情
     *
     * @param childId 子通知ID
     * @return 合并详情对象
     */
    SysNotificationMerge selectByChildId(
        @Param("childId") Long childId);
}
