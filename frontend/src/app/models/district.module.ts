// district.module.ts
import { Pincode } from "./pincode.module";

export interface District {
  districtId?: number;
  districtName?: string;
  stateId?: number; // To link back to the parent state
  pincodes?: Pincode[]; // Include pincodes for hierarchical location data
}