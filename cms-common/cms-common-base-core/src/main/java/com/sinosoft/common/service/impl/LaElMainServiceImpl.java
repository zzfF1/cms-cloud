package com.sinosoft.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sinosoft.common.schema.common.mapper.LaElMainMapper;
import com.sinosoft.common.schema.common.mapper.LaElRuleMapper;
import lombok.RequiredArgsConstructor;
import com.sinosoft.common.core.utils.MapstructUtils;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.common.redis.utils.RedisUtils;
import com.sinosoft.common.schema.common.domain.LaElMain;
import com.sinosoft.common.schema.common.domain.LaElRule;
import com.sinosoft.common.domain.bo.LaElMainBo;
import com.sinosoft.common.schema.common.domain.vo.LaElMainVo;
import com.sinosoft.common.service.ILaElMainService;
import com.sinosoft.common.utils.rule.RuleTree;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 规则Service业务层处理
 *
 * @author zzf
 * @date 2023-06-21
 */
@RequiredArgsConstructor
@Service
public class LaElMainServiceImpl implements ILaElMainService {

    /**
     * 规则主对象
     */
    private final LaElMainMapper baseMapper;
    /**
     * 规则明细对象
     */
    private final LaElRuleMapper laElRuleMapper;
    /**
     * redis 缓存key
     */
    private final static String RULE_REDIS_KEY = "RULE_REDIS_KEY";


    @Override
    public TableDataInfo<LaElMain> queryPageList(LaElMain bo, PageQuery pageQuery) {
        LambdaQueryWrapper<LaElMain> lqw = Wrappers.lambdaQuery();
        Page<LaElMain> result = baseMapper.selectPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public LaElMainVo queryById(Long id) {
        LaElMain laElMain = baseMapper.selectById(id);
        LaElMainVo laElMainVo = MapstructUtils.convert(laElMain, LaElMainVo.class);
        return laElMainVo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean insertElMain(LaElMainBo elMain) {
        LaElMain laElMain = MapstructUtils.convert(elMain, LaElMain.class);
        assert laElMain != null;
        laElMain.setCreateDate(new Date());
        return baseMapper.insert(laElMain) > 0;
    }

    @Override
    public Boolean updateByBo(LaElMainBo elMainBo) {
        LaElMain laElMain = MapstructUtils.convert(elMainBo, LaElMain.class);
        assert laElMain != null;
        return baseMapper.updateById(laElMain) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean insertRuleInfo(LaElMain elMain, List<LaElRule> elRuleList) {
        //保存信息
        if (baseMapper.insert(elMain) > 0) {
            //设置主键
            elRuleList.forEach(elRule -> elRule.setElMainId(elMain.getId()));
            //保存明细
            laElRuleMapper.insertBatch(elRuleList);
            //声明规则树
            RuleTree ruleTree = new RuleTree(elMain);
            //初始化规则树
            ruleTree.initTree(elRuleList);
            //保存redis
            saveCache(elMain.getCalCode(), ruleTree);
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteRuleMain(String calCode) {
        //获取规则主对象
        LaElMain elMain = getRuleMainByData(calCode);
        //不为空
        if (elMain != null) {
            //删除规则主对象
            baseMapper.deleteById(elMain.getId());
            //删除规则明细对象
            LambdaQueryWrapper<LaElRule> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(LaElRule::getElMainId, elMain.getId());
            laElRuleMapper.delete(queryWrapper);
            //删除redis
            delCache(calCode);
            return true;
        }
        return false;
    }

    /**
     * 获取规则主对象
     * 数据库查询
     *
     * @param calCode 计算代码
     * @return 规则主对象
     */
    @Override
    public LaElMain getRuleMainByData(String calCode) {
        LambdaQueryWrapper<LaElMain> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LaElMain::getCalCode, calCode);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 获取规则主对象
     * redis查询
     *
     * @param calCode 计算代码
     * @return 规则主对象
     */
    @Override
    public LaElMain getRuleMainByCache(String calCode) {
        return RedisUtils.getCacheObject(RULE_REDIS_KEY + ":MAIN:" + calCode);
    }


    @Override
    public RuleTree getRuleListByCode(String calCode) {
        //查询数据库
        LaElMain dataElMain = getRuleMainByData(calCode);
        //查询redis
        LaElMain cacheElMain = getRuleMainByCache(calCode);
        //对象不为空
        if (dataElMain != null) {
            //规则配置为空 或者 版本号不一致 重新加载
            if (cacheElMain == null || !dataElMain.getCalVersion().equals(cacheElMain.getCalVersion())) {
                // 版本号不一致或缓存为空，更新Redis
                List<LaElRule> elRuleList = getRuleListData(dataElMain.getId());
                RuleTree ruleTree = new RuleTree(dataElMain);
                ruleTree.initTree(elRuleList);
                //缓存
                saveCache(calCode, ruleTree);
                return ruleTree;
            } else {
                // 版本号一致，直接返回Redis
                return RedisUtils.getCacheObject(RULE_REDIS_KEY + ":DETAIL:" + calCode);
            }
        }
        return null;
    }

    /**
     * 查询规则明细对象
     *
     * @param elMainId 规则主对象id
     * @return 规则明细对象
     */
    private List<LaElRule> getRuleListData(Long elMainId) {
        LambdaQueryWrapper<LaElRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LaElRule::getElMainId, elMainId);
        List<LaElRule> elRuleList = laElRuleMapper.selectList(queryWrapper);
        return elRuleList;
    }

    /**
     * 缓存保存
     *
     * @param calCode  计算代码
     * @param ruleTree 规则树
     */
    private void saveCache(String calCode, RuleTree ruleTree) {
        //保存redis
        RedisUtils.setCacheObject(RULE_REDIS_KEY + ":TREE:" + calCode, ruleTree);
    }

    /**
     * 删除缓存
     *
     * @param calCode 计算代码
     */
    private void delCache(String calCode) {
        //删除redis
        RedisUtils.deleteObject(RULE_REDIS_KEY + ":TREE:" + calCode);
    }

    /**
     * 校验计算代码重复
     *
     * @param calCode 计算代码
     * @return true重复
     */
    public Boolean checkCalCodeRepeat(String calCode) {
        LambdaQueryWrapper<LaElMain> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(LaElMain::getId);
        queryWrapper.eq(LaElMain::getCalCode, calCode);
        queryWrapper.last("limit 1");
        return baseMapper.selectOne(queryWrapper) != null;
    }

    /**
     * 校验计算代码重复
     *
     * @param name 说明
     * @return true重复
     */
    public Boolean checkNameRepeat(String name) {
        LambdaQueryWrapper<LaElMain> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(LaElMain::getId);
        queryWrapper.eq(LaElMain::getMessage, name);
        queryWrapper.last("limit 1");
        return baseMapper.selectOne(queryWrapper) != null;
    }
}
