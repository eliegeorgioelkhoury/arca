import { Directive, ElementRef, Input, OnChanges, inject } from '@angular/core';

/**
 * Signature motion: counts a number up from zero on load. Honors
 * prefers-reduced-motion by rendering the final value immediately.
 */
@Directive({ selector: '[appCountUp]', standalone: true })
export class CountUpDirective implements OnChanges {
  @Input('appCountUp') value = 0;
  @Input() decimals = 2;
  @Input() prefix = '';

  private el = inject<ElementRef<HTMLElement>>(ElementRef);

  ngOnChanges(): void {
    const target = this.value ?? 0;
    const prefersReduced =
      typeof matchMedia === 'function' && matchMedia('(prefers-reduced-motion: reduce)').matches;

    if (prefersReduced) {
      this.render(target);
      return;
    }

    const duration = 700;
    const start = performance.now();
    const tick = (now: number) => {
      const progress = Math.min(1, (now - start) / duration);
      const eased = 1 - Math.pow(1 - progress, 3);
      this.render(target * eased);
      if (progress < 1) {
        requestAnimationFrame(tick);
      }
    };
    requestAnimationFrame(tick);
  }

  private render(v: number): void {
    const formatted = v.toLocaleString('en-US', {
      minimumFractionDigits: this.decimals,
      maximumFractionDigits: this.decimals,
    });
    this.el.nativeElement.textContent = this.prefix + formatted;
  }
}
