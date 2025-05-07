package com.sinosoft.system.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.http.HtmlUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.system.service.ISysDeptService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.core.exception.ServiceException;
import com.sinosoft.common.core.utils.SpringUtils;
import com.sinosoft.common.core.utils.StreamUtils;
import com.sinosoft.common.core.utils.ValidatorUtils;
import com.sinosoft.common.excel.core.ExcelListener;
import com.sinosoft.common.excel.core.ExcelResult;
import com.sinosoft.common.satoken.utils.LoginHelper;
import com.sinosoft.system.domain.bo.SysUserBo;
import com.sinosoft.system.domain.vo.SysUserImportVo;
import com.sinosoft.system.domain.vo.SysUserVo;
import com.sinosoft.system.service.ISysConfigService;
import com.sinosoft.system.service.ISysUserService;

import java.util.Date;
import java.util.List;

/**
 * 系统用户自定义导入
 *
 * @author zzf
 */
@Slf4j
public class SysUserImportListener extends AnalysisEventListener<SysUserImportVo> implements ExcelListener<SysUserImportVo> {

    private final ISysUserService userService;
    private final ISysDeptService deptService;
    private final String password;
    private final Boolean isUpdateSupport;
    private int successNum = 0;
    private int failureNum = 0;
    private final StringBuilder successMsg = new StringBuilder();
    private final StringBuilder failureMsg = new StringBuilder();

    public SysUserImportListener(Boolean isUpdateSupport) {
        String initPassword = SpringUtils.getBean(ISysConfigService.class).selectConfigByKey("sys.user.initPassword");
        this.userService = SpringUtils.getBean(ISysUserService.class);
        this.deptService = SpringUtils.getBean(ISysDeptService.class);
        this.password = BCrypt.hashpw(initPassword);
        this.isUpdateSupport = isUpdateSupport;
    }

    @Override
    public void invoke(SysUserImportVo userVo, AnalysisContext context) {
        SysUserBo user = BeanUtil.toBean(userVo, SysUserBo.class);

        // 验证数据
        ValidatorUtils.validate(user);

        // 验证IP格式
        if (StringUtils.isNotBlank(user.getBindIp()) && !ValidatorUtils.isIP(user.getBindIp())) {
            failureNum++;
            failureMsg.append("<br/>").append(failureNum).append("、账号 ").append(user.getUserName())
                .append(" IP格式不正确");
            return;
        }

        // 验证证件号格式
        if (StringUtils.isNotBlank(user.getIdNo()) && !ValidatorUtils.isIdCard(user.getIdNo())) {
            failureNum++;
            failureMsg.append("<br/>").append(failureNum).append("、账号 ").append(user.getUserName())
                .append(" 证件号码格式不正确");
            return;
        }

        // 查询用户是否存在
        SysUserVo dbUser = userService.selectUserByUserName(user.getUserName());

        if (ObjectUtil.isNull(dbUser)) {
            user.setPassword(password);
            user.setCreateBy(LoginHelper.getUserId());
            user.setCreateTime(new Date());

            // 设置默认值
            if (user.getAccType() == null) {
                user.setAccType(0);
            }
            if (StringUtils.isBlank(user.getSysType())) {
                user.setSysType("N");
            }

            userService.insertUser(user);
            successNum++;
            successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.getUserName()).append(" 导入成功");
        } else if (isUpdateSupport) {
            Long userId = dbUser.getUserId();
            user.setUserId(userId);
            user.setUpdateBy(LoginHelper.getUserId());
            user.setUpdateTime(new Date());
            userService.updateUser(user);
            successNum++;
            successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.getUserName()).append(" 更新成功");
        } else {
            failureNum++;
            failureMsg.append("<br/>").append(failureNum).append("、账号 ").append(user.getUserName()).append(" 已存在");
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }

    @Override
    public ExcelResult<SysUserImportVo> getExcelResult() {
        return new ExcelResult<SysUserImportVo>() {
            @Override
            public String getAnalysis() {
                if (failureNum > 0) {
                    failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
                    throw new ServiceException(failureMsg.toString());
                } else {
                    successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
                }
                return successMsg.toString();
            }

            @Override
            public List<SysUserImportVo> getList() {
                return null;
            }

            @Override
            public List<String> getErrorList() {
                return null;
            }
        };
    }
}
