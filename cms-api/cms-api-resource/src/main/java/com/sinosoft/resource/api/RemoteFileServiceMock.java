package com.sinosoft.resource.api;

import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.resource.api.domain.RemoteFile;

import java.util.List;

/**
 * 文件服务(降级处理)
 *
 * @author zzf
 */
@Slf4j
public class RemoteFileServiceMock implements RemoteFileService {

    /**
     * 上传文件
     *
     * @param file 文件信息
     * @return 结果
     */
    @Override
    public RemoteFile upload(String name, String originalFilename, String contentType, byte[] file) {
        log.warn("服务调用异常 -> 降级处理");
        return null;
    }

    /**
     * 通过ossId查询对应的url
     *
     * @param ossIds ossId串逗号分隔
     * @return url串逗号分隔
     */
    @Override
    public String selectUrlByIds(String ossIds) {
        log.warn("服务调用异常 -> 降级处理");
        return StringUtils.EMPTY;
    }

    /**
     * 通过ossId查询列表
     *
     * @param ossIds ossId串逗号分隔
     * @return 列表
     */
    @Override
    public List<RemoteFile> selectByIds(String ossIds) {
        log.warn("服务调用异常 -> 降级处理");
        return List.of();
    }

}
