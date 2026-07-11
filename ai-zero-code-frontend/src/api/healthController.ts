// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 GET /health/check */
export async function check(options?: { [key: string]: any }) {
  return request<API.BaseResponseString>('/health/check', {
    method: 'GET',
    ...(options || {}),
  })
}
