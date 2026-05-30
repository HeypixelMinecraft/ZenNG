<script lang="ts">
  import type { HudBox, PlayerInfo } from '../../types';
  import { boxStyle } from './hudUtils';

  export let box: HudBox | undefined;
  export let players: PlayerInfo[] = [];
</script>

{#if box?.enabled && players.length > 0}
  <section class="hud-panel players" style={boxStyle(box, 'left: 10px; top: 180px')}>
    <header><span>●</span><strong>Playerlist</strong></header>
    {#each players as player}
      <div class="hud-row"><span>{player.name}</span><kbd>{player.distance.toFixed(0)}m</kbd></div>
    {/each}
  </section>
{/if}
