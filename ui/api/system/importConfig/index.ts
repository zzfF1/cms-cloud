import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ImportConfigVO, ImportConfigForm, ImportConfigQuery, SheetConfig } from './types';

/**
 * 查询导入模板列表
 * @param query
 * @returns {*}
 */
export const listImportConfig = (query?: ImportConfigQuery): AxiosPromise<ImportConfigVO[]> => {
  return request({
    url: '/system/importConfig/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询导入模板详细
 * @param id
 */
export const getImportConfig = (id: string | number): AxiosPromise<ImportConfigVO> => {
  return request({
    url: '/system/importConfig/' + id,
    method: 'get'
  });
};

/**
 * 查询导入模板配置详情（字段配置）
 * @param id
 */
export const getImportConfigDetail = (id: string | number): AxiosPromise<SheetConfig[]> => {
  return request({
    url: '/system/importConfig/detail/' + id,
    method: 'get'
  });
};

/**
 * 新增导入模板
 * @param data
 */
export const addImportConfig = (data: ImportConfigForm) => {
  return request({
    url: '/system/importConfig',
    method: 'post',
    data: data
  });
};

/**
 * 修改导入模板
 * @param data
 */
export const updateImportConfig = (data: ImportConfigForm) => {
  return request({
    url: '/system/importConfig',
    method: 'put',
    data: data
  });
};

/**
 * 删除导入模板
 * @param id
 */
export const delImportConfig = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/importConfig/' + id,
    method: 'delete'
  });
};

/**
 * 保存导入模板字段配置
 * @param id 模板ID
 * @param data 字段配置数据
 */
export const saveImportConfigItems = (id: string | number, data: SheetConfig[]) => {
  return request({
    url: '/system/importConfig/saveItems/' + id,
    method: 'post',
    data: data
  });
};

/**
 * 下载导入模板
 * @param id
 */
export const downloadImportTemplate = (id: string | number) => {
  return request({
    url: '/system/importConfig/download/' + id,
    method: 'get',
    responseType: 'blob'
  });
};

/**
 * 一次性获取完整的模板信息（包含基本信息和Sheet配置）
 * @param id
 */
export const getFullImportConfig = (
  id: string | number
): AxiosPromise<{
  basicInfo: ImportConfigVO;
  sheetInfo: SheetConfig[];
}> => {
  return request({
    url: '/system/importConfig/full/' + id,
    method: 'get'
  });
};
