package com.sinosoft.common.mybatis.service;

import com.sinosoft.common.mybatis.core.mapper.LDMaxNoMapper;
import com.sinosoft.common.redis.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.math.BigInteger;

/**
 * @program: ncms
 * @description: 流水号生成
 * @author: zzf
 * @create: 2023-06-14 22:26
 */
@Component
public class LDMaxNoHelper {

    /**
     * 最大步长
     */
    private final int MAX_NO_STEP = 1000;

    @Autowired
    private LDMaxNoMapper ldMaxNoMapper;

    /**
     * 生成一个流水号
     *
     * @param notype  流水号名称
     * @param nolimit 流水类别
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
    public long getMaxNo(String notype, String nolimit) {
        return ldMaxNoMapper.findMaxNo(notype, nolimit);
    }

    /**
     * 获取缓存流水号
     *
     * @param notype  流水号名称
     * @param nolimit 流水类别
     * @param len     长度
     * @return 流水号
     */
    public String getCacheMaxNo(String notype, String nolimit, Integer len) {
        // 获取缓存流水号
        String key = "maxNo:" + notype + ":" + nolimit;
        String endKey = key + ":End";
        long maxNo = RedisUtils.incrAtomicValue(key);
        // 如果缓存流水号不存在，那么从数据库获取
        if (maxNo <= 1) {
            // 获取数据库流水号
            maxNo = getMaxNo(notype, nolimit);
            RedisUtils.setAtomicValue(key, maxNo);
        }
        // 获取截止流水号
        long endNo = RedisUtils.getAtomicValue(endKey);
        // 如果当前流水号大于截止流水号,重新缓存
        if (maxNo >= endNo - 100) {
            // 保存数据库流水号
            long maxEnd = ldMaxNoMapper.findMaxNoStep(notype, nolimit, MAX_NO_STEP);
            // 重新缓存数据
            RedisUtils.setAtomicValue(endKey, maxEnd);
        }
        return autoGenericCode(maxNo, len);
    }

    /**
     * 生成指定步长流水号
     *
     * @param notype
     * @param nolimit
     * @param step
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
    public long getMaxNoStep(String notype, String nolimit, int step) {
        return ldMaxNoMapper.findMaxNoStep(notype, nolimit, step);
    }

    /**
     * 如果没有name对应的信息，那么新创建一条这样的记录。<br />
     * 如果有的话，在以前的流水号的基础上增加1，并返回。
     *
     * @param name 流水号名称
     * @param len  长度
     * @return 生成的最大流水号
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
    public String creatMaxNo(String name, Integer len) {
        return creatMaxNo(name, len, "");
    }

    /**
     * 如果没有name对应的信息，那么新创建一条这样的记录。<br />
     * 如果有的话，在以前的流水号的基础上增加1，并返回。
     *
     * @param nolimit 流水类别
     * @param len     长度
     * @param notype  流水号名称
     * @return 生成的最大流水号
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
    public String creatMaxNo(String notype, Integer len, String nolimit) {
        if (StringUtils.isEmpty(nolimit)) {
            nolimit = "SN";
        }
        if (StringUtils.isEmpty(notype)) {
            Assert.notNull(notype, "notype is not null !");
        }
        long maxno = getMaxNo(notype, nolimit);
        return autoGenericCode(maxno, len);
    }

    /**
     * 获取指定步长流水
     *
     * @param notype
     * @param len
     * @param step
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
    public String[] creatMaxNoStep(String notype, Integer len, int step) {
        return creatMaxNoStep(notype, "", len, step);
    }

    /**
     * 获取指定步长流水
     *
     * @param notype
     * @param nolimit
     * @param len
     * @param step
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
    public String[] creatMaxNoStep(String notype, String nolimit, Integer len, int step) {
        if (StringUtils.isEmpty(nolimit)) {
            nolimit = "SN";
        }
        if (StringUtils.isEmpty(notype)) {
            Assert.notNull(notype, "notype is not null !");
        }
        String[] strReturn = new String[step];
        //生成指定步长流水号
        BigInteger tMaxNo = BigInteger.valueOf(getMaxNoStep(notype, nolimit, step));
        int nMin = (tMaxNo.intValue() - step + 1);
        for (int i = 0; i < strReturn.length; i++) {
            strReturn[i] = autoGenericCode(nMin + i, len);
        }
        return strReturn;
    }


    /**
     * @param name 字符串消息
     * @return 流水号消息
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
    public String creatMaxNo(String name) {
        return creatMaxNo(name, 20);
    }

    public String getMaxNo(Class<? extends Object> clazz) {
        return creatMaxNo(clazz.getSimpleName());
    }

    /**
     * @param code 最大数
     * @param num  长度
     * @return 返回流水号
     */
    public String autoGenericCode(long code, int num) {
        return autoGenericCode(code, num, "0");
    }

    /**
     * 保留num的位数 <Br/>
     * 0 代表前面补充0 <Br/>
     * num 代表长度为num <Br/>
     * d 代表参数为正数型
     *
     * @param code     末尾数字
     * @param num      总共长度
     * @param startStr 补充什么字符串
     * @return 返回字符串
     */
    public String autoGenericCode(long code, int num, String startStr) {
        return String.format("%" + startStr + num + "d", code);
    }
}
