<template>
    <footer class="app-footer">
        <el-row class="footer-container" :gutter="40" style="margin: 0 auto;">
            <el-col :span="6">
                <el-text tag="h4" size="large">社区</el-text>
                <el-space direction="vertical" alignment="start" style="margin-top: 16px;">
                    <el-link :underline="false" v-for="(link, i) in links" :key="i" :href="link.href" target="_blank">
                        {{ link.label }}
                    </el-link>
                </el-space>
            </el-col>

            <el-col :span="6">
                <el-text tag="h4" size="large">下载</el-text>
                <el-space direction="vertical" alignment="start" style="margin-top: 16px;">
                    <el-link :underline="false" v-for="(link, i) in downloads" :key="i" :href="link.href" target="_blank">
                        {{ link.label }}
                    </el-link>
                </el-space>
            </el-col>
        </el-row>
    </footer>
</template>

<script setup>
import { useDark } from '@vueuse/core'
const isDark = useDark()
const links = [
    { label: '牛客', href: 'https://www.nowcoder.com/' },
    { label: 'Leetcode', href: 'https://leetcode.cn/' },
    { label: 'Codeforces', href: 'https://codeforces.com/' },
    { label: 'Atcoder', href: '#https://atcoder.jp/' },
]

const downloads = [
    { label: 'Dev-cpp', href: 'https://github.com/Embarcadero/Dev-Cpp/releases' },
    { label: 'Python', href: 'https://www.python.org/downloads/' },
    { label: 'Java', href: 'https://www.oracle.com/cn/java/technologies/downloads/' },
    { label: 'Node.js', href: 'https://nodejs.org/zh-cn/download' },
]
</script>
  
<style scoped>
.app-footer {
    background-color: var(--ep-bg-color-page);
    color: var(--ep-text-color-regular);
    border-top: 1px solid var(--ep-border-color-light);
    padding: 32px 0;
    width: 100%;
}

.footer-container {
    text-align: left;
    max-width: 1000px;
    padding: 0 20px;
}
</style>
  