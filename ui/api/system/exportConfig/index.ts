import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ExportConfigVO, ExportConfigForm, ExportConfigQuery, SheetConfig, ExportConfigSubmitData, PreviewData } from './types';

/**
 * 查询导出配置列表
 * @param query
 * @returns {*}
 */
export const listExportConfig = (query?: ExportConfigQuery): AxiosPromise<ExportConfigVO[]> => {
  return request({
    url: '/system/exportConfig/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询导出配置详细信息（包含基本信息和sheets）
 * @param id
 */
export const getExportConfig = (id: string | number): AxiosPromise<ExportConfigVO> => {
  return request({
    url: '/system/exportConfig/' + id,
    method: 'get'
  });
};

/**
 * 新增导出配置
 * @param data
 */
export const addExportConfig = (data: ExportConfigSubmitData) => {
  return request({
    url: '/system/exportConfig',
    method: 'post',
    data: data
  });
};

/**
 * 修改导出配置
 * @param data
 */
export const updateExportConfig = (data: ExportConfigSubmitData) => {
  return request({
    url: '/system/exportConfig',
    method: 'put',
    data: data
  });
};

/**
 * 删除导出配置
 * @param id
 */
export const delExportConfig = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/exportConfig/' + id,
    method: 'delete'
  });
};

/**
 * 下载导出模板
 * @param id
 */
export const downloadExportTemplate = (id: string | number) => {
  return request({
    url: '/system/exportConfig/download/' + id,
    method: 'get',
    responseType: 'blob'
  });
};

/**
 * 上传模板文件
 * @param file
 */
export const uploadExportTemplate = (file: File) => {
  const formData = new FormData();
  formData.append('file', file);

  return request({
    url: '/system/exportConfig/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

/**
 * 预览数据
 * @param data
 */
export const previewExportData = (data: ExportConfigSubmitData): AxiosPromise<PreviewData> => {
  return request({
    url: '/system/exportConfig/preview',
    method: 'post',
    data: data
  });
};

/**
 * 一次性获取完整的配置信息（包含基本信息和Sheet配置）
 * @param id
 */
export const getFullExportConfig = (
  id: string | number
): AxiosPromise<{
  basicInfo: ExportConfigVO;
  sheetInfo: SheetConfig[];
}> => {
  return request({
    url: '/system/exportConfig/full/' + id,
    method: 'get'
  });
};
