package com.sinosoft.common.utils.saveframe;

/**
 * list类型数据处理接口
 *
 * @author zzf
 */
@FunctionalInterface
public interface DataCollectionHandler<T> {
    void dataCollectionHanlder(T t) throws Exception;
}
