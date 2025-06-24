<!-- src/pages/note.vue -->
<script setup >
import { ref, onMounted, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '~/composables/useAuth'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Plus, Delete } from '@element-plus/icons-vue'
import jwtDecode from 'jwt-decode'
import axios from '~/utils/request'
import CodeEditor from '~/components/CodeEditor.vue'
import RunHistory from '~/components/RunHistory.vue'
import dayjs from 'dayjs'

const router = useRouter()
const { logout } = useAuth()
const username = ref('未知用户')
const userid = ref('0')
const notes = ref([]) //  所有代码
const dialogVisible = ref(false) //  控制添加代码弹窗
const langs = ['C', 'C++', 'Java', 'Python', 'Javascript', 'Rust', 'Html']

const sortBy = ref('updated')  // updated 或 created
const timeRange = ref([])
const sortOrder = ref('desc')
const userFontSize = ref('14')
const viewMode = ref('note')
const dummyDialogVisible = ref(false)
const filterLang = ref('全部')// 当前筛选语言，默认为“全部”
const filterTags = ref([])
const allTags = ref([])
const newTagDialogVisible = ref(false)
const newTagName = ref("")
const allNotes = ref([])// 所有笔记（从后端请求得到）

const filteredNoteBase = computed(() => {
  let notes = allNotes.value
  if (filterLang.value !== '全部') {
    notes = notes.filter(note => note.type === filterLang.value)
  }
  if (filterTags.value && filterTags.value.length > 0) {
    notes = notes.filter(note =>
      filterTags.value.every(tagId => note.tags.includes(tagId))
    )
  }
  return notes
})

const filteredNoteIds = computed(() => filteredNoteBase.value.map(note => note.noteId))

const filteredNotes = computed(() => {
  let notes = filteredNoteBase.value

  if (timeRange.value && timeRange.value.length === 2) {
    const [start, end] = timeRange.value.map(d => dayjs(d))
    notes = notes.filter(note => {
      const time = dayjs(sortBy.value === 'updated' ? note.updatedAt : note.createdAt)
      return time.isAfter(start.subtract(1, 'day')) && time.isBefore(end.add(1, 'day'))
    })
  }
  notes = notes.slice().sort((a, b) => {
    const t1 = new Date(sortBy.value === 'updated' ? a.updatedAt : a.createdAt).getTime()
    const t2 = new Date(sortBy.value === 'updated' ? b.updatedAt : b.createdAt).getTime()
    return sortOrder.value === 'desc' ? t2 - t1 : t1 - t2
  })
  return notes;
})

const formatTagNames = (tagIds) => {
  const names = allTags.value
    .filter(tag => tagIds.includes(tag.id))
    .map(tag => tag.name)
  return names.length ? `标签: [${names.join(', ')}]` : '标签: [无]'
}
const handleTagChange = (value) => {
  if (value.includes("add_new_tag")) {
    editNote.tags = editNote.tags.filter(tag => tag !== "add_new_tag")
    newTagDialogVisible.value = true
  }
}

const submitNewTag = async () => {
  const name = newTagName.value.trim()
  if (!name) return
  try {
    const res = await axios.post("/api/tags", { name })
    const addedTag = res.data // 包含 id 和 name

    await fetchTags()
    // 选中新标签

    if (!editNote.value.tags.includes(addedTag.id)) {
      editNote.value.tags.push(addedTag.id)
    }
    console.log(editNote.value.tags, addedTag)
    ElMessage.success("标签添加成功")
    newTagDialogVisible.value = false
    newTagName.value = ""
  } catch (e) {
    ElMessage.error("添加失败，可能是标签已存在")
  }
}

const fetchTags = async () => {
  try {
    const res = await axios.get('/api/tags')
    console.log("返回标签：", res)
    allTags.value = res.data
    console.log('对象：', allTags.value)

  } catch (err) {
    console.error("请求失败，错误信息：", err)
    ElMessage.error('获取失败')
  }
}

