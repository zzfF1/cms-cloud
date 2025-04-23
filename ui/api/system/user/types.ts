import { RoleVO } from '@/api/system/role/types';
import { PostVO } from '@/api/system/post/types';

/**
 * 用户信息
 */
export interface UserInfo {
  user: UserVO;
  roles: string[];
  permissions: string[];
}

/**
 * 用户查询对象类型
 */
export interface UserQuery extends PageQuery {
  userName?: string;
  phonenumber?: string;
  status?: string;
  deptId?: string | number;
  roleId?: string | number;
  userIds?: string;
}

/**
 * 用户返回对象
 */
export interface UserVO extends BaseEntity {
  userId: string | number;
  tenantId: string;
  deptId: number;
  userName: string;
  nickName: string;
  userType: string;
  email: string;
  phonenumber: string;
  sex: string;
  avatar: string;
  status: string;
  delFlag: string;
  loginIp: string;
  loginDate: string;
  remark: string;
  deptName: string;
  roles: RoleVO[];
  roleIds: any;
  postIds: any;
  roleId: any;
  admin: boolean;
  branchType?: string; // 渠道类型
  idNo?: string; // 证件号码
  bindIp?: string; // 账号绑定IP
  accessStartTime?: string; // 允许访问时间开始
  accessEndTime?: string; // 允许访问时间结束
  accType?: number; // 账户类型
  validStartDate?: string; // 账户有效起期
  validEndDate?: string; // 账户有效止期
  realName?: string; // 真实姓名
  sysType?: string; // 系统内置
  manageCom?: string; // 管理机构
}

/**
 * 用户表单类型
 */
export interface UserForm {
  id?: string;
  userId?: string;
  deptId?: number;
  userName: string;
  nickName?: string;
  password: string;
  phonenumber?: string;
  email?: string;
  sex?: string;
  status: string;
  remark?: string;
  postIds: string[];
  roleIds: string[];
  branchType?: string; // 渠道类型
  idNo?: string; // 证件号码
  bindIp?: string; // 账号绑定IP
  accessTime?: string[]; // 允许访问时间[开始, 结束]
  accType?: number; // 账户类型
  validDate?: string[]; // 账户有效期[起期, 止期]
  realName?: string; // 真实姓名
  sysType?: string; // 系统内置 Y/N
}

export interface UserInfoVO {
  user: UserVO;
  roles: RoleVO[];
  roleIds: string[];
  posts: PostVO[];
  postIds: string[];
  roleGroup: string;
  postGroup: string;
}

export interface ResetPwdForm {
  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
}
