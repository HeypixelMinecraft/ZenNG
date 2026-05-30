<script lang="ts">
  import type { HudBox, Target } from '../../types';
  import { boxStyle, healthPct } from './hudUtils';

  export let box: HudBox | undefined;
  export let target: Target;
</script>

{#if box?.enabled && target.visible}
  <section class="target-hud" style={boxStyle(box, 'left: 220px; top: 260px')}>
    <div class="target-avatar">{target.name?.slice(0, 1) ?? '?'}</div>
    <div class="target-main">
      <strong>{target.name}</strong>
      <span>{target.distance?.toFixed(1)}m</span>
      <div class="health"><i style={`width:${healthPct(target)}%`}></i></div>
    </div>
    <em>{target.health?.toFixed(1)}</em>
  </section>
{/if}
