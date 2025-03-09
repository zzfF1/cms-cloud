package com.sinosoft.common.service;


import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.common.schema.common.domain.LaElMain;
import com.sinosoft.common.schema.common.domain.LaElRule;
import com.sinosoft.common.domain.bo.LaElMainBo;
import com.sinosoft.common.schema.common.domain.vo.LaElMainVo;
import com.sinosoft.common.utils.rule.RuleTree;

import java.util.List;

/**
 * 规则Service接口
 *
 * @author zzf
 * @date 2023-06-21
 */
public interface ILaElMainService {

    /**
     * 查询规则配置列表
     */
    TableDataInfo<LaElMain> queryPageList(LaElMain bo, PageQuery pageQuery);

    /**
     * 匹配配置对象
     *
     * @param id 主键
     * @return 配置对象
     */
    LaElMainVo queryById(Long id);

    /**
     * 新增配置规则
     *
     * @param elMain
     * @return true成功 false失败
     */
    Boolean insertElMain(LaElMainBo elMain);

    /**
     * 配置配置
     *
     * @param elMainBo 配置对象
     * @return true成功 false失败
     */
    Boolean updateByBo(LaElMainBo elMainBo);

    /**
     * 新增规则
     *
     * @param elMain     规则主对象
     * @param elRuleList 规则明细对象
     * @return true 成功 false 失败
     */
    Boolean insertRuleInfo(LaElMain elMain, List<LaElRule> elRuleList);

    /**
     * 删除规则
     *
     * @param calCode 计算代码
     * @return true 成功 false 失败
     */
    Boolean deleteRuleMain(String calCode);

    /**
     * 获取规则主对象 查询数据库
     *
     * @param calCode 计算代码
     * @return 规则主对象
     */
    LaElMain getRuleMainByData(String calCode);

    /**
     * 获取规则明细对象 查询redis
     *
     * @param calCode 计算代码
     * @return 规则主对象
     */
    LaElMain getRuleMainByCache(String calCode);

    /**
     * 获取规则明细对象
     * 数据库与redis main对象比较 如果不一致则更新redis 同时返回明细对象
     *
     * @param calCode 计算代码
     * @return 规则明细对象
     */
    RuleTree getRuleListByCode(String calCode);
}
