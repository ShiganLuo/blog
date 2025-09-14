// 字体大小项类型
interface FontSizeItem {
  key: number;
}

// 字体粗细项类型
interface FontWeightItem {
  key: number;
}

// 背景色类型（字符串）
type ColorValue = string;

// 选项卡类型
interface OptionTabItem {
  key: number;
  label: string;
}

// 字体大小列表
export const fontSizeList: FontSizeItem[] = [
  { key: 12 },
  { key: 14 },
  { key: 16 },
  { key: 18 },
  { key: 20 },
  { key: 22 },
  { key: 24 },
  { key: 28 },
  { key: 32 },
  { key: 36 },
];

// 字体宽度列表
export const fontWeightList: FontWeightItem[] = [
  { key: 400 },
  { key: 500 },
  { key: 600 },
  { key: 700 },
  { key: 800 },
  { key: 900 },
];

// 背景色列表
export const predefineColors: ColorValue[] = [
  "#ff4500",
  "#ff8c00",
  "#ffd700",
  "#90ee90",
  "#00ced1",
  "#1e90ff",
  "#c71585",
  "rgba(255, 69, 0, 0.68)",
  "rgb(255, 120, 0)",
  "hsv(51, 100, 98)",
  "hsva(120, 40, 94, 0.5)",
  "hsl(181, 100%, 37%)",
  "hsla(209, 100%, 56%, 0.73)",
  "#c7158577",
];

// 选项卡列表
export const opTabList: OptionTabItem[] = [
  { key: 0, label: "背景颜色" },
  { key: 1, label: "字体" },
  { key: 3, label: "标签" },
];

// 渐变色列表（目前为空）
export const gradientList: ColorValue[] = [];
