<template>
  <div class="home-container">
    <el-container style="height: 100vh;">
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <h2>管理系统</h2>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              <i class="el-icon-user"></i>
              {{ currentUser }}
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="changePassword">修改密码</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>

      <el-container>
        <!-- 左侧导航栏 -->
        <el-aside width="250px" class="sidebar">
          <el-menu
            :default-active="activeMenu"
            class="sidebar-menu"
            background-color="#304156"
            text-color="#bfcbd9"
            active-text-color="#ffffff"
            :unique-opened="true"
            router>

            <!-- 首页 -->
            <el-menu-item index="/home/dashboard">
              <i class="el-icon-s-home"></i>
              <span>首页</span>
            </el-menu-item>

            <!-- 系统管理 -->
            <el-submenu index="system">
              <template slot="title">
                <i class="el-icon-setting"></i>
                <span>系统管理</span>
              </template>
              <el-menu-item index="/home/user-management">
                <i class="el-icon-user"></i>
                <span>用户管理</span>
              </el-menu-item>
            </el-submenu>

            <!-- 关于 -->
            <el-menu-item index="/home/about">
              <i class="el-icon-info"></i>
              <span>关于系统</span>
            </el-menu-item>

          </el-menu>
        </el-aside>

        <!-- 主内容区域 -->
        <el-main class="main-content">
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { removeToken } from '@/utils/auth'

export default {
  name: 'HomePage',
  data() {
    return {
      currentUser: '管理员',
      activeMenu: '/home/user-management'
    }
  },
  methods: {
    handleCommand(command) {
      switch (command) {
        case 'profile':
          this.$message.info('个人信息功能待开发')
          break
        case 'changePassword':
          this.$router.push('/home/change-password')
          break
        case 'logout':
          this.logout()
          break
      }
    },

    logout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 清除token
        removeToken()
        this.$message.success('已退出登录')
        // 跳转到登录页
        this.$router.push('/login')
      }).catch(() => {
        // 取消退出
      })
    }
  },

  mounted() {
    // 设置默认激活的菜单项
    this.activeMenu = this.$route.path
  },

  watch: {
    '$route'(to) {
      this.activeMenu = to.path
    }
  }
}
</script>

<style scoped>
.home-container {
  height: 100vh;
  overflow: hidden;
}

/* 顶部导航栏样式 */
.header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}

.header-left h2 {
  margin: 0;
  color: #2c3e50;
  font-size: 20px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  cursor: pointer;
  color: #606266;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.el-dropdown-link:hover {
  color: #409EFF;
}

/* 左侧导航栏样式 */
.sidebar {
  background-color: #304156;
  overflow-x: hidden;
}

.sidebar-menu {
  border: none;
  height: 100%;
}

.sidebar-menu .el-submenu__title {
  height: 50px;
  line-height: 50px;
  font-size: 14px;
}

.sidebar-menu .el-menu-item {
  height: 45px;
  line-height: 45px;
  font-size: 14px;
  padding-left: 60px !important;
}

.sidebar-menu .el-menu-item:hover {
  background-color: #263445 !important;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: #337ab7 !important;
  color: #ffffff !important;
}

.sidebar-menu .el-submenu .el-menu-item.is-active {
  background-color: #337ab7 !important;
  color: #ffffff !important;
}

/* 主内容区域样式 */
.main-content {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 200px !important;
  }

  .header-left h2 {
    font-size: 18px;
  }

  .main-content {
    padding: 15px;
  }
}
</style>
