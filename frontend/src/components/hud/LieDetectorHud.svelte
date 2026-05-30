<script lang="ts">
  import type { HudBox, Target } from '../../types';
  import { boxStyle } from './hudUtils';

  export let box: HudBox | undefined;
  export let target: Target;
</script>

{#if box?.enabled && target.visible}
  <section class="lie-detector" style={boxStyle(box, 'left: 10px; top: 300px')}>
    <span>truth</span>
    <div class="meter"><i style={`transform: rotate(${Math.min(110, Number(target.hurtTime ?? 0) * 9) - 55}deg)`}></i></div>
    <span>lie</span>
  </section>
{/if}
