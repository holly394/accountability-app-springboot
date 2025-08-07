
import {api} from "boot/axios.ts";
import {UserDto} from "components/dto/UserDto.ts";

export function relationshipData() {
  const getApprovedPartners = async (): Promise<UserDto[]> => {
    return (await api.get<UserDto[]>(`/relationships/get-approved-partners`)).data;
  }

  return { getApprovedPartners };
}
