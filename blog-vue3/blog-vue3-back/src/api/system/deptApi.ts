import request from '@/utils/http'
import { DeptResult, DeptListResult } from '@/types/system/dept'
import { CodeMsgResult } from '@/types/axios'

// 部门
export class DeptService {
  // 查询部门列表
  static listDept(query: any) {
    return request.get<DeptListResult>({
      url: '/system/dept/list',
      params: query
    })
  }

  // 查询部门列表（排除节点）
  static listDeptExcludeChild(deptId: any) {
    return request.get<DeptListResult>({
      url: '/system/dept/list/exclude/' + deptId
    })
  }

  // 查询部门详细
  static getDept(deptId: any) {
    return request.get<DeptResult>({
      url: '/system/dept/' + deptId
    })
  }

  // 新增部门
  static addDept(data: any) {
    return request.post({
      url: '/system/dept',
      data: data
    })
  }

  // 修改部门
  static updateDept(data: any) {
    return request.put({
      url: '/system/dept',
      data: data
    })
  }

  // 删除部门
  static deleteDept(deptId: any) {
    return request.del({
      url: '/system/dept/' + deptId
    })
  }
}
