<script lang="ts">
  import type { DynamicIsland, ScaffoldInfo } from '../../types';

  export let island: DynamicIsland;
  export let scaffold: ScaffoldInfo;

  function dynamicIslandStyle(island: DynamicIsland) {
    if (island.type === 'watermark') {
      return '--island-width:172px;--island-height:24px;--island-top:25px';
    }
    const contentWidth = Number(island.size?.width ?? 170);
    const contentHeight = Number(island.size?.height ?? 25);
    const width = Math.max(0, contentWidth + 30);
    const height = Math.max(0, contentHeight + 3);
    const anchorCenterY = 25 + 25 / 2;
    const top = island.size?.alignment === 'center' ? anchorCenterY - height / 2 : 25;
    return `--island-width:${width}px;--island-height:${height}px;--island-top:${top}px`;
  }
</script>

{#if island?.visible}
  <section class="dynamic-island" class:compact={island.type !== 'watermark'} style={dynamicIslandStyle(island)}>
    {#if island.type === 'scaffold'}
      <div class="scaffold-island">
        <span class="block-count">{scaffold.blocks} blocks</span>
        <div class="scaffold-bar"><i style={`width: ${Math.min(100, (scaffold.blocks / 64) * 100)}%`}></i></div>
        <span>{scaffold.speed.toFixed(2)}b/s</span>
      </div>
    {:else if island.type === 'autoplay'}
      <div class="autoplay-island">
        <span class="progress-ring">{(Number(island.data.progress ?? 0) * 100).toFixed(0)}%</span>
        <strong>Sending you to next game...</strong>
      </div>
    {:else if island.type === 'tablist'}
      <strong class="tab-island">Player List ({String(island.data.count ?? 0)})</strong>
    {:else}
      <div class="watermark-island">
        <div class="island-brand">Z</div>
        <div class="island-separator"></div>
        <div class="island-info">
          <span>beta</span>
          <em>b1</em>
        </div>
        <div class="island-separator"></div>
        <div class="island-info island-right">
          <span>{String(island.data.line1 ?? 'Singleplayer')}</span>
          <em>{String(island.data.line2 ?? '1ms')}</em>
        </div>
      </div>
    {/if}
  </section>
{/if}
