<template>
  <div>
    <q-card class="no-shadow" bordered>
      <q-card-section class="text-h6">
        Bar Chart
      </q-card-section>
      <q-card-section>
        <div ref="barchart" style="width: 600px; height: 200px;" />
      </q-card-section>
    </q-card>
  </div>
</template>


<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import * as echarts from 'echarts';
import {WalletDto} from 'components/dto/WalletDto.js';
import {ECharts} from 'echarts';
import {walletData} from 'src/composables/WalletData.ts';
import {DefaultPage, Page} from "components/paging/Page.ts";
import {relationshipData} from "src/composables/RelationshipData.ts";

const {getCurrentUserWallet, getWalletsByUserIds} = walletData();
const { getPartnerIdList } = relationshipData();

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

const currentUserWallet = ref<WalletDto>();
const partners = ref<number[]>([]);
const partnerWallets = ref<Page<WalletDto>>(DefaultPage as Page<WalletDto>);

// Fetch data and initialize chart
onMounted(async () => {
  // 1. Fetch data from API
  partners.value = await getPartnerIdList();
  currentUserWallet.value = await getCurrentUserWallet();
  data.labels.push('YOU');
  data.values.push(currentUserWallet.value.balance);

  partnerWallets.value = await getWalletsByUserIds(partners.value);
  partnerWallets.value.content.forEach(walletDto => {
    data.labels.push(walletDto.userName);
    data.values.push(walletDto.balance);
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
