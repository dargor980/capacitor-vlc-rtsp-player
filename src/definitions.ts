export interface VlcRtspPlayerPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
