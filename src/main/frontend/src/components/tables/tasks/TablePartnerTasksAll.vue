<script setup lang="ts">
import {QMarkupTable} from 'quasar';
import {TaskDataDto} from 'components/dto/task/TaskDataDto.ts';
import {DefaultPage, Page} from 'components/paging/Page.ts';
import {onMounted, ref} from 'vue';
import {taskData} from 'src/composables/TaskData.ts';
import {relationshipData} from 'src/composables/RelationshipData.ts';
const { getAllTasksByUserList } = taskData();
const { getPartnerIdList } = relationshipData();

defineOptions({
  name: 'TablePartnerTasksAll',
});

const expanded = ref<boolean>(false);
const currentPage = ref<number>(0);
const maxPages = ref<number>(0);
const pageSize = 5;

const recentPartnerTasks = ref<Page<TaskDataDto>>(DefaultPage as Page<TaskDataDto>);
const partnerList = ref<number[]>([]);
const tableTitle=ref<string>('Recent tasks by partners');

onMounted(async () => {
  await reloadPartnerTaskList();
})

const reloadPartnerTaskList = async() => {
  partnerList.value = await getPartnerIdList();
  recentPartnerTasks.value = await getAllTasksByUserList(partnerList.value, currentPage.value-1, pageSize);
  currentPage.value = recentPartnerTasks.value.pageNumber+1;
  maxPages.value = recentPartnerTasks.value.totalPages;
}

async function changePage() {
  await reloadPartnerTaskList();
}

</script>

<template>
  <q-card
    class="rounded-outer-card my-card text-white"
    style="background: radial-gradient(circle, #35a2ff 0%, #014a88 100%)"
  >
    <q-card-section>
      <div class="text-h6">{{tableTitle}}</div>
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

    <q-card-section class="q-pt-none">
    <q-markup-table title="TASKS" class="q-pt-none">
      <thead>
        <tr>
          <th>Task ID</th>
          <th>Task Description</th>
          <th>Status</th>
          <th>Time Spent</th>
          <th>User</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="task in recentPartnerTasks.content" :key="task.id">
            <td v-text="task.id" />
            <td v-text="task.description" />
            <td v-text="task.status" />
            <td v-text="task.durationString" />
            <td v-text="task.userName" />
        </tr>
      </tbody>
      <q-pagination
        v-model="currentPage"
        :max="maxPages"
        @click="changePage()"
      />
    </q-markup-table>
    </q-card-section>
      </div>
    </q-slide-transition>
  </q-card>
</template>

<style lang="scss" scoped>
@import 'src/css/quasar.variables.scss';

.rounded-outer-card {
  @include card-style-5-columns;
}

</style>

