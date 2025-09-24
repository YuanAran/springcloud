<template>
  <div class="home-page">
    <el-container>
      <el-main>
        <div class="welcome-section">
          <h1>欢迎来到首页</h1>
          <p>这是一个使用 Vue.js 和 Element UI 构建的现代化网站</p>

          <el-row :gutter="20" class="feature-cards">
            <el-col :span="8">
              <el-card shadow="hover" class="feature-card">
                <div slot="header" class="card-header">
                  <span>功能特性</span>
                </div>
                <div class="card-content">
                  <i class="el-icon-star-on"></i>
                  <p>现代化的用户界面设计</p>
                </div>
              </el-card>
            </el-col>

            <el-col :span="8">
              <el-card shadow="hover" class="feature-card">
                <div slot="header" class="card-header">
                  <span>响应式布局</span>
                </div>
                <div class="card-content">
                  <i class="el-icon-mobile-phone"></i>
                  <p>适配各种设备屏幕尺寸</p>
                </div>
              </el-card>
            </el-col>

            <el-col :span="8">
              <el-card shadow="hover" class="feature-card">
                <div slot="header" class="card-header">
                  <span>快速开发</span>
                </div>
                <div class="card-content">
                  <i class="el-icon-lightning"></i>
                  <p>基于 Vue.js 的高效开发体验</p>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'HomePage',
  data() {
    return {
      // 组件数据
    }
  }
}
</script>

<style scoped>
.home-page {
  min-height: calc(100vh - 60px);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.welcome-section {
  text-align: center;
  color: white;
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-section h1 {
  font-size: 3rem;
  margin-bottom: 20px;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
}

.welcome-section p {
  font-size: 1.2rem;
  margin-bottom: 40px;
  opacity: 0.9;
}

.feature-cards {
  margin-top: 60px;
}

.feature-card {
  height: 200px;
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.card-header {
  font-weight: bold;
  color: #409EFF;
}

.card-content {
  text-align: center;
  padding: 20px 0;
}

.card-content i {
  font-size: 2rem;
  color: #409EFF;
  margin-bottom: 15px;
  display: block;
}

.card-content p {
  color: #606266;
  margin: 0;
}
</style>
