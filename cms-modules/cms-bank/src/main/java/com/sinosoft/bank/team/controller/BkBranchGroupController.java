package com.sinosoft.bank.team.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import com.sinosoft.common.core.domain.R;
import com.sinosoft.common.idempotent.annotation.RepeatSubmit;
import com.sinosoft.common.log.annotation.Log;
import com.sinosoft.common.log.enums.BusinessType;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.common.satoken.utils.LoginHelper;
import com.sinosoft.common.web.core.BaseController;
import com.sinosoft.common.domain.ConditionBase;
import com.sinosoft.common.schema.common.domain.vo.LabelShowVo;
import com.sinosoft.common.excel.SysExcelHandler;
import com.sinosoft.bank.team.domain.bo.BkBranchGroupFormBo;
import com.sinosoft.bank.team.domain.bo.BkBranchGroupQueryBo;
import com.sinosoft.bank.team.domain.vo.BkBranchGroupFormVo;
import com.sinosoft.bank.team.domain.vo.BkBranchGroupShowVo;
import com.sinosoft.bank.team.service.IBkBranchGroupService;
import com.sinosoft.common.schema.team.domain.Labranchgroup;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 银保销售机构管理
 *
 * @author zzf
 * @date 2023-06-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/bkbranch/branchGroup")
public class BkBranchGroupController extends BaseController {

    private final IBkBranchGroupService laBranchGroupService;
    private final SysExcelHandler sysExcelHandler;

    /**
     * 分页查询银保销售机构列表
     *
     * @param query 查询条件
     * @return 银保销售机构列表
     */
    @SaCheckPermission("bkbranch:branchGroup:list")
    @PostMapping("/list")
    public TableDataInfo<BkBranchGroupShowVo> list(@RequestBody ConditionBase query) {
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageSize(query.getPageSize());
        pageQuery.setPageNum(query.getPageNum());
        return laBranchGroupService.queryPageList(query, pageQuery, LoginHelper.getLoginUser());
    }

    /**
     * 销售机构导出
     *
     * @param query    查询条件
     * @param response 响应
     */
    @SaCheckPermission("bkbranch:branchGroup:export")
    @Log(title = "银保销售机构管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@RequestBody ConditionBase query, HttpServletResponse response) {
        sysExcelHandler.exportDataExcel("BANK_TEAM_EDIT", null, query, LoginHelper.getLoginUser(), response);
    }

    /**
     * 获取银保销售机构管理详细信息
     *
     * @param agentGroup 主键
     */
    @GetMapping("/{agentGroup}")
    public R<BkBranchGroupFormVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable String agentGroup) {
        return R.ok(laBranchGroupService.queryById(agentGroup));
    }

    /**
     * 新增银保销售机构管理
     *
     * @param bo 销售机构信息
     * @return 结果
     */
    @SaCheckPermission("bkbranch:branchGroup:add")
    @Log(title = "银保销售机构管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated @RequestBody BkBranchGroupFormBo bo) {
        laBranchGroupService.validEntityBeforeSave(bo, true);
        return toAjax(laBranchGroupService.insertByBo(bo, LoginHelper.getLoginUser()));
    }

    /**
     * 修改销售机构信息
     *
     * @param bo 销售机构信息
     * @return 结果
     */
    @SaCheckPermission("bkbranch:branchGroup:edit")
    @Log(title = "银保销售机构管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated @RequestBody BkBranchGroupFormBo bo) {
        laBranchGroupService.validEntityBeforeSave(bo, false);
        return toAjax(laBranchGroupService.updateByBo(bo, LoginHelper.getLoginUser()));
    }

    /**
     * 销售机构导入模板下载
     *
     * @param response 响应
     */
    @SaCheckPermission("bkbranch:branchGroup:download")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        sysExcelHandler.importTemple("BANK_BRANCH_IMPORT", null, response);
    }

    /**
     * 销售机构批量导入
     *
     * @param file 导入文件
     */
    @Log(title = "银保销售机构管理", businessType = BusinessType.IMPORT)
    @SaCheckPermission("bkbranch:branchGroup:import")
    @PostMapping(value = "/importData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Void> importData(@RequestPart("file") MultipartFile file) throws Exception {
        List<Labranchgroup> addList = new ArrayList<>();
        List<Labranchgroup> updateList = new ArrayList<>();
        laBranchGroupService.validBatchEntityBeforeSave(addList, updateList, file, LoginHelper.getLoginUser());
        laBranchGroupService.insertData(addList, updateList, LoginHelper.getLoginUser());
        return R.ok();
    }


    /**
     * 查询销售机构下拉数据
     *
     * @param queryBo 查询对象
     * @return 销售机构下拉数据
     */
    @PostMapping("/teamList")
    public R<List<LabelShowVo>> queryTeamList(BkBranchGroupQueryBo queryBo) {
        List<LabelShowVo> dataList = laBranchGroupService.queryBranchByLabel(queryBo);
        return R.ok(dataList);
    }

    /**
     * 查询销售机构级别下拉
     *
     * @return 销售机构级别下拉数据
     */
    @PostMapping("/queryBranchLevelLabel")
    public R<List<LabelShowVo>> queryBranchLevelLabel() {
        List<LabelShowVo> dataList = laBranchGroupService.queryBranchLevelLabel();
        return R.ok(dataList);
    }
}
