export interface VlcRtspPlayerPlugin {
  play(options: { url: string }): Promise<void>
}
