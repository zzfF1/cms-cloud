package com.sinosoft.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.sinosoft.common.core.utils.MapstructUtils;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.common.schema.common.domain.SysImportConfig;
import com.sinosoft.common.schema.common.domain.SysImportConfigItem;
import com.sinosoft.common.schema.common.mapper.SysImportConfigItemMapper;
import com.sinosoft.common.schema.common.mapper.SysImportConfigMapper;
import com.sinosoft.system.domain.bo.SysImportConfigBo;
import com.sinosoft.system.domain.bo.SysImportConfigItemBo;
import com.sinosoft.system.domain.vo.SysImportConfigItemVo;
import com.sinosoft.system.domain.vo.SysImportConfigVo;
import com.sinosoft.system.service.ISysImportConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * excel导入模板Service业务层处理
 *
 * @author zzf
 * @date 2024-01-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysImportConfigServiceImpl implements ISysImportConfigService {

    private final SysImportConfigMapper baseMapper;
    private final SysImportConfigItemMapper itemMapper;

    /**
     * 查询excel导入模板
     */
    @Override
    public SysImportConfigVo queryById(Long id) {
        SysImportConfigVo configVo = baseMapper.selectVoById(id, SysImportConfigVo.class);
        if (configVo != null) {
            // 查询关联的配置项
            LambdaQueryWrapper<SysImportConfigItem> itemWrapper = Wrappers.lambdaQuery();
            itemWrapper.eq(SysImportConfigItem::getConfigId, id);
            itemWrapper.orderByAsc(SysImportConfigItem::getSheetIndex)
                .orderByAsc(SysImportConfigItem::getSort);
            List<SysImportConfigItemVo> itemList = itemMapper.selectVoList(itemWrapper, SysImportConfigItemVo.class);
            configVo.setConfigItems(itemList);
        }
        return configVo;
    }

    /**
     * 查询excel导入模板列表
     */
    @Override
    public TableDataInfo<SysImportConfigVo> queryPageList(SysImportConfigBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysImportConfig> lqw = buildQueryWrapper(bo);
        Page<SysImportConfigVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw, SysImportConfigVo.class);
        return TableDataInfo.build(result);
    }

    /**
     * 查询excel导入模板列表
     */
    @Override
    public List<SysImportConfigVo> queryList(SysImportConfigBo bo) {
        LambdaQueryWrapper<SysImportConfig> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw, SysImportConfigVo.class);
    }

    private LambdaQueryWrapper<SysImportConfig> buildQueryWrapper(SysImportConfigBo bo) {
        LambdaQueryWrapper<SysImportConfig> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getCode()), SysImportConfig::getCode, bo.getCode());
        lqw.like(StringUtils.isNotBlank(bo.getName()), SysImportConfig::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getBranchType()), SysImportConfig::getBranchType, bo.getBranchType());
        return lqw;
    }

    /**
     * 新增excel导入模板
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(SysImportConfigBo bo) {
        SysImportConfig add = MapstructUtils.convert(bo, SysImportConfig.class);
        // 设置创建信息
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        add.setMakedate(now);
        add.setMaketime(dateFormat.format(now));
        add.setOperator("admin"); // 应从登录用户获取

        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改excel导入模板
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(SysImportConfigBo bo) {
        SysImportConfig update = MapstructUtils.convert(bo, SysImportConfig.class);
        // 设置修改信息
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        update.setModifydate(now);
        update.setModifytime(dateFormat.format(now));
        update.setModifyoperator("admin"); // 应从登录用户获取

        return baseMapper.updateById(update) > 0;
    }

    /**
     * 批量删除excel导入模板
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // 进行业务校验
        }

        // 删除关联的配置项
        LambdaQueryWrapper<SysImportConfigItem> itemWrapper = Wrappers.lambdaQuery();
        itemWrapper.in(SysImportConfigItem::getConfigId, ids);
        itemMapper.delete(itemWrapper);

        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 保存excel导入模板配置项
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveConfigItems(Long configId, List<SysImportConfigBo> sheetConfigs) {
        // 先删除原有配置
        LambdaQueryWrapper<SysImportConfigItem> itemWrapper = Wrappers.lambdaQuery();
        itemWrapper.eq(SysImportConfigItem::getConfigId, configId);
        itemMapper.delete(itemWrapper);

        // 提取所有字段并保存
        List<SysImportConfigItem> itemList = new ArrayList<>();

        // 记录标题和Sheet名
        StringBuilder titleNames = new StringBuilder();
        StringBuilder sheetNames = new StringBuilder();

        for (int i = 0; i < sheetConfigs.size(); i++) {
            SysImportConfigBo sheetConfig = sheetConfigs.get(i);
            String sheetName = sheetConfig.getName() != null ? sheetConfig.getName() : "Sheet" + (i + 1);

            // 添加到Sheet名称列表
            if (i > 0) {
                sheetNames.append(",");
            }
            sheetNames.append(sheetName);

            List<SysImportConfigItemBo> fields = sheetConfig.getConfigItems();
            if (fields != null && !fields.isEmpty()) {
                for (SysImportConfigItemBo field : fields) {
                    SysImportConfigItem item = MapstructUtils.convert(field, SysImportConfigItem.class);
                    item.setConfigId(configId);
                    item.setSheetIndex(i);

                    // 设置创建信息
                    Date now = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                    item.setMakedate(now);
                    item.setMaketime(dateFormat.format(now));
                    item.setOperator("admin"); // 应从登录用户获取

                    itemList.add(item);

                    // 添加到标题名称列表（只取第一个）
                    if (i == 0 && titleNames.length() == 0 && StringUtils.isNotBlank(field.getTitle())) {
                        titleNames.append(field.getTitle());
                    }
                }
            }
        }

        // 批量插入配置项
        for (SysImportConfigItem item : itemList) {
            itemMapper.insert(item);
        }

        // 更新模板的标题名称和Sheet名称
        SysImportConfig config = new SysImportConfig();
        config.setId(configId);
        config.setTitleNames(titleNames.toString());
        config.setSheetNames(sheetNames.toString());

        // 设置修改信息
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        config.setModifydate(now);
        config.setModifytime(dateFormat.format(now));
        config.setModifyoperator("admin"); // 应从登录用户获取

        baseMapper.updateById(config);

        return true;
    }

    /**
     * 生成Excel模板
     */
    @Override
    public byte[] generateExcelTemplate(Long id) {
        // 查询模板配置
        SysImportConfigVo config = queryById(id);
        if (config == null || config.getConfigItems() == null || config.getConfigItems().isEmpty()) {
            throw new RuntimeException("未找到有效的模板配置");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            // 创建样式 - 标题
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            titleStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleStyle.setFont(titleFont);
            titleStyle.setBorderTop(BorderStyle.THIN);
            titleStyle.setBorderBottom(BorderStyle.THIN);
            titleStyle.setBorderLeft(BorderStyle.THIN);
            titleStyle.setBorderRight(BorderStyle.THIN);

            // 按sheet索引分组
            Map<Long, List<SysImportConfigItemVo>> sheetMap = config.getConfigItems().stream()
                .collect(Collectors.groupingBy(SysImportConfigItemVo::getSheetIndex));

            // 分割Sheet名称
            String[] sheetNames = config.getSheetNames() != null
                ? config.getSheetNames().split(",")
                : new String[]{"Sheet1"};

            // 创建每个Sheet
            for (Map.Entry<Long, List<SysImportConfigItemVo>> entry : sheetMap.entrySet()) {
                Long sheetIndex = entry.getKey();
                List<SysImportConfigItemVo> items = entry.getValue();

                // 按排序字段排序
                items.sort(Comparator.comparing(SysImportConfigItemVo::getSort));

                // 获取Sheet名称
                String sheetName = sheetIndex < sheetNames.length ?
                    sheetNames[sheetIndex.intValue()] : "Sheet" + (sheetIndex + 1);

                Sheet sheet = workbook.createSheet(sheetName);

                // 创建标题行
                Row headerRow = sheet.createRow(0);

                for (int i = 0; i < items.size(); i++) {
                    SysImportConfigItemVo item = items.get(i);
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(item.getTitle());
                    cell.setCellStyle(titleStyle);

                    // 设置列宽
                    if (item.getWidth() != null) {
                        // 设置列宽，除以8得到大致的字符宽度
                        sheet.setColumnWidth(i, item.getWidth() / 8);
                    }

                    // 如果有下拉框数据源，添加数据有效性限制
                    if (StringUtils.isNotBlank(item.getDownSelHandler())) {
                        // 这里只是示例，实际应该根据处理类获取下拉数据
                        String[] options = {"选项1", "选项2", "选项3"};
                        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
                        DataValidationConstraint dvConstraint = dvHelper.createExplicitListConstraint(options);
                        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, i, i);
                        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
                        validation.setShowErrorBox(true);
                        sheet.addValidationData(validation);
                    }
                }

                // 创建一行示例数据（可选）
                Row exampleRow = sheet.createRow(1);
                for (int i = 0; i < items.size(); i++) {
                    SysImportConfigItemVo item = items.get(i);
                    Cell cell = exampleRow.createCell(i);

                    // 根据数据类型设置不同的示例值
                    switch (item.getDataType()) {
                        case "0": // 字符串
                            cell.setCellValue("示例文本");
                            break;
                        case "1": // 整数
                            cell.setCellValue(100);
                            break;
                        case "2": // 2位小数
                            cell.setCellValue(100.25);
                            break;
                        case "3": // 4位小数
                            cell.setCellValue(100.1234);
                            break;
                        case "4": // 日期
                            cell.setCellValue(new Date());
                            CellStyle dateStyle = workbook.createCellStyle();
                            dateStyle.setDataFormat(workbook.createDataFormat().getFormat("yyyy-mm-dd"));
                            cell.setCellStyle(dateStyle);
                            break;
                        case "9": // 序号
                            cell.setCellValue(1);
                            break;
                        default:
                            cell.setCellValue("");
                    }
                }

                // 冻结首行
                sheet.createFreezePane(0, 1);
            }

            // 将工作簿写入字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("生成Excel模板失败", e);
            throw new RuntimeException("生成Excel模板失败", e);
        }
    }
}
