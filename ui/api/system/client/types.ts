// @/api/system/client/types.ts

/**
 * API权限类型定义
 */
export interface ApiPermission {
  api: string;
  method: string;
}

/**
 * 客户端配置类型定义
 */
export interface ClientConfig {
  ak?: string;
  sk?: string;
  encryptionMode?: string;
  sm2PublicKey?: string;
  sm2PrivateKey?: string;
  serverPath?: string;
  port?: number | string;
}

/**
 * 密钥对类型定义
 */
export interface KeyPair {
  publicKey: string;
  privateKey: string;
}

/**
 * 客户端查询参数
 */
export interface ClientQuery extends PageQuery {
  /**
   * 客户端id
   */
  clientId?: string | number;

  /**
   * 客户端key
   */
  clientKey?: string;

  /**
   * 客户端秘钥
   */
  clientSecret?: string;

  /**
   * 授权类型
   */
  grantType?: string;

  /**
   * 设备类型
   */
  deviceType?: string;

  /**
   * token活跃超时时间
   */
  activeTimeout?: number;

  /**
   * token固定超时
   */
  timeout?: number;

  /**
   * 状态（0正常 1停用）
   */
  status?: string;
}

/**
 * 客户端表单对象
 */
export interface ClientForm extends BaseEntity {
  /**
   * id
   */
  id?: string | number;

  /**
   * 客户端id
   */
  clientId?: string | number;

  /**
   * 客户端key
   */
  clientKey?: string;

  /**
   * 客户端秘钥
   */
  clientSecret?: string;

  /**
   * 授权类型
   */
  grantTypeList?: string[];

  /**
   * 设备类型
   */
  deviceType?: string;

  /**
   * token活跃超时时间
   */
  activeTimeout?: number;

  /**
   * token固定超时
   */
  timeout?: number;

  /**
   * 状态（0正常 1停用）
   */
  status?: string;

  /**
   * 客户端配置 - 直接使用对象
   */
  config?: ClientConfig;

  /**
   * API权限列表 - 直接使用对象数组
   */
  apiPermissions?: ApiPermission[];
}

/**
 * 客户端视图对象
 */
export interface ClientVO {
  /**
   * id
   */
  id: string | number;

  /**
   * 客户端id
   */
  clientId: string;

  /**
   * 客户端key
   */
  clientKey: string;

  /**
   * 客户端秘钥
   */
  clientSecret: string;

  /**
   * 授权类型
   */
  grantTypeList: string[];

  /**
   * 设备类型
   */
  deviceType: string;

  /**
   * token活跃超时时间
   */
  activeTimeout: number;

  /**
   * token固定超时
   */
  timeout: number;

  /**
   * 状态（0正常 1停用）
   */
  status: string;

  /**
   * 客户端配置 - 直接使用对象
   */
  config?: ClientConfig;

  /**
   * API权限列表 - 直接使用对象数组
   */
  apiPermissions?: ApiPermission[];
}
