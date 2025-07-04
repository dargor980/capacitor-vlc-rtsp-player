import { registerPlugin } from '@capacitor/core';

import type { VlcRtspPlayerPlugin } from './definitions';

const VlcRtspPlayer = registerPlugin<VlcRtspPlayerPlugin>('VlcRtspPlayer', {
  web: () => import('./web').then((m) => new m.VlcRtspPlayerWeb()),
});

export * from './definitions';
export { VlcRtspPlayer };
