import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { PageConfigVO, PageConfigForm, PageConfigQuery } from '@/api/system/pageConfig/types';

/**
 * 查询界面配置列表
 * @param query
 * @returns {*}
 */
export const listPageConfig = (query?: PageConfigQuery): AxiosPromise<PageConfigVO[]> => {
  return request({
    url: '/system/pageConfig/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询界面配置详细
 * @param id
 */
export const getPageConfig = (id: string | number): AxiosPromise<PageConfigVO> => {
  return request({
    url: '/system/pageConfig/' + id,
    method: 'get'
  });
};

/**
 * 新增界面配置
 * @param data
 */
export const addPageConfig = (data: PageConfigForm) => {
  return request({
    url: '/system/pageConfig',
    method: 'post',
    data: data
  });
};

/**
 * 修改界面配置
 * @param data
 */
export const updatePageConfig = (data: PageConfigForm) => {
  return request({
    url: '/system/pageConfig',
    method: 'put',
    data: data
  });
};

/**
 * 删除界面配置
 * @param id
 */
export const delPageConfig = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/pageConfig/' + id,
    method: 'delete'
  });
};
