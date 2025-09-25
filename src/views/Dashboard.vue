<template>
  <div class="dashboard">
    <div class="page-header">
      <h2>系统首页</h2>
      <p>欢迎使用管理系统</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon">
              <i class="el-icon-user" style="color: #409EFF;"></i>
            </div>
            <div class="stats-info">
              <h3>{{ userCount }}</h3>
              <p>用户总数</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon">
              <i class="el-icon-s-data" style="color: #67C23A;"></i>
            </div>
            <div class="stats-info">
              <h3>{{ todayVisits }}</h3>
              <p>今日访问</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon">
              <i class="el-icon-s-order" style="color: #E6A23C;"></i>
            </div>
            <div class="stats-info">
              <h3>{{ totalOrders }}</h3>
              <p>订单总数</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon">
              <i class="el-icon-money" style="color: #F56C6C;"></i>
            </div>
            <div class="stats-info">
              <h3>¥{{ revenue }}</h3>
              <p>总收入</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-row :gutter="20" class="quick-actions">
      <el-col :span="12">
        <el-card>
          <div slot="header" class="card-header">
            <span>快捷操作</span>
          </div>
          <div class="quick-buttons">
            <el-button type="primary" icon="el-icon-plus" @click="goToUserManagement">
              新增用户
            </el-button>
            <el-button type="success" icon="el-icon-view" @click="goToUserManagement">
              查看用户
            </el-button>
            <el-button type="info" icon="el-icon-setting">
              系统设置
            </el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <div slot="header" class="card-header">
            <span>系统信息</span>
          </div>
          <div class="system-info">
            <p><strong>系统版本：</strong>v1.0.0</p>
            <p><strong>运行时间：</strong>{{ uptime }}</p>
            <p><strong>服务器状态：</strong><el-tag type="success" size="small">正常</el-tag></p>
            <p><strong>最后更新：</strong>{{ lastUpdate }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'SystemDashboard',
  data() {
    return {
      userCount: 156,
      todayVisits: 1234,
      totalOrders: 89,
      revenue: '12,345',
      uptime: '7天 12小时',
      lastUpdate: '2024-01-15 14:30:00'
    }
  },
  methods: {
    goToUserManagement() {
      this.$router.push('/home/user-management')
    }
  }
}
</script>

<style scoped>
.dashboard {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.page-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8e8e8;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #262626;
  font-size: 20px;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  color: #8c8c8c;
  font-size: 14px;
}

.stats-row {
  margin-bottom: 24px;
}

.stats-card {
  height: 120px;
}

.stats-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stats-icon {
  font-size: 40px;
  margin-right: 16px;
}

.stats-info h3 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #262626;
}

.stats-info p {
  margin: 0;
  color: #8c8c8c;
  font-size: 14px;
}

.quick-actions {
  margin-top: 24px;
}

.card-header {
  font-weight: 600;
  color: #262626;
}

.quick-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.system-info p {
  margin: 8px 0;
  color: #595959;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-row .el-col {
    margin-bottom: 16px;
  }

  .quick-buttons {
    flex-direction: column;
  }

  .quick-buttons .el-button {
    width: 100%;
  }
}
</style>
