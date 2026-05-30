<script lang="ts">
  import ClickGui from './components/ClickGui.svelte';
  import HudOverlay from './components/hud/HudOverlay.svelte';

  const params = new URLSearchParams(window.location.search);
  const view = params.get('view') === 'hud' ? 'hud' : 'clickgui';

  if (view === 'hud') {
    document.documentElement.classList.add('hud-root');
  } else {
    document.documentElement.classList.remove('hud-root');
  }
</script>

{#if view === 'hud'}
  <HudOverlay />
{:else}
  <ClickGui />
{/if}
