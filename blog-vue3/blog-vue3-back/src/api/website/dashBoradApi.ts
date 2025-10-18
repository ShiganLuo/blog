import request from '@/utils/http'
import { CountToObjectList,WeekChartObject,FiledCountList,MonthChartObject } from '@/types/website/dashBoard'

export class dashBoardService {
  static getBlogDetailNumber() {
    return request.get<CountToObjectList>({
      url: '/api/admin/dashBoard/getBlogDetailNumber'
    })
  }
  
  static getUserAddInLastWeek() {
    return request.get<WeekChartObject>({
      url: '/api/admin/dashBoard/getUserAddInLastWeek'
    })
  }

  static getUserAddComparedToLastWeek() {
    return request.get<string>({
      url: '/api/admin/dashBoard/getUserAddComparedToLastWeek'
    })
  }

  static getAccessAndUserCondition() {
    return request.get<FiledCountList>({
      url: '/api/admin/dashBoard/getAccessAndUserCondition'
    })
  }
  
  static getArticleAddInThisYear() {
    return request.get<MonthChartObject>({
      url: '/api/admin/dashBoard/getArticleAddInThisYear'
    })
  }

  static getArticleAddCompareToLastWeek() {
    return request.get<string>({
      url: '/api/admin/dashBoard/getArticleAddCompareToLastWeek'
    })
  }
}