package com.sinosoft.common.utils.saveframe;

import java.util.List;

public interface IBasicDataRecordCheck {
    /**
     * 校验数据的合法性
     *
     * @param recordList 数据集合
     * @return
     * @throws Exception
     */
    void checkDataRecordErrorMsg(List<DataRecord> recordList) throws Exception;
}
