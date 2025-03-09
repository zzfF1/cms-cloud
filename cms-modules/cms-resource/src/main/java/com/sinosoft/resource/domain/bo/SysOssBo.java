package com.sinosoft.resource.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import com.sinosoft.resource.domain.SysOss;

/**
 * OSS对象存储分页查询对象 sys_oss
 *
 * @author zzf
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysOss.class, reverseConvertGenerate = false)
public class SysOssBo extends BaseEntity {

    /**
     * ossId
     */
    private Long ossId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原名
     */
    private String originalName;

    /**
     * 文件后缀名
     */
    private String fileSuffix;

    /**
     * URL地址
     */
    private String url;

    /**
     * 服务商
     */
    private String service;

}
