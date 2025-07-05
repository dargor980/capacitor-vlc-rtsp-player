import { WebPlugin } from '@capacitor/core';

import type { VlcRtspPlayerPlugin } from './definitions';

export class VlcRtspPlayerWeb extends WebPlugin implements VlcRtspPlayerPlugin {
  async play(_options: { url: string }): Promise<void> {
    console.warn('VlcRtspPlayer is not available on web.');
    return;
  }
}
