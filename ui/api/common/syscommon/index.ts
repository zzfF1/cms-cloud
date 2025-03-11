import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { HelpDocShowVo, LabelShowVo, LcProcessShowVo, LdComQueryBo, ManageComVo, SysPageConfigTabVo } from '@/api/common/syscommon/types';
import dayjs from 'dayjs';

export const queryLcProcess = (dataId?: any): AxiosPromise<LcProcessShowVo[]> => {
  return request({
    url: '/system/buscommon/lcproclist',
    method: 'get',
    params: dataId
  });
};

/*
  查询基础标签方法
 */
export const queryBaseLabel = (url?: string, data?: any): AxiosPromise<LabelShowVo[]> => {
  return request({
    url: url,
    method: 'post',
    data: data
  });
};
/*
查询文档内容
 */
export const queryHelpDoc = (busCode?: any): AxiosPromise<HelpDocShowVo> => {
  return request({
    url: '/system/buscommon/queryHelpDoc',
    method: 'get',
    params: { busCode: busCode }
  });
};
/**
 * 导入数据
 * @param url 请求地址
 * @param data 附件数据
 */
export const importDataFile = (url: string, data: FormData): AxiosPromise<any> => {
  return request({
    url: url,
    method: 'post',
    data: data
  });
};

/**
 * 管理机构树
 * @param queryBo 查询条件
 */
export const manageTreeSelect = (queryBo?: LdComQueryBo): AxiosPromise<ManageComVo[]> => {
  return request({
    url: '/system/buscommon/manageTree',
    method: 'get',
    params: queryBo
  });
};

/**
 * 查询页面标签
 * @param pageCode 页面编码
 */
export const queryPageTable = (pageCode?: string): AxiosPromise<SysPageConfigTabVo[]> => {
  return request({
    url: '/system/buscommon/pageTabConfig',
    method: 'get',
    params: { pageCode: pageCode }
  });
};

/**
 * 获取服务器当前日期
 */
export const getSystemServiceDate = async (): Promise<string> => {
  return request({
    url: '/system/buscommon/getDate',
    method: 'get'
  })
    .then((response) => {
      if (response.code === 200) {
        return response.msg; // 如果 code 是 200，返回 msg
      } else {
        return dayjs().format('YYYY-MM-DD'); // 否则返回当前日期
      }
    })
    .catch(() => {
      // 捕获错误时返回当前日期
      return dayjs().format('YYYY-MM-DD');
    });
};

/**
 * 调动附件上传
 * @param data 附件
 */
export const sysUploadFile = (data: FormData) => {
  return request({
    url: '/system/buscommon/uploadFile',
    method: 'post',
    data: data
  });
};

/**
 * 删除附件
 * @param attIds 附件ID
 * @param busCode 业务类型
 */
export function delAttachments(attIds: string | number | Array<string | number>, busCode: string) {
  return request({
    url: '/system/buscommon/remove/' + attIds + '?busCode=' + busCode,
    method: 'delete'
  });
}
