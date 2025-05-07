package com.sinosoft.system.domain.bo;

import com.sinosoft.common.schema.agent.domain.Laagentgrade;
import com.sinosoft.common.schema.inermediary.domain.Lacom;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 人员查询对象
 *
 * @author zzf
 * @date 2023-11-13
 */
@Data
@AutoMapper(target = Laagentgrade.class, reverseConvertGenerate = false)
public class LaGradeQueryBo {
    /**
     * 渠道
     */
    @NotBlank(message = "渠道")
    private String branchType;
}
