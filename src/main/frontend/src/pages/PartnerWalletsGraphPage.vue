<template>
  <div>
    <q-card class="no-shadow" bordered>
      <q-card-section class="text-h6">
        Bar Chart
      </q-card-section>
      <q-card-section>
        <div ref="barchart" style="width: 600px; height: 400px;" />
      </q-card-section>
    </q-card>
  </div>
</template>


<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import * as echarts from 'echarts';
import {api} from "boot/axios.js";
import {WalletDto} from "components/dto/WalletDto.js";
import {ECharts} from "echarts";

defineOptions({
  name: 'TestGraph'
});

// ECharts instance (typed as ECharts or null)
let chartInstance: ECharts | null = null;

//in template, we have <div> with ref="barchart"
// DOM reference (strictly typed as HTMLDivElement or null)
const barchart = ref<HTMLDivElement | null>(null);

// Define interface for API response
const data = ({
  labels: <string[]>[],
  values: <number[]>[]
})

// Fetch data and initialize chart
onMounted(async () => {
  // 1. Fetch data from API
  api.get<WalletDto[]>('/wallet/get-partner-wallets')
    .then(res => {
      let walletArray = res.data
      walletArray.forEach(wallet => {
        data.labels.push(wallet.userName);
        data.values.push(wallet.balance);
      })
    })
    .catch(error => {
      console.log(error);
    })

  await api.get<WalletDto>('/wallet')
    .then(res => {
      let wallet = res.data
      data.labels.push("YOU");
      data.values.push(wallet.balance);
    })
    .catch(error => {
      console.log(error);
    })

  // 2. Initialize ECharts and attach to DOM element <div ref="barchart">
  // barchart.value = points to DOM element
  chartInstance = echarts.init(barchart.value);

  // 3. Update chart with API data
  chartInstance.setOption({
    xAxis: { type: 'category', data: data.labels },
    yAxis: { type: 'value' },
    series: [{ data: data.values, type: 'bar' }],
  });
});

//to avoid memory leaks, dispose chart on unmount
onUnmounted(() => {
  chartInstance?.dispose();
});

</script>

<style scoped>
</style>
