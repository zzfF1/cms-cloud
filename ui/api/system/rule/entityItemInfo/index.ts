import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { EntityItemInfoForm, EntityItemInfoQuery, EntityItemInfoVO } from './types';

/**
 * 查询实体属性信息列表
 * @param query
 * @returns {*}
 */

export const listEntityItemInfo = (query?: EntityItemInfoQuery): AxiosPromise<EntityItemInfoVO[]> => {
  return request({
    url: '/system/entityItemInfo/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询实体属性信息详细
 * @param itemId
 */
export const getEntityItemInfo = (itemId: string | number): AxiosPromise<EntityItemInfoVO> => {
  return request({
    url: '/system/entityItemInfo/' + itemId,
    method: 'get'
  });
};

/**
 * 新增实体属性信息
 * @param data
 */
export const addEntityItemInfo = (data: EntityItemInfoForm) => {
  return request({
    url: '/system/entityItemInfo',
    method: 'post',
    data: data
  });
};

/**
 * 修改实体属性信息
 * @param data
 */
export const updateEntityItemInfo = (data: EntityItemInfoForm) => {
  return request({
    url: '/system/entityItemInfo',
    method: 'put',
    data: data
  });
};

/**
 * 删除实体属性信息
 * @param itemId
 */
export const delEntityItemInfo = (itemId: string | number | Array<string | number>) => {
  return request({
    url: '/system/entityItemInfo/' + itemId,
    method: 'delete'
  });
};
