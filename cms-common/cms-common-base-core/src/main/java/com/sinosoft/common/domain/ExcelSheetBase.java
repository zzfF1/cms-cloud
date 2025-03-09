package com.sinosoft.common.domain;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import com.sinosoft.common.excel.HeadEntity;
import com.sinosoft.common.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: cms6
 * @description: excel配置对象
 * @author: zzf
 * @create: 2024-01-06 17:02
 */
@Data
public class ExcelSheetBase {

    /**
     * sheet索引
     */
    private int sheetIndex;
    /**
     * 标题
     */
    private String title;
    /**
     * sheet名称
     */
    private String sheetName;
    /**
     * 表头行
     */
    private int headRow;
    /**
     * 表头配置List对象
     */
    private List<HeadEntity> headEntities;
    /**
     * 表头配置map对象
     */
    private Map<Integer, HeadEntity> headEntitiesMap;
    /**
     * 表头文本
     */
    private List<List<String>> headTitles;
    /**
     * 数据
     */
    private List<List<String>> dataList;

    /**
     * 转换对象
     * @param headEntities 标题对象
     */
    public void convert(List<HeadEntity> headEntities) {
        //标题行
        int maxHeadRow = 1;
        Map<Integer, HeadEntity> headEntityMap = new HashMap<>();
        List<List<String>> headTitles = new ArrayList<>();
        //循环配置 初始化参数
        for (HeadEntity headEntity : headEntities) {
            headEntityMap.put(headEntity.getIndex(), headEntity);
            headTitles.add(CollUtil.newArrayList(headEntity.getHead().split(",")));
            headEntityMap.put(headEntity.getIndex(), headEntity);
            if (StringUtils.isNotBlank(headEntity.getHead())) {
                maxHeadRow = Math.max(maxHeadRow, headEntity.getHead().split(",").length);
            }
        }
        this.setHeadEntities(headEntities);
        this.setHeadEntitiesMap(headEntityMap);
        this.setHeadTitles(headTitles);
        this.setHeadRow(maxHeadRow);
    }
}
