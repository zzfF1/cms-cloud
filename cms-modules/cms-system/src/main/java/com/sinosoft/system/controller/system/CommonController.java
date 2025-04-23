package com.sinosoft.system.controller.system;

import cn.hutool.core.lang.tree.Tree;
import com.sinosoft.common.domain.dto.FieldOptionDTO;
import com.sinosoft.common.domain.dto.QueryFieldDTO;
import com.sinosoft.common.schema.common.domain.SysPageConfig;
import com.sinosoft.common.schema.common.domain.SysPageConfigQuery;
import com.sinosoft.common.schema.common.domain.vo.*;
import com.sinosoft.common.service.IBusinessHelpDocsService;
import com.sinosoft.common.service.ICmsCommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.core.domain.R;
import com.sinosoft.common.core.utils.DateUtils;
import com.sinosoft.common.domain.bo.AgentQueryBo;
import com.sinosoft.common.domain.bo.BranchGroupQueryBo;
import com.sinosoft.common.domain.bo.LaComQueryBo;
import com.sinosoft.common.domain.bo.LdComQueryBo;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.common.satoken.utils.LoginHelper;
import com.sinosoft.common.schema.agent.domain.bo.LaAgentGradeBo;
import com.sinosoft.common.schema.commission.domain.Lmriskapp;
import com.sinosoft.common.schema.common.domain.LaQualifyCode;
import com.sinosoft.common.schema.team.domain.vo.BranchGroupShowVo;
import com.sinosoft.common.web.core.BaseController;
import com.sinosoft.system.service.ICommonService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 公共接口
 *
 * @author zzf
 * @date 2023-06-30
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/buscommon")
public class CommonController extends BaseController {

    private final ICommonService commonService;
    private final ICmsCommonService cmsCommonService;
    private final IBusinessHelpDocsService businessHelpDocsService;
//    private final ISysAttachmentBusinessService sysAttachmentBusinessService;

    /**
     * 销售机构查询
     *
     * @param bo        查询对象
     * @param pageQuery 流程类型
     * @return 流程轨迹
     */
    @GetMapping("/branchlist")
    public TableDataInfo<BranchGroupShowVo> branchlist(BranchGroupQueryBo bo, PageQuery pageQuery) {
        return commonService.queryBranchPageList(bo, pageQuery);
    }

    /**
     * 查询流程轨迹
     *
     * @param dataId     业务数据id
     * @param lcSerialNo 流程类型
     * @return 流程轨迹
     */
    @GetMapping("/lcproclist")
    public TableDataInfo<LcProcessShowVo> lcproclist(String dataId, Integer lcSerialNo) {
        return TableDataInfo.build(commonService.queryProcess(dataId, lcSerialNo));
    }

    /**
     * 加载页面配置
     *
     * @param pageCode 业务代码
     * @return 流程轨迹
     */
    @GetMapping("/pageTabConfig")
    public R<List<SysPageConfigTabVo>> pageTabConfig(String pageCode) {
        return R.ok(commonService.queryPageTableConfig(pageCode, LoginHelper.getUserId()));
    }

