import gsap from "gsap";
import { ScrollTrigger } from "gsap/dist/ScrollTrigger";

// 注册 ScrollTrigger 插件
gsap.registerPlugin(ScrollTrigger);

/**
 * 动画：水平移动
 * @param list 盒子元素列表
 * @param x 位移的数值
 * @param duration 动画持续时间，单位为秒（默认 1s）
 * @param ease 动画过渡函数（默认 "power1.inOut"）
 */
function gsapTransX(list: HTMLElement[], x: number, duration: number = 1, ease: string = "power1.inOut"): void {
  list.map((v) => {
    gsap.fromTo(
      v,
      {
        scrollTrigger: v,
        x,
      },
      {
        scrollTrigger: v,
        x: 0,
        duration,
        ease,
      }
    );
  });
}

/**
 * 动画：垂直移动
 * @param list 盒子元素列表
 * @param y 位移的数值
 * @param duration 动画持续时间，单位为秒（默认 1s）
 * @param ease 动画过渡函数（默认 "power1.inOut"）
 */
function gsapTransY(list: (HTMLElement | string)[], y: number, duration: number = 1, ease: string = "power1.inOut"): void {
  list.map((v) => {
    gsap.fromTo(
      v,
      {
        scrollTrigger: v,
        y,
      },
      {
        scrollTrigger: v,
        y: 0,
        duration,
        ease,
      }
    );
  });
}

/**
 * 动画：透明度与位移
 * @param list 盒子元素列表
 * @param opacity 透明度（默认 0）
 * @param duration 动画持续时间，单位为秒（默认 0.6s）
 * @param ease 动画过渡函数（默认 "power2.inOut"）
 */
function gsapTransXScale(list: HTMLElement[], opacity: number = 0, duration: number = 0.6, ease: string = "power2.inOut"): void {
  list.map((v) => {
    gsap.fromTo(
      v,
      {
        scrollTrigger: v,
        duration: 0,
        y: 50,
        opacity: opacity,
      },
      {
        scrollTrigger: v,
        duration,
        opacity: 1,
        y: 0,
        ease,
      }
    );
  });
}

/**
 * 字体动画：文字上移
 * @param name 字体元素的选择器
 * @returns GSAP 动画控制器
 */
function gsapTransFont(name: string): GSAPTween {
  return gsap.to(name, {
    y: -10,
    stagger: 0.3,
  });
}

/**
 * 歌词动画
 * @param name 歌词元素选择器
 * @param duration 动画持续时间
 * @param reverse 是否反向动画（默认 false）
 * @param dom 可选的 DOM 元素，用于反向动画完成后清除滤镜
 * @returns GSAP 动画控制器
 */
function gsapTransLyric(name: string, duration: number, reverse: boolean = false, dom?: HTMLElement): GSAPTween {
  let gsapControl: GSAPTween;

  if (!reverse) {
    gsapControl = gsap.fromTo(
      name,
      {
        scaleX: 1.1,
        opacity: 0.5,
      },
      {
        opacity: 1,
        scaleX: 1,
        duration,
      }
    );
  } else {
    gsapControl = gsap.fromTo(
      name,
      {
        opacity: 1,
        scaleX: 1,
        filter: `blur(0px)`,
      },
      {
        opacity: 0,
        filter: `blur(2px)`,
        scaleX: 1.1,
        duration,
        onComplete() {
          if (dom) {
            dom.style.filter = "blur(0)";
          }
        },
      }
    );
  }

  return gsapControl;
}

/**
 * 歌词从左到右的背景动画
 * @param name 歌词元素选择器
 * @param duration 动画持续时间
 * @returns GSAP 动画控制器
 */
function gsapTransLyricLeftToRight(name: string, duration: number): GSAPTween {
  return gsap.fromTo(
    name,
    {
      backgroundSize: `0% 100%`,
    },
    {
      backgroundSize: `100% 100%`,
      duration,
      ease: "none",
    }
  );
}

export {
  gsapTransX,
  gsapTransXScale,
  gsapTransY,
  gsapTransFont,
  gsapTransLyric,
  gsapTransLyricLeftToRight,
};
