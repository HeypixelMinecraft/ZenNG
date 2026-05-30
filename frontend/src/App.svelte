<script lang="ts">
  type Category = { id: string; name: string };
  type ModuleEntry = {
    name: string;
    category: string;
    categoryName: string;
    enabled: boolean;
    bind: string;
    key: number;
    hasSettings: boolean;
  };
  type SettingEntry = {
    name: string;
    displayName: string;
    type: 'boolean' | 'number' | 'mode' | 'multi' | 'text';
    value: unknown;
    min?: number;
    max?: number;
    step?: number;
    values?: string[];
  };
  type GuiState = {
    success: boolean;
    categories: Category[];
    modules: ModuleEntry[];
    selectedModule: ModuleEntry | null;
    settings: SettingEntry[];
  };

  let categories: Category[] = [];
  let modules: ModuleEntry[] = [];
  let settings: SettingEntry[] = [];
  let selectedCategory = '';
  let selectedModule = '';
  let search = '';
  let loading = true;
  let error = '';
  let toast = '';
  let toastTimer = 0;

  $: filteredModules = modules
    .filter((module) => !selectedCategory || module.category === selectedCategory)
    .filter((module) => module.name.toLowerCase().includes(search.toLowerCase()))
    .sort((a, b) => a.name.localeCompare(b.name));
  $: activeModule = modules.find((module) => module.name === selectedModule) ?? null;

  async function loadState(moduleName = selectedModule) {
    try {
      const query = moduleName ? `?module=${encodeURIComponent(moduleName)}` : '';
      const response = await fetch(`/api/gui/state${query}`, { cache: 'no-store' });
      const data = (await response.json()) as GuiState;
      if (!data.success) throw new Error('State request failed');
      categories = data.categories ?? [];
      modules = data.modules ?? [];
      settings = data.settings ?? [];
      if (!selectedCategory && categories.length > 0) selectedCategory = categories[0].id;
      if (!moduleName && data.selectedModule) selectedModule = data.selectedModule.name;
      if (moduleName && !modules.some((module) => module.name === moduleName)) selectedModule = data.selectedModule?.name ?? '';
      error = '';
    } catch (err) {
      error = err instanceof Error ? err.message : String(err);
    } finally {
      loading = false;
    }
  }

  function showToast(message: string) {
    toast = message;
    window.clearTimeout(toastTimer);
    toastTimer = window.setTimeout(() => (toast = ''), 1800);
  }

  async function selectModule(module: ModuleEntry) {
    selectedModule = module.name;
    await loadState(module.name);
  }

  async function toggleModule(module: ModuleEntry, state = !module.enabled) {
    if (module.name === 'ClickGui' || module.name === 'WebUI') {
      showToast(`${module.name} cannot be toggled here`);
      await loadState(selectedModule || module.name);
      return;
    }
    const url = `/api/setStatus?module=${encodeURIComponent(module.name)}&state=${state}`;
    const response = await fetch(url, { cache: 'no-store' });
    const data = await response.json();
    if (data.success) {
      showToast(`${module.name} ${data.result ? 'enabled' : 'disabled'}`);
      await loadState(selectedModule || module.name);
    } else {
      showToast(data.reason ?? 'Toggle failed');
    }
  }

  function settingValue(setting: SettingEntry) {
    if (Array.isArray(setting.value)) return setting.value.join(',');
    return String(setting.value ?? '');
  }

  async function updateSetting(setting: SettingEntry, value: string | boolean | number | string[]) {
    const raw = Array.isArray(value) ? value.join(',') : String(value);
    const url = `/api/setModuleSettingValue?module=${encodeURIComponent(selectedModule)}&name=${encodeURIComponent(setting.name)}&value=${encodeURIComponent(raw)}`;
    const response = await fetch(url, { cache: 'no-store' });
    const data = await response.json();
    if (data.success) {
      showToast(`${setting.name}: ${Array.isArray(value) ? value.join(', ') || 'None' : value}`);
      await loadState(selectedModule);
    } else {
      showToast(data.reason ?? 'Setting failed');
    }
  }

  function toggleMulti(setting: SettingEntry, option: string) {
    const current = Array.isArray(setting.value) ? [...setting.value.map(String)] : [];
    const next = current.includes(option)
      ? current.filter((value) => value !== option)
      : [...current, option];
    void updateSetting(setting, next);
  }

  loadState();
  window.setInterval(() => {
    if (!document.hidden) void loadState(selectedModule);
  }, 1000);
