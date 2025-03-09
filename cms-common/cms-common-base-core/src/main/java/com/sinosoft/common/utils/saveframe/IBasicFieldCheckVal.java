package com.sinosoft.common.utils.saveframe;

import java.util.Map;

/**
 * 基础保存校验接口
 */
public interface IBasicFieldCheckVal {

    String checkValErrorMsg(Map<String, String> data, String val) throws Exception;
}
