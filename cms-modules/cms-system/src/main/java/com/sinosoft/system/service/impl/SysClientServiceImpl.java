package com.sinosoft.system.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.SM2;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sinosoft.common.json.utils.JsonUtils;
import com.sinosoft.system.api.domain.vo.ApiPermissionVo;
import com.sinosoft.system.api.domain.vo.ClientConfigVo;
import com.sinosoft.system.domain.bo.SysClientFormBo;
import com.sinosoft.system.domain.vo.KeyPairVo;
import com.sinosoft.system.domain.vo.SysClientDetailVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.core.constant.CacheNames;
import com.sinosoft.common.core.utils.MapstructUtils;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.system.domain.SysClient;
import com.sinosoft.system.domain.bo.SysClientBo;
import com.sinosoft.system.domain.vo.SysClientVo;
import com.sinosoft.system.mapper.SysClientMapper;
import com.sinosoft.system.service.ISysClientService;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 客户端管理Service业务层处理
 *
 * @author Michelle.Chung
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysClientServiceImpl implements ISysClientService {

    private final SysClientMapper baseMapper;

    /**
     * 查询客户端管理
     */
    @Override
    public SysClientDetailVo queryById(Long id) {
        // 获取基本数据
        SysClient entity = baseMapper.selectById(id);
        if (entity == null) {
            return null;
        }
        // 转换为DetailVo
        SysClientDetailVo detailVo = new SysClientDetailVo();
        // 复制基本属性
        BeanUtils.copyProperties(entity, detailVo);
        // 设置授权类型列表
        if (StringUtils.isNotBlank(entity.getGrantType())) {
            detailVo.setGrantTypeList(List.of(entity.getGrantType().split(",")));
        }
        // 设置配置参数（从JSON字符串转换为对象）
        if (StringUtils.isNotBlank(entity.getConfig())) {
            try {
                ClientConfigVo configVo = JsonUtils.parseObject(entity.getConfig(), ClientConfigVo.class);
                if (StringUtils.isNotBlank(configVo.getSm2PrivateKey())) {
                    configVo.setSm2PrivateKey(maskPrivateKey(configVo.getSm2PrivateKey()));
                }
                detailVo.setConfig(configVo);
            } catch (Exception e) {
                log.error("解析客户端配置失败", e);
            }
        }
        // 设置API权限列表（从JSON字符串转换为对象列表）
        if (StringUtils.isNotBlank(entity.getApiPermissions())) {
            try {
                List<ApiPermissionVo> permissionList = JsonUtils.parseArray(entity.getApiPermissions(), ApiPermissionVo.class);
                detailVo.setApiPermissions(permissionList);
            } catch (Exception e) {
                log.error("解析API权限列表失败", e);
            }
        }

        return detailVo;
    }


    /**
     * 查询客户端管理
     */
    @Cacheable(cacheNames = CacheNames.SYS_CLIENT, key = "#clientId")
    @Override
    public SysClientVo queryByClientId(String clientId) {
        return baseMapper.selectVoOne(new LambdaQueryWrapper<SysClient>().eq(SysClient::getClientId, clientId));
    }

    /**
     * 查询客户端管理列表
     */
    @Override
    public TableDataInfo<SysClientVo> queryPageList(SysClientBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysClient> lqw = buildQueryWrapper(bo);
        Page<SysClientVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        result.getRecords().forEach(r -> r.setGrantTypeList(List.of(r.getGrantType().split(","))));
        return TableDataInfo.build(result);
    }

    /**
     * 查询客户端管理列表
     */
    @Override
    public List<SysClientVo> queryList(SysClientBo bo) {
        LambdaQueryWrapper<SysClient> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysClient> buildQueryWrapper(SysClientBo bo) {
        LambdaQueryWrapper<SysClient> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getClientId()), SysClient::getClientId, bo.getClientId());
        lqw.eq(StringUtils.isNotBlank(bo.getClientKey()), SysClient::getClientKey, bo.getClientKey());
        lqw.eq(StringUtils.isNotBlank(bo.getClientSecret()), SysClient::getClientSecret, bo.getClientSecret());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SysClient::getStatus, bo.getStatus());
        lqw.orderByAsc(SysClient::getId);
        return lqw;
    }

    /**
     * 新增客户端管理
     */
    @Override
    public Boolean insertByBo(SysClientFormBo bo) {
        // 创建新的客户端实体
        SysClient add = new SysClient();
        BeanUtils.copyProperties(bo, add);
        // 处理授权类型列表
        if (bo.getGrantTypeList() != null && !bo.getGrantTypeList().isEmpty()) {
            add.setGrantType(String.join(",", bo.getGrantTypeList()));
        }
        // 处理配置数据
        if (bo.getConfig() != null) {
            add.setConfig(JSON.toJSONString(bo.getConfig()));
        }
        // 处理API权限列表
        if (bo.getApiPermissions() != null) {
            add.setApiPermissions(JSON.toJSONString(bo.getApiPermissions()));
        }
        // 生成clientId
        String clientKey = bo.getClientKey();
        String clientSecret = bo.getClientSecret();
        add.setClientId(SecureUtil.md5(clientKey + clientSecret));
        // 数据校验
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
            bo.setClientId(add.getClientId());
        }
        return flag;
    }

    /**
     * 修改客户端管理
     */
    @CacheEvict(cacheNames = CacheNames.SYS_CLIENT, key = "#bo.clientId")
    @Override
    public Boolean updateByBo(SysClientFormBo bo) {
        // 获取原始客户端数据
        SysClient originalClient = baseMapper.selectById(bo.getId());
        if (originalClient == null) {
            throw new RuntimeException("客户端不存在");
        }
        // 将表单数据转换为实体对象
        SysClient update = new SysClient();
        BeanUtils.copyProperties(bo, update);
        // 处理授权类型列表
        if (bo.getGrantTypeList() != null && !bo.getGrantTypeList().isEmpty()) {
            update.setGrantType(String.join(",", bo.getGrantTypeList()));
        }
        // 处理配置数据
        if (bo.getConfig() != null) {
            // 如果是通过前端表单编辑的配置信息
            ClientConfigVo configVo = bo.getConfig();
            // 获取原始配置
            ClientConfigVo originalConfig = null;
            if (StringUtils.isNotBlank(originalClient.getConfig())) {
                originalConfig = JSON.parseObject(originalClient.getConfig(), ClientConfigVo.class);
            } else {
                originalConfig = new ClientConfigVo();
            }
            // 保留原始的SM2密钥信息，不从前端更新
            configVo.setSm2PublicKey(originalConfig.getSm2PublicKey());
            configVo.setSm2PrivateKey(originalConfig.getSm2PrivateKey());
            // 更新配置
            update.setConfig(JSON.toJSONString(configVo));
        }
        // 处理API权限列表
        if (bo.getApiPermissions() != null) {
            update.setApiPermissions(JSON.toJSONString(bo.getApiPermissions()));
        }
        // 数据校验
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 修改状态
     */
    @CacheEvict(cacheNames = CacheNames.SYS_CLIENT, key = "#clientId")
    @Override
    public int updateClientStatus(String clientId, String status) {
        return baseMapper.update(null,
            new LambdaUpdateWrapper<SysClient>()
                .set(SysClient::getStatus, status)
                .eq(SysClient::getClientId, clientId));
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysClient entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除客户端管理
     */
    @CacheEvict(cacheNames = CacheNames.SYS_CLIENT, allEntries = true)
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 为指定客户端生成SM2密钥对并保存
     *
     * @param clientId 客户端ID
     * @param encrypt  是否加密私钥
     * @return 生成的密钥对
     */
    @CacheEvict(cacheNames = CacheNames.SYS_CLIENT, key = "#clientId")
    @Override
    public KeyPairVo generateAndSaveSm2KeyPair(String clientId, boolean encrypt) {
        // 查询客户端信息
        SysClient client = baseMapper.selectOne(
            new LambdaQueryWrapper<SysClient>().eq(SysClient::getClientId, clientId)
        );
        if (client == null) {
            throw new RuntimeException("客户端不存在");
        }
        // 生成SM2密钥对
        KeyPairVo keyPair = generateSm2KeyPair(encrypt);
        // 获取当前配置
        ClientConfigVo configVo;
        if (StringUtils.isNotBlank(client.getConfig())) {
            configVo = JSON.parseObject(client.getConfig(), ClientConfigVo.class);
        } else {
            configVo = new ClientConfigVo();
        }
        // 更新配置中的SM2密钥
        configVo.setSm2PublicKey(keyPair.getPublicKey());
        configVo.setSm2PrivateKey(keyPair.getRawPrivateKey()); // 保存完整私钥
        configVo.setEncryptionMode("SM2");
        configVo.setEncryptEnabled(encrypt);
        // 保存到数据库
        client.setConfig(JSON.toJSONString(configVo));
        baseMapper.updateById(client);
        // 返回脱敏后的密钥对
        return keyPair;
    }

    /**
     * 生成SM2密钥对
     *
     * @param encrypt 是否加密私钥
     * @return 密钥对
     */
    private KeyPairVo generateSm2KeyPair(boolean encrypt) {
        // 创建SM2算法对象
        SM2 sm2 = SmUtil.sm2();
        // 获取公钥和私钥
        String publicKey = Base64.encode(sm2.getPublicKey().getEncoded());
        String privateKey = Base64.encode(sm2.getPrivateKey().getEncoded());
        // 如果需要加密私钥
        String rawPrivateKey = privateKey;
        if (encrypt) {
            privateKey = encryptPrivateKey(privateKey);
        }
        // 对私钥进行脱敏处理
        String maskedPrivateKey = maskPrivateKey(privateKey);
        KeyPairVo keyPair = new KeyPairVo();
        keyPair.setPublicKey(publicKey);
        keyPair.setPrivateKey(maskedPrivateKey);
        keyPair.setRawPrivateKey(rawPrivateKey); // 原始私钥，用于保存到数据库

        return keyPair;
    }

    /**
     * 对私钥进行脱敏处理
     * 只显示前10位和后10位，中间用星号替代
     */
    private String maskPrivateKey(String privateKey) {
        if (privateKey == null || privateKey.length() <= 20) {
            return privateKey;
        }
        int length = privateKey.length();
        return privateKey.substring(0, 10) + "****" + privateKey.substring(length - 10);
    }

    /**
     * 对私钥进行加密处理
     * 使用系统内置的密钥进行加密
     */
    private String encryptPrivateKey(String privateKey) {
        try {
            // 这里使用系统固定密钥加密，实际使用中应从配置或安全存储获取
            String encryptKey = "abcdefghijklmnopqrstuvwxyz";
            // 使用AES加密私钥
            return HexUtil.encodeHexStr(cn.hutool.crypto.SecureUtil.aes(encryptKey.getBytes()).encrypt(privateKey));
        } catch (Exception e) {
            log.error("加密私钥失败", e);
            return privateKey;
        }
    }
}