// 显示的笔记（根据筛选更新）
//  添加代码表单
const newNote = ref({
  title: '',
  content: '',
  type: ''
})
const token = localStorage.getItem('token')
const parseToken = () => {
  try {
    if (!token) {
      ElMessage.error('请先登录')
      router.push('/')
      return
    }
    const decoded = jwtDecode(token)
    console.log(decoded)
    console.log(token)
    username.value = decoded.sub || '未知用户'
    userid.value = decoded.userId || '未知用户'
  } catch (err) {
    ElMessage.error('用户信息获取失败')
  }
}
//  获取代码列表
const fetchNotes = async () => {
  try {
    const token = localStorage.getItem('token')
    console.log("准备发起请求 /api/notes")
    console.log("当前 token:", token)
    const res = await axios.get('http://localhost:8080/api/notes', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    console.log("请求成功，返回内容：", res)
    notes.value = res.data.data
    allNotes.value = res.data.data
    notes.value.forEach(note => {
      console.log('代码对象：', note)
    })
  } catch (err) {
    console.error("请求失败，错误信息：", err)
    ElMessage.error('获取代码失败')
  }
}
//  添加代码
const addNote = async () => {
  if (!newNote.value.title || !newNote.value.content) {
    ElMessage.warning('标题和内容不能为空')
    return
  }
  try {
    await axios.post('/api/notes/add', newNote.value)
    ElMessage.success('添加成功')
    dialogVisible.value = false
    newNote.value = { title: '', content: '', type: '' } // 清空表单
    fetchNotes()
  } catch (err) {
    ElMessage.error('添加失败')
  }
}
// 删除代码
const deleteNote = async (id) => {
  try {
    await axios.delete(`/api/notes/${id}`)
    ElMessage.success('删除成功')
    fetchNotes()
  } catch (err) {
    ElMessage.error('删除失败')
  }
}
const editNote = ref({
  noteId: null,
  title: '',
  content: '',
  type: '',
  input: ''
})
//编辑代码
const editDialogVisible = ref(false)
const openEditDialog = (note) => {
  editNote.value = { ...note } // 拷贝原代码内容进编辑对象
  editDialogVisible.value = true
}

const updateNote = async () => {
  if (!editNote.value.title || !editNote.value.content) {
    ElMessage.warning('标题和内容不能为空')
    return
  }
  try {
    await axios.put(`/api/notes/${editNote.value.noteId}`, {
      title: editNote.value.title,
      content: editNote.value.content,
      type: editNote.value.type,
      input: editNote.value.input,
      tags: editNote.value.tags
    })
    ElMessage.success('修改成功')
    editDialogVisible.value = false
    fetchNotes()
  } catch (err) {
    console.error('修改失败：', err)
    ElMessage.error('修改失败')
  }
}
const handleLogout = () => {
  logout()
  ElMessage.success('已退出登录')
  router.push('/')
}

// 运行代码

const resultDialogVisible = ref(false)
const runResult = reactive({
  status: '',
  exitStatus: 0,
  time: 0,
  memory: 0,
  runTime: 0,
  stdout: '',
  stderr: ''
})
const runCode = async () => {
  try {
    const response = await axios.put(`/api/notes/run/${editNote.value.noteId}`, {
      title: editNote.value.title,
      content: editNote.value.content,
      type: editNote.value.type,
      input: editNote.value.input,
      tags: editNote.value.tags
    }, { timeout: 30000 })
    const data = response.data.data;
    console.log(data);
    runResult.value = {
      status: data.status,
      stdout: data.stdout || "",
      stderr: data.stderr || "",
      exitStatus: data.exitStatus,
      time: data.time,
      runTime: data.runTime,
      memory: data.memory
    };
    console.log(runResult.value);
    saveRunResult();
    resultDialogVisible.value = true
  } catch (error) {
    runResult.status = '运行失败'
    runResult.stderr = error.message
    resultDialogVisible.value = true
    console.error('运行失败:', error)
  }
}
//保存运行结果
const saveRunResult = async () => {
  try {
    const response = await axios.post('/api/runresults/add', {
      noteId: editNote.value.noteId,
      stdout: runResult.value.stdout,
      stderr: runResult.value.stderr,
      status: runResult.value.status,
      exitStatus: runResult.value.exitStatus,
      time: runResult.value.time,
      runTime: runResult.value.runTime,
      memory: runResult.value.memory,
      curTitle: editNote.value.title,
      curCode: editNote.value.content,
      curInput: editNote.value.input,
      curType: editNote.value.type,
    });
    console.log('保存运行结果成功:', response.data);
  } catch (error) {
    console.error('保存运行结果失败', error);
  }
}
//查询代码运行记录
const historyDialogVisible = ref(false)
const activeCollapse = ref('')
const runHistory = ref([])

const fetchRunHistory = async () => {
  try {
    const res = await axios.get(`/api/runresults`)
    runHistory.value = res.data
    console.log(runHistory);
  } catch (err) {
    console.error("获取运行记录失败", err)
  }
}

const showRunHistory = async () => {
  try {
    const res = await axios.get(`/api/runresults/note/${editNote.value.noteId}`)
    runHistory.value = res.data
    console.log(runHistory);
    historyDialogVisible.value = true
  } catch (err) {
    console.error("获取运行记录失败", err)
  }
}

const formatTime = (raw) => {
  return new Date(raw).toLocaleString()
}

//删除代码运行记录
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该运行记录？', '提示', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
    })
    await axios.delete(`/api/runresults/${id}`)
    runHistory.value = runHistory.value.filter(item => item.id !== id)
    ElMessage.success('删除成功')
  } catch (err) {
    if (err !== 'cancel') ElMessage.error('删除失败')
  }
}


