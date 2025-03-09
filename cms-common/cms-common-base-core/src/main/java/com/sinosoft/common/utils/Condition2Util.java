package com.sinosoft.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.system.api.model.LoginUser;
import com.sinosoft.common.core.exception.ServiceException;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.domain.ConditionBase;
import com.sinosoft.common.domain.CustomConditionVal;
import com.sinosoft.common.schema.common.domain.SysPageConfig;
import com.sinosoft.common.schema.common.domain.SysPageConfigQuery;
import com.sinosoft.common.enums.ConditionEnum;
import com.sinosoft.common.enums.FieldTypeEnum;
import com.sinosoft.common.service.ICmsCommonService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: cms6
 * @description: 自定义条件
 * @author: zzf
 * @create: 2024-06-24 18:43
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class Condition2Util {

    private final ICmsCommonService cmsCommonService;

    /**
     * 初始化查询条件
     *
     * @param pageCode     界面代码
     * @param query        查询条件
     * @param queryWrapper 查询对象
     * @param loginUser    登录用户
     */
    public void initQueryWrapper(String pageCode, ConditionBase query, QueryWrapper queryWrapper, LoginUser loginUser) {
        initQueryWrapper(pageCode, query, queryWrapper, loginUser, false);
    }

    /**
     * 初始化查询条件
     *
     * @param pageCode     界面代码
     * @param query        查询条件
     * @param queryWrapper 查询对象
     * @param loginUser    登录用户
     * @param bExport      true导出 false查询
     */
    @SuppressWarnings("unchecked")
    public void initQueryWrapper(String pageCode, ConditionBase query, QueryWrapper queryWrapper, LoginUser loginUser, boolean bExport) {
        SysPageConfig sysPageConfig = cmsCommonService.selectPageConfigByCode(pageCode);
        if (sysPageConfig == null) {
            log.warn("[{}]界面配置不存在", pageCode);
            throw new ServiceException("[" + pageCode + "]界面配置不存在");
        }

        // 查询界面查询配置
        List<SysPageConfigQuery> sysPageConfigQueries = cmsCommonService.selectPageConfigQueryByPageId(sysPageConfig.getId());
        //如果为空跳过
        if (sysPageConfigQueries.isEmpty()) {
            return;
        }
        //查询界面查询配置
        Map<String, CustomConditionVal> valMap = Optional.ofNullable(query.getConditions()).stream().flatMap(Collection::stream).filter(Objects::nonNull)
            .collect(Collectors.toMap(CustomConditionVal::getAlias, v -> v, (v1, v2) -> v1));
        //循环配置对象
        for (SysPageConfigQuery sysPageConfigQuery : sysPageConfigQueries) {
            String val = "";
            String fieldPrefix = sysPageConfigQuery.getFieldPrefix();
            String fieldName = sysPageConfigQuery.getFieldName();
            // 拼接字段前缀和字段名
            String fullFieldName = (fieldPrefix != null && !fieldPrefix.isEmpty()) ? fieldPrefix + "." + fieldName : fieldName;
            //如果是前台条件
            if ("0".equals(sysPageConfigQuery.getType())) {
                //如果不存在跳过
                if (!valMap.containsKey(sysPageConfigQuery.getAlias())) {
                    continue;
                }
                val = valMap.get(sysPageConfigQuery.getAlias()).getVal();
            } else if ("2".equals(sysPageConfigQuery.getType())) {
                //如果是导出则跳过
                if (bExport) {
                    continue;
                }
                //如果是排序
                if ("desc".equals(sysPageConfigQuery.getDefaultValue())) {
                    queryWrapper.orderByDesc(fullFieldName);
                } else {
                    queryWrapper.orderByAsc(fullFieldName);
                }
                continue;
            } else {
                val = sysPageConfigQuery.getDefaultValue();
            }
            //获取循环代码
            String specialCode = sysPageConfigQuery.getSpecialCode();
            ConditionEnum condOperator = ConditionEnum.fromString(sysPageConfigQuery.getCondOperator());
            FieldTypeEnum fieldType = FieldTypeEnum.fromString(sysPageConfigQuery.getFieldType());
            // 如果存在特殊代码不为空
            if (StringUtils.isNotBlank(specialCode)) {
                specialHandler(specialCode, queryWrapper, fullFieldName, fieldType, sysPageConfigQuery.getDefaultValue(), val, loginUser);
            } else {
                // 根据条件类型进行处理
                switch (condOperator) {
                    case IN:
                    case NOT_IN:
                        String[] values = val.split(",");
                        Object[] typedValues = fieldType.convertValues(values);
                        condOperator.apply(queryWrapper, fullFieldName, typedValues);
                        break;
                    default:
                        // 处理非集合情况
                        Object typedValue = fieldType.convertValue(val);
                        condOperator.apply(queryWrapper, fullFieldName, typedValue);
                        break;
                }
            }
        }
    }

    /**
     * 特殊处理
     *
     * @param specialCode   特殊代码
     * @param queryWrapper  查询条件
     * @param fullFieldName 字段名
     * @param fieldType     字段类型
     * @param defaultVal    默认值
     * @param val           值
     * @param loginUser     登录用户
     */
    @SuppressWarnings("unchecked")
    public void specialHandler(String specialCode, QueryWrapper queryWrapper, String fullFieldName, FieldTypeEnum fieldType, String defaultVal, String val, LoginUser loginUser) {
        switch (specialCode) {
            case "DATA_MANAGE_LIKE":
                //获取管理机构权限
                String manageCom = loginUser.getDeptId() + "";
                //如果不是管理机构权限
                if (!"86".equals(manageCom)) {
                    ConditionEnum.LIKE_RIGHT.apply(queryWrapper, fullFieldName, manageCom);
                }
                break;
            case "BRANCH_ATTR":
                //销售机构
                queryWrapper.inSql(fullFieldName, String.format("select branch11.agent_group from la_branch_group branch11 where branch11.branch_type = '%s' and branch11.branch_attr like '%s%%'", defaultVal, val));
                break;
            default:
                break;
        }
    }
}
