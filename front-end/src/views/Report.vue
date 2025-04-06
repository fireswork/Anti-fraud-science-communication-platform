<template>
  <div class="report-container">
    <AppHeader />
    <div class="report-content">
      <div class="emergency-section">
        <h2>紧急报警</h2>
        <a-row :gutter="[16, 16]" class="emergency-numbers">
          <a-col :span="8">
            <a-card>
              <PhoneOutlined class="phone-icon" />
              <h3>110</h3>
              <p>公安报警电话</p>
            </a-card>
          </a-col>
          <a-col :span="8">
            <a-card>
              <PhoneOutlined class="phone-icon" />
              <h3>96110</h3>
              <p>反诈中心热线</p>
            </a-card>
          </a-col>
          <a-col :span="8">
            <a-card>
              <PhoneOutlined class="phone-icon" />
              <h3>12345</h3>
              <p>政府服务热线</p>
            </a-card>
          </a-col>
        </a-row>
      </div>

      <div class="guide-section">
        <h2>报警指南</h2>
        <a-steps direction="vertical" :current="-1">
          <a-step
            title="保持冷静"
            description="遇到诈骗时首先要保持冷静，不要轻易相信对方的话语或威胁。"
          >
            <template #icon><MehOutlined /></template>
          </a-step>
          <a-step
            title="保存证据"
            description="及时截图保存聊天记录、转账记录等证据，不要删除任何相关信息。"
          >
            <template #icon><SaveOutlined /></template>
          </a-step>
          <a-step title="立即报警" description="拨打110或96110报警，或到就近派出所报案。">
            <template #icon><PhoneOutlined /></template>
          </a-step>
          <a-step title="冻结账户" description="如果涉及资金损失，立即联系银行冻结账户。">
            <template #icon><BankOutlined /></template>
          </a-step>
        </a-steps>
      </div>

      <div class="report-form-section">
        <h2>在线报警</h2>
        <a-form :model="formState" layout="vertical">
          <a-form-item
            label="事件类型"
            name="type"
            :rules="[{ required: true, message: '请选择事件类型' }]"
          >
            <a-select v-model:value="formState.type" placeholder="请选择事件类型">
              <a-select-option value="1">电信诈骗</a-select-option>
              <a-select-option value="2">网络诈骗</a-select-option>
              <a-select-option value="3">信用卡诈骗</a-select-option>
              <a-select-option value="4">其他诈骗</a-select-option>
            </a-select>
          </a-form-item>

          <a-form-item
            label="发生时间"
            name="time"
            :rules="[{ required: true, message: '请选择发生时间' }]"
          >
            <a-date-picker v-model:value="formState.time" show-time style="width: 100%" />
          </a-form-item>

          <a-form-item label="涉案金额" name="amount">
            <a-input-number
              v-model:value="formState.amount"
              style="width: 100%"
              placeholder="请输入涉案金额（元）"
            />
          </a-form-item>

          <a-form-item
            label="事件描述"
            name="description"
            :rules="[{ required: true, message: '请描述事件经过' }]"
          >
            <a-textarea
              v-model:value="formState.description"
              :rows="4"
              placeholder="请详细描述事件经过"
            />
          </a-form-item>

          <a-form-item
            label="联系方式"
            name="contact"
            :rules="[{ required: true, message: '请填写联系方式' }]"
          >
            <a-input v-model:value="formState.contact" placeholder="请填写您的联系电话" />
          </a-form-item>

          <a-form-item>
            <a-button type="primary" block @click="handleSubmit">提交报警信息</a-button>
          </a-form-item>
        </a-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { PhoneOutlined, MehOutlined, SaveOutlined, BankOutlined } from '@ant-design/icons-vue'
import AppHeader from '@/components/AppHeader.vue'

const formState = ref({
  type: undefined,
  time: null,
  amount: null,
  description: '',
  contact: '',
})

const handleSubmit = () => {
  // 这里添加表单提交逻辑
  message.success('报警信息已提交，我们会尽快处理')
}
</script>

<style lang="less" scoped>
.report-container {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.report-content {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 24px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.emergency-section {
  grid-column: 1 / -1;
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

  h2 {
    text-align: center;
    color: #001529;
    margin-bottom: 24px;
  }
}

.emergency-numbers {
  .ant-card {
    text-align: center;
    padding: 16px;
    transition: transform 0.3s ease;

    &:hover {
      transform: translateY(-5px);
    }

    .phone-icon {
      font-size: 32px;
      color: #1890ff;
      margin-bottom: 8px;
    }

    h3 {
      font-size: 24px;
      color: #001529;
      margin: 8px 0;
    }

    p {
      color: #666;
      margin: 0;
    }
  }
}

.guide-section {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

  h2 {
    color: #001529;
    margin-bottom: 24px;
  }

  :deep(.ant-steps-item-description) {
    color: #666;
  }
}

.report-form-section {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

  h2 {
    color: #001529;
    margin-bottom: 24px;
  }
}

@media (max-width: 768px) {
  .report-content {
    grid-template-columns: 1fr;
  }
}
</style>
