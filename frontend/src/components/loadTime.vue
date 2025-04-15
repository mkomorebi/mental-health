<template>
  <span>{{ countdown }} 秒</span>
</template>

<script>
export default {
  data() {
    return {
      countdown: 60,
      startTime: null,
    };
  },
  mounted() {
    this.init()
  },
  beforeDestroy() {
    // 在组件销毁前清除定时器
    clearInterval(this.timerId);
    // 保存开始时间到 localStorage 中
    localStorage.setItem('startTime', this.startTime);
  },
  methods: {
    init() {
      // 从 localStorage 中获取开始时间
      const startTime = localStorage.getItem('startTime');
      if (startTime) {
        // 如果存在开始时间，计算倒计时
        this.startTime = parseInt(startTime);
        this.countdown = 60 - Math.floor((Date.now() - this.startTime) / 1000);
        // 如果倒计时已经结束
        if (this.countdown <= 0) {
          clearInterval(this.timerId);
          localStorage.removeItem('startTime')
        }
      } else {
        return
      }
      // 每秒更新倒计时
      this.timerId = setInterval(() => {
        if (this.countdown !== 0) {
          this.countdown--;
        }
        this.$emit('input', this.countdown)
        if (this.countdown === 0) {
          clearInterval(this.timerId);
          localStorage.removeItem('startTime')
          // this.resetCountdown();
        }
      }, 1000);
    },
    resetCountdown() {
      this.countdown = 60;
      this.startTime = Date.now();
    },
    startTimeout() {
      if (this.countdown<0 || isNaN(this.countdown)) {
        localStorage.removeItem('startTime')
        this.countdown = 60;
      }
      localStorage.setItem('startTime', Date.now())
      this.init()
    }
  },
};
</script>
