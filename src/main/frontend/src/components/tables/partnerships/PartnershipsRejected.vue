<script setup lang="ts">
import { QMarkupTable } from 'quasar';
import { RelationshipDto } from 'components/dto/relationship/RelationshipDto.ts';
import {Page} from "components/paging/Page.ts";
import {relationshipData} from "src/composables/RelationshipData.ts";

const { deleteRelationship } = relationshipData();

defineOptions({
  name: 'PartnershipsRejected'
});

const props = defineProps<{
  rejectionsSent: Page<RelationshipDto>,
  rejectionsReceived: Page<RelationshipDto>
}>()

const emit = defineEmits(['deleteRelationship']);

async function deleteRequest(relationshipId: number) {
  await deleteRelationship(relationshipId);
  emit('deleteRelationship');
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
            <tr v-for="relationship in props.rejectionsSent.content" :key="relationship.id">
                <td v-text="relationship.id" />
                  <td v-text="relationship.partner.id" />
                  <td v-text="relationship.partner.username" />
                  <td v-text="relationship.status" />
                  <td><q-btn @click="deleteRequest(relationship.id)" label="UNDO" type="submit" color="primary"/></td>
            </tr>
            <tr v-for="relationship in props.rejectionsReceived.content" :key="relationship.id">
              <td v-text="relationship.id" />
              <td v-text="relationship.user.id" />
              <td v-text="relationship.user.username" />
              <td v-text="relationship.status" />
            </tr>
            </tbody>
          </q-markup-table>
        </q-card-section>
      </q-card>
    </div>
</template>
