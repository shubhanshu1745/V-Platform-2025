// role.module.ts
import { RoleType } from "./role-type.enum.enum";

export interface Role {
  roleId?: number;
  roleName?: RoleType;
}