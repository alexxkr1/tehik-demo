import {
  HttpInterceptorFn,
  HttpErrorResponse,
} from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      console.error('HTTP Error:', error);

      let message = "An unknown error occurred.";

      if (error.error) {
        const backendError = error.error;

        if (backendError.errors && typeof backendError.errors === 'object') {
          const entries = Object.entries(backendError.errors);
          if (entries.length > 0) {
            message = entries
              .map(([field, msg]) => `${(field)}: ${msg}`)
              .join('\n');
          }
        }
      }

      alert(message);
      return throwError(() => error);
    })
  );
};
