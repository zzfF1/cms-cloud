package com.sinosoft.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sinosoft.common.core.utils.MapstructUtils;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.common.schema.common.domain.SysExportConfig;
import com.sinosoft.common.schema.common.domain.SysExportConfigItem;
import com.sinosoft.common.schema.common.domain.SysExportConfigSheet;
import com.sinosoft.common.schema.common.mapper.SysExportConfigItemMapper;
import com.sinosoft.common.schema.common.mapper.SysExportConfigMapper;
import com.sinosoft.common.schema.common.mapper.SysExportConfigSheetMapper;
import com.sinosoft.system.domain.bo.SysExportConfigBo;
import com.sinosoft.system.domain.bo.SysExportConfigItemBo;
import com.sinosoft.system.domain.bo.SysExportConfigSheetBo;
import com.sinosoft.system.domain.vo.SysExportConfigItemVo;
import com.sinosoft.system.domain.vo.SysExportConfigSheetVo;
import com.sinosoft.system.domain.vo.SysExportConfigVo;
import com.sinosoft.system.service.ISysExportConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * excel导出配置Service业务层处理
 *
 * @author demo
 * @date 2024-04-20
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysExportConfigServiceImpl implements ISysExportConfigService {

    private final SysExportConfigMapper baseMapper;
    private final SysExportConfigSheetMapper sheetMapper;
    private final SysExportConfigItemMapper itemMapper;

    // 存储上传文件的路径
    private static final String UPLOAD_PATH = "/upload/templates/";

    /**
     * 查询excel导出配置
     */
    @Override
    public SysExportConfigVo queryById(Long id) {
        // 查询基本信息
        SysExportConfigVo configVo = baseMapper.selectVoById(id, SysExportConfigVo.class);
        if (configVo != null) {
            // 查询Sheet配置
            LambdaQueryWrapper<SysExportConfigSheet> sheetWrapper = Wrappers.lambdaQuery();
            sheetWrapper.eq(SysExportConfigSheet::getConfigId, id);
            sheetWrapper.orderByAsc(SysExportConfigSheet::getSheetIndex);
            List<SysExportConfigSheetVo> sheetList = sheetMapper.selectVoList(sheetWrapper, SysExportConfigSheetVo.class);

            if (!sheetList.isEmpty()) {
                // 查询每个Sheet对应的字段配置
                for (SysExportConfigSheetVo sheet : sheetList) {
                    LambdaQueryWrapper<SysExportConfigItem> itemWrapper = Wrappers.lambdaQuery();
                    itemWrapper.eq(SysExportConfigItem::getSheetId, sheet.getId());
                    itemWrapper.orderByAsc(SysExportConfigItem::getSort);
                    List<SysExportConfigItemVo> itemList = itemMapper.selectVoList(itemWrapper, SysExportConfigItemVo.class);
                    sheet.setItems(itemList);
                }
            }

            configVo.setSheets(sheetList);
        }
        return configVo;
    }

    /**
     * 查询excel导出配置列表
     */
    @Override
    public TableDataInfo<SysExportConfigVo> queryPageList(SysExportConfigBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysExportConfig> lqw = buildQueryWrapper(bo);
        Page<SysExportConfigVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw, SysExportConfigVo.class);

        // 填充Sheet信息
        if (result.getRecords() != null && !result.getRecords().isEmpty()) {
            for (SysExportConfigVo config : result.getRecords()) {
                // 查询Sheet配置
                LambdaQueryWrapper<SysExportConfigSheet> sheetWrapper = Wrappers.lambdaQuery();
                sheetWrapper.eq(SysExportConfigSheet::getConfigId, config.getId());
                sheetWrapper.orderByAsc(SysExportConfigSheet::getSheetIndex);
                List<SysExportConfigSheetVo> sheetList = sheetMapper.selectVoList(sheetWrapper, SysExportConfigSheetVo.class);

                if (!sheetList.isEmpty()) {
                    // 查询每个Sheet对应的字段配置
                    for (SysExportConfigSheetVo sheet : sheetList) {
                        LambdaQueryWrapper<SysExportConfigItem> itemWrapper = Wrappers.lambdaQuery();
                        itemWrapper.eq(SysExportConfigItem::getSheetId, sheet.getId());
                        itemWrapper.orderByAsc(SysExportConfigItem::getSort);
                        List<SysExportConfigItemVo> itemList = itemMapper.selectVoList(itemWrapper, SysExportConfigItemVo.class);
                        sheet.setItems(itemList);
                    }
                }

                config.setSheets(sheetList);
            }
        }

        return TableDataInfo.build(result);
    }

    /**
     * 查询excel导出配置列表
     */
    @Override
    public List<SysExportConfigVo> queryList(SysExportConfigBo bo) {
        LambdaQueryWrapper<SysExportConfig> lqw = buildQueryWrapper(bo);
        List<SysExportConfigVo> list = baseMapper.selectVoList(lqw, SysExportConfigVo.class);

        // 填充Sheet信息
        if (!list.isEmpty()) {
            for (SysExportConfigVo config : list) {
                // 查询Sheet配置
                LambdaQueryWrapper<SysExportConfigSheet> sheetWrapper = Wrappers.lambdaQuery();
                sheetWrapper.eq(SysExportConfigSheet::getConfigId, config.getId());
                sheetWrapper.orderByAsc(SysExportConfigSheet::getSheetIndex);
                List<SysExportConfigSheetVo> sheetList = sheetMapper.selectVoList(sheetWrapper, SysExportConfigSheetVo.class);

                if (!sheetList.isEmpty()) {
                    // 查询每个Sheet对应的字段配置
                    for (SysExportConfigSheetVo sheet : sheetList) {
                        LambdaQueryWrapper<SysExportConfigItem> itemWrapper = Wrappers.lambdaQuery();
                        itemWrapper.eq(SysExportConfigItem::getSheetId, sheet.getId());
                        itemWrapper.orderByAsc(SysExportConfigItem::getSort);
                        List<SysExportConfigItemVo> itemList = itemMapper.selectVoList(itemWrapper, SysExportConfigItemVo.class);
                        sheet.setItems(itemList);
                    }
                }

                config.setSheets(sheetList);
            }
        }

        return list;
    }

    private LambdaQueryWrapper<SysExportConfig> buildQueryWrapper(SysExportConfigBo bo) {
        LambdaQueryWrapper<SysExportConfig> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getCode()), SysExportConfig::getCode, bo.getCode());
        lqw.like(StringUtils.isNotBlank(bo.getName()), SysExportConfig::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getBranchType()), SysExportConfig::getBranchType, bo.getBranchType());
        lqw.eq(StringUtils.isNotBlank(bo.getType()), SysExportConfig::getType, bo.getType());
        lqw.orderByDesc(SysExportConfig::getId);
        return lqw;
    }

    /**
     * 新增excel导出配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long insertByBo(SysExportConfigBo bo) {
        // 设置创建信息
        Date now = new Date();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(now);
        String currentTime = new SimpleDateFormat("HH:mm:ss").format(now);

        // 1. 保存主表数据
        SysExportConfig entity = MapstructUtils.convert(bo, SysExportConfig.class);
        // 设置创建信息
        entity.setMakedate(now);
        entity.setMaketime(currentTime);
        entity.setOperator("admin"); // 应从当前登录用户获取

        baseMapper.insert(entity);
        Long configId = entity.getId();

        // 2. 保存Sheet配置
        if (bo.getSheets() != null && !bo.getSheets().isEmpty()) {
            for (int i = 0; i < bo.getSheets().size(); i++) {
                SysExportConfigSheetBo sheetBo = bo.getSheets().get(i);
                SysExportConfigSheet sheet = MapstructUtils.convert(sheetBo, SysExportConfigSheet.class);

                sheet.setConfigId(configId);
                sheet.setSheetIndex(i);
                // 设置创建信息
                sheet.setMakedate(now);
                sheet.setMaketime(currentTime);
                sheet.setOperator("admin"); // 应从当前登录用户获取

                sheetMapper.insert(sheet);
                Long sheetId = sheet.getId();

                // 3. 保存字段配置
                if (sheetBo.getItems() != null && !sheetBo.getItems().isEmpty()) {
                    for (SysExportConfigItemBo itemBo : sheetBo.getItems()) {
                        SysExportConfigItem item = MapstructUtils.convert(itemBo, SysExportConfigItem.class);

                        item.setConfigId(configId);
                        item.setSheetId(sheetId);
                        // 设置创建信息
                        item.setMakedate(now);
                        item.setMaketime(currentTime);
                        item.setOperator("admin"); // 应从当前登录用户获取

                        itemMapper.insert(item);
                    }
                }
            }
        }

        return configId;
    }

    /**
     * 修改excel导出配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(SysExportConfigBo bo) {
        // 设置修改信息
        Date now = new Date();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(now);
        String currentTime = new SimpleDateFormat("HH:mm:ss").format(now);

        // 1. 更新主表数据
        SysExportConfig entity = MapstructUtils.convert(bo, SysExportConfig.class);
        // 设置修改信息
        entity.setModifydate(now);
        entity.setModifytime(currentTime);
        entity.setModifyoperator("admin"); // 应从当前登录用户获取

        boolean result = baseMapper.updateById(entity) > 0;
        Long configId = entity.getId();

        // 2. 删除原有的Sheet和字段配置
        LambdaQueryWrapper<SysExportConfigSheet> sheetWrapper = Wrappers.lambdaQuery();
        sheetWrapper.eq(SysExportConfigSheet::getConfigId, configId);
        List<SysExportConfigSheet> oldSheets = sheetMapper.selectList(sheetWrapper);

        // 删除字段配置
        if (!oldSheets.isEmpty()) {
            List<Long> sheetIds = oldSheets.stream().map(SysExportConfigSheet::getId).collect(Collectors.toList());
            LambdaQueryWrapper<SysExportConfigItem> itemWrapper = Wrappers.lambdaQuery();
            itemWrapper.in(SysExportConfigItem::getSheetId, sheetIds);
            itemMapper.delete(itemWrapper);
        }

        // 删除Sheet配置
        sheetMapper.delete(sheetWrapper);

        // 3. 保存新的Sheet配置
        if (bo.getSheets() != null && !bo.getSheets().isEmpty()) {
            for (int i = 0; i < bo.getSheets().size(); i++) {
                SysExportConfigSheetBo sheetBo = bo.getSheets().get(i);
                SysExportConfigSheet sheet = MapstructUtils.convert(sheetBo, SysExportConfigSheet.class);

                sheet.setConfigId(configId);
                sheet.setSheetIndex(i);
                // 设置创建信息
                sheet.setMakedate(now);
                sheet.setMaketime(currentTime);
                sheet.setOperator("admin"); // 应从当前登录用户获取

                sheetMapper.insert(sheet);
                Long sheetId = sheet.getId();

                // 4. 保存新的字段配置
                if (sheetBo.getItems() != null && !sheetBo.getItems().isEmpty()) {
                    for (SysExportConfigItemBo itemBo : sheetBo.getItems()) {
                        SysExportConfigItem item = MapstructUtils.convert(itemBo, SysExportConfigItem.class);

                        item.setId(null); // 确保新增而不是更新
                        item.setConfigId(configId);
                        item.setSheetId(sheetId);
                        // 设置创建信息
                        item.setMakedate(now);
                        item.setMaketime(currentTime);
                        item.setOperator("admin"); // 应从当前登录用户获取

                        itemMapper.insert(item);
                    }
                }
            }
        }

        return result;
    }

    /**
     * 批量删除excel导出配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // 进行业务校验，判断是否可以删除
        }

        // 查找关联的Sheet配置
        LambdaQueryWrapper<SysExportConfigSheet> sheetWrapper = Wrappers.lambdaQuery();
        sheetWrapper.in(SysExportConfigSheet::getConfigId, ids);
        List<SysExportConfigSheet> sheets = sheetMapper.selectList(sheetWrapper);

        if (!sheets.isEmpty()) {
            // 删除关联的字段配置
            List<Long> sheetIds = sheets.stream().map(SysExportConfigSheet::getId).collect(Collectors.toList());
            LambdaQueryWrapper<SysExportConfigItem> itemWrapper = Wrappers.lambdaQuery();
            itemWrapper.in(SysExportConfigItem::getSheetId, sheetIds);
            itemMapper.delete(itemWrapper);

            // 删除Sheet配置
            sheetMapper.delete(sheetWrapper);
        }

        // 删除主表数据
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 生成Excel并返回文件路径
     */
    @Override
    public String generateExcel(Long id, Map<String, Object> params) {
        // 查询导出配置
        SysExportConfigVo config = queryById(id);
        if (config == null) {
            throw new RuntimeException("导出配置不存在");
        }

        try {
            Workbook workbook;

            if ("1".equals(config.getType()) && StringUtils.isNotEmpty(config.getPath())) {
                // 模板方式：基于模板生成
                // TODO: 实际项目中，应从文件存储系统获取模板文件
                // workbook = WorkbookFactory.create(new File(config.getPath()));

                // 此处简化处理，创建新Workbook
                workbook = new XSSFWorkbook();
            } else {
                // 生成方式：创建新Workbook
                workbook = new XSSFWorkbook();
            }

            // 处理每个Sheet
            if (config.getSheets() != null && !config.getSheets().isEmpty()) {
                for (SysExportConfigSheetVo sheetConfig : config.getSheets()) {
                    // 创建或获取Sheet
                    Sheet sheet;
                    if (workbook.getNumberOfSheets() > sheetConfig.getSheetIndex().intValue()) {
                        sheet = workbook.getSheetAt(sheetConfig.getSheetIndex().intValue());
                    } else {
                        sheet = workbook.createSheet(sheetConfig.getSheetName());
                    }

                    // 创建标题行
                    Row headerRow = sheet.createRow(0);

                    // 创建单元格样式
                    CellStyle headerStyle = workbook.createCellStyle();
                    headerStyle.setAlignment(HorizontalAlignment.CENTER);
                    headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    headerStyle.setBorderBottom(BorderStyle.THIN);
                    headerStyle.setBorderTop(BorderStyle.THIN);
                    headerStyle.setBorderLeft(BorderStyle.THIN);
                    headerStyle.setBorderRight(BorderStyle.THIN);

                    Font headerFont = workbook.createFont();
                    headerFont.setBold(true);
                    headerStyle.setFont(headerFont);

                    // 设置列标题
                    if (sheetConfig.getItems() != null && !sheetConfig.getItems().isEmpty()) {
                        for (int i = 0; i < sheetConfig.getItems().size(); i++) {
                            SysExportConfigItemVo item = sheetConfig.getItems().get(i);
                            Cell cell = headerRow.createCell(i);
                            cell.setCellValue(item.getName());
                            cell.setCellStyle(headerStyle);

                            // 设置列宽
                            if (item.getDispLength() != null) {
                                sheet.setColumnWidth(i, item.getDispLength() / 8);
                            }
                        }
                    }

                    // TODO: 根据SQL查询获取数据并填充Excel
                    // 此处为示例，实际应根据sheetConfig中的SQL配置查询数据库

                    // 模拟生成一些示例数据
                    for (int rowNum = 1; rowNum <= 5; rowNum++) {
                        Row dataRow = sheet.createRow(rowNum);

                        for (int colNum = 0; colNum < sheetConfig.getItems().size(); colNum++) {
                            SysExportConfigItemVo item = sheetConfig.getItems().get(colNum);
                            Cell cell = dataRow.createCell(colNum);

                            // 根据字段类型设置单元格值
                            switch (item.getType()) {
                                case 0: // 字符串
                                    cell.setCellValue("示例文本" + rowNum);
                                    break;
                                case 1: // 整数
                                    cell.setCellValue(rowNum * 100);
                                    break;
                                case 2: // 2位小数
                                    cell.setCellValue(rowNum * 10.25);
                                    break;
                                case 3: // 4位小数
                                    cell.setCellValue(rowNum * 10.1234);
                                    break;
                                case 4: // 日期
                                    cell.setCellValue(new Date());
                                    CellStyle dateStyle = workbook.createCellStyle();
                                    dateStyle.setDataFormat((short) 14); // mm/dd/yyyy
                                    cell.setCellStyle(dateStyle);
                                    break;
                                case 9: // 序号
                                    cell.setCellValue(rowNum);
                                    break;
                                default:
                                    cell.setCellValue("");
                            }
                        }
                    }
                }
            }

            // 保存Excel文件
            String filename = config.getName() + "_" + System.currentTimeMillis() + ".xlsx";
            String filePath = System.getProperty("java.io.tmpdir") + File.separator + filename;

            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            workbook.close();

            return filePath;
        } catch (Exception e) {
            log.error("生成Excel失败", e);
            throw new RuntimeException("生成Excel失败: " + e.getMessage());
        }
    }

    /**
     * 上传模板文件
     */
    @Override
    public String uploadTemplate(String originalFilename, byte[] fileData) {
        try {
            // 确保上传目录存在
            Path uploadDir = Paths.get(System.getProperty("user.dir") + UPLOAD_PATH);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 构建文件名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            Path filePath = uploadDir.resolve(filename);

            // 保存文件
            Files.write(filePath, fileData);

            // 返回相对路径
            return UPLOAD_PATH + filename;
        } catch (IOException e) {
            log.error("上传模板文件失败", e);
            throw new RuntimeException("上传模板文件失败: " + e.getMessage());
        }
    }

    /**
     * 根据配置ID查询Sheet配置列表
     *
     * @param configId 配置ID
     * @return Sheet配置列表
     */
    @Override
    public List<SysExportConfigSheetVo> querySheetsByConfigId(Long configId) {
        // 查询Sheet配置
        LambdaQueryWrapper<SysExportConfigSheet> sheetWrapper = Wrappers.lambdaQuery();
        sheetWrapper.eq(SysExportConfigSheet::getConfigId, configId);
        sheetWrapper.orderByAsc(SysExportConfigSheet::getSheetIndex);
        List<SysExportConfigSheetVo> sheetList = sheetMapper.selectVoList(sheetWrapper, SysExportConfigSheetVo.class);

        if (!sheetList.isEmpty()) {
            // 查询每个Sheet对应的字段配置
            for (SysExportConfigSheetVo sheet : sheetList) {
                LambdaQueryWrapper<SysExportConfigItem> itemWrapper = Wrappers.lambdaQuery();
                itemWrapper.eq(SysExportConfigItem::getSheetId, sheet.getId());
                itemWrapper.orderByAsc(SysExportConfigItem::getSort);
                List<SysExportConfigItemVo> itemList = itemMapper.selectVoList(itemWrapper, SysExportConfigItemVo.class);
                sheet.setItems(itemList);
            }
        }

        return sheetList;
    }

}
