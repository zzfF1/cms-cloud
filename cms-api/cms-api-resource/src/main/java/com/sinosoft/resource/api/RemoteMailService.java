package com.sinosoft.resource.api;

import com.sinosoft.common.core.exception.ServiceException;

/**
 * 邮件服务
 *
 * @author zzf
 */
public interface RemoteMailService {

    /**
     * 发送邮件
     *
     * @param to      接收人
     * @param subject 标题
     * @param text    内容
     */
    void send(String to, String subject, String text) throws ServiceException;

}
