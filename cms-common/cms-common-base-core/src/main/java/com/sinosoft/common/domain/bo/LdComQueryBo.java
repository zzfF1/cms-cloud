package com.sinosoft.common.domain.bo;

import com.sinosoft.common.schema.common.domain.Ldcom;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.util.List;

/**
 * @program: cms6
 * @description: 管理机构查询
 * @author: zzf
 * @create: 2023-10-02 09:52
 */
@Data
@AutoMapper(target = Ldcom.class, reverseConvertGenerate = false)
public class LdComQueryBo {
    /**
     * 管理机构代码
     */
    private String comCode;
    /**
     * 管理机构名称
     */
    private String name;
    /**
     * 管理机构级别
     */
    private List<String> comGrades;
    /**
     * 当前用户管理机构
     */
    private String curUserManageCom;
    /**
     * 显示级别
     */
    private String level;
}
