<template>
  <div class="post-container">
    <AppHeader />
    <div class="post-content">
      <div class="page-header">
        <h2>发表文章</h2>
      </div>

      <a-form
        :model="formState"
        name="post"
        autocomplete="off"
        @finish="onFinish"
        class="post-form"
        :rules="rules"
      >
        <a-form-item name="title">
          <a-input v-model:value="formState.title" placeholder="请输入文章标题">
            <template #prefix>
              <FileTextOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item name="location">
          <div class="location-wrapper">
            <a-input
              v-model:value="formState.location"
              placeholder="点击地图选择位置或输入地址"
              readonly
            >
              <template #prefix>
                <EnvironmentOutlined />
              </template>
            </a-input>
            <a-button type="primary" @click="showMapModal">
              <EnvironmentOutlined /> 选择位置
            </a-button>
          </div>
        </a-form-item>

        <a-form-item name="content">
          <a-textarea
            v-model:value="formState.content"
            placeholder="请输入文章内容"
            :rows="12"
            :auto-size="{ minRows: 12, maxRows: 20 }"
          />
        </a-form-item>

        <div class="form-actions">
          <a-button @click="$router.push('/')">取消</a-button>
          <a-button type="primary" html-type="submit" :loading="loading">发布</a-button>
        </div>
      </a-form>
    </div>

    <a-modal v-model:open="mapVisible" title="选择位置" width="800px" :footer="null" destroyOnClose>
      <div class="map-container">
        <div class="map-search">
          <a-input-search
            v-model:value="searchAddress"
            placeholder="搜索地址"
            @search="onSearch"
            :loading="searching"
            allowClear
          />
        </div>
        <div class="map-wrapper" id="container"></div>
        <div class="map-actions">
          <a-button @click="getCurrentLocation"> <AimOutlined /> 定位当前位置 </a-button>
          <a-button type="primary" @click="confirmLocation" :disabled="!marker">
            确认选择
          </a-button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { FileTextOutlined, EnvironmentOutlined, AimOutlined } from '@ant-design/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import { createPost } from '@/api/post'

const AMAP_KEY = 'b6d79262c73c4413d9e82736c346f2a2'
const AMAP_API_URL =
  'https://webapi.amap.com/maps?v=2.0&key=' +
  AMAP_KEY +
  '&plugin=AMap.PlaceSearch,AMap.Geocoder,AMap.Geolocation,AMap.Scale,AMap.ToolBar'
const AMAP_SECURITY_CONFIG = {
  securityJsCode: '5077eda0e025be5a95ffaea82fff34cf',
}

// 配置安全密钥
window._AMapSecurityConfig = AMAP_SECURITY_CONFIG

const router = useRouter()
const loading = ref(false)
const mapVisible = ref(false)
const searching = ref(false)
const searchAddress = ref('')
const mapCenter = ref([116.397428, 39.90923]) // 默认北京
const marker = ref(null)
let map = null
let amapMarker = null

const formState = reactive({
  title: '',
  content: '',
  location: '',
  longitude: null,
  latitude: null,
})

const rules = {
  title: [
    { required: true, message: '请输入文章标题' },
    { max: 100, message: '标题最多100个字符' },
  ],
  content: [{ required: true, message: '请输入文章内容' }],
  location: [{ required: true, message: '请选择位置' }],
}

// 加载高德地图脚本
const loadAMapScript = () => {
  return new Promise((resolve, reject) => {
    if (window.AMap) {
      resolve(window.AMap)
      return
    }

    const script = document.createElement('script')
    script.type = 'text/javascript'
    script.src = AMAP_API_URL
    script.onerror = reject
    script.onload = () => {
      resolve(window.AMap)
    }
    document.head.appendChild(script)
  })
}

// 初始化地图
const initMap = async () => {
  try {
    await loadAMapScript()
    map = new AMap.Map('container', {
      zoom: 13,
      center: mapCenter.value,
      geolocation: false, // 禁用默认的定位控件
    })

    // 添加地图控件
    map.plugin(['AMap.Scale', 'AMap.ToolBar'], () => {
      map.addControl(new AMap.Scale())
      map.addControl(new AMap.ToolBar())
    })

    // 点击地图事件
    map.on('click', onMapClick)

    // 如果有标记位置，添加标记
    if (marker.value) {
      addMarker(marker.value)
    }
  } catch (error) {
    console.error('地图加载失败:', error)
    message.error('地图加载失败')
  }
}

const addMarker = (position) => {
  if (amapMarker) {
    amapMarker.setPosition(position)
  } else {
    amapMarker = new AMap.Marker({
      position,
      map,
    })
  }
}

const showMapModal = () => {
  mapVisible.value = true
  // 等待 DOM 更新后初始化地图
  setTimeout(async () => {
    await initMap()
    // 如果没有选择过位置，自动定位到当前城市
    if (!marker.value) {
      getCurrentLocation()
    }
  }, 0)
}

