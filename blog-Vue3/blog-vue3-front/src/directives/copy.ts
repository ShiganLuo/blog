import type { DirectiveBinding, ObjectDirective } from "vue";
import { ElMessage } from "element-plus";

// 扩展 HTMLElement 类型，使其支持自定义属性
interface CopyHTMLElement extends HTMLElement {
  __copy_content__?: string;
  __copy_handler__?: () => void;
}

const vCopy: ObjectDirective = {
  beforeMount(el: CopyHTMLElement, binding: DirectiveBinding<string>) {
    el.__copy_content__ = binding.value;

    const handler = () => {
      if (!el.__copy_content__) {
        console.warn("没有需要复制的目标内容");
        return;
      }

      const textarea = document.createElement("textarea");
      textarea.readOnly = true;
      textarea.style.position = "fixed";
      textarea.style.top = "-99999px";
      textarea.value = el.__copy_content__;

      document.body.appendChild(textarea);
      textarea.select();

      const success = document.execCommand("copy");
      if (success) {
        ElMessage.info("复制成功，剪贴板内容：" + el.__copy_content__);
      }

      document.body.removeChild(textarea);
    };

    el.__copy_handler__ = handler;
    el.addEventListener("click", handler);
  },

  updated(el: CopyHTMLElement, binding: DirectiveBinding<string>) {
    el.__copy_content__ = binding.value;
  },

  unmounted(el: CopyHTMLElement) {
    if (el.__copy_handler__) {
      el.removeEventListener("click", el.__copy_handler__);
      delete el.__copy_handler__;
    }
  },
};

export default vCopy;