onMounted(() => {
  parseToken()
  fetchNotes()
  fetchTags()
  fetchRunHistory()
})
</script>

<template>
  <div class="notes-container">
    <!-- 用户栏 -->
    <div class="user-bar">
      <div class="user-info">
        <el-icon :size="20">
          <User />
        </el-icon>
        <span class="username">UID:{{ userid }}用户名:{{ username }} </span>
      </div>
      <el-button type="danger" @click="handleLogout">退出登录</el-button>
    </div>
    <!-- 内容区域 -->
    <div class="content">
      <h2>Welcome</h2>
      <el-button type="primary" @click="dialogVisible = true"><el-icon>
          <Plus />
        </el-icon>新增代码</el-button>

      <el-divider />
      <div class="view-mode-bar" style="display: flex; justify-content: space-between; align-items: center;">
        <div class="button-group">
          <el-button type="primary" @click="viewMode = 'note'" :plain="viewMode !== 'note'">我的代码</el-button>
          <el-button type="primary" @click="viewMode = 'history'; fetchRunHistory()"
            :plain="viewMode !== 'history'">运行记录</el-button>
        </div>

        <!-- 筛选区域 -->
        <div style="display: flex; align-items: center; gap: 12px;">
          <span>代码类型</span>
          <el-select v-model="filterLang" placeholder="筛选语言类型" style="width: 160px;">
            <el-option label="全部" value="全部" />
            <el-option v-for="lang in langs" :key="lang" :label="lang" :value="lang" />
          </el-select>

          <span>算法标签</span>
          <el-select v-model="filterTags" multiple clearable placeholder="筛选标签" style="width: 240px;">
            <el-option v-for="tag in allTags" :key="tag.id" :label="tag.name" :value="tag.id" />
          </el-select>
        </div>


      </div>

      <el-divider />
      <!-- 时间排序按钮组 -->
      <div style="display: flex; align-items: center; gap: 12px; margin-top: 12px;">
        <div v-show="viewMode === 'note'">
          <span style="display: block;">排序方式</span>
          <el-radio-group v-model="sortBy" size="small">
            <el-radio-button label="updated">按修改时间</el-radio-button>
            <el-radio-button label="created">按创建时间</el-radio-button>
          </el-radio-group>
        </div>
        <span style="margin-left: 20px;">时间范围：</span>
        <el-date-picker v-model="timeRange" type="daterange" unlink-panels start-placeholder="起始日期" end-placeholder="结束日期"
          value-format="YYYY-MM-DD" style="width: 260px;" />
        <span style="margin-left: 20px;">排序顺序：</span>
        <el-radio-group v-model="sortOrder" size="small">
          <el-radio-button label="desc">降序</el-radio-button>
          <el-radio-button label="asc">升序</el-radio-button>
        </el-radio-group>
      </div>


      <el-divider />


      <!--  代码列表卡片 -->
      <div class="notes-list" v-show="viewMode === 'note'">
        <el-card v-for="note in filteredNotes" :key="note.noteId" class="note-card">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>{{ note.title }}</span>
              <el-popconfirm title="确定要删除这条代码吗？" confirm-button-text="确定" cancel-button-text="取消"
                @confirm="deleteNote(note.noteId)">
                <template #reference>
                  <el-button size="small" type="danger" plain>删除</el-button>
                </template>
              </el-popconfirm>
            </div>
          </template>
          <p class="note-id">代码ID：{{ note.noteId }} | | 类型: {{ note.type }}</p>
          <p class="note-id">
            {{ formatTagNames(note.tags) }}
          </p>
          <p class="note-content">{{ note.content }}</p>
          <p class="note-time">🕒 {{ note.updatedAt }}</p>
          <el-button type="primary" size="small" @click="openEditDialog(note)"
            style="float: right; margin-top: 8px;margin-bottom: 8px;">
            编辑/运行
          </el-button>

        </el-card>
      </div>

      <RunHistory v-show="viewMode === 'history'" v-model:visible="dummyDialogVisible" :run-history="runHistory"
        :filter-lang="filterLang" :filtered-NoteIds="filteredNoteIds" :time-range="timeRange" :sort-order="sortOrder"
        @delete="handleDelete" />
    </div>
    <!--  添加代码弹窗 -->
    <el-dialog v-model="dialogVisible" title="新增代码" width="500px">
      <el-form label-position="top">
        <el-row :gutter="20">
          <!-- 标题 -->
          <el-col :span="16">
            <el-form-item label="标题">
              <el-input v-model="newNote.title" />
            </el-form-item>
          </el-col>

          <!-- 类型 -->
          <el-col :span="8">
            <el-form-item label="代码类型">
              <el-select v-model="newNote.type" placeholder="请选择">
                <el-option v-for="lang in langs" :key="lang" :label="lang" :value="lang" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="内容">
          <el-input type="textarea" rows="5" v-model="newNote.content" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addNote">确认添加</el-button>
      </template>
    </el-dialog>

    <!--  编辑代码弹窗 -->
    <el-dialog v-model="editDialogVisible" title="编辑代码" width="80vw" :style="{ maxHeight: '90vh' }" @close="fetchNotes">
      <el-form label-position="top">
        <div style="display: flex; gap: 16px;">

          <el-form-item label="标题" style="flex: 1;">
            <el-input v-model="editNote.title" />
          </el-form-item>

          <el-form-item label="代码类型" style="width: 180px;">
            <el-select v-model="editNote.type" placeholder="请选择代码类型">
              <el-option v-for="lang in langs" :key="lang" :label="lang" :value="lang" />
            </el-select>
          </el-form-item>

        </div>

        <div style="margin-bottom: 16px; display:flex; gap: 16px;">

          <el-form-item label="标签" style="flex:1;">
            <el-select v-model="editNote.tags" @change="handleTagChange" multiple clearable placeholder="选择标签"
              style="width: 100%;">
              <el-option v-for="tag in allTags" :key="tag.id" :label="tag.name" :value="tag.id" />
            </el-select>
          </el-form-item>

          <el-form-item label=" " style="width: 200px;margin-top: 22px;">
            <el-button type="primary" @click="newTagDialogVisible = true">
              添加自定义标签
            </el-button>
          </el-form-item>

          <el-form-item label="字体大小" style="width: 120px;">
            <el-select v-model="userFontSize" @change="onFontSizeChange">
              <el-option label="小 (12px)" :value="12" />
              <el-option label="中 (14px)" :value="14" />
              <el-option label="大 (16px)" :value="16" />
              <el-option label="超大 (18px)" :value="18" />
            </el-select>
          </el-form-item>
        </div>

        <div style="margin-bottom: 20px;">
          <el-form-item label="内容">
            <CodeEditor v-model="editNote.content" :language="editNote.type" :fontSize="userFontSize"
              @save="updateNote" />
          </el-form-item>
        </div>

      </el-form>

      <template #footer>
        <div style="display: flex; flex-direction: column; width: 100%;">
          <!-- 顶部是运行按钮和输入框 -->
          <div style="display: flex; align-items: center; margin-bottom: 10px; gap: 10px;">
            <el-input v-model="editNote.input" type="textarea" :rows="3" placeholder="运行时输入（stdin）" style="flex: 1;"
              clearable />
            <div class="button-column">
              <el-button type="primary" @click="runCode">运行</el-button>
              <el-button @click="showRunHistory" style="margin-left: 0%;">运行记录</el-button>
            </div>
          </div>

          <!-- 底部操作按钮 -->
          <div style="display: flex; justify-content: flex-end; gap: 10px;">
            <el-button @click="editDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="updateNote">保存修改</el-button>
          </div>
        </div>
      </template>

    </el-dialog>

    <!-- 运行结果弹窗 -->
    <el-dialog v-model="resultDialogVisible" title="运行结果" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="状态">{{ runResult.value.status }}</el-descriptions-item>
        <el-descriptions-item label="退出码">{{ runResult.value.exitStatus }}</el-descriptions-item>
        <el-descriptions-item label="执行时间">{{ (runResult.value.time / 1e6).toFixed(2) }} ms</el-descriptions-item>
        <el-descriptions-item label="运行时间">{{ (runResult.value.runTime / 1e6).toFixed(2) }} ms</el-descriptions-item>
        <el-descriptions-item label="内存使用">{{ (runResult.value.memory / 1024 / 1024).toFixed(2) }}
          MB</el-descriptions-item>
        <el-descriptions-item label="运行输出">
          <el-input type="textarea" :value="runResult.value.stdout" readonly rows="4" />
        </el-descriptions-item>
        <el-descriptions-item label="错误输出" v-if="runResult.value.stderr">
          <el-input type="textarea" :value="runResult.value.stderr" readonly rows="3" />
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button type="primary" @click="resultDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 运行记录弹窗 -->
    <el-dialog v-model="historyDialogVisible" title="运行记录" width="80vw">
      <el-collapse v-model="activeCollapse" accordion>
        <el-collapse-item v-for="record in runHistory" :key="record.id" :name="record.id.toString()">
          <template #title>
            <el-button @click.stop="handleDelete(record.id)" type="danger" size="small" icon="Delete" circle
              style="margin-right: 8px" />
            <strong>记录 #{{ record.id }}</strong> - {{ record.curTitle }} | {{ record.curType }} | {{
              formatTime(record.createTime) }}
          </template>

          <el-descriptions :column="2" border size="small" style="margin-bottom: 10px;">
            <el-descriptions-item label="退出码">{{ record.exitStatus }}</el-descriptions-item>
            <el-descriptions-item label="状态">{{ record.status }}</el-descriptions-item>
            <el-descriptions-item label="执行时间">{{ (record.time / 1e6).toFixed(2) }} ms</el-descriptions-item>
            <el-descriptions-item label="运行时长">{{ (record.runTime / 1e6).toFixed(2) }} ms</el-descriptions-item>
            <el-descriptions-item label="内存">{{ (record.memory / 1024 / 1024).toFixed(2) }} MB</el-descriptions-item>
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
    </el-dialog>


    <el-dialog v-model="newTagDialogVisible" title="添加新标签" width="30%">
      <el-form @submit.prevent>
        <el-form-item label="标签名称">
          <el-input v-model="newTagName" placeholder="输入新标签名" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="newTagDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitNewTag">确认</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<style scoped>
.notes-container {
  max-width: 960px;
  margin: 0 auto;
  padding: 20px;
}

.username {
  font-weight: bold;
  margin-left: 8px;
}

.user-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.notes-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}

.note-card {
  min-height: 160px;
}

.note-time {
  margin-top: 12px;
  font-size: 12px;
  color: #999;
  text-align: right;
}

.note-id {
  font-size: 12px;
  text-align: left;
  color: #888;
  margin-bottom: 6px;
}

.note-content {
  white-space: pre-wrap;
  text-align: left;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.button-column {
  display: flex;
  flex-direction: column;
  gap: 10px;
  /* 按钮之间的间距 */
  width: 120px;
  /* 可根据需要调整宽度 */
}

.button-column .el-button {
  width: 140px;
  height: 40px;
}
</style>
