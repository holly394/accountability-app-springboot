//RelationshipData.ts composable
//Specifically for relationship-related data
//This frontend composable is meant to organize commonly used API calls in one place
//the API calls from here to the backend (via controllers)
//we don't manipulate the data here!
//Data manipulation stays in the backend mainly in the controller

import {api} from "boot/axios.ts";
import {Page} from "components/paging/Page.ts";
import {RelationshipData} from "components/dto/RelationshipData.ts";
import {RelationshipStatus} from "components/dto/RelationshipStatus.ts";
import {RelationshipStatusDto} from "components/dto/RelationshipStatusDto.ts";

export function relationshipData() {

  const getPartnersByStatus = async (status: RelationshipStatus): Promise<Page<RelationshipData>> => {
    return (await api.get<Page<RelationshipData>>(`/relationships`, {
      params: {
        status: status,
      }
    })).data;
  }

  // FIXME: See RelationshipController about parameterizing what's available
  const getRequestsToAnswer = async (): Promise<Page<RelationshipData>> => {
    return (await api.get<Page<RelationshipData>>(`/relationships/pending-requests-to-answer`)).data;
  }

  const getRequestsToWait = async (): Promise<Page<RelationshipData>> => {
    return (await api.get<Page<RelationshipData>>(`/relationships/pending-requests-to-wait`)).data;
  }

  const getRejectionsSent = async (): Promise<Page<RelationshipData>> => {
    return (await api.get<Page<RelationshipData>>(`/relationships/rejected-requests-sent`)).data;
  }

  const getRejectionsReceived = async (): Promise<Page<RelationshipData>> => {
    return (await api.get<Page<RelationshipData>>(`/relationships/rejected-requests-received`)).data;
  }

  const updateRelationship = async (relationshipId: number, newStatus: RelationshipStatusDto): Promise<RelationshipData> => {
    return (await api.post<RelationshipData>(`/relationships/${relationshipId}`, newStatus)).data;
  }

  const deleteRelationship = async (relationshipId: number): Promise<void> => {
    await api.delete(`/relationships/${relationshipId}`)
  }

  async function sendRequest(partnerId: number) {
    await api.put(`/relationships/request/${partnerId}`)
  }

  async function search(inputName: string): Promise<RelationshipData[]> {
    return (await api.get<RelationshipData[]>(`/relationships/search`, {
      params: {
        username: inputName,
      }
    })).data;
  }


  return { getPartnersByStatus, deleteRelationship, sendRequest, search,
    getRequestsToAnswer, updateRelationship, getRequestsToWait, getRejectionsSent, getRejectionsReceived};
}
