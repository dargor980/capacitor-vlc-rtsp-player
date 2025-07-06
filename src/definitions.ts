export interface VlcRtspPlayerPlugin {
  play(options: { url: string }): Promise<void>
  pause(): Promise<void>;
  updateStream(option: { url: string }): Promise<void>;
  checkConnection(option: { url: string }): Promise<void>;
}
