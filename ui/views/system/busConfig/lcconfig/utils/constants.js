// constants.js
export const ACTION_TYPES = {
  0: '提交进入时',
  1: '退回时',
  2: '提交时',
  3: '退回进入时'
};

export const DZ_TYPES = {
  0: 'SQL',
  3: '实现类'
};

export const VALIDATION_RULES = {
  // 节点表单验证规则
  node: {
    name: [
      { required: true, message: '请输入节点名称', trigger: 'blur' },
      { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
    ],
    recno: [{ required: true, message: '请输入顺序号', trigger: 'blur' }]
  },

  // 动作表单验证规则
  action: {
    name: [{ required: true, message: '请输入动作名称', trigger: 'blur' }],
    type: [{ required: true, message: '请选择类型', trigger: 'change' }],
    dz: [{ required: true, message: '请输入动作内容', trigger: 'blur' }],
    dzType: [{ required: true, message: '请选择动作类型', trigger: 'change' }]
  },

  // 检查表单验证规则
  check: {
    name: [{ required: true, message: '请输入检查名称', trigger: 'blur' }],
    type: [{ required: true, message: '请选择类型', trigger: 'change' }],
    checkTj: [{ required: true, message: '请输入检查条件', trigger: 'blur' }],
    checkMsg: [{ required: true, message: '请输入提示信息', trigger: 'blur' }]
  },

  // 跳转表单验证规则
  jump: {
    recno: [{ required: true, message: '请输入顺序号', trigger: 'blur' }],
    lcNextId: [{ required: true, message: '请选择下一节点', trigger: 'change' }],
    tzTj: [{ required: true, message: '请输入跳转条件', trigger: 'blur' }],
    sm: [{ required: true, message: '请输入说明', trigger: 'blur' }]
  }
};
