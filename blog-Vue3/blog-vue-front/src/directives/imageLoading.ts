import type { ObjectDirective, DirectiveBinding } from 'vue';

const image: ObjectDirective<HTMLElement, string> = {
  beforeMount(el: HTMLElement, binding: DirectiveBinding<string>) {
    if (!binding.value) return;

    const img = new Image();
    img.src = binding.value;

    // 创建 loading 元素
    const cup = document.createElement("div");
    cup.classList.add("coffee_cup");
    el.appendChild(cup);

    img.onload = () => {
      if (el.contains(cup)) {
        el.removeChild(cup);
      }
    };

    img.onerror = () => {
      if (el.contains(cup)) {
        el.removeChild(cup);
      }
      // 你可以在此添加 fallback 逻辑，比如插入一张默认图片等
    };
  }
};

export default image;
