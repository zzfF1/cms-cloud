import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { LawVersionVO, LawVersionForm, LawVersionQuery, BaseLawVerDetailsVo } from '@/api/system/lawVersion/types';

/**
 * 查询基本法版本列表
 * @param query
 * @returns {*}
 */

export const listLawVersion = (query?: LawVersionQuery): AxiosPromise<LawVersionVO[]> => {
  return request({
    url: '/system/lawVersion/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询基本法版本详细
 * @param id
 */
export const getLawVersion = (id: string | number): AxiosPromise<LawVersionVO> => {
  return request({
    url: '/system/lawVersion/' + id,
    method: 'get'
  });
};

/**
 * 新增基本法版本
 * @param data
 */
export const addLawVersion = (data: LawVersionForm) => {
  return request({
    url: '/system/lawVersion',
    method: 'post',
    data: data
  });
};

/**
 * 修改基本法版本
 * @param data
 */
export const updateLawVersion = (data: LawVersionForm) => {
  return request({
    url: '/system/lawVersion',
    method: 'put',
    data: data
  });
};

/**
 * 删除基本法版本
 * @param id
 */
export const delLawVersion = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/lawVersion/' + id,
    method: 'delete'
  });
};

/**
 * 查询基本法配置
 * @param id
 */
export const getBaseLawConfig = (id: string | number): AxiosPromise<BaseLawVerDetailsVo> => {
  return request({
    url: '/system/lawConfig/' + id,
    method: 'get'
  });
};
