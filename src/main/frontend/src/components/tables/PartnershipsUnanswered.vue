<script setup lang="ts">
import {QMarkupTable} from 'quasar';
import {RelationshipData} from 'components/dto/RelationshipData.ts';
import {Page} from "components/paging/Page.ts";
import {relationshipData} from "src/composables/relationshipData.ts";
import {RelationshipStatus} from "components/dto/RelationshipStatus.ts";
import {RelationshipStatusDto} from "components/dto/RelationshipStatusDto.ts";
import {ref} from "vue";

const { updateRelationship } = relationshipData();

defineOptions({
  name: 'PartnershipsUnanswered'
});

const props = defineProps<{
  partnerList: Page<RelationshipData>
}>()

const emit = defineEmits(['updateRelationship'])

async function updateRelationshipButton(relationshipId: number, statusEnum: RelationshipStatus) {
  let status = ref<RelationshipStatusDto>({
    status: statusEnum
  })
  await updateRelationship(relationshipId, status.value);
  emit('updateRelationship');
}

</script>

<template>
    <div>
      <q-card
        class="my-card text-white"
        style="background: radial-gradient(circle, #35a2ff 0%, #014a88 100%)"
      >
        <q-card-section>
          <div class="text-h6">Pending requests</div>
          <div class="text-subtitle2">Answer partnership requests here</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-markup-table title="PARTNERSHIPS TO RESPOND TO">
            <thead>
            <tr>
              <th>RELATIONSHIP ID</th>
              <th>PARTNER ID</th>
              <th>NAME</th>
              <th>STATUS</th>
              <th>ACCEPT</th>
              <th>REJECT</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="relationship in props.partnerList.content" :key="relationship.id">
                <td v-text="relationship.id" />
                <td v-text="relationship.partnerId" />
                <td v-text="relationship.partnerName" />
                <td v-text="relationship.status" />
                <td><q-btn @click="updateRelationshipButton(relationship.id, RelationshipStatus.APPROVED)"
                           label="APPROVE" type="submit" color="primary"/></td>
                <td><q-btn @click="updateRelationshipButton(relationship.id, RelationshipStatus.REJECTED)"
                           label="REJECT" type="submit" color="primary"/></td>
            </tr>
            </tbody>
          </q-markup-table>
        </q-card-section>
      </q-card>
    </div>
</template>
