<template>
  <div class="doctor-home">
    <h1 class="page-title">医生工作台</h1>
    
    <!-- 数据概览 - 使用响应式数据 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon" style="background-color: rgba(42, 92, 138, 0.1);">
          <i class="iconfont icon-calendar"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ todayAppointments }}</div>
          <div class="stat-label">今日预约</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background-color: rgba(16, 185, 129, 0.1);">
          <i class="iconfont icon-user"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ weeklyPatients }}</div>
          <div class="stat-label">本周患者</div>
        </div>
      </div>
    </div>
    
    <!-- 预约概览和状态分布 -->
    <div class="dashboard-grid">
      <div class="dashboard-card">
        <div class="card-header">
          <h2 class="card-title">预约状态分布</h2>
          <div class="card-actions">
            <button class="card-action-btn" @click="refreshDashboard" :disabled="loading">
              <i class="iconfont icon-refresh" :class="{ 'rotating': loading }"></i>
              <span>刷新</span>
            </button>
            <select v-model="appointmentTimeRange" class="time-range-select" @change="handleTimeRangeChange">
              <option value="week">本周</option>
              <option value="month">本月</option>
              <option value="year">本年</option>
            </select>
          </div>
        </div>
        <div class="chart-container">
          <div v-if="loading" class="loading-state">
            <i class="el-icon-loading"></i>
            <p>加载中...</p>
          </div>
          <div v-else class="appointment-status-chart">
            <div class="status-item">
              <div class="status-bar">
                <div class="status-progress" 
                     :style="{ width: getProgressWidth(appointmentStatus.pending), backgroundColor: '#F97316' }"></div>
              </div>
              <div class="status-label">
                <span class="status-name">待审核</span>
                <span class="status-value">{{ appointmentStatus.pending }}</span>
              </div>
            </div>
            <div class="status-item">
              <div class="status-bar">
                <div class="status-progress" 
                     :style="{ width: getProgressWidth(appointmentStatus.approved), backgroundColor: '#10B981' }"></div>
              </div>
              <div class="status-label">
                <span class="status-name">审核通过</span>
                <span class="status-value">{{ appointmentStatus.approved }}</span>
              </div>
            </div>
            <div class="status-item">
              <div class="status-bar">
                <div class="status-progress" 
                     :style="{ width: getProgressWidth(appointmentStatus.completed), backgroundColor: '#6366F1' }"></div>
              </div>
              <div class="status-label">
                <span class="status-name">已完成</span>
                <span class="status-value">{{ appointmentStatus.completed }}</span>
              </div>
            </div>
            <div class="status-total">
              <span>总计: {{ getTotalAppointments() }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="dashboard-card">
        <div class="card-header">
          <h2 class="card-title">今日预约</h2>
          <div class="card-actions">
            <button class="card-action-btn primary" @click="viewAllAppointments">
              <span>查看全部</span>
            </button>
          </div>
        </div>
        <div class="appointment-list">
          <div v-if="loading" class="loading-state">
            <i class="el-icon-loading"></i>
            <p>加载中...</p>
          </div>
          <div v-else-if="recentAppointments.length === 0" class="empty-state">
            <i class="iconfont icon-calendar empty-icon"></i>
            <p>今日暂无预约</p>
          </div>
          <div v-else v-for="(appointment, index) in recentAppointments" :key="index" class="appointment-item">
            <div class="appointment-time">
              <div class="time">{{ appointment.time }}</div>
              <div class="duration">{{ appointment.duration || 45 }}分钟</div>
              <el-tag 
                :type="getStatusType(appointment.status)" 
                size="small" 
                effect="light"
                class="ml-auto"
              >
                {{ appointment.status || '待审核' }}
              </el-tag>
            </div>
            <div class="appointment-content">
              <div class="patient-info">
                <div class="patient-avatar">
                  <img v-if="appointment.patientAvatar" :src="appointment.patientAvatar" alt="患者头像">
                  <div v-else class="default-avatar">{{ getInitials(appointment.patientName) }}</div>
                </div>
                <div class="patient-details">
                  <div class="patient-name">{{ appointment.patientName }}</div>
                </div>
              </div>
              <div class="appointment-actions">
                <button 
                  v-if="appointment.status === '审核通过'" 
                  class="appointment-action-btn" 
                  @click="handleAppointment(appointment)"
                >
                  <i class="iconfont icon-check"></i>
                  <span>处理预约</span>
                </button>
                <button 
                  v-else-if="appointment.status === '待审核'" 
                  class="appointment-action-btn review" 
                  @click="reviewAppointment(appointment)"
                >
                  <i class="iconfont icon-edit"></i>
                  <span>审核预约</span>
                </button>
                <div v-else class="appointment-status">
                  {{ appointment.status === '已完成' ? '已完成' : '已拒绝' }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 宣传文章数据 -->
    <div class="dashboard-grid">
      <div class="dashboard-card">
        <div class="card-header">
          <h2 class="card-title">热门宣传文章</h2>
          <div class="card-actions">
            <button class="card-action-btn primary" @click="navigateTo('/doctor/doctorPropagate')">
              <span>查看全部</span>
            </button>
          </div>
        </div>
        <div class="article-list">
          <div v-for="(article, index) in popularArticles" :key="index" class="article-item" @click="viewArticleDetail(article.id)">
            <div class="article-image">
              <img :src="article.coverImage" alt="文章封面">
            </div>
            <div class="article-content">
              <div class="article-title">{{ article.title }}</div>
              <div class="article-meta">
                <span class="article-views">
                  <i class="iconfont icon-eye"></i> {{ article.views }}
                </span>
                <span class="article-date">{{ article.publishDate }}</span>
              </div>
              <!--<div class="article-summary">{{ article.summary }}</div>-->
            </div>
          </div>
        </div>
      </div>
      
      <div class="dashboard-card">
        <div class="card-header">
          <h2 class="card-title">最新宣传文章</h2>
          <div class="card-actions">
            <button class="card-action-btn primary" @click="navigateTo('/doctor/doctorPropagate')">
              <span>查看全部</span>
            </button>
          </div>
        </div>
        <div class="article-list">
          <div v-for="(article, index) in latestArticles" :key="index" class="article-item" @click="viewArticleDetail(article.id)">
            <div class="article-image">
              <img :src="article.coverImage" alt="文章封面">
            </div>
            <div class="article-content">
              <div class="article-title">{{ article.title }}</div>
              <div class="article-meta">
                <span class="article-date">{{ article.publishDate }}</span>
              </div>
              <!--<div class="article-summary">{{ article.summary }}</div>-->
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 心理测试数据 -->
    <!--<div class="dashboard-card">
      <div class="card-header">
        <h2 class="card-title">常用测试卷</h2>
        <div class="card-actions">
          <button class="card-action-btn primary" @click="navigateTo('/doctor/testPaper')">
            <span>查看全部</span>
          </button>
        </div>
      </div>
      <div class="test-paper-list">
        <div v-for="(testPaper, index) in popularTestPapers" :key="index" class="test-paper-item">
          <div class="test-paper-icon" :style="{ backgroundColor: testPaper.bgColor }">
            <i class="iconfont icon-form"></i>
          </div>
          <div class="test-paper-content">
            <div class="test-paper-title">{{ testPaper.title }}</div>
            <div class="test-paper-meta">
              <span class="test-paper-usage">使用次数: {{ testPaper.usageCount }}</span>
              <span class="test-paper-duration">{{ testPaper.duration }}分钟</span>
            </div>
            <div class="test-paper-description">{{ testPaper.description }}</div>
          </div>
          <div class="test-paper-actions">
            <button class="test-paper-action-btn" @click="viewTestPaperDetail(testPaper.id)">
              查看详情
            </button>
          </div>
        </div>
      </div>
    </div>-->
  </div>
</template>

<script>
import { ref, onMounted, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';

export default {
  name: "DoctorHome",
  setup() {
    const router = useRouter();
    
    // 数据状态
    const todayAppointments = ref(0);
    const weeklyPatients = ref(0);
    const appointmentStatus = reactive({
      pending: 0,
      approved: 0,
      completed: 0
    });
    const recentAppointments = ref([]);
    
    // 宣传文章数据
    const popularArticles = ref([]);
    const latestArticles = ref([]);
    
    // 测试卷数据（暂时使用空数组）
    const popularTestPapers = ref([]);
    
    const appointmentTimeRange = ref('week');
    const loading = ref(false);
    
    // 获取工作台数据
    const fetchDashboardData = async () => {
      loading.value = true;
      try {
        const res = await request.get('/reservation/doctorDashboard', {
          params: {
            timeRange: appointmentTimeRange.value
          }
        });
        if (res.code === '200') {
          const data = res.data;
          console.log('原始工作台数据:', data);
          
          // 更新统计数据
          todayAppointments.value = data.todayAppointments || 0;
          weeklyPatients.value = data.weeklyPatients || 0;
          
          // 更新预约状态分布
          if (data.appointmentStatus) {
            appointmentStatus.pending = data.appointmentStatus.pending || 0;
            appointmentStatus.approved = data.appointmentStatus.approved || 0;
            appointmentStatus.completed = data.appointmentStatus.completed || 0;
          }
          
          // 更新今日预约列表
          if (data.recentAppointments && Array.isArray(data.recentAppointments)) {
            console.log('今日预约原始数据:', data.recentAppointments);
            
            // 确保每个预约都有状态信息和必要的字段
            recentAppointments.value = data.recentAppointments
              .filter(appointment => {
                // 只保留待审核和审核通过状态的预约
                const status = appointment.status || '待审核';
                return status === '待审核' || status === '审核通过';
              })
              .map(appointment => {
                console.log('处理预约数据:', appointment);
                
                // 将后端返回的状态映射到前端显示状态
                let status = appointment.status || '待审核';
                
                // 确保每个预约都有必要的字段
                return {
                  id: appointment.id,
                  time: appointment.time || '未知时间',
                  duration: appointment.duration || 45,
                  patientName: appointment.patientName || '未知患者',
                  patientAvatar: appointment.patientAvatar || null,
                  status: status
                };
              });
            
            console.log('处理后的今日预约数据:', recentAppointments.value);
          } else {
            console.log('没有今日预约数据或数据格式不正确');
            recentAppointments.value = [];
          }
        } else {
          ElMessage.error(res.msg || '加载工作台数据失败');
        }
      } catch (err) {
        console.error('加载工作台数据失败:', err);
        ElMessage.error('加载工作台数据失败: ' + (err.message || err));
      } finally {
        loading.value = false;
      }
    };
    
    // 加载宣传文章数据
    const fetchArticles = async () => {
      loading.value = true;
      try {
        // 获取热门宣传文章（按浏览量排序）
        const popularRes = await request.get('/propagate/selectPageFront', {
          params: {
            pageNum: 1,
            pageSize: 3,
            orderBy: 'num',
            orderType: 'desc',
            status: 'APPROVED' // 只获取已审核通过的文章
          }
        });
        
        // 获取最新宣传文章（按时间排序）
        const latestRes = await request.get('/propagate/selectPageFront', {
          params: {
            pageNum: 1,
            pageSize: 3,
            orderBy: 'time',
            orderType: 'desc',
            status: 'APPROVED' // 只获取已审核通过的文章
          }
        });
        
        if (popularRes.code === '200' && popularRes.data) {
          // 处理热门文章数据
          popularArticles.value = popularRes.data.list.map(article => ({
            id: article.id,
            title: article.title,
            coverImage: article.img || 'https://picsum.photos/id/1/120/80', // 使用文章图片或默认图片
            views: article.num || 0,
            publishDate: article.time || '',
            summary: article.content ? (article.content.length > 100 ? article.content.substring(0, 100) + '...' : article.content) : ''
          }));
        }
        
        if (latestRes.code === '200' && latestRes.data) {
          // 处理最新文章数据
          latestArticles.value = latestRes.data.list.map(article => ({
            id: article.id,
            title: article.title,
            coverImage: article.img || 'https://picsum.photos/id/4/120/80', // 使用文章图片或默认图片
            publishDate: article.time || '',
            summary: article.content ? (article.content.length > 100 ? article.content.substring(0, 100) + '...' : article.content) : ''
          }));
        }
      } catch (err) {
        console.error('加载文章数据失败:', err);
        ElMessage.error('加载文章数据失败: ' + (err.message || err));
      } finally {
        loading.value = false;
      }
    };
    
    // 加载测试卷数据
    const fetchTestPapers = async () => {
      try {
        // 这里可以添加实际的API调用，目前使用示例数据
        // const res = await request.get('/testPaper/doctorTestPapers');
        // if (res.code === '200') {
        //   popularTestPapers.value = res.data || [];
        // }
      } catch (err) {
        console.error('加载测试卷数据失败:', err);
      }
    };
    
    // 查看所有预约
    const viewAllAppointments = () => {
      router.push('/doctor/reservation');
    };
    
    // 获取预约状态对应的类型
    const getStatusType = (status) => {
      console.log('获取状态类型:', status);
      const statusMap = {
        '待审核': 'warning',
        '审核通过': 'success',
        '审核拒绝': 'danger',
        '已完成': 'info',
        '已结束': 'info'  // 添加与后端一致的状态名称
      };
      return statusMap[status] || 'warning';
    };
    
    // 处理预约
    const handleAppointment = (appointment) => {
      ElMessageBox.confirm(
        `确认将与 ${appointment.patientName} 的预约标记为已完成吗？`,
        '完成预约',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'info'
        }
      ).then(() => {
        // 调用API更新预约状态为已完成
        request.put('/reservation/update', {
          id: appointment.id,
          status: '已结束'  // 修改为与后端一致的状态名称
        }).then(res => {
          if (res.code === '200') {
            ElMessage.success('预约已标记为完成');
            fetchDashboardData(); // 刷新数据
          } else {
            ElMessage.error(res.msg || '操作失败');
          }
        }).catch(err => {
          console.error('更新预约状态失败:', err);
          ElMessage.error('网络错误，请稍后重试');
        });
      }).catch(() => {
        // 用户取消操作
      });
    };
    
    // 审核预约
    const reviewAppointment = (appointment) => {
      ElMessageBox.confirm(
        `是否审核通过 ${appointment.patientName} 的预约？`,
        '预约审核',
        {
          confirmButtonText: '通过',
          cancelButtonText: '拒绝',
          distinguishCancelAndClose: true,
          type: 'warning'
        }
      ).then(() => {
        // 用户点击"通过"
        request.put('/reservation/update', {
          id: appointment.id,
          status: '审核通过'
        }).then(res => {
          if (res.code === '200') {
            ElMessage.success('预约已审核通过');
            fetchDashboardData(); // 刷新数据
          } else {
            ElMessage.error(res.msg || '操作失败');
          }
        }).catch(err => {
          console.error('更新预约状态失败:', err);
          ElMessage.error('网络错误，请稍后重试');
        });
      }).catch((action) => {
        if (action === 'cancel') {
          // 用户点击"拒绝"，弹出拒绝理由输入框
          ElMessageBox.prompt('请输入拒绝理由', '拒绝预约', {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            inputType: 'textarea',
            inputValidator: (value) => {
              return value.trim() !== '' || '拒绝理由不能为空';
            }
          }).then(({ value }) => {
            request.put('/reservation/update', {
              id: appointment.id,
              status: '审核拒绝',
              reason: value
            }).then(res => {
              if (res.code === '200') {
                ElMessage.success('预约已拒绝');
                fetchDashboardData(); // 刷新数据
              } else {
                ElMessage.error(res.msg || '操作失败');
              }
            }).catch(err => {
              console.error('更新预约状态失败:', err);
              ElMessage.error('网络错误，请稍后重试');
            });
          }).catch(() => {
            // 用户取消输入拒绝理由
          });
        }
      });
    };
    
    // 导航到其他页面
    const navigateTo = (path) => {
      router.push(path);
    };
    
    // 查看文章详情
    const viewArticleDetail = (articleId) => {
      router.push(`/doctor/propagateDetail/${articleId}`);
      
      // 增加文章浏览量
      request.put(`/propagate/incrementViews/${articleId}`).catch(err => {
        console.error('增加文章浏览量失败:', err);
      });
    };
    
    // 查看测试卷详情
    const viewTestPaperDetail = (testPaperId) => {
      router.push(`/doctor/testPaper/${testPaperId}`);
    };
    
    // 监听时间范围变化
    const handleTimeRangeChange = () => {
      fetchDashboardData();
    };
    
    // 刷新仪表板数据
    const refreshDashboard = () => {
      fetchDashboardData();
      ElMessage.success('数据已刷新');
    };
    
    // 获取进度条宽度
    const getProgressWidth = (value) => {
      const total = appointmentStatus.pending + 
                    appointmentStatus.approved + 
                    appointmentStatus.completed;
      
      if (total === 0) return '0%';
      return `${(value / total * 100).toFixed(0)}%`;
    };
    
    // 获取姓名首字母
    const getInitials = (name) => {
      if (!name) return '';
      return name.slice(0, 1);
    };
    
    // 获取总预约数
    const getTotalAppointments = () => {
      return appointmentStatus.pending + appointmentStatus.approved + appointmentStatus.completed;
    };
    
    // 组件挂载时加载数据
    onMounted(() => {
      fetchDashboardData();
      fetchArticles();
      fetchTestPapers();
    });
    
    return {
      todayAppointments,
      weeklyPatients,
      appointmentStatus,
      recentAppointments,
      appointmentTimeRange,
      loading,
      popularArticles,
      latestArticles,
      popularTestPapers,
      viewAllAppointments,
      navigateTo,
      viewArticleDetail,
      viewTestPaperDetail,
      handleTimeRangeChange,
      getProgressWidth,
      getInitials,
      getTotalAppointments,
      refreshDashboard,
      getStatusType,
      handleAppointment,
      reviewAppointment
    };
  }
};
</script>

<style scoped>
.doctor-home {
  padding: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #2A5C8A;
  margin-bottom: 24px;
}

/* 数据概览卡片样式 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: relative;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  color: #2A5C8A;
}

.stat-icon i {
  font-size: 24px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* 仪表板网格布局 */
.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.dashboard-card {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.card-header {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.card-action-btn {
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.card-action-btn:hover {
  background-color: #f5f5f5;
}

.card-action-btn.primary {
  background-color: #2A5C8A;
  color: white;
  padding: 6px 12px;
  border-radius: 6px;
}

.card-action-btn.primary:hover {
  background-color: #1e4266;
}

.card-action-btn.review {
  background-color: #F97316;
}

.card-action-btn.review:hover {
  background-color: #ea580c;
}

/* 预约状态分布图表 */
.chart-container {
  padding: 20px;
}

.time-range-select {
  padding: 6px 10px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  color: #333;
}

.appointment-status-chart {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.status-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.status-bar {
  height: 8px;
  background-color: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.status-progress {
  height: 100%;
  border-radius: 4px;
}

.status-label {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.status-name {
  color: #666;
}

.status-value {
  font-weight: 500;
  color: #333;
}

/* 预约列表样式 */
.appointment-list {
  padding: 0 20px;
  max-height: 350px;
  overflow-y: auto;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #999;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  color: #ccc;
}

.appointment-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.appointment-item:last-child {
  border-bottom: none;
}

.appointment-time {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.time {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.duration {
  font-size: 13px;
  color: #666;
  background-color: #f5f5f5;
  padding: 2px 8px;
  border-radius: 10px;
}

.appointment-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.patient-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.patient-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
}

.patient-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.patient-details {
  display: flex;
  flex-direction: column;
}

.patient-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.appointment-type {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  margin-top: 4px;
  display: inline-block;
}

.appointment-type.video {
  background-color: rgba(99, 102, 241, 0.1);
  color: #6366F1;
}

.appointment-type.chat {
  background-color: rgba(16, 185, 129, 0.1);
  color: #10B981;
}

.appointment-type.inperson {
  background-color: rgba(249, 115, 22, 0.1);
  color: #F97316;
}

.appointment-actions {
  display: flex;
  justify-content: flex-end;
}

.appointment-action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background-color: #2A5C8A;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.appointment-action-btn:hover {
  background-color: #1e4266;
}

/* 文章列表样式 */
.article-list {
  padding: 0 20px;
  max-height: 350px;
  overflow-y: auto;
}

.article-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  gap: 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.article-item:hover {
  background-color: #f9f9f9;
}

.article-item:last-child {
  border-bottom: none;
}

.article-image {
  width: 120px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.article-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.article-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.article-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #999;
}

.article-views {
  display: flex;
  align-items: center;
  gap: 4px;
}

.article-summary {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 测试卷列表样式 */
.test-paper-list {
  padding: 20px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.test-paper-item {
  background-color: #f9f9f9;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.test-paper-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.test-paper-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
}

.test-paper-icon i {
  font-size: 24px;
  color: #2A5C8A;
}

.test-paper-content {
  flex: 1;
}

.test-paper-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.test-paper-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
}

.test-paper-description {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.test-paper-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}

.test-paper-action-btn {
  padding: 6px 12px;
  background-color: #2A5C8A;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.test-paper-action-btn:hover {
  background-color: #1e4266;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
  
  .test-paper-list {
    grid-template-columns: 1fr;
  }
  
  .article-image {
    width: 80px;
    height: 60px;
  }
}

.default-avatar {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2A5C8A;
  color: white;
  font-size: 16px;
  font-weight: 500;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #999;
}

.loading-state i {
  font-size: 24px;
  margin-bottom: 10px;
}

.status-total {
  margin-top: 12px;
  text-align: right;
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.icon-refresh {
  font-size: 14px;
  transition: transform 0.5s ease;
}

.icon-refresh.rotating {
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.appointment-status {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}
</style>