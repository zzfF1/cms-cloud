package com.sinosoft.common.utils.saveframe;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: cms6
 * @description: 保存框架错误信息
 * @author: zzf
 * @create: 2023-07-09 11:06
 */
public class BasicSaveError {
    /**
     * 错误信息
     */
    public List<String> erroList = new ArrayList<>();

    /**
     * 添加错误信息
     *
     * @param msg 消息
     */
    public void addError(String msg) {
        erroList.add(msg);
    }
}
