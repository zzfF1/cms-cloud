package com.sinosoft.bank.team.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sinosoft.common.sync.enums.OperationType;
import com.sinosoft.common.sync.util.DataSyncHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.system.api.model.LoginUser;
import com.sinosoft.common.core.exception.ServiceException;
import com.sinosoft.common.core.utils.DateUtils;
import com.sinosoft.common.core.utils.MapstructUtils;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.common.mybatis.service.LDMaxNoHelper;
import com.sinosoft.common.satoken.utils.LoginHelper;
import com.sinosoft.common.domain.ConditionBase;
import com.sinosoft.common.schema.common.domain.vo.LabelShowVo;
import com.sinosoft.common.enums.BasicFieldTypeEnum;
import com.sinosoft.common.enums.BranchLevelEnum;
import com.sinosoft.common.enums.BranchTypeEnum;
import com.sinosoft.common.enums.EdorTypeEnum;
import com.sinosoft.common.exception.SaveFrameworkException;
import com.sinosoft.common.service.IBaseImpl;
import com.sinosoft.common.utils.Condition2Util;
import com.sinosoft.common.utils.saveframe.BasicSaveFramework;
import com.sinosoft.common.utils.saveframe.DataRecord;
import com.sinosoft.common.utils.saveframe.IBasicDataRecordCheck;
import com.sinosoft.bank.common.core.BkBranchTree;
import com.sinosoft.bank.common.domain.BkBranchTreeNode;
import com.sinosoft.bank.common.domain.dto.BkBranchBaseDTO;
import com.sinosoft.bank.common.frameworkcheck.BkBranchAttrFieldCheck;
import com.sinosoft.bank.common.frameworkcheck.BkDictFieldCheck;
import com.sinosoft.bank.common.frameworkcheck.BkManageComFieldCheck;
import com.sinosoft.bank.common.service.IBkBranchCommonService;
import com.sinosoft.bank.team.domain.bo.BkBranchGroupFormBo;
import com.sinosoft.bank.team.domain.bo.BkBranchGroupQueryBo;
import com.sinosoft.bank.team.domain.vo.BkBranchGroupFormVo;
import com.sinosoft.bank.team.domain.vo.BkBranchGroupShowVo;
import com.sinosoft.bank.team.mapper.BkBranchGroupMapper;
import com.sinosoft.bank.team.service.IBkBranchGroupService;
import com.sinosoft.common.schema.agent.domain.Laagent;
import com.sinosoft.common.schema.agent.mapper.LaagentMapper;
import com.sinosoft.common.schema.team.domain.Labranchgroup;
import com.sinosoft.common.schema.team.domain.Labranchgroupb;
import com.sinosoft.common.schema.team.domain.Labranchlevel;
import com.sinosoft.common.schema.team.mapper.LabranchgroupbMapper;
import com.sinosoft.common.schema.team.mapper.LabranchlevelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: cms6
 * @description: 销售机构管理Service业务层处理
 * @author: zzf
 * @create: 2024-05-23 11:17
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BkBranchGroupServiceImpl implements IBkBranchGroupService, IBaseImpl {

    private final BkBranchGroupMapper bkBranchGroupMapper;
    private final LabranchgroupbMapper branchGroupBMapper;
    private final LabranchlevelMapper branchLevelMapper;
    private final LaagentMapper agentMapper;
    private final IBkBranchCommonService bkBranchCommonService;
    private final DataSyncHelper dataSyncHelper;
    private final LDMaxNoHelper ldMaxNoHelper;
    private final Condition2Util condition2Util;

    @Override
    public BkBranchGroupFormVo queryById(String agentGroup) {
        return bkBranchGroupMapper.selectFromById(agentGroup);
    }

    @Override
    public TableDataInfo<BkBranchGroupShowVo> queryPageList(ConditionBase query, PageQuery pageQuery, LoginUser loginUser) {
        QueryWrapper<Labranchgroup> lqw = Wrappers.query();
        condition2Util.initQueryWrapper("BANK_TEAM_EDIT", query, lqw, loginUser);
        Page<BkBranchGroupShowVo> pageList = bkBranchGroupMapper.selectBkPageShowList(pageQuery.build(), lqw);
        return TableDataInfo.build(pageList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(BkBranchGroupFormBo bo, LoginUser loginUser) {
        Labranchgroup add = MapstructUtils.convert(bo, Labranchgroup.class);
        Assert.notNull(add, "销售机构数据为空!");
        //新增数据
        List<Labranchgroup> addList = new ArrayList<>();

        Labranchgroup upBranchGroup = null;
        //上级机构不为空
        if (StringUtils.isNotBlank(add.getUpbranch())) {
            //查找上级
            upBranchGroup = bkBranchGroupMapper.selectById(add.getUpbranch());
            Assert.notNull(upBranchGroup, "上级销售机构查询失败!");
        }
        BkBranchTree bkBranchTree = new BkBranchTree();
        //初始默认值
        add.setBranchtype2("01");
        add.setBranchclass("02");
        Labranchgroup quBranch = null;
        Labranchgroup buBranch = null;
        //如果是新增区
        if (BranchLevelEnum.YB03.getCode().equals(add.getBranchlevel())) {
            //设置区最大编号
            bkBranchTree.getRootNode().setNextLevMaxBh(getMaxBranchAttr(BranchLevelEnum.YB03, ""));
            quBranch = add;
            buBranch = new Labranchgroup();
            BeanUtils.copyProperties(quBranch, buBranch);
            //获取主键
            String[] branchs = ldMaxNoHelper.creatMaxNoStep("AGENTGROUP_BANK", 11, 2);
            String strQuBranch = formatBranch(branchs[0]);
            String strBuBranch = formatBranch(branchs[1]);
            String newBranchAttr = bkBranchTree.getNewBh(BranchLevelEnum.YB03, bkBranchTree.getRootNode());
            setBranch(quBranch, null, strQuBranch, newBranchAttr, quBranch.getName(), false, BranchLevelEnum.YB03);
            setBranch(buBranch, quBranch, strBuBranch, quBranch.getBranchattr() + "0001", quBranch.getName(), true, BranchLevelEnum.YB02);
            addList.add(quBranch);
        } else if (BranchLevelEnum.YB02.getCode().equals(add.getBranchlevel())) {
            String maxBranchAttr = getMaxBranchAttr(BranchLevelEnum.YB02, upBranchGroup.getBranchattr());
            BkBranchTreeNode node = bkBranchTree.putTreeNode(bkBranchTree.getRootNode(), upBranchGroup);
            node.setNextLevMaxBh(maxBranchAttr);
            buBranch = add;
            //获取主键
            String[] branchs = ldMaxNoHelper.creatMaxNoStep("AGENTGROUP_BANK", 11, 1);
            String strBuBranch = formatBranch(branchs[0]);
            String newBranchAttr = bkBranchTree.getNewBh(BranchLevelEnum.YB02, bkBranchTree.getBranchTreeNodeById(upBranchGroup.getAgentgroup()));
            setBranch(buBranch, upBranchGroup, strBuBranch, newBranchAttr, buBranch.getName(), false, BranchLevelEnum.YB02);
        }
        //部不为空
        if (buBranch != null) {
            addList.add(buBranch);
        }
        addList.forEach(branch -> {
            this.setInsertValue(branch, loginUser.getUsername());
        });
        boolean flag = bkBranchGroupMapper.insertBatch(addList);
        if (flag) {
            bo.setAgentgroup(add.getAgentgroup());
        }
//        dataSyncHelper.publishBatchEvent("labranchgroup", addList, OperationType.INSERT, loginUser);
        return flag;
    }

    /**
     * 获取最大机构编号
     *
     * @param levelEnum 级别
     * @param attr      机构编码
     * @return 新的机构编码
     */
    private String getMaxBranchAttr(BranchLevelEnum levelEnum, String attr) {
        return bkBranchGroupMapper.getMaxBranchAttr(BranchTypeEnum.YB.getCode(), levelEnum.getCode(), attr);
    }

    /**
     * 设置机构信息
     *
     * @param branchGroup   当前机构
     * @param upBranchGroup 上级机构
     * @param agentgroup    销售机构编号
     * @param branchAttr    销售机构代码
     * @param branchName    机构名称
     * @param bZxFlag       是否直辖
     */
    private void setBranch(Labranchgroup branchGroup, Labranchgroup upBranchGroup, String agentgroup, String branchAttr, String branchName, boolean bZxFlag, BranchLevelEnum levelEnum) {
        branchGroup.setAgentgroup(agentgroup);
        branchGroup.setBranchattr(branchAttr);
        if (upBranchGroup != null) {
            branchGroup.setUpbranch(upBranchGroup.getAgentgroup());
            branchGroup.setBranchseries(upBranchGroup.getBranchseries() + ":" + agentgroup);
        } else {
            branchGroup.setBranchseries(agentgroup);
            branchGroup.setUpbranch("");
        }
        String strName = branchName;
        String zxName = bZxFlag ? "直辖" : "";
        if (Objects.requireNonNull(levelEnum) == BranchLevelEnum.YB02) {
            if (!strName.endsWith("部")) {
                strName = strName + zxName + "部";
            } else {
                strName = strName + zxName;
            }
        }
        branchGroup.setName(strName);
        branchGroup.setBranchlevel(levelEnum.getCode());
        branchGroup.setUpbranchattr(bZxFlag ? "1" : "0");
        branchGroup.setBranchtype(BranchTypeEnum.YB.getCode());
    }

    /**
     * 格式化
     *
     * @param branch 机构代码
     * @return 新的机构代码
     */
    public static String formatBranch(String branch) {
        return "3" + branch;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(BkBranchGroupFormBo bo, LoginUser loginUser) {
        //基础数据准备和验证
        Labranchgroup update = MapstructUtils.convert(bo, Labranchgroup.class);
        Labranchgroup oldLaBranchGroup = bkBranchGroupMapper.selectById(bo.getAgentgroup());
        Assert.notNull(update, "销售机构数据为空!");
        Assert.notNull(oldLaBranchGroup, "数据查询失败!");

        List<Labranchgroupb> backupList = new ArrayList<>();
        List<Labranchgroup> modifyDataList = new ArrayList<>();

        //备份原始数据并添加更新数据
        backupList.add(this.doBack(oldLaBranchGroup, Labranchgroupb.class, EdorTypeEnum.ED30, loginUser.getUsername()));
        modifyDataList.add(update);

        //判断是否为区级机构
        boolean isDistrictLevel = BranchLevelEnum.YB03.getCode().equals(oldLaBranchGroup.getBranchlevel());
        //直辖机构
        Labranchgroup directBranch = null;

        //处理区级机构更新
        if (isDistrictLevel) {
            //处理名称变更
            boolean nameChanged = StringUtils.isNotBlank(update.getName()) && StringUtils.isNotBlank(oldLaBranchGroup.getName()) && !update.getName().equals(oldLaBranchGroup.getName());
            if (nameChanged) {
                List<Labranchgroup> branches = selectLowerTeam(oldLaBranchGroup.getBranchattr());
                for (Labranchgroup branch : branches) {
                    if (!"1".equals(branch.getUpbranchattr()) && branch.getName().contains(oldLaBranchGroup.getName())) {
                        backupList.add(this.doBack(branch, Labranchgroupb.class, EdorTypeEnum.ED30, loginUser.getUsername()));
                        Labranchgroup updateBranch = new Labranchgroup();
                        updateBranch.setAgentgroup(branch.getAgentgroup());
                        updateBranch.setName(branch.getName().replaceAll(oldLaBranchGroup.getName(), update.getName()));
                        modifyDataList.add(updateBranch);
                    }
                }
                directBranch = branches.stream().filter(b -> "1".equals(b.getUpbranchattr())).findFirst().orElse(null);
                if (directBranch != null) {
                    directBranch.setName(directBranch.getName().replaceAll(oldLaBranchGroup.getName(), update.getName()));
                }
            }

            //处理停业状态变更
            boolean statusChangedToInactive = "N".equals(oldLaBranchGroup.getEndflag()) && "Y".equals(update.getEndflag());
            if (statusChangedToInactive) {
                List<Labranchgroup> branches = selectLowerTeam(oldLaBranchGroup.getBranchattr());
                for (Labranchgroup branch : branches) {
                    if (!"1".equals(branch.getUpbranchattr())) {
                        backupList.add(this.doBack(branch, Labranchgroupb.class, EdorTypeEnum.ED30, loginUser.getUsername()));

                        Labranchgroup updateBranch = new Labranchgroup();
                        updateBranch.setAgentgroup(branch.getAgentgroup());
                        updateBranch.setEndflag(update.getEndflag());
                        updateBranch.setEnddate(update.getEnddate());
                        modifyDataList.add(updateBranch);
                    }
                }
                if (directBranch == null) {
                    directBranch = branches.stream().filter(b -> "1".equals(b.getUpbranchattr())).findFirst().orElse(null);
                }
            }

            //确保直辖机构数据完整性
            if (directBranch == null) {
                directBranch = queryZxBranch(oldLaBranchGroup.getBranchattr(), BranchLevelEnum.YB02.getCode());
            }
            if (directBranch != null) {
                backupList.add(this.doBack(directBranch, Labranchgroupb.class, EdorTypeEnum.ED30, loginUser.getUsername()));
                modifyDataList.add(directBranch);
            }
        }

        //处理恢复正常状态
        boolean statusRecovery = "N".equals(update.getEndflag()) && "Y".equals(oldLaBranchGroup.getEndflag());
        if (statusRecovery) {
            update.setEnddate(null);
            LambdaUpdateWrapper<Labranchgroup> recoveryWrapper = Wrappers.lambdaUpdate();
            recoveryWrapper.set(Labranchgroup::getEndflag, update.getEndflag())
                .set(Labranchgroup::getEnddate, update.getEnddate())
                .eq(Labranchgroup::getAgentgroup, update.getAgentgroup());
            bkBranchGroupMapper.update(null, recoveryWrapper);
        }

        //更新直辖机构信息
        if (isDistrictLevel && directBranch != null) {
            if (statusRecovery) {
                LambdaUpdateWrapper<Labranchgroup> directWrapper = Wrappers.lambdaUpdate();
                directWrapper.set(Labranchgroup::getEndflag, update.getEndflag())
                    .set(Labranchgroup::getEnddate, update.getEnddate())
                    .eq(Labranchgroup::getAgentgroup, directBranch.getAgentgroup());
                bkBranchGroupMapper.update(null, directWrapper);
                //不修改值 必须设置为null否则直辖会被覆盖
                directBranch.setEndflag(null);
                directBranch.setEnddate(null);
            } else {
                directBranch.setEndflag(update.getEndflag());
                directBranch.setEnddate(update.getEnddate());
            }
            // 更新直辖机构的其他信息
            directBranch.setFounddate(update.getFounddate());
            directBranch.setBranchaddress(update.getBranchaddress());
            directBranch.setBranchphone(update.getBranchphone());
            directBranch.setBranchfax(update.getBranchfax());
            directBranch.setFieldflag(update.getFieldflag());
        }
        //执行批量更新
        modifyDataList.forEach(branch -> this.setUpdateValue(branch, loginUser.getUsername()));
        boolean updateSuccess = bkBranchGroupMapper.updateBatchById(modifyDataList);
        branchGroupBMapper.insertBatch(backupList);
        //同步
        dataSyncHelper.publishSyncEvent(Labranchgroup.class, modifyDataList, OperationType.UPDATE, loginUser);
        return updateSuccess;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertData(List<Labranchgroup> addList, List<Labranchgroup> updateList, LoginUser loginUser) {
        if (CollUtil.isNotEmpty(addList)) {
            BkBranchTree bkBranchTree = new BkBranchTree();
            //初始化根节点最大编号
            if (addList.stream().anyMatch(b -> BranchLevelEnum.YB03.getCode().equals(b.getBranchlevel()))
                && StringUtils.isBlank(bkBranchTree.getRootNode().getNextLevMaxBh())) {
                bkBranchTree.getRootNode().setNextLevMaxBh(getMaxBranchAttr(BranchLevelEnum.YB03, ""));
            }
            //初始化区层最大编号
            addList.stream()
                .filter(b -> BranchLevelEnum.YB02.getCode().equals(b.getBranchlevel())).filter(b -> !bkBranchTree.branchTreeNodeContains(b.getUpbranch()))
                .forEach(b -> Optional.ofNullable(bkBranchGroupMapper.selectById(b.getUpbranch()))
                    .ifPresent(upBranch -> {
                        String maxBranchAttr = getMaxBranchAttr(BranchLevelEnum.YB02, upBranch.getBranchattr());
                        BkBranchTreeNode node = bkBranchTree.putTreeNode(bkBranchTree.getRootNode(), upBranch);
                        node.setNextLevMaxBh(maxBranchAttr);
                    }));
            //新增数据
            List<Labranchgroup> insertDataList = new ArrayList<>();
            //循环新增数据
            for (Labranchgroup addBranch : addList) {
                Labranchgroup upBranchGroup = null;
                //上级机构不为空
                if (StringUtils.isNotBlank(addBranch.getUpbranch())) {
                    BkBranchTreeNode node = bkBranchTree.getBranchTreeNodeById(addBranch.getUpbranch());
                    Assert.notNull(node, "上级销售机构查询失败!");
                    upBranchGroup = node.getBranchGroup();
                }
                //初始默认值
                addBranch.setBranchtype2("01");
                addBranch.setBranchclass("02");

                Labranchgroup quBranch = null;
                Labranchgroup buBranch = null;
                //如果是新增区
                if (BranchLevelEnum.YB03.getCode().equals(addBranch.getBranchlevel())) {
                    quBranch = addBranch;
                    buBranch = new Labranchgroup();
                    BeanUtils.copyProperties(quBranch, buBranch);
                    //获取主键
                    String[] branchs = ldMaxNoHelper.creatMaxNoStep("AGENTGROUP_BANK", 11, 2);
                    String strQuBranch = formatBranch(branchs[0]);
                    String strBuBranch = formatBranch(branchs[1]);
                    String newBranchAttr = bkBranchTree.getNewBh(BranchLevelEnum.YB03, bkBranchTree.getRootNode());
                    setBranch(quBranch, null, strQuBranch, newBranchAttr, quBranch.getName(), false, BranchLevelEnum.YB03);
                    setBranch(buBranch, quBranch, strBuBranch, quBranch.getBranchattr() + "0001", quBranch.getName(), true, BranchLevelEnum.YB02);
                    insertDataList.add(quBranch);
                } else if (BranchLevelEnum.YB02.getCode().equals(addBranch.getBranchlevel())) {
                    buBranch = addBranch;
                    //获取主键
                    String[] branchs = ldMaxNoHelper.creatMaxNoStep("AGENTGROUP_BANK", 11, 1);
                    String strBuBranch = formatBranch(branchs[0]);
                    String newBranchAttr = bkBranchTree.getNewBh(BranchLevelEnum.YB02, bkBranchTree.getBranchTreeNodeById(upBranchGroup.getAgentgroup()));
                    setBranch(buBranch, upBranchGroup, strBuBranch, newBranchAttr, buBranch.getName(), false, BranchLevelEnum.YB02);
                }
                //部不为空
                if (buBranch != null) {
                    insertDataList.add(buBranch);
                }
            }
            insertDataList.forEach(branch -> {
                this.setInsertValue(branch, loginUser.getUsername());
            });
            bkBranchGroupMapper.insertBatch(insertDataList);
        }
        if (CollUtil.isNotEmpty(updateList)) {
            List<Labranchgroup> modifyDataList = new ArrayList<>();
            List<Labranchgroupb> backList = new ArrayList<>();
            //循环修改数据
            for (Labranchgroup laBranchGroup : updateList) {
                //查询当前历史数据
                Labranchgroup oldLaBranchGroup = bkBranchGroupMapper.selectById(laBranchGroup.getAgentgroup());
                //备份原始数据并添加更新数据
                backList.add(this.doBack(oldLaBranchGroup, Labranchgroupb.class, EdorTypeEnum.ED30, loginUser.getUsername()));
                modifyDataList.add(oldLaBranchGroup);
                //判断是否为区级机构
                boolean isDistrictLevel = BranchLevelEnum.YB03.getCode().equals(oldLaBranchGroup.getBranchlevel());
                //直辖机构
                Labranchgroup directBranch = null;

                //处理区级机构更新
                if (isDistrictLevel) {
                    //处理名称变更
                    boolean nameChanged = StringUtils.isNotBlank(laBranchGroup.getName()) && StringUtils.isNotBlank(oldLaBranchGroup.getName()) && !laBranchGroup.getName().equals(oldLaBranchGroup.getName());
                    if (nameChanged) {
                        List<Labranchgroup> branches = selectLowerTeam(oldLaBranchGroup.getBranchattr());
                        for (Labranchgroup branch : branches) {
                            if (!"1".equals(branch.getUpbranchattr()) && branch.getName().contains(oldLaBranchGroup.getName())) {
                                backList.add(this.doBack(branch, Labranchgroupb.class, EdorTypeEnum.ED30, loginUser.getUsername()));
                                Labranchgroup updateBranch = new Labranchgroup();
                                updateBranch.setAgentgroup(branch.getAgentgroup());
                                updateBranch.setName(branch.getName().replaceAll(oldLaBranchGroup.getName(), laBranchGroup.getName()));
                                modifyDataList.add(updateBranch);
                            }
                        }
                        directBranch = branches.stream().filter(b -> "1".equals(b.getUpbranchattr())).findFirst().orElse(null);
                        if (directBranch != null) {
                            directBranch.setName(directBranch.getName().replaceAll(oldLaBranchGroup.getName(), laBranchGroup.getName()));
                        }
                    }

                    //处理停业状态变更
                    boolean statusChangedToInactive = "N".equals(oldLaBranchGroup.getEndflag()) && "Y".equals(laBranchGroup.getEndflag());
                    if (statusChangedToInactive) {
                        List<Labranchgroup> branches = selectLowerTeam(oldLaBranchGroup.getBranchattr());
                        for (Labranchgroup branch : branches) {
                            if (!"1".equals(branch.getUpbranchattr())) {
                                backList.add(this.doBack(branch, Labranchgroupb.class, EdorTypeEnum.ED30, loginUser.getUsername()));

                                Labranchgroup updateBranch = new Labranchgroup();
                                updateBranch.setAgentgroup(branch.getAgentgroup());
                                updateBranch.setEndflag(laBranchGroup.getEndflag());
                                updateBranch.setEnddate(laBranchGroup.getEnddate());
                                modifyDataList.add(updateBranch);
                            }
                        }
                        if (directBranch == null) {
                            directBranch = branches.stream().filter(b -> "1".equals(b.getUpbranchattr())).findFirst().orElse(null);
                        }
                    }

                    //确保直辖机构数据完整性
                    if (directBranch == null) {
                        directBranch = queryZxBranch(oldLaBranchGroup.getBranchattr(), BranchLevelEnum.YB02.getCode());
                    }
                    if (directBranch != null) {
                        backList.add(this.doBack(directBranch, Labranchgroupb.class, EdorTypeEnum.ED30, loginUser.getUsername()));
                        modifyDataList.add(directBranch);
                    }
                }
                //处理恢复正常状态
                boolean statusRecovery = "N".equals(laBranchGroup.getEndflag()) && "Y".equals(oldLaBranchGroup.getEndflag());
                if (statusRecovery) {
                    laBranchGroup.setEnddate(null);
                    LambdaUpdateWrapper<Labranchgroup> recoveryWrapper = Wrappers.lambdaUpdate();
                    recoveryWrapper.set(Labranchgroup::getEndflag, laBranchGroup.getEndflag())
                        .set(Labranchgroup::getEnddate, laBranchGroup.getEnddate())
                        .eq(Labranchgroup::getAgentgroup, laBranchGroup.getAgentgroup());
                    bkBranchGroupMapper.update(null, recoveryWrapper);
                }

                Optional.ofNullable(laBranchGroup.getName()).filter(StringUtils::isNotBlank).ifPresent(oldLaBranchGroup::setName);
                Optional.ofNullable(laBranchGroup.getBranchphone()).filter(StringUtils::isNotBlank).ifPresent(oldLaBranchGroup::setBranchphone);
                Optional.ofNullable(laBranchGroup.getBranchzipcode()).filter(StringUtils::isNotBlank).ifPresent(oldLaBranchGroup::setBranchzipcode);
                Optional.ofNullable(laBranchGroup.getBranchfax()).filter(StringUtils::isNotBlank).ifPresent(oldLaBranchGroup::setBranchfax);
                Optional.ofNullable(laBranchGroup.getEndflag()).filter(StringUtils::isNotBlank).ifPresent(oldLaBranchGroup::setEndflag);
                Optional.ofNullable(laBranchGroup.getBranchaddress()).filter(StringUtils::isNotBlank).ifPresent(oldLaBranchGroup::setBranchaddress);
                Optional.ofNullable(laBranchGroup.getFieldflag()).filter(StringUtils::isNotBlank).ifPresent(oldLaBranchGroup::setFieldflag);
                Optional.ofNullable(laBranchGroup.getFounddate()).ifPresent(oldLaBranchGroup::setFounddate);

                //更新直辖机构信息
                if (isDistrictLevel && directBranch != null) {
                    if (statusRecovery) {
                        LambdaUpdateWrapper<Labranchgroup> directWrapper = Wrappers.lambdaUpdate();
                        directWrapper.set(Labranchgroup::getEndflag, laBranchGroup.getEndflag())
                            .set(Labranchgroup::getEnddate, laBranchGroup.getEnddate())
                            .eq(Labranchgroup::getAgentgroup, directBranch.getAgentgroup());
                        bkBranchGroupMapper.update(null, directWrapper);
                        //不修改值 必须设置为null否则直辖会被覆盖
                        directBranch.setEndflag(null);
                        directBranch.setEnddate(null);
                    } else {
                        directBranch.setEndflag(laBranchGroup.getEndflag());
                        directBranch.setEnddate(laBranchGroup.getEnddate());
                    }
                    directBranch.setFounddate(oldLaBranchGroup.getFounddate());
                    directBranch.setBranchaddress(oldLaBranchGroup.getBranchaddress());
                    directBranch.setBranchphone(oldLaBranchGroup.getBranchphone());
                    directBranch.setBranchfax(oldLaBranchGroup.getBranchfax());
                    directBranch.setFieldflag(oldLaBranchGroup.getFieldflag());
                }


            }
            //执行批量更新
            modifyDataList.forEach(branch -> this.setUpdateValue(branch, loginUser.getUsername()));
            bkBranchGroupMapper.updateBatchById(modifyDataList);
            branchGroupBMapper.insertBatch(backList);
        }
    }

    /**
     * 数据保存前校验
     *
     * @param formBo 保存数据
     * @param bAdd   是否新增
     */
    @Override
    public void validEntityBeforeSave(BkBranchGroupFormBo formBo, boolean bAdd) {
        //去除空格
        Optional.ofNullable(formBo.getName()).filter(StringUtils::isNotBlank).map(String::trim).ifPresent(formBo::setName);
        Optional.ofNullable(formBo.getBranchaddress()).filter(StringUtils::isNotBlank).map(String::trim).ifPresent(formBo::setBranchaddress);
        //如果停业日期与成立日期不为空
        if (formBo.getEnddate() != null && formBo.getFounddate() != null) {
            //停业日期不能小于成立日期
            if (formBo.getEnddate().before(formBo.getFounddate())) {
                throw new ServiceException("停业日期不能小于成立日期!");
            }
            //停业日期不能大于当前日期
            if (formBo.getEnddate().after(new Date())) {
                throw new ServiceException("停业日期不能大于当前日期!");
            }
        }
        //如果是部
        if (BranchLevelEnum.YB02.getCode().equals(formBo.getBranchlevel())) {
            //校验上级
            if (StringUtils.isBlank(formBo.getUpbranch())) {
                throw new ServiceException("上级机构不能为空!");
            }
        } else if (BranchLevelEnum.YB03.getCode().equals(formBo.getBranchlevel())) {
            //校验上级
            if (StringUtils.isNotBlank(formBo.getUpbranch())) {
                throw new ServiceException("营业区上级机构必须为空!");
            }
        }
        //如果是新增
        if (bAdd) {
            //检查名称是否存在
            if (checkNameUnique(formBo.getName(), formBo.getBranchattr())) {
                throw new ServiceException("机构名称已存在!");
            }
        } else {
            //如果是区 判断辖下机构是否有未停业的
            if (BranchLevelEnum.YB03.getCode().equals(formBo.getBranchlevel()) && "Y".equals(formBo.getEndflag())) {
                if (checkBranchTeamEndFlag(formBo.getBranchattr())) {
                    throw new ServiceException("辖下机构存在未停业机构!");
                }
            }
            //如果是停业检查辖下是否有在职人员
            if ("Y".equals(formBo.getEndflag())) {
                if (checkBranchAgent(formBo.getBranchattr())) {
                    throw new ServiceException("当前团队架构辖下存在在职人员,不能停业!");
                }
            }
        }
    }

    @Override
    public Boolean checkNameUnique(String name, String branchAttr) {
        LambdaQueryWrapper<Labranchgroup> lqw = Wrappers.lambdaQuery();
        //如果销售机构不为空
        if (StringUtils.isNotBlank(branchAttr)) {
            lqw.ne(Labranchgroup::getBranchattr, branchAttr);
        }
        lqw.eq(Labranchgroup::getName, name);
        lqw.eq(Labranchgroup::getBranchtype, BranchTypeEnum.YB.getCode());
        return bkBranchGroupMapper.selectCount(lqw) > 0;
    }

    @Override
    public Boolean checkBranchAgent(String branchAttr) {
        QueryWrapper<Laagent> lqw = Wrappers.query();
        lqw.eq("branchtype", BranchTypeEnum.YB.getCode());
        lqw.lt("agentstate", "04");
        lqw.last("and agentgroup in (select agentgroup from labranchgroup b where b.branchtype=laagent.branchtype and b.branchattr like concat('" + branchAttr + "','%'))");
        return agentMapper.selectCount(lqw) > 0;
    }

    @Override
    public Boolean checkBranchTeamEndFlag(String branchAttr) {
        LambdaQueryWrapper<Labranchgroup> lqw = Wrappers.lambdaQuery();
        lqw.eq(Labranchgroup::getBranchtype, BranchTypeEnum.YB.getCode());
        lqw.eq(Labranchgroup::getUpbranchattr, "0");
        lqw.likeRight(Labranchgroup::getBranchattr, branchAttr);
        lqw.eq(Labranchgroup::getEndflag, "N");
        lqw.ne(Labranchgroup::getBranchattr, branchAttr);
        return bkBranchGroupMapper.selectCount(lqw) > 0;
    }

    @Override
    public List<LabelShowVo> queryBranchByLabel(BkBranchGroupQueryBo queryBo) {
        LambdaQueryWrapper<Labranchgroup> lqw = buildShowQueryWrapper(queryBo);
        List<Labranchgroup> dataList = bkBranchGroupMapper.selectList(lqw);
        return dataList.stream().map(branch -> {
            LabelShowVo labelShowVo = new LabelShowVo();
            labelShowVo.setLabel(branch.getName());
            labelShowVo.setValue(branch.getBranchattr());
            Map<String, Object> oMap = new HashMap<>();
            oMap.put("agentgroup", branch.getAgentgroup());
            oMap.put("branchmanager", branch.getBranchmanager());
            oMap.put("branchmanagername", branch.getBranchmanagername());
            labelShowVo.setOtherData(oMap);
            return labelShowVo;
        }).collect(Collectors.toList());
    }

    /**
     * 界面展示查询条件
     *
     * @param bo 查询条件
     * @return 构选条件
     */
    private LambdaQueryWrapper<Labranchgroup> buildShowQueryWrapper(BkBranchGroupQueryBo bo) {
        LambdaQueryWrapper<Labranchgroup> lqw = Wrappers.lambdaQuery();
        lqw.select(Labranchgroup::getAgentgroup, Labranchgroup::getBranchattr, Labranchgroup::getName, Labranchgroup::getBranchmanager, Labranchgroup::getBranchmanagername, Labranchgroup::getBranchlevel,
            Labranchgroup::getBranchattr, Labranchgroup::getEndflag, Labranchgroup::getManagecom);
        //管理机构
        lqw.likeRight(StringUtils.isNotBlank(bo.getManagecom()), Labranchgroup::getManagecom, bo.getManagecom());
        lqw.like(StringUtils.isNotBlank(bo.getName()), Labranchgroup::getName, bo.getName());
        lqw.eq(Labranchgroup::getBranchtype, BranchTypeEnum.YB.getCode());
        lqw.eq(StringUtils.isNotBlank(bo.getAgentgroup()), Labranchgroup::getAgentgroup, bo.getAgentgroup());
        lqw.eq(StringUtils.isNotBlank(bo.getBranchattr()), Labranchgroup::getBranchattr, bo.getBranchattr());
        //主管
        lqw.like(StringUtils.isNotBlank(bo.getBranchmanagername()), Labranchgroup::getBranchmanagername, bo.getBranchmanagername());
        //主管
        lqw.eq(StringUtils.isNotBlank(bo.getBranchmanager()), Labranchgroup::getBranchmanager, bo.getBranchmanager());
        //停业状态
        lqw.eq(StringUtils.isNotBlank(bo.getEndflag()), Labranchgroup::getEndflag, bo.getEndflag());
        //主管标志
        if (StringUtils.isNotBlank(bo.getHavemanager())) {
            //如果有主管
            if ("Y".equals(bo.getHavemanager())) {
                lqw.isNotNull(Labranchgroup::getBranchmanager);
            } else if ("N".equals(bo.getHavemanager())) {
                //没有主管
                lqw.isNull(Labranchgroup::getBranchmanager);
            }
        }
        //如果级别不为空
        lqw.in(CollUtil.isNotEmpty(bo.getBranchlevel()), Labranchgroup::getBranchlevel, bo.getBranchlevel());
        //如果不是超级管理员，只能查询自己管理的机构
        if (!LoginHelper.isSuperAdmin()) {
            LoginUser loginUser = LoginHelper.getLoginUser();
            lqw.likeRight(Labranchgroup::getManagecom, Objects.requireNonNull(loginUser).getDeptId());
        }
        lqw.orderByAsc(Labranchgroup::getManagecom, Labranchgroup::getBranchattr);
        return lqw;
    }

    @Override
    public List<LabelShowVo> queryBranchLevelLabel() {
        LambdaQueryWrapper<Labranchlevel> lqw = Wrappers.lambdaQuery();
        lqw.select(Labranchlevel::getBranchlevelcode, Labranchlevel::getBranchlevelname);
        lqw.eq(Labranchlevel::getBranchtype, BranchTypeEnum.YB.getCode());
        lqw.orderByAsc(Labranchlevel::getBranchlevelcode);
        List<Labranchlevel> dataList = branchLevelMapper.selectList(lqw);
        return dataList.stream().map(ldCom -> {
            LabelShowVo labelShowVo = new LabelShowVo();
            labelShowVo.setLabel(ldCom.getBranchlevelname());
            labelShowVo.setValue(ldCom.getBranchlevelcode());
            return labelShowVo;
        }).collect(Collectors.toList());
    }

    @Override
    public void validBatchEntityBeforeSave(List<Labranchgroup> addList, List<Labranchgroup> updateList, MultipartFile file, LoginUser loginUser) {
        //管理机构校验
        BkManageComFieldCheck manageComFieldCheck = new BkManageComFieldCheck();
        manageComFieldCheck.comGrade = "04";
        manageComFieldCheck.operatorManageCom = loginUser.getDeptId() + "";
        //销售机构校验
        BkBranchAttrFieldCheck agentGroupFieldCheck = new BkBranchAttrFieldCheck();
        agentGroupFieldCheck.branchLevel = BranchLevelEnum.YB03.getCode();
        agentGroupFieldCheck.manageComField = "managecom";
        agentGroupFieldCheck.writeAgentGroupField = "upagentgroup";

        //修改数据
        BasicSaveFramework updateFramework = new BasicSaveFramework();
        updateFramework.addExcelField(0, "branchattr", "销售机构代码", BasicFieldTypeEnum.STRING, true, true, true, "", "-", null);
        updateFramework.addExcelField(1, "name", "销售机构名称", BasicFieldTypeEnum.STRING, false, false, true, "", "", null);
        updateFramework.addExcelField(2, "branchphone", "电话", BasicFieldTypeEnum.STRING, false, false, true, "", "", null);
        updateFramework.addExcelField(3, "branchzipcode", "邮编", BasicFieldTypeEnum.STRING, false, false, true, "", "", null);
        updateFramework.addExcelField(4, "branchfax", "传真", BasicFieldTypeEnum.STRING, false, false, true, "", "", null);
        updateFramework.addExcelField(5, "fieldflag", "独立职场", BasicFieldTypeEnum.STRING, false, false, true, "", "-", new BkDictFieldCheck("sys_yes_no"));
        updateFramework.addExcelField(6, "founddate", "成立日期", BasicFieldTypeEnum.DATE1, false, false, true, "", "", null);
        updateFramework.addExcelField(7, "endflag", "停业状态", BasicFieldTypeEnum.STRING, false, false, true, "", "-", new BkDictFieldCheck("sys_yes_no"));
        updateFramework.addExcelField(8, "enddate", "停业日期", BasicFieldTypeEnum.DATE1, false, false, true, "", "", null);
        updateFramework.addExcelField(9, "branchaddress", "地址", BasicFieldTypeEnum.STRING, false, false, true, "", "", null);
        updateFramework.addExcelField(-1, "agentgroup", "内部编码", BasicFieldTypeEnum.STRING, false, false, true, "", "", null);
        updateFramework.disableDataSqlRepeat();
        //记录修改的机构
        Map<String, String> updateBranchattrMap = new HashMap<>();
        updateFramework.addDataRecordCheckRule(new IBasicDataRecordCheck() {
            @Override
            public void checkDataRecordErrorMsg(List<DataRecord> recordList) throws Exception {
                Map<String, BkBranchBaseDTO> branchGroupMap = new HashMap<>();
                for (DataRecord dataRecord : recordList) {
                    String branchattr = dataRecord.dataMap.get("branchattr");
                    String founddate = dataRecord.dataMap.get("founddate");
                    String enddate = dataRecord.dataMap.get("enddate");
                    String endflag = dataRecord.dataMap.get("endflag");
                    updateBranchattrMap.put(branchattr, "");
                    //如果为空
                    if (!branchGroupMap.containsKey(branchattr)) {
                        //查询机构
                        branchGroupMap.put(branchattr, bkBranchCommonService.getBranchBaseByBranchAttr(branchattr));
                    }
                    //获取机构
                    BkBranchBaseDTO branchGroup = branchGroupMap.get(branchattr);
                    //如果不为空
                    if (branchGroup == null) {
                        dataRecord.errorMsg.addErrorInfo(branchattr, "销售机构不合法!");
                    } else {
                        if (StringUtils.isNotBlank(branchGroup.getManagecom())) {
                            if (!branchGroup.getManagecom().startsWith(String.valueOf(loginUser.getDeptId()))) {
                                dataRecord.errorMsg.addErrorInfo(branchattr, "没有权限操作!");
                            }
                        }
                        //如果是停业状态
                        if ("Y".equals(endflag)) {
                            //如果有在职人员
                            if (checkBranchAgent(branchGroup.getBranchattr())) {
                                dataRecord.errorMsg.addErrorInfo("机构下存在在职人员,不能停业!");
                            }
                            //如果是区停业
                            if (BranchLevelEnum.YB03.getCode().equals(branchGroup.getBranchlevel())) {
                                if (checkBranchTeamEndFlag(branchGroup.getBranchattr())) {
                                    dataRecord.errorMsg.addErrorInfo("辖下机构存在未停业机构!");
                                }
                            }
                        }
                        //如果是修改直辖
                        if ("1".equals(branchGroup.getUpbranchattr())) {
                            dataRecord.errorMsg.addErrorInfo("直辖机构不允许修改!");
                        }
                        dataRecord.dataMap.put("agentgroup", branchGroup.getAgentgroup());
                    }
                    //校验日期
                    if (StringUtils.isNotBlank(founddate) && DateUtils.parseDate(founddate).after(new Date())) {
                        dataRecord.errorMsg.addErrorInfo("成立日期不能大于当前日期!");
                    }
                    if ("Y".equals(endflag) && StringUtils.isBlank(enddate)) {
                        dataRecord.errorMsg.addErrorInfo("停业状态为是,必须有停业日期!");
                    }
                    if ("N".equals(endflag) && StringUtils.isNotBlank(enddate)) {
                        dataRecord.errorMsg.addErrorInfo("停业状态为否,不能有停业日期!");
                    }
                    if (StringUtils.isBlank(endflag) && StringUtils.isNotBlank(enddate)) {
                        dataRecord.errorMsg.addErrorInfo("停业状态为空,不允许设置停业日期!");
                    }
                    if (StringUtils.isNotBlank(enddate)) {
                        //校验日期
                        if (Objects.requireNonNull(branchGroup).getFounddate() != null && DateUtils.parseDate(enddate).before(branchGroup.getFounddate())) {
                            dataRecord.errorMsg.addErrorInfo("停业日期不能小于成立日期!");
                        }
                        if (DateUtils.parseDate(enddate).after(new Date())) {
                            dataRecord.errorMsg.addErrorInfo("停业日期不能大于当前日期!");
                        }
                    }
                }
            }
        });

        //新增数据
        BasicSaveFramework addFramework = new BasicSaveFramework();
        addFramework.addExcelField(0, "managecom", "管理机构", BasicFieldTypeEnum.STRING, true, false, true, "", "-", manageComFieldCheck);
        addFramework.addExcelField(1, "upbranch", "上级机构", BasicFieldTypeEnum.STRING, false, false, true, "", "-", agentGroupFieldCheck);
        addFramework.addExcelField(2, "branchlevel", "级别", BasicFieldTypeEnum.STRING, true, false, true, "", "-", null);
        addFramework.addExcelField(3, "name", "机构名称", BasicFieldTypeEnum.STRING, true, true, true, "", "", null);
        addFramework.addExcelField(4, "branchphone", "电话", BasicFieldTypeEnum.STRING, false, false, true, "", "", null);
        addFramework.addExcelField(5, "branchzipcode", "邮编", BasicFieldTypeEnum.STRING, false, false, true, "", "", null);
        addFramework.addExcelField(6, "branchfax", "传真", BasicFieldTypeEnum.STRING, false, false, true, "", "", null);
        addFramework.addExcelField(7, "fieldflag", "独立职场", BasicFieldTypeEnum.STRING, true, false, true, "", "-", new BkDictFieldCheck("sys_yes_no"));
        addFramework.addExcelField(8, "founddate", "成立日期", BasicFieldTypeEnum.DATE1, true, false, true, "", "", null);
        addFramework.addExcelField(9, "branchaddress", "地址", BasicFieldTypeEnum.STRING, false, false, true, "", "", null);
        addFramework.addExcelField(-1, "upagentgroup", "上级内部代码", BasicFieldTypeEnum.STRING, false, false, false, "", "", null);
        addFramework.addExcelField(-1, "endflag", "停业状态", BasicFieldTypeEnum.STRING, false, false, true, "N", "", null);
        addFramework.disableDataSqlRepeat();
        addFramework.addDataRecordCheckRule(new IBasicDataRecordCheck() {
            @Override
            public void checkDataRecordErrorMsg(List<DataRecord> recordList) throws Exception {
                for (DataRecord dataRecord : recordList) {
                    String name = dataRecord.dataMap.get("name");
                    String branchLevel = dataRecord.dataMap.get("branchlevel");
                    String upbranch = dataRecord.dataMap.get("upbranch");
                    String founddate = dataRecord.dataMap.get("founddate");
                    String upagentgroup = dataRecord.dataMap.get("upagentgroup");
                    if (!BranchLevelEnum.YB03.getCode().equals(branchLevel) && !BranchLevelEnum.YB02.getCode().equals(branchLevel)) {
                        dataRecord.errorMsg.addErrorInfo("级别不合法!");
                    }
                    //如果是部层级
                    if (BranchLevelEnum.YB02.getCode().equals(branchLevel)) {
                        //如果上级为空
                        if (StringUtils.isBlank(upbranch)) {
                            dataRecord.errorMsg.addErrorInfo("上级机构为空!");
                        }
                    }
                    if (BranchLevelEnum.YB03.getCode().equals(branchLevel)) {
                        if (StringUtils.isNotBlank(upbranch)) {
                            dataRecord.errorMsg.addErrorInfo("营业区上级机构必须为空!");
                        }
                    }
                    //检查名称是否重复
                    if (checkNameUnique(name, "")) {
                        dataRecord.errorMsg.addErrorInfo(name, "机构名称已存在!");
                    }
                    //校验日期
                    if (DateUtils.parseDate(founddate).after(new Date())) {
                        dataRecord.errorMsg.addErrorInfo("成立日期不能大于当前日期!");
                    }
                    //不能存在 修改的数据 又新增的情况
                    if (updateBranchattrMap.containsKey(upbranch)) {
                        dataRecord.errorMsg.addErrorInfo("上级机构已存在修改数据,不允许同时新增!");
                    }
                    dataRecord.dataMap.put("upbranch", upagentgroup);
                }
            }
        });


        try {
            addFramework.readExcelCheckInsert(file, 0, 2, "labranchgroup");
            addList.addAll(addFramework.dataRecordToEntity(Labranchgroup.class));
            updateFramework.readExcelCheckInsert(file, 1, 2, "labranchgroup");
            updateList.addAll(updateFramework.dataRecordToEntity(Labranchgroup.class));
        } catch (SaveFrameworkException e) {
            log.error("批量导入数据校验失败:{}", e.getMessage());
            throw new ServiceException("批量导入数据校验失败!");
        }
    }

    /**
     * 查询直辖机构
     *
     * @param branchAttr  销售机构
     * @param branchLevel 级别
     * @return 直辖数据
     */
    public Labranchgroup queryZxBranch(String branchAttr, String branchLevel) {
        LambdaQueryWrapper<Labranchgroup> lqw = Wrappers.lambdaQuery();
        lqw.eq(Labranchgroup::getBranchtype, BranchTypeEnum.YB.getCode());
        lqw.eq(Labranchgroup::getUpbranchattr, "1");
        lqw.eq(Labranchgroup::getBranchlevel, branchLevel);
        lqw.likeRight(Labranchgroup::getBranchattr, branchAttr);
        lqw.ne(Labranchgroup::getBranchattr, branchAttr);
        lqw.last("limit 1");
        return bkBranchGroupMapper.selectOne(lqw);
    }

    /**
     * 查询辖下非停业机构
     *
     * @param branchAttr 销售机构代码
     * @return 辖下机构
     */
    public List<Labranchgroup> selectLowerTeam(String branchAttr) {
        LambdaQueryWrapper<Labranchgroup> lqw = Wrappers.lambdaQuery();
        lqw.eq(Labranchgroup::getBranchtype, BranchTypeEnum.YB.getCode());
        lqw.likeRight(Labranchgroup::getBranchattr, branchAttr);
        lqw.ne(Labranchgroup::getBranchattr, branchAttr);
        lqw.eq(Labranchgroup::getEndflag, "N");
        return bkBranchGroupMapper.selectList(lqw);
    }
}
