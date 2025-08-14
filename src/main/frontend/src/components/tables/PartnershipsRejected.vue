<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { api } from 'boot/axios'
import { QMarkupTable } from 'quasar';
import { RelationshipData } from 'components/dto/RelationshipData.ts';
import { UserDto } from 'components/dto/UserDto.ts';


defineOptions({
  name: 'PartnershipsRejected'
});

const thisUser = ref<UserDto>({
  id: 0,
  username: ''
});

const everythingList = ref<RelationshipData[]>([]);

onMounted(async () => {
  thisUser.value = await api.get<UserDto>('/user').then(res => res.data)
  everythingList.value = await api.get<RelationshipData[]>('/relationships').then(res => res.data)
});


async function deleteRequest(relationshipId: number) {
  await api.delete(`/relationships/${relationshipId}`)
    .then (response => {
      for(let i=0; i < response.data.length; i++) {
        const index = everythingList.value.findIndex(
          (relationship) => relationship.id === response.data[i]
        );
        everythingList.value.splice(index, 1);
      }
    })
    .catch(error => {
      console.log(error);
    })
}

</script>

<template>
    <div>
      <q-card
        class="my-card text-white"
        style="background: radial-gradient(circle, #35a2ff 0%, #014a88 100%)"
      >
        <q-card-section>
          <div class="text-h6">Rejected Partnerships</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-markup-table title="REJECTED PARTNERSHIPS">
            <thead>
            <tr>
              <th>RELATIONSHIP ID</th>
              <th>PARTNER ID</th>
              <th>NAME</th>
              <th>STATUS</th>
              <th>REMOVE</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="relationship in everythingList" :key="relationship.id">
              <template v-if="relationship.status === 'REJECTED' ">
                <td v-text="relationship.id" />
                <template v-if="relationship.userId === thisUser.id">
                  <td v-text="relationship.partnerId" />
                  <td v-text="relationship.partnerName" />
                  <td v-text="relationship.status" />
                  <td><q-btn @click="deleteRequest(relationship.id)" label="DELETE" type="submit" color="primary"/></td>
                </template>
                <template v-else>
                  <td v-text="relationship.userId" />
                  <td v-text="relationship.userName" />
                  <td v-text="relationship.status" />
                  <td> </td>
                </template>
              </template>
            </tr>
            </tbody>
          </q-markup-table>
        </q-card-section>
      </q-card>
    </div>
</template>
