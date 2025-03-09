package com.sinosoft.common.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.sinosoft.common.mybatis.core.domain.BaseEntity2;

import java.io.Serial;

/**
 * 业务帮助文档对象 business_help_docs
 *
 * @author zzf
 * @date 2023-11-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("business_help_docs")
public class BusinessHelpDocs extends BaseEntity2 {

    @Serial
    private static final long serialVersionUID = 1570047938426685476L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 业务编码
     */
    private String busCode;
    /**
     * 渠道
     */
    private String branchType;
    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;


}
