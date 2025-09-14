<script setup lang="ts">
import { watch, ref, nextTick } from "vue";
import { Delete, Plus, ZoomIn } from "@element-plus/icons-vue";
import type { UploadFile, UploadFiles } from "element-plus";

// Define the shape of a file item
interface FileItem {
  name?: string;
  url: string;
  uid?: number;
}

// Define the props interface
interface Props {
  fileList: FileItem[];
  limit: number;
  preview: boolean;
  multiple: boolean;
  width: number;
  height: number;
}

const emit = defineEmits<{
  (e: "update:fileList", value: FileItem[]): void;
  (e: "change", value: FileItem[]): void;
}>();

const props = withDefaults(defineProps<Props>(), {
  fileList: () => [],
  limit: 1,
  preview: false,
  multiple: false,
  width: 200,
  height: 200,
});

const dialogVisible = ref<boolean>(false);
const previewIndex = ref<number>(0);
const uploadFileList = ref<FileItem[]>([]);
const showUpload = ref<boolean>(true);

const uploadChange = (file: UploadFile, files: UploadFiles) => {
  nextTick(() => {
    // Cast UploadFiles to FileItem[] for consistency
    const newFileList = files as FileItem[];
    if (newFileList.length >= props.limit) {
      showUpload.value = false;
    }
    emit("change", newFileList);
    emit("update:fileList", newFileList);
  });
};

const handlePictureCardPreview = (file: UploadFile) => {
  // Find the index of the file in the list for initial preview index
  const index = uploadFileList.value.findIndex((v) => v.uid === file.uid);
  previewIndex.value = index;
  dialogVisible.value = true;
};

const closeImgViewer = () => {
  previewIndex.value = 0;
  dialogVisible.value = false;
};

const handleRemove = (file: UploadFile) => {
  if (file.url) {
    const index = uploadFileList.value.findIndex((v) => v.url === file.url);
    if (index !== -1) {
      uploadFileList.value.splice(index, 1);
    }
    showUpload.value = true;
    emit("change", uploadFileList.value);
    emit("update:fileList", uploadFileList.value);
  }
};

watch(
  () => props.fileList,
  (newVal) => {
    uploadFileList.value = newVal;
    if (newVal.length >= props.limit) {
      showUpload.value = false;
    }
    if (newVal.length === 0) {
      showUpload.value = true;
    }
  },
  {
    immediate: true,
    deep: true,
  }
);
</script>

<template>
  <el-upload
    ref="uploadAvatarRef"
    v-model:file-list="uploadFileList"
    :class="[showUpload && !preview ? '' : 'hide-upload']"
    action="#"
    list-type="picture-card"
    :auto-upload="false"
    :multiple="multiple"
    :limit="limit"
    :on-change="uploadChange"
  >
    <el-icon><Plus /></el-icon>
    <template #file="{ file }">
      <div>
        <el-image
          :style="{ width: width + 'px', height: height + 'px' }"
          fit="cover"
          :src="file.url"
          lazy
        />
        <span class="el-upload-list__item-actions">
          <span class="el-upload-list__item-preview" @click="handlePictureCardPreview(file)">
            <el-icon><zoom-in /></el-icon>
          </span>
          <span v-if="!preview" class="el-upload-list__item-delete" @click="handleRemove(file)">
            <el-icon><Delete /></el-icon>
          </span>
        </span>
      </div>
    </template>
  </el-upload>
  <el-image-viewer
    v-if="dialogVisible"
    :teleported="true"
    :url-list="uploadFileList.map((v) => v.url)"
    :initial-index="previewIndex"
    @close="closeImgViewer"
  />
</template>

<style lang="scss" scoped>
.hide-upload {
  :deep(.el-upload--picture-card) {
    display: none;
  }
}

.el-upload-list--picture-card {
  display: flex;
}
</style>
