import { inject } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { ApplicationsService } from "../service/applications.service";
import { UserActions } from "./application.action";
import { catchError, map, of, switchMap, tap } from "rxjs";

export const loadCountEffect = createEffect(
  () => {
   
    const actions$ = inject(Actions);
    const service = inject(ApplicationsService);

    return actions$.pipe(
      ofType(UserActions.getCount),
      switchMap(() => service.getUserApplicationCounts().pipe(
        map(count => {
          return UserActions.getCountSuccess({ count });
        }),
        catchError(error => {
          return of(UserActions.getCountFailure({ error }));
        })
      ))
    );
  },
  { functional: true }
);