package com.sinosoft.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.convert.Convert;
import com.sinosoft.common.core.utils.MapstructUtils;
import com.sinosoft.common.log.enums.EventType;
import com.sinosoft.system.domain.bo.SysDeptLdcomBo;
import com.sinosoft.system.domain.bo.SysDeptQuery;
import com.sinosoft.system.domain.vo.SysDeptLdcomVo;
import lombok.RequiredArgsConstructor;
import com.sinosoft.common.core.constant.SystemConstants;
import com.sinosoft.common.core.domain.R;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.log.annotation.Log;
import com.sinosoft.common.log.enums.BusinessType;
import com.sinosoft.common.web.core.BaseController;
import com.sinosoft.system.domain.bo.SysDeptBo;
import com.sinosoft.system.domain.vo.SysDeptVo;
import com.sinosoft.system.service.ISysDeptService;
import com.sinosoft.system.service.ISysPostService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 机构信息
 *
 * @author zzf
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dept")
public class SysDeptController extends BaseController {

    private final ISysDeptService deptService;
    private final ISysPostService postService;

    /**
     * 获取机构列表
     */
    @SaCheckPermission("system:dept:list")
    @GetMapping("/list")
    public R<List<SysDeptVo>> list(SysDeptQuery dept) {
        List<SysDeptVo> depts = deptService.selectDeptList(dept);
        return R.ok(depts);
    }

    /**
     * 查询机构列表（排除节点）
     *
     * @param deptId 机构ID
     */
    @SaCheckPermission("system:dept:list")
    @GetMapping("/list/exclude/{deptId}")
    public R<List<SysDeptVo>> excludeChild(@PathVariable(value = "deptId", required = false) Long deptId) {
        List<SysDeptVo> depts = deptService.selectDeptList(new SysDeptQuery());
        depts.removeIf(d -> d.getDeptId().equals(deptId)
            || StringUtils.splitList(d.getAncestors()).contains(Convert.toStr(deptId)));
        return R.ok(depts);
    }

    /**
     * 根据机构编号获取详细信息
     *
     * @param deptId 机构ID
     */
    @SaCheckPermission("system:dept:query")
    @GetMapping(value = "/{deptId}")
    public R<SysDeptVo> getInfo(@PathVariable Long deptId) {
        deptService.checkDeptDataScope(deptId);
        return R.ok(deptService.selectDeptById(deptId));
    }

    /**
     * 新增机构
     */
    @SaCheckPermission("system:dept:add")
    @Log(title = "机构管理", businessType = BusinessType.INSERT, eventType = EventType.system)
    @PostMapping("/add")
    public R<Void> add(@Validated @RequestBody SysDeptBo dept) {
        if (!deptService.checkDeptNameUnique(dept)) {
            return R.fail("新增机构'" + dept.getDeptName() + "'失败，机构名称已存在");
        }
        return toAjax(deptService.insertDept(dept));
    }

    /**
     * 修改机构
     */
    @SaCheckPermission("system:dept:edit")
    @Log(title = "机构管理", businessType = BusinessType.UPDATE, eventType = EventType.system)
    @PostMapping("/edit")
    public R<Void> edit(@Validated @RequestBody SysDeptBo dept) {
        Long deptId = dept.getDeptId();
        deptService.checkDeptDataScope(deptId);
        if (!deptService.checkDeptNameUnique(dept)) {
            return R.fail("修改机构'" + dept.getDeptName() + "'失败，机构名称已存在");
        } else if (dept.getParentId().equals(deptId)) {
            return R.fail("修改机构'" + dept.getDeptName() + "'失败，上级机构不能是自己");
        } else if (StringUtils.equals(SystemConstants.DISABLE, dept.getStatus())) {
            if (deptService.selectNormalChildrenDeptById(deptId) > 0) {
                return R.fail("该机构包含未停用的子机构!");
            } else if (deptService.checkDeptExistUser(deptId)) {
                return R.fail("该机构下存在已分配用户，不能禁用!");
            }
        }
        return toAjax(deptService.updateDept(dept));
    }