    /**
     * 保存用户表格列配置
     *
     * @param params 参数对象
     * @return 结果
     */
    @PostMapping("/saveTableConfig")
    public R<Boolean> saveUserTableConfig(@RequestBody Map<String, Object> params) {
        String pageCode = (String) params.get("pageCode");
        if (StringUtils.isEmpty(pageCode)) {
            return R.fail("页面编码不能为空");
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> columns = (List<Map<String, Object>>) params.get("columns");
        if (columns == null || columns.isEmpty()) {
            return R.fail("列配置不能为空");
        }
        List<UserTableColumnConfigVo> configs = new ArrayList<>();
        for (Map<String, Object> column : columns) {
            UserTableColumnConfigVo config = new UserTableColumnConfigVo();
            config.setPageTableId(Long.valueOf(String.valueOf(column.get("id"))));
            config.setProp((String) column.get("prop"));
            config.setWidth(column.get("width") != null ? String.valueOf(column.get("width")) : null);
            config.setVisible((Boolean) column.get("visible"));
            configs.add(config);
        }

        return R.ok(commonService.saveUserTableColumnConfigs(pageCode, configs, LoginHelper.getUserId()));
    }

    /**
     * 查询管理机构
     *
     * @param bo 管理机构查询条件
     * @return 管理机构
     */
    @PostMapping("/queryComLabel")
    public R<List<LabelShowVo>> queryComLabel(LdComQueryBo bo) {
        //设置当前用户管理机构
        bo.setCurUserManageCom(LoginHelper.getDeptId() + "");
        List<LabelShowVo> dataList = commonService.queryComLabel(bo);
        return R.ok(dataList);
    }

    @GetMapping("/queryQualifyLabel")
    public R<List<LabelShowVo>> queryQualifyLabel(LaQualifyCode bo) {
        List<LabelShowVo> dataList = commonService.queryQualifyLabel(bo);
        return R.ok(dataList);
    }

    /**
     * 查询代理机构标签
     *
     * @param bo 查询条件
     * @return 代理机构标签
     */
    @PostMapping("/queryLaComLabel")
    public R<List<LabelShowVo>> queryLaComLabel(LaComQueryBo bo) {
        //设置当前用户管理机构
        List<LabelShowVo> dataList = commonService.queryLaComLabel(bo);
        return R.ok(dataList);
    }

    /**
     * 查询所选管理机构的上级管理机构
     *
     * @param bo 查询条件
     * @return 管理机构
     */
    @GetMapping("/queryUpLaComLabel")
    public R<List<LabelShowVo>> queryUpLaComLabel(LaComQueryBo bo) {
        List<LabelShowVo> dataList = commonService.queryUpLaComLabel(bo);
        return R.ok(dataList);
    }

    @GetMapping("/queryGradeLabel/{branchType}")
    public R<List<LabelShowVo>> queryGradeLabel(@PathVariable("branchType") String branchType) {
        LaAgentGradeBo bo = new LaAgentGradeBo();
        //设置当前用户管理机构() 团险新客户数配置职级下拉
        bo.setBranchType(branchType);
        List<LabelShowVo> dataList = commonService.queryLaAgentGradeLabel();
        return R.ok(dataList);
    }

    /**
     * 查询流程label
     *
     * @param lcSerialno 流程类型
     * @return 管理机构
     */
    @PostMapping("/queryLcLabel")
    public R<List<LabelShowVo>> queryLcLabel(Integer lcSerialno) {
        List<LabelShowVo> dataList = commonService.queryLcLabel(lcSerialno);
        return R.ok(dataList);
    }

    /**
     * 查询帮忙文档
     *
     * @param busCode 业务编码
     * @return 帮忙文档
     */
    @GetMapping("/queryHelpDoc")
    public R<HelpDocShowVo> queryHelpDoc(String busCode) {
        return R.ok(businessHelpDocsService.queryContent(busCode));
    }

    /**
     * 查询管理机构
     *
     * @param agnetQuery 管理机构查询条件
     * @return 管理机构
     */
    @GetMapping("/queryAgentLabel")
    public R<List<LabelShowVo>> queryAgentLabel(AgentQueryBo agnetQuery) {
        List<LabelShowVo> dataList = commonService.queryAgentLabel(agnetQuery);
        return R.ok(dataList);
    }

    /**
     * @param queryBo 查询条件
     * @return 管理机构树
     */
    @GetMapping("/manageTree")
    public R<List<Tree<String>>> manageTree(LdComQueryBo queryBo) {
        return R.ok(commonService.selectManageTreeList(queryBo));
    }

    /**
     * 查询险种编码
     *
     * @param lmriskapp 管理机构查询条件
     * @return 管理机构
     */
    @PostMapping("/queryRiskCode")
    public R<List<LabelShowVo>> queryRiskCode(Lmriskapp lmriskapp) {
        List<LabelShowVo> dataList = commonService.queryRiskCode(lmriskapp);
        return R.ok(dataList);
    }

    /**
     * 获取服务器日期
     * 格式yyyy-MM-dd
     *
     * @return 当前服务器日期
     */
    @GetMapping("/getDate")
    public R<String> getServiceDate() {
        return R.ok(DateUtils.getDate());
    }

    /**
     * 获取查询字段配置
     *
     * @param pageCode 页面代码
     * @return 查询字段配置
     */
    @GetMapping("/queryFields")
    public R<List<QueryFieldDTO>> queryFields(String pageCode) {
        SysPageConfig sysPageConfig = cmsCommonService.selectPageConfigByCode(pageCode);
        if (sysPageConfig == null) {
            return R.fail("页面配置不存在");
        }

        // 获取查询配置
        List<SysPageConfigQuery> queries = cmsCommonService.selectPageConfigQueryByPageId(sysPageConfig.getId());

        // 转换为前端需要的格式
        List<QueryFieldDTO> fieldConfigs = queries.stream()
            .filter(q -> "3".equals(q.getType()))
            .map(this::convertToFieldConfig)
            .collect(Collectors.toList());

        return R.ok(fieldConfigs);
    }

    /**
     * 获取字段选项数据
     *
     * @param fieldType 字段类型
     * @param fieldId   字段标识
     * @return 字段选项数据
     */
    @GetMapping("/fieldOptions")
    public R<List<FieldOptionDTO>> fieldOptions(String fieldType, String fieldId) {
        // 根据字段类型和ID获取选项数据
        List<FieldOptionDTO> options = new ArrayList<>();
        return R.ok(options);
    }

    /**
     * 将配置对象转换为字段配置DTO
     */
    private QueryFieldDTO convertToFieldConfig(SysPageConfigQuery query) {
        QueryFieldDTO config = new QueryFieldDTO();
        config.setLabel(query.getRemark());
        config.setValue(query.getAlias());

        // 使用配置的组件类型，如果为空则根据字段类型推断
        String componentType = query.getComponentType();
        if (StringUtils.isEmpty(componentType)) {
            componentType = getComponentTypeByFieldType(query.getFieldType(), query.getSpecialCode());
        }
        config.setType(componentType);  // 这里应该使用 componentType，而不是其他值

        // 设置数据源信息
        if ("select".equals(componentType) || "multiSelect".equals(componentType)) {
            config.setDataSource(query.getDataSource());
            config.setDictType(query.getDictType());  // 这里应该使用 dictType，而不是其他值
            config.setBeanName(query.getBeanName());
            config.setDependencyField(query.getDependencyField());
        }
        // 设置占位提示文字
        config.setPlaceholder(query.getPlaceholder());
        // 设置操作符
        config.setOperators(getOperatorsByComponentType(componentType));
        return config;
    }

    /**
     * 根据字段类型和特殊代码获取组件类型
     */
    private String getComponentType(String fieldType, String specialCode) {
        if (specialCode != null && !specialCode.isEmpty()) {
            if (specialCode.equals("BRANCH_ATTR") || specialCode.equals("DATA_MANAGE_LIKE")) {
                return "popup";
            }
        }

        switch (fieldType) {
            case "STR":
                return "text";
            case "INT":
            case "LONG":
            case "DOUBLE":
                return "number";
            case "DATE":
                return "date";
            default:
                return "text";
        }
    }

    /**
     * 创建选项对象
     */
    private FieldOptionDTO createOption(String value, String label) {
        FieldOptionDTO option = new FieldOptionDTO();
        option.setLabel(label);
        option.setValue(value);
        return option;
    }

    /**
     * 根据组件类型获取操作符列表
     *
     * @param componentType 组件类型
     * @return 操作符列表
     */
    private List<String> getOperatorsByComponentType(String componentType) {
        switch (componentType) {
            case "input":
            case "text":
                return List.of("=", "!=", "LIKE", "LIKE_LEFT", "IS_NULL", "IS_NOT_NULL");
            case "number":
                return List.of("=", "!=", ">", "<", ">=", "<=", "BETWEEN", "IS_NULL", "IS_NOT_NULL");
            case "date":
                return List.of("=", ">", "<", "BETWEEN", "IS_NULL", "IS_NOT_NULL");
            case "select":
            case "multiSelect":
                return List.of("=", "!=", "IN", "IS_NULL", "IS_NOT_NULL");
            case "popup":
            case "tree": // 添加tree类型的操作符
                return List.of("=", "!=", "LIKE_RIGHT", "IS_NULL", "IS_NOT_NULL");
            default:
                return List.of("=", "!=");
        }
    }

    /**
     * 根据字段类型和特殊代码获取组件类型
     *
     * @param fieldType   字段类型
     * @param specialCode 特殊代码
     * @return 组件类型
     */
    private String getComponentTypeByFieldType(String fieldType, String specialCode) {
        if (specialCode != null && !specialCode.isEmpty()) {
            if (specialCode.equals("BRANCH_ATTR") || specialCode.equals("DATA_MANAGE_LIKE")) {
                return "popup";
            }
            if (specialCode.equals("DICT")) {
                return "select";
            }
        }

        switch (fieldType) {
            case "STR":
                return "text";
            case "INT":
            case "LONG":
            case "DOUBLE":
                return "number";
            case "DATE":
                return "date";
            default:
                return "text";
        }
    }
}
