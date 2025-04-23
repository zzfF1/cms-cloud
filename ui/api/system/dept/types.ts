/**
 * 部门查询参数
 */
export interface DeptQuery extends PageQuery {
  deptName?: string;
  deptCategory?: string;
  status?: number;
}

/**
 * 部门类型
 */
export interface DeptVO extends BaseEntity {
  id: number | string;
  manageCom: string;
  parentName: string;
  parentId: number | string;
  children: DeptVO[];
  deptId: number | string;
  deptName: string;
  deptCategory: string;
  orderNum: number;
  leader: string;
  phone: string;
  email: string;
  status: string;
  delFlag: string;
  ancestors: string;
  menuId: string | number;
}

/**
 * 部门类型（集成Ldcom）
 */
export interface DeptVO extends BaseEntity {
  id: number | string;
  manageCom: string;
  parentName: string;
  parentId: number | string;
  children: DeptVO[];
  deptId: number | string;
  deptName: string;
  deptCategory: string;
  orderNum: number;
  leader: string;
  phone: string;
  email: string;
  status: string;
  delFlag: string;
  ancestors: string;
  menuId: string | number;

  // Ldcom相关字段
  outcomcode?: string;
  name?: string;
  shortname?: string;
  address?: string;
  zipcode?: string;
  comPhone?: string;
  fax?: string;
  comgrade?: string;
  regionalismcode?: string;
  upcomcode?: string;
}

/**
 * 部门树类型
 */
export interface DeptTreeVO extends BaseEntity {
  id: number | string;
  label: string;
  parentId: number | string;
  weight: number;
  children: DeptTreeVO[];
  disabled: boolean;
}

/**
 * 部门表单类型
 */
export interface DeptForm {
  parentName?: string;
  manageCom: string;
  parentId?: number | string;
  children?: DeptForm[];
  deptId?: number | string;
  deptName?: string;
  deptCategory?: string;
  orderNum?: number;
  leader?: string;
  phone?: string;
  email?: string;
  status?: string;
  delFlag?: string;
  ancestors?: string;

  // Ldcom相关字段
  outcomcode?: string;
  name?: string;
  shortname?: string;
  address?: string;
  zipcode?: string;
  comPhone?: string;
  fax?: string;
  comgrade?: string;
  regionalismcode?: string;
  upcomcode?: string;
}
