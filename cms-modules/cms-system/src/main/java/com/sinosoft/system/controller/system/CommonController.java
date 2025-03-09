package com.sinosoft.system.controller.system;

import cn.hutool.core.lang.tree.Tree;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.core.domain.R;
import com.sinosoft.common.core.utils.DateUtils;
import com.sinosoft.common.domain.bo.AgentQueryBo;
import com.sinosoft.common.domain.bo.BranchGroupQueryBo;
import com.sinosoft.common.domain.bo.LaComQueryBo;
import com.sinosoft.common.domain.bo.LdComQueryBo;
import com.sinosoft.common.domain.vo.LcProcessShowVo;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.common.satoken.utils.LoginHelper;
import com.sinosoft.common.schema.agent.domain.bo.LaAgentGradeBo;
import com.sinosoft.common.schema.commission.domain.Lmriskapp;
import com.sinosoft.common.schema.common.domain.LaQualifyCode;
import com.sinosoft.common.schema.common.domain.vo.LabelShowVo;
import com.sinosoft.common.schema.common.domain.vo.SysPageConfigTabVo;
import com.sinosoft.common.schema.team.domain.vo.BranchGroupShowVo;
import com.sinosoft.common.web.core.BaseController;
import com.sinosoft.system.service.ICommonService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
//    private final IBusinessHelpDocsService businessHelpDocsService;
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
        return R.ok(commonService.queryPageTableConfig(pageCode));
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

//    /**
//     * 查询帮忙文档
//     *
//     * @param busCode 业务编码
//     * @return 帮忙文档
//     */
//    @GetMapping("/queryHelpDoc")
//    public R<HelpDocShowVo> queryHelpDoc(String busCode) {
//        return R.ok(businessHelpDocsService.queryContent(busCode));
//    }

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

//    /**
//     * 上传文件
//     *
//     * @param files        文件
//     * @param busDataType  附件类型
//     * @param uid          附件唯一标识
//     * @param attId        附件id
//     * @param markedUpdate 更新标记
//     * @param busCode      附件业务代码
//     * @param dataId       业务数据id
//     * @param delAttIds    删除附件id
//     * @return 结果
//     * @throws IOException 异常
//     */
//    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public R<List<AttachFileVo>> upload(@RequestPart(value = "files", required = false) MultipartFile[] files, @RequestParam(value = "busDataType", required = false) String[] busDataType,
//                                        @RequestParam(value = "uid", required = false) String[] uid, @RequestParam(value = "attId", required = false) String[] attId,
//                                        @RequestParam(value = "markedUpdate", required = false) String[] markedUpdate, @RequestParam(value = "markedAdd", required = false) String[] markedAdd,
//                                        @RequestPart("busCode") String busCode, @RequestPart("dataId") String dataId, @RequestPart(value = "delAttIds", required = false) String delAttIds) throws IOException {
//        List<AttachFileBo> fileList = new ArrayList<>();
//        //循环上传文件
//        if (files != null && files.length > 0) {
//            for (int i = 0; i < files.length; i++) {
//                AttachFileBo attachFileBo = new AttachFileBo();
//                attachFileBo.setMarkedUpdate(Boolean.parseBoolean(markedUpdate[i]));
//                attachFileBo.setMarkedAdd(Boolean.parseBoolean(markedAdd[i]));
//                if(attachFileBo.isMarkedUpdate()){
//                    attachFileBo.setAttId(StringUtils.isNotBlank(attId[i]) ? Long.parseLong(attId[i]) : 0L);
//                }
//                attachFileBo.setUid(uid[i]);
//                attachFileBo.setBusCode(busCode);
//                attachFileBo.setBusDataType(busDataType[i]);
//                attachFileBo.setBusDataId(dataId);
//                attachFileBo.setFile(files[i]);
//                attachFileBo.setMarkedDel(false);
//                fileList.add(attachFileBo);
//            }
//        }
//        //删除附件
//        if (StringUtils.isNotBlank(delAttIds)) {
//            String[] delAttIdArr = delAttIds.split(",");
//            for (String delAttId : delAttIdArr) {
//                AttachFileBo attachFileBo = new AttachFileBo();
//                attachFileBo.setAttId(Long.parseLong(delAttId));
//                attachFileBo.setBusCode(busCode);
//                attachFileBo.setBusDataId(dataId);
//                attachFileBo.setMarkedDel(true);
//                attachFileBo.setMarkedUpdate(false);
//                attachFileBo.setMarkedAdd(false);
//                fileList.add(attachFileBo);
//            }
//        }
//        log.info("附件上传:{}", fileList.stream().toList().toString());
//        if (CollUtil.isNotEmpty(fileList)) {
//            List<AttachFileVo> attachFileVoList = sysAttachmentBusinessService.upload(fileList, LoginHelper.getLoginUser());
//            return R.ok(attachFileVoList);
//        }
//        return R.ok();
//    }
//
//    /**
//     * 附件下载
//     *
//     * @param response 响应
//     * @param attId    附件id
//     * @param busCode  业务编码
//     * @throws IOException 异常
//     */
//    @Log(title = "附件下载", businessType = BusinessType.DOWNLOAD)
//    @GetMapping("/download")
//    public void download(HttpServletResponse response, String attId, String busCode) throws IOException {
//        sysAttachmentBusinessService.download(Long.valueOf(attId), busCode, response);
//    }
//
//    /**
//     * 删除附件
//     *
//     * @param attIds  附件主键
//     * @param busCode 业务代码
//     * @return 结果
//     */
//    @Log(title = "附件删除", businessType = BusinessType.DELETE)
//    @DeleteMapping("/remove/{attIds}")
//    public R<Void> remove(@NotEmpty(message = "附件主键不能为空") @PathVariable Long[] attIds, @RequestParam(required = true) String busCode) {
//        return toAjax(sysAttachmentBusinessService.remove(List.of(attIds), busCode, LoginHelper.getLoginUser()));
//    }

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
}
