import { createReducer, on } from "@ngrx/store";
import { UserActions } from "./application.action";

export const countReducer = createReducer(
 { All: 0, Submitted: 0, 'Under Review': 0, Approved: 0, Rejected: 0, loading: false }, // Initial state
  on(UserActions.getCount, (state) => ({ ...state, loading: true })),
  on(UserActions.getCountSuccess, (state, { count }) => ({ 
    ...state, 
    ...count,
    loading: false 
  })),
  on(UserActions.getCountFailure, (state) => ({
    ...state,
    loading: false
  }))
);