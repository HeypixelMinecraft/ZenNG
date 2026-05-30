<script lang="ts">
  import type { Effect, HudBox } from '../../types';
  import { boxStyle } from './hudUtils';

  export let box: HudBox | undefined;
  export let effects: Effect[] = [];
</script>

{#if box?.enabled && effects.length > 0}
  <section class="effects" style={boxStyle(box, 'left: 10px; top: 50px')}>
    {#each effects as effect}
      <div class="effect" style={`--effect:${effect.color}`}>
        <b>{effect.amplifier}</b>
        <span>{effect.name}</span>
        <em>{effect.duration}</em>
      </div>
    {/each}
  </section>
{/if}
