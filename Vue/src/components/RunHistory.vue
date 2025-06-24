<template>
    <el-collapse v-model="activeCollapse" accordion>
        <el-collapse-item v-for="record in filteredHistory" :key="record.id" :name="record.id.toString()">
            <template #title>
                <el-button @click.stop="$emit('delete', record.id)" type="danger" size="small" icon="Delete" circle
                    style="margin-right: 8px" />
                <strong>记录 #{{ record.id }}</strong> - {{ record.curTitle }} | {{ record.curType }} | {{
                    formatTime(record.createTime)
                }}
            </template>
            <el-descriptions :column="2" border size="small" style="margin-bottom: 10px;">
                <el-descriptions-item label="退出码">{{ record.exitStatus }}</el-descriptions-item>
                <el-descriptions-item label="状态">{{ record.status }}</el-descriptions-item>
                <el-descriptions-item label="执行时间">{{ (record.time / 1e6).toFixed(2) }} ms</el-descriptions-item>
                <el-descriptions-item label="运行时长">{{ (record.runTime / 1e6).toFixed(2) }} ms</el-descriptions-item>
                <el-descriptions-item label="内存">{{ (record.memory / 1024 / 1024).toFixed(2) }}
                    MB</el-descriptions-item>
            </el-descriptions>

            <el-form label-position="top">
                <el-form-item label="输入">
                    <el-input type="textarea" readonly :value="record.curInput" rows="2" />
                </el-form-item>
                <el-form-item label="代码">
                    <el-input type="textarea" readonly :value="record.curCode" rows="5" />
                </el-form-item>
                <el-form-item label="输出">
                    <el-input type="textarea" readonly :value="record.stdout" rows="3" />
                </el-form-item>
                <el-form-item label="错误">
                    <el-input type="textarea" readonly :value="record.stderr" rows="3" />
                </el-form-item>
            </el-form>
        </el-collapse-item>
    </el-collapse>
</template>
  
<script setup>
import { ref, computed } from 'vue'
import dayjs from 'dayjs'


const props = defineProps({
    visible: Boolean,
    runHistory: Array,
    filteredNoteIds: Array,
    timeRange: Array,          // [startDate, endDate]
    sortOrder: String
})

const emit = defineEmits(['update:visible', 'delete'])
const activeCollapse = ref('')

const formatTime = (raw) => {
    return new Date(raw).toLocaleString()
}
// const filteredHistory = computed(() =>
//     props.runHistory.filter(r => props.filteredNoteIds.includes(r.noteId))
// )
const filteredHistory = computed(() => {
  let history = props.runHistory.filter(r =>
    props.filteredNoteIds.includes(r.noteId)
  )

  if (props.timeRange && props.timeRange.length === 2) {
    const [start, end] = props.timeRange.map(d => dayjs(d))
    history = history.filter(r => {
      const runTime = dayjs(r.createTime)
      return runTime.isAfter(start.subtract(1, 'day')) && runTime.isBefore(end.add(1, 'day'))
    })
  }

  history = history.slice().sort((a, b) => {
    const t1 = new Date(a.createTime).getTime()
    const t2 = new Date(b.createTime).getTime()
    return props.sortOrder === 'desc' ? t2 - t1 : t1 - t2
  })

  return history
})

const visible = computed({
    get: () => props.visible,
    set: val => emit('update:visible', val)
})
</script>
  