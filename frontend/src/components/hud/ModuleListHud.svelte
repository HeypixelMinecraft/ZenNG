<script lang="ts">
  import type { HudBox, HudModule } from '../../types';
  import { boxStyle } from './hudUtils';

  export let box: HudBox | undefined;
  export let modules: HudModule[] = [];
  export let fps = 0;
</script>

<section class="hud-panel module-hud" style={boxStyle(box, 'left: 6px; top: 6px')}>
  <div class="watermark"><strong>Z</strong><span>en</span><em>{fps} FPS</em></div>
  {#each modules as module, index}
    <div class="module-line" style={`--i:${index}`}>{module.name}</div>
  {/each}
</section>
