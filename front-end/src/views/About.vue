<template>
  <div class="about-container">
    <AppHeader />
    <div class="about-content">
      <div class="about-card">
        <h2>关于我们</h2>
        <a-divider />

        <div class="section">
          <h3>平台简介</h3>
          <p>
            防诈科普及交流平台是一个致力于防范诈骗、提高公众安全意识的综合性平台。我们通过收集、分析和分享真实案例，帮助更多人了解诈骗手段，提高警惕性，共同构建安全的社会环境。
          </p>
        </div>

        <div class="section">
          <h3>主要功能</h3>
          <a-row :gutter="[16, 16]">
            <a-col :span="8">
              <a-card>
                <template #cover>
                  <FileTextOutlined class="feature-icon" />
                </template>
                <a-card-meta title="文章发布与阅读">
                  <template #description>分享和获取最新的防诈骗知识与经验</template>
                </a-card-meta>
              </a-card>
            </a-col>
            <a-col :span="8">
              <a-card>
                <template #cover>
                  <EnvironmentOutlined class="feature-icon" />
                </template>
                <a-card-meta title="地理信息可视化">
                  <template #description>直观展示各地区诈骗案例分布情况</template>
                </a-card-meta>
              </a-card>
            </a-col>
            <a-col :span="8">
              <a-card>
                <template #cover>
                  <AlertOutlined class="feature-icon" />
                </template>
                <a-card-meta title="快速报警通道">
                  <template #description>提供便捷的报警渠道和应急处置指南</template>
                </a-card-meta>
              </a-card>
            </a-col>
          </a-row>
        </div>

        <div class="section">
          <h3>联系我们</h3>
          <a-descriptions>
            <a-descriptions-item label="邮箱">contact@example.com</a-descriptions-item>
            <a-descriptions-item label="电话">400-XXX-XXXX</a-descriptions-item>
            <a-descriptions-item label="地址">四川省成都市XX区XX街XX号</a-descriptions-item>
          </a-descriptions>
        </div>

        <div class="section">
          <h3>合作伙伴</h3>
          <a-row :gutter="[16, 16]">
            <a-col :span="8">
              <div class="partner-item">四川省公安厅</div>
            </a-col>
            <a-col :span="8">
              <div class="partner-item">成都市反诈中心</div>
            </a-col>
            <a-col :span="8">
              <div class="partner-item">四川省消费者协会</div>
            </a-col>
          </a-row>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { FileTextOutlined, EnvironmentOutlined, AlertOutlined } from '@ant-design/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
</script>

<style lang="less" scoped>
.about-container {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.about-content {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 24px;
}

.about-card {
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

  h2 {
    text-align: center;
    color: #001529;
    font-size: 28px;
    margin-bottom: 0;
  }
}

.section {
  margin-bottom: 32px;

  h3 {
    color: #001529;
    font-size: 20px;
    margin-bottom: 16px;
  }

  p {
    color: #666;
    line-height: 1.8;
    font-size: 16px;
  }
}

.feature-icon {
  font-size: 48px;
  color: #1890ff;
  display: flex;
  justify-content: center;
  padding: 24px 0;
  background: #f0f7ff;
}

:deep(.ant-card) {
  text-align: center;
  transition: transform 0.3s ease;

  &:hover {
    transform: translateY(-5px);
  }

  .ant-card-meta-title {
    color: #001529;
    font-size: 16px;
  }

  .ant-card-meta-description {
    color: #666;
  }
}

.partner-item {
  background: #f0f7ff;
  padding: 16px;
  text-align: center;
  border-radius: 4px;
  color: #1890ff;
  font-weight: 500;
}
</style>