</script>

<main>
  <section class="shell">
    <aside class="sidebar">
      <div class="profile">
        <div class="avatar">ZN</div>
        <div>
          <div class="name">ZenNG</div>
          <div class="sub">Svelte ClickGUI</div>
        </div>
      </div>

      <div class="categories">
        {#each categories as category}
          <button
            class:active={selectedCategory === category.id}
            on:click={() => (selectedCategory = category.id)}
          >
            {category.name}
          </button>
        {/each}
      </div>
    </aside>

    <section class="module-list">
      <div class="list-head">
        <div>
          <h1>{categories.find((category) => category.id === selectedCategory)?.name ?? 'Modules'}</h1>
          <span>{filteredModules.length} modules</span>
        </div>
        <input bind:value={search} placeholder="search" />
      </div>

      <div class="modules">
        {#if loading}
          <div class="empty">Loading...</div>
        {:else if error}
          <div class="empty error">{error}</div>
        {:else if filteredModules.length === 0}
          <div class="empty">No modules</div>
        {:else}
          {#each filteredModules as module}
            <button
              class="module-row"
              class:selected={selectedModule === module.name}
              on:click={() => selectModule(module)}
              on:dblclick={() => toggleModule(module)}
            >
              <span class="module-title">{module.name}</span>
              <span class="module-meta">
                {#if module.bind !== 'None'}{module.bind}{/if}
                <label class="switch">
                  <input
                    type="checkbox"
                    checked={module.enabled}
                    disabled={module.name === 'ClickGui' || module.name === 'WebUI'}
                    on:click|stopPropagation
                    on:change={(event) => toggleModule(module, (event.currentTarget as HTMLInputElement).checked)}
                  />
                  <span></span>
                </label>
              </span>
            </button>
          {/each}
        {/if}
      </div>
    </section>

    <section class="settings">
      {#if activeModule}
        <div class="settings-head">
          <div>
            <h2>{activeModule.name}</h2>
            <span>{activeModule.categoryName} / {activeModule.enabled ? 'Enabled' : 'Disabled'}</span>
          </div>
          <button
            class="primary"
            disabled={activeModule.name === 'ClickGui' || activeModule.name === 'WebUI'}
            on:click={() => toggleModule(activeModule)}
          >
            {activeModule.enabled ? 'Disable' : 'Enable'}
          </button>
        </div>

        <div class="setting-list">
          {#if settings.length === 0}
            <div class="empty">No settings</div>
          {:else}
            {#each settings as setting}
              <div class="setting">
                <div class="setting-label">
                  <strong>{setting.displayName}</strong>
                  <span>{settingValue(setting)}</span>
                </div>

                {#if setting.type === 'boolean'}
                  <label class="wide-switch">
                    <input
                      type="checkbox"
                      checked={Boolean(setting.value)}
                      on:change={(event) => updateSetting(setting, (event.currentTarget as HTMLInputElement).checked)}
                    />
                    <span></span>
                  </label>
                {:else if setting.type === 'number'}
                  <input
                    type="range"
                    min={setting.min}
                    max={setting.max}
                    step={setting.step}
                    value={Number(setting.value)}
                    on:change={(event) => updateSetting(setting, Number((event.currentTarget as HTMLInputElement).value))}
                  />
                {:else if setting.type === 'mode'}
                  <div class="segments">
                    {#each setting.values ?? [] as option}
                      <button
                        class:active={setting.value === option}
                        on:click={() => updateSetting(setting, option)}
                      >
                        {option}
                      </button>
                    {/each}
                  </div>
                {:else if setting.type === 'multi'}
                  <div class="chips">
                    {#each setting.values ?? [] as option}
                      <button
                        class:active={Array.isArray(setting.value) && setting.value.map(String).includes(option)}
                        on:click={() => toggleMulti(setting, option)}
                      >
                        {option}
                      </button>
                    {/each}
                  </div>
                {:else}
                  <input
                    value={settingValue(setting)}
                    on:change={(event) => updateSetting(setting, (event.currentTarget as HTMLInputElement).value)}
                  />
                {/if}
              </div>
            {/each}
          {/if}
        </div>
      {:else}
        <div class="empty">Select a module</div>
      {/if}
    </section>
  </section>

  {#if toast}
    <div class="toast">{toast}</div>
  {/if}
</main>
