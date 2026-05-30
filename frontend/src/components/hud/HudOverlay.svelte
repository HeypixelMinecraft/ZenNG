<script lang="ts">
  import { fetchUiState } from '../../api';
  import type { HudBox, UiState } from '../../types';
  import DynamicIsland from './DynamicIsland.svelte';
  import ModuleListHud from './ModuleListHud.svelte';
  import KeyBindsHud from './KeyBindsHud.svelte';
  import PotionEffectsHud from './PotionEffectsHud.svelte';
  import TargetHud from './TargetHud.svelte';
  import PlayerListHud from './PlayerListHud.svelte';
  import LieDetectorHud from './LieDetectorHud.svelte';

  let uiState: UiState | null = null;

  $: hud = (name: string): HudBox | undefined =>
    uiState?.hud.find((entry) => entry.name.toLowerCase() === name.toLowerCase());

  async function loadUiState() {
    try {
      const data = await fetchUiState();
      if (data.success) uiState = data;
    } catch {
      // HUD overlay should fail silently so the Java fallback can keep the game usable.
    }
  }

  loadUiState();
  window.setInterval(() => {
    if (!document.hidden) void loadUiState();
  }, 150);
</script>

<main class="hud-overlay">
  {#if uiState}
    <DynamicIsland island={uiState.dynamicIsland} scaffold={uiState.scaffold} />
    <ModuleListHud box={hud('ModuleList')} modules={uiState.modules} fps={uiState.screen.fps} />
    <KeyBindsHud box={hud('KeyBinds')} keybinds={uiState.keybinds} />
    <PotionEffectsHud box={hud('Effects')} effects={uiState.effects} />
    <TargetHud box={hud('TargetHUD')} target={uiState.target} />
    <PlayerListHud box={hud('PlayerList')} players={uiState.players} />
    <LieDetectorHud box={hud('LieDetector')} target={uiState.target} />
  {/if}
</main>
