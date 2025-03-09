package com.sinosoft.common.service;

import com.sinosoft.common.domain.BusinessHelpDocs;
import com.sinosoft.common.schema.common.domain.vo.HelpDocShowVo;


/**
 * 业务帮助文档Service接口
 *
 * @author zzf
 * @date 2023-11-09
 */
public interface IBusinessHelpDocsService {

    /**
     * 查询业务帮助文档
     */
    BusinessHelpDocs queryById(Long id);

    /**
     * 根据业务编码查询业务帮助文档内容
     *
     * @param busCode 业务编码
     * @return 业务帮助文档
     */
    HelpDocShowVo queryContent(String busCode);
}
