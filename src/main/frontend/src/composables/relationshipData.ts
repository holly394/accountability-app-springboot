
import {api} from "boot/axios.ts";

export function relationshipData() {
  const getApprovedPartnerIds = async (): Promise<number[]> => {
    return (await api.get<number[]>(`/relationships/get-approved-partner-id-list`)).data;
  }

  return { getApprovedPartners: getApprovedPartnerIds };
}
