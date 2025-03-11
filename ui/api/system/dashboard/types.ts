/**
 * API通用响应接口
 */
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

/**
 * 通知项接口
 */
export interface Notification {
  id: number;
  title: string;
  description: string;
  time: string;
  category: 'business' | 'system' | 'team';
  priority: 'urgent' | 'important' | 'normal';
  date: string;
  read: boolean;
}

/**
 * 分页数据接口
 */
export interface PageData<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

/**
 * 通知查询参数接口
 */
export interface NotificationParams {
  page?: number;
  size?: number;
  category?: string;
  keyword?: string;
  startDate?: string;
  endDate?: string;
  read?: boolean;
}

/**
 * 工作量统计接口
 */
export interface WorkloadData {
  today: number;
  todayTrend: number;
  week: number;
  weekTrend: number;
  month: number;
  monthTrend: number;
}

/**
 * 销售统计接口
 */
export interface SalesData {
  today: number;
  todayTrend: number;
  week: number;
  weekTrend: number;
  month: number;
  monthTrend: number;
}

/**
 * 绩效统计接口
 */
export interface PerformanceData {
  rank: number;
  percentage: number;
  target: number;
}

/**
 * 业务统计接口
 */
export interface StatisticsData {
  newCustomers: number;
  policies: number;
  renewals: number;
  claims: number;
}

/**
 * 个人数据概览接口
 */
export interface PersonalData {
  workload: WorkloadData;
  sales: SalesData;
  performance: PerformanceData;
  statistics: StatisticsData;
}

/**
 * 日历事件接口
 */
export interface CalendarEvent {
  id: number;
  title: string;
  date: string;
  time: string;
  type: string;
  notes: string;
  color: string;
  isAllDay: boolean;
}

/**
 * 快捷入口项接口
 */
export interface ShortcutItem {
  id: string;
  label: string;
  icon: string;
  iconColor: string;
  route: string;
}

/**
 * 仪表板组件配置接口
 */
export interface DashboardComponent {
  id: string;
  name: string;
  description: string;
}

/**
 * 保单佣金项接口
 */
export interface PolicyCommission {
  policyId: string;
  policyNumber: string;
  productName: string;
  customerName: string;
  premium: number;
  paymentPeriod: string;
  commission: number;
  commissionRate: number;
  settleDate: string;
  status: string;
}

/**
 * 佣金汇总接口
 */
export interface CommissionSummary {
  totalCommission: number;
  settledCommission: number;
  pendingCommission: number;
  policyCount: number;
  avgCommissionRate: number;
}

/**
 * 佣金计算结果接口
 */
export interface CommissionResult {
  wageList: PolicyCommission[];
  summary: CommissionSummary;
}
