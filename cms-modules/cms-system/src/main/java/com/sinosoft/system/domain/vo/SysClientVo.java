package com.sinosoft.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import com.sinosoft.common.excel.annotation.ExcelDictFormat;
import com.sinosoft.common.excel.convert.ExcelDictConvert;
import com.sinosoft.system.domain.SysClient;

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
public class SysClientVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ExcelProperty(value = "id")
    private Long id;

    /**
     * 客户端id
     */
    @ExcelProperty(value = "客户端id")
    private String clientId;

    /**
     * 客户端key
     */
    @ExcelProperty(value = "客户端key")
    private String clientKey;

    /**
     * 客户端秘钥
     */
    @ExcelProperty(value = "客户端秘钥")
    private String clientSecret;

    /**
     * 授权类型
     */
    @ExcelProperty(value = "授权类型")
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
     * 服务处理状态（0服务者 1调用者 ）
     */
    private String serviceType;

    /**
     * 客户端配置参数
     */
    private String config;

    /**
     * API 访问权限列表
     */
    private String apiPermissions;

    /**
     * token活跃超时时间
     */
    @ExcelProperty(value = "token活跃超时时间")
    private Long activeTimeout;

    /**
     * token固定超时时间
     */
    @ExcelProperty(value = "token固定超时时间")
    private Long timeout;

    /**
     * 状态（0正常 1停用）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=正常,1=停用")
    private String status;


}
