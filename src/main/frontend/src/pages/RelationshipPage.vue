<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { api } from 'boot/axios'
import {QMarkupTable} from "quasar";
import {RelationshipStatusDto} from "components/dto/RelationshipStatusDto.ts";

defineOptions({
  name: 'RelationshipPage'
});

const relationships = ref<RelationshipStatusDto[]>( []);

const userSearch = ref<RelationshipStatusDto>( {
  id: 0,
  partnerId: 0,
  partnerUsername: '',
  status: ''
});

const searchResult = ref<RelationshipStatusDto[]>([]);

onMounted(async () => {
  relationships.value = await api.get<RelationshipStatusDto[]>('/relationships').then(res => res.data)
});

const searchFriend = async () => {
  await api.get<RelationshipStatusDto[]>(`/relationships/search?username=${userSearch.value.partnerUsername}`)
    .then(response => {
      searchResult.value = response.data;
    })
    .catch(error => {
      console.log(error);
    })
};

async function sendRequest(partnerId: number) {
  await api.get<RelationshipStatusDto>(`/relationships/send-request?partnerId=${partnerId}`)
    .then(response => {
      const index = relationships.value.findIndex(
        (relationship) => relationship.partnerId === partnerId
      );
      relationships.value.splice(index, 1, response.data);
    })
    .catch(error => {
      console.log(error);
    })
}

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
              v-model="userSearch.partnerUsername"
              label="search for a friend"
              filled
              type="textarea"
            />
        </q-card-section>

        <q-card-section>
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
            <tr v-for="partner in searchResult" :key="partner.id">
              <td v-text="partner.partnerId" />
              <td v-text="partner.partnerUsername" />
              <template v-if="partner.status === null">
                <q-btn @click="sendRequest(partner.id)" label="Request" type="submit" color="primary"/>
              </template>
              <template v-else>
                <td v-text="partner.status" />
              </template>
            </tr>
            </tbody>
          </q-markup-table>
        </q-card-section>
      </q-card>


    </div>
</template>
