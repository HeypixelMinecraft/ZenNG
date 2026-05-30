export type Category = { id: string; name: string };

export type ModuleEntry = {
  name: string;
  category: string;
  categoryName: string;
  enabled: boolean;
  bind: string;
  key: number;
  hasSettings: boolean;
};

export type SettingEntry = {
  name: string;
  displayName: string;
  type: 'boolean' | 'number' | 'mode' | 'multi' | 'text';
  value: unknown;
  min?: number;
  max?: number;
  step?: number;
  values?: string[];
};

export type GuiState = {
  success: boolean;
  categories: Category[];
  modules: ModuleEntry[];
  selectedModule: ModuleEntry | null;
  settings: SettingEntry[];
};

export type HudBox = {
  name: string;
  enabled: boolean;
  x: number;
  y: number;
  width: number;
  height: number;
};

export type HudModule = { name: string; category: string };
export type KeyBind = { name: string; key: string; enabled: boolean };
export type Effect = { name: string; amplifier: string; duration: string; color: string };

export type Target = {
  visible: boolean;
  name?: string;
  health?: number;
  maxHealth?: number;
  distance?: number;
  hurtTime?: number;
};

export type PlayerInfo = { name: string; distance: number; health: number };
export type ScaffoldInfo = { visible: boolean; enabled: boolean; blocks: number; speed: number };

export type DynamicIsland = {
  visible: boolean;
  type: string;
  size?: { width?: number; height?: number; alignment?: string };
  data: Record<string, unknown>;
};

export type NeverloseWatermark = {
  enabled: boolean;
  visible: boolean;
  style: string;
  username: string;
  config: string;
  ping: string;
  fps: string;
  server: string;
  time: string;
};

export type UiState = {
  success: boolean;
  screen: { width: number; height: number; fps: number };
  hud: HudBox[];
  modules: HudModule[];
  keybinds: KeyBind[];
  effects: Effect[];
  target: Target;
  players: PlayerInfo[];
  scaffold: ScaffoldInfo;
  dynamicIsland: DynamicIsland;
  watermark: NeverloseWatermark;
};
