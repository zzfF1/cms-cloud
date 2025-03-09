package com.sinosoft.common.domain.dto;

import cn.hutool.crypto.digest.DigestUtil;
import com.sinosoft.common.enums.IndexcalTypeEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: cms6
 * @description: 佣金计算消息
 * @author: zzf
 * @create: 2023-07-16 10:19
 */
@Data
public class WageCalMainDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -469818748518521038L;
    /**
     * 管理机构
     */
    private String manageCom;
    /**
     * 渠道
     */
    private String branchType;
    /**
     * 佣金年月
     */
    private String indexCalNo;
    /**
     * 计算类型 {@link IndexcalTypeEnum}
     */
    private String indexCalType;
    /**
     * 佣金类型
     */
    private String wageType;
    /**
     * 计算人
     */
    private String operator;
    /**
     * 操作人主键
     */
    private Long userId;
    /**
     * 版本号
     */
    private int version;
    /**
     * 计算次数
     */
    private Long calCount = 0L;
    /**
     * 计算人数
     */
    private Long calAgentCount = 0L;
    /**
     * 延时时间 默认30秒
     */
    private Long delayTime = 30000L;
    /**
     * 佣金计算历史编号
     */
    private Long historyNo;
    /**
     * 消息唯一标识
     * 渠道/佣金年月/计算类型/佣金类型
     */
    private String md5Val;
    /**
     * 佣金计算完成队列名称
     */
    private String finishQueue;

    /**
     * 生成md5唯一标识
     */
    public void generateMd5Val() {
        //生成md5唯一标识
        String input = branchType + indexCalNo + indexCalType + wageType + manageCom;
        byte[] md5 = DigestUtil.md5(input);

        String hexMd5 = DigestUtil.md5Hex(md5);
        this.setMd5Val(hexMd5.substring(8, 24));
    }

    public static void main(String[] args) {
        WageCalMainDto dto = new WageCalMainDto();
        dto.setBranchType("1");
        dto.setIndexCalNo("202307");
        dto.setIndexCalType("10");
        dto.setWageType("1");
        dto.setOperator("admin");
        dto.setUserId(1L);
        dto.setCalCount(0L);
        dto.setCalAgentCount(0L);
        dto.setDelayTime(2000L);
        dto.generateMd5Val();
        System.out.println(dto.getMd5Val());
    }
}
