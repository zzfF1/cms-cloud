import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ElMainVO, ElMainForm, ElMainQuery } from '@/api/system/rule/types';

/**
 * 查询规则配置列表
 * @param query
 * @returns {*}
 */

export const listElMain = (query?: ElMainQuery): AxiosPromise<ElMainVO[]> => {
  return request({
    url: '/elmain/main/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询规则配置详细
 * @param id
 */
export const getElMain = (id: string | number): AxiosPromise<ElMainVO> => {
  return request({
    url: '/elmain/main/' + id,
    method: 'get'
  });
};

/**
 * 新增规则配置
 * @param data
 */
export const addElMain = (data: ElMainForm) => {
  return request({
    url: '/elmain/main',
    method: 'post',
    data: data
  });
};

/**
 * 修改规则配置
 * @param data
 */
export const updateElMain = (data: ElMainForm) => {
  return request({
    url: '/elmain/main',
    method: 'put',
    data: data
  });
};

/**
 * 删除规则配置
 * @param id
 */
export const delElMain = (id: string | number | Array<string | number>) => {
  return request({
    url: '/elmain/main/' + id,
    method: 'delete'
  });
};
