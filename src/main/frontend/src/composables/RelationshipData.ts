//RelationshipDto.ts composable
//Specifically for relationship-related data
//This frontend composable is meant to organize commonly used API calls in one place
//the API calls from here to the backend (via controllers)
//we don't manipulate the data here!
//Data manipulation stays in the backend mainly in the controller

import {api} from 'boot/axios.ts';
import {Page} from 'components/paging/Page.ts';
import {RelationshipDto} from 'components/dto/relationship/RelationshipDto.ts';
import {RelationshipStatus} from 'components/dto/relationship/RelationshipStatus.ts';
import {RelationshipStatusDto} from 'components/dto/relationship/RelationshipStatusDto.ts';
import {RelationshipDirection} from 'components/dto/relationship/RelationshipDirection.ts';

export function relationshipData() {

  const getPartnerIdList = async (): Promise<number[]> => {
    return (await api.get<number[]>('/relationships/get-partner-id-list')).data;
  }

  const getRelationshipsByStatus = async (status: RelationshipStatus): Promise<Page<RelationshipDto>> => {
    return (await api.get<Page<RelationshipDto>>('/relationships',{
      params: {
        statuses: status
      },
      paramsSerializer: {
        indexes: null
      }
    })).data;
  }

  const getRequests = async (status: RelationshipStatus, direction: RelationshipDirection): Promise<Page<RelationshipDto>> => {
    return (await api.get<Page<RelationshipDto>>('/relationships',{
      params: {
        statuses: status,
        directions: direction
      },
      paramsSerializer: {
        indexes: null
      }
    })).data;
  }

  const updateRelationship = async (relationshipId: number, newStatus: RelationshipStatusDto): Promise<RelationshipDto> => {
    return (await api.post<RelationshipDto>(`/relationships/${relationshipId}`, newStatus)).data;
  }

  const deleteRelationship = async (relationshipId: number): Promise<void> => {
    await api.delete(`/relationships/${relationshipId}`)
  }

  async function sendRequest(partnerId: number) {
    await api.put(`/relationships/request/${partnerId}`)
  }

  async function search(inputName: string): Promise<RelationshipDto[]> {
    return (await api.get<RelationshipDto[]>('/relationships/search', {
      params: {
        username: inputName
      }
    })).data;
  }


  return { getPartnerIdList, getRelationshipsByStatus, getRequests, deleteRelationship, sendRequest, search, updateRelationship };
}
