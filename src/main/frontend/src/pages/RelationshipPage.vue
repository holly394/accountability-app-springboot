<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { api } from 'boot/axios'
import { QMarkupTable } from 'quasar';
import { RelationshipData } from 'components/dto/RelationshipData.ts';
import { UserDto } from 'components/dto/UserDto.ts';
import PartnershipsApproved from 'components/tables/PartnershipsApproved.vue';
import PartnershipsPending from 'components/tables/PartnershipsPending.vue';
import PartnershipsRejected from 'components/tables/PartnershipsRejected.vue';
import PartnershipsUnanswered from 'components/tables/PartnershipsUnanswered.vue';


defineOptions({
  name: 'RelationshipPage'
});

const thisUser = ref<UserDto>({
  id: 0,
  username: ''
});

const userSearch = ref<UserDto>({
  id: 0,
  username: ''
});

const searchResult = ref<RelationshipData[]>([]);
const everythingList = ref<RelationshipData[]>([]);

onMounted(async () => {
  thisUser.value = await api.get<UserDto>('/relationships/this-user').then(res => res.data)
  everythingList.value = await api.get<RelationshipData[]>('/relationships').then(res => res.data)
});

const searchFriend = async () => {
  await api.get<RelationshipData[]>(`/relationships/search?username=${userSearch.value.username}`)
    .then(response => {
      searchResult.value = response.data;
    })
    .catch(error => {
      console.log(error);
    })
};

async function sendRequest(partnerId: number) {
  await api.put(`/relationships/request/${partnerId}`)
    .then (response => {
      for(let i=0; i < response.data.length; i++) {
        everythingList.value.splice(everythingList.value.length, 0, response.data[i]);
      }
      window.location.reload();
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
              v-model="userSearch.username"
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
                <td v-text="partner.partnerName" />
              <template v-if="partner.status === null">
                <q-btn @click="sendRequest(partner.partnerId)" label="Request" type="submit" color="primary"/>
              </template>
              <template v-else>
                <td v-text="partner.status" />
              </template>
            </tr>
            </tbody>
          </q-markup-table>
        </q-card-section>
      </q-card>


      <PartnershipsApproved />
      <PartnershipsUnanswered />
      <PartnershipsPending />
      <PartnershipsRejected />

    </div>
</template>
