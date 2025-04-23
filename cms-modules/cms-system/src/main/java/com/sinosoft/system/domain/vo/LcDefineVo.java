package com.sinosoft.system.domain.vo;

import com.sinosoft.common.schema.common.domain.LcDefine;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 流程定义
 *
 * @author: zzf
 * @create: 2023-11-16 11:41
 */
@Data
@AutoMapper(target = LcDefine.class, reverseConvertGenerate = true)
public class LcDefineVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Integer id;
    /**
     * 流程名称
     */
    private String name;
    /**
     * 顺序号
     */
    private Integer recno;
    /**
     * 下一个流程节点
     */
    private Integer nextId;
    /**
     * 流程动作
     */
    private List<LcDzVo> actions;
    /**
     * 流程检查
     */
    private List<LcCheckVo> checks;
    /**
     * 流程跳转
     */
    private List<LcTzVo> jumps;
}
