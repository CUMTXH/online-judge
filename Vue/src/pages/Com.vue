<!-- src/pages/note.vue -->
<script setup >
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '~/composables/useAuth'
import { ElMessage } from 'element-plus'
import { User, Plus } from '@element-plus/icons-vue'
import jwtDecode from 'jwt-decode'
import axios from '~/utils/request'

const router = useRouter()
const { logout } = useAuth()
const username = ref('未知用户')
const userid = ref('0')
const dialogVisible = ref(false) //  控制添加代码弹窗
const langs = ['C', 'C++', 'Java', 'Python', 'Javascript', 'Rust', 'Html']

const timeRange = ref([])
const sortOrder = ref('desc')
const viewMode = ref('allsolution')
const filterLang = ref('全部')// 当前筛选语言，默认为“全部”
const filterTags = ref([])
const allTags = ref([])
const newTagDialogVisible = ref(false)
const editSolve = ref({
  noteId: null,
  title: '',
  content: 'default',
  type: '',
})

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
const submitNewTag = async () => {
  const name = newTagName.value.trim()
  if (!name) return
  try {
    const res = await axios.post("/api/tags", { name })
    const addedTag = res.data // 包含 id 和 name

    await fetchTags()
    // 选中新标签

    if (!editSolve.value.tags.includes(addedTag.id)) {
      editSolve.value.tags.push(addedTag.id)
    }
    console.log(editSolve.value.tags, addedTag)
    ElMessage.success("标签添加成功")
    newTagDialogVisible.value = false
    newTagName.value = ""
  } catch (e) {
    ElMessage.error("添加失败，可能是标签已存在")
  }
}

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

const handleLogout = () => {
  logout()
  ElMessage.success('已退出登录')
  router.push('/')
}

onMounted(() => {
  parseToken()
  // fetchNotes()
  fetchTags()
  // fetchRunHistory()
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
          <el-button type="primary" @click="viewMode = 'allsolution'; fetchSolotion()"
            :plain="viewMode !== 'allsolution'">全部</el-button>
          <el-button type="primary" @click="viewMode = 'mine'" :plain="viewMode !== 'mine'">我的</el-button>
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
    </div>


    <!--  编辑弹窗 -->
    <el-dialog v-model="dialogVisible" title="添加题解" width="80vw" :style="{ maxHeight: '90vh' }" @close="">
      <el-form label-position="top">
        <div style="display: flex; gap: 16px;">

          <el-form-item label="标题" style="flex: 1;">
            <el-input v-model="editSolve.title" />
          </el-form-item>

          <el-form-item label="代码类型" style="width: 180px;">
            <el-select v-model="editSolve.type" placeholder="请选择代码类型">
              <el-option v-for="lang in langs" :key="lang" :label="lang" :value="lang" />
            </el-select>
          </el-form-item>

        </div>

        <div style="margin-bottom: 16px; display:flex; gap: 16px;">

          <el-form-item label="标签" style="flex:1;">
            <el-select v-model="editSolve.tags" @change="handleTagChange" multiple clearable placeholder="选择标签"
              style="width: 100%;">
              <el-option v-for="tag in allTags" :key="tag.id" :label="tag.name" :value="tag.id" />
            </el-select>
          </el-form-item>

          <el-form-item label=" " style="width: 200px;line-height: 30px;">
            <el-button type="primary" @click="newTagDialogVisible = true">
              添加自定义标签
            </el-button>
          </el-form-item>

        </div>

        <div style="margin-bottom: 20px;">
          <el-form-item label="内容">
            <!-- <MdEditor mode="edit" height="500px" v-model="editSolve.content"/> -->
          </el-form-item>
        </div>

      </el-form>

      <template #footer>
        <div style="display: flex; flex-direction: column; width: 100%;">

          <!-- 底部操作按钮 -->
          <div style="display: flex; justify-content: flex-end; gap: 10px;">
            <el-button @click="editDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="updateNote">保存修改</el-button>
          </div>
        </div>
      </template>

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
  width: 120px;
}

.button-column .el-button {
  width: 140px;
  height: 40px;
}
</style>
