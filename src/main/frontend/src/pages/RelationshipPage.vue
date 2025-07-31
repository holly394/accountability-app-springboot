<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { api } from 'boot/axios'
import {QMarkupTable} from "quasar";
import {RelationshipData} from "components/dto/RelationshipData.ts";
import {UserDto} from "components/dto/UserDto.ts";

defineOptions({
  name: 'RelationshipPage'
});

const relationship = ref<RelationshipData>( {
  id: 0,
  userId: 0,
  partnerId: 0,
  status: ''
});

const userSearch = ref<UserDto>( {
  id: 0,
  username:''
});

const searchResult = ref<UserDto[]>([]);

onMounted(async () => {
  relationship.value = await api.get<RelationshipData>('/relationships/approved-list').then(res => res.data)
});


const searchFriend = async () => {
  await api.get<UserDto[]>(`/relationships/search?username=${userSearch.value.username}`)
    .then(response => {
      searchResult.value = response.data;
    })
    .catch(error => {
      console.log(error);
    })
};

</script>

<template>
    <div class="q-pa-md row items-start q-gutter-md">

      <q-card dark bordered class="bg-grey-9 my-card">
        <q-card-section>
          <div class="text-h6">Find a friend</div>
        </q-card-section>

        <q-card-section>
            <q-input
              v-on:keyup="searchFriend"
              v-model="userSearch.username"
              label="search for friend"
              filled
              type="textarea"
            />
          <q-markup-table title="EXISTING USERS">
            <thead>
            <tr><th>EXISTING USERS</th></tr>
            <tr>
              <th>ID</th>
              <th>USERNAME</th>
              <th>REQUEST</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="user in searchResult" :key="user.id">
                <td v-text="user.username" />
            </tr>
            </tbody>
          </q-markup-table>
        </q-card-section>
      </q-card>


    </div>
</template>
