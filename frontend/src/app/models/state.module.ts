// state.module.ts
import { District } from './district.module';

export interface State {
  stateId?: number;
  stateName?: string;
  stateCode?: string;
  countryId?: number; // To link back to the parent country
  districts?: District[]; // Include districts for hierarchical location data
}