import { WebPlugin } from '@capacitor/core';

import type { VlcRtspPlayerPlugin } from './definitions';

export class VlcRtspPlayerWeb extends WebPlugin implements VlcRtspPlayerPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
