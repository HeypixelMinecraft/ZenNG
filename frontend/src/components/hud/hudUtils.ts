import type { HudBox, Target } from '../../types';

export function boxStyle(box?: HudBox | null, fallback = 'left: 10px; top: 10px') {
  if (!box) return fallback;
  return `left: ${Math.max(0, box.x)}px; top: ${Math.max(0, box.y)}px`;
}

export function healthPct(target: Target) {
  if (!target.maxHealth || !target.health) return 0;
  return Math.max(0, Math.min(100, (target.health / target.maxHealth) * 100));
}
