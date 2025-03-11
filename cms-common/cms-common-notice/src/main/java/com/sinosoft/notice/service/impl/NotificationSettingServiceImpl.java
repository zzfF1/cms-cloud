package com.sinosoft.notice.service.impl;

import com.sinosoft.notice.domain.SysNotificationSetting;
import com.sinosoft.notice.mapper.SysNotificationSettingMapper;
import com.sinosoft.notice.service.INotificationSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 通知设置服务实现类
 *
 * @author zzf
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationSettingServiceImpl implements INotificationSettingService {

    private final SysNotificationSettingMapper settingMapper;

    // 定义时间格式化器
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    /**
     * 获取用户通知设置
     *
     * @param userId 用户ID
     * @return 通知设置信息
     */
    @Override
    public Map<String, Object> getUserSettings(Long userId) {
        // 查询用户设置
        SysNotificationSetting setting = settingMapper.selectByUserId(userId);

        // 如果不存在，创建默认设置
        if (setting == null) {
            setting = createDefaultSetting(userId);
        }

        // 转换为Map
        return convertSettingToMap(setting);
    }

    /**
     * 更新用户通知设置
     *
     * @param userId   用户ID
     * @param settings 设置信息
     * @return 是否更新成功
     */
    @Override
    @Transactional
    public boolean updateUserSettings(Long userId, Map<String, Object> settings) {
        // 查询用户设置
        SysNotificationSetting setting = settingMapper.selectByUserId(userId);

        // 如果不存在，创建默认设置
        if (setting == null) {
            setting = new SysNotificationSetting();
            setting.setId(getNextSettingId());
            setting.setUserId(userId);
            setting.setCreateTime(new Date());
        }

        // 更新设置
        updateSettingFromMap(setting, settings);

        // 保存设置
        if (setting.getId() == null) {
            return settingMapper.insert(setting) > 0;
        } else {
            setting.setUpdateTime(new Date());
            return settingMapper.updateById(setting) > 0;
        }
    }

    /**
     * 获取默认通知设置
     *
     * @return 默认设置
     */
    @Override
    public Map<String, Object> getDefaultSettings() {
        Map<String, Object> defaultSettings = new HashMap<>();

        // 待办通知设置
        defaultSettings.put("todoNotifySystem", "1"); // 系统内通知开启
        defaultSettings.put("todoNotifySms", "0");    // 短信通知关闭
        defaultSettings.put("todoNotifyEmail", "0");  // 邮件通知关闭

        // 预警通知设置
        defaultSettings.put("alertNotifySystem", "1"); // 系统内通知开启
        defaultSettings.put("alertNotifySms", "0");    // 短信通知关闭
        defaultSettings.put("alertNotifyEmail", "0");  // 邮件通知关闭

        // 公告通知设置
        defaultSettings.put("announceNotifySystem", "1"); // 系统内通知开启
        defaultSettings.put("announceNotifyEmail", "0");  // 邮件通知关闭

        // 免打扰设置
        defaultSettings.put("doNotDisturbStart", "22:00"); // 默认晚上10点开始
        defaultSettings.put("doNotDisturbEnd", "08:00");   // 默认早上8点结束

        return defaultSettings;
    }

    /**
     * 创建默认设置
     *
     * @param userId 用户ID
     * @return 默认设置对象
     */
    private SysNotificationSetting createDefaultSetting(Long userId) {
        SysNotificationSetting setting = new SysNotificationSetting();
        setting.setId(getNextSettingId());
        setting.setUserId(userId);

        // 设置默认值
        setting.setTodoNotifySystem("1"); // 系统内通知开启
        setting.setTodoNotifySms("0");    // 短信通知关闭
        setting.setTodoNotifyEmail("0");  // 邮件通知关闭

        setting.setAlertNotifySystem("1"); // 系统内通知开启
        setting.setAlertNotifySms("0");    // 短信通知关闭
        setting.setAlertNotifyEmail("0");  // 邮件通知关闭

        setting.setAnnounceNotifySystem("1"); // 系统内通知开启
        setting.setAnnounceNotifyEmail("0");  // 邮件通知关闭

        try {
            // 设置默认免打扰时间
            setting.setDoNotDisturbStart(TIME_FORMAT.parse("22:00")); // 默认晚上10点开始
            setting.setDoNotDisturbEnd(TIME_FORMAT.parse("08:00"));   // 默认早上8点结束
        } catch (ParseException e) {
            log.error("解析默认免打扰时间异常: {}", e.getMessage());
            // 设置为当前时间，避免空值
            setting.setDoNotDisturbStart(new Date());
            setting.setDoNotDisturbEnd(new Date());
        }

        setting.setCreateTime(new Date());

        // 保存设置
        settingMapper.insert(setting);

        return setting;
    }

    /**
     * 获取下一个设置ID
     *
     * @return 设置ID
     */
    private Long getNextSettingId() {
        // 实际项目中应该使用ID生成器或数据库序列
        return System.currentTimeMillis();
    }

    /**
     * 将设置对象转换为Map
     *
     * @param setting 设置对象
     * @return Map格式的设置
     */
    private Map<String, Object> convertSettingToMap(SysNotificationSetting setting) {
        Map<String, Object> result = new HashMap<>();

        result.put("todoNotifySystem", setting.getTodoNotifySystem());
        result.put("todoNotifySms", setting.getTodoNotifySms());
        result.put("todoNotifyEmail", setting.getTodoNotifyEmail());

        result.put("alertNotifySystem", setting.getAlertNotifySystem());
        result.put("alertNotifySms", setting.getAlertNotifySms());
        result.put("alertNotifyEmail", setting.getAlertNotifyEmail());

        result.put("announceNotifySystem", setting.getAnnounceNotifySystem());
        result.put("announceNotifyEmail", setting.getAnnounceNotifyEmail());

        // 将Date类型转换为字符串
        if (setting.getDoNotDisturbStart() != null) {
            result.put("doNotDisturbStart", TIME_FORMAT.format(setting.getDoNotDisturbStart()));
        }
        if (setting.getDoNotDisturbEnd() != null) {
            result.put("doNotDisturbEnd", TIME_FORMAT.format(setting.getDoNotDisturbEnd()));
        }

        return result;
    }

    /**
     * 从Map更新设置对象
     *
     * @param setting 设置对象
     * @param map     Map格式的设置
     */
    private void updateSettingFromMap(SysNotificationSetting setting, Map<String, Object> map) {
        // 待办通知设置
        if (map.containsKey("todoNotifySystem")) {
            setting.setTodoNotifySystem(getBooleanValue(map.get("todoNotifySystem")));
        }
        if (map.containsKey("todoNotifySms")) {
            setting.setTodoNotifySms(getBooleanValue(map.get("todoNotifySms")));
        }
        if (map.containsKey("todoNotifyEmail")) {
            setting.setTodoNotifyEmail(getBooleanValue(map.get("todoNotifyEmail")));
        }

        // 预警通知设置
        if (map.containsKey("alertNotifySystem")) {
            setting.setAlertNotifySystem(getBooleanValue(map.get("alertNotifySystem")));
        }
        if (map.containsKey("alertNotifySms")) {
            setting.setAlertNotifySms(getBooleanValue(map.get("alertNotifySms")));
        }
        if (map.containsKey("alertNotifyEmail")) {
            setting.setAlertNotifyEmail(getBooleanValue(map.get("alertNotifyEmail")));
        }

        // 公告通知设置
        if (map.containsKey("announceNotifySystem")) {
            setting.setAnnounceNotifySystem(getBooleanValue(map.get("announceNotifySystem")));
        }
        if (map.containsKey("announceNotifyEmail")) {
            setting.setAnnounceNotifyEmail(getBooleanValue(map.get("announceNotifyEmail")));
        }

        // 免打扰设置 - 将字符串转换为Date类型
        if (map.containsKey("doNotDisturbStart")) {
            try {
                String startTimeStr = map.get("doNotDisturbStart").toString();
                setting.setDoNotDisturbStart(TIME_FORMAT.parse(startTimeStr));
            } catch (ParseException e) {
                log.error("解析免打扰开始时间异常: {}", e.getMessage());
            }
        }
        if (map.containsKey("doNotDisturbEnd")) {
            try {
                String endTimeStr = map.get("doNotDisturbEnd").toString();
                setting.setDoNotDisturbEnd(TIME_FORMAT.parse(endTimeStr));
            } catch (ParseException e) {
                log.error("解析免打扰结束时间异常: {}", e.getMessage());
            }
        }
    }

    /**
     * 获取布尔值
     *
     * @param value 值
     * @return 布尔值字符串，"1"表示true，"0"表示false
     */
    private String getBooleanValue(Object value) {
        if (value == null) {
            return "0";
        }

        if (value instanceof Boolean) {
            return (Boolean) value ? "1" : "0";
        }

        String strValue = value.toString();
        if ("true".equalsIgnoreCase(strValue) || "1".equals(strValue)) {
            return "1";
        }

        return "0";
    }
}
