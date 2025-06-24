<template>
  <el-config-provider namespace="ep">
    <div class="app-wrapper">
      <BaseHeader />

      <div class="main-container">
        <RouterView />
        <!-- <CodeEditor v-model="content" :language="type"  /> -->
        <!-- <CodeTest v-model="content" /> -->
      </div>

      <AppFooter />
    </div>
  </el-config-provider>
</template>

<script setup>
import { RouterView } from 'vue-router'
// import { ref } from 'vue'
// import CodeEditor from '~/components/CodeEditor.vue'
// import CodeTest from '~/components/CodeTest.vue'
// const content  = ref('#include<iostream> \n using namespace std; \n int main(){ \n cout<<endl; \n return 0; \n}')
// const type  = ref('c++')

</script>

<style scoped>
#app {
  text-align: center;
  color: var(--ep-text-color-primary);
}

.app-wrapper {
  text-align: center;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* 主内容区域自动填充中间空间 */
.main-container {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}
</style>
