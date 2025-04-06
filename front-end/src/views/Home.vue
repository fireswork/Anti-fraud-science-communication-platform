<template>
  <div class="home-container">
    <AppHeader />

    <div class="content">
      <div class="center-content">
        <div class="menu-section">
          <div class="menu-grid">
            <div class="menu-item" @click="handleNavigation('post')">
              <FileTextOutlined class="icon" />
              <div class="text">发表文章</div>
            </div>
            <div class="menu-item" @click="handleNavigation('read')">
              <ReadOutlined class="icon" />
              <div class="text">阅读文章</div>
            </div>
            <div class="menu-item" @click="handleNavigation('center')">
              <ExperimentOutlined class="icon" />
              <div class="text">科普中心</div>
            </div>
            <div class="menu-item" @click="handleNavigation('about')">
              <InfoCircleOutlined class="icon" />
              <div class="text">关于本站</div>
            </div>
          </div>

          <div class="report-item" @click="handleNavigation('report')">
            <AlertOutlined class="icon" />
            <div class="text">快速报警</div>
          </div>
        </div>

        <div class="map-section" @click="handleNavigation('map')">
          <div class="map-title">文章地图</div>
          <div class="map-content">
            <EnvironmentOutlined class="map-icon" />
            <div class="map-hint">点击查看文章分布</div>
          </div>
        </div>
      </div>
    </div>

    <div class="footer">
      <div class="footer-item">友情链接：</div>
      <div class="footer-item">版权：</div>
      <div class="footer-item">备案号：</div>
    </div>
  </div>
</template>

<script setup>
import {
  FileTextOutlined,
  ReadOutlined,
  ExperimentOutlined,
  InfoCircleOutlined,
  AlertOutlined,
  EnvironmentOutlined,
} from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/AppHeader.vue'

const router = useRouter()

const handleNavigation = (path) => {
  router.push(`/${path}`)
}
</script>

<style lang="less" scoped>
.home-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #000;
  color: #fff;
}

.content {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 24px;

  .center-content {
    display: flex;
    gap: 24px;
    width: 1200px;
    max-width: 90%;

    .menu-section {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 16px;

      .menu-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 16px;

        .menu-item {
          background: #fff;
          border-radius: 8px;
          height: 120px;
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;
          cursor: pointer;
          transition: transform 0.3s ease;

          &:hover {
            transform: translateY(-5px);
          }

          .icon {
            font-size: 32px;
            margin-bottom: 8px;
            color: #000;
          }

          .text {
            font-size: 18px;
            font-weight: bold;
            color: #000;
          }
        }
      }

      .report-item {
        background: #fff;
        border-radius: 8px;
        height: 80px;
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 12px;
        cursor: pointer;
        transition: transform 0.3s ease;

        &:hover {
          transform: translateY(-5px);
        }

        .icon {
          font-size: 24px;
          color: #ff4d4f;
        }

        .text {
          font-size: 18px;
          font-weight: bold;
          color: #000;
        }
      }
    }

    .map-section {
      flex: 1;
      background: #1890ff;
      border-radius: 8px;
      overflow: hidden;
      display: flex;
      flex-direction: column;

      .map-title {
        padding: 16px;
        font-size: 16px;
        font-weight: bold;
        background: rgba(255, 255, 255, 0.1);
        border-bottom: 1px solid rgba(255, 255, 255, 0.2);
      }

      .map-content {
        flex: 1;
        display: flex;
        justify-content: center;
        align-items: center;
        background: rgba(255, 255, 255, 0.05);

        .map-icon {
          font-size: 64px;
          color: #fff;
        }
      }
    }
  }
}

.footer {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 16px;
  background-color: rgba(255, 255, 255, 0.1);
  height: 56px;

  .footer-item {
    color: rgba(255, 255, 255, 0.8);
  }
}
</style>
