<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { api } from 'boot/axios'
import {UserDto} from 'components/dto/UserDto.ts';

const user = ref<UserDto>( {
  username: '',
  id: 0
});

defineOptions({
  name: 'ThisUserName'
});

onMounted(async () => {
  user.value = await api.get<UserDto>('relationships/this-user').then(res => res.data)
});


</script>

<template>
    {{ user.username }}
</template>
