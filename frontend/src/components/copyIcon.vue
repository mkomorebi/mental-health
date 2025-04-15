<template>
  <article class="copy-text-icon" @click="clickCopy">
    <a-icon v-if="!copyFlag" type="copy" />
    <a-icon v-else type="check" />
    <span style="cursor: pointer; margin-left: 5px" >copy</span>
  </article>
</template>

<script>
export default {
  name: "copyIcon",
  data() {
    return {
      copyFlag: false
    }
  },
  methods: {
    clickCopy() {
      this.copyFlag = true
      setTimeout(() => {
        this.copyFlag = false
      }, 1300)
      this.$emit('clickCopy')
    }
  }
}
</script>

<style scoped>
.copy-text-icon {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 10px;
}
</style>
