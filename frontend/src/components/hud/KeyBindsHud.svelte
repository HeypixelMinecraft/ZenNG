<script lang="ts">
  import type { HudBox, KeyBind } from '../../types';
  import { boxStyle } from './hudUtils';

  export let box: HudBox | undefined;
  export let keybinds: KeyBind[] = [];
</script>

{#if box?.enabled && keybinds.length > 0}
  <section class="hud-panel keybinds" style={boxStyle(box, 'left: 8px; top: 34px')}>
    <header><span>⌘</span><strong>Hotkeys</strong></header>
    {#each keybinds as bind}
      <div class="hud-row"><span>{bind.name}</span><kbd>{bind.key || 'On'}</kbd></div>
    {/each}
  </section>
{/if}
