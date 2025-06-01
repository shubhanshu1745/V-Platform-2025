// country.moduele.ts
import { State } from "./state.module";

export interface Country {
  countryId?: number;
  countryName?: string;
  countryCode?: string;
  states?: State[]; // Include states for hierarchical location data
}