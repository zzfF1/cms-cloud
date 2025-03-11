///import { BaseBranchShowVO, BranchQuery } from '@/api/common/branch/types';

export const flag = ref();

/*
纯数字校验
 */
export const validateNumber = (rule: any, value: any, callback: any) => {
  const numberReg = /^\d+$|^\d+[.]?\d+$/;
  if (!numberReg.test(value)) {
    callback(new Error('请输入数字'));
  } else {
    callback();
  }
};

/**
 * 是否是手机号
 * @param rule
 * @param value
 * @param callback
 */
export const validatePhone = (rule: any, value: any, callback: any) => {
  const reg = /^[1][3-9][0-9]{9}$/;
  if (!reg.test(value)) {
    callback(new Error('请输入正确的手机号'));
  } else {
    callback();
  }
};

/**
 * 判断执业证是否为26位
 * @param rule
 * @param value
 * @param callback
 */

export const qualifiNoLength = (rule: any, value: any, callback: any) => {
  if (value.length != 26) {
    callback(new Error('执业证号必须是26位，请重新录入'));
  } else {
    callback();
  }
};

/**
 * 判断执业证是否为26位
 * @param rule
 * @param value
 * @param callback
 */

export const check = (rule: any, value: any, callback: any) => {
  flag.value = value;
  return true;
};

export const qualifiNoLength13 = (rule: any, value: any, callback: any) => {
  if ((flag.value === '13' || flag.value === '15') && value.length !== 26) {
    callback(new Error('执业证号必须是26位，请重新录入'));
  } else {
    callback();
  }
};


// 手机号校验
export const isValidMobile = (str: string) => /^1\d{10}$/.test(str);

// 身份证号校验
export const isValidIDCard = (str: string) => {
  const regIdCard = /^(^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$)|(^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])((\d{4})|\d{3}[Xx])$)$/;
  return regIdCard.test(str);
};

// 银行卡号校验
export const isValidBankCard = (rule: any, value: string, callback: Function) => {
  const luhmCheck = (str: string) => {
    let sum = 0;
    let shouldDouble = false;
    for (let i = str.length - 1; i >= 0; i--) {
      let digit = parseInt(str[i], 10);
      if (shouldDouble) {
        digit *= 2;
        if (digit > 9) digit -= 9;
      }
      sum += digit;
      shouldDouble = !shouldDouble;
    }
    return sum % 10 === 0;
  };
  if (/^[1-9]\d{9,24}$/.test(value) && luhmCheck(value)) {
    callback(); // 验证成功，调用回调函数无参数
  } else {
    callback(new Error('银行卡号格式不正确')); // 验证失败，调用回调函数并传递错误对象
  }
};
