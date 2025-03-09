package com.sinosoft.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import com.sinosoft.common.schema.common.domain.vo.HelpDocShowVo;
import org.springframework.stereotype.Service;
import com.sinosoft.common.domain.BusinessHelpDocs;
import com.sinosoft.common.mapper.BusinessHelpDocsMapper;
import com.sinosoft.common.service.IBusinessHelpDocsService;

/**
 * 业务帮助文档Service业务层处理
 *
 * @author zzf
 * @date 2023-11-09
 */
@RequiredArgsConstructor
@Service
public class BusinessHelpDocsServiceImpl implements IBusinessHelpDocsService {

    private final BusinessHelpDocsMapper baseMapper;

    /**
     * 查询业务帮助文档
     */
    @Override
    public BusinessHelpDocs queryById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public HelpDocShowVo queryContent(String busCode) {
        HelpDocShowVo helpDocShowVo = new HelpDocShowVo();
        LambdaQueryWrapper<BusinessHelpDocs> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.select(BusinessHelpDocs::getContent, BusinessHelpDocs::getTitle).eq(BusinessHelpDocs::getBusCode, busCode);
        BusinessHelpDocs helpDocs = baseMapper.selectOne(queryWrapper);
        helpDocShowVo.setTitle(helpDocs.getTitle());
        helpDocShowVo.setContent(helpDocs.getContent());
        return helpDocShowVo;
    }

}
