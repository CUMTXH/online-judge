<script lang="ts" setup>
import { repository } from '~/../package.json'
import { toggleDark } from '~/composables'
</script>

<template>
  <el-menu class="el-menu-demo" mode="horizontal" :ellipsis="false" router>
    <el-menu-item index="/">
      <div class="flex items-center justify-center gap-2">
        <div class="text-xl" i-ep-element-plus />
        <span>HOME
        </span>
      </div>
    </el-menu-item>

    <el-menu-item index="/notes">
      <div class="flex items-center justify-center gap-2">
        <span>代码</span>
      </div>
    </el-menu-item>

    <!-- <el-menu-item index="/com">
      <div class="flex items-center justify-center gap-2">
        <span>社区</span>
      </div>
    </el-menu-item> -->

    <el-menu-item h="full" @click="toggleDark()">
      <button class="w-full cursor-pointer border-none bg-transparent" style="height: var(--ep-menu-item-height)">
        <i inline-flex i="dark:ep-moon ep-sunny" />
      </button>
    </el-menu-item>


  </el-menu>
</template>

<style lang="scss">
.el-menu-demo {
  &.ep-menu--horizontal>.ep-menu-item:nth-child(2) {
    margin-right: auto;
  }
}
</style>
