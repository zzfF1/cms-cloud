package com.sinosoft.notice.domain.dto;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 待办查询结果数据传输对象
 *
 * @author zzf
 */
@Data
@NoArgsConstructor
public class TodoResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 待办数量
     */
    private Integer count;

    /**
     * 规则ID
     */
    private Long ruleId;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 菜单ID
     */
    private Long menuId;

    /**
     * 菜单URL
     */
    private String menuUrl;

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 模板代码
     */
    private String templateCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 优先级
     */
    private String priority;
}
