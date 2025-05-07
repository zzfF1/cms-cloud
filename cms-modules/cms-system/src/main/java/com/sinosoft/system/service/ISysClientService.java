package com.sinosoft.system.service;

import com.sinosoft.common.core.constant.CacheNames;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.system.domain.bo.SysClientBo;
import com.sinosoft.system.domain.bo.SysClientFormBo;
import com.sinosoft.system.domain.vo.KeyPairVo;
import com.sinosoft.system.domain.vo.SysClientDetailVo;
import com.sinosoft.system.domain.vo.SysClientVo;
import org.springframework.cache.annotation.CacheEvict;

import java.util.Collection;
import java.util.List;

/**
 * 客户端管理Service接口
 *
 * @author Michelle.Chung
 */
public interface ISysClientService {

    /**
     * 查询客户端管理
     */
    SysClientDetailVo queryById(Long id);

    /**
     * 查询客户端信息基于客户端id
     */
    SysClientVo queryByClientId(String clientId);

    /**
     * 查询客户端管理列表
     */
    TableDataInfo<SysClientVo> queryPageList(SysClientBo bo, PageQuery pageQuery);

    /**
     * 查询客户端管理列表
     */
    List<SysClientVo> queryList(SysClientBo bo);

    /**
     * 新增客户端管理
     */
    Boolean insertByBo(SysClientFormBo bo);

    /**
     * 修改客户端管理
     */
    Boolean updateByBo(SysClientFormBo bo);

    /**
     * 修改状态
     */
    int updateClientStatus(String clientId, String status);

    /**
     * 校验并批量删除客户端管理信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 生成sm2密钥并保存
     *
     * @param clientId 客户端ID
     * @param encrypt  是否加密
     * @return
     */
    KeyPairVo generateAndSaveSm2KeyPair(String clientId, boolean encrypt);
}