    /**
     * 删除机构
     *
     * @param deptId 机构ID
     */
    @SaCheckPermission("system:dept:remove")
    @Log(title = "机构管理", businessType = BusinessType.DELETE, eventType = EventType.system)
    @PostMapping("/remove/{deptId}")
    public R<Void> remove(@PathVariable Long deptId) {
        if (deptService.hasChildByDeptId(deptId)) {
            return R.warn("存在下级机构,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId)) {
            return R.warn("机构存在用户,不允许删除");
        }
        if (postService.countPostByDeptId(deptId) > 0) {
            return R.warn("机构存在岗位,不允许删除");
        }
        deptService.checkDeptDataScope(deptId);
        return toAjax(deptService.deleteDeptById(deptId));
    }

    /**
     * 获取机构选择框列表
     *
     * @param deptIds 机构ID串
     */
    @SaCheckPermission("system:dept:query")
    @GetMapping("/optionselect")
    public R<List<SysDeptVo>> optionselect(@RequestParam(required = false) Long[] deptIds) {
        return R.ok(deptService.selectDeptByIds(deptIds == null ? null : List.of(deptIds)));
    }

    /**
     * 获取机构列表（集成Ldcom）
     */
    @SaCheckPermission("system:dept:list")
    @GetMapping("/ldcom/list")
    public R<List<SysDeptLdcomVo>> ldcomList(SysDeptQuery dept) {
        List<SysDeptLdcomVo> depts = deptService.selectDeptLdcomList(dept);
        return R.ok(depts);
    }

    /**
     * 根据机构编号获取详细信息（集成Ldcom）
     *
     * @param deptId 机构ID
     */
    @SaCheckPermission("system:dept:query")
    @GetMapping(value = "/ldcom/{deptId}")
    public R<SysDeptLdcomVo> getLdcomInfo(@PathVariable Long deptId) {
        deptService.checkDeptDataScope(deptId);
        return R.ok(deptService.selectDeptLdcomById(deptId));
    }

    /**
     * 新增机构（集成Ldcom）
     */
    @SaCheckPermission("system:dept:add")
    @Log(title = "机构管理", businessType = BusinessType.INSERT, eventType = EventType.system)
    @PostMapping("/ldcom/add")
    public R<Void> addLdcom(@Validated @RequestBody SysDeptLdcomBo dept) {
        if (!deptService.checkDeptNameUnique(MapstructUtils.convert(dept, SysDeptBo.class))) {
            return R.fail("新增机构'" + dept.getDeptName() + "'失败，机构名称已存在");
        }
        return toAjax(deptService.insertDeptLdcom(dept));
    }

    /**
     * 修改机构（集成Ldcom）
     */
    @SaCheckPermission("system:dept:edit")
    @Log(title = "机构管理", businessType = BusinessType.UPDATE, eventType = EventType.system)
    @PostMapping("/ldcom/edit")
    public R<Void> editLdcom(@Validated @RequestBody SysDeptLdcomBo dept) {
        Long deptId = dept.getDeptId();
        deptService.checkDeptDataScope(deptId);
        if (!deptService.checkDeptNameUnique(MapstructUtils.convert(dept, SysDeptBo.class))) {
            return R.fail("修改机构'" + dept.getDeptName() + "'失败，机构名称已存在");
        } else if (dept.getParentId().equals(deptId)) {
            return R.fail("修改机构'" + dept.getDeptName() + "'失败，上级机构不能是自己");
        } else if (StringUtils.equals(SystemConstants.DISABLE, dept.getStatus())) {
            if (deptService.selectNormalChildrenDeptById(deptId) > 0) {
                return R.fail("该机构包含未停用的子机构!");
            } else if (deptService.checkDeptExistUser(deptId)) {
                return R.fail("该机构下存在已分配用户，不能禁用!");
            }
        }
        return toAjax(deptService.updateDeptLdcom(dept));
    }

}
