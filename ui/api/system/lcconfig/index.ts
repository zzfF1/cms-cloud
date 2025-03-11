import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { LcDefineVo, LcMainVo } from '@/api/system/lcconfig/types';

// 流程类型相关接口
export const listType = (): AxiosPromise<LcMainVo[]> => {
  return request({
    url: '/system/lcconfig/type/list',
    method: 'get'
  });
};

export function getType(serialno) {
  return request({
    url: `/system/lcconfig/type/${serialno}`,
    method: 'get'
  });
}

export function addType(data) {
  return request({
    url: '/system/lcconfig/type',
    method: 'post',
    data: data
  });
}

export function updateType(data) {
  return request({
    url: '/system/lcconfig/type',
    method: 'put',
    data: data
  });
}

export function delType(serialno) {
  return request({
    url: `/system/lcconfig/type/${serialno}`,
    method: 'delete'
  });
}

// 流程节点相关接口
export const listNode = (lcSerialno?: number): AxiosPromise<LcDefineVo[]> => {
  return request({
    url: `/system/lcconfig/node/list/${lcSerialno}`,
    method: 'get'
  });
};

export function getNode(id) {
  return request({
    url: `/system/lcconfig/node/${id}`,
    method: 'get'
  });
}

export function addNode(data) {
  return request({
    url: '/system/lcconfig/node',
    method: 'post',
    data: data
  });
}

export function updateNode(data) {
  return request({
    url: '/system/lcconfig/node',
    method: 'put',
    data: data
  });
}

export function delNode(id) {
  return request({
    url: `/system/lcconfig/node/${id}`,
    method: 'delete'
  });
}
