<template>
  <div class="about-page">
    <el-container>
      <el-main>
        <div class="about-content">
          <h1>关于我们</h1>

          <el-row :gutter="30">
            <el-col :span="12">
              <div class="company-info">
                <h2>公司简介</h2>
                <p>我们是一家专注于现代化Web应用开发的技术公司，致力于为客户提供高质量的数字化解决方案。</p>

                <h3>我们的使命</h3>
                <p>通过创新的技术和优秀的用户体验，帮助企业实现数字化转型，提升业务效率。</p>

                <h3>核心价值观</h3>
                <ul>
                  <li>创新驱动</li>
                  <li>用户至上</li>
                  <li>品质第一</li>
                  <li>团队协作</li>
                </ul>
              </div>
            </el-col>

            <el-col :span="12">
              <div class="team-info">
                <h2>团队介绍</h2>

                <el-card class="team-member" shadow="hover">
                  <div class="member-info">
                    <h4>技术团队</h4>
                    <p>由经验丰富的前端和后端工程师组成，精通Vue.js、Node.js等现代技术栈。</p>
                  </div>
                </el-card>

                <el-card class="team-member" shadow="hover">
                  <div class="member-info">
                    <h4>设计团队</h4>
                    <p>专业的UI/UX设计师，专注于创造直观、美观的用户界面和用户体验。</p>
                  </div>
                </el-card>

                <el-card class="team-member" shadow="hover">
                  <div class="member-info">
                    <h4>产品团队</h4>
                    <p>深入理解用户需求，负责产品规划和项目管理，确保交付高质量的产品。</p>
                  </div>
                </el-card>
              </div>
            </el-col>
          </el-row>

          <div class="contact-section">
            <h2>联系我们</h2>
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="contact-item">
                  <i class="el-icon-phone"></i>
                  <p>电话: +86 138-0000-0000</p>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="contact-item">
                  <i class="el-icon-message"></i>
                  <p>邮箱: contact@company.com</p>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="contact-item">
                  <i class="el-icon-location"></i>
                  <p>地址: 北京市朝阳区科技园</p>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'AboutPage',
  data() {
    return {
      // 组件数据
    }
  }
}
</script>

<style scoped>
.about-page {
  min-height: calc(100vh - 60px);
  background: #f5f7fa;
  padding: 40px 20px;
}

.about-content {
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.about-content h1 {
  text-align: center;
  color: #303133;
  margin-bottom: 40px;
  font-size: 2.5rem;
}

.about-content h2 {
  color: #409EFF;
  margin-bottom: 20px;
  border-bottom: 2px solid #409EFF;
  padding-bottom: 10px;
}

.about-content h3 {
  color: #606266;
  margin: 20px 0 10px 0;
}

.company-info p {
  line-height: 1.8;
  color: #606266;
  margin-bottom: 15px;
}

.company-info ul {
  color: #606266;
  line-height: 1.8;
}

.company-info li {
  margin-bottom: 8px;
}

.team-member {
  margin-bottom: 15px;
  transition: transform 0.3s ease;
}

.team-member:hover {
  transform: translateY(-2px);
}

.member-info h4 {
  color: #409EFF;
  margin-bottom: 10px;
}

.member-info p {
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

.contact-section {
  margin-top: 40px;
  text-align: center;
}

.contact-item {
  text-align: center;
  padding: 20px;
}

.contact-item i {
  font-size: 2rem;
  color: #409EFF;
  margin-bottom: 10px;
  display: block;
}

.contact-item p {
  color: #606266;
  margin: 0;
  font-size: 1.1rem;
}
</style>
