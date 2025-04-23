package com.sinosoft.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.sinosoft.common.excel.annotation.ExcelDictFormat;
import com.sinosoft.common.excel.convert.ExcelDictConvert;
import com.sinosoft.system.api.domain.vo.ApiPermissionVo;
import com.sinosoft.system.api.domain.vo.ClientConfigVo;
import com.sinosoft.system.domain.SysClient;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


/**
 * 授权管理视图对象 sys_client
 *
 * @author Michelle.Chung
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysClient.class)
public class SysClientDetailVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 客户端key
     */
    private String clientKey;

    /**
     * 客户端秘钥
     */
    private String clientSecret;

    /**
     * 授权类型
     */
    private List<String> grantTypeList;

    /**
     * 授权类型
     */
    private String grantType;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * token活跃超时时间
     */
    private Long activeTimeout;

    /**
     * token固定超时时间
     */
    private Long timeout;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 配置参数
     */
    private ClientConfigVo clientConfig;
    /**
     * 权限列表
     */
    private List<ApiPermissionVo> permissionVosList;
}
