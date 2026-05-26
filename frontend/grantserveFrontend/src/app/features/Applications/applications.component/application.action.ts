import { createActionGroup, emptyProps, props } from '@ngrx/store';

export const UserActions = createActionGroup({
  source: 'Count API',
  events: {
    'Get count': emptyProps(),                 // The "Start" trigger
    'Get count Success': props<{ count: Record<string, number> }>(), // The "Success" data
    'Get count Failure': props<{ error: any }>()    // The "Error"
  }
});