<template>
  <div>
    <q-card
      class="rounded-outer-card text-white no-shadow q-pt-none"
      bordered
    >
      <q-card-section>
        <div class="text-h6">Wallet overview</div>
        <div class="text-subtitle2">How you compare</div>
      </q-card-section>
      <q-card-section class="rounded-inner-card" bordered>
        <div ref="barchart" style="width: 500px; height: 500px;" />
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
import {DefaultPage, Page} from 'components/paging/Page.ts';
import {relationshipData} from 'src/composables/RelationshipData.ts';

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

const currentUserWallet = ref<WalletDto>({
  userId: 0,
  userName: '',
  balance: 0
});

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

<style lang="scss" scoped>
@import 'src/css/quasar.variables.scss';

.rounded-outer-card {
  @include outer-card;
}
.rounded-inner-card {
  @include inner-card-section;
}

</style>
