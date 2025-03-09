package com.sinosoft.common.domain.dto;

import cn.hutool.crypto.digest.DigestUtil;
import com.sinosoft.common.enums.BranchTypeEnum;
import com.sinosoft.common.enums.IndexcalTypeEnum;
import com.sinosoft.common.enums.WageTypeEnum;
import com.sinosoft.system.api.model.LoginUser;
import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * 佣金计算消息
 *
 * @author: zzf
 * @create: 2023-07-16 10:19
 */
@ToString
@Data
public class CommissionCalMainDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -6347456061843759083L;
    /**
     * 管理机构
     */
    private String manageCom;
    /**
     * 渠道
     */
    private BranchTypeEnum branchType;
    /**
     * 佣金年月
     */
    private String indexCalNo;
    /**
     * 计算类型 {@link IndexcalTypeEnum}
     */
    private IndexcalTypeEnum indexCalType;
    /**
     * 佣金类型
     */
    private WageTypeEnum wageType;
    /**
     * 基本法版本号
     */
    private Long baseLawVersion;
    /**
     * 消息唯一标识
     * 渠道/佣金年月/计算类型/佣金类型
     */
    private String md5Val;
    /**
     * 运行ID
     */
    private String uuid;
    /**
     * 操作人
     */
    private LoginUser loginUser;

    /**
     * 生成md5唯一标识
     */
    public void generateMd5Val() {
        //生成md5唯一标识
        String input = branchType + indexCalNo + indexCalType.getCode() + wageType.getCode() + manageCom;
        byte[] md5 = DigestUtil.md5(input);

        String hexMd5 = DigestUtil.md5Hex(md5);
        this.setMd5Val(hexMd5.substring(8, 24));
    }

    /**
     * 生成运行ID
     */
    public void generateUuid() {
        this.uuid = UUID.randomUUID().toString().replace("-", "");
    }
}
