package com.sinosoft.bank.team.service;

import com.sinosoft.system.api.model.LoginUser;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.common.domain.ConditionBase;
import com.sinosoft.common.schema.common.domain.vo.LabelShowVo;
import com.sinosoft.bank.team.domain.bo.BkBranchGroupFormBo;
import com.sinosoft.bank.team.domain.bo.BkBranchGroupQueryBo;
import com.sinosoft.bank.team.domain.vo.BkBranchGroupFormVo;
import com.sinosoft.bank.team.domain.vo.BkBranchGroupShowVo;
import com.sinosoft.common.schema.team.domain.Labranchgroup;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 销售机构管理Service接口
 */
public interface IBkBranchGroupService {
    /**
     * 查询销售机构管理
     */
    BkBranchGroupFormVo queryById(String agentGroup);


    /**
     * 分页查询销售机构管理
     * @param query 查询对象
     * @param pageQuery 分页参数
     * @param loginUser 登录用户
     * @return 销售机构管理集合
     */
    TableDataInfo<BkBranchGroupShowVo> queryPageList(ConditionBase query, PageQuery pageQuery, LoginUser loginUser);

    /**
     * 新增销售机构
     * @param bo 机构信息
     * @param loginUser 登录用户
     * @return true-成功,false-失败
     */
    Boolean insertByBo(BkBranchGroupFormBo bo, LoginUser loginUser);


    /**
     * 修改销售机构
     * @param bo 机构信息
     * @param loginUser 登录用户
     * @return true-成功,false-失败
     */
    Boolean updateByBo(BkBranchGroupFormBo bo, LoginUser loginUser);

    /**
     * 批量导入数据
     * @param addList 新增数据
     * @param updateList 修改数据
     * @param loginUser 登录用户
     * @return true-成功,false-失败
     */
    void insertData(List<Labranchgroup> addList, List<Labranchgroup> updateList, LoginUser loginUser);

    /**
     * 保存前数据校验
     *
     * @param formBo 保存对象
     */
    void validEntityBeforeSave(BkBranchGroupFormBo formBo, boolean bAdd);

    /**
     * 校验名称是否存在
     *
     * @param name       名称
     * @param branchAttr 销售机构编号
     * @return true-存在,false-不存在
     */
    Boolean checkNameUnique(String name, String branchAttr);

    /**
     * 校验机构下在职人数
     *
     * @param branchAttr 销售机构外部代码
     * @return 在职人数
     */
    Boolean checkBranchAgent(String branchAttr);

    /**
     * 辖下机构是否都停业
     *
     * @param branchAttr 销售机构编码
     * @return true存在未停业机构
     */
    Boolean checkBranchTeamEndFlag(String branchAttr);

    /**
     * 查询销售机构下拉
     * @param queryBo 查询对象
     * @return 销售机构下拉
     */
    List<LabelShowVo> queryBranchByLabel(BkBranchGroupQueryBo queryBo);

    /**
     * 销售机构级别下拉
     * @return 销售机构级别下拉
     */
    List<LabelShowVo> queryBranchLevelLabel();

    /**
     * 批量导入数据校验
     *
     * @param addList    新增数据
     * @param updateList 修改数据
     * @param file       文件
     * @param loginUser 登录用户
     */
    void validBatchEntityBeforeSave(List<Labranchgroup> addList, List<Labranchgroup> updateList, MultipartFile file,LoginUser loginUser);

}
