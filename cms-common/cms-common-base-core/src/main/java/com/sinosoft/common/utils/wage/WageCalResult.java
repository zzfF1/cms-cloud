package com.sinosoft.common.utils.wage;

import cn.hutool.core.util.IdUtil;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @program: cms6
 * @description: 佣金计算结果
 * @author: zzf
 * @create: 2023-07-16 15:08
 */
@Data
public class WageCalResult implements Serializable {
    @Serial
    private static final long serialVersionUID = -7069178911428581744L;
    /**
     * 结果
     */
    private Object resultVal;
    /**
     * 计算过程
     */
    private List<WageIndexInfo> resultProcess = new ArrayList<>();

    /**
     * 添加过程对象
     *
     * @param wageIndexInfo
     */
    public void addProcess(WageIndexInfo wageIndexInfo) {
        this.resultProcess.add(wageIndexInfo);
    }

    /**
     * 是否有过程
     *
     * @return true有 false无
     */
    public boolean haveProcess() {
        return this.resultProcess.size() > 0;
    }

    /**
     * 添加过程对象
     *
     * @param elements  计算元素值
     * @param agentCode 业务员
     * @param wageNo    佣金年月
     * @param key       流水ID
     * @param vals      结果集
     */
    public void addProcess(WageCalElements elements, String agentCode, String wageNo, int key, String... vals) {
        this.add(elements.getCalElementsCode(), agentCode, wageNo, String.valueOf(key), elements.getElementCount(), vals);
    }

    /**
     * 添加过程对象
     *
     * @param elements  计算元素值
     * @param agentCode 业务员
     * @param wageNo    佣金年月
     * @param vals      结果集
     */
    public void addProcess(WageCalElements elements, String agentCode, String wageNo, String... vals) {
        this.add(elements.getCalElementsCode(), agentCode, wageNo, IdUtil.getSnowflakeNextId() + "", elements.getElementCount(), vals);
    }

    /**
     * 添加过程对象
     *
     * @param elemCode   计算元素值
     * @param agentCode  业务员
     * @param wageNo     佣金年月
     * @param key        流水ID
     * @param resultSize 结果个数
     * @param vals       结果
     */
    private void add(String elemCode, String agentCode, String wageNo, String key, int resultSize, String... vals) {
        // 初始化用于存储结果的数组
        String[] resultValues = new String[resultSize];
//        // 计算总页数
//        int totalPages = (resultSize + WageIndexInfo.COLUMN_MAX - 1) / WageIndexInfo.COLUMN_MAX;
        // 循环遍历结果并进行处理
        int currentPage = 0;
        for (int rowIndex = 0; rowIndex < resultSize; rowIndex++) {
            if (rowIndex < vals.length) {
                resultValues[rowIndex] = vals[rowIndex];
            } else {
                resultValues[rowIndex] = "";
            }
            //循环
            if ((rowIndex + 1) % WageIndexInfo.COLUMN_MAX == 0 || rowIndex == resultSize - 1) {
                int startIdx = currentPage * WageIndexInfo.COLUMN_MAX;
                int endIdx = Math.min((currentPage + 1) * WageIndexInfo.COLUMN_MAX, resultSize);
                // 创建新的 WageIndexInfo 并设置其属性
                WageIndexInfo wageIndexInfo = new WageIndexInfo();
                wageIndexInfo.setAgentCode(agentCode);
                wageIndexInfo.setWageNo(wageNo);
                wageIndexInfo.setCalElements(elemCode);
                wageIndexInfo.setKey(key);
                wageIndexInfo.setIndex(currentPage);
                // 将相关数据复制到 WageIndexInfo
                wageIndexInfo.setValues(Arrays.copyOfRange(resultValues, startIdx, endIdx));
                this.addProcess(wageIndexInfo);

                currentPage++;
            }
        }
    }
}
