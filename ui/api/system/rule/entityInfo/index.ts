import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { EntityInfoForm, EntityInfoQuery, EntityInfoVO } from './types';

/**
 * 查询规则引擎实体信息列表
 * @param query
 * @returns {*}
 */

export const listEntityInfo = (query?: EntityInfoQuery): AxiosPromise<EntityInfoVO[]> => {
  return request({
    url: '/system/entityInfo/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询规则引擎实体信息详细
 * @param entityId
 */
export const getEntityInfo = (entityId: string | number): AxiosPromise<EntityInfoVO> => {
  return request({
    url: '/system/entityInfo/' + entityId,
    method: 'get'
  });
};

/**
 * 新增规则引擎实体信息
 * @param data
 */
export const addEntityInfo = (data: EntityInfoForm) => {
  return request({
    url: '/system/entityInfo',
    method: 'post',
    data: data
  });
};

/**
 * 修改规则引擎实体信息
 * @param data
 */
export const updateEntityInfo = (data: EntityInfoForm) => {
  return request({
    url: '/system/entityInfo',
    method: 'put',
    data: data
  });
};

/**
 * 删除规则引擎实体信息
 * @param entityId
 */
export const delEntityInfo = (entityId: string | number | Array<string | number>) => {
  return request({
    url: '/system/entityInfo/' + entityId,
    method: 'delete'
  });
};
