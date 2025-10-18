import { number } from "echarts"

export interface CountToObject {
    des: string,
    icon: string,
    startVal: number,
    duration: number,
    endVal:number,
    change: string
}

export interface WeekChartObject {
    days: string[],
    counts: number[]
}

export interface FiledCount {
    name: string,
    num: string
}

export interface MonthChartObject {
    months: string[],
    counts: number[]
}

export type CountToObjectList = CountToObject[]
export type FiledCountList = FiledCount[]