import { createFeatureSelector, createSelector } from "@ngrx/store";

export const selectCountFeature = createFeatureSelector<any>('applications');
export const selectCounts = createSelector(
  selectCountFeature, 
  
  (state) => state
);