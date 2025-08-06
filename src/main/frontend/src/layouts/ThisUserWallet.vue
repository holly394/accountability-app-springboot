<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { api } from 'boot/axios'
import {WalletDto} from 'components/dto/WalletDto.ts';

const wallet = ref<WalletDto>( {
  userId: 0,
  userName: '',
  balance: 0.00
});

defineOptions({
  name: 'ThisUserWallet'
});

onMounted(async () => {
  wallet.value = await api.get<WalletDto>('/wallet').then(res => res.data)
});

</script>

<template>
       {{ wallet.balance.toFixed(2) }}
</template>
