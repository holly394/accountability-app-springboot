<script setup lang="ts">
import {QMarkupTable} from 'quasar';
import {taskData} from 'src/composables/TaskData.ts';
import {TaskDataDto} from 'components/dto/task/TaskDataDto.ts';
import {DefaultPage, Page} from 'components/paging/Page.ts';
import {onMounted, ref} from 'vue';
import {TaskStatus} from 'components/dto/task/TaskStatus.ts';

const { deleteTask, getTasksByCurrentUserAndStatus } = taskData();

defineOptions({
  name: 'TableTasksApproved',
});

const expanded = ref<boolean>(false);
const currentPage = ref<number>(0);
const maxPages = ref<number>(0);
const pageSize = 5;
const approvedTasks = ref<Page<TaskDataDto>>(DefaultPage as Page<TaskDataDto>);

onMounted(async () => {
  await reloadApproved();
});

const reloadApproved = async () => {
  approvedTasks.value = await getTasksByCurrentUserAndStatus(
    TaskStatus.APPROVED, currentPage.value-1, pageSize);

  currentPage.value = approvedTasks.value.pageNumber+1;
  maxPages.value = approvedTasks.value.totalPages;
};

async function deleteTaskButton(taskId: number) {
  await deleteTask(taskId);
  await reloadApproved();
}

async function changePage() {
  await reloadApproved();
}

</script>

<template>
  <div class="col-12 col-sm-6 col-md-4 col-lg-3 container">

  <q-card class="outer-card" bordered>
    <div class="col column">

      <q-card-section class="col-8 col-sm-6"
                      style="flex-wrap: wrap;
                      align-items: center;"
                      bordered>

        <div class="text-h6 header-text q-my-md">Your approved tasks</div>
        <div class="text-subtitle2">See your most recently approved tasks</div>

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

      </q-card-section>


      <q-card-section class="col-8 col-sm-6"
                      style="flex-wrap: wrap;
                      align-items: center;"
                      bordered>

        <q-slide-transition>
          <div v-show="expanded">

            <q-markup-table title="APPROVED TASKS" class="col-8 col-sm-6">

              <thead>
                <tr>
                  <th>Task Description</th>
                  <th>Time Spent</th>
                  <th>Delete task</th>
                </tr>
              </thead>

              <tbody>
                <tr v-for="task in approvedTasks.content" :key="task.id" class="text-center">
                    <td v-text="task.description" />
                    <td v-text="task.durationString" />
                    <td>
                      <button @click="deleteTaskButton(task.id)">
                        Delete
                      </button>
                    </td>
                </tr>
              </tbody>

              <q-pagination
                v-model="currentPage"
                :max="maxPages"
                @click="changePage()"
              />

            </q-markup-table>

          </div>
        </q-slide-transition>

      </q-card-section>

    </div>
  </q-card>

  </div>
</template>

<style lang="scss" scoped>
@import 'src/css/quasar.variables.scss';

.outer-card {
  @include outer-card;
}

.inner-card-section {
  @include inner-card-section;
}

</style>
