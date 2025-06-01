// pincode.module.ts
export interface Pincode {
    pincodeId?: number;
    pincode?: string;
    officeName?: string;
    officeType?: string;
    delivery?: string;
    districtId?: number; // To link back to the parent district
    latitude?: number;
    longitude?: number;
  }