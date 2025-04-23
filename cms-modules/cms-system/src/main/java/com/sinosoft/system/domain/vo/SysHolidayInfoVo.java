package com.sinosoft.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.baomidou.mybatisplus.annotation.TableId;
import com.sinosoft.system.domain.SysHolidayInfo;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysHolidayInfo.class)
public class SysHolidayInfoVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 节假日id
     */
    @TableId(value = "holiday_id")
    private String  holidayId;

    /**
     *
     * 时间
     */
    private String date;

    /**
     *节假日名称
     */
    private String  holidayName;

    /**
     * 是否为节假日
     */
    private  boolean   is_holiday;

    /**
     *
     * 加班工资倍数
     **/
    private int wage;

    /**
     * 调休天数
     */

    private int rest;

    /**
     * 备注
     */
    private String  Remark;

}
