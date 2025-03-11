<template>
  <el-card class="calendar-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <span>日历</span>
      </div>
    </template>

    <div class="calendar-container">
      <div class="calendar-header">
        <el-button type="text" @click="changeMonth(-1)">
          <i class="el-icon-arrow-left"></i>
        </el-button>
        <span class="current-month">{{ currentYearMonth }}</span>
        <el-button type="text" @click="changeMonth(1)">
          <i class="el-icon-arrow-right"></i>
        </el-button>
      </div>

      <!-- 星期头部 -->
      <div class="calendar-weekdays">
        <div v-for="day in weekdays" :key="day" class="weekday">{{ day }}</div>
      </div>

      <!-- 日历日期 -->
      <div class="calendar-days">
        <div
          v-for="(day, index) in calendarDays"
          :key="index"
          class="calendar-day"
          :class="{
            'other-month': day.isOtherMonth,
            'today': day.isToday,
            'has-events': day.events && day.events.length > 0
          }"
          @click="selectDay(day)"
        >
          <div class="day-number">{{ day.day }}</div>
          <div class="day-events" v-if="day.events && day.events.length > 0">
            <div
              v-for="(event, eventIndex) in day.events.slice(0, 2)"
              :key="eventIndex"
              class="event-dot"
              :style="{ backgroundColor: event.color }"
              :title="event.title"
            ></div>
            <div class="more-events" v-if="day.events.length > 2">+{{ day.events.length - 2 }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加日程对话框 -->
    <el-dialog
      title="添加日程"
      v-model="addEventDialogVisible"
      width="30%"
    >
      <el-form :model="newEvent" label-width="80px" :rules="eventRules" ref="eventFormRef">
        <el-form-item label="标题" prop="title">
          <el-input v-model="newEvent.title" placeholder="请输入日程标题"></el-input>
        </el-form-item>
        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="newEvent.date"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="时间">
          <el-time-picker
            v-model="newEvent.time"
            placeholder="选择时间"
            style="width: 100%"
          ></el-time-picker>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="newEvent.type" placeholder="请选择日程类型" style="width: 100%">
            <el-option label="会议" value="meeting"></el-option>
            <el-option label="约见客户" value="client"></el-option>
            <el-option label="培训" value="training"></el-option>
            <el-option label="其他" value="other"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            type="textarea"
            v-model="newEvent.notes"
            placeholder="请输入备注"
            rows="3"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addEventDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addEvent">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 日程详情对话框 -->
    <el-dialog
      title="日程详情"
      v-model="dayEventsDialogVisible"
      width="40%"
    >
      <div v-if="selectedDay">
        <div class="selected-day-header">
          {{ formatSelectedDay }}
        </div>

        <el-empty v-if="!selectedDay.events || selectedDay.events.length === 0" description="暂无日程"></el-empty>

        <div v-else class="day-events-list">
          <el-card v-for="(event, index) in selectedDay.events" :key="index" class="event-card" shadow="hover">
            <div class="event-time">{{ formatEventTime(event) }}</div>
            <div class="event-title">{{ event.title }}</div>
            <div class="event-type">
              <el-tag :type="getEventTagType(event.type)" size="small">{{ getEventTypeName(event.type) }}</el-tag>
            </div>
            <div class="event-notes" v-if="event.notes">{{ event.notes }}</div>
            <div class="event-actions">
              <el-button type="text" size="small" @click="editEvent(event, index)">编辑</el-button>
              <el-button type="text" size="small" class="delete-btn" @click="removeEvent(index)">删除</el-button>
            </div>
          </el-card>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dayEventsDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="showAddEventDialog(selectedDay)">添加日程</el-button>
        </span>
      </template>
    </el-dialog>
  </el-card>
</template>

<script lang="ts" setup>
import { computed, defineExpose, onMounted, ref } from 'vue';
import dayjs from 'dayjs';

// 星期数组
const weekdays = ['日', '一', '二', '三', '四', '五', '六'];

// 当前日期和月份
const currentDate = ref(dayjs());
const currentYearMonth = computed(() => currentDate.value.format('YYYY年MM月'));

// 日历数据
const calendarDays = ref([]);

// 对话框状态
const addEventDialogVisible = ref(false);
const dayEventsDialogVisible = ref(false);
const selectedDay = ref(null);

// 格式化选中日期
const formatSelectedDay = computed(() => {
  if (!selectedDay.value) return '';
  const date = dayjs(`${selectedDay.value.year}-${selectedDay.value.month}-${selectedDay.value.day}`);
  return date.format('YYYY年MM月DD日 dddd');
});

// 新事件表单
const newEvent = ref({
  title: '',
  date: null,
  time: null,
  type: 'client',
  notes: '',
  color: ''
});

// 表单验证规则
const eventRules = {
  title: [
    { required: true, message: '请输入日程标题', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符之间', trigger: 'blur' }
  ],
  date: [
    { required: true, message: '请选择日期', trigger: 'change' }
  ]
};

// 事件数据
const events = ref([
  {
    id: 1,
    title: '客户回访',
    date: '2025-03-05',
    time: '10:30',
    type: 'client',
    notes: '张先生续保讨论',
    color: '#409EFF'
  },
  {
    id: 2,
    title: '产品培训',
    date: '2025-03-05',
    time: '14:00',
    type: 'training',
    notes: '新产品介绍',
    color: '#67C23A'
  },
  {
    id: 3,
    title: '团队会议',
    date: '2025-03-06',
    time: '09:30',
    type: 'meeting',
    notes: '月度总结',
    color: '#E6A23C'
  },
  {
    id: 4,
    title: '客户签约',
    date: '2025-03-10',
    time: '15:00',
    type: 'client',
    notes: '李女士-重疾险',
    color: '#409EFF'
  }
]);

// 刷新日历
const refreshCalendar = () => {
  generateCalendar();
};

// 生成日历数据
const generateCalendar = () => {
  const year = currentDate.value.year();
  const month = currentDate.value.month() + 1;
  const today = dayjs();

  // 当月第一天是星期几
  const firstDayOfMonth = dayjs(`${year}-${month}-01`);
  const firstDayWeekday = firstDayOfMonth.day();

  // 当月天数
  const daysInMonth = currentDate.value.daysInMonth();

  // 上个月天数
  const prevMonth = month === 1 ? 12 : month - 1;
  const prevYear = month === 1 ? year - 1 : year;
  const daysInPrevMonth = dayjs(`${prevYear}-${prevMonth}-01`).daysInMonth();

  const days = [];

  // 上个月的日期
  for (let i = 0; i < firstDayWeekday; i++) {
    const day = daysInPrevMonth - firstDayWeekday + i + 1;
    days.push({
      day,
      month: prevMonth,
      year: prevYear,
      isOtherMonth: true,
      isToday: false,
      events: getEventsForDay(prevYear, prevMonth, day)
    });
  }

  // 当月的日期
  for (let i = 1; i <= daysInMonth; i++) {
    const isToday = today.year() === year && today.month() + 1 === month && today.date() === i;
    days.push({
      day: i,
      month,
      year,
      isOtherMonth: false,
      isToday,
      events: getEventsForDay(year, month, i)
    });
  }

  // 下个月的日期
  const nextMonth = month === 12 ? 1 : month + 1;
  const nextYear = month === 12 ? year + 1 : year;
  const daysNeeded = 42 - days.length; // 6行7列 = 42个日期

  for (let i = 1; i <= daysNeeded; i++) {
    days.push({
      day: i,
      month: nextMonth,
      year: nextYear,
      isOtherMonth: true,
      isToday: false,
      events: getEventsForDay(nextYear, nextMonth, i)
    });
  }

  calendarDays.value = days;
};

// 获取某天的事件
const getEventsForDay = (year, month, day) => {
  const dateStr = `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
  return events.value.filter(event => event.date === dateStr);
};

// 切换月份
const changeMonth = (offset) => {
  currentDate.value = currentDate.value.add(offset, 'month');
  generateCalendar();
};

// 选择日期
const selectDay = (day) => {
  selectedDay.value = day;
  dayEventsDialogVisible.value = true;
};

// 显示添加事件对话框
const showAddEventDialog = (day = null) => {
  if (day) {
    newEvent.value.date = dayjs(`${day.year}-${day.month}-${day.day}`).toDate();
    dayEventsDialogVisible.value = false;
  } else {
    newEvent.value.date = dayjs().toDate();
  }

  newEvent.value.title = '';
  newEvent.value.time = null;
  newEvent.value.type = 'client';
  newEvent.value.notes = '';

  addEventDialogVisible.value = true;
};

// 添加事件
const addEvent = () => {
  if (!newEvent.value.title || !newEvent.value.date) {
    return;
  }

  const date = dayjs(newEvent.value.date);
  const timeStr = newEvent.value.time ? dayjs(newEvent.value.time).format('HH:mm') : '';

  const eventColors = {
    client: '#409EFF',
    meeting: '#E6A23C',
    training: '#67C23A',
    other: '#909399'
  };

  const newEventObj = {
    id: events.value.length + 1,
    title: newEvent.value.title,
    date: date.format('YYYY-MM-DD'),
    time: timeStr,
    type: newEvent.value.type,
    notes: newEvent.value.notes,
    color: eventColors[newEvent.value.type] || '#409EFF'
  };

  events.value.push(newEventObj);
  addEventDialogVisible.value = false;

  // 更新日历
  generateCalendar();
};

// 编辑事件
const editEvent = (event, index) => {
  // 实际应用中应该实现编辑功能
  console.log('编辑事件', event, index);
};

// 删除事件
const removeEvent = (index) => {
  if (!selectedDay.value || !selectedDay.value.events) return;

  const event = selectedDay.value.events[index];
  const eventIndex = events.value.findIndex(e => e.id === event.id);

  if (eventIndex !== -1) {
    events.value.splice(eventIndex, 1);
    // 更新日历
    generateCalendar();
  }
};

// 格式化事件时间
const formatEventTime = (event) => {
  return event.time || '全天';
};

// 获取事件类型标签样式
const getEventTagType = (type) => {
  const typeMap = {
    client: 'primary',
    meeting: 'warning',
    training: 'success',
    other: 'info'
  };
  return typeMap[type] || 'info';
};

// 获取事件类型名称
const getEventTypeName = (type) => {
  const typeMap = {
    client: '客户',
    meeting: '会议',
    training: '培训',
    other: '其他'
  };
  return typeMap[type] || '其他';
};

// 初始化
onMounted(() => {
  generateCalendar();
});

// 暴露方法给父组件
defineExpose({
  refreshCalendar,
  events
});
</script>

<style scoped>
.calendar-card {
  margin-bottom: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.header-actions i {
  cursor: pointer;
  font-size: 14px;
  color: #909399;
  transition: color 0.2s;
}

.header-actions i:hover {
  color: #409EFF;
}

.calendar-container {
  padding: 0;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.current-month {
  font-size: 14px;
  font-weight: 500;
}

.calendar-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-bottom: 4px;
}

.weekday {
  text-align: center;
  padding: 4px 0;
  font-size: 12px;
  color: #606266;
  font-weight: 500;
}

.calendar-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  grid-template-rows: repeat(6, 1fr);
  gap: 2px;
}

.calendar-day {
  position: relative;
  height: 34px;
  border: 1px solid #ebeef5;
  border-radius: 2px;
  padding: 2px;
  cursor: pointer;
  transition: background-color 0.2s;
  font-size: 12px;
}

.calendar-day:hover {
  background-color: #f5f7fa;
}

.calendar-day.other-month {
  color: #c0c4cc;
}

.calendar-day.today {
  background-color: #ecf5ff;
  color: #409eff;
  font-weight: bold;
}

.calendar-day.has-events {
  border-bottom: 2px solid #409eff;
}

.day-number {
  font-size: 12px;
}

.day-events {
  display: flex;
  flex-wrap: wrap;
  margin-top: 2px;
  gap: 2px;
}

.event-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
}

.more-events {
  font-size: 9px;
  color: #909399;
}

.selected-day-header {
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 12px;
  color: #303133;
}

.day-events-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.event-card {
  position: relative;
}

.event-time {
  color: #909399;
  font-size: 12px;
  margin-bottom: 3px;
}

.event-title {
  font-weight: 500;
  font-size: 13px;
  margin-bottom: 5px;
}

.event-type {
  margin-bottom: 5px;
}

.event-notes {
  color: #606266;
  font-size: 12px;
  margin-bottom: 5px;
}

.event-actions {
  display: flex;
  justify-content: flex-end;
  gap: 5px;
}

.delete-btn {
  color: #f56c6c;
}
</style>
