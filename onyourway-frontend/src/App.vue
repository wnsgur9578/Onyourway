<template>
  <div id="app-root">
    <Navbar />
    <router-view />
    <Footer/>
  </div>
</template>

<script setup>
import Navbar from './components/Navbar.vue'
import Footer from "./components/Footer.vue";
</script>

<style>
/* 전체 박스 모델 설정 */
* {
  box-sizing: border-box;
}

html, body {
  margin: 0;
  padding: 0;

}

#app {
  min-width: 100%;
  max-width: 150%;
}

#app-root {
  display: flex;
  flex-direction: column;
  width: 100%;
  background-color: #ffffff;
}


</style>
