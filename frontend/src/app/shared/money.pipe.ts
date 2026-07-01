import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'money', standalone: true })
export class MoneyPipe implements PipeTransform {
  transform(value: number | null | undefined, currency = 'USD'): string {
    if (value === null || value === undefined) {
      return '';
    }
    return new Intl.NumberFormat('en-US', { style: 'currency', currency }).format(value);
  }
}
