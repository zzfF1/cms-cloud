package com.sinosoft.system.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.sinosoft.common.excel.annotation.ExcelDictFormat;
import com.sinosoft.common.excel.convert.ExcelDictConvert;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户对象导入VO
 *
 * @author zzf
 */
@Data
@NoArgsConstructor
public class SysUserImportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户序号")
    private Long userId;

    /**
     * 部门ID
     */
    @ExcelProperty(value = "部门编号")
    private Long deptId;

    /**
     * 用户账号
     */
    @ExcelProperty(value = "登录名称")
    private String userName;

    /**
     * 用户昵称
     */
    @ExcelProperty(value = "用户昵称")
    private String nickName;

    /**
     * 真实姓名
     */
    @ExcelProperty(value = "真实姓名")
    private String realName;

    /**
     * 用户邮箱
     */
    @ExcelProperty(value = "用户邮箱")
    private String email;

    /**
     * 手机号码
     */
    @ExcelProperty(value = "手机号码")
    private String phonenumber;

    /**
     * 用户性别
     */
    @ExcelProperty(value = "用户性别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_user_sex")
    private String sex;

    /**
     * 帐号状态（0正常 1停用）
     */
    @ExcelProperty(value = "帐号状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private String status;

    /**
     * 渠道类型
     */
    @ExcelProperty(value = "渠道类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "branchtype")
    private String branchType;

    /**
     * 证件号码
     */
    @ExcelProperty(value = "证件号码")
    private String idNo;

    /**
     * 账号绑定IP
     */
    @ExcelProperty(value = "绑定IP")
    private String bindIp;

    /**
     * 允许访问时间开始
     */
    @ExcelProperty(value = "访问开始时间")
    private String accessStartTime;

    /**
     * 允许访问时间结束
     */
    @ExcelProperty(value = "访问结束时间")
    private String accessEndTime;

    /**
     * 账户类型（0长期用户 1临时用户）
     */
    @ExcelProperty(value = "账户类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "user_acc_type")
    private Integer accType;

    /**
     * 账户有效起期
     */
    @ExcelProperty(value = "有效起期")
    private String validStartDate;

    /**
     * 账户有效止期
     */
    @ExcelProperty(value = "有效止期")
    private String validEndDate;
}
