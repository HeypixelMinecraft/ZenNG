import type { GuiState, UiState } from './types';

export async function fetchGuiState(moduleName = '') {
  const query = moduleName ? `?module=${encodeURIComponent(moduleName)}` : '';
  const response = await fetch(`/api/gui/state${query}`, { cache: 'no-store' });
  return (await response.json()) as GuiState;
}

export async function fetchUiState() {
  const response = await fetch('/api/ui/state', { cache: 'no-store' });
  return (await response.json()) as UiState;
}

export async function setModuleStatus(moduleName: string, state: boolean) {
  const url = `/api/setStatus?module=${encodeURIComponent(moduleName)}&state=${state}`;
  const response = await fetch(url, { cache: 'no-store' });
  return await response.json();
}

export async function setModuleSetting(moduleName: string, settingName: string, rawValue: string) {
  const url = `/api/setModuleSettingValue?module=${encodeURIComponent(moduleName)}&name=${encodeURIComponent(settingName)}&value=${encodeURIComponent(rawValue)}`;
  const response = await fetch(url, { cache: 'no-store' });
  return await response.json();
}
