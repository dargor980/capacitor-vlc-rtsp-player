import { WebPlugin } from '@capacitor/core';

import type { VlcRtspPlayerPlugin } from './definitions';

export class VlcRtspPlayerWeb extends WebPlugin implements VlcRtspPlayerPlugin {
  async play(_options: { url: string }): Promise<void> {
    console.warn('VlcRtspPlayer is not available on web.');
    return;
  }

  async pause(): Promise<void> {
    console.warn('VlcRtspPlayer is not available on web.');
    return;
  }

  async checkOverlayPermission(): Promise<void> {
    console.warn('VlcRtspPlayer is not available on web.');
    return;
  }

  async requestOverlayPermission(): Promise<void> {
    console.warn('VlcRtspPlayer is not available on web.');
    return;
  }

  async updateStream(_options: { url: string }): Promise<void> {
    console.warn('VlcRtspPlayer is not available on web.');
    return;
  }

  async checkConnection(_options: { url: string }): Promise<void> {
    console.warn('VlcRtspPlayer is not available on web.');
    return;
  }
}
