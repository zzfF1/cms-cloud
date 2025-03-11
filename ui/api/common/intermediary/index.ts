import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { AgentComQuery, BaseAgentComShowVO } from '@/api/common/intermediary/types';

// 查询代理机构列表
export const listBaseBranch = (query?: AgentComQuery): AxiosPromise<BaseAgentComShowVO[]> => {
  return request({
    url: '/buscommon/branchlist',
    method: 'get',
    params: query
  });
};
