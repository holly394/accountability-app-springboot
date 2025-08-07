<script setup>
import ListPagination from 'src/components/paginations/ListPagination.vue'
</script>

<template>
  <ListPagination api-url="url" :per-page="5">
    <template #msg="{ name, msg, time }">
      <div class="item">
        <p>{{ name }}</p>
        <p class="meta">by {{ msg }} | {{ time }} </p>
      </div>
    </template>
  </ListPagination>
</template>

<style scoped>
.meta {
  font-size: 0.8em;
  color: #42b883;
}
</style>
