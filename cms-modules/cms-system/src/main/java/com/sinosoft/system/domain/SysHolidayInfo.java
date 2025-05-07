package com.sinosoft.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sinosoft.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_holiday_info")
public class SysHolidayInfo extends BaseEntity {

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
    private  boolean   isHoliday;


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
