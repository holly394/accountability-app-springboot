<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import * as echarts from 'echarts';
import {ECharts} from 'echarts';
import {DefaultPage, Page} from 'components/paging/Page.ts';
import { taskData } from 'src/composables/TaskData.ts';
import { relationshipData } from 'src/composables/RelationshipData.ts';
import { TaskDataDto } from 'components/dto/task/TaskDataDto.ts';
import {TaskStatus} from 'components/dto/task/TaskStatus.ts';
import {userData} from 'src/composables/UserData.ts';
import {UserDto} from 'components/dto/UserDto.ts';
import {datasetTypeDto} from 'components/dto/datasetTypeDto.ts';

const {getCurrentUserInfo} = userData();
const {getTasksByUserListAndStatusOrderByDuration} = taskData();
const { getPartnerIdList } = relationshipData();

defineOptions({
  name: 'PartnerTaskDurationGraph'
});

const expanded = ref<boolean>(false);

// ECharts instance (typed as ECharts or null)
let chartInstance: ECharts | null = null;

//in template, we have <div> with ref="barchart"
// DOM reference (strictly typed as HTMLDivElement or null)
const partnerTaskBarChart = ref<HTMLDivElement | null>(null);

const userIdList = ref<number[]>([]);

const currentUserId = ref<UserDto>({
  id: 0,
  username: ''
});

const tasks = ref<Page<TaskDataDto>>(DefaultPage as Page<TaskDataDto>);

// Fetch data and initialize chart
onMounted(async () => {
  // 1. Fetch data from API
  currentUserId.value = await getCurrentUserInfo();
  userIdList.value = await getPartnerIdList();
  userIdList.value.push(currentUserId.value.id);

  tasks.value = await getTasksByUserListAndStatusOrderByDuration(userIdList.value, TaskStatus.APPROVED);

  const barData = tasks.value.content.map(function (item) {
    return {
      name: item.description,
      value: [item.durationNumber, item.durationString, item.userName]
    };
  });

  const byUserCategory = Array.from(new Set(tasks.value.content.map(item => item.userName)))

  // 2. Initialize ECharts and attach to DOM element <div ref="barchart">
  // barchart.value = points to DOM element
  chartInstance = echarts.init(partnerTaskBarChart.value);

  // 3. Update chart with API data
  chartInstance.setOption({
    xAxis: {type: 'value', name: 'seconds'},
    yAxis: {type: 'category', inverse: true,
      axisLabel: {formatter: ''}},
    series: [{
      realtimeSort: true,
      type: 'bar',
      data: barData,
    }],
    tooltip: {formatter: function (params: datasetTypeDto) {
      return `<b>Task: ${params.name}</b><br/>
              Name: ${params.value[2]}<br/>
              Duration: ${params.value[1]}`;
      }},
    visualMap: {
      type: 'piecewise', // or 'continuous' if it was a number
      categories: byUserCategory, // our list of categories
      dimension: 2, // Maps the 3rd item in the value array (index 2) to the color
      inRange: {
        color: ['#5470c6', '#91cc75', '#fac858'] // assign colors to categories
      },
      seriesIndex: 0, // applies to our scatter series
      top: '10' // position it
    }
  });

});

//to avoid memory leaks, dispose chart on unmount
onUnmounted(() => {
  chartInstance?.dispose();
});

</script>

<template>
  <div>
    <q-card
      class="rounded-outer-card text-white no-shadow q-pt-none" bordered>
      <q-card-section class="sizing-graph" >
        <div class="text-h6">Longest approved tasks</div>
        <div class="text-subtitle2">Among yours and your partners' tasks</div>
      </q-card-section>
      <q-card-actions>
        <q-btn
          color="grey"
          round
          flat
          dense
          :icon="expanded ? 'keyboard_arrow_up' : 'keyboard_arrow_down'"
          @click="expanded = !expanded"
        />
      </q-card-actions>

      <q-slide-transition>
        <div v-show="expanded">

      <q-card-section class="rounded-inner-card sizing-graph" bordered>
        <div ref="partnerTaskBarChart" style="width: 550px; height: 400px;" />
      </q-card-section>
        </div>
      </q-slide-transition>
    </q-card>
  </div>
</template>


<style lang="scss" scoped>
@import 'src/css/quasar.variables.scss';

.rounded-outer-card {
  @include graph-style;
}
.rounded-inner-card {
  @include card-section-style;
}
.sizing-graph {
  @include inner-graph-size;
}

</style>
