<template>
  <div
      class="fixed top-10 right-10 w-96 p-6 bg-white border border-gray-300 rounded-xl shadow-lg z-50"
  >
    <div class="flex justify-between items-center mb-4">
      <h3 class="text-lg font-semibold text-gray-800">✍️ GPT 일정 요약</h3>
      <button @click="$emit('close')" class="text-gray-500 hover:text-red-500 text-xl">×</button>
    </div>
    <p class="text-gray-700 whitespace-pre-line">{{ summary }}</p>
  </div>
</template>

<script setup>
defineProps({
  summary: String,
})
</script>
