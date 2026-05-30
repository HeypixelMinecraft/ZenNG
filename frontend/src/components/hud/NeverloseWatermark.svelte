<script lang="ts">
  import type { NeverloseWatermark } from '../../types';

  export let watermark: NeverloseWatermark;

  const sections = [
    { key: 'username', icon: 'person' },
    { key: 'config', icon: 'tune' },
    { key: 'ping', icon: 'signal_cellular_alt' },
    { key: 'fps', icon: 'monitoring' },
    { key: 'server', icon: 'public' },
    { key: 'time', icon: 'schedule' }
  ] as const;
</script>

{#if watermark?.visible}
  <section class="neverlose-watermark">
    <div class="neverlose-section neverlose-logo">ZEN</div>
    {#each sections as section}
      <div class="neverlose-section">
        <span class="material-symbol">{section.icon}</span>
        <span>{String(watermark[section.key] ?? '')}</span>
      </div>
    {/each}
  </section>
{/if}
