import request from '@/utils/http'
import { CountToObjectList,WeekChartObject,FiledCountList,MonthChartObject } from '@/types/website/dashBoard'

export class dashBoardService {
  static getBlogDetailNumber() {
    return request.get<CountToObjectList>({
      url: '/admin/dashBoard/getBlogDetailNumber'
    })
  }
  
  static getUserAddInLastWeek() {
    return request.get<WeekChartObject>({
      url: '/admin/dashBoard/getUserAddInLastWeek'
    })
  }

  static getUserAddComparedToLastWeek() {
    return request.get<string>({
      url: '/admin/dashBoard/getUserAddComparedToLastWeek'
    })
  }

  static getAccessAndUserCondition() {
    return request.get<FiledCountList>({
      url: '/admin/dashBoard/getAccessAndUserCondition'
    })
  }
  
  static getArticleAddInThisYear() {
    return request.get<MonthChartObject>({
      url: '/admin/dashBoard/getArticleAddInThisYear'
    })
  }

  static getArticleAddCompareToLastWeek() {
    return request.get<string>({
      url: '/admin/dashBoard/getArticleAddCompareToLastWeek'
    })
  }
}