const getCurrentLocation = async () => {
  if (!map) return

  try {
    // 创建定位对象
    const geolocation = new AMap.Geolocation({
      timeout: 10000,
      convert: true, // 自动偏移坐标，偏移后的坐标为高德坐标
    })

    map.addControl(geolocation)

    // 直接使用 IP 定位
    const result = await new Promise((resolve, reject) => {
      geolocation.getCityInfo((status, result) => {
        if (status === 'complete') {
          resolve(result)
        } else {
          reject(result)
        }
      })
    })

    console.log('IP定位结果:', result)

    // 使用 position 数组中的经纬度
    if (result.position) {
      const [lng, lat] = result.position
      updateLocation(lng, lat, result.city)
    } else {
      // 如果没有 position，使用 center
      const [lng, lat] = result.center.split(',').map(Number)
      updateLocation(lng, lat, result.city)
    }
  } catch (error) {
    console.error('IP定位失败:', error)
    message.error('获取当前位置失败')
  }
}

// 更新位置信息的辅助函数
const updateLocation = (lng, lat, address) => {
  mapCenter.value = [lng, lat]
  marker.value = [lng, lat]
  formState.longitude = lng
  formState.latitude = lat
  formState.location = address

  map.setCenter([lng, lat])
  addMarker([lng, lat])
}

const onMapClick = (e) => {
  const { lng, lat } = e.lnglat
  marker.value = [lng, lat]
  formState.longitude = lng
  formState.latitude = lat

  addMarker([lng, lat])

  // 使用高德地图逆地理编码服务
  const geocoder = new AMap.Geocoder()
  geocoder.getAddress([lng, lat], (status, result) => {
    if (status === 'complete' && result.info === 'OK') {
      formState.location = result.regeocode.formattedAddress
    } else {
      message.error('获取地址信息失败')
    }
  })
}

const onSearch = async (value) => {
  if (!value || !map) return

  searching.value = true
  try {
    const placeSearch = new AMap.PlaceSearch({
      city: '全国',
    })

    const result = await new Promise((resolve, reject) => {
      placeSearch.search(value, (status, result) => {
        if (status === 'complete' && result.info === 'OK') {
          resolve(result)
        } else {
          reject(new Error('搜索失败'))
        }
      })
    })

    if (result.poiList.pois.length > 0) {
      const poi = result.poiList.pois[0]
      const position = [poi.location.lng, poi.location.lat]

      mapCenter.value = position
      marker.value = position
      formState.longitude = poi.location.lng
      formState.latitude = poi.location.lat
      formState.location = poi.address || poi.name

      map.setCenter(position)
      addMarker(position)
    } else {
      message.warning('未找到相关地址')
    }
  } catch (error) {
    message.error('地址搜索失败')
  } finally {
    searching.value = false
  }
}

const confirmLocation = () => {
  mapVisible.value = false
}

// 监听弹窗关闭事件，销毁地图实例
const handleMapModalClose = () => {
  if (map) {
    map.destroy()
    map = null
  }
}

const onFinish = async (values) => {
  try {
    loading.value = true
    
    // 获取当前登录用户ID
    const userInfo = JSON.parse(localStorage.getItem('userInfo'))
    if (!userInfo || !userInfo.userId) {
      message.error('请先登录')
      router.push('/login')
      return
    }
    
    const postData = {
      userId: userInfo.userId,
      title: formState.title,
      content: formState.content,
      location: formState.location,
      longitude: formState.longitude,
      latitude: formState.latitude
    }
    
    const res = await createPost(postData)
    
    if (res.code === 200) {
      message.success('文章发布成功')
      router.push('/read')
    } else {
      message.error(res.message || '发布失败')
    }
  } catch (error) {
    console.error('发布失败:', error)
    message.error('发布失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

// 组件卸载时清理地图实例
onBeforeUnmount(() => {
  if (map) {
    map.destroy()
    map = null
  }
})
</script>

<style lang="less" scoped>
.post-container {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.post-content {
  max-width: 800px;
  margin: 40px auto;
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.page-header {
  margin-bottom: 32px;
  text-align: center;

  h2 {
    font-size: 24px;
    color: #001529;
    margin: 0;
  }
}

.location-wrapper {
  display: flex;
  gap: 16px;

  .ant-input-affix-wrapper {
    flex: 1;
  }
}

.map-container {
  .map-search {
    margin-bottom: 16px;
  }

  .map-wrapper {
    height: 400px;
    margin-bottom: 16px;
    border-radius: 8px;
    overflow: hidden;
  }

  .map-actions {
    display: flex;
    justify-content: space-between;
  }
}

.post-form {
  :deep(.ant-input-prefix) {
    color: rgba(0, 0, 0, 0.45);
  }

  :deep(.ant-input) {
    font-size: 14px;
  }

  :deep(.ant-input-affix-wrapper) {
    padding: 8px 11px;
  }
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 24px;

  .ant-btn {
    min-width: 100px;
  }
}
</style>
