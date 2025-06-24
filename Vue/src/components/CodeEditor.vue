<template>
  <div :style="{ fontSize: fontSize + 'px' }" class="code-editor" ref="editorRef">
    <Codemirror v-model="code" :extensions="extensions" :theme="oneDark" placeholder="请输入代码..." />

  </div>
</template>
  
<script setup>
import { Codemirror } from 'vue-codemirror'
import { oneDark } from '@codemirror/theme-one-dark'

import { javascript } from '@codemirror/lang-javascript'
import { python } from '@codemirror/lang-python'
import { cpp } from '@codemirror/lang-cpp'
import { java } from '@codemirror/lang-java'
import { rust } from '@codemirror/lang-rust'
import { html } from '@codemirror/lang-html'
import { computed } from 'vue'
import { EditorView } from '@codemirror/view'
import { onMounted, ref } from 'vue'

const editorRef = ref(null)

const props = defineProps({
  modelValue: String,
  language: String,
  fontSize: { type: Number, default: 18 }
})
const emit = defineEmits(['update:modelValue'])

const code = computed({
  get: () => props.modelValue,
  set: val => emit('update:modelValue', val)
})

const handleKeydown = (event) => {
  if ((event.ctrlKey || event.metaKey) && event.key === 's') {
    event.preventDefault()
    console.log('Ctrl+S 被触发，执行保存逻辑')

    emit('save', code.value)
  }
}


// 自定义样式扩展：滚动 + 左对齐
const editorTheme = EditorView.theme({
  '.cm-scroller': {
    maxHeight: '500px',
    overflow: 'auto'
  }
})

const editorAttrs = EditorView.editorAttributes.of({
  style: 'text-align: left;'
})

const languageExtension = (() => {
  // console.log(props.language?.toLowerCase())
  switch (props.language?.toLowerCase()) {
    case 'javascript': return [javascript()]
    case 'python': return [python()]
    case 'c': case 'c++': return [cpp()]
    case 'java': return [java()]
    case 'rust': return [rust()]
    case 'html': return [html()]
    default: return []
  }
})()

const extensions = computed(() => {
  const result = []
  result.push(languageExtension)
  result.push(editorAttrs)
  result.push(editorTheme)
  console.log(result)
  return result
})

onMounted(() => {
  if (editorRef.value) {
    editorRef.value.addEventListener('keydown', handleKeydown)
  }
})
</script>
  
<style scoped>
.code-editor {
  width: 100%;
  height: 400px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.code-editor .cm-editor {
  height: 100%;
}
</style>
  