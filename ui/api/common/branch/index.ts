import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { BaseBranchShowVO, BranchQuery } from '@/api/common/branch/types';

// 查询销售机构列表
export const listBaseBranch = (query?: BranchQuery): AxiosPromise<BaseBranchShowVO[]> => {
  return request({
    url: '/system/buscommon/branchlist',
    method: 'get',
    params: query
  });
};
