package com.sinosoft.common.domain.dto;

import lombok.Data;
import com.sinosoft.common.domain.ExcelConfigBase;
import com.sinosoft.common.domain.ExcelSheetBase;

import java.util.List;

/**
 * @program: cms6
 * @description: excel配置
 * @author: zzf
 * @create: 2024-01-06 17:01
 */
@Data
public class ExcelConfigDto {
    /**
     * excel配置
     */
    private ExcelConfigBase configBase;
    /**
     * excel sheet配置
     */
    private List<ExcelSheetBase> sheetBases;
}
